package edu.uiuc.ncsa.qdl.parsing;

import edu.uiuc.ncsa.qdl.generated.QDLParserLexer;
import edu.uiuc.ncsa.qdl.generated.QDLParserParser;
import edu.uiuc.ncsa.qdl.state.State;
import edu.uiuc.ncsa.qdl.statements.Element;
import edu.uiuc.ncsa.security.core.configuration.XProperties;
import edu.uiuc.ncsa.security.util.configuration.TemplateUtil;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

/**
 * The main parser. Feed it text, it returns a list of elements which may be executed.
 * <p>Created by Jeff Gaynor<br>
 * on 1/10/20 at  2:21 PM
 */
public class QDLParserDriver {
    public boolean isDebugOn() {
        return debugOn;
    }

    public void setDebugOn(boolean debugOn) {
        this.debugOn = debugOn;
    }

    boolean debugOn = false;

    State state;

    public QDLParserDriver(XProperties environment, State state) {
        this.environment = environment;
        this.state = state;
    }



    XProperties environment;

    ParsingMap parsingMap = new ParsingMap();

    public List<Element> getElements() {
        if (parsingMap == null) {
            return new ArrayList<>();
        }
        return parsingMap.getElements();
    }

    protected Reader preProcessStream(Reader reader, XProperties replacements) throws Exception {
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

    QDLParserLexer lexer;
    QDLParserParser parser;

    /*
    This is from the Antlr 4 reference p. 247. The idea is to have an input stream that we keep copying too
    and this will generate tokens
     */
    protected QDLParserParser getParser2(InputStream inputStream, State state) throws Throwable {
        if (parser == null) {
            CharStream input = new UnbufferedCharStream(inputStream);
            lexer = new QDLParserLexer(input);
            lexer.setTokenFactory(new CommonTokenFactory(true));
            parser = new QDLParserParser(new CommonTokenStream(lexer));
            TokenStream tokens = new UnbufferedTokenStream<CommonToken>(lexer);
            parser = new QDLParserParser(tokens);
            parser.setBuildParseTree(false);
            parsingMap = new ParsingMap();
            QDLListener qdlListener = new QDLListener(parsingMap, state);
            if (isDebugOn()) {
                // This will spit out the inner workings of the parser so it may be a TON of output.
                QDLDebugListener debugListener = new QDLDebugListener();
                parser.addParseListener(debugListener);
            }
            parser.addParseListener(qdlListener);
        }
        return parser;

    }

    protected QDLParserParser getParser(Reader reader, State state) throws Throwable {
        if (parser == null) {
            lexer = new QDLParserLexer(CharStreams.fromReader(reader));

            parser = new QDLParserParser(new CommonTokenStream(lexer));

            parsingMap = new ParsingMap();
            QDLListener qdlListener = new QDLListener(parsingMap, state);
            if (isDebugOn()) {
                // This will spit out the inner workings of the parser so it may be a TON of output.
                QDLDebugListener debugListener = new QDLDebugListener();
                parser.addParseListener(debugListener);
                parser.addErrorListener(new BaseErrorListener() {
                    @Override
                    public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e) {
                        throw new IllegalStateException("failed to parse at line " + line + " due to " + msg, e);
                    }
                });
                for (ANTLRErrorListener listener : parser.getErrorListeners()) {
                                  System.out.println("antlr listeners = " + listener);
                          }
            }else{
                // The default is to have the error listener on and this in turn prints every little thing
                // to the console. Great for debugging, but very annoying for users who type in a boo-boo.
                lexer.removeErrorListener(ConsoleErrorListener.INSTANCE);
                parser.removeErrorListener(ConsoleErrorListener.INSTANCE);
                /*
                 * To get an exception at parsing rather than having everything get piped to System.err
                 * we have to implement a listener that throws an exception
                 */
                parser.addErrorListener(ParserExceptionListener.INSTANCE);
            }


            parser.addParseListener(qdlListener);
        }
        return parser;
    }

    public List<Element> parse(Reader reader) throws Throwable {
        QDLParserParser parser = getParser(preProcessStream(reader, environment), state);
        parser.elements();
        return getElements();
    }

    protected void executeDirectly(Reader reader) throws Throwable {
        QDLParserParser parser = getParser(preProcessStream(reader, environment), state);
        parser.elements();
        for (Element element : getElements()) {
            element.getStatement().evaluate(state);
        }
    }

    protected void executeTreeWalker(Reader reader) throws Throwable {
        lexer = new QDLParserLexer(CharStreams.fromReader(reader));

        parser = new QDLParserParser(new CommonTokenStream(lexer));
        parser.addErrorListener(new BaseErrorListener() {
            @Override
            public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e) {
                throw new IllegalStateException("failed to parse at line " + line + " due to " + msg, e);
            }
        });
        QDLDebugListener qdlListener = new QDLDebugListener();
        parser.addParseListener(qdlListener);
        ParseTree tree = parser.elements();
        ParseTreeWalker walker = new ParseTreeWalker();
        walker.walk(qdlListener, tree);
    }

    public void execute(Reader reader) throws Throwable {
        // executeTreeWalker(reader);
        executeDirectly(reader);
    }

    public void execute(String line) throws Throwable {
        StringReader r = new StringReader(line);
        lexer.setInputStream(CharStreams.fromReader(r));
        lexer.reset();
        parser.setTokenStream(new CommonTokenStream(lexer));
        parser.reset();
        parser.elements();

    }
}
