package edu.uiuc.ncsa.security.util.functor;

import edu.uiuc.ncsa.security.util.functor.logic.jElse;
import edu.uiuc.ncsa.security.util.functor.logic.jIf;
import edu.uiuc.ncsa.security.util.functor.logic.jThen;
import net.sf.json.JSONObject;

/**
 * This class contains a {@link JFunctor} if-then-else block. You supply a JSONObject, this parses it
 * into its correct elements,
 * <p>Created by Jeff Gaynor<br>
 * on 2/27/18 at  4:33 PM
 */
public class LogicBlock {
    jIf ifBlock;
    jElse elseBlock;
    jThen thenBlock;

    JSONObject json;

    public LogicBlock(JSONObject json) {
        this.json = json;
    }

    protected JFunctor createBlock(String component) {
        JFunctor ff = null;
        if (json.containsKey(component)) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(component, json.getJSONArray(component));
            ff = JFunctorFactory.create(jsonObject);
        }
        return ff;
    }

    boolean isInitialized = false;

    protected void initialize() {
        // the assumption is that this object has three elements for if, then and else, plus possibly others.
        if (json.containsKey("$if")) {
            ifBlock = (jIf) createBlock("$if");
        }
        if (json.containsKey("$then")) {
            thenBlock = (jThen) createBlock("$then");
        }
        if (json.containsKey("$else")) {
            elseBlock = (jElse) createBlock("$else");
        }
        isInitialized = (ifBlock != null) && (thenBlock != null || elseBlock != null);
    }

    public void execute() {
        if (!isInitialized) {
            throw new IllegalStateException("Error: this component has not been initialized yet");
        }
        ifBlock.execute();
        if (ifBlock.getBooleanResult()) {
            thenBlock.execute();
        } else {
            if (elseBlock != null) {
                elseBlock.execute();
            }
        }
    }
}
