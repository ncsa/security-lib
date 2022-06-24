package edu.uiuc.ncsa.qdl;

import edu.uiuc.ncsa.qdl.evaluate.IOEvaluator;
import edu.uiuc.ncsa.qdl.evaluate.SystemEvaluator;
import edu.uiuc.ncsa.qdl.exceptions.QDLIOException;
import edu.uiuc.ncsa.qdl.exceptions.QDLServerModeException;
import edu.uiuc.ncsa.qdl.parsing.QDLInterpreter;
import edu.uiuc.ncsa.qdl.state.State;
import edu.uiuc.ncsa.qdl.vfs.*;
import edu.uiuc.ncsa.security.core.configuration.XProperties;
import edu.uiuc.ncsa.security.storage.sql.ConnectionPool;
import edu.uiuc.ncsa.security.storage.sql.derby.DerbyConnectionParameters;
import edu.uiuc.ncsa.security.storage.sql.mysql.MySQLConnectionParameters;
import edu.uiuc.ncsa.security.storage.sql.mysql.MySQLConnectionPool;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static edu.uiuc.ncsa.qdl.evaluate.AbstractEvaluator.FILE_OP_AUTO;
import static edu.uiuc.ncsa.qdl.evaluate.AbstractEvaluator.FILE_OP_INPUT_STREAM;
import static edu.uiuc.ncsa.qdl.vfs.VFSPaths.PATH_SEPARATOR;
import static edu.uiuc.ncsa.qdl.vfs.VFSPaths.SCHEME_DELIMITER;

/** Large and complex text of the VFS (Virtual File System). This repeats the
 * same tests on several different store types, including databases, zip files
 * and the underlying file system.
 * <p>Created by Jeff Gaynor<br>
 * on 3/4/20 at  8:57 AM
 */
public class VFSTest extends AbstractQDLTester {
    public void testPaths() {
        String absFQPath1 = "A#/a/b/c";
        String absUNQPath1 = "/a/b/c";
        String relFQPath1 = "A#b/c";
        String relUNQPath1 = "b/c";
        String absFQPath2 = "B#/p/q/r";
        String relFQPath2 = "B#r";
        String absUNQPath2 = "/p/q/r";
        String relUNQPath2 = "r/s";
        assert VFSPaths.hasSameScheme(absFQPath1, relFQPath1);
        assert !VFSPaths.hasSameScheme(absFQPath1, absFQPath2);
        assert VFSPaths.getScheme(absFQPath1).equals("A");
        assert !VFSPaths.getScheme(absUNQPath1).equals("A");
        assert VFSPaths.getScheme(relFQPath1).equals("A");
        assert VFSPaths.isUnq(absUNQPath1);
        assert !VFSPaths.isUnq(absFQPath1);
        assert VFSPaths.hasSameScheme(absFQPath1, relFQPath1);
        assert !VFSPaths.hasSameScheme(absFQPath1, relFQPath2);
        assert VFSPaths.endsWith(absFQPath2, relFQPath2);
        assert !VFSPaths.endsWith(absFQPath2, relUNQPath1); // paths are disjoint, one does not have a scheme
        assert VFSPaths.endsWith(absFQPath2, relFQPath2);
        assert VFSPaths.isAbsolute(absUNQPath2);
        assert !VFSPaths.isAbsolute(relFQPath2);
        assert !VFSPaths.isAbsolute(relUNQPath2);

        assert VFSPaths.getParentPath(absFQPath1).equals("A#/a/b");
        assert VFSPaths.getParentPath(relUNQPath1).equals("b");

        assert VFSPaths.getUnqPath(absUNQPath1).equals(absUNQPath1);
        String paths2[] = VFSPaths.toPathComponents(absFQPath2);
        assert paths2.length == 3;
        assert paths2[0].equals("p");
        assert paths2[1].equals("q");
        assert paths2[2].equals("r");

        assert VFSPaths.getComponentAt(absFQPath2, 0).equals("p");
        assert VFSPaths.getComponentAt(absFQPath2, 1).equals("q");
        assert VFSPaths.getComponentAt(absFQPath2, 2).equals("r");
        // test normalizing absolute paths
        assert VFSPaths.normalize("A#/a/b/../c/d").equals("A#/a/c/d") : "Path with .. did not normalize";
        assert VFSPaths.normalize("A#/a/b/../c/./././d").equals("A#/a/c/d") : "path with . did not normalize ";
        assert VFSPaths.normalize("A#/a/b///c/././/./d").equals("A#/a/b/c/d") : "Paths with extra // did not normalize.";
        // now for relative paths
        assert VFSPaths.normalize("A#a/b/../c/d").equals("A#a/c/d") : "Path with .. did not normalize";
        assert VFSPaths.normalize("A#a/b/../c/./././d").equals("A#a/c/d") : "path with . did not normalize ";
        assert VFSPaths.normalize("A#a/b///c/././/./d").equals("A#a/b/c/d") : "Paths with extra // did not normalize.";

        assert VFSPaths.resolve(absFQPath1, relUNQPath2).equals("A#/a/b/c/r/s");
        assert VFSPaths.resolveSibling(absFQPath1, relUNQPath2).equals("A#/a/b/r/s");
        assert VFSPaths.resolveSibling(absFQPath1, relFQPath2).equals("A#/a/b/r");

        assert VFSPaths.relativize(absFQPath1, VFSPaths.resolve(absFQPath1, "A#z")).equals("A#z") : "relativize(resolve) fail to be inverese of each other";
        assert VFSPaths.compareTo(absFQPath1, absFQPath1) == 0;
        assert VFSPaths.compareTo(absFQPath1, absFQPath2) != 0;
    }

