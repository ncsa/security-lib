package edu.uiuc.ncsa.qdl.workspace;

import edu.uiuc.ncsa.security.core.util.MyLoggingFacade;
import edu.uiuc.ncsa.security.util.terminal.ISO6429Terminal;
import edu.uiuc.ncsa.security.util.terminal.KeyStroke;

import java.io.IOException;

/**
 * Keymap extensions for QDL only.
 * <p>Created by Jeff Gaynor<br>
 * on 6/10/21 at  11:35 AM
 */
public class QDLTerminal extends ISO6429Terminal {
    public QDLTerminal(MyLoggingFacade loggingFacade) throws IOException {
        super(loggingFacade);
    }
    @Override
    protected KeyStroke getKeyRemap(char y) throws IOException {
            if(reader.peek(1) == -2){
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
                    case 'd':
                        return new KeyStroke('→');
                    case 'f':
                        return new KeyStroke('⊥');
                    case 'n':
                        return new KeyStroke('∅');
                    case 'p':
                        return new KeyStroke('π');
                    case 't':
                        return new KeyStroke('⊤');
                    case ':':
                        return new KeyStroke('≔');
                    case '"':
                        return new KeyStroke('≕');
                    case '\\':
                        return new KeyStroke('≠');

                }

            }
            return null;
        }
}
