package edu.uiuc.ncsa.security.util.cli;

import edu.uiuc.ncsa.security.core.util.DebugUtil;
import edu.uiuc.ncsa.security.core.util.DoubleHashMap;
import edu.uiuc.ncsa.security.core.util.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

import static edu.uiuc.ncsa.security.util.cli.FormatUtil.say;

/**
 * A very simple help facility for CLIs. This takes an HTML help file and turns it into
 * help entries. It then has a {@link #printHelp(InputLine)} that will format and print
 * out the help entries.
 * <p>Created by Jeff Gaynor<br>
 * on 11/26/22 at  5:47 AM
 */
public class HelpUtil {
    public Map<String, String> getOnlineHelp() {
        return onlineHelp;
    }

    public void setOnlineHelp(Map<String, String> onlineHelp) {
        this.onlineHelp = onlineHelp;
    }

    Map<String, String> onlineHelp = new HashMap<>();

    public Map<String, String> getOnlineExamples() {
        return onlineExamples;
    }

    public void setOnlineExamples(Map<String, String> onlineExamples) {
        this.onlineExamples = onlineExamples;
    }

    /**
     * Take <b>all</b> the state and entries from another help util and copy them.
     * This allows you to have a base set of help and extend it, so add the help
     * first, then do an loading that is needed for specific help.
     *
     * @param helpUtil
     */
    public void addHelp(HelpUtil helpUtil) {
        if (helpUtil.getAltLookup() != null) {
            for (String key : helpUtil.getAltLookup().keySet()) {
                if (!getAltLookup().containsKey(key)) {
                    getAltLookup().put(key, helpUtil.getAltLookup().get(key));
                }
            }
        }
        if (helpUtil.getOnlineHelp() != null) {
            for (String key : helpUtil.getOnlineHelp().keySet()) {
                if (!getOnlineHelp().containsKey(key)) {
                    getOnlineHelp().put(key, helpUtil.getOnlineHelp().get(key));
                }
            }
        }
        setReverseCharLookupMap(helpUtil.getReverseCharLookupMap());
    }

    /**
     * This is a mop with key being the topic, value being the alternative, e.g.
     * has_value vs. ∈ in QDL.
     *
     * @return
     */
    public DoubleHashMap<String, String> getAltLookup() {
        return altLookup;
    }

    public void setAltLookup(DoubleHashMap<String, String> altLookup) {
        this.altLookup = altLookup;
    }

    Map<String, String> onlineExamples = new HashMap<>();

    DoubleHashMap<String, String> altLookup = new DoubleHashMap<>();
    public static final String ONLINE_HELP_EXAMPLE_FLAG = "-ex";
    public static final String ONLINE_HELP_COMMAND = "online";

    /**
     * Loads the given resource, e.g. "/func_help.xml" from the resources folder.
     *
     * @param helpFile
     * @return
     * @throws Throwable
     */
    public boolean load(String helpFile) throws Throwable {
        return load(getClass().getResourceAsStream(helpFile));
    }

