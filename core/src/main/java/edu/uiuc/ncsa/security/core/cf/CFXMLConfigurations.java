package edu.uiuc.ncsa.security.core.cf;

import edu.uiuc.ncsa.security.core.exceptions.MyConfigurationException;
import edu.uiuc.ncsa.security.core.inheritance.CyclicalError;
import edu.uiuc.ncsa.security.core.util.DebugUtil;
import edu.uiuc.ncsa.security.core.util.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.time.LocalTime;
import java.util.Collection;
import java.util.HashMap;
import java.util.TreeSet;

import static edu.uiuc.ncsa.security.core.util.StringUtils.isTrivial;

/**
 * Utility class for processing XML configuration files. This is for managing XML
 * nodes at a low level.
 */
public class CFXMLConfigurations {

    /**
     * Top-level tag for all configurations this system understands
      */
    public static final String CONFIGURATION_TAG = "config";
    public static final String CONFIGURATION2_TAG = "bundle";
    public static final String FILE_TAG = "file";
    public static final String INCLUDE_TAG = "include";

    /**
     * This just returns the {@link Document}.
     *
     * @param file
     * @return
     * @throws MyConfigurationException
     */
    public static Document loadDocument(File file) throws MyConfigurationException {
        try {
            return loadDocument(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            throw new MyConfigurationException("could not load file \"" + file.getAbsolutePath() + "\"", e);
        }
    }

    /**
     * Loads a file as a resource. This just returns the {@link Document}.
     *
     * @param uri
     * @return
     * @throws MyConfigurationException
     */
    public static Document loadDocument(String uri) throws MyConfigurationException {
        return loadDocument(CFXMLConfigurations.class.getClassLoader().getResourceAsStream(uri));
    }


    /**
     * Loads tjhe given inputStream. The stream is closed at the end. This just returns the {@link Document}.
     *
     * @param inputStream
     * @return
     * @throws MyConfigurationException
     */
    public static Document loadDocument(InputStream inputStream) throws MyConfigurationException {
        try {
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document doc = builder.parse(inputStream);
            doc.getDocumentElement().normalize();
            inputStream.close();
            return doc;

        } catch (Throwable t) {
            throw new MyConfigurationException("Cannot read input stream", t);
        }
    }


    /**
     * Convenience method for getting the value of a single node, i.e. the contents, so
     * <br><br>
     * &lt;foo&gt;value&lt;/foo&gt;<br><br> would have name equal to 'foo' and return the
     * string 'value'.
     *
     * @param node
     * @param name
     * @return
     */
    public static String getNodeValue(Node node, String name) {
        if (node == null) {
            return null;
        }
        Node node1 = getFirstNode(node, name);
        if (node1 == null) return null;
        return node1.getNodeValue();
    }

    /**
     * Return the first named child configuration node of the given node or null if there is
     * no such named child. Very useful if your specification only allows for a single child node.
     *
     * @param node
     * @param name
     * @return
     */
    public static Node getFirstNode(Node node, String name) {
        if (node == null) return null;
        NodeList list = node.getChildNodes();

        if (list.getLength() == 0) {
            return null;
        }
        for (int i = 0; i < list.getLength(); i++) {
            Node n = list.item(i);
            if (n.getNodeName().equals(name)) {
                return n;
            }
        }
        return null;
    }

    /**
     * Get the very first attribute with the given name from the {@link Node}.
     * A null is returned if there is no such value.
     *
     * @param node
     * @param name
     * @return
     */
    public static String getFirstAttribute(Node node, String name) {
        if (node == null) {
            return null;
        }
        NamedNodeMap namedNodeMap = node.getAttributes();
        for (int i = 0; i < namedNodeMap.getLength(); i++) {

            Node n = namedNodeMap.item(i);
            if (n.getNodeName().equals(name)) {
                return n.getNodeValue();
            }
        }
        return null;
    }

    public static boolean getFirstBooleanValue(Node node, String attrib, boolean defaultValue) {
        if (node == null) return defaultValue;
        try {
            String x = getFirstAttribute(node, attrib);
            if (isTrivial(x)) {
                return defaultValue;
            } //  Null argument returns false.
            return Boolean.parseBoolean(x);
        } catch (Throwable t) {

        }
        return defaultValue;
    }

    /**
     * Return the first long value of an attribute. Must have the default value specified. There is no
     * promise as to what "first value" means thanks to the XML spec., so effectively you should restrict
     * your XML to have at most one such value.
     *
     * @param node
     * @param attrib
     * @param defaultValue
     * @return
     */
    public static long getFirstLongValue(Node node, String attrib, long defaultValue) {
        if (node == null) return defaultValue;
        try {
            String x = getFirstAttribute(node, attrib);
            if (isTrivial(x)) {
                return defaultValue;
            } //  Null argument returns false.
            return Long.parseLong(x);
        } catch (Throwable t) {

        }
        return defaultValue;
    }


    /**
     * Return the default value if none is found.
     *
     * @param node
     * @param name
     * @param defaultValue
     * @return
     */
    public static String getNodeValue(Node node, String name, String defaultValue) {
        if (node == null) return defaultValue;
        String x = getNodeValue(node, name);
        return x == null ? defaultValue : x;
    }

    /**
     * Loads the named configuration that resides under the tagName, e.g. tagName might be
     *  'my-server'. This supports aliases too, so if the top level
     * tag contains an alias="x" attribute, then the configuration named "x" is loaded instead. This
     * allows for things like a "default" or "current" configuration which need never change once deployed.
     * The aim is to make this all as configuration driven as possible.
     *
     * @param cfg
     * @param tagName
     * @param configName
     * @return
     */
    public static Node getConfig(Document cfg, String tagName, String configName) {
        // start the recursion
        return resolveAliases(cfg, tagName, configName);
    }


    /**
     * Given an alias, resolve it to the correct node, checking for cycles.
     *
     * @param cfg
     * @param topNodeName The tag inside the outermost tag you want, e.g. "service", "client"
     * @param configName  The name (i.e. value of name attributes)
     * @return
     */
    public static Node resolveAliases(Document cfg, String topNodeName, String configName) {
        HashMap<String, String> aliasTable = new HashMap<>();
        HashMap<String, Node> nodeMap = new HashMap<>(); // all the nodes by name. This means we need one pass through document
        NodeList nodeList = cfg.getDocumentElement().getElementsByTagName(topNodeName);

        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            NamedNodeMap attrs = node.getAttributes();
            Node nameNode = attrs.getNamedItem("name");
            if (nameNode == null) {
                throw new MyConfigurationException("missing name for node.");
            }

            String nodeName = nameNode.getNodeValue();
            Node aliasNode = attrs.getNamedItem("alias");
            if (aliasNode == null) {
                nodeMap.put(nodeName, node);
                continue;
            }
            String alias = aliasNode.getNodeValue();
            if (alias != null) {
                if (aliasTable.containsKey(alias)) {
                    throw new CyclicalError("Cyclical exception. The alias \"" + alias + "\" is already in the configuration.");
                }
                aliasTable.put(nodeName, alias);
            }
        }
        /* So we have a table of aliases. E.g. if A -> B -> C -> D The table is
             A  B
             B  C
             C  D
             No entry for D ==> D is an actual node, not an alias.
        */
        String currentAlias = configName;
        while (aliasTable.containsKey(currentAlias)) {
            currentAlias = aliasTable.get(currentAlias);
        }
        if (nodeMap.containsKey(currentAlias)) {
            return nodeMap.get(currentAlias);
        }
        throw new MyConfigurationException("Configuration \"" + configName + "\" not found");
    }


