package edu.uiuc.ncsa.qdl.functions;

import edu.uiuc.ncsa.qdl.parsing.QDLInterpreter;
import edu.uiuc.ncsa.qdl.statements.Documentable;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.Serializable;
import java.util.List;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 3/15/21 at  6:18 AM
 * @deprecated do not use. Use instance of {@link FTable}
 */
public interface FunctionTable extends Cloneable, Serializable, Documentable {
    FunctionRecord put(FunctionRecord value);

    FunctionRecord get(String key, int argCount);

    boolean containsKey(String var, int argCount);

    boolean containsKey(String var, int argCount, int startTableIndex);

    /**
     * Gets a function reference when there are no arguments. E.g. in a call to
     * a function like f(@g()), this would search for g.
     * @param name
     * @return
     */
    FunctionRecord getFunctionReference(String name);

    /**
     * Finds every {@link FunctionRecord} in all stacks for a given name.
     *
     * @param name
     * @return
     */
    List<FunctionRecord> getByAllName(String name);

    List<FunctionRecord> getAll();


    void toXML(XMLStreamWriter xsw) throws XMLStreamException;

    void fromXML(XMLEventReader xer, QDLInterpreter qi) throws XMLStreamException;

    void remove(String fName, int argCount);

    boolean isEmpty();

    int size();
}
