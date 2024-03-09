package edu.uiuc.ncsa.sas.cli;

import edu.uiuc.ncsa.sas.Executable;
import edu.uiuc.ncsa.sas.thing.action.Action;
import edu.uiuc.ncsa.sas.thing.response.Response;
import edu.uiuc.ncsa.security.util.cli.CLIDriver;
import edu.uiuc.ncsa.security.util.cli.Commands;
import edu.uiuc.ncsa.security.util.cli.IOInterface;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 3/6/24 at  3:02 PM
 */
public class SASServerSideCLiDriver extends CLIDriver implements Executable {
    public SASServerSideCLiDriver(Commands... cci) {
        super(cci);
    }

    @Override
    public Response execute(Action action) {
        return null;
    }


    @Override
    public IOInterface getIO() {
        return null;
    }

    @Override
    public void setIO(IOInterface io) {

    }
}
