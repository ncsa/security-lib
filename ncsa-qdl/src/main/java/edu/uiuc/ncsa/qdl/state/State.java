package edu.uiuc.ncsa.qdl.state;

import edu.uiuc.ncsa.qdl.config.QDLEnvironment;
import edu.uiuc.ncsa.qdl.evaluate.MetaEvaluator;
import edu.uiuc.ncsa.qdl.evaluate.OpEvaluator;
import edu.uiuc.ncsa.qdl.extensions.JavaModule;
import edu.uiuc.ncsa.qdl.module.Module;
import edu.uiuc.ncsa.qdl.module.ModuleMap;
import edu.uiuc.ncsa.qdl.scripting.QDLScript;
import edu.uiuc.ncsa.qdl.scripting.Scripts;
import edu.uiuc.ncsa.qdl.statements.FunctionTable;
import edu.uiuc.ncsa.qdl.statements.TryCatch;
import edu.uiuc.ncsa.qdl.variables.Constant;
import edu.uiuc.ncsa.qdl.variables.StemVariable;
import edu.uiuc.ncsa.qdl.vfs.VFSEntry;
import edu.uiuc.ncsa.qdl.vfs.VFSFileProvider;
import edu.uiuc.ncsa.qdl.vfs.VFSPaths;
import edu.uiuc.ncsa.security.core.util.Iso8601;
import edu.uiuc.ncsa.security.core.util.MyLoggingFacade;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

/**
 * This is a facade for the various stateful components we have to track.
 * Represents the internal state of the system. It has the {@link ImportManager},
 * {@link SymbolTable}, {@link edu.uiuc.ncsa.qdl.evaluate.MetaEvaluator} and such.
 * <p>Created by Jeff Gaynor<br>
 * on 1/21/20 at  7:25 AM
 */
public class State extends FunctionState {
    private static final long serialversionUID = 4129348937L;


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
        systemInfo= new StemVariable();
        // Add some from Java, if not in server mode.
         if (!isServerMode()) {
             StemVariable os = new StemVariable();
             os.put("version", System.getProperty("os.version"));
             os.put("name", System.getProperty("os.name"));
             os.put("architecture", System.getProperty("os.arch"));
             systemInfo.put("os.", os);
             StemVariable system = new StemVariable();
             system.put("jvm_version", System.getProperty("java.version"));
             system.put("initial_memory", (Runtime.getRuntime().totalMemory() / (1024 * 1024)) + " MB");
             system.put("processors", Runtime.getRuntime().availableProcessors());
             systemInfo.put("system.", system);
             StemVariable user = new StemVariable();
             user.put("working_dir", System.getProperty("user.dir"));
             user.put("home_dir", System.getProperty("user.home"));
             systemInfo.put("user.", user);
         }

         StemVariable versionInfo = addManifestConstants();
         if (versionInfo != null) {
             systemInfo.put("qdl_version.", versionInfo);
         }
         if (qe != null && qe.isEnabled()) {
             StemVariable qdl_props = new StemVariable();
             qdl_props.put("qdl_home", qe.getWSHomeDir());
             if (!qe.getBootScript().isEmpty()) {
                 qdl_props.put("boot_script", qe.getBootScript());
             }
             qdl_props.put("cfg_name", qe.getName());
             qdl_props.put("cfg_file", qe.getCfgFile());
             qdl_props.put("log_file", qe.getMyLogger().getFileName());
             qdl_props.put("logging_name", qe.getMyLogger().getClassName());
             systemInfo.put("boot.", qdl_props);
         }

    }
    public void createSystemConstants() {
        if (systemConstants != null) {
            return;
        }
        // Start off with the actual constants that the system must have
        systemConstants = new StemVariable();

        StemVariable varTypes = new StemVariable();
        varTypes.put("string", new Long(Constant.STRING_TYPE));
        varTypes.put("stem", new Long(Constant.STEM_TYPE));
        varTypes.put("boolean", new Long(Constant.BOOLEAN_TYPE));
        varTypes.put("null", new Long(Constant.NULL_TYPE));
        varTypes.put("integer", new Long(Constant.LONG_TYPE));
        varTypes.put("decimal", new Long(Constant.DECIMAL_TYPE));
        varTypes.put("undefined", new Long(Constant.UNKNOWN_TYPE));
        systemConstants.put("var_types.", varTypes);
        StemVariable errorCodes = new StemVariable();
        errorCodes.put("system_error", TryCatch.RESERVED_ERROR_CODE);
        systemConstants.put("error_codes.", errorCodes);

    }


    /**
     * If this is packaged in a jar, read off the information from the manifest file.
     * If no manifest, skip this.
     * @return
     */
    protected StemVariable addManifestConstants() {
        InputStream is = getClass().getResourceAsStream("/META-INF/MANIFEST.MF");
        if (is == null) {
            return null;
        }
        StemVariable versionInfo = new StemVariable();
        try {
            if (is != null) {
                InputStreamReader isr = new InputStreamReader(is);
                BufferedReader bufferedInputStream = new BufferedReader(isr);
                String linein = null;
                while (null != (linein = bufferedInputStream.readLine())) {
                    if (linein.startsWith("application-version:")) {
                        // e.g.  application-version: 1.0.0
                        versionInfo.put("version", truncateLine("application-version:", linein));
                    }
                    if (linein.startsWith("Build-Jdk:")) {
                        // e.g. Build-Jdk: 1.8.0_231
                        versionInfo.put("build_jdk", truncateLine("Build-Jdk:", linein));
                    }
                    if (linein.startsWith("build-time:")) {
                        // e.g. build-time: 1586726889841
                        try {
                            Long ts = Long.parseLong(truncateLine("build-time:", linein));
                            versionInfo.put("build_time", Iso8601.date2String(ts));
                        } catch (Throwable t) {
                            versionInfo.put("build_time", "?");
                        }
                    }
                    if (linein.startsWith("Created-By:")) {
                        // e.g. Created-By: Apache Maven 3.6.0
                        versionInfo.put("created_by", truncateLine("Created-By:", linein));
                    }
                    if (linein.startsWith("implementation-build:")) {
                        // e.g.     implementation-build: Build: #21 (2020-04-12T16:28:09.841-05:00)
                        String build = truncateLine("implementation-build:", linein);
                        build = build.substring(0, build.indexOf("("));
                        build = truncateLine("Build:", build);
                        if (build.startsWith("#")) {
                            build = build.substring(1);
                        }
                        versionInfo.put("build_nr", build);
                    }
                }
                bufferedInputStream.close();
            }
        } catch (Throwable t) {
            warn("Could not load version information:" + t.getMessage());
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
     * so tjhe "onus is on the app" to make sure this is a script.
     *
     * @param fqName
     * @return
     */
    public QDLScript getScriptFromVFS(String fqName) throws Throwable {
        VFSEntry entry = getFileFromVFS(fqName);
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
        //System.out.println("** State, creating new local state **");
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
        return newState;
    }

    public State newLoopState() {
        //System.out.println("** State, creating new local state **");
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
        return newState;
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
