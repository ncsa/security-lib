package edu.uiuc.ncsa.qdl.config;

import static edu.uiuc.ncsa.security.core.util.StringUtils.isTrivial;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 3/12/21 at  8:12 AM
 */
public class QDLEditorArg {
    public String flag;
    public String connector = null;
    public boolean hasConnector(){
        return !isTrivial(connector);
    }
    public String value;
    public boolean hasValue(){
        return !isTrivial(value);
    }
}
