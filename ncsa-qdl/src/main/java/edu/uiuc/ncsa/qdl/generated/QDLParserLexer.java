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
		T__0=1, T__1=2, T__2=3, T__3=4, T__4=5, T__5=6, ConstantKeywords=7, ASSERT=8, 
		ASSERT2=9, BOOL_FALSE=10, BOOL_TRUE=11, BLOCK=12, LOCAL=13, BODY=14, CATCH=15, 
		COMPLEX_I=16, DEFINE=17, DO=18, ELSE=19, IF=20, MODULE=21, Null=22, SWITCH=23, 
		THEN=24, TRY=25, WHILE=26, Integer=27, Decimal=28, SCIENTIFIC_NUMBER=29, 
		Bool=30, STRING=31, LeftBracket=32, RightBracket=33, Comma=34, Colon=35, 
		SemiColon=36, LDoubleBracket=37, RDoubleBracket=38, LambdaConnector=39, 
		Times=40, Divide=41, PlusPlus=42, Plus=43, MinusMinus=44, Minus=45, LessThan=46, 
		GreaterThan=47, SingleEqual=48, LessEquals=49, MoreEquals=50, Equals=51, 
		NotEquals=52, RegexMatches=53, LogicalNot=54, Exponentiation=55, And=56, 
		Or=57, Backtick=58, Percent=59, Tilde=60, Backslash=61, Hash=62, Stile=63, 
		TildeRight=64, StemDot=65, UnaryMinus=66, UnaryPlus=67, Floor=68, Ceiling=69, 
		FunctionMarker=70, ASSIGN=71, Identifier=72, FuncStart=73, F_REF=74, FDOC=75, 
		WS=76, COMMENT=77, LINE_COMMENT=78;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	private static String[] makeRuleNames() {
		return new String[] {
			"T__0", "T__1", "T__2", "T__3", "T__4", "T__5", "ConstantKeywords", "ASSERT", 
			"ASSERT2", "BOOL_FALSE", "BOOL_TRUE", "BLOCK", "LOCAL", "BODY", "CATCH", 
			"COMPLEX_I", "DEFINE", "DO", "ELSE", "IF", "MODULE", "Null", "SWITCH", 
			"THEN", "TRY", "WHILE", "Integer", "Decimal", "SCIENTIFIC_NUMBER", "E", 
			"SIGN", "Bool", "STRING", "ESC", "UnicodeEscape", "HexDigit", "StringCharacters", 
			"StringCharacter", "LeftBracket", "RightBracket", "Comma", "Colon", "SemiColon", 
			"LDoubleBracket", "RDoubleBracket", "LambdaConnector", "Times", "Divide", 
			"PlusPlus", "Plus", "MinusMinus", "Minus", "LessThan", "GreaterThan", 
			"SingleEqual", "LessEquals", "MoreEquals", "Equals", "NotEquals", "RegexMatches", 
			"LogicalNot", "Exponentiation", "And", "Or", "Backtick", "Percent", "Tilde", 
			"Backslash", "Hash", "Stile", "TildeRight", "StemDot", "UnaryMinus", 
			"UnaryPlus", "Floor", "Ceiling", "FunctionMarker", "ASSIGN", "Identifier", 
			"FuncStart", "F_REF", "AllOps", "FUNCTION_NAME", "FDOC", "WS", "COMMENT", 
			"LINE_COMMENT"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'{'", "'}'", "')'", "'('", "'?'", "'\u2208'", null, "'assert'", 
			"'\u22A8'", null, null, "'block'", "'local'", "'body'", "'catch'", "'I'", 
			"'define'", "'do'", "'else'", "'if'", "'module'", null, "'switch'", "'then'", 
			"'try'", "'while'", null, null, null, null, null, "'['", "']'", "','", 
			"':'", "';'", null, null, null, null, null, "'++'", "'+'", "'--'", "'-'", 
			"'<'", "'>'", "'='", null, null, null, null, null, null, "'^'", null, 
			null, "'`'", "'%'", "'~'", "'\\'", "'#'", "'|'", null, "'.'", "'\u00AF'", 
			"'\u207A'", "'\u230A'", "'\u2308'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, null, null, null, null, null, null, "ConstantKeywords", "ASSERT", 
			"ASSERT2", "BOOL_FALSE", "BOOL_TRUE", "BLOCK", "LOCAL", "BODY", "CATCH", 
			"COMPLEX_I", "DEFINE", "DO", "ELSE", "IF", "MODULE", "Null", "SWITCH", 
			"THEN", "TRY", "WHILE", "Integer", "Decimal", "SCIENTIFIC_NUMBER", "Bool", 
			"STRING", "LeftBracket", "RightBracket", "Comma", "Colon", "SemiColon", 
			"LDoubleBracket", "RDoubleBracket", "LambdaConnector", "Times", "Divide", 
			"PlusPlus", "Plus", "MinusMinus", "Minus", "LessThan", "GreaterThan", 
			"SingleEqual", "LessEquals", "MoreEquals", "Equals", "NotEquals", "RegexMatches", 
			"LogicalNot", "Exponentiation", "And", "Or", "Backtick", "Percent", "Tilde", 
			"Backslash", "Hash", "Stile", "TildeRight", "StemDot", "UnaryMinus", 
			"UnaryPlus", "Floor", "Ceiling", "FunctionMarker", "ASSIGN", "Identifier", 
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
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2P\u024b\b\1\4\2\t"+
		"\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13"+
		"\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31\t\31"+
		"\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4 \t \4!"+
		"\t!\4\"\t\"\4#\t#\4$\t$\4%\t%\4&\t&\4\'\t\'\4(\t(\4)\t)\4*\t*\4+\t+\4"+
		",\t,\4-\t-\4.\t.\4/\t/\4\60\t\60\4\61\t\61\4\62\t\62\4\63\t\63\4\64\t"+
		"\64\4\65\t\65\4\66\t\66\4\67\t\67\48\t8\49\t9\4:\t:\4;\t;\4<\t<\4=\t="+
		"\4>\t>\4?\t?\4@\t@\4A\tA\4B\tB\4C\tC\4D\tD\4E\tE\4F\tF\4G\tG\4H\tH\4I"+
		"\tI\4J\tJ\4K\tK\4L\tL\4M\tM\4N\tN\4O\tO\4P\tP\4Q\tQ\4R\tR\4S\tS\4T\tT"+
		"\4U\tU\4V\tV\4W\tW\4X\tX\3\2\3\2\3\3\3\3\3\4\3\4\3\5\3\5\3\6\3\6\3\7\3"+
		"\7\3\b\3\b\3\b\3\b\5\b\u00c2\n\b\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\n\3\n\3"+
		"\13\3\13\3\13\3\13\3\13\3\13\5\13\u00d3\n\13\3\f\3\f\3\f\3\f\3\f\5\f\u00da"+
		"\n\f\3\r\3\r\3\r\3\r\3\r\3\r\3\16\3\16\3\16\3\16\3\16\3\16\3\17\3\17\3"+
		"\17\3\17\3\17\3\20\3\20\3\20\3\20\3\20\3\20\3\21\3\21\3\22\3\22\3\22\3"+
		"\22\3\22\3\22\3\22\3\23\3\23\3\23\3\24\3\24\3\24\3\24\3\24\3\25\3\25\3"+
		"\25\3\26\3\26\3\26\3\26\3\26\3\26\3\26\3\27\3\27\3\27\3\27\3\27\5\27\u0113"+
		"\n\27\3\30\3\30\3\30\3\30\3\30\3\30\3\30\3\31\3\31\3\31\3\31\3\31\3\32"+
		"\3\32\3\32\3\32\3\33\3\33\3\33\3\33\3\33\3\33\3\34\6\34\u012c\n\34\r\34"+
		"\16\34\u012d\3\35\3\35\3\35\3\35\3\36\3\36\3\36\5\36\u0137\n\36\3\36\3"+
		"\36\5\36\u013b\n\36\3\37\3\37\3 \3 \3 \3 \5 \u0143\n \3!\3!\5!\u0147\n"+
		"!\3\"\3\"\5\"\u014b\n\"\3\"\3\"\3#\3#\3#\5#\u0152\n#\3$\3$\6$\u0156\n"+
		"$\r$\16$\u0157\3$\3$\3$\3$\3$\3%\3%\3&\6&\u0162\n&\r&\16&\u0163\3\'\3"+
		"\'\5\'\u0168\n\'\3(\3(\3)\3)\3*\3*\3+\3+\3,\3,\3-\3-\3-\5-\u0177\n-\3"+
		".\3.\3.\5.\u017c\n.\3/\3/\3/\5/\u0181\n/\3\60\3\60\3\61\3\61\3\62\3\62"+
		"\3\62\3\63\3\63\3\64\3\64\3\64\3\65\3\65\3\66\3\66\3\67\3\67\38\38\39"+
		"\39\39\39\39\59\u019c\n9\3:\3:\3:\3:\3:\5:\u01a3\n:\3;\3;\3;\5;\u01a8"+
		"\n;\3<\3<\3<\5<\u01ad\n<\3=\3=\3=\5=\u01b2\n=\3>\3>\3?\3?\3@\3@\3@\5@"+
		"\u01bb\n@\3A\3A\3A\5A\u01c0\nA\3B\3B\3C\3C\3D\3D\3E\3E\3F\3F\3G\3G\3H"+
		"\3H\3H\5H\u01d1\nH\3I\3I\3J\3J\3K\3K\3L\3L\3M\3M\3N\3N\3O\3O\3O\3O\3O"+
		"\3O\3O\3O\3O\3O\3O\3O\3O\3O\3O\3O\3O\3O\3O\3O\5O\u01f3\nO\3P\3P\7P\u01f7"+
		"\nP\fP\16P\u01fa\13P\3Q\3Q\3Q\3R\3R\3R\3R\3R\3R\5R\u0205\nR\3S\3S\3S\3"+
		"S\3S\3S\3S\3S\3S\3S\3S\3S\3S\3S\3S\3S\3S\3S\3S\5S\u021a\nS\3T\3T\7T\u021e"+
		"\nT\fT\16T\u0221\13T\3U\3U\3U\3U\7U\u0227\nU\fU\16U\u022a\13U\3V\6V\u022d"+
		"\nV\rV\16V\u022e\3V\3V\3W\3W\3W\3W\7W\u0237\nW\fW\16W\u023a\13W\3W\3W"+
		"\3W\3W\3W\3X\3X\3X\3X\7X\u0245\nX\fX\16X\u0248\13X\3X\3X\3\u0238\2Y\3"+
		"\3\5\4\7\5\t\6\13\7\r\b\17\t\21\n\23\13\25\f\27\r\31\16\33\17\35\20\37"+
		"\21!\22#\23%\24\'\25)\26+\27-\30/\31\61\32\63\33\65\34\67\359\36;\37="+
		"\2?\2A C!E\2G\2I\2K\2M\2O\"Q#S$U%W&Y\'[(])_*a+c,e-g.i/k\60m\61o\62q\63"+
		"s\64u\65w\66y\67{8}9\177:\u0081;\u0083<\u0085=\u0087>\u0089?\u008b@\u008d"+
		"A\u008fB\u0091C\u0093D\u0095E\u0097F\u0099G\u009bH\u009dI\u009fJ\u00a1"+
		"K\u00a3L\u00a5\2\u00a7\2\u00a9M\u00abN\u00adO\u00afP\3\2\21\3\2\62;\4"+
		"\2GGgg\t\2))^^ddhhppttvv\5\2\62;CHch\6\2\f\f\17\17))^^\4\2,,\u00d9\u00d9"+
		"\4\2\61\61\u00f9\u00f9\4\2##\u00ae\u00ae\5\2\u2229\u2229\u222b\u222b\u22c2"+
		"\u22c2\5\2\u222a\u222a\u222c\u222c\u22c3\u22c3\4\2BB\u2299\u2299\13\2"+
		"&&C\\aac|\u0393\u03ab\u03b3\u03cb\u03d3\u03d3\u03d8\u03d8\u03f2\u03f3"+
		"\n\2&&\62;C\\aac|\u0393\u03ab\u03b3\u03cb\u03d3\u03d3\4\2\f\f\17\17\5"+
		"\2\13\f\16\17\"\"\2\u0283\2\3\3\2\2\2\2\5\3\2\2\2\2\7\3\2\2\2\2\t\3\2"+
		"\2\2\2\13\3\2\2\2\2\r\3\2\2\2\2\17\3\2\2\2\2\21\3\2\2\2\2\23\3\2\2\2\2"+
		"\25\3\2\2\2\2\27\3\2\2\2\2\31\3\2\2\2\2\33\3\2\2\2\2\35\3\2\2\2\2\37\3"+
		"\2\2\2\2!\3\2\2\2\2#\3\2\2\2\2%\3\2\2\2\2\'\3\2\2\2\2)\3\2\2\2\2+\3\2"+
		"\2\2\2-\3\2\2\2\2/\3\2\2\2\2\61\3\2\2\2\2\63\3\2\2\2\2\65\3\2\2\2\2\67"+
		"\3\2\2\2\29\3\2\2\2\2;\3\2\2\2\2A\3\2\2\2\2C\3\2\2\2\2O\3\2\2\2\2Q\3\2"+
		"\2\2\2S\3\2\2\2\2U\3\2\2\2\2W\3\2\2\2\2Y\3\2\2\2\2[\3\2\2\2\2]\3\2\2\2"+
		"\2_\3\2\2\2\2a\3\2\2\2\2c\3\2\2\2\2e\3\2\2\2\2g\3\2\2\2\2i\3\2\2\2\2k"+
		"\3\2\2\2\2m\3\2\2\2\2o\3\2\2\2\2q\3\2\2\2\2s\3\2\2\2\2u\3\2\2\2\2w\3\2"+
		"\2\2\2y\3\2\2\2\2{\3\2\2\2\2}\3\2\2\2\2\177\3\2\2\2\2\u0081\3\2\2\2\2"+
		"\u0083\3\2\2\2\2\u0085\3\2\2\2\2\u0087\3\2\2\2\2\u0089\3\2\2\2\2\u008b"+
		"\3\2\2\2\2\u008d\3\2\2\2\2\u008f\3\2\2\2\2\u0091\3\2\2\2\2\u0093\3\2\2"+
		"\2\2\u0095\3\2\2\2\2\u0097\3\2\2\2\2\u0099\3\2\2\2\2\u009b\3\2\2\2\2\u009d"+
		"\3\2\2\2\2\u009f\3\2\2\2\2\u00a1\3\2\2\2\2\u00a3\3\2\2\2\2\u00a9\3\2\2"+
		"\2\2\u00ab\3\2\2\2\2\u00ad\3\2\2\2\2\u00af\3\2\2\2\3\u00b1\3\2\2\2\5\u00b3"+
		"\3\2\2\2\7\u00b5\3\2\2\2\t\u00b7\3\2\2\2\13\u00b9\3\2\2\2\r\u00bb\3\2"+
		"\2\2\17\u00c1\3\2\2\2\21\u00c3\3\2\2\2\23\u00ca\3\2\2\2\25\u00d2\3\2\2"+
		"\2\27\u00d9\3\2\2\2\31\u00db\3\2\2\2\33\u00e1\3\2\2\2\35\u00e7\3\2\2\2"+
		"\37\u00ec\3\2\2\2!\u00f2\3\2\2\2#\u00f4\3\2\2\2%\u00fb\3\2\2\2\'\u00fe"+
		"\3\2\2\2)\u0103\3\2\2\2+\u0106\3\2\2\2-\u0112\3\2\2\2/\u0114\3\2\2\2\61"+
		"\u011b\3\2\2\2\63\u0120\3\2\2\2\65\u0124\3\2\2\2\67\u012b\3\2\2\29\u012f"+
		"\3\2\2\2;\u0133\3\2\2\2=\u013c\3\2\2\2?\u0142\3\2\2\2A\u0146\3\2\2\2C"+
		"\u0148\3\2\2\2E\u0151\3\2\2\2G\u0153\3\2\2\2I\u015e\3\2\2\2K\u0161\3\2"+
		"\2\2M\u0167\3\2\2\2O\u0169\3\2\2\2Q\u016b\3\2\2\2S\u016d\3\2\2\2U\u016f"+
		"\3\2\2\2W\u0171\3\2\2\2Y\u0176\3\2\2\2[\u017b\3\2\2\2]\u0180\3\2\2\2_"+
		"\u0182\3\2\2\2a\u0184\3\2\2\2c\u0186\3\2\2\2e\u0189\3\2\2\2g\u018b\3\2"+
		"\2\2i\u018e\3\2\2\2k\u0190\3\2\2\2m\u0192\3\2\2\2o\u0194\3\2\2\2q\u019b"+
		"\3\2\2\2s\u01a2\3\2\2\2u\u01a7\3\2\2\2w\u01ac\3\2\2\2y\u01b1\3\2\2\2{"+
		"\u01b3\3\2\2\2}\u01b5\3\2\2\2\177\u01ba\3\2\2\2\u0081\u01bf\3\2\2\2\u0083"+
		"\u01c1\3\2\2\2\u0085\u01c3\3\2\2\2\u0087\u01c5\3\2\2\2\u0089\u01c7\3\2"+
		"\2\2\u008b\u01c9\3\2\2\2\u008d\u01cb\3\2\2\2\u008f\u01d0\3\2\2\2\u0091"+
		"\u01d2\3\2\2\2\u0093\u01d4\3\2\2\2\u0095\u01d6\3\2\2\2\u0097\u01d8\3\2"+
		"\2\2\u0099\u01da\3\2\2\2\u009b\u01dc\3\2\2\2\u009d\u01f2\3\2\2\2\u009f"+
		"\u01f4\3\2\2\2\u00a1\u01fb\3\2\2\2\u00a3\u01fe\3\2\2\2\u00a5\u0219\3\2"+
		"\2\2\u00a7\u021b\3\2\2\2\u00a9\u0222\3\2\2\2\u00ab\u022c\3\2\2\2\u00ad"+
		"\u0232\3\2\2\2\u00af\u0240\3\2\2\2\u00b1\u00b2\7}\2\2\u00b2\4\3\2\2\2"+
		"\u00b3\u00b4\7\177\2\2\u00b4\6\3\2\2\2\u00b5\u00b6\7+\2\2\u00b6\b\3\2"+
		"\2\2\u00b7\u00b8\7*\2\2\u00b8\n\3\2\2\2\u00b9\u00ba\7A\2\2\u00ba\f\3\2"+
		"\2\2\u00bb\u00bc\7\u220a\2\2\u00bc\16\3\2\2\2\u00bd\u00c2\5\27\f\2\u00be"+
		"\u00c2\5\25\13\2\u00bf\u00c2\5-\27\2\u00c0\u00c2\5!\21\2\u00c1\u00bd\3"+
		"\2\2\2\u00c1\u00be\3\2\2\2\u00c1\u00bf\3\2\2\2\u00c1\u00c0\3\2\2\2\u00c2"+
		"\20\3\2\2\2\u00c3\u00c4\7c\2\2\u00c4\u00c5\7u\2\2\u00c5\u00c6\7u\2\2\u00c6"+
		"\u00c7\7g\2\2\u00c7\u00c8\7t\2\2\u00c8\u00c9\7v\2\2\u00c9\22\3\2\2\2\u00ca"+
		"\u00cb\7\u22aa\2\2\u00cb\24\3\2\2\2\u00cc\u00cd\7h\2\2\u00cd\u00ce\7c"+
		"\2\2\u00ce\u00cf\7n\2\2\u00cf\u00d0\7u\2\2\u00d0\u00d3\7g\2\2\u00d1\u00d3"+
		"\7\u22a7\2\2\u00d2\u00cc\3\2\2\2\u00d2\u00d1\3\2\2\2\u00d3\26\3\2\2\2"+
		"\u00d4\u00d5\7v\2\2\u00d5\u00d6\7t\2\2\u00d6\u00d7\7w\2\2\u00d7\u00da"+
		"\7g\2\2\u00d8\u00da\7\u22a6\2\2\u00d9\u00d4\3\2\2\2\u00d9\u00d8\3\2\2"+
		"\2\u00da\30\3\2\2\2\u00db\u00dc\7d\2\2\u00dc\u00dd\7n\2\2\u00dd\u00de"+
		"\7q\2\2\u00de\u00df\7e\2\2\u00df\u00e0\7m\2\2\u00e0\32\3\2\2\2\u00e1\u00e2"+
		"\7n\2\2\u00e2\u00e3\7q\2\2\u00e3\u00e4\7e\2\2\u00e4\u00e5\7c\2\2\u00e5"+
		"\u00e6\7n\2\2\u00e6\34\3\2\2\2\u00e7\u00e8\7d\2\2\u00e8\u00e9\7q\2\2\u00e9"+
		"\u00ea\7f\2\2\u00ea\u00eb\7{\2\2\u00eb\36\3\2\2\2\u00ec\u00ed\7e\2\2\u00ed"+
		"\u00ee\7c\2\2\u00ee\u00ef\7v\2\2\u00ef\u00f0\7e\2\2\u00f0\u00f1\7j\2\2"+
		"\u00f1 \3\2\2\2\u00f2\u00f3\7K\2\2\u00f3\"\3\2\2\2\u00f4\u00f5\7f\2\2"+
		"\u00f5\u00f6\7g\2\2\u00f6\u00f7\7h\2\2\u00f7\u00f8\7k\2\2\u00f8\u00f9"+
		"\7p\2\2\u00f9\u00fa\7g\2\2\u00fa$\3\2\2\2\u00fb\u00fc\7f\2\2\u00fc\u00fd"+
		"\7q\2\2\u00fd&\3\2\2\2\u00fe\u00ff\7g\2\2\u00ff\u0100\7n\2\2\u0100\u0101"+
		"\7u\2\2\u0101\u0102\7g\2\2\u0102(\3\2\2\2\u0103\u0104\7k\2\2\u0104\u0105"+
		"\7h\2\2\u0105*\3\2\2\2\u0106\u0107\7o\2\2\u0107\u0108\7q\2\2\u0108\u0109"+
		"\7f\2\2\u0109\u010a\7w\2\2\u010a\u010b\7n\2\2\u010b\u010c\7g\2\2\u010c"+
		",\3\2\2\2\u010d\u010e\7p\2\2\u010e\u010f\7w\2\2\u010f\u0110\7n\2\2\u0110"+
		"\u0113\7n\2\2\u0111\u0113\7\u2207\2\2\u0112\u010d\3\2\2\2\u0112\u0111"+
		"\3\2\2\2\u0113.\3\2\2\2\u0114\u0115\7u\2\2\u0115\u0116\7y\2\2\u0116\u0117"+
		"\7k\2\2\u0117\u0118\7v\2\2\u0118\u0119\7e\2\2\u0119\u011a\7j\2\2\u011a"+
		"\60\3\2\2\2\u011b\u011c\7v\2\2\u011c\u011d\7j\2\2\u011d\u011e\7g\2\2\u011e"+
		"\u011f\7p\2\2\u011f\62\3\2\2\2\u0120\u0121\7v\2\2\u0121\u0122\7t\2\2\u0122"+
		"\u0123\7{\2\2\u0123\64\3\2\2\2\u0124\u0125\7y\2\2\u0125\u0126\7j\2\2\u0126"+
		"\u0127\7k\2\2\u0127\u0128\7n\2\2\u0128\u0129\7g\2\2\u0129\66\3\2\2\2\u012a"+
		"\u012c\t\2\2\2\u012b\u012a\3\2\2\2\u012c\u012d\3\2\2\2\u012d\u012b\3\2"+
		"\2\2\u012d\u012e\3\2\2\2\u012e8\3\2\2\2\u012f\u0130\5\67\34\2\u0130\u0131"+
		"\7\60\2\2\u0131\u0132\5\67\34\2\u0132:\3\2\2\2\u0133\u013a\59\35\2\u0134"+
		"\u0136\5=\37\2\u0135\u0137\5? \2\u0136\u0135\3\2\2\2\u0136\u0137\3\2\2"+
		"\2\u0137\u0138\3\2\2\2\u0138\u0139\5\67\34\2\u0139\u013b\3\2\2\2\u013a"+
		"\u0134\3\2\2\2\u013a\u013b\3\2\2\2\u013b<\3\2\2\2\u013c\u013d\t\3\2\2"+
		"\u013d>\3\2\2\2\u013e\u0143\5e\63\2\u013f\u0143\5\u0095K\2\u0140\u0143"+
		"\5i\65\2\u0141\u0143\5\u0093J\2\u0142\u013e\3\2\2\2\u0142\u013f\3\2\2"+
		"\2\u0142\u0140\3\2\2\2\u0142\u0141\3\2\2\2\u0143@\3\2\2\2\u0144\u0147"+
		"\5\27\f\2\u0145\u0147\5\25\13\2\u0146\u0144\3\2\2\2\u0146\u0145\3\2\2"+
		"\2\u0147B\3\2\2\2\u0148\u014a\7)\2\2\u0149\u014b\5K&\2\u014a\u0149\3\2"+
		"\2\2\u014a\u014b\3\2\2\2\u014b\u014c\3\2\2\2\u014c\u014d\7)\2\2\u014d"+
		"D\3\2\2\2\u014e\u014f\7^\2\2\u014f\u0152\t\4\2\2\u0150\u0152\5G$\2\u0151"+
		"\u014e\3\2\2\2\u0151\u0150\3\2\2\2\u0152F\3\2\2\2\u0153\u0155\7^\2\2\u0154"+
		"\u0156\7w\2\2\u0155\u0154\3\2\2\2\u0156\u0157\3\2\2\2\u0157\u0155\3\2"+
		"\2\2\u0157\u0158\3\2\2\2\u0158\u0159\3\2\2\2\u0159\u015a\5I%\2\u015a\u015b"+
		"\5I%\2\u015b\u015c\5I%\2\u015c\u015d\5I%\2\u015dH\3\2\2\2\u015e\u015f"+
		"\t\5\2\2\u015fJ\3\2\2\2\u0160\u0162\5M\'\2\u0161\u0160\3\2\2\2\u0162\u0163"+
		"\3\2\2\2\u0163\u0161\3\2\2\2\u0163\u0164\3\2\2\2\u0164L\3\2\2\2\u0165"+
		"\u0168\n\6\2\2\u0166\u0168\5E#\2\u0167\u0165\3\2\2\2\u0167\u0166\3\2\2"+
		"\2\u0168N\3\2\2\2\u0169\u016a\7]\2\2\u016aP\3\2\2\2\u016b\u016c\7_\2\2"+
		"\u016cR\3\2\2\2\u016d\u016e\7.\2\2\u016eT\3\2\2\2\u016f\u0170\7<\2\2\u0170"+
		"V\3\2\2\2\u0171\u0172\7=\2\2\u0172X\3\2\2\2\u0173\u0174\7]\2\2\u0174\u0177"+
		"\7~\2\2\u0175\u0177\7\u27e8\2\2\u0176\u0173\3\2\2\2\u0176\u0175\3\2\2"+
		"\2\u0177Z\3\2\2\2\u0178\u0179\7~\2\2\u0179\u017c\7_\2\2\u017a\u017c\7"+
		"\u27e9\2\2\u017b\u0178\3\2\2\2\u017b\u017a\3\2\2\2\u017c\\\3\2\2\2\u017d"+
		"\u017e\7/\2\2\u017e\u0181\7@\2\2\u017f\u0181\7\u2194\2\2\u0180\u017d\3"+
		"\2\2\2\u0180\u017f\3\2\2\2\u0181^\3\2\2\2\u0182\u0183\t\7\2\2\u0183`\3"+
		"\2\2\2\u0184\u0185\t\b\2\2\u0185b\3\2\2\2\u0186\u0187\7-\2\2\u0187\u0188"+
		"\7-\2\2\u0188d\3\2\2\2\u0189\u018a\7-\2\2\u018af\3\2\2\2\u018b\u018c\7"+
		"/\2\2\u018c\u018d\7/\2\2\u018dh\3\2\2\2\u018e\u018f\7/\2\2\u018fj\3\2"+
		"\2\2\u0190\u0191\7>\2\2\u0191l\3\2\2\2\u0192\u0193\7@\2\2\u0193n\3\2\2"+
		"\2\u0194\u0195\7?\2\2\u0195p\3\2\2\2\u0196\u0197\7>\2\2\u0197\u019c\7"+
		"?\2\2\u0198\u019c\7\u2266\2\2\u0199\u019a\7?\2\2\u019a\u019c\7>\2\2\u019b"+
		"\u0196\3\2\2\2\u019b\u0198\3\2\2\2\u019b\u0199\3\2\2\2\u019cr\3\2\2\2"+
		"\u019d\u019e\7@\2\2\u019e\u01a3\7?\2\2\u019f\u01a3\7\u2267\2\2\u01a0\u01a1"+
		"\7?\2\2\u01a1\u01a3\7@\2\2\u01a2\u019d\3\2\2\2\u01a2\u019f\3\2\2\2\u01a2"+
		"\u01a0\3\2\2\2\u01a3t\3\2\2\2\u01a4\u01a5\7?\2\2\u01a5\u01a8\7?\2\2\u01a6"+
		"\u01a8\7\u2263\2\2\u01a7\u01a4\3\2\2\2\u01a7\u01a6\3\2\2\2\u01a8v\3\2"+
		"\2\2\u01a9\u01aa\7#\2\2\u01aa\u01ad\7?\2\2\u01ab\u01ad\7\u2262\2\2\u01ac"+
		"\u01a9\3\2\2\2\u01ac\u01ab\3\2\2\2\u01adx\3\2\2\2\u01ae\u01af\7?\2\2\u01af"+
		"\u01b2\7\u0080\2\2\u01b0\u01b2\7\u224a\2\2\u01b1\u01ae\3\2\2\2\u01b1\u01b0"+
		"\3\2\2\2\u01b2z\3\2\2\2\u01b3\u01b4\t\t\2\2\u01b4|\3\2\2\2\u01b5\u01b6"+
		"\7`\2\2\u01b6~\3\2\2\2\u01b7\u01b8\7(\2\2\u01b8\u01bb\7(\2\2\u01b9\u01bb"+
		"\t\n\2\2\u01ba\u01b7\3\2\2\2\u01ba\u01b9\3\2\2\2\u01bb\u0080\3\2\2\2\u01bc"+
		"\u01bd\7~\2\2\u01bd\u01c0\7~\2\2\u01be\u01c0\t\13\2\2\u01bf\u01bc\3\2"+
		"\2\2\u01bf\u01be\3\2\2\2\u01c0\u0082\3\2\2\2\u01c1\u01c2\7b\2\2\u01c2"+
		"\u0084\3\2\2\2\u01c3\u01c4\7\'\2\2\u01c4\u0086\3\2\2\2\u01c5\u01c6\7\u0080"+
		"\2\2\u01c6\u0088\3\2\2\2\u01c7\u01c8\7^\2\2\u01c8\u008a\3\2\2\2\u01c9"+
		"\u01ca\7%\2\2\u01ca\u008c\3\2\2\2\u01cb\u01cc\7~\2\2\u01cc\u008e\3\2\2"+
		"\2\u01cd\u01ce\7\u0080\2\2\u01ce\u01d1\7~\2\2\u01cf\u01d1\7\u2243\2\2"+
		"\u01d0\u01cd\3\2\2\2\u01d0\u01cf\3\2\2\2\u01d1\u0090\3\2\2\2\u01d2\u01d3"+
		"\7\60\2\2\u01d3\u0092\3\2\2\2\u01d4\u01d5\7\u00b1\2\2\u01d5\u0094\3\2"+
		"\2\2\u01d6\u01d7\7\u207c\2\2\u01d7\u0096\3\2\2\2\u01d8\u01d9\7\u230c\2"+
		"\2\u01d9\u0098\3\2\2\2\u01da\u01db\7\u230a\2\2\u01db\u009a\3\2\2\2\u01dc"+
		"\u01dd\t\f\2\2\u01dd\u009c\3\2\2\2\u01de\u01f3\7\u2256\2\2\u01df\u01e0"+
		"\7<\2\2\u01e0\u01f3\7?\2\2\u01e1\u01f3\7\u2257\2\2\u01e2\u01e3\7?\2\2"+
		"\u01e3\u01f3\7<\2\2\u01e4\u01e5\7-\2\2\u01e5\u01f3\7?\2\2\u01e6\u01e7"+
		"\7/\2\2\u01e7\u01f3\7?\2\2\u01e8\u01e9\5_\60\2\u01e9\u01ea\7?\2\2\u01ea"+
		"\u01f3\3\2\2\2\u01eb\u01ec\5a\61\2\u01ec\u01ed\7?\2\2\u01ed\u01f3\3\2"+
		"\2\2\u01ee\u01ef\7\'\2\2\u01ef\u01f3\7?\2\2\u01f0\u01f1\7`\2\2\u01f1\u01f3"+
		"\7?\2\2\u01f2\u01de\3\2\2\2\u01f2\u01df\3\2\2\2\u01f2\u01e1\3\2\2\2\u01f2"+
		"\u01e2\3\2\2\2\u01f2\u01e4\3\2\2\2\u01f2\u01e6\3\2\2\2\u01f2\u01e8\3\2"+
		"\2\2\u01f2\u01eb\3\2\2\2\u01f2\u01ee\3\2\2\2\u01f2\u01f0\3\2\2\2\u01f3"+
		"\u009e\3\2\2\2\u01f4\u01f8\t\r\2\2\u01f5\u01f7\t\16\2\2\u01f6\u01f5\3"+
		"\2\2\2\u01f7\u01fa\3\2\2\2\u01f8\u01f6\3\2\2\2\u01f8\u01f9\3\2\2\2\u01f9"+
		"\u00a0\3\2\2\2\u01fa\u01f8\3\2\2\2\u01fb\u01fc\5\u00a7T\2\u01fc\u01fd"+
		"\7*\2\2\u01fd\u00a2\3\2\2\2\u01fe\u0204\5\u009bN\2\u01ff\u0205\5\u00a5"+
		"S\2\u0200\u0205\5\u00a7T\2\u0201\u0202\5\u00a1Q\2\u0202\u0203\7+\2\2\u0203"+
		"\u0205\3\2\2\2\u0204\u01ff\3\2\2\2\u0204\u0200\3\2\2\2\u0204\u0201\3\2"+
		"\2\2\u0205\u00a4\3\2\2\2\u0206\u021a\5_\60\2\u0207\u021a\5a\61\2\u0208"+
		"\u021a\5e\63\2\u0209\u021a\5i\65\2\u020a\u021a\5k\66\2\u020b\u021a\5q"+
		"9\2\u020c\u021a\5m\67\2\u020d\u021a\5}?\2\u020e\u021a\5q9\2\u020f\u021a"+
		"\5s:\2\u0210\u021a\5u;\2\u0211\u021a\5w<\2\u0212\u021a\5\177@\2\u0213"+
		"\u021a\5\u0081A\2\u0214\u021a\5\u0085C\2\u0215\u021a\5\u0087D\2\u0216"+
		"\u021a\5\u008fH\2\u0217\u021a\5{>\2\u0218\u021a\5y=\2\u0219\u0206\3\2"+
		"\2\2\u0219\u0207\3\2\2\2\u0219\u0208\3\2\2\2\u0219\u0209\3\2\2\2\u0219"+
		"\u020a\3\2\2\2\u0219\u020b\3\2\2\2\u0219\u020c\3\2\2\2\u0219\u020d\3\2"+
		"\2\2\u0219\u020e\3\2\2\2\u0219\u020f\3\2\2\2\u0219\u0210\3\2\2\2\u0219"+
		"\u0211\3\2\2\2\u0219\u0212\3\2\2\2\u0219\u0213\3\2\2\2\u0219\u0214\3\2"+
		"\2\2\u0219\u0215\3\2\2\2\u0219\u0216\3\2\2\2\u0219\u0217\3\2\2\2\u0219"+
		"\u0218\3\2\2\2\u021a\u00a6\3\2\2\2\u021b\u021f\t\r\2\2\u021c\u021e\t\16"+
		"\2\2\u021d\u021c\3\2\2\2\u021e\u0221\3\2\2\2\u021f\u021d\3\2\2\2\u021f"+
		"\u0220\3\2\2\2\u0220\u00a8\3\2\2\2\u0221\u021f\3\2\2\2\u0222\u0223\7@"+
		"\2\2\u0223\u0224\7@\2\2\u0224\u0228\3\2\2\2\u0225\u0227\n\17\2\2\u0226"+
		"\u0225\3\2\2\2\u0227\u022a\3\2\2\2\u0228\u0226\3\2\2\2\u0228\u0229\3\2"+
		"\2\2\u0229\u00aa\3\2\2\2\u022a\u0228\3\2\2\2\u022b\u022d\t\20\2\2\u022c"+
		"\u022b\3\2\2\2\u022d\u022e\3\2\2\2\u022e\u022c\3\2\2\2\u022e\u022f\3\2"+
		"\2\2\u022f\u0230\3\2\2\2\u0230\u0231\bV\2\2\u0231\u00ac\3\2\2\2\u0232"+
		"\u0233\7\61\2\2\u0233\u0234\7,\2\2\u0234\u0238\3\2\2\2\u0235\u0237\13"+
		"\2\2\2\u0236\u0235\3\2\2\2\u0237\u023a\3\2\2\2\u0238\u0239\3\2\2\2\u0238"+
		"\u0236\3\2\2\2\u0239\u023b\3\2\2\2\u023a\u0238\3\2\2\2\u023b\u023c\7,"+
		"\2\2\u023c\u023d\7\61\2\2\u023d\u023e\3\2\2\2\u023e\u023f\bW\2\2\u023f"+
		"\u00ae\3\2\2\2\u0240\u0241\7\61\2\2\u0241\u0242\7\61\2\2\u0242\u0246\3"+
		"\2\2\2\u0243\u0245\n\17\2\2\u0244\u0243\3\2\2\2\u0245\u0248\3\2\2\2\u0246"+
		"\u0244\3\2\2\2\u0246\u0247\3\2\2\2\u0247\u0249\3\2\2\2\u0248\u0246\3\2"+
		"\2\2\u0249\u024a\bX\2\2\u024a\u00b0\3\2\2\2%\2\u00c1\u00d2\u00d9\u0112"+
		"\u012d\u0136\u013a\u0142\u0146\u014a\u0151\u0157\u0163\u0167\u0176\u017b"+
		"\u0180\u019b\u01a2\u01a7\u01ac\u01b1\u01ba\u01bf\u01d0\u01f2\u01f8\u0204"+
		"\u0219\u021f\u0228\u022e\u0238\u0246\3\b\2\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}