package edu.uiuc.ncsa.qdl.functions;

import edu.uiuc.ncsa.qdl.parsing.QDLInterpreter;
import edu.uiuc.ncsa.qdl.state.State;
import edu.uiuc.ncsa.qdl.state.StateUtils;
import edu.uiuc.ncsa.qdl.statements.Statement;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 1/22/20 at  10:48 AM
 */
public class FunctionRecord implements Serializable, Cloneable {
    public static int FREF_ARG_COUNT = -10;
    public String name;
    public List<String> sourceCode = new ArrayList<>();
    public List<String> documentation = new ArrayList<>();
    public List<Statement> statements = new ArrayList<>();
    public List<String> argNames = new ArrayList<>();
    public boolean isFuncRef = false;
    public String fRefName = null;

    public int getArgCount() {
        if (isFuncRef) return FREF_ARG_COUNT;
        return argNames.size();
    }

    @Override
    public String toString() {
        return "FunctionRecord{" +
                "name='" + name + '\'' +
                ", sourceCode='" + sourceCode + '\'' +
                ", statements=" + statements +
                ", argNames=" + argNames +
                ", arg count = " + getArgCount() +
                ", f_ref? = " + isFuncRef +
                (fRefName == null ? "" : ", ref_name = \"" + fRefName + "\"") +
                '}';
    }

    @Override
    public FunctionRecord clone() throws CloneNotSupportedException {
        FunctionRecord functionRecord = new FunctionRecord();
        functionRecord.name = name;
        functionRecord.sourceCode = sourceCode;
        functionRecord.documentation = documentation;
        functionRecord.statements = statements;
        functionRecord.argNames = argNames;
        return functionRecord;
    }

   transient byte[] template = null;
    /**
     * Create a new , clean copy of this function record for use. It will be
     * modified as part of being the local state during execution so a
     * new copy is necessary. This uses Java serialization since the
     * resulting function may be quite a complex network of objects,
     * so this is serialized then deserialized.
     * @return
     * @throws Throwable
     */
    public FunctionRecord newInstance() throws Throwable {
        if (template == null) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(this);
            oos.flush();
            oos.close();
            template = baos.toByteArray();
        }
        ByteArrayInputStream bais = new ByteArrayInputStream(template);
        ObjectInputStream ois = new ObjectInputStream(bais);
        FunctionRecord fr = (FunctionRecord) ois.readObject();
        ois.close();
        return fr;
    }

    /**
     * One other way to do the {@link #newInstance()} method is to simply grab the source
     * code and re-interpret this. This works fine, but is a lot slower than serialization
     * since a whole new interpreter with state has to be created and all the work to get this far
     * has to be repeated every time a reference to the function is made.
     * @return
     * @throws Throwable
     */
    protected FunctionRecord oldNewInstance() throws Throwable {
        State state = StateUtils.newInstance();
        QDLInterpreter interpreter = new QDLInterpreter(state);
        interpreter.execute(sourceCode);
        List<FunctionRecord> frs = state.getFTStack().peek().getAll();
        return frs.get(0);
    }
    /*
       a. := [[n(3),5+n(3)],[[n(4),5+n(4)],[n(5),6+n(5)]]]
       b. := 100 + a.
     */
}
