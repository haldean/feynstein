#!/usr/bin/env python3

import os,sys

if len(sys.argv) != 2:
    print('listing.py [directory]')
d=sys.argv[1]

for root, dirs, files in os.walk(d):
    for path in files:
        path = os.path.join(root, path)
        print('\\subsection*{%s}\n\\begin{lstlisting}' % path.replace('_', '\\_'))
        with open(path) as f:
            for line in f:
                sys.stdout.write(line)
        print('\\end{lstlisting}\n')
