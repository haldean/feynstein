#!/usr/bin/env python3

import glob, os, subprocess, sys, translator

def get_classpath():
    libs = ':'.join(glob.glob('./libs/*.jar'))
    return '.:%s:%s' % (os.getcwd(), libs)

def get_jvm_vars():
    return '-Djava.library.path=/lib/:libs/'

def compile_feynstein(infile):
    java_source = translator.main(infile)
    subprocess.call(['javac', '-classpath', get_classpath(), 
                     '-Xlint:unchecked', java_source])
    return java_source

def run_feynstein(infile):
    package = '.'.join(infile.replace('.', '/').split('/')[:-1])
    subprocess.call(['java', '-classpath', get_classpath(), 
                     get_jvm_vars(), package])

if __name__ == '__main__':
    infile = sys.argv[1]
    if len(sys.argv) > 2 and sys.argv[2] == 'compile':
        compile_feynstein(infile)
    else:
        run_feynstein(compile_feynstein(infile))
