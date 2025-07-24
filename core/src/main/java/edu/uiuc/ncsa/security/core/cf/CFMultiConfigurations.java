package edu.uiuc.ncsa.security.core.cf;

import edu.uiuc.ncsa.security.core.MultiConfigurationsInterface;
import edu.uiuc.ncsa.security.core.exceptions.MyConfigurationException;
import edu.uiuc.ncsa.security.core.inheritance.CyclicalError;
import edu.uiuc.ncsa.security.core.inheritance.InheritanceList;
import edu.uiuc.ncsa.security.core.inheritance.InheritanceMap;
import edu.uiuc.ncsa.security.core.inheritance.MultipleInheritanceEngine;
import org.w3c.dom.Node;

import java.util.*;

import static edu.uiuc.ncsa.security.core.util.StringUtils.isTrivial;

/**
 * Configuration Utility that allows for inheritance from other configurations. This
 * also has sseveral static methods to help with migration of existing configurations.
 * For testing this see the MultipleInheritanceTest in the testing harness, which exercises
 * this very well.
 * <p>Created by Jeff Gaynor<br>
 * on 1/31/21 at  4:59 PM
 */
public class CFMultiConfigurations implements MultiConfigurationsInterface {
    public static final String EXTENDS_TAG = "extends";
    public static final String NAME_TAG = "name";
    public static final String ALIAS_TAG = "alias";



    Map<String, Node> allNodes = new HashMap<>();
    /**
     * Every named node that does <b>not</b> have an alias, i.e. "real" nodes
     */
    public InheritanceMap getNamedNodes() {
        return namedNodes;
    }
    /**
     * Every node with a name attribute
     */
    public Map<String, Node> getAllNodes() {
        return allNodes;
    }


    InheritanceMap namedNodes = new InheritanceMap();
    // Next two are needed for internal processing during ingestion.
    InheritanceMap unresolvedAliases = new InheritanceMap(); // alias + node
    InheritanceMap unresolvedOverrides = new InheritanceMap(); // name  + list

    @Override
    public MultipleInheritanceEngine getInheritanceEngine() {
        return inheritanceEngine;
    }

    MultipleInheritanceEngine inheritanceEngine;

    /*
    One major difference between this and the old way is that inheritance means that the entire configuration
    has to be processed rather than before where only the specific requested named configuration would be
    resolved (so if there were alias cycles some place else, they would not get found.
    Same with duplicate named configurations.
     */
    public void ingestConfig(CFBundle cfg,
                             String tagName // the tag of elements to look at, e.g. service, client, qdl
    ) {

        List<CFNode> cfNodes = cfg.getAllNodes(tagName);

        for (CFNode cfNode : cfNodes) {
            Node cn = cfNode.getNodes().get(0); // should be single nodes per...
            String name = CFXMLConfigurations.getFirstAttribute(cn, NAME_TAG);
            if (isTrivial(name)) {
                throw new MyConfigurationException("Missing name. The configuration does not contain a name. Configuration load is aborted.");
            }
            if (allNodes.containsKey(name)) {
                throw new CyclicalError("Duplicated name \"" + name + "\"  Configuration load is aborted.");
            }
            allNodes.put(name, cn);
            String alias = CFXMLConfigurations.getFirstAttribute(cn, ALIAS_TAG);
            String parents = CFXMLConfigurations.getFirstAttribute(cn, EXTENDS_TAG);

            if (alias == null) {
                namedNodes.put(name, name, splitParentList(parents));
                if (parents != null) {
                    unresolvedOverrides.put(name, splitParentList(parents));
                }

            } else {
                if (parents != null) {
                    unresolvedOverrides.put(name, alias, splitParentList(parents));
                }

                unresolvedAliases.put(name, alias, splitParentList(parents));
            }
        }
        inheritanceEngine = new MultipleInheritanceEngine(namedNodes, unresolvedAliases, unresolvedOverrides);
        inheritanceEngine.resolve();
    }

    /**
     * This will return all the {@link Node}s with the given name.
     *
     * @param cfgName
     * @return
     */
    public CFNode getNamedConfig(String cfgName) {
        // everything has been resolved.
        // Get the list of names to return
        if (!getInheritanceEngine().isResolutionsRun()) {
            throw new MyConfigurationException("No configurations have been resolved.. Configuration load is aborted.");
        }

        if (!getInheritanceEngine().getResolvedOverrides().containsKey(cfgName)) {
            throw new MyConfigurationException("Configuration \"" + cfgName + "\" not found. Configuration load is aborted.");
        }

        InheritanceList inheritanceList = getInheritanceEngine().getResolvedOverrides().get(cfgName);
        List<Node> nodes = new ArrayList<>();

        for (String x : inheritanceList) {
            nodes.add(allNodes.get(x));
        }
        return new CFNode(nodes);
    }


