package edu.uiuc.ncsa.security.util.functor.parser.event;

import edu.uiuc.ncsa.security.core.exceptions.NFWException;
import edu.uiuc.ncsa.security.util.configuration.TemplateUtil;
import edu.uiuc.ncsa.security.util.functor.JFunctor;
import edu.uiuc.ncsa.security.util.functor.JFunctorFactory;
import edu.uiuc.ncsa.security.util.functor.parser.AbstractHandler;
import edu.uiuc.ncsa.security.util.functor.parser.ParserError;

import java.util.LinkedList;
import java.util.Map;

import static edu.uiuc.ncsa.security.util.functor.parser.AbstractHandler.*;

/**
 * An event driven parser for functors. Note that this does allow for escape characters. Any single character
 * preceeded by a backslash will be passed along unaltered.
 * <p>Created by Jeff Gaynor<br>
 * on 7/16/18 at  11:27 AM
 */
public class EventDrivenParser {
    public EventDrivenParser() {
    }

    /**
     * Create and initialize this parser.
     *
     * @param functorFactory
     */
    public EventDrivenParser(JFunctorFactory functorFactory) {
        this.functorFactory = functorFactory;
    }

    /**
     * Completely reset the internal state of this parser.
     */
    public void reset() {
        functorHandler = null;
        conditionalHandler = null;
        switchHandler = null;
        hasSwitch = false;
        hasConditional = false;
        hasFunctor = false;
        initialized = false;
        braceListeners = new LinkedList<>();
        bracketListeners = new LinkedList<>();
        parenthesisListeners = new LinkedList<>();
        dqListeners = new LinkedList<>();
        commaListeners = new LinkedList<>();
    }

    JFunctorFactory functorFactory;
    public static final char escapeChar = '\\';
    public boolean debugOn = false; // this is public, so set it if you need it. It does get very verbose!
    LinkedList<DelimiterListener> braceListeners = new LinkedList<>();
    LinkedList<DelimiterListener> bracketListeners = new LinkedList<>();
    ;
    LinkedList<DelimiterListener> parenthesisListeners = new LinkedList<>();
    /*
    Once upon a time arguments were delimited with double quotes. It turned out that it was nearly impossible to manage
    keeping that straight in JSON (which required escaping every single one). Hence the delimiter is now a single quote.
    The dq... variables refer to the older Double Quote handling.
     */
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

    public void removeBracketListener(DelimiterListener d) {
        bracketListeners.remove(d);
    }

    public void removeBraceListener(DelimiterListener d) {
        braceListeners.remove(d);
    }

    public void removeParenthesisListener(DelimiterListener d) {
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
        if (debugOn) {
            System.out.println(getClass().getSimpleName() + ".notifyOpenDelim: " + event.getDelimiter() + ", name=" + event.getName());
        }
        for (DelimiterListener d : listeners) {
            d.openDelimiter(event);
        }
    }

    protected void notifyCloseListeners(LinkedList<DelimiterListener> listeners, DelimiterEvent event) {
        if (debugOn) {
            System.out.println(getClass().getSimpleName() + ".notifyCloseDelim: " + event.getDelimiter() + ", name=" + event.getName());
        }
        for (DelimiterListener d : listeners) {
            d.closeDelimiter(event);
        }
    }

    protected void notifyCommaListeners(LinkedList<CommaListener> listeners, CommaEvent event) {
        if (debugOn) {
            System.out.println(getClass().getSimpleName() + ".notifyComma text:" + event.getText());
        }

        for (CommaListener d : listeners) {
            d.gotComma(event);
        }
    }

    protected void notifyDQListeners(LinkedList<DoubleQuoteListener> listeners, DoubleQuoteEvent event) {
        if (debugOn) {
            System.out.println(getClass().getSimpleName() + ".notifyDQ:" + event.getCharacters());
        }

        for (DoubleQuoteListener d : listeners) {
            d.gotText(event);
        }
    }

    boolean initialized = false;

    public SwitchHandler getSwitchHandler() {
        return switchHandler;
    }


    SwitchHandler switchHandler; // handle logic blocks
    ConditionalHandler conditionalHandler; // handle a single block
    FunctorHandler functorHandler; // handle a single functor

    public FunctorHandler getFunctorHandler() {
        return functorHandler;
    }

    public ConditionalHandler getConditionalHandler() {
        return conditionalHandler;
    }


    protected void execute() {
        if (hasFunctor()) {
            functorHandler.getFunctor().execute();
        }
        if (hasConditional()) {
            getConditionalHandler().getLogicBlock().execute();
        }
        if (hasSwitch()) {
            getSwitchHandler().execute();
        }
    }

