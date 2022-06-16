package edu.uiuc.ncsa.qdl.workspace;

import edu.uiuc.ncsa.security.core.util.MyLoggingFacade;
import edu.uiuc.ncsa.security.util.terminal.ISO6429Terminal;
import edu.uiuc.ncsa.security.util.terminal.KeyStroke;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Keymap extensions for QDL only.
 * <p>Created by Jeff Gaynor<br>
 * on 6/10/21 at  11:35 AM
 */
public class QDLTerminal extends ISO6429Terminal {
    public QDLTerminal(MyLoggingFacade loggingFacade) throws IOException {
        super(loggingFacade);
    }

    static char[] charMap = null;

    public static char[] characterMap() {
        if (charMap == null) {
            charMap = new char[256];
            Arrays.fill(charMap, (char) 0);
            charMap['\''] = '»';
            charMap['{'] = '⟦';
            charMap['}'] = '⟧';
            charMap['<'] = '≤';
            charMap['>'] = '≥';
            charMap['='] = '≡';
            charMap['!'] = '¬';
            charMap['*'] = '×';
            charMap['/'] = '÷';
            charMap['&'] = '∧';
            charMap['|'] = '∨';
            charMap['+'] = '⁺';
            charMap['?'] = '≈';
            charMap['-'] = '¯';
            charMap['.'] = '·';
            charMap['a'] = '⊨';
            charMap['t'] = 'τ';
            charMap['d'] = '→';
            charMap['D'] = '∆';
            charMap['e'] = '∈';
            charMap['E'] = '∉';
            charMap['i'] = '∩';
            charMap['l'] = '⌊';
            charMap['k'] = '⌈';
            charMap['n'] = '∅';
            charMap['p'] = 'π';
            charMap['s'] = '⊢';
            charMap['u'] = '∪';
            charMap[':'] = '≔';
            charMap['"'] = '≕';
            charMap['\\'] = '≠';
            charMap['~'] = '≁';
            charMap['@'] = '⊗';
        }
        return charMap;
    }

    static Map<String, String> charLookupMap = null;

    public static Map<String, String> getCharLookupMap() {
        if (charLookupMap == null) {
            charLookupMap = new HashMap<>();
            char[] localChars = characterMap();
            for(int i = 0; i < localChars.length; i++){
                char c = localChars[i];
                if(c != 0){
                    charLookupMap.put(Character.toString(localChars[i]), Character.toString((char)i));
                }
            }
        }
        return charLookupMap;
    }

    @Override
    protected KeyStroke getKeyRemap(char y) throws IOException {
        return getKeyRemapNEW(y);
    }

    protected KeyStroke getKeyRemapNEW(char y) throws IOException {
        if (reader.peek(1) == -2) {
            char v = characterMap()[y];
            if (v != 0) return new KeyStroke(v);
        }
        return null;
    }

    protected KeyStroke getKeyRemapOLD(char y) throws IOException {
        if (reader.peek(1) == -2) {
            switch (y) {

                case '{':
                    return new KeyStroke('⟦');
                case '}':
                    return new KeyStroke('⟧');
                case '<':
                    return new KeyStroke('≤');
                case '>':
                    return new KeyStroke('≥');
                case '=':
                    return new KeyStroke('≡');
                case '!':
                    return new KeyStroke('¬');
                case '*':
                    return new KeyStroke('×');
                case '/':
                    return new KeyStroke('÷');
                case '&':
                    return new KeyStroke('∧');
                case '|':
                    return new KeyStroke('∨');
                case '+':
                    return new KeyStroke('⁺');
                case '?':
                    return new KeyStroke('≈');
                case '-':
                    return new KeyStroke('¯');
                case '.':
                    return new KeyStroke('·');
                case 'a':
                    return new KeyStroke('⊨');
                case 't':
                    return new KeyStroke('τ');
                case 'd':
                    return new KeyStroke('→');
                case 'D':
                    return new KeyStroke('∆');
                case 'e':
                    return new KeyStroke('∈');
                case 'E':
                    return new KeyStroke('∉');
                case 'i':
                    return new KeyStroke('∩');
                case 'l':
                    return new KeyStroke('⌊');
                case 'k':
                    return new KeyStroke('⌈');
                case 'n':
                    return new KeyStroke('∅');
                case 'p':
                    return new KeyStroke('π');
                case 's':
                    return new KeyStroke('⊢');
                case 'u':
                    return new KeyStroke('∪');
                case ':':
                    return new KeyStroke('≔');
                case '"':
                    return new KeyStroke('≕');
                case '\\':
                    return new KeyStroke('≠');
                case '~':
                    return new KeyStroke('≁');
                case '@':
                    return new KeyStroke('⊗');

            }

        }
        return null;
    }
}
