package edu.uiuc.ncsa.security.util.functor;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.util.ArrayList;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 2/27/18 at  8:53 AM
 */
public abstract class JFunctorImpl implements JFunctor {
    protected JFunctorImpl(String name) {
        this.name = name;
    }
    protected boolean executed = false;

    public boolean isExecuted() {
        return executed;
    }

    public void reset(){
        executed = false;
        result = null;
        args = new ArrayList<>();
    }

    protected void checkArgs() {
        if (args.isEmpty()) {
            throw new IllegalStateException("Error: no arguments");
        }
        for (int i = 0; i < args.size(); i++) {
            if (!(args.get(i) instanceof JFunctorImpl)) {
                throw new IllegalStateException("Error: argument is not a functor");
            }
        }
    }

    protected Object result;
    protected ArrayList<Object>args = new ArrayList<Object>();

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

    public void addArg(Integer x) {
        getArgs().add(x);
    }

    public void addArg(Boolean x) {
        getArgs().add(x);
    }

    protected String name;

    @Override
    public String getName() {
        return name;
    }

    public JSONObject toJSON(){
        JSONObject json = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        for(int i = 0; i < args.size(); i++){
            Object obj = args.get(i);
            if(obj instanceof JFunctorImpl){
                JFunctorImpl ff = (JFunctorImpl) obj;
                jsonArray.add(ff.toJSON());
            }else{
                jsonArray.add(obj);
            }
        }
        json.put(getName(), jsonArray);
        return json;
    }
}
