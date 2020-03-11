package edu.uiuc.ncsa.qdl.statements;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 3/11/20 at  6:33 AM
 */
public interface HasResultInterface {
    Object getResult();

    void setResult(Object object);

    int getResultType();

    void setResultType(int type);

    boolean isEvaluated();

    void setEvaluated(boolean evaluated);
}
