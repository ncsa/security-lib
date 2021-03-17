package edu.uiuc.ncsa.security.util.cli.editing;

import static edu.uiuc.ncsa.security.core.util.StringUtils.isTrivial;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 3/12/21 at  8:12 AM
 */
public class EditorArg {
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
