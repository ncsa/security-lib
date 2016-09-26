package edu.uiuc.ncsa.security.oauth_2_0;

import edu.uiuc.ncsa.security.core.exceptions.GeneralException;
import org.apache.http.HttpStatus;

/**
 * This is thrown by the AT servlet and is used to construct the response.
 * <p>Created by Jeff Gaynor<br>
 * on 9/14/16 at  12:26 PM
 */
// This class is part of the fix for CIL-332.
public class OA2ATException extends GeneralException {

    public OA2ATException(String error, String description, int statusCode) {
        this.description = description;
        this.error = error;
        this.statusCode = statusCode;
    }

    public OA2ATException(String error, String description) {
           this.description = description;
           this.error = error;
           this.statusCode = HttpStatus.SC_BAD_REQUEST; //default
       }

    String error;
    String description;
    int statusCode;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }
}
