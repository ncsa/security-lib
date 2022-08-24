package edu.uiuc.ncsa.sat.thing;

import edu.uiuc.ncsa.sat.SATConstants;

/**
 * Request a new symmetric key from the server with the given bit size. The format is
 * <pre>
 *     {"action":"new_key", "arg":int}
 * </pre>
 * <p>Created by Jeff Gaynor<br>
 * on 8/24/22 at  7:14 AM
 */
public class NewKeyAction extends Action{
    public NewKeyAction() {
        super(SATConstants.ACTION_NEW_KEY);
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    int size = 1024;
}
