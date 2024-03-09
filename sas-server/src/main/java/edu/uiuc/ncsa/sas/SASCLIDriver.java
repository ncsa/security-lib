package edu.uiuc.ncsa.sas;

import edu.uiuc.ncsa.sas.exceptions.SASException;
import edu.uiuc.ncsa.sas.thing.action.Action;
import edu.uiuc.ncsa.sas.thing.action.ExecuteAction;
import edu.uiuc.ncsa.sas.thing.response.OutputResponse;
import edu.uiuc.ncsa.sas.thing.response.Response;
import edu.uiuc.ncsa.security.core.exceptions.NotImplementedException;
import edu.uiuc.ncsa.security.util.cli.CLIDriver;
import edu.uiuc.ncsa.security.util.cli.Commands;
import edu.uiuc.ncsa.security.util.cli.ExitException;
import edu.uiuc.ncsa.security.util.cli.IOInterface;

import static edu.uiuc.ncsa.sas.SASConstants.ACTION_EXECUTE;
import static edu.uiuc.ncsa.sas.SASConstants.ACTION_INVOKE;

/**
 * Extends {@link CLIDriver} tp run as a SAS executable on the server. Implement this
 * to
 * <p>Created by Jeff Gaynor<br>
 * on 3/7/24 at  2:33 PM
 */
public  class SASCLIDriver extends CLIDriver implements Executable {
    public SASCLIDriver(IOInterface ioInterface) {
        super(ioInterface);
    }

    public SASCLIDriver(Commands... cci) {
        super(cci);
    }

    @Override
    public Response execute(Action action) {
        switch (action.getType()) {
            case ACTION_EXECUTE:
                ExecuteAction executeAction = (ExecuteAction) action;
                int rc = execute(executeAction.getArg());
                if (rc == SHUTDOWN_RC || rc == USER_EXIT_RC) {
                    throw new ExitException();
                }
                return new OutputResponse(action, ((StringIO) getIO()).getOutput().toString());
            case ACTION_INVOKE:
                throw new NotImplementedException("invoke not supported.");
        }
        throw new SASException("unknown action \"" + action.getType());
    }

    IOInterface ioInterface;

    @Override
    public IOInterface getIO() {
        return ioInterface;
    }

    @Override
    public void setIO(IOInterface io) {
        this.ioInterface = io;
    }

    protected void init() {

    }
}
