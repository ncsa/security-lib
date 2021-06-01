/*

   This contains the parser elements for parsing conditionals, the while loop and switch.
*/
grammar QDLControlParser;

import QDLVariableParser2,QDLExprParser;

element : (statement ';' ) | (moduleStatement ';') ;

statement :
            defineStatement
          | conditionalStatement
          | loopStatement
          | switchStatement
          | expression
          | assignment
          | tryCatchStatement
          | lambdaStatement
          ;

 assignment : (expression  op=ASSIGN)+  expression;

 conditionalStatement : ifStatement | ifElseStatement;
 
ifStatement  :
//		LogicalIf expression (LogicalThen | StatementConnector) (statement ';')* RightBracket;
     IF conditionalBlock THEN? statementBlock;
//     IF '[' expression ']' THEN? (EmptyBlock | '[' (statement ';')+ ']');

ifElseStatement :
	//	LogicalIf expression (LogicalThen | StatementConnector) (statement ';')* LogicalElse (statement ';')* RightBracket;
	  IF conditionalBlock THEN? statementBlock ELSE statementBlock;
    //  IF '[' expression ']' THEN? (EmptyBlock | '[' (statement ';')+ ']') ELSE '[' (statement ';')* ']';



loopStatement:
     //WhileLoop expression (WhileDo | StatementConnector) (statement ';')* RightBracket;
     WHILE conditionalBlock DO? statementBlock ;

switchStatement:
    SWITCH '['  (ifStatement ';')* ']';

defineStatement:
     //DefineStatement function (BodyStatement | StatementConnector) fdoc* (statement ';')+ RightBracket;
     DEFINE '[' function ']' BODY? docStatementBlock;

lambdaStatement:
    //function LambdaConnector  (statement) | (LeftBracket  (statement ';')+ RightBracket)  ;
    function LambdaConnector  (statement) | statementBlock  ;

moduleStatement:
     //ModuleStatement STRING (',' STRING)? (BodyStatement | StatementConnector) fdoc* (statement ';')* RightBracket;
     MODULE LeftBracket STRING (',' STRING)? RightBracket BODY? docStatementBlock;

tryCatchStatement:
     //TryStatement (statement ';')* CatchStatement (statement ';')* RightBracket;
     TRY statementBlock CATCH statementBlock;


    statementBlock : LeftBracket (statement ';')* RightBracket;
 docStatementBlock : LeftBracket fdoc* (statement ';')+ RightBracket;
   expressionBlock : LeftBracket expression (',' expression)+ RightBracket;
  conditionalBlock : LeftBracket expression RightBracket;
  //moduleBlock : LeftBracket STRING (',' STRING)? RightBracket;
   fdoc : FDOC;


