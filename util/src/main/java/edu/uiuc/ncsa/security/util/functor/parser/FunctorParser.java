package edu.uiuc.ncsa.security.util.functor.parser;

import edu.uiuc.ncsa.security.util.functor.parser.old.DefaultHandler;

/**
 * This will take functor notation and parse it into executable JSON.
 * <p>Created by Jeff Gaynor<br>
 * on 7/12/18 at  4:32 PM
 */
public class FunctorParser {
    public void parse(String input, DefaultHandler handler) {
        char[] chars = input.toCharArray();
        int foundDQCount = 0; // the number of double quotes. This should alway end up being even or a parser error should be raised.
        int openBraceCount = 0; // the number of open braces, {.
        int openBracketCount = 0; // the number of open brackets, [.
        int openParenthesistCount = 0; // the number of open parentheses, (.
        int closeBraceCount = 0; // the number of close braces, }.
        int closeBracketCount = 0; // the number of close brackets, ].
        int closeParenthesisCount = 0; // the number of close parentheses, ).
        StringBuffer sb = new StringBuffer();
        for (char c : chars) {
            switch (c) {
                case '{':
                    handler.startBrace(sb.toString().trim());
                    openBraceCount++;
                    sb = new StringBuffer();
                    break;
                case '}':
                    handler.endBrace(sb.toString().trim());
                    sb = new StringBuffer();
                    closeBraceCount++;
                    break;
                case '[':
                    handler.startBracket(sb.toString().trim());
                    openBracketCount++;
                    sb = new StringBuffer();
                    break;
                case ']':
                    handler.endBracket(sb.toString().trim());
                    sb = new StringBuffer();
                    closeBracketCount++;
                    break;
                case '(':
                    handler.startParenthesis(sb.toString().trim());
                    openParenthesistCount++;
                    sb = new StringBuffer();
                    break;
                case ')':
                    handler.endParenthesis(sb.toString().trim());
                    closeParenthesisCount++;
                    sb = new StringBuffer();
                    break;
                case '"':
                    foundDQCount++;
                    if (0 == foundDQCount % 2) {
                        handler.foundDoubleQuotes(sb.toString()); // we should send this along as is, including the whitespace inside the quotes
                    } else {
                        // so this is an open quote. Since we allow whitespace before that, we wait until we hit an actual
                        // quote to ignore any.
                        sb = new StringBuffer();
                    }
                    break;
                case ',':
                    handler.foundComma(sb.toString().trim());
                    sb = new StringBuffer();
                    break;
                default:
                    sb.append(c);
            }
        }
        if (0 != foundDQCount % 2) {
            throw new ParserError("Error: mismatched double quotes");
        }
        if (openBracketCount != closeBracketCount) {
            throw new ParserError("Error: mismatched open/close brackets. Open count = " + openBracketCount + ", close count = " + closeBracketCount);
        }
        if (openParenthesistCount != closeParenthesisCount) {
            throw new ParserError("Error: mismatched open/close parentheses. Open count = " + openParenthesistCount + ", close count = " + closeParenthesisCount);
        }
        if (openBraceCount != closeBraceCount) {
            throw new ParserError("Error: mismatched open/close braces. Open count = " + openBraceCount + ", close count = " + closeBraceCount);
        }

    }
}
