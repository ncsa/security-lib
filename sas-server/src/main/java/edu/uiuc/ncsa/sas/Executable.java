package edu.uiuc.ncsa.sas;

import edu.uiuc.ncsa.sas.thing.action.Action;
import edu.uiuc.ncsa.sas.thing.response.Response;
import edu.uiuc.ncsa.security.util.cli.IOInterface;

/**
 * This is the executable that is run on the server. When a client makes a call to the
 * SAS servlet, the {@link SASServlet#createExecutable()} is invoked and an instance of
 * this is created. Generally if you want to run (usually character mode) program X on the server, you extend it
 * to implement this (taking care that it manages state sanely) and set its {@link IOInterface}
 * to be {@link StringIO}, which intercepts all output statements and input statements.
 * <p>Created by Jeff Gaynor<br>
 * on 8/15/22 at  3:41 PM
 */
public interface Executable {
    public Response execute(Action action);
    public IOInterface getIO();
    public void setIO(IOInterface io);
}
