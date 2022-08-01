package edu.uiuc.ncsa.security.util.cli;

import edu.uiuc.ncsa.security.core.Identifiable;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 5/22/14 at  9:42 AM
 */
public interface Sortable {
    /**
     * Does the actual sorting. The returned {@link ArrayList} is sorted according to the
     * current internal state of the object.
     * @param arg
     * @return
     */
    public ArrayList<Identifiable> sort(List<Identifiable> arg);

    /**
     * Given a string of arguments, set the internal state. Alternately, you can
     * set the state manually.
     * @param args
     */
    public void setState(String args);
}