    public JFunctor getFunctor() {
        return functorHandler.getFunctor();
    }


    boolean hasConditional = false;
    boolean hasSwitch = false;
    boolean hasFunctor = false;

    /**
     * If you prefer to create the {@link SwitchHandler} yourself, pass it in. You can always get it back after parsing
     * via {@link #getSwitchHandler()}.
     *
     * @param raw
     */
    public void init(String raw) {
        if (initialized) {
            return;
        }
        switch (getType(raw.trim())) {
            case SWITCH_TYPE:
                functorHandler = new FunctorHandler(functorFactory);
                conditionalHandler = new ConditionalHandler(functorHandler, functorFactory);
                switchHandler = new SwitchHandler(conditionalHandler, functorFactory);
                addBraceListener(switchHandler);
                addCommaListener(switchHandler);
                hasSwitch = true;
                break;
            case CONDITIONAL_TYPE:
                functorHandler = new FunctorHandler(functorFactory);
                conditionalHandler = new ConditionalHandler(functorHandler, functorFactory);
                addBracketListener(conditionalHandler);
                addCommaListener(conditionalHandler);
                hasConditional = true;
                break;
            case FUNCTOR_TYPE:
            default:
                functorHandler = new FunctorHandler(functorFactory);
                addParenthesisListener(functorHandler);
                addDoubleQuoteListener(functorHandler);
                addCommaListener(functorHandler);
                hasFunctor = true;

        }
        initialized = true;
    }


    /**
     * The parser is rather stupid on purpose. It can evolve but for now, the strict format is all that is allowed.
     * This method is a very simple-minded way to turn a simple functor or even simple conditional into something the
     * parser understands. It should be amended, improved and replaced as time goes on.
     *
     * @param raw
     * @return
     */
    protected int getType(String raw) {
        if (raw.startsWith("if[")) return CONDITIONAL_TYPE;
        if (raw.startsWith("or{") || raw.startsWith("xor{") || raw.startsWith("and{")) return SWITCH_TYPE;
        return FUNCTOR_TYPE;
    }


    public AbstractHandler parse(String input) {
        return parse(input, null);
    }


    /**
     * Parse a single line. If you supply a map of replacements with key-value pairs, then every
     * occurance of the form ${key} will be replaced by its value. E.g. if <br>
     * <pre>
     *     key = vo
     *     value = voPersonExternalID
     * </pre>
     * then the following transformation on the input line
     * <pre>
     *     get("${vo}");
     * </pre>
     * would result in the execution of
     * <pre>
     *     get("voPersonExternalID");
     * </pre>
     * You may set these values with the {@link edu.uiuc.ncsa.security.util.functor.system.jsetEnv} function in your script.
     *
     * @param input
     * @param replacements
     * @return
     */
    public AbstractHandler parse(String input, Map<String, String> replacements) {
        input = input.trim();
        reset();
        init(input);
        if (replacements != null) {
            input = TemplateUtil.replaceAll(input, replacements);
        }
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
            if (escapeCharFound) {
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
                    notifyCloseListeners(braceListeners, new DelimiterEvent(this, sb.toString().trim(), '}'));
                    sb = new StringBuffer();
                    closeBraceCount++;
                    break;
                case '[':
                    notifyOpenListeners(bracketListeners, new DelimiterEvent(this, sb.toString().trim(), '['));
                    openBracketCount++;
                    sb = new StringBuffer();
                    break;
                case ']':
                    notifyCloseListeners(bracketListeners, new DelimiterEvent(this, sb.toString().trim(), ']'));
                    sb = new StringBuffer();
                    closeBracketCount++;
                    break;
                case '(':
                    notifyOpenListeners(parenthesisListeners, new DelimiterEvent(this, sb.toString().trim(), '('));
                    openParenthesistCount++;
                    sb = new StringBuffer();
                    break;
                case ')':
                    notifyCloseListeners(parenthesisListeners, new DelimiterEvent(this, sb.toString().trim(), ')'));
                    closeParenthesisCount++;
                    sb = new StringBuffer();
                    break;
                case '\'':
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
        execute(); // run the handler now that it has the correct information.
        if (hasSwitch()) {
            return getSwitchHandler();
        }
        if (hasConditional()) {
            return getConditionalHandler();
        }
        if (hasFunctor()) {
            //  getFunctorHandler().getFunctor().execute();
            return getFunctorHandler();
        }
        throw new NFWException("Error: Unknown construction for parser");
    }

    public boolean hasSwitch() {
        return hasSwitch;
    }

    public boolean hasConditional() {
        return hasConditional;
    }

    public boolean hasFunctor() {
        return hasFunctor;
    }
}
