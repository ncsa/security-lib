package edu.uiuc.ncsa.security.core.configuration;

import edu.uiuc.ncsa.security.core.cf.CFBundle;
import edu.uiuc.ncsa.security.core.cf.CFNode;
import edu.uiuc.ncsa.security.core.cf.CFXMLConfigurations;
import junit.framework.TestCase;
import org.w3c.dom.Document;

import java.util.List;
import java.util.Map;

/**
 * Abstract class that allows for testing a configuration. This tests both Apache commons Configuration.
 * Implement the {@link #getConfiguration()}
 * method to pull in the named configuration. This does not run here, but is
 * <pre>
 * ╔══════════════╗
 * ║ used in OA4MP║
 * ╚══════════════╝
 * </pre>
 * for several tests, and is here because it is then available across various modules.
 *
 * <p>Created by Jeff Gaynor<br>
 * on 1/19/12 at  10:49 AM
 */
public abstract class ConfigTest extends TestCase {

    /**
     * Implements this to get the configuration file with a given name from the resources directory.
     *
     * @return
     */
    abstract protected CFBundle getConfiguration() ;

    /**
     * The type of the configuration, aka the tag name for it.
     *
     * @return
     */
    abstract protected String getConfigurationType();

    protected CFBundle getConfiguration(String resourceName) {
        if (configuration == null) {
            Document document = CFXMLConfigurations.loadDocument(resourceName);
            configuration = new CFBundle(document, getConfigurationType());
        }

        return configuration;
    }

    CFBundle configuration;


    /**
     * gets a node by name from this configuration bundle.
     *
     * @param configName
     * @return
     */
    protected CFNode getConfig(String configName) {
        return getConfig(getConfiguration(), configName);
    }

    /*
        protected ConfigurationNode getConfig(CFBundle cfg, String configName) throws ConfigurationException {
            return Configurations.getConfig(cfg, getConfigurationType(), configName);
        }
    */
    protected CFNode getConfig(CFBundle cfg, String configName) {
        return cfg.getNamedConfig(configName);
    }

    /*    protected void printNodes(CFNode root) {
            for (Object kid : root.getChildren()) {
                ConfigurationNode cn = (ConfigurationNode) kid;
                say("key=" + cn.getName() + ", value=" + cn.getValue());
                printNodes(cn);
            }
        }*/
    protected void print(CFBundle bundle) {
        List<CFNode> allNodes = bundle.getAllNodes();
        for (CFNode node : allNodes) {
            print(node);
        }
/*
        NodeList nodeList =  doc.getDocumentElement().getElementsByTagName("service");
        say("size =" + nodeList.getLength() + " elements");
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                NamedNodeMap nodeMap = node.getAttributes();
                say("name= " + nodeMap.getNamedItem("name"));
            }
        }
*/
    }

    /**
     * Print the children of a given node.
     *
     * @param node
     */
    protected void print(CFNode node) {
        List<CFNode> nodeList = node.getChildren();
        String indent = "   ";
        for (int i = 0; i < nodeList.size(); i++) {
            CFNode n = nodeList.get(i);
            Map<String, String> attributes = n.getAttributes();
            say(attributes.get("name") + " size =" + nodeList.size() + " elements");
            for (String key : attributes.keySet()) {
                if (!attributes.get(key).equals("name")) {
                    say(indent + "@" + key + "=" + attributes.get(key));
                }
            }
            List<CFNode> kids = n.getChildren();
            for (CFNode kid : kids) {
                say(indent + kid.getName());
            }
/*
            if (n.getANodeType() == Node.ELEMENT_NODE) {
                NamedNodeMap nodeMap = n.getAttributes();
                say("name= " + nodeMap.getNamedItem("name"));
            }
*/
        }
    }

    protected void say(Object x) {
        System.out.println(x.toString());
    }
}
