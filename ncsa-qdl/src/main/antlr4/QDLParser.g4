/*
   Top-level for the QDL language. This should contain only imports, no actual grammar
*/
grammar QDLParser;

import QDLVariableParser,QDLExprParser, QDLControlParser;

elements : element* EOF;