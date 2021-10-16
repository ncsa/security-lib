package edu.uiuc.ncsa.qdl.exceptions;

/**
 * <p>This should only be thrown during deserialization if there is an actual problem
 * with the deserialization of on otherwise valid file, e.g. missing java module classes
 * This disambiguates it from the case that, e.g., we can only check the format of a file
 * by trying to deserialize it and, e.g., finding no XML (in a QDL file).
 * </p>
 * <p>Basically this means the file is otherwise fine, but the workspace cannot be
 * reconstructed for whatever reason.</p>
 * <p>Created by Jeff Gaynor<br>
 * on 10/15/21 at  6:09 AM
 */
public class DeserializationException extends QDLRuntimeException{
    public DeserializationException() {
    }

    public DeserializationException(Throwable cause) {
        super(cause);
    }

    public DeserializationException(String message) {
        super(message);
    }

    public DeserializationException(String message, Throwable cause) {
        super(message, cause);
    }
}
