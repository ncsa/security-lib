package edu.uiuc.ncsa.qdl.state;

import edu.uiuc.ncsa.qdl.config.QDLEnvironment;
import edu.uiuc.ncsa.qdl.evaluate.IOEvaluator;
import edu.uiuc.ncsa.qdl.evaluate.MetaEvaluator;
import edu.uiuc.ncsa.qdl.evaluate.OpEvaluator;
import edu.uiuc.ncsa.qdl.evaluate.StringEvaluator;
import edu.uiuc.ncsa.qdl.extensions.JavaModule;
import edu.uiuc.ncsa.qdl.module.Module;
import edu.uiuc.ncsa.qdl.module.ModuleMap;
import edu.uiuc.ncsa.qdl.scripting.QDLScript;
import edu.uiuc.ncsa.qdl.scripting.Scripts;
import edu.uiuc.ncsa.qdl.statements.FunctionTable;
import edu.uiuc.ncsa.qdl.statements.TryCatch;
import edu.uiuc.ncsa.qdl.util.FileUtil;
import edu.uiuc.ncsa.qdl.variables.Constant;
import edu.uiuc.ncsa.qdl.variables.StemVariable;
import edu.uiuc.ncsa.qdl.vfs.VFSEntry;
import edu.uiuc.ncsa.qdl.vfs.VFSFileProvider;
import edu.uiuc.ncsa.qdl.vfs.VFSPaths;
import edu.uiuc.ncsa.security.core.util.Iso8601;
import edu.uiuc.ncsa.security.core.util.MyLoggingFacade;

import java.util.HashMap;
import java.util.List;

/**
 * This is a facade for the various stateful components we have to track.
 * Represents the internal state of the system. It has the {@link ImportManager},
 * {@link SymbolTable}, {@link edu.uiuc.ncsa.qdl.evaluate.MetaEvaluator} and such.
 * <p>Created by Jeff Gaynor<br>
 * on 1/21/20 at  7:25 AM
 */
public class State extends FunctionState implements QDLConstants {
    private static final long serialVersionUID = 0xcafed00d1L;


    public State(ImportManager resolver,
                 SymbolStack symbolStack,
                 OpEvaluator opEvaluator,
                 MetaEvaluator metaEvaluator,
                 FunctionTable functionTable,
                 ModuleMap moduleMap,
                 MyLoggingFacade myLoggingFacade,
                 boolean isServerMode) {
        super(resolver,
                symbolStack,
                opEvaluator,
                metaEvaluator,
                functionTable,
                moduleMap,
                myLoggingFacade);
        this.serverMode = isServerMode;
    }

    public StemVariable getSystemConstants() {
        return systemConstants;
    }

    StemVariable systemConstants = null;

    public StemVariable getSystemInfo() {
        return systemInfo;
    }

    StemVariable systemInfo = null;

    public void createSystemInfo(QDLEnvironment qe) {
        if (systemInfo != null) {
            return;
        }
        systemInfo = new StemVariable();
        // Add some from Java, if not in server mode.
        if (!isServerMode()) {
            StemVariable os = new StemVariable();
            os.put(SYS_INFO_OS_VERSION, System.getProperty("os.version"));
            os.put(SYS_INFO_OS_NAME, System.getProperty("os.name"));
            os.put(SYS_INFO_OS_ARCHITECTURE, System.getProperty("os.arch"));

            systemInfo.put(SYS_INFO_OS, os);
            StemVariable system = new StemVariable();
            system.put(SYS_INFO_JVM_VERSION, System.getProperty("java.version"));
            system.put(SYS_INFO_INIT_MEMORY, (Runtime.getRuntime().totalMemory() / (1024 * 1024)) + " MB");
            system.put(SYS_INFO_SYSTEM_PROCESSORS, Runtime.getRuntime().availableProcessors());
            systemInfo.put(SYS_INFO_SYSTEM, system);
            StemVariable user = new StemVariable();
            user.put(SYS_INFO_USER_INVOCATION_DIR, System.getProperty("user.dir"));
            user.put(SYS_INFO_USER_HOME_DIR, System.getProperty("user.home"));
            systemInfo.put(SYS_INFO_USER, user);
        }


        if (qe != null && qe.isEnabled()) {
            StemVariable qdl_props = new StemVariable();
            qdl_props.put(SYS_BOOT_QDL_HOME, qe.getWSHomeDir());
            if (!qe.getBootScript().isEmpty()) {
                qdl_props.put(SYS_BOOT_BOOT_SCRIPT, qe.getBootScript());
            }
            qdl_props.put(SYS_BOOT_CONFIG_NAME, qe.getName());
            qdl_props.put(SYS_BOOT_CONFIG_FILE, qe.getCfgFile());
            qdl_props.put(SYS_BOOT_LOG_FILE, qe.getMyLogger().getFileName());
            qdl_props.put(SYS_BOOT_LOG_NAME, qe.getMyLogger().getClassName());
            qdl_props.put(SYS_BOOT_SERVER_MODE, isServerMode());
            qdl_props.put(SYS_SCRIPTS_PATH, qe.getScriptPath());
            systemInfo.put(SYS_BOOT, qdl_props);
            StemVariable versionInfo = addManifestConstants(qe.getWSHomeDir());
            if (versionInfo != null) {
                systemInfo.put(SYS_QDL_VERSION, versionInfo);
            }
        }

    }

