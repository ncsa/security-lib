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
    String MODULE_SOURCE_TAG = "source";
    String MODULE_DOCUMENTATION_TAG = "documentation";

    String WORKSPACE_TAG = "workspace";
    String WS_ENV_TAG = "env";
    String EXTRINSIC_VARIABLES_TAG = "extrinsic_variables";
    String ENV_FILE = "env_file";
    String PRETTY_PRINT = "pretty_print";
    String OVERWRITE_BASE_FUNCTIONS = "overwrite_base_functions";
    String BUFFER_DEFAULT_SAVE_PATH = "buffer_default_save_path";
    String ECHO_MODE = "echo_mode";
    String DEBUG_MODE = "debug_mode";
    String AUTOSAVE_ON = "autosave_on";
    String AUTOSAVE_INTERVAL = "autosave_interval";
    String ENABLE_LIBRARY_SUPPORT = "enable_library_support";
    String EXTERNAL_EDITOR_NAME = "external_editor";
    String AUTOSAVE_MESSAGES_ON = "autosave_messages_on";
    String START_TS = "start_ts";
    String ROOT_DIR = "root_dir";
    String SAVE_DIR = "save_dir";
    String RUN_SCRIPT_PATH = "run_script_path";
    String RUN_INIT_ON_LOAD = "run_init_on_load";
    String CURRENT_PID = "current_pid";
    String CURRENT_WORKSPACE = "current_workspace"; // default file for saves
    String EDITOR_CLIPBOARD = "editor_clipboard";
    String ENV_PROPERTIES = "env_properties";
    String SCRIPT_PATH = "script_path";
    String MODULE_PATH = "module_path";
    String COMPRESS_XML = "compress_xml";
    String WS_ID = "ws_id";
    String DESCRIPTION = "description";
    String EXTERNAL_EDITOR_PATH = "external_editor_path";
    String USE_EXTERNAL_EDITOR = "use_external_editor";
    String COMMAND_HISTORY = "command_history";
    String ASSERTIONS_ON = "assertions_on";


    String VARIABLE_TAG = "var";
    String VARIABLE_NAME_TAG = "name";
    String FUNCTION_TABLE_STACK_TAG = "function_stack";
    String FUNCTIONS_TAG = "functions";
    String FUNCTION_TAG = "func";
    String FUNCTION_NAME_TAG = "name";
    String FUNCTION_ARG_COUNT_TAG = "arg_count";


    String STACKS_TAG = "stacks";
    String STACK_TAG = "stack";


    String STATE_TAG = "state";
    String STATE_ID_TAG = "internal_id";
    String OLD_IMPORTED_MODULES_TAG = "imports";
    String OLD_MODULE_TEMPLATE_TAG = "templates";

    String IMPORTED_MODULES_TAG = "module_imports";
    String MODULE_TEMPLATE_TAG = "module_templates";

    String MODULE_STACK_TAG = "module_stack";

    String BUFFER_MANAGER = "buffer_manager";
    String BUFFER_RECORDS = "records";
    String BUFFER_RECORD = "record";
    String BR_SOURCE = "src";
    String BR_ALIAS = "alias";
    String BR_SOURCE_SAVE_PATH = "src_save_path";
    String BR_LINK = "link";
    String BR_LINK_SAVE_PATH = "link_save_path";
    String BR_EDITED = "edited";
    String BR_MEMORY_ONLY = "memory_only";
    String BR_DELETED = "deleted";
    String BR_CONTENT = "content";

    // Version 2 tags
    String SERIALIZATION_VERSION_TAG = "serialization_version";
    String VERSION_2_0_TAG = "2.0";
    String TEMPLATE_REFERENCE_TAG = "template_reference";
    String INSTANCE_REFERENCE_TAG = "instance_reference";
    String STATE_REFERENCE_TAG = "state_reference";
    String UUID_TAG = "uuid";
    String STATE_CONSTANTS_TAG = "constants";
    String STATE_RESTRICTED_IO_TAG = "restricted_io";
    String STATE_SERVER_MODE_TAG = "server_mode";
    String STATE_PID_TAG = "pid";
    String STATE_ASSERTIONS_ENABLED_TAG = "assertions_on";
    String STATE_NUMERIC_DIGITS_TAG = "numeric_digits";
    String STATES_TAG = "states";
    String MODULE_INSTANCES_TAG = "module_instances";
    String TEMPLATE_STACK = "template_stack";
    String INSTANCE_STACK = "instance_stack";
    String MODULES_TAG = "modules";
    String VARIABLE_STACK = "variable_stack";
    String VARIABLES_TAG = "variables";

}
