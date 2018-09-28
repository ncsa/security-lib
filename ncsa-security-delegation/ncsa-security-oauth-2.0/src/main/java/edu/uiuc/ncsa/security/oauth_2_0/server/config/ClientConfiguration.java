package edu.uiuc.ncsa.security.oauth_2_0.server.config;

import edu.uiuc.ncsa.security.util.functor.parser.Script;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 8/30/17 at  3:49 PM
 */
public class ClientConfiguration {
    public void setRuntime(Script runtime) {
        this.runtime = runtime;
    }

    public Script getRuntime(){
        return runtime;
    }

    /**
     * Executes the runtime. This returns true if there is  a runtime and it is executed, false otherwise.
     * @return
     */
    public boolean executeRuntime(){
        if(runtime!= null){
            runtime.execute();
            return true;
        }
        return false;
    }
    Script runtime;
}
