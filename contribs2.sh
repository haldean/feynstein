find feynstein/ translator/ tests/ -name '*.*' | \
     xargs -If \     
     git blame -c -w f | perl contribs2.pl