    /**
     * Get alarms that are in a given tag. returns null if no alarms are set
     *
     * @param node
     * @param tag
     * @return
     */
    public static Collection<LocalTime> getAlarms(Node node, String tag) {
        Collection<LocalTime> alarms = null;
        String raw = getFirstAttribute(node, tag);
        if (!StringUtils.isTrivial(raw)) {
            alarms = new TreeSet<>();  // sorts them.
            String[] ta = raw.split(",");
            // get all the times that parse correctly
            for (String time : ta) {
                try {
                    alarms.add(LocalTime.parse(time.trim()));
                } catch (Throwable t) {
                    DebugUtil.error(CFXMLConfigurations.class, "cannot parse alarm date \"" + ta + "\"");
                    // do nothing
                }
            }
            DebugUtil.trace(CFXMLConfigurations.class, tag + " found: " + alarms);
        }
        return alarms;

    }

    /**
     * Utility to print a DOM document. This is really basic, just a debugging tool really.
     * @param doc
     * @param out
     * @throws IOException
     * @throws TransformerException
     */
    public static void printDocument(Document doc, OutputStream out)  {
        try {
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer();
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
            transformer.setOutputProperty(OutputKeys.METHOD, "xml");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

            transformer.transform(new DOMSource(doc),
                    new StreamResult(new OutputStreamWriter(out, "UTF-8")));
        }catch(Throwable t) {
            System.out.println("Could not print DOM document");
            t.printStackTrace();
        }
    }
    public static String printNode(Node node)  {
        String nv = node.getNodeName();
        String content = node.getTextContent();
        String out = "node[name=" + node.getNodeName() ;
        if(!StringUtils.isTrivial(nv)) {
            out += ",value=" + nv ;
        }
        if(!StringUtils.isTrivial(content)) {
            out += ",content=" + content ;
        }
        out +=  "]";
        return out;
    }

}
