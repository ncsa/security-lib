package edu.uiuc.ncsa.qdl.install;

import java.io.*;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.*;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.stream.Stream;

import static java.lang.Integer.MAX_VALUE;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 3/30/20 at  7:23 AM
 */
public class Installer {
    public static String[] allDirs = new String[]{"bin", "docs", "etc", "lib", "lib/cp", "var", "var/ws", "log"};

    public static void main(String[] args) {
        try {

            // ==============
            Installer installer = new Installer();
            installer.doIt();
            installer.copyFiles();
        } catch (Exception ex) {
            ex.printStackTrace(); //handle an exception here
        }
    }

    protected void copyFiles() throws Exception {

        URI uri = Installer.class.getResource("/lib").toURI(); // start with something we know is there
        Path myPath;
        if (uri.getScheme().equals("jar")) {
            System.out.println("got uri=" + uri);
            FileSystem fileSystem = FileSystems.newFileSystem(uri, Collections.<String, Object>emptyMap());
            myPath = fileSystem.getPath("/");
        } else {
            myPath = Paths.get(uri);
        }

        System.out.println("myPath=" + myPath);

        Stream<Path> walk = Files.walk(myPath, MAX_VALUE);
        for (Iterator<Path> it = walk.iterator(); it.hasNext(); ) {
            Path current = it.next();
            if (!current.startsWith("/edu/") && !current.startsWith("/META-INF/")) {
                // so it's not a directory and not one of the installer files.
                System.out.println("got file = " + current);
                 String target = rootDir.getAbsolutePath() + current;
                File f = new File(target);
                System.out.println("   canonical path = " + f.getCanonicalPath());

                 if(current.endsWith(File.separator)){
                     System.out.println("   got directory");
                 }else{
                     System.out.println("   got file");
                     f.getParentFile().mkdirs();

                 }

            }
        }

    }

    protected void doIt() throws Exception {
        say("Welcome to the QDL the installer.");
        String lineIn = readline("Enter the target path. This is where QDL will be installed:");
        rootDir = new File(lineIn);
        if (rootDir.exists()) {
            say("Sorry, this exists. Please re-run this program and specify a new directory for this.");
            return;
        }
        rootDir.mkdirs();

/*
        InputStream inputStream = Installer.class.getResourceAsStream("/version.txt");
        copy(inputStream, "version.txt");
        inputStream = Installer.class.getResourceAsStream("/lib/qdl.jar");
        copy(inputStream, "lib/qdl.jar");
        inputStream = Installer.class.getResourceAsStream("/etc/qdl-cfg.xml");
        copy(inputStream, "etc/qdl-cfg.xml");
*/

    }

    File rootDir = null;

    protected void mkdirs(File root) throws Exception {
        say("making directories");
        for (String x : allDirs) {
            File f = new File(root, x);
            f.mkdirs();
        }
        say("...done!");
    }

    protected void copy(InputStream inputStream, File targetFile) throws Exception {
        FileOutputStream fileOutputStream = new FileOutputStream(targetFile);
        Files.copy(inputStream, targetFile.toPath());

    }

    protected BufferedReader getBufferedReader() {
        if (bufferedReader == null) {
            bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        }
        return bufferedReader;
    }

    protected void setBufferedReader(BufferedReader bufferedReader) {
        this.bufferedReader = bufferedReader;
    }

    BufferedReader bufferedReader;

    public String readline(String prompt) throws Exception {
        System.out.print(prompt);
        return getBufferedReader().readLine();
    }

    protected void say(String x) {
        System.out.println(x);
    }

    /*
    mkdir "bin"
    mkdir "docs"
    cp /home/ncsa/dev/ncsa-git/cilogon.github.io.git/qdl/docs/*.pdf docs
    mkdir "etc"
    cp /home/ncsa/dev/ncsa-git/security-lib/ncsa-qdl/src/main/resources/min-cfg.xml etc/min-cfg.xml
    mkdir "lib"
    cp "$QDL_ROOT/target/qdl.jar" lib
    mkdir "log"
    mkdir "lib/cp"
    mkdir "var"
    mkdir "var/ws"
     */
    protected HashMap<String, String> getFileList() {
        HashMap<String, String> fileList = new HashMap<>();
        //  fileList.put("")
        return fileList;
    }
}
