package edu.uiuc.ncsa.qdl.statements;

import java.io.Serializable;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 11/4/21 at  7:05 AM
 */
public class TokenPosition implements Serializable {
    public TokenPosition(int line, int col) {
        this.col = col;
        this.line = line;
    }

    public int col = -1;
    public int line = -1;
}
