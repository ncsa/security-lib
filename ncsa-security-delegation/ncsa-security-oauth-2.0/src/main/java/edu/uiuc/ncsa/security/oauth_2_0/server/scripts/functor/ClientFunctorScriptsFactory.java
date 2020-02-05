package edu.uiuc.ncsa.security.oauth_2_0.server.scripts.functor;

import edu.uiuc.ncsa.security.util.functor.JFunctorFactory;
import edu.uiuc.ncsa.security.util.functor.parser.Script;
import net.sf.json.JSONObject;

import javax.inject.Provider;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 2/5/20 at  2:16 PM
 */
public class ClientFunctorScriptsFactory<V extends ClientFunctorScripts> implements Provider<V> {
    public ClientFunctorScriptsFactory(JFunctorFactory functorFactory) {
          this.functorFactory = functorFactory;
      }

      protected JFunctorFactory functorFactory;


      /**
       * Create a new {@link ClientFunctorScripts}.
       * @param json
       * @return
       */
      public V newInstance(JSONObject json) {
          V cc = get();
          cc.setRuntime(new Script(functorFactory, ClientFunctorScriptsUtil.getRuntime(json)));
          return cc;
      }


      @Override
      public V get() {
          return (V) new ClientFunctorScripts();
      }
}
