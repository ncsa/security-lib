package edu.uiuc.ncsa.security.util.functor;

import net.sf.json.JSONObject;

/**
 * Topmost interface for functor like things. These can execute something.
 * <p>Created by Jeff Gaynor<br>
 * on 6/14/18 at  10:21 AM
 */
public interface JMetaFunctor {
    Object execute();
    Object getResult();
    JSONObject toJSON();
}
