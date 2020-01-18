package edu.uiuc.ncsa.security.util.qdl;

import edu.uiuc.ncsa.security.core.util.MyLoggingFacade;
import edu.uiuc.ncsa.security.util.cli.CommonCommands;
import edu.uiuc.ncsa.security.util.cli.InputLine;
import edu.uiuc.ncsa.security.util.qdl.expressions.FunctionEvaluator;
import edu.uiuc.ncsa.security.util.qdl.expressions.OpEvaluator;
import edu.uiuc.ncsa.security.util.qdl.util.SymbolStack;

import java.util.Vector;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 1/11/20 at  4:21 PM
 */
public class QDLCommands extends CommonCommands {
    @Override
    public String getPrompt() {
        return "qdl>";
    }

    public QDLCommands(MyLoggingFacade logger) {
        super(logger);
    }

    protected void showRunHelp() {
        say("run [-x line | -line]");
        sayi("-x means to immediately execute the rest of the line and not enter the interpreter.");
        sayi("-line = run in line mode. Every line is executed. This lets you use this much like a calculator.");
        sayi("        (Be sure you end each command with a semicolon or you will get errors");
        say("No arguments means to buffer all input until you issue a single period (\".\") at the");
        say("beginning of the line, which will then run everything in the buffer since the time you did it.");
        say("(you can turn on debugging for the parser with the -debug flag. Use it wisely).");
        say(exitCommand);
    }

    String exitCommand = ")exit";

    public SymbolStack getSymbolStack() {
        if(symbolStack == null){
            symbolStack = new SymbolStack();
        }
        return symbolStack;
    }

    SymbolStack symbolStack;
    OpEvaluator opEvaluator = new OpEvaluator();
    FunctionEvaluator functionEvaluator = new FunctionEvaluator();

    public void run(InputLine inputLine) throws Throwable {
        if (showHelp(inputLine)) {
            showRunHelp();
            return;
        }
        if (inputLine.hasArg("-x")) {
            Vector v = inputLine.argsToVector();
            v.remove("-x");
            String xx = "";
            for (Object obj : v) {
                xx = xx + obj.toString();
            }
            QDLInterpreter interpreter = new QDLInterpreter(getDriver().getEnv(), opEvaluator, getSymbolStack(), functionEvaluator);
            interpreter.execute(xx);
            return;

        }
        boolean lineMode = inputLine.hasArg("-line");
        boolean isDebugOn = inputLine.hasArg("-debug");
        if (lineMode) {
            say("Line mode. Every time you hit return, the line executes. Enter " + exitCommand + " to exit.");
        } else {
            say("Buffered mode. To execute the buffer, enter a single '.' or )exit to exit");
        }
        boolean isExit = false;
        StringBuffer buffer = new StringBuffer();
        QDLInterpreter interpreter = new QDLInterpreter(getDriver().getEnv(), opEvaluator, getSymbolStack(), functionEvaluator);
         interpreter.setDebugOn(isDebugOn);
        while (!isExit) {
            if(lineMode){
                System.out.print(">");
            }
            String input = readline().trim();
            if (input.equals(exitCommand)) {
                say("exiting...");
                isExit = true;
                continue;
            }
            boolean isExecute = false;
            if (lineMode) {
                buffer.append(input + "\n");
                try {
                    interpreter.execute(buffer.toString());
                }catch(Throwable t){
                    t.printStackTrace();
                }
                buffer = new StringBuffer();

            } else {
                if (input.equals(".")) {
                    try {
                        interpreter.execute(buffer.toString());
                    } catch (Throwable t) {
                        t.printStackTrace();
                    }
                    buffer = new StringBuffer();
                } else {
                    buffer.append(input + "\n");
                }
            }

        }
    }
}
