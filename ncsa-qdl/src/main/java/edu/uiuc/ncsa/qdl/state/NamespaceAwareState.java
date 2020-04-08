package edu.uiuc.ncsa.qdl.state;

import edu.uiuc.ncsa.qdl.evaluate.MetaEvaluator;
import edu.uiuc.ncsa.qdl.evaluate.OpEvaluator;
import edu.uiuc.ncsa.qdl.exceptions.ImportException;
import edu.uiuc.ncsa.qdl.exceptions.NamespaceException;
import edu.uiuc.ncsa.qdl.exceptions.UnknownSymbolException;
import edu.uiuc.ncsa.qdl.module.Module;
import edu.uiuc.ncsa.qdl.module.ModuleMap;
import edu.uiuc.ncsa.qdl.statements.FunctionTable;
import edu.uiuc.ncsa.security.core.util.MyLoggingFacade;

import java.net.URI;

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
     * Takes any function and looks for the NS delimiter, returning a module if there
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
        if (!getImportManager().hasAlias(alias)) {
            throw new UnknownSymbolException("Error: No such alias exists");
        }
        return getImportedModules().get(alias);

    }

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

    /**
     * For and unqualified name, i.e., no #.
     *
     * @param x
     * @return
     */
    public boolean isUNQName(String x) {
        return -1 == x.indexOf(ImportManager.NS_DELIMITER);
    }

    public void checkNSClash(String x) {
        if (isNSQname(x) || isNSQLocalName(x)) return;
        // so it has no qualification.
        if (isNSQname(x)) {
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
        boolean isFound = getSymbolStack().isDefined(x);
        for (URI uri : importManager.keySet()) {
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
        }
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
