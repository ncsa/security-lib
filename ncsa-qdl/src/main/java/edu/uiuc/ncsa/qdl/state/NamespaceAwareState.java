package edu.uiuc.ncsa.qdl.state;

import edu.uiuc.ncsa.qdl.evaluate.*;
import edu.uiuc.ncsa.qdl.exceptions.ImportException;
import edu.uiuc.ncsa.qdl.module.MIStack;
import edu.uiuc.ncsa.qdl.module.MTStack;
import edu.uiuc.ncsa.qdl.variables.VStack;
import edu.uiuc.ncsa.security.core.util.MyLoggingFacade;

import java.util.Arrays;
import java.util.List;


/**
 * This adds the namespace resolution awareness to the state object.
 * <p>Created by Jeff Gaynor<br>
 * on 2/2/20 at  6:40 AM
 */
public abstract class NamespaceAwareState extends ModuleState {
    public NamespaceAwareState(VStack vStack,
                               OpEvaluator opEvaluator,
                               MetaEvaluator metaEvaluator,
                               MTStack mTemplates,
                               MIStack mInstances,
                               MyLoggingFacade myLoggingFacade) {
        super(vStack,
                opEvaluator,
                metaEvaluator,
                mTemplates,
                mInstances,
                myLoggingFacade);
    }
    private static final long serialVersionUID = 0xcafed00d6L;
    /**
     * Delimiter for namespaces. Change this and you will probably break then entire system...
     */
    public static final String NS_DELIMITER = "#";

    public static List<String> RESERVED_NAMESPACE = Arrays.asList(
            MathEvaluator.MATH_NAMESPACE,
            TMathEvaluator.TMATH_NAMESPACE,
            SystemEvaluator.SYS_NAMESPACE,
            IOEvaluator.IO_NAMESPACE,
            StemEvaluator.STEM_NAMESPACE,
            StringEvaluator.STRING_NAMESPACE,
            ListEvaluator.LIST_NAMESPACE);

    /**
     * Checks if the alias  is in fact a reserved namespace. E.g. if the user tries to create an alias of
     * "math" the system should reject it.
     * @param alias
     */
    public void checkReservedAlias(String alias) {
        if (alias == null || alias.isEmpty()) {
            throw new ImportException("the alias is empty or null.");
        }
        if(RESERVED_NAMESPACE.contains(alias)){
            throw new ImportException("the alias \"" + alias + "\" is reserved for system use.");
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

    public String getUNQName(String variable) {
        return variable.substring(variable.indexOf(NS_DELIMITER) + 1);
    }

}
