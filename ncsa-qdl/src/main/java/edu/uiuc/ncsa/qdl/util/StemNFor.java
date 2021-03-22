package edu.uiuc.ncsa.qdl.util;

import edu.uiuc.ncsa.qdl.variables.StemVariable;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 3/22/21 at  10:07 AM
 */
public class StemNFor extends NFor{
    public StemNFor(StemVariable leftArg,  NForActionInterface action) {
        super(new int[]{leftArg.size()}, action);
    }
}
