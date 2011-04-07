#!/usr/bin/env python3

'''
The Feynstein compiler. (Mostly) written early one morning, with the
help of lots of beer. I'm going to bed now, I think.

TODO

-- Comments 

-- Check to ensure that all of the blocks are there (maybe emit a
   warning if they aren't?)

-- Test how this mangles strings when they contain something that
   looks like either builder syntax or shape accessors. Shape
   accessors should already be immune to string literals, but it's
   untested.

'''

import matchers, re, sys

class SyntaxException(Exception): 
    '''
    An exception raised when a syntax error is detected when parsing a
    Feynstein source file.
    '''
    pass

class Block:
    '''
    Represents a block of code with an identifier. 

    This identifier usually corresponds to either a method or class
    declaration. Tags are used to keep track of the Feynstein context
    which this block exists within (i.e., is it in the shape block,
    the onFrame block, etc.).
    '''

    def __init__(self, block_id, children, tag=None):
        self.block_id = block_id
        self.children = children
        self.tag = tag

    def block_to_string(self, tab_level=0):
        '''
        Convert a block to Java syntax.
        '''

        rest = '  ' * tab_level + '%s {\n' % (self.block_id)
        for child in self.children:
            if isinstance(child, Block):
                rest += child.block_to_string(tab_level+1)
            else:
                rest += '  ' * (tab_level + 1) + '%s;\n' % child
        rest += '  ' * tab_level + '}\n'
        return rest

    def __str__(self):
        return self.block_to_string()

def split(source):
    '''
    Converts a source file to a series of statements, removing
    comments. Block identifiers have not yet been removed from the
    expressions at the end of this pass.
    '''

    # Replace all whitespace by a single space
    source = re.sub(r'[\n\t ]+', r' ', source)

    # Place a semicolor after all braces, so that open and close block
    # statements will be split in the following split-on-semicolon.
    source = re.sub(r'([{}])', r'\1;', source)

    # Remove trailing whitespace.
    exprs = [x.strip() for x in source.split(';')]
    return exprs
        
def is_open_block(expr):
    '''
    Returns true if an expression opens a block -- i.e., if an
    expression ends with an open-brace.
    '''
    return expr.endswith('{')

def is_close_block(expr):
    '''
    Returns true if an expression closes a block -- i.e., if an
    expression is composed solely of a close-brace.
    '''
    return expr == '}'

def block_id(expr):
    '''
    Given an open block expression, returns the corresponding
    identifier of the block that is opened.
    '''

    if not is_open_block(expr):
        raise SyntaxException('Not a block open statement: %s' % expr)
    return expr.strip('{ ')

def parse(exprs):
    '''
    Convert a series of expressions to a syntax tree. This also
    creates a "root" block which encompasses all parent expressions,
    to ensure that the syntax tree is rooted at a single node.
    '''

    return Block(None, make_blocks(exprs), 'root')

def make_blocks(exprs):
    '''
    Convert a list of expressions into a syntax tree. Called
    recursively to generate a full tree.
    '''

    blocks = []
    i = 0

    while i < len(exprs):
        expr = exprs[i]
        if is_open_block(expr):
            try:
                inner_blocks = 1
                j = i

                # Find the corresponding close-block
                while inner_blocks > 0:
                    j += 1
                    if is_close_block(exprs[j]):
                        inner_blocks -= 1
                    elif is_open_block(exprs[j]):
                        inner_blocks += 1

                # Create a new block, recursively call make_blocks
                # on the statements between the open and close block,
                # and set the new block's children to the result.
                blocks.append(Block(block_id(expr), make_blocks(exprs[i+1:j])))

                # Move the stack pointer forward to represent all of
                # the statements that were just added to the new
                # block's children.
                i = j
            except IndexError:
                raise SyntaxException('Not enough close block statements. ' +
                                      'You\'re probably missing a close-brace ' +
                                      'or a semicolon.')

        # If it's not an open block statement, just add it to the
        # sequence of expressions we've found thus far.
        elif expr: blocks.append(expr)
        i += 1

    return blocks

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
        if isinstance(expr, Block):
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
            if isinstance(expr, Block):
                set_child_tags(expr, tag)

    # If this isn't a scene block, assume that it's the root and find
    # the scene block in its children.
    if root.tag != 'scene':
        for child in root.children:
            if child.tag == 'scene':
                disperse_tags(child)
                return
        raise SyntaxException('Source must contain a scene.')
    
    for child in root.children:
        set_child_tags(child, child.tag)

def is_builder(expr):
    '''
    Return a best-guess for whether or not expr represents an
    expression that contains builder syntax. Note that the result of
    this method is not guaranteed to be correct.
    '''

    return re.search(matchers.builder_hint, expr) != None

def translate_builder(expr):
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
        elif (char == ',' or char == ')') and nest_level == 0:
            params.append(param_list[last_boundary:i])
            last_boundary = i

    # Add the remainder, removing any trailing commas.
    params.append(param_list[last_boundary:].strip(','))

    # Process each key-value pair
    for i, p in enumerate(params):
        attr, eql, value = p.strip().partition('=')

        # If the value starts with a parenthesis, we remove a set of
        # parentheses from the outside of this statement. This is for
        # tuple invocation of multiparametered methods.
        if value.startswith('('):
            value = value[1:-1]

        params[i] = 'set_%s(%s)' % (attr, value)

    # Return a formatted string, leaving the prefix to the builder
    # syntax intact.
    return '%s(new %s()).%s' % (expr[:match.start()], class_name, '.'.join(params))

def translate_builders(block):
    '''
    Recursively translate builder syntax.
    '''

    for i, expr in enumerate(block.children):
        if isinstance(expr, Block):
            translate_builders(expr)
        else:
            if is_builder(expr):
                block.children[i] = translate_builder(expr)

def translate_block_directives(block):
    '''
    Recursively translate block directives, like "shape" or "force"
    in the "shapes" or "forces" block, respectively.
    '''

    for i, expr in enumerate(block.children):
        if not isinstance(expr, Block):
            # If an expression starts with "shape" in the shapes
            # block, it is an add shape directive, and likewise for
            # "force" in the forces block.
            if block.tag == 'shapes' and expr.startswith('shape '):
                block.children[i] = 'addShape(%s)' % expr[len('shape '):]
            elif block.tag == 'forces' and expr.startswith('force '):
                block.children[i] = 'addForce(%s)' % expr[len('force '):]
        else: translate_block_directives(expr)

def translate_shape_accessors(block):
    '''
    Recursively translate shape accessors.
    '''

    for i, expr in enumerate(block.children):
        if not isinstance(expr, Block):
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

def translate(root):
    '''
    Perform all translation tasks on a syntax tree.
    '''

    translate_ids(root)
    disperse_tags(root)
    translate_builders(root)
    translate_block_directives(root)
    translate_shape_accessors(root)

def create_java(root):
    '''
    Create a Java source string from a translated root block.
    '''

    return '\n'.join([str(x) for x in root.children])

def feync(source):
    '''
    Compile a source file.
    '''

    exprs = split(source)
    root = parse(exprs)
    translate(root)
    return create_java(root)

def main(infile, outfile):
    '''
    Compile infile and save the output to outfile.
    '''

    with open(infile) as f:
        source = f.read()
    with open(outfile, 'w') as f:
        f.write(feync(source))

if __name__ == '__main__':
    infile = sys.argv[1]
    outfile = '%s.scene' % sys.argv[1]
    main(infile, outfile)
