package edu.uiuc.ncsa.qdl.evaluate;

import edu.uiuc.ncsa.qdl.exceptions.QDLException;
import edu.uiuc.ncsa.qdl.exceptions.QDLIOException;
import edu.uiuc.ncsa.qdl.exceptions.QDLRuntimeException;
import edu.uiuc.ncsa.qdl.exceptions.QDLServerModeException;
import edu.uiuc.ncsa.qdl.expressions.Polyad;
import edu.uiuc.ncsa.qdl.state.ImportManager;
import edu.uiuc.ncsa.qdl.state.State;
import edu.uiuc.ncsa.qdl.util.FileUtil;
import edu.uiuc.ncsa.qdl.variables.Constant;
import edu.uiuc.ncsa.qdl.variables.QDLNull;
import edu.uiuc.ncsa.qdl.variables.StemVariable;
import edu.uiuc.ncsa.qdl.vfs.*;
import edu.uiuc.ncsa.security.core.configuration.XProperties;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

import static edu.uiuc.ncsa.qdl.config.QDLConfigurationConstants.*;
import static edu.uiuc.ncsa.qdl.config.QDLConfigurationLoaderUtils.setupMySQLDatabase;
import static edu.uiuc.ncsa.qdl.evaluate.StemEvaluator.STEM_FUNCTION_BASE_VALUE;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 1/16/20 at  9:18 AM
 */
public class IOEvaluator extends MathEvaluator {

    public static final String IO_NAMESPACE = "io";
    public static final String IO_FQ = IO_NAMESPACE + ImportManager.NS_DELIMITER;
    public static final int IO_FUNCTION_BASE_VALUE = 4000;

    public static final String SAY_FUNCTION = "say";
    public static final String IO_SAY_FUNCTION = IO_FQ + SAY_FUNCTION;
    public static final String PRINT_FUNCTION = "print";
    public static final String IO_PRINT_FUNCTION = IO_FQ + PRINT_FUNCTION;
    public static final int SAY_TYPE = 1 + IO_FUNCTION_BASE_VALUE;
    public static final String SCAN_FUNCTION = "scan";
    public static final String IO_SCAN_FUNCTION = IO_FQ + SCAN_FUNCTION;
    public static final int SCAN_TYPE = 2 + IO_FUNCTION_BASE_VALUE;

    public static final String READ_FILE = "read_file";
    public static final String IO_READ_FILE = IO_FQ + "read";
    public static final int READ_FILE_TYPE = 3 + IO_FUNCTION_BASE_VALUE;

    public static final String WRITE_FILE = "write_file";
    public static final String IO_WRITE_FILE = IO_FQ + WRITE_FILE;
    public static final int WRITE_FILE_TYPE = 4 + IO_FUNCTION_BASE_VALUE;

    public static final String DIR = "dir";
    public static final String IO_DIR = IO_FQ + DIR;
    public static final int DIR_TYPE = 5 + IO_FUNCTION_BASE_VALUE;

    public static final String MKDIR = "mkdir";
    public static final String IO_MKDIR = IO_FQ + MKDIR;
    public static final int MKDIR_TYPE = 6 + IO_FUNCTION_BASE_VALUE;

    public static final String RMDIR = "rmdir";
    public static final String IO_RMDIR = IO_FQ + RMDIR;
    public static final int RMDIR_TYPE = 7 + IO_FUNCTION_BASE_VALUE;

    public static final String RM_FILE = "rm";
    public static final String IO_RM_FILE = IO_FQ + RM_FILE;
    public static final int RM_FILE_TYPE = 8 + IO_FUNCTION_BASE_VALUE;

    public static final String TO_STRING = "to_string";
    public static final String FQ_TO_STRING = SYS_FQ + TO_STRING;
    public static final int TO_STRING_TYPE = 12 + STEM_FUNCTION_BASE_VALUE;


