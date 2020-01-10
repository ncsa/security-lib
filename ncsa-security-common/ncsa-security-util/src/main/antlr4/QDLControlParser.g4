/*

   This contains the parser elements for parsing conditionals, the while loop and switch.
*/
grammar QDLControlParser;

import QDLVariableParser,QDLExprParser;

element : statement ';' ;

statement :
            defineStatement
          | conditionalStatement
          | loopStatement
          | switchStatement
          | assignment
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