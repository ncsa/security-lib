package edu.uiuc.ncsa.security.util.qdl.parsing;

import edu.uiuc.ncsa.security.util.qdl.statements.Element;
import org.antlr.v4.runtime.tree.ParseTree;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 1/15/20 at  1:50 PM
 */
public class ElementRecord extends ParseRecord {
    public ElementRecord(ParseTree parseTree) {
        super(parseTree);
    }

    public ElementRecord(ParseTree parseTree, Element element) {
        super(parseTree);
        this.element = element;
    }

    public Element getElement() {
        return element;
    }

    public void setElement(Element element) {
        this.element = element;
    }

    Element element;

    @Override
      public String toString() {
          return "StatementRecord[" +
                  "identifier='" + identifier + '\'' +
                  ", parentIdentifier='" + parentIdentifier + '\'' +
                  ", element=" + element +
                  ']';
      }
}
