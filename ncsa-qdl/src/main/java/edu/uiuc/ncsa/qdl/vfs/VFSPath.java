package edu.uiuc.ncsa.qdl.vfs;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.*;
import java.util.Iterator;
import java.util.jar.JarFile;

/**
 * This is compatible with the {@link Path} interface but <b>does not implement it</b>.
 * The reason is that while {@link java.nio.file.FileSystem} is really slick, policies for accessing
 * files are set by the security manager per JVM. So, if this code is to run aon a server
 * with restricted access to the real file system (but access its own virtual mounts) we
 * have to restrict access for the <b><i>entire</i></b> JVM which means that the server can
 * no longer function. As such until something changes, we can't actually use the Java stuff, which,
 * to be fair, is made so that Java developers have seamless access to various file systems.
 * <br/><br/>
 * Just in case, if we ever decide to extends {@link Path}, all we need to do is make this class
 * implement it and add a few methods, so most of the work is done because we do need to do things like
 * compare paths, fine parent paths, etc.
 * <p>Created by Jeff Gaynor<br>
 * on 2/29/20 at  7:03 AM
 */
public class VFSPath  {

      
    public boolean isAbsolute() {
        return false;
    }

      
    public Path getRoot() {
        return null;
    }

      
    public Path getFileName() {
        return null;
    }

      
    public Path getParent() {
        return null;
    }

      
    public int getNameCount() {
        return 0;
    }

      
    public Path getName(int index) {
        return null;
    }

      
    public Path subpath(int beginIndex, int endIndex) {
        return null;
    }

      
    public boolean startsWith(Path other) {
        return false;
    }

      
    public boolean startsWith(String other) {
        return false;
    }

      
    public boolean endsWith(Path other) {
        return false;
    }

      
    public boolean endsWith(String other) {
        return false;
    }

      
    public VFSPath normalize() {
        return null;
    }

      
    public VFSPath resolve(VFSPath other) {
        return null;
    }

      
    public Path resolve(String other) {
        return null;
    }

      
    public Path resolveSibling(Path other) {
        return null;
    }

      
    public Path resolveSibling(String other) {
        return null;
    }

      
    public Path relativize(Path other) {
        return null;
    }

      
    public URI toUri() {
        return null;
    }

      
    public Path toAbsolutePath() {
        return null;
    }

      
    public Path toRealPath(LinkOption... options) throws IOException {
        return null;
    }

      
    public File toFile() {
        return null;
    }


      
    public Iterator<Path> iterator() {
        return null;
    }

      
    public int compareTo(Path other) {
        return 0;
    }
    public static void main(String[] args) throws Exception{
        /*
        Nifty example where a jar is read using the usual machinery then a FileSystem is created
        and it is navigated like a regular File.
         */
        String pathToJar = "/home/ncsa/dev/temp-deploy/cilogon-oa2/cilogon-oa2-cp.jar";
        String pathInsideTheJar = "edu/uiuc/ncsa/oa4mp/oauth2/client";
        File testZip = new File(pathToJar);
        JarFile jarFile = new JarFile(testZip);
        jarFile.stream().forEach((jarEntry -> {
            System.out.println(jarEntry.getName());
            if (jarEntry.getName().lastIndexOf('/') != -1) {
                System.out.println( "parent -> " + jarEntry.getName().substring(0, jarEntry
                        .getName()
                        .lastIndexOf('/')));
            }
        }));
        // now just mount it like a file system and print it (with a snazzy lambda)
        System.out.println("\n-------\nUsing FileSystems\n-----");
        FileSystem fileSystem = FileSystems.newFileSystem(testZip.toPath(), null);
        // need a path in the jar
        Path meow = fileSystem.getPath(pathInsideTheJar);
        Files.list(meow).forEach((path) -> System.out.println(path.getFileName()));
    }
}
