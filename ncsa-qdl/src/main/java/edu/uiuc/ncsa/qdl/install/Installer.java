package edu.uiuc.ncsa.qdl.install;

import java.io.*;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.file.FileSystem;
import java.nio.file.*;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

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
     protected void trace(String message){
        if(debugOn){
            say(message);
        }
     }
     boolean debugOn = false;
    public static void main(String[] args) {

        try {
            Installer installer = new Installer();

            // Contract for debug: set label with -debug switch. No label means "trace"
            for (String arg : args) {
                if (arg.equals("-debug")) {
                    installer.debugOn = true;
                    break;
                }
            }
            installer.trace("Starting install...");
            installer.init();
            installer.copyFiles();
        } catch (Exception ex) {
            ex.printStackTrace(); //handle an exception here
        }
    }

    protected void copyFiles() throws Exception {

        // Start with a path we know is there. Annoyingly this fails for "/".
        URI uri = Installer.class.getResource("/lib").toURI();
        Path myPath;
        if (uri.getScheme().equals("jar")) {
            FileSystem fileSystem = FileSystems.newFileSystem(uri, Collections.<String, Object>emptyMap());
            myPath = fileSystem.getPath("/");
        } else {
            myPath = Paths.get(uri);
        }

        Stream<Path> walk = Files.walk(myPath, MAX_VALUE);
        trace("copying files");
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
                  trace("creating directories");
                  Files.createDirectories(f.toPath());
                } else {
                    if(!f.getParentFile().exists()) {
                        Files.createDirectories(f.toPath());
                    }
                    trace("copying file");
                    InputStream is = Installer.class.getResourceAsStream(cString); // start with something we know is there
                    Files.copy(is, f.toPath());
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
        String lineIn = readline("Enter the target path. This is where QDL will be installed:");
        rootDir = new File(lineIn);
        if (rootDir.exists()) {
            if(rootDir.list().length != 0) {
                say("This exists and is not empty. Exiting...");
            }
            return;
        }
        trace("creating directories for root path");
        Files.createDirectories(rootDir.toPath());
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
        //  fileList.put("")
        return fileList;
    }
}