    public static final String VFS_MOUNT = "vfs_mount";
    public static final String IO_VFS_MOUNT = IO_FQ + "mount";
    public static final int VFS_MOUNT_TYPE = 100 + IO_FUNCTION_BASE_VALUE;


    public static final String VFS_UNMOUNT = "vfs_unmount";
    public static final String IO_VFS_UNMOUNT = IO_FQ + "unmount";
    public static final int VFS_UNMOUNT_TYPE = 101 + IO_FUNCTION_BASE_VALUE;

    public static String[] FUNC_NAMES = new String[]{
            TO_STRING,
            SAY_FUNCTION,
            PRINT_FUNCTION,
            SCAN_FUNCTION,
            READ_FILE,
            WRITE_FILE,
            DIR,
            VFS_MOUNT,
            VFS_UNMOUNT};

    public static String[] FQ_FUNC_NAMES = new String[]{
            FQ_TO_STRING,
            IO_SAY_FUNCTION,
            IO_PRINT_FUNCTION,
            IO_SCAN_FUNCTION,
            IO_READ_FILE,
            IO_WRITE_FILE,
            IO_DIR,
            IO_VFS_MOUNT,
            IO_VFS_UNMOUNT};

    @Override
    public String[] getFunctionNames() {
        return FUNC_NAMES;
    }

    public TreeSet<String> listFunctions(boolean listFQ) {
        TreeSet<String> names = new TreeSet<>();
        String[] fnames = listFQ ? FQ_FUNC_NAMES : FUNC_NAMES;
        for (String key : fnames) {
            names.add(key + "()");
        }
        return names;
    }

    @Override
    public int getType(String name) {
        switch (name) {
            case TO_STRING:
            case FQ_TO_STRING:
                return TO_STRING_TYPE;

            case PRINT_FUNCTION:
            case IO_PRINT_FUNCTION:
            case IO_SAY_FUNCTION:
            case SAY_FUNCTION:
                return SAY_TYPE;
            case SCAN_FUNCTION:
            case IO_SCAN_FUNCTION:
                return SCAN_TYPE;
            case IO_DIR:
                return DIR_TYPE;
            case IO_MKDIR:
            case MKDIR:
                return MKDIR_TYPE;
            case IO_RM_FILE:
            case RM_FILE:
                return RM_FILE_TYPE;
            case IO_RMDIR:
            case RMDIR:
                return RMDIR_TYPE;
            case IO_READ_FILE:
            case READ_FILE:
                return READ_FILE_TYPE;
            case IO_WRITE_FILE:
            case WRITE_FILE:
                return WRITE_FILE_TYPE;
            case IO_VFS_MOUNT:
            case VFS_MOUNT:
                return VFS_MOUNT_TYPE;
            case IO_VFS_UNMOUNT:
            case VFS_UNMOUNT:
                return VFS_UNMOUNT_TYPE;
        }
        return EvaluatorInterface.UNKNOWN_VALUE;
    }


    @Override
    public boolean evaluate(Polyad polyad, State state) {
        boolean printIt = false;
        switch (polyad.getName()) {

            case PRINT_FUNCTION:
            case IO_PRINT_FUNCTION:
            case IO_SAY_FUNCTION:
            case SAY_FUNCTION:
                printIt = true;
            case TO_STRING:
            case FQ_TO_STRING:

                doPrint(polyad, state, printIt);
                return true;
            case SCAN_FUNCTION:
            case IO_SCAN_FUNCTION:
                doScan(polyad, state);
                return true;
            case IO_DIR:
            case DIR:
                doDir(polyad, state);
                return true;
            case IO_MKDIR:
            case MKDIR:
                doMkDir(polyad, state);
                return true;
            case IO_RM_FILE:
            case RM_FILE:
                doRMFile(polyad, state);
                return true;
            case IO_RMDIR:
            case RMDIR:
                doRMDir(polyad, state);
                return true;
            case IO_READ_FILE:
            case READ_FILE:
                doReadFile(polyad, state);
                return true;
            case IO_WRITE_FILE:
            case WRITE_FILE:
                doWriteFile(polyad, state);
                return true;
            case IO_VFS_MOUNT:
            case VFS_MOUNT:
                vfsMount(polyad, state);
                return true;
            case IO_VFS_UNMOUNT:
            case VFS_UNMOUNT:
                vfsUnmount(polyad, state);
                return true;
        }
        return false;
    }

