package edu.uiuc.ncsa.security.core.cf;

import edu.uiuc.ncsa.security.core.configuration.EnvEntry;
import edu.uiuc.ncsa.security.core.configuration.EnvStack;
import edu.uiuc.ncsa.security.core.configuration.EnvTable;
import edu.uiuc.ncsa.security.core.exceptions.CircularReferenceException;
import edu.uiuc.ncsa.security.core.exceptions.MyConfigurationException;
import edu.uiuc.ncsa.security.core.inheritance.CyclicalError;
import edu.uiuc.ncsa.security.core.state.SKey;
import edu.uiuc.ncsa.security.core.util.DebugUtil;
import edu.uiuc.ncsa.security.core.util.FileUtil;
import edu.uiuc.ncsa.security.core.util.StringUtils;
import edu.uiuc.ncsa.security.core.util.TemplateUtility;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static edu.uiuc.ncsa.security.core.cf.CFXMLConfigurations.*;

public class CFLoader {
    public static final String FILE_TAG = "file";
    public static final String INCLUDE_TAG = "include";
    public static final String ENV_TAG = "env";
    public static final String KEY_TAG = "key";
    public static final String ENV_START_ELEMENT = "<" + ENV_TAG;
    public static final String ENV_END_ELEMENT = "</" + ENV_TAG + ">";

    static boolean DEBUG_ON = false;
    void say(String message) {if(DEBUG_ON) System.out.println(message);}

    /**
     * Loads the specified resource as a stream.
     * @param resource
     * @param tagName
     * @return
     * @throws MyConfigurationException
     * @throws CircularReferenceException
     */
    public   CFBundle loadBundle(String resource, String tagName) throws MyConfigurationException, CircularReferenceException {
        return loadBundle(getClass().getClassLoader().getResourceAsStream(resource), tagName);
    }

    /**
     * Loads the specified file.
     * @param file
     * @param tagName
     * @return
     * @throws MyConfigurationException
     * @throws CircularReferenceException
     */
    public   CFBundle loadBundle(File file, String tagName) throws MyConfigurationException, CircularReferenceException {
        if (file == null) {
            throw new MyConfigurationException("Error: no configuration file");
        }
        if (!file.exists()) {
            throw new MyConfigurationException("Error: file \"" + file.getAbsolutePath() + "\" does not exist");
        }
        if (!file.isFile()) {
            throw new MyConfigurationException("Error: \"" + file.getAbsolutePath() + "\" is not a file");
        }
        try {
            FileInputStream fis = new FileInputStream(file);
            CFBundle cfBundle = loadBundle(fis, tagName, new EnvStack(), new HashSet<>());
            fis.close();
            return cfBundle;
        }catch(MyConfigurationException | CircularReferenceException e) {
            throw e;
        }catch(Exception e) {
            throw new MyConfigurationException("Cannot load file:"+e.getMessage(), e);
        }
    }

    /**
     * Main entry point for loading a configuration. This resolves all environment variables and
     * file import, then parses the result as (valid) XML.
     * @param inputStream
     * @return
     * @throws MyConfigurationException
     */
    public   CFBundle loadBundle(InputStream inputStream, String tagName) throws MyConfigurationException, CircularReferenceException {
        return loadBundle(inputStream, tagName, new EnvStack(), new HashSet<>());
    }

    /**
     * Loads a configuration. This will also have the environment and
     * @param inputStream
     * @param loadedFiles
     * @return
     * @throws MyConfigurationException
     */
    protected   CFBundle loadBundle(InputStream inputStream, String tagName, EnvStack envStack, Set<String> loadedFiles) throws MyConfigurationException, CircularReferenceException {
        try {
            Document doc = resolveEnvironment(inputStream, envStack, loadedFiles, true);
            resolveFileReferences(doc, doc.getDocumentElement(), envStack, loadedFiles);
            return new CFBundle(doc, tagName);
        }catch(MyConfigurationException | CircularReferenceException mrx){
            throw mrx;
        }catch (Throwable t) {
            throw new MyConfigurationException("could not load configuration", t);
        }
    }

