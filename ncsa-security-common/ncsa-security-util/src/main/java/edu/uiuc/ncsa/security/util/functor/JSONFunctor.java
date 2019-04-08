package edu.uiuc.ncsa.security.util.functor;

import net.sf.json.JSONObject;

/**
 * Interface for functor like things that are defined in JSON. These can execute something.
 * <p>Created by Jeff Gaynor<br>
 * on 6/14/18 at  10:21 AM
 */
public interface JSONFunctor extends JMetaMetaFunctor{
    JSONObject toJSON();

}
