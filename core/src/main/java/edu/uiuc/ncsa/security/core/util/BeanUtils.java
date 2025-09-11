package edu.uiuc.ncsa.security.core.util;

/**
 * Utilities for beans.
 * <p>Created by Jeff Gaynor<br>
 * on May 26, 2011 at  10:16:20 AM
 */
public class BeanUtils {
    /**
     * This checks if two objects are equal. This uses the object's equals method to check, so if the object allows
     * for nulls then this will pass. Some objects that have no content allow equality to null objects.
     *
     * @param x
     * @param y
     * @return
     */
    public static boolean checkEquals(Object x, Object y) {
        if (x == y) return true;
        if (x == null) {
            if (y == null) {
                return true;
            }
            return y.equals(x);
        }
        return x.equals(y);
    }

    /**
     * Check two string with the option of doing so case insensitively.
     * @param x
     * @param y
     * @param ignoreCase
     * @return
     */
    public static boolean checkEquals(String x, String y, boolean ignoreCase) {
        if (x == y) return true;
        if (x == null) {
            if (y == null) {
                return true;
            }
            return y.equals(x);
        }
        if(ignoreCase){
            return x.equalsIgnoreCase(y);
        }
        return x.equals(y);
    }

    /**
     * This presupposes that nulls are not permitted, so if one of the objects is null, it cannot possible
     * be equal to the other, e.g. comparing URIs.
     *
     * @param x
     * @param y
     * @return
     */
    public static boolean checkNoNulls(Object x, Object y) {
        if (x == y) return true;
        if (x == null) {
            if (y == null) {
                return true;
            }
            return false;
        } else {
            if (y == null) {
                return false;
            }
        }
        return x.equals(y);
    }

    public static boolean checkNoNulls(String x, String y, boolean ignoreCase) {
        if (x == y) return true;
        if (x == null) {
            if (y == null) {
                return true;
            }
            return false;
        } else {
            if (y == null) {
                return false;
            }
        }
        if(ignoreCase){
            return x.equalsIgnoreCase(y);
        }
        return x.equals(y);
    }

    /**
     * Very basic checks that all objects should do as part of their equals methods.
     * Checks null-ity of the an object  and if the second is an instance of the first.
     * <pre>
     * public boolean equals(fnord){
     * if(!BeanUtils.checkBasic(this, fnord)) return false;
     * // Rock on feel free to cast too
     * }
     * </pre>
     *
     * @param obj1
     * @param obj2
     * @return
     */
    public static boolean checkBasic(Object obj1, Object obj2) {
        if(obj1 == null && obj2 == null) return true;
        if (obj1 == null) return false; // one is null, the other isn't
        if (obj2 == null) return false;
        if (obj1 == obj2) return true;
        if (!obj1.getClass().isAssignableFrom(obj2.getClass())) return false;
        return true;
    }
}
