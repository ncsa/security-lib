package edu.uiuc.ncsa.qdl.vfs;

import edu.uiuc.ncsa.qdl.config.QDLConfigurationConstants;
import edu.uiuc.ncsa.qdl.evaluate.AbstractEvaluator;
import edu.uiuc.ncsa.qdl.exceptions.QDLIOException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static edu.uiuc.ncsa.qdl.vfs.VFSPaths.PATH_SEPARATOR;

/**
 * This is experimental. Part of the issue is that the file has to be read in one go (can be quite time consuming)
 * and stashed then managed.
 * This means making a virtual file system and managing it potentially. Need to think about how to do
 * this more efficiently. It is not practical to mount zip file sin any other than read only mode for now
 * since the Java classes that manage this require the entire file get re-written for any changes.
 * <p>Created by Jeff Gaynor<br>
 * on 3/5/20 at  7:36 AM
 */
/*
   There are many archive formats available in apache commons, such as tar
   http://commons.apache.org/proper/commons-compress/examples.html

 */
public class VFSZipFileProvider extends AbstractVFSFileProvider {
    @Override
    public String getType() {
        return QDLConfigurationConstants.VFS_TYPE_ZIP;
    }

    public VFSZipFileProvider(String pathToZip, String scheme, String mountPoint, boolean canRead, boolean canWrite) {
        super(scheme, mountPoint, canRead, canWrite);
        this.pathToZip = pathToZip;
        init();
    }



    String pathToZip;
    FileSystem fileSystem;
    HashMap<String, MapZipEntry> map = new HashMap<>();

    protected void init() {
        try {
            if (pathToZip == null || pathToZip.isEmpty()) {
                throw new QDLIOException("Error: no zip file specified");
            }
            ZipFile zipFile = new ZipFile(pathToZip);

            Enumeration<? extends ZipEntry> entries = zipFile.entries();

            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                InputStream stream = zipFile.getInputStream(entry);
                byte[] contents = new byte[(int) entry.getSize()];
                stream.read(contents, 0, contents.length);
                map.put(entry.getName(), new MapZipEntry(entry, contents));
            }
        } catch (Exception e) {
            if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            }
            throw new QDLIOException("Error: Could not load zip file:" + e.getMessage(), e);
        }
    }

    public static class MapZipEntry {
        byte[] content;
        ZipEntry ze;
        FileEntry fe = null;

        public FileEntry getFE() throws Throwable {
            if (fe == null) {
                // Don't do it unless they ask.
                fe = FileEntries.toEntry(ze, content, AbstractEvaluator.FILE_OP_AUTO);
            }
            return fe;
        }

        public MapZipEntry(ZipEntry ze, byte[] content) {
            this.ze = ze;
            this.content = content;
        }
    }

    protected Path getZipPath(String path) {
        // need a path in the jar
        if (fileSystem == null) {
            try {
                fileSystem = FileSystems.newFileSystem((new File(pathToZip)).toURI(), null);
            } catch (IOException e) {
                throw new QDLIOException("Error; Cannot mount zip file");
            }
        }
        return fileSystem.getPath(getRealPath(path));
    }

    @Override
    public VFSEntry get(String path, int type) throws Throwable {
        super.get(path, type);
        String rPath = getRealPath(path);
        if (map.containsKey(rPath)) {
            return map.get(getRealPath(path)).getFE();
        }
        return null;
    }


    @Override
    public boolean contains(String path) throws Throwable {
        super.contains(path);
        return map.containsKey(getRealPath(path));
    }


    @Override
    public String[] dir(String path) throws Throwable {
        super.dir(path);

        String relPath = VFSPaths.relativize(getStoreRoot(), path);
        relPath = relPath + (relPath.endsWith(PATH_SEPARATOR) ? "" : PATH_SEPARATOR);
        relPath = VFSPaths.getUnqPath(relPath);
        boolean isRoot = relPath.equals(PATH_SEPARATOR);
        ArrayList<String> output = new ArrayList<>();
        String[] root = VFSPaths.toPathComponents(relPath);
        for (String key : map.keySet()) {
            if (isChildOf(root, key, isRoot)) {
                if (isRoot) {
                    // no surgery on root nodes.
                    output.add(key);
                } else {
                    output.add(key.substring(relPath.length()));
                }
            }
        }

        String[] fileList = new String[output.size()];
        int i = 0;

        for (String x : output) {
            fileList[i++] = x;
        }

        return fileList;

    }

    /**
     * Zip files are not writeable.
     *
     * @return
     */
    @Override
    public boolean canWrite() {
        return false;
    }
}
