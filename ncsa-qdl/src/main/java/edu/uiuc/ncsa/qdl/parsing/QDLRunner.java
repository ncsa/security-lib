package edu.uiuc.ncsa.qdl.parsing;

import edu.uiuc.ncsa.qdl.evaluate.MetaEvaluator;
import edu.uiuc.ncsa.qdl.evaluate.OpEvaluator;
import edu.uiuc.ncsa.qdl.module.ModuleMap;
import edu.uiuc.ncsa.qdl.state.NamespaceResolver;
import edu.uiuc.ncsa.qdl.state.State;
import edu.uiuc.ncsa.qdl.state.SymbolStack;
import edu.uiuc.ncsa.qdl.state.SymbolTableImpl;
import edu.uiuc.ncsa.qdl.statements.Element;
import edu.uiuc.ncsa.qdl.statements.FunctionTable;
import edu.uiuc.ncsa.qdl.statements.ModuleStatement;
import edu.uiuc.ncsa.qdl.statements.Statement;

import java.util.List;

/**
 * /**
 * The main class that runs a parse tree
 * <p>Created by Jeff Gaynor<br>
 * on 1/22/20 at  6:15 AM
 */
public class QDLRunner {
    NamespaceResolver namespaceResolver = NamespaceResolver.getResolver();

    public State getState() {
        if (state == null) {
            SymbolStack stack = new SymbolStack(namespaceResolver);
            SymbolTableImpl symbolTable = new SymbolTableImpl(namespaceResolver);
            stack.addParent(symbolTable);
            state = new State(NamespaceResolver.getResolver(),
                    stack,
                    new OpEvaluator(),
                    MetaEvaluator.getInstance(),
                    new FunctionTable(),
                    new ModuleMap());
        }
        return state;
    }

    /**
     * You may inject state at runtime if you need this to start with some existing state.
     *
     * @param state
     */
    public void setState(State state) {
        this.state = state;
    }

    State state;

    public QDLRunner(List<Element> elements) {
        this.elements = elements;
    }

    public List<Element> getElements() {
        return elements;
    }

    public void setElements(List<Element> elements) {
        this.elements = elements;
    }

    List<Element> elements;

    public void run() throws Throwable {
        State currentState = getState();
        for (Element element : elements) {
            if (element.getStatement() != null) {
                // it can happen that the parser returns an empty statement. Skip it.
                Statement stmt = element.getStatement();
                if (stmt instanceof ModuleStatement) {
                    ModuleStatement ms = (ModuleStatement) stmt;
                    ms.evaluate(currentState);
                } else {
                    stmt.evaluate(currentState);
                }
            }
        }

    }

}
