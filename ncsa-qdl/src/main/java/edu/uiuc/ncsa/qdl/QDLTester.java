package edu.uiuc.ncsa.qdl;

import edu.uiuc.ncsa.qdl.generated.QDLParserLexer;
import edu.uiuc.ncsa.qdl.generated.QDLParserParser;
import edu.uiuc.ncsa.qdl.parsing.QDLDebugListener;
import org.antlr.v4.runtime.*;

import java.io.FileReader;
import java.io.StringReader;
import java.util.HashMap;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 1/10/20 at  10:34 AM
 */
public class QDLTester {
    public static void main(String[] args) {
        try {
            test1();
            testVars();
        } catch (Throwable t) {
            t.printStackTrace();
        }

    }

    protected static void test0() throws Throwable {
        System.out.println("YO!");
        // How to do it manually, if there is ever a question.
        FileReader vars =    new FileReader("/home/ncsa/dev/ncsa-git/security-lib/ncsa-security-common/ncsa-security-util/src/main/resources/antlr4/test-variables.cmd");
        FileReader funcs =   new FileReader("/home/ncsa/dev/ncsa-git/security-lib/ncsa-security-common/ncsa-security-util/src/main/resources/antlr4/test-expressions.cmd");
        FileReader control = new FileReader("/home/ncsa/dev/ncsa-git/security-lib/ncsa-security-common/ncsa-security-util/src/main/resources/antlr4/test-controls.cmd");

        QDLParserLexer l = new QDLParserLexer(CharStreams.fromReader(control));
        QDLParserParser p = new QDLParserParser(new CommonTokenStream(l));
        p.addErrorListener(new BaseErrorListener() {
            @Override
            public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e) {
                throw new IllegalStateException("failed to parse at line " + line + " due to " + msg, e);
            }
        });
        QDLDebugListener qdlListener = new QDLDebugListener();
        p.addParseListener(qdlListener);
        p.elements();
    }

    protected static void test1() throws Throwable {
        String in = "the ${speed} brown \n${dawg} jumped.";
        HashMap<String, String> templates = new HashMap<>();
        templates.put("speed", "quick");
        templates.put("dawg", "fox");
        StringReader sr = new StringReader(in);
        QDLParserDriver runner = new QDLParserDriver(templates);
        StringReader rc = (StringReader) runner.preProcessStream(sr, templates);
        System.out.println(rc.toString());
    }

    protected static void testVars() throws Throwable {
        FileReader vars = new FileReader("/home/ncsa/dev/ncsa-git/security-lib/ncsa-security-common/ncsa-security-util/src/main/resources/antlr4/test-variables.cmd");
        QDLParserDriver runner = new QDLParserDriver(new HashMap());
        runner.execute(vars);
    }
}
