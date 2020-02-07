package edu.uiuc.ncsa.security.oauth_2_0.server.scripts.functor;

import edu.uiuc.ncsa.security.oauth_2_0.server.scripts.ClientScripts;
import edu.uiuc.ncsa.security.util.functor.parser.Script;
import edu.uiuc.ncsa.security.util.scripting.ScriptSet;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 2/5/20 at  2:15 PM
 */
public class ClientFunctorScripts<V extends Script> extends ScriptSet<V> implements ClientScripts  {
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
