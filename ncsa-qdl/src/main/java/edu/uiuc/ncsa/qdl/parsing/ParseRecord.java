package edu.uiuc.ncsa.qdl.parsing;

import org.antlr.v4.runtime.tree.ParseTree;

import static edu.uiuc.ncsa.qdl.parsing.IDUtils.createIdentifier;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 1/15/20 at  1:48 PM
 */
public abstract class ParseRecord {
    public ParseRecord(ParseTree parseTree) {
         setIdentifier(parseTree);
         this.parentIdentifier = IDUtils.createIdentifier(parseTree.getParent());
     }
    String identifier;
    String parentIdentifier;

    public void setIdentifier(ParseTree parseTree){
             identifier = createIdentifier(parseTree);
    }

}
