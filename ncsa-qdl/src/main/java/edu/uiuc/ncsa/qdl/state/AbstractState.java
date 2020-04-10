package edu.uiuc.ncsa.qdl.state;

import edu.uiuc.ncsa.qdl.evaluate.MetaEvaluator;
import edu.uiuc.ncsa.qdl.evaluate.OpEvaluator;
import edu.uiuc.ncsa.qdl.module.Module;
import edu.uiuc.ncsa.qdl.module.ModuleMap;
import edu.uiuc.ncsa.qdl.statements.FunctionTable;
import edu.uiuc.ncsa.qdl.variables.StemVariable;
import edu.uiuc.ncsa.security.core.Logable;
import edu.uiuc.ncsa.security.core.util.MyLoggingFacade;
import edu.uiuc.ncsa.security.util.scripting.StateInterface;

import java.util.HashMap;
import java.util.Map;

/**
 * This helps us organize the functionality of the state object. There are
 * sublclasses of this that do specific tasks. The inheritance hierarchy is
 * <pre>
 * namespaces --> variables --> functions --. total state.
 * </pre>
 * <p>Created by Jeff Gaynor<br>
 * on 2/2/20 at  6:37 AM
 */
public abstract class AbstractState implements StateInterface, Logable {
    public MyLoggingFacade getLogger() {
        return logger;
    }

    public void setLogger(MyLoggingFacade logger) {
        this.logger = logger;
    }

    transient MyLoggingFacade logger; // makes no sense to serialize a logger.

    private static final long serialversionUID = 129348937L;

    public AbstractState(ImportManager importManager,
                         SymbolStack symbolStack,
                         OpEvaluator opEvaluator,
                         MetaEvaluator metaEvaluator,
                         FunctionTable functionTable,
                         ModuleMap moduleMap,
                         MyLoggingFacade myLoggingFacade) {
        this.importManager = importManager;
        this.symbolStack = symbolStack;
        this.metaEvaluator = metaEvaluator;
        this.opEvaluator = opEvaluator;
        this.moduleMap = moduleMap;
        this.functionTable = functionTable;
        this.logger = myLoggingFacade;
    }

    public FunctionTable getFunctionTable() {
        return functionTable;
    }

    public void setFunctionTable(FunctionTable functionTable) {
        this.functionTable = functionTable;
    }

    FunctionTable functionTable;

    public ModuleMap getModuleMap() {
        return moduleMap;
    }

    /*
    How's it work?
    ModuleMap has the templates keyed by uri.
    ImportedModules has instances from the ModuleMap with their own state, keyed by alias
    ImportManager lets us look up which alias goes with which MS without having to slog through all the modules
     */
    public void setModuleMap(ModuleMap moduleMap) {
        this.moduleMap = moduleMap;
    }

    protected ModuleMap moduleMap;
    ImportManager importManager;

    public ImportManager getImportManager() {
        return importManager;
    }

    public void setImportManager(ImportManager importManager) {
        this.importManager = importManager;
    }

    public SymbolStack getSymbolStack() {
        return symbolStack;

    }

    /**
     * Modules (with their state) that have been imported and are keyed by alias.
     *
     * @return
     */
    public Map<String, Module> getImportedModules() {
        return importedModules;
    }

    public void setImportedModules(Map<String, Module> importedModules) {
        this.importedModules = importedModules;
    }

    Map<String, Module> importedModules = new HashMap<>();

    public void setSymbolStack(SymbolStack symbolStack) {
        this.symbolStack = symbolStack;
    }

    public OpEvaluator getOpEvaluator() {
        return opEvaluator;
    }

    public void setOpEvaluator(OpEvaluator opEvaluator) {
        this.opEvaluator = opEvaluator;
    }

    public MetaEvaluator getMetaEvaluator() {

        return metaEvaluator;
    }

    public void setMetaEvaluator(MetaEvaluator metaEvaluator) {
        this.metaEvaluator = metaEvaluator;
    }

    SymbolStack symbolStack;
    transient MetaEvaluator metaEvaluator;
    transient OpEvaluator opEvaluator;

    public int getOperatorType(String name) {
        return getOpEvaluator().getType(name);
    }


    public abstract State newModuleState();

    public abstract State newStateWithImports();

    public abstract State newStateNoImports();

    @Override
    public boolean isDebugOn() {
        if (hasLogging()) {
            return false;
        }
        return logger.isDebugOn();
    }

    @Override
    public void setDebugOn(boolean setOn) {
        if (hasLogging()) {
            logger.setDebugOn(setOn);
        }
    }

    @Override
    public void debug(String x) {
        if (hasLogging()) {
            logger.debug(x);
        }

    }

    @Override
    public void info(String x) {
        if (hasLogging()) {
            logger.info(x);
        }

    }

    public boolean hasLogging() {
        return logger != null;
    }

    @Override
    public void warn(String x) {
        if (hasLogging()) {
            logger.warn(x);
        }
    }

    public void error(Throwable t) {
        if (hasLogging()) {
            logger.error(t);
        }
    }

    public void error(String message, Throwable t) {
        if (hasLogging()) {
            logger.error(message, t);
        }
    }

    @Override
    public void error(String x) {
        if (hasLogging()) {
            logger.error(x);
        }
    }
}
