package edu.uiuc.ncsa.security.core.configuration;

import junit.framework.TestCase;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.configuration.tree.ConfigurationNode;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

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
    abstract protected XMLConfiguration getConfiguration() throws ConfigurationException;

    /**
     * The type of the configuration, aka the tag name for it.
     *
     * @return
     */
    abstract protected String getConfigurationType();

    protected XMLConfiguration getConfiguration(String resourceName) throws ConfigurationException {
        if (configuration == null) {
            configuration = Configurations.getConfiguration(getClass().getResource(resourceName));
        }

        return configuration;
    }

    XMLConfiguration configuration;


    protected ConfigurationNode getConfig(String configName) throws ConfigurationException {
        return getConfig(getConfiguration(), configName);
    }

    protected ConfigurationNode getConfig(XMLConfiguration cfg, String configName) throws ConfigurationException {
        return Configurations.getConfig(cfg, getConfigurationType(), configName);
    }

    protected void printNodes(ConfigurationNode root) {
        for (Object kid : root.getChildren()) {
            ConfigurationNode cn = (ConfigurationNode) kid;
            say("key=" + cn.getName() + ", value=" + cn.getValue());
            printNodes(cn);
        }

    }
    protected void print(Document doc){
        NodeList nodeList =  doc.getDocumentElement().getElementsByTagName("service");
        say("size =" + nodeList.getLength() + " elements");
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                NamedNodeMap nodeMap = node.getAttributes();
                say("name= " + nodeMap.getNamedItem("name"));
            }
        }
    }
    protected void print(Node node) {
        NodeList nodeList =  node.getChildNodes();
        say("size =" + nodeList.getLength() + " elements");
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node n = nodeList.item(i);
            if (n.getNodeType() == Node.ELEMENT_NODE) {
                NamedNodeMap nodeMap = n.getAttributes();
                say("name= " + nodeMap.getNamedItem("name"));
            }
        }
    }

    protected void say(Object x) {
        System.out.println(x.toString());
    }
}