    protected void runVFSTests(VFSFileProvider vfs) throws Throwable {
        testMkdir(vfs);
        testMultipleSchemes(vfs);
        testMultipleMount(vfs);
        testMultipleSchemesAndMountPoint(vfs);
        testStayInStore(vfs);
        testWriteable(vfs);
        testReadable(vfs);
    }

    protected void testMkdir(VFSFileProvider vfs) throws Throwable {
        String testHeadPath = vfs.getScheme() + SCHEME_DELIMITER + vfs.getMountPoint();
        if (!vfs.canWrite()) {
            // If it is not writeable ALL of these should get intercepted before any othe processing
            // takes place.
            boolean bad = true;
            try {
                vfs.mkdir(testHeadPath + "a/b/c/d/e/f");
            } catch (QDLIOException q) {
                bad = false;
            }
            if (bad) {
                assert false : "Error: Could create directory in not writeable VFS";
            }
            bad = true;

            try {
                vfs.rmdir(testHeadPath + "a/");
            } catch (QDLIOException q) {
                bad = false;
            }
            if (bad) {
                assert false : "Error: Could remove directory in not writeable VFS";
            }
            bad = true;

            try {
                vfs.rm(testHeadPath + "a/foo");
                assert false : "Error: Could remove file in not writeable VFS";
            } catch (QDLIOException q) {
                bad = false;
            }
            if (bad) {
                assert false : "Was able to make an assignment with = not :=";
            }


            return;
        }
        vfs.mkdir(testHeadPath + "VFS_TEST/a/b/c/d");
        assert vfs.dir(testHeadPath + "VFS_TEST/a/b/c/d").length == 0;
        vfs.rmdir(testHeadPath + "VFS_TEST/a/b/c/d");
        assert vfs.dir(testHeadPath + "VFS_TEST/a/b/c").length == 0;

        vfs.rmdir(testHeadPath + "VFS_TEST/a/b/c");
        assert vfs.dir(testHeadPath + "VFS_TEST/a/b").length == 0;
        vfs.rmdir(testHeadPath + "VFS_TEST/a/b");
        assert vfs.dir(testHeadPath + "VFS_TEST/a").length == 0;
        vfs.rmdir(testHeadPath + "VFS_TEST/a");
        assert vfs.dir(testHeadPath + "VFS_TEST").length == 0;
        vfs.rmdir(testHeadPath + "VFS_TEST");
    }