    protected void doScan(Polyad polyad, State state) {
        if (state.isServerMode()) {
            throw new QDLRuntimeException("Error: scan is not allowed in server mode.");
        }

        if (polyad.getArgumments().size() != 0) {
            // This is the prompt.
            System.out.print(polyad.evalArg(0, state));
        }
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        String result = "";
        try {
            result = bufferedReader.readLine().trim();
        } catch (IOException e) {
            result = "(error)";
        }
        polyad.setResult(result);
        polyad.setResultType(Constant.STRING_TYPE);
        polyad.setEvaluated(true);
    }

    /**
     * Does print, say and to_string commands.
     *
     * @param polyad
     * @param state
     * @param printIt
     */
    protected void doPrint(Polyad polyad, State state, boolean printIt) {
        if (printIt && state.isServerMode()) {
            polyad.setResult(QDLNull.getInstance());
            polyad.setResultType(Constant.NULL_TYPE);
            polyad.setEvaluated(true);
            return;
        }
        String result = "";
        boolean prettyPrintForStems = false;
        if (polyad.getArgumments().size() != 0) {
            Object temp = polyad.evalArg(0, state);
            if (polyad.getArgumments().size() == 2) {
                // assume pretty print for stems.
                Object flag = polyad.evalArg(1, state);
                if (flag instanceof Boolean) {
                    prettyPrintForStems = (Boolean) flag;
                }
            }
            if (temp == null || temp instanceof QDLNull) {
                result = "null";

            } else {
                if (temp instanceof StemVariable) {
                    if (prettyPrintForStems) {
                        result = ((StemVariable) temp).toString(1);
                    } else {
                        result = temp.toString();
                    }
                } else {
                    result = temp.toString();
                }
            }
        }
        if (printIt) {
            System.out.println(result);
        }
        if (polyad.getArgumments().size() == 0) {
            polyad.setResult(QDLNull.getInstance());
            polyad.setResultType(Constant.NULL_TYPE);
        } else {
            if (printIt) {
                polyad.setResult(polyad.getArgumments().get(0).getResult());
                polyad.setResultType(polyad.getArgumments().get(0).getResultType());

            } else {
                polyad.setResult(result);
                polyad.setResultType(Constant.STRING_TYPE);
            }
        }
        polyad.setEvaluated(true);
    }

    protected void doRMDir(Polyad polyad, State state) {
        if (0 == polyad.getArgumments().size()) {
            throw new IllegalArgumentException("Error: " + RMDIR + " requires a file name to read.");
        }
        Object obj = polyad.evalArg(0, state);
        if (obj == null || !isString(obj)) {
            throw new IllegalArgumentException("Error: The " + RMDIR + " command requires a string for its first argument.");
        }
        String fileName = obj.toString();

        VFSFileProvider vfs = null;
        Boolean rc = false;
        boolean hasVF = false;
        if (state.isVFSFile(fileName)) {
            try {
                vfs = state.getVFS(fileName);
                if (vfs != null) {
                    rc = vfs.rmdir(fileName);
                }

            } catch (Throwable throwable) {
                throw new QDLIOException("Error; Could not resolve virtual file system for \"" + fileName + "\"");
            }
        } else {
            // So its just a file.
            if (state.isServerMode()) {
                throw new QDLServerModeException("File system operations not permitted in server mode.");
            }
            File f = new File(fileName);
            if (!f.isDirectory()) {
                throw new QDLIOException("Error: the requested object \"" + f + "\" is not a directory on this system.");
            }
            if (f.list() != null && f.list().length != 0) {
                throw new QDLIOException("Error: The directory \"" + f + "\" is not empty.");
            }
            rc = f.delete();
        }
        polyad.setEvaluated(true);
        polyad.setResult(rc);
        polyad.setResultType(Constant.BOOLEAN_TYPE);
    }

