if [ -z "$1" ]; then
    find feynstein/ translator/ tests/ -name '*.*' | \
	xargs -if \
	git blame -c -w f | cut -d\( -f2 |
	sed 's/ //g' | cut -f1 |
	sed 's/Sam$/SamanthaAinsley/g' |
	sed 's/Rob/RobPost/g' |
	sed 's/eva/EvaAsplund/g' |
	sed 's/Я/ColleenMcKenzie/g' | sort | uniq -c |
	sed 's/^[ ]*//g' | sed 's/ / \& /g' | sed 's/$/ \\\\/g'
elif [ -f "$1" ]; then
    git blame -c -w $1 | cut -d\( -f2 |
	sed 's/ //g' | cut -f1 |
	sed 's/Sam$/SamanthaAinsley/g' |
	sed 's/Rob/RobPost/g' |
	sed 's/eva/EvaAsplund/g' |
	sed 's/Я/ColleenMcKenzie/g' | sort | uniq -c
else 
    find $1 -name '*.*' | \
	xargs -if \
	git blame -c -w f | cut -d\( -f2 |
	sed 's/ //g' | cut -f1 |
	sed 's/Sam$/SamanthaAinsley/g' |
	sed 's/Rob/RobPost/g' |
	sed 's/eva/EvaAsplund/g' |
	sed 's/Я/ColleenMcKenzie/g' | sort | uniq -c |
	sed 's/^[ ]*//g' | sed 's/ / \& /g' | sed 's/$/ \\\\/g'
fi