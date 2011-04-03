%{
  #include <stdio.h>
  #include <stdlib.h>
  #include <string.h>

  #define YYSTYPE char *

  int yylex(void);
  void yyerror(char*);

  char* strblock(const char* block_method, char* block_contents) {
    int block_length = strlen(block_method) + strlen(block_contents) + 6;
    char* block = (char*) calloc(block_length, sizeof(char));

    sprintf(block, "%s { %s }\n", block_method, block_contents);
    return block;
  }

%}

%token FORCE_BLOCK
%token FRAME_BLOCK
%token IDENTIFIER
%token PROPERTIES_BLOCK
%token SHAPE_ACCESSOR
%token SHAPE_BLOCK
%token STRING_LITERAL
%token TERMINATOR

%%

source_file : scene_name '{' properties_block shape_block force_block frame_block '}' {
  printf(" {\n\t%s\n\t%s\n\t%s\n\t%s\n}", $3, $4, $5, $6);
}

exprs : term_expr | exprs term_expr {
  $$ = (char*) calloc(strlen($1) + strlen($2) + 1, sizeof(char));
  sprintf($$, "%s\n%s", $1, $2);
 };

term_expr : expr TERMINATOR {
  $$ = (char*) calloc(strlen($1) + 2, sizeof(char));
  sprintf($$, "%s;", $1);
 };

expr : IDENTIFIER | shape_accessor | ;

shape_accessor : '#' IDENTIFIER {
  const char* shape_access = "getShape(\"";
  $$ = (char*) calloc(strlen(shape_access) + strlen($2) + 3, sizeof(char));
  sprintf($$, "%s%s\")", shape_access, $2);
 }

scene_name : IDENTIFIER {printf("public class %s extends Scene", $$);};

block : '{' exprs '}' {$$ = $2;} | '{' '}' {$$ = ";\0";};

shape_block : SHAPE_BLOCK block {
  $$ = strblock("void createShapes()", $2);
 };

force_block : FORCE_BLOCK block {
  $$ = strblock("void createForces()", $2);
 };

frame_block : FRAME_BLOCK block {
  $$ = strblock("void onFrame()", $2);
 };

properties_block : PROPERTIES_BLOCK block {
  $$ = strblock("void setProperties()", $2);
 };

%%

void yyerror(char *s) {
  fprintf(stderr, "Error: %s\n", s);
}

int main() {
  yyparse();
  return 0;
}