    public void createSystemConstants() {
        if (systemConstants != null) {
            return;
        }
        // Start off with the actual constants that the system must have
        systemConstants = new StemVariable();

        StemVariable varTypes = new StemVariable();
        varTypes.put(SYS_VAR_TYPE_STRING, new Long(Constant.STRING_TYPE));
        varTypes.put(SYS_VAR_TYPE_STEM, new Long(Constant.STEM_TYPE));
        varTypes.put(SYS_VAR_TYPE_BOOLEAN, new Long(Constant.BOOLEAN_TYPE));
        varTypes.put(SYS_VAR_TYPE_NULL, new Long(Constant.NULL_TYPE));
        varTypes.put(SYS_VAR_TYPE_INTEGER, new Long(Constant.LONG_TYPE));
        varTypes.put(SYS_VAR_TYPE_DECIMAL, new Long(Constant.DECIMAL_TYPE));
        varTypes.put(SYS_VAR_TYPE_UNDEFINED, new Long(Constant.UNKNOWN_TYPE));
        systemConstants.put(SYS_VAR_TYPES, varTypes);

        StemVariable detokenizeTypes = new StemVariable();
        detokenizeTypes.put(SYS_DETOKENIZE_PREPEND, StringEvaluator.DETOKENIZE_PREPEND_VALUE);
        detokenizeTypes.put(SYS_DETOKENIZE_OMIT_DANGLING_DELIMITER, StringEvaluator.DETOKENIZE_OMIT_DANGLING_DELIMITER_VALUE);
        systemConstants.put(SYS_DETOKENIZE_TYPE, detokenizeTypes);

        StemVariable errorCodes = new StemVariable();
        errorCodes.put(SYS_ERROR_CODE_SYSTEM_ERROR, TryCatch.RESERVED_ERROR_CODE);
        systemConstants.put(SYS_ERROR_CODES, errorCodes);
        StemVariable fileTypes = new StemVariable();
        fileTypes.put(SYS_FILE_TYPE_BINARY, new Long(IOEvaluator.FILE_OP_BINARY));
        fileTypes.put(SYS_FILE_TYPE_STEM, new Long(IOEvaluator.FILE_OP_TEXT_STEM));
        fileTypes.put(SYS_FILE_TYPE_STRING, new Long(IOEvaluator.FILE_OP_TEXT_STRING));
        systemConstants.put(SYS_FILE_TYPES, fileTypes);
    }


