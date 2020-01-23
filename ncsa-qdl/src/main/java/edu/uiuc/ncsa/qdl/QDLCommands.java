package edu.uiuc.ncsa.qdl;

import edu.uiuc.ncsa.qdl.evaluate.MetaEvaluator;
import edu.uiuc.ncsa.qdl.expressions.OpEvaluator;
import edu.uiuc.ncsa.qdl.module.ModuleMap;
import edu.uiuc.ncsa.qdl.state.NamespaceResolver;
import edu.uiuc.ncsa.qdl.state.State;
import edu.uiuc.ncsa.qdl.state.SymbolStack;
import edu.uiuc.ncsa.qdl.state.SymbolTableImpl;
import edu.uiuc.ncsa.qdl.statements.FunctionTable;
import edu.uiuc.ncsa.security.core.util.MyLoggingFacade;
import edu.uiuc.ncsa.security.util.cli.CommonCommands;
import edu.uiuc.ncsa.security.util.cli.InputLine;
import net.sf.json.JSON;
import net.sf.json.JSONObject;

import java.io.*;
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
        say(OFF_COMMAND);
    }

    // String exitCommand = ")exit";
    State state;

    protected State getState() {
        if (state == null) {
            NamespaceResolver namespaceResolver = NamespaceResolver.getResolver();
            SymbolTableImpl symbolTable = new SymbolTableImpl(namespaceResolver);
            SymbolStack stack = new SymbolStack(namespaceResolver);
            stack.addParent(symbolTable);
            state = new State(NamespaceResolver.getResolver(),
                    stack,
                    new OpEvaluator(),
                    MetaEvaluator.getInstance(),
                    new FunctionTable(),
                    new ModuleMap());
        }
        return state;

    }

    protected String OFF_COMMAND = ")off";
    protected String VARS_COMMAND = ")vars";
    protected String FUNCS_COMMAND = ")funcs";
    protected String RUN_PROMPT = "    ";
    protected String DEBUG_PROMPT = ")debug"; // toggle debug
    protected String MODE_COMMAND = ")mode";
    protected String SAVE_COMMAND = ")save";
    protected void loadState(String path){
        try {

            FileReader fis  = new FileReader(path);
            BufferedReader bi = new BufferedReader(fis);
            String lineIn = bi.readLine();
            StringBuffer stringBuffer = new StringBuffer();
            while(lineIn != null){
                   stringBuffer.append(lineIn);
                   lineIn = bi.readLine();
            }
            bi.close();
            JSONObject jsonObject = JSONObject.fromObject(stringBuffer.toString());
            ModuleMap mm = new ModuleMap();
            mm.fromJSON(jsonObject.getJSONObject("module"));


        }catch(Throwable t){
            say("workspace not loaded.");
            t.printStackTrace();
        }
    }
     protected void saveState(String path){
         try {
             File f = new File(path);
             JSONObject jsonObject = new JSONObject();
             JSON json = state.getSymbolStack().toJSON();
             jsonObject.put("stack", json);
             JSON mm = state.getModuleMap().toJSON();
             jsonObject.put("modules", mm);
             FileWriter fw = new FileWriter("/tmp/qdl.ws");
             fw.write(jsonObject.toString(2));
             fw.flush();
             fw.close();

             say("Symbol table saved ");
         }catch(Throwable t){
             say("Symbol not table saved ");

             t.printStackTrace();
         }
     }
    public void run(InputLine inputLine) throws Throwable {
        boolean lineMode = inputLine.hasArg("-line");
        boolean isDebugOn = inputLine.hasArg("-debug");
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
            QDLInterpreter interpreter = new QDLInterpreter(getDriver().getEnv(), getState());
            interpreter.execute(xx);
            return;

        }

        if (lineMode) {
            say("Line mode. Every time you hit return, the line executes. Enter " + OFF_COMMAND + " to exit.");
        } else {
            say("Buffered mode. To execute the buffer, enter a single '.' or )exit to exit");
        }
        boolean isExit = false;
        StringBuffer buffer = new StringBuffer();
        QDLInterpreter interpreter = new QDLInterpreter(getDriver().getEnv(), getState());
        interpreter.setDebugOn(isDebugOn);
        if (isDebugOn) {
            say("parser debug mode enabled.");
        }
        while (!isExit) {
            if (lineMode) {
                System.out.print(RUN_PROMPT);
            }
            String input = readline().trim();
            if (input.equals(VARS_COMMAND)) {
                String vars = state.getSymbolStack().listVariables().toString().trim();
                vars = vars.substring(1); // chop off lead [
                vars = vars.substring(0, vars.length() - 1);
                say(vars);
                continue;
            }
            if (input.equals(DEBUG_PROMPT)) {
                isDebugOn = !isDebugOn;
                interpreter.setDebugOn(isDebugOn);

                say("Parser debug mode toggled to " + (isDebugOn ? "ON" : "OFF"));
                continue;
            }
            if (input.equals(SAVE_COMMAND)) {
                saveState("/tmp/qdl.ws");

                continue;
            }
            if (input.equals(OFF_COMMAND)) {
                say("exiting...");
                isExit = true;
                continue;
            }
            boolean isExecute = false;
            if (lineMode) {
                buffer.append(input + "\n");
                try {
                    interpreter.execute(buffer.toString());
                } catch (Throwable t) {
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
