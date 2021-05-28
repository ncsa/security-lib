// Generated from QDLParser.g4 by ANTLR 4.9.1
package edu.uiuc.ncsa.qdl.generated;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class QDLParserLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.9.1", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, T__2=3, T__3=4, T__4=5, T__5=6, T__6=7, T__7=8, T__8=9, 
		Integer=10, Identifier=11, Bool=12, ASSIGN=13, FuncStart=14, F_REF=15, 
		BOOL_TRUE=16, BOOL_FALSE=17, Null=18, STRING=19, Decimal=20, SCIENTIFIC_NUMBER=21, 
		LambdaConnector=22, Times=23, Divide=24, PlusPlus=25, Plus=26, MinusMinus=27, 
		Minus=28, LessThan=29, GreaterThan=30, SingleEqual=31, LessEquals=32, 
		MoreEquals=33, Equals=34, NotEquals=35, RegexMatches=36, LogicalNot=37, 
		Exponentiation=38, And=39, Or=40, Backtick=41, Percent=42, Tilde=43, Backslash=44, 
		Stile=45, TildeRight=46, LeftBracket=47, RightBracket=48, LogicalIf=49, 
		LogicalThen=50, LogicalElse=51, WhileLoop=52, WhileDo=53, SwitchStatement=54, 
		DefineStatement=55, BodyStatement=56, ModuleStatement=57, TryStatement=58, 
		CatchStatement=59, StatementConnector=60, FDOC=61, WS=62, COMMENT=63, 
		LINE_COMMENT=64;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	private static String[] makeRuleNames() {
		return new String[] {
			"T__0", "T__1", "T__2", "T__3", "T__4", "T__5", "T__6", "T__7", "T__8", 
			"Integer", "Identifier", "Bool", "ASSIGN", "FuncStart", "F_REF", "BOOL_TRUE", 
			"BOOL_FALSE", "Null", "STRING", "Decimal", "AllOps", "FUNCTION_NAME", 
			"SCIENTIFIC_NUMBER", "E", "SIGN", "LambdaConnector", "Times", "Divide", 
			"PlusPlus", "Plus", "MinusMinus", "Minus", "LessThan", "GreaterThan", 
			"SingleEqual", "LessEquals", "MoreEquals", "Equals", "NotEquals", "RegexMatches", 
			"LogicalNot", "Exponentiation", "And", "Or", "Backtick", "Percent", "Tilde", 
			"Backslash", "Stile", "TildeRight", "LeftBracket", "RightBracket", "LogicalIf", 
			"LogicalThen", "LogicalElse", "WhileLoop", "WhileDo", "SwitchStatement", 
			"DefineStatement", "BodyStatement", "ModuleStatement", "TryStatement", 
			"CatchStatement", "StatementConnector", "ESC", "UnicodeEscape", "HexDigit", 
			"StringCharacters", "StringCharacter", "FDOC", "WS", "COMMENT", "LINE_COMMENT"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'{'", "','", "'}'", "':'", "')'", "'.'", "'('", "'?'", "';'", 
			null, null, null, null, null, null, "'true'", "'false'", null, null, 
			null, null, null, null, null, "'++'", "'+'", "'--'", "'-'", "'<'", "'>'", 
			"'='", null, null, null, null, null, null, "'^'", null, null, "'`'", 
			"'%'", "'~'", "'\\'", "'|'", "'~|'", "']'", "'['", "'if['", "']then['", 
			"']else['", "'while['", "']do['", "'switch['", "'define['", "']body['", 
			"'module['", "'try['", "']catch['", "']['"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, null, null, null, null, null, null, null, null, null, "Integer", 
			"Identifier", "Bool", "ASSIGN", "FuncStart", "F_REF", "BOOL_TRUE", "BOOL_FALSE", 
			"Null", "STRING", "Decimal", "SCIENTIFIC_NUMBER", "LambdaConnector", 
			"Times", "Divide", "PlusPlus", "Plus", "MinusMinus", "Minus", "LessThan", 
			"GreaterThan", "SingleEqual", "LessEquals", "MoreEquals", "Equals", "NotEquals", 
			"RegexMatches", "LogicalNot", "Exponentiation", "And", "Or", "Backtick", 
			"Percent", "Tilde", "Backslash", "Stile", "TildeRight", "LeftBracket", 
			"RightBracket", "LogicalIf", "LogicalThen", "LogicalElse", "WhileLoop", 
			"WhileDo", "SwitchStatement", "DefineStatement", "BodyStatement", "ModuleStatement", 
			"TryStatement", "CatchStatement", "StatementConnector", "FDOC", "WS", 
			"COMMENT", "LINE_COMMENT"
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


	public QDLParserLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "QDLParser.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public String[] getChannelNames() { return channelNames; }

	@Override
	public String[] getModeNames() { return modeNames; }

	@Override
	public ATN getATN() { return _ATN; }

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2B\u0206\b\1\4\2\t"+
		"\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13"+
		"\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31\t\31"+
		"\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4 \t \4!"+
		"\t!\4\"\t\"\4#\t#\4$\t$\4%\t%\4&\t&\4\'\t\'\4(\t(\4)\t)\4*\t*\4+\t+\4"+
		",\t,\4-\t-\4.\t.\4/\t/\4\60\t\60\4\61\t\61\4\62\t\62\4\63\t\63\4\64\t"+
		"\64\4\65\t\65\4\66\t\66\4\67\t\67\48\t8\49\t9\4:\t:\4;\t;\4<\t<\4=\t="+
		"\4>\t>\4?\t?\4@\t@\4A\tA\4B\tB\4C\tC\4D\tD\4E\tE\4F\tF\4G\tG\4H\tH\4I"+
		"\tI\4J\tJ\3\2\3\2\3\3\3\3\3\4\3\4\3\5\3\5\3\6\3\6\3\7\3\7\3\b\3\b\3\t"+
		"\3\t\3\n\3\n\3\13\6\13\u00a9\n\13\r\13\16\13\u00aa\3\f\3\f\7\f\u00af\n"+
		"\f\f\f\16\f\u00b2\13\f\3\r\3\r\5\r\u00b6\n\r\3\16\3\16\3\16\3\16\3\16"+
		"\3\16\3\16\3\16\3\16\3\16\3\16\3\16\3\16\3\16\3\16\3\16\3\16\3\16\3\16"+
		"\3\16\5\16\u00cc\n\16\3\17\3\17\3\17\3\20\3\20\3\20\3\20\3\20\3\20\5\20"+
		"\u00d7\n\20\3\21\3\21\3\21\3\21\3\21\3\22\3\22\3\22\3\22\3\22\3\22\3\23"+
		"\3\23\3\23\3\23\3\23\5\23\u00e9\n\23\3\24\3\24\5\24\u00ed\n\24\3\24\3"+
		"\24\3\25\3\25\3\25\3\25\3\25\3\25\5\25\u00f7\n\25\3\26\3\26\3\26\3\26"+
		"\3\26\3\26\3\26\3\26\3\26\3\26\3\26\3\26\3\26\3\26\3\26\3\26\3\26\3\26"+
		"\5\26\u010b\n\26\3\27\3\27\7\27\u010f\n\27\f\27\16\27\u0112\13\27\3\30"+
		"\3\30\3\30\5\30\u0117\n\30\3\30\3\30\5\30\u011b\n\30\3\31\3\31\3\32\3"+
		"\32\3\33\3\33\3\33\5\33\u0124\n\33\3\34\3\34\3\35\3\35\3\36\3\36\3\36"+
		"\3\37\3\37\3 \3 \3 \3!\3!\3\"\3\"\3#\3#\3$\3$\3%\3%\3%\3%\3%\5%\u013f"+
		"\n%\3&\3&\3&\3&\3&\5&\u0146\n&\3\'\3\'\3\'\5\'\u014b\n\'\3(\3(\3(\5(\u0150"+
		"\n(\3)\3)\3)\5)\u0155\n)\3*\3*\3+\3+\3,\3,\3,\5,\u015e\n,\3-\3-\3-\5-"+
		"\u0163\n-\3.\3.\3/\3/\3\60\3\60\3\61\3\61\3\62\3\62\3\63\3\63\3\63\3\64"+
		"\3\64\3\65\3\65\3\66\3\66\3\66\3\66\3\67\3\67\3\67\3\67\3\67\3\67\3\67"+
		"\38\38\38\38\38\38\38\39\39\39\39\39\39\39\3:\3:\3:\3:\3:\3;\3;\3;\3;"+
		"\3;\3;\3;\3;\3<\3<\3<\3<\3<\3<\3<\3<\3=\3=\3=\3=\3=\3=\3=\3>\3>\3>\3>"+
		"\3>\3>\3>\3>\3?\3?\3?\3?\3?\3@\3@\3@\3@\3@\3@\3@\3@\3A\3A\3A\3B\3B\3B"+
		"\5B\u01c6\nB\3C\3C\6C\u01ca\nC\rC\16C\u01cb\3C\3C\3C\3C\3C\3D\3D\3E\6"+
		"E\u01d6\nE\rE\16E\u01d7\3F\3F\5F\u01dc\nF\3G\3G\3G\3G\7G\u01e2\nG\fG\16"+
		"G\u01e5\13G\3H\6H\u01e8\nH\rH\16H\u01e9\3H\3H\3I\3I\3I\3I\7I\u01f2\nI"+
		"\fI\16I\u01f5\13I\3I\3I\3I\3I\3I\3J\3J\3J\3J\7J\u0200\nJ\fJ\16J\u0203"+
		"\13J\3J\3J\3\u01f3\2K\3\3\5\4\7\5\t\6\13\7\r\b\17\t\21\n\23\13\25\f\27"+
		"\r\31\16\33\17\35\20\37\21!\22#\23%\24\'\25)\26+\2-\2/\27\61\2\63\2\65"+
		"\30\67\319\32;\33=\34?\35A\36C\37E G!I\"K#M$O%Q&S\'U(W)Y*[+],_-a.c/e\60"+
		"g\61i\62k\63m\64o\65q\66s\67u8w9y:{;}<\177=\u0081>\u0083\2\u0085\2\u0087"+
		"\2\u0089\2\u008b\2\u008d?\u008f@\u0091A\u0093B\3\2\22\3\2\62;\b\2%&C\\"+
		"aac|\u0393\u03ab\u03b3\u03cb\n\2%&\60\60\62;C\\aac|\u0393\u03ab\u03b3"+
		"\u03cb\t\2%&\62;C\\aac|\u0393\u03ab\u03b3\u03cb\4\2GGgg\4\2--//\4\2,,"+
		"\u00d9\u00d9\4\2\61\61\u00f9\u00f9\4\2##\u00ae\u00ae\4\2\u2229\u2229\u22c2"+
		"\u22c2\4\2\u222a\u222a\u22c3\u22c3\t\2))^^ddhhppttvv\5\2\62;CHch\6\2\f"+
		"\f\17\17))^^\4\2\f\f\17\17\5\2\13\f\16\17\"\"\2\u0233\2\3\3\2\2\2\2\5"+
		"\3\2\2\2\2\7\3\2\2\2\2\t\3\2\2\2\2\13\3\2\2\2\2\r\3\2\2\2\2\17\3\2\2\2"+
		"\2\21\3\2\2\2\2\23\3\2\2\2\2\25\3\2\2\2\2\27\3\2\2\2\2\31\3\2\2\2\2\33"+
		"\3\2\2\2\2\35\3\2\2\2\2\37\3\2\2\2\2!\3\2\2\2\2#\3\2\2\2\2%\3\2\2\2\2"+
		"\'\3\2\2\2\2)\3\2\2\2\2/\3\2\2\2\2\65\3\2\2\2\2\67\3\2\2\2\29\3\2\2\2"+
		"\2;\3\2\2\2\2=\3\2\2\2\2?\3\2\2\2\2A\3\2\2\2\2C\3\2\2\2\2E\3\2\2\2\2G"+
		"\3\2\2\2\2I\3\2\2\2\2K\3\2\2\2\2M\3\2\2\2\2O\3\2\2\2\2Q\3\2\2\2\2S\3\2"+
		"\2\2\2U\3\2\2\2\2W\3\2\2\2\2Y\3\2\2\2\2[\3\2\2\2\2]\3\2\2\2\2_\3\2\2\2"+
		"\2a\3\2\2\2\2c\3\2\2\2\2e\3\2\2\2\2g\3\2\2\2\2i\3\2\2\2\2k\3\2\2\2\2m"+
		"\3\2\2\2\2o\3\2\2\2\2q\3\2\2\2\2s\3\2\2\2\2u\3\2\2\2\2w\3\2\2\2\2y\3\2"+
		"\2\2\2{\3\2\2\2\2}\3\2\2\2\2\177\3\2\2\2\2\u0081\3\2\2\2\2\u008d\3\2\2"+
		"\2\2\u008f\3\2\2\2\2\u0091\3\2\2\2\2\u0093\3\2\2\2\3\u0095\3\2\2\2\5\u0097"+
		"\3\2\2\2\7\u0099\3\2\2\2\t\u009b\3\2\2\2\13\u009d\3\2\2\2\r\u009f\3\2"+
		"\2\2\17\u00a1\3\2\2\2\21\u00a3\3\2\2\2\23\u00a5\3\2\2\2\25\u00a8\3\2\2"+
		"\2\27\u00ac\3\2\2\2\31\u00b5\3\2\2\2\33\u00cb\3\2\2\2\35\u00cd\3\2\2\2"+
		"\37\u00d0\3\2\2\2!\u00d8\3\2\2\2#\u00dd\3\2\2\2%\u00e8\3\2\2\2\'\u00ea"+
		"\3\2\2\2)\u00f6\3\2\2\2+\u010a\3\2\2\2-\u010c\3\2\2\2/\u0113\3\2\2\2\61"+
		"\u011c\3\2\2\2\63\u011e\3\2\2\2\65\u0123\3\2\2\2\67\u0125\3\2\2\29\u0127"+
		"\3\2\2\2;\u0129\3\2\2\2=\u012c\3\2\2\2?\u012e\3\2\2\2A\u0131\3\2\2\2C"+
		"\u0133\3\2\2\2E\u0135\3\2\2\2G\u0137\3\2\2\2I\u013e\3\2\2\2K\u0145\3\2"+
		"\2\2M\u014a\3\2\2\2O\u014f\3\2\2\2Q\u0154\3\2\2\2S\u0156\3\2\2\2U\u0158"+
		"\3\2\2\2W\u015d\3\2\2\2Y\u0162\3\2\2\2[\u0164\3\2\2\2]\u0166\3\2\2\2_"+
		"\u0168\3\2\2\2a\u016a\3\2\2\2c\u016c\3\2\2\2e\u016e\3\2\2\2g\u0171\3\2"+
		"\2\2i\u0173\3\2\2\2k\u0175\3\2\2\2m\u0179\3\2\2\2o\u0180\3\2\2\2q\u0187"+
		"\3\2\2\2s\u018e\3\2\2\2u\u0193\3\2\2\2w\u019b\3\2\2\2y\u01a3\3\2\2\2{"+
		"\u01aa\3\2\2\2}\u01b2\3\2\2\2\177\u01b7\3\2\2\2\u0081\u01bf\3\2\2\2\u0083"+
		"\u01c5\3\2\2\2\u0085\u01c7\3\2\2\2\u0087\u01d2\3\2\2\2\u0089\u01d5\3\2"+
		"\2\2\u008b\u01db\3\2\2\2\u008d\u01dd\3\2\2\2\u008f\u01e7\3\2\2\2\u0091"+
		"\u01ed\3\2\2\2\u0093\u01fb\3\2\2\2\u0095\u0096\7}\2\2\u0096\4\3\2\2\2"+
		"\u0097\u0098\7.\2\2\u0098\6\3\2\2\2\u0099\u009a\7\177\2\2\u009a\b\3\2"+
		"\2\2\u009b\u009c\7<\2\2\u009c\n\3\2\2\2\u009d\u009e\7+\2\2\u009e\f\3\2"+
		"\2\2\u009f\u00a0\7\60\2\2\u00a0\16\3\2\2\2\u00a1\u00a2\7*\2\2\u00a2\20"+
		"\3\2\2\2\u00a3\u00a4\7A\2\2\u00a4\22\3\2\2\2\u00a5\u00a6\7=\2\2\u00a6"+
		"\24\3\2\2\2\u00a7\u00a9\t\2\2\2\u00a8\u00a7\3\2\2\2\u00a9\u00aa\3\2\2"+
		"\2\u00aa\u00a8\3\2\2\2\u00aa\u00ab\3\2\2\2\u00ab\26\3\2\2\2\u00ac\u00b0"+
		"\t\3\2\2\u00ad\u00af\t\4\2\2\u00ae\u00ad\3\2\2\2\u00af\u00b2\3\2\2\2\u00b0"+
		"\u00ae\3\2\2\2\u00b0\u00b1\3\2\2\2\u00b1\30\3\2\2\2\u00b2\u00b0\3\2\2"+
		"\2\u00b3\u00b6\5!\21\2\u00b4\u00b6\5#\22\2\u00b5\u00b3\3\2\2\2\u00b5\u00b4"+
		"\3\2\2\2\u00b6\32\3\2\2\2\u00b7\u00cc\7\u2256\2\2\u00b8\u00b9\7<\2\2\u00b9"+
		"\u00cc\7?\2\2\u00ba\u00cc\7\u2257\2\2\u00bb\u00bc\7?\2\2\u00bc\u00cc\7"+
		"<\2\2\u00bd\u00be\7-\2\2\u00be\u00cc\7?\2\2\u00bf\u00c0\7/\2\2\u00c0\u00cc"+
		"\7?\2\2\u00c1\u00c2\5\67\34\2\u00c2\u00c3\7?\2\2\u00c3\u00cc\3\2\2\2\u00c4"+
		"\u00c5\59\35\2\u00c5\u00c6\7?\2\2\u00c6\u00cc\3\2\2\2\u00c7\u00c8\7\'"+
		"\2\2\u00c8\u00cc\7?\2\2\u00c9\u00ca\7`\2\2\u00ca\u00cc\7?\2\2\u00cb\u00b7"+
		"\3\2\2\2\u00cb\u00b8\3\2\2\2\u00cb\u00ba\3\2\2\2\u00cb\u00bb\3\2\2\2\u00cb"+
		"\u00bd\3\2\2\2\u00cb\u00bf\3\2\2\2\u00cb\u00c1\3\2\2\2\u00cb\u00c4\3\2"+
		"\2\2\u00cb\u00c7\3\2\2\2\u00cb\u00c9\3\2\2\2\u00cc\34\3\2\2\2\u00cd\u00ce"+
		"\5-\27\2\u00ce\u00cf\7*\2\2\u00cf\36\3\2\2\2\u00d0\u00d6\7B\2\2\u00d1"+
		"\u00d7\5+\26\2\u00d2\u00d7\5-\27\2\u00d3\u00d4\5\35\17\2\u00d4\u00d5\7"+
		"+\2\2\u00d5\u00d7\3\2\2\2\u00d6\u00d1\3\2\2\2\u00d6\u00d2\3\2\2\2\u00d6"+
		"\u00d3\3\2\2\2\u00d7 \3\2\2\2\u00d8\u00d9\7v\2\2\u00d9\u00da\7t\2\2\u00da"+
		"\u00db\7w\2\2\u00db\u00dc\7g\2\2\u00dc\"\3\2\2\2\u00dd\u00de\7h\2\2\u00de"+
		"\u00df\7c\2\2\u00df\u00e0\7n\2\2\u00e0\u00e1\7u\2\2\u00e1\u00e2\7g\2\2"+
		"\u00e2$\3\2\2\2\u00e3\u00e4\7p\2\2\u00e4\u00e5\7w\2\2\u00e5\u00e6\7n\2"+
		"\2\u00e6\u00e9\7n\2\2\u00e7\u00e9\7\u2207\2\2\u00e8\u00e3\3\2\2\2\u00e8"+
		"\u00e7\3\2\2\2\u00e9&\3\2\2\2\u00ea\u00ec\7)\2\2\u00eb\u00ed\5\u0089E"+
		"\2\u00ec\u00eb\3\2\2\2\u00ec\u00ed\3\2\2\2\u00ed\u00ee\3\2\2\2\u00ee\u00ef"+
		"\7)\2\2\u00ef(\3\2\2\2\u00f0\u00f1\5\25\13\2\u00f1\u00f2\7\60\2\2\u00f2"+
		"\u00f3\5\25\13\2\u00f3\u00f7\3\2\2\2\u00f4\u00f5\7\60\2\2\u00f5\u00f7"+
		"\5\25\13\2\u00f6\u00f0\3\2\2\2\u00f6\u00f4\3\2\2\2\u00f7*\3\2\2\2\u00f8"+
		"\u010b\5\67\34\2\u00f9\u010b\59\35\2\u00fa\u010b\5=\37\2\u00fb\u010b\5"+
		"A!\2\u00fc\u010b\5C\"\2\u00fd\u010b\5I%\2\u00fe\u010b\5E#\2\u00ff\u010b"+
		"\5U+\2\u0100\u010b\5I%\2\u0101\u010b\5K&\2\u0102\u010b\5M\'\2\u0103\u010b"+
		"\5O(\2\u0104\u010b\5W,\2\u0105\u010b\5Y-\2\u0106\u010b\5]/\2\u0107\u010b"+
		"\5_\60\2\u0108\u010b\5S*\2\u0109\u010b\5Q)\2\u010a\u00f8\3\2\2\2\u010a"+
		"\u00f9\3\2\2\2\u010a\u00fa\3\2\2\2\u010a\u00fb\3\2\2\2\u010a\u00fc\3\2"+
		"\2\2\u010a\u00fd\3\2\2\2\u010a\u00fe\3\2\2\2\u010a\u00ff\3\2\2\2\u010a"+
		"\u0100\3\2\2\2\u010a\u0101\3\2\2\2\u010a\u0102\3\2\2\2\u010a\u0103\3\2"+
		"\2\2\u010a\u0104\3\2\2\2\u010a\u0105\3\2\2\2\u010a\u0106\3\2\2\2\u010a"+
		"\u0107\3\2\2\2\u010a\u0108\3\2\2\2\u010a\u0109\3\2\2\2\u010b,\3\2\2\2"+
		"\u010c\u0110\t\3\2\2\u010d\u010f\t\5\2\2\u010e\u010d\3\2\2\2\u010f\u0112"+
		"\3\2\2\2\u0110\u010e\3\2\2\2\u0110\u0111\3\2\2\2\u0111.\3\2\2\2\u0112"+
		"\u0110\3\2\2\2\u0113\u011a\5)\25\2\u0114\u0116\5\61\31\2\u0115\u0117\5"+
		"\63\32\2\u0116\u0115\3\2\2\2\u0116\u0117\3\2\2\2\u0117\u0118\3\2\2\2\u0118"+
		"\u0119\5\25\13\2\u0119\u011b\3\2\2\2\u011a\u0114\3\2\2\2\u011a\u011b\3"+
		"\2\2\2\u011b\60\3\2\2\2\u011c\u011d\t\6\2\2\u011d\62\3\2\2\2\u011e\u011f"+
		"\t\7\2\2\u011f\64\3\2\2\2\u0120\u0121\7/\2\2\u0121\u0124\7@\2\2\u0122"+
		"\u0124\7\u2194\2\2\u0123\u0120\3\2\2\2\u0123\u0122\3\2\2\2\u0124\66\3"+
		"\2\2\2\u0125\u0126\t\b\2\2\u01268\3\2\2\2\u0127\u0128\t\t\2\2\u0128:\3"+
		"\2\2\2\u0129\u012a\7-\2\2\u012a\u012b\7-\2\2\u012b<\3\2\2\2\u012c\u012d"+
		"\7-\2\2\u012d>\3\2\2\2\u012e\u012f\7/\2\2\u012f\u0130\7/\2\2\u0130@\3"+
		"\2\2\2\u0131\u0132\7/\2\2\u0132B\3\2\2\2\u0133\u0134\7>\2\2\u0134D\3\2"+
		"\2\2\u0135\u0136\7@\2\2\u0136F\3\2\2\2\u0137\u0138\7?\2\2\u0138H\3\2\2"+
		"\2\u0139\u013a\7>\2\2\u013a\u013f\7?\2\2\u013b\u013f\7\u2266\2\2\u013c"+
		"\u013d\7?\2\2\u013d\u013f\7>\2\2\u013e\u0139\3\2\2\2\u013e\u013b\3\2\2"+
		"\2\u013e\u013c\3\2\2\2\u013fJ\3\2\2\2\u0140\u0141\7@\2\2\u0141\u0146\7"+
		"?\2\2\u0142\u0146\7\u2267\2\2\u0143\u0144\7?\2\2\u0144\u0146\7@\2\2\u0145"+
		"\u0140\3\2\2\2\u0145\u0142\3\2\2\2\u0145\u0143\3\2\2\2\u0146L\3\2\2\2"+
		"\u0147\u0148\7?\2\2\u0148\u014b\7?\2\2\u0149\u014b\7\u2263\2\2\u014a\u0147"+
		"\3\2\2\2\u014a\u0149\3\2\2\2\u014bN\3\2\2\2\u014c\u014d\7#\2\2\u014d\u0150"+
		"\7?\2\2\u014e\u0150\7\u2262\2\2\u014f\u014c\3\2\2\2\u014f\u014e\3\2\2"+
		"\2\u0150P\3\2\2\2\u0151\u0152\7?\2\2\u0152\u0155\7\u0080\2\2\u0153\u0155"+
		"\7\u224a\2\2\u0154\u0151\3\2\2\2\u0154\u0153\3\2\2\2\u0155R\3\2\2\2\u0156"+
		"\u0157\t\n\2\2\u0157T\3\2\2\2\u0158\u0159\7`\2\2\u0159V\3\2\2\2\u015a"+
		"\u015b\7(\2\2\u015b\u015e\7(\2\2\u015c\u015e\t\13\2\2\u015d\u015a\3\2"+
		"\2\2\u015d\u015c\3\2\2\2\u015eX\3\2\2\2\u015f\u0160\7~\2\2\u0160\u0163"+
		"\7~\2\2\u0161\u0163\t\f\2\2\u0162\u015f\3\2\2\2\u0162\u0161\3\2\2\2\u0163"+
		"Z\3\2\2\2\u0164\u0165\7b\2\2\u0165\\\3\2\2\2\u0166\u0167\7\'\2\2\u0167"+
		"^\3\2\2\2\u0168\u0169\7\u0080\2\2\u0169`\3\2\2\2\u016a\u016b\7^\2\2\u016b"+
		"b\3\2\2\2\u016c\u016d\7~\2\2\u016dd\3\2\2\2\u016e\u016f\7\u0080\2\2\u016f"+
		"\u0170\7~\2\2\u0170f\3\2\2\2\u0171\u0172\7_\2\2\u0172h\3\2\2\2\u0173\u0174"+
		"\7]\2\2\u0174j\3\2\2\2\u0175\u0176\7k\2\2\u0176\u0177\7h\2\2\u0177\u0178"+
		"\7]\2\2\u0178l\3\2\2\2\u0179\u017a\7_\2\2\u017a\u017b\7v\2\2\u017b\u017c"+
		"\7j\2\2\u017c\u017d\7g\2\2\u017d\u017e\7p\2\2\u017e\u017f\7]\2\2\u017f"+
		"n\3\2\2\2\u0180\u0181\7_\2\2\u0181\u0182\7g\2\2\u0182\u0183\7n\2\2\u0183"+
		"\u0184\7u\2\2\u0184\u0185\7g\2\2\u0185\u0186\7]\2\2\u0186p\3\2\2\2\u0187"+
		"\u0188\7y\2\2\u0188\u0189\7j\2\2\u0189\u018a\7k\2\2\u018a\u018b\7n\2\2"+
		"\u018b\u018c\7g\2\2\u018c\u018d\7]\2\2\u018dr\3\2\2\2\u018e\u018f\7_\2"+
		"\2\u018f\u0190\7f\2\2\u0190\u0191\7q\2\2\u0191\u0192\7]\2\2\u0192t\3\2"+
		"\2\2\u0193\u0194\7u\2\2\u0194\u0195\7y\2\2\u0195\u0196\7k\2\2\u0196\u0197"+
		"\7v\2\2\u0197\u0198\7e\2\2\u0198\u0199\7j\2\2\u0199\u019a\7]\2\2\u019a"+
		"v\3\2\2\2\u019b\u019c\7f\2\2\u019c\u019d\7g\2\2\u019d\u019e\7h\2\2\u019e"+
		"\u019f\7k\2\2\u019f\u01a0\7p\2\2\u01a0\u01a1\7g\2\2\u01a1\u01a2\7]\2\2"+
		"\u01a2x\3\2\2\2\u01a3\u01a4\7_\2\2\u01a4\u01a5\7d\2\2\u01a5\u01a6\7q\2"+
		"\2\u01a6\u01a7\7f\2\2\u01a7\u01a8\7{\2\2\u01a8\u01a9\7]\2\2\u01a9z\3\2"+
		"\2\2\u01aa\u01ab\7o\2\2\u01ab\u01ac\7q\2\2\u01ac\u01ad\7f\2\2\u01ad\u01ae"+
		"\7w\2\2\u01ae\u01af\7n\2\2\u01af\u01b0\7g\2\2\u01b0\u01b1\7]\2\2\u01b1"+
		"|\3\2\2\2\u01b2\u01b3\7v\2\2\u01b3\u01b4\7t\2\2\u01b4\u01b5\7{\2\2\u01b5"+
		"\u01b6\7]\2\2\u01b6~\3\2\2\2\u01b7\u01b8\7_\2\2\u01b8\u01b9\7e\2\2\u01b9"+
		"\u01ba\7c\2\2\u01ba\u01bb\7v\2\2\u01bb\u01bc\7e\2\2\u01bc\u01bd\7j\2\2"+
		"\u01bd\u01be\7]\2\2\u01be\u0080\3\2\2\2\u01bf\u01c0\7_\2\2\u01c0\u01c1"+
		"\7]\2\2\u01c1\u0082\3\2\2\2\u01c2\u01c3\7^\2\2\u01c3\u01c6\t\r\2\2\u01c4"+
		"\u01c6\5\u0085C\2\u01c5\u01c2\3\2\2\2\u01c5\u01c4\3\2\2\2\u01c6\u0084"+
		"\3\2\2\2\u01c7\u01c9\7^\2\2\u01c8\u01ca\7w\2\2\u01c9\u01c8\3\2\2\2\u01ca"+
		"\u01cb\3\2\2\2\u01cb\u01c9\3\2\2\2\u01cb\u01cc\3\2\2\2\u01cc\u01cd\3\2"+
		"\2\2\u01cd\u01ce\5\u0087D\2\u01ce\u01cf\5\u0087D\2\u01cf\u01d0\5\u0087"+
		"D\2\u01d0\u01d1\5\u0087D\2\u01d1\u0086\3\2\2\2\u01d2\u01d3\t\16\2\2\u01d3"+
		"\u0088\3\2\2\2\u01d4\u01d6\5\u008bF\2\u01d5\u01d4\3\2\2\2\u01d6\u01d7"+
		"\3\2\2\2\u01d7\u01d5\3\2\2\2\u01d7\u01d8\3\2\2\2\u01d8\u008a\3\2\2\2\u01d9"+
		"\u01dc\n\17\2\2\u01da\u01dc\5\u0083B\2\u01db\u01d9\3\2\2\2\u01db\u01da"+
		"\3\2\2\2\u01dc\u008c\3\2\2\2\u01dd\u01de\7@\2\2\u01de\u01df\7@\2\2\u01df"+
		"\u01e3\3\2\2\2\u01e0\u01e2\n\20\2\2\u01e1\u01e0\3\2\2\2\u01e2\u01e5\3"+
		"\2\2\2\u01e3\u01e1\3\2\2\2\u01e3\u01e4\3\2\2\2\u01e4\u008e\3\2\2\2\u01e5"+
		"\u01e3\3\2\2\2\u01e6\u01e8\t\21\2\2\u01e7\u01e6\3\2\2\2\u01e8\u01e9\3"+
		"\2\2\2\u01e9\u01e7\3\2\2\2\u01e9\u01ea\3\2\2\2\u01ea\u01eb\3\2\2\2\u01eb"+
		"\u01ec\bH\2\2\u01ec\u0090\3\2\2\2\u01ed\u01ee\7\61\2\2\u01ee\u01ef\7,"+
		"\2\2\u01ef\u01f3\3\2\2\2\u01f0\u01f2\13\2\2\2\u01f1\u01f0\3\2\2\2\u01f2"+
		"\u01f5\3\2\2\2\u01f3\u01f4\3\2\2\2\u01f3\u01f1\3\2\2\2\u01f4\u01f6\3\2"+
		"\2\2\u01f5\u01f3\3\2\2\2\u01f6\u01f7\7,\2\2\u01f7\u01f8\7\61\2\2\u01f8"+
		"\u01f9\3\2\2\2\u01f9\u01fa\bI\2\2\u01fa\u0092\3\2\2\2\u01fb\u01fc\7\61"+
		"\2\2\u01fc\u01fd\7\61\2\2\u01fd\u0201\3\2\2\2\u01fe\u0200\n\20\2\2\u01ff"+
		"\u01fe\3\2\2\2\u0200\u0203\3\2\2\2\u0201\u01ff\3\2\2\2\u0201\u0202\3\2"+
		"\2\2\u0202\u0204\3\2\2\2\u0203\u0201\3\2\2\2\u0204\u0205\bJ\2\2\u0205"+
		"\u0094\3\2\2\2\37\2\u00aa\u00b0\u00b5\u00cb\u00d6\u00e8\u00ec\u00f6\u010a"+
		"\u0110\u0116\u011a\u0123\u013e\u0145\u014a\u014f\u0154\u015d\u0162\u01c5"+
		"\u01cb\u01d7\u01db\u01e3\u01e9\u01f3\u0201\3\b\2\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}