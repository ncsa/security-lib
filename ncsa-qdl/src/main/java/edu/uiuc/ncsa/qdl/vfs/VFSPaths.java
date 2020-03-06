package edu.uiuc.ncsa.qdl.vfs;

import java.io.File;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.jar.JarFile;

/**
 * This is compatible with the {@link Path} interface but <b>does not implement it</b>.
 * The reason is that while {@link java.nio.file.FileSystem} is really slick, policies for accessing
 * files are set by the security manager per JVM. So, if this code is to run on a server
 * with restricted access to the real file system (but access its own virtual mounts) we
 * have to restrict access for the <b><i>entire</i></b> JVM which means that the server can
 * no longer function, at least without very specific configuration which is going to be
 * hard for others to do. As such until something changes, we can't actually use the Java stuff, which,
 * to be fair, is made so that Java developers have seamless access to various file systems.
 * <br/><br/>
 * A path in the store is of the form
 * <pre>
 *     scheme#/a/b/c/d  (absolute)
 *     scheme#p/q/r     (relative)
 * </pre>
 * as such these are <i>almost</i> uris. The problem with using a URI vs this is that we must have a scheme at
 * all times to tell us which store this goes in and relative uris do not have schemes. No scheme
 * (which probably should include <code>#/a/b/c</code>)  means native file.
 * <p>Created by Jeff Gaynor<br>
 * on 2/29/20 at  7:03 AM
 */
public class VFSPaths {
    public static String SCHEME_DELIMITER = "#";
    public static String PATH_SEPARATOR = "/";
    public static String THIS_COMPONENT = ".";
    public static String PARENT_COMPONENT = "..";

    /**
     * Check is a path has this scheme. True if so, false if not
     *
     * @param scheme
     * @param path
     * @return
     */
    public static boolean checkScheme(String scheme, String path) {
        return getScheme(path).equals(scheme);
    }

    /**
     * Check if two paths have the same scheme
     * @param path1
     * @param path2
     * @return
     */
    public static boolean hasSameScheme(String path1, String path2){
        if (isUnq(path1) && isUnq(path2)) return true;
        if ((isUnq(path1) && !isUnq(path2)) || ((!isUnq(path1) && isUnq(path2)))) return false;
        // everything is FQ.
        return getScheme(path1).equals(getScheme(path2));

    }
    /**
     * Is this path unqualified? I.e., there is no scheme
     *
     * @param path
     * @return
     */
    public static boolean isUnq(String path) {
        return path.indexOf(SCHEME_DELIMITER) == -1;
    }

    /**
     * Returns the scheme for this path or an empty string if there is none.
     *
     * @param path
     * @return
     */
    public static String getScheme(String path) {
        int index = path.indexOf(SCHEME_DELIMITER);
        if(index <=0)return ""; // so no scheme or trivial scheme
        return path.substring(0, index);
    }

    /**
     * Returns if this path is absolute, i.e. if it starts with the path separator.
     * So A#/a/b/c is absolute, A#b/c is not. <br/><br/>
     * An absolute path contains enough information to find the file. A relative path
     * does not. This will compare them ignoring schemes.
     *
     * @param path
     * @return
     */
    public static boolean isAbsolute(String path) {
        return getUnqPath(path).startsWith(PATH_SEPARATOR);
    }

    /**
     * Takes a fully qualified path and returns the path without the scheme
     *
     * @param fqPath
     * @return
     */
    public static String getUnqPath(String fqPath) {
        return fqPath.substring(fqPath.indexOf(SCHEME_DELIMITER) + 1);
    }

/*
    public static String[] getComponents() {
        if (components == null) {
            // remove any empty paths
            String[] firstTry = path.getPath().split(VFSFileProvider.PATH_SEPARATOR);
            String[] components = new String[firstTry.length];
            int j = 0;
            for(int i  = 0; i < firstTry.length; i++){
                if(firstTry[i] != null && !firstTry[i].isEmpty()){
                    components[j++] = firstTry[i];
                }
            }
            if(components.length != firstTry.length){
                this.components = new String[j];
                System.arraycopy(components,0, this.components,0,this.components.length);
            }else{
                this.components = components;
            }
        }
        return components;
    }
*/


    public static String getFileName(String path) {
        String[] firstTry = path.split(PATH_SEPARATOR);
        return firstTry[firstTry.length - 1];
    }

    public static String getParentPath(String path){
        if(path==null || path.isEmpty()) return path;
        int index =path.lastIndexOf(PATH_SEPARATOR);
        return path.substring(0,index);
    }
    /**
     * The number of components in this path. Note that this does not, e.g.
     * {@link #normalize(String)} this path first so if that is an issue,
     * normalize first.
     *
     * @param path
     * @return
     */
    public int getPathComponentCount(String path) {
        return getUnqPath(path).split(PATH_SEPARATOR).length;
    }

    /**
     * Return the un-normalized components of this path. So A#a/b/c
     * would return the array <code>{"a","b","c"}</code>, as would the relative path a/b/c.
     *
     * @param path
     * @return
     */
    public static String[] toPathComponents(String path) {
        String uPath = getUnqPath(path);
        if(uPath.startsWith(PATH_SEPARATOR)){
            // if this is an absolute path, like /a/b/c, then the split method below will give a spurious
            // empty first element. We want the components regardless
            uPath = uPath.substring(1);
        }
        return uPath.split(PATH_SEPARATOR);
    }