    public static final String LIST_DELIMITERS = " ,;:\\/&!";
    public static final String DEFAULT_PARENT_LIST_DELIMITER = " ";

    /**
     * If the list starts with one of the {@link #LIST_DELIMITERS}, use that
     * otherwise, use a blank. Leading and trailing blanks on entries are ignored.
     * <br/><br/>
     * E.g. a list of
     * <pre>
     * ;A;B;C
     * </pre>
     * means ; is the delimiter. You only need to put the delimeter first
     * if it is not a blank, so "A B C" is fine. Different delimeter let you have
     * embedded blanks etc. E.g. ";mairzy doats;and dozey;daots! and"
     * parses as "mairzey doats", "and dozey", "doats! and"
     *
     * @param rawList
     * @return
     */

    protected List<String> splitParentList(String rawList) {
        List<String> output = new ArrayList<>();
        if (isTrivial(rawList)) {
            return output;
        }
        String delimiter = DEFAULT_PARENT_LIST_DELIMITER;
        if (-1 != LIST_DELIMITERS.indexOf(rawList.substring(0, 1))) {
            delimiter = rawList.substring(0, 1);
        }
        StringTokenizer st = new StringTokenizer(rawList, delimiter);
        while (st.hasMoreTokens()) {
            String r = st.nextToken().trim();
            if (isTrivial(r)) {
                continue;
            }
            output.add(r);
        }
        return output;
    }

    /**
     * Get the very first attribute with the given name found in the nodes.
     * A null is returned if there is no such value.
     *
     * @param node
     * @param name
     * @return
     */
    public static String getFirstAttribute(CFNode node, String name) {
        return node.getFirstAttribute(name);
/*
        for (Node n : node.getNodes()) {
            String x = CFXMLConfigurations.getFirstAttribute(n, name);
            if (!StringUtils.isTrivial(x)) {
                return x;
            }
        }
        return null;
*/
    }

    /**
     * Return the first named child configuration node of the given node or null if there is
     * no such named child. Very useful if your specification only allows for a single child node.
     *
     * @param node
     * @param name
     * @return
     */
    public static CFNode getFirstNode(CFNode node, String name) {
        return node.getFirstNode(name);
/*
        for (Node n : node.getNodes()) {
            NodeList nodeList = n.getChildNodes();
            if (nodeList.getLength() != 0) {
                for (int i = 0; i < nodeList.getLength(); i++) {
                    Node nn = nodeList.item(i);
                    String x = CFXMLConfigurations.getFirstAttribute(nn, name);
                    if (name.equals(x)) {
                        List<Node> rcList = new ArrayList<>();
                        rcList.add(nn);
                        return new CFNode(rcList);
                    }
                }
            }
        }
        return null;
*/
    }

    /**
     * The contract is that this will return all the named
     * children nodes of the given node in order
     * So if the argument is [node0,node1,...] Then result is all the children of node0,
     * followed by all the children of node1,... Note that you can feed the result of
     * this list back in to get the next level of children.
     *
     * @param node
     * @return
     */
    public static CFNode getChildren(CFNode node, String name) {
        return node.getChildren(name);
/*
        List<Node> kids = new ArrayList<>();
        for (Node n : node.getNodes()) {
            NodeList nodeList = n.getChildNodes();
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node nn = nodeList.item(i);
                if (nn.getNodeName().equals(name)) {
                    kids.add(nn);
                }
            }
        }
        return new CFNode(kids);
*/
    }

    /**
     * Use this to get <b>all</b> the named nodes off the list. Rather than overrides, this
     * returns the kitchen sink. This is the equivalent of glomming together all of
     * the like-named nodes into one big virtual node. This also gets other nodes in the configuration
     * too so if there are multiple ones in any configuration, they are added in as well.
     * <p>
     * Sometimes this is necessary.
     *
     * @param node
     * @param name
     * @return
     */
    public static CFNode getAllNodes(CFNode node, String name) {
        return node.getNodes(name);
/*
        List<Node> returnedNodes = new ArrayList<>();
        if (!node.hasNodes()) return new CFNode(returnedNodes);
        for (Node n : node.getNodes()) {
            NodeList nodeList = n.getChildNodes();
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node nn = nodeList.item(i);
                if (nn.getNodeName().equals(name)) {
                    returnedNodes.add(nn);
                }
            }
        }
        return new CFNode(returnedNodes);
*/
    }

