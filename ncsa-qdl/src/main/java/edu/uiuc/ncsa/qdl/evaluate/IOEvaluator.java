package edu.uiuc.ncsa.qdl.evaluate;

import edu.uiuc.ncsa.qdl.exceptions.QDLException;
import edu.uiuc.ncsa.qdl.exceptions.QDLIOException;
import edu.uiuc.ncsa.qdl.exceptions.QDLRuntimeException;
import edu.uiuc.ncsa.qdl.exceptions.QDLServerModeException;
import edu.uiuc.ncsa.qdl.expressions.Polyad;
import edu.uiuc.ncsa.qdl.state.State;
import edu.uiuc.ncsa.qdl.util.FileUtil;
import edu.uiuc.ncsa.qdl.variables.StemVariable;
import edu.uiuc.ncsa.qdl.variables.Constant;
import edu.uiuc.ncsa.qdl.vfs.VFSEntry;
import edu.uiuc.ncsa.qdl.vfs.VFSFileProvider;
import edu.uiuc.ncsa.qdl.vfs.VFSPassThruFileProvider;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.TreeSet;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 1/16/20 at  9:18 AM
 */
public class IOEvaluator extends MathEvaluator {
    public static final int IO_FUNCTION_BASE_VALUE = 4000;
    public static final String SAY_FUNCTION = "say";
    public static final String PRINT_FUNCTION = "print";
    public static final int SAY_TYPE = 1 + IO_FUNCTION_BASE_VALUE;
    public static final String SCAN_FUNCTION = "scan";
    public static final int SCAN_TYPE = 2 + IO_FUNCTION_BASE_VALUE;

    public static final String READ_FILE = "read_file";
    public static final String IO_READ_FILE = "io#read";
    public static final int READ_FILE_TYPE = 3 + IO_FUNCTION_BASE_VALUE;

    public static final String IO_WRITE_FILE = "io#write";
    public static final String WRITE_FILE = "write_file";
    public static final int WRITE_FILE_TYPE = 4 + IO_FUNCTION_BASE_VALUE;

    public static final String IO_DIR = "io#dir";
    public static final String DIR = "dir";
    public static final int DIR_TYPE = 5 + IO_FUNCTION_BASE_VALUE;

    public static final String IO_MKDIR = "io#mkdir";
    public static final String MKDIR = "mkdir";
    public static final int MKDIR_TYPE = 6 + IO_FUNCTION_BASE_VALUE;

    public static final String IO_RMDIR = "io#rmdir";
    public static final String RMDIR = "rmdir";
    public static final int RMDIR_TYPE = 7 + IO_FUNCTION_BASE_VALUE;

    public static final String IO_RM_FILE = "io#rm";
    public static final String RM_FILE = "rm";
    public static final int RM_FILE_TYPE = 8 + IO_FUNCTION_BASE_VALUE;

    public static final String IO_VFS_MOUNT = "io#mount";
    public static final String VFS_MOUNT = "vfs_mount";
    public static final int VFS_MOUNT_TYPE = 100 + IO_FUNCTION_BASE_VALUE;


    public static final String IO_VFS_UNMOUNT = "io#unmount";
    public static final String VFS_UNMOUNT = "vfs_unmount";
    public static final int VFS_UNMOUNT_TYPE = 101 + IO_FUNCTION_BASE_VALUE;

    public static String[] FUNC_NAMES = new String[]{
            SAY_FUNCTION,
            PRINT_FUNCTION,
            SCAN_FUNCTION,
            READ_FILE,
            WRITE_FILE,
            DIR,
            VFS_MOUNT,
            VFS_UNMOUNT};

    @Override
    public String[] getFunctionNames() {
        return FUNC_NAMES;
    }

    public TreeSet<String> listFunctions() {
        TreeSet<String> names = new TreeSet<>();
        for (String key : FUNC_NAMES) {
            names.add(key + "()");
        }
        return names;
    }