    protected void testReadable(VFSFileProvider vfs) throws Throwable {
        boolean orig = vfs.canRead();
        vfs.setRead(false);
        String testHeadPath = vfs.getScheme() + SCHEME_DELIMITER + vfs.getMountPoint();
        String testFileName = "foo.txt";
        String p = testHeadPath + testFileName;
        boolean bad = true;
        try {
            vfs.get(p, FILE_OP_AUTO);
        } catch (QDLIOException q) {
            bad = false;
        }
        if (bad) {
            assert false : "Was able to read from a non-readable store";
        }
        bad = true;
        try {
            vfs.contains(p);
        } catch (QDLIOException q) {
            bad = false;
        }
        if (bad) {
            assert false : "Was able to read from a non-readable store";
        }
        bad = true;
        try {
            vfs.dir(p);
        } catch (QDLIOException q) {
            bad = false;
        }
        if (bad) {
            assert false : "Was able to read from a non-readable store";
        }


        vfs.setRead(orig);
    }

    /**
     * Toggle the store to not being writable and make sure all the right stuff fails
     *
     * @param vfs
     * @throws Throwable
     */
    protected void testWriteable(VFSFileProvider vfs) throws Throwable {
        boolean orig = vfs.canWrite();
        vfs.setWrite(false);
        VFSEntry fileEntry = makeFE();
        String testHeadPath = vfs.getScheme() + SCHEME_DELIMITER + vfs.getMountPoint();
        String testFileName = "foo.txt";
        String p = testHeadPath + testFileName;
        fileEntry.setPath(p);
        boolean bad = true;
        try {
            vfs.put(fileEntry);
        } catch (QDLIOException q) {
            bad = false;
        }
        if (bad) {
            assert false : " Was able to write to a not writeable vfs";
        }
        bad = true;
        try {
            vfs.put(p, fileEntry);
        } catch (QDLIOException q) {
            bad = false;
        }
        if (bad) {
            assert false : " Was able to write to a not writeable vfs";
        }
        bad = true;
        try {
            vfs.delete(p);
        } catch (QDLIOException q) {
            bad = false;
        }
        if (bad) {
            assert false : "Was able to delete from a not writeable vfs";
        }

        vfs.setWrite(orig);

    }

    /**
     * Show that attempts to get out of the mount point with a path like <code>../../..</code>
     * are intercepted by the store and fail.
     *
     * @param vfs
     * @throws Throwable
     */
    protected void testStayInStore(VFSFileProvider vfs) throws Throwable {
        String testHeadPath = vfs.getScheme() + SCHEME_DELIMITER + vfs.getMountPoint();

        String testFileName = "/";
        String p = testHeadPath + testFileName;
        String[] originalDir = vfs.dir(p);
        // now we try to reget them using the parent paths.
        p = testHeadPath + "../../../../../../..";
        String[] otherDir = vfs.dir(p);
        assert otherDir.length == originalDir.length;
        List<String> originalList = Arrays.asList(originalDir);
        List<String> otherList = Arrays.asList(otherDir);
        for (String x : originalList) {
            assert otherList.contains(x) : "Found element \"" + x + "\" not contained in the directory listing";
        }
    }

    /**
     * Very similar to the multiple mount test, this does the same for changing the scheme.
     *
     * @param vfs
     * @throws Throwable
     */
    protected void testMultipleSchemes(VFSFileProvider vfs) throws Throwable {
        VFSEntry fileEntry = makeFE();
        String testHeadPath = vfs.getScheme() + SCHEME_DELIMITER + vfs.getMountPoint();
        String testFileName = "foo.txt";
        String p = testHeadPath + testFileName;
        vfs.put(p, fileEntry);

        assert vfs.contains(p);
        assert !vfs.contains(p + "1"); // show that not every file is in store.
        VFSEntry entry = vfs.get(p, FILE_OP_AUTO);
        assert entry.getLines().get(0).equals(fileEntry.getLines().get(0));
        assert entry.getLines().get(1).equals(fileEntry.getLines().get(1));

        // And now the reader
        VFSEntry entry1 = vfs.get(p, FILE_OP_INPUT_STREAM);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(entry1.getInputStream()));
        assert bufferedReader.readLine().equals(fileEntry.getLines().get(0));
        assert bufferedReader.readLine().equals(fileEntry.getLines().get(1));
        bufferedReader.close();