    protected void doRMFile(Polyad polyad, State state) {
        if (0 == polyad.getArgumments().size()) {
            throw new IllegalArgumentException("Error: " + RM_FILE + " requires a file name to read.");
        }
        Object obj = polyad.evalArg(0, state);
        if (obj == null || !isString(obj)) {
            throw new IllegalArgumentException("Error: The " + RM_FILE + " command requires a string for its first argument.");
        }
        String fileName = obj.toString();

        VFSFileProvider vfs = null;
        Boolean rc = false;
        boolean hasVF = false;
        if (state.isVFSFile(fileName)) {
            try {
                vfs = state.getVFS(fileName);
                if (vfs != null) {
                    vfs.rm(fileName);
                    rc = true;
                }

            } catch (Throwable throwable) {
                if (throwable instanceof RuntimeException) {
                    throw (RuntimeException) throwable;
                }
                throw new QDLIOException("Error; Could not resolve virtual file system for \"" + fileName + "\"");
            }
        } else {
            // So its just a file.
            if (state.isServerMode()) {
                throw new QDLServerModeException("File system operations not permitted in server mode.");
            }
            File f = new File(fileName);
            if (!f.isFile()) {
                throw new QDLIOException("Error: the requested object \"" + f + "\" is not a file on this system.");
            }
            rc = f.delete();
        }
        polyad.setEvaluated(true);
        polyad.setResult(rc);
        polyad.setResultType(Constant.BOOLEAN_TYPE);
    }

    protected void doMkDir(Polyad polyad, State state) {
        if (0 == polyad.getArgumments().size()) {
            throw new IllegalArgumentException("Error: " + MKDIR + " requires a file name to read.");
        }
        Object obj = polyad.evalArg(0, state);
        if (obj == null || !isString(obj)) {
            throw new IllegalArgumentException("Error: The " + MKDIR + " command requires a string for its first argument.");
        }
        String fileName = obj.toString();

        VFSFileProvider vfs = null;
        Boolean rc = false;
        boolean hasVF = false;
        if (state.isVFSFile(fileName)) {
            try {
                vfs = state.getVFS(fileName);
                if (vfs != null) {
                    rc = vfs.mkdir(fileName);
                }

            } catch (Throwable throwable) {
                if (throwable instanceof RuntimeException) {
                    throw (RuntimeException) throwable;
                }
                throw new QDLIOException("Error; Could not resolve virtual file system for \"" + fileName + "\"");
            }
        } else {
            // So its just a file.
            if (state.isServerMode()) {
                throw new QDLServerModeException("File system operations not permitted in server mode.");
            }
            File f = new File(fileName);
            rc = f.mkdirs();
        }
        polyad.setEvaluated(true);
        polyad.setResult(rc);
        polyad.setResultType(Constant.BOOLEAN_TYPE);

    }

