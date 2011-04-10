#!/usr/bin/env python3

import re

from translator import SyntaxException

import blocks, matchers

def split(source):
    '''
    Converts a source file to a series of statements, removing
    comments. blocks.Block identifiers have not yet been removed from the
    expressions at the end of this pass.
    '''

    # Replace all single line comments before we mangle whitespace
    source = re.sub(matchers.line_comments, '', source)

    # Replace all whitespace by a single space
    source = re.sub(matchers.whitespace, r' ', source)

    # Replace all multiline comments (this must be done after newlines
    # are removed).
    source = re.sub(matchers.multiline_comments, '', source)

    # Place a semicolor after all braces, so that open and close block
    # statements will be split in the following split-on-semicolon.
    source = re.sub(matchers.braces, r'\1;', source)

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

    root = blocks.Block(None, make_blocks(exprs), 'root')
    root.children.insert(0, 'import feynstein.shapes.*;')
    root.children.insert(0, 'import feynstein.forces.*;')
    root.children.insert(0, 'import feynstein.*;')
    return root

def make_blocks(exprs):
    '''
    Convert a list of expressions into a syntax tree. Called
    recursively to generate a full tree.
    '''

    block_list = []
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
                block_list.append(blocks.Block(block_id(expr), make_blocks(exprs[i+1:j])))

                # Move the stack pointer forward to represent all of
                # the statements that were just added to the new
                # block's children.
                i = j
            except IndexError:
                print('Exception encountered while parsing %s' % expr)
                raise SyntaxException('Not enough close block statements. ' +
                                      'You\'re probably missing a close-brace ' +
                                      'or a semicolon.')

        # If it's not an open block statement, just add it to the
        # sequence of expressions we've found thus far.
        elif expr: block_list.append(expr)
        i += 1

    return block_list