    /**
     * Convenience method for getting the value of a single node, i.e. the contents, so
     * <br><br>
     * &lt;foo&gt;value&lt;/foo&gt;<br><br> would have name equal to 'foo' and return the
     * string 'value'. Returns the default value if no such value is found.
     *
     * @param node
     * @param name
     * @param defaultValue
     * @return
     */
    public static String getNodeContents(CFNode node, String name, String defaultValue) {
        return node.getNodeContents(name, defaultValue);
/*
        if (!node.hasNodes()) {
            return defaultValue;
        }
        for (Node n : node.getNodes()) {
            System.out.println(n.getNodeName() + " " + n.getNodeValue());
            NodeList kids = n.getChildNodes();
            for (int i = 0; i < kids.getLength(); i++) {
                Node nn = kids.item(i);
                System.out.println(nn.getNodeName() + " " + nn.getNodeValue() + " text=" + nn.getTextContent());
                if (nn.getNodeName().equals(name)) {
                    String out = nn.getTextContent();
                    if(out != null) {
                        return out;
                    }else{
                        out = nn.getNodeValue();
                        if(out != null) {
                            return out;
                        }
                        return defaultValue;
                    }
                }
            }


            }
        return defaultValue;
*/
    }

    /**
     * Convenience method for getting the value of a single node, i.e. the contents, so
     * <br><br>
     * &lt;foo&gt;value&lt;/foo&gt;<br><br> would have name equal to 'foo' and return the
     * string 'value'. Returns a null if no such value is found.  This is also clever enough
     * to pull things out of CDATA nodes.
     *
     * @param node
     * @param name
     * @return
     */

    public static String getNodeContents(CFNode node, String name) {
        return node.getNodeContents(name);
        //return getNodeContents(node, name, null);
    }

    /**
     * Finds the first attribute with the given name and then converts to boolean.
     * If the conversion fails, the default is returned. This supports values of
     * true, false, on, off.
     *
     * @param node
     * @param attrib
     * @param defaultValue
     * @return
     */
    public static boolean getFirstBooleanValue(CFNode node, String attrib, boolean defaultValue) {
        return node.getFirstBooleanAttribute(attrib, defaultValue);
/*
        if (!node.hasNodes()) return defaultValue;
        try {
            return getFirstBooleanValue(node, attrib);
        } catch (IllegalArgumentException illegalArgumentException) {
            return defaultValue;
        }
*/
    }

    /**
     * Get the first attribute and return a boolean. Note that this supports values of
     * true, false, on and off. If no such value is found, an exception is raised.
     *
     * @param node
     * @param attrib
     * @return
     */
    public static boolean getFirstBooleanValue(CFNode node, String attrib) {
        return node.getFirstBooleanAttribute(attrib);
/*
        if (!node.hasNodes()) throw new IllegalArgumentException("no such node " + attrib);
        for (Node n : node.getNodes()) {
            NamedNodeMap namedNodeMap = n.getAttributes();
            Node nn = namedNodeMap.getNamedItem(attrib);
            if (nn != null) {
                String x = nn.getNodeValue();
                if (isTrivial(x)) {
                    throw new IllegalArgumentException("no value for " + attrib);
                }
                if (x.equalsIgnoreCase("true") || x.equalsIgnoreCase("on")) return true;
                if (x.equalsIgnoreCase("false") || x.equalsIgnoreCase("off")) return false;
                throw new IllegalArgumentException("illegal boolean value \"" + x + "\" for attribute " + attrib);
            }
        }
        throw new IllegalArgumentException("no such node " + attrib);
*/

    }

    public static long getFirstLongValue(CFNode node, String attrib) {
        return node.getFirstLongAttribute(attrib);
/*
        if (!node.hasNodes()) throw new IllegalArgumentException("no such node " + attrib);
        String x = getFirstAttribute(node, attrib);
        if (isTrivial(x)) {
            throw new IllegalArgumentException("no value for " + attrib);
        }
        try {
            return Long.parseLong(x);
        } catch (NumberFormatException nfx) {
            throw new IllegalArgumentException("Could not parse \"" + x + "\" as a long for " + attrib);
        }
*/
    }

    public static long getFirstLongValue(CFNode node, String attrib, long defaultValue) {
        return node.getFirstLongAttribute(attrib, defaultValue);
/*
        if (!node.hasNodes()) return defaultValue;
        try {
            return getFirstLongValue(node, attrib);
        } catch (IllegalArgumentException nfx) {
        }
        return defaultValue;
*/
    }

    /**
     * Looks for the given node and returns the first attribute. This is the method that lets you override
     * a single attribute in a tag.
     *
     * @param node
     * @param nodeName
     * @param attributeName
     * @return
     */
    public static String getAttributeInNode(CFNode node, String nodeName, String attributeName) {
        return node.getAttributeInNode(nodeName, attributeName);
/*
        for (Node n : node.getNodes()) {
            Node node1 = CFXMLConfigurations.getFirstNode(n, nodeName);
            if (node1 != null) {
                NamedNodeMap namedNodeMap = node1.getAttributes();
                for (int i = 0; i < namedNodeMap.getLength(); i++) {
                    Node nn = namedNodeMap.item(i);
                    if (nn.getNodeName().equals(attributeName)) {
                        return nn.getNodeValue();
                    }
                }

            }
        }
        return null;
*/
    }

}
