/*
  This contains the functional grammar for variables (stem and scalar) plus constants.


*/
lexer grammar QDLLexer;


// § 1 Keywords
  //ConstantKeywords: BOOL_TRUE | BOOL_FALSE | Null | Null_Set | COMPLEX_I;
  ConstantKeywords: BOOL_TRUE | BOOL_FALSE | Null | Null_Set;

         ASSERT : 'assert'; 
        ASSERT2 : '⊨'; // unicode 22a8
     BOOL_FALSE : 'false';
      BOOL_TRUE : 'true';
          BLOCK : 'block';
          LOCAL : 'local';
           BODY : 'body';
          CATCH : 'catch';
//      COMPLEX_I : 'I';
         DEFINE : 'define';
             DO : 'do';
           ELSE : 'else';
             IF : 'if';
         MODULE : 'module';
           Null : 'null';
       Null_Set : '∅';  // unicode 2205
         SWITCH : 'switch';
           THEN : 'then';
            TRY : 'try';
          WHILE : 'while';
/*
     Type_List : NULL2 | STRING2 | BOOLEAN2 | INTEGER2 | DECIMAL2 | NUMBER2 | LIST2 | SET2 | STEM2;

     BOOLEAN2 : 'Boolean';
     INTEGER2 : 'Integer';
     DECIMAL2 : 'Decimal';
      NUMBER2 : 'Number';
      STRING2 : 'String';
        NULL2 : 'Null';
        LIST2 : 'List';
        STEM2 : 'Stem';
         SET2 : 'Set';*/


// § 2 integers
      Integer : [0-9]+;

// § 3 Decimals, scientific notations

// Note you might be inclined to set this to
// Decimal : Integer? '.' Integer
// to  get numbers like .23, BUT, it will then misparse all stems like
// a.23 as a and .23 and bomb since there is no operator.
      Decimal : Integer '.' Integer;

/*
COMPLEX_NUMBER : NNN J NNN;
   fragment NNN : (Integer | Decimal | SCIENTIFIC_NUMBER);
       fragment J : 'J' | 'j';
*/

SCIENTIFIC_NUMBER : Decimal (E SIGN? Integer)?;
       fragment E : 'E' | 'e';
    fragment SIGN : (Plus | UnaryPlus | Minus | UnaryMinus);

// § 4 Booleans

         Bool : BOOL_TRUE | BOOL_FALSE;

