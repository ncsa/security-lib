/*

   This contains the parser elements for parsing conditionals, the while loop and switch.
*/
grammar QDLControlParser;

import QDLVariableParser,QDLExprParser;

element : (statement ';' ) | moduleStatement ;

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
	:	LogicalIf expression LogicalThen (statement ';')* LeftBracket;


ifElseStatement
	:	LogicalIf expression LogicalThen (statement ';')* LogicalElse (statement ';')* LeftBracket;

conditionalStatement : ( ifElseStatement | ifStatement);

loopStatement:
     WhileLoop expression WhileDo (statement ';')* LeftBracket;

switchStatement:
    SwitchStatement (ifStatement ';')* LeftBracket;

defineStatement:
     DefineStatement function BodyStatement (statement ';')* LeftBracket;

moduleStatement:
     ModuleStatement (statement ';')* BodyStatement (statement ';')* LeftBracket;

tryCatchStatement:
     TryStatement (statement ';')* CatchStatement (statement ';')* LeftBracket;