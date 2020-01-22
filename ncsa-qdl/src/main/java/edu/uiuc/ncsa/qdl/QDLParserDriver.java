package edu.uiuc.ncsa.qdl;

import edu.uiuc.ncsa.qdl.evaluate.MetaEvaluator;
import edu.uiuc.ncsa.qdl.expressions.OpEvaluator;
import edu.uiuc.ncsa.qdl.generated.QDLParserLexer;
import edu.uiuc.ncsa.qdl.generated.QDLParserParser;
import edu.uiuc.ncsa.qdl.module.ModuleMap;
import edu.uiuc.ncsa.qdl.parsing.ParsingMap;
import edu.uiuc.ncsa.qdl.parsing.QDLDebugListener;
import edu.uiuc.ncsa.qdl.parsing.QDLListener;
import edu.uiuc.ncsa.qdl.state.NamespaceResolver;
import edu.uiuc.ncsa.qdl.state.State;
import edu.uiuc.ncsa.qdl.state.SymbolStack;
import edu.uiuc.ncsa.qdl.statements.Element;
import edu.uiuc.ncsa.qdl.statements.FunctionTable;
import edu.uiuc.ncsa.security.util.configuration.TemplateUtil;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import java.io.BufferedReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

    public QDLParserDriver(Map<String, String> environment, State state) {
        this.environment = environment;
        this.state = state;
    }

    public QDLParserDriver(Map<String, String> environment) {
        this.environment = environment;
        NamespaceResolver namespaceResolver = NamespaceResolver.getResolver();
        this.state = new State(NamespaceResolver.getResolver(),
                new SymbolStack(namespaceResolver),
                new OpEvaluator(),
                MetaEvaluator.getInstance(),
                new FunctionTable(),
                new ModuleMap());
    }

    Map<String, String> environment;

    ParsingMap parsingMap = new ParsingMap();

    public List<Element> getElements() {
        if (parsingMap == null) {
            return new ArrayList<>();
        }
        return parsingMap.getElements();
    }

    protected Reader preProcessStream(Reader reader, Map<String, String> replacements) throws Exception {
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

    protected QDLParserParser getParser(Reader reader, State state) throws Throwable {
        if (parser == null) {
            lexer = new QDLParserLexer(CharStreams.fromReader(reader));

            parser = new QDLParserParser(new CommonTokenStream(lexer));
            parser.addErrorListener(new BaseErrorListener() {
                @Override
                public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e) {
                    throw new IllegalStateException("failed to parse at line " + line + " due to " + msg, e);
                }
            });
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

    public List<Element> parse(Reader reader) throws Throwable{
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
