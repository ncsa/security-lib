package edu.uiuc.ncsa.security.util.cli;

import java.io.IOException;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 5/11/20 at  6:57 AM
 */
public interface IOInterface {
    String readline(String prompt) throws IOException;
    String readline() throws IOException;
    void print(Object x);
    void println(Object x);
    void flush();
}
