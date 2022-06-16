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
		ELSE=22, IF=23, MODULE=24, Null=25, Null_Set=26, SWITCH=27, THEN=28, TRY=29, 
		WHILE=30, Integer=31, Decimal=32, SCIENTIFIC_NUMBER=33, Bool=34, STRING=35, 
		LeftBracket=36, RightBracket=37, Comma=38, Colon=39, SemiColon=40, LDoubleBracket=41, 
		RDoubleBracket=42, LambdaConnector=43, Times=44, Divide=45, PlusPlus=46, 
		Plus=47, MinusMinus=48, Minus=49, LessThan=50, GreaterThan=51, SingleEqual=52, 
		To_Set=53, LessEquals=54, MoreEquals=55, IsA=56, Equals=57, NotEquals=58, 
		RegexMatches=59, LogicalNot=60, Membership=61, Exponentiation=62, And=63, 
		Or=64, Backtick=65, Percent=66, Tilde=67, Backslash=68, Hash=69, Stile=70, 
		TildeRight=71, StemDot=72, UnaryMinus=73, UnaryPlus=74, Floor=75, Ceiling=76, 
		FunctionMarker=77, ASSIGN=78, Identifier=79, FuncStart=80, F_REF=81, FDOC=82, 
		WS=83, COMMENT=84, LINE_COMMENT=85;
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
			"MODULE", "Null", "Null_Set", "SWITCH", "THEN", "TRY", "WHILE", "Integer", 
			"Decimal", "SCIENTIFIC_NUMBER", "E", "SIGN", "Bool", "STRING", "ESC", 
			"UnicodeEscape", "HexDigit", "StringCharacters", "StringCharacter", "LeftBracket", 
			"RightBracket", "Comma", "Colon", "SemiColon", "LDoubleBracket", "RDoubleBracket", 
			"LambdaConnector", "Times", "Divide", "PlusPlus", "Plus", "MinusMinus", 
			"Minus", "LessThan", "GreaterThan", "SingleEqual", "To_Set", "LessEquals", 
			"MoreEquals", "IsA", "Equals", "NotEquals", "RegexMatches", "LogicalNot", 
			"Membership", "Exponentiation", "And", "Or", "Backtick", "Percent", "Tilde", 
			"Backslash", "Hash", "Stile", "TildeRight", "StemDot", "UnaryMinus", 
			"UnaryPlus", "Floor", "Ceiling", "FunctionMarker", "ASSIGN", "Identifier", 
			"FuncStart", "F_REF", "AllOps", "FUNCTION_NAME", "FDOC", "WS", "COMMENT", 
			"LINE_COMMENT"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'{'", "'}'", "')'", "'('", "'\\/'", "'\u2229'", "'/\\'", "'\u222A'", 
			"'?'", null, "'assert'", "'\u22A8'", "'false'", "'true'", "'block'", 
			"'local'", "'body'", "'catch'", "'I'", "'define'", "'do'", "'else'", 
			"'if'", "'module'", "'null'", "'\u2205'", "'switch'", "'then'", "'try'", 
			"'while'", null, null, null, null, null, "'['", "']'", "','", "':'", 
			"';'", null, null, null, null, null, "'++'", "'+'", "'--'", "'-'", "'<'", 
			"'>'", "'='", null, null, null, "'<<'", null, null, null, null, null, 
			"'^'", null, null, "'`'", null, "'~'", "'\\'", "'#'", "'|'", null, "'.'", 
			"'\u00AF'", "'\u207A'", "'\u230A'", "'\u2308'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, null, null, null, null, null, null, null, null, null, "ConstantKeywords", 
			"ASSERT", "ASSERT2", "BOOL_FALSE", "BOOL_TRUE", "BLOCK", "LOCAL", "BODY", 
			"CATCH", "COMPLEX_I", "DEFINE", "DO", "ELSE", "IF", "MODULE", "Null", 
			"Null_Set", "SWITCH", "THEN", "TRY", "WHILE", "Integer", "Decimal", "SCIENTIFIC_NUMBER", 
			"Bool", "STRING", "LeftBracket", "RightBracket", "Comma", "Colon", "SemiColon", 
			"LDoubleBracket", "RDoubleBracket", "LambdaConnector", "Times", "Divide", 
			"PlusPlus", "Plus", "MinusMinus", "Minus", "LessThan", "GreaterThan", 
			"SingleEqual", "To_Set", "LessEquals", "MoreEquals", "IsA", "Equals", 
			"NotEquals", "RegexMatches", "LogicalNot", "Membership", "Exponentiation", 
			"And", "Or", "Backtick", "Percent", "Tilde", "Backslash", "Hash", "Stile", 
			"TildeRight", "StemDot", "UnaryMinus", "UnaryPlus", "Floor", "Ceiling", 
			"FunctionMarker", "ASSIGN", "Identifier", "FuncStart", "F_REF", "FDOC", 
			"WS", "COMMENT", "LINE_COMMENT"
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
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2W\u0272\b\1\4\2\t"+
		"\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13"+
		"\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31\t\31"+
		"\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4 \t \4!"+
		"\t!\4\"\t\"\4#\t#\4$\t$\4%\t%\4&\t&\4\'\t\'\4(\t(\4)\t)\4*\t*\4+\t+\4"+
		",\t,\4-\t-\4.\t.\4/\t/\4\60\t\60\4\61\t\61\4\62\t\62\4\63\t\63\4\64\t"+
		"\64\4\65\t\65\4\66\t\66\4\67\t\67\48\t8\49\t9\4:\t:\4;\t;\4<\t<\4=\t="+
		"\4>\t>\4?\t?\4@\t@\4A\tA\4B\tB\4C\tC\4D\tD\4E\tE\4F\tF\4G\tG\4H\tH\4I"+
		"\tI\4J\tJ\4K\tK\4L\tL\4M\tM\4N\tN\4O\tO\4P\tP\4Q\tQ\4R\tR\4S\tS\4T\tT"+
		"\4U\tU\4V\tV\4W\tW\4X\tX\4Y\tY\4Z\tZ\4[\t[\4\\\t\\\4]\t]\4^\t^\4_\t_\3"+
		"\2\3\2\3\3\3\3\3\4\3\4\3\5\3\5\3\6\3\6\3\6\3\7\3\7\3\b\3\b\3\b\3\t\3\t"+
		"\3\n\3\n\3\13\3\13\3\13\3\13\3\13\5\13\u00d9\n\13\3\f\3\f\3\f\3\f\3\f"+
		"\3\f\3\f\3\r\3\r\3\16\3\16\3\16\3\16\3\16\3\16\3\17\3\17\3\17\3\17\3\17"+
		"\3\20\3\20\3\20\3\20\3\20\3\20\3\21\3\21\3\21\3\21\3\21\3\21\3\22\3\22"+
		"\3\22\3\22\3\22\3\23\3\23\3\23\3\23\3\23\3\23\3\24\3\24\3\25\3\25\3\25"+
		"\3\25\3\25\3\25\3\25\3\26\3\26\3\26\3\27\3\27\3\27\3\27\3\27\3\30\3\30"+
		"\3\30\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3\32\3\32\3\32\3\32\3\32\3\33"+
		"\3\33\3\34\3\34\3\34\3\34\3\34\3\34\3\34\3\35\3\35\3\35\3\35\3\35\3\36"+
		"\3\36\3\36\3\36\3\37\3\37\3\37\3\37\3\37\3\37\3 \6 \u013f\n \r \16 \u0140"+
		"\3!\3!\3!\3!\3\"\3\"\3\"\5\"\u014a\n\"\3\"\3\"\5\"\u014e\n\"\3#\3#\3$"+
		"\3$\3$\3$\5$\u0156\n$\3%\3%\5%\u015a\n%\3&\3&\5&\u015e\n&\3&\3&\3\'\3"+
		"\'\3\'\5\'\u0165\n\'\3(\3(\6(\u0169\n(\r(\16(\u016a\3(\3(\3(\3(\3(\3)"+
		"\3)\3*\6*\u0175\n*\r*\16*\u0176\3+\3+\5+\u017b\n+\3,\3,\3-\3-\3.\3.\3"+
		"/\3/\3\60\3\60\3\61\3\61\3\61\5\61\u018a\n\61\3\62\3\62\3\62\5\62\u018f"+
		"\n\62\3\63\3\63\3\63\5\63\u0194\n\63\3\64\3\64\3\65\3\65\3\66\3\66\3\66"+
		"\3\67\3\67\38\38\38\39\39\3:\3:\3;\3;\3<\3<\3=\3=\3=\5=\u01ad\n=\3>\3"+
		">\3>\5>\u01b2\n>\3?\3?\3?\5?\u01b7\n?\3@\3@\3@\3A\3A\3A\5A\u01bf\nA\3"+
		"B\3B\3B\5B\u01c4\nB\3C\3C\3C\5C\u01c9\nC\3D\3D\3E\3E\3F\3F\3G\3G\3G\5"+
		"G\u01d4\nG\3H\3H\3H\5H\u01d9\nH\3I\3I\3J\3J\3K\3K\3L\3L\3M\3M\3N\3N\3"+
		"O\3O\3O\5O\u01ea\nO\3P\3P\3Q\3Q\3R\3R\3S\3S\3T\3T\3U\3U\3V\3V\3V\3V\3"+
		"V\3V\3V\3V\3V\3V\3V\3V\3V\3V\3V\3V\3V\3V\3V\3V\5V\u020c\nV\3W\5W\u020f"+
		"\nW\3W\3W\7W\u0213\nW\fW\16W\u0216\13W\3X\3X\3X\3Y\3Y\3Y\3Y\3Y\7Y\u0220"+
		"\nY\fY\16Y\u0223\13Y\3Y\3Y\3Y\3Y\5Y\u0229\nY\3Z\3Z\3Z\3Z\3Z\3Z\3Z\3Z\3"+
		"Z\3Z\3Z\3Z\3Z\3Z\3Z\3Z\3Z\3Z\3Z\3Z\3Z\3Z\3Z\3Z\5Z\u0243\nZ\3[\3[\7[\u0247"+
		"\n[\f[\16[\u024a\13[\3\\\3\\\7\\\u024e\n\\\f\\\16\\\u0251\13\\\3]\6]\u0254"+
		"\n]\r]\16]\u0255\3]\3]\3^\3^\3^\3^\7^\u025e\n^\f^\16^\u0261\13^\3^\3^"+
		"\3^\3^\3^\3_\3_\3_\3_\7_\u026c\n_\f_\16_\u026f\13_\3_\3_\3\u025f\2`\3"+
		"\3\5\4\7\5\t\6\13\7\r\b\17\t\21\n\23\13\25\f\27\r\31\16\33\17\35\20\37"+
		"\21!\22#\23%\24\'\25)\26+\27-\30/\31\61\32\63\33\65\34\67\359\36;\37="+
		" ?!A\"C#E\2G\2I$K%M\2O\2Q\2S\2U\2W&Y\'[(])_*a+c,e-g.i/k\60m\61o\62q\63"+
		"s\64u\65w\66y\67{8}9\177:\u0081;\u0083<\u0085=\u0087>\u0089?\u008b@\u008d"+
		"A\u008fB\u0091C\u0093D\u0095E\u0097F\u0099G\u009bH\u009dI\u009fJ\u00a1"+
		"K\u00a3L\u00a5M\u00a7N\u00a9O\u00abP\u00adQ\u00afR\u00b1S\u00b3\2\u00b5"+
		"\2\u00b7T\u00b9U\u00bbV\u00bdW\3\2\20\3\2\62;\4\2GGgg\t\2))^^ddhhpptt"+
		"vv\5\2\62;CHch\6\2\f\f\17\17))^^\4\2,,\u00d9\u00d9\4\2\61\61\u00f9\u00f9"+
		"\4\2##\u00ae\u00ae\4\2\'\'\u2208\u2208\4\2BB\u2299\u2299\13\2&&C\\aac"+
		"|\u0393\u03ab\u03b3\u03cb\u03d3\u03d3\u03d8\u03d8\u03f2\u03f3\n\2&&\62"+
		";C\\aac|\u0393\u03ab\u03b3\u03cb\u03d3\u03d3\4\2\f\f\17\17\5\2\13\f\16"+
		"\17\"\"\2\u02ae\2\3\3\2\2\2\2\5\3\2\2\2\2\7\3\2\2\2\2\t\3\2\2\2\2\13\3"+
		"\2\2\2\2\r\3\2\2\2\2\17\3\2\2\2\2\21\3\2\2\2\2\23\3\2\2\2\2\25\3\2\2\2"+
		"\2\27\3\2\2\2\2\31\3\2\2\2\2\33\3\2\2\2\2\35\3\2\2\2\2\37\3\2\2\2\2!\3"+
		"\2\2\2\2#\3\2\2\2\2%\3\2\2\2\2\'\3\2\2\2\2)\3\2\2\2\2+\3\2\2\2\2-\3\2"+
		"\2\2\2/\3\2\2\2\2\61\3\2\2\2\2\63\3\2\2\2\2\65\3\2\2\2\2\67\3\2\2\2\2"+
		"9\3\2\2\2\2;\3\2\2\2\2=\3\2\2\2\2?\3\2\2\2\2A\3\2\2\2\2C\3\2\2\2\2I\3"+
		"\2\2\2\2K\3\2\2\2\2W\3\2\2\2\2Y\3\2\2\2\2[\3\2\2\2\2]\3\2\2\2\2_\3\2\2"+
		"\2\2a\3\2\2\2\2c\3\2\2\2\2e\3\2\2\2\2g\3\2\2\2\2i\3\2\2\2\2k\3\2\2\2\2"+
		"m\3\2\2\2\2o\3\2\2\2\2q\3\2\2\2\2s\3\2\2\2\2u\3\2\2\2\2w\3\2\2\2\2y\3"+
		"\2\2\2\2{\3\2\2\2\2}\3\2\2\2\2\177\3\2\2\2\2\u0081\3\2\2\2\2\u0083\3\2"+
		"\2\2\2\u0085\3\2\2\2\2\u0087\3\2\2\2\2\u0089\3\2\2\2\2\u008b\3\2\2\2\2"+
		"\u008d\3\2\2\2\2\u008f\3\2\2\2\2\u0091\3\2\2\2\2\u0093\3\2\2\2\2\u0095"+
		"\3\2\2\2\2\u0097\3\2\2\2\2\u0099\3\2\2\2\2\u009b\3\2\2\2\2\u009d\3\2\2"+
		"\2\2\u009f\3\2\2\2\2\u00a1\3\2\2\2\2\u00a3\3\2\2\2\2\u00a5\3\2\2\2\2\u00a7"+
		"\3\2\2\2\2\u00a9\3\2\2\2\2\u00ab\3\2\2\2\2\u00ad\3\2\2\2\2\u00af\3\2\2"+
		"\2\2\u00b1\3\2\2\2\2\u00b7\3\2\2\2\2\u00b9\3\2\2\2\2\u00bb\3\2\2\2\2\u00bd"+
		"\3\2\2\2\3\u00bf\3\2\2\2\5\u00c1\3\2\2\2\7\u00c3\3\2\2\2\t\u00c5\3\2\2"+
		"\2\13\u00c7\3\2\2\2\r\u00ca\3\2\2\2\17\u00cc\3\2\2\2\21\u00cf\3\2\2\2"+
		"\23\u00d1\3\2\2\2\25\u00d8\3\2\2\2\27\u00da\3\2\2\2\31\u00e1\3\2\2\2\33"+
		"\u00e3\3\2\2\2\35\u00e9\3\2\2\2\37\u00ee\3\2\2\2!\u00f4\3\2\2\2#\u00fa"+
		"\3\2\2\2%\u00ff\3\2\2\2\'\u0105\3\2\2\2)\u0107\3\2\2\2+\u010e\3\2\2\2"+
		"-\u0111\3\2\2\2/\u0116\3\2\2\2\61\u0119\3\2\2\2\63\u0120\3\2\2\2\65\u0125"+
		"\3\2\2\2\67\u0127\3\2\2\29\u012e\3\2\2\2;\u0133\3\2\2\2=\u0137\3\2\2\2"+
		"?\u013e\3\2\2\2A\u0142\3\2\2\2C\u0146\3\2\2\2E\u014f\3\2\2\2G\u0155\3"+
		"\2\2\2I\u0159\3\2\2\2K\u015b\3\2\2\2M\u0164\3\2\2\2O\u0166\3\2\2\2Q\u0171"+
		"\3\2\2\2S\u0174\3\2\2\2U\u017a\3\2\2\2W\u017c\3\2\2\2Y\u017e\3\2\2\2["+
		"\u0180\3\2\2\2]\u0182\3\2\2\2_\u0184\3\2\2\2a\u0189\3\2\2\2c\u018e\3\2"+
		"\2\2e\u0193\3\2\2\2g\u0195\3\2\2\2i\u0197\3\2\2\2k\u0199\3\2\2\2m\u019c"+
		"\3\2\2\2o\u019e\3\2\2\2q\u01a1\3\2\2\2s\u01a3\3\2\2\2u\u01a5\3\2\2\2w"+
		"\u01a7\3\2\2\2y\u01ac\3\2\2\2{\u01b1\3\2\2\2}\u01b6\3\2\2\2\177\u01b8"+
		"\3\2\2\2\u0081\u01be\3\2\2\2\u0083\u01c3\3\2\2\2\u0085\u01c8\3\2\2\2\u0087"+
		"\u01ca\3\2\2\2\u0089\u01cc\3\2\2\2\u008b\u01ce\3\2\2\2\u008d\u01d3\3\2"+
		"\2\2\u008f\u01d8\3\2\2\2\u0091\u01da\3\2\2\2\u0093\u01dc\3\2\2\2\u0095"+
		"\u01de\3\2\2\2\u0097\u01e0\3\2\2\2\u0099\u01e2\3\2\2\2\u009b\u01e4\3\2"+
		"\2\2\u009d\u01e9\3\2\2\2\u009f\u01eb\3\2\2\2\u00a1\u01ed\3\2\2\2\u00a3"+
		"\u01ef\3\2\2\2\u00a5\u01f1\3\2\2\2\u00a7\u01f3\3\2\2\2\u00a9\u01f5\3\2"+
		"\2\2\u00ab\u020b\3\2\2\2\u00ad\u020e\3\2\2\2\u00af\u0217\3\2\2\2\u00b1"+
		"\u021a\3\2\2\2\u00b3\u0242\3\2\2\2\u00b5\u0244\3\2\2\2\u00b7\u024b\3\2"+
		"\2\2\u00b9\u0253\3\2\2\2\u00bb\u0259\3\2\2\2\u00bd\u0267\3\2\2\2\u00bf"+
		"\u00c0\7}\2\2\u00c0\4\3\2\2\2\u00c1\u00c2\7\177\2\2\u00c2\6\3\2\2\2\u00c3"+
		"\u00c4\7+\2\2\u00c4\b\3\2\2\2\u00c5\u00c6\7*\2\2\u00c6\n\3\2\2\2\u00c7"+
		"\u00c8\7^\2\2\u00c8\u00c9\7\61\2\2\u00c9\f\3\2\2\2\u00ca\u00cb\7\u222b"+
		"\2\2\u00cb\16\3\2\2\2\u00cc\u00cd\7\61\2\2\u00cd\u00ce\7^\2\2\u00ce\20"+
		"\3\2\2\2\u00cf\u00d0\7\u222c\2\2\u00d0\22\3\2\2\2\u00d1\u00d2\7A\2\2\u00d2"+
		"\24\3\2\2\2\u00d3\u00d9\5\35\17\2\u00d4\u00d9\5\33\16\2\u00d5\u00d9\5"+
		"\63\32\2\u00d6\u00d9\5\65\33\2\u00d7\u00d9\5\'\24\2\u00d8\u00d3\3\2\2"+
		"\2\u00d8\u00d4\3\2\2\2\u00d8\u00d5\3\2\2\2\u00d8\u00d6\3\2\2\2\u00d8\u00d7"+
		"\3\2\2\2\u00d9\26\3\2\2\2\u00da\u00db\7c\2\2\u00db\u00dc\7u\2\2\u00dc"+
		"\u00dd\7u\2\2\u00dd\u00de\7g\2\2\u00de\u00df\7t\2\2\u00df\u00e0\7v\2\2"+
		"\u00e0\30\3\2\2\2\u00e1\u00e2\7\u22aa\2\2\u00e2\32\3\2\2\2\u00e3\u00e4"+
		"\7h\2\2\u00e4\u00e5\7c\2\2\u00e5\u00e6\7n\2\2\u00e6\u00e7\7u\2\2\u00e7"+
		"\u00e8\7g\2\2\u00e8\34\3\2\2\2\u00e9\u00ea\7v\2\2\u00ea\u00eb\7t\2\2\u00eb"+
		"\u00ec\7w\2\2\u00ec\u00ed\7g\2\2\u00ed\36\3\2\2\2\u00ee\u00ef\7d\2\2\u00ef"+
		"\u00f0\7n\2\2\u00f0\u00f1\7q\2\2\u00f1\u00f2\7e\2\2\u00f2\u00f3\7m\2\2"+
		"\u00f3 \3\2\2\2\u00f4\u00f5\7n\2\2\u00f5\u00f6\7q\2\2\u00f6\u00f7\7e\2"+
		"\2\u00f7\u00f8\7c\2\2\u00f8\u00f9\7n\2\2\u00f9\"\3\2\2\2\u00fa\u00fb\7"+
		"d\2\2\u00fb\u00fc\7q\2\2\u00fc\u00fd\7f\2\2\u00fd\u00fe\7{\2\2\u00fe$"+
		"\3\2\2\2\u00ff\u0100\7e\2\2\u0100\u0101\7c\2\2\u0101\u0102\7v\2\2\u0102"+
		"\u0103\7e\2\2\u0103\u0104\7j\2\2\u0104&\3\2\2\2\u0105\u0106\7K\2\2\u0106"+
		"(\3\2\2\2\u0107\u0108\7f\2\2\u0108\u0109\7g\2\2\u0109\u010a\7h\2\2\u010a"+
		"\u010b\7k\2\2\u010b\u010c\7p\2\2\u010c\u010d\7g\2\2\u010d*\3\2\2\2\u010e"+
		"\u010f\7f\2\2\u010f\u0110\7q\2\2\u0110,\3\2\2\2\u0111\u0112\7g\2\2\u0112"+
		"\u0113\7n\2\2\u0113\u0114\7u\2\2\u0114\u0115\7g\2\2\u0115.\3\2\2\2\u0116"+
		"\u0117\7k\2\2\u0117\u0118\7h\2\2\u0118\60\3\2\2\2\u0119\u011a\7o\2\2\u011a"+
		"\u011b\7q\2\2\u011b\u011c\7f\2\2\u011c\u011d\7w\2\2\u011d\u011e\7n\2\2"+
		"\u011e\u011f\7g\2\2\u011f\62\3\2\2\2\u0120\u0121\7p\2\2\u0121\u0122\7"+
		"w\2\2\u0122\u0123\7n\2\2\u0123\u0124\7n\2\2\u0124\64\3\2\2\2\u0125\u0126"+
		"\7\u2207\2\2\u0126\66\3\2\2\2\u0127\u0128\7u\2\2\u0128\u0129\7y\2\2\u0129"+
		"\u012a\7k\2\2\u012a\u012b\7v\2\2\u012b\u012c\7e\2\2\u012c\u012d\7j\2\2"+
		"\u012d8\3\2\2\2\u012e\u012f\7v\2\2\u012f\u0130\7j\2\2\u0130\u0131\7g\2"+
		"\2\u0131\u0132\7p\2\2\u0132:\3\2\2\2\u0133\u0134\7v\2\2\u0134\u0135\7"+
		"t\2\2\u0135\u0136\7{\2\2\u0136<\3\2\2\2\u0137\u0138\7y\2\2\u0138\u0139"+
		"\7j\2\2\u0139\u013a\7k\2\2\u013a\u013b\7n\2\2\u013b\u013c\7g\2\2\u013c"+
		">\3\2\2\2\u013d\u013f\t\2\2\2\u013e\u013d\3\2\2\2\u013f\u0140\3\2\2\2"+
		"\u0140\u013e\3\2\2\2\u0140\u0141\3\2\2\2\u0141@\3\2\2\2\u0142\u0143\5"+
		"? \2\u0143\u0144\7\60\2\2\u0144\u0145\5? \2\u0145B\3\2\2\2\u0146\u014d"+
		"\5A!\2\u0147\u0149\5E#\2\u0148\u014a\5G$\2\u0149\u0148\3\2\2\2\u0149\u014a"+
		"\3\2\2\2\u014a\u014b\3\2\2\2\u014b\u014c\5? \2\u014c\u014e\3\2\2\2\u014d"+
		"\u0147\3\2\2\2\u014d\u014e\3\2\2\2\u014eD\3\2\2\2\u014f\u0150\t\3\2\2"+
		"\u0150F\3\2\2\2\u0151\u0156\5m\67\2\u0152\u0156\5\u00a3R\2\u0153\u0156"+
		"\5q9\2\u0154\u0156\5\u00a1Q\2\u0155\u0151\3\2\2\2\u0155\u0152\3\2\2\2"+
		"\u0155\u0153\3\2\2\2\u0155\u0154\3\2\2\2\u0156H\3\2\2\2\u0157\u015a\5"+
		"\35\17\2\u0158\u015a\5\33\16\2\u0159\u0157\3\2\2\2\u0159\u0158\3\2\2\2"+
		"\u015aJ\3\2\2\2\u015b\u015d\7)\2\2\u015c\u015e\5S*\2\u015d\u015c\3\2\2"+
		"\2\u015d\u015e\3\2\2\2\u015e\u015f\3\2\2\2\u015f\u0160\7)\2\2\u0160L\3"+
		"\2\2\2\u0161\u0162\7^\2\2\u0162\u0165\t\4\2\2\u0163\u0165\5O(\2\u0164"+
		"\u0161\3\2\2\2\u0164\u0163\3\2\2\2\u0165N\3\2\2\2\u0166\u0168\7^\2\2\u0167"+
		"\u0169\7w\2\2\u0168\u0167\3\2\2\2\u0169\u016a\3\2\2\2\u016a\u0168\3\2"+
		"\2\2\u016a\u016b\3\2\2\2\u016b\u016c\3\2\2\2\u016c\u016d\5Q)\2\u016d\u016e"+
		"\5Q)\2\u016e\u016f\5Q)\2\u016f\u0170\5Q)\2\u0170P\3\2\2\2\u0171\u0172"+
		"\t\5\2\2\u0172R\3\2\2\2\u0173\u0175\5U+\2\u0174\u0173\3\2\2\2\u0175\u0176"+
		"\3\2\2\2\u0176\u0174\3\2\2\2\u0176\u0177\3\2\2\2\u0177T\3\2\2\2\u0178"+
		"\u017b\n\6\2\2\u0179\u017b\5M\'\2\u017a\u0178\3\2\2\2\u017a\u0179\3\2"+
		"\2\2\u017bV\3\2\2\2\u017c\u017d\7]\2\2\u017dX\3\2\2\2\u017e\u017f\7_\2"+
		"\2\u017fZ\3\2\2\2\u0180\u0181\7.\2\2\u0181\\\3\2\2\2\u0182\u0183\7<\2"+
		"\2\u0183^\3\2\2\2\u0184\u0185\7=\2\2\u0185`\3\2\2\2\u0186\u0187\7]\2\2"+
		"\u0187\u018a\7~\2\2\u0188\u018a\7\u27e8\2\2\u0189\u0186\3\2\2\2\u0189"+
		"\u0188\3\2\2\2\u018ab\3\2\2\2\u018b\u018c\7~\2\2\u018c\u018f\7_\2\2\u018d"+
		"\u018f\7\u27e9\2\2\u018e\u018b\3\2\2\2\u018e\u018d\3\2\2\2\u018fd\3\2"+
		"\2\2\u0190\u0191\7/\2\2\u0191\u0194\7@\2\2\u0192\u0194\7\u2194\2\2\u0193"+
		"\u0190\3\2\2\2\u0193\u0192\3\2\2\2\u0194f\3\2\2\2\u0195\u0196\t\7\2\2"+
		"\u0196h\3\2\2\2\u0197\u0198\t\b\2\2\u0198j\3\2\2\2\u0199\u019a\7-\2\2"+
		"\u019a\u019b\7-\2\2\u019bl\3\2\2\2\u019c\u019d\7-\2\2\u019dn\3\2\2\2\u019e"+
		"\u019f\7/\2\2\u019f\u01a0\7/\2\2\u01a0p\3\2\2\2\u01a1\u01a2\7/\2\2\u01a2"+
		"r\3\2\2\2\u01a3\u01a4\7>\2\2\u01a4t\3\2\2\2\u01a5\u01a6\7@\2\2\u01a6v"+
		"\3\2\2\2\u01a7\u01a8\7?\2\2\u01a8x\3\2\2\2\u01a9\u01aa\7~\2\2\u01aa\u01ad"+
		"\7@\2\2\u01ab\u01ad\7\u22a4\2\2\u01ac\u01a9\3\2\2\2\u01ac\u01ab\3\2\2"+
		"\2\u01adz\3\2\2\2\u01ae\u01af\7>\2\2\u01af\u01b2\7?\2\2\u01b0\u01b2\7"+
		"\u2266\2\2\u01b1\u01ae\3\2\2\2\u01b1\u01b0\3\2\2\2\u01b2|\3\2\2\2\u01b3"+
		"\u01b4\7@\2\2\u01b4\u01b7\7?\2\2\u01b5\u01b7\7\u2267\2\2\u01b6\u01b3\3"+
		"\2\2\2\u01b6\u01b5\3\2\2\2\u01b7~\3\2\2\2\u01b8\u01b9\7>\2\2\u01b9\u01ba"+
		"\7>\2\2\u01ba\u0080\3\2\2\2\u01bb\u01bc\7?\2\2\u01bc\u01bf\7?\2\2\u01bd"+
		"\u01bf\7\u2263\2\2\u01be\u01bb\3\2\2\2\u01be\u01bd\3\2\2\2\u01bf\u0082"+
		"\3\2\2\2\u01c0\u01c1\7#\2\2\u01c1\u01c4\7?\2\2\u01c2\u01c4\7\u2262\2\2"+
		"\u01c3\u01c0\3\2\2\2\u01c3\u01c2\3\2\2\2\u01c4\u0084\3\2\2\2\u01c5\u01c6"+
		"\7?\2\2\u01c6\u01c9\7\u0080\2\2\u01c7\u01c9\7\u224a\2\2\u01c8\u01c5\3"+
		"\2\2\2\u01c8\u01c7\3\2\2\2\u01c9\u0086\3\2\2\2\u01ca\u01cb\t\t\2\2\u01cb"+
		"\u0088\3\2\2\2\u01cc\u01cd\4\u220a\u220b\2\u01cd\u008a\3\2\2\2\u01ce\u01cf"+
		"\7`\2\2\u01cf\u008c\3\2\2\2\u01d0\u01d1\7(\2\2\u01d1\u01d4\7(\2\2\u01d2"+
		"\u01d4\7\u2229\2\2\u01d3\u01d0\3\2\2\2\u01d3\u01d2\3\2\2\2\u01d4\u008e"+
		"\3\2\2\2\u01d5\u01d6\7~\2\2\u01d6\u01d9\7~\2\2\u01d7\u01d9\7\u222a\2\2"+
		"\u01d8\u01d5\3\2\2\2\u01d8\u01d7\3\2\2\2\u01d9\u0090\3\2\2\2\u01da\u01db"+
		"\7b\2\2\u01db\u0092\3\2\2\2\u01dc\u01dd\t\n\2\2\u01dd\u0094\3\2\2\2\u01de"+
		"\u01df\7\u0080\2\2\u01df\u0096\3\2\2\2\u01e0\u01e1\7^\2\2\u01e1\u0098"+
		"\3\2\2\2\u01e2\u01e3\7%\2\2\u01e3\u009a\3\2\2\2\u01e4\u01e5\7~\2\2\u01e5"+
		"\u009c\3\2\2\2\u01e6\u01e7\7\u0080\2\2\u01e7\u01ea\7~\2\2\u01e8\u01ea"+
		"\7\u2243\2\2\u01e9\u01e6\3\2\2\2\u01e9\u01e8\3\2\2\2\u01ea\u009e\3\2\2"+
		"\2\u01eb\u01ec\7\60\2\2\u01ec\u00a0\3\2\2\2\u01ed\u01ee\7\u00b1\2\2\u01ee"+
		"\u00a2\3\2\2\2\u01ef\u01f0\7\u207c\2\2\u01f0\u00a4\3\2\2\2\u01f1\u01f2"+
		"\7\u230c\2\2\u01f2\u00a6\3\2\2\2\u01f3\u01f4\7\u230a\2\2\u01f4\u00a8\3"+
		"\2\2\2\u01f5\u01f6\t\13\2\2\u01f6\u00aa\3\2\2\2\u01f7\u020c\7\u2256\2"+
		"\2\u01f8\u01f9\7<\2\2\u01f9\u020c\7?\2\2\u01fa\u020c\7\u2257\2\2\u01fb"+
		"\u01fc\7?\2\2\u01fc\u020c\7<\2\2\u01fd\u01fe\7-\2\2\u01fe\u020c\7?\2\2"+
		"\u01ff\u0200\7/\2\2\u0200\u020c\7?\2\2\u0201\u0202\5g\64\2\u0202\u0203"+
		"\7?\2\2\u0203\u020c\3\2\2\2\u0204\u0205\5i\65\2\u0205\u0206\7?\2\2\u0206"+
		"\u020c\3\2\2\2\u0207\u0208\7\'\2\2\u0208\u020c\7?\2\2\u0209\u020a\7`\2"+
		"\2\u020a\u020c\7?\2\2\u020b\u01f7\3\2\2\2\u020b\u01f8\3\2\2\2\u020b\u01fa"+
		"\3\2\2\2\u020b\u01fb\3\2\2\2\u020b\u01fd\3\2\2\2\u020b\u01ff\3\2\2\2\u020b"+
		"\u0201\3\2\2\2\u020b\u0204\3\2\2\2\u020b\u0207\3\2\2\2\u020b\u0209\3\2"+
		"\2\2\u020c\u00ac\3\2\2\2\u020d\u020f\7(\2\2\u020e\u020d\3\2\2\2\u020e"+
		"\u020f\3\2\2\2\u020f\u0210\3\2\2\2\u0210\u0214\t\f\2\2\u0211\u0213\t\r"+
		"\2\2\u0212\u0211\3\2\2\2\u0213\u0216\3\2\2\2\u0214\u0212\3\2\2\2\u0214"+
		"\u0215\3\2\2\2\u0215\u00ae\3\2\2\2\u0216\u0214\3\2\2\2\u0217\u0218\5\u00b5"+
		"[\2\u0218\u0219\7*\2\2\u0219\u00b0\3\2\2\2\u021a\u0228\5\u00a9U\2\u021b"+
		"\u0229\5\u00b3Z\2\u021c\u021d\5\u00adW\2\u021d\u021e\5\u0099M\2\u021e"+
		"\u0220\3\2\2\2\u021f\u021c\3\2\2\2\u0220\u0223\3\2\2\2\u0221\u021f\3\2"+
		"\2\2\u0221\u0222\3\2\2\2\u0222\u0224\3\2\2\2\u0223\u0221\3\2\2\2\u0224"+
		"\u0229\5\u00b5[\2\u0225\u0226\5\u00afX\2\u0226\u0227\7+\2\2\u0227\u0229"+
		"\3\2\2\2\u0228\u021b\3\2\2\2\u0228\u0221\3\2\2\2\u0228\u0225\3\2\2\2\u0229"+
		"\u00b2\3\2\2\2\u022a\u0243\5g\64\2\u022b\u0243\5i\65\2\u022c\u0243\5m"+
		"\67\2\u022d\u0243\5q9\2\u022e\u0243\5s:\2\u022f\u0243\5{>\2\u0230\u0243"+
		"\5u;\2\u0231\u0243\5\u008bF\2\u0232\u0243\5{>\2\u0233\u0243\5}?\2\u0234"+
		"\u0243\5\u0081A\2\u0235\u0243\5\u0083B\2\u0236\u0243\5\u008dG\2\u0237"+
		"\u0243\5\u008fH\2\u0238\u0243\5\u0093J\2\u0239\u0243\5\u0095K\2\u023a"+
		"\u0243\5\u009dO\2\u023b\u0243\5\u0087D\2\u023c\u0243\5\u0085C\2\u023d"+
		"\u0243\5\u00a5S\2\u023e\u0243\5\u00a7T\2\u023f\u0243\5\u0089E\2\u0240"+
		"\u0243\5y=\2\u0241\u0243\5\177@\2\u0242\u022a\3\2\2\2\u0242\u022b\3\2"+
		"\2\2\u0242\u022c\3\2\2\2\u0242\u022d\3\2\2\2\u0242\u022e\3\2\2\2\u0242"+
		"\u022f\3\2\2\2\u0242\u0230\3\2\2\2\u0242\u0231\3\2\2\2\u0242\u0232\3\2"+
		"\2\2\u0242\u0233\3\2\2\2\u0242\u0234\3\2\2\2\u0242\u0235\3\2\2\2\u0242"+
		"\u0236\3\2\2\2\u0242\u0237\3\2\2\2\u0242\u0238\3\2\2\2\u0242\u0239\3\2"+
		"\2\2\u0242\u023a\3\2\2\2\u0242\u023b\3\2\2\2\u0242\u023c\3\2\2\2\u0242"+
		"\u023d\3\2\2\2\u0242\u023e\3\2\2\2\u0242\u023f\3\2\2\2\u0242\u0240\3\2"+
		"\2\2\u0242\u0241\3\2\2\2\u0243\u00b4\3\2\2\2\u0244\u0248\t\f\2\2\u0245"+
		"\u0247\t\r\2\2\u0246\u0245\3\2\2\2\u0247\u024a\3\2\2\2\u0248\u0246\3\2"+
		"\2\2\u0248\u0249\3\2\2\2\u0249\u00b6\3\2\2\2\u024a\u0248\3\2\2\2\u024b"+
		"\u024f\7\u00bd\2\2\u024c\u024e\n\16\2\2\u024d\u024c\3\2\2\2\u024e\u0251"+
		"\3\2\2\2\u024f\u024d\3\2\2\2\u024f\u0250\3\2\2\2\u0250\u00b8\3\2\2\2\u0251"+
		"\u024f\3\2\2\2\u0252\u0254\t\17\2\2\u0253\u0252\3\2\2\2\u0254\u0255\3"+
		"\2\2\2\u0255\u0253\3\2\2\2\u0255\u0256\3\2\2\2\u0256\u0257\3\2\2\2\u0257"+
		"\u0258\b]\2\2\u0258\u00ba\3\2\2\2\u0259\u025a\7\61\2\2\u025a\u025b\7,"+
		"\2\2\u025b\u025f\3\2\2\2\u025c\u025e\13\2\2\2\u025d\u025c\3\2\2\2\u025e"+
		"\u0261\3\2\2\2\u025f\u0260\3\2\2\2\u025f\u025d\3\2\2\2\u0260\u0262\3\2"+
		"\2\2\u0261\u025f\3\2\2\2\u0262\u0263\7,\2\2\u0263\u0264\7\61\2\2\u0264"+
		"\u0265\3\2\2\2\u0265\u0266\b^\2\2\u0266\u00bc\3\2\2\2\u0267\u0268\7\61"+
		"\2\2\u0268\u0269\7\61\2\2\u0269\u026d\3\2\2\2\u026a\u026c\n\16\2\2\u026b"+
		"\u026a\3\2\2\2\u026c\u026f\3\2\2\2\u026d\u026b\3\2\2\2\u026d\u026e\3\2"+
		"\2\2\u026e\u0270\3\2\2\2\u026f\u026d\3\2\2\2\u0270\u0271\b_\2\2\u0271"+
		"\u00be\3\2\2\2%\2\u00d8\u0140\u0149\u014d\u0155\u0159\u015d\u0164\u016a"+
		"\u0176\u017a\u0189\u018e\u0193\u01ac\u01b1\u01b6\u01be\u01c3\u01c8\u01d3"+
		"\u01d8\u01e9\u020b\u020e\u0214\u0221\u0228\u0242\u0248\u024f\u0255\u025f"+
		"\u026d\3\b\2\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}