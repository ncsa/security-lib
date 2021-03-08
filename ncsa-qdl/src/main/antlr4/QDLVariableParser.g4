/*
  This contains the functional grammar for variables (stem and scalar) plus constants.


*/
grammar QDLVariableParser;
 // stem_ref : StemStart  ((Identifier | integer) '.')*;
  variable : Identifier;
    number : Decimal | Integer;
   integer : Integer;
      fdoc : FDOC;

// Remember that changing this file is taking your life in your hands, since tiny changes here
// can completely change parsing in fundamental ways.

// Lexer stuff
// NOTE: ORDER MATTERS!! You can easily break the parser if you change the order of these
// so if you add a rule, you must re-run the tests and look for regression and be prepared
// to move the new rule around to the right spot.
/*
   Parsing variables separate from stems. Unless the marker for the stem is
   changed to something other than a period (!!!), it is impossible to get
   a separate parser rule for stems -- The integer indices always get picked up
   as integers first then are flagged as bad expressions.
   However, changing the stem marker is a fundamental change driven not by
   the notation -- which is simple and good -- but by my lack of parser
   writing skills. Probably means a parser rewrite, not just tweaks.
   Ultimate decision therefore is to keep it as is
   and someday, maybe rewrite the parser to have it identify stems as first
   class objects (rather than just have "variable" and teasing it out in Java).
*/
//   Stem_ref : Identifier '.';
    Integer : [0-9]+;
    Decimal : (Integer '.' Integer) | ('.' Integer);
 Identifier : [a-zA-Z_$#][a-zA-Z_$0-9#.]*;   // Implicit definition of stem variables here!
 //Identifier : [a-zA-Z_$#][a-zA-Z_$0-9#]*;   // No implicit definition of stem variables here!
       Bool : BOOL_TRUE | BOOL_FALSE;
     ASSIGN : ':=' | '+=' | '-=' | '*=' | '/=' | '%=' | '^=';
  FuncStart : [a-zA-Z_$#][a-zA-Z_$0-9#]* '(';
  //StemStart : Identifier '.';
  BOOL_TRUE : 'true';
 BOOL_FALSE : 'false';
       Null : 'null';
     STRING : '\'' (ESC|.)*? '\'';

/*
  Constants. These are here so they are lexical units and the parser can access them as such.
  Note: we have a single equals sign here as a lexical unit so the parser can later flag it as an error
  If it is not here, then statments like a=2 are turned into the variable a and the rest of the
  statement is simply ignored.  Since this is the most common error for beginners (using the wrong
  assignment operator), this just must get flagged as a syntax error in parsing.
*/
   LambdaConnector : '->';
             Times : '*';
            Divide : '/';
          PlusPlus : '++';
              Plus : '+';
        MinusMinus : '--';
             Minus : '-';
          LessThan : '<';
       GreaterThan : '>';
       SingleEqual : '=';
        LessEquals : '<=';
        MoreEquals : '>=';
            Equals : '==';
         NotEquals : '!=';
               And : '&&';
                Or : '||';
          Backtick : '`';
           Percent : '%';
             Tilde : '~';
               Dot : '.';

// The left bracket, as the end of a control statement, has to be found in the lexer.
   LeftBracket: ']';
   RightBracket: '[';

// Control structures.

         LogicalIf : 'if[';
       LogicalThen : ']then[';
       LogicalElse : ']else[';
         WhileLoop : 'while[';
           WhileDo : ']do[';
   SwitchStatement : 'switch[';
   DefineStatement : 'define[';
     BodyStatement : ']body[';
   ModuleStatement : 'module[';
     TryStatement  : 'try[';
   CatchStatement  : ']catch[';
StatementConnector : '][';

fragment ESC : '\\\'';

// C-style comments ok.
COMMENT :   '/*' .*? '*/' -> skip;

// C++ style comments ok.
LINE_COMMENT:   '//' ~[\r\n]* -> skip;

WS2 : [ \t\r\n]+ ->skip;

FDOC :  '>>' ~[\r\n]*;