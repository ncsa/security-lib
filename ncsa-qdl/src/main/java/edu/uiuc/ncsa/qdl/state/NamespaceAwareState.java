package edu.uiuc.ncsa.qdl.state;

import edu.uiuc.ncsa.qdl.evaluate.MetaEvaluator;
import edu.uiuc.ncsa.qdl.evaluate.OpEvaluator;
import edu.uiuc.ncsa.qdl.module.MTemplates;
import edu.uiuc.ncsa.security.core.util.MyLoggingFacade;

import static edu.uiuc.ncsa.qdl.module.MAliases.NS_DELIMITER;

/**
 * This adds the namespace resolution awareness to the state object.
 * <p>Created by Jeff Gaynor<br>
 * on 2/2/20 at  6:40 AM
 */
public abstract class NamespaceAwareState extends AbstractState {
    public NamespaceAwareState(edu.uiuc.ncsa.qdl.module.MAliases mAliases,
                               SymbolStack symbolStack,
                               OpEvaluator opEvaluator,
                               MetaEvaluator metaEvaluator,
                               MTemplates MTemplates,
                               MyLoggingFacade myLoggingFacade) {
        super(mAliases,
                symbolStack,
                opEvaluator,
                metaEvaluator,
                MTemplates,
                myLoggingFacade);
    }
    private static final long serialVersionUID = 0xcafed00d6L;



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
