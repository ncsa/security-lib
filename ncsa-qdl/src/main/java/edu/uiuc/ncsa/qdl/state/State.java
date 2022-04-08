package edu.uiuc.ncsa.qdl.state;

import edu.uiuc.ncsa.qdl.config.QDLEnvironment;
import edu.uiuc.ncsa.qdl.evaluate.*;
import edu.uiuc.ncsa.qdl.extensions.JavaModule;
import edu.uiuc.ncsa.qdl.functions.FKey;
import edu.uiuc.ncsa.qdl.functions.FStack;
import edu.uiuc.ncsa.qdl.functions.FTable;
import edu.uiuc.ncsa.qdl.functions.FunctionRecord;
import edu.uiuc.ncsa.qdl.module.*;
import edu.uiuc.ncsa.qdl.scripting.QDLScript;
import edu.uiuc.ncsa.qdl.scripting.Scripts;
import edu.uiuc.ncsa.qdl.state.legacy.SymbolStack;
import edu.uiuc.ncsa.qdl.statements.TryCatch;
import edu.uiuc.ncsa.qdl.util.QDLFileUtil;
import edu.uiuc.ncsa.qdl.variables.Constant;
import edu.uiuc.ncsa.qdl.variables.StemVariable;
import edu.uiuc.ncsa.qdl.variables.VStack;
import edu.uiuc.ncsa.qdl.vfs.VFSEntry;
import edu.uiuc.ncsa.qdl.vfs.VFSFileProvider;
import edu.uiuc.ncsa.qdl.vfs.VFSPaths;
import edu.uiuc.ncsa.qdl.workspace.WorkspaceCommands;
import edu.uiuc.ncsa.qdl.xml.XMLMissingCloseTagException;
import edu.uiuc.ncsa.qdl.xml.XMLSerializationState;
import edu.uiuc.ncsa.qdl.xml.XMLUtils;
import edu.uiuc.ncsa.qdl.xml.XMLUtilsV2;
import edu.uiuc.ncsa.security.core.configuration.XProperties;
import edu.uiuc.ncsa.security.core.exceptions.NFWException;
import edu.uiuc.ncsa.security.core.util.Iso8601;
import edu.uiuc.ncsa.security.core.util.MetaDebugUtil;
import edu.uiuc.ncsa.security.core.util.MyLoggingFacade;
import net.sf.json.JSONObject;
import org.apache.commons.codec.binary.Base32;
import org.apache.commons.codec.binary.Base64;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import static edu.uiuc.ncsa.qdl.xml.XMLConstants.*;

/**
 * This is a facade for the various stateful components we have to track.
 * Represents the internal state of the system.
 * <p>Created by Jeff Gaynor<br>
 * on 1/21/20 at  7:25 AM
 */
public class State extends FunctionState implements QDLConstants {
    private static final long serialVersionUID = 0xcafed00d1L;

    public String getInternalID() {
        if (internalID == null) {
            internalID = getUuid().toString(); // basically just cache it.
        }
        return internalID;
    }

    String internalID = null;

    public int getPID() {
        return pid;
    }

    public void setPID(int pid) {
        this.pid = pid;
    }

    int pid = 0;

    /**
     * If you extend this class, you must override this method to return a new instance
     * of your state with everything in it you want or need.
     *
     * @param opEvaluator
     * @param metaEvaluator
     * @param ftStack
     * @param mtStack
     * @param miStack
     * @param myLoggingFacade
     * @param isServerMode
     * @param isRestrictedIO
     * @param assertionsOn
     * @return
     */
    public State newInstance(VStack symbolStack,
                             OpEvaluator opEvaluator,
                             MetaEvaluator metaEvaluator,
                             FStack<? extends FTable<? extends FKey, ? extends FunctionRecord>> ftStack,
                             MTStack mtStack,
                             MIStack miStack,
                             MyLoggingFacade myLoggingFacade,
                             boolean isServerMode,
                             boolean isRestrictedIO,
                             boolean assertionsOn) {
        return new State(symbolStack,
                opEvaluator,
                metaEvaluator,
                ftStack,
                mtStack,
                miStack,
                myLoggingFacade,
                isServerMode,
                isRestrictedIO,
                assertionsOn);
    }

