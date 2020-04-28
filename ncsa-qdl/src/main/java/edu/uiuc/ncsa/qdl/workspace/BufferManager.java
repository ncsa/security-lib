package edu.uiuc.ncsa.qdl.workspace;

import edu.uiuc.ncsa.qdl.evaluate.AbstractFunctionEvaluator;
import edu.uiuc.ncsa.qdl.evaluate.IOEvaluator;
import edu.uiuc.ncsa.qdl.expressions.ConstantNode;
import edu.uiuc.ncsa.qdl.expressions.Polyad;
import edu.uiuc.ncsa.qdl.parsing.QDLParser;
import edu.uiuc.ncsa.qdl.state.State;
import edu.uiuc.ncsa.qdl.variables.Constant;
import edu.uiuc.ncsa.qdl.variables.StemEntry;
import edu.uiuc.ncsa.qdl.variables.StemVariable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * This manages buffers, i.e., things that may be edited and run.
 * <p>Created by Jeff Gaynor<br>
 * on 4/27/20 at  6:47 AM
 */
public class BufferManager implements Serializable {

    LinkedList<String> clipboard = new LinkedList<>();

    public static class BufferRecord {
        String src;
        String link;
        boolean edited = false;
        boolean deleted = false;

        public List<String> getContent() {
            return content;
        }

        public void setContent(List<String> content) {
            this.content = content;
        }

        List<String> content = null;

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
    }

    public ArrayList<BufferRecord> getBufferRecords() {
        return bufferRecords;
    }

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
        String f = useSource ? currentBR.src : currentBR.link;
        return readFile(f);
    }


    public boolean write(BufferRecord currentBR) {
        //BufferRecord currentBR = getBufferRecord(index);
        if (currentBR == null) {
            return false;
        }
        if (currentBR.isLink()) {
            // so this is a link but there is nothing in the buffer
            // copy the link to the source
            String readIt = IOEvaluator.READ_FILE + "('" + currentBR.link + "')";
            String raw = IOEvaluator.WRITE_FILE + "('" + currentBR.src + "'," + readIt + ");";
            try {
                QDLParser parser = new QDLParser(getState());
                parser.setEchoModeOn(false);// no output
                parser.execute(raw);
            } catch (Throwable throwable) {
                getState().getLogger().warn("could not write file", throwable);
                return false;
            }
            return true;
        }

        if (currentBR.edited) {
            Polyad request = new Polyad(IOEvaluator.WRITE_FILE);
            StemVariable stemVariable = new StemVariable();
            List<Object> castList = new LinkedList<>();
            castList.addAll(currentBR.getContent());
            stemVariable.addList(castList);
            request.addArgument(new ConstantNode(currentBR.src, Constant.STRING_TYPE));
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

}
