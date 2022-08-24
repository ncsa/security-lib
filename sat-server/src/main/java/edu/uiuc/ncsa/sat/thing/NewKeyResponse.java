package edu.uiuc.ncsa.sat.thing;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 8/24/22 at  7:18 AM
 */
public class NewKeyResponse extends Response{
    public NewKeyResponse(Action action, byte[] key) {
        super(action);
        this.key = key;
    }

    public byte[] getKey() {
        return key;
    }

    public void setKey(byte[] key) {
        this.key = key;
    }

    byte[] key;
}
