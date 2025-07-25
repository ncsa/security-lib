package edu.uiuc.ncsa.security.core.configuration;

import edu.uiuc.ncsa.security.core.exceptions.MyConfigurationException;
import edu.uiuc.ncsa.security.core.inheritance.InheritanceList;
import edu.uiuc.ncsa.security.core.inheritance.InheritanceMap;
import edu.uiuc.ncsa.security.core.inheritance.MultipleInheritanceEngine;
import org.apache.commons.configuration.SubnodeConfiguration;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.configuration.tree.ConfigurationNode;

import java.util.*;

import static edu.uiuc.ncsa.security.core.util.StringUtils.isTrivial;

/**
 * Configuration Utility that allows for inheritance from other configurations
 * For testing this see the MultipleInheritanceTest in the testing harness, which exercises
 * this very well.
 * <p>Created by Jeff Gaynor<br>
 * on 1/31/21 at  4:59 PM
 */
public class MultiConfigurations implements edu.uiuc.ncsa.security.core.MultiConfigurationsInterface{
    public static final String EXTENDS_TAG = "extends";
    public static final String NAME_TAG = "name";
    public static final String ALIAS_TAG = "alias";


    Map<String, ConfigurationNode> allNodes = new HashMap<>();
    InheritanceMap namedNodes = new InheritanceMap();
    InheritanceMap unresolvedAliases = new InheritanceMap(); // alias + node
    InheritanceMap unresolvedOverrides = new InheritanceMap(); // name  + list

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
    public void ingestConfig(XMLConfiguration cfg,
                             String topNodeTag // the tag of elements to look at, e.g. service, client, qdl
    ) {

        List list = cfg.configurationsAt(topNodeTag);

        for (int i = 0; i < list.size(); i++) {
            SubnodeConfiguration cn = (SubnodeConfiguration) list.get(i);
            String name = Configurations.getFirstAttribute(cn.getRootNode(), NAME_TAG);
            if (isTrivial(name)) {
                throw new MyConfigurationException("Missing name. The configuration does not contain a name. Configuration load is aborted.");
            }
            if (allNodes.containsKey(name)) {
                throw new MyConfigurationException("Duplicated name \"" + name + "\"  Configuration load is aborted.");
            }
            allNodes.put(name, cn.getRootNode());
            String alias = Configurations.getFirstAttribute(cn.getRootNode(), ALIAS_TAG);
            String parents = Configurations.getFirstAttribute(cn.getRootNode(), EXTENDS_TAG);

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
     * This being XML, it is possible to have elements with the same name -- stylistically very bad
     * and no config should do that, but it is unavoidable. Therefore, this will return all the nodes
     * with the given name and generally the calling program should either throw an exception if there
     * is more than one or have some strategy for dealing with multiples.
     *
     * @param cfgName
     * @return
     */
    public List<ConfigurationNode> getNamedConfig(String cfgName) {
        // everything has been resolved.
        // Get the list of names to return
        if (!getInheritanceEngine().isResolutionsRun()) {
            throw new MyConfigurationException("No configurations have been resolved.. Configuration load is aborted.");
        }

        if (!getInheritanceEngine().getResolvedOverrides().containsKey(cfgName)) {
            throw new MyConfigurationException("Configuration \"" + cfgName + "\" not found. Configuration load is aborted.");
        }

        InheritanceList inheritanceList = getInheritanceEngine().getResolvedOverrides().get(cfgName);
        List<ConfigurationNode> nodes = new ArrayList<>();

        for (String x : inheritanceList) {
            nodes.add(allNodes.get(x));
        }
        return nodes;
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
     * @param nodes
     * @param name
     * @return
     */
    public String getFirstAttribute(List<ConfigurationNode> nodes, String name) {
        for (ConfigurationNode node : nodes) {
            List list = node.getAttributes(name);
            if (!list.isEmpty()) {
                ConfigurationNode cn = (ConfigurationNode) list.get(0);
                return cn.getValue().toString();
            }
        }
        return null;
    }

    /**
     * Return the first named child configuration node of the given node or null if there is
     * no such named child. Very useful if your specification only allows for a single child node.
     *
     * @param nodes
     * @param name
     * @return
     */
    public ConfigurationNode getFirstNode(List<ConfigurationNode> nodes, String name) {
        for (ConfigurationNode node : nodes) {
            List list = node.getChildren(name);
            if (!list.isEmpty()) {
                return ((ConfigurationNode) list.get(0));
            }
        }
        return null;
    }

    /**
     * The contract is that this will return all the named https://localhost:9443/oauth2/authorize?scope=org.cilogon.userinfo+openid+profile+email+storage.read%3A%2F+wlcg.capabilityset%3A%2Fduneana+wlcg.groups&response_type=code&redirect_uri=https%3A%2F%2Flocalhost%3A9443%2Fclient2%2Fready&state=vpfVnb1M4VfybbmZP3gCj724wahA0bYUJk2kbtmDy1k&nonce=kH9WLyty1xQ2EyMHjLz1MGgPZmDg1TFL-RORH7-2pC8&prompt=login&client_id=localhost%3Atest%2Ffnal&skin=dataOne
     * hildren nodes of the given node in order
     * So if the argument is [node0,node1,...] Then result is all the children of node0,
     * followed by all the children of node1,... Note that you can feed the result of
     * this list back in to get the next level of children.
     * @param nodes
     * @return
     */
    public List<ConfigurationNode> getChildren(List<ConfigurationNode> nodes, String name) {
       List<ConfigurationNode> kids = new ArrayList<>();
        for (ConfigurationNode node : nodes) {
            List list = node.getChildren(name);
            kids.addAll(list);
        }
        return kids;
    }

    /**
     * Use this to get <b>all</b> the named nodes off the list. Rather than overrides, this
     * returns the kitchen sink. This is the equivalent of glomming together all of
     * the like-named nodes into one big virtual node. This also gets other nodes in the configuration
     * too so if there are multiple ones in any configuration, they are added in as well.
     * <p>
     * Sometimes this is necessary.
     *
     * @param nodes
     * @param name
     * @return
     */
    public List<ConfigurationNode> getAllNodes(List<ConfigurationNode> nodes, String name) {
        List<ConfigurationNode> returnedNodes = new ArrayList<>();
        if (nodes.isEmpty()) return returnedNodes;
        for (ConfigurationNode node : nodes) {
            List list = node.getChildren(name);
            if (!list.isEmpty()) {
                returnedNodes.addAll(list);
            }

        }
        return returnedNodes;
    }

    /**
     * Convenience method for getting the value of a single node, i.e. the contents, so
     * <br><br>
     * &lt;foo&gt;value&lt;/foo&gt;<br><br> would have name equal to 'foo' and return the
     * string 'value'. Returns the default value if no such value is found.
     *
     * @param nodes
     * @param name
     * @param defaultValue
     * @return
     */
    public String getNodeContents(List<ConfigurationNode> nodes, String name, String defaultValue) {
        for (ConfigurationNode node : nodes) {
            ConfigurationNode node1 = Configurations.getFirstNode(node, name);
            if (node1 != null) {
                return node1.getValue().toString();
            }
        }
        return defaultValue;
    }

    /**
     * Convenience method for getting the value of a single node, i.e. the contents, so
     * <br><br>
     * &lt;foo&gt;value&lt;/foo&gt;<br><br> would have name equal to 'foo' and return the
     * string 'value'. Returns a null if no such value is found.  This is also clever enough
     * to pull things out of CDATA nodes.
     *
     * @param nodes
     * @param name
     * @return
     */

    public String getNodeContents(List<ConfigurationNode> nodes, String name) {
        return getNodeContents(nodes, name, null);
    }

    /**
     * Finds the first attribute with the given name and then converts to boolean.
     * If the conversion fails, the default is returned. This supports values of
     * true, false, on, off.
     *
     * @param nodes
     * @param attrib
     * @param defaultValue
     * @return
     */
    public boolean getFirstBooleanValue(List<ConfigurationNode> nodes, String attrib, boolean defaultValue) {
        if (nodes.isEmpty()) return defaultValue;
        try {
            return getFirstBooleanValue(nodes, attrib);
        } catch (IllegalArgumentException illegalArgumentException) {
            return defaultValue;
        }
    }

    /**
     * Get the first attribute and return a boolean. Note that this supports values of
     * true, false, on and off. If no such value is found, an exception is raised.
     *
     * @param nodes
     * @param attrib
     * @return
     */
    public boolean getFirstBooleanValue(List<ConfigurationNode> nodes, String attrib) {
        if (nodes.isEmpty()) throw new IllegalArgumentException("no such node " + attrib);
        String x = getFirstAttribute(nodes, attrib);
        if (isTrivial(x)) {
            throw new IllegalArgumentException("no value for " + attrib);
        }
        if (x.equalsIgnoreCase("true") || x.equalsIgnoreCase("on")) return true;
        if (x.equalsIgnoreCase("false") || x.equalsIgnoreCase("off")) return false;
        throw new IllegalArgumentException("illegal boolean value \"" + x + "\" for attribute " + attrib);
    }

    public long getFirstLongValue(List<ConfigurationNode> nodes, String attrib) {
        if (nodes.isEmpty()) throw new IllegalArgumentException("no such node " + attrib);
        String x = getFirstAttribute(nodes, attrib);
        if (isTrivial(x)) {
            throw new IllegalArgumentException("no value for " + attrib);
        }
        try{
            return Long.parseLong(x);
        }catch(NumberFormatException nfx){
             throw new IllegalArgumentException("Could not parse \"" + x + "\" as a long for " + attrib);
        }
    }

    public long getFirstLongValue(List<ConfigurationNode> nodes, String attrib, long defaultValue) {
        if (nodes.isEmpty()) return defaultValue;
        try{
            return getFirstLongValue(nodes, attrib);
        }catch(IllegalArgumentException nfx){
        }
        return defaultValue;
    }
    /**
     * Looks for the given node and returns the first attribute. This is the method that lets you override
     * a single attribute in a tag.
     *
     * @param nodes
     * @param nodeName
     * @param attributeName
     * @return
     */
    public String getAttributeInNode(List<ConfigurationNode> nodes, String nodeName, String attributeName) {
        for (ConfigurationNode node : nodes) {
            ConfigurationNode node1 = Configurations.getFirstNode(node, nodeName);
            if (node1 != null) {
                List<ConfigurationNode> attrs = node1.getAttributes(attributeName);
                if (!attrs.isEmpty()) {
                    ConfigurationNode cn = (ConfigurationNode) attrs.get(0);
                    Object object = cn.getValue();
                    if (object != null) {
                        return object.toString();
                    }
                }
            }
        }
        return null;
    }

}
