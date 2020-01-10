package edu.uiuc.ncsa.security.util.qdl;

import edu.uiuc.ncsa.security.util.configuration.TemplateUtil;
import edu.uiuc.ncsa.security.util.qdl.generated.QDLParserLexer;
import edu.uiuc.ncsa.security.util.qdl.generated.QDLParserParser;
import edu.uiuc.ncsa.security.util.qdl.util.SymbolTable;
import org.antlr.v4.runtime.*;

import java.io.BufferedReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.Map;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 1/10/20 at  2:21 PM
 */
public class QDLRunner {
    Map<String, Object> environment;
    Map<String, Object> variables;

    protected Reader proProcessStream(Reader reader, Map<String, Object> replacements) throws Exception {
        BufferedReader br = new BufferedReader(reader);
        if (replacements == null || replacements.isEmpty()) {
            return reader;
        }
        String lineIn = br.readLine();
        if (lineIn == null) {
            return new StringReader("");
        }
        // there is both something there and now something to do.
        StringBuffer stringBuffer = new StringBuffer();
        while (lineIn != null) {
            lineIn = TemplateUtil.replaceAll(lineIn, replacements);
            stringBuffer.append(lineIn + "\n"); // put back in the new lines
            // do stuff
            lineIn = br.readLine();
        }

        br.close();
        StringReader stringReader = new StringReader(stringBuffer.toString());
        return stringReader;
    }

    protected QDLParserParser getParser(Reader reader, SymbolTable symbols) throws Throwable {
        QDLParserLexer l = new QDLParserLexer(CharStreams.fromReader(reader));

        QDLParserParser p = new QDLParserParser(new CommonTokenStream(l));
        p.addErrorListener(new BaseErrorListener() {
            @Override
            public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e) {
                throw new IllegalStateException("failed to parse at line " + line + " due to " + msg, e);
            }
        });
        QDLListener qdlListener = new QDLListener();
        qdlListener.setSymbolTable(symbols);
        p.addParseListener(qdlListener);
        return p;
    }

    public void execute(Reader reader) throws Throwable {
        SymbolTable symbols = new SymbolTable();
        QDLParserParser parser = getParser(reader, symbols);

        parser.elements();
        System.out.println(symbols);
    }

}
