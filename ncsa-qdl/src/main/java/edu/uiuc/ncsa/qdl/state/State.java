package edu.uiuc.ncsa.qdl.state;

import edu.uiuc.ncsa.qdl.evaluate.MetaEvaluator;
import edu.uiuc.ncsa.qdl.evaluate.OpEvaluator;
import edu.uiuc.ncsa.qdl.exceptions.ImportException;
import edu.uiuc.ncsa.qdl.exceptions.UnknownSymbolException;
import edu.uiuc.ncsa.qdl.expressions.Polyad;
import edu.uiuc.ncsa.qdl.extensions.JavaModule;
import edu.uiuc.ncsa.qdl.module.Module;
import edu.uiuc.ncsa.qdl.module.ModuleMap;
import edu.uiuc.ncsa.qdl.statements.FR_WithState;
import edu.uiuc.ncsa.qdl.statements.FunctionRecord;
import edu.uiuc.ncsa.qdl.statements.FunctionTable;
import edu.uiuc.ncsa.security.core.exceptions.NFWException;

import java.io.Serializable;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import static edu.uiuc.ncsa.qdl.state.NamespaceResolver.NS_DELIMITER;

/**
 * /**
 * represents the internal state of the system. It has the {@link NamespaceResolver},
 * {@link SymbolTable}, {@link edu.uiuc.ncsa.qdl.evaluate.MetaEvaluator} and such.
 * <p>Created by Jeff Gaynor<br>
 * on 1/21/20 at  7:25 AM
 */
public class State implements Serializable {
    private static final long serialversionUID = 129348937L;
    public State(NamespaceResolver resolver,
                 SymbolStack symbolStack,
                 OpEvaluator opEvaluator,
                 MetaEvaluator metaEvaluator,
                 FunctionTable functionTable,
                 ModuleMap moduleMap) {
        this.resolver = resolver;
        this.symbolStack = symbolStack;
        this.metaEvaluator = metaEvaluator;
        this.opEvaluator = opEvaluator;
        this.moduleMap = moduleMap;
        this.functionTable = functionTable;
        //metaEvaluator.addFunctionTable(functionTable);
    }

    public FunctionTable getFunctionTable() {
        return functionTable;
    }

    public void setFunctionTable(FunctionTable functionTable) {
        this.functionTable = functionTable;
    }

    FunctionTable functionTable;

    public Object getValue(String variableName) {
        return doNSOp(variableName, OP_GET, null);
    }

    /**
     * Convenience, just looks up name and arg count
     *
     * @param polyad
     * @return
     */
    public FR_WithState resolveFunction(Polyad polyad) {
        return resolveFunction(polyad.getName(), polyad.getArgumments().size());
    }

    /**
     * Takes any name -- variable or function -- and looks for the NS delimiter, returning a module if there
     * is one.
     *
     * @param rawName
     * @return
     */
    protected Module resolveRawNameToModule(String rawName) {
        String alias = rawName.trim().substring(0, rawName.indexOf(NS_DELIMITER));
        if (alias == null || alias.isEmpty()) {
            throw new UnknownSymbolException(("Internal error: The alias has not set"));
        }
        if (!getResolver().hasAlias(alias)) {
            throw new UnknownSymbolException("Error: No such alias exists");
        }
        URI moduleURI = getResolver().getByAlias(alias);
        return getModuleMap().get(moduleURI);
    }

    public FR_WithState resolveFunction(String name, int argCount) {
        FR_WithState frs = new FR_WithState();
        if (name == null || name.isEmpty()) {
            throw new NFWException(("Internal error: The function has not been named"));
        }

        if (name.contains(NS_DELIMITER)) {
            String resolvedName = name.substring(name.indexOf(NS_DELIMITER) + 1);
            Module module = resolveRawNameToModule(name);
            if (argCount == -1) {
                frs.functionRecord = module.getState().getFunctionTable().getSomeFunction(resolvedName);
            } else {
                frs.functionRecord = module.getState().getFunctionTable().get(resolvedName, argCount);
            }
            frs.state = module.getState();
            frs.isExternalModule = module.isExternal();
            return frs;


        } else {
            if (!getResolver().hasImports()) {
                // Nothing imported, so nothing to look through.
                frs.state = this;
                if (argCount == -1) {
                    frs.functionRecord = getFunctionTable().getSomeFunction(name);
                } else {
                    frs.functionRecord = getFunctionTable().get(name, argCount);
                }
                return frs;
            }
            // check for unqualified names.
            FR_WithState fr = new FR_WithState();
            fr.functionRecord = getFunctionTable().get(name, argCount);
            fr.state = this;
            for (URI ns : moduleMap.keySet()) {
                if (fr.functionRecord == null) {
                    FunctionRecord tempFR = moduleMap.get(ns).getState().getFunctionTable().get(name, argCount);
                    fr.functionRecord = tempFR;
                    fr.state = moduleMap.get(ns).getState();
                    fr.isExternalModule = moduleMap.get(ns).isExternal();
                } else {
                    FunctionRecord tempFR = moduleMap.get(ns).getState().getFunctionTable().get(name, argCount);
                    if (tempFR != null) {
                        throw new ImportException("Error: There are multiple modules with a function named \"" + name + "\". You must fully qualify which one you want.");
                    }
                }
            }
            return fr;
        }
    } // )load module_example.qdl

