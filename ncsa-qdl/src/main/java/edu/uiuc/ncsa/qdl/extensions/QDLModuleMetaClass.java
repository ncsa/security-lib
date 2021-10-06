package edu.uiuc.ncsa.qdl.extensions;

import java.io.Serializable;

/**
 * One way to make a Java module is to have a super class and have each method
 * or variable defined as non-static inner classes. These then can share state
 * between them. This interface should be implemented for those classes in order
 * to ensure compatibility with the system. It has no methods and is a
 * marker interface.
 * <p>Created by Jeff Gaynor<br>
 * on 10/4/21 at  6:58 AM
 */
public interface QDLModuleMetaClass extends Serializable {
}
