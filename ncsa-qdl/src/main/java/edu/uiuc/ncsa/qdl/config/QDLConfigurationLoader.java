package edu.uiuc.ncsa.qdl.config;

import edu.uiuc.ncsa.qdl.evaluate.OpEvaluator;
import edu.uiuc.ncsa.qdl.exceptions.QDLException;
import edu.uiuc.ncsa.qdl.util.QDLVersion;
import edu.uiuc.ncsa.qdl.workspace.WorkspaceCommands;
import edu.uiuc.ncsa.security.core.configuration.StorageConfigurationTags;
import edu.uiuc.ncsa.security.core.util.ConfigurationLoader;
import edu.uiuc.ncsa.security.core.util.DebugUtil;
import edu.uiuc.ncsa.security.core.util.LoggingConfigLoader;
import edu.uiuc.ncsa.security.core.util.MyLoggingFacade;
import edu.uiuc.ncsa.security.util.cli.editing.EditorEntry;
import edu.uiuc.ncsa.security.util.cli.editing.EditorUtils;
import edu.uiuc.ncsa.security.util.cli.editing.Editors;
import edu.uiuc.ncsa.security.util.configuration.ConfigUtil;
import org.apache.commons.configuration.tree.ConfigurationNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static edu.uiuc.ncsa.security.core.configuration.Configurations.*;
import static edu.uiuc.ncsa.security.core.util.StringUtils.isTrivial;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 2/27/20 at  7:37 AM
 */
public class QDLConfigurationLoader<T extends QDLEnvironment> extends LoggingConfigLoader<T> implements QDLConfigurationConstants, ConfigurationLoader<T> {
    public QDLConfigurationLoader(String cfgFile, ConfigurationNode node) {
        super("qdl.log", "qdl", node, null); // This makes it read the logging configuration and create the logger. Logging should just work
        configFile = cfgFile;
    }

    /**
     * Use only in those cases where the logger is constructed elsewhere. This will override the logging
     * configuration and use this logger instead.
     *
     * @param node
     * @param logger
     */
    public QDLConfigurationLoader(String cfgFile, ConfigurationNode node, MyLoggingFacade logger) {
        // set defaults for the logger if none configured or you get references to NCSA Delegation
        super("qdl.log", "qdl", node, logger);
        configFile = cfgFile;
    }

    @Override
    public T load() {
        T env = createInstance();
        return env;
    }

    /**
     * This is set to point to the configuration file (that information is not contained inside the file). It is optional.
     *
     * @param configFile
     */
    public void setConfigFile(String configFile) {
        this.configFile = configFile;
    }

    protected String getConfigFile() {
        return configFile;
    }

    String configFile = null;

    protected String getBootScript() {
        return getNodeValue(cn, BOOT_SCRIPT_TAG, "");
    }

    protected String getWSEnvFile() {
        ConfigurationNode node = getFirstNode(cn, WS_TAG);
        return getNodeValue(node, WS_ENV, "");
    }

    protected String getWSHomeDir() {
        ConfigurationNode node = getFirstNode(cn, WS_TAG);
        return getNodeValue(node, WS_HOME_DIR_TAG, "");
    }

    protected boolean getCompressionOn() {
        ConfigurationNode node = getFirstNode(cn, WS_TAG);
        return getFirstBooleanValue(node, WS_COMPRESS_SERIALIZATION_TAG, true);
    }

    protected String getScriptPath() {
        String x = getFirstAttribute(cn, SCRIPT_PATH_TAG);
        return x == null ? "" : x;
    }

    protected String getLibPath() {
        String x = getFirstAttribute(cn, LIB_PATH_TAG);
        return x == null ? "" : x;
    }

    protected String getModulePath() {
        String x = getFirstAttribute(cn, MODULE_PATH_TAG);
        return x == null ? "" : x;
    }

    protected boolean getFirstBooleanValue(ConfigurationNode node, String attrib, boolean defaultValue) {
        if (node == null) return defaultValue;
        try {
            String x = getFirstAttribute(node, attrib);
            if (isTrivial(x)) {
                return defaultValue;
            } //  Null argument returns false.
            return Boolean.parseBoolean(x);
        } catch (Throwable t) {

        }
        return defaultValue;
    }

    protected boolean useWSExternalEditor() {
        ConfigurationNode node = getFirstNode(cn, WS_TAG);
        return getFirstBooleanValue(node, WS_EDITOR_ENABLE, false);
    }

    protected String getExternalEditorPath() {
        ConfigurationNode node = getFirstNode(cn, WS_TAG);
        String x = getFirstAttribute(node, WS_EDITOR_NAME);
        if (isTrivial(x)) {
            return "";
        }
        return x;
    }
    protected String getSaveDir() {
         ConfigurationNode node = getFirstNode(cn, WS_TAG);
         String x = getFirstAttribute(node, WS_SAVE_DIR);
         if (isTrivial(x)) {
             return null; // means none set.
         }
         return x;
     }

