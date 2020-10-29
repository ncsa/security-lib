package edu.uiuc.ncsa.qdl.state;

import edu.uiuc.ncsa.qdl.parsing.QDLInterpreter;
import edu.uiuc.ncsa.qdl.parsing.QDLRunner;

import java.io.Serializable;
import java.util.Date;

/**
 * An entry in the SI (state indicator) table.
 * <p>Created by Jeff Gaynor<br>
 * on 10/25/20 at  2:35 PM
 */
public class SIEntry implements Serializable {
    public boolean initialized = false;
    public int pid = -1;
    public Date timestamp = new Date();
    public State state = null;
    public String message = null;
    public QDLRunner qdlRunner;  // needed to restart where the interpreter left off
    public int lineNumber = -1;
    public QDLInterpreter interpreter = null; // Can't be set at the point of the interrupt.
}
