package edu.uiuc.ncsa.qdl.variables;

import edu.uiuc.ncsa.qdl.state.State;
import edu.uiuc.ncsa.qdl.statements.StatementWithResultInterface;
import edu.uiuc.ncsa.security.core.exceptions.NFWException;
import net.sf.json.JSONObject;

import java.util.ArrayList;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 9/28/20 at  1:28 PM
 */
public class StemListNode implements StatementWithResultInterface {
    StemVariable result;

    @Override
    public Object getResult() {
        return result;
    }

    @Override
    public void setResult(Object object) {
        if (!(result instanceof StemVariable)) {
            throw new IllegalStateException("error: cannot set a " + getClass().getSimpleName() + " to type " + object.getClass().getSimpleName());
        }

    }

    @Override
    public int getResultType() {
        return Constant.STEM_TYPE;
    }

    @Override
    public void setResultType(int type) {
        // No op, actually, since this only returns a single type of object.
        if(type != Constant.STEM_TYPE){
            throw new NFWException("Internal error: Attempt to set stem to type = " + type);
        }
    }

    boolean evaluated = false;

    @Override
    public boolean isEvaluated() {
        return evaluated;
    }

    @Override
    public void setEvaluated(boolean evaluated) {
        this.evaluated = evaluated;
    }

    public ArrayList<StatementWithResultInterface> getStatements() {
        return statements;
    }

    public void setStatements(ArrayList<StatementWithResultInterface> statements) {
        this.statements = statements;
    }

    ArrayList<StatementWithResultInterface> statements = new ArrayList<>();

    @Override
    public Object evaluate(State state) {
        result = new StemVariable();
        long i = 0;
        for (StatementWithResultInterface stmt : statements) {
            stmt.evaluate(state);
            stmt.setEvaluated(true);
            stmt.setResultType(Constant.getType(stmt.getResult()));
            result.put(i++, stmt.getResult());
        }

        return result;
    }

    String sourceCode = "";

    @Override
    public String getSourceCode() {
        return sourceCode;
    }

    @Override
    public void setSourceCode(String sourceCode) {
        this.sourceCode = sourceCode;
    }

    @Override
    public StatementWithResultInterface makeCopy() {
        StemListNode newSLN = new StemListNode();
        for(StatementWithResultInterface s : statements){
            newSLN.getStatements().add(s.makeCopy());
        }
        StemVariable newStem = new StemVariable();

        // Kludge, but it works.
        newStem.fromJSON((JSONObject)((StemVariable)getResult()).toJSON());
        newSLN.setResult(newStem);
        newSLN.setSourceCode(getSourceCode());
        newSLN.setEvaluated(isEvaluated());
        return newSLN;
    }
}
