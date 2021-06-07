package edu.uiuc.ncsa.qdl.variables;

import edu.uiuc.ncsa.qdl.state.State;
import edu.uiuc.ncsa.qdl.statements.HasResultInterface;
import edu.uiuc.ncsa.qdl.statements.StatementWithResultInterface;
import edu.uiuc.ncsa.security.core.exceptions.NFWException;
import edu.uiuc.ncsa.security.core.exceptions.NotImplementedException;
import net.sf.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * This is used in parsing. It holds the result of a direct creation of a stem or list
 *
 * <p>Created by Jeff Gaynor<br>
 * on 9/28/20 at  10:57 AM
 */
public class StemVariableNode implements StatementWithResultInterface {
    public String getVariableReference() {
        return variableReference;
    }

    public void setVariableReference(String variableReference) {
        this.variableReference = variableReference;
    }

    String variableReference;
    StemVariable result = new StemVariable();

    public ArrayList<StemEntryNode> getStatements() {
        return statements;
    }

    public void setStatements(ArrayList<StemEntryNode> statements) {
        this.statements = statements;
    }

    ArrayList<StemEntryNode> statements = new ArrayList<>();

    @Override
    public Object getResult() {
        return result;
    }

    @Override
    public void setResult(Object object) {
        throw new NotImplementedException("Error: Not implements");
    }

    @Override
    public int getResultType() {
        return Constant.STEM_TYPE;
    }

    @Override
    public void setResultType(int type) {
        if (type != Constant.STEM_TYPE) {
            throw new NFWException("error: Attempt to reset stem to type " + type);
        }
        // result type is fixed since these are created to only manage stems
    }

    @Override
    public boolean isEvaluated() {
        return true;
    }

    @Override
    public void setEvaluated(boolean evaluated) {

    }

    @Override
    public Object evaluate(State state) {
        result = new StemVariable();
        for (StemEntryNode sen : getStatements()) {
            sen.evaluate(state);
            Object value = ((HasResultInterface) sen.getValue()).getResult();

            if (sen.isDefaultValue) {
                result.setDefaultValue(value);
                return result;
            }

            StatementWithResultInterface keyRI = sen.getKey();

            switch (keyRI.getResultType()) {
                case Constant.LONG_TYPE:
                    result.put((Long) keyRI.getResult(), value);
                    break;
                case Constant.STRING_TYPE:
                case Constant.DECIMAL_TYPE:

                    result.put(keyRI.getResult().toString(), value);
                    break;

                default:
                    throw new IllegalArgumentException("Error: Illegal type for key \"" + keyRI.getResult() + "\"");
            }
        }
        return result;
    }

    List<String> sourceCode;

    @Override
    public List<String> getSourceCode() {
        return sourceCode;
    }

    @Override
    public void setSourceCode(List<String> sourceCode) {
        this.sourceCode = sourceCode;
    }

    @Override
    public StatementWithResultInterface makeCopy() {
        StemVariableNode newSVN = new StemVariableNode();
        for (StemEntryNode s : statements) {
            newSVN.getStatements().add((StemEntryNode) s.makeCopy());
        }
        StemVariable newStem = new StemVariable();

        // Kludge, but it works.
        newStem.fromJSON((JSONObject) ((StemVariable) getResult()).toJSON());
        newSVN.setResult(newStem);
        newSVN.setSourceCode(getSourceCode());
        newSVN.setEvaluated(isEvaluated());
        return newSVN;
    }
}
