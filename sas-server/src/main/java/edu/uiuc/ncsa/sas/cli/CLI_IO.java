package edu.uiuc.ncsa.sas.cli;

import edu.uiuc.ncsa.sas.thing.response.LogonResponse;
import edu.uiuc.ncsa.sas.thing.response.OutputResponse;
import edu.uiuc.ncsa.sas.thing.response.Response;
import edu.uiuc.ncsa.sas.webclient.Client;
import edu.uiuc.ncsa.sas.webclient.ResponseDeserializer;
import edu.uiuc.ncsa.security.util.cli.IOInterface;

import java.io.IOException;

/**
 * This implementation of the {@link IOInterface} is for SAS command line clients. It
 * will do the IO with an SAS server.
 */
public class CLI_IO implements IOInterface, Runnable {
    public CLI_IO(IOInterface localIO, Client sasClient) throws Exception {
        this.sasClient = sasClient;
        this.localIO = localIO;
        init();
    }

    public IOInterface getLocalIO() {
        return localIO;
    }

    public void setLocalIO(IOInterface localIO) {
        this.localIO = localIO;
    }

    IOInterface localIO;

    @Override
    public String readline(String prompt) throws IOException {
        return doExecute(getLocalIO().readline(prompt));
    }

    protected String doExecute(String input) throws IOException {
        String output = "";
        try {
            Response response = sasClient.doExecute(input);
            if (response instanceof OutputResponse) {
                String out = ((OutputResponse) response).getContent();
                getLocalIO().println(out);
            }else{
                System.out.println(getClass().getSimpleName() + " response:\n" + response);
            }
        } catch (Throwable t) {
            getLocalIO().println("could not contact service");
        }
        return output;

    }

    @Override
    public String readline() throws IOException {
        return doExecute(getLocalIO().readline());
    }

    @Override
    public void print(Object x) {
        getLocalIO().print(x);
    }

    @Override
    public void println(Object x) {
        getLocalIO().println(x);
    }

    @Override
    public void flush() {
        getLocalIO().flush();
    }

    @Override
    public void clearQueue() {

    }

    @Override
    public boolean isQueueEmpty() {
        return false;
    }

    @Override
    public void setBufferingOn(boolean bufferOn) {

    }

    @Override
    public boolean isBufferingOn() {
        return false;
    }

    @Override
    public void run() {

    }

    Client sasClient;

    /**
     * Initialize the service client. At the end of this call, the CLI
     * will be communicating with the SAS server and a session will have
     * been established.
     *
     * @throws Exception
     */
    protected void init() throws Exception {
        sasClient.setResponseDeserializer(new ResponseDeserializer());
        LogonResponse logonResponse = (LogonResponse) sasClient.doLogon();
    }
}
