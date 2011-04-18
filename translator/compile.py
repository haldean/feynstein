#!/usr/bin/env python3

import os, subprocess, sys, translator

def get_classpath():
    return '.:%s' % os.getcwd()

def compile_feynstein(infile):
    java_source = translator.main(infile)
    subprocess.call(['javac', '-classpath', get_classpath(), 
                     '-Xlint:unchecked', java_source])
    return java_source

def run_feynstein(infile):
    package = '.'.join(infile.replace('.', '/').split('/')[:-1])
    subprocess.call(['java', '-classpath', get_classpath(), package])

if __name__ == '__main__':
    infile = sys.argv[1]
    run_feynstein(compile_feynstein(infile))
