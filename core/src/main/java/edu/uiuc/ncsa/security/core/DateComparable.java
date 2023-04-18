package edu.uiuc.ncsa.security.core;

import java.util.Date;

/**
 * Interface to be implemented by objects that can be compared as dates.
 * <p>Created by Jeff Gaynor<br>
 * on 11/13/21 at  6:36 AM
 */
public interface DateComparable{
    Date getCreationTS();
}
