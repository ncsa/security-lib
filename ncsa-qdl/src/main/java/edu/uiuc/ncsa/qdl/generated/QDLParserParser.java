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
		T__0=1, T__1=2, T__2=3, T__3=4, T__4=5, T__5=6, T__6=7, T__7=8, T__8=9, 
		T__9=10, T__10=11, T__11=12, Number=13, ID=14, Bool=15, ASSIGN=16, FuncStart=17, 
		BOOL_TRUE=18, BOOL_FALSE=19, Null=20, STRING=21, Times=22, Divide=23, 
		PlusPlus=24, Plus=25, MinusMinus=26, Minus=27, LessThan=28, GreaterThan=29, 
		LessEquals=30, MoreEquals=31, Equals=32, NotEquals=33, And=34, Or=35, 
		Backtick=36, Percent=37, Tilde=38, LeftBracket=39, LogicalIf=40, LogicalThen=41, 
		LogicalElse=42, WhileLoop=43, WhileDo=44, SwitchStatement=45, DefineStatement=46, 
		BodyStatement=47, ModuleStatement=48, TryStatement=49, CatchStatement=50, 
		StatementConnector=51, COMMENT=52, LINE_COMMENT=53, WS2=54, FDOC=55;
	public static final int
		RULE_elements = 0, RULE_variable = 1, RULE_number = 2, RULE_fdoc = 3, 
		RULE_assignment = 4, RULE_stemVariable = 5, RULE_stemEntry = 6, RULE_stemList = 7, 
		RULE_argList = 8, RULE_stemValue = 9, RULE_function = 10, RULE_expression = 11, 
		RULE_element = 12, RULE_statement = 13, RULE_ifStatement = 14, RULE_ifElseStatement = 15, 
		RULE_conditionalStatement = 16, RULE_loopStatement = 17, RULE_switchStatement = 18, 
		RULE_defineStatement = 19, RULE_moduleStatement = 20, RULE_tryCatchStatement = 21;
	private static String[] makeRuleNames() {
		return new String[] {
			"elements", "variable", "number", "fdoc", "assignment", "stemVariable", 
			"stemEntry", "stemList", "argList", "stemValue", "function", "expression", 
			"element", "statement", "ifStatement", "ifElseStatement", "conditionalStatement", 
			"loopStatement", "switchStatement", "defineStatement", "moduleStatement", 
			"tryCatchStatement"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'{'", "','", "'}'", "':'", "'['", "')'", "'!'", "'^'", "'=<'", 
			"'=>'", "'('", "';'", null, null, null, null, null, "'true'", "'false'", 
			"'null'", null, "'*'", "'/'", "'++'", "'+'", "'--'", "'-'", "'<'", "'>'", 
			"'<='", "'>='", "'=='", "'!='", "'&&'", "'||'", "'`'", "'%'", "'~'", 
			"']'", "'if['", "']then['", "']else['", "'while['", "']do['", "'switch['", 
			"'define['", "']body['", "'module['", "'try['", "']catch['", "']['"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, "Number", "ID", "Bool", "ASSIGN", "FuncStart", "BOOL_TRUE", "BOOL_FALSE", 
			"Null", "STRING", "Times", "Divide", "PlusPlus", "Plus", "MinusMinus", 
			"Minus", "LessThan", "GreaterThan", "LessEquals", "MoreEquals", "Equals", 
			"NotEquals", "And", "Or", "Backtick", "Percent", "Tilde", "LeftBracket", 
			"LogicalIf", "LogicalThen", "LogicalElse", "WhileLoop", "WhileDo", "SwitchStatement", 
			"DefineStatement", "BodyStatement", "ModuleStatement", "TryStatement", 
			"CatchStatement", "StatementConnector", "COMMENT", "LINE_COMMENT", "WS2", 
			"FDOC"
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
			setState(47);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__0) | (1L << T__4) | (1L << T__6) | (1L << T__10) | (1L << T__11) | (1L << Number) | (1L << ID) | (1L << Bool) | (1L << FuncStart) | (1L << Null) | (1L << STRING) | (1L << PlusPlus) | (1L << Plus) | (1L << MinusMinus) | (1L << Minus) | (1L << LeftBracket) | (1L << LogicalIf) | (1L << WhileLoop) | (1L << SwitchStatement) | (1L << DefineStatement) | (1L << ModuleStatement) | (1L << TryStatement))) != 0)) {
				{
				{
				setState(44);
				element();
				}
				}
				setState(49);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(50);
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
			setState(52);
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
			setState(54);
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
			setState(56);
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
		public StemVariableContext stemVariable() {
			return getRuleContext(StemVariableContext.class,0);
		}
		public StemListContext stemList() {
			return getRuleContext(StemListContext.class,0);
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
			setState(61); 
			_errHandler.sync(this);
			_alt = 1;
			do {
				switch (_alt) {
				case 1:
					{
					{
					setState(58);
					variable();
					setState(59);
					((AssignmentContext)_localctx).op = match(ASSIGN);
					}
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				setState(63); 
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,1,_ctx);
			} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
			setState(68);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,2,_ctx) ) {
			case 1:
				{
				setState(65);
				expression(0);
				}
				break;
			case 2:
				{
				setState(66);
				stemVariable();
				}
				break;
			case 3:
				{
				setState(67);
				stemList();
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

	public static class StemVariableContext extends ParserRuleContext {
		public List<StemEntryContext> stemEntry() {
			return getRuleContexts(StemEntryContext.class);
		}
		public StemEntryContext stemEntry(int i) {
			return getRuleContext(StemEntryContext.class,i);
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
		enterRule(_localctx, 10, RULE_stemVariable);
		int _la;
		try {
			setState(83);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,4,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(70);
				match(T__0);
				setState(71);
				stemEntry();
				setState(76);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__1) {
					{
					{
					setState(72);
					match(T__1);
					setState(73);
					stemEntry();
					}
					}
					setState(78);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(79);
				match(T__2);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(81);
				match(T__0);
				setState(82);
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
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public StemValueContext stemValue() {
			return getRuleContext(StemValueContext.class,0);
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
		enterRule(_localctx, 12, RULE_stemEntry);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(85);
			expression(0);
			setState(86);
			match(T__3);
			setState(87);
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
		public List<StemValueContext> stemValue() {
			return getRuleContexts(StemValueContext.class);
		}
		public StemValueContext stemValue(int i) {
			return getRuleContext(StemValueContext.class,i);
		}
		public TerminalNode LeftBracket() { return getToken(QDLParserParser.LeftBracket, 0); }
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
		enterRule(_localctx, 14, RULE_stemList);
		int _la;
		try {
			setState(102);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,6,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(89);
				match(T__4);
				setState(90);
				stemValue();
				setState(95);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__1) {
					{
					{
					setState(91);
					match(T__1);
					setState(92);
					stemValue();
					}
					}
					setState(97);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(98);
				match(LeftBracket);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(100);
				match(T__4);
				setState(101);
				match(LeftBracket);
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

	public static class ArgListContext extends ParserRuleContext {
		public List<StemValueContext> stemValue() {
			return getRuleContexts(StemValueContext.class);
		}
		public StemValueContext stemValue(int i) {
			return getRuleContext(StemValueContext.class,i);
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
		enterRule(_localctx, 16, RULE_argList);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(104);
			stemValue();
			setState(109);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__1) {
				{
				{
				setState(105);
				match(T__1);
				setState(106);
				stemValue();
				}
				}
				setState(111);
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
		enterRule(_localctx, 18, RULE_stemValue);
		try {
			setState(115);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,8,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(112);
				expression(0);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(113);
				stemVariable();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(114);
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
		enterRule(_localctx, 20, RULE_function);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(117);
			match(FuncStart);
			setState(121);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__0) | (1L << T__4) | (1L << T__6) | (1L << T__10) | (1L << T__11) | (1L << Number) | (1L << ID) | (1L << Bool) | (1L << FuncStart) | (1L << Null) | (1L << STRING) | (1L << PlusPlus) | (1L << Plus) | (1L << MinusMinus) | (1L << Minus) | (1L << LeftBracket))) != 0)) {
				{
				{
				setState(118);
				argList();
				}
				}
				setState(123);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(124);
			match(T__5);
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
	public static class TildeExpressionContext extends ExpressionContext {
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public TerminalNode Tilde() { return getToken(QDLParserParser.Tilde, 0); }
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

	public final ExpressionContext expression() throws RecognitionException {
		return expression(0);
	}

	private ExpressionContext expression(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		ExpressionContext _localctx = new ExpressionContext(_ctx, _parentState);
		ExpressionContext _prevctx = _localctx;
		int _startState = 22;
		enterRecursionRule(_localctx, 22, RULE_expression, _p);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(147);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case FuncStart:
				{
				_localctx = new FunctionsContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;

				setState(127);
				function();
				}
				break;
			case T__0:
				{
				_localctx = new StemVarContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(128);
				stemVariable();
				}
				break;
			case T__4:
				{
				_localctx = new StemLiContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(129);
				stemList();
				}
				break;
			case PlusPlus:
			case MinusMinus:
				{
				_localctx = new PrefixContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(130);
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
				setState(131);
				expression(19);
				}
				break;
			case T__6:
				{
				_localctx = new NotExpressionContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(132);
				match(T__6);
				setState(133);
				expression(18);
				}
				break;
			case Plus:
			case Minus:
				{
				_localctx = new UnaryMinusExpressionContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(134);
				_la = _input.LA(1);
				if ( !(_la==Plus || _la==Minus) ) {
				_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(135);
				expression(14);
				}
				break;
			case T__10:
				{
				_localctx = new AssociationContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(136);
				match(T__10);
				setState(137);
				expression(0);
				setState(138);
				match(T__5);
				}
				break;
			case LeftBracket:
				{
				_localctx = new LeftBracketContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(140);
				match(LeftBracket);
				}
				break;
			case Number:
				{
				_localctx = new NumbersContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(141);
				number();
				}
				break;
			case ID:
				{
				_localctx = new VariablesContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(142);
				variable();
				}
				break;
			case Bool:
				{
				_localctx = new LogicalContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(143);
				match(Bool);
				}
				break;
			case Null:
				{
				_localctx = new NullContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(144);
				match(Null);
				}
				break;
			case STRING:
				{
				_localctx = new StringsContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(145);
				match(STRING);
				}
				break;
			case T__11:
				{
				_localctx = new Semi_for_empty_expressionsContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(146);
				match(T__11);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			_ctx.stop = _input.LT(-1);
			setState(177);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,12,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					setState(175);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,11,_ctx) ) {
					case 1:
						{
						_localctx = new TildeExpressionContext(new ExpressionContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(149);
						if (!(precpred(_ctx, 17))) throw new FailedPredicateException(this, "precpred(_ctx, 17)");
						setState(150);
						match(Tilde);
						setState(151);
						expression(18);
						}
						break;
					case 2:
						{
						_localctx = new PowerExpressionContext(new ExpressionContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(152);
						if (!(precpred(_ctx, 16))) throw new FailedPredicateException(this, "precpred(_ctx, 16)");
						setState(153);
						match(T__7);
						setState(154);
						expression(17);
						}
						break;
					case 3:
						{
						_localctx = new MultiplyExpressionContext(new ExpressionContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(155);
						if (!(precpred(_ctx, 15))) throw new FailedPredicateException(this, "precpred(_ctx, 15)");
						setState(156);
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
						setState(157);
						expression(16);
						}
						break;
					case 4:
						{
						_localctx = new AddExpressionContext(new ExpressionContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(158);
						if (!(precpred(_ctx, 13))) throw new FailedPredicateException(this, "precpred(_ctx, 13)");
						setState(159);
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
						setState(160);
						expression(14);
						}
						break;
					case 5:
						{
						_localctx = new CompExpressionContext(new ExpressionContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(161);
						if (!(precpred(_ctx, 12))) throw new FailedPredicateException(this, "precpred(_ctx, 12)");
						setState(162);
						((CompExpressionContext)_localctx).op = _input.LT(1);
						_la = _input.LA(1);
						if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__8) | (1L << T__9) | (1L << LessThan) | (1L << GreaterThan) | (1L << LessEquals) | (1L << MoreEquals))) != 0)) ) {
							((CompExpressionContext)_localctx).op = (Token)_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(163);
						expression(13);
						}
						break;
					case 6:
						{
						_localctx = new EqExpressionContext(new ExpressionContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(164);
						if (!(precpred(_ctx, 11))) throw new FailedPredicateException(this, "precpred(_ctx, 11)");
						setState(165);
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
						setState(166);
						expression(12);
						}
						break;
					case 7:
						{
						_localctx = new AndExpressionContext(new ExpressionContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(167);
						if (!(precpred(_ctx, 10))) throw new FailedPredicateException(this, "precpred(_ctx, 10)");
						setState(168);
						match(And);
						setState(169);
						expression(11);
						}
						break;
					case 8:
						{
						_localctx = new OrExpressionContext(new ExpressionContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(170);
						if (!(precpred(_ctx, 9))) throw new FailedPredicateException(this, "precpred(_ctx, 9)");
						setState(171);
						match(Or);
						setState(172);
						expression(10);
						}
						break;
					case 9:
						{
						_localctx = new PostfixContext(new ExpressionContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(173);
						if (!(precpred(_ctx, 20))) throw new FailedPredicateException(this, "precpred(_ctx, 20)");
						setState(174);
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
				setState(179);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,12,_ctx);
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
		enterRule(_localctx, 24, RULE_element);
		try {
			setState(186);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__0:
			case T__4:
			case T__6:
			case T__10:
			case T__11:
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
				setState(180);
				statement();
				setState(181);
				match(T__11);
				}
				}
				break;
			case ModuleStatement:
				enterOuterAlt(_localctx, 2);
				{
				{
				setState(183);
				moduleStatement();
				setState(184);
				match(T__11);
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
		public StemVariableContext stemVariable() {
			return getRuleContext(StemVariableContext.class,0);
		}
		public StemListContext stemList() {
			return getRuleContext(StemListContext.class,0);
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
		enterRule(_localctx, 26, RULE_statement);
		try {
			setState(197);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,14,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(188);
				defineStatement();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(189);
				conditionalStatement();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(190);
				loopStatement();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(191);
				switchStatement();
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(192);
				assignment();
				}
				break;
			case 6:
				enterOuterAlt(_localctx, 6);
				{
				setState(193);
				tryCatchStatement();
				}
				break;
			case 7:
				enterOuterAlt(_localctx, 7);
				{
				setState(194);
				stemVariable();
				}
				break;
			case 8:
				enterOuterAlt(_localctx, 8);
				{
				setState(195);
				stemList();
				}
				break;
			case 9:
				enterOuterAlt(_localctx, 9);
				{
				setState(196);
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
		enterRule(_localctx, 28, RULE_ifStatement);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(199);
			match(LogicalIf);
			setState(200);
			expression(0);
			setState(201);
			_la = _input.LA(1);
			if ( !(_la==LogicalThen || _la==StatementConnector) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			setState(207);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,15,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(202);
					statement();
					setState(203);
					match(T__11);
					}
					} 
				}
				setState(209);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,15,_ctx);
			}
			setState(210);
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
		enterRule(_localctx, 30, RULE_ifElseStatement);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(212);
			match(LogicalIf);
			setState(213);
			expression(0);
			setState(214);
			_la = _input.LA(1);
			if ( !(_la==LogicalThen || _la==StatementConnector) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			setState(220);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__0) | (1L << T__4) | (1L << T__6) | (1L << T__10) | (1L << T__11) | (1L << Number) | (1L << ID) | (1L << Bool) | (1L << FuncStart) | (1L << Null) | (1L << STRING) | (1L << PlusPlus) | (1L << Plus) | (1L << MinusMinus) | (1L << Minus) | (1L << LeftBracket) | (1L << LogicalIf) | (1L << WhileLoop) | (1L << SwitchStatement) | (1L << DefineStatement) | (1L << TryStatement))) != 0)) {
				{
				{
				setState(215);
				statement();
				setState(216);
				match(T__11);
				}
				}
				setState(222);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(223);
			match(LogicalElse);
			setState(229);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,17,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(224);
					statement();
					setState(225);
					match(T__11);
					}
					} 
				}
				setState(231);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,17,_ctx);
			}
			setState(232);
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
		enterRule(_localctx, 32, RULE_conditionalStatement);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(236);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,18,_ctx) ) {
			case 1:
				{
				setState(234);
				ifElseStatement();
				}
				break;
			case 2:
				{
				setState(235);
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
		enterRule(_localctx, 34, RULE_loopStatement);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(238);
			match(WhileLoop);
			setState(239);
			expression(0);
			setState(240);
			_la = _input.LA(1);
			if ( !(_la==WhileDo || _la==StatementConnector) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			setState(246);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,19,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(241);
					statement();
					setState(242);
					match(T__11);
					}
					} 
				}
				setState(248);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,19,_ctx);
			}
			setState(249);
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
		enterRule(_localctx, 36, RULE_switchStatement);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(251);
			match(SwitchStatement);
			setState(257);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==LogicalIf) {
				{
				{
				setState(252);
				ifStatement();
				setState(253);
				match(T__11);
				}
				}
				setState(259);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(260);
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
		enterRule(_localctx, 38, RULE_defineStatement);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(262);
			match(DefineStatement);
			setState(263);
			function();
			setState(264);
			_la = _input.LA(1);
			if ( !(_la==BodyStatement || _la==StatementConnector) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			setState(268);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==FDOC) {
				{
				{
				setState(265);
				fdoc();
				}
				}
				setState(270);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(274); 
			_errHandler.sync(this);
			_alt = 1;
			do {
				switch (_alt) {
				case 1:
					{
					{
					setState(271);
					statement();
					setState(272);
					match(T__11);
					}
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				setState(276); 
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,22,_ctx);
			} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
			setState(278);
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
		enterRule(_localctx, 40, RULE_moduleStatement);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(280);
			match(ModuleStatement);
			setState(281);
			match(STRING);
			setState(284);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__1) {
				{
				setState(282);
				match(T__1);
				setState(283);
				match(STRING);
				}
			}

			setState(286);
			_la = _input.LA(1);
			if ( !(_la==BodyStatement || _la==StatementConnector) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			setState(292);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,24,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(287);
					statement();
					setState(288);
					match(T__11);
					}
					} 
				}
				setState(294);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,24,_ctx);
			}
			setState(295);
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
		enterRule(_localctx, 42, RULE_tryCatchStatement);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(297);
			match(TryStatement);
			setState(303);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__0) | (1L << T__4) | (1L << T__6) | (1L << T__10) | (1L << T__11) | (1L << Number) | (1L << ID) | (1L << Bool) | (1L << FuncStart) | (1L << Null) | (1L << STRING) | (1L << PlusPlus) | (1L << Plus) | (1L << MinusMinus) | (1L << Minus) | (1L << LeftBracket) | (1L << LogicalIf) | (1L << WhileLoop) | (1L << SwitchStatement) | (1L << DefineStatement) | (1L << TryStatement))) != 0)) {
				{
				{
				setState(298);
				statement();
				setState(299);
				match(T__11);
				}
				}
				setState(305);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(306);
			match(CatchStatement);
			setState(312);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,26,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(307);
					statement();
					setState(308);
					match(T__11);
					}
					} 
				}
				setState(314);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,26,_ctx);
			}
			setState(315);
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
		case 11:
			return expression_sempred((ExpressionContext)_localctx, predIndex);
		}
		return true;
	}
	private boolean expression_sempred(ExpressionContext _localctx, int predIndex) {
		switch (predIndex) {
		case 0:
			return precpred(_ctx, 17);
		case 1:
			return precpred(_ctx, 16);
		case 2:
			return precpred(_ctx, 15);
		case 3:
			return precpred(_ctx, 13);
		case 4:
			return precpred(_ctx, 12);
		case 5:
			return precpred(_ctx, 11);
		case 6:
			return precpred(_ctx, 10);
		case 7:
			return precpred(_ctx, 9);
		case 8:
			return precpred(_ctx, 20);
		}
		return true;
	}

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\39\u0140\4\2\t\2\4"+
		"\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13\t"+
		"\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\3\2\7\2\60\n\2\f\2"+
		"\16\2\63\13\2\3\2\3\2\3\3\3\3\3\4\3\4\3\5\3\5\3\6\3\6\3\6\6\6@\n\6\r\6"+
		"\16\6A\3\6\3\6\3\6\5\6G\n\6\3\7\3\7\3\7\3\7\7\7M\n\7\f\7\16\7P\13\7\3"+
		"\7\3\7\3\7\3\7\5\7V\n\7\3\b\3\b\3\b\3\b\3\t\3\t\3\t\3\t\7\t`\n\t\f\t\16"+
		"\tc\13\t\3\t\3\t\3\t\3\t\5\ti\n\t\3\n\3\n\3\n\7\nn\n\n\f\n\16\nq\13\n"+
		"\3\13\3\13\3\13\5\13v\n\13\3\f\3\f\7\fz\n\f\f\f\16\f}\13\f\3\f\3\f\3\r"+
		"\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3"+
		"\r\3\r\3\r\5\r\u0096\n\r\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3"+
		"\r\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3\r\7\r\u00b2\n"+
		"\r\f\r\16\r\u00b5\13\r\3\16\3\16\3\16\3\16\3\16\3\16\5\16\u00bd\n\16\3"+
		"\17\3\17\3\17\3\17\3\17\3\17\3\17\3\17\3\17\5\17\u00c8\n\17\3\20\3\20"+
		"\3\20\3\20\3\20\3\20\7\20\u00d0\n\20\f\20\16\20\u00d3\13\20\3\20\3\20"+
		"\3\21\3\21\3\21\3\21\3\21\3\21\7\21\u00dd\n\21\f\21\16\21\u00e0\13\21"+
		"\3\21\3\21\3\21\3\21\7\21\u00e6\n\21\f\21\16\21\u00e9\13\21\3\21\3\21"+
		"\3\22\3\22\5\22\u00ef\n\22\3\23\3\23\3\23\3\23\3\23\3\23\7\23\u00f7\n"+
		"\23\f\23\16\23\u00fa\13\23\3\23\3\23\3\24\3\24\3\24\3\24\7\24\u0102\n"+
		"\24\f\24\16\24\u0105\13\24\3\24\3\24\3\25\3\25\3\25\3\25\7\25\u010d\n"+
		"\25\f\25\16\25\u0110\13\25\3\25\3\25\3\25\6\25\u0115\n\25\r\25\16\25\u0116"+
		"\3\25\3\25\3\26\3\26\3\26\3\26\5\26\u011f\n\26\3\26\3\26\3\26\3\26\7\26"+
		"\u0125\n\26\f\26\16\26\u0128\13\26\3\26\3\26\3\27\3\27\3\27\3\27\7\27"+
		"\u0130\n\27\f\27\16\27\u0133\13\27\3\27\3\27\3\27\3\27\7\27\u0139\n\27"+
		"\f\27\16\27\u013c\13\27\3\27\3\27\3\27\2\3\30\30\2\4\6\b\n\f\16\20\22"+
		"\24\26\30\32\34\36 \"$&(*,\2\n\4\2\32\32\34\34\4\2\33\33\35\35\4\2\30"+
		"\31\'\'\4\2\13\f\36!\3\2\"#\4\2++\65\65\4\2..\65\65\4\2\61\61\65\65\2"+
		"\u0160\2\61\3\2\2\2\4\66\3\2\2\2\68\3\2\2\2\b:\3\2\2\2\n?\3\2\2\2\fU\3"+
		"\2\2\2\16W\3\2\2\2\20h\3\2\2\2\22j\3\2\2\2\24u\3\2\2\2\26w\3\2\2\2\30"+
		"\u0095\3\2\2\2\32\u00bc\3\2\2\2\34\u00c7\3\2\2\2\36\u00c9\3\2\2\2 \u00d6"+
		"\3\2\2\2\"\u00ee\3\2\2\2$\u00f0\3\2\2\2&\u00fd\3\2\2\2(\u0108\3\2\2\2"+
		"*\u011a\3\2\2\2,\u012b\3\2\2\2.\60\5\32\16\2/.\3\2\2\2\60\63\3\2\2\2\61"+
		"/\3\2\2\2\61\62\3\2\2\2\62\64\3\2\2\2\63\61\3\2\2\2\64\65\7\2\2\3\65\3"+
		"\3\2\2\2\66\67\7\20\2\2\67\5\3\2\2\289\7\17\2\29\7\3\2\2\2:;\79\2\2;\t"+
		"\3\2\2\2<=\5\4\3\2=>\7\22\2\2>@\3\2\2\2?<\3\2\2\2@A\3\2\2\2A?\3\2\2\2"+
		"AB\3\2\2\2BF\3\2\2\2CG\5\30\r\2DG\5\f\7\2EG\5\20\t\2FC\3\2\2\2FD\3\2\2"+
		"\2FE\3\2\2\2G\13\3\2\2\2HI\7\3\2\2IN\5\16\b\2JK\7\4\2\2KM\5\16\b\2LJ\3"+
		"\2\2\2MP\3\2\2\2NL\3\2\2\2NO\3\2\2\2OQ\3\2\2\2PN\3\2\2\2QR\7\5\2\2RV\3"+
		"\2\2\2ST\7\3\2\2TV\7\5\2\2UH\3\2\2\2US\3\2\2\2V\r\3\2\2\2WX\5\30\r\2X"+
		"Y\7\6\2\2YZ\5\24\13\2Z\17\3\2\2\2[\\\7\7\2\2\\a\5\24\13\2]^\7\4\2\2^`"+
		"\5\24\13\2_]\3\2\2\2`c\3\2\2\2a_\3\2\2\2ab\3\2\2\2bd\3\2\2\2ca\3\2\2\2"+
		"de\7)\2\2ei\3\2\2\2fg\7\7\2\2gi\7)\2\2h[\3\2\2\2hf\3\2\2\2i\21\3\2\2\2"+
		"jo\5\24\13\2kl\7\4\2\2ln\5\24\13\2mk\3\2\2\2nq\3\2\2\2om\3\2\2\2op\3\2"+
		"\2\2p\23\3\2\2\2qo\3\2\2\2rv\5\30\r\2sv\5\f\7\2tv\5\20\t\2ur\3\2\2\2u"+
		"s\3\2\2\2ut\3\2\2\2v\25\3\2\2\2w{\7\23\2\2xz\5\22\n\2yx\3\2\2\2z}\3\2"+
		"\2\2{y\3\2\2\2{|\3\2\2\2|~\3\2\2\2}{\3\2\2\2~\177\7\b\2\2\177\27\3\2\2"+
		"\2\u0080\u0081\b\r\1\2\u0081\u0096\5\26\f\2\u0082\u0096\5\f\7\2\u0083"+
		"\u0096\5\20\t\2\u0084\u0085\t\2\2\2\u0085\u0096\5\30\r\25\u0086\u0087"+
		"\7\t\2\2\u0087\u0096\5\30\r\24\u0088\u0089\t\3\2\2\u0089\u0096\5\30\r"+
		"\20\u008a\u008b\7\r\2\2\u008b\u008c\5\30\r\2\u008c\u008d\7\b\2\2\u008d"+
		"\u0096\3\2\2\2\u008e\u0096\7)\2\2\u008f\u0096\5\6\4\2\u0090\u0096\5\4"+
		"\3\2\u0091\u0096\7\21\2\2\u0092\u0096\7\26\2\2\u0093\u0096\7\27\2\2\u0094"+
		"\u0096\7\16\2\2\u0095\u0080\3\2\2\2\u0095\u0082\3\2\2\2\u0095\u0083\3"+
		"\2\2\2\u0095\u0084\3\2\2\2\u0095\u0086\3\2\2\2\u0095\u0088\3\2\2\2\u0095"+
		"\u008a\3\2\2\2\u0095\u008e\3\2\2\2\u0095\u008f\3\2\2\2\u0095\u0090\3\2"+
		"\2\2\u0095\u0091\3\2\2\2\u0095\u0092\3\2\2\2\u0095\u0093\3\2\2\2\u0095"+
		"\u0094\3\2\2\2\u0096\u00b3\3\2\2\2\u0097\u0098\f\23\2\2\u0098\u0099\7"+
		"(\2\2\u0099\u00b2\5\30\r\24\u009a\u009b\f\22\2\2\u009b\u009c\7\n\2\2\u009c"+
		"\u00b2\5\30\r\23\u009d\u009e\f\21\2\2\u009e\u009f\t\4\2\2\u009f\u00b2"+
		"\5\30\r\22\u00a0\u00a1\f\17\2\2\u00a1\u00a2\t\3\2\2\u00a2\u00b2\5\30\r"+
		"\20\u00a3\u00a4\f\16\2\2\u00a4\u00a5\t\5\2\2\u00a5\u00b2\5\30\r\17\u00a6"+
		"\u00a7\f\r\2\2\u00a7\u00a8\t\6\2\2\u00a8\u00b2\5\30\r\16\u00a9\u00aa\f"+
		"\f\2\2\u00aa\u00ab\7$\2\2\u00ab\u00b2\5\30\r\r\u00ac\u00ad\f\13\2\2\u00ad"+
		"\u00ae\7%\2\2\u00ae\u00b2\5\30\r\f\u00af\u00b0\f\26\2\2\u00b0\u00b2\t"+
		"\2\2\2\u00b1\u0097\3\2\2\2\u00b1\u009a\3\2\2\2\u00b1\u009d\3\2\2\2\u00b1"+
		"\u00a0\3\2\2\2\u00b1\u00a3\3\2\2\2\u00b1\u00a6\3\2\2\2\u00b1\u00a9\3\2"+
		"\2\2\u00b1\u00ac\3\2\2\2\u00b1\u00af\3\2\2\2\u00b2\u00b5\3\2\2\2\u00b3"+
		"\u00b1\3\2\2\2\u00b3\u00b4\3\2\2\2\u00b4\31\3\2\2\2\u00b5\u00b3\3\2\2"+
		"\2\u00b6\u00b7\5\34\17\2\u00b7\u00b8\7\16\2\2\u00b8\u00bd\3\2\2\2\u00b9"+
		"\u00ba\5*\26\2\u00ba\u00bb\7\16\2\2\u00bb\u00bd\3\2\2\2\u00bc\u00b6\3"+
		"\2\2\2\u00bc\u00b9\3\2\2\2\u00bd\33\3\2\2\2\u00be\u00c8\5(\25\2\u00bf"+
		"\u00c8\5\"\22\2\u00c0\u00c8\5$\23\2\u00c1\u00c8\5&\24\2\u00c2\u00c8\5"+
		"\n\6\2\u00c3\u00c8\5,\27\2\u00c4\u00c8\5\f\7\2\u00c5\u00c8\5\20\t\2\u00c6"+
		"\u00c8\5\30\r\2\u00c7\u00be\3\2\2\2\u00c7\u00bf\3\2\2\2\u00c7\u00c0\3"+
		"\2\2\2\u00c7\u00c1\3\2\2\2\u00c7\u00c2\3\2\2\2\u00c7\u00c3\3\2\2\2\u00c7"+
		"\u00c4\3\2\2\2\u00c7\u00c5\3\2\2\2\u00c7\u00c6\3\2\2\2\u00c8\35\3\2\2"+
		"\2\u00c9\u00ca\7*\2\2\u00ca\u00cb\5\30\r\2\u00cb\u00d1\t\7\2\2\u00cc\u00cd"+
		"\5\34\17\2\u00cd\u00ce\7\16\2\2\u00ce\u00d0\3\2\2\2\u00cf\u00cc\3\2\2"+
		"\2\u00d0\u00d3\3\2\2\2\u00d1\u00cf\3\2\2\2\u00d1\u00d2\3\2\2\2\u00d2\u00d4"+
		"\3\2\2\2\u00d3\u00d1\3\2\2\2\u00d4\u00d5\7)\2\2\u00d5\37\3\2\2\2\u00d6"+
		"\u00d7\7*\2\2\u00d7\u00d8\5\30\r\2\u00d8\u00de\t\7\2\2\u00d9\u00da\5\34"+
		"\17\2\u00da\u00db\7\16\2\2\u00db\u00dd\3\2\2\2\u00dc\u00d9\3\2\2\2\u00dd"+
		"\u00e0\3\2\2\2\u00de\u00dc\3\2\2\2\u00de\u00df\3\2\2\2\u00df\u00e1\3\2"+
		"\2\2\u00e0\u00de\3\2\2\2\u00e1\u00e7\7,\2\2\u00e2\u00e3\5\34\17\2\u00e3"+
		"\u00e4\7\16\2\2\u00e4\u00e6\3\2\2\2\u00e5\u00e2\3\2\2\2\u00e6\u00e9\3"+
		"\2\2\2\u00e7\u00e5\3\2\2\2\u00e7\u00e8\3\2\2\2\u00e8\u00ea\3\2\2\2\u00e9"+
		"\u00e7\3\2\2\2\u00ea\u00eb\7)\2\2\u00eb!\3\2\2\2\u00ec\u00ef\5 \21\2\u00ed"+
		"\u00ef\5\36\20\2\u00ee\u00ec\3\2\2\2\u00ee\u00ed\3\2\2\2\u00ef#\3\2\2"+
		"\2\u00f0\u00f1\7-\2\2\u00f1\u00f2\5\30\r\2\u00f2\u00f8\t\b\2\2\u00f3\u00f4"+
		"\5\34\17\2\u00f4\u00f5\7\16\2\2\u00f5\u00f7\3\2\2\2\u00f6\u00f3\3\2\2"+
		"\2\u00f7\u00fa\3\2\2\2\u00f8\u00f6\3\2\2\2\u00f8\u00f9\3\2\2\2\u00f9\u00fb"+
		"\3\2\2\2\u00fa\u00f8\3\2\2\2\u00fb\u00fc\7)\2\2\u00fc%\3\2\2\2\u00fd\u0103"+
		"\7/\2\2\u00fe\u00ff\5\36\20\2\u00ff\u0100\7\16\2\2\u0100\u0102\3\2\2\2"+
		"\u0101\u00fe\3\2\2\2\u0102\u0105\3\2\2\2\u0103\u0101\3\2\2\2\u0103\u0104"+
		"\3\2\2\2\u0104\u0106\3\2\2\2\u0105\u0103\3\2\2\2\u0106\u0107\7)\2\2\u0107"+
		"\'\3\2\2\2\u0108\u0109\7\60\2\2\u0109\u010a\5\26\f\2\u010a\u010e\t\t\2"+
		"\2\u010b\u010d\5\b\5\2\u010c\u010b\3\2\2\2\u010d\u0110\3\2\2\2\u010e\u010c"+
		"\3\2\2\2\u010e\u010f\3\2\2\2\u010f\u0114\3\2\2\2\u0110\u010e\3\2\2\2\u0111"+
		"\u0112\5\34\17\2\u0112\u0113\7\16\2\2\u0113\u0115\3\2\2\2\u0114\u0111"+
		"\3\2\2\2\u0115\u0116\3\2\2\2\u0116\u0114\3\2\2\2\u0116\u0117\3\2\2\2\u0117"+
		"\u0118\3\2\2\2\u0118\u0119\7)\2\2\u0119)\3\2\2\2\u011a\u011b\7\62\2\2"+
		"\u011b\u011e\7\27\2\2\u011c\u011d\7\4\2\2\u011d\u011f\7\27\2\2\u011e\u011c"+
		"\3\2\2\2\u011e\u011f\3\2\2\2\u011f\u0120\3\2\2\2\u0120\u0126\t\t\2\2\u0121"+
		"\u0122\5\34\17\2\u0122\u0123\7\16\2\2\u0123\u0125\3\2\2\2\u0124\u0121"+
		"\3\2\2\2\u0125\u0128\3\2\2\2\u0126\u0124\3\2\2\2\u0126\u0127\3\2\2\2\u0127"+
		"\u0129\3\2\2\2\u0128\u0126\3\2\2\2\u0129\u012a\7)\2\2\u012a+\3\2\2\2\u012b"+
		"\u0131\7\63\2\2\u012c\u012d\5\34\17\2\u012d\u012e\7\16\2\2\u012e\u0130"+
		"\3\2\2\2\u012f\u012c\3\2\2\2\u0130\u0133\3\2\2\2\u0131\u012f\3\2\2\2\u0131"+
		"\u0132\3\2\2\2\u0132\u0134\3\2\2\2\u0133\u0131\3\2\2\2\u0134\u013a\7\64"+
		"\2\2\u0135\u0136\5\34\17\2\u0136\u0137\7\16\2\2\u0137\u0139\3\2\2\2\u0138"+
		"\u0135\3\2\2\2\u0139\u013c\3\2\2\2\u013a\u0138\3\2\2\2\u013a\u013b\3\2"+
		"\2\2\u013b\u013d\3\2\2\2\u013c\u013a\3\2\2\2\u013d\u013e\7)\2\2\u013e"+
		"-\3\2\2\2\35\61AFNUahou{\u0095\u00b1\u00b3\u00bc\u00c7\u00d1\u00de\u00e7"+
		"\u00ee\u00f8\u0103\u010e\u0116\u011e\u0126\u0131\u013a";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}