// § 5 Strings. Allow unicode
/*
  Next bit is for string support. Allow unicode, line feed etc. Disallow control characters.
*/
                     STRING : '\'' StringCharacters? '\'';
               fragment ESC : '\\' [btnfr'\\] | UnicodeEscape;
     fragment UnicodeEscape :  '\\' 'u'+  HexDigit HexDigit HexDigit HexDigit;
          fragment HexDigit : [0-9a-fA-F];
  fragment StringCharacters : StringCharacter+;
   fragment StringCharacter : ~['\\\r\n] | ESC;

// § 6 Separators and brackets
       LeftBracket : '[';
      RightBracket : ']';
             Comma : ',';
             Colon : ':';
         SemiColon : ';';
    LDoubleBracket : '[|' |'⟦'; // unicode 27e6
    RDoubleBracket : '|]' |'⟧'; // unicode 27e7

// § 7 Operators
/*
  These are here so they are lexical units and the parser can access them as such.
  Note: we have a single equals sign here as a lexical unit so the parser can later flag it as an error
  If it is not here, then statments like a=2 are turned into the variable a and the rest of the
  statement is simply ignored.  Since this is the most common error for beginners (using the wrong
  assignment operator), this just must get flagged as a syntax error in parsing.
*/

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
            To_Set : '|>' | '⊢'; // unicode 22a2
        LessEquals : '<=' | '≤' ;  // unicode 2264
        MoreEquals : '>=' | '≥' ;  // unicode 2265
               IsA : '<<';
            Equals : '==' | '≡';  // unicode 2261
         NotEquals : '!=' | '≠';  // unicode 2260
      RegexMatches : '=~' | '≈';  // unicode 2248
        LogicalNot : '!'  | '¬';  // unicode ac
        Membership : '∈' | '∉'; // unicode 2208, 2209
    Exponentiation : '^';
    
    // Note that the extra characters for && and || are there because certain unicode aware keyboards
    // have them rather than the correct one. \u22c0 \u22c1 are for n-ary expressions properly
               And : '&&'  | '∧'  ; // unicode  2227
                Or : '||'  | '∨'  ; // unicode  2228
          Backtick : '`';
           Percent : '%' | '∆'; // \u2206 -- laplace or symm diff operator
             Tilde : '~';
         Backslash : '\\!' | '\\' ;
        Backslash2 : '\\!*' | '\\*' ;
        Backslash3 : '\\>!' | '\\>' ;
        Backslash4 : '\\>!*' | '\\>*' ;
              Hash : '#';
             Stile : '|';
        TildeRight : '~|' | '≁'; // unicode 2241, tilde slash
           StemDot : '.' ;
        UnaryMinus : '¯'; // unicode 00af raised unary minus.
         UnaryPlus : '⁺' ;// unicode 207a raised unary plus.
             Floor : '⌊'; // unicode 230a 
           Ceiling : '⌈'; // unicode 2308
     FunctionMarker: '@' | '⊗';  // unicode 2297
            ASSIGN : '≔' | ':=' | '≕' | '=:' | '+=' | '-=' | (Times '=') | (Divide '=') | '%=' | '^=' ;  // unicode 2254, 2255

// Remember that changing this file is taking your life in your hands, since tiny changes here
// can completely change parsing in fundamental ways.

// Lexer stuff
// NOTE: ORDER MATTERS!! You can easily break the parser if you change the order of these
// so if you add a rule, you must re-run the tests and look for regression and be prepared
// to move the new rule around to the right spot.

// § 8 Identifiers
   /*
      This include standard upper and lower case Greek letters as well as
      ϑ - var theta, \u03d1,
      ϖ - var pi, \u03d6
      ϰ - script kappa, \u03f0
      ϱ - var rho, \u03f1
   */
   Identifier :  ('&')?[a-zA-Z_$\u03b1-\u03c9\u0391-\u03a9\u03d1\u03d6\u03f0\u03f1][a-zA-Z_$0-9\u03b1-\u03c9\u0391-\u03a9\u03d1]*;   // no .!

    FuncStart :  FUNCTION_NAME '(';
        F_REF : FunctionMarker (AllOps | (Identifier Hash)* FUNCTION_NAME | (FuncStart ')'));  // This allows for @f and @f() as equivalent.

    // AllOps must be a fragment or every bloody operator outside of a function reference will
    // get flagged as a possible match.

fragment AllOps :
     Times | Divide | Plus | Minus | LessThan | LessEquals | GreaterThan | Exponentiation |
     LessEquals | MoreEquals | Equals | NotEquals | And | Or | Percent | Tilde | TildeRight |
     LogicalNot | RegexMatches | Floor | Ceiling | Membership | To_Set | IsA;

fragment FUNCTION_NAME :
     [a-zA-Z_$\u03b1-\u03c9\u0391-\u03a9\u03d1\u03d6\u03f0\u03f1][a-zA-Z_$0-9\u03b1-\u03c9\u0391-\u03a9\u03d1]*;

   //                       URL  : (Identifier Colon)  ((Identifier) Divide)* (Identifier)?;

/*
   § 9 Comments and white space
*/
           FDOC :  '»' ~[\r\n]*;
             WS : [ \t\r\n\u000C]+ -> skip;
        COMMENT : '/*' .*? '*/' -> skip;
   LINE_COMMENT : '//' ~[\r\n]* -> skip;