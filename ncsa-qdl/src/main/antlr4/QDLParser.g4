/*
   Top-level for the QDL language. This should contain only imports, no actual grammar
*/
grammar QDLParser;

import QDLLexer;


elements : element* EOF;

element : (statement ';' ) | (moduleStatement ';') ;
       
statement :
            defineStatement
          | conditionalStatement
          | loopStatement
          | switchStatement
          | expression
          | tryCatchStatement
          | lambdaStatement
          | assertStatement
          | assertStatement2
          | blockStatement
          ;

 conditionalStatement : ifStatement | ifElseStatement;
                   
ifStatement  :
     IF conditionalBlock THEN? statementBlock;

ifElseStatement :
	  IF conditionalBlock THEN? statementBlock ELSE statementBlock;

loopStatement:
     WHILE conditionalBlock DO? statementBlock ;
    // WHILE loopBlock DO? statementBlock ;

switchStatement:
    SWITCH '['  (ifStatement ';')* ']';

defineStatement:
     DEFINE '[' function ']' BODY? docStatementBlock;

lambdaStatement:
    function LambdaConnector  (statement) | statementBlock;

moduleStatement:
     MODULE LeftBracket STRING (',' STRING)? RightBracket BODY? docStatementBlock;

tryCatchStatement:
     TRY statementBlock CATCH statementBlock;

  assertStatement :
       ASSERT LeftBracket expression RightBracket LeftBracket expression RightBracket;

   blockStatement:
       BLOCK statementBlock;

assertStatement2:
  ASSERT2 expression (':' expression)?;

    statementBlock : LeftBracket (statement ';')* RightBracket;
 docStatementBlock : LeftBracket fdoc* (statement ';')+ RightBracket;
   expressionBlock : LeftBracket expression ';' ( expression ';')+ RightBracket;
  conditionalBlock : LeftBracket expression RightBracket;
  //loopBlock : LeftBracket (variable '∈')? expression RightBracket;
   fdoc : FDOC;

   iInterval : LeftBracket expression? ';' expression (';' expression)? RightBracket;
   rInterval : LDoubleBracket expression? ';' expression ';' expression RDoubleBracket;


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
// and checking for regression. Also Antlr 4 interprets the #tag in the right hand column and
// will use these for generating method names in Java. Be careful of altering these, they are not comments!


expression
 :
   function                                                                    #functions
  | expression StemDot+ expression                                             #dotOp
  | expression postfix=StemDot                                                #dotOp2
   | (function | '(' f_args* ')')
       LambdaConnector (expression | expressionBlock)                          #lambdaDef
 | stemVariable                                                                #stemVar
 | stemList                                                                    #stemLi
 | rInterval                                                                   #realInterval
 | iInterval                                                                   #intInterval
 | expression ('~' | '~|' ) expression                                         #tildeExpression
 | expression postfix=('++' | '--')                                            #postfix
 | prefix=('++'|'--') expression                                               #prefix
 | expression Exponentiation expression                                        #powerExpression
 | expression op=(Times | Divide | '%' ) expression                            #multiplyExpression
 | (Plus | UnaryPlus | Minus | UnaryMinus) expression                          #unaryMinusExpression
 | expression op=('+' | '-' ) expression                                       #addExpression
 | expression op=(LessThan | GreaterThan | LessEquals | MoreEquals) expression #compExpression
 | expression op=(Equals | NotEquals) expression                               #eqExpression
 | expression op=RegexMatches expression                                       #regexMatches
 | expression And expression                                                   #andExpression
 | expression Or expression                                                    #orExpression
 | LogicalNot expression                                                       #notExpression
 | '(' expression ')'                                                          #association
 | expression '?' expression ':' expression                                    #altIFExpression
 | expression Backslash + expression                                           #restriction
// | expression '`'+ expression                                                  #index
// | expression '|'+ expression                                                  #stile
// | prefix=',' expression                                                       #unravel
// | expression '∈' expression                                                   #epsilon
 | STRING                                                                      #strings
 | integer                                                                     #integers
 | number                                                                      #numbers
 | variable                                                                    #variables
 | keyword                                                                     #keywords
 | Bool                                                                        #logical
 | Null                                                                        #null
 | expression  op=ASSIGN  expression                                           #assignment
 | ';'                                                                         #semi_for_empty_expressions
 ;

       variable : Identifier ;
         number : Decimal |  SCIENTIFIC_NUMBER;
        integer : Integer;

   keyword : ConstantKeywords;