    /**
     * Quick note. This is a directory command. That means that it will list the things in a directory.
     * This is not like, e.g., the unix ls command. The ls command <i>lists</i> information so<br/><br/>
     * <code>ls filename</code>
     * <br/><br/>
     * returns the file name. This command returns what is in the directory. So <code>dir(filename)</code>
     * returns null since the name is not a directory.
     *
     * @param polyad
     * @param state
     */
    protected void doDir(Polyad polyad, State state) {
        if (0 == polyad.getArgumments().size()) {
            throw new IllegalArgumentException("Error: " + DIR + " requires a file name to read.");
        }
        Object obj = polyad.evalArg(0, state);
        if (obj == null || !isString(obj)) {
            throw new IllegalArgumentException("Error: The " + DIR + " command requires a string for its first argument.");
        }
        String fileName = obj.toString();
        int op = -1; // default

        VFSFileProvider vfs = null;
        String[] entries = null;
        boolean hasVF = false;
        if (state.isVFSFile(fileName)) {
            try {
                vfs = state.getVFS(fileName);
                if (vfs != null) {
                    entries = vfs.dir(fileName);
                }

            } catch (Throwable throwable) {
                if (throwable instanceof RuntimeException) {
                    throw (RuntimeException) throwable;
                }
                throw new QDLIOException("Error; Could not resolve virtual file system for \"" + fileName + "\"");
            }
        } else {
            // So its just a file.
            if (state.isServerMode()) {
                throw new QDLServerModeException("File system operations not permitted in server mode.");
            }
            File f = new File(fileName);
            entries = f.list();
        }
        if (entries == null) {
            // Then this is not a directory the request was made for a file.
            // The result should be null
            polyad.setEvaluated(true);
            polyad.setResult(QDLNull.getInstance());
            polyad.setResultType(Constant.NULL_TYPE);
            return;
        }
        StemVariable dir = new StemVariable();
        for (String x : entries) {
            dir.getStemList().append(x);
        }
        polyad.setEvaluated(true);
        polyad.setResult(dir);
        polyad.setResultType(Constant.STEM_TYPE);

    }

    protected void vfsUnmount(Polyad polyad, State state) {
        if (state.isServerMode()) {
            throw new QDLServerModeException("Unmounting virtual file systems is not permitted in server mode.");
        }
    }

    protected void vfsMount(Polyad polyad, State state) {
        if (state.isServerMode()) {
            throw new QDLServerModeException("Mounting virtual file systems is not permitted in server mode.");
        }
        if (polyad.getArgumments().size() != 1) {
            throw new IllegalArgumentException("Error: " + VFS_MOUNT + " requires one argument");
        }
        Object arg1 = polyad.evalArg(0, state);
        if (!isStem(arg1)) {
            throw new IllegalArgumentException("Error: The argument must be a stem");
        }
        StemVariable cfg = (StemVariable) arg1;
        if (!cfg.containsKey(VFS_ATTR_TYPE)) {
            // create a memory store
            cfg.put(VFS_ATTR_TYPE, VFS_TYPE_MEMORY);
        }
        // Now grab defaults
        String mountPoint = null;
        if (!cfg.containsKey(VFS_MOUNT_POINT_TAG)) {
            throw new IllegalArgumentException("Error: No " + VFS_MOUNT_POINT_TAG + "  specified for VFS");
        } else {
            mountPoint = cfg.getString(VFS_MOUNT_POINT_TAG);
        }
        String scheme = null;
        if (!cfg.containsKey(VFS_SCHEME_TAG)) {
            throw new IllegalArgumentException("Error: No " + VFS_SCHEME_TAG + " specified for VFS");
        } else {
            scheme = cfg.getString(VFS_SCHEME_TAG);
        }
        String access = "r";
        if (!cfg.containsKey(VFS_ATTR_ACCESS)) {
            access = cfg.getString(VFS_ATTR_ACCESS);
        }

        VFSFileProvider vfs = null;
        switch (cfg.getString(VFS_ATTR_TYPE)) {
            case VFS_TYPE_MEMORY:
                vfs = new VFSMemoryFileProvider(scheme, mountPoint, access.contains("r"), access.contains("w"));
                break;
            case VFS_TYPE_PASS_THROUGH:
                if (!cfg.containsKey(VFS_ROOT_DIR_TAG)) {
                    throw new IllegalArgumentException("Error: VFS type of " + VFS_TYPE_PASS_THROUGH + " requires a " + VFS_ROOT_DIR_TAG);
                }
                vfs = new VFSPassThruFileProvider(cfg.getString(VFS_ROOT_DIR_TAG),
                        scheme,
                        mountPoint,
                        access.contains("r"),
                        access.contains("w"));
                break;
            case VFS_TYPE_MYSQL:
                Map<String, String> myCfg = new HashMap<>();
                for (String key : cfg.keySet()) {
                    myCfg.put(key, cfg.getString(key));
                }
                VFSDatabase db = setupMySQLDatabase(null, myCfg);
                vfs = new VFSMySQLProvider(db,
                        scheme, mountPoint, access.contains("r"), access.contains("w"));
                break;

            case VFS_TYPE_ZIP:
                if (!cfg.containsKey(VFS_ZIP_FILE_PATH)) {
                    throw new IllegalArgumentException("Error: VFS type of " + VFS_TYPE_ZIP + " requires a " + VFS_ZIP_FILE_PATH);
                }
                vfs = new VFSZipFileProvider(cfg.getString(VFS_ZIP_FILE_PATH),
                        scheme,
                        mountPoint,
                        access.contains("r"),
                        access.contains("w"));
                break;
            default:
                throw new IllegalArgumentException("Error: unknown VFS type \"" + cfg.getString(VFS_ATTR_TYPE) + "\"");

        }
        state.addVFSProvider(vfs);
        polyad.setResult(QDLNull.getInstance());
        polyad.setResult(Constant.NULL_TYPE);
        polyad.setEvaluated(true);
        return;
    }

