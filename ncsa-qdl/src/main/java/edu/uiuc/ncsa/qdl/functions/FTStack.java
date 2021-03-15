package edu.uiuc.ncsa.qdl.functions;

import edu.uiuc.ncsa.qdl.parsing.QDLInterpreter;
import edu.uiuc.ncsa.security.core.exceptions.NotImplementedException;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 3/15/21 at  6:22 AM
 */
public class FTStack implements FunctionTable{
    public FTStack() {
        pushNew();
    }

    /**
     * remove <b>all</b> instances of the function from the local stack
     * @param name
     * @param argCount
     * @return
     */
    public void remove(String name, int argCount){
        peek().remove(name, argCount);
    }

    ArrayList<FunctionTable> ftables = new ArrayList<>();
    public FunctionTable peek(){
        return ftables.get(0);
    }

    public void push(FunctionTable ft){
        ftables.add(0, ft);
    }

    public void pushNew(){
        push(new FunctionTableImpl());
    }

    @Override
    public FunctionRecord put(FunctionRecord value) {
        return peek().put(value);
    }

    @Override
    public FunctionRecord get(String key, int argCount) {
        for(FunctionTable functionTable : ftables){
            FunctionRecord fr = functionTable.get(key,argCount);
            if(fr != null){
                return fr;
            }
        }
        return null;
    }

    @Override
    public boolean isDefined(String var, int argCount) {
        for(FunctionTable functionTable : ftables){
            if(functionTable.isDefined(var,argCount)){
                return true;
            }
        }
        return false;
    }

    /**
     * Used in the case there is a single function by this name so we don't have to know ahead
     * of time the arg count (which might not be available).
     * @param name
     * @return
     */
    @Override
    public FunctionRecord getSomeFunction(String name) {
        for(FunctionTable functionTable : ftables){
            FunctionRecord fr = functionTable.getSomeFunction(name);
            if(fr != null){
                return fr;
            }
        }

        return null;
    }
       public List<FunctionRecord> getByAllName(String name){
           List<FunctionRecord> all = new ArrayList<>();
           // Note this walks backwards through the stack.
           for(int i = ftables.size()-1; 0 <= i; i--){
               all.addAll(ftables.get(i).getByAllName(name));
           }
           return all;
        
       }
    @Override
    public void toXML(XMLStreamWriter xsw) throws XMLStreamException {
         throw new NotImplementedException("Implement me!");
    }

    @Override
    public void fromXML(XMLEventReader xer, QDLInterpreter qi) throws XMLStreamException {
        throw new NotImplementedException("Implement me!");

    }

    @Override
    public TreeSet<String> listFunctions(String regex) {
        TreeSet<String> all = new TreeSet<>();
        // Note this walks backwards through the stack since this means that if
        // there is local documentation it overwrites the global documentation.
        for(int i = ftables.size()-1; 0 <= i; i--){
            all.addAll(ftables.get(i).listFunctions(regex));
        }
        return all;
    }

    @Override
    public List<String> listAllDocs() {
        List<String> all = new ArrayList<>();
        // Note this walks backwards through the stack since this means that if
        // there is local documentation it overwrites the global documentation.
        for(int i = ftables.size()-1; 0 <= i; i--){
            all.addAll(ftables.get(i).listAllDocs());
        }
        return all;
    }

    @Override
    public List<String> listAllDocs(String functionName) {
        List<String> all = new ArrayList<>();
        // Note this walks backwards through the stack since this means that if
        // there is local documentation it overwrites the global documentation.
        for(int i = ftables.size()-1; 0 <= i; i--){
            all.addAll(ftables.get(i).listAllDocs(functionName));
        }
        return all;
    }

    @Override
    public List<String> getDocumentation(String fName, int argCount) {
        List<String> all = new ArrayList<>();
        // Note this walks backwards through the stack since this means that if
        // there is local documentation it overwrites the global documentation.
        for(int i = ftables.size()-1; 0 <= i; i--){
            all.addAll(ftables.get(i).getDocumentation(fName, argCount));
        }
        return all;
    }
}
