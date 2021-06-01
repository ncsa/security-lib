/*
   Top-level for the QDL language. This should contain only imports, no actual grammar
*/
grammar QDLParser2;

import QDLVariableParser2,QDLExprParser, QDLControlParser;

elements : element* EOF;