    public State(
            VStack vStack,
            OpEvaluator opEvaluator,
            MetaEvaluator metaEvaluator,
            FStack<? extends FTable<? extends FKey, ? extends FunctionRecord>> ftStack,
            MTStack mtStack,
            MIStack miStack,
            MyLoggingFacade myLoggingFacade,
            boolean isServerMode,
            boolean isRestrictedIO,
            boolean assertionsOn) {
        super(vStack,
                opEvaluator,
                metaEvaluator,
                ftStack,
                mtStack,
                miStack,
                myLoggingFacade);
        this.serverMode = isServerMode;
        this.assertionsOn = assertionsOn;
        this.restrictedIO = isRestrictedIO;
    }

    public StemVariable getSystemConstants() {
        if (systemConstants == null) {
            createSystemConstants();
        }
        return systemConstants;
    }

    public void setSystemConstants(StemVariable systemConstants) {
        this.systemConstants = systemConstants;
    }

    StemVariable systemConstants = null;

    public StemVariable getSystemInfo() {
        return systemInfo;
    }

    public void setSystemInfo(StemVariable systemInfo) {
        this.systemInfo = systemInfo;
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
            qdl_props.put(SYS_BOOT_RESTRICTED_IO_MODE, isRestrictedIO());
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

        StemVariable characters = new StemVariable();
        characters.put("00ac", OpEvaluator.NOT2);
        characters.put("00af", OpEvaluator.MINUS2);
        characters.put("00b7", QDLConstants.STEM_PATH_MARKER2);
        characters.put("00d7", OpEvaluator.TIMES2);
        characters.put("00f7", OpEvaluator.DIVIDE2);
        characters.put("207a", OpEvaluator.PLUS2);
        characters.put("2192", "→");
        characters.put("2205", "∅");
        characters.put("2227", OpEvaluator.AND3);
        characters.put("2228", OpEvaluator.OR3);
        characters.put("2248", "≈");
        characters.put("2254", "≔");
        characters.put("2255", "≕");
        characters.put("2260", OpEvaluator.NOT_EQUAL2);
        characters.put("2261", OpEvaluator.EQUALS2);
        characters.put("2264", OpEvaluator.LESS_THAN_EQUAL3);
        characters.put("2265", OpEvaluator.MORE_THAN_EQUAL3);
        characters.put("22a4", "⊤");
        characters.put("22a5", "⊥");
        characters.put("22a8", "⊨");
        characters.put("2241", "≁");
        characters.put("2297", "⊗");
        characters.put("2308", "⌈");
        characters.put("230a", "⌊");
        characters.put("27e6", "⟦");
        characters.put("27e7", "⟧");
        characters.put("03c4", StemEvaluator.TRANSPOSE2);
        characters.put("03c0", TMathEvaluator.PI2);
        characters.put("22c0", OpEvaluator.AND2);
        characters.put("22c1", OpEvaluator.OR2);

        systemConstants.put(SYS_VAR_TYPE_CHARACTERS, characters);
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
        errorCodes.put(SYS_ERROR_CODE_DEFAULT_USER_ERROR, TryCatch.RESERVED_USER_ERROR_CODE);
        systemConstants.put(SYS_ERROR_CODES, errorCodes);
        StemVariable fileTypes = new StemVariable();
        fileTypes.put(SYS_FILE_TYPE_BINARY, new Long(IOEvaluator.FILE_OP_BINARY));
        fileTypes.put(SYS_FILE_TYPE_STEM, new Long(IOEvaluator.FILE_OP_TEXT_STEM));
        fileTypes.put(SYS_FILE_TYPE_STRING, new Long(IOEvaluator.FILE_OP_TEXT_STRING));
        fileTypes.put(SYS_FILE_TYPE_INIT, new Long(IOEvaluator.FILE_OP_TEXT_INI));
        systemConstants.put(SYS_FILE_TYPES, fileTypes);

        StemVariable uriFields = new StemVariable();
        uriFields.put(URI_AUTHORITY, URI_AUTHORITY);
        uriFields.put(URI_HOST, URI_HOST);
        uriFields.put(URI_FRAGMENT, URI_FRAGMENT);
        uriFields.put(URI_QUERY, URI_QUERY);
        uriFields.put(URI_PORT, URI_PORT);
        uriFields.put(URI_PATH, URI_PATH);
        uriFields.put(URI_SCHEME, URI_SCHEME);
        uriFields.put(URI_SCHEME_SPECIFIC_PART, URI_SCHEME_SPECIFIC_PART);
        uriFields.put(URI_USER_INFO, URI_USER_INFO);
        systemConstants.put(URI_FIELDS, uriFields);

        StemVariable logLevels = new StemVariable();
        logLevels.put(SYS_LOG_NONE, SystemEvaluator.LOG_LEVEL_NONE);
        logLevels.put(SYS_LOG_TRACE, SystemEvaluator.LOG_LEVEL_TRACE);
        logLevels.put(SYS_LOG_INFO, SystemEvaluator.LOG_LEVEL_INFO);
        logLevels.put(SYS_LOG_WARN, SystemEvaluator.LOG_LEVEL_WARN);
        logLevels.put(SYS_LOG_ERROR, SystemEvaluator.LOG_LEVEL_ERROR);
        logLevels.put(SYS_LOG_SEVERE, SystemEvaluator.LOG_LEVEL_SEVERE);
        systemConstants.put(SYS_LOG_LEVELS, logLevels);

    }

