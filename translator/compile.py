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
    for e in err:
        #print(e)
        if re.search('java:',e):
            m = re.search(':[0-9]+',e)
            n = re.search('[0-9]+',m.group())
            errorlist.append(int(n.group()))
    #print(str(len(errorlist)))

    #for el in errorlist:
        #print(str(el))
    if len(errorlist) > 0:
        errormessage = 'Feynstein errors:\n'
        for er in errorlist:
            for j in javamap:
                #print(str(j[0])+' '+str(j[1])+'\n')
                if j[1] == er:
                    errormessage=errormessage+'Check error in line ' + str(j[0]) + '.\n'

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