    protected void doWriteFile(Polyad polyad, State state) {
        if (state.isServerMode()) {
            throw new QDLServerModeException("File operations are not permitted in server mode");
        }

        if (polyad.getArgumments().size() < 2) {
            throw new IllegalArgumentException("Error: " + WRITE_FILE + " requires a two arguments.");
        }
        Object obj = polyad.evalArg(0, state);
        Object obj2 = polyad.evalArg(1, state);
        if (obj == null || !isString(obj)) {
            throw new IllegalArgumentException("Error: The first argument to \"" + WRITE_FILE + "\" must be a string that is the file name.");
        }
        String fileName = obj.toString();
        if (obj2 == null) {
            throw new IllegalArgumentException("Error: The second argument to \"" + WRITE_FILE + "\" must be a string or a stem list.");
        }
        boolean isBase64 = false;
        if (polyad.getArgumments().size() == 3) {
            Object obj3 = polyad.evalArg(2, state);
            if (!isBoolean(obj3)) {
                throw new IllegalArgumentException("Error: The third argument to \"" + WRITE_FILE + "\" must be a boolean.");
            }
            isBase64 = (Boolean) obj3;
        }
        boolean didIt = false;
        if (state.isVFSFile(fileName)) {
            try {
                VFSFileProvider vfs = state.getVFS(fileName);
                XProperties xProperties = new XProperties();
                ArrayList<String> lines = new ArrayList<>();
                if (isStem(obj2)) {
                    StemVariable contents = (StemVariable) obj2;

                    xProperties.put(FileEntry.CONTENT_TYPE, FileEntry.TEXT_TYPE);
                    if (!contents.containsKey("0")) {
                        throw new IllegalArgumentException("Error: The given stem is not a list. It must be a list to use this function.");
                    }
                    for (int i = 0; i < contents.size(); i++) {
                        lines.add(contents.getString(Integer.toString(i)));
                    }
                    didIt = true;

                }
                if (isString(obj2)) {
                    xProperties.put(FileEntry.CONTENT_TYPE, FileEntry.TEXT_TYPE);
                    lines.add(obj2.toString());
                    didIt = true;

                }

                if (isBase64) {
                    xProperties.put(FileEntry.CONTENT_TYPE, FileEntry.BINARY_TYPE);
                    lines.add(obj2.toString());  // in VFS stores we store a binary string
                    didIt = true;

                }

                VFSEntry entry = new FileEntry(lines, xProperties);
                vfs.put(fileName, entry);
            } catch (Throwable t) {
                throw new QDLException("Error: Could not write file to store." + t.getMessage());
            }
        }else{
            try {
                if (isStem(obj2)) {
                    FileUtil.writeStemToFile(fileName, (StemVariable) obj2);
                    didIt = true;
                }
                if (isString(obj2)) {
                    FileUtil.writeStringToFile(fileName, (String) obj2);
                    didIt = true;
                }
                if (isBase64) {
                    FileUtil.writeFileAsBinary(fileName, (String) obj2);
                    didIt = true;
                }
            } catch (Throwable t) {
                if (t instanceof QDLException) {
                    throw (RuntimeException) t;
                }
                throw new QDLException("Error: could not write file \"" + fileName + "\":" + t.getMessage());
            }
        }


        if (!didIt) {
            throw new IllegalArgumentException("Error: The argument to " + WRITE_FILE + " is an unecognized type");
        }
        polyad.setResult(Boolean.TRUE);
        polyad.setResultType(Constant.BOOLEAN_TYPE);
        polyad.setEvaluated(true);

    }

