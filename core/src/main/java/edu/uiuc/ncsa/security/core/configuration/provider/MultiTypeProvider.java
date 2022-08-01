package edu.uiuc.ncsa.security.core.configuration.provider;

import edu.uiuc.ncsa.security.core.util.MyLoggingFacade;
import org.apache.commons.configuration.tree.ConfigurationNode;

import java.util.List;

/**
 * A configured provider that notifies its listeners when its get method is called and returns the first
 * non-null object. It no listeners respond, then this provides for a default store.
 *
 * Note that this should be the top-level provider for stores that can have multiple implementations.
 * It's constructors are, by and large, ignored but need to be there for Apache's APIs.
 * <p>Created by Jeff Gaynor<br>
 * on 1/18/12 at  10:09 AM
 */
public abstract class MultiTypeProvider<T> extends TypedProvider<T> {
    protected boolean disableDefaultStore = false;
    protected MyLoggingFacade logger;
    protected MultiTypeProvider() {
    }

    protected MultiTypeProvider(ConfigurationNode config,
                                boolean disableDefaultStore,
                                MyLoggingFacade logger, String type, String target) {
        super(config, type, target);
        this.logger =logger;
        this.disableDefaultStore = disableDefaultStore;
    }

    protected MultiTypeProvider(MyLoggingFacade logger, String type, String target) {
        super(type, target);
        this.logger = logger;
    }

    @Override
    public Object componentFound(CfgEvent configurationEvent) {
        return get();
    }


    /**
     * This will notify all listeners and return the first instance found. If you do not want/need listeners
     * @return
     */
    @Override
      public T get() {
          List kidList = getConfig().getChildren();
          for (int i = 0; i < kidList.size(); i++) {
              ConfigurationNode foo = (ConfigurationNode) kidList.get(i);
              T gotOne = fireComponentFound(new CfgEvent(this, foo));
              if (gotOne != null) {
                  return gotOne;
              }
          }
          // if we get to here, return the default store.
          if(disableDefaultStore){
              throw new DefaultStoreDisabledException("Error: Default store is not enabled for this configuration.(" + getClass().getSimpleName() + ")");
          }
          return getDefaultStore();
      }
    /**
     * Supply a default store if none is specified. It is also acceptable to throw an exception
     * for this method if no default store should be given.
     * @return
     */
    public abstract T getDefaultStore();
}