    public MetaDebugUtil getDebugUtil() {
        if (debugUtil == null) {
            debugUtil = new MetaDebugUtil();
            debugUtil.setDebugLevel(MetaDebugUtil.DEBUG_LEVEL_OFF_LABEL);
        }
        return debugUtil;
    }

    public void setDebugUtil(MetaDebugUtil debugUtil) {
        this.debugUtil = debugUtil;
    }

    MetaDebugUtil debugUtil = null;

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
            manifest = QDLFileUtil.readFileAsLines(path + (path.endsWith("/") ? "" : "/") + "lib/build-info.txt");
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

    /**
     * In server mode, some IO for debugging can still be allowed. This is checked
     *
     * @return
     */
    public boolean isRestrictedIO() {
        return restrictedIO;
    }

    public void setRestrictedIO(boolean restrictedIO) {
        this.restrictedIO = restrictedIO;
    }

    boolean restrictedIO = false;

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

    public boolean hasMountPoint(String mountPoint) {
        return vfsFileProviders.containsKey(mountPoint);
    }

    public void removeVFSProvider(String mountPoint) {
        vfsFileProviders.remove(mountPoint);
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
        VFSEntry entry = getFileFromVFS(fqName, AbstractFunctionEvaluator.FILE_OP_TEXT_STRING);
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

    public VFSEntry getFileFromVFS(String fqName, int type) throws Throwable {
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
        return vfsFileProvider.get(fqName, type);
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

/*
    public State newStateNoImports() {
        SymbolStack newStack = new SymbolStack(symbolStack.getParentTables());
        MIStack miStack = new MIStack();
        State newState = newInstance(newStack,
                getOpEvaluator(),
                getMetaEvaluator(),
                getFTStack(),
                getMTemplates(),
                miStack,
                getLogger(),
                isServerMode(),
                isRestrictedIO(),
                isAssertionsOn());
        newState.setScriptArgs(getScriptArgs());
        newState.setScriptPaths(getScriptPaths());
        newState.setModulePaths(getModulePaths());
        return newState;

    }
*/

    /**
     * Convenience method for {@link #newLocalState(State)} with a null argument
     *
     * @return State
     */
    public State newLocalState() {
        return newLocalState(null);
    }

    /**
     * Creates a new state object and pushes the moduleState's stacks onto
     * the current one. This means the resulting state inherits everything.
     *
     * @param moduleState
     * @return
     */
    public State newLocalState(State moduleState) {
        //   return newStateWithImportsOLD(moduleState);
        return newStateWithImportsNEW(moduleState);
    }

    protected State newStateWithImportsNEW(State moduleState) {
        VStack newStack = new VStack(); // always creates an empty symbol table, replace it
        if (moduleState != null && !moduleState.vStack.isEmpty()) {
            //newStack.addAll(moduleState.symbolStack.getParentTables());
            newStack.appendTables(moduleState.vStack);
        }

        if (!vStack.isEmpty()) {
            //newStack.addAll(symbolStack.getParentTables());
            newStack.appendTables(vStack);
        }

        FStack<? extends FTable<? extends FKey, ? extends FunctionRecord>> ftStack = new FStack();
        if (moduleState != null && !moduleState.getFTStack().isEmpty()) {
            ftStack.appendTables(moduleState.getFTStack());
        }

        if (!getFTStack().isEmpty()) {
            ftStack.appendTables(getFTStack()); // pushes elements in reverse order
        }


        MTStack mtStack = new MTStack();
        if (moduleState != null && !moduleState.getMTemplates().isEmpty()) {
            mtStack.appendTables(moduleState.getMTemplates());
        }

        if (!getMTemplates().isEmpty()) {
            mtStack.appendTables(getMTemplates());
        }

        if (moduleState != null && !moduleState.getMTemplates().isEmpty()) {
            mtStack.appendTables(moduleState.getMTemplates());
        }

        MIStack miStack = new MIStack();
        if (moduleState != null && !moduleState.getMInstances().isEmpty()) {
            miStack.appendTables(moduleState.getMInstances());
        }

        if (!getMInstances().isEmpty()) {
            miStack.appendTables(getMInstances());
        }


        State newState = newInstance(
                newStack,
                getOpEvaluator(),
                getMetaEvaluator(),
                ftStack,
                mtStack,
                miStack,
                getLogger(),
                isServerMode(),
                isRestrictedIO(),
                isAssertionsOn());
        newState.setScriptArgs(getScriptArgs());
        newState.setScriptPaths(getScriptPaths());
        newState.setModulePaths(getModulePaths());
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

        for (Object mod : getMInstances().keySet()) {
            XKey xKey = (XKey) mod;
            getMInstances().getModule(xKey).getState().injectTransientFields(oldState);
        }
        setIoInterface(oldState.getIoInterface());
    }

    /**
     * This creates a completely clean state, using the current environment
     * (so modules and script paths, but not variables, modules etc.)
     *
     * @return
     */
    public State newCleanState() {
        // NOTE this has no parents. Modules have completely clear state when starting!
        State newState = newInstance(
                new VStack(),
                getOpEvaluator(),
                getMetaEvaluator(),
                new FStack(),
                new MTStack(),
                new MIStack(),
                getLogger(),
                isServerMode(),
                isRestrictedIO(),
                isAssertionsOn());
        newState.setScriptArgs(getScriptArgs());
        newState.setScriptPaths(getScriptPaths());
        newState.setModulePaths(getModulePaths());
        newState.setVfsFileProviders(getVfsFileProviders());
        return newState;
    }

    /**
     * This is needed for XML deserlization and makes dummy state for everything, assuming the deserializer will
     * replace it all. Generally do not use outside of XML deserialization.
     */
    public State() {
        super(new VStack(), new OpEvaluator(), MetaEvaluator.getInstance(), new FStack(), new MTStack(), new MIStack(), new MyLoggingFacade((Logger) null));
    }

    /**
     * Add the module under the default alias
     *
     * @param module
     */
    public void addModule(Module module) {
        if (module instanceof JavaModule) {
            ((JavaModule) module).init(this.newCleanState());
        }
        getMTemplates().put(module);
    }

    public int getStackSize() {
        return getVStack().size();
    }

    public void toXML(XMLStreamWriter xsw, XMLSerializationState xmlSerializationState) throws XMLStreamException {
        xsw.writeStartElement(STATE_TAG);
        xsw.writeAttribute(UUID_TAG, getInternalID());
        writeExtraXMLAttributes(xsw);
        //getSymbolStack().toXML(xsw);
        getVStack().toXML(xsw, xmlSerializationState);
        getFTStack().toXML(xsw, xmlSerializationState);
        getMTemplates().toXML(xsw, xmlSerializationState);
        getMInstances().toXML(xsw, xmlSerializationState);
        writeExtraXMLElements(xsw);
        xsw.writeEndElement(); // end state tag

    }

    public void toXML(XMLStreamWriter xsw) throws XMLStreamException {
        xsw.writeStartElement(STATE_TAG);
        xsw.writeAttribute(STATE_ID_TAG, getInternalID());
        writeExtraXMLAttributes(xsw);
        // The list of aliases and their corresponding modules
        // NEXT BIT IS DONE IN NEW MODULE STACK, SEE BELOW
/*        if (!getMInstances().isEmpty()) {
            xsw.writeStartElement(OLD_IMPORTED_MODULES_TAG);
            xsw.writeComment("The imported modules with their state and alias.");
            for (String alias : getMInstances().keySet()) {
                Module module = getMInstances().get(alias);
                module.toXML(xsw, alias);
            }
            xsw.writeEndElement(); //end imports
        }*/
        // NOTE that the order in which things are serialized matters! Do module declarations first
        // then variables and functions. This means that on deserialization (which follows document order exactly)
        // any modules are prcocessed (including the setting of variables and functions) then the user's modifications
        // overwrite what is there. This way if the user has modified things, it is preserved.

        // Templates
        getMTemplates().toXML(xsw, null);

        // imports
        getMInstances().toXML(xsw, null);

        // Symbol stack has the variables
        getVStack().toXML(xsw, null);
        // Function table has the functions
        getFTStack().toXML(xsw, null);
        writeExtraXMLElements(xsw);
        xsw.writeEndElement(); // end state tag
    }

    public void fromXML(XMLEventReader xer, XProperties xp, XMLSerializationState xmlSerializationState) throws XMLStreamException {
        // At this point, caller peeked and knows this is the right type of event,
        // so we know where in the stream we are starting automatically.

        XMLEvent xe = xer.nextEvent(); // start iteration it should be at the state tag
        if (xe.isStartElement() && xe.asStartElement().getName().getLocalPart().equals(STATE_TAG)) {
            internalID = xe.asStartElement().getAttributeByName(new QName(UUID_TAG)).getValue();
            readExtraXMLAttributes(xe.asStartElement());
        }
        while (xer.hasNext()) {
            xe = xer.peek(); // start iteration
            switch (xe.getEventType()) {
                case XMLEvent.START_ELEMENT:
                    switch (xe.asStartElement().getName().getLocalPart()) {
                        case VARIABLE_STACK:
                            if (xmlSerializationState.getVersion().equals(VERSION_2_0_TAG)) {
                                XMLUtilsV2.deserializeVariables(xer, this, xmlSerializationState);
                            } else {
                                // Legacy.
                                VStack vStack = new VStack();
                                SymbolStack st = new SymbolStack();
                                st.fromXML(xer);
                                // Have to transfer functions over.
                                vStack.fromJSON(st.toJSON(), null);
                                setvStack(vStack);
                            }
                            break;
                        case FUNCTION_TABLE_STACK_TAG:
                            if (xmlSerializationState.getVersion().equals(VERSION_2_0_TAG)) {
                                XMLUtilsV2.deserializeFunctions(xer, this, xmlSerializationState);
                            } else {
                                XMLUtils.deserializeFunctions(xer, xp, this);
                            }
                            break;
                        case INSTANCE_STACK:
                            if (xmlSerializationState.getVersion().equals(VERSION_2_0_TAG)) {
                                XMLUtilsV2.deserializeInstances(xer, this, xmlSerializationState);
                            } else {
                                XMLUtils.deserializeImports(xer, xp, this);
                            }
                            break;
                        case TEMPLATE_STACK:
                            if (xmlSerializationState.getVersion().equals(VERSION_2_0_TAG)) {
                                XMLUtilsV2.deserializeTemplates(xer, this, xmlSerializationState);
                            } else {
                                XMLUtils.deserializeTemplates(xer, xp, this);
                            }
                            break;
                        default:
                            readExtraXMLElements(xe, xer);
                            break;
                    }

                    break;
                case XMLEvent.END_ELEMENT:
                    if (xe.asEndElement().getName().getLocalPart().equals(STATE_TAG)) {
                        return;
                    }
                    break;
            }
            xer.next(); // advance cursor
        }
        throw new XMLMissingCloseTagException(STATE_TAG);
    }

    public void fromXML(XMLEventReader xer, XProperties xp) throws XMLStreamException {
        // At this point, caller peeked and knows this is the right type of event,
        // so we know where in the stream we are starting automatically.

        XMLEvent xe = xer.nextEvent(); // start iteration it should be at the state tag
        if (xe.isStartElement() && xe.asStartElement().getName().getLocalPart().equals(STATE_TAG)) {
            internalID = xe.asStartElement().getAttributeByName(new QName(STATE_ID_TAG)).getValue();
            readExtraXMLAttributes(xe.asStartElement());
        }
        while (xer.hasNext()) {
            xe = xer.peek(); // start iteration
            switch (xe.getEventType()) {
                case XMLEvent.START_ELEMENT:
                    switch (xe.asStartElement().getName().getLocalPart()) {
                        case STACKS_TAG:
                            SymbolStack st = new SymbolStack();
                            st.fromXML(xer);
                            VStack vStack = new VStack();
                            vStack.fromJSON(st.toJSON(), null);
                            setvStack(vStack);
                            break;
                        case FUNCTIONS_TAG:
                            XMLUtils.oldDeserializeFunctions(xer, xp, this);
                            break;
                        case FUNCTION_TABLE_STACK_TAG:
                            XMLUtils.deserializeFunctions(xer, xp, this);
                            break;
                        case OLD_IMPORTED_MODULES_TAG:
                            XMLUtils.deserializeImports(xer, xp, this);
                            break;
                        case OLD_MODULE_TEMPLATE_TAG:
                            XMLUtils.deserializeTemplates(xer, xp, this);
                            break;
                        default:
                            readExtraXMLElements(xe, xer);
                            break;
                    }

                    break;
                case XMLEvent.END_ELEMENT:
                    if (xe.asEndElement().getName().getLocalPart().equals(STATE_TAG)) {
                        return;
                    }
                    break;
            }
            xer.next(); // advance cursor
        }
        throw new XMLMissingCloseTagException(STATE_TAG);
    }

    /**
     * This just drives running through a bunch of modules
     *
     * @param xer
     * @throws XMLStreamException
     */
    protected void doXMLImportedModules(XMLEventReader xer) throws XMLStreamException {
        XMLEvent xe = xer.nextEvent();
        while (xer.hasNext()) {
            switch (xe.getEventType()) {
                case XMLEvent.START_ELEMENT:
                    if (xe.asStartElement().getName().getLocalPart().equals(MODULE_TAG)) {
                        Module module = new Module() {
                            @Override
                            public Module newInstance(State state) {
                                return null;
                            }

                            List<String> doc = new ArrayList<>();

                            @Override
                            public List<String> getListByTag() {
                                return doc;
                            }

                            @Override
                            public void setDocumentation(List<String> documentation) {
                                doc = documentation;
                            }

                            @Override
                            public List<String> getDocumentation() {
                                return doc;
                            }
                        };
                    }
                    break;
                case XMLEvent.END_ELEMENT:
                    if (xe.asEndElement().getName().getLocalPart().equals(OLD_IMPORTED_MODULES_TAG)) {
                        return;
                    }
            }
            xe = xer.nextEvent();
        }
        throw new XMLMissingCloseTagException(OLD_IMPORTED_MODULES_TAG);

    }

    /**
     * This is invoked at the end of the serialization and lets you add additional things to be serialized
     * in the State. All new elements are added right before the final closing tag for the state object.
     *
     * @param xsr
     * @throws XMLStreamException
     */
    public void writeExtraXMLElements(XMLStreamWriter xsr) throws XMLStreamException {
        xsr.writeStartElement(STATE_CONSTANTS_TAG);
        JSONObject json = new JSONObject();
        json.put(STATE_ASSERTIONS_ENABLED_TAG, isAssertionsOn());
        json.put(STATE_PID_TAG, getPID());
        json.put(STATE_RESTRICTED_IO_TAG, isRestrictedIO());
        json.put(STATE_SERVER_MODE_TAG, isServerMode());
        // Saving the numeric digits this way is unsatisfactory since it is also done in all
        // of the sub-states, but there is no easy way to set this once and get it right
        // The top-level state is the last read, so it wil always end up getting set
        // correctly at the end. Best we can do without a ton of machinery...
        json.put(STATE_NUMERIC_DIGITS_TAG, OpEvaluator.getNumericDigits());
        xsr.writeCData(Base64.encodeBase64URLSafeString(json.toString().getBytes(StandardCharsets.UTF_8)));
        xsr.writeEndElement();
    }

    /**
     * This exists to let you add additional attributes to the state tag. It should <b><i>only</i></b>
     * contain {@link XMLStreamWriter#writeAttribute(String, String)} calls, nothing else.
     *
     * @param xsw
     * @throws XMLStreamException
     */
    public void writeExtraXMLAttributes(XMLStreamWriter xsw) throws XMLStreamException {

    }

    /**
     * This passes in the current start event so you can add your own event loop and cases.
     * Note you need have only a switch on the tag names you want.
     *
     * @param xe
     * @param xer
     * @throws XMLStreamException
     */

    public void readExtraXMLElements(XMLEvent xe, XMLEventReader xer) throws XMLStreamException {
        if(xe.asStartElement().getName().getLocalPart().equals(STATE_CONSTANTS_TAG)) {
            // only process the tag if it is the right one
            String text = XMLUtilsV2.getText(xer, STATE_CONSTANTS_TAG);
            text = new String(Base64.decodeBase64(text));
            JSONObject json = JSONObject.fromObject(text);
            setAssertionsOn(json.getBoolean(STATE_ASSERTIONS_ENABLED_TAG));
            setPID(json.getInt(STATE_PID_TAG));
            setServerMode(json.getBoolean(STATE_SERVER_MODE_TAG));
            setRestrictedIO(json.getBoolean(STATE_RESTRICTED_IO_TAG));
            OpEvaluator.setNumericDigits(json.getInt(STATE_NUMERIC_DIGITS_TAG));
        }
    }

    /**
     * Allows you to read custom attributes from the state tag. This should <b><i>only</i></b> contain
     * calls to {@link StartElement#getAttributeByName(QName)} by name calls.
     *
     * @param xe
     * @throws XMLStreamException
     */
    public void readExtraXMLAttributes(StartElement xe) throws XMLStreamException {

    }

    SecureRandom secureRandom = new SecureRandom();
    transient Base32 base32 = new Base32('_'); // set trailing char to be an underscore

    /**
     * Returns an unused variable name.
     *
     * @return
     */
    public String getTempVariableName() {
        byte[] b = new byte[16];
        for (int i = 0; i < 10; i++) {
            String var = base32.encodeToString(b);
            if (!isDefined(var)) {
                return var;
            }
        }
        throw new NFWException("Was unable to create a random, unused variable");
    }

    public boolean isAssertionsOn() {
        return assertionsOn;
    }

    public void setAssertionsOn(boolean assertionsOn) {
        this.assertionsOn = assertionsOn;
    }

    boolean assertionsOn = true;

    /**
     * Allows back reference to workspace to run macros.
     *
     * @return
     */
    public WorkspaceCommands getWorkspaceCommands() {
        return workspaceCommands;
    }

    public void setWorkspaceCommands(WorkspaceCommands workspaceCommands) {
        this.workspaceCommands = workspaceCommands;
    }

    WorkspaceCommands workspaceCommands;

    /**
     * Recurse through thst modules and collects templates and state objects from instances.
     * Now that templates and instances are handled as stacks with local state, old form of
     * serialization fails due to recursion.<br/><br/>
     * These are serialized into a flat list and references to them are used.
     *
     * @param XMLSerializationState
     */
    public void buildSO(XMLSerializationState XMLSerializationState) {
        for (Object key : getMTemplates().keySet()) {
            MTKey mtKey = (MTKey) key;
            Module module = getMTemplates().getModule(mtKey);
            if (!XMLSerializationState.processedTemplate(module)) {
                XMLSerializationState.addTemplate(module);
            }
        }
        for (Object key : getMInstances().keySet()) {
            XKey xKey = (XKey) key;
            MIWrapper wrapper = (MIWrapper) getMInstances().get(xKey);
            Module module = wrapper.getModule();
            if (!XMLSerializationState.processedInstance(module)) {
                XMLSerializationState.addInstance(wrapper);
                if (!XMLSerializationState.processedState(module.getState())) {
                    XMLSerializationState.addState(module.getState());
                    module.getState().buildSO(XMLSerializationState);
                }
            }

        }

    }

}
