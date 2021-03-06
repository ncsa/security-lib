package edu.uiuc.ncsa.qdl.evaluate;

import edu.uiuc.ncsa.qdl.exceptions.QDLException;
import edu.uiuc.ncsa.qdl.exceptions.QDLIOException;
import edu.uiuc.ncsa.qdl.exceptions.QDLRuntimeException;
import edu.uiuc.ncsa.qdl.exceptions.QDLServerModeException;
import edu.uiuc.ncsa.qdl.expressions.Polyad;
import edu.uiuc.ncsa.qdl.state.ImportManager;
import edu.uiuc.ncsa.qdl.state.State;
import edu.uiuc.ncsa.qdl.util.InputFormUtil;
import edu.uiuc.ncsa.qdl.util.QDLFileUtil;
import edu.uiuc.ncsa.qdl.variables.Constant;
import edu.uiuc.ncsa.qdl.variables.QDLNull;
import edu.uiuc.ncsa.qdl.variables.StemVariable;
import edu.uiuc.ncsa.qdl.vfs.*;
import edu.uiuc.ncsa.security.core.configuration.XProperties;
import edu.uiuc.ncsa.security.core.util.DebugUtil;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
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
public class IOEvaluator extends AbstractFunctionEvaluator {

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

    public static final String READ_FILE = "file_read";
    public static final String IO_READ_FILE = IO_FQ + "read";
    public static final int READ_FILE_TYPE = 3 + IO_FUNCTION_BASE_VALUE;

    public static final String WRITE_FILE = "file_write";
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

    public static final String TO_NUMBER = "to_number";
    public static final String FQ_TO_NUMBER = SYS_FQ + TO_NUMBER;
    public static final int TO_NUMBER_TYPE = 20 + STEM_FUNCTION_BASE_VALUE;

    public static final String TO_BOOLEAN = "to_boolean";
    public static final String FQ_TO_BOOLEAN = SYS_FQ + TO_BOOLEAN;
    public static final int TO_BOOLEAN_TYPE = 21 + STEM_FUNCTION_BASE_VALUE;

    public static final String VFS_MOUNT = "vfs_mount";
    public static final String IO_VFS_MOUNT = IO_FQ + "mount";
    public static final int VFS_MOUNT_TYPE = 100 + IO_FUNCTION_BASE_VALUE;


    public static final String VFS_UNMOUNT = "vfs_unmount";
    public static final String IO_VFS_UNMOUNT = IO_FQ + "unmount";
    public static final int VFS_UNMOUNT_TYPE = 101 + IO_FUNCTION_BASE_VALUE;

    public static String[] FUNC_NAMES = new String[]{
            TO_NUMBER,
            TO_STRING,
            TO_BOOLEAN,
            SAY_FUNCTION,
            PRINT_FUNCTION,
            SCAN_FUNCTION,
            READ_FILE,
            WRITE_FILE,
            DIR,
            VFS_MOUNT,
            VFS_UNMOUNT};

