package edu.uiuc.ncsa.security.util.terminal;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 6/5/20 at  1:45 PM
 */
public class KeyStroke {
    ISO6429Terminal.CSI csi;

    public ISO6429Terminal.CSI getCsi() {
        return csi;
    }

    public void setCsi(ISO6429Terminal.CSI csi) {
        this.csi = csi;
    }

    public KeyStroke(char character) {
        this(KeyType.Character, character);
    }

    public KeyStroke(KeyType keyType, char character) {
        this(keyType, null);
        this.character = character;
    }
    public KeyStroke(KeyType keyType, ISO6429Terminal.CSI csi) {
           this.keyType = keyType;
           this.csi = csi;
       }

    KeyType keyType;
    char character;

    public KeyType getKeyType() {
        return keyType;
    }

    public void setKeyType(KeyType keyType) {
        this.keyType = keyType;
    }

    public char getCharacter() {
        return character;
    }

    public void setCharacter(char character) {
        this.character = character;
    }

    @Override
    public String toString() {
        return "KeyStroke{" +
                "keyType=" + keyType +
                ", character=\"" + character + "\""+
                ", csi=" + csi +
                '}';
    }
}
