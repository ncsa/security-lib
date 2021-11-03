package edu.uiuc.ncsa.qdl.exceptions;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 11/2/21 at  7:07 AM
 */
public class MacroException extends QDLException{
    public MacroException() {
    }

    public MacroException(Throwable cause) {
        super(cause);
    }

    public MacroException(String message) {
        super(message);
    }

    public MacroException(String message, Throwable cause) {
        super(message, cause);
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    String command;
}