    /**
     * Loads the given InputStream containing the help file. Note that existing entries will
     * be overwritten, allowing for overriding entries.
     *
     * @param helpStream
     * @return
     * @throws Throwable
     */
    public boolean load(InputStream helpStream) throws Throwable {
        if (helpStream == null) {
            return false;
        } else {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(helpStream);
            doc.getDocumentElement().normalize();
            NodeList nList = doc.getElementsByTagName("entry");
            for (int temp = 0; temp < nList.getLength(); temp++) {
                Node nNode = nList.item(temp);
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;
                    String name = eElement.getAttribute("id");
                    String altName = eElement.getAttribute("alt");
                    if (!StringUtils.isTrivial(altName)) {
                        altLookup.put(name, altName);
                    }
                    Node x = eElement.getElementsByTagName("body").item(0);
                    String content = getNodeContents(x);
                    if (content != null) {
                        onlineHelp.put(name, content);
                    }
                    // Process examples
                    x = eElement.getElementsByTagName("example").item(0);
                    content = getNodeContents(x);
                    if (content != null) {
                        onlineExamples.put(name, content);
                    }
                }
            }
        }
        return true;
    }

    /**
     * Root around for the actual content of the node if there is text, ignoring nodes that are
     * whitespace only.
     * @param node
     * @return
     */
    protected String getNodeContents(Node node) {
        // Fixes https://github.com/ncsa/security-lib/issues/60
        if (node == null) {
            return null;
        }
        NodeList kids = node.getChildNodes();
        for (int i = 0; i < kids.getLength(); i++) {
            Node nn = kids.item(i);
            String out = nn.getTextContent();
            if (!StringUtils.isTrivial(out)) {
                System.out.println("out1: " + out);
                return out;
            } else {
                out = nn.getNodeValue();
                if (!StringUtils.isTrivial(out)) {
                    System.out.println("out2: " + out);
                    return out;
                }
            }
        }
        return null;
    }

    protected String[] resolveRealHelpName(String text) {
        String realName = null;
        String altName = null;
        if (onlineHelp.containsKey(text) || altLookup.getByValue(text) != null) { // show single topic
            if (onlineHelp.containsKey(text)) {
                realName = text;
                altName = altLookup.get(realName);
            } else {
                altName = text;
                realName = altLookup.getByValue(text);
            }
            return new String[]{realName, altName};
        }
        return null;
    }

    /**
     * Returns true if there were results printed, false otherwise.
     *
     * @param inputLine
     * @return
     */
    public boolean printHelp(InputLine inputLine) {
        boolean doOnlineExample = inputLine.hasArg(ONLINE_HELP_EXAMPLE_FLAG);
        inputLine.removeSwitch(ONLINE_HELP_EXAMPLE_FLAG);
        String name;
        if (inputLine.getArgCount() == 0) {
            name = ONLINE_HELP_COMMAND; // default, print a listing of all help.
        } else {
            name = inputLine.getArg(1);
        }
        boolean isRegex = inputLine.hasArg("-r");
        if (name.equals(ONLINE_HELP_COMMAND)) {  // show every topic
            TreeSet<String> treeSet = new TreeSet<>();
            // For display in full listing
            for (String key : onlineHelp.keySet()) {
                if (altLookup.containsKey(key)) {
                    treeSet.add(key + " (" + altLookup.get(key) + ")");
                } else {
                    treeSet.add(key);
                }
            }
            //treeSet.addAll(onlineHelp.keySet());
            if (treeSet.isEmpty()) {
                return false;
            }
            // if it's a regex, we have no idea what the display function will do, so don't have a count.
            say("Help is available for the following " + (isRegex ? "" : treeSet.size()) + " topics:");
            FormatUtil.formatList(inputLine, treeSet);
        }
        String[] names = resolveRealHelpName(name);


        if (names == null) {
            return false; // nothing to show
        } else {
            String realName = names[0];
            if (doOnlineExample) {
                String x = getHelpTopicExample(realName);
                if (x == null) {
                    return false;
                } else {
                    say(x);
                }

            } else {
                String x = getHelpTopic(realName);
                if (x == null) {
                    //say("no help for " + name);
                    return false;
                } else {
                    say(onlineHelp.get(realName));
                    if (onlineExamples.containsKey(realName)) {
                        say("use -ex to see examples for this topic.");
                    }
                }
            }
        }
        return true;
    }

    /**
     * Use this to get an example for a topic. It returns null if no such example.
     *
     * @param text
     * @return
     */
    public String getHelpTopicExample(String text) {
        String[] names = resolveRealHelpName(text);
        if (names == null) {
            return null;
        }
        if (onlineExamples.containsKey(names[0])) {
            return onlineExamples.get(names[0]);
        }
        return null;
    }

    /**
     * Use this to get help for a topic. It returns null if no such topic.
     *
     * @param text
     * @return
     */
    public String getHelpTopic(String text) {
        String[] names = resolveRealHelpName(text);
        if (names == null) {
            return null;
        }
        String realName = names[0];
        String altName = names[1];
        // now we have the real name (main entry name) and the alt name.
        String out = onlineHelp.get(realName);
        if (altName != null && getReverseCharLookupMap() != null) {
            String altKey = null;
            if (getReverseCharLookupMap().containsKey(altName)) {
                altKey = getReverseCharLookupMap().get(altName);
            }
            out = out + "\n" + "unicode: " + altName + " (" + StringUtils.toUnicode(altName) + ")" + (altKey == null ? "" : ", alt + " + altKey);
        }
        return out;
    }

    /**
     * This is a map that allows you to specify special characters as alternates for help topics.
     * E.g. "∈" for "has_a". Normally (e.g. QDL) this is done in the terminal where this map is
     * constructed, then you set a pointer to it here.
     *
     * @return
     */

    public static Map<String, String> getReverseCharLookupMap() {
        return reverseCharLookupMap;
    }

    public static void setReverseCharLookupMap(Map<String, String> reverseCharLookupMap) {
        HelpUtil.reverseCharLookupMap = reverseCharLookupMap;
    }

    static Map<String, String> reverseCharLookupMap = null;

    public static void main(String[] args) throws Throwable {
        // Example of a very large, full-featured help file that works. Testing only.
        File f = new File(DebugUtil.getDevPath() + "/qdl/language/src/main/resources/func_help.xml");
        FileInputStream fis = new FileInputStream(f);
        HelpUtil helpUtil = new HelpUtil();
        helpUtil.load(fis);
        fis.close();
        InputLine inputLine = new InputLine("/help online");
        helpUtil.printHelp(inputLine);
        inputLine = new InputLine("/help script_args");
        helpUtil.printHelp(inputLine);
    }
}
