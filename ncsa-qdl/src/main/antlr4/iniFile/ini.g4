/*
   Attempt at making an ini (initialization) file grammar for QDL. This should
   be sort of like a Windows or python ini file in that it has sections and
   key/value pairs. The intent is to make a plain text representation of a
   very simple stem. So
   [section0]
   key0 = value0
   key1 = value1
   [section1]
   key3 = value3
   key4 = value4

   Would turn into
   my_ini. := {'section0':{'key0':value0, 'key1':value1}, 'section1':{'key4':value3, 'key4':value4}}
   with access pattern like

   my_ini.section0.key0

   lines allow for boolean, integer, decimal, scientific or string (in quotes), also
         lists if commas between elements, so

   key5 = 345,-3.14159,'foo'

   would be the stem entry [345, -3.14159, foo]

   Grammar itself is really short -- 6 statements. Rest of this is boiler-plated from
   QDL lexer for consistent behavior. Changes there need to be reflected
   here if applicable

   Major difference with QDL
   - only left-hand assignments
   - := and = both works as assignments
   - decimals do not need a lead 0, so ¯.123 is fine here
   - lists do not use []'s. Those go around section headings.
   - EOL (end of line) ends a statement not a ;.

   Differences with python/windows
   - entries are typed as boolen, number, string or aggregate
   - lines and sections may have valid QDL identifiers, including some unicode
   - strings allow for escaped unicode
   - comments are line or block, not ; or #

   Improvement?
   - Let final , on a line denote continuation?
     Problem now is that ,,, is intepreted as missing values, so
     how to tell if the line continues or is just missing values?
     Alternate is to allow for a special character like \ or \\
     to be continuation
*/
grammar ini;
          ini : (section | EOL)*;
      section : sectionheader line* ;
sectionheader : '[' Identifier ']' EOL;
         line : (Identifier (Assign entries) EOL) | EOL ;
       entries: entry (',' entry?)*;
        entry : ConstantKeywords | Number | String;

/*
    We want to be consistent with QDL for much of the syntax, so we have boiler-plated these
    to ensure this.
*/

           ConstantKeywords: BOOL_TRUE | BOOL_FALSE ;
                UnaryMinus : '¯'; // unicode 00af raised unary minus.
                 UnaryPlus : '⁺' ;// unicode 207a raised unary plus.
                      Plus : '+';
                     Minus : '-';
             
                    Assign : '=' | ':=' | '≔';
                    String : '\'' StringCharacters? '\'';
              fragment ESC : '\\' [btnfr'\\] | UnicodeEscape;
    fragment UnicodeEscape :  '\\' 'u'+  HexDigit HexDigit HexDigit HexDigit;
         fragment HexDigit : [0-9a-fA-F];
 fragment StringCharacters : StringCharacter+;
   fragment StringCharacter : ~['\\\r\n] | ESC;

    Identifier :  [a-zA-Z_$\u03b1-\u03c9\u0391-\u03a9\u03d1\u03d6\u03f0\u03f1][a-zA-Z_$0-9\u03b1-\u03c9\u0391-\u03a9\u03d1]*;   // no .!

      BOOL_FALSE : 'false'  | '⊥';
       BOOL_TRUE : 'true'   | '⊤'; // unicode 22a4
            Bool : BOOL_TRUE | BOOL_FALSE;
          Number : SIGN? Integer | Decimal |  SCIENTIFIC_NUMBER;

         Integer : [0-9]+;
          Decimal : Integer? '.' Integer;
SCIENTIFIC_NUMBER : SIGN? Decimal (E SIGN? Integer)?;
       fragment E : 'E' | 'e';
    fragment SIGN : (Plus | UnaryPlus | Minus | UnaryMinus);


LINE_COMMENT : ('//') ~ [\r\n]* EOL -> skip;
     COMMENT : '/*' .*? '*/' -> skip;
         EOL : [\r\n];
          WS : [ \t\u000C]+ -> skip;