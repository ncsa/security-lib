package edu.uiuc.ncsa.security.util.functor.parser.event;

import edu.uiuc.ncsa.security.util.functor.parser.ParserError;

import java.util.LinkedList;

/**
 * An event driven parser for functors. Note that this does allow for escape characters. Any single character
 * preceeded by a backslash will be passed along unaltered.
 * <p>Created by Jeff Gaynor<br>
 * on 7/16/18 at  11:27 AM
 */
public class EventDrivenParser {
    public static  final char escapeChar = '\\';
    public boolean debugOn = false; // this is public, so set it if you need it. It does get very verbose!
    LinkedList<DelimiterListener> braceListeners = new LinkedList<>();
    LinkedList<DelimiterListener> bracketListeners = new LinkedList<>();
    ;
    LinkedList<DelimiterListener> parenthesisListeners = new LinkedList<>();
    LinkedList<DoubleQuoteListener> dqListeners = new LinkedList<>();
    LinkedList<CommaListener> commaListeners = new LinkedList<>();

    public void addBraceListener(DelimiterListener d) {
        braceListeners.add(d);
    }

    public void addCommaListener(CommaListener c) {
        commaListeners.add(c);
    }

    public void removeCommaListener(CommaListener c) {
        commaListeners.remove(c);
    }

    public void removeDQListener(DoubleQuoteListener d) {
        dqListeners.remove(d);
    }
    public void removeBracketListener(DelimiterListener d){
        bracketListeners.remove(d);
    }
    public void removeBraceListener(DelimiterListener d){
        braceListeners.remove(d);
    }

    public void removeParenthesisListener(DelimiterListener d){
        parenthesisListeners.remove(d);
    }

    public void addDoubleQuoteListener(DoubleQuoteListener d) {
        dqListeners.add(d);
    }

    public void addBracketListener(DelimiterListener d) {
        bracketListeners.add(d);
    }

    public void addParenthesisListener(DelimiterListener d) {
        parenthesisListeners.add(d);
    }

    protected void notifyOpenListeners(LinkedList<DelimiterListener> listeners, DelimiterEvent event) {
        if(debugOn){
            System.out.println(getClass().getSimpleName() + ".notifyOpenDelim: " + event.getDelimiter()+", name=" + event.getName());
        }
        for (DelimiterListener d : listeners) {
            d.openDelimiter(event);
        }
    }

    protected void notifyCloseListeners(LinkedList<DelimiterListener> listeners, DelimiterEvent event) {
        if(debugOn){
            System.out.println(getClass().getSimpleName() + ".notifyCloseDelim: " + event.getDelimiter() + ", name=" + event.getName());
        }
        for (DelimiterListener d : listeners) {
            d.closeDelimiter(event);
        }
    }

    protected void notifyCommaListeners(LinkedList<CommaListener> listeners, CommaEvent event) {
        if(debugOn){
            System.out.println(getClass().getSimpleName() + ".notifyComma text:" + event.getText());
        }

        for (CommaListener d : listeners) {
            d.gotComma(event);
        }
    }

    protected void notifyDQListeners(LinkedList<DoubleQuoteListener> listeners, DoubleQuoteEvent event) {
        if(debugOn){
            System.out.println(getClass().getSimpleName() + ".notifyDQ:" + event.getCharacters());
        }

        for (DoubleQuoteListener d : listeners) {
            d.gotText(event);
        }
    }

    public void parse(String input) {
        char[] chars = input.toCharArray();
        int foundDQCount = 0; // the number of double quotes. This should alway end up being even or a parser error should be raised.
        int openBraceCount = 0; // the number of open braces, {.
        int openBracketCount = 0; // the number of open brackets, [.
        int openParenthesistCount = 0; // the number of open parentheses, (.
        int closeBraceCount = 0; // the number of close braces, }.
        int closeBracketCount = 0; // the number of close brackets, ].
        int closeParenthesisCount = 0; // the number of close parentheses, ).
        StringBuffer sb = new StringBuffer();
        boolean escapeCharFound = false;
        for (char c : chars) {
            if(escapeCharFound){
                sb.append(c);
                escapeCharFound = false;
                continue;
            }
            switch (c) {
                case '{':
                    notifyOpenListeners(braceListeners, new DelimiterEvent(this, sb.toString().trim(), '{'));
                    openBraceCount++;
                    sb = new StringBuffer();
                    break;
                case '}':
                    notifyCloseListeners(braceListeners, new DelimiterEvent(this, sb.toString().trim(),'}'));
                    sb = new StringBuffer();
                    closeBraceCount++;
                    break;
                case '[':
                    notifyOpenListeners(bracketListeners, new DelimiterEvent(this, sb.toString().trim(), '['));
                    openBracketCount++;
                    sb = new StringBuffer();
                    break;
                case ']':
                    notifyCloseListeners(bracketListeners, new DelimiterEvent(this, sb.toString().trim(),']'));
                    sb = new StringBuffer();
                    closeBracketCount++;
                    break;
                case '(':
                    notifyOpenListeners(parenthesisListeners, new DelimiterEvent(this, sb.toString().trim(),'('));
                    openParenthesistCount++;
                    sb = new StringBuffer();
                    break;
                case ')':
                    notifyCloseListeners(parenthesisListeners, new DelimiterEvent(this, sb.toString().trim(),')'));
                    closeParenthesisCount++;
                    sb = new StringBuffer();
                    break;
                case '"':
                    foundDQCount++;
                    if (0 == foundDQCount % 2) {
                        notifyDQListeners(dqListeners, new DoubleQuoteEvent(this, sb.toString()));
                    } else {
                        // so this is an open quote. Since we allow whitespace before that, we wait until we hit an actual
                        // quote to ignore any.
                        sb = new StringBuffer();
                    }
                    break;
                case ',':
                    notifyCommaListeners(commaListeners, new CommaEvent(this, sb.toString().trim()));
                    sb = new StringBuffer();
                    break;
                case escapeChar:
                    escapeCharFound = true;
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
