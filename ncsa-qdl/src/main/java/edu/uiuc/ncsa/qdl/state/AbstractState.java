package edu.uiuc.ncsa.qdl.state;

import edu.uiuc.ncsa.qdl.evaluate.MetaEvaluator;
import edu.uiuc.ncsa.qdl.evaluate.OpEvaluator;
import edu.uiuc.ncsa.qdl.functions.FTStack;
import edu.uiuc.ncsa.qdl.module.Module;
import edu.uiuc.ncsa.qdl.module.ModuleMap;
import edu.uiuc.ncsa.qdl.vfs.VFSPaths;
import edu.uiuc.ncsa.security.core.Logable;
import edu.uiuc.ncsa.security.core.util.MyLoggingFacade;
import edu.uiuc.ncsa.security.util.cli.BasicIO;
import edu.uiuc.ncsa.security.util.cli.IOInterface;
import edu.uiuc.ncsa.security.util.scripting.StateInterface;

import java.util.*;

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

    public IOInterface getIoInterface() {
        if (ioInterface == null) {
            ioInterface = new BasicIO();
        }
        return ioInterface;
    }

    public void setIoInterface(IOInterface ioInterface) {
        this.ioInterface = ioInterface;
    }

    transient IOInterface ioInterface;

    public MyLoggingFacade getLogger() {
        return logger;
    }

    public void setLogger(MyLoggingFacade logger) {
        this.logger = logger;
    }

    transient MyLoggingFacade logger; // makes no sense to serialize a logger.

    private static final long serialVersionUID = 0xcafed00d3L;

    public AbstractState(ImportManager importManager,
                         SymbolStack symbolStack,
                         OpEvaluator opEvaluator,
                         MetaEvaluator metaEvaluator,
                         ModuleMap moduleMap,
                         MyLoggingFacade myLoggingFacade) {
        this.importManager = importManager;
        this.symbolStack = symbolStack;
        this.metaEvaluator = metaEvaluator;
        this.opEvaluator = opEvaluator;
        this.moduleMap = moduleMap;
        this.logger = myLoggingFacade;
    }

     public abstract FTStack getFTStack();

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

    protected Object[] scriptArgs = null;

    public boolean hasScriptArgs() {
        return scriptArgs != null;
    }

    /**
     * Command line arguments if this is being run in script mode.  This is an array of objects.
     * If invoked from inside QDL then it may be any QDL variable. When coming from outside, these
     * will be strings (since, e.g., bash is unaware of QDL variable types).
     *
     * @return
     */
    public Object[] getScriptArgs() {
        return scriptArgs;
    }

    public void setScriptArgs(Object[] scriptArgs) {
        this.scriptArgs = scriptArgs;
    }

    public List<String> getScriptPaths() {
        return scriptPaths;
    }

    public boolean isEnableLibrarySupport() {
        return enableLibrarySupport;
    }

    public void setEnableLibrarySupport(boolean enableLibrarySupport) {
        this.enableLibrarySupport = enableLibrarySupport;
    }

    boolean enableLibrarySupport = true;

    public List<String> getLibPath() {
        return libPath;
    }

    public void setLibPath(String rawPath) {
        libPath = pathToList(rawPath);
    }

    public void setLibPath(List<String> libPath) {
        this.libPath = libPath;
    }

    protected List<String> libPath = new ArrayList<>();

    public List<String> getModulePaths() {
        return modulePaths;
    }

    public void setModulePaths(List<String> newModulePaths) {
        modulePaths = newModulePaths;
    }

    public void setModulePaths(String rawPath) {
        modulePaths = pathToList(rawPath);
    }

    List<String> modulePaths = new ArrayList<>();

    /**
     * Internally paths are always normalized to end in a "/";
     *
     * @param rawPath
     * @return
     */
    protected List<String> pathToList(String rawPath) {
        List<String> x = new ArrayList<>();
        StringTokenizer st = new StringTokenizer(rawPath, ":");
        while (st.hasMoreTokens()) {
            String token = st.nextToken();
            token = token + (token.endsWith(VFSPaths.PATH_SEPARATOR) ? "" : VFSPaths.PATH_SEPARATOR);
            x.add(token);
        }
        return x;
    }

    /**
     * Sets the script path from a string like path0:path1:path2. Each path in normalized form ends with a /.
     *
     * @param rawPath
     */
    public void setScriptPaths(String rawPath) {
        scriptPaths = pathToList(rawPath);
    }

    public void setScriptPaths(List<String> scriptPaths) {
        this.scriptPaths = scriptPaths;
    }

    List<String> scriptPaths = new ArrayList<>();

    public static boolean isPrintUnicode() {
        return printUnicode;
    }

    public static void setPrintUnicode(boolean printU) {
        printUnicode = printU;
    }

    static boolean printUnicode = false;
}
