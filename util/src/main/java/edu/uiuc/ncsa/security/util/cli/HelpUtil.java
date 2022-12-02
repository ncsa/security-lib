package edu.uiuc.ncsa.security.util.cli;

import edu.uiuc.ncsa.security.core.util.DoubleHashMap;
import edu.uiuc.ncsa.security.core.util.StringUtils;
import org.w3c.dom.CharacterData;
import org.w3c.dom.*;

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
     * Loads the given InputStream containing the help file.
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
                    Node x = eElement.getElementsByTagName("body")
                            .item(0);
                    Node child = x.getFirstChild().getNextSibling();
                    CharacterData cd = (CharacterData) child;
                    if (cd != null && cd.getTextContent() != null) {
                        onlineHelp.put(name, cd.getTextContent());
                    }
                    // Process examples
                    x = eElement.getElementsByTagName("example")
                            .item(0);
                    if (x != null) {
                        child = x.getFirstChild().getNextSibling();
                        cd = (CharacterData) child;
                        if (cd != null && cd.getTextContent() != null) {
                            onlineExamples.put(name, cd.getTextContent());
                        }
                    }
                }
            }
        }
        return true;
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
     * @param inputLine
     * @return
     */
    public boolean printHelp(InputLine inputLine) {
        boolean doOnlineExample = inputLine.hasArg(ONLINE_HELP_EXAMPLE_FLAG);
        inputLine.removeSwitch(ONLINE_HELP_EXAMPLE_FLAG);
        String name;
        if(inputLine.getArgCount() == 0){
            name = ONLINE_HELP_COMMAND; // default, print a listing of all help.
        }else{
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


        if (names != null) {
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
        File f = new File("/home/ncsa/dev/ncsa-git/qdl/language/src/main/resources/func_help.xml");
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
