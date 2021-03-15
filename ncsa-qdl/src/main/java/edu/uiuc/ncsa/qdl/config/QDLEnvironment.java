package edu.uiuc.ncsa.qdl.config;

import edu.uiuc.ncsa.qdl.evaluate.OpEvaluator;
import edu.uiuc.ncsa.security.core.util.AbstractEnvironment;
import edu.uiuc.ncsa.security.core.util.MyLoggingFacade;

import java.io.Serializable;
import java.util.List;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 2/27/20 at  9:11 AM
 */
public class QDLEnvironment extends AbstractEnvironment implements QDLConfigurationConstants, Serializable {
    /**
     * A convenience constructor to make an instance of this that is disabled. Nothing else is
     * initialized.
     */
    public QDLEnvironment() {
        this.enabled = false;
        this.name = "disabled";
    }

    public QDLEnvironment(MyLoggingFacade myLogger,
                          String cfgFile,
                          String name,
                          boolean isEnabled,
                          boolean isServerModeOn,
                          int numericDigits,
                          String bootScript,
                          String wsHomeDir,
                          String wsEnv,
                          boolean echoModeOn,
                          boolean prettyPrint,
                          boolean verboseOn,
                          boolean showBanner,
                          boolean compressionOn,
                          List<VFSConfig> vfsConfigs,
                          List<ModuleConfig> moduleConfigs,
                          String scriptPath,
                          String modulePath,
                          String libPath,
                          String debugLevel,
                          boolean autosaveOn,
                          long autosaveInterval,
                          boolean autosaveMessagesOn,
                          boolean useExternalEditor,
                          String externalEditorPath,
                          QDLEditors qdlEditors,
                          boolean enableLibrarySupport) {
        super(myLogger);
        this.cfgFile = cfgFile;
        this.name = name;
        this.enabled = isEnabled;
        this.serverModeOn = isServerModeOn;
        this.bootScript = bootScript;
        this.wsHomeDir = wsHomeDir;
        this.wsEnv = wsEnv;
        this.verboseOn = verboseOn;
        this.vfsConfigs = vfsConfigs;
        this.moduleConfigs = moduleConfigs;
        this.echoModeOn = echoModeOn;
        this.prettyPrint = prettyPrint;
        this.numericDigits = numericDigits;
        this.showBanner = showBanner;
        this.scriptPath = scriptPath;
        this.libPath = libPath;
        this.debugLevel = debugLevel;
        this.modulePath = modulePath;
        this.compressionOn = compressionOn;
        this.autosaveOn = autosaveOn;
        this.autosaveInterval = autosaveInterval;
        this.autosaveMessagesOn = autosaveMessagesOn;
        this.externalEditorPath = externalEditorPath;
        this.useExternalEditor = useExternalEditor;
        this.qdlEditors = qdlEditors;
        this.enableLibrarySupport = enableLibrarySupport;
    }

    public QDLEditors getQdlEditors() {
        return qdlEditors;
    }

    public void setQdlEditors(QDLEditors qdlEditors) {
        this.qdlEditors = qdlEditors;
    }

    QDLEditors qdlEditors;
    boolean useExternalEditor;

    public boolean isUseExternalEditor() {
        return useExternalEditor;
    }

    public void setUseExternalEditor(boolean useExternalEditor) {
        this.useExternalEditor = useExternalEditor;
    }

    public String getExternalEditorPath() {
        return externalEditorPath;
    }

    public void setExternalEditorPath(String externalEditorPath) {
        this.externalEditorPath = externalEditorPath;
    }

    String externalEditorPath;
    public boolean isAutosaveMessagesOn() {
        return autosaveMessagesOn;
    }

    public void setAutosaveMessagesOn(boolean autosaveMessagesOn) {
        this.autosaveMessagesOn = autosaveMessagesOn;
    }

    boolean autosaveMessagesOn;
    public long getAutosaveInterval() {
        return autosaveInterval;
    }

    public void setAutosaveInterval(long autosaveInterval) {
        this.autosaveInterval = autosaveInterval;
    }

    long autosaveInterval;
    public boolean isAutosaveOn() {
        return autosaveOn;
    }

    public void setAutosaveOn(boolean autosaveOn) {
        this.autosaveOn = autosaveOn;
    }

    boolean autosaveOn;

    public boolean isCompressionOn() {
        return compressionOn;
    }

    public void setCompressionOn(boolean compressionOn) {
        this.compressionOn = compressionOn;
    }

    public boolean compressionOn = false;

    public String getDebugLevel() {
        return debugLevel;
    }

    public void setDebugLevel(String debugLevel) {
        this.debugLevel = debugLevel;
    }

    String debugLevel;
    public boolean isPrettyPrint() {
        return prettyPrint;
    }

    public void setPrettyPrint(boolean prettyPrint) {
        this.prettyPrint = prettyPrint;
    }

    boolean prettyPrint = false;

    public boolean isShowBanner() {
        return showBanner;
    }

    boolean showBanner;

    public int getNumericDigits() {
        return numericDigits;
    }

    public void setNumericDigits(int numericDigits) {
        this.numericDigits = numericDigits;
    }

    int numericDigits = OpEvaluator.numericDigits;

    public String getCfgFile() {
        return cfgFile;
    }

    String cfgFile;

    public String getName() {
        return name;
    }

    String name = null;

    public boolean isEchoModeOn() {
        return echoModeOn;
    }

    boolean echoModeOn = true;
    List<VFSConfig> vfsConfigs;

    public List<VFSConfig> getVFSConfigurations() {
        return vfsConfigs;
    }

    List<ModuleConfig> moduleConfigs;

    public List<ModuleConfig> getModuleConfigs() {
        return moduleConfigs;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public boolean isServerModeOn() {
        return serverModeOn;
    }

    boolean enabled = true;
    boolean serverModeOn = false;

    String bootScript = null;

    public boolean hasBootScript() {
        return bootScript != null && !bootScript.isEmpty();
    }

    public String getBootScript() {
        return bootScript;
    }

    /**
     * Should be set in the constructor. The setter here is allowed if the user is overriding it from the command line.
     *
     * @param wsHomeDir
     */
    public void setWsHomeDir(String wsHomeDir) {
        this.wsHomeDir = wsHomeDir;
    }

    String wsHomeDir = null;

    public String getWSHomeDir() {
        return wsHomeDir;
    }

    String wsEnv;

    public String getWSEnv() {
        return wsEnv;
    }

    boolean verboseOn = false;

    public boolean isWSVerboseOn() {
        return verboseOn;
    }

    String scriptPath = null;

    public String getScriptPath() {
        return scriptPath;
    }

    String modulePath = null;
    public String getModulePath(){
        return modulePath;
    }

    public String getLibPath() {
        return libPath;
    }

    String libPath = null;

    public boolean isEnableLibrarySupport() {
        return enableLibrarySupport;
    }

    public void setEnableLibrarySupport(boolean enableLibrarySupport) {
        this.enableLibrarySupport = enableLibrarySupport;
    }

    boolean enableLibrarySupport = false;
}
