package edu.uiuc.ncsa.qdl;

import edu.uiuc.ncsa.qdl.vfs.*;
import edu.uiuc.ncsa.security.core.configuration.XProperties;
import edu.uiuc.ncsa.security.storage.sql.mysql.MySQLConnectionParameters;
import edu.uiuc.ncsa.security.storage.sql.mysql.MySQLConnectionPool;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 3/4/20 at  8:57 AM
 */
public class VFSTest extends TestBase {
    @Test
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

    @Test
    public void testFilePassThrough() throws Throwable {
        String rootDir = "/home/ncsa/dev/ncsa-git/security-lib/ncsa-qdl/src/test/resources";
        VFSPassThruFileProvider vfs = new VFSPassThruFileProvider(
                rootDir,
                "qdl-vfs",
                "/",
                true,
                false);
        VFSEntry fileEntry = vfs.get("qdl-vfs#/hello_world.qdl");
        System.out.println(fileEntry.getText());
        System.out.println(fileEntry.getProperties());


        String[] files = vfs.dir("qdl-vfs#/");
        for (String f : files) {
            System.out.println(f);
        }
        String currentDir = "qdl-vfs#/";

        vfs.setCurrentDir(currentDir);
        fileEntry = vfs.get("qdl-vfs#boot.qdl");
        System.out.println(fileEntry.getText());
        System.out.println(fileEntry.getProperties());

    }

    protected FileEntry makeFE() {
        List<String> lines = new ArrayList<>();
        lines.add(getRandomString());
        lines.add(getRandomString());
        XProperties xProperties = new XProperties();
        xProperties.put(FileEntry.CONTENT_TYPE, FileEntry.TEXT_TYPE);
        return new FileEntry(lines, xProperties);

    }

    @Test
    public void testMemoryStoreVFS() throws Throwable {
        VFSMemoryFileProvider vfs = new VFSMemoryFileProvider(
                "qdl-vfs", "/", true, true
        );

        VFSEntry fileEntry = makeFE();
        vfs.put("qdl-vfs#/foo.txt", fileEntry);
        System.out.println(vfs.get("qdl-vfs#/foo.txt").getText());
        String[] files = vfs.dir("qdl-vfs#/");
        for (String f : files) {
            System.out.println(f);
        }

        String currentDir = "qdl-vfs#/";

        vfs.setCurrentDir(currentDir);
        fileEntry = vfs.get("qdl-vfs#foo.txt");
        System.out.println(fileEntry.getText());
        System.out.println(fileEntry.getProperties());

    }


    @Test
    public void testDBVFS() throws Throwable {
         if(System.getProperty("username") == null || System.getProperty("password") == null){
             System.out.println("No user name and password supplied, cannot do VFS MySQL tests ");
             return;
         }
        MySQLConnectionParameters params = new MySQLConnectionParameters(
                System.getProperty("username"),
                System.getProperty("password"),
                "oauth2",
                "oauth2",
                "localhost",
                3306,
                "com.mysql.jdbc.Driver",
                false,
                "useJDBCCompliantTimezoneShift=true&amp;useLegacyDatetimeCode=false&amp;serverTimezone=America/Chicago"
        );
        MySQLConnectionPool connectionPool = new MySQLConnectionPool(params);
        VFSDatabase db = new VFSDatabase(connectionPool, "qdl_vfs");
        VFSMySQLProvider vfs = new VFSMySQLProvider(
                db, "qdl-vfs", "/", true, true
        );
        VFSEntry fileEntry = makeFE();
        System.out.println("contains file?" + vfs.contains("qdl-vfs#/foo.txt"));
        vfs.put("qdl-vfs#/foo.txt", fileEntry);
        VFSEntry entry = vfs.get("qdl-vfs#/foo.txt");
        assert entry.getLines().get(0).equals(fileEntry.getLines().get(0));
        assert entry.getLines().get(1).equals(fileEntry.getLines().get(1));

        // now remount it and get get same file
        vfs = new VFSMySQLProvider(
                        db, "qdl-vfs2", "/mysql", true, true
                );
        System.out.println("contains file?" + vfs.contains("qdl-vfs2#/mysql/foo.txt"));
        entry = vfs.get("qdl-vfs2#/mysql/foo.txt");
        assert entry.getLines().get(0).equals(fileEntry.getLines().get(0));
        assert entry.getLines().get(1).equals(fileEntry.getLines().get(1));

    }
}
