LEX	= flex
YACC	= bison -y -d
CC	= gcc 
EXEC	= feync
CC_OPT	= -g

calcu: y.tab.o lex.yy.o
	${CC} ${CC_OPT} -o ${EXEC} y.tab.o lex.yy.o -ly -lfl

y.tab.c y.tab.h: parser.y
	${YACC} -d parser.y

y.tab.o: y.tab.c
	${CC} ${CC_OPT} -c y.tab.c

lex.yy.o: y.tab.h lex.yy.c 
	${CC} ${CC_OPT} -c lex.yy.c

lex.yy.c: y.tab.h
	${LEX} lexer.l

clean:
	rm *.o *.c *.h ${EXEC}