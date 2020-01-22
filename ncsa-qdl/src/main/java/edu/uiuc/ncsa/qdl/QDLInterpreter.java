package edu.uiuc.ncsa.qdl;

import edu.uiuc.ncsa.qdl.parsing.QDLRunner;
import edu.uiuc.ncsa.qdl.state.State;

import java.io.StringReader;
import java.util.Map;

/**
 * This manages a parser and allows you to execute commands one at a time.
 * <p>Created by Jeff Gaynor<br>
 * on 1/11/20 at  4:46 PM
 */
public class QDLInterpreter {
    boolean debugOn = false;

    public boolean isDebugOn() {
        return debugOn;
    }

    public void setDebugOn(boolean debugOn) {
        this.debugOn = debugOn;
    }

    Map<String, String> environment;

    public QDLInterpreter(Map<String, String> environment, State state) {
        this.environment = environment;
        this.state = state;
    }

    State state;

    /**
     * Creates a new parser. NOTE Antlr <b>really</b> sucks at unbuffered input, so the official way to try and do this
     * is to create a new parser each time, but we manage the state in between. For working directly from
     * the command line this is ok but does not scale in any way.
     */
    public void execute(String line) throws Throwable {
        StringReader reader = new StringReader(line);
        QDLParserDriver driver = new QDLParserDriver(environment, state);
        driver.setDebugOn(isDebugOn());
        try {
            QDLRunner runner = new QDLRunner(driver.parse(reader));
            runner.setState(state);
            runner.run();

        }catch(IllegalStateException isx){
            isx.printStackTrace();
          System.out.println("syntax error");
        }
    }

}
