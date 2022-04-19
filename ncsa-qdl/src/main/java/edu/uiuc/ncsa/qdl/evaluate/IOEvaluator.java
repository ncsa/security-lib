package edu.uiuc.ncsa.qdl.evaluate;

import edu.uiuc.ncsa.qdl.exceptions.*;
import edu.uiuc.ncsa.qdl.expressions.Polyad;
import edu.uiuc.ncsa.qdl.parsing.IniParserDriver;
import edu.uiuc.ncsa.qdl.state.State;
import edu.uiuc.ncsa.qdl.util.InputFormUtil;
import edu.uiuc.ncsa.qdl.util.QDLFileUtil;
import edu.uiuc.ncsa.qdl.variables.Constant;
import edu.uiuc.ncsa.qdl.variables.QDLNull;
import edu.uiuc.ncsa.qdl.variables.StemVariable;
import edu.uiuc.ncsa.qdl.vfs.*;
import edu.uiuc.ncsa.security.core.configuration.XProperties;
import edu.uiuc.ncsa.security.core.util.DebugUtil;
import edu.uiuc.ncsa.security.core.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static edu.uiuc.ncsa.qdl.config.QDLConfigurationConstants.*;
import static edu.uiuc.ncsa.qdl.config.QDLConfigurationLoaderUtils.setupMySQLDatabase;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 1/16/20 at  9:18 AM
 */
public class IOEvaluator extends AbstractFunctionEvaluator {

    public static final String IO_NAMESPACE = "io";
    public static final int IO_FUNCTION_BASE_VALUE = 4000;


    public static final String SCAN_FUNCTION = "scan";
    public static final int SCAN_TYPE = 2 + IO_FUNCTION_BASE_VALUE;

    public static final String READ_FILE = "file_read";
    public static final int READ_FILE_TYPE = 3 + IO_FUNCTION_BASE_VALUE;

    public static final String WRITE_FILE = "file_write";
    public static final int WRITE_FILE_TYPE = 4 + IO_FUNCTION_BASE_VALUE;

    public static final String DIR = "dir";
    public static final int DIR_TYPE = 5 + IO_FUNCTION_BASE_VALUE;

    public static final String MKDIR = "mkdir";
    public static final int MKDIR_TYPE = 6 + IO_FUNCTION_BASE_VALUE;

    public static final String RMDIR = "rmdir";
    public static final int RMDIR_TYPE = 7 + IO_FUNCTION_BASE_VALUE;

    public static final String RM_FILE = "rm";
    public static final int RM_FILE_TYPE = 8 + IO_FUNCTION_BASE_VALUE;

    public static final String VFS_MOUNT = "vfs_mount";
    public static final int VFS_MOUNT_TYPE = 100 + IO_FUNCTION_BASE_VALUE;


    public static final String VFS_UNMOUNT = "vfs_unmount";
    public static final int VFS_UNMOUNT_TYPE = 101 + IO_FUNCTION_BASE_VALUE;

    @Override
    public String getNamespace() {
        return IO_NAMESPACE;
    }


    @Override
    public String[] getFunctionNames() {

        if (fNames == null) {
            fNames = new String[]{
                    SCAN_FUNCTION,
                    READ_FILE,
                    WRITE_FILE,
                    DIR,
                    VFS_MOUNT,
                    VFS_UNMOUNT};
        }
        return fNames;
    }


    @Override
    public int getType(String name) {
        switch (name) {

            case SCAN_FUNCTION:
                return SCAN_TYPE;
            case DIR:
                return DIR_TYPE;
            case MKDIR:
                return MKDIR_TYPE;
            case RM_FILE:
                return RM_FILE_TYPE;
            case RMDIR:
                return RMDIR_TYPE;
            case READ_FILE:
                return READ_FILE_TYPE;
            case WRITE_FILE:
                return WRITE_FILE_TYPE;
            case VFS_MOUNT:
                return VFS_MOUNT_TYPE;
            case VFS_UNMOUNT:
                return VFS_UNMOUNT_TYPE;
        }
        return EvaluatorInterface.UNKNOWN_VALUE;
    }


