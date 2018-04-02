package edu.uiuc.ncsa.security.util.functor;

import edu.uiuc.ncsa.security.core.exceptions.NFWException;
import edu.uiuc.ncsa.security.util.functor.logic.jElse;
import edu.uiuc.ncsa.security.util.functor.logic.jIf;
import edu.uiuc.ncsa.security.util.functor.logic.jThen;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static edu.uiuc.ncsa.security.util.functor.FunctorTypeImpl.ELSE;

/**
 * This class contains a {@link JFunctor} if-then-else block. You supply a JSONObject, this parses it
 * into its correct elements at runtime.
 * <p>Created by Jeff Gaynor<br>
 * on 2/27/18 at  4:33 PM
 */
public class LogicBlock {
    jIf ifBlock;

    public jElse getElseBlock() {
        return elseBlock;
    }

    public jThen getThenBlock() {
        return thenBlock;
    }

    jElse elseBlock;
    jThen thenBlock;

    JSONObject json;
    JFunctorFactory factory;

    public LogicBlock(JFunctorFactory factory, JSONObject json) {
        this.json = json;
        this.factory = factory;
    }

    /**
     * A jIf functor is a functor with the agreement that the argument in text may consists of a functor (rather than
     * an array with a single functor). This method will enclose the argument in an array if need be.
     * @return
     */
    protected jIf createIfBlock() {
        JSONObject tempJ = new JSONObject();
        Object obj = json.get("$if");
        if(obj instanceof JSONArray){
            tempJ.put("$if", obj); // convert it to a functor
        }
        if(obj instanceof JSONObject){
            // float it to an array
            JSONArray array = new JSONArray();
            array.add(obj);
            tempJ.put("$if", array); // convert it to a functor
        }
        jIf jIf  = (jIf) factory.fromJSON(tempJ);
        return jIf;
    }

    public boolean isIfTrue(){
        if(ifBlock == null) return false;
        return ifBlock.getBooleanResult();
    }
    protected JFunctor createThenOrElseBlock(FunctorTypeImpl type) {
        JFunctor ff = null;
        switch(type){
            case ELSE:
           ff = new jElse();

                break;
            case THEN:
                ff = new jThen();
                break;
        }
        if(ff== null){
               throw new NFWException("Error: Unknown logical type");
        }

        Object obj = json.get(type.getValue());
        JSONArray jsonArray = null;
        if(obj instanceof JSONArray){
            jsonArray = json.getJSONArray(type.getValue());
        }else{
            jsonArray = new JSONArray();
            jsonArray.add(obj);
        }
        List<JFunctor> functors = factory.create(jsonArray);


        ff.addArg(functors);
        return ff;
    }


    protected void initialize() {
        // the assumption is that this object has three elements for if, then and else, plus possibly others.
        if (json.containsKey(FunctorTypeImpl.IF.getValue())) {
            ifBlock = createIfBlock();
        }else{
            throw new IllegalStateException("Error: cannot find if block. Invalid logic block.");
        }
        if (json.containsKey(FunctorTypeImpl.THEN.getValue())) {
            thenBlock = (jThen) createThenOrElseBlock(FunctorTypeImpl.THEN);
        }
        if (json.containsKey(ELSE.getValue())) {
            elseBlock = (jElse) createThenOrElseBlock(ELSE);
        }
    }

    protected void addResults(Object obj){
        if(obj instanceof Collection){
            results.addAll((Collection<?>) obj);
        }else{
            results.add(obj);
        }

    }

    public ArrayList<Object> getResults() {
        return results;
    }

    ArrayList<Object> results = new ArrayList<>();
    public void execute() {
        initialize();
        ifBlock.execute();
        if (ifBlock.getBooleanResult()) {
            thenBlock.execute();
            addResults(thenBlock.getResult());
        } else {
            if (elseBlock != null) {
                elseBlock.execute();
                addResults(elseBlock.getResult());
            }
        }
    }

    @Override
    public String toString() {
        return json.toString();

    }
}
