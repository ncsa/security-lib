package edu.uiuc.ncsa.qdl.state;

import edu.uiuc.ncsa.qdl.evaluate.MetaEvaluator;
import edu.uiuc.ncsa.qdl.evaluate.OpEvaluator;
import edu.uiuc.ncsa.qdl.exceptions.NamespaceException;
import edu.uiuc.ncsa.qdl.exceptions.UndefinedFunctionException;
import edu.uiuc.ncsa.qdl.expressions.Polyad;
import edu.uiuc.ncsa.qdl.functions.*;
import edu.uiuc.ncsa.qdl.module.*;
import edu.uiuc.ncsa.qdl.variables.VStack;
import edu.uiuc.ncsa.security.core.exceptions.NFWException;
import edu.uiuc.ncsa.security.core.util.MyLoggingFacade;

import java.util.*;


/**
 * <p>Created by Jeff Gaynor<br>
 * on 2/2/20 at  6:48 AM
 */
public abstract class FunctionState extends VariableState {
    public FunctionState(VStack vStack,
                         OpEvaluator opEvaluator,
                         MetaEvaluator metaEvaluator,
                         FStack fStack,
                         MTStack mtStack,
                         MIStack miStack,
                         MyLoggingFacade myLoggingFacade) {
        super(
                vStack,
                opEvaluator,
                metaEvaluator,
                mtStack,
                miStack,
                myLoggingFacade);
        this.fStack = fStack;
    }

    private static final long serialVersionUID = 0xcafed00d4L;

    public void setFTStack(FStack<? extends FTable<? extends FKey, ? extends FunctionRecord>> fStack) {
        this.fStack = fStack;
    }

    public FStack<? extends FTable> getFTStack() {
        return fStack;
    }


    FStack<? extends FTable<? extends FKey, ? extends FunctionRecord>> fStack = new FStack();

    /**
     * Convenience, just looks up name and arg count
     *
     * @param polyad
     * @return
     */
    public FR_WithState resolveFunction(Polyad polyad, boolean checkForDuplicates) {
        return resolveFunction(polyad.getName(), polyad.getArgCount(), checkForDuplicates);
    }

    public FR_WithState resolveFunction(String name, int argCount, boolean checkForDuplicates) {
        if (name == null || name.isEmpty()) {
            throw new NFWException(("Internal error: The function has not been named"));
        }

        if (getMInstances().isEmpty()) {
            FR_WithState frs = new FR_WithState();

            // Nothing imported, so nothing to look through.
            frs.state = this;
            if (getFTStack().containsKey(new FKey(name, -1))) {
                frs.functionRecord = (FunctionRecord) getFTStack().get(new FKey(name, argCount));

            } else {

                frs.functionRecord = null;
            }

            return frs;
        }
        // check for unqualified names.
        FR_WithState fr = new FR_WithState();
        fr.functionRecord = (FunctionRecord) getFTStack().get(new FKey(name, argCount));

        fr.state = this;
        // if there is an unqualified named function, return it.
        // Note that there cannot ever be an actual new definition of a function in a module
        // since QDLListener will flag FunctionDefinitionStatements as illegal.
        // So e.g.
        //    X#f(x,y)->x*y;
        // will fail.

        if (fr.functionRecord != null) {
            return fr;
        }
        // No UNQ function, so try to find one, but check that it is actually unique.
        if (!isIntrinsic(name)) {
            for (Object xx : getMInstances().keySet()) {
                XKey xkey = (XKey) xx;
                Module module = getMInstances().getModule(xkey);

                if (fr.functionRecord == null) {
                    FunctionRecord tempFR = (FunctionRecord) getImportedModule(xkey.getKey()).getState().getFTStack().get(new FKey(name, argCount));
                    if (tempFR != null) {
                        fr.functionRecord = tempFR;
                        fr.state = module.getState();
                        fr.isExternalModule = module.isExternal();
                        fr.isModule = true;
                        if (!checkForDuplicates) {
                            return fr;
                        }
                    }
                } else {
                    FunctionRecord tempFR = (FunctionRecord) module.getState().getFTStack().get(new FKey(name, argCount));
                    if ((checkForDuplicates) && tempFR != null) {
                        throw new NamespaceException("Error: There are multiple modules with a function named \"" + name + "\". You must fully qualify which one you want.");
                    }
                }
            }

        }
        if (fr.functionRecord == null) {
            // edge case is that it is actually a built-in function reference.
            fr.functionRecord = getFTStack().getFunctionReference(name);
            //fr.isExternalModule = false; // just to be sure.
            if (fr.functionRecord == null) {
                throw new UndefinedFunctionException("Error: No such function named \"" + name + "\" exists with " + argCount + " argument" + (argCount == 1 ? "" : "s"));
            }
        }
        return fr;
    }
     /*
             module_import(module_load('test'))
               module_import('a:/b', 'Y')

           X#u := 5
             X#f(2,3)

  module['a:/b','X'][u := 2;v := 3;times(x,y)->x*y;f(x,y)->times(x,u)+times(y,v);g()->u+v;];
  module_import('a:/b','X')
  module_import('a:/b','Y')
    X#u := 5;X#v := 7
  X#g()
      */


