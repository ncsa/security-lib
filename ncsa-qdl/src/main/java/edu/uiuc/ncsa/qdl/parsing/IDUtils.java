package edu.uiuc.ncsa.qdl.parsing;

import org.antlr.v4.runtime.tree.ParseTree;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 1/15/20 at  6:30 AM
 */
public class IDUtils {

    // At least in ANTLR 4, ParseTree is the interface that all contexts must implement
    public static String createIdentifier(ParseTree parseTree) {
        return Integer.toHexString(parseTree.hashCode());
    }
}
