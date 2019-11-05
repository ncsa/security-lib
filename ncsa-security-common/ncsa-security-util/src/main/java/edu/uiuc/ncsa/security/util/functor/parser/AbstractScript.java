package edu.uiuc.ncsa.security.util.functor.parser;

import edu.uiuc.ncsa.security.core.exceptions.GeneralException;
import edu.uiuc.ncsa.security.util.functor.JFunctorFactory;
import edu.uiuc.ncsa.security.util.functor.logic.FunctorMap;
import edu.uiuc.ncsa.security.util.functor.parser.event.*;
import net.sf.json.JSONObject;

import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.util.LinkedList;
import java.util.List;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 3/7/19 at  6:52 PM
 */
public abstract class AbstractScript {
    /*
        public Script(JFunctorFactory functorFactory, JSONObject rawContent) {
        this.functorFactory = functorFactory;
        this.rawContent = rawContent;

    }

    public Script(JFunctorFactory functorFactory) {
        this.functorFactory = functorFactory;
    }
     */

    public AbstractScript(JFunctorFactory functorFactory) {
        this.functorFactory = functorFactory;
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
      /*  if (rawContent.containsKey(VERSION_KEY)) {
            String version = rawContent.getString(VERSION_KEY);
            if (!version.equals(VERSION_1_0)) {
                throw new UnsupportedVersionException("Error: This parser only supports version " + VERSION_1_0);
            }
        } else {
            throw new UnsupportedVersionException("Error: This parser only supports version " + VERSION_1_0);
        }*/
    }
}
