package edu.uiuc.ncsa.security.util.cli.editing;

import static edu.uiuc.ncsa.security.core.util.StringUtils.isTrivial;

/**
 * Arguments for an editor. Most editors allow for some sort of flag with a value
 * but the connector is up to the program. E.g. "-foo bar" might be one, as might
 * "-foo=bar". You can specify a flag connector value. 
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