    public ModuleMap getModuleMap() {
        return moduleMap;
    }

    public void setModuleMap(ModuleMap moduleMap) {
        this.moduleMap = moduleMap;
    }

    protected ModuleMap moduleMap;
    NamespaceResolver resolver;

    public NamespaceResolver getResolver() {
        return resolver;
    }

    public void setResolver(NamespaceResolver resolver) {
        this.resolver = resolver;
    }

    public SymbolStack getSymbolStack() {
        return symbolStack;

    }

    public void setSymbolStack(SymbolStack symbolStack) {
        this.symbolStack = symbolStack;
    }

    public OpEvaluator getOpEvaluator() {
        return opEvaluator;
    }

    public void setOpEvaluator(OpEvaluator opEvaluator) {
        this.opEvaluator = opEvaluator;
    }

    public MetaEvaluator getMetaEvaluator() {

        return metaEvaluator;
    }

    public void setMetaEvaluator(MetaEvaluator metaEvaluator) {
        this.metaEvaluator = metaEvaluator;
    }

    SymbolStack symbolStack;
    MetaEvaluator metaEvaluator;
    OpEvaluator opEvaluator;

    public int getOperatorType(String name) {
        return getOpEvaluator().getType(name);
    }

    public int getFunctionType(String name) {
        return getMetaEvaluator().getType(name);
    }

    /**
     * Takes this state object and sets up a new local environment. This is passed to things
     * like loops, conditionals etc. The lifecycle of these is that they are basically abandoned
     * when done then garbage collected.
     *
     * @return State
     */
    public State newLocalState() {
        //System.out.println("** State, creating new local state **");
        SymbolStack newStack = new SymbolStack(resolver, symbolStack.getParentTables());
        State newState = new State(resolver,
                newStack,
                getOpEvaluator(),
                getMetaEvaluator(),
                getFunctionTable(),
                getModuleMap());
        return newState;
    }

    /**
     * For modules only. This copies the state except that no functions are inherited. The
     * contract is that modules only internal state that may be imported.
     *
     * @return
     */
    public State newModuleState() {
        // NOTE this has no parents. Modules have completely clear state when starting!
        NamespaceResolver r = new NamespaceResolver();
        SymbolStack newStack = new SymbolStack(r);
        newStack.addParent(new SymbolTableImpl(r));
        State newState = new State(r,
                newStack,
                getOpEvaluator(),
                getMetaEvaluator(),
                new FunctionTable(),
                getModuleMap());
        return newState;
    }

    static final int OP_SET = 0;
    static final int OP_GET = 1;
    static final int OP_REMOVE = 2;

    /**
     * Very much internal method to do the basic operations with qualified names. Since most of the
     * machinery for resolving the variable is identical, this centralizes it rather than
     * having boiler plate all over the place. For any operation on variables, add a case here.
     *
     * @param variableName
     * @param op
     * @param value
     * @return
     */
    protected Object doNSOp(String variableName, int op, Object value) {
        if (variableName.contains(NS_DELIMITER)) {
            // easy case. Everything is qualified
            if (variableName.indexOf(NS_DELIMITER) == 0) {
                // Then this is of the form #foo and they are accessing local state explicitly
                switch (op) {
                    case OP_GET:
                        return getSymbolStack().resolveValue(variableName.substring(1));
                    case OP_SET:
                        getSymbolStack().setValue(variableName.substring(1), value);
                        return null;
                    case OP_REMOVE:
                        getSymbolStack().remove(variableName.substring(1));
                        return null;
                }
                return null; // should never get here
            }
            String resolvedName = variableName.substring(variableName.indexOf(NS_DELIMITER) + 1);
            Module module = resolveRawNameToModule(variableName);
            switch (op) {
                case OP_GET:
                    return module.getState().getSymbolStack().resolveValue(resolvedName);
                case OP_SET:
                    module.getState().getSymbolStack().setValue(resolvedName, value);
                    return null;
                case OP_REMOVE:
                    module.getState().getSymbolStack().remove(resolvedName);
                    return null;
            }

        }
        if (!getResolver().hasImports()) {
            if (variableName.contains(NS_DELIMITER)) {
                throw new ImportException("Error: There are no modules imported. " +
                        "It is not possible to resolve \"" + variableName + "\". ");
            }
            // nothing is imported.

            switch (op) {
                case OP_GET:
                    return getSymbolStack().resolveValue(variableName);
                case OP_SET:
                    getSymbolStack().setValue(variableName, value);
                    return null;
                case OP_REMOVE:
                    getSymbolStack().remove(variableName);
                    return null;
            }
            return null; // default
        }
        // so there are imports and the name is not qualified,

        SymbolTable st = null;
        // see if it is defined locally first.
        if (getSymbolStack().isDefined(variableName)) {
            st = getSymbolStack();
        }
        for (URI mKey : getModuleMap().keySet()) {
            SymbolTable tempST = null;
            if (getModuleMap().get(mKey).getState().getSymbolStack().isDefined(variableName)) {
                tempST = getModuleMap().get(mKey).getState().getSymbolStack();
            }
            if (st == null) {
                if (tempST != null) {
                    st = tempST;
                }
            } else {
                if (tempST != null) {
                    throw new ImportException("Error: The variable \"" + variableName
                            + "\" exists in multiple modules. You must qualify which one you wish to use.");
                }
            }
        }
          if(st == null){
              // Didn't find it any place so keep to the local state
              st = getSymbolStack().getTopST();
          }
        switch (op) {
            case OP_GET:
                return st.resolveValue(variableName);
            case OP_SET:
                st.setValue(variableName, value);
                return null;
            case OP_REMOVE:
                st.remove(variableName);
                return null;
        }
        return null;
    }


