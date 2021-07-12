package edu.uiuc.ncsa.qdl.workspace;

import edu.uiuc.ncsa.qdl.evaluate.AbstractFunctionEvaluator;
import edu.uiuc.ncsa.qdl.evaluate.IOEvaluator;
import edu.uiuc.ncsa.qdl.expressions.ConstantNode;
import edu.uiuc.ncsa.qdl.expressions.Polyad;
import edu.uiuc.ncsa.qdl.parsing.QDLInterpreter;
import edu.uiuc.ncsa.qdl.state.State;
import edu.uiuc.ncsa.qdl.variables.Constant;
import edu.uiuc.ncsa.qdl.variables.StemEntry;
import edu.uiuc.ncsa.qdl.variables.StemVariable;
import edu.uiuc.ncsa.qdl.vfs.VFSPaths;
import edu.uiuc.ncsa.qdl.xml.XMLUtils;
import edu.uiuc.ncsa.security.core.util.StringUtils;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.XMLEvent;
import java.io.File;
import java.io.Serializable;
import java.util.*;

import static edu.uiuc.ncsa.qdl.xml.XMLConstants.*;

/**
 * This manages buffers, i.e., things that may be edited and run.
 * <p>Created by Jeff Gaynor<br>
 * on 4/27/20 at  6:47 AM
 */
public class BufferManager implements Serializable {


    public static class BufferRecord {
        String src;
        String link;
        boolean edited = false;
        boolean deleted = false;
        List<String> content = null;
        boolean memoryOnly = false;
        String srcSavePath = null;
        String linkSavePath = null;


        public List<String> getContent() {
            return content;
        }

        public void setContent(List<String> content) {
            this.content = content;
        }


        public boolean hasContent() {
            if (content == null) {
                return false;
            }
            return !content.isEmpty();
        }

        public boolean isLink() {
            return link != null;
        }

        public String toString() {
            return src + (isLink() ? " --> " + link : "");
        }

        public void toXML(XMLStreamWriter xsw) throws XMLStreamException {
            xsw.writeStartElement(BUFFER_RECORD);
            if (!StringUtils.isTrivial(src)) {
                xsw.writeAttribute(BR_SOURCE, src);
            }
            if (!StringUtils.isTrivial(link)) {
                xsw.writeAttribute(BR_LINK, link);
            }
            xsw.writeAttribute(BR_EDITED, Boolean.toString(edited));
            xsw.writeAttribute(BR_DELETED, Boolean.toString(deleted));
            xsw.writeAttribute(BR_MEMORY_ONLY, Boolean.toString(memoryOnly));
            if(srcSavePath != null){
                xsw.writeAttribute(BR_SOURCE_SAVE_PATH, srcSavePath);
            }
            if(linkSavePath != null){
                xsw.writeAttribute(BR_LINK_SAVE_PATH, linkSavePath);
            }
            if (content != null && !content.isEmpty()) {
                xsw.writeStartElement(BR_CONTENT);
                StemVariable s = new StemVariable();
                s.addList(content);
                XMLUtils.write(xsw, s);
                xsw.writeEndElement(); //end content tag
            }
            xsw.writeEndElement(); //end BR tag
        }

        public void fromXML(XMLEventReader xer) throws XMLStreamException {
            XMLEvent xe = xer.nextEvent(); // position at start
            // get the attributes
            Iterator iterator = xe.asStartElement().getAttributes(); // Use iterator since it tracks state
            while (iterator.hasNext()) {
                Attribute a = (Attribute) iterator.next();
                String v = a.getValue();
                switch (a.getName().getLocalPart()) {
                    case BR_SOURCE:
                        src = v;
                        break;
                    case BR_SOURCE_SAVE_PATH:
                        srcSavePath = v;
                        break;
                    case BR_LINK:
                        link = v;
                        break;
                    case BR_LINK_SAVE_PATH:
                        linkSavePath = v;
                        break;
                    case BR_DELETED:
                        deleted = Boolean.parseBoolean(v);
                        break;
                    case BR_EDITED:
                        edited = Boolean.parseBoolean(v);
                        break;
                    case BR_MEMORY_ONLY:
                        memoryOnly = Boolean.parseBoolean(v);
                        break;
                }

            }
            while (xer.hasNext()) {
                xe = xer.peek();
                switch (xe.getEventType()) {
                    case XMLEvent.START_ELEMENT:
                        if (xe.asStartElement().getName().getLocalPart().equals(STEM_TAG)) {
                            Object obj = XMLUtils.resolveConstant(xer);
                            if (obj instanceof StemVariable) {
                                content = ((StemVariable) obj).getStemList().toJSON();
                            }
                        }
                        break;
                    case XMLEvent.END_ELEMENT:
                        if (xe.asEndElement().getName().getLocalPart().equals(BUFFER_RECORD)) {
                            return;
                        }
                        break;
                }

                xer.next();
            }
            throw new IllegalStateException("Error: XML file corrupt. No end tag for " + BUFFER_RECORD);

        }
    }

