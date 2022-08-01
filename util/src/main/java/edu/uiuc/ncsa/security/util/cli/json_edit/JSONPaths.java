package edu.uiuc.ncsa.security.util.cli.json_edit;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 8/17/20 at  11:30 AM
 */
public class JSONPaths {
    public static String SCHEME_DELIMITER = "#";

    public static String PATH_SEPARATOR = "/";
    public static String THIS_COMPONENT = ".";
    public static String PARENT_COMPONENT = "..";
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
    public  static String getUnqPath(String fqPath) {
        return fqPath.substring(fqPath.indexOf(SCHEME_DELIMITER) + 1);
    }

    static public  String getFileName(String path) {
        String[] firstTry = path.split(PATH_SEPARATOR);
        return firstTry[firstTry.length - 1];
    }

    public  static String getParentPath(String path) {
        if (path == null || path.isEmpty()) return path;
        int index = path.lastIndexOf(PATH_SEPARATOR);
        return path.substring(0, index);
    }

    /**
     * Return the un-normalized components of this path. So A#a/b/c
     * would return the array <code>{"a","b","c"}</code>, as would the relative path a/b/c.
     *
     * @param path
     * @return
     */
    public  static String[] toPathComponents(String path) {
        String uPath = getUnqPath(path);
        if (uPath.startsWith(PATH_SEPARATOR)) {
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
    public  static String getComponentAt(String path, int index) {
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
    public  static String subpath(String path, int beginIndex, int endIndex) {
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
    public  static boolean startsWith(String path, String other) {
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
    public  static boolean endsWith(String path, String other) {
        return getUnqPath(path).endsWith(getUnqPath(other));
    }

    /**
     * Removes redundant path components.
     *
     * @param path
     * @return
     */
    public  static String normalize(String path) {
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
     * relativize(p,resolve(p,q)).equals(q) <br/><br/>
     * as long as they have the same scheme (or the relative path gets the scheme of the path).
     *
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
     *
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
     * relativize(p,resolve(p,q)).equals(q) <br/><br/>
     * as long as they have the same scheme (or the relative path gets the scheme of the path).
     *
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
     *
     * @param path  rin
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
     *
     * @param path1
     * @param path2
     * @return
     */
    public static boolean equals(String path1, String path2) {
        return getScheme(path1).equals(getScheme(path2)) && compareTo(normalize(path1), normalize(path2)) == 0;
    }


    public static void main(String[] args) {
        try {
            String a = "/abc/de\\/fg/h\\/i\\/j\\//k";
            for(String x : getComponents(a)){
                System.out.println(x);

            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    public static String[] getComponents(String path){
        List<String> tokens = new ArrayList<>();
        StringTokenizer stringTokenizer = new StringTokenizer(path, "/");
                 while (stringTokenizer.hasMoreTokens()) {
                     String tokken = stringTokenizer.nextToken();
                     if (tokken.endsWith("\\")) {
                         tokken = tokken.substring(0, tokken.length() - 1) + "/";
                         String nt = stringTokenizer.nextToken();

                         while (nt.endsWith("\\")) {
                             tokken = tokken + nt.substring(0, nt.length() - 1) + "/";
                             nt = stringTokenizer.nextToken();
                         }
                         tokken = tokken + nt;
                     }
                     tokens.add(tokken);
                 }
                 return tokens.toArray(new String[]{});
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
}
