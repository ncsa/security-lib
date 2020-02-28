package edu.uiuc.ncsa.qdl.config;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 2/27/20 at  7:40 AM
 */
public interface QDLConfigurationConstants {
    String CONFIG_FILE_FLAG = "-cfg";
    String CONFIG_NAME_FLAG = "-name";

    /*
    <!DOCTYPE config [
            <!ELEMENT config (qdl)*>
            <!ELEMENT qdl (virtual_file_systems|modules|boot_script|wp|logging)*>
            <!ATTLIST qdl
                    name CDATA #REQUIRED
                    enabled CDATA #REQUIRED
                    debug CDATA #REQUIRED
                    server_mode CDATA #REQUIRED>
                    */
    String CONFIG_TAG_NAME = "qdl";
    String CONFG_ATTR_NAME = "name";
    String CONFG_ATTR_ENABLED = "enabled";
    String CONFG_ATTR_DEBUG = "debug";
    String CONFG_ATTR_SERVER_MODE_ENABLED = "server_mode";

    /*
            <!ELEMENT  (vfs)*>
            <!ELEMENT vfs (root_dir|scheme|mount_point)*>
            <!ATTLIST vfs
                    type CDATA #REQUIRED
                    access CDATA #REQUIRED>
            <!ELEMENT root_dir (#PCDATA)>
            <!ELEMENT scheme (#PCDATA)>
            <!ELEMENT mount_point (#PCDATA)>
            */
    String VIRTUAL_FILE_SYSTEMS_TAG_NAME = "virtual_file_systems";
    String VFS_TAG_NAME = "vfs";
    String VFS_ATTR_TYPE = "type";
    String VFS_ATTR_ACCESS = "access";

    String VFS_TYPE_PASS_THRU = "pass_through";
    String VFS_ROOT_DIR_TAG = "root_dir";
    String VFS_SCHEME_TAG = "scheme";
    String VFS_MOUNT_POINT_TAG = "mount_point";
    String VFS_TYPE_PASS_THROUGH = "pass_through";
    String VFS_TYPE_MYSQL = "mysql";

    /*
            <!ELEMENT modules (module)*>
            <!ELEMENT module (class_name|path)*>
            <!ATTLIST module
                    load_on_start CDATA #IMPLIED
                    type CDATA #REQUIRED>
            <!ELEMENT class_name (#PCDATA)>
            <!ELEMENT path (#PCDATA)>
            <!ELEMENT boot_script (#PCDATA)>
            */
    String MODULES_TAG_NAME = "modules";
    String MODULE_TAG_NAME = "module";
    // The next attribute only works for Java module. QDL modules can specify in their scripts if they should be
    // imported and how.
    String MODULE_ATTR_IMPORT_ON_START = "import_on_start";
    String MODULE_ATTR_TYPE = "type";
    String MODULE_PATH_TAG = "path";
    String MODULE_CLASS_NAME_TAG = "class_name";
    String MODULE_TYPE_JAVA = "java";
    String MODULE_TYPE_QDL = "qdl";
    String BOOT_SCRIPT_TAG = "boot_script";

    /*
            <!ELEMENT wp (home_dir|env)*>
            <!ATTLIST wp
                    verbose CDATA #REQUIRED>
            <!ELEMENT home_dir (#PCDATA)>
            <!ELEMENT env (#PCDATA)>
            <!ELEMENT logging (#PCDATA)>
            */
    String WS_TAG = "workspace";
    String WS_ATTR_VERBOSE = "verbose";
    String WS_ATTR_ECHO_MODE_ON = "echoModeOn";
    String WS_ENV = "env";
    String WS_HOME_DIR_TAG = "home_dir";

    /*
            <!ATTLIST logging
                    logFileName CDATA #REQUIRED
                    logName CDATA #REQUIRED
                    logSize CDATA #REQUIRED
                    logFileCount CDATA #REQUIRED
                    debug CDATA #REQUIRED>
            ]>
     */


}
