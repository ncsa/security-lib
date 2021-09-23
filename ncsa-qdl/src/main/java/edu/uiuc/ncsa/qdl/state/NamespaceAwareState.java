package edu.uiuc.ncsa.qdl.state;

import edu.uiuc.ncsa.qdl.evaluate.MetaEvaluator;
import edu.uiuc.ncsa.qdl.evaluate.OpEvaluator;
import edu.uiuc.ncsa.qdl.module.ModuleMap;
import edu.uiuc.ncsa.security.core.util.MyLoggingFacade;

import static edu.uiuc.ncsa.qdl.state.ImportManager.NS_DELIMITER;

/**
 * This adds the namespace resolution awareness to the state object.
 * <p>Created by Jeff Gaynor<br>
 * on 2/2/20 at  6:40 AM
 */
public abstract class NamespaceAwareState extends AbstractState {
    public NamespaceAwareState(ImportManager resolver,
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
    private static final long serialVersionUID = 0xcafed00d6L;

    public boolean isNSQname(String x) {
        return x.contains(ImportManager.NS_DELIMITER) && !x.startsWith(NS_DELIMITER);
    }

    /**
     * For a NS way to access the local state, a variable of the form <i>#name</i>.
     *
     * @param x
     * @return
     */
    public boolean isNSQLocalName(String x) {
        return x.startsWith(ImportManager.NS_DELIMITER);
    }


    public void checkNSClash(String x) {

/*        if (isNSQname(x) || isNSQLocalName(x)) return;
        // so it has no qualification.
        if (isNSQname(x)) {
            System.out.println("VariableState.checkNSClash: checking for variable '" + x + "'");

            if (getImportManager().hasImports()) {
                return;
            } else {
                throw new ImportException("Error: The variable \"" + x + "\" is qualified but no modules have been imported.");
            }
        }
        if (!getImportManager().hasImports()) {
            // no imports mean no clashes.
            return;
        }
        // so we look at all imported modules for the name.
        boolean isFound = getSymbolStack().isDefined(x);*/
      /*  for (URI uri : importManager.keySet()) {
            Module mm = getModuleMap().get(uri);
            if (mm != null) {
                boolean y = mm.getState().isDefined(x);
                if (isFound) {
                    if (y) {
                        throw new NamespaceException("Error: The variable \"" + x + "\" is defined in multiple modules. " +
                                "You must fully qualify it to use it.");
                    }
                } else {
                    if (y) {
                        isFound = y;
                    }
                }
            }
        }*/
    }

    /**
     * This assumes that there is an alias, so it us not a local name, e.g. #name or unqualified.
     *
     * @param variable
     * @return
     */
    public String getAlias(String variable) {
        return variable.substring(0, variable.indexOf(NS_DELIMITER));
    }

    public String getFQName(String variable) {
        return variable.substring(variable.indexOf(NS_DELIMITER) + 1);
    }

}
