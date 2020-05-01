package edu.uiuc.ncsa.qdl.install;

import java.io.*;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.file.FileSystem;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static java.lang.Integer.MAX_VALUE;

/**
 * Really simple installer.
 * This gets copied to your jar and will just copy everything in the jar to
 * a given directory (including sub directories). So make the tree you want, jar it up with this class
 * and run it. This is a completely stand alone class because otherwise you have to manage dependencies
 * for this installer program which can get out of hand. The idea is that this is lean.
 * <p>Created by Jeff Gaynor<br>
 * on 3/30/20 at  7:23 AM
 */
public class Installer {
    protected void trace(String message) {
        if (debugOn) {
            say(message);
        }
    }

    boolean debugOn = false;
    boolean upgrade = false;

    public static void main(String[] args) {

        try {
            Installer installer = new Installer();

            // Contract for debug: set label with -debug switch. No label means "trace"
            for (int i = 0; i < args.length; i++) {
                String arg = args[i];
                switch (arg) {
                    case UPGRADE_FLAG:
                        installer.upgrade = true;
                        break;
                    case HELP_FLAG:
                        installer.showHelp();
                        return;
                    case DEBUG_FLAG:
                        installer.debugOn = true;
                        break;

                    case TARGET_DIR_FLAG:
                        installer.rootDir = new File(args[++i]);
                        break;
                }
            }
            installer.trace("Starting " + (installer.upgrade ? "upgrade" : "install") + "...");
            installer.init();
            if (installer.upgrade) {
                installer.doUpgrade();
            } else {
                installer.copyFiles();
            }
        } catch (Exception ex) {
            ex.printStackTrace(); //handle an exception here
        }
    }

    static protected final String UPGRADE_FLAG = "-u";
    static protected final String HELP_FLAG = "--help";
    static protected final String TARGET_DIR_FLAG = "-dir";
    static protected final String DEBUG_FLAG = "-debug";

    private void showHelp() {
        say("install [" + "] [-dir target_dir]");
        say("This will install QDL to your system. Options are");

    }

