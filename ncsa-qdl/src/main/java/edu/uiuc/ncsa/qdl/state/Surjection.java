package edu.uiuc.ncsa.qdl.state;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This is a map that tracks a surjection (many values map to a single one) between
 * the namespace uri its aliases, allowing reverse lookups. Aliases and module ids
 *  are unique in the workspace, <b>but</b> there may be many imports for a given uri,
 *  hence this class.
 * <p>Created by Jeff Gaynor<br>
 * on 4/1/20 at  9:13 AM
 */
public class Surjection<String, URI> extends HashMap<String,URI> {
    /**
     * For a given URI, get all the aliases. Note that this is not terribly efficient.
     * The use pattern at its creation date is that this is a necessary but infrequently used
     * method, so the alternate of having another internal map and managing that (for every insertion,
     * delete, etc) is just not worth the work. If somehow this turns in to a bottleneck, then
     * this should change.
     * @param uri
     * @return
     */
   public List<String> getByURI(URI uri){
       ArrayList<String> out = new ArrayList<>();
            for(Map.Entry<String, URI> entry: entrySet()){
                if(uri.equals(entry.getValue())){
                    out.add(entry.getKey());
                }
       }
       return out;
   }
}
