package edu.uiuc.ncsa.security.core.configuration;

import edu.uiuc.ncsa.security.core.cf.CFXMLConfigurations;
import edu.uiuc.ncsa.security.core.util.FileUtil;
import edu.uiuc.ncsa.security.core.util.StringUtils;
import edu.uiuc.ncsa.security.core.util.TemplateUtility;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static edu.uiuc.ncsa.security.core.cf.CFXMLConfigurations.loadDocument;

/**
 * This class does the work of taking a raw configuration file,
 * possibly with environment elements,
 * snooping the lines for the environment tags, and resolving them
 * It will create the environment for a single document.
 * <h2>Usage</h2>
 * <ol>
 *     <li>Instantiate this class</li>
 *     <li>Invoke {@link #resolveEnvironment(InputStream)} on your configuration file</li>
 *     <li>retrieve the processed file using {@link #getRootDocument()}</li>
 *     <li>If you need the environment, it is in {@link #getEnv()}</li>
 * </ol>
 */
public class EnvironmentProcessor {

    public static final String ENV_TAG = "env";
    public static final String KEY_TAG = "key";
    public static final String INCLUDE_TAG = "include";
    public static final String ENV_START_ELEMENT = "<" + ENV_TAG;
    public static final String ENV_END_ELEMENT = "</" + ENV_TAG + ">";

    /**
     * Does the trimmed line start and environment entry?
     *
     * @param l
     * @return
     */
    protected boolean isStartEnv(String l) {
        return l.startsWith(ENV_START_ELEMENT + " ") // so tag and a space
                ||
                l.startsWith(ENV_START_ELEMENT) && l.length() > ENV_START_ELEMENT.length(); // tag only on line
    }

    /**
     * Does the trimmed line end with an environment end tag?
     *
     * @param l
     * @return
     */
    protected boolean isEndEnv(String l) {
        return l.endsWith(ENV_END_ELEMENT);
    }

    /**
     * The final document when all is said and done.
     *
     * @return
     */
    public Document getRootDocument() {
        return rootDocument;
    }

    public void setRootDocument(Document rootDocument) {
        this.rootDocument = rootDocument;
    }

    Document rootDocument;

    public Map<String, String> getEnv() {
        if (env == null) {
            env = new HashMap<>();
        }
        return env;
    }

    public void setEnv(Map<String, String> env) {
        this.env = env;
    }

    Map<String, String> env = null;

    /**
     * This starts the environment resolution on a file. It will read through the lines
     * for environment tags, collect them then process them. You do not call this
     * on a file with only environment elements in it.
     *
     * @param inputStream
     * @throws Throwable
     */
    public void resolveEnvironment(InputStream inputStream) throws Throwable {
        EnvStack stack = new EnvStack();
        stack.pushNewTable();
        resolveEnvironment(inputStream, stack);
        setEnv(stack.getAsMap());
    }
    protected void resolveEnvironment(InputStream inputStream, EnvStack stack) throws Throwable {
        byte[] buffer = inputStream.readAllBytes();
        ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
        List<String> content = FileUtil.readFileAsLines(bais);
        StringBuilder builder = new StringBuilder();
        builder.append("<" + CFXMLConfigurations.CONFIGURATION_TAG + ">\n");
        boolean readVars = false;
        boolean gotOne = false;
        for (String line : content) {
            line = line.trim();
            if (!readVars && isStartEnv(line)) {
                builder.append(line + "\n");
                gotOne = true; // so we know whether or not any env elements are present
                if (!(isEndEnv(line) || line.endsWith("/>"))) {
                    // so this is NOT a single, well-behaved entry on a single line
                    readVars = true; // continue reading lines until EOL
                }
            }
            if (readVars) {
                builder.append(line + "\n");
                if (isEndEnv(line)) {
                    readVars = false;
                    break;
                }
            }
        }
        if (!gotOne) {
            ByteArrayInputStream processedBA = new ByteArrayInputStream(StringUtils.listToString(content).getBytes(StandardCharsets.UTF_8));
            setRootDocument(loadDocument(processedBA));
            return;
        }
        builder.append("</" + CFXMLConfigurations.CONFIGURATION_TAG + ">"); // we now have bookends and a valid XML document
        System.out.println(builder.toString());
        byte[] envDoc = builder.toString().getBytes(StandardCharsets.UTF_8);
        stack.pushNewTable();
        processEnvDocument(new ByteArrayInputStream(envDoc), stack);
        // Now we are left with processing the rest of the document.

        List<String> newContent = new ArrayList<>(content.size());
        // We already have the entire file as a set of lines. Now we can
        // apply templates
        Map<String,String> stackMap = stack.getAsMap();
        for (String line : content) {
            String newLine = TemplateUtility.replaceAll(line, stackMap);
            System.out.println(newLine);
            newContent.add(newLine);
        }
        // Now convert back to something the XML engine can grok
        System.out.println(StringUtils.listToString(newContent));
        ByteArrayInputStream processedBA = new ByteArrayInputStream(StringUtils.listToString(newContent).getBytes(StandardCharsets.UTF_8));
        stack.pop(); // get rid of local env.
        // now process files
        Document document = loadDocument(processedBA);

    }

    /**
     * Process a single env document from any source. At the end of this, every envionment element
     * will have been processed and added to the current environment {@link #getEnv()}.
     *
     * @param inputStream
     * @return
     * @throws Throwable
     */
    protected void processEnvDocument(InputStream inputStream, EnvStack envStack) throws Throwable {
        Document doc = loadDocument(inputStream);
        doc.getDocumentElement().normalize();
        NodeList nodeList = doc.getDocumentElement().getElementsByTagName(ENV_TAG);
        if (nodeList.getLength() == 0) return; // nix to do
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            NamedNodeMap attrs = node.getAttributes();
            Node keyNode = attrs.getNamedItem(KEY_TAG);
            if (keyNode == null) {
                Node fileNode = attrs.getNamedItem(INCLUDE_TAG);
                if (fileNode != null) {
                    String path = fileNode.getNodeValue();
                    if (!StringUtils.isTrivial(path)) {
                        FileInputStream fis = new FileInputStream(path);
                        processEnvDocument(fis, envStack); // don't push a new table for include
                        fis.close();
                    }
                }
            } else {
                String key = keyNode.getNodeValue();
                String value = node.getTextContent();
                envStack.localPut(new EnvEntry(key, value));
            }
        }
    }

}
