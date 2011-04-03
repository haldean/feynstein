%{

  #include <stdio.h>
  #include "y.tab.h"

  void yyerror(char*);

%}

ws [\n\t\r\f ]
sep [\n\t\r\f ]
passthru [\{\}\(\)#]
letter [a-zA-Z]
identifier {letter}[a-zA-Z0-9_]*

%%

 /* String literals */
\"[^\"]*\" {
  yylval = (int) yytext;
  return STRING_LITERAL;
}

 /***********************
  *  BLOCK DEFINITIONS  *
  ***********************/
^{ws}*shapes{sep}* { return SHAPE_BLOCK; }
^{ws}*forces{sep}* { return FORCE_BLOCK; }
^{ws}*onFrame{sep}* { return FRAME_BLOCK; }
^{ws}*properties{sep}* { return PROPERTIES_BLOCK; }

 /**********************
  *  SYNTAX ADDITIONS  *
  **********************/

 /*************************
  *  LANGUAGE PRIMITIVES  *
  *************************/
{identifier} {
  yylval = (int) yytext;
  return IDENTIFIER;
}

 /************************
  *  SPECIAL CHARACTERS  *
  ************************/
; { 
  yylval = (int) yytext; 
  return TERMINATOR; 
}

{passthru} { return *yytext; }
{ws} { /* ignore whitespace. */ }
 /* TODO: Make this use yyerror. */
. { printf("Did not recognize %s\n", yytext); }

%%

int yywrap(void) {
  return 1;
}
