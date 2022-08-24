package edu.uiuc.ncsa.sat;

import edu.uiuc.ncsa.sat.thing.Action;
import edu.uiuc.ncsa.security.util.cli.IOInterface;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 8/15/22 at  3:41 PM
 */
public interface Executable {
    public void execute(Action action);
    public IOInterface getIO();
    public void setIO(IOInterface io);
}
