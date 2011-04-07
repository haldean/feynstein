#!/usr/bin/env python3

'''
The Feynstein compiler. (Mostly) written early one morning, with the
help of lots of beer. I'm going to bed now, I think.

TODO

-- Test how this mangles strings when they contain something that
   looks like either builder syntax or shape accessors. Shape
   accessors should already be immune to string literals, but it's
   untested.

'''

class SyntaxException(Exception): 
    '''
    An exception raised when a syntax error is detected when parsing a
    Feynstein source file.
    '''
    pass

import blocks, matchers, parse, re, sys, syntax, translate

def create_java(root):
    '''
    Create a Java source string from a translated root block.
    '''

    return '\n'.join([str(x) for x in root.children])

def feync(source):
    '''
    Compile a source file.
    '''

    exprs = parse.split(source)
    root = parse.parse(exprs)
    translate.translate(root)
    syntax.check_syntax(root)
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
