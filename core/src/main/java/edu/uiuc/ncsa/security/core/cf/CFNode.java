package edu.uiuc.ncsa.security.core.cf;

import edu.uiuc.ncsa.security.core.util.StringUtils;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static edu.uiuc.ncsa.security.core.util.StringUtils.isTrivial;

/**
 * A node in a configuration. Note that if there is inheritance, this will be backed by
 * mutliple {@link Node}s, otherwise just a single one.
 */
public class CFNode {
    public CFNode() {
    }


    public int size() {
        if (!hasNodes()) return 0;
        return nodes.size();
    }

    public CFNode(Node node) {
        this.nodes = new ArrayList<Node>(1);
        this.nodes.add(node);
    }

    public CFNode(List<Node> nodes) {
        this.nodes = nodes;
    }

    public List<Node> getNodes() {
        return nodes;
    }

    public void setNodes(List<Node> nodes) {
        this.nodes = nodes;
    }

    public boolean hasNodes() {
        return nodes != null && !nodes.isEmpty();
    }

    List<Node> nodes;

    public String getName() {
        if (!hasNodes()) return null;
        return nodes.get(0).getNodeName();
    }

    /**
     * Get the very first attribute with the given name found in the nodes.
     * A null is returned if there is no such value.
     *
     * @param name
     * @return
     */
    public String getFirstAttribute(String name) {
        for (Node n : getNodes()) {
            String x = CFXMLConfigurations.getFirstAttribute(n, name);
            if (!StringUtils.isTrivial(x)) {
                return x;
            }
        }
        return null;
    }

    /**
     * Returns all attributes as strings.
     *
     * @param name
     * @return
     */
    public List<String> getAttributes(String name) {
        List<String> attrs = new ArrayList<>();
        for (Node n : getNodes()) {
            String x = CFXMLConfigurations.getFirstAttribute(n, name);
            if (!StringUtils.isTrivial(x)) {
                attrs.add(x);
            }
        }
        return attrs;
    }

    /**
     * Return <i>all</i> the attributes as a map. You must convert these to whatever they
     * are supposed to be. Note that this does resolve overrides.
     *
     * @return
     */
    public Map<String, String> getAttributes() {
        Map<String, String> map = new HashMap<>();
        for (Node n : getNodes()) {
            NamedNodeMap namedNodeMap = n.getAttributes();
            for (int i = 0; i < namedNodeMap.getLength(); i++) {
                Node attribute = namedNodeMap.item(i);
                if (attribute != null) {
                    map.put(attribute.getNodeName(), attribute.getNodeValue());
                /*    System.out.println(getClass().getSimpleName() + " attribute Name: " + attribute.getNodeName() +
                            ", Value: " + attribute.getNodeValue());*/
                }
            }
        }
        return map;
    }

    /**
     * Returns all the long values for the attribute. This does not flag errors,
     * it just skips them.
     *
     * @param name
     * @return
     */
    public List<Long> getLongAttributes(String name) {
        List<Long> attrs = new ArrayList<>();
        for (Node n : getNodes()) {
            String x = CFXMLConfigurations.getFirstAttribute(n, name);
            if (!StringUtils.isTrivial(x)) {
                try {
                    attrs.add(Long.parseLong(x));
                } catch (NumberFormatException e) {

                }
            }
        }
        return attrs;
    }


    /**
     * returns the boolean values for the attribute. It does not flag
     * bad ones, it just skips them.
     *
     * @param name
     * @return
     */
    public List<Boolean> getBooleanAttributes(String name) {
        List<Boolean> attrs = new ArrayList<>();
        for (Node n : getNodes()) {
            String x = CFXMLConfigurations.getFirstAttribute(n, name);
            if (!StringUtils.isTrivial(x)) {
                Boolean b = StringUtils.toBoolean(x);
                if (b != null) attrs.add(b);
            }
        }
        return attrs;
    }

