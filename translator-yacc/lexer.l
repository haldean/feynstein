%{

  #include <stdio.h>
  #include "y.tab.h"

  extern int line_no;

  void yyerror(char*);

  int yylstring(char* yytext) {
    char* yyl = calloc(strlen(yytext) + 2, sizeof(char));
    strcpy(yyl, yytext);
    return (int) yyl;
  }

%}

%option yylineno

%s BUILDER
%s BUILDER_EQ
%s BUILDER_VAL
%s PASSTHRU_STRING

ws [\n\t\r\f ]
sep [^a-zA-Z0-9_]
passthru [\(\)#\{\}]
letter [a-zA-Z]
identifier {letter}[a-zA-Z0-9_]*

%%

<*>\n line_no++;

 /* String literals */
<*>\"[^\"]*\" {
  yylval = yylstring(yytext);
  return STRING_LITERAL;
}

 /***********************
  *  BLOCK DEFINITIONS  *
  ***********************/
^{ws}*shapes/{sep} { return SHAPE_BLOCK; }
^{ws}*forces/{sep} { return FORCE_BLOCK; }
^{ws}*onFrame/{sep} { return FRAME_BLOCK; }
^{ws}*properties/{sep} { return PROPERTIES_BLOCK; }

 /********************
  *  BUILDER SYNTAX  *
  ********************/

{identifier}{ws}*/\({identifier}=[^)]+\) {
  yylval = yylstring(yytext);
  BEGIN BUILDER;
  return OBJ_BUILDER;
}

<BUILDER>{identifier}/"=" {
  yylval = yylstring(yytext);
  BEGIN BUILDER_EQ;
  return BUILDER_KEY;
 }

<BUILDER_EQ>"=" {
  BEGIN BUILDER_VAL;
  return *yytext;
 }

<BUILDER_VAL>"("[^\)]*")" {
  yylval = yylstring(yytext);
  return BUILDER_TUPLE;
 }

<BUILDER_VAL>[^=,\(\)]+/[,\)] {
  yylval = yylstring(yytext);
  return BUILDER_VALUE;
 }

<BUILDER_VAL>, {
  BEGIN BUILDER;
  return *yytext;
 }

<BUILDER_VAL>\) {
  BEGIN 0;
  return *yytext;
 }

 /*************************
  *  LANGUAGE PRIMITIVES  *
  *************************/

<*>{identifier}/{sep} {
  if (strcmp(yytext, "shape") == 0) {
    return SHAPE;
  }

  yylval = yylstring(yytext);
  return SYMBOL;
}

 /************************
  *  SPECIAL CHARACTERS  *
  ************************/
<*>; { 
  yylval = (int) yytext; 
  BEGIN 0;
  return TERMINATOR; 
}

<*>{passthru} { return *yytext; }

<PASSTHRU_STRING>[^;]* {
  yylval = yylstring(yytext);
  return STRING;
 }

{ws} ;

<*>. { 
  yylval = yylstring(yytext);
  return STRING;
}

%%

int yywrap(void) {
  return 1;
}
