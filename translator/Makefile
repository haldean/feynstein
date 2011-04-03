LEX	= flex
YACC	= bison -y -d
CC	= gcc 
EXEC	= feync

calcu: y.tab.o lex.yy.o
	${CC} -o ${EXEC} y.tab.o lex.yy.o -ly -lfl

y.tab.c y.tab.h: parser.y
	${YACC} -d parser.y

y.tab.o: y.tab.c
	${CC} -c y.tab.c

lex.yy.o: y.tab.h lex.yy.c 
	${CC} -c lex.yy.c

lex.yy.c: y.tab.h
	${LEX} lexer.l

clean:
	rm *.o *.c *.h ${EXEC}