    /**
     * If this is packaged in a jar, read off the information from the manifest file.
     * If no manifest, skip this.
     *
     * @return
     */
    protected StemVariable addManifestConstants(String path) {
        StemVariable versionInfo = new StemVariable();
        List<String> manifest = null;
        try {
            manifest = FileUtil.readFileAsLines(path + (path.endsWith("/") ? "" : "/") + "lib/build-info.txt");
        } catch (Throwable throwable) {
            if (getLogger() != null) {
                getLogger().info("Could not find the build info file. Looked in " + path + (path.endsWith("/") ? "" : "/") + "lib/build-info.txt");
            }
            return versionInfo;
        }

        for (String linein : manifest) {
            if (linein.startsWith("application-version:")) {
                // e.g.  application-version: 1.0.1
                versionInfo.put(SYS_QDL_VERSION_VERSION, truncateLine("application-version:", linein));
            }
            if (linein.startsWith("Build-Jdk:")) {
                // e.g. Build-Jdk: 1.8.0_231
                versionInfo.put(SYS_QDL_VERSION_BUILD_JDK, truncateLine("Build-Jdk:", linein));
            }
            if (linein.startsWith("build-time:")) {
                // e.g. build-time: 1586726889841
                try {
                    Long ts = Long.parseLong(truncateLine("build-time:", linein));
                    versionInfo.put(SYS_QDL_VERSION_BUILD_TIME, Iso8601.date2String(ts));
                } catch (Throwable t) {
                    versionInfo.put(SYS_QDL_VERSION_BUILD_TIME, "?");
                }
            }
            if (linein.startsWith("Created-By:")) {
                // e.g. Created-By: Apache Maven 3.6.0
                versionInfo.put(SYS_QDL_VERSION_CREATED_BY, truncateLine("Created-By:", linein));
            }
            if (linein.startsWith("implementation-build:")) {
                // e.g.     implementation-build: Build: #21 (2020-04-12T16:28:09.841-05:00)
                String build = truncateLine("implementation-build:", linein);
                build = build.substring(0, build.indexOf("("));
                build = truncateLine("Build:", build);
                if (build.startsWith("#")) {
                    build = build.substring(1);
                }
                versionInfo.put(SYS_QDL_VERSION_BUILD_NUMBER, build);
            }

        }
        return versionInfo;
    }

    String truncateLine(String head, String line) {
        if (line.startsWith(head)) {
            return line.substring(head.length()).trim();
        }
        return line;
    }

    public boolean isServerMode() {
        return serverMode;
    }

    public void setServerMode(boolean serverMode) {
        this.serverMode = serverMode;
    }

    boolean serverMode = false;

    public HashMap<String, VFSFileProvider> getVfsFileProviders() {
        return vfsFileProviders;
    }

    public void setVfsFileProviders(HashMap<String, VFSFileProvider> vfsFileProviders) {
        this.vfsFileProviders = vfsFileProviders;
    }

    transient HashMap<String, VFSFileProvider> vfsFileProviders = new HashMap<>();

    public void addVFSProvider(VFSFileProvider scriptProvider) {
        vfsFileProviders.put(scriptProvider.getScheme() + VFSPaths.SCHEME_DELIMITER + scriptProvider.getMountPoint(), scriptProvider);
    }

    public void removeScriptProvider(String scheme) {
        vfsFileProviders.remove(scheme);
    }

    /**
     * Convenience to get a script from the VFS. This takes any file and tries to turn it in to a script,
     * so the "onus is on the app" to make sure this is a script.
     *
     * @param fqName
     * @return
     */
    public QDLScript getScriptFromVFS(String fqName) throws Throwable {
        VFSEntry entry = getFileFromVFS(fqName);
        if (entry == null) {
            return null;
        }
        if (entry.getType().equals(Scripts.SCRIPT)) {
            return (QDLScript) entry;
        }
        QDLScript s = new QDLScript(entry.getLines(), entry.getProperties());
        return s;
    }

    /**
     * Given a fully qualified path, find the VFS corresponding to the mount point and
     * return it or null if no such mount point exists
     *
     * @param fqName
     * @return
     * @throws Throwable
     */
    public VFSFileProvider getVFS(String fqName) throws Throwable {
        if (vfsFileProviders.isEmpty()) return null;
        VFSFileProvider vfsFileProvider = null;

        for (String mountPoint : vfsFileProviders.keySet()) {
            // key is of the form scheme#/mounpoint/ -- note trailing slash! This lets us tell
            // things like A#/a/b from A#/abc
            if (fqName.startsWith(mountPoint)) {
                return vfsFileProviders.get(mountPoint);
            }
        }
        return null;
    }

    public VFSEntry getFileFromVFS(String fqName) throws Throwable {
        if (vfsFileProviders.isEmpty()) return null;
        VFSFileProvider vfsFileProvider = null;
        for (String key : vfsFileProviders.keySet()) {
            // key is of the form scheme#/mounpoint/ -- note trailing slash! This lets us tell
            // things like A#/a/b from A#/abc
            if (fqName.startsWith(key)) {
                vfsFileProvider = vfsFileProviders.get(key);
                break;
            }
        }
        if (vfsFileProvider == null) {
            return null;
        }
        return vfsFileProvider.get(fqName);
    }

