package edu.uiuc.ncsa.security.util.functor;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 2/27/18 at  8:53 AM
 */
public abstract class JFunctorImpl implements JFunctor {
   protected FunctorType type;

    protected JFunctorImpl(FunctorType type) {

        this.type = type;
    }

    protected boolean executed = false;

    public boolean isExecuted() {
        return executed;
    }

    public void clearState() {
        executed = false;
        result = null;
        for (int i = 0; i < getArgs().size(); i++) {
            Object obj = getArgs().get(i);
            if (obj instanceof JFunctorImpl) {
                ((JFunctorImpl) obj).clearState();
            }
        }
    }

    /**
     * This resets the entire state of the functor <b>including erasing the argument list.</b>
     * If you need to clear the executed state and re-run everything, consider invoking {@link #clearState()}.
     */
    public void reset() {
        executed = false;
        result = null;
        args = new ArrayList<>();
    }

    /**
     * This will check that there are args (assumes that the functor <b>requires</b> arguments and that each
     * argument in turn is a functor. This is used in various functors.
     */
    protected void checkArgs() {
        checkArgs(false);
    }

    protected void checkArgs(boolean allowEmptyList) {
        if (!allowEmptyList && args.isEmpty()) {
            throw new IllegalStateException("Error: no arguments");
        }
        for (int i = 0; i < args.size(); i++) {
            if (!(args.get(i) instanceof JFunctorImpl)) {
                throw new IllegalStateException("Error: argument is not a functor");
            }
        }

    }

    @Override
    public void addArg(List<JFunctor> functors) {
        args.addAll(functors);
    }

    protected Object result;
    protected ArrayList<Object> args = new ArrayList<Object>();

    @Override
    public Object getResult() {
        return result;
    }

    public boolean getBooleanResult() {
        if (result == null) {
            throw new NullPointerException();
        }
        if (result instanceof Boolean) {
            return (Boolean) result;
        }
        throw new IllegalStateException("The result is of type \"" + result.getClass().getSimpleName() + "\", not boolean.");
    }

    public String getStringResult() {
        if (result == null) {
            return null;
        }
        return result.toString();
    }

    public int getIntResult() {
        if (result == null) {
            throw new NullPointerException();
        }
        if (result instanceof Integer) {
            return (Integer) result;
        }
        throw new IllegalStateException("The result is of type \"" + result.getClass().getSimpleName() + "\", not integer.");
    }


    @Override
    public ArrayList<Object> getArgs() {
        return args;
    }

    public void addArg(String x) {
        getArgs().add(x);
    }

    public void addArg(JFunctor x) {
        getArgs().add(x);
    }
    public void addArg(JMetaFunctor x) {
        getArgs().add(x);
    }

    public void addArg(Integer x) {
        getArgs().add(x);
    }

    public void addArg(Boolean x) {
        getArgs().add(x);
    }


    @Override
    public String getName() {
        return type.getValue();
    }

    public JSONObject toJSON() {
        JSONObject json = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        for (int i = 0; i < args.size(); i++) {
            Object obj = args.get(i);
            if(obj == null) {

            }else{
                // null objects are possible. intercept it here or it will be rendered as the string "null" by the
                // JSON library.
                if (obj instanceof JMetaFunctor) {
                    JMetaFunctor ff = (JMetaFunctor) obj;
                    jsonArray.add(ff.toJSON());
                } else {
                    jsonArray.add(obj);
                }
            }
        }
        json.put(getName(), jsonArray);
        return json;
    }

    @Override
    public String toString() {
        String out = toJSON().toString();
        return out;
    }
}