    public ArrayList<BufferRecord> getBufferRecords() {
        return bufferRecords;
    }

    /*
    These have the same buffer records. The list lets us display them with indices
    the map allows for lookup by key.
     */
    ArrayList<BufferRecord> bufferRecords = new ArrayList<>();
    HashMap<String, BufferRecord> brMap = new HashMap<>();

    public State getState() {
        return state;
    }

    State state;

    /**
     * Get the record by index
     *
     * @param index
     * @return
     */
    public BufferRecord getBufferRecord(int index) {
        if (!hasBR(index)) {
            return null;
        }
        return bufferRecords.get(index);
    }

    public boolean anyEdited() {
        for (String key : brMap.keySet()) {
            if (getBufferRecord(key).edited) return true;
        }
        return false;
    }

    public BufferRecord getBufferRecord(String name) {
        if (!brMap.containsKey(name)) {
            return null;
        }
        return brMap.get(name);
    }

    public boolean hasBR(int index) {
        if (bufferRecords.isEmpty()) return false;
        if (index < 0 || bufferRecords.size() < index) return false;
        return true;
    }

    public boolean hasBR(String name) {
        if (bufferRecords.isEmpty()) return false;
        return brMap.containsKey(name);
    }

    public int create(String source) {
        BufferRecord br = new BufferRecord();
        br.src = source;
        bufferRecords.add(br);
        brMap.put(source, br);
        return bufferRecords.size() - 1;
    }

    public int link(String source, String target) {
        BufferRecord br = new BufferRecord();
        br.src = source;
        br.link = target;
        bufferRecords.add(br);
        brMap.put(source, br);
        return bufferRecords.size() - 1;
    }

    protected int getIndex(BufferRecord br) {
        for (int i = 0; i < bufferRecords.size(); i++) {
            if (bufferRecords.get(i) == br) {
                return i;
            }
        }
        return -1;
    }

    public boolean remove(String name) {
        if (!hasBR(name)) {
            return false;
        }
        BufferRecord br = brMap.get(name);
        // We don't actually remove the record since we do not want indices to change.
        // If they recreate it later, they will get a different index. Best we can do...

        br.deleted = true;
        brMap.remove(br.src);
        br.src = null;
        br.link = null;
        return true;
    }


    protected List<String> readFile(String fName) {
        Polyad request = new Polyad(IOEvaluator.READ_FILE);
        request.addArgument(new ConstantNode(fName, Constant.STRING_TYPE));
        request.addArgument(new ConstantNode(new Long(AbstractFunctionEvaluator.FILE_OP_TEXT_STEM), Constant.LONG_TYPE));
        getState().getMetaEvaluator().evaluate(request, getState());
        if (request.getResultType() != Constant.STEM_TYPE) {
            throw new IllegalStateException("Error: Could not read file \"" + fName + "\"");
        }
        StemVariable stem = (StemVariable) request.getResult();
        if (stem == null) {
            return null;
        }
        ArrayList<String> response = new ArrayList<>();
        for (StemEntry se : stem.getStemList()) {
            response.add(se.entry.toString());
        }
        return response;

    }

    public List<String> read(int index, boolean useSource) throws Throwable {
        BufferRecord currentBR = getBufferRecord(index);
        if (currentBR == null) {
            return null;
        }

        String f = useSource ? currentBR.srcSavePath : currentBR.linkSavePath;
        return readFile(f);
    }

