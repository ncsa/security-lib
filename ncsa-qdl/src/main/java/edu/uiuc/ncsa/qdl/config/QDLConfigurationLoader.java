package edu.uiuc.ncsa.qdl.config;

import edu.uiuc.ncsa.qdl.exceptions.QDLException;
import edu.uiuc.ncsa.qdl.util.QDLVersion;
import edu.uiuc.ncsa.security.core.configuration.StorageConfigurationTags;
import edu.uiuc.ncsa.security.core.util.ConfigurationLoader;
import edu.uiuc.ncsa.security.core.util.LoggingConfigLoader;
import edu.uiuc.ncsa.security.core.util.MyLoggingFacade;
import edu.uiuc.ncsa.security.util.configuration.ConfigUtil;
import org.apache.commons.configuration.tree.ConfigurationNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static edu.uiuc.ncsa.security.core.configuration.Configurations.*;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 2/27/20 at  7:37 AM
 */
public class QDLConfigurationLoader<T extends QDLEnvironment> extends LoggingConfigLoader<T> implements QDLConfigurationConstants, ConfigurationLoader<T> {
    public QDLConfigurationLoader(ConfigurationNode node) {
        super(node, null); // This makes it read the logging configuration and create the logger. Logging should just work
    }

    /**
     * Use only in those cases where the logger is constructed elsewhere. This will override the logging
     * configuration and use this logger instead.
     *
     * @param node
     * @param logger
     */
    public QDLConfigurationLoader(ConfigurationNode node, MyLoggingFacade logger) {
        super(node, logger);
    }

    @Override
    public T load() {
        T env = createInstance();
        return env;
    }

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

    protected boolean getFirstBooleanValue(ConfigurationNode node, String attrib, boolean defaultValue) {
        if (node == null) return defaultValue;
        try {
            return Boolean.parseBoolean(getFirstAttribute(node, attrib));
        } catch (Throwable t) {

        }
        return defaultValue;
    }

    protected boolean isWSVerboseOn() {
        ConfigurationNode node = getFirstNode(cn, WS_TAG);
        return getFirstBooleanValue(node, WS_ATTR_VERBOSE, false);
    }

    protected boolean isEnabled() {
        return getFirstBooleanValue(cn, CONFG_ATTR_ENABLED, true);
    }

    protected String getName() {
        String name = getFirstAttribute(cn, CONFG_ATTR_NAME);
        if (name == null || name.isEmpty()) {
            return "(none)";
        }
        return name;

    }

    protected boolean isServerModeOn() {
        return getFirstBooleanValue(cn, CONFG_ATTR_SERVER_MODE_ENABLED, false);
    }

    protected boolean isEchoModeOn() {
        ConfigurationNode node = getFirstNode(cn, WS_TAG);
        return getFirstBooleanValue(node, WS_ATTR_ECHO_MODE_ON, true);
    }

    protected List<VFSConfig> getVFSConfigs() {
        ArrayList<VFSConfig> configs = new ArrayList<>();
        ConfigurationNode vNode = getFirstNode(cn, VIRTUAL_FILE_SYSTEMS_TAG_NAME);
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
                v.path = getNodeValue(kid, MODULE_PATH_TAG);
                configs.add(v);
            }
        }
        return configs;

    }

    @Override
    public T createInstance() {
        return (T) new QDLEnvironment(myLogger,
                getName(),
                isEnabled(),
                isServerModeOn(),
                getBootScript(),
                getWSHomeDir(),
                getWSEnvFile(),
                isEchoModeOn(),
                isWSVerboseOn(),
                getVFSConfigs(),
                getModuleConfigs());
    }

    @Override
    public HashMap<String, String> getConstants() {
        return null;
    }


    public static void main(String[] args) {
        String path = "/home/ncsa/dev/ncsa-git/security-lib/ncsa-qdl/src/main/resources/qdl-cfg.xml";
        ConfigurationNode node = ConfigUtil.findConfiguration(path, "test", QDLConfigurationConstants.CONFIG_TAG_NAME);
        QDLConfigurationLoader loader = new QDLConfigurationLoader(node);

        QDLEnvironment config = loader.load();
        System.out.println("Root node = " + node.getName());
        System.out.println("is enabled = " + config.isEnabled());
        System.out.println("server mode on = " + config.isServerModeOn());
        System.out.println("boot script = " + config.getBootScript());
        System.out.println("ws env = " + config.getWSEnv());
        System.out.println("ws home dir = " + config.getWSHomeDir());
        System.out.println("ws verbose = " + config.isWSVerboseOn());
        System.out.println("ws echo mode on = " + config.isEchoModeOn());
        System.out.println("vfs config = " + config.getVFSConfigurations());
        System.out.println("module config = " + config.getModuleConfigs());

    }

    @Override
    public String getVersionString() {
        return QDLVersion.VERSION;
    }
}