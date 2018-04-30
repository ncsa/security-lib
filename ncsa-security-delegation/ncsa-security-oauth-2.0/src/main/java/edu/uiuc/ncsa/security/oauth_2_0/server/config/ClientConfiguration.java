package edu.uiuc.ncsa.security.oauth_2_0.server.config;

import edu.uiuc.ncsa.security.util.functor.LogicBlock;
import edu.uiuc.ncsa.security.util.functor.LogicBlocks;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 8/30/17 at  3:49 PM
 */
public class ClientConfiguration {
    public void setRuntime(LogicBlocks<? extends LogicBlock> runtime) {
        this.runtime = runtime;
    }

    public LogicBlocks<? extends LogicBlock> getRuntime(){
        return runtime;
    }

    /**
     * Executes the runtime. This returns true if there is  a runtime and it is executed, false otherwise.
     * @return
     */
    public boolean executeRuntime(){
        if(runtime != null){
            return runtime.execute();
        }
        return false;
    }
    LogicBlocks<? extends LogicBlock> runtime;
}
