package edu.uiuc.ncsa.qdl;

import edu.uiuc.ncsa.qdl.evaluate.MetaEvaluator;
import edu.uiuc.ncsa.qdl.evaluate.OpEvaluator;
import edu.uiuc.ncsa.qdl.extensions.QDLLoader;
import edu.uiuc.ncsa.qdl.extensions.QDLLoaderImpl;
import edu.uiuc.ncsa.qdl.module.Module;
import edu.uiuc.ncsa.qdl.module.ModuleMap;
import edu.uiuc.ncsa.qdl.state.NamespaceResolver;
import edu.uiuc.ncsa.qdl.state.State;
import edu.uiuc.ncsa.qdl.state.SymbolStack;
import edu.uiuc.ncsa.qdl.state.SymbolTableImpl;
import edu.uiuc.ncsa.qdl.statements.FunctionTable;
import edu.uiuc.ncsa.qdl.util.QDLVersion;
import edu.uiuc.ncsa.security.core.util.MyLoggingFacade;
import edu.uiuc.ncsa.security.util.cli.CommonCommands;
import edu.uiuc.ncsa.security.util.cli.InputLine;
import net.sf.json.JSON;
import net.sf.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.net.URI;
import java.util.HashMap;
import java.util.TreeSet;
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
            // Experiment for a Java module
        }
        return state;

    }

    protected void setupJavaModule(State state, QDLLoader loader) {
        for (Module m : loader.load()) {
            state.addModule(m); // done!
        }
    }

    protected String CLEAR_COMMAND = ")clear"; // reset the state to being new.
    protected String DEBUG_PROMPT = ")debug"; // toggle debug
    protected String FUNCS_COMMAND = ")funcs";
    protected String HELP_COMMAND = ")help"; // reset the state to being new.
    protected String OFF_COMMAND = ")off";
    protected String LOAD_COMMAND = ")load"; // grab a file and run it
    protected String BUFFER_COMMAND = ")buffer";
    protected String MODULES_COMMAND = ")modules";
    protected String SAVE_COMMAND = ")save";
    protected String RUN_COMMAND = ")run";
    protected String VARS_COMMAND = ")vars";
    protected String RUN_PROMPT = "    ";

    protected void showRunHelp() {
        say("This is the QDL (Quick and Dirty Language, pronounced 'quiddle') workspace.");
        sayi("You may enter commands and execute them much like any other interpreter.");
        sayi("There are several commands available to help you manage this workspace.");
        sayi("Generally these start with a right parenthesis, e.g., ')off' (no quotes) exits this program.");
        sayi("Here is a quick summary of what they are and do.");
        sayii(CLEAR_COMMAND + " -- Clear the state of the workspace. All variables, functions etc. will be lost.");
        //sayii(DEBUG_PROMPT + " -- ");
        sayii(FUNCS_COMMAND + " -- List all of the imported and user defined functions this workspace knows about.");
        sayii(HELP_COMMAND + " -- This message.");
        sayii(OFF_COMMAND + " -- Exit the workpace without saving.");
        sayii(LOAD_COMMAND + " file -- Load a file of QDL commands and execute it immediately in the current workspace.");
        sayii(BUFFER_COMMAND + "ON|OFF -- Toggle between line mode (default, every line is executed on return) and ");
        sayii("      buffered mode, where lines are stored. Entering a single period as the first character on a line and hitting ");
        sayii("      return executes and flushes the buffer.");
        sayii(MODULES_COMMAND + " -- Lists all the imported modules this workspace knows about.");
        sayii(RUN_COMMAND + "file -- Loads a file into its own interpreter and runs it. Output comes to this terminal.");
        sayii(SAVE_COMMAND + " -- Save the current state of the workspace.");
        sayii(VARS_COMMAND + " -- Lists all of the variables this workspace knows about.");
    }

    protected void loadState(String path) {
        try {

            FileReader fis = new FileReader(path);
            BufferedReader bi = new BufferedReader(fis);
            String lineIn = bi.readLine();
            StringBuffer stringBuffer = new StringBuffer();
            while (lineIn != null) {
                stringBuffer.append(lineIn);
                lineIn = bi.readLine();
            }
            bi.close();
            JSONObject jsonObject = JSONObject.fromObject(stringBuffer.toString());
            ModuleMap mm = new ModuleMap();
            mm.fromJSON(jsonObject.getJSONObject("module"));


        } catch (Throwable t) {
            say("workspace not loaded.");
            t.printStackTrace();
        }
    }

    protected String getCommandArg(String command, String input) {
        return input.substring(command.length() + 1).trim();

    }

    protected void saveState(String path) {
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
        } catch (Throwable t) {
            say("Symbol not table saved ");

            t.printStackTrace();
        }
    }

    protected StringBuffer snarfFile(String name) {
        File f;
        File rootDir = new File("/home/ncsa/dev/ncsa-git/security-lib/ncsa-qdl/src/test/resources/");

        if (name == null || name.isEmpty()) {
            f = new File(rootDir, "recursion_test_fib.qdl");
        } else {
            f = new File(rootDir, name);

        }
        StringBuffer stringBuffer = new StringBuffer();

        try {
            FileReader fileReader = new FileReader(f);

            BufferedReader br = new BufferedReader(fileReader);
            String line = br.readLine();
            while (line != null) {
                stringBuffer.append(line + "\n"); // so it has lines to check if it blows up
                line = br.readLine();
            }
            br.close();
        } catch (Throwable t) {
            say("Sorry. I could not read the file \"" + name + "\"...");
            return null;
        }

        return stringBuffer;
    }

    public void run(InputLine inputLine) throws Throwable {
        boolean bufferMode = inputLine.hasArg("-buffer");
        boolean isDebugOn = inputLine.hasArg("-debug");
        say("*****************************************");
        say("QDL Command Line Interpreter");
        say("Version " + QDLVersion.VERSION);
        say("Type " + HELP_COMMAND + " for help.");
        say("*****************************************");
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
            QDLInterpreter interpreter = new QDLInterpreter(new HashMap<>(), getState());
            interpreter.execute(xx);
            return;

        }


        boolean isExit = false;
        StringBuffer buffer = new StringBuffer();
        QDLInterpreter interpreter = new QDLInterpreter(new HashMap<>(), getState());
        interpreter.setDebugOn(isDebugOn);
        if (isDebugOn) {
            say("parser debug mode enabled.");
        }
        while (!isExit) {
            if (!bufferMode) {
                System.out.print(RUN_PROMPT);
            }
            String input = readline().trim();
            if (input.equals(VARS_COMMAND)) {
                String vars = state.listVariables().toString().trim();
                vars = vars.substring(1); // chop off lead [
                vars = vars.substring(0, vars.length() - 1);
                say(vars);
                continue;
            }
            if (input.equals(HELP_COMMAND)) {
                showRunHelp();
                continue;
            }
            if (input.startsWith(RUN_COMMAND)) {
                String fileName = getCommandArg(RUN_COMMAND, input);
                StringBuffer stringBuffer = snarfFile(fileName);
                if (stringBuffer != null) {
                    NamespaceResolver namespaceResolver = new NamespaceResolver();
                    State runtime = new State(namespaceResolver,
                            new SymbolStack(namespaceResolver),
                            new OpEvaluator(),
                            new MetaEvaluator(),
                            new FunctionTable(),
                            new ModuleMap());
                    QDLInterpreter interpreter2 = new QDLInterpreter(new HashMap<>(), runtime);
                    try {
                        say("   >> running " + fileName);
                        interpreter2.execute(stringBuffer.toString());
                    } catch (Throwable t) {
                        say("oh-oh. That didn't work. Message was:" + t.getMessage());
                    }

                }
                continue;
            }
            if (input.equals(MODULES_COMMAND)) {
                TreeSet<String> m = new TreeSet<>();
                for (URI key : state.getModuleMap().keySet()) {
                    m.add(key + " = " + state.getModuleMap().get(key).getAlias());
                }
                String modules = m.toString();
                modules = modules.substring(1); // chop off lead [
                modules = modules.substring(0, modules.length() - 1);
                say(modules);
                continue;
            }
            if (input.startsWith(BUFFER_COMMAND)) {
                String command = getCommandArg(BUFFER_COMMAND, input);
                if (command.toLowerCase().equals("on")) {
                    bufferMode = true;
                    buffer = new StringBuffer(); // hello
                    sayi("Buffer mode on.");
                    continue;
                }
                if (command.toLowerCase().equals("off")) {
                    if (!(buffer == null || buffer.length() == 0)) {
                        say2("sure? (y/n)");
                        String in = readline();
                        if (in.equals("y")) {
                            buffer = new StringBuffer(); // bye-bye
                        }
                    }
                    bufferMode = false;
                    sayi("Line mode on.");

                    continue;
                }
                say("I'm sorry, I didn't understand \"" + command + "\". No change. Options are 'on' or 'off'.");
                continue;
            }
            if (input.startsWith(LOAD_COMMAND)) {
                String fileName = getCommandArg(LOAD_COMMAND, input);
                StringBuffer stringBuffer = snarfFile(fileName);
                if (stringBuffer != null) {
                    say("   >> loading " + fileName);

                    interpreter.execute(stringBuffer.toString());
                    say("ok");
                }

                continue;
            }
            if (input.equals(CLEAR_COMMAND)) {
                NamespaceResolver resolver = state.getResolver();
                State newState = new State(resolver,
                        new SymbolStack(resolver),
                        state.getOpEvaluator(),
                        state.getMetaEvaluator(),
                        new FunctionTable(),
                        new ModuleMap());
                state = newState;
                say("workspace cleared");
                continue;
            }
            if (input.equals(FUNCS_COMMAND)) {
                String funs = state.listFunctions().toString().trim();
                funs = funs.substring(1); // chop off lead [
                funs = funs.substring(0, funs.length() - 1);
                say(funs);
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
            if (!bufferMode) {
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

    protected static String DUMMY_FUNCTION = "dummy0"; // used to create initial command line

    public static void main(String[] args) throws Throwable {
        Vector<String> vector = new Vector<>();
        vector.add(DUMMY_FUNCTION); // Dummy zero-th arg.
        for (String arg : args) {
            vector.add(arg);
        }
        InputLine argLine = new InputLine(vector); // now we have a bunch of utilities for this
        QDLCommands qc = new QDLCommands(new MyLoggingFacade("QDLCommands"));
       QDLLoaderImpl q = new QDLLoaderImpl();
       
       System.out.println(q.getClass().getCanonicalName());
        State state = qc.getState();
        if (argLine.hasArg("-ext")) {
            String loaderClass = argLine.getNextArgFor("-ext");
            Class klasse = state.getClass().forName(loaderClass);
            QDLLoader loader = (QDLLoader) klasse.newInstance();
            qc.setupJavaModule(state, loader);
        }

        qc.run(new InputLine(new Vector()));

/*
        CLIDriver cli = new CLIDriver());
        // Easy case -- no arguments, so just start.
        if (args == null || args.length == 0) {
            cli.start();
            return;
        }
*/
    }
}
