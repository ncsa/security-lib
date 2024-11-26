package edu.uiuc.ncsa.security.util.cli;

import java.util.Set;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 2/22/21 at  6:19 AM
 */
public interface ComponentManager {
    boolean use(InputLine inputLine) throws Throwable;
    Set<String> listComponents();
}
