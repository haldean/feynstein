#!/usr/bin/env python3

'''
The Feynstein compiler. (Mostly) written early one morning, with the
help of lots of beer. I'm going to bed now, I think.
'''

class SyntaxException(Exception): 
    '''
    An exception raised when a syntax error is detected when parsing a
    Feynstein source file.
    '''
    pass

import blocks, matchers, os, parse, re, sys, syntax, translate

def create_java(root):
    '''
    Create a Java source string from a translated root block.
    '''

    return '\n'.join([str(x) for x in root.children])

def feync(source, path):
    '''
    Compile a source file, returning a tuple with the Java source code
    and the name of the scene.
    '''

    exprs = parse.split(source)
    root = parse.parse(exprs)
    translate.translate(root)
    syntax.check_syntax(root)

    package = os.path.dirname(path).replace('/', '.')
    root.children.insert(0, 'package %s;' % package)

    scene_name = root.get_by_tag('scene').name

    main_file = "%s/main.txt" % os.path.dirname(__file__)
    with open(main_file) as f:
        main_method = f.read().replace('SceneClass', scene_name);
        if main_method:
            root.get_by_tag('scene').children.append(main_method)

    return create_java(root), scene_name

def main(infile):
    '''
    Compile infile and return the filename of the generated Java
    source code.
    '''

    with open(infile) as f:
        source = f.read()

    source, scene_name = feync(source, infile)
    output_file = '%s/%s.java' % (os.path.dirname(infile), scene_name)

    with open(output_file, 'w') as f:
        f.write(source)
    print('Compiled to %s' % output_file)
    
    return output_file

if __name__ == '__main__':
    infile = sys.argv[1]
    main(infile)