    public static String[] FQ_FUNC_NAMES = new String[]{
            FQ_TO_NUMBER,
            FQ_TO_STRING,
            FQ_TO_BOOLEAN,
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
            case TO_NUMBER:
            case FQ_TO_NUMBER:
                return TO_NUMBER_TYPE;
            case TO_STRING:
            case FQ_TO_STRING:
                return TO_STRING_TYPE;
            case TO_BOOLEAN:
            case FQ_TO_BOOLEAN:
                return TO_BOOLEAN_TYPE;
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
            case TO_NUMBER:
            case FQ_TO_NUMBER:
                doToNumber(polyad, state);
                return true;
            case TO_BOOLEAN:
            case FQ_TO_BOOLEAN:
                doToBoolean(polyad, state);
                return true;
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

    // Convert a wide variety of inputs to boolean. This is useful in scripts where the arguments might
    // be string from external sources, e.g.
    private void doToBoolean(Polyad polyad, State state) {
        if (polyad.getArgCount() != 1) {
            throw new IllegalArgumentException("" + TO_BOOLEAN_TYPE + " requires an argument");
        }
        Object arg = polyad.evalArg(0, state);
        AbstractFunctionEvaluator.fPointer pointer = new AbstractFunctionEvaluator.fPointer() {
            @Override
            public AbstractFunctionEvaluator.fpResult process(Object... objects) {
                AbstractFunctionEvaluator.fpResult r = new AbstractFunctionEvaluator.fpResult();
                switch (Constant.getType(objects[0])) {
                    case Constant.BOOLEAN_TYPE:
                        r.result = ((Boolean) objects[0]);
                        r.resultType = Constant.BOOLEAN_TYPE;
                        break;
                    case Constant.STRING_TYPE:
                        String x = (String) objects[0];
                        r.result = x.equals("true");
                        r.resultType = Constant.BOOLEAN_TYPE;
                        break;
                    case Constant.LONG_TYPE:
                        Long y = (Long) objects[0];
                        r.result = y.equals(1L);
                        r.resultType = Constant.BOOLEAN_TYPE;
                        break;
                    case Constant.DECIMAL_TYPE:
                        BigDecimal bd = (BigDecimal) objects[0];

                        r.result = bd.longValue() == 1;
                        r.resultType = Constant.BOOLEAN_TYPE;
                        break;
                    case Constant.NULL_TYPE:
                        throw new IllegalArgumentException("" + TO_BOOLEAN + " cannot convert null.");
                    case Constant.STEM_TYPE:
                        throw new IllegalArgumentException("" + TO_BOOLEAN + " cannot convert a stem.");
                    case Constant.UNKNOWN_TYPE:
                        throw new IllegalArgumentException("" + TO_BOOLEAN + " unknown argument type.");
                }
                return r;
            }
        };
        process1(polyad, pointer, TO_BOOLEAN, state);


    }

    //   s.0 := '123';s.1 := '-3.14159'; s.2 := true; s.3:=365;
    private void doToNumber(Polyad polyad, State state) {
        if (polyad.getArgCount() != 1) {
            throw new IllegalArgumentException("" + TO_NUMBER + " requires an argument");
        }
        Object arg = polyad.evalArg(0, state);
        AbstractFunctionEvaluator.fPointer pointer = new AbstractFunctionEvaluator.fPointer() {
            @Override
            public AbstractFunctionEvaluator.fpResult process(Object... objects) {
                AbstractFunctionEvaluator.fpResult r = new AbstractFunctionEvaluator.fpResult();
                switch (Constant.getType(objects[0])) {
                    case Constant.BOOLEAN_TYPE:
                        r.result = ((Boolean) objects[0]) ? 1L : 0L;
                        r.resultType = Constant.LONG_TYPE;
                        break;
                    case Constant.STRING_TYPE:
                        String x = (String) objects[0];
                        try {
                            r.result = Long.parseLong(x);
                            r.resultType = Constant.LONG_TYPE;
                        } catch (NumberFormatException nfx0) {
                            try {
                                r.result = new BigDecimal(x);
                                r.resultType = Constant.DECIMAL_TYPE;
                            } catch (NumberFormatException nfx2) {
                                // ok, kill it here.
                                throw new IllegalArgumentException(("" + objects[0] + " is not a number."));
                            }
                        }
                        break;
                    case Constant.LONG_TYPE:
                        r.result = objects[0];
                        r.resultType = Constant.LONG_TYPE;
                        break;
                    case Constant.DECIMAL_TYPE:
                        r.result = objects[0];
                        r.resultType = Constant.DECIMAL_TYPE;
                        break;
                    case Constant.NULL_TYPE:
                        throw new IllegalArgumentException("" + TO_NUMBER + " cannot convert null.");
                    case Constant.STEM_TYPE:
                        throw new IllegalArgumentException("" + TO_NUMBER + " cannot convert a stem.");
                    case Constant.UNKNOWN_TYPE:
                        throw new IllegalArgumentException("" + TO_NUMBER + " unknown argument type.");
                }
                return r;
            }
        };
        process1(polyad, pointer, TO_NUMBER, state);
    }

    protected void doScan(Polyad polyad, State state) {
        if (state.isServerMode()) {
            throw new QDLRuntimeException("scan is not allowed in server mode.");
        }
        if(polyad.isEvaluated()){
            // If this has already been run, then do not prompt the user repeatedly.
            // If scan is used as an argument to a function, e.g.
            // to_number(scan('>'))
            // then it would be called twice, once, for itself, and the second time when
            // to_number tries to re-evaluate it to get the most current state.
            // scanned input is a one-time read operation, in other words. 
            return;
        }
        if (polyad.getArgCount() != 0) {
            // This is the prompt.
            state.getIoInterface().print(polyad.evalArg(0, state));
            //System.out.print(polyad.evalArg(0, state));
        }
        //BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        String result = "";
        try {
            result = state.getIoInterface().readline();
            //result = bufferedReader.readLine().trim();
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
        if (polyad.getArgCount() != 0) {
            Object temp = null;
            temp = polyad.evalArg(0, state);
            if (polyad.getArgCount() == 2) {
                // assume pretty print for stems.
                Object flag = polyad.evalArg(1, state);
                if (flag instanceof Boolean) {
                    prettyPrintForStems = (Boolean) flag;
                }
            }
            if (temp == null || temp instanceof QDLNull) {
                if(State.isPrintUnicode()){
                    result = "∅";
                }else {
                    result = "null";
                }

            } else {
                if (temp instanceof StemVariable) {
                    StemVariable s = ((StemVariable) temp);
                    if (prettyPrintForStems) {

                        result = ((StemVariable) temp).toString(1);
                    } else {
                        result = temp.toString();
                    }
                } else {
                    if(temp instanceof BigDecimal){
                        result = InputFormUtil.inputForm((BigDecimal) temp);

                    } else{
                         if(State.isPrintUnicode() && temp instanceof Boolean){
                             result = ((Boolean)temp)?"⊤":"⊥";
                         }else {
                             result = temp.toString();
                         }
                    }
                }
            }
        }

        if (printIt) {
            state.getIoInterface().println(result);
            //System.out.println(result);
        }
        if (polyad.getArgCount() == 0) {
            polyad.setResult(QDLNull.getInstance());
            polyad.setResultType(Constant.NULL_TYPE);
        } else {
            if (printIt) {
                polyad.setResult(polyad.getArguments().get(0).getResult());
                polyad.setResultType(polyad.getArguments().get(0).getResultType());

            } else {
                polyad.setResult(result);
                polyad.setResultType(Constant.STRING_TYPE);
            }
        }
        polyad.setEvaluated(true);
    }

    protected void doRMDir(Polyad polyad, State state) {
        if (0 == polyad.getArgCount()) {
            throw new IllegalArgumentException("" + RMDIR + " requires a file name to read.");
        }
        Object obj = polyad.evalArg(0, state);
        if (obj == null || !isString(obj)) {
            throw new IllegalArgumentException("The " + RMDIR + " command requires a string for its first argument.");
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
                throw new QDLIOException("Error; Could not resolve virtual file system for '" + fileName + "'");
            }
        } else {
            // So its just a file.
            if (state.isServerMode()) {
                throw new QDLServerModeException("File system operations not permitted in server mode.");
            }
            File f = new File(fileName);
            if (!f.isDirectory()) {
                throw new QDLIOException("the requested object '" + f + "' is not a directory on this system.");
            }
            if (f.list() != null && f.list().length != 0) {
                throw new QDLIOException("The directory '" + f + "' is not empty.");
            }
            rc = f.delete();
        }
        polyad.setEvaluated(true);
        polyad.setResult(rc);
        polyad.setResultType(Constant.BOOLEAN_TYPE);
    }

    protected void doRMFile(Polyad polyad, State state) {
        if (0 == polyad.getArgCount()) {
            throw new IllegalArgumentException("" + RM_FILE + " requires a file name to read.");
        }
        Object obj = polyad.evalArg(0, state);
        if (obj == null || !isString(obj)) {
            throw new IllegalArgumentException("The " + RM_FILE + " command requires a string for its first argument.");
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
                throw new QDLIOException("Error; Could not resolve virtual file system for '" + fileName + "'");
            }
        } else {
            // So its just a file.
            if (state.isServerMode()) {
                throw new QDLServerModeException("File system operations not permitted in server mode.");
            }
            File f = new File(fileName);
            if (!f.isFile()) {
                throw new QDLIOException("the requested object '" + f + "' is not a file on this system.");
            }
            rc = f.delete();
        }
        polyad.setEvaluated(true);
        polyad.setResult(rc);
        polyad.setResultType(Constant.BOOLEAN_TYPE);
    }

