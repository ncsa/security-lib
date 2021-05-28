/*
  This contains the functional grammar for variables (stem and scalar) plus constants.


*/
grammar QDLVariableParser;
 // stem_ref : StemStart  ((Identifier | integer) '.')*;
  variable : Identifier;
    number : Decimal | Integer | SCIENTIFIC_NUMBER;
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
 Identifier : [a-zA-Z_$#\u03b1-\u03c9\u0391-\u03a9][a-zA-Z_$0-9#\u03b1-\u03c9\u0391-\u03a9.]*;   // Implicit definition of stem variables here!
 //Identifier : [a-zA-Z_$#][a-zA-Z_$0-9#]*;   // No implicit definition of stem variables here!
         Bool : BOOL_TRUE | BOOL_FALSE;
       ASSIGN : '≔' | ':=' | '≕' | '=:' | '+=' | '-=' | (Times '=') | (Divide '=') | '%=' | '^=' ;  // unicode 2254, 2255
    FuncStart :  FUNCTION_NAME '(';
        F_REF : '@' (AllOps | FUNCTION_NAME | (FuncStart ')'));
    BOOL_TRUE : 'true';
   BOOL_FALSE : 'false';
         Null : 'null' | '∅';  // unicode 2205
        STRING : '\'' StringCharacters? '\'';

      Decimal : (Integer '.' Integer) | ('.' Integer);
    // AllOps must be a fragment or every bloody operator outside of a function reference will
    // get flagged as a possible match.

fragment AllOps :
     Times | Divide | Plus | Minus | LessThan | LessEquals | GreaterThan | Exponentiation |
     LessEquals | MoreEquals | Equals | NotEquals | And | Or | Percent | Tilde | LogicalNot |
     RegexMatches;

fragment FUNCTION_NAME :
     [a-zA-Z_$#\u03b1-\u03c9\u0391-\u03a9][a-zA-Z_$0-9#\u03b1-\u03c9\u0391-\u03a9]*;

SCIENTIFIC_NUMBER : Decimal (E SIGN? Integer)?;
       fragment E : 'E' | 'e';
    fragment SIGN : ('+' | '-');

/*
  Constants. These are here so they are lexical units and the parser can access them as such.
  Note: we have a single equals sign here as a lexical unit so the parser can later flag it as an error
  If it is not here, then statments like a=2 are turned into the variable a and the rest of the
  statement is simply ignored.  Since this is the most common error for beginners (using the wrong
  assignment operator), this just must get flagged as a syntax error in parsing.
*/

 // Note that the extra characters for && and || are there because certain unicode aware keyboards
 // have them rather than the correct one. \u2227 \u2228 are for n-ary expressions properly

// The left bracket, as the end of a control statement, has to be found in the lexer.

   LambdaConnector : '->' | '→';  // unicode 2192
             Times : '*'  | '×';  // unicode d7
            Divide : '/'  | '÷';  // unicode f7
          PlusPlus : '++';
              Plus : '+';
        MinusMinus : '--';
             Minus : '-';
          LessThan : '<';
       GreaterThan : '>';
       SingleEqual : '='; 
        LessEquals : '<=' | '≤' | '=<';  // unicode 2264
        MoreEquals : '>=' | '≥' | '=>';  // unicode 2265
            Equals : '==' | '≡';  // unicode 2261
         NotEquals : '!=' | '≠';  // unicode 2260
      RegexMatches : '=~' | '≈';  // unicode 2248
        LogicalNot : '!'  | '¬';  // unicode ac
    Exponentiation : '^';
               And : '&&' | '⋀' | '∧'; // unicode 22c0, 2227
                Or : '||' | '⋁' | '∨'; // unicode 22c2, 2228
          Backtick : '`';
           Percent : '%';
             Tilde : '~';
         Backslash : '\\';
             Stile : '|';
        TildeRight : '~|';
       LeftBracket : ']';
      RightBracket : '[';

 //            Dot : '.';

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

/*
  Next bit is for string support. Allow unicode, line feed etc. Disallow control characters.
*/
               fragment ESC : '\\' [btnfr'\\] | UnicodeEscape;
     fragment UnicodeEscape :  '\\' 'u'+  HexDigit HexDigit HexDigit HexDigit;
          fragment HexDigit : [0-9a-fA-F];
  fragment StringCharacters : StringCharacter+;
   fragment StringCharacter : ~['\\\r\n] | ESC;

/*
   Comments and white space
*/
           FDOC :  '>>' ~[\r\n]*;
             WS : [ \t\r\n\u000C]+ -> skip;
        COMMENT : '/*' .*? '*/' -> skip;
   LINE_COMMENT : '//' ~[\r\n]* -> skip;