package edu.uiuc.ncsa.qdl.config;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 2/27/20 at  7:40 AM
 */
public interface QDLConfigurationConstants {
    String CONFIG_FILE_FLAG = "-cfg";
    String CONFIG_NAME_FLAG = "-name";

    String CONFIG_TAG_NAME = "qdl";
    String CONFG_ATTR_NAME = "name";
    String CONFG_ATTR_ENABLED = "enabled";
    String CONFG_ATTR_DEBUG = "debug";
    String CONFG_ATTR_SERVER_MODE_ENABLED = "server_mode";
    String CONFG_ATTR_RESTRICTED_IO_RESTRICTED = "restricted_io";
    String CONFG_ATTR_ASSERTIONS_ENABLED = "assertions_on";
    String CONFG_ATTR_NUMERIC_DIGITS = "numeric_digits";
    String VIRTUAL_FILE_SYSTEMS_TAG_NAME = "virtual_file_systems";
    String VFS_TAG_NAME = "vfs";
    String VFS_ATTR_TYPE = "type";
    String VFS_ATTR_ACCESS = "access";

    String VFS_ROOT_DIR_TAG = "root_dir";
    String VFS_ZIP_FILE_PATH = "zip_file";
    String VFS_SCHEME_TAG = "scheme";
    String VFS_MOUNT_POINT_TAG = "mount_point";
    String VFS_TYPE_PASS_THROUGH = "pass_through";
    String VFS_TYPE_MYSQL = "mysql";
    String VFS_TYPE_MEMORY = "memory";
    String VFS_TYPE_ZIP = "zip";

    String MODULES_TAG_NAME = "modules";
    String MODULE_TAG_NAME = "module";
    // The next attribute only works for Java module. QDL modules can specify in their scripts if they should be
    // imported and how.
    String MODULE_ATTR_IMPORT_ON_START = "import_on_start";
    String MODULE_ATTR_TYPE = "type";
    String MODULE_TYPE_JAVA = "java";
    String MODULE_TYPE_QDL = "qdl";
    String MODULE_CLASS_NAME_TAG = "class_name";
    String QDL_MODULE_PATH_TAG = "path";
    String BOOT_SCRIPT_TAG = "boot_script";
    String WS_TAG = "workspace";
    String WS_ATTR_VERBOSE = "verbose";
    String WS_ATTR_SHOW_BANNER = "showBanner";
    String WS_ATTR_ECHO_MODE_ON = "echoModeOn";
    String WS_ATTR_PRETTY_PRINT = "prettyPrint";
    String WS_ATTR_AUTOSAVE_ON = "autoSaveOn";
    String WS_ATTR_AUTOSAVE_INTERVAL = "autoSaveInterval";
    String WS_ATTR_AUTOSAVE_MESSAGES_ON = "autoSaveMessagesOn";
    String WS_ENV = "env";
    String WS_HOME_DIR_TAG = "home_dir";
    String WS_SAVE_DIR = "save_dir"; // for specifying the initial WS directory.
    String WS_COMPRESS_SERIALIZATION_TAG = "compress_xml";
    String WS_EDITOR_NAME = "editor_name";
    String WS_EDITOR_ENABLE = "use_editor";
    String SCRIPT_PATH_TAG = "script_path";
    String MODULE_PATH_TAG = "module_path";
    String LIB_PATH_TAG = "lib_path";
    String ENABLE_LIBRARY_SUPPORT = "enable_library_support";
    String RUN_INIT_ON_LOAD = "run_init_on_load";


}
