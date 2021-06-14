// Generated from QDLParser.g4 by ANTLR 4.9.1
package edu.uiuc.ncsa.qdl.generated;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNDeserializer;
import org.antlr.v4.runtime.atn.ParserATNSimulator;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.tree.ParseTreeListener;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.List;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class QDLParserParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.9.1", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, T__2=3, T__3=4, T__4=5, ConstantKeywords=6, ASSERT=7, 
		ASSERT2=8, BOOL_FALSE=9, BOOL_TRUE=10, BODY=11, CATCH=12, DEFINE=13, DO=14, 
		ELSE=15, IF=16, MODULE=17, Null=18, SWITCH=19, THEN=20, TRY=21, WHILE=22, 
		Integer=23, Decimal=24, SCIENTIFIC_NUMBER=25, Bool=26, STRING=27, LeftBracket=28, 
		RightBracket=29, Comma=30, Colon=31, SemiColon=32, LDoubleBracket=33, 
		RDoubleBracket=34, LambdaConnector=35, Times=36, Divide=37, PlusPlus=38, 
		Plus=39, MinusMinus=40, Minus=41, LessThan=42, GreaterThan=43, SingleEqual=44, 
		LessEquals=45, MoreEquals=46, Equals=47, NotEquals=48, RegexMatches=49, 
		LogicalNot=50, Exponentiation=51, And=52, Or=53, Backtick=54, Percent=55, 
		Tilde=56, Backslash=57, Stile=58, TildeRight=59, StemDot=60, ASSIGN=61, 
		Identifier=62, FuncStart=63, F_REF=64, FDOC=65, WS=66, COMMENT=67, LINE_COMMENT=68;
	public static final int
		RULE_elements = 0, RULE_element = 1, RULE_statement = 2, RULE_conditionalStatement = 3, 
		RULE_ifStatement = 4, RULE_ifElseStatement = 5, RULE_loopStatement = 6, 
		RULE_switchStatement = 7, RULE_defineStatement = 8, RULE_lambdaStatement = 9, 
		RULE_moduleStatement = 10, RULE_tryCatchStatement = 11, RULE_assertStatement = 12, 
		RULE_assertStatement2 = 13, RULE_statementBlock = 14, RULE_docStatementBlock = 15, 
		RULE_expressionBlock = 16, RULE_conditionalBlock = 17, RULE_fdoc = 18, 
		RULE_iInterval = 19, RULE_rInterval = 20, RULE_stemVariable = 21, RULE_stemEntry = 22, 
		RULE_stemList = 23, RULE_stemValue = 24, RULE_function = 25, RULE_f_arg = 26, 
		RULE_f_args = 27, RULE_f_ref = 28, RULE_expression = 29, RULE_variable = 30, 
		RULE_number = 31, RULE_integer = 32, RULE_keyword = 33;
	private static String[] makeRuleNames() {
		return new String[] {
			"elements", "element", "statement", "conditionalStatement", "ifStatement", 
			"ifElseStatement", "loopStatement", "switchStatement", "defineStatement", 
			"lambdaStatement", "moduleStatement", "tryCatchStatement", "assertStatement", 
			"assertStatement2", "statementBlock", "docStatementBlock", "expressionBlock", 
			"conditionalBlock", "fdoc", "iInterval", "rInterval", "stemVariable", 
			"stemEntry", "stemList", "stemValue", "function", "f_arg", "f_args", 
			"f_ref", "expression", "variable", "number", "integer", "keyword"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'{'", "'}'", "')'", "'('", "'?'", null, "'assert'", "'\u22A8'", 
			null, null, "'body'", "'catch'", "'define'", "'do'", "'else'", "'if'", 
			"'module'", null, "'switch'", "'then'", "'try'", "'while'", null, null, 
			null, null, null, "'['", "']'", "','", "':'", "';'", null, null, null, 
			null, null, "'++'", "'+'", "'--'", "'-'", "'<'", "'>'", "'='", null, 
			null, null, null, null, null, "'^'", null, null, "'`'", "'%'", "'~'", 
			"'\\'", "'|'", "'~|'", "'.'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, null, null, null, null, null, "ConstantKeywords", "ASSERT", "ASSERT2", 
			"BOOL_FALSE", "BOOL_TRUE", "BODY", "CATCH", "DEFINE", "DO", "ELSE", "IF", 
			"MODULE", "Null", "SWITCH", "THEN", "TRY", "WHILE", "Integer", "Decimal", 
			"SCIENTIFIC_NUMBER", "Bool", "STRING", "LeftBracket", "RightBracket", 
			"Comma", "Colon", "SemiColon", "LDoubleBracket", "RDoubleBracket", "LambdaConnector", 
			"Times", "Divide", "PlusPlus", "Plus", "MinusMinus", "Minus", "LessThan", 
			"GreaterThan", "SingleEqual", "LessEquals", "MoreEquals", "Equals", "NotEquals", 
			"RegexMatches", "LogicalNot", "Exponentiation", "And", "Or", "Backtick", 
			"Percent", "Tilde", "Backslash", "Stile", "TildeRight", "StemDot", "ASSIGN", 
			"Identifier", "FuncStart", "F_REF", "FDOC", "WS", "COMMENT", "LINE_COMMENT"
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
			setState(71);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__0) | (1L << T__3) | (1L << ConstantKeywords) | (1L << ASSERT) | (1L << ASSERT2) | (1L << DEFINE) | (1L << IF) | (1L << MODULE) | (1L << Null) | (1L << SWITCH) | (1L << TRY) | (1L << WHILE) | (1L << Integer) | (1L << Decimal) | (1L << SCIENTIFIC_NUMBER) | (1L << Bool) | (1L << STRING) | (1L << LeftBracket) | (1L << SemiColon) | (1L << LDoubleBracket) | (1L << PlusPlus) | (1L << Plus) | (1L << MinusMinus) | (1L << Minus) | (1L << LogicalNot) | (1L << Identifier) | (1L << FuncStart))) != 0)) {
				{
				{
				setState(68);
				element();
				}
				}
				setState(73);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(74);
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

	public static class ElementContext extends ParserRuleContext {
		public StatementContext statement() {
			return getRuleContext(StatementContext.class,0);
		}
		public TerminalNode SemiColon() { return getToken(QDLParserParser.SemiColon, 0); }
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
		enterRule(_localctx, 2, RULE_element);
		try {
			setState(82);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__0:
			case T__3:
			case ConstantKeywords:
			case ASSERT:
			case ASSERT2:
			case DEFINE:
			case IF:
			case Null:
			case SWITCH:
			case TRY:
			case WHILE:
			case Integer:
			case Decimal:
			case SCIENTIFIC_NUMBER:
			case Bool:
			case STRING:
			case LeftBracket:
			case SemiColon:
			case LDoubleBracket:
			case PlusPlus:
			case Plus:
			case MinusMinus:
			case Minus:
			case LogicalNot:
			case Identifier:
			case FuncStart:
				enterOuterAlt(_localctx, 1);
				{
				{
				setState(76);
				statement();
				setState(77);
				match(SemiColon);
				}
				}
				break;
			case MODULE:
				enterOuterAlt(_localctx, 2);
				{
				{
				setState(79);
				moduleStatement();
				setState(80);
				match(SemiColon);
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
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public TryCatchStatementContext tryCatchStatement() {
			return getRuleContext(TryCatchStatementContext.class,0);
		}
		public LambdaStatementContext lambdaStatement() {
			return getRuleContext(LambdaStatementContext.class,0);
		}
		public AssertStatementContext assertStatement() {
			return getRuleContext(AssertStatementContext.class,0);
		}
		public AssertStatement2Context assertStatement2() {
			return getRuleContext(AssertStatement2Context.class,0);
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
		enterRule(_localctx, 4, RULE_statement);
		try {
			setState(93);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,2,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(84);
				defineStatement();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(85);
				conditionalStatement();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(86);
				loopStatement();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(87);
				switchStatement();
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(88);
				expression(0);
				}
				break;
			case 6:
				enterOuterAlt(_localctx, 6);
				{
				setState(89);
				tryCatchStatement();
				}
				break;
			case 7:
				enterOuterAlt(_localctx, 7);
				{
				setState(90);
				lambdaStatement();
				}
				break;
			case 8:
				enterOuterAlt(_localctx, 8);
				{
				setState(91);
				assertStatement();
				}
				break;
			case 9:
				enterOuterAlt(_localctx, 9);
				{
				setState(92);
				assertStatement2();
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

	public static class ConditionalStatementContext extends ParserRuleContext {
		public IfStatementContext ifStatement() {
			return getRuleContext(IfStatementContext.class,0);
		}
		public IfElseStatementContext ifElseStatement() {
			return getRuleContext(IfElseStatementContext.class,0);
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
		enterRule(_localctx, 6, RULE_conditionalStatement);
		try {
			setState(97);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,3,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(95);
				ifStatement();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(96);
				ifElseStatement();
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
		public TerminalNode IF() { return getToken(QDLParserParser.IF, 0); }
		public ConditionalBlockContext conditionalBlock() {
			return getRuleContext(ConditionalBlockContext.class,0);
		}
		public StatementBlockContext statementBlock() {
			return getRuleContext(StatementBlockContext.class,0);
		}
		public TerminalNode THEN() { return getToken(QDLParserParser.THEN, 0); }
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
		enterRule(_localctx, 8, RULE_ifStatement);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(99);
			match(IF);
			setState(100);
			conditionalBlock();
			setState(102);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==THEN) {
				{
				setState(101);
				match(THEN);
				}
			}

			setState(104);
			statementBlock();
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
		public TerminalNode IF() { return getToken(QDLParserParser.IF, 0); }
		public ConditionalBlockContext conditionalBlock() {
			return getRuleContext(ConditionalBlockContext.class,0);
		}
		public List<StatementBlockContext> statementBlock() {
			return getRuleContexts(StatementBlockContext.class);
		}
		public StatementBlockContext statementBlock(int i) {
			return getRuleContext(StatementBlockContext.class,i);
		}
		public TerminalNode ELSE() { return getToken(QDLParserParser.ELSE, 0); }
		public TerminalNode THEN() { return getToken(QDLParserParser.THEN, 0); }
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
		enterRule(_localctx, 10, RULE_ifElseStatement);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(106);
			match(IF);
			setState(107);
			conditionalBlock();
			setState(109);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==THEN) {
				{
				setState(108);
				match(THEN);
				}
			}

			setState(111);
			statementBlock();
			setState(112);
			match(ELSE);
			setState(113);
			statementBlock();
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
		public TerminalNode WHILE() { return getToken(QDLParserParser.WHILE, 0); }
		public ConditionalBlockContext conditionalBlock() {
			return getRuleContext(ConditionalBlockContext.class,0);
		}
		public StatementBlockContext statementBlock() {
			return getRuleContext(StatementBlockContext.class,0);
		}
		public TerminalNode DO() { return getToken(QDLParserParser.DO, 0); }
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
		enterRule(_localctx, 12, RULE_loopStatement);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(115);
			match(WHILE);
			setState(116);
			conditionalBlock();
			setState(118);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==DO) {
				{
				setState(117);
				match(DO);
				}
			}

			setState(120);
			statementBlock();
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
		public TerminalNode SWITCH() { return getToken(QDLParserParser.SWITCH, 0); }
		public TerminalNode LeftBracket() { return getToken(QDLParserParser.LeftBracket, 0); }
		public TerminalNode RightBracket() { return getToken(QDLParserParser.RightBracket, 0); }
		public List<IfStatementContext> ifStatement() {
			return getRuleContexts(IfStatementContext.class);
		}
		public IfStatementContext ifStatement(int i) {
			return getRuleContext(IfStatementContext.class,i);
		}
		public List<TerminalNode> SemiColon() { return getTokens(QDLParserParser.SemiColon); }
		public TerminalNode SemiColon(int i) {
			return getToken(QDLParserParser.SemiColon, i);
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
		enterRule(_localctx, 14, RULE_switchStatement);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(122);
			match(SWITCH);
			setState(123);
			match(LeftBracket);
			setState(129);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==IF) {
				{
				{
				setState(124);
				ifStatement();
				setState(125);
				match(SemiColon);
				}
				}
				setState(131);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(132);
			match(RightBracket);
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
		public TerminalNode DEFINE() { return getToken(QDLParserParser.DEFINE, 0); }
		public TerminalNode LeftBracket() { return getToken(QDLParserParser.LeftBracket, 0); }
		public FunctionContext function() {
			return getRuleContext(FunctionContext.class,0);
		}
		public TerminalNode RightBracket() { return getToken(QDLParserParser.RightBracket, 0); }
		public DocStatementBlockContext docStatementBlock() {
			return getRuleContext(DocStatementBlockContext.class,0);
		}
		public TerminalNode BODY() { return getToken(QDLParserParser.BODY, 0); }
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
		enterRule(_localctx, 16, RULE_defineStatement);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(134);
			match(DEFINE);
			setState(135);
			match(LeftBracket);
			setState(136);
			function();
			setState(137);
			match(RightBracket);
			setState(139);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==BODY) {
				{
				setState(138);
				match(BODY);
				}
			}

			setState(141);
			docStatementBlock();
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

	public static class LambdaStatementContext extends ParserRuleContext {
		public FunctionContext function() {
			return getRuleContext(FunctionContext.class,0);
		}
		public TerminalNode LambdaConnector() { return getToken(QDLParserParser.LambdaConnector, 0); }
		public StatementContext statement() {
			return getRuleContext(StatementContext.class,0);
		}
		public StatementBlockContext statementBlock() {
			return getRuleContext(StatementBlockContext.class,0);
		}
		public LambdaStatementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_lambdaStatement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof QDLParserListener ) ((QDLParserListener)listener).enterLambdaStatement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof QDLParserListener ) ((QDLParserListener)listener).exitLambdaStatement(this);
		}
	}

	public final LambdaStatementContext lambdaStatement() throws RecognitionException {
		LambdaStatementContext _localctx = new LambdaStatementContext(_ctx, getState());
		enterRule(_localctx, 18, RULE_lambdaStatement);
		try {
			setState(148);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case FuncStart:
				enterOuterAlt(_localctx, 1);
				{
				setState(143);
				function();
				setState(144);
				match(LambdaConnector);
				{
				setState(145);
				statement();
				}
				}
				break;
			case LeftBracket:
				enterOuterAlt(_localctx, 2);
				{
				setState(147);
				statementBlock();
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

	public static class ModuleStatementContext extends ParserRuleContext {
		public TerminalNode MODULE() { return getToken(QDLParserParser.MODULE, 0); }
		public TerminalNode LeftBracket() { return getToken(QDLParserParser.LeftBracket, 0); }
		public List<TerminalNode> STRING() { return getTokens(QDLParserParser.STRING); }
		public TerminalNode STRING(int i) {
			return getToken(QDLParserParser.STRING, i);
		}
		public TerminalNode RightBracket() { return getToken(QDLParserParser.RightBracket, 0); }
		public DocStatementBlockContext docStatementBlock() {
			return getRuleContext(DocStatementBlockContext.class,0);
		}
		public TerminalNode Comma() { return getToken(QDLParserParser.Comma, 0); }
		public TerminalNode BODY() { return getToken(QDLParserParser.BODY, 0); }
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
		enterRule(_localctx, 20, RULE_moduleStatement);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(150);
			match(MODULE);
			setState(151);
			match(LeftBracket);
			setState(152);
			match(STRING);
			setState(155);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==Comma) {
				{
				setState(153);
				match(Comma);
				setState(154);
				match(STRING);
				}
			}

			setState(157);
			match(RightBracket);
			setState(159);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==BODY) {
				{
				setState(158);
				match(BODY);
				}
			}

			setState(161);
			docStatementBlock();
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
		public TerminalNode TRY() { return getToken(QDLParserParser.TRY, 0); }
		public List<StatementBlockContext> statementBlock() {
			return getRuleContexts(StatementBlockContext.class);
		}
		public StatementBlockContext statementBlock(int i) {
			return getRuleContext(StatementBlockContext.class,i);
		}
		public TerminalNode CATCH() { return getToken(QDLParserParser.CATCH, 0); }
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
		enterRule(_localctx, 22, RULE_tryCatchStatement);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(163);
			match(TRY);
			setState(164);
			statementBlock();
			setState(165);
			match(CATCH);
			setState(166);
			statementBlock();
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

	public static class AssertStatementContext extends ParserRuleContext {
		public TerminalNode ASSERT() { return getToken(QDLParserParser.ASSERT, 0); }
		public List<TerminalNode> LeftBracket() { return getTokens(QDLParserParser.LeftBracket); }
		public TerminalNode LeftBracket(int i) {
			return getToken(QDLParserParser.LeftBracket, i);
		}
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public List<TerminalNode> RightBracket() { return getTokens(QDLParserParser.RightBracket); }
		public TerminalNode RightBracket(int i) {
			return getToken(QDLParserParser.RightBracket, i);
		}
		public AssertStatementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_assertStatement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof QDLParserListener ) ((QDLParserListener)listener).enterAssertStatement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof QDLParserListener ) ((QDLParserListener)listener).exitAssertStatement(this);
		}
	}

	public final AssertStatementContext assertStatement() throws RecognitionException {
		AssertStatementContext _localctx = new AssertStatementContext(_ctx, getState());
		enterRule(_localctx, 24, RULE_assertStatement);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(168);
			match(ASSERT);
			setState(169);
			match(LeftBracket);
			setState(170);
			expression(0);
			setState(171);
			match(RightBracket);
			setState(172);
			match(LeftBracket);
			setState(173);
			expression(0);
			setState(174);
			match(RightBracket);
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

	public static class AssertStatement2Context extends ParserRuleContext {
		public TerminalNode ASSERT2() { return getToken(QDLParserParser.ASSERT2, 0); }
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public TerminalNode Colon() { return getToken(QDLParserParser.Colon, 0); }
		public AssertStatement2Context(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_assertStatement2; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof QDLParserListener ) ((QDLParserListener)listener).enterAssertStatement2(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof QDLParserListener ) ((QDLParserListener)listener).exitAssertStatement2(this);
		}
	}

	public final AssertStatement2Context assertStatement2() throws RecognitionException {
		AssertStatement2Context _localctx = new AssertStatement2Context(_ctx, getState());
		enterRule(_localctx, 26, RULE_assertStatement2);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(176);
			match(ASSERT2);
			setState(177);
			expression(0);
			setState(180);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==Colon) {
				{
				setState(178);
				match(Colon);
				setState(179);
				expression(0);
				}
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

	public static class StatementBlockContext extends ParserRuleContext {
		public TerminalNode LeftBracket() { return getToken(QDLParserParser.LeftBracket, 0); }
		public TerminalNode RightBracket() { return getToken(QDLParserParser.RightBracket, 0); }
		public List<StatementContext> statement() {
			return getRuleContexts(StatementContext.class);
		}
		public StatementContext statement(int i) {
			return getRuleContext(StatementContext.class,i);
		}
		public List<TerminalNode> SemiColon() { return getTokens(QDLParserParser.SemiColon); }
		public TerminalNode SemiColon(int i) {
			return getToken(QDLParserParser.SemiColon, i);
		}
		public StatementBlockContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_statementBlock; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof QDLParserListener ) ((QDLParserListener)listener).enterStatementBlock(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof QDLParserListener ) ((QDLParserListener)listener).exitStatementBlock(this);
		}
	}

	public final StatementBlockContext statementBlock() throws RecognitionException {
		StatementBlockContext _localctx = new StatementBlockContext(_ctx, getState());
		enterRule(_localctx, 28, RULE_statementBlock);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(182);
			match(LeftBracket);
			setState(188);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__0) | (1L << T__3) | (1L << ConstantKeywords) | (1L << ASSERT) | (1L << ASSERT2) | (1L << DEFINE) | (1L << IF) | (1L << Null) | (1L << SWITCH) | (1L << TRY) | (1L << WHILE) | (1L << Integer) | (1L << Decimal) | (1L << SCIENTIFIC_NUMBER) | (1L << Bool) | (1L << STRING) | (1L << LeftBracket) | (1L << SemiColon) | (1L << LDoubleBracket) | (1L << PlusPlus) | (1L << Plus) | (1L << MinusMinus) | (1L << Minus) | (1L << LogicalNot) | (1L << Identifier) | (1L << FuncStart))) != 0)) {
				{
				{
				setState(183);
				statement();
				setState(184);
				match(SemiColon);
				}
				}
				setState(190);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(191);
			match(RightBracket);
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

	public static class DocStatementBlockContext extends ParserRuleContext {
		public TerminalNode LeftBracket() { return getToken(QDLParserParser.LeftBracket, 0); }
		public TerminalNode RightBracket() { return getToken(QDLParserParser.RightBracket, 0); }
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
		public List<TerminalNode> SemiColon() { return getTokens(QDLParserParser.SemiColon); }
		public TerminalNode SemiColon(int i) {
			return getToken(QDLParserParser.SemiColon, i);
		}
		public DocStatementBlockContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_docStatementBlock; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof QDLParserListener ) ((QDLParserListener)listener).enterDocStatementBlock(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof QDLParserListener ) ((QDLParserListener)listener).exitDocStatementBlock(this);
		}
	}

	public final DocStatementBlockContext docStatementBlock() throws RecognitionException {
		DocStatementBlockContext _localctx = new DocStatementBlockContext(_ctx, getState());
		enterRule(_localctx, 30, RULE_docStatementBlock);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(193);
			match(LeftBracket);
			setState(197);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==FDOC) {
				{
				{
				setState(194);
				fdoc();
				}
				}
				setState(199);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(203); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(200);
				statement();
				setState(201);
				match(SemiColon);
				}
				}
				setState(205); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( (((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__0) | (1L << T__3) | (1L << ConstantKeywords) | (1L << ASSERT) | (1L << ASSERT2) | (1L << DEFINE) | (1L << IF) | (1L << Null) | (1L << SWITCH) | (1L << TRY) | (1L << WHILE) | (1L << Integer) | (1L << Decimal) | (1L << SCIENTIFIC_NUMBER) | (1L << Bool) | (1L << STRING) | (1L << LeftBracket) | (1L << SemiColon) | (1L << LDoubleBracket) | (1L << PlusPlus) | (1L << Plus) | (1L << MinusMinus) | (1L << Minus) | (1L << LogicalNot) | (1L << Identifier) | (1L << FuncStart))) != 0) );
			setState(207);
			match(RightBracket);
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

	public static class ExpressionBlockContext extends ParserRuleContext {
		public TerminalNode LeftBracket() { return getToken(QDLParserParser.LeftBracket, 0); }
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public List<TerminalNode> SemiColon() { return getTokens(QDLParserParser.SemiColon); }
		public TerminalNode SemiColon(int i) {
			return getToken(QDLParserParser.SemiColon, i);
		}
		public TerminalNode RightBracket() { return getToken(QDLParserParser.RightBracket, 0); }
		public ExpressionBlockContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_expressionBlock; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof QDLParserListener ) ((QDLParserListener)listener).enterExpressionBlock(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof QDLParserListener ) ((QDLParserListener)listener).exitExpressionBlock(this);
		}
	}

	public final ExpressionBlockContext expressionBlock() throws RecognitionException {
		ExpressionBlockContext _localctx = new ExpressionBlockContext(_ctx, getState());
		enterRule(_localctx, 32, RULE_expressionBlock);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(209);
			match(LeftBracket);
			setState(210);
			expression(0);
			setState(211);
			match(SemiColon);
			setState(215); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(212);
				expression(0);
				setState(213);
				match(SemiColon);
				}
				}
				setState(217); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( (((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__0) | (1L << T__3) | (1L << ConstantKeywords) | (1L << Null) | (1L << Integer) | (1L << Decimal) | (1L << SCIENTIFIC_NUMBER) | (1L << Bool) | (1L << STRING) | (1L << LeftBracket) | (1L << SemiColon) | (1L << LDoubleBracket) | (1L << PlusPlus) | (1L << Plus) | (1L << MinusMinus) | (1L << Minus) | (1L << LogicalNot) | (1L << Identifier) | (1L << FuncStart))) != 0) );
			setState(219);
			match(RightBracket);
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

	public static class ConditionalBlockContext extends ParserRuleContext {
		public TerminalNode LeftBracket() { return getToken(QDLParserParser.LeftBracket, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public TerminalNode RightBracket() { return getToken(QDLParserParser.RightBracket, 0); }
		public ConditionalBlockContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_conditionalBlock; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof QDLParserListener ) ((QDLParserListener)listener).enterConditionalBlock(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof QDLParserListener ) ((QDLParserListener)listener).exitConditionalBlock(this);
		}
	}

	public final ConditionalBlockContext conditionalBlock() throws RecognitionException {
		ConditionalBlockContext _localctx = new ConditionalBlockContext(_ctx, getState());
		enterRule(_localctx, 34, RULE_conditionalBlock);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(221);
			match(LeftBracket);
			setState(222);
			expression(0);
			setState(223);
			match(RightBracket);
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
		enterRule(_localctx, 36, RULE_fdoc);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(225);
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

	public static class IIntervalContext extends ParserRuleContext {
		public TerminalNode LeftBracket() { return getToken(QDLParserParser.LeftBracket, 0); }
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public List<TerminalNode> SemiColon() { return getTokens(QDLParserParser.SemiColon); }
		public TerminalNode SemiColon(int i) {
			return getToken(QDLParserParser.SemiColon, i);
		}
		public TerminalNode RightBracket() { return getToken(QDLParserParser.RightBracket, 0); }
		public IIntervalContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_iInterval; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof QDLParserListener ) ((QDLParserListener)listener).enterIInterval(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof QDLParserListener ) ((QDLParserListener)listener).exitIInterval(this);
		}
	}

	public final IIntervalContext iInterval() throws RecognitionException {
		IIntervalContext _localctx = new IIntervalContext(_ctx, getState());
		enterRule(_localctx, 38, RULE_iInterval);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(227);
			match(LeftBracket);
			setState(228);
			expression(0);
			setState(229);
			match(SemiColon);
			setState(230);
			expression(0);
			setState(233);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==SemiColon) {
				{
				setState(231);
				match(SemiColon);
				setState(232);
				expression(0);
				}
			}

			setState(235);
			match(RightBracket);
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

	public static class RIntervalContext extends ParserRuleContext {
		public TerminalNode LDoubleBracket() { return getToken(QDLParserParser.LDoubleBracket, 0); }
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public List<TerminalNode> SemiColon() { return getTokens(QDLParserParser.SemiColon); }
		public TerminalNode SemiColon(int i) {
			return getToken(QDLParserParser.SemiColon, i);
		}
		public TerminalNode RDoubleBracket() { return getToken(QDLParserParser.RDoubleBracket, 0); }
		public RIntervalContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_rInterval; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof QDLParserListener ) ((QDLParserListener)listener).enterRInterval(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof QDLParserListener ) ((QDLParserListener)listener).exitRInterval(this);
		}
	}

	public final RIntervalContext rInterval() throws RecognitionException {
		RIntervalContext _localctx = new RIntervalContext(_ctx, getState());
		enterRule(_localctx, 40, RULE_rInterval);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(237);
			match(LDoubleBracket);
			setState(238);
			expression(0);
			setState(239);
			match(SemiColon);
			setState(240);
			expression(0);
			setState(241);
			match(SemiColon);
			setState(242);
			expression(0);
			setState(243);
			match(RDoubleBracket);
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

	public static class StemVariableContext extends ParserRuleContext {
		public List<StemEntryContext> stemEntry() {
			return getRuleContexts(StemEntryContext.class);
		}
		public StemEntryContext stemEntry(int i) {
			return getRuleContext(StemEntryContext.class,i);
		}
		public List<TerminalNode> Comma() { return getTokens(QDLParserParser.Comma); }
		public TerminalNode Comma(int i) {
			return getToken(QDLParserParser.Comma, i);
		}
		public StemVariableContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_stemVariable; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof QDLParserListener ) ((QDLParserListener)listener).enterStemVariable(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof QDLParserListener ) ((QDLParserListener)listener).exitStemVariable(this);
		}
	}

	public final StemVariableContext stemVariable() throws RecognitionException {
		StemVariableContext _localctx = new StemVariableContext(_ctx, getState());
		enterRule(_localctx, 42, RULE_stemVariable);
		int _la;
		try {
			setState(258);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,19,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(245);
				match(T__0);
				setState(246);
				stemEntry();
				setState(251);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==Comma) {
					{
					{
					setState(247);
					match(Comma);
					setState(248);
					stemEntry();
					}
					}
					setState(253);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(254);
				match(T__1);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(256);
				match(T__0);
				setState(257);
				match(T__1);
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

	public static class StemEntryContext extends ParserRuleContext {
		public TerminalNode Colon() { return getToken(QDLParserParser.Colon, 0); }
		public StemValueContext stemValue() {
			return getRuleContext(StemValueContext.class,0);
		}
		public TerminalNode Times() { return getToken(QDLParserParser.Times, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public StemEntryContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_stemEntry; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof QDLParserListener ) ((QDLParserListener)listener).enterStemEntry(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof QDLParserListener ) ((QDLParserListener)listener).exitStemEntry(this);
		}
	}

	public final StemEntryContext stemEntry() throws RecognitionException {
		StemEntryContext _localctx = new StemEntryContext(_ctx, getState());
		enterRule(_localctx, 44, RULE_stemEntry);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(262);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case Times:
				{
				setState(260);
				match(Times);
				}
				break;
			case T__0:
			case T__3:
			case ConstantKeywords:
			case Null:
			case Integer:
			case Decimal:
			case SCIENTIFIC_NUMBER:
			case Bool:
			case STRING:
			case LeftBracket:
			case SemiColon:
			case LDoubleBracket:
			case PlusPlus:
			case Plus:
			case MinusMinus:
			case Minus:
			case LogicalNot:
			case Identifier:
			case FuncStart:
				{
				setState(261);
				expression(0);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			setState(264);
			match(Colon);
			setState(265);
			stemValue();
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

	public static class StemListContext extends ParserRuleContext {
		public TerminalNode LeftBracket() { return getToken(QDLParserParser.LeftBracket, 0); }
		public List<StemValueContext> stemValue() {
			return getRuleContexts(StemValueContext.class);
		}
		public StemValueContext stemValue(int i) {
			return getRuleContext(StemValueContext.class,i);
		}
		public TerminalNode RightBracket() { return getToken(QDLParserParser.RightBracket, 0); }
		public List<TerminalNode> Comma() { return getTokens(QDLParserParser.Comma); }
		public TerminalNode Comma(int i) {
			return getToken(QDLParserParser.Comma, i);
		}
		public StemListContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_stemList; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof QDLParserListener ) ((QDLParserListener)listener).enterStemList(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof QDLParserListener ) ((QDLParserListener)listener).exitStemList(this);
		}
	}

	public final StemListContext stemList() throws RecognitionException {
		StemListContext _localctx = new StemListContext(_ctx, getState());
		enterRule(_localctx, 46, RULE_stemList);
		int _la;
		try {
			setState(280);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,22,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(267);
				match(LeftBracket);
				setState(268);
				stemValue();
				setState(273);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==Comma) {
					{
					{
					setState(269);
					match(Comma);
					setState(270);
					stemValue();
					}
					}
					setState(275);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(276);
				match(RightBracket);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(278);
				match(LeftBracket);
				setState(279);
				match(RightBracket);
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

	public static class StemValueContext extends ParserRuleContext {
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public StemVariableContext stemVariable() {
			return getRuleContext(StemVariableContext.class,0);
		}
		public StemListContext stemList() {
			return getRuleContext(StemListContext.class,0);
		}
		public StemValueContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_stemValue; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof QDLParserListener ) ((QDLParserListener)listener).enterStemValue(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof QDLParserListener ) ((QDLParserListener)listener).exitStemValue(this);
		}
	}

	public final StemValueContext stemValue() throws RecognitionException {
		StemValueContext _localctx = new StemValueContext(_ctx, getState());
		enterRule(_localctx, 48, RULE_stemValue);
		try {
			setState(285);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,23,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(282);
				expression(0);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(283);
				stemVariable();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(284);
				stemList();
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

	public static class FunctionContext extends ParserRuleContext {
		public TerminalNode FuncStart() { return getToken(QDLParserParser.FuncStart, 0); }
		public List<F_argsContext> f_args() {
			return getRuleContexts(F_argsContext.class);
		}
		public F_argsContext f_args(int i) {
			return getRuleContext(F_argsContext.class,i);
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
		enterRule(_localctx, 50, RULE_function);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(287);
			match(FuncStart);
			setState(291);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (((((_la - 1)) & ~0x3f) == 0 && ((1L << (_la - 1)) & ((1L << (T__0 - 1)) | (1L << (T__3 - 1)) | (1L << (ConstantKeywords - 1)) | (1L << (Null - 1)) | (1L << (Integer - 1)) | (1L << (Decimal - 1)) | (1L << (SCIENTIFIC_NUMBER - 1)) | (1L << (Bool - 1)) | (1L << (STRING - 1)) | (1L << (LeftBracket - 1)) | (1L << (SemiColon - 1)) | (1L << (LDoubleBracket - 1)) | (1L << (PlusPlus - 1)) | (1L << (Plus - 1)) | (1L << (MinusMinus - 1)) | (1L << (Minus - 1)) | (1L << (LogicalNot - 1)) | (1L << (Identifier - 1)) | (1L << (FuncStart - 1)) | (1L << (F_REF - 1)))) != 0)) {
				{
				{
				setState(288);
				f_args();
				}
				}
				setState(293);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(294);
			match(T__2);
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

	public static class F_argContext extends ParserRuleContext {
		public StemValueContext stemValue() {
			return getRuleContext(StemValueContext.class,0);
		}
		public F_refContext f_ref() {
			return getRuleContext(F_refContext.class,0);
		}
		public F_argContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_f_arg; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof QDLParserListener ) ((QDLParserListener)listener).enterF_arg(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof QDLParserListener ) ((QDLParserListener)listener).exitF_arg(this);
		}
	}

	public final F_argContext f_arg() throws RecognitionException {
		F_argContext _localctx = new F_argContext(_ctx, getState());
		enterRule(_localctx, 52, RULE_f_arg);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(298);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__0:
			case T__3:
			case ConstantKeywords:
			case Null:
			case Integer:
			case Decimal:
			case SCIENTIFIC_NUMBER:
			case Bool:
			case STRING:
			case LeftBracket:
			case SemiColon:
			case LDoubleBracket:
			case PlusPlus:
			case Plus:
			case MinusMinus:
			case Minus:
			case LogicalNot:
			case Identifier:
			case FuncStart:
				{
				setState(296);
				stemValue();
				}
				break;
			case F_REF:
				{
				setState(297);
				f_ref();
				}
				break;
			default:
				throw new NoViableAltException(this);
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

	public static class F_argsContext extends ParserRuleContext {
		public List<F_argContext> f_arg() {
			return getRuleContexts(F_argContext.class);
		}
		public F_argContext f_arg(int i) {
			return getRuleContext(F_argContext.class,i);
		}
		public List<TerminalNode> Comma() { return getTokens(QDLParserParser.Comma); }
		public TerminalNode Comma(int i) {
			return getToken(QDLParserParser.Comma, i);
		}
		public F_argsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_f_args; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof QDLParserListener ) ((QDLParserListener)listener).enterF_args(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof QDLParserListener ) ((QDLParserListener)listener).exitF_args(this);
		}
	}

	public final F_argsContext f_args() throws RecognitionException {
		F_argsContext _localctx = new F_argsContext(_ctx, getState());
		enterRule(_localctx, 54, RULE_f_args);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(300);
			f_arg();
			setState(305);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==Comma) {
				{
				{
				setState(301);
				match(Comma);
				setState(302);
				f_arg();
				}
				}
				setState(307);
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

	public static class F_refContext extends ParserRuleContext {
		public TerminalNode F_REF() { return getToken(QDLParserParser.F_REF, 0); }
		public F_refContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_f_ref; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof QDLParserListener ) ((QDLParserListener)listener).enterF_ref(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof QDLParserListener ) ((QDLParserListener)listener).exitF_ref(this);
		}
	}

	public final F_refContext f_ref() throws RecognitionException {
		F_refContext _localctx = new F_refContext(_ctx, getState());
		enterRule(_localctx, 56, RULE_f_ref);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(308);
			match(F_REF);
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
	public static class KeywordsContext extends ExpressionContext {
		public KeywordContext keyword() {
			return getRuleContext(KeywordContext.class,0);
		}
		public KeywordsContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof QDLParserListener ) ((QDLParserListener)listener).enterKeywords(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof QDLParserListener ) ((QDLParserListener)listener).exitKeywords(this);
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
	public static class TildeExpressionContext extends ExpressionContext {
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public TerminalNode Tilde() { return getToken(QDLParserParser.Tilde, 0); }
		public TerminalNode TildeRight() { return getToken(QDLParserParser.TildeRight, 0); }
		public TildeExpressionContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof QDLParserListener ) ((QDLParserListener)listener).enterTildeExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof QDLParserListener ) ((QDLParserListener)listener).exitTildeExpression(this);
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
		public TerminalNode LogicalNot() { return getToken(QDLParserParser.LogicalNot, 0); }
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
	public static class IntegersContext extends ExpressionContext {
		public IntegerContext integer() {
			return getRuleContext(IntegerContext.class,0);
		}
		public IntegersContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof QDLParserListener ) ((QDLParserListener)listener).enterIntegers(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof QDLParserListener ) ((QDLParserListener)listener).exitIntegers(this);
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
	public static class DotOp2Context extends ExpressionContext {
		public Token postfix;
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public TerminalNode StemDot() { return getToken(QDLParserParser.StemDot, 0); }
		public DotOp2Context(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof QDLParserListener ) ((QDLParserListener)listener).enterDotOp2(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof QDLParserListener ) ((QDLParserListener)listener).exitDotOp2(this);
		}
	}
	public static class LambdaDefContext extends ExpressionContext {
		public TerminalNode LambdaConnector() { return getToken(QDLParserParser.LambdaConnector, 0); }
		public FunctionContext function() {
			return getRuleContext(FunctionContext.class,0);
		}
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public ExpressionBlockContext expressionBlock() {
			return getRuleContext(ExpressionBlockContext.class,0);
		}
		public List<F_argsContext> f_args() {
			return getRuleContexts(F_argsContext.class);
		}
		public F_argsContext f_args(int i) {
			return getRuleContext(F_argsContext.class,i);
		}
		public LambdaDefContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof QDLParserListener ) ((QDLParserListener)listener).enterLambdaDef(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof QDLParserListener ) ((QDLParserListener)listener).exitLambdaDef(this);
		}
	}
	public static class RealIntervalContext extends ExpressionContext {
		public RIntervalContext rInterval() {
			return getRuleContext(RIntervalContext.class,0);
		}
		public RealIntervalContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof QDLParserListener ) ((QDLParserListener)listener).enterRealInterval(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof QDLParserListener ) ((QDLParserListener)listener).exitRealInterval(this);
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
	public static class AssignmentContext extends ExpressionContext {
		public Token op;
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public TerminalNode ASSIGN() { return getToken(QDLParserParser.ASSIGN, 0); }
		public AssignmentContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof QDLParserListener ) ((QDLParserListener)listener).enterAssignment(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof QDLParserListener ) ((QDLParserListener)listener).exitAssignment(this);
		}
	}
	public static class StemLiContext extends ExpressionContext {
		public StemListContext stemList() {
			return getRuleContext(StemListContext.class,0);
		}
		public StemLiContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof QDLParserListener ) ((QDLParserListener)listener).enterStemLi(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof QDLParserListener ) ((QDLParserListener)listener).exitStemLi(this);
		}
	}
	public static class RegexMatchesContext extends ExpressionContext {
		public Token op;
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public TerminalNode RegexMatches() { return getToken(QDLParserParser.RegexMatches, 0); }
		public RegexMatchesContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof QDLParserListener ) ((QDLParserListener)listener).enterRegexMatches(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof QDLParserListener ) ((QDLParserListener)listener).exitRegexMatches(this);
		}
	}
	public static class IntIntervalContext extends ExpressionContext {
		public IIntervalContext iInterval() {
			return getRuleContext(IIntervalContext.class,0);
		}
		public IntIntervalContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof QDLParserListener ) ((QDLParserListener)listener).enterIntInterval(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof QDLParserListener ) ((QDLParserListener)listener).exitIntInterval(this);
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
	public static class AltIFExpressionContext extends ExpressionContext {
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public TerminalNode Colon() { return getToken(QDLParserParser.Colon, 0); }
		public AltIFExpressionContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof QDLParserListener ) ((QDLParserListener)listener).enterAltIFExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof QDLParserListener ) ((QDLParserListener)listener).exitAltIFExpression(this);
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
		public TerminalNode Exponentiation() { return getToken(QDLParserParser.Exponentiation, 0); }
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
	public static class RestrictionContext extends ExpressionContext {
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public List<TerminalNode> Backslash() { return getTokens(QDLParserParser.Backslash); }
		public TerminalNode Backslash(int i) {
			return getToken(QDLParserParser.Backslash, i);
		}
		public RestrictionContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof QDLParserListener ) ((QDLParserListener)listener).enterRestriction(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof QDLParserListener ) ((QDLParserListener)listener).exitRestriction(this);
		}
	}
	public static class Semi_for_empty_expressionsContext extends ExpressionContext {
		public TerminalNode SemiColon() { return getToken(QDLParserParser.SemiColon, 0); }
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
	public static class StemVarContext extends ExpressionContext {
		public StemVariableContext stemVariable() {
			return getRuleContext(StemVariableContext.class,0);
		}
		public StemVarContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof QDLParserListener ) ((QDLParserListener)listener).enterStemVar(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof QDLParserListener ) ((QDLParserListener)listener).exitStemVar(this);
		}
	}
	public static class DotOpContext extends ExpressionContext {
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public List<TerminalNode> StemDot() { return getTokens(QDLParserParser.StemDot); }
		public TerminalNode StemDot(int i) {
			return getToken(QDLParserParser.StemDot, i);
		}
		public DotOpContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof QDLParserListener ) ((QDLParserListener)listener).enterDotOp(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof QDLParserListener ) ((QDLParserListener)listener).exitDotOp(this);
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
		int _startState = 58;
		enterRecursionRule(_localctx, 58, RULE_expression, _p);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(350);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,30,_ctx) ) {
			case 1:
				{
				_localctx = new FunctionsContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;

				setState(311);
				function();
				}
				break;
			case 2:
				{
				_localctx = new LambdaDefContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(321);
				_errHandler.sync(this);
				switch (_input.LA(1)) {
				case FuncStart:
					{
					setState(312);
					function();
					}
					break;
				case T__3:
					{
					setState(313);
					match(T__3);
					setState(317);
					_errHandler.sync(this);
					_la = _input.LA(1);
					while (((((_la - 1)) & ~0x3f) == 0 && ((1L << (_la - 1)) & ((1L << (T__0 - 1)) | (1L << (T__3 - 1)) | (1L << (ConstantKeywords - 1)) | (1L << (Null - 1)) | (1L << (Integer - 1)) | (1L << (Decimal - 1)) | (1L << (SCIENTIFIC_NUMBER - 1)) | (1L << (Bool - 1)) | (1L << (STRING - 1)) | (1L << (LeftBracket - 1)) | (1L << (SemiColon - 1)) | (1L << (LDoubleBracket - 1)) | (1L << (PlusPlus - 1)) | (1L << (Plus - 1)) | (1L << (MinusMinus - 1)) | (1L << (Minus - 1)) | (1L << (LogicalNot - 1)) | (1L << (Identifier - 1)) | (1L << (FuncStart - 1)) | (1L << (F_REF - 1)))) != 0)) {
						{
						{
						setState(314);
						f_args();
						}
						}
						setState(319);
						_errHandler.sync(this);
						_la = _input.LA(1);
					}
					setState(320);
					match(T__2);
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				setState(323);
				match(LambdaConnector);
				setState(326);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,29,_ctx) ) {
				case 1:
					{
					setState(324);
					expression(0);
					}
					break;
				case 2:
					{
					setState(325);
					expressionBlock();
					}
					break;
				}
				}
				break;
			case 3:
				{
				_localctx = new StemVarContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(328);
				stemVariable();
				}
				break;
			case 4:
				{
				_localctx = new StemLiContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(329);
				stemList();
				}
				break;
			case 5:
				{
				_localctx = new RealIntervalContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(330);
				rInterval();
				}
				break;
			case 6:
				{
				_localctx = new IntIntervalContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(331);
				iInterval();
				}
				break;
			case 7:
				{
				_localctx = new PrefixContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(332);
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
				setState(333);
				expression(23);
				}
				break;
			case 8:
				{
				_localctx = new UnaryMinusExpressionContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(334);
				_la = _input.LA(1);
				if ( !(_la==Plus || _la==Minus) ) {
				_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(335);
				expression(20);
				}
				break;
			case 9:
				{
				_localctx = new NotExpressionContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(336);
				match(LogicalNot);
				setState(337);
				expression(13);
				}
				break;
			case 10:
				{
				_localctx = new AssociationContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(338);
				match(T__3);
				setState(339);
				expression(0);
				setState(340);
				match(T__2);
				}
				break;
			case 11:
				{
				_localctx = new StringsContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(342);
				match(STRING);
				}
				break;
			case 12:
				{
				_localctx = new IntegersContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(343);
				integer();
				}
				break;
			case 13:
				{
				_localctx = new NumbersContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(344);
				number();
				}
				break;
			case 14:
				{
				_localctx = new VariablesContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(345);
				variable();
				}
				break;
			case 15:
				{
				_localctx = new KeywordsContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(346);
				keyword();
				}
				break;
			case 16:
				{
				_localctx = new LogicalContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(347);
				match(Bool);
				}
				break;
			case 17:
				{
				_localctx = new NullContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(348);
				match(Null);
				}
				break;
			case 18:
				{
				_localctx = new Semi_for_empty_expressionsContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(349);
				match(SemiColon);
				}
				break;
			}
			_ctx.stop = _input.LT(-1);
			setState(408);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,34,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					setState(406);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,33,_ctx) ) {
					case 1:
						{
						_localctx = new DotOpContext(new ExpressionContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(352);
						if (!(precpred(_ctx, 32))) throw new FailedPredicateException(this, "precpred(_ctx, 32)");
						setState(354); 
						_errHandler.sync(this);
						_la = _input.LA(1);
						do {
							{
							{
							setState(353);
							match(StemDot);
							}
							}
							setState(356); 
							_errHandler.sync(this);
							_la = _input.LA(1);
						} while ( _la==StemDot );
						setState(358);
						expression(33);
						}
						break;
					case 2:
						{
						_localctx = new TildeExpressionContext(new ExpressionContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(359);
						if (!(precpred(_ctx, 25))) throw new FailedPredicateException(this, "precpred(_ctx, 25)");
						setState(360);
						_la = _input.LA(1);
						if ( !(_la==Tilde || _la==TildeRight) ) {
						_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(361);
						expression(26);
						}
						break;
					case 3:
						{
						_localctx = new PowerExpressionContext(new ExpressionContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(362);
						if (!(precpred(_ctx, 22))) throw new FailedPredicateException(this, "precpred(_ctx, 22)");
						setState(363);
						match(Exponentiation);
						setState(364);
						expression(23);
						}
						break;
					case 4:
						{
						_localctx = new MultiplyExpressionContext(new ExpressionContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(365);
						if (!(precpred(_ctx, 21))) throw new FailedPredicateException(this, "precpred(_ctx, 21)");
						setState(366);
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
						setState(367);
						expression(22);
						}
						break;
					case 5:
						{
						_localctx = new AddExpressionContext(new ExpressionContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(368);
						if (!(precpred(_ctx, 19))) throw new FailedPredicateException(this, "precpred(_ctx, 19)");
						setState(369);
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
						setState(370);
						expression(20);
						}
						break;
					case 6:
						{
						_localctx = new CompExpressionContext(new ExpressionContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(371);
						if (!(precpred(_ctx, 18))) throw new FailedPredicateException(this, "precpred(_ctx, 18)");
						setState(372);
						((CompExpressionContext)_localctx).op = _input.LT(1);
						_la = _input.LA(1);
						if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << LessThan) | (1L << GreaterThan) | (1L << LessEquals) | (1L << MoreEquals))) != 0)) ) {
							((CompExpressionContext)_localctx).op = (Token)_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(373);
						expression(19);
						}
						break;
					case 7:
						{
						_localctx = new EqExpressionContext(new ExpressionContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(374);
						if (!(precpred(_ctx, 17))) throw new FailedPredicateException(this, "precpred(_ctx, 17)");
						setState(375);
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
						setState(376);
						expression(18);
						}
						break;
					case 8:
						{
						_localctx = new RegexMatchesContext(new ExpressionContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(377);
						if (!(precpred(_ctx, 16))) throw new FailedPredicateException(this, "precpred(_ctx, 16)");
						setState(378);
						((RegexMatchesContext)_localctx).op = match(RegexMatches);
						setState(379);
						expression(17);
						}
						break;
					case 9:
						{
						_localctx = new AndExpressionContext(new ExpressionContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(380);
						if (!(precpred(_ctx, 15))) throw new FailedPredicateException(this, "precpred(_ctx, 15)");
						setState(381);
						match(And);
						setState(382);
						expression(16);
						}
						break;
					case 10:
						{
						_localctx = new OrExpressionContext(new ExpressionContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(383);
						if (!(precpred(_ctx, 14))) throw new FailedPredicateException(this, "precpred(_ctx, 14)");
						setState(384);
						match(Or);
						setState(385);
						expression(15);
						}
						break;
					case 11:
						{
						_localctx = new AltIFExpressionContext(new ExpressionContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(386);
						if (!(precpred(_ctx, 11))) throw new FailedPredicateException(this, "precpred(_ctx, 11)");
						setState(387);
						match(T__4);
						setState(388);
						expression(0);
						setState(389);
						match(Colon);
						setState(390);
						expression(12);
						}
						break;
					case 12:
						{
						_localctx = new RestrictionContext(new ExpressionContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(392);
						if (!(precpred(_ctx, 10))) throw new FailedPredicateException(this, "precpred(_ctx, 10)");
						setState(394); 
						_errHandler.sync(this);
						_la = _input.LA(1);
						do {
							{
							{
							setState(393);
							match(Backslash);
							}
							}
							setState(396); 
							_errHandler.sync(this);
							_la = _input.LA(1);
						} while ( _la==Backslash );
						setState(398);
						expression(11);
						}
						break;
					case 13:
						{
						_localctx = new AssignmentContext(new ExpressionContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(399);
						if (!(precpred(_ctx, 2))) throw new FailedPredicateException(this, "precpred(_ctx, 2)");
						setState(400);
						((AssignmentContext)_localctx).op = match(ASSIGN);
						setState(401);
						expression(3);
						}
						break;
					case 14:
						{
						_localctx = new DotOp2Context(new ExpressionContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(402);
						if (!(precpred(_ctx, 31))) throw new FailedPredicateException(this, "precpred(_ctx, 31)");
						setState(403);
						((DotOp2Context)_localctx).postfix = match(StemDot);
						}
						break;
					case 15:
						{
						_localctx = new PostfixContext(new ExpressionContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(404);
						if (!(precpred(_ctx, 24))) throw new FailedPredicateException(this, "precpred(_ctx, 24)");
						setState(405);
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
				setState(410);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,34,_ctx);
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

	public static class VariableContext extends ParserRuleContext {
		public TerminalNode Identifier() { return getToken(QDLParserParser.Identifier, 0); }
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
		enterRule(_localctx, 60, RULE_variable);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(411);
			match(Identifier);
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
		public TerminalNode Decimal() { return getToken(QDLParserParser.Decimal, 0); }
		public TerminalNode SCIENTIFIC_NUMBER() { return getToken(QDLParserParser.SCIENTIFIC_NUMBER, 0); }
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
		enterRule(_localctx, 62, RULE_number);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(413);
			_la = _input.LA(1);
			if ( !(_la==Decimal || _la==SCIENTIFIC_NUMBER) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
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

	public static class IntegerContext extends ParserRuleContext {
		public TerminalNode Integer() { return getToken(QDLParserParser.Integer, 0); }
		public IntegerContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_integer; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof QDLParserListener ) ((QDLParserListener)listener).enterInteger(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof QDLParserListener ) ((QDLParserListener)listener).exitInteger(this);
		}
	}

	public final IntegerContext integer() throws RecognitionException {
		IntegerContext _localctx = new IntegerContext(_ctx, getState());
		enterRule(_localctx, 64, RULE_integer);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(415);
			match(Integer);
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

	public static class KeywordContext extends ParserRuleContext {
		public TerminalNode ConstantKeywords() { return getToken(QDLParserParser.ConstantKeywords, 0); }
		public KeywordContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_keyword; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof QDLParserListener ) ((QDLParserListener)listener).enterKeyword(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof QDLParserListener ) ((QDLParserListener)listener).exitKeyword(this);
		}
	}

	public final KeywordContext keyword() throws RecognitionException {
		KeywordContext _localctx = new KeywordContext(_ctx, getState());
		enterRule(_localctx, 66, RULE_keyword);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(417);
			match(ConstantKeywords);
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
		case 29:
			return expression_sempred((ExpressionContext)_localctx, predIndex);
		}
		return true;
	}
	private boolean expression_sempred(ExpressionContext _localctx, int predIndex) {
		switch (predIndex) {
		case 0:
			return precpred(_ctx, 32);
		case 1:
			return precpred(_ctx, 25);
		case 2:
			return precpred(_ctx, 22);
		case 3:
			return precpred(_ctx, 21);
		case 4:
			return precpred(_ctx, 19);
		case 5:
			return precpred(_ctx, 18);
		case 6:
			return precpred(_ctx, 17);
		case 7:
			return precpred(_ctx, 16);
		case 8:
			return precpred(_ctx, 15);
		case 9:
			return precpred(_ctx, 14);
		case 10:
			return precpred(_ctx, 11);
		case 11:
			return precpred(_ctx, 10);
		case 12:
			return precpred(_ctx, 2);
		case 13:
			return precpred(_ctx, 31);
		case 14:
			return precpred(_ctx, 24);
		}
		return true;
	}

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\3F\u01a6\4\2\t\2\4"+
		"\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13\t"+
		"\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31\t\31"+
		"\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4 \t \4!"+
		"\t!\4\"\t\"\4#\t#\3\2\7\2H\n\2\f\2\16\2K\13\2\3\2\3\2\3\3\3\3\3\3\3\3"+
		"\3\3\3\3\5\3U\n\3\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\5\4`\n\4\3\5\3\5"+
		"\5\5d\n\5\3\6\3\6\3\6\5\6i\n\6\3\6\3\6\3\7\3\7\3\7\5\7p\n\7\3\7\3\7\3"+
		"\7\3\7\3\b\3\b\3\b\5\by\n\b\3\b\3\b\3\t\3\t\3\t\3\t\3\t\7\t\u0082\n\t"+
		"\f\t\16\t\u0085\13\t\3\t\3\t\3\n\3\n\3\n\3\n\3\n\5\n\u008e\n\n\3\n\3\n"+
		"\3\13\3\13\3\13\3\13\3\13\5\13\u0097\n\13\3\f\3\f\3\f\3\f\3\f\5\f\u009e"+
		"\n\f\3\f\3\f\5\f\u00a2\n\f\3\f\3\f\3\r\3\r\3\r\3\r\3\r\3\16\3\16\3\16"+
		"\3\16\3\16\3\16\3\16\3\16\3\17\3\17\3\17\3\17\5\17\u00b7\n\17\3\20\3\20"+
		"\3\20\3\20\7\20\u00bd\n\20\f\20\16\20\u00c0\13\20\3\20\3\20\3\21\3\21"+
		"\7\21\u00c6\n\21\f\21\16\21\u00c9\13\21\3\21\3\21\3\21\6\21\u00ce\n\21"+
		"\r\21\16\21\u00cf\3\21\3\21\3\22\3\22\3\22\3\22\3\22\3\22\6\22\u00da\n"+
		"\22\r\22\16\22\u00db\3\22\3\22\3\23\3\23\3\23\3\23\3\24\3\24\3\25\3\25"+
		"\3\25\3\25\3\25\3\25\5\25\u00ec\n\25\3\25\3\25\3\26\3\26\3\26\3\26\3\26"+
		"\3\26\3\26\3\26\3\27\3\27\3\27\3\27\7\27\u00fc\n\27\f\27\16\27\u00ff\13"+
		"\27\3\27\3\27\3\27\3\27\5\27\u0105\n\27\3\30\3\30\5\30\u0109\n\30\3\30"+
		"\3\30\3\30\3\31\3\31\3\31\3\31\7\31\u0112\n\31\f\31\16\31\u0115\13\31"+
		"\3\31\3\31\3\31\3\31\5\31\u011b\n\31\3\32\3\32\3\32\5\32\u0120\n\32\3"+
		"\33\3\33\7\33\u0124\n\33\f\33\16\33\u0127\13\33\3\33\3\33\3\34\3\34\5"+
		"\34\u012d\n\34\3\35\3\35\3\35\7\35\u0132\n\35\f\35\16\35\u0135\13\35\3"+
		"\36\3\36\3\37\3\37\3\37\3\37\3\37\7\37\u013e\n\37\f\37\16\37\u0141\13"+
		"\37\3\37\5\37\u0144\n\37\3\37\3\37\3\37\5\37\u0149\n\37\3\37\3\37\3\37"+
		"\3\37\3\37\3\37\3\37\3\37\3\37\3\37\3\37\3\37\3\37\3\37\3\37\3\37\3\37"+
		"\3\37\3\37\3\37\3\37\3\37\5\37\u0161\n\37\3\37\3\37\6\37\u0165\n\37\r"+
		"\37\16\37\u0166\3\37\3\37\3\37\3\37\3\37\3\37\3\37\3\37\3\37\3\37\3\37"+
		"\3\37\3\37\3\37\3\37\3\37\3\37\3\37\3\37\3\37\3\37\3\37\3\37\3\37\3\37"+
		"\3\37\3\37\3\37\3\37\3\37\3\37\3\37\3\37\3\37\3\37\3\37\6\37\u018d\n\37"+
		"\r\37\16\37\u018e\3\37\3\37\3\37\3\37\3\37\3\37\3\37\3\37\7\37\u0199\n"+
		"\37\f\37\16\37\u019c\13\37\3 \3 \3!\3!\3\"\3\"\3#\3#\3#\2\3<$\2\4\6\b"+
		"\n\f\16\20\22\24\26\30\32\34\36 \"$&(*,.\60\62\64\668:<>@BD\2\t\4\2(("+
		"**\4\2))++\4\2::==\4\2&\'99\4\2,-/\60\3\2\61\62\3\2\32\33\2\u01cb\2I\3"+
		"\2\2\2\4T\3\2\2\2\6_\3\2\2\2\bc\3\2\2\2\ne\3\2\2\2\fl\3\2\2\2\16u\3\2"+
		"\2\2\20|\3\2\2\2\22\u0088\3\2\2\2\24\u0096\3\2\2\2\26\u0098\3\2\2\2\30"+
		"\u00a5\3\2\2\2\32\u00aa\3\2\2\2\34\u00b2\3\2\2\2\36\u00b8\3\2\2\2 \u00c3"+
		"\3\2\2\2\"\u00d3\3\2\2\2$\u00df\3\2\2\2&\u00e3\3\2\2\2(\u00e5\3\2\2\2"+
		"*\u00ef\3\2\2\2,\u0104\3\2\2\2.\u0108\3\2\2\2\60\u011a\3\2\2\2\62\u011f"+
		"\3\2\2\2\64\u0121\3\2\2\2\66\u012c\3\2\2\28\u012e\3\2\2\2:\u0136\3\2\2"+
		"\2<\u0160\3\2\2\2>\u019d\3\2\2\2@\u019f\3\2\2\2B\u01a1\3\2\2\2D\u01a3"+
		"\3\2\2\2FH\5\4\3\2GF\3\2\2\2HK\3\2\2\2IG\3\2\2\2IJ\3\2\2\2JL\3\2\2\2K"+
		"I\3\2\2\2LM\7\2\2\3M\3\3\2\2\2NO\5\6\4\2OP\7\"\2\2PU\3\2\2\2QR\5\26\f"+
		"\2RS\7\"\2\2SU\3\2\2\2TN\3\2\2\2TQ\3\2\2\2U\5\3\2\2\2V`\5\22\n\2W`\5\b"+
		"\5\2X`\5\16\b\2Y`\5\20\t\2Z`\5<\37\2[`\5\30\r\2\\`\5\24\13\2]`\5\32\16"+
		"\2^`\5\34\17\2_V\3\2\2\2_W\3\2\2\2_X\3\2\2\2_Y\3\2\2\2_Z\3\2\2\2_[\3\2"+
		"\2\2_\\\3\2\2\2_]\3\2\2\2_^\3\2\2\2`\7\3\2\2\2ad\5\n\6\2bd\5\f\7\2ca\3"+
		"\2\2\2cb\3\2\2\2d\t\3\2\2\2ef\7\22\2\2fh\5$\23\2gi\7\26\2\2hg\3\2\2\2"+
		"hi\3\2\2\2ij\3\2\2\2jk\5\36\20\2k\13\3\2\2\2lm\7\22\2\2mo\5$\23\2np\7"+
		"\26\2\2on\3\2\2\2op\3\2\2\2pq\3\2\2\2qr\5\36\20\2rs\7\21\2\2st\5\36\20"+
		"\2t\r\3\2\2\2uv\7\30\2\2vx\5$\23\2wy\7\20\2\2xw\3\2\2\2xy\3\2\2\2yz\3"+
		"\2\2\2z{\5\36\20\2{\17\3\2\2\2|}\7\25\2\2}\u0083\7\36\2\2~\177\5\n\6\2"+
		"\177\u0080\7\"\2\2\u0080\u0082\3\2\2\2\u0081~\3\2\2\2\u0082\u0085\3\2"+
		"\2\2\u0083\u0081\3\2\2\2\u0083\u0084\3\2\2\2\u0084\u0086\3\2\2\2\u0085"+
		"\u0083\3\2\2\2\u0086\u0087\7\37\2\2\u0087\21\3\2\2\2\u0088\u0089\7\17"+
		"\2\2\u0089\u008a\7\36\2\2\u008a\u008b\5\64\33\2\u008b\u008d\7\37\2\2\u008c"+
		"\u008e\7\r\2\2\u008d\u008c\3\2\2\2\u008d\u008e\3\2\2\2\u008e\u008f\3\2"+
		"\2\2\u008f\u0090\5 \21\2\u0090\23\3\2\2\2\u0091\u0092\5\64\33\2\u0092"+
		"\u0093\7%\2\2\u0093\u0094\5\6\4\2\u0094\u0097\3\2\2\2\u0095\u0097\5\36"+
		"\20\2\u0096\u0091\3\2\2\2\u0096\u0095\3\2\2\2\u0097\25\3\2\2\2\u0098\u0099"+
		"\7\23\2\2\u0099\u009a\7\36\2\2\u009a\u009d\7\35\2\2\u009b\u009c\7 \2\2"+
		"\u009c\u009e\7\35\2\2\u009d\u009b\3\2\2\2\u009d\u009e\3\2\2\2\u009e\u009f"+
		"\3\2\2\2\u009f\u00a1\7\37\2\2\u00a0\u00a2\7\r\2\2\u00a1\u00a0\3\2\2\2"+
		"\u00a1\u00a2\3\2\2\2\u00a2\u00a3\3\2\2\2\u00a3\u00a4\5 \21\2\u00a4\27"+
		"\3\2\2\2\u00a5\u00a6\7\27\2\2\u00a6\u00a7\5\36\20\2\u00a7\u00a8\7\16\2"+
		"\2\u00a8\u00a9\5\36\20\2\u00a9\31\3\2\2\2\u00aa\u00ab\7\t\2\2\u00ab\u00ac"+
		"\7\36\2\2\u00ac\u00ad\5<\37\2\u00ad\u00ae\7\37\2\2\u00ae\u00af\7\36\2"+
		"\2\u00af\u00b0\5<\37\2\u00b0\u00b1\7\37\2\2\u00b1\33\3\2\2\2\u00b2\u00b3"+
		"\7\n\2\2\u00b3\u00b6\5<\37\2\u00b4\u00b5\7!\2\2\u00b5\u00b7\5<\37\2\u00b6"+
		"\u00b4\3\2\2\2\u00b6\u00b7\3\2\2\2\u00b7\35\3\2\2\2\u00b8\u00be\7\36\2"+
		"\2\u00b9\u00ba\5\6\4\2\u00ba\u00bb\7\"\2\2\u00bb\u00bd\3\2\2\2\u00bc\u00b9"+
		"\3\2\2\2\u00bd\u00c0\3\2\2\2\u00be\u00bc\3\2\2\2\u00be\u00bf\3\2\2\2\u00bf"+
		"\u00c1\3\2\2\2\u00c0\u00be\3\2\2\2\u00c1\u00c2\7\37\2\2\u00c2\37\3\2\2"+
		"\2\u00c3\u00c7\7\36\2\2\u00c4\u00c6\5&\24\2\u00c5\u00c4\3\2\2\2\u00c6"+
		"\u00c9\3\2\2\2\u00c7\u00c5\3\2\2\2\u00c7\u00c8\3\2\2\2\u00c8\u00cd\3\2"+
		"\2\2\u00c9\u00c7\3\2\2\2\u00ca\u00cb\5\6\4\2\u00cb\u00cc\7\"\2\2\u00cc"+
		"\u00ce\3\2\2\2\u00cd\u00ca\3\2\2\2\u00ce\u00cf\3\2\2\2\u00cf\u00cd\3\2"+
		"\2\2\u00cf\u00d0\3\2\2\2\u00d0\u00d1\3\2\2\2\u00d1\u00d2\7\37\2\2\u00d2"+
		"!\3\2\2\2\u00d3\u00d4\7\36\2\2\u00d4\u00d5\5<\37\2\u00d5\u00d9\7\"\2\2"+
		"\u00d6\u00d7\5<\37\2\u00d7\u00d8\7\"\2\2\u00d8\u00da\3\2\2\2\u00d9\u00d6"+
		"\3\2\2\2\u00da\u00db\3\2\2\2\u00db\u00d9\3\2\2\2\u00db\u00dc\3\2\2\2\u00dc"+
		"\u00dd\3\2\2\2\u00dd\u00de\7\37\2\2\u00de#\3\2\2\2\u00df\u00e0\7\36\2"+
		"\2\u00e0\u00e1\5<\37\2\u00e1\u00e2\7\37\2\2\u00e2%\3\2\2\2\u00e3\u00e4"+
		"\7C\2\2\u00e4\'\3\2\2\2\u00e5\u00e6\7\36\2\2\u00e6\u00e7\5<\37\2\u00e7"+
		"\u00e8\7\"\2\2\u00e8\u00eb\5<\37\2\u00e9\u00ea\7\"\2\2\u00ea\u00ec\5<"+
		"\37\2\u00eb\u00e9\3\2\2\2\u00eb\u00ec\3\2\2\2\u00ec\u00ed\3\2\2\2\u00ed"+
		"\u00ee\7\37\2\2\u00ee)\3\2\2\2\u00ef\u00f0\7#\2\2\u00f0\u00f1\5<\37\2"+
		"\u00f1\u00f2\7\"\2\2\u00f2\u00f3\5<\37\2\u00f3\u00f4\7\"\2\2\u00f4\u00f5"+
		"\5<\37\2\u00f5\u00f6\7$\2\2\u00f6+\3\2\2\2\u00f7\u00f8\7\3\2\2\u00f8\u00fd"+
		"\5.\30\2\u00f9\u00fa\7 \2\2\u00fa\u00fc\5.\30\2\u00fb\u00f9\3\2\2\2\u00fc"+
		"\u00ff\3\2\2\2\u00fd\u00fb\3\2\2\2\u00fd\u00fe\3\2\2\2\u00fe\u0100\3\2"+
		"\2\2\u00ff\u00fd\3\2\2\2\u0100\u0101\7\4\2\2\u0101\u0105\3\2\2\2\u0102"+
		"\u0103\7\3\2\2\u0103\u0105\7\4\2\2\u0104\u00f7\3\2\2\2\u0104\u0102\3\2"+
		"\2\2\u0105-\3\2\2\2\u0106\u0109\7&\2\2\u0107\u0109\5<\37\2\u0108\u0106"+
		"\3\2\2\2\u0108\u0107\3\2\2\2\u0109\u010a\3\2\2\2\u010a\u010b\7!\2\2\u010b"+
		"\u010c\5\62\32\2\u010c/\3\2\2\2\u010d\u010e\7\36\2\2\u010e\u0113\5\62"+
		"\32\2\u010f\u0110\7 \2\2\u0110\u0112\5\62\32\2\u0111\u010f\3\2\2\2\u0112"+
		"\u0115\3\2\2\2\u0113\u0111\3\2\2\2\u0113\u0114\3\2\2\2\u0114\u0116\3\2"+
		"\2\2\u0115\u0113\3\2\2\2\u0116\u0117\7\37\2\2\u0117\u011b\3\2\2\2\u0118"+
		"\u0119\7\36\2\2\u0119\u011b\7\37\2\2\u011a\u010d\3\2\2\2\u011a\u0118\3"+
		"\2\2\2\u011b\61\3\2\2\2\u011c\u0120\5<\37\2\u011d\u0120\5,\27\2\u011e"+
		"\u0120\5\60\31\2\u011f\u011c\3\2\2\2\u011f\u011d\3\2\2\2\u011f\u011e\3"+
		"\2\2\2\u0120\63\3\2\2\2\u0121\u0125\7A\2\2\u0122\u0124\58\35\2\u0123\u0122"+
		"\3\2\2\2\u0124\u0127\3\2\2\2\u0125\u0123\3\2\2\2\u0125\u0126\3\2\2\2\u0126"+
		"\u0128\3\2\2\2\u0127\u0125\3\2\2\2\u0128\u0129\7\5\2\2\u0129\65\3\2\2"+
		"\2\u012a\u012d\5\62\32\2\u012b\u012d\5:\36\2\u012c\u012a\3\2\2\2\u012c"+
		"\u012b\3\2\2\2\u012d\67\3\2\2\2\u012e\u0133\5\66\34\2\u012f\u0130\7 \2"+
		"\2\u0130\u0132\5\66\34\2\u0131\u012f\3\2\2\2\u0132\u0135\3\2\2\2\u0133"+
		"\u0131\3\2\2\2\u0133\u0134\3\2\2\2\u01349\3\2\2\2\u0135\u0133\3\2\2\2"+
		"\u0136\u0137\7B\2\2\u0137;\3\2\2\2\u0138\u0139\b\37\1\2\u0139\u0161\5"+
		"\64\33\2\u013a\u0144\5\64\33\2\u013b\u013f\7\6\2\2\u013c\u013e\58\35\2"+
		"\u013d\u013c\3\2\2\2\u013e\u0141\3\2\2\2\u013f\u013d\3\2\2\2\u013f\u0140"+
		"\3\2\2\2\u0140\u0142\3\2\2\2\u0141\u013f\3\2\2\2\u0142\u0144\7\5\2\2\u0143"+
		"\u013a\3\2\2\2\u0143\u013b\3\2\2\2\u0144\u0145\3\2\2\2\u0145\u0148\7%"+
		"\2\2\u0146\u0149\5<\37\2\u0147\u0149\5\"\22\2\u0148\u0146\3\2\2\2\u0148"+
		"\u0147\3\2\2\2\u0149\u0161\3\2\2\2\u014a\u0161\5,\27\2\u014b\u0161\5\60"+
		"\31\2\u014c\u0161\5*\26\2\u014d\u0161\5(\25\2\u014e\u014f\t\2\2\2\u014f"+
		"\u0161\5<\37\31\u0150\u0151\t\3\2\2\u0151\u0161\5<\37\26\u0152\u0153\7"+
		"\64\2\2\u0153\u0161\5<\37\17\u0154\u0155\7\6\2\2\u0155\u0156\5<\37\2\u0156"+
		"\u0157\7\5\2\2\u0157\u0161\3\2\2\2\u0158\u0161\7\35\2\2\u0159\u0161\5"+
		"B\"\2\u015a\u0161\5@!\2\u015b\u0161\5> \2\u015c\u0161\5D#\2\u015d\u0161"+
		"\7\34\2\2\u015e\u0161\7\24\2\2\u015f\u0161\7\"\2\2\u0160\u0138\3\2\2\2"+
		"\u0160\u0143\3\2\2\2\u0160\u014a\3\2\2\2\u0160\u014b\3\2\2\2\u0160\u014c"+
		"\3\2\2\2\u0160\u014d\3\2\2\2\u0160\u014e\3\2\2\2\u0160\u0150\3\2\2\2\u0160"+
		"\u0152\3\2\2\2\u0160\u0154\3\2\2\2\u0160\u0158\3\2\2\2\u0160\u0159\3\2"+
		"\2\2\u0160\u015a\3\2\2\2\u0160\u015b\3\2\2\2\u0160\u015c\3\2\2\2\u0160"+
		"\u015d\3\2\2\2\u0160\u015e\3\2\2\2\u0160\u015f\3\2\2\2\u0161\u019a\3\2"+
		"\2\2\u0162\u0164\f\"\2\2\u0163\u0165\7>\2\2\u0164\u0163\3\2\2\2\u0165"+
		"\u0166\3\2\2\2\u0166\u0164\3\2\2\2\u0166\u0167\3\2\2\2\u0167\u0168\3\2"+
		"\2\2\u0168\u0199\5<\37#\u0169\u016a\f\33\2\2\u016a\u016b\t\4\2\2\u016b"+
		"\u0199\5<\37\34\u016c\u016d\f\30\2\2\u016d\u016e\7\65\2\2\u016e\u0199"+
		"\5<\37\31\u016f\u0170\f\27\2\2\u0170\u0171\t\5\2\2\u0171\u0199\5<\37\30"+
		"\u0172\u0173\f\25\2\2\u0173\u0174\t\3\2\2\u0174\u0199\5<\37\26\u0175\u0176"+
		"\f\24\2\2\u0176\u0177\t\6\2\2\u0177\u0199\5<\37\25\u0178\u0179\f\23\2"+
		"\2\u0179\u017a\t\7\2\2\u017a\u0199\5<\37\24\u017b\u017c\f\22\2\2\u017c"+
		"\u017d\7\63\2\2\u017d\u0199\5<\37\23\u017e\u017f\f\21\2\2\u017f\u0180"+
		"\7\66\2\2\u0180\u0199\5<\37\22\u0181\u0182\f\20\2\2\u0182\u0183\7\67\2"+
		"\2\u0183\u0199\5<\37\21\u0184\u0185\f\r\2\2\u0185\u0186\7\7\2\2\u0186"+
		"\u0187\5<\37\2\u0187\u0188\7!\2\2\u0188\u0189\5<\37\16\u0189\u0199\3\2"+
		"\2\2\u018a\u018c\f\f\2\2\u018b\u018d\7;\2\2\u018c\u018b\3\2\2\2\u018d"+
		"\u018e\3\2\2\2\u018e\u018c\3\2\2\2\u018e\u018f\3\2\2\2\u018f\u0190\3\2"+
		"\2\2\u0190\u0199\5<\37\r\u0191\u0192\f\4\2\2\u0192\u0193\7?\2\2\u0193"+
		"\u0199\5<\37\5\u0194\u0195\f!\2\2\u0195\u0199\7>\2\2\u0196\u0197\f\32"+
		"\2\2\u0197\u0199\t\2\2\2\u0198\u0162\3\2\2\2\u0198\u0169\3\2\2\2\u0198"+
		"\u016c\3\2\2\2\u0198\u016f\3\2\2\2\u0198\u0172\3\2\2\2\u0198\u0175\3\2"+
		"\2\2\u0198\u0178\3\2\2\2\u0198\u017b\3\2\2\2\u0198\u017e\3\2\2\2\u0198"+
		"\u0181\3\2\2\2\u0198\u0184\3\2\2\2\u0198\u018a\3\2\2\2\u0198\u0191\3\2"+
		"\2\2\u0198\u0194\3\2\2\2\u0198\u0196\3\2\2\2\u0199\u019c\3\2\2\2\u019a"+
		"\u0198\3\2\2\2\u019a\u019b\3\2\2\2\u019b=\3\2\2\2\u019c\u019a\3\2\2\2"+
		"\u019d\u019e\7@\2\2\u019e?\3\2\2\2\u019f\u01a0\t\b\2\2\u01a0A\3\2\2\2"+
		"\u01a1\u01a2\7\31\2\2\u01a2C\3\2\2\2\u01a3\u01a4\7\b\2\2\u01a4E\3\2\2"+
		"\2%IT_chox\u0083\u008d\u0096\u009d\u00a1\u00b6\u00be\u00c7\u00cf\u00db"+
		"\u00eb\u00fd\u0104\u0108\u0113\u011a\u011f\u0125\u012c\u0133\u013f\u0143"+
		"\u0148\u0160\u0166\u018e\u0198\u019a";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}