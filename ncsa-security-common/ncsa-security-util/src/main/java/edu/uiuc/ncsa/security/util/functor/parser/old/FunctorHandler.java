package edu.uiuc.ncsa.security.util.functor.parser.old;

/**
 * Really a facade for the various handlers
 * <p>Created by Jeff Gaynor<br>
 * on 7/13/18 at  10:09 AM
 */
public class FunctorHandler implements DefaultHandler {
    @Override
    public void endParenthesis(String name) {

    }

    @Override
    public void startParenthesis(String name) {

    }

    @Override
    public void foundDoubleQuotes(String content) {

    }

    @Override
    public void startBracket(String name) {

    }

    @Override
    public void endBracket(String name) {

    }

    @Override
    public void startBrace(String name) {

    }

    @Override
    public void endBrace(String name) {

    }

    @Override
    public void reset() {

    }

    @Override
    public void foundComma(String name) {

    }
    /*  LogicBlocksHandlerInterface lbsHandler;
    LogicBlockHandlerInterface lbHandler;
    FunctorHandlerInterface fHandler;
    TopHandlerInterface currentHandler = null;
    TopHandlerInterface previousHandler = null;

    public FunctorHandler(FunctorHandlerInterface fHandler,
                          LogicBlockHandlerInterface lbHandler,
                          LogicBlocksHandlerInterface lbsHandler) {
        this.fHandler = fHandler;
        this.lbHandler = lbHandler;
        this.lbsHandler = lbsHandler;
    }

    @Override
    public void endParenthesis(String name) {
      if(currentHandler instanceof FunctorHandlerInterface){
          fHandler.endParenthesis(name);

      }
    }

    @Override
    public void startParenthesis(String name) {

    }

    @Override
    public void foundDoubleQuotes(String content) {

    }

    @Override
    public void startBracket(String name) {
        if()
        lbsHandler.startBracket(name);
    }

    @Override
    public void endBracket(String name) {
        if(currentHandler instanceof LogicBlockHandlerInterface){
            lbHandler.endBracket(name);
            currentHandler = previousHandler;
        }
    }

    @Override
    public void startBrace(String name) {

        lbsHandler.startBracket(name);
    }

    @Override
    public void endBrace(String name) {
        if(currentHandler instanceof LogicBlocksHandlerInterface){
            lbsHandler.endBracket(name);
            currentHandler = previousHandler;
        }
        // or do nothing.
    }

    @Override
    public void reset() {
        lbsHandler.reset();
        lbsHandler.reset();
        fHandler.reset();
        currentHandler = null;
        previousHandler = null;
    }

    @Override
    public void foundComma(String name) {
           currentHandler.foundComma(name);
    }*/
}
