package edu.uiuc.ncsa.qdl.extensions;

import edu.uiuc.ncsa.qdl.module.Module;
import edu.uiuc.ncsa.qdl.state.State;
import edu.uiuc.ncsa.qdl.variables.Constant;
import edu.uiuc.ncsa.qdl.xml.XMLConstants;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;
import java.util.regex.Pattern;

import static edu.uiuc.ncsa.qdl.state.legacy.SymbolTable.var_regex;

/**
 * This will let you create your own extensions to QDL in Java. Simply implement the interfaces
 * {@link QDLFunction} for functions and {@link QDLVariable} for variables, add the module
 * and you can use it in QDL like any other module.
 * <p>Created by Jeff Gaynor<br>
 * on 1/27/20 at  12:03 PM
 */
public abstract class JavaModule extends Module {
    /**
     * Used by {@link QDLLoader}
     */
    public JavaModule() {
    }

    @Override
    public boolean isExternal() {
        return true;
    }

    String className;

    public String getClassname() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getLoaderClassName() {
        return loaderClassName;
    }

    public void setLoaderClassName(String loaderClassName) {
        this.loaderClassName = loaderClassName;
    }

    String loaderClassName;
    /**
     * Used by the factory method {@link #newInstance(State)}
     *
     * @param namespace
     * @param alias
     */
    protected JavaModule(URI namespace, String alias) {
        super(namespace, alias, null); // no state here -- it is injected later
    }

    public void addFunctions(List<QDLFunction> functions) {
        funcs.addAll(functions);
    }

    protected List<QDLVariable> vars = new ArrayList<>();
    protected List<QDLFunction> funcs = new ArrayList<>();

    public void addVariables(List<QDLVariable> variables) {
        vars.addAll(variables);
    }

    Pattern pattern = Pattern.compile(var_regex);
    boolean initialized = false;

    /**
     * This is critical in that it puts all the functions and variables (with their correct alias) in
     * to the state for this module. Normally this is called when module_import is invoked
     * on each module, so generally you do not need to call this ever. It is, however, what makes
     * any module work.
     *
     * @param state
     */
    public void init(State state) {
                      init(state, true);
    }
    public void init(State state, boolean doVariables) {
        if (initialized) return;
        setDocumentation(createDefaultDocs());
        if (state == null) return;
        setState(state);
        // If this is being recreated from its serialization, skip the variables so whatever
        // the has set is not overwritten.
        if(doVariables) {
            for (QDLVariable v : vars) {
                if (Constant.getType(v.getValue()) == Constant.UNKNOWN_TYPE) {
                    throw new IllegalArgumentException("Error: The value of  " + v.getValue() + " is unknown.");
                }
                if (!pattern.matcher(v.getName()).matches()) {
                    throw new IllegalArgumentException("Error: The variable name \"" + v.getName() + "\" is not a legal variable name.");
                }
                state.setValue(v.getName(), v.getValue());
            }
        }
        for (QDLFunction f : funcs) {
            for (int i : f.getArgCount()) {
                QDLFunctionRecord fr = new QDLFunctionRecord();
                fr.qdlFunction = f;
                fr.argCount = i;
                fr.name = f.getName();
                if (f.getDocumentation(i) != null && !f.getDocumentation(i).isEmpty()) {
                    fr.documentation = f.getDocumentation(i);
                }
                state.getFTStack().put(fr);

            }
        }
        setClassName(this.getClass().getCanonicalName());
        initialized = true;
    }


    @Override
    public void writeExtraXMLAttributes(XMLStreamWriter xsw) throws XMLStreamException {
        super.writeExtraXMLAttributes(xsw);
        xsw.writeAttribute(XMLConstants.MODULE_TYPE_TAG, XMLConstants.MODULE_TYPE_JAVA_TAG);
        xsw.writeAttribute(XMLConstants.MODULE_CLASS_NAME_TAG, getClassname());
    }



    /**
     * Creates the documentation from the first of each line of every function. Use this or
     * override as needed.
     *
     * @return
     */
    public List<String> createDefaultDocs() {
        List<String> docs = new ArrayList<>();
        docs.add("  module name : " + getClass().getSimpleName() );
        docs.add("    namespace : " + getNamespace());
        docs.add("default alias : " + getAlias());
        docs.add("   java class : " + getClass().getCanonicalName());
        if(getDescription() != null ){
              docs.addAll(getDescription());
        }
        if(!funcs.isEmpty()) {
            docs.add("functions:");
            // Now sort the functions
            TreeSet<String> treeSet = new TreeSet<>();
            for (QDLFunction f : funcs) {
                for (int argCount : f.getArgCount()) {
                    List<String> x = f.getDocumentation(argCount);
                    if (x != null && !x.isEmpty()) {
                        treeSet.add("  " + f.getDocumentation(argCount).get(0));// indent too...
                    }
                }
            }
            docs.addAll(treeSet);
        }
        if(!vars.isEmpty()){
            docs.add("variables:");
            TreeSet<String> treeSet = new TreeSet<>();
            for (QDLVariable variable : vars) {
                treeSet.add("  " + variable.getName()); // indent
            }
            docs.addAll(treeSet);
        }

        return docs;
    }

    /**
     * The {@link #createDefaultDocs()} will create basic documentation for functions and such,
     * and is called automatically during module {@link #init(State)},
     * but the actual description of this module -- if any -- is done here. Override and return your description.
     * @return
     */
     public List<String> getDescription(){
        return null;
     }

     List<String> documentation = new ArrayList<>();

    @Override
    public List<String> getListByTag() {
        return documentation;
    }

    @Override
    public void setDocumentation(List<String> documentation) {
                   this.documentation = documentation;
    }


}
