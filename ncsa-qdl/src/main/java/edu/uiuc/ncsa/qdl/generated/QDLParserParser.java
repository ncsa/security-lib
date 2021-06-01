// Generated from QDLParser.g4 by ANTLR 4.9.1
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
	static { RuntimeMetaData.checkVersion("4.9.1", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, T__2=3, T__3=4, T__4=5, T__5=6, T__6=7, ConstantKeywords=8, 
		ASSERT=9, BOOL_FALSE=10, BOOL_TRUE=11, BODY=12, CATCH=13, DEFINE=14, DO=15, 
		ELSE=16, IF=17, MODULE=18, Null=19, SWITCH=20, THEN=21, TRY=22, WHILE=23, 
		Integer=24, Decimal=25, SCIENTIFIC_NUMBER=26, Bool=27, STRING=28, LeftBracket=29, 
		RightBracket=30, Comma=31, Colon=32, LDoubleBracket=33, RDoubleBracket=34, 
		LambdaConnector=35, Times=36, Divide=37, PlusPlus=38, Plus=39, MinusMinus=40, 
		Minus=41, LessThan=42, GreaterThan=43, SingleEqual=44, LessEquals=45, 
		MoreEquals=46, Equals=47, NotEquals=48, RegexMatches=49, LogicalNot=50, 
		Exponentiation=51, And=52, Or=53, Backtick=54, Percent=55, Tilde=56, Backslash=57, 
		Stile=58, TildeRight=59, ASSIGN=60, Identifier=61, FuncStart=62, F_REF=63, 
		FDOC=64, WS=65, COMMENT=66, LINE_COMMENT=67;
	public static final int
		RULE_elements = 0, RULE_element = 1, RULE_statement = 2, RULE_assignment = 3, 
		RULE_conditionalStatement = 4, RULE_ifStatement = 5, RULE_ifElseStatement = 6, 
		RULE_loopStatement = 7, RULE_switchStatement = 8, RULE_defineStatement = 9, 
		RULE_lambdaStatement = 10, RULE_moduleStatement = 11, RULE_tryCatchStatement = 12, 
		RULE_statementBlock = 13, RULE_docStatementBlock = 14, RULE_expressionBlock = 15, 
		RULE_conditionalBlock = 16, RULE_fdoc = 17, RULE_iInterval = 18, RULE_rInterval = 19, 
		RULE_stemVariable = 20, RULE_stemEntry = 21, RULE_stemList = 22, RULE_stemValue = 23, 
		RULE_function = 24, RULE_f_arg = 25, RULE_f_args = 26, RULE_f_ref = 27, 
		RULE_expression = 28, RULE_variable = 29, RULE_number = 30, RULE_integer = 31, 
		RULE_keyword = 32;
	private static String[] makeRuleNames() {
		return new String[] {
			"elements", "element", "statement", "assignment", "conditionalStatement", 
			"ifStatement", "ifElseStatement", "loopStatement", "switchStatement", 
			"defineStatement", "lambdaStatement", "moduleStatement", "tryCatchStatement", 
			"statementBlock", "docStatementBlock", "expressionBlock", "conditionalBlock", 
			"fdoc", "iInterval", "rInterval", "stemVariable", "stemEntry", "stemList", 
			"stemValue", "function", "f_arg", "f_args", "f_ref", "expression", "variable", 
			"number", "integer", "keyword"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "';'", "'{'", "'}'", "')'", "'.'", "'('", "'?'", null, null, null, 
			null, "'body'", "'catch'", "'define'", "'do'", "'else'", "'if'", "'module'", 
			null, "'switch'", "'then'", "'try'", "'while'", null, null, null, null, 
			null, "'['", "']'", "','", "':'", null, null, null, null, null, "'++'", 
			"'+'", "'--'", "'-'", "'<'", "'>'", "'='", null, null, null, null, null, 
			null, "'^'", null, null, "'`'", "'%'", "'~'", "'\\'", "'|'", "'~|'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, null, null, null, null, null, null, null, "ConstantKeywords", "ASSERT", 
			"BOOL_FALSE", "BOOL_TRUE", "BODY", "CATCH", "DEFINE", "DO", "ELSE", "IF", 
			"MODULE", "Null", "SWITCH", "THEN", "TRY", "WHILE", "Integer", "Decimal", 
			"SCIENTIFIC_NUMBER", "Bool", "STRING", "LeftBracket", "RightBracket", 
			"Comma", "Colon", "LDoubleBracket", "RDoubleBracket", "LambdaConnector", 
			"Times", "Divide", "PlusPlus", "Plus", "MinusMinus", "Minus", "LessThan", 
			"GreaterThan", "SingleEqual", "LessEquals", "MoreEquals", "Equals", "NotEquals", 
			"RegexMatches", "LogicalNot", "Exponentiation", "And", "Or", "Backtick", 
			"Percent", "Tilde", "Backslash", "Stile", "TildeRight", "ASSIGN", "Identifier", 
			"FuncStart", "F_REF", "FDOC", "WS", "COMMENT", "LINE_COMMENT"
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
			setState(69);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__0) | (1L << T__1) | (1L << T__5) | (1L << ConstantKeywords) | (1L << DEFINE) | (1L << IF) | (1L << MODULE) | (1L << Null) | (1L << SWITCH) | (1L << TRY) | (1L << WHILE) | (1L << Integer) | (1L << Decimal) | (1L << SCIENTIFIC_NUMBER) | (1L << Bool) | (1L << STRING) | (1L << LeftBracket) | (1L << LDoubleBracket) | (1L << PlusPlus) | (1L << Plus) | (1L << MinusMinus) | (1L << Minus) | (1L << LogicalNot) | (1L << Identifier) | (1L << FuncStart))) != 0)) {
				{
				{
				setState(66);
				element();
				}
				}
				setState(71);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(72);
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
			setState(80);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__0:
			case T__1:
			case T__5:
			case ConstantKeywords:
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
				setState(74);
				statement();
				setState(75);
				match(T__0);
				}
				}
				break;
			case MODULE:
				enterOuterAlt(_localctx, 2);
				{
				{
				setState(77);
				moduleStatement();
				setState(78);
				match(T__0);
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
		public AssignmentContext assignment() {
			return getRuleContext(AssignmentContext.class,0);
		}
		public TryCatchStatementContext tryCatchStatement() {
			return getRuleContext(TryCatchStatementContext.class,0);
		}
		public LambdaStatementContext lambdaStatement() {
			return getRuleContext(LambdaStatementContext.class,0);
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
			setState(90);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,2,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(82);
				defineStatement();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(83);
				conditionalStatement();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(84);
				loopStatement();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(85);
				switchStatement();
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(86);
				expression(0);
				}
				break;
			case 6:
				enterOuterAlt(_localctx, 6);
				{
				setState(87);
				assignment();
				}
				break;
			case 7:
				enterOuterAlt(_localctx, 7);
				{
				setState(88);
				tryCatchStatement();
				}
				break;
			case 8:
				enterOuterAlt(_localctx, 8);
				{
				setState(89);
				lambdaStatement();
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

	public static class AssignmentContext extends ParserRuleContext {
		public Token op;
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
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
		enterRule(_localctx, 6, RULE_assignment);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(95); 
			_errHandler.sync(this);
			_alt = 1;
			do {
				switch (_alt) {
				case 1:
					{
					{
					setState(92);
					expression(0);
					setState(93);
					((AssignmentContext)_localctx).op = match(ASSIGN);
					}
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				setState(97); 
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,3,_ctx);
			} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
			setState(99);
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
		enterRule(_localctx, 8, RULE_conditionalStatement);
		try {
			setState(103);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,4,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(101);
				ifStatement();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(102);
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
		enterRule(_localctx, 10, RULE_ifStatement);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(105);
			match(IF);
			setState(106);
			conditionalBlock();
			setState(108);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==THEN) {
				{
				setState(107);
				match(THEN);
				}
			}

			setState(110);
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
		enterRule(_localctx, 12, RULE_ifElseStatement);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(112);
			match(IF);
			setState(113);
			conditionalBlock();
			setState(115);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==THEN) {
				{
				setState(114);
				match(THEN);
				}
			}

			setState(117);
			statementBlock();
			setState(118);
			match(ELSE);
			setState(119);
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
		enterRule(_localctx, 14, RULE_loopStatement);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(121);
			match(WHILE);
			setState(122);
			conditionalBlock();
			setState(124);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==DO) {
				{
				setState(123);
				match(DO);
				}
			}

			setState(126);
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
		enterRule(_localctx, 16, RULE_switchStatement);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(128);
			match(SWITCH);
			setState(129);
			match(LeftBracket);
			setState(135);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==IF) {
				{
				{
				setState(130);
				ifStatement();
				setState(131);
				match(T__0);
				}
				}
				setState(137);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(138);
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
		enterRule(_localctx, 18, RULE_defineStatement);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(140);
			match(DEFINE);
			setState(141);
			match(LeftBracket);
			setState(142);
			function();
			setState(143);
			match(RightBracket);
			setState(145);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==BODY) {
				{
				setState(144);
				match(BODY);
				}
			}

			setState(147);
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
		enterRule(_localctx, 20, RULE_lambdaStatement);
		try {
			setState(154);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case FuncStart:
				enterOuterAlt(_localctx, 1);
				{
				setState(149);
				function();
				setState(150);
				match(LambdaConnector);
				{
				setState(151);
				statement();
				}
				}
				break;
			case LeftBracket:
				enterOuterAlt(_localctx, 2);
				{
				setState(153);
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
		enterRule(_localctx, 22, RULE_moduleStatement);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(156);
			match(MODULE);
			setState(157);
			match(LeftBracket);
			setState(158);
			match(STRING);
			setState(161);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==Comma) {
				{
				setState(159);
				match(Comma);
				setState(160);
				match(STRING);
				}
			}

			setState(163);
			match(RightBracket);
			setState(165);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==BODY) {
				{
				setState(164);
				match(BODY);
				}
			}

			setState(167);
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
		enterRule(_localctx, 24, RULE_tryCatchStatement);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(169);
			match(TRY);
			setState(170);
			statementBlock();
			setState(171);
			match(CATCH);
			setState(172);
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

	public static class StatementBlockContext extends ParserRuleContext {
		public TerminalNode LeftBracket() { return getToken(QDLParserParser.LeftBracket, 0); }
		public TerminalNode RightBracket() { return getToken(QDLParserParser.RightBracket, 0); }
		public List<StatementContext> statement() {
			return getRuleContexts(StatementContext.class);
		}
		public StatementContext statement(int i) {
			return getRuleContext(StatementContext.class,i);
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
		enterRule(_localctx, 26, RULE_statementBlock);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(174);
			match(LeftBracket);
			setState(180);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__0) | (1L << T__1) | (1L << T__5) | (1L << ConstantKeywords) | (1L << DEFINE) | (1L << IF) | (1L << Null) | (1L << SWITCH) | (1L << TRY) | (1L << WHILE) | (1L << Integer) | (1L << Decimal) | (1L << SCIENTIFIC_NUMBER) | (1L << Bool) | (1L << STRING) | (1L << LeftBracket) | (1L << LDoubleBracket) | (1L << PlusPlus) | (1L << Plus) | (1L << MinusMinus) | (1L << Minus) | (1L << LogicalNot) | (1L << Identifier) | (1L << FuncStart))) != 0)) {
				{
				{
				setState(175);
				statement();
				setState(176);
				match(T__0);
				}
				}
				setState(182);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(183);
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
		enterRule(_localctx, 28, RULE_docStatementBlock);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(185);
			match(LeftBracket);
			setState(189);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==FDOC) {
				{
				{
				setState(186);
				fdoc();
				}
				}
				setState(191);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(195); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(192);
				statement();
				setState(193);
				match(T__0);
				}
				}
				setState(197); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( (((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__0) | (1L << T__1) | (1L << T__5) | (1L << ConstantKeywords) | (1L << DEFINE) | (1L << IF) | (1L << Null) | (1L << SWITCH) | (1L << TRY) | (1L << WHILE) | (1L << Integer) | (1L << Decimal) | (1L << SCIENTIFIC_NUMBER) | (1L << Bool) | (1L << STRING) | (1L << LeftBracket) | (1L << LDoubleBracket) | (1L << PlusPlus) | (1L << Plus) | (1L << MinusMinus) | (1L << Minus) | (1L << LogicalNot) | (1L << Identifier) | (1L << FuncStart))) != 0) );
			setState(199);
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
		public TerminalNode RightBracket() { return getToken(QDLParserParser.RightBracket, 0); }
		public List<TerminalNode> Comma() { return getTokens(QDLParserParser.Comma); }
		public TerminalNode Comma(int i) {
			return getToken(QDLParserParser.Comma, i);
		}
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
		enterRule(_localctx, 30, RULE_expressionBlock);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(201);
			match(LeftBracket);
			setState(202);
			expression(0);
			setState(205); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(203);
				match(Comma);
				setState(204);
				expression(0);
				}
				}
				setState(207); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==Comma );
			setState(209);
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
		enterRule(_localctx, 32, RULE_conditionalBlock);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(211);
			match(LeftBracket);
			setState(212);
			expression(0);
			setState(213);
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
		enterRule(_localctx, 34, RULE_fdoc);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(215);
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
		enterRule(_localctx, 36, RULE_iInterval);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(217);
			match(LeftBracket);
			setState(218);
			expression(0);
			setState(219);
			match(T__0);
			setState(220);
			expression(0);
			setState(223);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__0) {
				{
				setState(221);
				match(T__0);
				setState(222);
				expression(0);
				}
			}

			setState(225);
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
		enterRule(_localctx, 38, RULE_rInterval);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(227);
			match(LDoubleBracket);
			setState(228);
			expression(0);
			setState(229);
			match(T__0);
			setState(230);
			expression(0);
			setState(231);
			match(T__0);
			setState(232);
			expression(0);
			setState(233);
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
		enterRule(_localctx, 40, RULE_stemVariable);
		int _la;
		try {
			setState(248);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,19,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(235);
				match(T__1);
				setState(236);
				stemEntry();
				setState(241);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==Comma) {
					{
					{
					setState(237);
					match(Comma);
					setState(238);
					stemEntry();
					}
					}
					setState(243);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(244);
				match(T__2);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(246);
				match(T__1);
				setState(247);
				match(T__2);
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
		enterRule(_localctx, 42, RULE_stemEntry);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(252);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case Times:
				{
				setState(250);
				match(Times);
				}
				break;
			case T__0:
			case T__1:
			case T__5:
			case ConstantKeywords:
			case Null:
			case Integer:
			case Decimal:
			case SCIENTIFIC_NUMBER:
			case Bool:
			case STRING:
			case LeftBracket:
			case LDoubleBracket:
			case PlusPlus:
			case Plus:
			case MinusMinus:
			case Minus:
			case LogicalNot:
			case Identifier:
			case FuncStart:
				{
				setState(251);
				expression(0);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			setState(254);
			match(Colon);
			setState(255);
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
		enterRule(_localctx, 44, RULE_stemList);
		int _la;
		try {
			setState(270);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,22,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(257);
				match(LeftBracket);
				setState(258);
				stemValue();
				setState(263);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==Comma) {
					{
					{
					setState(259);
					match(Comma);
					setState(260);
					stemValue();
					}
					}
					setState(265);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(266);
				match(RightBracket);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(268);
				match(LeftBracket);
				setState(269);
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
		enterRule(_localctx, 46, RULE_stemValue);
		try {
			setState(275);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,23,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(272);
				expression(0);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(273);
				stemVariable();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(274);
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
		enterRule(_localctx, 48, RULE_function);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(277);
			match(FuncStart);
			setState(281);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__0) | (1L << T__1) | (1L << T__5) | (1L << ConstantKeywords) | (1L << Null) | (1L << Integer) | (1L << Decimal) | (1L << SCIENTIFIC_NUMBER) | (1L << Bool) | (1L << STRING) | (1L << LeftBracket) | (1L << LDoubleBracket) | (1L << PlusPlus) | (1L << Plus) | (1L << MinusMinus) | (1L << Minus) | (1L << LogicalNot) | (1L << Identifier) | (1L << FuncStart) | (1L << F_REF))) != 0)) {
				{
				{
				setState(278);
				f_args();
				}
				}
				setState(283);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(284);
			match(T__3);
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
		enterRule(_localctx, 50, RULE_f_arg);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(288);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__0:
			case T__1:
			case T__5:
			case ConstantKeywords:
			case Null:
			case Integer:
			case Decimal:
			case SCIENTIFIC_NUMBER:
			case Bool:
			case STRING:
			case LeftBracket:
			case LDoubleBracket:
			case PlusPlus:
			case Plus:
			case MinusMinus:
			case Minus:
			case LogicalNot:
			case Identifier:
			case FuncStart:
				{
				setState(286);
				stemValue();
				}
				break;
			case F_REF:
				{
				setState(287);
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
		enterRule(_localctx, 52, RULE_f_args);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(290);
			f_arg();
			setState(295);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==Comma) {
				{
				{
				setState(291);
				match(Comma);
				setState(292);
				f_arg();
				}
				}
				setState(297);
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
		enterRule(_localctx, 54, RULE_f_ref);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(298);
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
		int _startState = 56;
		enterRecursionRule(_localctx, 56, RULE_expression, _p);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(324);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,27,_ctx) ) {
			case 1:
				{
				_localctx = new FunctionsContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;

				setState(301);
				function();
				}
				break;
			case 2:
				{
				_localctx = new StringsContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(302);
				match(STRING);
				}
				break;
			case 3:
				{
				_localctx = new StemVarContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(303);
				stemVariable();
				}
				break;
			case 4:
				{
				_localctx = new StemLiContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(304);
				stemList();
				}
				break;
			case 5:
				{
				_localctx = new RealIntervalContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(305);
				rInterval();
				}
				break;
			case 6:
				{
				_localctx = new IntIntervalContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(306);
				iInterval();
				}
				break;
			case 7:
				{
				_localctx = new PrefixContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(307);
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
				setState(308);
				expression(20);
				}
				break;
			case 8:
				{
				_localctx = new UnaryMinusExpressionContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(309);
				_la = _input.LA(1);
				if ( !(_la==Plus || _la==Minus) ) {
				_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(310);
				expression(17);
				}
				break;
			case 9:
				{
				_localctx = new NotExpressionContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(311);
				match(LogicalNot);
				setState(312);
				expression(10);
				}
				break;
			case 10:
				{
				_localctx = new AssociationContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(313);
				match(T__5);
				setState(314);
				expression(0);
				setState(315);
				match(T__3);
				}
				break;
			case 11:
				{
				_localctx = new IntegersContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(317);
				integer();
				}
				break;
			case 12:
				{
				_localctx = new NumbersContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(318);
				number();
				}
				break;
			case 13:
				{
				_localctx = new VariablesContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(319);
				variable();
				}
				break;
			case 14:
				{
				_localctx = new KeywordsContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(320);
				keyword();
				}
				break;
			case 15:
				{
				_localctx = new LogicalContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(321);
				match(Bool);
				}
				break;
			case 16:
				{
				_localctx = new NullContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(322);
				match(Null);
				}
				break;
			case 17:
				{
				_localctx = new Semi_for_empty_expressionsContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(323);
				match(T__0);
				}
				break;
			}
			_ctx.stop = _input.LT(-1);
			setState(366);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,29,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					setState(364);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,28,_ctx) ) {
					case 1:
						{
						_localctx = new DotOpContext(new ExpressionContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(326);
						if (!(precpred(_ctx, 23))) throw new FailedPredicateException(this, "precpred(_ctx, 23)");
						setState(327);
						match(T__4);
						setState(328);
						expression(24);
						}
						break;
					case 2:
						{
						_localctx = new TildeExpressionContext(new ExpressionContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(329);
						if (!(precpred(_ctx, 22))) throw new FailedPredicateException(this, "precpred(_ctx, 22)");
						setState(330);
						_la = _input.LA(1);
						if ( !(_la==Tilde || _la==TildeRight) ) {
						_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(331);
						expression(23);
						}
						break;
					case 3:
						{
						_localctx = new PowerExpressionContext(new ExpressionContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(332);
						if (!(precpred(_ctx, 19))) throw new FailedPredicateException(this, "precpred(_ctx, 19)");
						setState(333);
						match(Exponentiation);
						setState(334);
						expression(20);
						}
						break;
					case 4:
						{
						_localctx = new MultiplyExpressionContext(new ExpressionContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(335);
						if (!(precpred(_ctx, 18))) throw new FailedPredicateException(this, "precpred(_ctx, 18)");
						setState(336);
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
						setState(337);
						expression(19);
						}
						break;
					case 5:
						{
						_localctx = new AddExpressionContext(new ExpressionContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(338);
						if (!(precpred(_ctx, 16))) throw new FailedPredicateException(this, "precpred(_ctx, 16)");
						setState(339);
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
						setState(340);
						expression(17);
						}
						break;
					case 6:
						{
						_localctx = new CompExpressionContext(new ExpressionContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(341);
						if (!(precpred(_ctx, 15))) throw new FailedPredicateException(this, "precpred(_ctx, 15)");
						setState(342);
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
						setState(343);
						expression(16);
						}
						break;
					case 7:
						{
						_localctx = new EqExpressionContext(new ExpressionContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(344);
						if (!(precpred(_ctx, 14))) throw new FailedPredicateException(this, "precpred(_ctx, 14)");
						setState(345);
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
						setState(346);
						expression(15);
						}
						break;
					case 8:
						{
						_localctx = new RegexMatchesContext(new ExpressionContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(347);
						if (!(precpred(_ctx, 13))) throw new FailedPredicateException(this, "precpred(_ctx, 13)");
						setState(348);
						((RegexMatchesContext)_localctx).op = match(RegexMatches);
						setState(349);
						expression(14);
						}
						break;
					case 9:
						{
						_localctx = new AndExpressionContext(new ExpressionContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(350);
						if (!(precpred(_ctx, 12))) throw new FailedPredicateException(this, "precpred(_ctx, 12)");
						setState(351);
						match(And);
						setState(352);
						expression(13);
						}
						break;
					case 10:
						{
						_localctx = new OrExpressionContext(new ExpressionContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(353);
						if (!(precpred(_ctx, 11))) throw new FailedPredicateException(this, "precpred(_ctx, 11)");
						setState(354);
						match(Or);
						setState(355);
						expression(12);
						}
						break;
					case 11:
						{
						_localctx = new AltIFExpressionContext(new ExpressionContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(356);
						if (!(precpred(_ctx, 8))) throw new FailedPredicateException(this, "precpred(_ctx, 8)");
						setState(357);
						match(T__6);
						setState(358);
						expression(0);
						setState(359);
						match(Colon);
						setState(360);
						expression(9);
						}
						break;
					case 12:
						{
						_localctx = new PostfixContext(new ExpressionContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(362);
						if (!(precpred(_ctx, 21))) throw new FailedPredicateException(this, "precpred(_ctx, 21)");
						setState(363);
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
				setState(368);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,29,_ctx);
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
		enterRule(_localctx, 58, RULE_variable);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(369);
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
		public TerminalNode Integer() { return getToken(QDLParserParser.Integer, 0); }
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
		enterRule(_localctx, 60, RULE_number);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(371);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << Integer) | (1L << Decimal) | (1L << SCIENTIFIC_NUMBER))) != 0)) ) {
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
		enterRule(_localctx, 62, RULE_integer);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(373);
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
		enterRule(_localctx, 64, RULE_keyword);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(375);
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
		case 28:
			return expression_sempred((ExpressionContext)_localctx, predIndex);
		}
		return true;
	}
	private boolean expression_sempred(ExpressionContext _localctx, int predIndex) {
		switch (predIndex) {
		case 0:
			return precpred(_ctx, 23);
		case 1:
			return precpred(_ctx, 22);
		case 2:
			return precpred(_ctx, 19);
		case 3:
			return precpred(_ctx, 18);
		case 4:
			return precpred(_ctx, 16);
		case 5:
			return precpred(_ctx, 15);
		case 6:
			return precpred(_ctx, 14);
		case 7:
			return precpred(_ctx, 13);
		case 8:
			return precpred(_ctx, 12);
		case 9:
			return precpred(_ctx, 11);
		case 10:
			return precpred(_ctx, 8);
		case 11:
			return precpred(_ctx, 21);
		}
		return true;
	}

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\3E\u017c\4\2\t\2\4"+
		"\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13\t"+
		"\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31\t\31"+
		"\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4 \t \4!"+
		"\t!\4\"\t\"\3\2\7\2F\n\2\f\2\16\2I\13\2\3\2\3\2\3\3\3\3\3\3\3\3\3\3\3"+
		"\3\5\3S\n\3\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\5\4]\n\4\3\5\3\5\3\5\6\5b"+
		"\n\5\r\5\16\5c\3\5\3\5\3\6\3\6\5\6j\n\6\3\7\3\7\3\7\5\7o\n\7\3\7\3\7\3"+
		"\b\3\b\3\b\5\bv\n\b\3\b\3\b\3\b\3\b\3\t\3\t\3\t\5\t\177\n\t\3\t\3\t\3"+
		"\n\3\n\3\n\3\n\3\n\7\n\u0088\n\n\f\n\16\n\u008b\13\n\3\n\3\n\3\13\3\13"+
		"\3\13\3\13\3\13\5\13\u0094\n\13\3\13\3\13\3\f\3\f\3\f\3\f\3\f\5\f\u009d"+
		"\n\f\3\r\3\r\3\r\3\r\3\r\5\r\u00a4\n\r\3\r\3\r\5\r\u00a8\n\r\3\r\3\r\3"+
		"\16\3\16\3\16\3\16\3\16\3\17\3\17\3\17\3\17\7\17\u00b5\n\17\f\17\16\17"+
		"\u00b8\13\17\3\17\3\17\3\20\3\20\7\20\u00be\n\20\f\20\16\20\u00c1\13\20"+
		"\3\20\3\20\3\20\6\20\u00c6\n\20\r\20\16\20\u00c7\3\20\3\20\3\21\3\21\3"+
		"\21\3\21\6\21\u00d0\n\21\r\21\16\21\u00d1\3\21\3\21\3\22\3\22\3\22\3\22"+
		"\3\23\3\23\3\24\3\24\3\24\3\24\3\24\3\24\5\24\u00e2\n\24\3\24\3\24\3\25"+
		"\3\25\3\25\3\25\3\25\3\25\3\25\3\25\3\26\3\26\3\26\3\26\7\26\u00f2\n\26"+
		"\f\26\16\26\u00f5\13\26\3\26\3\26\3\26\3\26\5\26\u00fb\n\26\3\27\3\27"+
		"\5\27\u00ff\n\27\3\27\3\27\3\27\3\30\3\30\3\30\3\30\7\30\u0108\n\30\f"+
		"\30\16\30\u010b\13\30\3\30\3\30\3\30\3\30\5\30\u0111\n\30\3\31\3\31\3"+
		"\31\5\31\u0116\n\31\3\32\3\32\7\32\u011a\n\32\f\32\16\32\u011d\13\32\3"+
		"\32\3\32\3\33\3\33\5\33\u0123\n\33\3\34\3\34\3\34\7\34\u0128\n\34\f\34"+
		"\16\34\u012b\13\34\3\35\3\35\3\36\3\36\3\36\3\36\3\36\3\36\3\36\3\36\3"+
		"\36\3\36\3\36\3\36\3\36\3\36\3\36\3\36\3\36\3\36\3\36\3\36\3\36\3\36\3"+
		"\36\3\36\5\36\u0147\n\36\3\36\3\36\3\36\3\36\3\36\3\36\3\36\3\36\3\36"+
		"\3\36\3\36\3\36\3\36\3\36\3\36\3\36\3\36\3\36\3\36\3\36\3\36\3\36\3\36"+
		"\3\36\3\36\3\36\3\36\3\36\3\36\3\36\3\36\3\36\3\36\3\36\3\36\3\36\3\36"+
		"\3\36\7\36\u016f\n\36\f\36\16\36\u0172\13\36\3\37\3\37\3 \3 \3!\3!\3\""+
		"\3\"\3\"\2\3:#\2\4\6\b\n\f\16\20\22\24\26\30\32\34\36 \"$&(*,.\60\62\64"+
		"\668:<>@B\2\t\4\2((**\4\2))++\4\2::==\4\2&\'99\4\2,-/\60\3\2\61\62\3\2"+
		"\32\34\2\u0198\2G\3\2\2\2\4R\3\2\2\2\6\\\3\2\2\2\ba\3\2\2\2\ni\3\2\2\2"+
		"\fk\3\2\2\2\16r\3\2\2\2\20{\3\2\2\2\22\u0082\3\2\2\2\24\u008e\3\2\2\2"+
		"\26\u009c\3\2\2\2\30\u009e\3\2\2\2\32\u00ab\3\2\2\2\34\u00b0\3\2\2\2\36"+
		"\u00bb\3\2\2\2 \u00cb\3\2\2\2\"\u00d5\3\2\2\2$\u00d9\3\2\2\2&\u00db\3"+
		"\2\2\2(\u00e5\3\2\2\2*\u00fa\3\2\2\2,\u00fe\3\2\2\2.\u0110\3\2\2\2\60"+
		"\u0115\3\2\2\2\62\u0117\3\2\2\2\64\u0122\3\2\2\2\66\u0124\3\2\2\28\u012c"+
		"\3\2\2\2:\u0146\3\2\2\2<\u0173\3\2\2\2>\u0175\3\2\2\2@\u0177\3\2\2\2B"+
		"\u0179\3\2\2\2DF\5\4\3\2ED\3\2\2\2FI\3\2\2\2GE\3\2\2\2GH\3\2\2\2HJ\3\2"+
		"\2\2IG\3\2\2\2JK\7\2\2\3K\3\3\2\2\2LM\5\6\4\2MN\7\3\2\2NS\3\2\2\2OP\5"+
		"\30\r\2PQ\7\3\2\2QS\3\2\2\2RL\3\2\2\2RO\3\2\2\2S\5\3\2\2\2T]\5\24\13\2"+
		"U]\5\n\6\2V]\5\20\t\2W]\5\22\n\2X]\5:\36\2Y]\5\b\5\2Z]\5\32\16\2[]\5\26"+
		"\f\2\\T\3\2\2\2\\U\3\2\2\2\\V\3\2\2\2\\W\3\2\2\2\\X\3\2\2\2\\Y\3\2\2\2"+
		"\\Z\3\2\2\2\\[\3\2\2\2]\7\3\2\2\2^_\5:\36\2_`\7>\2\2`b\3\2\2\2a^\3\2\2"+
		"\2bc\3\2\2\2ca\3\2\2\2cd\3\2\2\2de\3\2\2\2ef\5:\36\2f\t\3\2\2\2gj\5\f"+
		"\7\2hj\5\16\b\2ig\3\2\2\2ih\3\2\2\2j\13\3\2\2\2kl\7\23\2\2ln\5\"\22\2"+
		"mo\7\27\2\2nm\3\2\2\2no\3\2\2\2op\3\2\2\2pq\5\34\17\2q\r\3\2\2\2rs\7\23"+
		"\2\2su\5\"\22\2tv\7\27\2\2ut\3\2\2\2uv\3\2\2\2vw\3\2\2\2wx\5\34\17\2x"+
		"y\7\22\2\2yz\5\34\17\2z\17\3\2\2\2{|\7\31\2\2|~\5\"\22\2}\177\7\21\2\2"+
		"~}\3\2\2\2~\177\3\2\2\2\177\u0080\3\2\2\2\u0080\u0081\5\34\17\2\u0081"+
		"\21\3\2\2\2\u0082\u0083\7\26\2\2\u0083\u0089\7\37\2\2\u0084\u0085\5\f"+
		"\7\2\u0085\u0086\7\3\2\2\u0086\u0088\3\2\2\2\u0087\u0084\3\2\2\2\u0088"+
		"\u008b\3\2\2\2\u0089\u0087\3\2\2\2\u0089\u008a\3\2\2\2\u008a\u008c\3\2"+
		"\2\2\u008b\u0089\3\2\2\2\u008c\u008d\7 \2\2\u008d\23\3\2\2\2\u008e\u008f"+
		"\7\20\2\2\u008f\u0090\7\37\2\2\u0090\u0091\5\62\32\2\u0091\u0093\7 \2"+
		"\2\u0092\u0094\7\16\2\2\u0093\u0092\3\2\2\2\u0093\u0094\3\2\2\2\u0094"+
		"\u0095\3\2\2\2\u0095\u0096\5\36\20\2\u0096\25\3\2\2\2\u0097\u0098\5\62"+
		"\32\2\u0098\u0099\7%\2\2\u0099\u009a\5\6\4\2\u009a\u009d\3\2\2\2\u009b"+
		"\u009d\5\34\17\2\u009c\u0097\3\2\2\2\u009c\u009b\3\2\2\2\u009d\27\3\2"+
		"\2\2\u009e\u009f\7\24\2\2\u009f\u00a0\7\37\2\2\u00a0\u00a3\7\36\2\2\u00a1"+
		"\u00a2\7!\2\2\u00a2\u00a4\7\36\2\2\u00a3\u00a1\3\2\2\2\u00a3\u00a4\3\2"+
		"\2\2\u00a4\u00a5\3\2\2\2\u00a5\u00a7\7 \2\2\u00a6\u00a8\7\16\2\2\u00a7"+
		"\u00a6\3\2\2\2\u00a7\u00a8\3\2\2\2\u00a8\u00a9\3\2\2\2\u00a9\u00aa\5\36"+
		"\20\2\u00aa\31\3\2\2\2\u00ab\u00ac\7\30\2\2\u00ac\u00ad\5\34\17\2\u00ad"+
		"\u00ae\7\17\2\2\u00ae\u00af\5\34\17\2\u00af\33\3\2\2\2\u00b0\u00b6\7\37"+
		"\2\2\u00b1\u00b2\5\6\4\2\u00b2\u00b3\7\3\2\2\u00b3\u00b5\3\2\2\2\u00b4"+
		"\u00b1\3\2\2\2\u00b5\u00b8\3\2\2\2\u00b6\u00b4\3\2\2\2\u00b6\u00b7\3\2"+
		"\2\2\u00b7\u00b9\3\2\2\2\u00b8\u00b6\3\2\2\2\u00b9\u00ba\7 \2\2\u00ba"+
		"\35\3\2\2\2\u00bb\u00bf\7\37\2\2\u00bc\u00be\5$\23\2\u00bd\u00bc\3\2\2"+
		"\2\u00be\u00c1\3\2\2\2\u00bf\u00bd\3\2\2\2\u00bf\u00c0\3\2\2\2\u00c0\u00c5"+
		"\3\2\2\2\u00c1\u00bf\3\2\2\2\u00c2\u00c3\5\6\4\2\u00c3\u00c4\7\3\2\2\u00c4"+
		"\u00c6\3\2\2\2\u00c5\u00c2\3\2\2\2\u00c6\u00c7\3\2\2\2\u00c7\u00c5\3\2"+
		"\2\2\u00c7\u00c8\3\2\2\2\u00c8\u00c9\3\2\2\2\u00c9\u00ca\7 \2\2\u00ca"+
		"\37\3\2\2\2\u00cb\u00cc\7\37\2\2\u00cc\u00cf\5:\36\2\u00cd\u00ce\7!\2"+
		"\2\u00ce\u00d0\5:\36\2\u00cf\u00cd\3\2\2\2\u00d0\u00d1\3\2\2\2\u00d1\u00cf"+
		"\3\2\2\2\u00d1\u00d2\3\2\2\2\u00d2\u00d3\3\2\2\2\u00d3\u00d4\7 \2\2\u00d4"+
		"!\3\2\2\2\u00d5\u00d6\7\37\2\2\u00d6\u00d7\5:\36\2\u00d7\u00d8\7 \2\2"+
		"\u00d8#\3\2\2\2\u00d9\u00da\7B\2\2\u00da%\3\2\2\2\u00db\u00dc\7\37\2\2"+
		"\u00dc\u00dd\5:\36\2\u00dd\u00de\7\3\2\2\u00de\u00e1\5:\36\2\u00df\u00e0"+
		"\7\3\2\2\u00e0\u00e2\5:\36\2\u00e1\u00df\3\2\2\2\u00e1\u00e2\3\2\2\2\u00e2"+
		"\u00e3\3\2\2\2\u00e3\u00e4\7 \2\2\u00e4\'\3\2\2\2\u00e5\u00e6\7#\2\2\u00e6"+
		"\u00e7\5:\36\2\u00e7\u00e8\7\3\2\2\u00e8\u00e9\5:\36\2\u00e9\u00ea\7\3"+
		"\2\2\u00ea\u00eb\5:\36\2\u00eb\u00ec\7$\2\2\u00ec)\3\2\2\2\u00ed\u00ee"+
		"\7\4\2\2\u00ee\u00f3\5,\27\2\u00ef\u00f0\7!\2\2\u00f0\u00f2\5,\27\2\u00f1"+
		"\u00ef\3\2\2\2\u00f2\u00f5\3\2\2\2\u00f3\u00f1\3\2\2\2\u00f3\u00f4\3\2"+
		"\2\2\u00f4\u00f6\3\2\2\2\u00f5\u00f3\3\2\2\2\u00f6\u00f7\7\5\2\2\u00f7"+
		"\u00fb\3\2\2\2\u00f8\u00f9\7\4\2\2\u00f9\u00fb\7\5\2\2\u00fa\u00ed\3\2"+
		"\2\2\u00fa\u00f8\3\2\2\2\u00fb+\3\2\2\2\u00fc\u00ff\7&\2\2\u00fd\u00ff"+
		"\5:\36\2\u00fe\u00fc\3\2\2\2\u00fe\u00fd\3\2\2\2\u00ff\u0100\3\2\2\2\u0100"+
		"\u0101\7\"\2\2\u0101\u0102\5\60\31\2\u0102-\3\2\2\2\u0103\u0104\7\37\2"+
		"\2\u0104\u0109\5\60\31\2\u0105\u0106\7!\2\2\u0106\u0108\5\60\31\2\u0107"+
		"\u0105\3\2\2\2\u0108\u010b\3\2\2\2\u0109\u0107\3\2\2\2\u0109\u010a\3\2"+
		"\2\2\u010a\u010c\3\2\2\2\u010b\u0109\3\2\2\2\u010c\u010d\7 \2\2\u010d"+
		"\u0111\3\2\2\2\u010e\u010f\7\37\2\2\u010f\u0111\7 \2\2\u0110\u0103\3\2"+
		"\2\2\u0110\u010e\3\2\2\2\u0111/\3\2\2\2\u0112\u0116\5:\36\2\u0113\u0116"+
		"\5*\26\2\u0114\u0116\5.\30\2\u0115\u0112\3\2\2\2\u0115\u0113\3\2\2\2\u0115"+
		"\u0114\3\2\2\2\u0116\61\3\2\2\2\u0117\u011b\7@\2\2\u0118\u011a\5\66\34"+
		"\2\u0119\u0118\3\2\2\2\u011a\u011d\3\2\2\2\u011b\u0119\3\2\2\2\u011b\u011c"+
		"\3\2\2\2\u011c\u011e\3\2\2\2\u011d\u011b\3\2\2\2\u011e\u011f\7\6\2\2\u011f"+
		"\63\3\2\2\2\u0120\u0123\5\60\31\2\u0121\u0123\58\35\2\u0122\u0120\3\2"+
		"\2\2\u0122\u0121\3\2\2\2\u0123\65\3\2\2\2\u0124\u0129\5\64\33\2\u0125"+
		"\u0126\7!\2\2\u0126\u0128\5\64\33\2\u0127\u0125\3\2\2\2\u0128\u012b\3"+
		"\2\2\2\u0129\u0127\3\2\2\2\u0129\u012a\3\2\2\2\u012a\67\3\2\2\2\u012b"+
		"\u0129\3\2\2\2\u012c\u012d\7A\2\2\u012d9\3\2\2\2\u012e\u012f\b\36\1\2"+
		"\u012f\u0147\5\62\32\2\u0130\u0147\7\36\2\2\u0131\u0147\5*\26\2\u0132"+
		"\u0147\5.\30\2\u0133\u0147\5(\25\2\u0134\u0147\5&\24\2\u0135\u0136\t\2"+
		"\2\2\u0136\u0147\5:\36\26\u0137\u0138\t\3\2\2\u0138\u0147\5:\36\23\u0139"+
		"\u013a\7\64\2\2\u013a\u0147\5:\36\f\u013b\u013c\7\b\2\2\u013c\u013d\5"+
		":\36\2\u013d\u013e\7\6\2\2\u013e\u0147\3\2\2\2\u013f\u0147\5@!\2\u0140"+
		"\u0147\5> \2\u0141\u0147\5<\37\2\u0142\u0147\5B\"\2\u0143\u0147\7\35\2"+
		"\2\u0144\u0147\7\25\2\2\u0145\u0147\7\3\2\2\u0146\u012e\3\2\2\2\u0146"+
		"\u0130\3\2\2\2\u0146\u0131\3\2\2\2\u0146\u0132\3\2\2\2\u0146\u0133\3\2"+
		"\2\2\u0146\u0134\3\2\2\2\u0146\u0135\3\2\2\2\u0146\u0137\3\2\2\2\u0146"+
		"\u0139\3\2\2\2\u0146\u013b\3\2\2\2\u0146\u013f\3\2\2\2\u0146\u0140\3\2"+
		"\2\2\u0146\u0141\3\2\2\2\u0146\u0142\3\2\2\2\u0146\u0143\3\2\2\2\u0146"+
		"\u0144\3\2\2\2\u0146\u0145\3\2\2\2\u0147\u0170\3\2\2\2\u0148\u0149\f\31"+
		"\2\2\u0149\u014a\7\7\2\2\u014a\u016f\5:\36\32\u014b\u014c\f\30\2\2\u014c"+
		"\u014d\t\4\2\2\u014d\u016f\5:\36\31\u014e\u014f\f\25\2\2\u014f\u0150\7"+
		"\65\2\2\u0150\u016f\5:\36\26\u0151\u0152\f\24\2\2\u0152\u0153\t\5\2\2"+
		"\u0153\u016f\5:\36\25\u0154\u0155\f\22\2\2\u0155\u0156\t\3\2\2\u0156\u016f"+
		"\5:\36\23\u0157\u0158\f\21\2\2\u0158\u0159\t\6\2\2\u0159\u016f\5:\36\22"+
		"\u015a\u015b\f\20\2\2\u015b\u015c\t\7\2\2\u015c\u016f\5:\36\21\u015d\u015e"+
		"\f\17\2\2\u015e\u015f\7\63\2\2\u015f\u016f\5:\36\20\u0160\u0161\f\16\2"+
		"\2\u0161\u0162\7\66\2\2\u0162\u016f\5:\36\17\u0163\u0164\f\r\2\2\u0164"+
		"\u0165\7\67\2\2\u0165\u016f\5:\36\16\u0166\u0167\f\n\2\2\u0167\u0168\7"+
		"\t\2\2\u0168\u0169\5:\36\2\u0169\u016a\7\"\2\2\u016a\u016b\5:\36\13\u016b"+
		"\u016f\3\2\2\2\u016c\u016d\f\27\2\2\u016d\u016f\t\2\2\2\u016e\u0148\3"+
		"\2\2\2\u016e\u014b\3\2\2\2\u016e\u014e\3\2\2\2\u016e\u0151\3\2\2\2\u016e"+
		"\u0154\3\2\2\2\u016e\u0157\3\2\2\2\u016e\u015a\3\2\2\2\u016e\u015d\3\2"+
		"\2\2\u016e\u0160\3\2\2\2\u016e\u0163\3\2\2\2\u016e\u0166\3\2\2\2\u016e"+
		"\u016c\3\2\2\2\u016f\u0172\3\2\2\2\u0170\u016e\3\2\2\2\u0170\u0171\3\2"+
		"\2\2\u0171;\3\2\2\2\u0172\u0170\3\2\2\2\u0173\u0174\7?\2\2\u0174=\3\2"+
		"\2\2\u0175\u0176\t\b\2\2\u0176?\3\2\2\2\u0177\u0178\7\32\2\2\u0178A\3"+
		"\2\2\2\u0179\u017a\7\n\2\2\u017aC\3\2\2\2 GR\\cinu~\u0089\u0093\u009c"+
		"\u00a3\u00a7\u00b6\u00bf\u00c7\u00d1\u00e1\u00f3\u00fa\u00fe\u0109\u0110"+
		"\u0115\u011b\u0122\u0129\u0146\u016e\u0170";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}