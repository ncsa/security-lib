package edu.uiuc.ncsa.qdl.expressions;

import edu.uiuc.ncsa.qdl.statements.StatementWithResultInterface;
import edu.uiuc.ncsa.qdl.variables.Constant;
import edu.uiuc.ncsa.qdl.variables.QDLSet;
import edu.uiuc.ncsa.qdl.variables.QDLStem;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 7/1/22 at  4:51 PM
 */
public class IndexArg {
    public boolean interpretListArg = false;

    public IndexArg() {
    }

    public Collection createKeySet() {
        return createKeySet(null);
    }

    public Collection createKeySet(QDLStem in) {
        if ((in != null) && isWildcard()) {
            return in.keySet();
        }
        Object obj = swri.getResult();

        List stemKeys = new ArrayList();
        if (Constant.isScalar(obj)) {
            stemKeys.add(obj);
        }
        if (Constant.isStem(obj)) {
            // NOTE that the stem is contractually a list of indices. Take the values
            QDLStem stem = (QDLStem) obj;
            for (Object key : stem.keySet()) {
                stemKeys.add(stem.get(key));
            }
            return stemKeys;
        }

        if (Constant.isSet(obj)) {
            stemKeys.addAll(((QDLSet) obj));
        }

        return stemKeys;

    }


    public IndexArg(StatementWithResultInterface swri, boolean strictOrder) {
        this.swri = swri;
        this.strictOrder = strictOrder;
    }

    public boolean isWildcard() {
        return swri instanceof AllIndices;
    }

    public StatementWithResultInterface swri;
    public boolean strictOrder = false;

    @Override
    public String toString() {
        return "IndexArg{" +
                "swri='" + swri + "(evaluated? " + swri.isEvaluated() + ")?" + 
                "', strictOrder=" + strictOrder +
                ", isList=" + interpretListArg +
                '}';
    }
}
