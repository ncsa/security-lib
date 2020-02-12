package edu.uiuc.ncsa.qdl.parsing;

import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.misc.ParseCancellationException;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 2/9/20 at  9:33 PM
 */
public class ParserExceptionListener extends BaseErrorListener {
    public static final ParserExceptionListener INSTANCE = new ParserExceptionListener();

    @Override
    public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol,
                            int line,
                            int charPositionInLine,
                            String msg,
                            RecognitionException e)
            throws ParseCancellationException {
        throw new ParseCancellationException("line " + line + ":" + charPositionInLine + " " + msg);
    }
}


