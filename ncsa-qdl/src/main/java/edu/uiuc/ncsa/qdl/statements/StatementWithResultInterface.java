package edu.uiuc.ncsa.qdl.statements;

/**
 * This pair occurs over and over again, so it is now officially an idiom.
 * <p>Created by Jeff Gaynor<br>
 * on 9/28/20 at  4:18 PM
 */
public interface StatementWithResultInterface extends Statement, HasResultInterface {
    public StatementWithResultInterface makeCopy(); // would prefer clone, but there is a conflict in packages because it has protected access

}
