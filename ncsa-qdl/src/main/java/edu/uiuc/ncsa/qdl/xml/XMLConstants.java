package edu.uiuc.ncsa.qdl.xml;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 12/27/20 at  7:02 AM
 */
public interface XMLConstants {
    String INTEGER_TAG = "integer";
    String BOOLEAN_TAG = "boolean";
    String DECIMAL_TAG = "decimal";
    String NULL_TAG = "null";
    String STRING_TAG = "string";
    String STEM_TAG = "stem";
    String STEM_KEY_TAG = "key";
    String STEM_ENTRY_TAG = "entry";
    String LIST_INDEX_ATTR = "index";

    String MODULE_TAG = "module";
    String MODULE_NS_ATTR = "namespace";
    String MODULE_ALIAS_ATTR = "alias";
    String MODULE_TYPE_TAG = "type";
    String MODULE_TYPE_JAVA_TAG = "java";
    String MODULE_CLASS_NAME_TAG = "class_name";

    String WORKSPACE_TAG = "workspace";
    String WS_ENV_TAG = "env";
    String ENV_FILE = "env_file";
    String PRETTY_PRINT = "pretty_print";
    String ECHO_MODE = "echo_mode";
    String DEBUG_MODE = "debug_mode";
    String START_TS = "start_ts";
    String ROOT_DIR = "root_dir";
    String SAVE_DIR = "save_dir";
    String RUN_SCRIPT_PATH = "run_script_path";
    String CURRENT_PID = "current_pid";
    String CURRENT_WORKSPACE = "current_workspace"; // default file for saves
    String EDITOR_CLIPBOARD = "editor_clipboard";
    String ENV_PROPERTIES = "env_properties";
    String SCRIPT_PATH = "script_path";
    String MODULE_PATH = "module_path";
    String COMPRESS_XML = "compress_xml";
    String WS_ID = "ws_id";
    String DESCRIPTION = "description";


    String VARIABLE_TAG = "var";
    String VARIABLE_NAME_TAG = "name";
    String FUNCTIONS_TAG = "functions";
    String FUNCTION_TAG = "func";
    String FUNCTION_NAME_TAG = "name";
    String FUNCTION_ARG_COUNT_TAG = "arg_count";


    String STACKS_TAG = "stacks";
    String STACK_TAG = "stack";


    String STATE_TAG = "state";
    String STATE_ID_TAG = "internal_id";
    String IMPORTED_MODULES = "imports";
    String MODULE_TEMPLATE_TAG = "templates";
    /*
     String src;
        String link;
        boolean edited = false;
        boolean deleted = false;
        List<String> content = null;
     */
    String BUFFER_MANAGER = "buffer_manager";
    String BUFFER_RECORDS = "records";
    String BUFFER_RECORD = "record";
    String BR_SOURCE = "src";
    String BR_LINK = "link";
    String BR_EDITED = "edited";
    String BR_DELETED = "deleted";
    String BR_CONTENT = "content";
}