/*
  Grammar for parsing functions.
*/
grammar QDLExprParser;

import QDLVariableParser;

assignment : ( scalar | stem) op=ASSIGN  expression;
   argList : expression (',' expression)*;
  function : FuncStart argList* ')';

// Again, the order here has been tweaked and any changes to this list will require running all the tests
// and checking for regression.

expression
 :
   function                                             #functions
 | expression postfix=('++' | '--')                     #postfix
 | prefix=('++'|'--') expression                        #prefix
 | '-' expression                                       #unaryMinusExpression
 | '!' expression                                       #notExpression
 | expression op=('*' | '/') expression                 #multiplyExpression
 | expression op=('+' | '-') expression                 #addExpression
 | expression op=('<' | '>' | '<=' | '>=') expression   #compExpression
 | expression op=('==' | '!=') expression               #eqExpression
 | expression '&&' expression                           #andExpression
 | expression '||' expression                           #orExpression
 | '(' expression ')'                                   #association
 | LeftBracket                                          #leftBracket
 | Number                                               #numbers
 | Bool                                                 #logical
 | Null                                                 #null
 | scalar                                               #variables
 | stem                                                 #stemVariables
 | STRING                                               #strings
 | ';'                                                  #semi_for_empty_expressions
 ;
