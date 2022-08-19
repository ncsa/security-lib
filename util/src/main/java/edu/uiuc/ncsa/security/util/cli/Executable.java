package edu.uiuc.ncsa.security.util.cli;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 8/15/22 at  3:41 PM
 */
public interface Executable {
    public void execute(String x);
    public IOInterface getIO();
    public void setIO(IOInterface io);
}
