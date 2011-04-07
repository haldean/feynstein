#!/usr/bin/env python3

'''
The Feynstein compiler. (Mostly) written early one morning, with the
help of lots of beer. I'm going to bed now, I think.

TODO

-- Comments (both in this code and supporting them) 

-- Check to ensure that all of the blocks are there (maybe emit a
   warning if they aren't?)

-- Test how this mangles strings when they contain something that
   looks like either builder syntax or shape accessors. Shape
   accessors should already be immune to string literals, but it's
   untested.

'''

import matchers, re, sys

class SyntaxException(Exception): pass

class Block:
    def __init__(self, block_id, children, tag=None):
        self.block_id = block_id
        self.children = children
        self.tag = tag

    def block_to_string(self, tab_level=0):
        rest = '  ' * tab_level + '%s { // tag:%s\n' % (self.block_id, self.tag)
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

    source = re.sub(r'[\n\t ]+', r' ', source)
    source = re.sub(r'([{}])', r'\1;', source)
    exprs = [x.strip() for x in source.split(';')]
    return exprs
        
def is_open_block(expr):
    return expr.endswith('{')

def is_close_block(expr):
    return expr == '}'

def block_id(expr):
    if not is_open_block(expr):
        raise SyntaxException('Not a block open statement: %s' % expr)
    return expr.strip('{ ')

def parse(exprs):
    return Block(None, make_blocks(exprs), 'root')

def make_blocks(exprs):
    blocks = []
    i = 0

    while i < len(exprs):
        expr = exprs[i]
        if is_open_block(expr):
            try:
                inner_blocks = 1
                j = i
                while inner_blocks > 0:
                    j += 1
                    if is_close_block(exprs[j]):
                        inner_blocks -= 1
                    elif is_open_block(exprs[j]):
                        inner_blocks += 1
                blocks.append(Block(block_id(expr), make_blocks(exprs[i+1:j])))
                i = j
            except IndexError:
                raise SyntaxException('Not enough close block statements. ' +
                                      'You\'re probably missing a close-brace ' +
                                      'or a semicolon.')
        elif expr: blocks.append(expr)
        i += 1

    return blocks

def translate_block_id(block):
    if block.block_id in matchers.block_translations:
        block.tag = block.block_id
        block.block_id = matchers.block_translations[block.block_id]

def translate_root_id(block):
    block.block_id = 'public class %s extends Scene' % block.block_id
    block.tag = 'scene'

def translate_ids(block, is_root=True):
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
            translate_ids(expr, is_root=False)

    if is_root and not block_seen:
        raise SyntaxException('Source must contain a scene.')

def disperse_tags(root):
    def set_child_tags(block, tag):
        block.tag = tag
        for expr in block.children:
            if isinstance(expr, Block):
                set_child_tags(expr, tag)

    if root.tag != 'scene':
        for child in root.children:
            if child.tag == 'scene':
                disperse_tags(child)
                return
        raise SyntaxException('Source must contain a scene.')
    
    for child in root.children:
        set_child_tags(child, child.tag)

def is_builder(expr):
    return re.search(matchers.builder_hint, expr) != None

def translate_builder(expr):
    match = re.search(matchers.outer_parens, expr)
    class_name, param_list = match.groups()
    
    params = []
    nest_level = last_boundary = 0
    for i in range(len(param_list)):
        char = param_list[i]

        if char == '(':
            nest_level += 1
        elif char == ')':
            nest_level -= 1
        elif (char == ',' or char == ')') and nest_level == 0:
            params.append(param_list[last_boundary:i])
            last_boundary = i

    params.append(param_list[last_boundary:].strip(','))

    for i, p in enumerate(params):
        attr, eql, value = p.strip().partition('=')
        if value.startswith('('):
            value = value[1:-1]
        params[i] = 'set_%s(%s)' % (attr, value)
    return '%s(new %s()).%s' % (expr[:match.start()], class_name, '.'.join(params))

def translate_builders(block):
    for i, expr in enumerate(block.children):
        if isinstance(expr, Block):
            translate_builders(expr)
        else:
            if is_builder(expr):
                block.children[i] = translate_builder(expr)

def translate_block_directives(block):
    for i, expr in enumerate(block.children):
        if not isinstance(expr, Block):
            if block.tag == 'shapes' and expr.startswith('shape '):
                block.children[i] = 'addShape(%s)' % expr[len('shape '):]
            elif block.tag == 'forces' and expr.startswith('force '):
                block.children[i] = 'addForce(%s)' % expr[len('force '):]
        else: translate_block_directives(expr)

def translate_shape_accessors(block):
    for i, expr in enumerate(block.children):
        if not isinstance(expr, Block):
            bits = expr.split('"')
            for j, b in enumerate(bits):
                if j % 2 == 0:
                    bits[j] = re.sub(matchers.accessor, r'getShape("\1")', b)
            block.children[i] = ''.join(bits)
        else: translate_shape_accessors(expr)

def translate(root):
    translate_ids(root)
    disperse_tags(root)
    translate_builders(root)
    translate_block_directives(root)
    translate_shape_accessors(root)

def create_java(root):
    return '\n'.join([str(x) for x in root.children])

def feync(source):
    exprs = split(source)
    root = parse(exprs)
    translate(root)
    return create_java(root)

def main(infile, outfile):
    with open(infile) as f:
        source = f.read()
    with open(outfile, 'w') as f:
        f.write(feync(source))

if __name__ == '__main__':
    infile = sys.argv[1]
    outfile = '%s.scene' % sys.argv[1]
    main(infile, outfile)