    public void setValue(String variableName, Object value) {
        doNSOp(variableName, OP_SET, value);
        return;
    }


    public boolean isDefined(String symbol) {
        return getValue(symbol) != null;
    }

    public void remove(String symbol) {
        doNSOp(symbol, OP_REMOVE, null);
    }


    public TreeSet<String> listVariables() {
        TreeSet<String> out = getSymbolStack().listVariables();
        for (URI key : getResolver().keySet()) {
            TreeSet<String> uqVars = getModuleMap().get(key).getState().listVariables();
            for (String x : uqVars) {
                out.add(getResolver().getAlias(key) + NS_DELIMITER + x);
            }
        }
        return out;
    }

    public TreeSet<String> listFunctions() {
        TreeSet<String> out = getFunctionTable().listFunctions();
        for (URI key : getResolver().keySet()) {
            TreeSet<String> uqVars = getModuleMap().get(key).getState().listFunctions();
            for (String x : uqVars) {
                out.add(getResolver().getAlias(key) + NS_DELIMITER + x);
            }
        }
        return out;
    }

    public void addModule(Module module) {
        if (module instanceof JavaModule) {
            ((JavaModule) module).init(this.newModuleState());
        }
        getModuleMap().put(module);

    }

    public List<String> listDocumentation() {
        List<String> out = getFunctionTable().listDoxx();
        for (URI key : getResolver().keySet()) {
            List<String> uqVars = getModuleMap().get(key).getState().getFunctionTable().listDoxx();
            for (String x : uqVars) {
                out.add(getResolver().getAlias(key) + NS_DELIMITER + x);
            }
        }
        return out;

    }

    public List<String> listFunctionDoc(String fname, int argCount) {
        if (fname.contains(NS_DELIMITER)) {
            String alias = fname.substring(0, fname.indexOf(NS_DELIMITER));
            String realName = fname.substring(1 + fname.indexOf(NS_DELIMITER));
            if (alias == null || alias.isEmpty()) {
                List<String> out = getFunctionTable().getDocumentation(realName, argCount);
                if (out == null) {
                    return new ArrayList<>();
                }
                return out;
            }
            if (!resolver.hasAlias(alias)) {
                // so they asked for something that didn't exist
                return new ArrayList<>();
            }
            URI ns = resolver.getByAlias(alias);
            List<String> docs = getModuleMap().get(ns).getState().getFunctionTable().getDocumentation(realName, argCount);
            if (docs == null) {
                return new ArrayList<>();
            }
            return docs;
            // easy cases.

        }
        // No imports, not qualified, hand back whatever we have
        if (!resolver.hasImports()) {
            List<String> out = getFunctionTable().getDocumentation(fname, argCount);
            if (out == null) {
                return new ArrayList<>();
            }
            return out;
        }
        // Final case, unqualified name and there are imports. Return all that match.
        List<String> out = getFunctionTable().getDocumentation(fname, argCount);
        if (out == null) {
            out = new ArrayList<>();
        }
        for (URI key : getResolver().keySet()) {
            String caput = getResolver().getAlias(key) + NS_DELIMITER + fname + "(" + argCount + "):";

            List<String> doxx = getModuleMap().get(key).getState().getFunctionTable().getDocumentation(fname,
                    argCount);
            if (doxx == null) {
                out.add(caput + " none");
            } else {
                out.add(caput);
                out.addAll(doxx);
            }
        }
        return out;
    }

}
