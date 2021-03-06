/*
  Grammar for parsing functions.
*/
grammar QDLExprParser;

import QDLVariableParser;

   assignment : (variable  op=ASSIGN)+  (expression | stemVariable | stemList);

 stemVariable : '{' stemEntry (',' stemEntry)* '}'
              | '{' '}';
    stemEntry : (Times | expression) ':' stemValue;
     stemList : '[' stemValue (',' stemValue)* ']'
              | '[' ']';
      argList : stemValue (',' stemValue)*;

    stemValue : expression
              | stemVariable
              | stemList;

     function : FuncStart argList* ')';

// Again, the order here has been tweaked and any changes to this list will require running all the tests
// and checking for regression. Also Antlr 4 interprets the comments in the right hand column and
// will use these for generating method names in Java. Be careful of actually putting comments there!

expression
 :
   function                                                              #functions
 | stemVariable                                                          #stemVar
 | stemList                                                              #stemLi
 | expression '.' expression                                             #dotOp
 | expression postfix=('++' | '--')                                      #postfix
 | prefix=('++'|'--') expression                                         #prefix
 | '!' expression                                                        #notExpression
 | expression '~' expression                                             #tildeExpression
 | expression '^' expression                                             #powerExpression
 | expression op=('*' | '/' | '%' ) expression                           #multiplyExpression
 | ('+' | '-') expression                                                #unaryMinusExpression
 | expression op=('+' | '-' ) expression                                 #addExpression
 | expression op=('<' | '>' | '<=' | '>=' | '=<' | '=>' ) expression     #compExpression
 | expression op=('==' | '!=') expression                                #eqExpression
 | expression '&&' expression                                            #andExpression
 | expression '||' expression                                            #orExpression
 | '(' expression ')'                                                    #association
 | LeftBracket                                                           #leftBracket
 | number                                                                #numbers
 | integer                                                               #integers
 | variable                                                              #variables
 | Bool                                                                  #logical
 | Null                                                                  #null
 | STRING                                                                #strings
 | ';'                                                                   #semi_for_empty_expressions
 ;
// This *could* be added but does not work quite as expected because variables are allowed to have . to show they
// are stems. A (probably quite substantial) rewrite of the parser would be in order to change this
