package edu.uiuc.ncsa.qdl.module;

import edu.uiuc.ncsa.qdl.state.XKey;

import java.net.URI;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 12/13/21 at  7:18 AM
 */
public class MTKey extends XKey {
   public MTKey(URI uri){
       super(uri.toString());
       uriKey = uri;
   }

    public URI getUriKey() {
        return uriKey;
    }

    URI uriKey;
}