    protected void doMkDir(Polyad polyad, State state) {
        if (0 == polyad.getArgCount()) {
            throw new IllegalArgumentException("" + MKDIR + " requires a file name to read.");
        }
        Object obj = polyad.evalArg(0, state);
        DebugUtil.trace(this, "in " + MKDIR + ": starting, arg = " + obj);
        if (obj == null || !isString(obj)) {
            throw new IllegalArgumentException("The " + MKDIR + " command requires a string for its first argument.");
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
                } else {
                    DebugUtil.trace(this, "in " + MKDIR + ": NO VFS");

                }

            } catch (Throwable throwable) {
                DebugUtil.trace(this, "in " + MKDIR + ": got exception \"" + throwable.getMessage() + "\"");
                state.getLogger().error("in " + MKDIR + ", got exception", throwable);

                if (throwable instanceof RuntimeException) {
                    throw (RuntimeException) throwable;
                }
                throw new QDLIOException("Error; Could not resolve virtual file system for '" + fileName + "'");
            }
        } else {
            // So its just a file.
            if (state.isServerMode()) {
                state.getLogger().error("in " + MKDIR + ", in server mode, file ops not allowed.");

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
        if (0 == polyad.getArgCount()) {
            throw new IllegalArgumentException("" + DIR + " requires a file name to read.");
        }
        DebugUtil.trace(this, "starting " + DIR + " command");
        Object obj = polyad.evalArg(0, state);
        if (obj == null || !isString(obj)) {
            throw new IllegalArgumentException("The " + DIR + " command requires a string for its first argument.");
        }
        String fileName = obj.toString();
        DebugUtil.trace(this, "in " + DIR + " command: file name =" + fileName);

        int op = -1; // default

        VFSFileProvider vfs = null;
        String[] entries = null;
        boolean hasVF = false;
        if (state.isVFSFile(fileName)) {
            try {
                vfs = state.getVFS(fileName);
                if (vfs != null) {
                    DebugUtil.trace(this, "in " + DIR + " command: got a VFS=" + vfs);

                    entries = vfs.dir(fileName);
                } else {
                    DebugUtil.trace(this, "in " + DIR + " command: NO VFS");

                }

            } catch (Throwable throwable) {
                DebugUtil.trace(this, "Got an exception:" + throwable.getMessage());
                state.getLogger().error("Error accessing VFS!", throwable);
                if (throwable instanceof RuntimeException) {
                    throw (RuntimeException) throwable;
                }
                throw new QDLIOException("Error; Could not resolve virtual file system for '" + fileName + "'");
            }
        } else {
            // So its just a file.
            if (state.isServerMode()) {
                throw new QDLServerModeException("File system operations not permitted in server mode.");
            }
            File f = new File(fileName);
            entries = f.list();
        }
        DebugUtil.trace(this, "in " + DIR + " command: entries =" + entries);

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
        if (polyad.getArgCount() != 1) {
            throw new IllegalArgumentException("" + VFS_MOUNT + " requires one argument");
        }
        Object arg1 = polyad.evalArg(0, state);
        if (!isStem(arg1)) {
            throw new IllegalArgumentException("The argument must be a stem");
        }
        StemVariable cfg = (StemVariable) arg1;
        if (!cfg.containsKey(VFS_ATTR_TYPE)) {
            // create a memory store
            cfg.put(VFS_ATTR_TYPE, VFS_TYPE_MEMORY);
        }
        // Now grab defaults
        String mountPoint = null;
        if (!cfg.containsKey(VFS_MOUNT_POINT_TAG)) {
            throw new IllegalArgumentException("No " + VFS_MOUNT_POINT_TAG + "  specified for VFS");
        } else {
            mountPoint = cfg.getString(VFS_MOUNT_POINT_TAG);
        }
        String scheme = null;
        if (!cfg.containsKey(VFS_SCHEME_TAG)) {
            throw new IllegalArgumentException("No " + VFS_SCHEME_TAG + " specified for VFS");
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
                    throw new IllegalArgumentException("VFS type of " + VFS_TYPE_PASS_THROUGH + " requires a " + VFS_ROOT_DIR_TAG);
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
                    throw new IllegalArgumentException("VFS type of " + VFS_TYPE_ZIP + " requires a " + VFS_ZIP_FILE_PATH);
                }
                vfs = new VFSZipFileProvider(cfg.getString(VFS_ZIP_FILE_PATH),
                        scheme,
                        mountPoint,
                        access.contains("r"),
                        access.contains("w"));
                break;
            default:
                throw new IllegalArgumentException("unknown VFS type '" + cfg.getString(VFS_ATTR_TYPE) + "'");

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

        if (polyad.getArgCount() < 2) {
            throw new IllegalArgumentException("" + WRITE_FILE + " requires a two arguments.");
        }
        Object obj = polyad.evalArg(0, state);
        Object obj2 = polyad.evalArg(1, state);
        if (obj == null || !isString(obj)) {
            throw new IllegalArgumentException("The first argument to '" + WRITE_FILE + "' must be a string that is the file name.");
        }
        String fileName = obj.toString();
        if (obj2 == null) {
            throw new IllegalArgumentException("The second argument to '" + WRITE_FILE + "' must be a string or a stem list.");
        }
        boolean isBase64 = false;
        if (polyad.getArgCount() == 3) {
            Object obj3 = polyad.evalArg(2, state);
            if (!isBoolean(obj3)) {
                throw new IllegalArgumentException("The third argument to '" + WRITE_FILE + "' must be a boolean.");
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
                    // allow for writing empty files. Edge case but happens.
                    if (!contents.containsKey("0") && !contents.isEmpty()) {
                        throw new IllegalArgumentException("The given stem is not a list. It must be a list to use this function.");
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
                throw new QDLException("Could not write file to store." + t.getMessage());
            }
        } else {
            try {
                if (isStem(obj2)) {
                    QDLFileUtil.writeStemToFile(fileName, (StemVariable) obj2);
                    didIt = true;
                }
                if (isString(obj2)) {
                    QDLFileUtil.writeStringToFile(fileName, (String) obj2);
                    didIt = true;
                }
                if (isBase64) {
                    QDLFileUtil.writeFileAsBinary(fileName, (String) obj2);
                    didIt = true;
                }
            } catch (Throwable t) {
                if (t instanceof QDLException) {
                    throw (RuntimeException) t;
                }
                throw new QDLException("could not write file '" + fileName + "':" + t.getMessage());
            }
        }


        if (!didIt) {
            throw new IllegalArgumentException("The argument to " + WRITE_FILE + " is an unecognized type");
        }
        polyad.setResult(Boolean.TRUE);
        polyad.setResultType(Constant.BOOLEAN_TYPE);
        polyad.setEvaluated(true);

    }

    protected void doReadFile(Polyad polyad, State state) {

        if (0 == polyad.getArgCount()) {
            throw new IllegalArgumentException("" + READ_FILE + " requires a file name to read.");
        }
        Object obj = polyad.evalArg(0, state);
        if (obj == null || !isString(obj)) {
            throw new IllegalArgumentException("The " + READ_FILE + " command requires a string for its first argument.");
        }
        String fileName = obj.toString();
        int op = -1; // default
        if (polyad.getArgCount() == 2) {
            Object obj2 = polyad.evalArg(1, state);
            if (!isLong(obj2)) {
                throw new IllegalArgumentException("The " + READ_FILE + " command's second argument must be an integer.");
            }
            op = ((Long) obj2).intValue();
      /*      if (op != 0 && op != 1) {
                throw new IllegalArgumentException("The " + READ_FILE + " command's second argument must have value of 1 or 0.");
            }*/
        }
        VFSEntry vfsEntry = null;
        boolean hasVF = false;
        if (state.isVFSFile(fileName)) {
            vfsEntry = resolveResourceToFile(fileName, state);
            if (vfsEntry == null) {
                throw new QDLException("The resource '" + fileName + "' was not found in the virtual file system");
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
                        polyad.setResult(QDLFileUtil.readFileAsBinary(fileName));
                    }
                    polyad.setResultType(Constant.STRING_TYPE);
                    polyad.setEvaluated(true);
                    return;
                case FILE_OP_TEXT_STEM:
                    if (hasVF) {
                        polyad.setResult(vfsEntry.convertToStem());// if this is binary, the contents are a single base64 encoded string.
                    } else {
                        polyad.setResult(QDLFileUtil.readFileAsStem(fileName));
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
                        polyad.setResult(QDLFileUtil.readFileAsString(fileName));
                    }
                    polyad.setResultType(Constant.STRING_TYPE);
                    polyad.setEvaluated(true);
                    return;

            }
        } catch (Throwable t) {
            if (t instanceof QDLException) {
                throw (RuntimeException) t;
            }
            throw new QDLException("Error reading file '" + fileName + "'" + t.getMessage());
        }

    }
}
