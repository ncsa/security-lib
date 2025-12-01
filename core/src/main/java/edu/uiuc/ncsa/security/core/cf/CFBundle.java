package edu.uiuc.ncsa.security.core.cf;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import static edu.uiuc.ncsa.security.core.util.StringUtils.isTrivial;

/**
 * Models a bundle of configurations.
 */
public class CFBundle {
    public CFBundle() {
    }

    public CFBundle(Document document, String tagName) {
        this.document = document;
        this.tagName = tagName;
    }

    Document document;

    public Document getDocument() {
        return document;
    }

    public void setDocument(Document document) {
        this.document = document;
    }

    public Node getRootNode() {
        if (rootNode == null) {
            rootNode = document.getDocumentElement();
        }
        return rootNode;
    }

    public void setRootNode(Node rootNode) {
        this.rootNode = rootNode;
    }

    Node rootNode;

    /**
     * The tag name for configuration elements. This must be set to access the correct named
     * configurations.
     *
     * @return
     */
    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    String tagName;


    /**
     * Get all the nodes <i>in the root</i> for the given tag name. This does not use the
     * tag naem in {@link #getTagName()}. Invoking this with a null tag name gets <i>every</i>
     * node in the root.
     *
     * @param tagName
     * @return
     */
    public List<CFNode> getAllNodes(String tagName) {
        NodeList nodeList;
        if (tagName == null) {
            nodeList = ((Element) getRootNode()).getChildNodes();
        } else {
            nodeList = ((Element) getRootNode()).getElementsByTagName(tagName);
        }
        List<CFNode> cfNodes = new ArrayList<>(nodeList.getLength());

        for (int i = 0; i < nodeList.getLength(); i++) {
            cfNodes.add(new CFNode(nodeList.item(i)));
        }

        return cfNodes;
    }

    /**
     * Get all of the nodes in the root with the tag name set in {@link #getTagName()}.
     *
     * @return
     */
    public List<CFNode> getAllNodes() {
        return getAllNodes(getTagName());
    }

    /**
     * Get the configuration with the given name.
     *
     * @param nodeName
     * @return
     */
    public CFNode getNamedConfig(String nodeName) {
        return getMultiConfigurations().getNamedConfig(nodeName);
    }

    public CFMultiConfigurations getMultiConfigurations() {
        if (multiConfigurations == null) {
            multiConfigurations = new CFMultiConfigurations();
            multiConfigurations.ingestConfig(this, getTagName());
        }
        return multiConfigurations;
    }

    CFMultiConfigurations multiConfigurations = null;

    /**
     * For this bundle, get all nodes and return a sorted list '
     * of names. This allows you to display a list of named configurations
     * and have the user select the one the need to load.
     * @return
     */
    public List<String> getAllConfigNames() {
    List<CFNode> nodes = getAllNodes();
    TreeSet<String> names = new TreeSet<>();
    for(CFNode node : nodes) {
        String n = node.getFirstAttribute("name");
        if(!isTrivial(n)) {
            names.add(n);
        }
    }
    return new ArrayList<>(names);
}
}
