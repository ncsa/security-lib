package edu.uiuc.ncsa.security.util.functor.parser;

import edu.uiuc.ncsa.security.core.exceptions.GeneralException;
import edu.uiuc.ncsa.security.util.functor.JFunctorFactory;
import edu.uiuc.ncsa.security.util.functor.LogicBlock;
import edu.uiuc.ncsa.security.util.functor.LogicBlocks;
import edu.uiuc.ncsa.security.util.functor.logic.FunctorMap;
import edu.uiuc.ncsa.security.util.functor.parser.event.*;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.LinkedList;
import java.util.List;

/**
 * A wrapper for a couple of type of scripting objects. This is to determine which is which and execute the script.
 * Either this has a JSONObject (like a {@link edu.uiuc.ncsa.security.util.functor.LogicBlock} or it has a JSONArray
 * of strings that are to be executed sequentially. <br>
 * Note that the factory and parser contain the environment of the execution. If an older set of logic blocks,
 * these are available in the {@link #getLogicBlocks()} call. If this is from a newer script, the complete set
 * of returned handlers from the parser is available in the {@link #getHandlers()} call.
 * <br>
 * The two formats for the JSON object are either
 * <pre>
 *      {"script":[
 *         "line 1",
 *         "line 2",
 *         ...
 *      ],
 *      "version":"1.0"}
 *  </pre>
 * where each line is a separate command (version is optional) or as described in the comment
 * to {@link JFunctorFactory#createLogicBlock(JSONObject)}
 * <br>Note this this has several convenience methods, such as {@link #getFunctorMap()}.<br>
 * Also, you may either create this and execute is against arguments <b><i>OR</i></b>
 * initialize it with JSON and execute it later when needed in your control flow. This
 * allows you to configure everything the way you need it up front.
 * <p>Created by Jeff Gaynor<br>
 * on 9/24/18 at  1:49 PM
 */
public class Script {
    public static String SCRIPT_KEY = "script";
    public static String VERSION_KEY = "version";
    public static final String VERSION_1_0 = "1.0";
    JSONObject rawContent = null;
    JFunctorFactory functorFactory = null;

    /**
     * Use this to initialize the entire parser in advance of execution. You may then execute it
     * by simply invoking the {@link #execute()} method when you need to.
     *
     * @param functorFactory
     * @param rawContent
     */
    public Script(JFunctorFactory functorFactory, JSONObject rawContent) {
        this.functorFactory = functorFactory;
        this.rawContent = rawContent;

    }

    public Script(JFunctorFactory functorFactory) {
        this.functorFactory = functorFactory;
    }

    public void execute() {
        if (rawContent == null) {
            throw new IllegalStateException("Error: no json has been set for this script");
        }
        execute(rawContent);
    }


    /**
     * Figure out what type of object is to be executed. Then run it.
     *
     * @throws Throwable
     */
    public void execute(JSONObject rawContent) {
        if (rawContent.containsKey(SCRIPT_KEY)) {
            executeScript(rawContent);
            return;
        }
        executeJSON(rawContent);
    }

    /**
     * Executes a file.
     *
     * @param file
     */
    public void execute(File file) {
        if (!file.exists()) {
            throw new IllegalStateException("Error: the file \"" + file.getAbsolutePath() + "\" does not exist");
        }
        try {
            FileReader fileReader = new FileReader(file);
            executeScript(ParserUtil.processInput(fileReader));
        } catch (Throwable throwable) {
            throw new GeneralException("Error parsing file. \"" + throwable.getMessage() + "\"", throwable);
        }
    }

    public void execute(List<String> commands) {
        executeScript(commands);
    }

    public void execute(Reader reader) {
        try {
            execute(ParserUtil.processInput(reader, true));
        } catch (Throwable throwable) {
            throw new ParserError("Error parsing input reader", throwable);
        }
    }

    protected EventDrivenParser createParser() {
        return new EventDrivenParser(functorFactory);
    }

    protected void checkVersion() {
      /*  if (rawContent.containsKey(VERSION_KEY)) {
            String version = rawContent.getString(VERSION_KEY);
            if (!version.equals(VERSION_1_0)) {
                throw new UnsupportedVersionException("Error: This parser only supports version " + VERSION_1_0);
            }
        } else {
            throw new UnsupportedVersionException("Error: This parser only supports version " + VERSION_1_0);
        }*/
    }


    protected void executeScript(JSONObject rawContent) {
        JSONArray array = rawContent.getJSONArray(SCRIPT_KEY);
        // now we have to turn it into a collection of lines that can be read correctly.
        // The collection of lines is just the same as any script in a file.
        StringBuffer buffer = new StringBuffer();
        for (Object obj : array) {
            buffer.append(obj.toString() + "\n");
        }
        StringReader reader = new StringReader(buffer.toString());
        execute(reader);
    }

    protected void executeScript(List<String> commands) {
        checkVersion();

        EventDrivenParser parser = createParser();
        functorMap = new FunctorMap();
        for (String command : commands) {
            try {
                AbstractHandler abstractHandler = parser.parse(command, functorFactory.getReplacementTemplates());

                handlers.add(abstractHandler);
                if (abstractHandler.getHandlerType() == AbstractHandler.SWITCH_TYPE) {
                    SwitchHandler s = (SwitchHandler) abstractHandler;
                    functorMap.addAll(s.getLogicBlocks().getFunctorMap());
                }
                if (abstractHandler.getHandlerType() == AbstractHandler.FUNCTOR_TYPE) {
                    FunctorHandler functorHandler = (FunctorHandler) abstractHandler;
                    functorMap.put(functorHandler.getFunctor());
                }
                if (abstractHandler.getHandlerType() == AbstractHandler.CONDITIONAL_TYPE) {

                    ConditionalHandler conditionalHandler = (ConditionalHandler) abstractHandler;
                    if (conditionalHandler.getLogicBlock() != null){
                        if (conditionalHandler.getLogicBlock().hasConsequent()) {
                            if (conditionalHandler.getLogicBlock().getConsequent().getFunctorMap() != null) {
                                functorMap.addAll(conditionalHandler.getLogicBlock().getConsequent().getFunctorMap());
                            }
                        }
                    }
                }
            } catch (Throwable t) {
                t.printStackTrace();
            }

        }
    }

    public boolean hasHandlers() {
        return 0 < handlers.size();
    }

    public boolean hasLogicBlocks() {
        return logicBlocks != null;
    }

    List<AbstractHandler> handlers = new LinkedList<>();
    LogicBlocks<? extends LogicBlock> logicBlocks = null;

    /**
     * Execute the old JSON functor and put it into a script object. The pre-supposes that the content is
     * functor notation and the functor factory will unscramble it. Then we just execute the result. The scripting
     * notation is in a JSONObject of the form
     * <pre>
     *     {"script":[array of lines as they are in a command file]}
     * </pre>
     * So be sure you are sending that if this method executes for no apparent reason. If the argument is not
     * in the above form, it falls through to this case since it can be hard to figure out and the factory is good
     * at that.
     *
     * @param rawContent
     */
    protected void executeJSON(JSONObject rawContent) {
        logicBlocks = functorFactory.createLogicBlock(rawContent);
        logicBlocks.execute();
        functorMap = logicBlocks.getFunctorMap();
    }

    public LogicBlocks<? extends LogicBlock> getLogicBlocks() {
        return logicBlocks;
    }

    public List<AbstractHandler> getHandlers() {
        return handlers;
    }

    FunctorMap functorMap;

    public FunctorMap getFunctorMap() {
        return functorMap;

    }
}
