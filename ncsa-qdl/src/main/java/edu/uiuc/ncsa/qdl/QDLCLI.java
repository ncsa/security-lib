package edu.uiuc.ncsa.qdl;

import edu.uiuc.ncsa.security.core.util.MyLoggingFacade;
import edu.uiuc.ncsa.security.util.cli.CLIDriver;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 1/11/20 at  4:24 PM
 */
public class QDLCLI {
    public static void main(String[] args){
        QDLCommands commands = new QDLCommands(new MyLoggingFacade("QDL"));
        CLIDriver driver = new CLIDriver(commands);
        driver.start();

    }
}
