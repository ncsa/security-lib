package edu.uiuc.ncsa.qdl.module;

import edu.uiuc.ncsa.qdl.state.XKey;
import edu.uiuc.ncsa.qdl.state.XThing;

/**
 * A wrapper for module instances. These have to exist since they are keyed off alias
 * rather than the {@link MTKey} for templates.
 * <p>Created by Jeff Gaynor<br>
 * on 2/1/22 at  6:41 AM
 */
public class MIWrapper implements XThing {
    public Module getModule() {
        return module;
    }

    Module module;

    public MIWrapper(Module module) {
        this.module = module;
        this.key = module.key; // use default alias
    }

    public MIWrapper(XKey key, Module module) {
        this.key = key;
        this.module = module;
    }

    @Override
    public String getName() {
        return key.getKey();
    }

    XKey key;
    @Override
    public XKey getKey() {
        return key;
    }
}