    @Override
    public boolean evaluate(Polyad polyad, State state) {
        try {
            return evaluate2(polyad, state);
        } catch (QDLException q) {
            throw q;
        } catch (Throwable t) {
            QDLStatementExecutionException qq = new QDLStatementExecutionException(t, polyad);
            throw qq;
        }
    }

    public boolean evaluate2(Polyad polyad, State state) {
        switch (polyad.getName()) {

            case SCAN_FUNCTION:
                doScan(polyad, state);
                return true;
            case DIR:
                doDir(polyad, state);
                return true;
            case MKDIR:
                doMkDir(polyad, state);
                return true;
            case RM_FILE:
                doRMFile(polyad, state);
                return true;
            case RMDIR:
                doRMDir(polyad, state);
                return true;
            case READ_FILE:
                doReadFile(polyad, state);
                return true;
            case WRITE_FILE:
                doWriteFile(polyad, state);
                return true;
            case VFS_MOUNT:
                vfsMount(polyad, state);
                return true;
            case VFS_UNMOUNT:
                vfsUnmount(polyad, state);
                return true;
        }
        return false;
    }


    protected void doScan(Polyad polyad, State state) {
        if (polyad.isSizeQuery()) {
            polyad.setResult(new int[]{0, 1});
            polyad.setEvaluated(true);
            return;
        }
        if (state.isServerMode()) {
            throw new QDLRuntimeException("scan is not allowed in server mode.");
        }

        if (1 < polyad.getArgCount()) {
            throw new ExtraArgException(SCAN_FUNCTION + " requires at most 1 argument");
        }

        if (polyad.isEvaluated()) {
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


    protected void doRMDir(Polyad polyad, State state) {
        if (polyad.isSizeQuery()) {
            polyad.setResult(new int[]{1});
            polyad.setEvaluated(true);
            return;
        }
        if (polyad.getArgCount() < 1) {
            throw new MissingArgException(RMDIR + " requires at least 1 argument");
        }

        if (1 < polyad.getArgCount()) {
            throw new ExtraArgException(RMDIR + " requires at most 1 argument");
        }

        if (0 == polyad.getArgCount()) {
            throw new BadArgException(RMDIR + " requires a file name to read.");
        }
        Object obj = polyad.evalArg(0, state);
        checkNull(obj, polyad.getArgAt(0));
        if (!isString(obj)) {
            throw new BadArgException(RMDIR + " requires a string for its first argument.");
        }
        String fileName = obj.toString();

        VFSFileProvider vfs = null;
        Boolean rc = false;
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
        if (polyad.isSizeQuery()) {
            polyad.setResult(new int[]{1});
            polyad.setEvaluated(true);
            return;
        }
        if (polyad.getArgCount() < 1) {
            throw new MissingArgException(RM_FILE + " requires at least 1 argument");
        }

        if (1 < polyad.getArgCount()) {
            throw new ExtraArgException(RM_FILE + " requires at most 1 argument");
        }

        Object obj = polyad.evalArg(0, state);
        checkNull(obj, polyad.getArgAt(0));
        if (!isString(obj)) {
            throw new BadArgException(RM_FILE + " requires a string for its argument.");
        }
        String fileName = obj.toString();

        VFSFileProvider vfs = null;
        Boolean rc = false;
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
        if (polyad.isSizeQuery()) {
            polyad.setResult(new int[]{1});
            polyad.setEvaluated(true);
            return;
        }
        if (polyad.getArgCount() < 1) {
            throw new MissingArgException(MKDIR + " requires at least 1 argument");
        }

        if (1 < polyad.getArgCount()) {
            throw new ExtraArgException(MKDIR + " requires at most 1 argument");
        }

        Object obj = polyad.evalArg(0, state);
        checkNull(obj, polyad.getArgAt(0));
        if (!isString(obj)) {
            throw new BadArgException(MKDIR + " requires a string for its argument.");
        }
        DebugUtil.trace(this, "in " + MKDIR + ": starting, arg = " + obj);
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
        if (polyad.isSizeQuery()) {
            polyad.setResult(new int[]{1});
            polyad.setEvaluated(true);
            return;
        }
        if (polyad.getArgCount() < 1) {
            throw new MissingArgException(DIR + " requires at least 1 argument");
        }

        if (1 < polyad.getArgCount()) {
            throw new ExtraArgException(DIR + " requires at most 1 argument");
        }

        DebugUtil.trace(this, "starting " + DIR + " command");
        Object obj = polyad.evalArg(0, state);
        checkNull(obj, polyad.getArgAt(0));
        if (!isString(obj)) {
            throw new BadArgException(DIR + " requires a string for its first argument.");
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
        if (polyad.isSizeQuery()) {
            polyad.setResult(new int[]{1});
            polyad.setEvaluated(true);
            return;
        }
        if (state.isServerMode()) {
            throw new QDLServerModeException("Unmounting virtual file systems is not permitted in server mode.");
        }

        if (polyad.getArgCount() < 1) {
            throw new MissingArgException(VFS_UNMOUNT + " requires at least 1 argument");
        }

        if (1 < polyad.getArgCount()) {
            throw new ExtraArgException(VFS_UNMOUNT + " requires at most 1 argument");
        }

        Object arg = polyad.evalArg(0, state);

        checkNull(arg, polyad.getArgAt(0), state);
        if (!isString(arg)) {
            throw new BadArgException(VFS_UNMOUNT + " requires a string as its argument");
        }
        String mountPoint = (String) arg;
        if (!state.hasMountPoint(mountPoint)) {
            throw new IllegalArgumentException("the mount point '" + mountPoint + "' is not valid.");
        }
        state.removeVFSProvider(mountPoint);

        polyad.setEvaluated(true);
        polyad.setResult(Boolean.TRUE);
        polyad.setResultType(Constant.BOOLEAN_TYPE);

    }

    protected void vfsMount(Polyad polyad, State state) {
        if (polyad.isSizeQuery()) {
            polyad.setResult(new int[]{1});
            polyad.setEvaluated(true);
            return;
        }
        if (state.isServerMode()) {
            throw new QDLServerModeException("Mounting virtual file systems is not permitted in server mode.");
        }
        if (polyad.getArgCount() < 1) {
            throw new MissingArgException(VFS_MOUNT + " requires at least 1 argument");
        }

        if (1 < polyad.getArgCount()) {
            throw new ExtraArgException(VFS_MOUNT + " requires at most 1 argument");
        }

        Object arg1 = polyad.evalArg(0, state);
        checkNull(arg1, polyad.getArgAt(0));
        if (!isStem(arg1)) {
            throw new BadArgException("The argument must be a stem");
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
        if (polyad.isSizeQuery()) {
            polyad.setResult(new int[]{2, 3});
            polyad.setEvaluated(true);
            return;
        }
        if (state.isServerMode()) {
            throw new QDLServerModeException("File operations are not permitted in server mode");
        }

        if (polyad.getArgCount() < 2) {
            throw new MissingArgException(WRITE_FILE + " requires at least 2 arguments");
        }

        if (3 < polyad.getArgCount()) {
            throw new ExtraArgException(WRITE_FILE + " requires at most 3 arguments");
        }

        Object obj = polyad.evalArg(0, state);
        checkNull(obj, polyad.getArgAt(0));

        Object obj2 = polyad.evalArg(1, state);
        checkNull(obj2, polyad.getArgAt(1));
        if (!isString(obj)) {
            throw new BadArgException("The first argument to '" + WRITE_FILE + "' must be a string that is the file name.");
        }
        String fileName = obj.toString();
        //boolean isBase64 = false;
        int fileType = FILE_OP_TEXT_STRING;
        if (polyad.getArgCount() == 3) {
            Object obj3 = polyad.evalArg(2, state);
            checkNull(obj3, polyad.getArgAt(2));
            if (!isLong(obj3)) {
                throw new BadArgException("The third argument to '" + WRITE_FILE + "' must be an integer.");
            }
            fileType = ((Long) obj3).intValue();
        }
        if (state.isVFSFile(fileName)) {
            try {
                VFSFileProvider vfs = state.getVFS(fileName);
                XProperties xProperties = new XProperties();
                ArrayList<String> lines = new ArrayList<>();

                switch (fileType) {
                    case FILE_OP_TEXT_INI:
                        if (!isStem(obj2)) {
                            throw new IllegalArgumentException(WRITE_FILE + " requires a stem for ini output");
                        }
                        xProperties.put(FileEntry.CONTENT_TYPE, FileEntry.TEXT_TYPE);
                        lines.add(convertToIni((StemVariable) obj2));
                        break;
                    case FILE_OP_BINARY:
                        xProperties.put(FileEntry.CONTENT_TYPE, FileEntry.BINARY_TYPE);
                        lines.add(obj2.toString());  // in VFS stores we store a binary string
                        break;
                    case FILE_OP_TEXT_STRING:
                    case FILE_OP_TEXT_STEM:
                        if (isStem(obj2)) {
                            StemVariable contents = (StemVariable) obj2;

                            xProperties.put(FileEntry.CONTENT_TYPE, FileEntry.TEXT_TYPE);
                            // allow for writing empty files. Edge case but happens.
                            if (!contents.containsKey("0") && !contents.isEmpty()) {
                                throw new BadArgException("The given stem is not a list. It must be a list to use this function.");
                            }
                            for (int i = 0; i < contents.size(); i++) {
                                lines.add(contents.getString(Integer.toString(i)));
                            }
                        }
                        if (isString(obj2)) {
                            xProperties.put(FileEntry.CONTENT_TYPE, FileEntry.TEXT_TYPE);
                            lines.add(obj2.toString());
                        }
                    default:
                        throw new IllegalArgumentException("unknown file type '" + fileType + "'");
                }

                VFSEntry entry = new FileEntry(lines, xProperties);
                vfs.put(fileName, entry);
            } catch (Throwable t) {
                throw new QDLException("Could not write file to store." + t.getMessage());
            }
        } else {
            try {
                switch (fileType) {
                    case FILE_OP_BINARY:
                        QDLFileUtil.writeFileAsBinary(fileName, (String) obj2);
                        break;
                    case FILE_OP_TEXT_INI:
                        if (!isStem(obj2)) {
                            throw new IllegalArgumentException(WRITE_FILE + " requires a stem for ini output");
                        }
                        QDLFileUtil.writeStringToFile(fileName, convertToIni((StemVariable) obj2));
                        break;
                    case FILE_OP_TEXT_STEM:
                    case FILE_OP_TEXT_STRING:
                        if (isStem(obj2)) {
                            QDLFileUtil.writeStemToFile(fileName, (StemVariable) obj2);
                        }
                        if (isString(obj2)) {
                            QDLFileUtil.writeStringToFile(fileName, (String) obj2);
                        }
                        break;
                    default:
                        throw new IllegalArgumentException("unknown file type '" + fileType + "'");
                }
            } catch (Throwable t) {
                if (t instanceof QDLException) {
                    throw (RuntimeException) t;
                }
                throw new QDLException("could not write file '" + fileName + "':" + t.getMessage());
            }
        }

        polyad.setResult(Boolean.TRUE);
        polyad.setResultType(Constant.BOOLEAN_TYPE);
        polyad.setEvaluated(true);

    }

    private String convertToIni(StemVariable obj2) {
        StringBuilder stringBuilder = new StringBuilder();
        for (String key0 : obj2.keySet()) {
            stringBuilder.append("[" + key0 + "]\n");
            Object obj = obj2.get(key0);
            if (!(obj instanceof StemVariable)) {
                throw new IllegalArgumentException("ini files must have stems as entries.");
            }
            StemVariable innerStem = (StemVariable) obj;
            for (String key1 : innerStem.keySet()) {
                Object innerObject = innerStem.get(key1);
                if (!isStem(innerObject)) {
                    stringBuilder.append(key1 + " := " + InputFormUtil.inputForm(innerObject) + "\n");
                } else {
                    StemVariable innerInnerObject = (StemVariable) innerObject;
                    String out = "";
                    boolean isFirst = true;
                    for (String key2 : innerInnerObject.keySet()) {
                        Object object2 = innerInnerObject.get(key2);
                        if (isStem(object2)) {
                            throw new IllegalArgumentException("Ini files do not support nexted stems");
                        }
                        out = out + (isFirst ? "" : ",") + InputFormUtil.inputForm(object2);
                        if (isFirst) isFirst = false;
                    }

                    stringBuilder.append(key1 + " := " + out + "\n");
                }
            }
            stringBuilder.append("\n"); // helps with readability.
        }
        return stringBuilder.toString();
    }

    protected void doReadFile(Polyad polyad, State state) {
        if (polyad.isSizeQuery()) {
            polyad.setResult(new int[]{1, 2});
            polyad.setEvaluated(true);
            return;
        }
        if (polyad.getArgCount() < 1) {
            throw new MissingArgException(READ_FILE + " requires at least 1 argument");
        }

        if (2 < polyad.getArgCount()) {
            throw new ExtraArgException(READ_FILE + " requires at most 2 arguments");
        }


        Object obj = polyad.evalArg(0, state);
        checkNull(obj, polyad.getArgAt(0));

        if (!isString(obj)) {
            throw new BadArgException(READ_FILE + " requires a string for its first argument.");
        }
        String fileName = obj.toString();
        int op = FILE_OP_AUTO; // default
        boolean hasSecondArg = polyad.getArgCount() == 2;
        if (hasSecondArg) {
            Object obj2 = polyad.evalArg(1, state);
            checkNull(obj2, polyad.getArgAt(1));

            if (!isLong(obj2)) {
                throw new BadArgException(READ_FILE + " requires its second argument be an integer.");
            }
            op = ((Long) obj2).intValue();
        }
        VFSEntry vfsEntry = null;
        boolean hasVF = false;
        if (state.isVFSFile(fileName)) {
            vfsEntry = resolveResourceToFile(fileName, op, state);
            if (vfsEntry == null) {
                throw new QDLException("The resource '" + fileName + "' was not found in the virtual file system");
            }
            hasVF = true;
        } else {
            // Only allow for virtual file reads in server mode.
            // If the file does not live in a VFS throw an exception.
            if (state.isServerMode()) {
                throw new QDLServerModeException("File system operations not permitted in server mode.");
            }
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
                case FILE_OP_TEXT_INI:
                    // read it as a stem and start parsing
                    String content;
                    if (hasVF) {
                        content = vfsEntry.getText();
                    } else {
                        content = QDLFileUtil.readFileAsString(fileName);
                    }
                    if (StringUtils.isTrivial(content)) {
                        polyad.setResult(new StemVariable());
                        polyad.setResultType(Constant.STEM_TYPE);
                        polyad.setEvaluated(true);
                        return;
                    }
                    IniParserDriver iniParserDriver = new IniParserDriver();
                    StringReader stringReader = new StringReader(content);
                    StemVariable out = iniParserDriver.parse(stringReader);

                    polyad.setResult(out);
                    polyad.setResultType(Constant.STEM_TYPE);
                    polyad.setEvaluated(true);
                    return;
                default:
                    throw new IllegalArgumentException(" unknown file type '" + op + "'");
                case FILE_OP_TEXT_STRING:
                case FILE_OP_AUTO:
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
