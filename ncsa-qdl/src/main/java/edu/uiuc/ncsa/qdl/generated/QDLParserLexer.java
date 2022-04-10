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
		ConstantKeywords=10, ASSERT=11, ASSERT2=12, BOOL_FALSE=13, BOOL_TRUE=14, 
		BLOCK=15, LOCAL=16, BODY=17, CATCH=18, COMPLEX_I=19, DEFINE=20, DO=21, 
		ELSE=22, IF=23, MODULE=24, Null=25, SWITCH=26, THEN=27, TRY=28, WHILE=29, 
		Integer=30, Decimal=31, SCIENTIFIC_NUMBER=32, Bool=33, STRING=34, LeftBracket=35, 
		RightBracket=36, Comma=37, Colon=38, SemiColon=39, LDoubleBracket=40, 
		RDoubleBracket=41, LambdaConnector=42, Times=43, Divide=44, PlusPlus=45, 
		Plus=46, MinusMinus=47, Minus=48, LessThan=49, GreaterThan=50, SingleEqual=51, 
		LessEquals=52, MoreEquals=53, Equals=54, NotEquals=55, RegexMatches=56, 
		LogicalNot=57, Membership=58, Exponentiation=59, And=60, Or=61, Backtick=62, 
		Percent=63, Tilde=64, Backslash=65, Hash=66, Stile=67, TildeRight=68, 
		StemDot=69, UnaryMinus=70, UnaryPlus=71, Floor=72, Ceiling=73, FunctionMarker=74, 
		ASSIGN=75, Identifier=76, FuncStart=77, F_REF=78, FDOC=79, WS=80, COMMENT=81, 
		LINE_COMMENT=82;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	private static String[] makeRuleNames() {
		return new String[] {
			"T__0", "T__1", "T__2", "T__3", "T__4", "T__5", "T__6", "T__7", "T__8", 
			"ConstantKeywords", "ASSERT", "ASSERT2", "BOOL_FALSE", "BOOL_TRUE", "BLOCK", 
			"LOCAL", "BODY", "CATCH", "COMPLEX_I", "DEFINE", "DO", "ELSE", "IF", 
			"MODULE", "Null", "SWITCH", "THEN", "TRY", "WHILE", "Integer", "Decimal", 
			"SCIENTIFIC_NUMBER", "E", "SIGN", "Bool", "STRING", "ESC", "UnicodeEscape", 
			"HexDigit", "StringCharacters", "StringCharacter", "LeftBracket", "RightBracket", 
			"Comma", "Colon", "SemiColon", "LDoubleBracket", "RDoubleBracket", "LambdaConnector", 
			"Times", "Divide", "PlusPlus", "Plus", "MinusMinus", "Minus", "LessThan", 
			"GreaterThan", "SingleEqual", "LessEquals", "MoreEquals", "Equals", "NotEquals", 
			"RegexMatches", "LogicalNot", "Membership", "Exponentiation", "And", 
			"Or", "Backtick", "Percent", "Tilde", "Backslash", "Hash", "Stile", "TildeRight", 
			"StemDot", "UnaryMinus", "UnaryPlus", "Floor", "Ceiling", "FunctionMarker", 
			"ASSIGN", "Identifier", "FuncStart", "F_REF", "AllOps", "FUNCTION_NAME", 
			"FDOC", "WS", "COMMENT", "LINE_COMMENT"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'{'", "'}'", "')'", "'('", "'\\/'", "'\u2229'", "'/\\'", "'\u222A'", 
			"'?'", null, "'assert'", "'\u22A8'", null, null, "'block'", "'local'", 
			"'body'", "'catch'", "'I'", "'define'", "'do'", "'else'", "'if'", "'module'", 
			null, "'switch'", "'then'", "'try'", "'while'", null, null, null, null, 
			null, "'['", "']'", "','", "':'", "';'", null, null, null, null, null, 
			"'++'", "'+'", "'--'", "'-'", "'<'", "'>'", "'='", null, null, null, 
			null, null, null, null, "'^'", null, null, "'`'", "'%'", "'~'", "'\\'", 
			"'#'", "'|'", null, "'.'", "'\u00AF'", "'\u207A'", "'\u230A'", "'\u2308'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, null, null, null, null, null, null, null, null, null, "ConstantKeywords", 
			"ASSERT", "ASSERT2", "BOOL_FALSE", "BOOL_TRUE", "BLOCK", "LOCAL", "BODY", 
			"CATCH", "COMPLEX_I", "DEFINE", "DO", "ELSE", "IF", "MODULE", "Null", 
			"SWITCH", "THEN", "TRY", "WHILE", "Integer", "Decimal", "SCIENTIFIC_NUMBER", 
			"Bool", "STRING", "LeftBracket", "RightBracket", "Comma", "Colon", "SemiColon", 
			"LDoubleBracket", "RDoubleBracket", "LambdaConnector", "Times", "Divide", 
			"PlusPlus", "Plus", "MinusMinus", "Minus", "LessThan", "GreaterThan", 
			"SingleEqual", "LessEquals", "MoreEquals", "Equals", "NotEquals", "RegexMatches", 
			"LogicalNot", "Membership", "Exponentiation", "And", "Or", "Backtick", 
			"Percent", "Tilde", "Backslash", "Hash", "Stile", "TildeRight", "StemDot", 
			"UnaryMinus", "UnaryPlus", "Floor", "Ceiling", "FunctionMarker", "ASSIGN", 
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
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2T\u025d\b\1\4\2\t"+
		"\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13"+
		"\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31\t\31"+
		"\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4 \t \4!"+
		"\t!\4\"\t\"\4#\t#\4$\t$\4%\t%\4&\t&\4\'\t\'\4(\t(\4)\t)\4*\t*\4+\t+\4"+
		",\t,\4-\t-\4.\t.\4/\t/\4\60\t\60\4\61\t\61\4\62\t\62\4\63\t\63\4\64\t"+
		"\64\4\65\t\65\4\66\t\66\4\67\t\67\48\t8\49\t9\4:\t:\4;\t;\4<\t<\4=\t="+
		"\4>\t>\4?\t?\4@\t@\4A\tA\4B\tB\4C\tC\4D\tD\4E\tE\4F\tF\4G\tG\4H\tH\4I"+
		"\tI\4J\tJ\4K\tK\4L\tL\4M\tM\4N\tN\4O\tO\4P\tP\4Q\tQ\4R\tR\4S\tS\4T\tT"+
		"\4U\tU\4V\tV\4W\tW\4X\tX\4Y\tY\4Z\tZ\4[\t[\4\\\t\\\3\2\3\2\3\3\3\3\3\4"+
		"\3\4\3\5\3\5\3\6\3\6\3\6\3\7\3\7\3\b\3\b\3\b\3\t\3\t\3\n\3\n\3\13\3\13"+
		"\3\13\3\13\5\13\u00d2\n\13\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\r\3\r\3\16\3"+
		"\16\3\16\3\16\3\16\3\16\5\16\u00e3\n\16\3\17\3\17\3\17\3\17\3\17\5\17"+
		"\u00ea\n\17\3\20\3\20\3\20\3\20\3\20\3\20\3\21\3\21\3\21\3\21\3\21\3\21"+
		"\3\22\3\22\3\22\3\22\3\22\3\23\3\23\3\23\3\23\3\23\3\23\3\24\3\24\3\25"+
		"\3\25\3\25\3\25\3\25\3\25\3\25\3\26\3\26\3\26\3\27\3\27\3\27\3\27\3\27"+
		"\3\30\3\30\3\30\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3\32\3\32\3\32\3\32"+
		"\3\32\5\32\u0123\n\32\3\33\3\33\3\33\3\33\3\33\3\33\3\33\3\34\3\34\3\34"+
		"\3\34\3\34\3\35\3\35\3\35\3\35\3\36\3\36\3\36\3\36\3\36\3\36\3\37\6\37"+
		"\u013c\n\37\r\37\16\37\u013d\3 \3 \3 \3 \3!\3!\3!\5!\u0147\n!\3!\3!\5"+
		"!\u014b\n!\3\"\3\"\3#\3#\3#\3#\5#\u0153\n#\3$\3$\5$\u0157\n$\3%\3%\5%"+
		"\u015b\n%\3%\3%\3&\3&\3&\5&\u0162\n&\3\'\3\'\6\'\u0166\n\'\r\'\16\'\u0167"+
		"\3\'\3\'\3\'\3\'\3\'\3(\3(\3)\6)\u0172\n)\r)\16)\u0173\3*\3*\5*\u0178"+
		"\n*\3+\3+\3,\3,\3-\3-\3.\3.\3/\3/\3\60\3\60\3\60\5\60\u0187\n\60\3\61"+
		"\3\61\3\61\5\61\u018c\n\61\3\62\3\62\3\62\5\62\u0191\n\62\3\63\3\63\3"+
		"\64\3\64\3\65\3\65\3\65\3\66\3\66\3\67\3\67\3\67\38\38\39\39\3:\3:\3;"+
		"\3;\3<\3<\3<\3<\3<\5<\u01ac\n<\3=\3=\3=\3=\3=\5=\u01b3\n=\3>\3>\3>\5>"+
		"\u01b8\n>\3?\3?\3?\5?\u01bd\n?\3@\3@\3@\5@\u01c2\n@\3A\3A\3B\3B\3C\3C"+
		"\3D\3D\3D\5D\u01cd\nD\3E\3E\3E\5E\u01d2\nE\3F\3F\3G\3G\3H\3H\3I\3I\3J"+
		"\3J\3K\3K\3L\3L\3L\5L\u01e3\nL\3M\3M\3N\3N\3O\3O\3P\3P\3Q\3Q\3R\3R\3S"+
		"\3S\3S\3S\3S\3S\3S\3S\3S\3S\3S\3S\3S\3S\3S\3S\3S\3S\3S\3S\5S\u0205\nS"+
		"\3T\3T\7T\u0209\nT\fT\16T\u020c\13T\3U\3U\3U\3V\3V\3V\3V\3V\3V\5V\u0217"+
		"\nV\3W\3W\3W\3W\3W\3W\3W\3W\3W\3W\3W\3W\3W\3W\3W\3W\3W\3W\3W\5W\u022c"+
		"\nW\3X\3X\7X\u0230\nX\fX\16X\u0233\13X\3Y\3Y\3Y\3Y\7Y\u0239\nY\fY\16Y"+
		"\u023c\13Y\3Z\6Z\u023f\nZ\rZ\16Z\u0240\3Z\3Z\3[\3[\3[\3[\7[\u0249\n[\f"+
		"[\16[\u024c\13[\3[\3[\3[\3[\3[\3\\\3\\\3\\\3\\\7\\\u0257\n\\\f\\\16\\"+
		"\u025a\13\\\3\\\3\\\3\u024a\2]\3\3\5\4\7\5\t\6\13\7\r\b\17\t\21\n\23\13"+
		"\25\f\27\r\31\16\33\17\35\20\37\21!\22#\23%\24\'\25)\26+\27-\30/\31\61"+
		"\32\63\33\65\34\67\359\36;\37= ?!A\"C\2E\2G#I$K\2M\2O\2Q\2S\2U%W&Y\'["+
		"(])_*a+c,e-g.i/k\60m\61o\62q\63s\64u\65w\66y\67{8}9\177:\u0081;\u0083"+
		"<\u0085=\u0087>\u0089?\u008b@\u008dA\u008fB\u0091C\u0093D\u0095E\u0097"+
		"F\u0099G\u009bH\u009dI\u009fJ\u00a1K\u00a3L\u00a5M\u00a7N\u00a9O\u00ab"+
		"P\u00ad\2\u00af\2\u00b1Q\u00b3R\u00b5S\u00b7T\3\2\21\3\2\62;\4\2GGgg\t"+
		"\2))^^ddhhppttvv\5\2\62;CHch\6\2\f\f\17\17))^^\4\2,,\u00d9\u00d9\4\2\61"+
		"\61\u00f9\u00f9\4\2##\u00ae\u00ae\4\2\u2229\u2229\u22c2\u22c2\4\2\u222a"+
		"\u222a\u22c3\u22c3\4\2BB\u2299\u2299\13\2&&C\\aac|\u0393\u03ab\u03b3\u03cb"+
		"\u03d3\u03d3\u03d8\u03d8\u03f2\u03f3\n\2&&\62;C\\aac|\u0393\u03ab\u03b3"+
		"\u03cb\u03d3\u03d3\4\2\f\f\17\17\5\2\13\f\16\17\"\"\2\u0295\2\3\3\2\2"+
		"\2\2\5\3\2\2\2\2\7\3\2\2\2\2\t\3\2\2\2\2\13\3\2\2\2\2\r\3\2\2\2\2\17\3"+
		"\2\2\2\2\21\3\2\2\2\2\23\3\2\2\2\2\25\3\2\2\2\2\27\3\2\2\2\2\31\3\2\2"+
		"\2\2\33\3\2\2\2\2\35\3\2\2\2\2\37\3\2\2\2\2!\3\2\2\2\2#\3\2\2\2\2%\3\2"+
		"\2\2\2\'\3\2\2\2\2)\3\2\2\2\2+\3\2\2\2\2-\3\2\2\2\2/\3\2\2\2\2\61\3\2"+
		"\2\2\2\63\3\2\2\2\2\65\3\2\2\2\2\67\3\2\2\2\29\3\2\2\2\2;\3\2\2\2\2=\3"+
		"\2\2\2\2?\3\2\2\2\2A\3\2\2\2\2G\3\2\2\2\2I\3\2\2\2\2U\3\2\2\2\2W\3\2\2"+
		"\2\2Y\3\2\2\2\2[\3\2\2\2\2]\3\2\2\2\2_\3\2\2\2\2a\3\2\2\2\2c\3\2\2\2\2"+
		"e\3\2\2\2\2g\3\2\2\2\2i\3\2\2\2\2k\3\2\2\2\2m\3\2\2\2\2o\3\2\2\2\2q\3"+
		"\2\2\2\2s\3\2\2\2\2u\3\2\2\2\2w\3\2\2\2\2y\3\2\2\2\2{\3\2\2\2\2}\3\2\2"+
		"\2\2\177\3\2\2\2\2\u0081\3\2\2\2\2\u0083\3\2\2\2\2\u0085\3\2\2\2\2\u0087"+
		"\3\2\2\2\2\u0089\3\2\2\2\2\u008b\3\2\2\2\2\u008d\3\2\2\2\2\u008f\3\2\2"+
		"\2\2\u0091\3\2\2\2\2\u0093\3\2\2\2\2\u0095\3\2\2\2\2\u0097\3\2\2\2\2\u0099"+
		"\3\2\2\2\2\u009b\3\2\2\2\2\u009d\3\2\2\2\2\u009f\3\2\2\2\2\u00a1\3\2\2"+
		"\2\2\u00a3\3\2\2\2\2\u00a5\3\2\2\2\2\u00a7\3\2\2\2\2\u00a9\3\2\2\2\2\u00ab"+
		"\3\2\2\2\2\u00b1\3\2\2\2\2\u00b3\3\2\2\2\2\u00b5\3\2\2\2\2\u00b7\3\2\2"+
		"\2\3\u00b9\3\2\2\2\5\u00bb\3\2\2\2\7\u00bd\3\2\2\2\t\u00bf\3\2\2\2\13"+
		"\u00c1\3\2\2\2\r\u00c4\3\2\2\2\17\u00c6\3\2\2\2\21\u00c9\3\2\2\2\23\u00cb"+
		"\3\2\2\2\25\u00d1\3\2\2\2\27\u00d3\3\2\2\2\31\u00da\3\2\2\2\33\u00e2\3"+
		"\2\2\2\35\u00e9\3\2\2\2\37\u00eb\3\2\2\2!\u00f1\3\2\2\2#\u00f7\3\2\2\2"+
		"%\u00fc\3\2\2\2\'\u0102\3\2\2\2)\u0104\3\2\2\2+\u010b\3\2\2\2-\u010e\3"+
		"\2\2\2/\u0113\3\2\2\2\61\u0116\3\2\2\2\63\u0122\3\2\2\2\65\u0124\3\2\2"+
		"\2\67\u012b\3\2\2\29\u0130\3\2\2\2;\u0134\3\2\2\2=\u013b\3\2\2\2?\u013f"+
		"\3\2\2\2A\u0143\3\2\2\2C\u014c\3\2\2\2E\u0152\3\2\2\2G\u0156\3\2\2\2I"+
		"\u0158\3\2\2\2K\u0161\3\2\2\2M\u0163\3\2\2\2O\u016e\3\2\2\2Q\u0171\3\2"+
		"\2\2S\u0177\3\2\2\2U\u0179\3\2\2\2W\u017b\3\2\2\2Y\u017d\3\2\2\2[\u017f"+
		"\3\2\2\2]\u0181\3\2\2\2_\u0186\3\2\2\2a\u018b\3\2\2\2c\u0190\3\2\2\2e"+
		"\u0192\3\2\2\2g\u0194\3\2\2\2i\u0196\3\2\2\2k\u0199\3\2\2\2m\u019b\3\2"+
		"\2\2o\u019e\3\2\2\2q\u01a0\3\2\2\2s\u01a2\3\2\2\2u\u01a4\3\2\2\2w\u01ab"+
		"\3\2\2\2y\u01b2\3\2\2\2{\u01b7\3\2\2\2}\u01bc\3\2\2\2\177\u01c1\3\2\2"+
		"\2\u0081\u01c3\3\2\2\2\u0083\u01c5\3\2\2\2\u0085\u01c7\3\2\2\2\u0087\u01cc"+
		"\3\2\2\2\u0089\u01d1\3\2\2\2\u008b\u01d3\3\2\2\2\u008d\u01d5\3\2\2\2\u008f"+
		"\u01d7\3\2\2\2\u0091\u01d9\3\2\2\2\u0093\u01db\3\2\2\2\u0095\u01dd\3\2"+
		"\2\2\u0097\u01e2\3\2\2\2\u0099\u01e4\3\2\2\2\u009b\u01e6\3\2\2\2\u009d"+
		"\u01e8\3\2\2\2\u009f\u01ea\3\2\2\2\u00a1\u01ec\3\2\2\2\u00a3\u01ee\3\2"+
		"\2\2\u00a5\u0204\3\2\2\2\u00a7\u0206\3\2\2\2\u00a9\u020d\3\2\2\2\u00ab"+
		"\u0210\3\2\2\2\u00ad\u022b\3\2\2\2\u00af\u022d\3\2\2\2\u00b1\u0234\3\2"+
		"\2\2\u00b3\u023e\3\2\2\2\u00b5\u0244\3\2\2\2\u00b7\u0252\3\2\2\2\u00b9"+
		"\u00ba\7}\2\2\u00ba\4\3\2\2\2\u00bb\u00bc\7\177\2\2\u00bc\6\3\2\2\2\u00bd"+
		"\u00be\7+\2\2\u00be\b\3\2\2\2\u00bf\u00c0\7*\2\2\u00c0\n\3\2\2\2\u00c1"+
		"\u00c2\7^\2\2\u00c2\u00c3\7\61\2\2\u00c3\f\3\2\2\2\u00c4\u00c5\7\u222b"+
		"\2\2\u00c5\16\3\2\2\2\u00c6\u00c7\7\61\2\2\u00c7\u00c8\7^\2\2\u00c8\20"+
		"\3\2\2\2\u00c9\u00ca\7\u222c\2\2\u00ca\22\3\2\2\2\u00cb\u00cc\7A\2\2\u00cc"+
		"\24\3\2\2\2\u00cd\u00d2\5\35\17\2\u00ce\u00d2\5\33\16\2\u00cf\u00d2\5"+
		"\63\32\2\u00d0\u00d2\5\'\24\2\u00d1\u00cd\3\2\2\2\u00d1\u00ce\3\2\2\2"+
		"\u00d1\u00cf\3\2\2\2\u00d1\u00d0\3\2\2\2\u00d2\26\3\2\2\2\u00d3\u00d4"+
		"\7c\2\2\u00d4\u00d5\7u\2\2\u00d5\u00d6\7u\2\2\u00d6\u00d7\7g\2\2\u00d7"+
		"\u00d8\7t\2\2\u00d8\u00d9\7v\2\2\u00d9\30\3\2\2\2\u00da\u00db\7\u22aa"+
		"\2\2\u00db\32\3\2\2\2\u00dc\u00dd\7h\2\2\u00dd\u00de\7c\2\2\u00de\u00df"+
		"\7n\2\2\u00df\u00e0\7u\2\2\u00e0\u00e3\7g\2\2\u00e1\u00e3\7\u22a7\2\2"+
		"\u00e2\u00dc\3\2\2\2\u00e2\u00e1\3\2\2\2\u00e3\34\3\2\2\2\u00e4\u00e5"+
		"\7v\2\2\u00e5\u00e6\7t\2\2\u00e6\u00e7\7w\2\2\u00e7\u00ea\7g\2\2\u00e8"+
		"\u00ea\7\u22a6\2\2\u00e9\u00e4\3\2\2\2\u00e9\u00e8\3\2\2\2\u00ea\36\3"+
		"\2\2\2\u00eb\u00ec\7d\2\2\u00ec\u00ed\7n\2\2\u00ed\u00ee\7q\2\2\u00ee"+
		"\u00ef\7e\2\2\u00ef\u00f0\7m\2\2\u00f0 \3\2\2\2\u00f1\u00f2\7n\2\2\u00f2"+
		"\u00f3\7q\2\2\u00f3\u00f4\7e\2\2\u00f4\u00f5\7c\2\2\u00f5\u00f6\7n\2\2"+
		"\u00f6\"\3\2\2\2\u00f7\u00f8\7d\2\2\u00f8\u00f9\7q\2\2\u00f9\u00fa\7f"+
		"\2\2\u00fa\u00fb\7{\2\2\u00fb$\3\2\2\2\u00fc\u00fd\7e\2\2\u00fd\u00fe"+
		"\7c\2\2\u00fe\u00ff\7v\2\2\u00ff\u0100\7e\2\2\u0100\u0101\7j\2\2\u0101"+
		"&\3\2\2\2\u0102\u0103\7K\2\2\u0103(\3\2\2\2\u0104\u0105\7f\2\2\u0105\u0106"+
		"\7g\2\2\u0106\u0107\7h\2\2\u0107\u0108\7k\2\2\u0108\u0109\7p\2\2\u0109"+
		"\u010a\7g\2\2\u010a*\3\2\2\2\u010b\u010c\7f\2\2\u010c\u010d\7q\2\2\u010d"+
		",\3\2\2\2\u010e\u010f\7g\2\2\u010f\u0110\7n\2\2\u0110\u0111\7u\2\2\u0111"+
		"\u0112\7g\2\2\u0112.\3\2\2\2\u0113\u0114\7k\2\2\u0114\u0115\7h\2\2\u0115"+
		"\60\3\2\2\2\u0116\u0117\7o\2\2\u0117\u0118\7q\2\2\u0118\u0119\7f\2\2\u0119"+
		"\u011a\7w\2\2\u011a\u011b\7n\2\2\u011b\u011c\7g\2\2\u011c\62\3\2\2\2\u011d"+
		"\u011e\7p\2\2\u011e\u011f\7w\2\2\u011f\u0120\7n\2\2\u0120\u0123\7n\2\2"+
		"\u0121\u0123\7\u2207\2\2\u0122\u011d\3\2\2\2\u0122\u0121\3\2\2\2\u0123"+
		"\64\3\2\2\2\u0124\u0125\7u\2\2\u0125\u0126\7y\2\2\u0126\u0127\7k\2\2\u0127"+
		"\u0128\7v\2\2\u0128\u0129\7e\2\2\u0129\u012a\7j\2\2\u012a\66\3\2\2\2\u012b"+
		"\u012c\7v\2\2\u012c\u012d\7j\2\2\u012d\u012e\7g\2\2\u012e\u012f\7p\2\2"+
		"\u012f8\3\2\2\2\u0130\u0131\7v\2\2\u0131\u0132\7t\2\2\u0132\u0133\7{\2"+
		"\2\u0133:\3\2\2\2\u0134\u0135\7y\2\2\u0135\u0136\7j\2\2\u0136\u0137\7"+
		"k\2\2\u0137\u0138\7n\2\2\u0138\u0139\7g\2\2\u0139<\3\2\2\2\u013a\u013c"+
		"\t\2\2\2\u013b\u013a\3\2\2\2\u013c\u013d\3\2\2\2\u013d\u013b\3\2\2\2\u013d"+
		"\u013e\3\2\2\2\u013e>\3\2\2\2\u013f\u0140\5=\37\2\u0140\u0141\7\60\2\2"+
		"\u0141\u0142\5=\37\2\u0142@\3\2\2\2\u0143\u014a\5? \2\u0144\u0146\5C\""+
		"\2\u0145\u0147\5E#\2\u0146\u0145\3\2\2\2\u0146\u0147\3\2\2\2\u0147\u0148"+
		"\3\2\2\2\u0148\u0149\5=\37\2\u0149\u014b\3\2\2\2\u014a\u0144\3\2\2\2\u014a"+
		"\u014b\3\2\2\2\u014bB\3\2\2\2\u014c\u014d\t\3\2\2\u014dD\3\2\2\2\u014e"+
		"\u0153\5k\66\2\u014f\u0153\5\u009dO\2\u0150\u0153\5o8\2\u0151\u0153\5"+
		"\u009bN\2\u0152\u014e\3\2\2\2\u0152\u014f\3\2\2\2\u0152\u0150\3\2\2\2"+
		"\u0152\u0151\3\2\2\2\u0153F\3\2\2\2\u0154\u0157\5\35\17\2\u0155\u0157"+
		"\5\33\16\2\u0156\u0154\3\2\2\2\u0156\u0155\3\2\2\2\u0157H\3\2\2\2\u0158"+
		"\u015a\7)\2\2\u0159\u015b\5Q)\2\u015a\u0159\3\2\2\2\u015a\u015b\3\2\2"+
		"\2\u015b\u015c\3\2\2\2\u015c\u015d\7)\2\2\u015dJ\3\2\2\2\u015e\u015f\7"+
		"^\2\2\u015f\u0162\t\4\2\2\u0160\u0162\5M\'\2\u0161\u015e\3\2\2\2\u0161"+
		"\u0160\3\2\2\2\u0162L\3\2\2\2\u0163\u0165\7^\2\2\u0164\u0166\7w\2\2\u0165"+
		"\u0164\3\2\2\2\u0166\u0167\3\2\2\2\u0167\u0165\3\2\2\2\u0167\u0168\3\2"+
		"\2\2\u0168\u0169\3\2\2\2\u0169\u016a\5O(\2\u016a\u016b\5O(\2\u016b\u016c"+
		"\5O(\2\u016c\u016d\5O(\2\u016dN\3\2\2\2\u016e\u016f\t\5\2\2\u016fP\3\2"+
		"\2\2\u0170\u0172\5S*\2\u0171\u0170\3\2\2\2\u0172\u0173\3\2\2\2\u0173\u0171"+
		"\3\2\2\2\u0173\u0174\3\2\2\2\u0174R\3\2\2\2\u0175\u0178\n\6\2\2\u0176"+
		"\u0178\5K&\2\u0177\u0175\3\2\2\2\u0177\u0176\3\2\2\2\u0178T\3\2\2\2\u0179"+
		"\u017a\7]\2\2\u017aV\3\2\2\2\u017b\u017c\7_\2\2\u017cX\3\2\2\2\u017d\u017e"+
		"\7.\2\2\u017eZ\3\2\2\2\u017f\u0180\7<\2\2\u0180\\\3\2\2\2\u0181\u0182"+
		"\7=\2\2\u0182^\3\2\2\2\u0183\u0184\7]\2\2\u0184\u0187\7~\2\2\u0185\u0187"+
		"\7\u27e8\2\2\u0186\u0183\3\2\2\2\u0186\u0185\3\2\2\2\u0187`\3\2\2\2\u0188"+
		"\u0189\7~\2\2\u0189\u018c\7_\2\2\u018a\u018c\7\u27e9\2\2\u018b\u0188\3"+
		"\2\2\2\u018b\u018a\3\2\2\2\u018cb\3\2\2\2\u018d\u018e\7/\2\2\u018e\u0191"+
		"\7@\2\2\u018f\u0191\7\u2194\2\2\u0190\u018d\3\2\2\2\u0190\u018f\3\2\2"+
		"\2\u0191d\3\2\2\2\u0192\u0193\t\7\2\2\u0193f\3\2\2\2\u0194\u0195\t\b\2"+
		"\2\u0195h\3\2\2\2\u0196\u0197\7-\2\2\u0197\u0198\7-\2\2\u0198j\3\2\2\2"+
		"\u0199\u019a\7-\2\2\u019al\3\2\2\2\u019b\u019c\7/\2\2\u019c\u019d\7/\2"+
		"\2\u019dn\3\2\2\2\u019e\u019f\7/\2\2\u019fp\3\2\2\2\u01a0\u01a1\7>\2\2"+
		"\u01a1r\3\2\2\2\u01a2\u01a3\7@\2\2\u01a3t\3\2\2\2\u01a4\u01a5\7?\2\2\u01a5"+
		"v\3\2\2\2\u01a6\u01a7\7>\2\2\u01a7\u01ac\7?\2\2\u01a8\u01ac\7\u2266\2"+
		"\2\u01a9\u01aa\7?\2\2\u01aa\u01ac\7>\2\2\u01ab\u01a6\3\2\2\2\u01ab\u01a8"+
		"\3\2\2\2\u01ab\u01a9\3\2\2\2\u01acx\3\2\2\2\u01ad\u01ae\7@\2\2\u01ae\u01b3"+
		"\7?\2\2\u01af\u01b3\7\u2267\2\2\u01b0\u01b1\7?\2\2\u01b1\u01b3\7@\2\2"+
		"\u01b2\u01ad\3\2\2\2\u01b2\u01af\3\2\2\2\u01b2\u01b0\3\2\2\2\u01b3z\3"+
		"\2\2\2\u01b4\u01b5\7?\2\2\u01b5\u01b8\7?\2\2\u01b6\u01b8\7\u2263\2\2\u01b7"+
		"\u01b4\3\2\2\2\u01b7\u01b6\3\2\2\2\u01b8|\3\2\2\2\u01b9\u01ba\7#\2\2\u01ba"+
		"\u01bd\7?\2\2\u01bb\u01bd\7\u2262\2\2\u01bc\u01b9\3\2\2\2\u01bc\u01bb"+
		"\3\2\2\2\u01bd~\3\2\2\2\u01be\u01bf\7?\2\2\u01bf\u01c2\7\u0080\2\2\u01c0"+
		"\u01c2\7\u224a\2\2\u01c1\u01be\3\2\2\2\u01c1\u01c0\3\2\2\2\u01c2\u0080"+
		"\3\2\2\2\u01c3\u01c4\t\t\2\2\u01c4\u0082\3\2\2\2\u01c5\u01c6\4\u220a\u220b"+
		"\2\u01c6\u0084\3\2\2\2\u01c7\u01c8\7`\2\2\u01c8\u0086\3\2\2\2\u01c9\u01ca"+
		"\7(\2\2\u01ca\u01cd\7(\2\2\u01cb\u01cd\t\n\2\2\u01cc\u01c9\3\2\2\2\u01cc"+
		"\u01cb\3\2\2\2\u01cd\u0088\3\2\2\2\u01ce\u01cf\7~\2\2\u01cf\u01d2\7~\2"+
		"\2\u01d0\u01d2\t\13\2\2\u01d1\u01ce\3\2\2\2\u01d1\u01d0\3\2\2\2\u01d2"+
		"\u008a\3\2\2\2\u01d3\u01d4\7b\2\2\u01d4\u008c\3\2\2\2\u01d5\u01d6\7\'"+
		"\2\2\u01d6\u008e\3\2\2\2\u01d7\u01d8\7\u0080\2\2\u01d8\u0090\3\2\2\2\u01d9"+
		"\u01da\7^\2\2\u01da\u0092\3\2\2\2\u01db\u01dc\7%\2\2\u01dc\u0094\3\2\2"+
		"\2\u01dd\u01de\7~\2\2\u01de\u0096\3\2\2\2\u01df\u01e0\7\u0080\2\2\u01e0"+
		"\u01e3\7~\2\2\u01e1\u01e3\7\u2243\2\2\u01e2\u01df\3\2\2\2\u01e2\u01e1"+
		"\3\2\2\2\u01e3\u0098\3\2\2\2\u01e4\u01e5\7\60\2\2\u01e5\u009a\3\2\2\2"+
		"\u01e6\u01e7\7\u00b1\2\2\u01e7\u009c\3\2\2\2\u01e8\u01e9\7\u207c\2\2\u01e9"+
		"\u009e\3\2\2\2\u01ea\u01eb\7\u230c\2\2\u01eb\u00a0\3\2\2\2\u01ec\u01ed"+
		"\7\u230a\2\2\u01ed\u00a2\3\2\2\2\u01ee\u01ef\t\f\2\2\u01ef\u00a4\3\2\2"+
		"\2\u01f0\u0205\7\u2256\2\2\u01f1\u01f2\7<\2\2\u01f2\u0205\7?\2\2\u01f3"+
		"\u0205\7\u2257\2\2\u01f4\u01f5\7?\2\2\u01f5\u0205\7<\2\2\u01f6\u01f7\7"+
		"-\2\2\u01f7\u0205\7?\2\2\u01f8\u01f9\7/\2\2\u01f9\u0205\7?\2\2\u01fa\u01fb"+
		"\5e\63\2\u01fb\u01fc\7?\2\2\u01fc\u0205\3\2\2\2\u01fd\u01fe\5g\64\2\u01fe"+
		"\u01ff\7?\2\2\u01ff\u0205\3\2\2\2\u0200\u0201\7\'\2\2\u0201\u0205\7?\2"+
		"\2\u0202\u0203\7`\2\2\u0203\u0205\7?\2\2\u0204\u01f0\3\2\2\2\u0204\u01f1"+
		"\3\2\2\2\u0204\u01f3\3\2\2\2\u0204\u01f4\3\2\2\2\u0204\u01f6\3\2\2\2\u0204"+
		"\u01f8\3\2\2\2\u0204\u01fa\3\2\2\2\u0204\u01fd\3\2\2\2\u0204\u0200\3\2"+
		"\2\2\u0204\u0202\3\2\2\2\u0205\u00a6\3\2\2\2\u0206\u020a\t\r\2\2\u0207"+
		"\u0209\t\16\2\2\u0208\u0207\3\2\2\2\u0209\u020c\3\2\2\2\u020a\u0208\3"+
		"\2\2\2\u020a\u020b\3\2\2\2\u020b\u00a8\3\2\2\2\u020c\u020a\3\2\2\2\u020d"+
		"\u020e\5\u00afX\2\u020e\u020f\7*\2\2\u020f\u00aa\3\2\2\2\u0210\u0216\5"+
		"\u00a3R\2\u0211\u0217\5\u00adW\2\u0212\u0217\5\u00afX\2\u0213\u0214\5"+
		"\u00a9U\2\u0214\u0215\7+\2\2\u0215\u0217\3\2\2\2\u0216\u0211\3\2\2\2\u0216"+
		"\u0212\3\2\2\2\u0216\u0213\3\2\2\2\u0217\u00ac\3\2\2\2\u0218\u022c\5e"+
		"\63\2\u0219\u022c\5g\64\2\u021a\u022c\5k\66\2\u021b\u022c\5o8\2\u021c"+
		"\u022c\5q9\2\u021d\u022c\5w<\2\u021e\u022c\5s:\2\u021f\u022c\5\u0085C"+
		"\2\u0220\u022c\5w<\2\u0221\u022c\5y=\2\u0222\u022c\5{>\2\u0223\u022c\5"+
		"}?\2\u0224\u022c\5\u0087D\2\u0225\u022c\5\u0089E\2\u0226\u022c\5\u008d"+
		"G\2\u0227\u022c\5\u008fH\2\u0228\u022c\5\u0097L\2\u0229\u022c\5\u0081"+
		"A\2\u022a\u022c\5\177@\2\u022b\u0218\3\2\2\2\u022b\u0219\3\2\2\2\u022b"+
		"\u021a\3\2\2\2\u022b\u021b\3\2\2\2\u022b\u021c\3\2\2\2\u022b\u021d\3\2"+
		"\2\2\u022b\u021e\3\2\2\2\u022b\u021f\3\2\2\2\u022b\u0220\3\2\2\2\u022b"+
		"\u0221\3\2\2\2\u022b\u0222\3\2\2\2\u022b\u0223\3\2\2\2\u022b\u0224\3\2"+
		"\2\2\u022b\u0225\3\2\2\2\u022b\u0226\3\2\2\2\u022b\u0227\3\2\2\2\u022b"+
		"\u0228\3\2\2\2\u022b\u0229\3\2\2\2\u022b\u022a\3\2\2\2\u022c\u00ae\3\2"+
		"\2\2\u022d\u0231\t\r\2\2\u022e\u0230\t\16\2\2\u022f\u022e\3\2\2\2\u0230"+
		"\u0233\3\2\2\2\u0231\u022f\3\2\2\2\u0231\u0232\3\2\2\2\u0232\u00b0\3\2"+
		"\2\2\u0233\u0231\3\2\2\2\u0234\u0235\7@\2\2\u0235\u0236\7@\2\2\u0236\u023a"+
		"\3\2\2\2\u0237\u0239\n\17\2\2\u0238\u0237\3\2\2\2\u0239\u023c\3\2\2\2"+
		"\u023a\u0238\3\2\2\2\u023a\u023b\3\2\2\2\u023b\u00b2\3\2\2\2\u023c\u023a"+
		"\3\2\2\2\u023d\u023f\t\20\2\2\u023e\u023d\3\2\2\2\u023f\u0240\3\2\2\2"+
		"\u0240\u023e\3\2\2\2\u0240\u0241\3\2\2\2\u0241\u0242\3\2\2\2\u0242\u0243"+
		"\bZ\2\2\u0243\u00b4\3\2\2\2\u0244\u0245\7\61\2\2\u0245\u0246\7,\2\2\u0246"+
		"\u024a\3\2\2\2\u0247\u0249\13\2\2\2\u0248\u0247\3\2\2\2\u0249\u024c\3"+
		"\2\2\2\u024a\u024b\3\2\2\2\u024a\u0248\3\2\2\2\u024b\u024d\3\2\2\2\u024c"+
		"\u024a\3\2\2\2\u024d\u024e\7,\2\2\u024e\u024f\7\61\2\2\u024f\u0250\3\2"+
		"\2\2\u0250\u0251\b[\2\2\u0251\u00b6\3\2\2\2\u0252\u0253\7\61\2\2\u0253"+
		"\u0254\7\61\2\2\u0254\u0258\3\2\2\2\u0255\u0257\n\17\2\2\u0256\u0255\3"+
		"\2\2\2\u0257\u025a\3\2\2\2\u0258\u0256\3\2\2\2\u0258\u0259\3\2\2\2\u0259"+
		"\u025b\3\2\2\2\u025a\u0258\3\2\2\2\u025b\u025c\b\\\2\2\u025c\u00b8\3\2"+
		"\2\2%\2\u00d1\u00e2\u00e9\u0122\u013d\u0146\u014a\u0152\u0156\u015a\u0161"+
		"\u0167\u0173\u0177\u0186\u018b\u0190\u01ab\u01b2\u01b7\u01bc\u01c1\u01cc"+
		"\u01d1\u01e2\u0204\u020a\u0216\u022b\u0231\u023a\u0240\u024a\u0258\3\b"+
		"\2\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}