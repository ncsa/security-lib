package edu.uiuc.ncsa.security.util.qdl;

import edu.uiuc.ncsa.security.util.TestBase;
import edu.uiuc.ncsa.security.util.qdl.expressions.*;
import edu.uiuc.ncsa.security.util.qdl.util.SymbolTable;
import edu.uiuc.ncsa.security.util.qdl.util.SymbolTableImpl;
import edu.uiuc.ncsa.security.util.qdl.variables.Constant;
import org.junit.Test;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 1/13/20 at  8:11 PM
 */
public class ExpressionTest extends TestBase {
    @Test
    public void testExpression1() throws Exception{
        // test !(a+2)<(b-3) for a = 10, b = 4. Should be TRUE
        SymbolTable symbolTable = new SymbolTableImpl();
        symbolTable.setLongValue("a", 10L);
        symbolTable.setLongValue("b", 4L);
        ConstantNode twoNode = new ConstantNode(2L, Constant.LONG_TYPE);
        ConstantNode threeNode = new ConstantNode(3L, Constant.LONG_TYPE);
        VariableNode aNode = new VariableNode("a", symbolTable);
        VariableNode bNode = new VariableNode("b", symbolTable);
        OpEvaluator opEvaluator = new OpEvaluator();
        // so to make these, ou start at the bottom and assemble as you rise up.
        Dyad aPlus2 = new Dyad(opEvaluator, OpEvaluator.PLUS_VALUE, aNode, bNode);
        Dyad bMinus3 = new Dyad(opEvaluator, OpEvaluator.MINUS_VALUE, bNode, threeNode);
        Dyad lessThanNode = new Dyad(opEvaluator, OpEvaluator.LESS_THAN_VALUE, aPlus2, bMinus3);
        // top node
        Monad notNode = new Monad(opEvaluator, OpEvaluator.NOT_VALUE, lessThanNode);
        notNode.evaluate();
        assert (Boolean)notNode.getResult();
    }
}