    protected void doReadFile(Polyad polyad, State state) {

        if (0 == polyad.getArgumments().size()) {
            throw new IllegalArgumentException("Error: " + READ_FILE + " requires a file name to read.");
        }
        Object obj = polyad.evalArg(0, state);
        if (obj == null || !isString(obj)) {
            throw new IllegalArgumentException("Error: The " + READ_FILE + " command requires a string for its first argument.");
        }
        String fileName = obj.toString();
        int op = -1; // default
        if (polyad.getArgumments().size() == 2) {
            Object obj2 = polyad.evalArg(1, state);
            if (!isLong(obj2)) {
                throw new IllegalArgumentException("Error: The " + READ_FILE + " command's second argument must be an integer.");
            }
            op = ((Long) obj2).intValue();
            if (op != 0 && op != 1) {
                throw new IllegalArgumentException("Error: The " + READ_FILE + " command's second argument must have value of 1 or 0.");
            }
        }
        VFSEntry vfsEntry = null;
        boolean hasVF = false;
        if (state.isVFSFile(fileName)) {
            vfsEntry = resolveResourceToFile(fileName, state);
            if (vfsEntry == null) {
                throw new QDLException("Error: The resource \"" + fileName + "\" was not found in the virtual file system");
            }
            hasVF = true;
        }
        try {
            switch (op) {
                case FILE_OP_BINARY:
                    // binary file. Read it and base64 encode it
                    if (hasVF) {
                        polyad.setResult(vfsEntry.getText());// if this is binary, the contents are a single base64 encoded string.
                    } else {
                        polyad.setResult(FileUtil.readFileAsBinary(fileName));
                    }
                    polyad.setResultType(Constant.STRING_TYPE);
                    polyad.setEvaluated(true);
                    return;
                case FILE_OP_TEXT_STEM:
                    if (hasVF) {
                        polyad.setResult(vfsEntry.convertToStem());// if this is binary, the contents are a single base64 encoded string.
                    } else {
                        polyad.setResult(FileUtil.readFileAsStem(fileName));
                    }
                    // Read as lines, put in a stem
                    polyad.setResultType(Constant.STEM_TYPE);
                    polyad.setEvaluated(true);
                    return;
                default:
                case FILE_OP_TEXT_STRING:
                    // read it as a long string.
                    if (hasVF) {
                        polyad.setResult(vfsEntry.getText());
                    } else {
                        polyad.setResult(FileUtil.readFileAsString(fileName));
                    }
                    polyad.setResultType(Constant.STRING_TYPE);
                    polyad.setEvaluated(true);
                    return;

            }
        } catch (Throwable t) {
            if (t instanceof QDLException) {
                throw (RuntimeException) t;
            }
            throw new QDLException("Error reading file \"" + fileName + "\"" + t.getMessage());
        }

    }
}
