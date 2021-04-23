/*
  Grammar for parsing functions.
*/
grammar QDLExprParser;

import QDLVariableParser;

/*
  Uncomment the next line to allow the parser to recognize expressions, e.g.
     f().j(4) := 42;
 You can enable it now, but actually turning the left hand into something reasonable
 is going to be quite tricky. Now the solution is to just use variables, so
     a. := f(); n := j(4);
     a. n := 42;
*/
   assignment : (expression  op=ASSIGN)+  expression;

//   assignment : (variable  op=ASSIGN)+  expression;

 stemVariable : '{' stemEntry (',' stemEntry)* '}'
              | '{' '}';
    stemEntry : (Times | expression) ':' stemValue;
     stemList : '[' stemValue (',' stemValue)* ']'
              | '[' ']';
      argList : stemValue (',' stemValue)*;

    stemValue : expression
              | stemVariable
              | stemList;

     function : FuncStart f_args* ')';
        f_arg : (stemValue | f_ref);
        //f_arg : stemValue;
       f_args : f_arg (',' f_arg)* ;
        f_ref : F_REF;

// Again, the order here has been tweaked and any changes to this list will require running all the tests
// and checking for regression. Also Antlr 4 interprets the comments in the right hand column and
// will use these for generating method names in Java. Be careful of actually putting comments there!

expression
 :
   function                                                                    #functions
 | stemVariable                                                                #stemVar
 | stemList                                                                    #stemLi
 | expression '.' expression                                                   #dotOp
 | expression ('~' | '~|' ) expression                                         #tildeExpression
 | expression postfix=('++' | '--')                                            #postfix
 | prefix=('++'|'--') expression                                               #prefix
 //| prefix='*' expression                                                       #f_ref
 | LogicalNot expression                                                       #notExpression
 | expression '^' expression                                                   #powerExpression
 | expression op=(Times | Divide | '%' ) expression                            #multiplyExpression
 | ('+' | '-') expression                                                      #unaryMinusExpression
 | expression op=('+' | '-' ) expression                                       #addExpression
 | expression op=(LessThan | GreaterThan | LessEquals | MoreEquals) expression #compExpression
 | expression op=(Equals | NotEquals) expression                               #eqExpression
 | expression And expression                                                   #andExpression
 | expression Or expression                                                    #orExpression
 | '(' expression ')'                                                          #association
 | LeftBracket                                                                 #leftBracket
 | integer                                                                     #integers
 | number                                                                      #numbers
 | variable                                                                    #variables
 | Bool                                                                        #logical
 | Null                                                                        #null
 | STRING                                                                      #strings
 | ';'                                                                         #semi_for_empty_expressions
 ;
// This *could* be added but does not work quite as expected because variables are allowed to have . to show they
// are stems. A (probably quite substantial) rewrite of the parser would be in order to change this