    public static void pack(final Path folder, final Path zipFilePath) throws IOException {
        try (
                FileOutputStream fos = new FileOutputStream(zipFilePath.toFile());
                ZipOutputStream zos = new ZipOutputStream(fos)
        ) {
            Files.walkFileTree(folder, new SimpleFileVisitor<Path>() {
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    zos.putNextEntry(new ZipEntry(folder.relativize(file).toString()));
                    Files.copy(file, zos);
                    zos.closeEntry();
                    return FileVisitResult.CONTINUE;
                }

                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                    zos.putNextEntry(new ZipEntry(folder.relativize(dir).toString() + "/"));
                    zos.closeEntry();
                    return FileVisitResult.CONTINUE;
                }
            });
        }
    }

    protected boolean checkUpgradeDir(File dir) {
        trace("checking if dir exists for " + dir);
        if (!dir.exists()) {
            if (!dir.mkdir()) {
                trace("  nope");
                return false;
            }

        }
        trace("  yup");

        return true;
    }

    protected void nukeDir(File dir) {
        File[] contents = dir.listFiles();
        for (File f : contents) {
            trace("found " + f);

            if (f.isFile()) {
                trace("   deleting " + f);
                f.delete();
            }
        }
    }

    protected void doUpgrade() throws Exception {
        // Cleanup first
        File libDir = new File(rootDir, "lib");
        File docDir = new File(rootDir, "docs");
        File exxDir = new File(rootDir, "examples");
        File[] dirs = new File[]{libDir, docDir, exxDir};
        for (File d : dirs) {
            if (!checkUpgradeDir(d)) {
                say("Could not find directory \"" + d + "\"");
                return;
            }
            nukeDir(d);
        }
        // Don't nuke unless everything worked!
        for (File d : dirs) {
            trace("removing directory contents.");
            nukeDir(d);
        }

        Stream<Path> walk = getInstallerStream();
        trace("starting upgrade...");
        for (Iterator<Path> it = walk.iterator(); it.hasNext(); ) {
            Path current = it.next();
            trace("current file is " + current);
            // Omit the installer itself and the manifest for the jar. everything else gets copied.
            if (!current.startsWith("/edu/") && !current.startsWith("/META-INF/")) {

                String cString = current.toString();
                File f = new File(rootDir.getAbsolutePath() + current);
                trace("processing " + f.getCanonicalPath());
                if (f.getParentFile().equals(libDir)) {
                    trace("processing lib dir" + f);
                    InputStream is = Installer.class.getResourceAsStream(cString); // start with something we know is there                     
                    cp(cString, f);

                }
                if (f.getParentFile().equals(exxDir)) {
                    trace("processing examples dir" + f);
                    cp(cString, f);

                }

                if (f.getParentFile().equals(docDir)) {
                    trace("processing doc dir" + f);
                    cp(cString, f);

                }
                if (f.getAbsolutePath().endsWith("/version.txt")) {
                    trace("processing version file" + f);
                    f.delete();
                    cp(cString, f);


                }
            }
        }
    }

    protected void cp(String x, File target) throws IOException {
        if(target.isDirectory()){
            trace("Skipping directory "+ target);
            return;
        }
        InputStream is = getClass().getResourceAsStream(x); // start with something we know is there
        Files.copy(is, target.toPath());
    }

    protected Stream<Path> getInstallerStream() throws Exception {
        // Start with a path we know is there. Annoyingly this fails for "/".
        URI uri = Installer.class.getResource("/lib").toURI();
        Path myPath;
        if (uri.getScheme().equals("jar")) {
            FileSystem fileSystem = FileSystems.newFileSystem(uri, Collections.<String, Object>emptyMap());
            myPath = fileSystem.getPath("/");
        } else {
            myPath = Paths.get(uri);
        }

        return Files.walk(myPath, MAX_VALUE);

    }

    protected void copyFiles() throws Exception {
        Stream<Path> walk = getInstallerStream();
        trace("starting install...");
        for (Iterator<Path> it = walk.iterator(); it.hasNext(); ) {
            Path current = it.next();
            trace("current file is " + current);
            // Omit the installer itself and the manifest for the jar. everything else gets copied.
            if (!current.startsWith("/edu/") && !current.startsWith("/META-INF/")) {

                String cString = current.toString();
                boolean isDir = Files.isDirectory(current); // best check if it is a directory.
                File f = new File(rootDir.getAbsolutePath() + current);
                trace("creating " + f.getCanonicalPath());
                if (isDir) {
                    // At this point we never create directories in an upgrade.
                    trace("creating directories");
                    Files.createDirectories(f.toPath());
                } else {
                    if (!f.getParentFile().exists()) {
                        Files.createDirectories(f.toPath());
                    }
                    InputStream is = Installer.class.getResourceAsStream(cString); // start with something we know is there

                    trace("copying file");
                    cp(cString, f);
                    if (cString.endsWith("bin/qdl")) {
                        trace("setting qdl script to be executable");
                        doSetupExec(f);
                    }
                }
            }
        }

    }


    /**
     * Read the executable file (the one they invoke to run QDL) and set the root directory in it,
     * then set it to be executable.
     *
     * @param f
     */
    private void doSetupExec(File f) throws IOException {
        List<String> lines = Files.readAllLines(f.toPath());
        for (int i = 0; i < lines.size(); i++) {
            if (lines.get(i).startsWith("QDL_HOME")) {
                lines.set(i, "QDL_HOME=\"" + rootDir.getCanonicalPath() + "\"");
                break;
            }
        }
        Files.write(f.toPath(), lines, Charset.defaultCharset());
        f.setExecutable(true);
    }

    protected void init() throws Exception {
        say("Welcome to the QDL the installer.");

        if (rootDir == null) {
            String lineIn = readline("Enter the target path. This is where QDL will be " + (upgrade ? "upgraded" : "installed") + ":");
            rootDir = new File(lineIn);
        }
        if (upgrade) {
            if (!rootDir.exists()) {
                say("Sorry, but that directory does not exist so no upgrade can be done. Exiting...");
                return;
            }
        } else {
            if (rootDir.exists()) {
                if (rootDir.list().length != 0) {
                    say("This exists and is not empty. Exiting...");
                }
                return;
            }
            trace("creating directories for root path");
            Files.createDirectories(rootDir.toPath());
        }
    }

    File rootDir = null;


    protected BufferedReader getBufferedReader() {
        if (bufferedReader == null) {
            bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        }
        return bufferedReader;
    }


    BufferedReader bufferedReader;

    public String readline(String prompt) throws Exception {
        System.out.print(prompt);
        return getBufferedReader().readLine();
    }

    protected void say(String x) {
        System.out.println(x);
    }


    protected HashMap<String, String> getFileList() {
        HashMap<String, String> fileList = new HashMap<>();
        return fileList;
    }
}
