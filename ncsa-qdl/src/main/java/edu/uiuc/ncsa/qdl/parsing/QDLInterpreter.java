package edu.uiuc.ncsa.qdl.parsing;

import edu.uiuc.ncsa.qdl.state.State;
import edu.uiuc.ncsa.security.core.configuration.XProperties;

import java.io.Reader;
import java.io.Serializable;
import java.io.StringReader;

/**
 * This is a facade for the various components of the parser and lexer.
 * It allows you to execute commands one at a time.<br/><br/>
 * Generally you want to use this for parsing. <br/>
 * <p>Created by Jeff Gaynor<br>
 * on 1/11/20 at  4:46 PM
 */
public class QDLInterpreter implements Serializable {
    boolean debugOn = false;

    public boolean isDebugOn() {
        return debugOn;
    }

    public void setDebugOn(boolean debugOn) {
        this.debugOn = debugOn;
    }

    XProperties environment;

    public QDLInterpreter(State state) {
        this.environment = new XProperties();
        this.state = state;
    }

    public QDLInterpreter(XProperties environment, State state) {
        this.environment = environment;
        this.state = state;
    }

    State state;

    public boolean isEchoModeOn() {
        return echoModeOn;
    }

    public void setEchoModeOn(boolean echoModeOn) {
        this.echoModeOn = echoModeOn;
    }

    boolean echoModeOn = false;

    /**
     * Creates a new parser. NOTE Antlr <b>really</b> sucks at unbuffered input, so the official way to try and do this
     * is to create a new parser each time, but we manage the state in between. For working directly from
     * the command line this is ok but does not scale in any way.
     */
    public void execute(String line) throws Throwable {
        StringReader reader = new StringReader(line);
        execute(reader);
    }

    public void execute(Reader r) throws Throwable {
        QDLParserDriver driver = new QDLParserDriver(environment, state);
        driver.setDebugOn(isDebugOn());
        QDLRunner runner = new QDLRunner(driver.parse(r));
        runner.setState(state);
        runner.setEchoModeOn(isEchoModeOn());
        runner.setPrettyPrint(isPrettyPrint());
        runner.setInterpreter(this); // needed for state indicator operations.
        runner.run();

    }

    public boolean isPrettyPrint() {
        return prettyPrint;
    }

    public void setPrettyPrint(boolean prettyPrint) {
        this.prettyPrint = prettyPrint;
    }

    boolean prettyPrint = false;
}
