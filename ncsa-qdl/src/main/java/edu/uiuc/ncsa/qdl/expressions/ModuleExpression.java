package edu.uiuc.ncsa.qdl.expressions;

import edu.uiuc.ncsa.qdl.exceptions.ImportException;
import edu.uiuc.ncsa.qdl.exceptions.IntrinsicViolation;
import edu.uiuc.ncsa.qdl.exceptions.UnknownSymbolException;
import edu.uiuc.ncsa.qdl.module.Module;
import edu.uiuc.ncsa.qdl.state.State;
import edu.uiuc.ncsa.qdl.statements.StatementWithResultInterface;
import edu.uiuc.ncsa.qdl.variables.Constant;

/**
 * Models a single module expression of the form <b>A</b>#<i>expression</i> where <b>A</b>
 * is the alias giving the instance of the current module
 * and <i>expression</i> is a general expression to be evaluated against
 * the state of the module.
 * <p>Created by Jeff Gaynor<br>
 * on 9/23/21 at  6:10 AM
 */
public class ModuleExpression extends ExpressionImpl {

    public ModuleExpression() {
    }

    public ModuleExpression(int operatorType) {
        super(operatorType);
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    String alias;

    @Override
    public Object evaluate(State state) {
        if (state.getMetaEvaluator().isSystemNS(getAlias())) {
            // In this case, it is a built in function and there are no constants
            // or variables defined in those modules.
            if (getExpression() instanceof ConstantNode) {
                ConstantNode cNode = (ConstantNode)getExpression();
                setResult(cNode.getResult());
                setResultType(cNode.getResultType());
                setEvaluated(true);
                return getResult();
            }
            if (getExpression() instanceof VariableNode) {
                throw new UnknownSymbolException("variable not found");
            }
            if (getExpression() instanceof Polyad) {
                state.getMetaEvaluator().evaluate(getAlias(), (Polyad) getExpression(), state);
            } else {
                // Since this should not happen if the parser is working right, it implies
                // that something in the parser changed and non-expressions are not
                // being sent along.
                throw new IllegalArgumentException("cannot evaluate expression '" + getExpression().getSourceCode() + "' in this module");
            }
            setResult(getExpression().getResult());
            setResultType(getExpression().getResultType());
            setEvaluated(true);
            return getResult();
        }
        if (!state.getMAliases().hasImports()) {
            throw new ImportException("module '" + getAlias() + "' not found");
        }
        // no module state means to look at global state to find the module state.
        if (getExpression() instanceof ModuleExpression) {
            ModuleExpression nextME = (ModuleExpression) getExpression();
            if (getModuleState(state).getmInstances().containsKey(nextME.getAlias())) {
                nextME.setModuleState(getModuleState(state).getmInstances().get(nextME.getAlias()).getState());
            }
        }
        getExpression().setAlias(getAlias());
        Object result = getExpression().evaluate(getLocalState(state));
        setResult(result);
        setResultType(Constant.getType(result));
        setEvaluated(true);
        return result;
    }

    public StatementWithResultInterface getExpression() {
        if (getArguments().isEmpty()) {
            throw new IllegalStateException("no expression set for module reference");
        }
        return getArguments().get(0);
    }

    public void setExpression(StatementWithResultInterface statementWithResultInterface) {
        if (getArguments().isEmpty()) {
            getArguments().add(statementWithResultInterface);
        } else {
            getArguments().set(0, statementWithResultInterface);
        }
    }

    @Override
    public StatementWithResultInterface makeCopy() {
        ModuleExpression moduleExpression = new ModuleExpression();
        moduleExpression.setAlias(getAlias());
        moduleExpression.setExpression(getExpression().makeCopy());
        moduleExpression.setModuleState(getModuleState(null)); // can't be cloned
        return moduleExpression;
    }

    /**
     * The state of the module only. This is used to construct the local state.
     *
     * @return
     */
    public State getModuleState(State state) {
        if (state == null) {
            return null;
        }
        if (moduleState == null) {
            if (!state.getmInstances().containsKey(getAlias())) {
                throw new IllegalArgumentException("no module named \"" + getAlias() + "\" was  imported");
            }
            Module module = state.getmInstances().get(getAlias());
            setModuleState(module.getState());
        }
        return moduleState;
    }

    public void setModuleState(State moduleState) {
        this.moduleState = moduleState;
    }

    public State getLocalState(State state) {
        return state.newStateWithImports(getModuleState(state));
    }

    State moduleState;

    /**
     * Set the value of this expression using the state
     *
     * @param newValue
     */
    public void set(State state, Object newValue) {
        if (getExpression() instanceof VariableNode) {
            VariableNode vNode = (VariableNode) getExpression();
            String variableName = vNode.getVariableReference();
            if (state.isPrivate(variableName) && !getModuleState(state).isDefined(variableName)) {
                // check that if this is private
                throw new IntrinsicViolation("cannot set an intrinsic variable");
            }
            if (getModuleState(state).isDefined(variableName)) {
                State localState = getLocalState(state);
                localState.setValue(variableName, newValue);
            } else {
                throw new IllegalArgumentException("Cannot define new variables in a module.");
            }
            return;
        }
        if (getExpression() instanceof ConstantNode) {
            throw new IllegalArgumentException("cannot assign a constant a value.");
        }
        if (getExpression() instanceof ModuleExpression) {
            ModuleExpression nextME = (ModuleExpression) getExpression();
            if (getModuleState(state).getmInstances().containsKey(nextME.getAlias())) {
                nextME.setModuleState(getModuleState(state).getmInstances().get(nextME.getAlias()).getState());
            }
            ((ModuleExpression) getExpression()).set(state, newValue);
            return;
        }
        throw new IllegalArgumentException("unkown left assignment argument.");
    }
}
