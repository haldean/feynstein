#!/usr/bin/env python3

import os, subprocess, sys, translator

def get_classpath():
    return '.:%s' % os.getcwd()

def compile_feynstein(infile):
    java_source = translator.main(infile)
    subprocess.call(['javac', '-classpath', get_classpath(), java_source])

def run_feynstein(infile):
    subprocess.call(['java', '-classpath', get_classpath(), java_source])

if __name__ == '__main__':
    infile = sys.argv[1]
    compile_feynstein(infile)
    run_feynstein(infile)
