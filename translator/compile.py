#!/usr/bin/env python3

import glob,re, os, subprocess, sys, translator

def get_classpath():
    libs = ':'.join(glob.glob('./libs/*.jar'))
    return '.:%s:%s' % (os.getcwd(), libs)

def get_jvm_vars():
    return '-Djava.library.path=/lib/:libs/'

def error_gen(err,javamap):
    err = str(err)
    err = err.split('\\n')
    errorlist = []
    errormessage = ''
    for ind,e in enumerate(err):
        if re.search('java:',e):
            m = re.search(':[0-9]+',e)
            n = re.search('[0-9]+',m.group())
            errorlist.append([int(n.group()),err[ind]+'\n'+err[ind+1]+'\n'+err[ind+2]])
            

    if len(errorlist) > 0:
        errormessage = 'Feynstein errors:\n'
        for er in errorlist:
            for j in javamap:
                if j[1] == er[0]:
                    errormessage=errormessage+'Feynstein error in line ' + str(j[0]) + '.\nJava error:\n '+er[1]+'\n'

    return errormessage

def compile_feynstein(infile):
    java_source,javamap = translator.main(infile)
    p = subprocess.Popen(['javac', '-classpath', get_classpath(), 
                     '-Xlint:unchecked', java_source],stderr = subprocess.PIPE)
    out,err = p.communicate()
    print(error_gen(err,javamap))

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
