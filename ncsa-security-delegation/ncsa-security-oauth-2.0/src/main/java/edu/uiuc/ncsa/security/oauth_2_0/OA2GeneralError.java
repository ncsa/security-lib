package edu.uiuc.ncsa.security.oauth_2_0;

import edu.uiuc.ncsa.security.core.exceptions.GeneralException;
import org.apache.http.HttpStatus;

/**
 * This is for use places where there is no redirect url available. Examples are the userInfo and getCert endpoints for OA4MP.
 * It has an error and description but will be turned into a standard
 * response with the given status code. It is up to any client to interpret this correctly.
 * <p>Created by Jeff Gaynor<br>
 * on 10/22/15 at  11:18 AM
 */
public class OA2GeneralError extends GeneralException {

    /**
     * Convert a redirectable error to a general one. The default is to set the status code
     * to 400 = bad request so something is there.
     * @param error
     */
    public OA2GeneralError(OA2RedirectableError error) {
        setDescription(error.getDescription());
        setError(error.getError());
        setHttpStatus(HttpStatus.SC_BAD_REQUEST);
    }

    public OA2GeneralError(String error,String description,  int httpStatus) {
        this.description = description;
        this.error = error;
        this.httpStatus = httpStatus;
    }

    public OA2GeneralError(Throwable cause,String error, String description,  int httpStatus) {
        super(cause);
        this.description = description;
        this.error = error;
        this.httpStatus = httpStatus;
    }

    public OA2GeneralError(String message, String error, String description, int httpStatus) {
        super(message);
        this.description = description;
        this.error = error;
        this.httpStatus = httpStatus;
    }

    public OA2GeneralError(String message, Throwable cause, String error, String description,  int httpStatus) {
        super(message, cause);
        this.description = description;
        this.error = error;
        this.httpStatus = httpStatus;
    }

    public int getHttpStatus() {
        return httpStatus;
    }

    public void setHttpStatus(int httpStatus) {
        this.httpStatus = httpStatus;
    }

    int httpStatus;

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    String error;
    String description;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
