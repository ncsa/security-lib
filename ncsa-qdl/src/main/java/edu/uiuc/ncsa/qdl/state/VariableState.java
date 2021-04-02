package edu.uiuc.ncsa.qdl.state;

import edu.uiuc.ncsa.qdl.evaluate.MetaEvaluator;
import edu.uiuc.ncsa.qdl.evaluate.OpEvaluator;
import edu.uiuc.ncsa.qdl.exceptions.IndexError;
import edu.uiuc.ncsa.qdl.exceptions.QDLException;
import edu.uiuc.ncsa.qdl.exceptions.UnknownSymbolException;
import edu.uiuc.ncsa.qdl.module.Module;
import edu.uiuc.ncsa.qdl.module.ModuleMap;
import edu.uiuc.ncsa.qdl.variables.QDLNull;
import edu.uiuc.ncsa.qdl.variables.StemVariable;
import edu.uiuc.ncsa.security.core.exceptions.NFWException;
import edu.uiuc.ncsa.security.core.util.MyLoggingFacade;

import java.net.URI;
import java.util.ArrayList;
import java.util.TreeSet;
import java.util.regex.Pattern;

import static edu.uiuc.ncsa.qdl.state.ImportManager.NS_DELIMITER;
import static edu.uiuc.ncsa.qdl.state.SymbolTable.int_regex;
import static edu.uiuc.ncsa.qdl.variables.StemVariable.STEM_INDEX_MARKER;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 2/2/20 at  6:42 AM
 */
public abstract class VariableState extends NamespaceAwareState {
    public VariableState(ImportManager resolver,
                         SymbolStack symbolStack,
                         OpEvaluator opEvaluator,
                         MetaEvaluator metaEvaluator,
                         ModuleMap moduleMap,
                         MyLoggingFacade myLoggingFacade) {
        super(resolver,
                symbolStack,
                opEvaluator,
                metaEvaluator,
                moduleMap,
                myLoggingFacade);
    }

    private static final long serialVersionUID = 0xcafed00d7L;



    /**
     * Checks if a symbol is defined. Note that this does do stem tail resolution and namespace resolution.
     *
     * @param symbol
     * @return
     */
    public boolean isDefined(String symbol) {
        try {
            return getValue(symbol) != null;
        } catch (UnknownSymbolException u) {
            // Can happen if the request is for a stem that eventually does not resolve.
            return false;
        }
    }


    /**
     * GetValue may return null if the variable has not been set.
     *
     * @param variableName
     * @return
     */
    public Object getValue(String variableName) {
        if (isStem(variableName)) {
            /*
            If we want to have system variables this is how to do it. A long as nothing is put there
            this won't get evaluated.
             */
            /*if (!systemVars.getMap().isEmpty() && variableName.startsWith(MetaEvaluator.SYS_FQ)) {
                StemMultiIndex w = new StemMultiIndex(variableName);
                variableName = getFQName(w.name);
                StemVariable s = (StemVariable) systemVars.getMap().get(MetaEvaluator.SYS_FQ+variableName);
                if (w.isEmpty()) {
                    return s;
                }
                return s.get(w);
            }*/
            StemMultiIndex w = new StemMultiIndex(variableName);
            return gsrNSStemOp(w, OP_GET, null);
        }
        return gsrNSScalarOp(variableName, OP_GET, null);
    }

    public void setValue(String variableName, Object value) {
        if (isStem(variableName)) {
            StemMultiIndex w = new StemMultiIndex(variableName);
            if (!w.isStem() && (value instanceof StemVariable)) {
                throw new IndexError("Error: You cannot set a scalar variable to a stem value");
            }
            gsrNSStemOp(w, OP_SET, value);
            return;
        }
        if (value instanceof StemVariable) {
            throw new IndexError("Error: You cannot set a scalar variable to a stem value");
        }

        gsrNSScalarOp(variableName, OP_SET, value);
        return;
    }

    public void remove(String variableName) {
        if (isStem(variableName)) {
            StemMultiIndex w = new StemMultiIndex(variableName);
            gsrNSStemOp(w, OP_REMOVE, null);
            return;
        }
        gsrNSScalarOp(variableName, OP_REMOVE, null);
    }


