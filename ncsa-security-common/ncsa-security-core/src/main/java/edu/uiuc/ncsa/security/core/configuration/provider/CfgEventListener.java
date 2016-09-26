package edu.uiuc.ncsa.security.core.configuration.provider;

import java.util.EventListener;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 1/16/12 at  8:46 AM
 */
public interface CfgEventListener extends EventListener {
    public Object componentFound(CfgEvent configurationEvent);
}
