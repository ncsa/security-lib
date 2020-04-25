package edu.uiuc.ncsa.security.util.functor.parser;

import edu.uiuc.ncsa.security.core.configuration.XProperties;
import edu.uiuc.ncsa.security.core.exceptions.GeneralException;
import edu.uiuc.ncsa.security.util.functor.JFunctorFactory;
import edu.uiuc.ncsa.security.util.functor.LogicBlock;
import edu.uiuc.ncsa.security.util.functor.LogicBlocks;
import edu.uiuc.ncsa.security.util.functor.logic.FunctorMap;
import edu.uiuc.ncsa.security.util.functor.parser.event.*;
import edu.uiuc.ncsa.security.util.scripting.ScriptInterface;
import edu.uiuc.ncsa.security.util.scripting.StateInterface;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.LinkedList;
import java.util.List;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 3/7/19 at  6:52 PM
 */
public class FunctorScript implements ScriptInterface {


    public static String SCRIPT_KEY = "script";
    LogicBlocks<? extends LogicBlock> logicBlocks = null;

    public FunctorScript(JFunctorFactory functorFactory) {
        this.functorFactory = functorFactory;
    }
    /**
         * Use this to initialize the entire parser in advance of execution. You may then execute it
         * by simply invoking the {@link #execute()} method when you need to.
         *
         * @param functorFactory
         * @param rawContent
         */
        public FunctorScript(JFunctorFactory functorFactory, JSONObject rawContent) {
            this(functorFactory);
            this.rawContent = rawContent;
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
    List<AbstractHandler> handlers = new LinkedList<>();

    public boolean hasHandlers() {
        return 0 < handlers.size();
    }

    public List<AbstractHandler> getHandlers() {
        return handlers;
    }

    protected EventDrivenParser createParser() {
        return new EventDrivenParser(functorFactory);
    }

    JSONObject rawContent = null;
    JFunctorFactory functorFactory = null;

    FunctorMap functorMap;

    public FunctorMap getFunctorMap() {
        return functorMap;

    }

    protected void executeScript(List<String> commands) {
        checkVersion();
        if(commands== null || commands.isEmpty()){
            return;
        }
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
                    if (conditionalHandler.getLogicBlock() != null) {
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

    protected void checkVersion() {

    }

    @Override
    public XProperties getProperties() {
        return null;
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

    public boolean hasLogicBlocks() {
        return logicBlocks != null;
    }

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

    @Override
    public void execute(StateInterface state) {
        execute();
    }

    @Override
    public String toString() {
        return "FunctorScript{" +
                "rawContent=" + rawContent +
                ", functorMap=" + functorMap +
                '}';
    }
}