    /**
     * This loops from right to left through the indices of the wrapper. The result is the fully resolved
     * indices against whatever the current state of the symbol stacks are (including modules).
     * The resulting wrapper (which may be substantially smaller than the original) may then be used
     * against the symbole table to actually do the GSR operation.
     *
     * @param w
     * @return
     */
    private StemMultiIndex resolveStemIndices(StemMultiIndex w) {
        int startinglength = w.getRealLength() - 1; // first pass require some special handling
        for (int i = w.getRealLength() - 1; -1 < i; i--) {
            String x = w.getComponents()[i];
            if (x == null || x.isEmpty()) {
                // skip missing stem elements. so a...b == a.b;
                continue;
            }
            String newIndex = resolveStemIndex(x);

            if (i == startinglength) {
                // No stem resolution should be attempted on the first pass.r
                w.getComponents()[i] = newIndex;
                continue;
            }
            // Check if current index is a stem and feed what's to the right to it as a multi-index
            if (isDefined(newIndex + STEM_INDEX_MARKER)) {
                StemMultiIndex ww = new StemMultiIndex(w, i);
                Object v = gsrNSStemOp(ww, OP_GET, null);
                if (v == null) {
                    throw new IndexError("Error: The stem in the index \"" + ww.getName() + "\" did not resolve to a value");
                }
                w.getComponents()[i] = v.toString();
                w.setRealLength(i + 1);
            } else {
                w.getComponents()[i] = newIndex;
            }
        }
        return w.truncate();
    }


    /**
     * gsr = get, set or remove. This resolves the name of the
     *
     * @param w
     * @param op
     * @param value
     * @return
     */
    protected Object gsrNSStemOp(StemMultiIndex w, int op, Object value) {
        checkNSClash(w.name);
        w = resolveStemIndices(w);
        String variableName;
        StemVariable stem = null;
        boolean isNSQ = false;
        URI uri = null;
        boolean isQDLNull = false;
        SymbolTable st = null;
        if (isNSQname(w.name)) {
            // easy case. Everything is qualified
            isNSQ = true;
            variableName = getFQName(w.name);
            uri = importManager.getByAlias(getAlias(w.name));
            Module m = getModuleMap().get(uri);
            if(m == null){
                // So they specified a non-existent module. No such value.
                return null;
            }
            stem = (StemVariable) m.getState().getSymbolStack().resolveValue(variableName);
        } else {
            // Local variables. Remove lead # if needed.
            variableName = w.name;
            if (isNSQLocalName(variableName)) {
                variableName = variableName.substring(1); // whack off first bit
            }
            st = getSymbolStack().getRightST(variableName);

            Object object = st.resolveValue(getFQName(variableName));
            if (object instanceof QDLNull) {
                isQDLNull = true;
            } else {
                Object oooo = st.resolveValue(getFQName(variableName));
                if(oooo!=null && !(oooo instanceof StemVariable)){
                    throw new IllegalArgumentException("error: a stem was expected");
                }
                stem = (StemVariable) oooo;
            }
            // most likely place for it was in the main symbol table. But since there is
            // no name clash, look for it in the modules.
            if (stem == null && !isQDLNull) {
                if (importManager.hasImports()) {
                    for (URI key : importManager.keySet()) {
                        Module m = getModuleMap().get(key);
                        if (m != null) {
                            Object obj = m.getState().getValue(variableName);
                            if (obj != null && (obj instanceof StemVariable)) {
                                stem = (StemVariable) obj;
                                break;
                            }

                        }
                    }
                }
            }
        }
        // Then this is of the form #foo and they are accessing local state explicitly
        switch (op) {
            case OP_GET:
                if (stem == null && !isQDLNull) {
                    //    throw new UnknownSymbolException("error: The stem variable \"" + variableName + "\" does not exist, so cannot get its value.");
                    return null;
                }
                if (isQDLNull) {
                    return QDLNull.getInstance();
                }
                if (w.isEmpty()) {
                    return stem;
                }
                return stem.get(w);
            case OP_SET:

                if (w.isEmpty()) {
                    // set the whole stem, not a value within the stem to the new value (which is a stem)
                    if (isNSQ) {
                        getModuleMap().get(uri).getState().getSymbolStack().getLocalST().setValue(variableName, value);
                    } else {
                        //getSymbolStack().getLocalST().setValue(variableName, value);
                        st.setValue(variableName, value);
                    }

                } else {
                    if (stem == null || isQDLNull) {
                        stem = new StemVariable();
                        st.setValue(variableName, stem);
                    }
                    stem.set(w, value);
                    if (isNSQ) {
                        getModuleMap().get(uri).getState().getSymbolStack().getLocalST().setValue(variableName, stem);
                    } else {
                        //getSymbolStack().getLocalST().setValue(variableName, stem);
                        st.setValue(variableName, stem);
                    }
                }
                //  stash it in the right place. The value within the stem is set. The stack only manages top-level instances.

                return null;
            case OP_REMOVE:
                if (stem == null && !isQDLNull) {
                    throw new UnknownSymbolException("error: The stem variable \"" + variableName + "\" does not exist, so cannot remove a value from it.");
                }
                if (w.isEmpty()) {
                    if (isNSQ) {
                        getModuleMap().get(uri).getState().getSymbolStack().getLocalST().remove(variableName);
                    } else {
                        //getSymbolStack().getLocalST().remove(variableName);
                        st.remove(variableName);
                    }
                } else {

                    stem.remove(w);
                }
                return null;
        }
        throw new NFWException("Internal error; unknown operation type on stem variables.");
    }

