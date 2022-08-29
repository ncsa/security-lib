package edu.uiuc.ncsa.sas;

import edu.uiuc.ncsa.sas.thing.action.Action;
import edu.uiuc.ncsa.sas.thing.response.Response;
import edu.uiuc.ncsa.security.util.cli.IOInterface;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 8/15/22 at  3:41 PM
 */
public interface Executable {
    public Response execute(Action action);
    public IOInterface getIO();
    public void setIO(IOInterface io);
}
