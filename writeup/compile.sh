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
cd ..
python3 writeup/listing.py tests > writeup/tests.listing.tex
python3 writeup/listing.py translator > writeup/translator.listing.tex
python3 writeup/listing.py feynstein > writeup/feynstein.listing.tex
./contribs.sh > writeup/contribs.tex
