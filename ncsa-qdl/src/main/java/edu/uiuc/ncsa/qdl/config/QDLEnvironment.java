package edu.uiuc.ncsa.qdl.config;

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
                          String name,
                          boolean isEnabled,
                          boolean isServerModeOn,
                          String bootScript,
                          String wsHomeDir,
                          String wsEnv,
                          boolean echoModeOn,
                          boolean verboseOn,
                          List<VFSConfig> vfsConfigs,
                          List<ModuleConfig> moduleConfigs) {
        super(myLogger);
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
    }

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

}
