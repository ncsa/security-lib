package edu.uiuc.ncsa.qdl.state;

import java.util.Arrays;
import java.util.List;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 4/20/20 at  12:53 PM
 */
public interface QDLConstants {
    /*
      varTypes.put("string", new Long(Constant.STRING_TYPE));
        varTypes.put("stem", new Long(Constant.STEM_TYPE));
        varTypes.put("boolean", new Long(Constant.BOOLEAN_TYPE));
        varTypes.put("null", new Long(Constant.NULL_TYPE));
        varTypes.put("integer", new Long(Constant.LONG_TYPE));
        varTypes.put("decimal", new Long(Constant.DECIMAL_TYPE));
        varTypes.put("undefined", new Long(Constant.UNKNOWN_TYPE));
        systemConstants.put("", varTypes);
        StemVariable errorCodes = new StemVariable();
        errorCodes.put("system_error", TryCatch.RESERVED_ERROR_CODE);
        systemConstants.put("error_codes.", errorCodes);
        StemVariable fileTypes = new StemVariable();
        fileTypes.put("binary", new Long(IOEvaluator.FILE_OP_BINARY));
        fileTypes.put("stem", new Long(IOEvaluator.FILE_OP_TEXT_STEM));
        fileTypes.put("string", new Long(IOEvaluator.FILE_OP_TEXT_STRING));
        systemConstants.put("file_types.", errorCodes);
     */
    // Only three reserved words in QDL. Used in the parser.

    String RESERVED_TRUE = "true";
    //String RESERVED_TRUE2 = "⊤";
    String RESERVED_FALSE = "false";
    //String RESERVED_FALSE2 = "⊥";
    String RESERVED_NULL = "null";
    String RESERVED_NULL_SET = "∅";
  //  String RESERVED_NULL_SET = "null_set";
  //  String RESERVED_COMPLEX_I = "I";

    String FUNCTION_REFERENCE_MARKER = "@";
    String FUNCTION_REFERENCE_MARKER2 = "⊗"; // /u2297



    static boolean isReservedWord(String x) {
        return RESERVED_TRUE.equals(x) || RESERVED_FALSE.equals(x) || RESERVED_NULL.equals(x);
    }
    // For the system constants:

    String SYS_DETOKENIZE_TYPE = "detokenize.";
    String SYS_DETOKENIZE_PREPEND = "prepend";
    String SYS_DETOKENIZE_APPEND = "append";
    String SYS_DETOKENIZE_OMIT_DANGLING_DELIMITER = "omit_dangling_delimiter";


    String SYS_VAR_TYPES = "var_type.";
    String SYS_VAR_TYPE_DECIMAL = "decimal";
    String SYS_VAR_TYPE_INTEGER = "integer";
    String SYS_VAR_TYPE_STEM = "stem";
    String SYS_VAR_TYPE_BOOLEAN = "boolean";
    String SYS_VAR_TYPE_NULL = RESERVED_NULL;
    String SYS_VAR_TYPE_UNDEFINED = "undefined";
    String SYS_VAR_TYPE_STRING = "string";
    String SYS_VAR_TYPE_SET = "set";
    String SYS_VAR_TYPE_CHARACTERS = "characters";

    String SYS_ERROR_CODES = "error_codes.";
    String SYS_ERROR_CODE_SYSTEM_ERROR = "system_error";
    String SYS_ASSERT_CODE_SYSTEM_ERROR = "assert_error_code";
    String SYS_ERROR_CODE_DEFAULT_USER_ERROR = "default_user_error";

    String SYS_FILE_TYPES = "file_types.";
    String SYS_FILE_TYPE_BINARY = "binary";
    String SYS_FILE_TYPE_STEM = "stem";
    String SYS_FILE_TYPE_STRING = "string";
    String SYS_FILE_TYPE_INIT = "ini";

    String SYS_INFO_USER = "user.";
    String SYS_INFO_USER_INVOCATION_DIR = "invocation_dir";
    String SYS_INFO_USER_HOME_DIR = "home_dir";

    String SYS_INFO_SYSTEM = "system.";
    String SYS_INFO_SYSTEM_PROCESSORS = "processors";
    String SYS_INFO_INIT_MEMORY = "initial_memory";
    String SYS_INFO_JVM_VERSION = "jvm_version";

    String SYS_INFO_OS = "os.";
    String SYS_INFO_OS_ARCHITECTURE = "architecture";
    String SYS_INFO_OS_NAME = "name";
    String SYS_INFO_OS_VERSION = "version";

    String SYS_QDL_VERSION = "qdl_version.";
    String SYS_QDL_VERSION_VERSION = "version";
    String SYS_QDL_VERSION_BUILD_JDK = "build_jdk";
    String SYS_QDL_VERSION_BUILD_TIME = "build_time";
    String SYS_QDL_VERSION_CREATED_BY = "created_by";
    String SYS_QDL_VERSION_BUILD_NUMBER = "build_nr";

    String SYS_BOOT = "boot.";
    String SYS_BOOT_QDL_HOME = "qdl_home";
    String SYS_BOOT_BOOT_SCRIPT = "boot_script";
    String SYS_BOOT_CONFIG_NAME = "cfg_name";
    String SYS_BOOT_CONFIG_FILE = "cfg_file";
    String SYS_BOOT_LOG_NAME = "log_name";
    String SYS_BOOT_LOG_FILE = "log_file";

    String SYS_BOOT_SERVER_MODE = "server_mode_on";
    String SYS_BOOT_RESTRICTED_IO_MODE = "restricted_io";
    String SYS_SCRIPTS_PATH = "scripts_path";

    String URI_FIELDS = "uri_fields.";
    String URI_AUTHORITY = "authority";
    String URI_FRAGMENT = "fragment";
    String URI_HOST = "host";
    String URI_PATH = "path";
    String URI_QUERY = "query";
    String URI_SCHEME_SPECIFIC_PART = "scheme_specific_part";
    String URI_SCHEME = "scheme";
    String URI_USER_INFO = "user_info";
    String URI_PORT = "port";

    String SYS_LOG_LEVELS = "sys_log.";
    String SYS_LOG_NONE = "off";
    String SYS_LOG_TRACE = "trace";
    String SYS_LOG_INFO = "info";
    String SYS_LOG_WARN = "warn";
    String SYS_LOG_ERROR = "error";
    String SYS_LOG_SEVERE = "severe";

    String STEM_PATH_MARKER = "`";
    String STEM_PATH_MARKER2 = "·";

    String JSON_QDL_NULL = "$$$qdl_null$$$";
}
