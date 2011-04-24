#!/usr/bin/env python3

from translator import SyntaxException

import blocks, matchers, re, units

def translate_block_id(block):
    '''
    Replaces block identifiers with their corresponding replacement,
    as defined in matchers.block_translations. Any block identifier
    not in matchers.block_translations remains untouched.
    '''

    if block.block_id in matchers.block_translations:
        # If we're translating this block_id, set its tag to the
        # block_id specified in the Feynstein source.
        block.tag = block.block_id
        block.block_id = matchers.block_translations[block.block_id]

def translate_root_id(block):
    '''
    Replaces the block identifier for a scene block with the
    corresponding class definition.
    '''

    block.name = block.block_id
    block.block_id = 'public class %s extends Scene' % block.block_id
    block.tag = 'scene'

def translate_ids(block, is_root=True):
    '''
    Translate all block identifiers recursively.
    '''

    # Keep track of whether we've seen a block; the root must contain
    # exactly one block (the scene).
    block_seen = False

    for expr in block.children:
        if isinstance(expr, blocks.Block):
            if is_root and block_seen:
                raise SyntaxException('Source may only contain one top-level block. ' +
                                      'You cannot specify more than one scene in one file.')
            block_seen = True
            if is_root:
                translate_root_id(expr)
            else:
                translate_block_id(expr)

            # Recursively call on this block.
            translate_ids(expr, is_root=False)

    if is_root and not block_seen:
        raise SyntaxException('Source must contain a scene.')

def disperse_tags(root):
    '''
    Trickle down tags so that all blocks within other blocks in a
    scene are tagged with their parents' tags. This is necessary for
    later translation steps; for example, the "shape" directive has a
    different meaning if it is in the "shapes" block.
    '''

    def set_child_tags(block, tag):
        '''
        Set the tag for block and all of its children.
        '''

        block.tag = tag
        for expr in block.children:
            if isinstance(expr, blocks.Block):
                set_child_tags(expr, tag)

    # If this isn't a scene block, assume that it's the root and find
    # the scene block in its children.
    if root.tag != 'scene':
        root = root.get_by_tag('scene')
        if not root:
            raise SyntaxException('Source must contain a scene.')
    
    for child in root.children:
        if isinstance(child, blocks.Block):
            set_child_tags(child, child.tag)

def is_builder(expr):
    '''
    Return a best-guess for whether or not expr represents an
    expression that contains builder syntax. Note that the result of
    this method is not guaranteed to be correct.
    '''
    match = re.search(matchers.builder_hint, expr)
    if match and expr.count('"', 0, match.start()) % 2 == 0:
        return True
    return False

def translate_builder(expr, parent_ref=False):
    '''
    Given an expresion in builder syntax, output the corresponding
    Java translation.
    '''

    # Get the class name and parameter list
    match = re.search(matchers.outer_parens, expr)
    class_name, param_list = match.groups()
    
    params = []
    nest_level = last_boundary = 0
    in_quote = False

    # Process input one character at a time; this is essentially a PDA
    # to determine the boundaries between arguments at the parent
    # level, and ignore commas that correspond to other method calls.
    for i in range(len(param_list)):
        char = param_list[i]

        if in_quote and char == '"':
            in_quote = False
        elif char == '"':
            in_quote = True
        elif char == '(':
            nest_level += 1
        elif char == ')':
            nest_level -= 1
        elif (char == ',' or char == ')') and nest_level == 0 and in_quote == False:
            params.append(param_list[last_boundary:i])
            last_boundary = i

    # Add the remainder, removing any trailing commas.
    params.append(param_list[last_boundary:].strip(','))

    # Process each key-value pair
    for i, p in enumerate(params):
        attr, eql, value = p.strip(' ,').partition('=')

        # If the value starts with a parenthesis, we remove a set of
        # parentheses from the outside of this statement. This is for
        # tuple invocation of multiparametered methods.
        if value.startswith('('):
            value = value[1:-1]

        params[i] = 'set_%s(%s)' % (attr, value)

    # If a parent reference is required, add one. This is usually only
    # necessary for properties.
    if parent_ref:
        constructor_args = 'this';
    else:
        constructor_args = '';

    # Return a formatted string, leaving the prefix to the builder
    # syntax intact.
    return '%s(new %s(%s)).%s.finalizeShape()' % (
        expr[:match.start()], class_name, constructor_args, '.'.join(params))

def translate_builders(block):
    '''
    Recursively translate builder syntax.
    '''

    for i, expr in enumerate(block.children):
        if isinstance(expr, blocks.Block):
            translate_builders(expr)
        else:
            if is_builder(expr):
                block.children[i] = translate_builder(expr, block.tag == 'properties')

def translate_block_directives(block):
    '''
    Recursively translate block directives, like "shape" or "force"
    in the "shapes" or "forces" block, respectively.
    '''

    for i, expr in enumerate(block.children):
        if not isinstance(expr, blocks.Block):
            # If an expression starts with "shape" in the shapes
            # block, it is an add shape directive, and likewise for
            # "force" in the forces block.
            if block.tag == 'shapes' and expr.startswith('shape '):
                block.children[i] = 'addShape(%s)' % expr[len('shape '):]
            elif block.tag == 'forces' and expr.startswith('force '):
                block.children[i] = 'addForce(%s)' % expr[len('force '):]
            elif block.tag == 'properties' and expr.startswith('property '):
                block.children[i] = 'addProperty(%s)' % expr[len('property '):]
        else: translate_block_directives(expr)

def translate_shape_accessors(block):
    '''
    Recursively translate shape accessors.
    '''

    for i, expr in enumerate(block.children):
        if not isinstance(expr, blocks.Block):
            # Split the string by double quotes, then only replace
            # accessors in the even bits. The odd bits correspond to
            # string literals in this expression, and should not be
            # touched.
            bits = expr.split('"')
            for j, b in enumerate(bits):
                if j % 2 == 0:
                    bits[j] = re.sub(matchers.accessor, r'getShape("\1")', b)
            block.children[i] = '"'.join(bits)
        else: translate_shape_accessors(expr)

def translate_empty_blocks(block):
    '''
    Recursively translate the "block none;" construct.
    '''

    for i, expr in enumerate(block.children):
        if not isinstance(expr, blocks.Block):
            if expr.endswith(' none'):
                block.children[i] = blocks.Block(expr[:-5], [])
        else:
            translate_empty_blocks(expr)

def translate(root):
    '''
    Perform all translation tasks on a syntax tree.
    '''

    translate_empty_blocks(root)
    translate_ids(root)
    disperse_tags(root)
    translate_shape_accessors(root)
    translate_builders(root)
    translate_block_directives(root)
    units.translate_units(root)
