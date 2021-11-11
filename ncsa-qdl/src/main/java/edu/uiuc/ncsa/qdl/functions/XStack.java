package edu.uiuc.ncsa.qdl.functions;

import java.util.ArrayList;
import java.util.List;

/**
 * The zero-th element is the current local table.
 * <p>Created by Jeff Gaynor<br>
 * on 11/8/21 at  6:27 AM
 */
public class XStack<V extends XTable<? extends QDLStateThing>> {
    public List<XTable<? extends QDLStateThing>> getStack() {
        return stack;
    }

    public void setStack(List<XTable<? extends QDLStateThing>> stack) {
        this.stack = stack;
    }

    List<XTable<? extends QDLStateThing>> stack = new ArrayList<>();
    public boolean isEmpty(){return getStack().isEmpty();}

    public void push(XTable xTable){
        getStack().add(0, xTable);
    }

    public XTable peek(){
        return getStack().get(0);
    }

    /**
     * Append the table to the end of the stack -- this sets the root for the table.
     * @param v
     */
    public void append(V v){
        getStack().add(v);
    }

    public QDLStateThing get(String key){
        for (XTable xTable : stack) {
             QDLStateThing qdlStateThing = xTable.get(key);
            if (qdlStateThing != null) {
                return qdlStateThing;
            }
        }
        return null;
    }
    public void set(QDLStateThing qdlStateThing){
        peek().put(qdlStateThing);
    }

    public boolean isDefined(String var, int argCount, int startTableIndex) {
        for (int i = startTableIndex; i < stack.size(); i++) {
            if (stack.get(i).isDefined(var)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Since all new tables are added at 0, the initial one, called the root, is last. This gets
     * the root {@link XTable}.
     * @return
     */
    public XTable getRoot(){
        return stack.get(stack.size()-1);
    }
}
