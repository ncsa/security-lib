// Generated from QDLParser.g4 by ANTLR 4.7.2
package edu.uiuc.ncsa.qdl.generated;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class QDLParserParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.7.2", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, T__2=3, T__3=4, T__4=5, T__5=6, T__6=7, T__7=8, Number=9, 
		ID=10, Bool=11, ASSIGN=12, FuncStart=13, BOOL_TRUE=14, BOOL_FALSE=15, 
		Null=16, STRING=17, Times=18, Divide=19, PlusPlus=20, Plus=21, MinusMinus=22, 
		Minus=23, LessThan=24, GreaterThan=25, LessEquals=26, MoreEquals=27, Equals=28, 
		NotEquals=29, And=30, Or=31, Backtick=32, Percent=33, LeftBracket=34, 
		LogicalIf=35, LogicalThen=36, LogicalElse=37, WhileLoop=38, WhileDo=39, 
		SwitchStatement=40, DefineStatement=41, BodyStatement=42, ModuleStatement=43, 
		TryStatement=44, CatchStatement=45, StatementConnector=46, COMMENT=47, 
		LINE_COMMENT=48, WS2=49, FDOC=50;
	public static final int
		RULE_elements = 0, RULE_variable = 1, RULE_number = 2, RULE_fdoc = 3, 
		RULE_assignment = 4, RULE_argList = 5, RULE_function = 6, RULE_expression = 7, 
		RULE_element = 8, RULE_statement = 9, RULE_ifStatement = 10, RULE_ifElseStatement = 11, 
		RULE_conditionalStatement = 12, RULE_loopStatement = 13, RULE_switchStatement = 14, 
		RULE_defineStatement = 15, RULE_moduleStatement = 16, RULE_tryCatchStatement = 17;
	private static String[] makeRuleNames() {
		return new String[] {
			"elements", "variable", "number", "fdoc", "assignment", "argList", "function", 
			"expression", "element", "statement", "ifStatement", "ifElseStatement", 
			"conditionalStatement", "loopStatement", "switchStatement", "defineStatement", 
			"moduleStatement", "tryCatchStatement"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "','", "')'", "'!'", "'^'", "'=<'", "'=>'", "'('", "';'", null, 
			null, null, null, null, "'true'", "'false'", "'null'", null, "'*'", "'/'", 
			"'++'", "'+'", "'--'", "'-'", "'<'", "'>'", "'<='", "'>='", "'=='", "'!='", 
			"'&&'", "'||'", "'`'", "'%'", "']'", "'if['", "']then['", "']else['", 
			"'while['", "']do['", "'switch['", "'define['", "']body['", "'module['", 
			"'try['", "']catch['", "']['"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, null, null, null, null, null, null, null, null, "Number", "ID", 
			"Bool", "ASSIGN", "FuncStart", "BOOL_TRUE", "BOOL_FALSE", "Null", "STRING", 
			"Times", "Divide", "PlusPlus", "Plus", "MinusMinus", "Minus", "LessThan", 
			"GreaterThan", "LessEquals", "MoreEquals", "Equals", "NotEquals", "And", 
			"Or", "Backtick", "Percent", "LeftBracket", "LogicalIf", "LogicalThen", 
			"LogicalElse", "WhileLoop", "WhileDo", "SwitchStatement", "DefineStatement", 
			"BodyStatement", "ModuleStatement", "TryStatement", "CatchStatement", 
			"StatementConnector", "COMMENT", "LINE_COMMENT", "WS2", "FDOC"
		};
	}
	private static final String[] _SYMBOLIC_NAMES = makeSymbolicNames();
	public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	@Deprecated
	public static final String[] tokenNames;
	static {
		tokenNames = new String[_SYMBOLIC_NAMES.length];
		for (int i = 0; i < tokenNames.length; i++) {
			tokenNames[i] = VOCABULARY.getLiteralName(i);
			if (tokenNames[i] == null) {
				tokenNames[i] = VOCABULARY.getSymbolicName(i);
			}

			if (tokenNames[i] == null) {
				tokenNames[i] = "<INVALID>";
			}
		}
	}

	@Override
	@Deprecated
	public String[] getTokenNames() {
		return tokenNames;
	}

	@Override

	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}

	@Override
	public String getGrammarFileName() { return "QDLParser.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public QDLParserParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	public static class ElementsContext extends ParserRuleContext {
		public TerminalNode EOF() { return getToken(QDLParserParser.EOF, 0); }
		public List<ElementContext> element() {
			return getRuleContexts(ElementContext.class);
		}
		public ElementContext element(int i) {
			return getRuleContext(ElementContext.class,i);
		}
		public ElementsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_elements; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof QDLParserListener ) ((QDLParserListener)listener).enterElements(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof QDLParserListener ) ((QDLParserListener)listener).exitElements(this);
		}
	}

	public final ElementsContext elements() throws RecognitionException {
		ElementsContext _localctx = new ElementsContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_elements);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(39);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__2) | (1L << T__6) | (1L << T__7) | (1L << Number) | (1L << ID) | (1L << Bool) | (1L << FuncStart) | (1L << Null) | (1L << STRING) | (1L << PlusPlus) | (1L << Plus) | (1L << MinusMinus) | (1L << Minus) | (1L << LeftBracket) | (1L << LogicalIf) | (1L << WhileLoop) | (1L << SwitchStatement) | (1L << DefineStatement) | (1L << ModuleStatement) | (1L << TryStatement))) != 0)) {
				{
				{
				setState(36);
				element();
				}
				}
				setState(41);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(42);
			match(EOF);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class VariableContext extends ParserRuleContext {
		public TerminalNode ID() { return getToken(QDLParserParser.ID, 0); }
		public VariableContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_variable; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof QDLParserListener ) ((QDLParserListener)listener).enterVariable(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof QDLParserListener ) ((QDLParserListener)listener).exitVariable(this);
		}
	}

	public final VariableContext variable() throws RecognitionException {
		VariableContext _localctx = new VariableContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_variable);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(44);
			match(ID);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class NumberContext extends ParserRuleContext {
		public TerminalNode Number() { return getToken(QDLParserParser.Number, 0); }
		public NumberContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_number; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof QDLParserListener ) ((QDLParserListener)listener).enterNumber(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof QDLParserListener ) ((QDLParserListener)listener).exitNumber(this);
		}
	}

	public final NumberContext number() throws RecognitionException {
		NumberContext _localctx = new NumberContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_number);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(46);
			match(Number);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class FdocContext extends ParserRuleContext {
		public TerminalNode FDOC() { return getToken(QDLParserParser.FDOC, 0); }
		public FdocContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_fdoc; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof QDLParserListener ) ((QDLParserListener)listener).enterFdoc(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof QDLParserListener ) ((QDLParserListener)listener).exitFdoc(this);
		}
	}

	public final FdocContext fdoc() throws RecognitionException {
		FdocContext _localctx = new FdocContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_fdoc);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(48);
			match(FDOC);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class AssignmentContext extends ParserRuleContext {
		public Token op;
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public List<VariableContext> variable() {
			return getRuleContexts(VariableContext.class);
		}
		public VariableContext variable(int i) {
			return getRuleContext(VariableContext.class,i);
		}
		public List<TerminalNode> ASSIGN() { return getTokens(QDLParserParser.ASSIGN); }
		public TerminalNode ASSIGN(int i) {
			return getToken(QDLParserParser.ASSIGN, i);
		}
		public AssignmentContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_assignment; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof QDLParserListener ) ((QDLParserListener)listener).enterAssignment(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof QDLParserListener ) ((QDLParserListener)listener).exitAssignment(this);
		}
	}

	public final AssignmentContext assignment() throws RecognitionException {
		AssignmentContext _localctx = new AssignmentContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_assignment);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(53); 
			_errHandler.sync(this);
			_alt = 1;
			do {
				switch (_alt) {
				case 1:
					{
					{
					setState(50);
					variable();
					setState(51);
					((AssignmentContext)_localctx).op = match(ASSIGN);
					}
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				setState(55); 
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,1,_ctx);
			} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
			setState(57);
			expression(0);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ArgListContext extends ParserRuleContext {
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public ArgListContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_argList; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof QDLParserListener ) ((QDLParserListener)listener).enterArgList(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof QDLParserListener ) ((QDLParserListener)listener).exitArgList(this);
		}
	}

	public final ArgListContext argList() throws RecognitionException {
		ArgListContext _localctx = new ArgListContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_argList);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(59);
			expression(0);
			setState(64);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__0) {
				{
				{
				setState(60);
				match(T__0);
				setState(61);
				expression(0);
				}
				}
				setState(66);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class FunctionContext extends ParserRuleContext {
		public TerminalNode FuncStart() { return getToken(QDLParserParser.FuncStart, 0); }
		public List<ArgListContext> argList() {
			return getRuleContexts(ArgListContext.class);
		}
		public ArgListContext argList(int i) {
			return getRuleContext(ArgListContext.class,i);
		}
		public FunctionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_function; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof QDLParserListener ) ((QDLParserListener)listener).enterFunction(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof QDLParserListener ) ((QDLParserListener)listener).exitFunction(this);
		}
	}

	public final FunctionContext function() throws RecognitionException {
		FunctionContext _localctx = new FunctionContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_function);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(67);
			match(FuncStart);
			setState(71);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__2) | (1L << T__6) | (1L << T__7) | (1L << Number) | (1L << ID) | (1L << Bool) | (1L << FuncStart) | (1L << Null) | (1L << STRING) | (1L << PlusPlus) | (1L << Plus) | (1L << MinusMinus) | (1L << Minus) | (1L << LeftBracket))) != 0)) {
				{
				{
				setState(68);
				argList();
				}
				}
				setState(73);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(74);
			match(T__1);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ExpressionContext extends ParserRuleContext {
		public ExpressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_expression; }
	 
		public ExpressionContext() { }
		public void copyFrom(ExpressionContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class VariablesContext extends ExpressionContext {
		public VariableContext variable() {
			return getRuleContext(VariableContext.class,0);
		}
		public VariablesContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof QDLParserListener ) ((QDLParserListener)listener).enterVariables(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof QDLParserListener ) ((QDLParserListener)listener).exitVariables(this);
		}
	}
	public static class FunctionsContext extends ExpressionContext {
		public FunctionContext function() {
			return getRuleContext(FunctionContext.class,0);
		}
		public FunctionsContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof QDLParserListener ) ((QDLParserListener)listener).enterFunctions(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof QDLParserListener ) ((QDLParserListener)listener).exitFunctions(this);
		}
	}
	public static class PrefixContext extends ExpressionContext {
		public Token prefix;
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public TerminalNode PlusPlus() { return getToken(QDLParserParser.PlusPlus, 0); }
		public TerminalNode MinusMinus() { return getToken(QDLParserParser.MinusMinus, 0); }
		public PrefixContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof QDLParserListener ) ((QDLParserListener)listener).enterPrefix(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof QDLParserListener ) ((QDLParserListener)listener).exitPrefix(this);
		}
	}
	public static class NumbersContext extends ExpressionContext {
		public NumberContext number() {
			return getRuleContext(NumberContext.class,0);
		}
		public NumbersContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof QDLParserListener ) ((QDLParserListener)listener).enterNumbers(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof QDLParserListener ) ((QDLParserListener)listener).exitNumbers(this);
		}
	}
	public static class AssociationContext extends ExpressionContext {
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public AssociationContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof QDLParserListener ) ((QDLParserListener)listener).enterAssociation(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof QDLParserListener ) ((QDLParserListener)listener).exitAssociation(this);
		}
	}
	public static class NotExpressionContext extends ExpressionContext {
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public NotExpressionContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof QDLParserListener ) ((QDLParserListener)listener).enterNotExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof QDLParserListener ) ((QDLParserListener)listener).exitNotExpression(this);
		}
	}
	public static class LeftBracketContext extends ExpressionContext {
		public TerminalNode LeftBracket() { return getToken(QDLParserParser.LeftBracket, 0); }
		public LeftBracketContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof QDLParserListener ) ((QDLParserListener)listener).enterLeftBracket(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof QDLParserListener ) ((QDLParserListener)listener).exitLeftBracket(this);
		}
	}
	public static class MultiplyExpressionContext extends ExpressionContext {
		public Token op;
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public TerminalNode Times() { return getToken(QDLParserParser.Times, 0); }
		public TerminalNode Divide() { return getToken(QDLParserParser.Divide, 0); }
		public TerminalNode Percent() { return getToken(QDLParserParser.Percent, 0); }
		public MultiplyExpressionContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof QDLParserListener ) ((QDLParserListener)listener).enterMultiplyExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof QDLParserListener ) ((QDLParserListener)listener).exitMultiplyExpression(this);
		}
	}
	public static class LogicalContext extends ExpressionContext {
		public TerminalNode Bool() { return getToken(QDLParserParser.Bool, 0); }
		public LogicalContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof QDLParserListener ) ((QDLParserListener)listener).enterLogical(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof QDLParserListener ) ((QDLParserListener)listener).exitLogical(this);
		}
	}
	public static class OrExpressionContext extends ExpressionContext {
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public TerminalNode Or() { return getToken(QDLParserParser.Or, 0); }
		public OrExpressionContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof QDLParserListener ) ((QDLParserListener)listener).enterOrExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof QDLParserListener ) ((QDLParserListener)listener).exitOrExpression(this);
		}
	}
	public static class UnaryMinusExpressionContext extends ExpressionContext {
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public TerminalNode Plus() { return getToken(QDLParserParser.Plus, 0); }
		public TerminalNode Minus() { return getToken(QDLParserParser.Minus, 0); }
		public UnaryMinusExpressionContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof QDLParserListener ) ((QDLParserListener)listener).enterUnaryMinusExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof QDLParserListener ) ((QDLParserListener)listener).exitUnaryMinusExpression(this);
		}
	}
	public static class PowerExpressionContext extends ExpressionContext {
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public PowerExpressionContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof QDLParserListener ) ((QDLParserListener)listener).enterPowerExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof QDLParserListener ) ((QDLParserListener)listener).exitPowerExpression(this);
		}
	}
	public static class EqExpressionContext extends ExpressionContext {
		public Token op;
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public TerminalNode Equals() { return getToken(QDLParserParser.Equals, 0); }
		public TerminalNode NotEquals() { return getToken(QDLParserParser.NotEquals, 0); }
		public EqExpressionContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof QDLParserListener ) ((QDLParserListener)listener).enterEqExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof QDLParserListener ) ((QDLParserListener)listener).exitEqExpression(this);
		}
	}
	public static class AndExpressionContext extends ExpressionContext {
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public TerminalNode And() { return getToken(QDLParserParser.And, 0); }
		public AndExpressionContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof QDLParserListener ) ((QDLParserListener)listener).enterAndExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof QDLParserListener ) ((QDLParserListener)listener).exitAndExpression(this);
		}
	}
	public static class NullContext extends ExpressionContext {
		public TerminalNode Null() { return getToken(QDLParserParser.Null, 0); }
		public NullContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof QDLParserListener ) ((QDLParserListener)listener).enterNull(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof QDLParserListener ) ((QDLParserListener)listener).exitNull(this);
		}
	}
	public static class StringsContext extends ExpressionContext {
		public TerminalNode STRING() { return getToken(QDLParserParser.STRING, 0); }
		public StringsContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof QDLParserListener ) ((QDLParserListener)listener).enterStrings(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof QDLParserListener ) ((QDLParserListener)listener).exitStrings(this);
		}
	}
	public static class AddExpressionContext extends ExpressionContext {
		public Token op;
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public TerminalNode Plus() { return getToken(QDLParserParser.Plus, 0); }
		public TerminalNode Minus() { return getToken(QDLParserParser.Minus, 0); }
		public AddExpressionContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof QDLParserListener ) ((QDLParserListener)listener).enterAddExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof QDLParserListener ) ((QDLParserListener)listener).exitAddExpression(this);
		}
	}
	public static class CompExpressionContext extends ExpressionContext {
		public Token op;
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public TerminalNode LessThan() { return getToken(QDLParserParser.LessThan, 0); }
		public TerminalNode GreaterThan() { return getToken(QDLParserParser.GreaterThan, 0); }
		public TerminalNode LessEquals() { return getToken(QDLParserParser.LessEquals, 0); }
		public TerminalNode MoreEquals() { return getToken(QDLParserParser.MoreEquals, 0); }
		public CompExpressionContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof QDLParserListener ) ((QDLParserListener)listener).enterCompExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof QDLParserListener ) ((QDLParserListener)listener).exitCompExpression(this);
		}
	}
	public static class PostfixContext extends ExpressionContext {
		public Token postfix;
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public TerminalNode PlusPlus() { return getToken(QDLParserParser.PlusPlus, 0); }
		public TerminalNode MinusMinus() { return getToken(QDLParserParser.MinusMinus, 0); }
		public PostfixContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof QDLParserListener ) ((QDLParserListener)listener).enterPostfix(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof QDLParserListener ) ((QDLParserListener)listener).exitPostfix(this);
		}
	}
	public static class Semi_for_empty_expressionsContext extends ExpressionContext {
		public Semi_for_empty_expressionsContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof QDLParserListener ) ((QDLParserListener)listener).enterSemi_for_empty_expressions(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof QDLParserListener ) ((QDLParserListener)listener).exitSemi_for_empty_expressions(this);
		}
	}

	public final ExpressionContext expression() throws RecognitionException {
		return expression(0);
	}

	private ExpressionContext expression(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		ExpressionContext _localctx = new ExpressionContext(_ctx, _parentState);
		ExpressionContext _prevctx = _localctx;
		int _startState = 14;
		enterRecursionRule(_localctx, 14, RULE_expression, _p);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(95);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case FuncStart:
				{
				_localctx = new FunctionsContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;

				setState(77);
				function();
				}
				break;
			case PlusPlus:
			case MinusMinus:
				{
				_localctx = new PrefixContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(78);
				((PrefixContext)_localctx).prefix = _input.LT(1);
				_la = _input.LA(1);
				if ( !(_la==PlusPlus || _la==MinusMinus) ) {
					((PrefixContext)_localctx).prefix = (Token)_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(79);
				expression(18);
				}
				break;
			case T__2:
				{
				_localctx = new NotExpressionContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(80);
				match(T__2);
				setState(81);
				expression(17);
				}
				break;
			case Plus:
			case Minus:
				{
				_localctx = new UnaryMinusExpressionContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(82);
				_la = _input.LA(1);
				if ( !(_la==Plus || _la==Minus) ) {
				_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(83);
				expression(14);
				}
				break;
			case T__6:
				{
				_localctx = new AssociationContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(84);
				match(T__6);
				setState(85);
				expression(0);
				setState(86);
				match(T__1);
				}
				break;
			case LeftBracket:
				{
				_localctx = new LeftBracketContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(88);
				match(LeftBracket);
				}
				break;
			case Number:
				{
				_localctx = new NumbersContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(89);
				number();
				}
				break;
			case ID:
				{
				_localctx = new VariablesContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(90);
				variable();
				}
				break;
			case Bool:
				{
				_localctx = new LogicalContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(91);
				match(Bool);
				}
				break;
			case Null:
				{
				_localctx = new NullContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(92);
				match(Null);
				}
				break;
			case STRING:
				{
				_localctx = new StringsContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(93);
				match(STRING);
				}
				break;
			case T__7:
				{
				_localctx = new Semi_for_empty_expressionsContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(94);
				match(T__7);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			_ctx.stop = _input.LT(-1);
			setState(122);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,6,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					setState(120);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,5,_ctx) ) {
					case 1:
						{
						_localctx = new PowerExpressionContext(new ExpressionContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(97);
						if (!(precpred(_ctx, 16))) throw new FailedPredicateException(this, "precpred(_ctx, 16)");
						setState(98);
						match(T__3);
						setState(99);
						expression(17);
						}
						break;
					case 2:
						{
						_localctx = new MultiplyExpressionContext(new ExpressionContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(100);
						if (!(precpred(_ctx, 15))) throw new FailedPredicateException(this, "precpred(_ctx, 15)");
						setState(101);
						((MultiplyExpressionContext)_localctx).op = _input.LT(1);
						_la = _input.LA(1);
						if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << Times) | (1L << Divide) | (1L << Percent))) != 0)) ) {
							((MultiplyExpressionContext)_localctx).op = (Token)_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(102);
						expression(16);
						}
						break;
					case 3:
						{
						_localctx = new AddExpressionContext(new ExpressionContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(103);
						if (!(precpred(_ctx, 13))) throw new FailedPredicateException(this, "precpred(_ctx, 13)");
						setState(104);
						((AddExpressionContext)_localctx).op = _input.LT(1);
						_la = _input.LA(1);
						if ( !(_la==Plus || _la==Minus) ) {
							((AddExpressionContext)_localctx).op = (Token)_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(105);
						expression(14);
						}
						break;
					case 4:
						{
						_localctx = new CompExpressionContext(new ExpressionContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(106);
						if (!(precpred(_ctx, 12))) throw new FailedPredicateException(this, "precpred(_ctx, 12)");
						setState(107);
						((CompExpressionContext)_localctx).op = _input.LT(1);
						_la = _input.LA(1);
						if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__4) | (1L << T__5) | (1L << LessThan) | (1L << GreaterThan) | (1L << LessEquals) | (1L << MoreEquals))) != 0)) ) {
							((CompExpressionContext)_localctx).op = (Token)_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(108);
						expression(13);
						}
						break;
					case 5:
						{
						_localctx = new EqExpressionContext(new ExpressionContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(109);
						if (!(precpred(_ctx, 11))) throw new FailedPredicateException(this, "precpred(_ctx, 11)");
						setState(110);
						((EqExpressionContext)_localctx).op = _input.LT(1);
						_la = _input.LA(1);
						if ( !(_la==Equals || _la==NotEquals) ) {
							((EqExpressionContext)_localctx).op = (Token)_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(111);
						expression(12);
						}
						break;
					case 6:
						{
						_localctx = new AndExpressionContext(new ExpressionContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(112);
						if (!(precpred(_ctx, 10))) throw new FailedPredicateException(this, "precpred(_ctx, 10)");
						setState(113);
						match(And);
						setState(114);
						expression(11);
						}
						break;
					case 7:
						{
						_localctx = new OrExpressionContext(new ExpressionContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(115);
						if (!(precpred(_ctx, 9))) throw new FailedPredicateException(this, "precpred(_ctx, 9)");
						setState(116);
						match(Or);
						setState(117);
						expression(10);
						}
						break;
					case 8:
						{
						_localctx = new PostfixContext(new ExpressionContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(118);
						if (!(precpred(_ctx, 19))) throw new FailedPredicateException(this, "precpred(_ctx, 19)");
						setState(119);
						((PostfixContext)_localctx).postfix = _input.LT(1);
						_la = _input.LA(1);
						if ( !(_la==PlusPlus || _la==MinusMinus) ) {
							((PostfixContext)_localctx).postfix = (Token)_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						}
						break;
					}
					} 
				}
				setState(124);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,6,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			unrollRecursionContexts(_parentctx);
		}
		return _localctx;
	}

	public static class ElementContext extends ParserRuleContext {
		public StatementContext statement() {
			return getRuleContext(StatementContext.class,0);
		}
		public ModuleStatementContext moduleStatement() {
			return getRuleContext(ModuleStatementContext.class,0);
		}
		public ElementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_element; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof QDLParserListener ) ((QDLParserListener)listener).enterElement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof QDLParserListener ) ((QDLParserListener)listener).exitElement(this);
		}
	}

	public final ElementContext element() throws RecognitionException {
		ElementContext _localctx = new ElementContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_element);
		try {
			setState(131);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__2:
			case T__6:
			case T__7:
			case Number:
			case ID:
			case Bool:
			case FuncStart:
			case Null:
			case STRING:
			case PlusPlus:
			case Plus:
			case MinusMinus:
			case Minus:
			case LeftBracket:
			case LogicalIf:
			case WhileLoop:
			case SwitchStatement:
			case DefineStatement:
			case TryStatement:
				enterOuterAlt(_localctx, 1);
				{
				{
				setState(125);
				statement();
				setState(126);
				match(T__7);
				}
				}
				break;
			case ModuleStatement:
				enterOuterAlt(_localctx, 2);
				{
				{
				setState(128);
				moduleStatement();
				setState(129);
				match(T__7);
				}
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class StatementContext extends ParserRuleContext {
		public DefineStatementContext defineStatement() {
			return getRuleContext(DefineStatementContext.class,0);
		}
		public ConditionalStatementContext conditionalStatement() {
			return getRuleContext(ConditionalStatementContext.class,0);
		}
		public LoopStatementContext loopStatement() {
			return getRuleContext(LoopStatementContext.class,0);
		}
		public SwitchStatementContext switchStatement() {
			return getRuleContext(SwitchStatementContext.class,0);
		}
		public AssignmentContext assignment() {
			return getRuleContext(AssignmentContext.class,0);
		}
		public TryCatchStatementContext tryCatchStatement() {
			return getRuleContext(TryCatchStatementContext.class,0);
		}
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public StatementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_statement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof QDLParserListener ) ((QDLParserListener)listener).enterStatement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof QDLParserListener ) ((QDLParserListener)listener).exitStatement(this);
		}
	}

	public final StatementContext statement() throws RecognitionException {
		StatementContext _localctx = new StatementContext(_ctx, getState());
		enterRule(_localctx, 18, RULE_statement);
		try {
			setState(140);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,8,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(133);
				defineStatement();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(134);
				conditionalStatement();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(135);
				loopStatement();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(136);
				switchStatement();
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(137);
				assignment();
				}
				break;
			case 6:
				enterOuterAlt(_localctx, 6);
				{
				setState(138);
				tryCatchStatement();
				}
				break;
			case 7:
				enterOuterAlt(_localctx, 7);
				{
				setState(139);
				expression(0);
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class IfStatementContext extends ParserRuleContext {
		public TerminalNode LogicalIf() { return getToken(QDLParserParser.LogicalIf, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public TerminalNode LeftBracket() { return getToken(QDLParserParser.LeftBracket, 0); }
		public TerminalNode LogicalThen() { return getToken(QDLParserParser.LogicalThen, 0); }
		public TerminalNode StatementConnector() { return getToken(QDLParserParser.StatementConnector, 0); }
		public List<StatementContext> statement() {
			return getRuleContexts(StatementContext.class);
		}
		public StatementContext statement(int i) {
			return getRuleContext(StatementContext.class,i);
		}
		public IfStatementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_ifStatement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof QDLParserListener ) ((QDLParserListener)listener).enterIfStatement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof QDLParserListener ) ((QDLParserListener)listener).exitIfStatement(this);
		}
	}

	public final IfStatementContext ifStatement() throws RecognitionException {
		IfStatementContext _localctx = new IfStatementContext(_ctx, getState());
		enterRule(_localctx, 20, RULE_ifStatement);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(142);
			match(LogicalIf);
			setState(143);
			expression(0);
			setState(144);
			_la = _input.LA(1);
			if ( !(_la==LogicalThen || _la==StatementConnector) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			setState(150);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,9,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(145);
					statement();
					setState(146);
					match(T__7);
					}
					} 
				}
				setState(152);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,9,_ctx);
			}
			setState(153);
			match(LeftBracket);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class IfElseStatementContext extends ParserRuleContext {
		public TerminalNode LogicalIf() { return getToken(QDLParserParser.LogicalIf, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public TerminalNode LogicalElse() { return getToken(QDLParserParser.LogicalElse, 0); }
		public TerminalNode LeftBracket() { return getToken(QDLParserParser.LeftBracket, 0); }
		public TerminalNode LogicalThen() { return getToken(QDLParserParser.LogicalThen, 0); }
		public TerminalNode StatementConnector() { return getToken(QDLParserParser.StatementConnector, 0); }
		public List<StatementContext> statement() {
			return getRuleContexts(StatementContext.class);
		}
		public StatementContext statement(int i) {
			return getRuleContext(StatementContext.class,i);
		}
		public IfElseStatementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_ifElseStatement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof QDLParserListener ) ((QDLParserListener)listener).enterIfElseStatement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof QDLParserListener ) ((QDLParserListener)listener).exitIfElseStatement(this);
		}
	}

	public final IfElseStatementContext ifElseStatement() throws RecognitionException {
		IfElseStatementContext _localctx = new IfElseStatementContext(_ctx, getState());
		enterRule(_localctx, 22, RULE_ifElseStatement);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(155);
			match(LogicalIf);
			setState(156);
			expression(0);
			setState(157);
			_la = _input.LA(1);
			if ( !(_la==LogicalThen || _la==StatementConnector) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			setState(163);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__2) | (1L << T__6) | (1L << T__7) | (1L << Number) | (1L << ID) | (1L << Bool) | (1L << FuncStart) | (1L << Null) | (1L << STRING) | (1L << PlusPlus) | (1L << Plus) | (1L << MinusMinus) | (1L << Minus) | (1L << LeftBracket) | (1L << LogicalIf) | (1L << WhileLoop) | (1L << SwitchStatement) | (1L << DefineStatement) | (1L << TryStatement))) != 0)) {
				{
				{
				setState(158);
				statement();
				setState(159);
				match(T__7);
				}
				}
				setState(165);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(166);
			match(LogicalElse);
			setState(172);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,11,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(167);
					statement();
					setState(168);
					match(T__7);
					}
					} 
				}
				setState(174);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,11,_ctx);
			}
			setState(175);
			match(LeftBracket);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ConditionalStatementContext extends ParserRuleContext {
		public IfElseStatementContext ifElseStatement() {
			return getRuleContext(IfElseStatementContext.class,0);
		}
		public IfStatementContext ifStatement() {
			return getRuleContext(IfStatementContext.class,0);
		}
		public ConditionalStatementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_conditionalStatement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof QDLParserListener ) ((QDLParserListener)listener).enterConditionalStatement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof QDLParserListener ) ((QDLParserListener)listener).exitConditionalStatement(this);
		}
	}

	public final ConditionalStatementContext conditionalStatement() throws RecognitionException {
		ConditionalStatementContext _localctx = new ConditionalStatementContext(_ctx, getState());
		enterRule(_localctx, 24, RULE_conditionalStatement);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(179);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,12,_ctx) ) {
			case 1:
				{
				setState(177);
				ifElseStatement();
				}
				break;
			case 2:
				{
				setState(178);
				ifStatement();
				}
				break;
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class LoopStatementContext extends ParserRuleContext {
		public TerminalNode WhileLoop() { return getToken(QDLParserParser.WhileLoop, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public TerminalNode LeftBracket() { return getToken(QDLParserParser.LeftBracket, 0); }
		public TerminalNode WhileDo() { return getToken(QDLParserParser.WhileDo, 0); }
		public TerminalNode StatementConnector() { return getToken(QDLParserParser.StatementConnector, 0); }
		public List<StatementContext> statement() {
			return getRuleContexts(StatementContext.class);
		}
		public StatementContext statement(int i) {
			return getRuleContext(StatementContext.class,i);
		}
		public LoopStatementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_loopStatement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof QDLParserListener ) ((QDLParserListener)listener).enterLoopStatement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof QDLParserListener ) ((QDLParserListener)listener).exitLoopStatement(this);
		}
	}

	public final LoopStatementContext loopStatement() throws RecognitionException {
		LoopStatementContext _localctx = new LoopStatementContext(_ctx, getState());
		enterRule(_localctx, 26, RULE_loopStatement);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(181);
			match(WhileLoop);
			setState(182);
			expression(0);
			setState(183);
			_la = _input.LA(1);
			if ( !(_la==WhileDo || _la==StatementConnector) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			setState(189);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,13,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(184);
					statement();
					setState(185);
					match(T__7);
					}
					} 
				}
				setState(191);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,13,_ctx);
			}
			setState(192);
			match(LeftBracket);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class SwitchStatementContext extends ParserRuleContext {
		public TerminalNode SwitchStatement() { return getToken(QDLParserParser.SwitchStatement, 0); }
		public TerminalNode LeftBracket() { return getToken(QDLParserParser.LeftBracket, 0); }
		public List<IfStatementContext> ifStatement() {
			return getRuleContexts(IfStatementContext.class);
		}
		public IfStatementContext ifStatement(int i) {
			return getRuleContext(IfStatementContext.class,i);
		}
		public SwitchStatementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_switchStatement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof QDLParserListener ) ((QDLParserListener)listener).enterSwitchStatement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof QDLParserListener ) ((QDLParserListener)listener).exitSwitchStatement(this);
		}
	}

	public final SwitchStatementContext switchStatement() throws RecognitionException {
		SwitchStatementContext _localctx = new SwitchStatementContext(_ctx, getState());
		enterRule(_localctx, 28, RULE_switchStatement);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(194);
			match(SwitchStatement);
			setState(200);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==LogicalIf) {
				{
				{
				setState(195);
				ifStatement();
				setState(196);
				match(T__7);
				}
				}
				setState(202);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(203);
			match(LeftBracket);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class DefineStatementContext extends ParserRuleContext {
		public TerminalNode DefineStatement() { return getToken(QDLParserParser.DefineStatement, 0); }
		public FunctionContext function() {
			return getRuleContext(FunctionContext.class,0);
		}
		public TerminalNode LeftBracket() { return getToken(QDLParserParser.LeftBracket, 0); }
		public TerminalNode BodyStatement() { return getToken(QDLParserParser.BodyStatement, 0); }
		public TerminalNode StatementConnector() { return getToken(QDLParserParser.StatementConnector, 0); }
		public List<FdocContext> fdoc() {
			return getRuleContexts(FdocContext.class);
		}
		public FdocContext fdoc(int i) {
			return getRuleContext(FdocContext.class,i);
		}
		public List<StatementContext> statement() {
			return getRuleContexts(StatementContext.class);
		}
		public StatementContext statement(int i) {
			return getRuleContext(StatementContext.class,i);
		}
		public DefineStatementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_defineStatement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof QDLParserListener ) ((QDLParserListener)listener).enterDefineStatement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof QDLParserListener ) ((QDLParserListener)listener).exitDefineStatement(this);
		}
	}

	public final DefineStatementContext defineStatement() throws RecognitionException {
		DefineStatementContext _localctx = new DefineStatementContext(_ctx, getState());
		enterRule(_localctx, 30, RULE_defineStatement);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(205);
			match(DefineStatement);
			setState(206);
			function();
			setState(207);
			_la = _input.LA(1);
			if ( !(_la==BodyStatement || _la==StatementConnector) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			setState(211);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==FDOC) {
				{
				{
				setState(208);
				fdoc();
				}
				}
				setState(213);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(217); 
			_errHandler.sync(this);
			_alt = 1;
			do {
				switch (_alt) {
				case 1:
					{
					{
					setState(214);
					statement();
					setState(215);
					match(T__7);
					}
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				setState(219); 
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,16,_ctx);
			} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
			setState(221);
			match(LeftBracket);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ModuleStatementContext extends ParserRuleContext {
		public TerminalNode ModuleStatement() { return getToken(QDLParserParser.ModuleStatement, 0); }
		public List<TerminalNode> STRING() { return getTokens(QDLParserParser.STRING); }
		public TerminalNode STRING(int i) {
			return getToken(QDLParserParser.STRING, i);
		}
		public TerminalNode LeftBracket() { return getToken(QDLParserParser.LeftBracket, 0); }
		public TerminalNode BodyStatement() { return getToken(QDLParserParser.BodyStatement, 0); }
		public TerminalNode StatementConnector() { return getToken(QDLParserParser.StatementConnector, 0); }
		public List<StatementContext> statement() {
			return getRuleContexts(StatementContext.class);
		}
		public StatementContext statement(int i) {
			return getRuleContext(StatementContext.class,i);
		}
		public ModuleStatementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_moduleStatement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof QDLParserListener ) ((QDLParserListener)listener).enterModuleStatement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof QDLParserListener ) ((QDLParserListener)listener).exitModuleStatement(this);
		}
	}

	public final ModuleStatementContext moduleStatement() throws RecognitionException {
		ModuleStatementContext _localctx = new ModuleStatementContext(_ctx, getState());
		enterRule(_localctx, 32, RULE_moduleStatement);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(223);
			match(ModuleStatement);
			setState(224);
			match(STRING);
			setState(227);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__0) {
				{
				setState(225);
				match(T__0);
				setState(226);
				match(STRING);
				}
			}

			setState(229);
			_la = _input.LA(1);
			if ( !(_la==BodyStatement || _la==StatementConnector) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			setState(235);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,18,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(230);
					statement();
					setState(231);
					match(T__7);
					}
					} 
				}
				setState(237);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,18,_ctx);
			}
			setState(238);
			match(LeftBracket);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class TryCatchStatementContext extends ParserRuleContext {
		public TerminalNode TryStatement() { return getToken(QDLParserParser.TryStatement, 0); }
		public TerminalNode CatchStatement() { return getToken(QDLParserParser.CatchStatement, 0); }
		public TerminalNode LeftBracket() { return getToken(QDLParserParser.LeftBracket, 0); }
		public List<StatementContext> statement() {
			return getRuleContexts(StatementContext.class);
		}
		public StatementContext statement(int i) {
			return getRuleContext(StatementContext.class,i);
		}
		public TryCatchStatementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_tryCatchStatement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof QDLParserListener ) ((QDLParserListener)listener).enterTryCatchStatement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof QDLParserListener ) ((QDLParserListener)listener).exitTryCatchStatement(this);
		}
	}

	public final TryCatchStatementContext tryCatchStatement() throws RecognitionException {
		TryCatchStatementContext _localctx = new TryCatchStatementContext(_ctx, getState());
		enterRule(_localctx, 34, RULE_tryCatchStatement);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(240);
			match(TryStatement);
			setState(246);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__2) | (1L << T__6) | (1L << T__7) | (1L << Number) | (1L << ID) | (1L << Bool) | (1L << FuncStart) | (1L << Null) | (1L << STRING) | (1L << PlusPlus) | (1L << Plus) | (1L << MinusMinus) | (1L << Minus) | (1L << LeftBracket) | (1L << LogicalIf) | (1L << WhileLoop) | (1L << SwitchStatement) | (1L << DefineStatement) | (1L << TryStatement))) != 0)) {
				{
				{
				setState(241);
				statement();
				setState(242);
				match(T__7);
				}
				}
				setState(248);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(249);
			match(CatchStatement);
			setState(255);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,20,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(250);
					statement();
					setState(251);
					match(T__7);
					}
					} 
				}
				setState(257);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,20,_ctx);
			}
			setState(258);
			match(LeftBracket);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public boolean sempred(RuleContext _localctx, int ruleIndex, int predIndex) {
		switch (ruleIndex) {
		case 7:
			return expression_sempred((ExpressionContext)_localctx, predIndex);
		}
		return true;
	}
	private boolean expression_sempred(ExpressionContext _localctx, int predIndex) {
		switch (predIndex) {
		case 0:
			return precpred(_ctx, 16);
		case 1:
			return precpred(_ctx, 15);
		case 2:
			return precpred(_ctx, 13);
		case 3:
			return precpred(_ctx, 12);
		case 4:
			return precpred(_ctx, 11);
		case 5:
			return precpred(_ctx, 10);
		case 6:
			return precpred(_ctx, 9);
		case 7:
			return precpred(_ctx, 19);
		}
		return true;
	}

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\3\64\u0107\4\2\t\2"+
		"\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13"+
		"\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\3\2\7\2(\n\2\f\2\16\2+\13\2\3\2\3\2\3\3\3\3\3\4\3\4\3\5\3\5"+
		"\3\6\3\6\3\6\6\68\n\6\r\6\16\69\3\6\3\6\3\7\3\7\3\7\7\7A\n\7\f\7\16\7"+
		"D\13\7\3\b\3\b\7\bH\n\b\f\b\16\bK\13\b\3\b\3\b\3\t\3\t\3\t\3\t\3\t\3\t"+
		"\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\5\tb\n\t\3\t\3\t"+
		"\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3"+
		"\t\3\t\3\t\3\t\7\t{\n\t\f\t\16\t~\13\t\3\n\3\n\3\n\3\n\3\n\3\n\5\n\u0086"+
		"\n\n\3\13\3\13\3\13\3\13\3\13\3\13\3\13\5\13\u008f\n\13\3\f\3\f\3\f\3"+
		"\f\3\f\3\f\7\f\u0097\n\f\f\f\16\f\u009a\13\f\3\f\3\f\3\r\3\r\3\r\3\r\3"+
		"\r\3\r\7\r\u00a4\n\r\f\r\16\r\u00a7\13\r\3\r\3\r\3\r\3\r\7\r\u00ad\n\r"+
		"\f\r\16\r\u00b0\13\r\3\r\3\r\3\16\3\16\5\16\u00b6\n\16\3\17\3\17\3\17"+
		"\3\17\3\17\3\17\7\17\u00be\n\17\f\17\16\17\u00c1\13\17\3\17\3\17\3\20"+
		"\3\20\3\20\3\20\7\20\u00c9\n\20\f\20\16\20\u00cc\13\20\3\20\3\20\3\21"+
		"\3\21\3\21\3\21\7\21\u00d4\n\21\f\21\16\21\u00d7\13\21\3\21\3\21\3\21"+
		"\6\21\u00dc\n\21\r\21\16\21\u00dd\3\21\3\21\3\22\3\22\3\22\3\22\5\22\u00e6"+
		"\n\22\3\22\3\22\3\22\3\22\7\22\u00ec\n\22\f\22\16\22\u00ef\13\22\3\22"+
		"\3\22\3\23\3\23\3\23\3\23\7\23\u00f7\n\23\f\23\16\23\u00fa\13\23\3\23"+
		"\3\23\3\23\3\23\7\23\u0100\n\23\f\23\16\23\u0103\13\23\3\23\3\23\3\23"+
		"\2\3\20\24\2\4\6\b\n\f\16\20\22\24\26\30\32\34\36 \"$\2\n\4\2\26\26\30"+
		"\30\4\2\27\27\31\31\4\2\24\25##\4\2\7\b\32\35\3\2\36\37\4\2&&\60\60\4"+
		"\2))\60\60\4\2,,\60\60\2\u011e\2)\3\2\2\2\4.\3\2\2\2\6\60\3\2\2\2\b\62"+
		"\3\2\2\2\n\67\3\2\2\2\f=\3\2\2\2\16E\3\2\2\2\20a\3\2\2\2\22\u0085\3\2"+
		"\2\2\24\u008e\3\2\2\2\26\u0090\3\2\2\2\30\u009d\3\2\2\2\32\u00b5\3\2\2"+
		"\2\34\u00b7\3\2\2\2\36\u00c4\3\2\2\2 \u00cf\3\2\2\2\"\u00e1\3\2\2\2$\u00f2"+
		"\3\2\2\2&(\5\22\n\2\'&\3\2\2\2(+\3\2\2\2)\'\3\2\2\2)*\3\2\2\2*,\3\2\2"+
		"\2+)\3\2\2\2,-\7\2\2\3-\3\3\2\2\2./\7\f\2\2/\5\3\2\2\2\60\61\7\13\2\2"+
		"\61\7\3\2\2\2\62\63\7\64\2\2\63\t\3\2\2\2\64\65\5\4\3\2\65\66\7\16\2\2"+
		"\668\3\2\2\2\67\64\3\2\2\289\3\2\2\29\67\3\2\2\29:\3\2\2\2:;\3\2\2\2;"+
		"<\5\20\t\2<\13\3\2\2\2=B\5\20\t\2>?\7\3\2\2?A\5\20\t\2@>\3\2\2\2AD\3\2"+
		"\2\2B@\3\2\2\2BC\3\2\2\2C\r\3\2\2\2DB\3\2\2\2EI\7\17\2\2FH\5\f\7\2GF\3"+
		"\2\2\2HK\3\2\2\2IG\3\2\2\2IJ\3\2\2\2JL\3\2\2\2KI\3\2\2\2LM\7\4\2\2M\17"+
		"\3\2\2\2NO\b\t\1\2Ob\5\16\b\2PQ\t\2\2\2Qb\5\20\t\24RS\7\5\2\2Sb\5\20\t"+
		"\23TU\t\3\2\2Ub\5\20\t\20VW\7\t\2\2WX\5\20\t\2XY\7\4\2\2Yb\3\2\2\2Zb\7"+
		"$\2\2[b\5\6\4\2\\b\5\4\3\2]b\7\r\2\2^b\7\22\2\2_b\7\23\2\2`b\7\n\2\2a"+
		"N\3\2\2\2aP\3\2\2\2aR\3\2\2\2aT\3\2\2\2aV\3\2\2\2aZ\3\2\2\2a[\3\2\2\2"+
		"a\\\3\2\2\2a]\3\2\2\2a^\3\2\2\2a_\3\2\2\2a`\3\2\2\2b|\3\2\2\2cd\f\22\2"+
		"\2de\7\6\2\2e{\5\20\t\23fg\f\21\2\2gh\t\4\2\2h{\5\20\t\22ij\f\17\2\2j"+
		"k\t\3\2\2k{\5\20\t\20lm\f\16\2\2mn\t\5\2\2n{\5\20\t\17op\f\r\2\2pq\t\6"+
		"\2\2q{\5\20\t\16rs\f\f\2\2st\7 \2\2t{\5\20\t\ruv\f\13\2\2vw\7!\2\2w{\5"+
		"\20\t\fxy\f\25\2\2y{\t\2\2\2zc\3\2\2\2zf\3\2\2\2zi\3\2\2\2zl\3\2\2\2z"+
		"o\3\2\2\2zr\3\2\2\2zu\3\2\2\2zx\3\2\2\2{~\3\2\2\2|z\3\2\2\2|}\3\2\2\2"+
		"}\21\3\2\2\2~|\3\2\2\2\177\u0080\5\24\13\2\u0080\u0081\7\n\2\2\u0081\u0086"+
		"\3\2\2\2\u0082\u0083\5\"\22\2\u0083\u0084\7\n\2\2\u0084\u0086\3\2\2\2"+
		"\u0085\177\3\2\2\2\u0085\u0082\3\2\2\2\u0086\23\3\2\2\2\u0087\u008f\5"+
		" \21\2\u0088\u008f\5\32\16\2\u0089\u008f\5\34\17\2\u008a\u008f\5\36\20"+
		"\2\u008b\u008f\5\n\6\2\u008c\u008f\5$\23\2\u008d\u008f\5\20\t\2\u008e"+
		"\u0087\3\2\2\2\u008e\u0088\3\2\2\2\u008e\u0089\3\2\2\2\u008e\u008a\3\2"+
		"\2\2\u008e\u008b\3\2\2\2\u008e\u008c\3\2\2\2\u008e\u008d\3\2\2\2\u008f"+
		"\25\3\2\2\2\u0090\u0091\7%\2\2\u0091\u0092\5\20\t\2\u0092\u0098\t\7\2"+
		"\2\u0093\u0094\5\24\13\2\u0094\u0095\7\n\2\2\u0095\u0097\3\2\2\2\u0096"+
		"\u0093\3\2\2\2\u0097\u009a\3\2\2\2\u0098\u0096\3\2\2\2\u0098\u0099\3\2"+
		"\2\2\u0099\u009b\3\2\2\2\u009a\u0098\3\2\2\2\u009b\u009c\7$\2\2\u009c"+
		"\27\3\2\2\2\u009d\u009e\7%\2\2\u009e\u009f\5\20\t\2\u009f\u00a5\t\7\2"+
		"\2\u00a0\u00a1\5\24\13\2\u00a1\u00a2\7\n\2\2\u00a2\u00a4\3\2\2\2\u00a3"+
		"\u00a0\3\2\2\2\u00a4\u00a7\3\2\2\2\u00a5\u00a3\3\2\2\2\u00a5\u00a6\3\2"+
		"\2\2\u00a6\u00a8\3\2\2\2\u00a7\u00a5\3\2\2\2\u00a8\u00ae\7\'\2\2\u00a9"+
		"\u00aa\5\24\13\2\u00aa\u00ab\7\n\2\2\u00ab\u00ad\3\2\2\2\u00ac\u00a9\3"+
		"\2\2\2\u00ad\u00b0\3\2\2\2\u00ae\u00ac\3\2\2\2\u00ae\u00af\3\2\2\2\u00af"+
		"\u00b1\3\2\2\2\u00b0\u00ae\3\2\2\2\u00b1\u00b2\7$\2\2\u00b2\31\3\2\2\2"+
		"\u00b3\u00b6\5\30\r\2\u00b4\u00b6\5\26\f\2\u00b5\u00b3\3\2\2\2\u00b5\u00b4"+
		"\3\2\2\2\u00b6\33\3\2\2\2\u00b7\u00b8\7(\2\2\u00b8\u00b9\5\20\t\2\u00b9"+
		"\u00bf\t\b\2\2\u00ba\u00bb\5\24\13\2\u00bb\u00bc\7\n\2\2\u00bc\u00be\3"+
		"\2\2\2\u00bd\u00ba\3\2\2\2\u00be\u00c1\3\2\2\2\u00bf\u00bd\3\2\2\2\u00bf"+
		"\u00c0\3\2\2\2\u00c0\u00c2\3\2\2\2\u00c1\u00bf\3\2\2\2\u00c2\u00c3\7$"+
		"\2\2\u00c3\35\3\2\2\2\u00c4\u00ca\7*\2\2\u00c5\u00c6\5\26\f\2\u00c6\u00c7"+
		"\7\n\2\2\u00c7\u00c9\3\2\2\2\u00c8\u00c5\3\2\2\2\u00c9\u00cc\3\2\2\2\u00ca"+
		"\u00c8\3\2\2\2\u00ca\u00cb\3\2\2\2\u00cb\u00cd\3\2\2\2\u00cc\u00ca\3\2"+
		"\2\2\u00cd\u00ce\7$\2\2\u00ce\37\3\2\2\2\u00cf\u00d0\7+\2\2\u00d0\u00d1"+
		"\5\16\b\2\u00d1\u00d5\t\t\2\2\u00d2\u00d4\5\b\5\2\u00d3\u00d2\3\2\2\2"+
		"\u00d4\u00d7\3\2\2\2\u00d5\u00d3\3\2\2\2\u00d5\u00d6\3\2\2\2\u00d6\u00db"+
		"\3\2\2\2\u00d7\u00d5\3\2\2\2\u00d8\u00d9\5\24\13\2\u00d9\u00da\7\n\2\2"+
		"\u00da\u00dc\3\2\2\2\u00db\u00d8\3\2\2\2\u00dc\u00dd\3\2\2\2\u00dd\u00db"+
		"\3\2\2\2\u00dd\u00de\3\2\2\2\u00de\u00df\3\2\2\2\u00df\u00e0\7$\2\2\u00e0"+
		"!\3\2\2\2\u00e1\u00e2\7-\2\2\u00e2\u00e5\7\23\2\2\u00e3\u00e4\7\3\2\2"+
		"\u00e4\u00e6\7\23\2\2\u00e5\u00e3\3\2\2\2\u00e5\u00e6\3\2\2\2\u00e6\u00e7"+
		"\3\2\2\2\u00e7\u00ed\t\t\2\2\u00e8\u00e9\5\24\13\2\u00e9\u00ea\7\n\2\2"+
		"\u00ea\u00ec\3\2\2\2\u00eb\u00e8\3\2\2\2\u00ec\u00ef\3\2\2\2\u00ed\u00eb"+
		"\3\2\2\2\u00ed\u00ee\3\2\2\2\u00ee\u00f0\3\2\2\2\u00ef\u00ed\3\2\2\2\u00f0"+
		"\u00f1\7$\2\2\u00f1#\3\2\2\2\u00f2\u00f8\7.\2\2\u00f3\u00f4\5\24\13\2"+
		"\u00f4\u00f5\7\n\2\2\u00f5\u00f7\3\2\2\2\u00f6\u00f3\3\2\2\2\u00f7\u00fa"+
		"\3\2\2\2\u00f8\u00f6\3\2\2\2\u00f8\u00f9\3\2\2\2\u00f9\u00fb\3\2\2\2\u00fa"+
		"\u00f8\3\2\2\2\u00fb\u0101\7/\2\2\u00fc\u00fd\5\24\13\2\u00fd\u00fe\7"+
		"\n\2\2\u00fe\u0100\3\2\2\2\u00ff\u00fc\3\2\2\2\u0100\u0103\3\2\2\2\u0101"+
		"\u00ff\3\2\2\2\u0101\u0102\3\2\2\2\u0102\u0104\3\2\2\2\u0103\u0101\3\2"+
		"\2\2\u0104\u0105\7$\2\2\u0105%\3\2\2\2\27)9BIaz|\u0085\u008e\u0098\u00a5"+
		"\u00ae\u00b5\u00bf\u00ca\u00d5\u00dd\u00e5\u00ed\u00f8\u0101";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}