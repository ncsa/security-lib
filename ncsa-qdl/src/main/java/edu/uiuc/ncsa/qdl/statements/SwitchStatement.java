package edu.uiuc.ncsa.qdl.statements;

import edu.uiuc.ncsa.qdl.state.State;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 1/17/20 at  3:42 PM
 */
public class SwitchStatement implements Statement {


    public List<ConditionalStatement> getArguments() {
        return arguments;
    }

    public void setArguments(List<ConditionalStatement> arguments) {
        this.arguments = arguments;
    }

    List<ConditionalStatement> arguments = new ArrayList<>();
    @Override
    public Object evaluate(State state) {

        for(ConditionalStatement c : getArguments()){
            if((Boolean)c.conditional.evaluate(state)){
                c.evaluate(state);
                return Boolean.TRUE;
            }
        }
        return Boolean.FALSE;
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
}
