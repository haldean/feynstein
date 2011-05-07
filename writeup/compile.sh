#!/bin/bash

figures() {
    if [[ -d `pwd`/figures ]]; then
	echo "Generating figures"
	find `pwd`/figures -name "*.dot" | sed s/\.dot$//g | xargs -t -Idotfile dot -Tpdf -odotfile.pdf dotfile.dot
	find `pwd`/figures -name "*.dot" | sed s/\.dot$//g | xargs -t -Idotfile dot -Teps -odotfile.eps dotfile.dot
    else 
	echo "No figures directory. Skipping."
    fi
}

figures
python3 listing.py ../tests > tests.listing.tex
python3 listing.py ../translator > translator.listing.tex
python3 listing.py ../feynstein > feynstein.listing.tex
