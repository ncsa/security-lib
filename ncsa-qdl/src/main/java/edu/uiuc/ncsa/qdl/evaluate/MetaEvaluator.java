package edu.uiuc.ncsa.qdl.evaluate;

import edu.uiuc.ncsa.qdl.exceptions.UndefinedFunctionException;
import edu.uiuc.ncsa.qdl.expressions.Polyad;
import edu.uiuc.ncsa.qdl.state.State;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TreeSet;

/**
 * This is charged with managing the build-in functions as well as any that the
 * user defines.
 * <p>Created by Jeff Gaynor<br>
 * on 1/16/20 at  6:14 AM
 */
/*

This inherits from all the other built in functions and does very little except be a facade for them.
 */
public class MetaEvaluator extends AbstractFunctionEvaluator {
    /**
     * Factory method. You should not override this class to add more of your own evaluators. Just
     * get the instance and {@link #addEvaluator(AbstractFunctionEvaluator)} to it. It will snoop
     * through your evaluator too. If you are writing your own evaluator, start your type numbers
     * should be negative.
     *
     * @return
     */
    public static MetaEvaluator getInstance() {
        if (metaEvaluator == null) {
            metaEvaluator = new MetaEvaluator();
            metaEvaluator.addEvaluator(new StringEvaluator());
            metaEvaluator.addEvaluator(new StemEvaluator());
            metaEvaluator.addEvaluator(new IOEvaluator());
            metaEvaluator.addEvaluator(new MathEvaluator());
            metaEvaluator.addEvaluator(new ControlEvaluator());
            metaEvaluator.addEvaluator(new FunctionEvaluator());
        }
        return metaEvaluator;
    }


    public static void setMetaEvaluator(MetaEvaluator metaEvaluator) {
        MetaEvaluator.metaEvaluator = metaEvaluator;
    }

    static MetaEvaluator metaEvaluator = null;

    public static final int META_BASE_VALUE = 6000;
    List<AbstractFunctionEvaluator> evaluators = new ArrayList<>();

    public void addEvaluator(AbstractFunctionEvaluator evaluator) {
        evaluators.add(evaluator);
    }


    @Override
    public int getType(String name) {
        for (AbstractFunctionEvaluator evaluator : evaluators) {
            int type = evaluator.getType(name);
            if (type != UNKNOWN_VALUE) {
                return type;
            }
        }
        return EvaluatorInterface.UNKNOWN_VALUE;
    }

    @Override
    public boolean evaluate(Polyad polyad, State state) {
        for (AbstractFunctionEvaluator evaluator : evaluators) {
            if (evaluator.evaluate(polyad, state)) return true;
        }
        throw new UndefinedFunctionException("Unknown function '" + polyad.getName() + "'.");
    }


    public TreeSet<String> listFunctions(boolean listFQ) {
        TreeSet<String> names = new TreeSet<>();
        for (AbstractFunctionEvaluator evaluator : evaluators) {
            names.addAll(evaluator.listFunctions(listFQ));
        }
        return names;
    }

    String[] allNames = null;

    @Override
    public String[] getFunctionNames() {
        if (allNames == null) {
            ArrayList<String> namesList = new ArrayList<>();
            for (AbstractFunctionEvaluator evaluator : evaluators) {
                namesList.addAll(Arrays.asList(evaluator.getFunctionNames()));
            }
            allNames = new String[namesList.size()];
            int i = 0;
            for (String x : namesList) {
                allNames[i++] = x;
            }
        }
        return allNames;
    }

}