    /**
     * Lists the functions for various components.
     *
     * @param useCompactNotation
     * @param regex
     * @return
     */
    public TreeSet<String> listFunctions(boolean useCompactNotation, String regex,
                                         boolean includeModules,
                                         boolean showIntrinsic) {
        HashSet<XKey> processedAliases = new HashSet<>();
        return listFunctions(useCompactNotation, regex, includeModules, showIntrinsic, processedAliases);
    }

    /**
     * Since multiple aliases may be imported, just stop at the first on in the stack
     * rather then trying to list all of them.
     * @param useCompactNotation
     * @param regex
     * @param includeModules
     * @param showIntrinsic
     * @param processedAliases
     * @return
     */
    protected TreeSet<String> listFunctions(boolean useCompactNotation, String regex,
                                           boolean includeModules,
                                           boolean showIntrinsic,
                                           Set<XKey> processedAliases
    ) {
        TreeSet<String> out = getFTStack().listFunctions(regex);
            // no module templates, so no need to snoop through them
            if ((!includeModules) || getMTemplates().isEmpty()) {
                return out;
            }
            // Get the functions for active (current) module instances
            for (Object key : getMInstances().keySet()) {
                XKey xKey = (XKey) key;

                Module m = ((MIWrapper) getMInstances().get(xKey)).getModule();
        //        processedAliases.add(xKey);
                TreeSet<String> uqFuncs = m.getState().getFTStack().listFunctions( regex);
                for (String x : uqFuncs) {
                    if (isIntrinsic(x) && !showIntrinsic) {
                        continue;
                    }
                    if (useCompactNotation) {
                        out.add(getMInstances().getAliasesAsString(m.getMTKey()) + NS_DELIMITER + x);
                    } else {
                        for (Object alias : getMInstances().getAliasesAsString(m.getMTKey())) {
                            out.add(alias + NS_DELIMITER + x);
                        }
                    }
                }

            }
            return out;
    }
    public List<String> listAllDocumentation() {
        List<String> out = getFTStack().listAllDocs();
        for (Object key : getMTemplates().keySet()) {
            MTKey mtKey = (MTKey) key;
            Module template = (Module) getMTemplates().get(mtKey);
            List<String> uqVars = template.getState().getFTStack().listAllDocs();
            for (String x : uqVars) {
                List<String> aliases = getMInstances().getAliasesAsString(mtKey);
                ;
                if (aliases.size() == 1) {
                    // don't put list notation in if there is no list.
                    out.add(aliases.get(0) + NS_DELIMITER + x);
                } else {
                    out.add(aliases + NS_DELIMITER + x);
                }
            }

        }
/*        for (URI key : getMAliases().keySet()) {
            List<String> uqVars = getMTemplates().get(key).getState().getFTStack().listAllDocs();
            for (String x : uqVars) {
                List<String> aliases = getMAliases().getAliases(key);
                if (aliases.size() == 1) {
                    // don't put list notation in if there is no list.
                    out.add(getMAliases().getAliases(key).get(0) + NS_DELIMITER + x);
                } else {
                    out.add(getMAliases().getAliases(key) + NS_DELIMITER + x);
                }
            }
        }*/
        return out;

    }