    Pattern intPattern = Pattern.compile(int_regex);

    protected Object gsrNSScalarOp(String variableName, int op, Object value) {
        // if(!pattern.matcher(v.getName()).matches()){
        if(variableName.equals("0") || intPattern.matcher(variableName).matches()){
            // so its an actual index, like 0, 1, ...
            // Short circuit all the machinery because it will never resolve
            // and there is no need to jump through all of this.
               return null;
           }
        checkNSClash(variableName); // just check first since its quick
        if (isNSQname(variableName)) {
            // get the module, hand back the value.
            Module module = getImportedModules().get(getAlias(variableName));
            switch (op) {
                case OP_GET:
                    return module.getState().getValue(getFQName(variableName));
                case OP_SET:
                    module.getState().setValue(getFQName(variableName), value);
                    return null;
                case OP_REMOVE:
                    module.getState().resolveStemIndex(getFQName(variableName));

            }
            return null;
            // end of case that there is FQ name
        }
        if (isNSQLocalName(variableName)) {
            variableName = variableName.substring(1); // whack off first bit
        }
        SymbolTable st = getSymbolStack().getRightST(variableName);
        Object v = st.resolveValue(variableName);
        if (v == null) {
            if (!getImportedModules().isEmpty()) {
                for (String key : getImportedModules().keySet()) {
                    Module m = getImportedModules().get(key);
                    if (m != null) {
                        Object obj = m.getState().getValue(variableName);
                        if (obj != null) {
                            v = obj;
                        }

                    }
                }
            }
        }
        switch (op) {
            case OP_GET:
                return v;
            case OP_SET:
                st.setValue(variableName, value);
                return null;
            case OP_REMOVE:
                st.remove(variableName);
        }

        return null;
    }

    static final int OP_SET = 0;
    static final int OP_GET = 1;
    static final int OP_REMOVE = 2;


    public static class ResolveState {
        ArrayList<String> foundVars = new ArrayList<>();

        public void addFound(String index) {
            if (foundVars.contains(index)) {
                throw new CyclicalError("Cyclical index error for " + foundVars);
            }
            foundVars.add(index);
        }
    }

    /**
     * Thrown by {@link ResolveState} if a cycle is found.
     */
    public static class CyclicalError extends QDLException {
        public CyclicalError(String message) {
            super(message);
        }
    }

    protected String resolveStemIndex(String index, ResolveState resolveState) {
   
        Object obj = getValue(index);
        if (obj == null) {
            return index; // null value means does not resolve to anything
        }
        String newIndex = obj.toString();
        resolveState.addFound(newIndex);
        return resolveStemIndex(newIndex, resolveState);
    }

    protected String resolveStemIndex(String index) {
        ResolveState resolveState = new ResolveState();
        // start the cyclical monitoring
        return resolveStemIndex(index, resolveState);
    }

    public TreeSet<String> listVariables(boolean useCompactNotation) {
        TreeSet<String> out = getSymbolStack().listVariables();
        for (URI key : getImportManager().keySet()) {
            Module m = getModuleMap().get(key);
            if(m == null){
                continue; // the user specified a non-existent module.
            }
            TreeSet<String> uqVars = m.getState().listVariables(useCompactNotation);
            for (String x : uqVars) {
                if (useCompactNotation) {
                    out.add(getImportManager().getAlias(key) + NS_DELIMITER + x);
                } else {
                    for (String alias : getImportManager().getAlias(key)) {
                        out.add(alias + NS_DELIMITER + x);
                    }
                }
            }
        }
        return out;
    }

    public boolean isStem(String var) {
        return var.contains(STEM_INDEX_MARKER); // if ther eis an embedded period, needs to be resolved
    }
}
