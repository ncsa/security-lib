package edu.uiuc.ncsa.sas.example;

import edu.uiuc.ncsa.sas.Executable;
import edu.uiuc.ncsa.sas.SASConstants;
import edu.uiuc.ncsa.sas.StringIO;
import edu.uiuc.ncsa.sas.thing.action.Action;
import edu.uiuc.ncsa.sas.thing.action.ExecuteAction;
import edu.uiuc.ncsa.sas.thing.action.InvokeAction;
import edu.uiuc.ncsa.sas.thing.response.OutputResponse;
import edu.uiuc.ncsa.sas.thing.response.Response;
import edu.uiuc.ncsa.security.util.cli.IOInterface;

/**
 * Really simple implementation of an {@link Executable}. This handles the execute and invoke actions.
 * They both just echo back whatever the user types.
 * <p>Created by Jeff Gaynor<br>
 * on 4/27/23 at  9:56 AM
 */
public class EchoExecutable implements Executable {
    @Override
    public Response execute(Action action) {
        StringBuilder output;
        switch (action.getType()) {
            case SASConstants.ACTION_EXECUTE:
                ExecuteAction executeAction = (ExecuteAction) action;
                getIO().println("test: execute(" + executeAction.getArg() + ")");
                break;
            case SASConstants.ACTION_INVOKE:
                InvokeAction invokeAction = (InvokeAction) action;
                getIO().println("test: " + invokeAction.getName() + "(" + invokeAction.getArgs() + ")");
                break;
            default:
                getIO().println("test exec, got action:" + action.getType());
        }
        output = ((StringIO) getIO()).getOutput();
        return new OutputResponse(action, output.toString());

    }

    IOInterface ioInterface = new StringIO("");

    @Override
    public IOInterface getIO() {
        return ioInterface;
    }

    @Override
    public void setIO(IOInterface io) {
        ioInterface = io;
    }
}
