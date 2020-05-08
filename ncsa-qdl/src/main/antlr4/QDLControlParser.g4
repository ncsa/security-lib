/*

   This contains the parser elements for parsing conditionals, the while loop and switch.
*/
grammar QDLControlParser;

import QDLVariableParser,QDLExprParser;

element : (statement ';' ) | (moduleStatement ';') ;

statement :
            defineStatement
          | conditionalStatement
          | loopStatement
          | switchStatement
          | assignment
          | tryCatchStatement
          | expression
          ;



ifStatement
	:	LogicalIf expression (LogicalThen | StatementConnector) (statement ';')* LeftBracket;


ifElseStatement
	:	LogicalIf expression (LogicalThen | StatementConnector) (statement ';')* LogicalElse (statement ';')* LeftBracket;

conditionalStatement : ( ifElseStatement | ifStatement);

loopStatement:
     WhileLoop expression (WhileDo | StatementConnector) (statement ';')* LeftBracket;

switchStatement:
    SwitchStatement (ifStatement ';')* LeftBracket;


defineStatement:
     DefineStatement function (BodyStatement | StatementConnector) fdoc* (statement ';')+ LeftBracket;


moduleStatement:
     ModuleStatement STRING (',' STRING)? (BodyStatement | StatementConnector) (statement ';')* LeftBracket;

tryCatchStatement:
     TryStatement (statement ';')* CatchStatement (statement ';')* LeftBracket;