    /**
     *
     * @param parent
     * @param name
     * @return
     */
    protected String figureOutSavePath(String parent, String name) {
        // Figure it out.
        if (name.contains(VFSPaths.SCHEME_DELIMITER)) {
            // so its in a VFS and its absolute
            return name;
        }
        if (parent.contains(VFSPaths.SCHEME_DELIMITER)) {
            return parent + name;
        }

        // it's a regular file
        File targetForSave = new File(name);
        if (!targetForSave.isAbsolute()) {
            // resolve it against the parent
            return (new File(parent, name)).getAbsolutePath();
        } else {
            return targetForSave.getAbsolutePath();
        }
    }

    public boolean write(BufferRecord currentBR) {
        //BufferRecord currentBR = getBufferRecord(index);
        if (currentBR == null) {
            return false;
        }


        if (currentBR.isLink()) {
            // save br.link to br.src
            String readIt = IOEvaluator.READ_FILE + "('" + currentBR.linkSavePath + "')";
            String raw = IOEvaluator.WRITE_FILE + "('" + currentBR.srcSavePath + "'," + readIt + ");";
            try {
                QDLInterpreter parser = new QDLInterpreter(getState());
                parser.setEchoModeOn(false);// no output
                parser.execute(raw);
            } catch (Throwable throwable) {
                getState().error("could not write file", throwable);
                return false;
            }
            return true;
        }

        if (currentBR.edited) {
            // Some save logic first
            File f = new File(currentBR.src);
            Polyad request = new Polyad(IOEvaluator.WRITE_FILE);
            StemVariable stemVariable = new StemVariable();
            List<Object> castList = new LinkedList<>();
            castList.addAll(currentBR.getContent());
            stemVariable.addList(castList);
            request.addArgument(new ConstantNode(currentBR.srcSavePath, Constant.STRING_TYPE));
            request.addArgument(new ConstantNode(stemVariable, Constant.STEM_TYPE));
            request.addArgument(new ConstantNode(Boolean.FALSE, Constant.BOOLEAN_TYPE)); // or its will be treated as binary
            getState().getMetaEvaluator().evaluate(request, getState());
            currentBR.setContent(null); // flush the buffer.
            currentBR.edited = false;
            return true;
        }
        return false;
    }


    BufferRecord defaultBR = null;

    public BufferRecord defaultBR() {
        return defaultBR;
    }

    public BufferRecord defaultBR(BufferRecord br) {
        return defaultBR = br;
    }

    public void toXML(XMLStreamWriter xsw) throws XMLStreamException {
        xsw.writeStartElement(BUFFER_MANAGER);
        xsw.writeStartElement(BUFFER_RECORDS);
        for (BufferRecord br : bufferRecords) {
            br.toXML(xsw);
        }
        xsw.writeEndElement(); // records
        xsw.writeEndElement(); // buffer manager
    }

    public void fromXML(XMLEventReader xer) throws XMLStreamException {
        XMLEvent xe = xer.nextEvent();
        while (xer.hasNext()) {
            xe = xer.peek();
            switch (xe.getEventType()) {
                case XMLEvent.START_ELEMENT:
                    if (xe.asStartElement().getName().getLocalPart().equals(BUFFER_RECORDS)) {
                        doBRecs(xer);
                    }
                    break;
                case XMLEvent.END_ELEMENT:
                    if (xe.asEndElement().getName().getLocalPart().equals(BUFFER_MANAGER)) {
                        return;
                    }
                    break;
            }
            xer.next();
        }
        throw new IllegalStateException("Error: XML file corrupt. No end tag for " + BUFFER_MANAGER);

    }

    protected void doBRecs(XMLEventReader xer) throws XMLStreamException {
        XMLEvent xe = xer.nextEvent();
        while (xer.hasNext()) {
            xe = xer.peek();
            switch (xe.getEventType()) {
                case XMLEvent.START_ELEMENT:
                    if (xe.asStartElement().getName().getLocalPart().equals(BUFFER_RECORD)) {
                        BufferRecord br = new BufferRecord();
                        br.fromXML(xer);
                        bufferRecords.add(br);
                        brMap.put(br.src, br);
                    }
                    break;
                case XMLEvent.END_ELEMENT:
                    if (xe.asEndElement().getName().getLocalPart().equals(BUFFER_RECORDS)) {
                        return;
                    }
                    break;
            }
            xer.next();
        }
        throw new IllegalStateException("Error: XML file corrupt. No end tag for " + BUFFER_RECORDS);

    }

    public boolean isEmpty() {
        return bufferRecords.isEmpty();
    }
}
