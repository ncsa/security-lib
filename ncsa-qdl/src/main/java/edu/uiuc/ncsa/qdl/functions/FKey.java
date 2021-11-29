package edu.uiuc.ncsa.qdl.functions;

import edu.uiuc.ncsa.qdl.state.XKey;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 11/26/21 at  7:46 AM
 */
public class FKey extends XKey {
    public int getArgCount() {
        return argCount;
    }

    int argCount = -1;
    String munger = "$$$";

    public String getfName() {
        return fName;
    }

    String fName;

    /**
     * Convenience for a very common idiom.
     * @param name
     * @return
     */
     public boolean hasName(String name){
         return fName.equals(name);
     }
    public FKey(String fkey, int argCount) {
        super();
        this.fName = fkey;
        this.argCount = argCount;
        if (-1 < argCount) {
            setKey(fkey + munger + argCount);
        } else {
            setKey(fkey + munger);
        }
    }

}