    protected Editors getEditors() {
        Editors editors = EditorUtils.getEditors(cn); // never null
        if (!editors.hasEntry(WorkspaceCommands.LINE_EDITOR_NAME)) {
            /*
               Create the line editor so there is always something available.
             */
            EditorEntry qdlEditor = new EditorEntry();
            qdlEditor.name = WorkspaceCommands.LINE_EDITOR_NAME;
            //qdlEditor.exec = null;
            //qdlEditor.clearScreen = false;
            editors.put(qdlEditor);
        }
        return editors;
    }


    protected boolean isWSVerboseOn() {
        ConfigurationNode node = getFirstNode(cn, WS_TAG);
        return getFirstBooleanValue(node, WS_ATTR_VERBOSE, false);
    }

    protected boolean showBanner() {
        ConfigurationNode node = getFirstNode(cn, WS_TAG);
        return getFirstBooleanValue(node, WS_ATTR_SHOW_BANNER, true);
    }

    protected boolean isEnabled() {
        return getFirstBooleanValue(cn, CONFG_ATTR_ENABLED, true);
    }


    protected String getName() {
        String name = getFirstAttribute(cn, CONFG_ATTR_NAME);
        if (isTrivial(name)) {
            return "(none)";
        }
        return name;
    }


    protected boolean isServerModeOn() {
        return getFirstBooleanValue(cn, CONFG_ATTR_SERVER_MODE_ENABLED, false);
    }

    protected boolean areAssertionsEnabled() {
        return getFirstBooleanValue(cn, CONFG_ATTR_ASSERTIONS_ENABLED, true);
    }

    protected String getDebugLevel() {
        String level = getFirstAttribute(cn, CONFG_ATTR_DEBUG);
        if (level == null) {
            level = DebugUtil.DEBUG_LEVEL_OFF_LABEL;
        }
        return level;
    }

    protected boolean isEchoModeOn() {
        ConfigurationNode node = getFirstNode(cn, WS_TAG);
        return getFirstBooleanValue(node, WS_ATTR_ECHO_MODE_ON, true);
    }

    protected boolean isPrettyPrint() {
        ConfigurationNode node = getFirstNode(cn, WS_TAG);
        return getFirstBooleanValue(node, WS_ATTR_PRETTY_PRINT, false);
    }

    protected boolean isAutosaveOn() {
        ConfigurationNode node = getFirstNode(cn, WS_TAG);
        return getFirstBooleanValue(node, WS_ATTR_AUTOSAVE_ON, false);
    }

    protected boolean isAutosaveMessagesOn() {
        ConfigurationNode node = getFirstNode(cn, WS_TAG);
        return getFirstBooleanValue(node, WS_ATTR_AUTOSAVE_MESSAGES_ON, false);
    }

    protected long getAutosaveInterval() {
        ConfigurationNode node = getFirstNode(cn, WS_TAG);
        String rawValue = getFirstAttribute(node, WS_ATTR_AUTOSAVE_INTERVAL);
        long autosaveInterval = 10 * 60 * 1000L; // 10 minutes in milliseconds.
        if (rawValue != null) {
            autosaveInterval = ConfigUtil.getValueSecsOrMillis(rawValue);
        }
        return autosaveInterval;
    }

    protected boolean isEnableLibrarySupport() {
        return getFirstBooleanValue(cn, ENABLE_LIBRARY_SUPPORT, true);
    }
      protected boolean isRunInitOnLoad(){
          ConfigurationNode node = getFirstNode(cn, WS_TAG);
          return getFirstBooleanValue(cn, RUN_INIT_ON_LOAD, true);

      }
    protected int getNumericDigits() {
        String raw = getFirstAttribute(cn, CONFG_ATTR_NUMERIC_DIGITS);
        if (isTrivial(raw)) {
            return OpEvaluator.numericDigits;
        }
        try {
            return Integer.parseInt(raw);
        } catch (Throwable t) {
            return OpEvaluator.numericDigits;
        }
    }

