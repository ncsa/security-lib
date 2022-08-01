package edu.uiuc.ncsa.security.servlet;

import edu.uiuc.ncsa.security.core.exceptions.GeneralException;

/**
 * In cases where there is a problem with a request (defined as getting a non-success status code back)
 * this will wrap the content and the status code for further processing. Standard use of this has the content
 * AND the message. The message may be empty, but if there is another cause it will be set. Properly the message should
 * be logged and the content parsed for further use.
 * <p>Created by Jeff Gaynor<br>
 * on 10/22/15 at  4:30 PM
 */
public class ServiceClientHTTPException extends GeneralException {
    String content;
    int status;

    public boolean hasContent(){
        return ((content != null) && 0 < content.length());
    }

    /**
     * This should have the body of the response as a string.
     * @return
     */
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    /**
     * This should have the status code returned by the server.
     * @return
     */
    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public ServiceClientHTTPException() {
    }

    public ServiceClientHTTPException(Throwable cause) {
        super(cause);
    }

    public ServiceClientHTTPException(String message) {
        super(message);
    }

    public ServiceClientHTTPException(String message, Throwable cause) {
        super(message, cause);
    }
}