        vfs.setScheme("w00fity");
        testHeadPath = vfs.getScheme() + SCHEME_DELIMITER + vfs.getMountPoint();
        assert testHeadPath.startsWith("w00fity" + SCHEME_DELIMITER);
        p = testHeadPath + testFileName;
        // rerun the tests above because they have to work no matter how the vfs is configured
        assert vfs.contains(p) : "Could not get file at \"" + p + "\"";
        assert !vfs.contains(p + "1"); // show that not every file is in store.
        entry = vfs.get(p, FILE_OP_AUTO);
        assert entry.getLines().get(0).equals(fileEntry.getLines().get(0));
        assert entry.getLines().get(1).equals(fileEntry.getLines().get(1));

        // And now the reader
         entry1 = vfs.get(p, FILE_OP_INPUT_STREAM);
        bufferedReader = new BufferedReader(new InputStreamReader(entry1.getInputStream()));
        assert bufferedReader.readLine().equals(fileEntry.getLines().get(0));
        assert bufferedReader.readLine().equals(fileEntry.getLines().get(1));
        bufferedReader.close();

        // clean up
        vfs.delete(p);
        assert !vfs.contains(p) : "Could not delete file from store";
    }

    /**
     * Change both the scheme and mount point at the same time. This is a test just in case.
     *
     * @param vfs
     * @throws Throwable
     */
    protected void testMultipleSchemesAndMountPoint(VFSFileProvider vfs) throws Throwable {
        VFSEntry fileEntry = makeFE();
        String testHeadPath = vfs.getScheme() + SCHEME_DELIMITER + vfs.getMountPoint();
        String testFileName = "foo.txt";
        String p = testHeadPath + testFileName;
        vfs.put(p, fileEntry);

        assert vfs.contains(p);
        assert !vfs.contains(p + "1"); // show that not every file is in store.
        VFSEntry entry = vfs.get(p, FILE_OP_AUTO);
        assert entry.getLines().get(0).equals(fileEntry.getLines().get(0));
        assert entry.getLines().get(1).equals(fileEntry.getLines().get(1));
        // And now the reader
        VFSEntry entry1 = vfs.get(p, FILE_OP_INPUT_STREAM);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(entry1.getInputStream()));
        assert bufferedReader.readLine().equals(fileEntry.getLines().get(0));
        assert bufferedReader.readLine().equals(fileEntry.getLines().get(1));
        bufferedReader.close();

        vfs.setScheme("fnord");
        vfs.setMountPoint("blarg");
        testHeadPath = vfs.getScheme() + SCHEME_DELIMITER + vfs.getMountPoint();
        assert testHeadPath.startsWith("fnord" + SCHEME_DELIMITER);
        assert testHeadPath.endsWith(PATH_SEPARATOR + "blarg" + PATH_SEPARATOR);
        p = testHeadPath + testFileName;
        // rerun the tests above because they have to work no matter how the vfs is configured
        assert vfs.contains(p) : "Could not get file at \"" + p + "\"";
        assert !vfs.contains(p + "1"); // show that not every file is in store.
        entry = vfs.get(p, FILE_OP_AUTO);
        assert entry.getLines().get(0).equals(fileEntry.getLines().get(0));
        assert entry.getLines().get(1).equals(fileEntry.getLines().get(1));

        // And now the reader
         entry1 = vfs.get(p, FILE_OP_INPUT_STREAM);
        bufferedReader = new BufferedReader(new InputStreamReader(entry1.getInputStream()));
        assert bufferedReader.readLine().equals(fileEntry.getLines().get(0));
        assert bufferedReader.readLine().equals(fileEntry.getLines().get(1));
        bufferedReader.close();

        // clean up
        vfs.delete(p);
        assert !vfs.contains(p) : "Could not delete file from store";

    }


    /**
     * Get a VFS and put stuff in it. Change the mount point. The files should still be there
     * just the same. Change it again, just in case. This means that storage is indeed persistent
     * across mounts
     *
     * @param vfs
     * @throws Throwable
     */
    protected void testMultipleMount(VFSFileProvider vfs) throws Throwable {
        VFSEntry fileEntry = makeFE();
        String testHeadPath = vfs.getScheme() + SCHEME_DELIMITER + vfs.getMountPoint();
        String testFileName = "foo.txt";
        String p = testHeadPath + testFileName;
        vfs.put(p, fileEntry);

        assert vfs.contains(p);
        assert !vfs.contains(p + "1"); // show that not every file is in store.
        VFSEntry entry = vfs.get(p, FILE_OP_AUTO);
        assert entry.getLines().get(0).equals(fileEntry.getLines().get(0));
        assert entry.getLines().get(1).equals(fileEntry.getLines().get(1));
        // And now the reader
        VFSEntry entry1 = vfs.get(p, FILE_OP_INPUT_STREAM);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(entry1.getInputStream()));
        assert bufferedReader.readLine().equals(fileEntry.getLines().get(0));
        assert bufferedReader.readLine().equals(fileEntry.getLines().get(1));
        bufferedReader.close();

        vfs.setMountPoint(PATH_SEPARATOR + "w00f");
        testHeadPath = vfs.getScheme() + SCHEME_DELIMITER + vfs.getMountPoint();
        assert testHeadPath.endsWith(PATH_SEPARATOR + "w00f" + PATH_SEPARATOR);
        p = testHeadPath + testFileName;
        // rerun the tests above because they have to work no matter how the vfs is configured
        assert vfs.contains(p) : "Could not get file at \"" + p + "\"";
        assert !vfs.contains(p + "1"); // show that not every file is in store.
        entry = vfs.get(p, FILE_OP_AUTO);
        assert entry.getLines().get(0).equals(fileEntry.getLines().get(0));
        assert entry.getLines().get(1).equals(fileEntry.getLines().get(1));
        // And now the reader
        entry1 = vfs.get(p, FILE_OP_INPUT_STREAM);
        bufferedReader = new BufferedReader(new InputStreamReader(entry1.getInputStream()));
        assert bufferedReader.readLine().equals(fileEntry.getLines().get(0));
        assert bufferedReader.readLine().equals(fileEntry.getLines().get(1));
        bufferedReader.close();
        // let's beat a dead horse and do it again
        vfs.setMountPoint("/onomatopoeia");
        testHeadPath = vfs.getScheme() + SCHEME_DELIMITER + vfs.getMountPoint();
        assert testHeadPath.endsWith("/onomatopoeia/");
        p = testHeadPath + testFileName;
        // rerun the tests above because they have to work no matter how the vfs is configured
        assert vfs.contains(p);
        assert !vfs.contains(p + "1"); // show that not every file is in store.
        entry = vfs.get(p, FILE_OP_AUTO);
        assert entry.getLines().get(0).equals(fileEntry.getLines().get(0));
        assert entry.getLines().get(1).equals(fileEntry.getLines().get(1));
        // And now the reader
        entry1 = vfs.get(p, FILE_OP_INPUT_STREAM);
        bufferedReader = new BufferedReader(new InputStreamReader(entry1.getInputStream()));
        assert bufferedReader.readLine().equals(fileEntry.getLines().get(0));
        assert bufferedReader.readLine().equals(fileEntry.getLines().get(1));
        bufferedReader.close();
        // clean up
        vfs.delete(p);
        assert !vfs.contains(p) : "Could not delete file from store";
    }


    public void testFilePassThrough() throws Throwable {
        String rootDir = "/home/ncsa/dev/ncsa-git/security-lib/ncsa-qdl/src/test/resources";
        VFSPassThruFileProvider vfs = new VFSPassThruFileProvider(
                rootDir,
                "qdl-vfs",
                "/",
                true,
                true);

        runVFSTests(vfs);
    }

    protected FileEntry makeFE() {
        List<String> lines = new ArrayList<>();
        lines.add(getRandomString());
        lines.add(getRandomString());
        XProperties xProperties = new XProperties();
        xProperties.put(FileEntry.CONTENT_TYPE, FileEntry.TEXT_TYPE);
        return new FileEntry(lines, xProperties);

    }


    public void testMemoryStoreVFS() throws Throwable {
        VFSMemoryFileProvider vfs = new VFSMemoryFileProvider(
                "qdl-vfs", "/", true, true
        );
        runVFSTests(vfs);

    }

    public void testDerbyVFS() throws Throwable {
        DerbyConnectionParameters params = null;
        if (hasConfig()) {
            params = new DerbyConnectionParameters(
                    getXp().getString(DERBY + USERNAME).trim(),
                    getXp().getString(DERBY + PASSWORD).trim(),
                    getXp().getString(DERBY + DATABASE).trim(),
                    getXp().getString(DERBY + SCHEMA).trim(),
                    "localhost",
                    1527,
                    "org.apache.derby.jdbc.EmbeddedDriver",
                    false,
                    false,
                    getXp().getString(DERBY + BOOT_PASSWORD).trim(),
                    ""
            );
        } else {
            System.out.println("No user name and password supplied, cannot do VFS MySQL tests.");
        }

        ConnectionPool connectionPool = new ConnectionPool(params, ConnectionPool.CONNECTION_TYPE_DEBRY);

        VFSDatabase db = new VFSDatabase(connectionPool, getXp().getString(DERBY + TABLE).trim());
        VFSMySQLProvider vfs = new VFSMySQLProvider(
                db,
                "qdl-vfs", // scheme here refers to QDL, not the database.
                "/",
                true,
                true);


        runVFSTests(vfs);
    }

    String propertiesFile = "/home/ncsa/dev/csd/config/qdl-test.properties";
    // names of things in the properties file.
    String MYSQL = "mysql.";
    String DERBY = "derby.";
    String USERNAME = "user";
    String PASSWORD = "password";
    String DATABASE = "database";
    String SCHEMA = "schema";
    String TABLE = "table";
    String BOOT_PASSWORD = "bootPassword";

    boolean gotConfig = false;

    boolean hasConfig() {
        return getXp() != null;
    }

    public XProperties getXp() {
        if (xp == null && !gotConfig) {
            xp = new XProperties();
            File file = new File(propertiesFile);
            if (!file.exists()) {
                System.out.println("warning, properties file " + propertiesFile + " not found");
                gotConfig = true;
                return null;
            }
            xp.load(propertiesFile);
        }
        return xp;
    }

    XProperties xp;


    public void testMySQLVFS() throws Throwable {
        MySQLConnectionParameters params;
        String tableName = "qdl_vfs"; // default;
        if (hasConfig()) {
            params = new MySQLConnectionParameters(
                    getXp().getString(MYSQL + USERNAME).trim(),
                    getXp().getString(MYSQL + PASSWORD).trim(),
                    getXp().getString(MYSQL + DATABASE).trim(),
                    getXp().getString(MYSQL + SCHEMA).trim(),
                    "localhost",
                    3306,
                    "com.mysql.cj.jdbc.Driver",
                    false,
                    "useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=America/Chicago"
            );
            tableName = getXp().getString(MYSQL + TABLE).trim();
        } else {
            if (System.getProperty("username") == null || System.getProperty("password") == null) {
                System.out.println("No user name and password supplied, cannot do VFS MySQL tests.");
                return;
            }
            params = new MySQLConnectionParameters(
                    System.getProperty("username"),
                    System.getProperty("password"),
                    "oauth2",
                    "oauth2",
                    "localhost",
                    3306,
                    "com.mysql.cj.jdbc.Driver",
                    false,
                    "useJDBCCompliantTimezoneShift=true&amp;useLegacyDatetimeCode=false&amp;serverTimezone=America/Chicago"
            );
        }

        MySQLConnectionPool connectionPool = new MySQLConnectionPool(params);
        VFSDatabase db = new VFSDatabase(connectionPool, tableName);
        VFSMySQLProvider vfs = new VFSMySQLProvider(
                db,
                "qdl-vfs",  // Scheme here refers to QDL, not the database!
                "/",
                true,
                true);


        runVFSTests(vfs);
    }

    /**
     * Hmmm This tests it. But... Performance is slow and there is just not a good way to improve it
     * since it is a zip file. Mostly mounting zip file is a convenience for the programmer who needs to
     * navigate a file and pull out an entry or two.
     *
     * @throws Throwable
     */

    public void testZipVFS() throws Throwable {

        String pathToZip = "/home/ncsa/dev/ncsa-git/security-lib/ncsa-qdl/src/test/resources/vfs-test/vfs-test.zip";
        // now for the path inside the zip file. Note that when mounted, this is absolute with respect to the
        // mount point. See the readme in the folder with the vfs-test.zip file for more info
        String mountPoint = PATH_SEPARATOR;
        String scheme = "qdl-zip";
        String storeRoot = scheme + SCHEME_DELIMITER + mountPoint; // prepend this to the paths in the zip

        String dirInZip = mountPoint + "root/other/sub-folder1";
        String fileInZip = mountPoint + "root/other/sub-folder1/math.txt";
        String pathInsideTheZip = mountPoint + "root/other/sub-folder1";
        String testPath = scheme + SCHEME_DELIMITER + pathInsideTheZip;
        VFSZipFileProvider vfs = new VFSZipFileProvider(pathToZip,
                scheme,
                mountPoint,
                true,
                false);

        // This is just for testing. It will print out a tree listing of what is in the file.
        // Practical problem with having a VFS fronting a zip file is that the entire file has
        // to be decompressed and stashed, so this is apt to be a huge amount of memory.
        // You can however, easily list files in the given directory:

        String[] dir = vfs.dir(storeRoot + "root");

        assert dir.length == 3;
        List<String> dirList = Arrays.asList(dir);
        assert dirList.contains("readme.txt");
        assert dirList.contains("scripts/");
        assert dirList.contains("other/");

        dir = vfs.dir(storeRoot);
        assert dir[0].equals("root/");

        assert vfs.contains(storeRoot + fileInZip);
        VFSEntry e = vfs.get(storeRoot + fileInZip, FILE_OP_AUTO);

        assert e.getText().equals("2+2 =4\n"); // contains a single line of text.
        testMkdir(vfs);
    }

    public void testServerMode() throws Throwable {
        String rootDir = "/home/ncsa/dev/ncsa-git/security-lib/ncsa-qdl/src/test/resources";
        State state = testUtils.getNewState();
        StringBuffer script = new StringBuffer();
        addLine(script, "    cfg.type := 'pass_through';");
        addLine(script, "  cfg.scheme := 'vfs';");
        addLine(script, "  cfg.access := 'rw';");
        addLine(script, "cfg.root_dir := '" + rootDir + "';");
        addLine(script, "cfg.mount_point := '/';");
        addLine(script, "vfs_mount(cfg.);");
        QDLInterpreter interpreter = new QDLInterpreter(null, state);
        interpreter.execute(script.toString());
        // now try to do a few things in server mode
        state.setServerMode(true);
        script = new StringBuffer();
        // should work.
        addLine(script, "readme := file_read('vfs#/vfs-test/readme.txt');");
        // NOTE that server mode for java modules should allow loading.
        addLine(script, "q := module_load('edu.uiuc.ncsa.qdl.extensions.http.QDLHTTPLoader','java');");
        interpreter = new QDLInterpreter(null, state);
        interpreter.execute(script.toString());
    }

    public void testServerModeBad() throws Throwable {
        String rootDir = "/home/ncsa/dev/ncsa-git/security-lib/ncsa-qdl/src/test/resources";
        State state = testUtils.getNewState();
        StringBuffer script = new StringBuffer();
        state.setServerMode(true);
        // should not work.
        addLine(script, "readme := file_read('" + rootDir + "/vfs-test/readme.txt');");
        QDLInterpreter interpreter = new QDLInterpreter(null, state);
        boolean good = false;
        try {
            interpreter.execute(script.toString());
        } catch (QDLServerModeException smx) {
            good = true;
        }
        assert good : "was able to access file in server mode.";
    }
    boolean testServerCall(StringBuffer script, State state) throws Throwable{
        QDLInterpreter interpreter = new QDLInterpreter(null, state);
        boolean good = false;
        try {
            interpreter.execute(script.toString());
        } catch (QDLServerModeException smx) {
            good = true;
        }
        return good;

    }

    /**
     * There are many places where server mode should prohibit access. This is a list
     * of them for regression testing, since having this contract change without warning
     * could be very bad  indeed for any servers.
     * @throws Throwable
     */
    public void testServerModes() throws Throwable{
        String rootDir = "/home/ncsa/dev/ncsa-git/security-lib/ncsa-qdl/src/test/resources";
        State state = testUtils.getNewState();
        StringBuffer script = new StringBuffer();
        state.setServerMode(true);

        // idea is to try all of the calls that fail in server mode.
        addLine(script, IOEvaluator.VFS_UNMOUNT + "('abc');");
        assert testServerCall(script, state) : " was able to call " + IOEvaluator.VFS_UNMOUNT + " in server mode";

        script = new StringBuffer();
        addLine(script, IOEvaluator.SCAN_FUNCTION + "('foo');");
        assert testServerCall(script, state) : " was able to call " + IOEvaluator.SCAN_FUNCTION + " in server mode";

        script = new StringBuffer();
        addLine(script, IOEvaluator.RMDIR + "('abc');");
        assert testServerCall(script, state) : " was able to call " + IOEvaluator.RMDIR + " in server mode";

        script = new StringBuffer();
        addLine(script, IOEvaluator.RM_FILE + "('abc');");
        assert testServerCall(script, state) : " was able to call " + IOEvaluator.RM_FILE + " in server mode";

        script = new StringBuffer();
        addLine(script, IOEvaluator.MKDIR + "('abc');");
        assert testServerCall(script, state) : " was able to call " + IOEvaluator.MKDIR + " in server mode";

        script = new StringBuffer();
        addLine(script, IOEvaluator.DIR + "('abc');");
        assert testServerCall(script, state) : " was able to call " + IOEvaluator.DIR + " in server mode";

        script = new StringBuffer();
        addLine(script, IOEvaluator.WRITE_FILE + "('abc', 'pqr');");
        assert testServerCall(script, state) : " was able to call " + IOEvaluator.WRITE_FILE + " in server mode";

        script = new StringBuffer();
        addLine(script, SystemEvaluator.CLIPBOARD_COPY + "();");
        assert testServerCall(script, state) : " was able to call " + SystemEvaluator.CLIPBOARD_COPY + " in server mode";

        script = new StringBuffer();
        addLine(script, SystemEvaluator.CLIPBOARD_PASTE + "('abc');");
        assert testServerCall(script, state) : " was able to call " + SystemEvaluator.CLIPBOARD_PASTE + " in server mode";

        script = new StringBuffer();
        addLine(script, SystemEvaluator.HAS_CLIPBOARD + "();");
        assert testServerCall(script, state) : " was able to call " + SystemEvaluator.HAS_CLIPBOARD + " in server mode";

        script = new StringBuffer();
        addLine(script, SystemEvaluator.OS_ENV + "();");
        assert testServerCall(script, state) : " was able to call " + SystemEvaluator.OS_ENV + " in server mode";

        // module loading needs to be checked.

        script = new StringBuffer();
        addLine(script, SystemEvaluator.MODULE_LOAD + "('" + rootDir + "/vfs-test/readme.txt');");
        assert testServerCall(script, state) : " was able to call " + SystemEvaluator.MODULE_LOAD + " on a non VFS file in server mode";
    }

}
