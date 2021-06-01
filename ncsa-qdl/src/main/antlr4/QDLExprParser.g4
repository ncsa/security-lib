/*
  Grammar for parsing functions.
*/
grammar QDLExprParser;

import QDLVariableParser2;


   iInterval : LeftBracket expression ';' expression (';' expression)? RightBracket;
   rInterval : LDoubleBracket expression ';' expression ';' expression RDoubleBracket;


 stemVariable : '{' stemEntry (',' stemEntry)* '}'
              | '{' '}';
    stemEntry : (Times | expression) ':' stemValue;
     stemList : '[' stemValue (',' stemValue)* ']'
              | '[' ']';

    stemValue : expression
              | stemVariable
              | stemList;

     function : FuncStart f_args* ')';
        f_arg : (stemValue | f_ref);
       f_args : f_arg (',' f_arg)* ;
        f_ref : F_REF;

// Again, the order here has been tweaked and any changes to this list will require running all the tests
// and checking for regression. Also Antlr 4 interprets the comments in the right hand column and
// will use these for generating method names in Java. Be careful of actually putting comments there!

expression
 :
   function                                                                    #functions
 //| function LambdaConnector expression                                         #lambdaDef
  | STRING                                                                      #strings
 | stemVariable                                                                #stemVar
 | stemList                                                                    #stemLi
 | rInterval                                                                   #realInterval
 | iInterval                                                                   #intInterval
 | expression '.' expression                                                   #dotOp
 | expression ('~' | '~|' ) expression                                         #tildeExpression
 | expression postfix=('++' | '--')                                            #postfix
 | prefix=('++'|'--') expression                                               #prefix
 | expression Exponentiation expression                                        #powerExpression
 | expression op=(Times | Divide | '%' ) expression                            #multiplyExpression
 | ('+' | '-') expression                                                      #unaryMinusExpression
 | expression op=('+' | '-' ) expression                                       #addExpression
 | expression op=(LessThan | GreaterThan | LessEquals | MoreEquals) expression #compExpression
 | expression op=(Equals | NotEquals) expression                               #eqExpression
 | expression op=RegexMatches expression                                       #regexMatches
 | expression And expression                                                   #andExpression
 | expression Or expression                                                    #orExpression
 | LogicalNot expression                                                       #notExpression
 | '(' expression ')'                                                          #association
 | expression '?' expression ':' expression                                    #altIFExpression
 | integer                                                                     #integers
 | number                                                                      #numbers
 | variable                                                                    #variables
 | keyword                                                                     #keywords
 | Bool                                                                        #logical
 | Null                                                                        #null
 | ';'                                                                         #semi_for_empty_expressions
 ;
// This *could* be added but does not work quite as expected because variables are allowed to have . to show they
// are stems. A (probably quite substantial) rewrite of the parser would be in order to change this


  variable : Identifier ;
    number : Decimal | Integer | SCIENTIFIC_NUMBER;
   integer : Integer;

   keyword : ConstantKeywords;

