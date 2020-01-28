package edu.uiuc.ncsa.qdl.extensions.example;

import edu.uiuc.ncsa.qdl.extensions.QDLVariable;
import edu.uiuc.ncsa.qdl.util.StemVariable;

import java.math.BigDecimal;
import java.util.Date;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 1/27/20 at  1:26 PM
 */
public class EGStem implements QDLVariable {
    @Override
    public String getName() {
        return "eg.";
    }

    @Override
    public Object getValue() {
        StemVariable stemVariable = new StemVariable();
        stemVariable.put("time", "Current time is " + new Date());
        stemVariable.put("long", 456456546L);
        stemVariable.put("decimal", new BigDecimal("3455476.987654567654567"));
        stemVariable.put("boolean", Boolean.TRUE);
        StemVariable nestedStem = new StemVariable();
        nestedStem.put("0", 0L);
        nestedStem.put("1", 1L);
        nestedStem.put("2", 2L);
        nestedStem.put("3", 3L);
        stemVariable.put("list", nestedStem);
        return stemVariable;
    }
}
