package edu.uiuc.ncsa.qdl.evaluate;

import edu.uiuc.ncsa.qdl.exceptions.UndefinedFunctionException;
import edu.uiuc.ncsa.qdl.expressions.Polyad;
import edu.uiuc.ncsa.qdl.parsing.QDLParser;
import edu.uiuc.ncsa.qdl.state.State;
import edu.uiuc.ncsa.qdl.util.StemVariable;
import edu.uiuc.ncsa.qdl.variables.Constant;
import edu.uiuc.ncsa.security.core.configuration.XProperties;
import edu.uiuc.ncsa.security.core.exceptions.GeneralException;

import java.util.ArrayList;
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


    public static String EXECUTE = "execute";
    public static final int EXECUTE_TYPE = 101 + META_BASE_VALUE;

    @Override
    public int getType(String name) {
        for (AbstractFunctionEvaluator evaluator : evaluators) {
            int type = evaluator.getType(name);
            if (type != UNKNOWN_VALUE){ return type;}
        }
        if (name.equals(EXECUTE)) return EXECUTE_TYPE;
        return EvaluatorInterface.UNKNOWN_VALUE;
    }

    @Override
    public boolean evaluate(Polyad polyad, State state) {
        for (AbstractFunctionEvaluator evaluator : evaluators) {
            if (evaluator.evaluate(polyad,state)) return true;
        }
        switch (polyad.getOperatorType()) {
            case EXECUTE_TYPE:
                doExecute(polyad, state);
                return true;
        }
        throw new UndefinedFunctionException("Unknown function \"" + polyad.getName() + "\".");
    }

    protected void doExecute(Polyad polyad, State state) {
        // execute a string.
        if (polyad.getArgumments().size() != 1) {
            throw new IllegalArgumentException("Error. Wrong number of arguments. " +
                    "This requires a single argument that is a string or a list of them.");
        }
        Object result = polyad.evalArg(0,state);;
        StemVariable stem = null;

        if (isString(result)) {
            stem = new StemVariable();
            stem.put("0", result); // dummy argument
        }
        if (isStemList(result)) {
            stem = (StemVariable) result;
        }
        if (stem == null) {
            throw new IllegalArgumentException("No executable argument found.");
        }


        QDLParser p = new QDLParser(new XProperties(), state);
        for (int i = 0; i < stem.size(); i++) {
            String currentIndex = Integer.toString(i);
            if (!stem.containsKey(currentIndex)) {
                return;
            }
            try {
                p.execute(stem.getString(Integer.toString(i)));
            } catch (Throwable throwable) {
                throwable.printStackTrace();
                throw new GeneralException("Error during execution at index " + i + ".", throwable);
            }
        }
        polyad.setResultType(Constant.BOOLEAN_TYPE);
        polyad.setResult(Boolean.TRUE);
        polyad.setEvaluated(true);

    }

    public TreeSet<String> listFunctions() {
          TreeSet<String> names = new TreeSet<>();
          for(AbstractFunctionEvaluator evaluator: evaluators){
              names.addAll(evaluator.listFunctions());
          }
          return names;
      }

}
