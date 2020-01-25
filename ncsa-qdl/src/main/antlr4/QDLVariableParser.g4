/*
  This contains the functional grammar for variables (stem and scalar) plus constants.


*/
grammar QDLVariableParser;
  variable : ID;

   number : Number;


// Lexer stuff
// NOTE: ORDER MATTERS!! You can easily break the parser if you change the order of these
// so if you add a rule, you must re-run the tests and look for regression and be prepared
// to move the new rule around to the right spot.
     Number : [0-9]+ ('.')? [0-9]*;
         ID : [a-zA-Z_$][a-zA-Z_$0-9#.]*;
       Bool : BOOL_TRUE | BOOL_FALSE;
     ASSIGN : ':=';
   FuncStart: ID '(';
   BOOL_TRUE: 'true';
   BOOL_FALSE: 'false';
        Null : 'null';
     STRING : '\'' .*? '\'';


// Constants. These are here so they are lexical units and the parser can access them as such.

    Times   : '*';
    Divide  : '/';
    PlusPlus: '++';
       Plus : '+';
  MinusMinus: '--';
       Minus: '-';
    LessThan: '<';
 GreaterThan: '>';
  LessEquals: '<=';
  MoreEquals: '>=';
     Equals : '==';
  NotEquals : '!=';
        And : '&&';
         Or : '||';

// The left bracket, as the end of a control statement, has to be found in the lexer.
   LeftBracket: ']';

// Control structures.

      LogicalIf: 'if[';
    LogicalThen: ']then[';
    LogicalElse: ']else[';
      WhileLoop: 'while[';
        WhileDo: ']do[';
SwitchStatement: 'switch[';
DefineStatement: 'define[';
  BodyStatement: ']body[';
ModuleStatement: 'module[';
  TryStatement : 'try[';
CatchStatement : ']catch[';


// C-style comments ok.
COMMENT :   '/*' .*? '*/' -> skip;

// C++ style comments ok.
LINE_COMMENT:   '//' ~[\r\n]* -> skip;

WS2 : [ \t\r\n]+ ->skip;
