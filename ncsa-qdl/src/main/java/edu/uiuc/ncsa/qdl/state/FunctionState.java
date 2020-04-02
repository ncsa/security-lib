package edu.uiuc.ncsa.qdl.state;

import edu.uiuc.ncsa.qdl.evaluate.MetaEvaluator;
import edu.uiuc.ncsa.qdl.evaluate.OpEvaluator;
import edu.uiuc.ncsa.qdl.exceptions.ImportException;
import edu.uiuc.ncsa.qdl.exceptions.UndefinedFunctionException;
import edu.uiuc.ncsa.qdl.expressions.Polyad;
import edu.uiuc.ncsa.qdl.module.Module;
import edu.uiuc.ncsa.qdl.module.ModuleMap;
import edu.uiuc.ncsa.qdl.statements.FR_WithState;
import edu.uiuc.ncsa.qdl.statements.FunctionRecord;
import edu.uiuc.ncsa.qdl.statements.FunctionTable;
import edu.uiuc.ncsa.security.core.exceptions.NFWException;
import edu.uiuc.ncsa.security.core.util.MyLoggingFacade;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.TreeSet;

import static edu.uiuc.ncsa.qdl.state.ImportManager.NS_DELIMITER;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 2/2/20 at  6:48 AM
 */
public abstract class FunctionState extends VariableState {
    public FunctionState(ImportManager resolver,
                         SymbolStack symbolStack,
                         OpEvaluator opEvaluator,
                         MetaEvaluator metaEvaluator,
                         FunctionTable functionTable,
                         ModuleMap moduleMap,
                         MyLoggingFacade myLoggingFacade) {
        super(resolver,
                symbolStack,
                opEvaluator,
                metaEvaluator,
                functionTable,
                moduleMap,
                myLoggingFacade);
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

    public FR_WithState resolveFunction(String name, int argCount) {
        FR_WithState frs = new FR_WithState();
        if (name == null || name.isEmpty()) {
            throw new NFWException(("Internal error: The function has not been named"));
        }

        if (name.contains(NS_DELIMITER)) {
            if (name.startsWith(NS_DELIMITER)) {
                // case is that it is directly qualified for the top leve. Check it, return what is there.
                frs.functionRecord = getFunctionTable().get(name.substring(1), argCount);
                frs.state = this;
                return frs;
            }
            StringTokenizer st = new StringTokenizer(name, NS_DELIMITER);
            ArrayList<String> arrayList = new ArrayList<>();
            while (st.hasMoreTokens()) {
                arrayList.add(st.nextToken());
            }
            String resolvedName = arrayList.get(arrayList.size() - 1);// last one is name
            arrayList.remove(arrayList.size() - 1); // remove it

            Module currentModule = getImportedModules().get(arrayList.get(0));// there is at least one.
            arrayList.remove(0);
            Module nextModule = null;
            for (String aa : arrayList) {
                nextModule = currentModule.getState().getImportedModules().get(aa);
                currentModule = nextModule;
            }

            Module module = currentModule;


            if (argCount == -1) {
                frs.functionRecord = module.getState().getFunctionTable().getSomeFunction(resolvedName);
            } else {
                frs.functionRecord = module.getState().getFunctionTable().get(resolvedName, argCount);
            }
            frs.state = module.getState();
            frs.isExternalModule = module.isExternal();
            return frs;


        } else {
            if (!getImportManager().hasImports()) {
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
            for (String alias : getImportedModules().keySet()) {
                if (fr.functionRecord == null) {
                    FunctionRecord tempFR = getImportedModules().get(alias).getState().getFunctionTable().get(name, argCount);
                    fr.functionRecord = tempFR;
                    fr.state = getImportedModules().get(alias).getState();
                    fr.isExternalModule = getImportedModules().get(alias).isExternal();
                } else {
                    FunctionRecord tempFR = importedModules.get(alias).getState().getFunctionTable().get(name, argCount);
                    if (tempFR != null) {
                        throw new ImportException("Error: There are multiple modules with a function named \"" + name + "\". You must fully qualify which one you want.");
                    }
                }
            }
            if (fr.functionRecord == null) {
                throw new UndefinedFunctionException("Error: No such function named \"" + name + "\" exists with " + argCount + " argument" + (argCount == 1 ? "." : "s."));
            }
            return fr;
        }
    } // )load module_example.qdl

    public TreeSet<String> listFunctions(String regex) {
        TreeSet<String> out = getFunctionTable().listFunctions(regex);
        for (URI key : getImportManager().keySet()) {
            TreeSet<String> uqVars = getModuleMap().get(key).getState().listFunctions(regex);
            for (String x : uqVars) {
                out.add(getImportManager().getAlias(key) + NS_DELIMITER + x);
            }
        }
        return out;
    }

    public List<String> listDocumentation() {
        List<String> out = getFunctionTable().listDoxx();
        for (URI key : getImportManager().keySet()) {
            List<String> uqVars = getModuleMap().get(key).getState().getFunctionTable().listDoxx();
            for (String x : uqVars) {
                out.add(getImportManager().getAlias(key) + NS_DELIMITER + x);
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
            if (!importManager.hasAlias(alias)) {
                // so they asked for something that didn't exist
                return new ArrayList<>();
            }
            URI ns = importManager.getByAlias(alias);
            List<String> docs = getModuleMap().get(ns).getState().getFunctionTable().getDocumentation(realName, argCount);
            if (docs == null) {
                return new ArrayList<>();
            }
            return docs;
            // easy cases.

        }
        // No imports, not qualified, hand back whatever we have
        if (!importManager.hasImports()) {
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
        for (URI key : getImportManager().keySet()) {
            if (getModuleMap().get(key).getState().getFunctionTable().get(fname, argCount) != null) {
                String caput = getImportManager().getAlias(key) + NS_DELIMITER + fname + "(" + argCount + "):";

                List<String> doxx = getModuleMap().get(key).getState().getFunctionTable().getDocumentation(fname,
                        argCount);
                if (doxx == null) {
                    out.add(caput + " none");
                } else {
                    out.add(caput);
                    out.addAll(doxx);
                }
            }
        }
        return out;
    }

}
