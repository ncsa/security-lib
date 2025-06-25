package edu.uiuc.ncsa.sas.cli;

import edu.uiuc.ncsa.sas.Executable;
import edu.uiuc.ncsa.sas.StringIO;
import edu.uiuc.ncsa.sas.exceptions.SASException;
import edu.uiuc.ncsa.sas.thing.action.Action;
import edu.uiuc.ncsa.sas.thing.action.ExecuteAction;
import edu.uiuc.ncsa.sas.thing.response.OutputResponse;
import edu.uiuc.ncsa.sas.thing.response.Response;
import edu.uiuc.ncsa.sas.webclient.Client;
import edu.uiuc.ncsa.security.core.exceptions.NotImplementedException;
import edu.uiuc.ncsa.security.util.cli.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static edu.uiuc.ncsa.sas.SASConstants.ACTION_EXECUTE;
import static edu.uiuc.ncsa.sas.SASConstants.ACTION_INVOKE;

/**
 * Extends {@link CLIDriver} tp run as a SAS executable on the server. Implement this
 * to do whatever you need it to. The server runs a copy of this, populated by a
 * {@link Commands} object, and the client uses this for straight IO.
 * as well.
 * <p>Created by Jeff Gaynor<br>
 * on 3/7/24 at  2:33 PM
 */
public class SASCLIDriver extends CLIDriver implements Executable {
    public SASCLIDriver(IOInterface ioInterface, Client sasClient) throws Exception {
        CLI_IO cliIO = new CLI_IO(ioInterface, sasClient);
        setIO(cliIO);
    }

    public SASCLIDriver(IOInterface ioInterface) throws Exception {
        setIO(ioInterface);
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
                if (rc == HELP_RC) {
                    InputLine inputLine = new InputLine(executeAction.getArg());
                    listCLIMethods(inputLine);
                }
                return new OutputResponse(action, ((StringIO) getIO()).getOutput().toString());
            case ACTION_INVOKE:
                throw new NotImplementedException("invoke not supported.");
        }
        throw new SASException("unknown action \"" + action.getType());
    }


    @Override
    public IOInterface getIO() {
        return super.getIOInterface();
    }

    @Override
    public void setIO(IOInterface io) {
        super.setIOInterface(io);
    }

    protected void init() {

    }

    public static class DC implements Commands {
        @Override
        public String getPrompt() {
            return getName() + ">";
        }

        @Override
        public String getName() {
            return "sas";
        }

        @Override
        public void print_help() throws Exception {

        }

        @Override
        public void setDriver(CLIDriver driver) {
            this.driver = driver;
        }

        CLIDriver driver;

        @Override
        public CLIDriver getDriver() {
            return driver;
        }

        @Override
        public void bootstrap(InputLine inputLine) throws Throwable {

        }

        @Override
        public HelpUtil getHelpUtil() {
            return null;
        }

        @Override
        public IOInterface getIOInterface() {
            return null;
        }

        @Override
        public void setIOInterface(IOInterface io) {

        }

        @Override
        public boolean isDebugOn() {
            return false;
        }

        @Override
        public void setDebugOn(boolean setOn) {

        }

        @Override
        public void debug(String x) {

        }

        @Override
        public void info(String x) {

        }

        @Override
        public void warn(String x) {

        }

        @Override
        public void error(String x) {

        }
    }

    public static void main(String[] args) {
        try {
            List argList = new ArrayList();
            argList.add("sas");
            argList.addAll(Arrays.asList(args));
            Client sasClient = Client.newInstance(new InputLine(argList));

            SASCLIDriver sascliDriver = new SASCLIDriver(new BasicIO(), sasClient);
            sascliDriver.addCommands(new DC());
            sascliDriver.start();
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
}