    /**
     * Returns the path component at the given index. So <code>A#a/b/c</code>
     * and index = 1 returns "b".
     *
     * @param path
     * @param index
     * @return
     */
    public static String getComponentAt(String path, int index) {
        String[] components = toPathComponents(path);
        return components[index];
    }

    /**
     * Returns a subpath of components. This is FQ if the argument was/
     *
     * @param beginIndex
     * @param endIndex
     * @return
     */
    public static String subpath(String path, int beginIndex, int endIndex) {
        String p = "";
        String[] components = toPathComponents(path);
        for (int i = beginIndex; i < endIndex; i++) {
            p = p + (p.isEmpty() ? "" : PATH_SEPARATOR) + components[i];
        }
        if (isUnq(path)) {
            return p;
        }

        return path.substring(0, path.indexOf(SCHEME_DELIMITER)) + p;
    }

    /**
     * Does the path start with the other path? So if path=A#/a/b/c/d and other=B#/a/b
     * the answer is true. Note that the <b>paths</b> are compared here, not the
     * schemes so you can use this when comparing paths in different stores.
     *
     * @param path
     * @param other
     * @return
     */
    public static boolean startsWith(String path, String other) {
        return getUnqPath(path).startsWith(getUnqPath(other));
    }


    /**
     * Does this path end with the other? E.g. path=A#/a/b/c/d other=B#c/d
     * would return true.
     *
     * @param path
     * @param other
     * @return
     */
    public static boolean endsWith(String path, String other) {
        return getUnqPath(path).endsWith(getUnqPath(other));
    }

    /**
     * Removes redundant path components.
     *
     * @param path
     * @return
     */
    public static String normalize(String path) {
        String scheme = getScheme(path);
        String unq = getUnqPath(path);
        File file = new File(unq);
        String normalized = file.toPath().normalize().toString();
        if (isUnq(path)) {
            return normalized;
        }
        return getScheme(path) + SCHEME_DELIMITER + normalized;
    }

    /**
     * Resolves the relativePath against the path. So if path=A#/a/b/c and relativePath=B#q/r
     * then this returns A#/a/b/c/q/r. Note two edge cases. If the relativePath si actually absolute,
     * this will return that. If the relative Path is trivial, the the path is returned.
     * In point of  fact <br/><br/>
     *      relativize(p,resolve(p,q)).equals(q) <br/><br/>
     *  as long as they have the same scheme (or the relative path gets the scheme of the path).
     * @param path
     * @param relativePath
     * @return
     */
    public static String resolve(String path, String relativePath) {
        File pFile = new File(getUnqPath(path));
        File oFile = new File(getUnqPath(relativePath));
        Path p = pFile.toPath().resolve(oFile.toPath());
        if (isUnq(path)) {
            return p.toString();
        }
        return getScheme(path) + SCHEME_DELIMITER + p.toString();
    }

    /**
     * This resolves the other against the <i>parent</i> of the path -- making them siblings, i.e. in the same directory.
     * So if path=A#/a/b/c/d and other=q the result is A#/a/b/c/q, <i>i.e.</i>, d and q are now siblings in the
     * directory A#/a/b/c.
     * @param path
     * @param other
     * @return
     */

    public static String resolveSibling(String path, String other) {
        File pFile = new File(getUnqPath(path));
        File oFile = new File(getUnqPath(other));
        Path p = pFile.toPath().resolveSibling(oFile.toPath());
        if (isUnq(path)) {
            return p.toString();
        }
        return getScheme(path) + SCHEME_DELIMITER + p.toString();
    }

    /**
     * Attempts to relativize the other path against the path. So if path=/a/b other=/a/b/c/d
     * then this returns c/d. 
     * In point of  fact <br/><br/>
     *      relativize(p,resolve(p,q)).equals(q) <br/><br/>
     *  as long as they have the same scheme (or the relative path gets the scheme of the path).
     * @param path
     * @param other
     * @return
     */

    public static String relativize(String path, String other) {
        File pFile = new File(getUnqPath(path));
        File oFile = new File(getUnqPath(other));
        Path p = pFile.toPath().relativize(oFile.toPath());
        if (isUnq(path)) {
            return p.toString();
        }
        return getScheme(path) + SCHEME_DELIMITER + p.toString();
    }

    /**
     * Lexically compares two paths without schemes. This returns a 0 (zero) if they are equal. This may
     * not make much sense if they are in different schemes, but it does allow to see when two paths are
     * otherwise equal.
     * @param path
     * @param other
     * @return
     */
    public static int compareTo(String path, String other) {
        File pFile = new File(getUnqPath(path));
        File oFile = new File(getUnqPath(other));
        return pFile.toPath().compareTo(oFile.toPath());
    }

    /**
     * Compares two paths. These are equal if their schemes are equal and their normalizations are.
     * @param path1
     * @param path2
     * @return
     */
    public static boolean equals(String path1, String path2){
        return getScheme(path1).equals(getScheme(path2)) && compareTo(normalize(path1),normalize(path2))==0;
    }
    public static void main(String[] args) throws Exception {
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
                System.out.println("parent -> " + jarEntry.getName().substring(0, jarEntry
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