    protected List<VFSConfig> getVFSConfigs() {
        ArrayList<VFSConfig> configs = new ArrayList<>();
        ConfigurationNode vNode = getFirstNode(cn, VIRTUAL_FILE_SYSTEMS_TAG_NAME);
        if (vNode == null) {
            return new ArrayList<>();
        }
        // need to snoop through children and create VFSEntries.
        for (ConfigurationNode kid : vNode.getChildren()) {
            String access = getFirstAttribute(kid, VFS_ATTR_ACCESS);
            VFSAbstractConfig v = null;
            switch (getFirstAttribute(kid, VFS_ATTR_TYPE)) {
                case VFS_TYPE_PASS_THROUGH:
                    v = new VFSPassThroughConfig(
                            getNodeValue(kid, VFS_ROOT_DIR_TAG),
                            getNodeValue(kid, VFS_SCHEME_TAG),
                            getNodeValue(kid, VFS_MOUNT_POINT_TAG),
                            access.contains("r"),
                            access.contains("w")
                    );
                    break;
                case VFS_TYPE_ZIP:
                    v = new VFSZipFileConfig(getNodeValue(kid, VFS_ZIP_FILE_PATH),
                            getNodeValue(kid, VFS_SCHEME_TAG),
                            getNodeValue(kid, VFS_MOUNT_POINT_TAG),
                            access.contains("r"),
                            false);
                    break;
                case VFS_TYPE_MYSQL:
                    VFSSQLConfig vv = new VFSSQLConfig(
                            getNodeValue(kid, VFS_SCHEME_TAG),
                            getNodeValue(kid, VFS_MOUNT_POINT_TAG),
                            access.contains("r"),
                            access.contains("w")
                    );
                    ConfigurationNode myNode = getFirstNode(kid, StorageConfigurationTags.MYSQL_STORE);
                    // stash everything into the map.
                    Map<String, String> map = vv.getConnectionParameters();
                    for (ConfigurationNode attr : myNode.getAttributes()) {
                        map.put(attr.getName(), attr.getValue().toString());
                    }
                    // Add the defaults here if they are missing
                    v = vv;
                    break;
                case VFS_TYPE_MEMORY:
                    v = new VFSMemoryConfig(
                            getNodeValue(kid, VFS_SCHEME_TAG),
                            getNodeValue(kid, VFS_MOUNT_POINT_TAG),
                            access.contains("r"),
                            access.contains("w")
                    );
                    break;
                default:
                    throw new QDLException("Error: unsupported VFS type of " + getFirstAttribute(kid, VFS_ATTR_TYPE));

            }
            configs.add(v);

        }
        return configs;
    }

    protected List<ModuleConfig> getModuleConfigs() {
        ArrayList<ModuleConfig> configs = new ArrayList<>();
        ConfigurationNode vNode = getFirstNode(cn, MODULES_TAG_NAME);
        if (vNode == null) {
            return new ArrayList<>();
        }
        // need to snoop through children and create VFSEntries.
        for (ConfigurationNode kid : vNode.getChildren()) {
            if (getFirstAttribute(kid, MODULE_ATTR_TYPE).equals(MODULE_TYPE_JAVA)) {
                JavaModuleConfig v = new JavaModuleConfig();
                v.loadOnStart = getFirstBooleanValue(kid, MODULE_ATTR_IMPORT_ON_START, false);
                v.className = getNodeValue(kid, MODULE_CLASS_NAME_TAG);
                configs.add(v);
            }
            if (getFirstAttribute(kid, MODULE_ATTR_TYPE).equals(MODULE_TYPE_QDL)) {
                QDLModuleConfig v = new QDLModuleConfig();
                v.importOnStart = getFirstBooleanValue(kid, MODULE_ATTR_IMPORT_ON_START, false);
                v.path = getNodeValue(kid, QDL_MODULE_PATH_TAG);
                configs.add(v);
            }
        }
        return configs;

    }

    @Override
    public T createInstance() {
        return (T) new QDLEnvironment(
                myLogger,
                getConfigFile(),
                getName(),
                isEnabled(),
                isServerModeOn(),
                getNumericDigits(),
                getBootScript(),
                getWSHomeDir(),
                getWSEnvFile(),
                isEchoModeOn(),
                isPrettyPrint(),
                isWSVerboseOn(),
                getCompressionOn(),
                showBanner(),
                getVFSConfigs(),
                getModuleConfigs(),
                getScriptPath(),
                getModulePath(),
                getLibPath(),
                getDebugLevel(),
                isAutosaveOn(),
                getAutosaveInterval(),
                isAutosaveMessagesOn(),
                useWSExternalEditor(),
                getExternalEditorPath(),
                getEditors(),
                isEnableLibrarySupport(),
                areAssertionsEnabled(),
                getSaveDir());
    }

    @Override
    public HashMap<String, String> getConstants() {
        return null;
    }


    public static void main(String[] args) {
        String path = "/home/ncsa/dev/ncsa-git/security-lib/ncsa-qdl/src/main/resources/qdl-cfg.xml";
        ConfigurationNode node = ConfigUtil.findConfiguration(path, "test", QDLConfigurationConstants.CONFIG_TAG_NAME);
        QDLConfigurationLoader loader = new QDLConfigurationLoader(path, node);

        QDLEnvironment config = loader.load();
        System.out.println("Root node = " + node.getName());
        System.out.println("is enabled = " + config.isEnabled());
        System.out.println("server mode on = " + config.isServerModeOn());
        System.out.println("boot script = " + config.getBootScript());
        System.out.println("ws env = " + config.getWSEnv());
        System.out.println("ws home dir = " + config.getWSHomeDir());
        System.out.println("ws verbose = " + config.isWSVerboseOn());
        System.out.println("ws echo mode on = " + config.isEchoModeOn());
        System.out.println("ws pretty print = " + config.isPrettyPrint());
        System.out.println("vfs config = " + config.getVFSConfigurations());
        System.out.println("module config = " + config.getModuleConfigs());
    }

    @Override
    public String getVersionString() {
        return QDLVersion.VERSION;
    }
}
