package edu.uiuc.ncsa.security.util.cli;

import java.util.List;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 2/22/21 at  6:19 AM
 */
public interface ComponentManager {
    boolean use(InputLine inputLine) throws Throwable;
    List<String> listComponents();
}
