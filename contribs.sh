if [ -z "$1" ]; then
    find feynstein/ translator/ tests/ -name '*.*' | \
	xargs -if \
	git blame -c -w f | cut -d\( -f2 |
	sed 's/ //g' | cut -f1 |
	sed 's/Sam$/SamanthaAinsley/g' |
	sed 's/Rob/RobPost/g' |
	sed 's/eva/EvaAsplund/g' |
	sed 's/NotCommittedYet/EvaAsplund/g' |
	sed 's/Я/ColleenMcKenzie/g' | sort | uniq -c
else
    git blame -c -w $1 | cut -d\( -f2 |
	sed 's/ //g' | cut -f1 |
	sed 's/Sam$/SamanthaAinsley/g' |
	sed 's/Rob/RobPost/g' |
	sed 's/eva/EvaAsplund/g' |
	sed 's/NotCommittedYet/EvaAsplund/g' |
	sed 's/Я/ColleenMcKenzie/g' | sort | uniq -c
fi