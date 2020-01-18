package edu.uiuc.ncsa.security.util.qdl;

import edu.uiuc.ncsa.security.util.qdl.expressions.FunctionEvaluator;
import edu.uiuc.ncsa.security.util.qdl.expressions.OpEvaluator;
import edu.uiuc.ncsa.security.util.qdl.util.SymbolStack;

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

    public QDLInterpreter(Map<String, String> environment, OpEvaluator opEvaluator, SymbolStack symbolTable, FunctionEvaluator functionEvaluator) {
        this.environment = environment;
        this.opEvaluator = opEvaluator;
        this.symbolTable = symbolTable;
        this.functionEvaluator = functionEvaluator;
    }

    SymbolStack symbolTable;
    OpEvaluator opEvaluator;
    FunctionEvaluator functionEvaluator;


    /**
     * Creates a new parser. NOTE Antlr <b>really</b> sucks at unbuffered input, so the official way to try and do this
     * is to create a new parser each time, but we manage the state in between. For working directly from
     * the command line this is ok but does not scale in any way.
     */
    public void execute(String line) throws Throwable {
        StringReader reader = new StringReader(line);
        QDLParserDriver driver = new QDLParserDriver(environment, symbolTable, opEvaluator, functionEvaluator);
        driver.setDebugOn(isDebugOn());
        driver.execute(reader);
    }

}