    /**
     * This list the documentation templates by their preferred alias
     *
     * @param fname
     * @return
     */
    public List<String> listModuleDoc(String fname) {
        if (!fname.contains(NS_DELIMITER)) {
            return new ArrayList<>(); // no help
        }
        String alias = fname.substring(0, fname.indexOf(NS_DELIMITER));
        XKey xKey = new XKey(alias);
        Module module = getMInstances().getModule(xKey);
        if (module == null) {
            return new ArrayList<>();

        }
/*
        if (!getMTemplates().containsKey(xKey)) {
            return new ArrayList<>();
        }
*/
        //Module module = getMTemplates().get(xKey);
        List<String> docs = module.getListByTag();
        if (docs == null) {
            return new ArrayList<>();
        }
        return docs;
    }

    public List<String> listFunctionDoc(String fname, int argCount) {
        if (fname.contains(NS_DELIMITER)) {
            String alias = fname.substring(0, fname.indexOf(NS_DELIMITER));
            XKey aliasKey = new XKey(alias);
            String realName = fname.substring(1 + fname.indexOf(NS_DELIMITER));
            if (alias == null || alias.isEmpty()) {
                List<String> out;
                if (argCount == -1) {
                    out = getFTStack().listAllDocs(realName);
                } else {
                    out = getFTStack().getDocumentation(new FKey(realName, argCount));
                }
                if (out == null) {
                    return new ArrayList<>();
                }
                return out;
            }
            if (!getMInstances().containsKey(aliasKey)) {
                // so they asked for something that didn't exist
                return new ArrayList<>();
            }
            Module module = (Module) getMInstances().getByAlias(aliasKey);
            List<String> docs;
            if (argCount == -1) {
                docs = module.getState().getFTStack().listAllDocs(realName);
            } else {
                docs = module.getState().getFTStack().getDocumentation(new FKey(realName, argCount));
            }
            if (docs == null) {
                return new ArrayList<>();
            }
            return docs;
            // easy cases.

        }
        // No imports, not qualified, hand back whatever we have
        if (getMInstances().isEmpty()) {
            List<String> out;
            if (argCount == -1) {
                out = getFTStack().listAllDocs(fname);
            } else {
                out = getFTStack().getDocumentation(new FKey(fname, argCount));
            }
            if (out == null) {
                return new ArrayList<>();
            }
            return out;
        }
        // Final case, unqualified name and there are imports. Return all that match.
//        List<String> out = getFTStack().getDocumentation(fname, argCount);
        List<String> out;
        if (argCount == -1) {
            out = getFTStack().listAllDocs(fname);
        } else {
            out = getFTStack().getDocumentation(new FKey(fname, argCount));
        }

        if (out == null) {
            out = new ArrayList<>();
        }
        for (Object key : getMInstances().keySet()) {
            XKey xKey = (XKey) key;
            Module m = getMInstances().getModule(xKey);
            if (m.getState().getFTStack().containsKey(new FKey(fname, argCount))) {
                List<String> doxx;
                if (argCount == -1) {
                    doxx = m.getState().getFTStack().listAllDocs(fname);
                } else {
                    doxx = m.getState().getFTStack().getDocumentation(new FKey(fname, argCount));
                }
                if (doxx == null) {
                    String caput = xKey.getKey() + NS_DELIMITER + fname;
                    if (0 <= argCount) {
                        caput = caput + "(" + argCount + "):";
                    }
                    out.add(caput + " none");
                } else {
                    out.addAll(doxx);
                }
            }
        }
        return out;
    }

}
