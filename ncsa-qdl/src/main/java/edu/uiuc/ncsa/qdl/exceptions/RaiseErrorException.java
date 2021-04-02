package edu.uiuc.ncsa.qdl.exceptions;

import edu.uiuc.ncsa.qdl.expressions.Polyad;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 1/24/20 at  3:48 PM
 */
public class RaiseErrorException extends QDLException {
    public Polyad getPolyad() {
        return polyad;
    }

    public void setPolyad(Polyad polyad) {
        this.polyad = polyad;
    }

    Polyad polyad;

    public RaiseErrorException(Polyad polyad) {
        this.polyad = polyad;
    }
    public RaiseErrorException(Polyad polyad, String message) {
        super(message);
        this.polyad = polyad;

    }
}