    @Override
    public int getType(String name) {
        if (name.equals(SAY_FUNCTION)) return SAY_TYPE;
        if (name.equals(PRINT_FUNCTION)) return SAY_TYPE;
        if (name.equals(SCAN_FUNCTION)) return SCAN_TYPE;
        if (name.equals(DIR)) return DIR_TYPE;
        if (name.equals(RMDIR)) return RMDIR_TYPE;
        if (name.equals(RM_FILE)) return RM_FILE_TYPE;
        if (name.equals(MKDIR)) return MKDIR_TYPE;
        if (name.equals(READ_FILE)) return READ_FILE_TYPE;
        if (name.equals(WRITE_FILE)) return WRITE_FILE_TYPE;
        if (name.equals(VFS_MOUNT)) return VFS_MOUNT_TYPE;
        if (name.equals(VFS_UNMOUNT)) return VFS_UNMOUNT_TYPE;
        return EvaluatorInterface.UNKNOWN_VALUE;
    }


    @Override
    public boolean evaluate(Polyad polyad, State state) {
        switch (polyad.getName()) {
            case PRINT_FUNCTION:
            case SAY_FUNCTION:
                if (state.isServerMode()) {
                    polyad.setResult(null);
                    polyad.setResultType(Constant.NULL_TYPE);
                    polyad.setEvaluated(true);
                    return true;
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
                    if (temp == null) {
                        result = "null";

                    } else {
                        if (prettyPrintForStems) {
                            if (temp instanceof StemVariable) {
                                result = ((StemVariable) temp).toString(1);
                            }
                        } else {
                            result = temp.toString();
                        }
                    }
                }
                System.out.println(result);
                if (polyad.getArgumments().size() == 0) {
                    polyad.setResult(null);
                    polyad.setResultType(Constant.NULL_TYPE);

                } else {
                    polyad.setResult(polyad.getArgumments().get(0).getResult());
                    polyad.setResultType(polyad.getArgumments().get(0).getResultType());

                }
                polyad.setEvaluated(true);
                return true;
            case SCAN_FUNCTION:
                if (state.isServerMode()) {
                    throw new QDLRuntimeException("Error: scan is not allowed in server mode.");
                }

                if (polyad.getArgumments().size() != 0) {
                    // This is the prompt.
                    System.out.print(polyad.evalArg(0, state));
                }
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
                result = "";
                try {
                    result = bufferedReader.readLine().trim();
                } catch (IOException e) {
                    result = "(error)";
                }
                polyad.setResult(result);
                polyad.setResultType(Constant.STRING_TYPE);
                polyad.setEvaluated(true);
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
                     if(!f.isDirectory()){
                         throw new QDLIOException("Error: the requested object \"" + f + "\" is not a directory on this system.");
                     }
                     if(f.list() != null &&  f.list().length != 0){
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
                    if(throwable instanceof RuntimeException){
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
                if(!f.isFile()){
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
                if(throwable instanceof RuntimeException){
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
                if(throwable instanceof RuntimeException){
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
            polyad.setResult(null);
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
        if (3 <= polyad.getArgumments().size() && polyad.getArgumments().size() <= 4) {
            throw new IllegalArgumentException("Error: " + VFS_MOUNT + " requires 3 or 4arguments");
        }
        Object arg1 = polyad.evalArg(0, state);
        Object arg2 = polyad.evalArg(1, state);
        Object arg3 = polyad.evalArg(2, state);
        Object arg4 = "r"; // mount in read only mode
        if (polyad.getArgumments().size() == 4) {
            arg4 = polyad.evalArg(2, state);
        }
        if (isString(arg1) && isString(arg2) && isString(arg3) && isString(arg4)) {
            // TODO - make sure there is not another one of these?
            // TODO - check that mount points don't conflict.
            String permissions = arg4.toString();
            VFSPassThruFileProvider vfs = new VFSPassThruFileProvider(arg1.toString(),
                    arg2.toString(),
                    arg3.toString(),
                    permissions.contains("r"),
                    permissions.contains("w"));
            state.addVFSProvider(vfs);
            polyad.setResult(null);
            polyad.setResult(Constant.NULL_TYPE);
            polyad.setEvaluated(true);
            return;
        }
        throw new IllegalArgumentException("Error: " + VFS_MOUNT + " requires that all arguments be strings.");
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
        try {
            boolean didIt = false;
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
            if (!didIt) {
                throw new IllegalArgumentException("Error: The argument to " + WRITE_FILE + " is an unecognized type");
            }
            polyad.setResult(Boolean.TRUE);
            polyad.setResultType(Constant.BOOLEAN_TYPE);
            polyad.setEvaluated(true);
        } catch (Throwable t) {
            if (t instanceof QDLException) {
                throw (RuntimeException) t;
            }
            throw new QDLException("Error: could not write file \"" + fileName + "\":" + t.getMessage());
        }

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
