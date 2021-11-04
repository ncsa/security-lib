package edu.uiuc.ncsa.qdl.statements;

import edu.uiuc.ncsa.qdl.exceptions.RaiseErrorException;
import edu.uiuc.ncsa.qdl.state.State;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 1/22/20 at  10:36 AM
 */
public class TryCatch implements Statement {
    TokenPosition tokenPosition = null;
    @Override
    public void setTokenPosition(TokenPosition tokenPosition) {this.tokenPosition=tokenPosition;}

    @Override
    public TokenPosition getTokenPosition() {return tokenPosition;}

    @Override
    public boolean hasTokenPosition() {return tokenPosition!=null;}
    public static final Long RESERVED_ERROR_CODE = -1L;
    public static final Long RESERVED_USER_ERROR_CODE = 0L;

    @Override
    public Object evaluate(State state) {
        State localState = state.newStateWithImports();
        try {
            for (Statement s : tryStatements) {
                s.evaluate(localState);
            }
        } catch (RaiseErrorException t) {
            // custom error handling
            localState.getSymbolStack().getLocalST().setStringValue("error_message", t.getPolyad().getArguments().get(0).getResult().toString());
            if(t.getPolyad().getArgCount() ==2) {
                localState.getSymbolStack().getLocalST().setLongValue("error_code", (Long) t.getPolyad().getArguments().get(1).getResult());
            }else{
                localState.getSymbolStack().getLocalST().setLongValue("error_code", RESERVED_USER_ERROR_CODE);
            }
            for (Statement c : catchStatements) {
                c.evaluate(localState);
            }
        } catch (Throwable otherT) {
            // everything else.
            localState.getSymbolStack().getLocalST().setStringValue("error_message", otherT.getMessage());
            localState.getSymbolStack().getLocalST().setLongValue("error_code", RESERVED_ERROR_CODE);
            for (Statement c : catchStatements) {
                c.evaluate(localState);
            }
        }
        return null;
    }
   /*
   g(x)->[raise_error('oops');];
   try[g(1);]catch[return(0);];
    */
    List<Statement> catchStatements = new ArrayList<>();
    List<Statement> tryStatements = new ArrayList<>();

    public List<Statement> getCatchStatements() {
        return catchStatements;
    }

    public void setCatchStatements(List<Statement> catchStatements) {
        this.catchStatements = catchStatements;
    }

    public List<Statement> getTryStatements() {
        return this.tryStatements;
    }

    public void setTryStatements(List<Statement> tryStatements) {
        this.tryStatements = tryStatements;
    }

    @Override
    public List<String> getSourceCode() {
        return sourceCode;
    }

    @Override
    public void setSourceCode(List<String> sourceCode) {
        this.sourceCode = sourceCode;
    }

    List<String> sourceCode;
}

