package edu.uiuc.ncsa.security.storage.sql;

import edu.uiuc.ncsa.security.core.Initializable;
import edu.uiuc.ncsa.security.core.configuration.provider.CfgEvent;
import edu.uiuc.ncsa.security.core.configuration.provider.TypedProvider;
import org.apache.commons.configuration.tree.ConfigurationNode;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 5/4/12 at  9:24 AM
 */
public abstract class SQLInitializerProvider extends TypedProvider<Initializable> {
    public SQLInitializerProvider(ConfigurationNode config, String type, String target) {
        super(config, type, target);
    }

    @Override
    public Object componentFound(CfgEvent configurationEvent) {
        return null;
    }
}