    /**
     * Return the first named child configuration node of the given node or null if there is
     * no such named child. Very useful if your specification only allows for a single child node.
     *
     * @param name
     * @return
     */
    public CFNode getFirstNode(String name) {
        for (Node n : getNodes()) {
            NodeList nodeList = n.getChildNodes();
            if (nodeList.getLength() != 0) {
                for (int i = 0; i < nodeList.getLength(); i++) {
                    Node nn = nodeList.item(i);
                    if (nn.getNodeName().equals(name)) {
                        List<Node> rcList = new ArrayList<>();
                        rcList.add(nn);
                        return new CFNode(rcList);
                    }
                }
            }
        }
        return null;
    }

    /**
     * The contract is that this will return all the named
     * children nodes of the given node in order
     * So if the argument is [node0,node1,...] Then result is all the children of node0,
     * followed by all the children of node1,... Note that you can feed the result of
     * this list back in to get the next level of children.
     *
     * @return
     */
    public List<CFNode> getChildren(String name) {
        List<CFNode> kids = new ArrayList<>();
        for (Node n : getNodes()) {
            NodeList nodeList = n.getChildNodes();
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node nn = nodeList.item(i);
                if (nn.getNodeName().equals(name)) {
                    kids.add(new CFNode(nn));
                }
            }
        }
        return kids;
    }

    /**
     * Get <b>all</b> child nodes.
     *
     * @return
     */
    public List<CFNode> getChildren() {
        List<CFNode> kids = new ArrayList<>();
        for (Node n : getNodes()) {
            NodeList nodeList = n.getChildNodes();
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node nn = nodeList.item(i);
                kids.add(new CFNode(nn));
            }
        }
        return kids;
    }

    /**
     * Synonym for {@link #getNodeContents()}
     * @return
     */
    public String getValue(){
        return getNodeContents();
    }
    /**
     * Convenience method to get the zero-th node or null if there is none. In many cases you know
     * there is exactly a single node and you just want that.
     *
     * @param name
     * @return
     */
    public CFNode getFirstChild(String name) {
        List<CFNode> nodes = getChildren(name);
        if (nodes.size() == 0) return null;
        return nodes.get(0);
    }

    /**
     * Use this to get <b>all</b> the named nodes off the list. Rather than overrides, this
     * returns the kitchen sink. This is the equivalent of glomming together all of
     * the like-named nodes into one big virtual node. This also gets other nodes in the configuration
     * too so if there are multiple ones in any configuration, they are added in as well.
     * <p>
     * Sometimes this is necessary.
     *
     * @param name
     * @return
     */
    public CFNode getNodes(String name) {
        List<Node> returnedNodes = new ArrayList<>();
        if (!hasNodes()) return new CFNode(returnedNodes);
        for (Node n : getNodes()) {
            NodeList nodeList = n.getChildNodes();
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node nn = nodeList.item(i);
                if (nn.getNodeName().equals(name)) {
                    returnedNodes.add(nn);
                }
            }
        }
        return new CFNode(returnedNodes);
    }

    /**
     * Convenience method for getting the value of a single named child node, i.e. the contents, so
     * <br><br>
     * &lt;foo&gt;value&lt;/foo&gt;<br><br> would have name equal to 'foo' and return the
     * string 'value'. Returns the default value if no such value is found.
     *
     * @param name
     * @param defaultValue
     * @return
     */
    public String getNodeContents(String name, String defaultValue) {
        if (!hasNodes()) {
            return defaultValue;
        }
        for (Node n : getNodes()) {
            //   System.out.println(n.getNodeName() + " " + n.getNodeValue());
            NodeList kids = n.getChildNodes();
            for (int i = 0; i < kids.getLength(); i++) {
                Node nn = kids.item(i);
                //     System.out.println(nn.getNodeName() + " " + nn.getNodeValue() + " text=" + nn.getTextContent());
                if (nn.getNodeName().equals(name)) {
                    String out = nn.getTextContent();
                    if (out != null) {
                        return out;
                    } else {
                        out = nn.getNodeValue();
                        if (out != null) {
                            return out;
                        }
                        return defaultValue;
                    }
                }
            }

        }
        return defaultValue;
    }

    /**
     * Get the contents of the current node.
     *
     * @return
     */
    public String getNodeContents() {
        for (Node n : getNodes()) {
            // System.out.println(n.getNodeName() + " " + n.getNodeValue());
            NodeList kids = n.getChildNodes();
            for (int i = 0; i < kids.getLength(); i++) {
                Node nn = kids.item(i);
                String out = nn.getTextContent();
                if (!StringUtils.isTrivial(out)) {
                    return out;
                } else {
                    out = nn.getNodeValue();
                    if (!StringUtils.isTrivial(out)) {
                        return out;
                    } else {
                        return null;
                    }
                }
            }

        }
        return null;
    }


    /**
     * Convenience method for getting the value of a single node, i.e. the contents, so
     * <br><br>
     * &lt;foo&gt;value&lt;/foo&gt;<br><br> would have name equal to 'foo' and return the
     * string 'value'. Returns a null if no such value is found.  This is also clever enough
     * to pull things out of CDATA nodes.
     *
     * @param name
     * @return
     */

    public String getNodeContents(String name) {
        return getNodeContents(name, null);
    }

    /**
     * Finds the first attribute with the given name and then converts to boolean.
     * If the conversion fails, the default is returned. This supports values of
     * true, false, on, off.
     *
     * @param attrib
     * @param defaultValue
     * @return
     */
    public boolean getFirstBooleanValue(String attrib, boolean defaultValue) {
        if (!hasNodes()) return defaultValue;
        try {
            return getFirstBooleanValue(attrib);
        } catch (IllegalArgumentException illegalArgumentException) {
            return defaultValue;
        }
    }


    /**
     * Get the first attribute and return a boolean. Note that this supports values of
     * {@link StringUtils#LOGICAL_TRUES} and {@link StringUtils#LOGICAL_FALSES}.
     * If no such value is found, an error is raised, since use of this method
     * asserts the value is a boolean.
     *
     * @param attrib
     * @return
     */
    public Boolean getFirstBooleanValue(String attrib) {
        if (!hasNodes()) return null;
        for (Node n : getNodes()) {
            NamedNodeMap namedNodeMap = n.getAttributes();
            Node nn = namedNodeMap.getNamedItem(attrib);
            if (nn != null) {
                String x = nn.getNodeValue();
                if (isTrivial(x)) {
                    return null;
                }
                Boolean b = StringUtils.toBoolean(x);
                if (b != null) return b;
                throw new IllegalArgumentException("Could not parse " + x + " as a long");
            }
        }
        throw new IllegalArgumentException("No such value for attribute >" + attrib + "<");
    }

    /**
     * Return the value as a long {@link Long}. If no such value, a null is returned. If
     * the value fails to parse as  a long, an illegal argument exception is raised
     *
     * @param attrib
     * @return
     */
    public Long getFirstLongAttribute(String attrib) {
        if (!hasNodes()) return null;
        List<String> x = getAttributes(attrib);
        if (x.isEmpty()) {
            return null;
        }
        String s = x.get(0);
        try {
            return Long.parseLong(s);
        } catch (NumberFormatException nfx) {
            throw new IllegalArgumentException("Could not parse " + s + " as a long for attribute >" + attrib + "<");
        }
    }

    /**
     * Get the first attribute that is a long. If no such attribute, return the default value.
     *
     * @param attrib
     * @param defaultValue
     * @return
     */
    public long getFirstLongAttribute(String attrib, long defaultValue) {
        if (!hasNodes()) return defaultValue;
        try {
            return getFirstLongAttribute(attrib);
        } catch (IllegalArgumentException nfx) {
        }
        return defaultValue;
    }

    /**
     * Looks for the given node and returns the first attribute. This is the method that lets you override
     * a single attribute in a tag.
     *
     * @param nodeName
     * @param attributeName
     * @return
     */
    public String getAttributeInNode(String nodeName, String attributeName) {
        for (Node n : getNodes()) {
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
    }

}
