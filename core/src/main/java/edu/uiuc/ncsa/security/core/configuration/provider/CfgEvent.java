package edu.uiuc.ncsa.security.core.configuration.provider;

import edu.uiuc.ncsa.security.core.cf.CFNode;

import java.util.EventObject;
import java.util.List;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 1/16/12 at  8:46 AM
 */
public class CfgEvent extends EventObject {


    public CfgEvent(Object source, CFNode config) {
        super(source);
        cfNode = config;
    }


    public CFNode getCFNode() {
        return cfNode;
    }

    public void setCFNode(CFNode cfNode) {
        this.cfNode = cfNode;
    }

    CFNode cfNode;

    protected boolean hasCFNode() {
        return cfNode != null;
    }

    public String getName() {
        return cfNode.getName();
    }

    List children = null;

    public List getChildren(String name) {
        if (children == null) {
            children = cfNode.getChildren(name);
        }
        return children;
    }

    public boolean hasChildren(String name) {
        return !getChildren(name).isEmpty();
    }
}