    public boolean hasVFSProviders() {
        if (vfsFileProviders == null) return false;
        return !vfsFileProviders.isEmpty();
    }

    public boolean isVFSFile(String path) {
        if (path.startsWith(VFSPaths.SCHEME_DELIMITER) || path.indexOf(VFSPaths.SCHEME_DELIMITER) == -1) {
            return false;
        } // legit this is a file uri, not a virtual one
        return 0 < path.indexOf(VFSPaths.SCHEME_DELIMITER);
    }

    public State newStateNoImports() {
        ImportManager nr = new ImportManager();
        SymbolStack newStack = new SymbolStack(symbolStack.getParentTables());
        State newState = new State(nr,
                newStack,
                getOpEvaluator(),
                getMetaEvaluator(),
                getFunctionTable(),
                getModuleMap(),
                getLogger(),
                isServerMode());
        newState.setScriptArgs(getScriptArgs());
        newState.setScriptPaths(getScriptPaths());
        return newState;

    }

    /**
     * Takes this state object and sets up a new local environment. This is passed to things
     * like loops, functions, conditionals etc. The lifecycle of these is that they are basically abandoned
     * when done then garbage collected.
     *
     * @return State
     */
    public State newStateWithImports() {
        SymbolStack newStack = new SymbolStack(symbolStack.getParentTables());
        State newState = new State(importManager,
                newStack,
                getOpEvaluator(),
                getMetaEvaluator(),
                getFunctionTable(),
                getModuleMap(),
                getLogger(),
                isServerMode());
        newState.setImportedModules(getImportedModules());
        newState.setScriptArgs(getScriptArgs());
        newState.setScriptPaths(getScriptPaths());
        newState.setVfsFileProviders(getVfsFileProviders());
        return newState;
    }

    /**
     * For the case the this has been deserialized and needs to have its transient
     * fields initialized. These are things like the {@link MetaEvaluator} that
     * should not be serialized or current mount points (which can't be serialized
     * because you'd have to serialize the entire backing file system to satisfy
     * the contract of serialization!)
     *
     * @param oldState
     */
    public void injectTransientFields(State oldState) {
        if (getMetaEvaluator() != null && getOpEvaluator() != null) {
            // This is effectively a check if this has been done. If so, don't re-inject state.
            return;
        }
        setLogger(oldState.getLogger()); // set the logger to whatever the current one is
        setMetaEvaluator(oldState.getMetaEvaluator());
        setOpEvaluator(oldState.getOpEvaluator());
        if (oldState.hasVFSProviders()) {
            setVfsFileProviders(new HashMap<>()); // Make sure something is in the current state before we muck with it.
            for (String name : oldState.getVfsFileProviders().keySet()) {
                addVFSProvider(oldState.getVfsFileProviders().get(name));
            }
        }
        // Each imported module has its state serialized too. Hence each has to have current
        // transient fields updated. This will act recursively (so imports in imports in imports etc.)

        for (String mod : getImportedModules().keySet()) {
            getImportedModules().get(mod).getState().injectTransientFields(oldState);
        }
        setIoInterface(oldState.getIoInterface());
    }

    /**
     * For modules only. This copies the state except that no functions are inherited. The
     * contract is that modules only internal state that may be imported.
     *
     * @return
     */
    public State newModuleState() {
        // NOTE this has no parents. Modules have completely clear state when starting!
        ImportManager r = new ImportManager();
        SymbolStack newStack = new SymbolStack();
        newStack.addParent(new SymbolTableImpl());
        State newState = new State(r,
                newStack,
                getOpEvaluator(),
                getMetaEvaluator(),
                new FunctionTable(),
                getModuleMap(),
                getLogger(),
                isServerMode());
        // May want to rethink setting these...
        newState.setScriptArgs(getScriptArgs());
        newState.setScriptPaths(getScriptPaths());
        newState.setVfsFileProviders(getVfsFileProviders());
        return newState;
    }

    /**
     * Add the module under the default alias
     *
     * @param module
     */
    public void addModule(Module module) {
        if (module instanceof JavaModule) {
            ((JavaModule) module).init(this.newModuleState());
        }
        getModuleMap().put(module.getNamespace(), module);
    }

    public int getStackSize() {
        return getSymbolStack().getSymbolCount();
    }


}
