%{
  #include <stdio.h>
  #include <stdlib.h>
  #include <string.h>

  #define YYSTYPE char *

  int line_no = 1;
  int yylex(void);
  void yyerror(char*);

  char* strblock(const char* block_method, char* block_contents) {
    const char* extra_chars = "@Override private {  } \n";
    int block_length = strlen(block_method) + strlen(block_contents) + 
      strlen(extra_chars) + 2;
    char* block = (char*) calloc(block_length, sizeof(char));

    sprintf(block, "@Override private %s { %s }\n", block_method, block_contents);
    return block;
  }

%}

%token BUILDER_KEY
%token BUILDER_TUPLE
%token BUILDER_VALUE
%token FORCE_BLOCK
%token FRAME_BLOCK
%token OBJ_BUILDER
%token PROPERTIES_BLOCK
%token SHAPE
%token SHAPE_ACCESSOR
%token SHAPE_BLOCK
%token STRING
%token STRING_LITERAL
%token SYMBOL
%token TERMINATOR

%%

source_file : scene_name '{' properties_block shape_block force_block frame_block '}' {
  printf("import feynstein.*;%s {\n\t%s\n\t%s\n\t%s\n\t%s\n}", $1, $3, $4, $5, $6);
}

scene_name : SYMBOL {
  $$ = (char*) calloc(strlen($1) + 31, sizeof(char));
  printf("Scene name %s\n", $1);
  sprintf($$, "public class %s extends Scene", $1);
 };

exprs : term_expr | exprs term_expr {
  $$ = (char*) calloc(strlen($1) + strlen($2) + 2, sizeof(char));
  sprintf($$, "%s\n%s", $1, $2);
 };

term_expr : expr TERMINATOR {
  $$ = (char*) calloc(strlen($1) + 2, sizeof(char));
  sprintf($$, "%s;", $1);
 };

expr : | SYMBOL
       | shape_def
       | shape_accessor 
       | string
       | builder 
       | error {printf("Expr error .\n");};

string : string_chunk {$$ = $1;} | string_chunk string {
  $$ = (char*) calloc(strlen($1) + strlen($2) + 2, sizeof(char));
  sprintf($$, "%s%s", $1, $2);
 }

string_chunk : SYMBOL | STRING

shape_accessor : '#' SYMBOL {
  const char* shape_access = "getShape(\"";
  $$ = (char*) calloc(strlen(shape_access) + strlen($2) + 3, sizeof(char));
  sprintf($$, "%s%s\")", shape_access, $2);
 }

block : '{' exprs '}' {$$ = $2;} | '{' '}' {$$ = ";\0";};

shape_def : SHAPE builder {
  const char* shapeKeyword = "addShape(";
  $$ = (char*) calloc(strlen(shapeKeyword) + strlen($2) + 3, sizeof(char));
  sprintf($$, "%s%s)", shapeKeyword, $2);
 }

shape_block : SHAPE_BLOCK block {
  $$ = strblock("void createShapes()", $2);
 };

force_block : FORCE_BLOCK block {
  $$ = strblock("void createForces()", $2);
 };

frame_block : FRAME_BLOCK block {
  $$ = strblock("void onFrame()", $2);
 };

properties_block : {$$ = strblock("void setProperties()", ";");} 
| PROPERTIES_BLOCK block {
  $$ = strblock("void setProperties()", $2);
 };

builder : OBJ_BUILDER '(' builder_pairs ')' {
  $$ = (char*) calloc(strlen($1) + strlen($3) + 10, sizeof(char));
  sprintf($$, "(new %s())%s", $1, $3);
 };

builder_pairs: builder_pair { $$ = $1; } | builder_pairs ',' builder_pair {
  $$ = (char*) calloc(strlen($1) + strlen($3) + 3, sizeof(char));
  sprintf($$, "%s%s", $1, $3);
 };

builder_pair: BUILDER_KEY '=' builder_value {
  $$ = (char*) calloc(strlen($1) + strlen($3) + 9, sizeof(char));
  sprintf($$, ".set_%s%s", $2, $3);
 }

builder_value: BUILDER_TUPLE | BUILDER_VALUE {
  $$ = (char*) calloc(strlen($1) + 3, sizeof(char));
  sprintf($$, "(%s)", $1);
 }

%%

void yyerror(char *s) {
  fprintf(stderr, "Error near line %d: %s\n", line_no, s);
}

int main() {
  yyparse();
  return 0;
}
