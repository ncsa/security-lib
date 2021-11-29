package edu.uiuc.ncsa.qdl.state;

import edu.uiuc.ncsa.qdl.evaluate.MetaEvaluator;
import edu.uiuc.ncsa.qdl.evaluate.OpEvaluator;
import edu.uiuc.ncsa.qdl.exceptions.NamespaceException;
import edu.uiuc.ncsa.qdl.exceptions.UndefinedFunctionException;
import edu.uiuc.ncsa.qdl.expressions.Polyad;
import edu.uiuc.ncsa.qdl.functions.FR_WithState;
import edu.uiuc.ncsa.qdl.functions.FTStack;
import edu.uiuc.ncsa.qdl.functions.FunctionRecord;
import edu.uiuc.ncsa.qdl.module.MTemplates;
import edu.uiuc.ncsa.qdl.module.Module;
import edu.uiuc.ncsa.security.core.exceptions.NFWException;
import edu.uiuc.ncsa.security.core.util.MyLoggingFacade;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import static edu.uiuc.ncsa.qdl.module.MAliases.NS_DELIMITER;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 2/2/20 at  6:48 AM
 */
public abstract class FunctionState extends VariableState {
    public FunctionState(edu.uiuc.ncsa.qdl.module.MAliases mAliases,
                         SymbolStack symbolStack,
                         OpEvaluator opEvaluator,
                         MetaEvaluator metaEvaluator,
                         FTStack ftStack,
                         MTemplates MTemplates,
                         MyLoggingFacade myLoggingFacade) {
        super(mAliases,
                symbolStack,
                opEvaluator,
                metaEvaluator,
                MTemplates,
                myLoggingFacade);
        this.ftStack = ftStack;
    }

    private static final long serialVersionUID = 0xcafed00d4L;

    @Override
    public FTStack getFTStack() {
        return ftStack;
    }

    public void setFTStack(FTStack ftStack) {
        this.ftStack = ftStack;
    }

    FTStack ftStack = new FTStack();

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

        if (!getMAliases().hasImports()) {
            FR_WithState frs = new FR_WithState();

            // Nothing imported, so nothing to look through.
            frs.state = this;
            if(getFTStack().containsKey(name, -1)){
                frs.functionRecord = getFTStack().get(name, argCount);

            }else{

                frs.functionRecord = null;
            }
/*
            if (argCount == -1) {
                frs.functionRecord = getFTStack().hasA(name);
            } else {
                frs.functionRecord = getFTStack().get(name, argCount);
            }
*/
            return frs;
        }
        // check for unqualified names.
        FR_WithState fr = new FR_WithState();
        fr.functionRecord = getFTStack().get(name, argCount);

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
        if (!isPrivate(name)) {
            for (String alias : getmInstances().keySet()) {
                if (fr.functionRecord == null) {
                    FunctionRecord tempFR = getImportedModule(alias).getState().getFTStack().get(name, argCount);
                    if (tempFR != null) {
                        fr.functionRecord = tempFR;
                        fr.state = getmInstances().get(alias).getState();
                        fr.isExternalModule = getmInstances().get(alias).isExternal();
                        fr.isModule = true;
                        if (!checkForDuplicates) {
                            return fr;
                        }
                    }
                } else {
                    FunctionRecord tempFR = mInstances.get(alias).getState().getFTStack().get(name, argCount);
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
     * @param useCompactNotation
     * @param regex
     * @return
     */
    public TreeSet<String> listFunctions(boolean useCompactNotation, String regex,
                                         boolean includeModules,
                                         boolean showIntrinsic) {
        TreeSet<String> out = getFTStack().listFunctions(regex);
        // no module templates, so no need to snoop through them
        if ((!includeModules) || getModuleMap().isEmpty()) {
            return out;
        }
        for (URI key : getMAliases().keySet()) {
            Module mm = getModuleMap().get(key);
            TreeSet<String> uqVars = mm.getState().listFunctions(useCompactNotation, regex, true, showIntrinsic);
            for (String x : uqVars) {
                if (isPrivate(x)&& !showIntrinsic) {
                    continue;
                }
                if (useCompactNotation) {
                    out.add(getMAliases().getAlias(key) + NS_DELIMITER + x);
                } else {
                    for (String alias : getMAliases().getAlias(key)) {
                        out.add(alias + NS_DELIMITER + x);
                    }
                }
            }
        }
        return out;
    }

    public List<String> listAllDocumentation() {
        List<String> out = getFTStack().listAllDocs();
        for (URI key : getMAliases().keySet()) {
            List<String> uqVars = getModuleMap().get(key).getState().getFTStack().listAllDocs();
            for (String x : uqVars) {
                List<String> aliases = getMAliases().getAlias(key);
                if (aliases.size() == 1) {
                    // don't put list notation in if there is no list.
                    out.add(getMAliases().getAlias(key).get(0) + NS_DELIMITER + x);
                } else {
                    out.add(getMAliases().getAlias(key) + NS_DELIMITER + x);
                }
            }
        }
        return out;

    }

    public List<String> listModuleDoc(String fname) {
        if (!fname.contains(NS_DELIMITER)) {
            return new ArrayList<>(); // no help
        }
        String alias = fname.substring(0, fname.indexOf(NS_DELIMITER));

        if (!getModuleMap().containsKey(alias)) {
            return new ArrayList<>();
        }
        Module module = getModuleMap().get(alias);
        List<String> docs = module.getDocumentation();
        if (docs == null) {
            return new ArrayList<>();
        }
        return docs;
    }

    public List<String> listFunctionDoc(String fname, int argCount) {
        if (fname.contains(NS_DELIMITER)) {
            String alias = fname.substring(0, fname.indexOf(NS_DELIMITER));
            String realName = fname.substring(1 + fname.indexOf(NS_DELIMITER));
            if (alias == null || alias.isEmpty()) {
                List<String> out;
                if (argCount == -1) {
                    out = getFTStack().listAllDocs(realName);
                } else {
                    out = getFTStack().getDocumentation(realName, argCount);
                }
                if (out == null) {
                    return new ArrayList<>();
                }
                return out;
            }
            if (!MAliases.hasAlias(alias)) {
                // so they asked for something that didn't exist
                return new ArrayList<>();
            }
            URI ns = MAliases.getByAlias(alias);
            List<String> docs;
            if (argCount == -1) {
                docs = getModuleMap().get(ns).getState().getFTStack().listAllDocs(realName);
            } else {
                docs = getModuleMap().get(ns).getState().getFTStack().getDocumentation(realName, argCount);
            }
            if (docs == null) {
                return new ArrayList<>();
            }
            return docs;
            // easy cases.

        }
        // No imports, not qualified, hand back whatever we have
        if (!MAliases.hasImports()) {
            List<String> out;
            if (argCount == -1) {
                out = getFTStack().listAllDocs(fname);
            } else {
                out = getFTStack().getDocumentation(fname, argCount);
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
            out = getFTStack().getDocumentation(fname, argCount);
        }

        if (out == null) {
            out = new ArrayList<>();
        }
        for (URI key : getMAliases().keySet()) {
            if (getModuleMap().get(key).getState().getFTStack().containsKey(fname, argCount)) {
                List<String> doxx;
                if (argCount == -1) {
                    doxx = getModuleMap().get(key).getState().getFTStack().listAllDocs(fname);
                } else {
                    doxx = getModuleMap().get(key).getState().getFTStack().getDocumentation(fname, argCount);
                }
                if (doxx == null) {
                    String caput = getMAliases().getAlias(key) + NS_DELIMITER + fname;
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
