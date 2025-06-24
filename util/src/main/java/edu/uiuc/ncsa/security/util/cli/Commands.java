package edu.uiuc.ncsa.security.util.cli;

import edu.uiuc.ncsa.security.core.Logable;

import java.io.Serializable;

/**
 * All sets of commands implement this interface. Each command you want to have found in the interface should fulfill the
 * following contract;<br/>
 * <ul>
 * <li><b>signature requirement:</b> public void name({@link InputLine}</li>
 * <li><b>help requirement</b> a protected method, suggested name is "showXHelp" where "X" is the name of the method above</li>
 * </ul>
 * This means that if a user asks for help, the methods with the right signature will be displayed by the driver. All you need
 * to do is implement the functionality. The method is called by reflection on the name and is passed whatever arguments
 * are supplied by the user, parsed into a {@link InputLine}.
 * <p>There is no limit to the number or type of command that you may have here.</p>
 * Creation date: (10/11/02 8:26:14 pm)
 *
 * @author Jeff Gaynor
 */
public interface Commands extends Logable, Serializable {
    /**
     * The prompt displayed to the user. This allows it to change based on context.
     * @return java.lang.String
     */
    String getPrompt();
    void print_help() throws Exception;  // replaced by CLI Driver /help facility which is much better.

    void bootstrap() throws Throwable;

    HelpUtil getHelpUtil();
    IOInterface getIOInterface();
    void setIOInterface(IOInterface io);
    String getName();
    public boolean isBatchMode();
    public void setBatchMode(boolean batchMode);

    /**
     * Back reference to the driver that manages this instance.
     * @return
     */
    CLIDriver getDriver();
    void setDriver(CLIDriver driver);
}