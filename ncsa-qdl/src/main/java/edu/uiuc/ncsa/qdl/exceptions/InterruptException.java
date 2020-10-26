package edu.uiuc.ncsa.qdl.exceptions;

import edu.uiuc.ncsa.qdl.state.SIEntry;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 10/25/20 at  2:35 PM
 */
public class InterruptException extends QDLException {
    public InterruptException(SIEntry siEntry) {
        this.siEntry = siEntry;
    }

    public SIEntry getSiEntry() {
        return siEntry;
    }

    public void setSiEntry(SIEntry siEntry) {
        this.siEntry = siEntry;
    }

    SIEntry siEntry;

}