    /**
     * Returns a valid XML document with the environment references done.
     * @param inputStream
     * @param stack
     * @return
     * @throws Throwable
     */
    protected   Document resolveEnvironment(InputStream inputStream,
                                                 EnvStack stack,
                                                 Set<String> loadedFiles,
                                                 boolean isRoot) throws Throwable {
        byte[] buffer = inputStream.readAllBytes();
        ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
        List<String> rawEnvDoc = FileUtil.readFileAsLines(bais);
        List<String> noEnvDoc = new ArrayList<>();
        say("\n------- Start Resolve Env ----------------------");
        say("resolveEnv, raw file:" + rawEnvDoc);
        StringBuilder builder = new StringBuilder();

        builder.append("<" + CFXMLConfigurations.CONFIGURATION_TAG + ">\n");
        boolean readVars = false;
        boolean gotOne = false;
        for (String line : rawEnvDoc) {
            line = line.trim();
            if (!readVars && isStartEnv(line)) {
                builder.append(line + "\n");
                gotOne = true; // so we know whether or not any env elements are present
                if (!(isEndEnv(line) || line.endsWith("/>"))) {
                    // so this is NOT a single, well-behaved entry on a single line
                    readVars = true; // continue reading lines until EOL
                }
            }else{
                noEnvDoc.add(line);
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
            ByteArrayInputStream processedBA = new ByteArrayInputStream(StringUtils.listToString(rawEnvDoc).getBytes(StandardCharsets.UTF_8));
            return loadDocument(processedBA);
        }
        builder.append("</" + CFXMLConfigurations.CONFIGURATION_TAG + ">"); // we now have bookends and a valid XML document
        say("\nenv doc:\n" + builder.toString());
        say("\nNo Env:\n" +StringUtils.listToString( noEnvDoc));
        byte[] envDoc = builder.toString().getBytes(StandardCharsets.UTF_8);
        EnvTable eTable = processEnvDocument(new ByteArrayInputStream(envDoc));
        stack.push(eTable);
        // Now we are left with processing the rest of the document.

        List<String> newContent = new ArrayList<>(noEnvDoc.size());
        // We already have the entire file as a set of lines. Now we can
        // apply templates
        Map<String,String> stackMap = stack.getAsMap();
        for (String line : noEnvDoc) {
            String newLine = TemplateUtility.replaceAll(line, stackMap);
            say(newLine);
            newContent.add(newLine);
        }

        // Now convert back to something the XML engine can grok
        say("\nAfter templates:"+StringUtils.listToString(newContent));
        ByteArrayInputStream processedBA = new ByteArrayInputStream(StringUtils.listToString(newContent).getBytes(StandardCharsets.UTF_8));
        if(!isRoot) stack.pop(); // get rid of local env.
        // now process files
        say("------- End Resolve Env ----------------------\n");
        return loadDocument(processedBA);
   }
    /**
     * Does the trimmed line start and environment entry?
     *
     * @param l
     * @return
     */
    protected   boolean isStartEnv(String l) {
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
    protected   boolean isEndEnv(String l) {
        return l.endsWith(ENV_END_ELEMENT);
    }

    /**
     * Process a single env document from any source. The document is a valid XML file and
     * only environment tags are processed.
     * At the end of this, every envionment element
     * will have been processed and put into a table.
     *
     * @param inputStream
     * @return
     * @throws Throwable
     */
    protected   EnvTable<? extends SKey, ? extends EnvEntry> processEnvDocument(InputStream inputStream) throws Throwable {
        Document doc = loadDocument(inputStream);
        EnvTable eTable = new EnvTable();
        doc.getDocumentElement().normalize();
        NodeList nodeList = doc.getDocumentElement().getElementsByTagName(ENV_TAG);
        if (nodeList.getLength() == 0) return eTable; // nix to do
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
                        eTable.putAll( processEnvDocument(fis)); // don't push a new table for include
                        fis.close();
                    }
                }
            } else {
                String key = keyNode.getNodeValue();
                String value = node.getTextContent();
                eTable.put(new EnvEntry(key, value));
            }
        }
        return eTable;
    }
    /**
     * All the actual work for loading files is done here. This looks for elements inside the config tag
     * of the form
     * <pre>
     *     &lt;file include="path"/&gt;
     * </pre>
     * These are read and each line is inserted into the document. Included files may also include
     * other files, which will be resolved as well. No promise of ordering is done.
     * <h3>Nota Bene</h3>
     * <p>Each include is a complete configuration file, not a fragment! They are processed
     * as if independent (with their own env that retains scope).</p>
     * @param cfg
     * @param visitedFiles Files already processed. Prevents cycles.
     * @return
     */
    protected   Node resolveFileReferences(Document rootDoc, Node cfg, EnvStack stack, Set<String> visitedFiles) throws Throwable {
        NodeList list = cfg.getChildNodes();
        for (int i = 0; i < list.getLength(); i++) {
            Node fileNode = list.item(i);
            if (!fileNode.getNodeName().equals(FILE_TAG)) {
                continue;
            }
            NamedNodeMap nameAttribs = fileNode.getAttributes();
            for (int j = 0; j < nameAttribs.getLength(); j++) {
                Node attrcn = nameAttribs.item(j);
                String currentFile = attrcn.getNodeValue();
                if (visitedFiles.contains(currentFile)) {
                    throw new CyclicalError("circular reference for file \"" + currentFile + "\"");
                }else{
                    // hit a cycle, bail
                    visitedFiles.add(currentFile);
                    File x = new File(currentFile);
                    if (x.isDirectory()) {
                        if (DebugUtil.isTraceEnabled()) {
                            System.out.println("Configuration error: The file named \"" + currentFile + "\" is a directory. Skipping...");
                        }
                        continue;
                    }
                    if (!x.exists()) {
                        if (DebugUtil.isTraceEnabled()) {
                            System.out.println("Configuration error: The file named \"" + currentFile + "\" does not exist. Skipping...");
                        }
                        continue;
                    }
                    if (x.canRead()) {
                        Document doc = loadDocument(x);
                        System.out.println("resolveFiles: loaded doc");
                        printDocument(doc, System.out);
                        resolveFileReferences(rootDoc, doc.getDocumentElement(), stack, visitedFiles);
                        Node root = doc.getDocumentElement();
                        NodeList kids = root.getChildNodes();
                        for (int k = 0; k < kids.getLength(); k++) {
                            /*
                            Have to import the node to the root document, then add it, either with
                            insertBefore or appendChild.
                             */
                            Node nextNode = kids.item(k);

                            if((!nextNode.getNodeName().equals(FILE_TAG)) && nextNode.getNodeType() != Node.TEXT_NODE && nextNode.getNodeType() != Node.COMMENT_NODE) {
                                Node importedNode = rootDoc.importNode(nextNode, true);
                                rootDoc.getDocumentElement().appendChild(importedNode);
                            }
                        }
                        System.out.println("resolveFiles: root  doc post");
                        printDocument(rootDoc, System.out);
                    } else {
                        if (DebugUtil.isTraceEnabled()) {
                            System.out.println("Configuration error: The file named \"" + currentFile + "\" cannot be read (permission issue?). Skipping...");
                        }
                    }
                }
            }
        }
        return cfg;

/*
        NodeList list = cfg.getChildNodes();
        for (int i = 0; i < list.getLength(); i++) {
            Node cn = list.item(i);
            if (!cn.getNodeName().equals(FILE_TAG)) {
                continue;
            }
            NamedNodeMap nameAttribs = cn.getAttributes();
            for (int j = 0; j < nameAttribs.getLength(); j++) {
                Node attrcn = nameAttribs.item(j);
                String currentFile = attrcn.getNodeValue();
                if (!visitedFiles.contains(currentFile)) {
                    // hit a cycle, bail
                    visitedFiles.add(currentFile);
                    File x = new File(currentFile);
                    if (x.isDirectory()) {
                            say("Configuration error: The file named \"" + currentFile + "\" is a directory. Skipping...");
                        continue;
                    }
                    if (!x.exists()) {
                            say("Configuration error: The file named \"" + currentFile + "\" does not exist. Skipping...");
                        continue;
                    }
                    if (x.canRead()) {
                        Document doc = resolveEnvironment(new FileInputStream(x), stack, visitedFiles, false);
                        doc.getDocumentElement().normalize();
                        say(doc.toString());
                        resolveFileReferences(rootDoc, doc.getDocumentElement(), stack, visitedFiles);
                        Node root = rootDoc.getDocumentElement();
                        NodeList configNodes = rootDoc.getElementsByTagName(CONFIGURATION_TAG);
                        Node configNode = configNodes.item(0);
                        say("root node: " + root.getNodeName() + " =" + root.getTextContent());
                        say("config node: " + configNode.getNodeName() + " =" + configNode.getTextContent());
                        NodeList kids = doc.getElementsByTagName(CONFIGURATION_TAG).item(0).getChildNodes();
                        for (int k = 0; k < kids.getLength(); k++) {
                            */
/*
                            Have to import the node to the root document, then add it, either with
                            insertBefore or appendChild.
                             *//*

                            Node nextNode = kids.item(k);

                            if(nextNode.getNodeType() == Node.ELEMENT_NODE) {
                                Node importedNode = rootDoc.importNode(nextNode, true);
                                say("node to insert: " + nextNode.getNodeName() + " =" + nextNode.getTextContent());
                                //root.getFirstChild().appendChild(importedNode);
                                configNode.appendChild(importedNode);
                            }
                        }
                    } else {
                        if (DebugUtil.isTraceEnabled()) {
                            say("Configuration error: The file named \"" + currentFile + "\" cannot be read (permission issue?). Skipping...");
                        }
                    }
                }
                cn.getParentNode().removeChild(cn); // done processing it, get rid of it
            } // end out for loop
        }
        return cfg;
*/
    }
}
