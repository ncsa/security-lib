package edu.uiuc.ncsa.security.core.configuration.provider;

import edu.uiuc.ncsa.security.core.cf.CFNode;
import org.apache.commons.configuration.tree.ConfigurationNode;

import java.util.EventObject;
import java.util.List;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 1/16/12 at  8:46 AM
 */
public class CfgEvent extends EventObject {
    /**
     * Constructs a prototypical Event.
     *
     * @param source The object on which the Event initially occurred.
     * @throws IllegalArgumentException if source is null.
     */
    public CfgEvent(Object source, ConfigurationNode config) {
        super(source);
        configuration = config;
    }

    public CfgEvent(Object source, CFNode config) {
        super(source);
        cfNode = config;
    }

    public ConfigurationNode getConfiguration() {
        return configuration;
    }

    public void setConfiguration(ConfigurationNode configuration) {
        this.configuration = configuration;
    }

    ConfigurationNode configuration;

    public CFNode getCFNode() {
        return cfNode;
    }

    public void setCFNode(CFNode cfNode) {
        this.cfNode = cfNode;
    }

    CFNode cfNode;
    protected boolean hasCFNode() {return cfNode != null;}
    public String getName(){
        if(hasCFNode()){

        }
        return configuration.getName();
    }
    List children = null;

    public List getChildren(String name) {
        if(children == null){
            if(hasCFNode()){
                children = cfNode.getChildren(name);
            }else{
                children = configuration.getChildren(name);
            }
        }
        return children;
    }
    public boolean hasChildren(String name) {
        return !getChildren(name).isEmpty();
    }
}
