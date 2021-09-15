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
		T__0=1, T__1=2, T__2=3, T__3=4, T__4=5, ConstantKeywords=6, ASSERT=7, 
		ASSERT2=8, BOOL_FALSE=9, BOOL_TRUE=10, BLOCK=11, BODY=12, CATCH=13, COMPLEX_I=14, 
		DEFINE=15, DO=16, ELSE=17, IF=18, MODULE=19, Null=20, SWITCH=21, THEN=22, 
		TRY=23, WHILE=24, Integer=25, Decimal=26, SCIENTIFIC_NUMBER=27, Bool=28, 
		STRING=29, LeftBracket=30, RightBracket=31, Comma=32, Colon=33, SemiColon=34, 
		LDoubleBracket=35, RDoubleBracket=36, LambdaConnector=37, Times=38, Divide=39, 
		PlusPlus=40, Plus=41, MinusMinus=42, Minus=43, LessThan=44, GreaterThan=45, 
		SingleEqual=46, LessEquals=47, MoreEquals=48, Equals=49, NotEquals=50, 
		RegexMatches=51, LogicalNot=52, Exponentiation=53, And=54, Or=55, Backtick=56, 
		Percent=57, Tilde=58, Backslash=59, Stile=60, TildeRight=61, StemDot=62, 
		UnaryMinus=63, UnaryPlus=64, Floor=65, Ceiling=66, FunctionMarker=67, 
		ASSIGN=68, Identifier=69, FuncStart=70, F_REF=71, FDOC=72, WS=73, COMMENT=74, 
		LINE_COMMENT=75;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	private static String[] makeRuleNames() {
		return new String[] {
			"T__0", "T__1", "T__2", "T__3", "T__4", "ConstantKeywords", "ASSERT", 
			"ASSERT2", "BOOL_FALSE", "BOOL_TRUE", "BLOCK", "BODY", "CATCH", "COMPLEX_I", 
			"DEFINE", "DO", "ELSE", "IF", "MODULE", "Null", "SWITCH", "THEN", "TRY", 
			"WHILE", "Integer", "Decimal", "SCIENTIFIC_NUMBER", "E", "SIGN", "Bool", 
			"STRING", "ESC", "UnicodeEscape", "HexDigit", "StringCharacters", "StringCharacter", 
			"LeftBracket", "RightBracket", "Comma", "Colon", "SemiColon", "LDoubleBracket", 
			"RDoubleBracket", "LambdaConnector", "Times", "Divide", "PlusPlus", "Plus", 
			"MinusMinus", "Minus", "LessThan", "GreaterThan", "SingleEqual", "LessEquals", 
			"MoreEquals", "Equals", "NotEquals", "RegexMatches", "LogicalNot", "Exponentiation", 
			"And", "Or", "Backtick", "Percent", "Tilde", "Backslash", "Stile", "TildeRight", 
			"StemDot", "UnaryMinus", "UnaryPlus", "Floor", "Ceiling", "FunctionMarker", 
			"ASSIGN", "Identifier", "FuncStart", "F_REF", "AllOps", "FUNCTION_NAME", 
			"FDOC", "WS", "COMMENT", "LINE_COMMENT"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'{'", "'}'", "')'", "'('", "'?'", null, "'assert'", "'\u22A8'", 
			null, null, "'block'", "'body'", "'catch'", "'I'", "'define'", "'do'", 
			"'else'", "'if'", "'module'", null, "'switch'", "'then'", "'try'", "'while'", 
			null, null, null, null, null, "'['", "']'", "','", "':'", "';'", null, 
			null, null, null, null, "'++'", "'+'", "'--'", "'-'", "'<'", "'>'", "'='", 
			null, null, null, null, null, null, "'^'", null, null, "'`'", "'%'", 
			"'~'", "'\\'", "'|'", null, "'.'", "'\u00AF'", "'\u207A'", "'\u230A'", 
			"'\u2308'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, null, null, null, null, null, "ConstantKeywords", "ASSERT", "ASSERT2", 
			"BOOL_FALSE", "BOOL_TRUE", "BLOCK", "BODY", "CATCH", "COMPLEX_I", "DEFINE", 
			"DO", "ELSE", "IF", "MODULE", "Null", "SWITCH", "THEN", "TRY", "WHILE", 
			"Integer", "Decimal", "SCIENTIFIC_NUMBER", "Bool", "STRING", "LeftBracket", 
			"RightBracket", "Comma", "Colon", "SemiColon", "LDoubleBracket", "RDoubleBracket", 
			"LambdaConnector", "Times", "Divide", "PlusPlus", "Plus", "MinusMinus", 
			"Minus", "LessThan", "GreaterThan", "SingleEqual", "LessEquals", "MoreEquals", 
			"Equals", "NotEquals", "RegexMatches", "LogicalNot", "Exponentiation", 
			"And", "Or", "Backtick", "Percent", "Tilde", "Backslash", "Stile", "TildeRight", 
			"StemDot", "UnaryMinus", "UnaryPlus", "Floor", "Ceiling", "FunctionMarker", 
			"ASSIGN", "Identifier", "FuncStart", "F_REF", "FDOC", "WS", "COMMENT", 
			"LINE_COMMENT"
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
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2M\u023b\b\1\4\2\t"+
		"\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13"+
		"\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31\t\31"+
		"\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4 \t \4!"+
		"\t!\4\"\t\"\4#\t#\4$\t$\4%\t%\4&\t&\4\'\t\'\4(\t(\4)\t)\4*\t*\4+\t+\4"+
		",\t,\4-\t-\4.\t.\4/\t/\4\60\t\60\4\61\t\61\4\62\t\62\4\63\t\63\4\64\t"+
		"\64\4\65\t\65\4\66\t\66\4\67\t\67\48\t8\49\t9\4:\t:\4;\t;\4<\t<\4=\t="+
		"\4>\t>\4?\t?\4@\t@\4A\tA\4B\tB\4C\tC\4D\tD\4E\tE\4F\tF\4G\tG\4H\tH\4I"+
		"\tI\4J\tJ\4K\tK\4L\tL\4M\tM\4N\tN\4O\tO\4P\tP\4Q\tQ\4R\tR\4S\tS\4T\tT"+
		"\4U\tU\3\2\3\2\3\3\3\3\3\4\3\4\3\5\3\5\3\6\3\6\3\7\3\7\3\7\3\7\5\7\u00ba"+
		"\n\7\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\t\3\t\3\n\3\n\3\n\3\n\3\n\3\n\5\n\u00cb"+
		"\n\n\3\13\3\13\3\13\3\13\3\13\5\13\u00d2\n\13\3\f\3\f\3\f\3\f\3\f\3\f"+
		"\3\r\3\r\3\r\3\r\3\r\3\16\3\16\3\16\3\16\3\16\3\16\3\17\3\17\3\20\3\20"+
		"\3\20\3\20\3\20\3\20\3\20\3\21\3\21\3\21\3\22\3\22\3\22\3\22\3\22\3\23"+
		"\3\23\3\23\3\24\3\24\3\24\3\24\3\24\3\24\3\24\3\25\3\25\3\25\3\25\3\25"+
		"\5\25\u0105\n\25\3\26\3\26\3\26\3\26\3\26\3\26\3\26\3\27\3\27\3\27\3\27"+
		"\3\27\3\30\3\30\3\30\3\30\3\31\3\31\3\31\3\31\3\31\3\31\3\32\6\32\u011e"+
		"\n\32\r\32\16\32\u011f\3\33\3\33\3\33\3\33\3\34\3\34\3\34\5\34\u0129\n"+
		"\34\3\34\3\34\5\34\u012d\n\34\3\35\3\35\3\36\3\36\3\36\3\36\5\36\u0135"+
		"\n\36\3\37\3\37\5\37\u0139\n\37\3 \3 \5 \u013d\n \3 \3 \3!\3!\3!\5!\u0144"+
		"\n!\3\"\3\"\6\"\u0148\n\"\r\"\16\"\u0149\3\"\3\"\3\"\3\"\3\"\3#\3#\3$"+
		"\6$\u0154\n$\r$\16$\u0155\3%\3%\5%\u015a\n%\3&\3&\3\'\3\'\3(\3(\3)\3)"+
		"\3*\3*\3+\3+\3+\5+\u0169\n+\3,\3,\3,\5,\u016e\n,\3-\3-\3-\5-\u0173\n-"+
		"\3.\3.\3/\3/\3\60\3\60\3\60\3\61\3\61\3\62\3\62\3\62\3\63\3\63\3\64\3"+
		"\64\3\65\3\65\3\66\3\66\3\67\3\67\3\67\3\67\3\67\5\67\u018e\n\67\38\3"+
		"8\38\38\38\58\u0195\n8\39\39\39\59\u019a\n9\3:\3:\3:\5:\u019f\n:\3;\3"+
		";\3;\5;\u01a4\n;\3<\3<\3=\3=\3>\3>\3>\5>\u01ad\n>\3?\3?\3?\5?\u01b2\n"+
		"?\3@\3@\3A\3A\3B\3B\3C\3C\3D\3D\3E\3E\3E\5E\u01c1\nE\3F\3F\3G\3G\3H\3"+
		"H\3I\3I\3J\3J\3K\3K\3L\3L\3L\3L\3L\3L\3L\3L\3L\3L\3L\3L\3L\3L\3L\3L\3"+
		"L\3L\3L\3L\5L\u01e3\nL\3M\3M\7M\u01e7\nM\fM\16M\u01ea\13M\3N\3N\3N\3O"+
		"\3O\3O\3O\3O\3O\5O\u01f5\nO\3P\3P\3P\3P\3P\3P\3P\3P\3P\3P\3P\3P\3P\3P"+
		"\3P\3P\3P\3P\3P\5P\u020a\nP\3Q\3Q\7Q\u020e\nQ\fQ\16Q\u0211\13Q\3R\3R\3"+
		"R\3R\7R\u0217\nR\fR\16R\u021a\13R\3S\6S\u021d\nS\rS\16S\u021e\3S\3S\3"+
		"T\3T\3T\3T\7T\u0227\nT\fT\16T\u022a\13T\3T\3T\3T\3T\3T\3U\3U\3U\3U\7U"+
		"\u0235\nU\fU\16U\u0238\13U\3U\3U\3\u0228\2V\3\3\5\4\7\5\t\6\13\7\r\b\17"+
		"\t\21\n\23\13\25\f\27\r\31\16\33\17\35\20\37\21!\22#\23%\24\'\25)\26+"+
		"\27-\30/\31\61\32\63\33\65\34\67\359\2;\2=\36?\37A\2C\2E\2G\2I\2K M!O"+
		"\"Q#S$U%W&Y\'[(])_*a+c,e-g.i/k\60m\61o\62q\63s\64u\65w\66y\67{8}9\177"+
		":\u0081;\u0083<\u0085=\u0087>\u0089?\u008b@\u008dA\u008fB\u0091C\u0093"+
		"D\u0095E\u0097F\u0099G\u009bH\u009dI\u009f\2\u00a1\2\u00a3J\u00a5K\u00a7"+
		"L\u00a9M\3\2\21\3\2\62;\4\2GGgg\t\2))^^ddhhppttvv\5\2\62;CHch\6\2\f\f"+
		"\17\17))^^\4\2,,\u00d9\u00d9\4\2\61\61\u00f9\u00f9\4\2##\u00ae\u00ae\4"+
		"\2\u2229\u2229\u22c2\u22c2\4\2\u222a\u222a\u22c3\u22c3\4\2BB\u2299\u2299"+
		"\13\2%&C\\aac|\u0393\u03ab\u03b3\u03cb\u03d3\u03d3\u03d8\u03d8\u03f2\u03f3"+
		"\n\2%&\62;C\\aac|\u0393\u03ab\u03b3\u03cb\u03d3\u03d3\4\2\f\f\17\17\5"+
		"\2\13\f\16\17\"\"\2\u0273\2\3\3\2\2\2\2\5\3\2\2\2\2\7\3\2\2\2\2\t\3\2"+
		"\2\2\2\13\3\2\2\2\2\r\3\2\2\2\2\17\3\2\2\2\2\21\3\2\2\2\2\23\3\2\2\2\2"+
		"\25\3\2\2\2\2\27\3\2\2\2\2\31\3\2\2\2\2\33\3\2\2\2\2\35\3\2\2\2\2\37\3"+
		"\2\2\2\2!\3\2\2\2\2#\3\2\2\2\2%\3\2\2\2\2\'\3\2\2\2\2)\3\2\2\2\2+\3\2"+
		"\2\2\2-\3\2\2\2\2/\3\2\2\2\2\61\3\2\2\2\2\63\3\2\2\2\2\65\3\2\2\2\2\67"+
		"\3\2\2\2\2=\3\2\2\2\2?\3\2\2\2\2K\3\2\2\2\2M\3\2\2\2\2O\3\2\2\2\2Q\3\2"+
		"\2\2\2S\3\2\2\2\2U\3\2\2\2\2W\3\2\2\2\2Y\3\2\2\2\2[\3\2\2\2\2]\3\2\2\2"+
		"\2_\3\2\2\2\2a\3\2\2\2\2c\3\2\2\2\2e\3\2\2\2\2g\3\2\2\2\2i\3\2\2\2\2k"+
		"\3\2\2\2\2m\3\2\2\2\2o\3\2\2\2\2q\3\2\2\2\2s\3\2\2\2\2u\3\2\2\2\2w\3\2"+
		"\2\2\2y\3\2\2\2\2{\3\2\2\2\2}\3\2\2\2\2\177\3\2\2\2\2\u0081\3\2\2\2\2"+
		"\u0083\3\2\2\2\2\u0085\3\2\2\2\2\u0087\3\2\2\2\2\u0089\3\2\2\2\2\u008b"+
		"\3\2\2\2\2\u008d\3\2\2\2\2\u008f\3\2\2\2\2\u0091\3\2\2\2\2\u0093\3\2\2"+
		"\2\2\u0095\3\2\2\2\2\u0097\3\2\2\2\2\u0099\3\2\2\2\2\u009b\3\2\2\2\2\u009d"+
		"\3\2\2\2\2\u00a3\3\2\2\2\2\u00a5\3\2\2\2\2\u00a7\3\2\2\2\2\u00a9\3\2\2"+
		"\2\3\u00ab\3\2\2\2\5\u00ad\3\2\2\2\7\u00af\3\2\2\2\t\u00b1\3\2\2\2\13"+
		"\u00b3\3\2\2\2\r\u00b9\3\2\2\2\17\u00bb\3\2\2\2\21\u00c2\3\2\2\2\23\u00ca"+
		"\3\2\2\2\25\u00d1\3\2\2\2\27\u00d3\3\2\2\2\31\u00d9\3\2\2\2\33\u00de\3"+
		"\2\2\2\35\u00e4\3\2\2\2\37\u00e6\3\2\2\2!\u00ed\3\2\2\2#\u00f0\3\2\2\2"+
		"%\u00f5\3\2\2\2\'\u00f8\3\2\2\2)\u0104\3\2\2\2+\u0106\3\2\2\2-\u010d\3"+
		"\2\2\2/\u0112\3\2\2\2\61\u0116\3\2\2\2\63\u011d\3\2\2\2\65\u0121\3\2\2"+
		"\2\67\u0125\3\2\2\29\u012e\3\2\2\2;\u0134\3\2\2\2=\u0138\3\2\2\2?\u013a"+
		"\3\2\2\2A\u0143\3\2\2\2C\u0145\3\2\2\2E\u0150\3\2\2\2G\u0153\3\2\2\2I"+
		"\u0159\3\2\2\2K\u015b\3\2\2\2M\u015d\3\2\2\2O\u015f\3\2\2\2Q\u0161\3\2"+
		"\2\2S\u0163\3\2\2\2U\u0168\3\2\2\2W\u016d\3\2\2\2Y\u0172\3\2\2\2[\u0174"+
		"\3\2\2\2]\u0176\3\2\2\2_\u0178\3\2\2\2a\u017b\3\2\2\2c\u017d\3\2\2\2e"+
		"\u0180\3\2\2\2g\u0182\3\2\2\2i\u0184\3\2\2\2k\u0186\3\2\2\2m\u018d\3\2"+
		"\2\2o\u0194\3\2\2\2q\u0199\3\2\2\2s\u019e\3\2\2\2u\u01a3\3\2\2\2w\u01a5"+
		"\3\2\2\2y\u01a7\3\2\2\2{\u01ac\3\2\2\2}\u01b1\3\2\2\2\177\u01b3\3\2\2"+
		"\2\u0081\u01b5\3\2\2\2\u0083\u01b7\3\2\2\2\u0085\u01b9\3\2\2\2\u0087\u01bb"+
		"\3\2\2\2\u0089\u01c0\3\2\2\2\u008b\u01c2\3\2\2\2\u008d\u01c4\3\2\2\2\u008f"+
		"\u01c6\3\2\2\2\u0091\u01c8\3\2\2\2\u0093\u01ca\3\2\2\2\u0095\u01cc\3\2"+
		"\2\2\u0097\u01e2\3\2\2\2\u0099\u01e4\3\2\2\2\u009b\u01eb\3\2\2\2\u009d"+
		"\u01ee\3\2\2\2\u009f\u0209\3\2\2\2\u00a1\u020b\3\2\2\2\u00a3\u0212\3\2"+
		"\2\2\u00a5\u021c\3\2\2\2\u00a7\u0222\3\2\2\2\u00a9\u0230\3\2\2\2\u00ab"+
		"\u00ac\7}\2\2\u00ac\4\3\2\2\2\u00ad\u00ae\7\177\2\2\u00ae\6\3\2\2\2\u00af"+
		"\u00b0\7+\2\2\u00b0\b\3\2\2\2\u00b1\u00b2\7*\2\2\u00b2\n\3\2\2\2\u00b3"+
		"\u00b4\7A\2\2\u00b4\f\3\2\2\2\u00b5\u00ba\5\25\13\2\u00b6\u00ba\5\23\n"+
		"\2\u00b7\u00ba\5)\25\2\u00b8\u00ba\5\35\17\2\u00b9\u00b5\3\2\2\2\u00b9"+
		"\u00b6\3\2\2\2\u00b9\u00b7\3\2\2\2\u00b9\u00b8\3\2\2\2\u00ba\16\3\2\2"+
		"\2\u00bb\u00bc\7c\2\2\u00bc\u00bd\7u\2\2\u00bd\u00be\7u\2\2\u00be\u00bf"+
		"\7g\2\2\u00bf\u00c0\7t\2\2\u00c0\u00c1\7v\2\2\u00c1\20\3\2\2\2\u00c2\u00c3"+
		"\7\u22aa\2\2\u00c3\22\3\2\2\2\u00c4\u00c5\7h\2\2\u00c5\u00c6\7c\2\2\u00c6"+
		"\u00c7\7n\2\2\u00c7\u00c8\7u\2\2\u00c8\u00cb\7g\2\2\u00c9\u00cb\7\u22a7"+
		"\2\2\u00ca\u00c4\3\2\2\2\u00ca\u00c9\3\2\2\2\u00cb\24\3\2\2\2\u00cc\u00cd"+
		"\7v\2\2\u00cd\u00ce\7t\2\2\u00ce\u00cf\7w\2\2\u00cf\u00d2\7g\2\2\u00d0"+
		"\u00d2\7\u22a6\2\2\u00d1\u00cc\3\2\2\2\u00d1\u00d0\3\2\2\2\u00d2\26\3"+
		"\2\2\2\u00d3\u00d4\7d\2\2\u00d4\u00d5\7n\2\2\u00d5\u00d6\7q\2\2\u00d6"+
		"\u00d7\7e\2\2\u00d7\u00d8\7m\2\2\u00d8\30\3\2\2\2\u00d9\u00da\7d\2\2\u00da"+
		"\u00db\7q\2\2\u00db\u00dc\7f\2\2\u00dc\u00dd\7{\2\2\u00dd\32\3\2\2\2\u00de"+
		"\u00df\7e\2\2\u00df\u00e0\7c\2\2\u00e0\u00e1\7v\2\2\u00e1\u00e2\7e\2\2"+
		"\u00e2\u00e3\7j\2\2\u00e3\34\3\2\2\2\u00e4\u00e5\7K\2\2\u00e5\36\3\2\2"+
		"\2\u00e6\u00e7\7f\2\2\u00e7\u00e8\7g\2\2\u00e8\u00e9\7h\2\2\u00e9\u00ea"+
		"\7k\2\2\u00ea\u00eb\7p\2\2\u00eb\u00ec\7g\2\2\u00ec \3\2\2\2\u00ed\u00ee"+
		"\7f\2\2\u00ee\u00ef\7q\2\2\u00ef\"\3\2\2\2\u00f0\u00f1\7g\2\2\u00f1\u00f2"+
		"\7n\2\2\u00f2\u00f3\7u\2\2\u00f3\u00f4\7g\2\2\u00f4$\3\2\2\2\u00f5\u00f6"+
		"\7k\2\2\u00f6\u00f7\7h\2\2\u00f7&\3\2\2\2\u00f8\u00f9\7o\2\2\u00f9\u00fa"+
		"\7q\2\2\u00fa\u00fb\7f\2\2\u00fb\u00fc\7w\2\2\u00fc\u00fd\7n\2\2\u00fd"+
		"\u00fe\7g\2\2\u00fe(\3\2\2\2\u00ff\u0100\7p\2\2\u0100\u0101\7w\2\2\u0101"+
		"\u0102\7n\2\2\u0102\u0105\7n\2\2\u0103\u0105\7\u2207\2\2\u0104\u00ff\3"+
		"\2\2\2\u0104\u0103\3\2\2\2\u0105*\3\2\2\2\u0106\u0107\7u\2\2\u0107\u0108"+
		"\7y\2\2\u0108\u0109\7k\2\2\u0109\u010a\7v\2\2\u010a\u010b\7e\2\2\u010b"+
		"\u010c\7j\2\2\u010c,\3\2\2\2\u010d\u010e\7v\2\2\u010e\u010f\7j\2\2\u010f"+
		"\u0110\7g\2\2\u0110\u0111\7p\2\2\u0111.\3\2\2\2\u0112\u0113\7v\2\2\u0113"+
		"\u0114\7t\2\2\u0114\u0115\7{\2\2\u0115\60\3\2\2\2\u0116\u0117\7y\2\2\u0117"+
		"\u0118\7j\2\2\u0118\u0119\7k\2\2\u0119\u011a\7n\2\2\u011a\u011b\7g\2\2"+
		"\u011b\62\3\2\2\2\u011c\u011e\t\2\2\2\u011d\u011c\3\2\2\2\u011e\u011f"+
		"\3\2\2\2\u011f\u011d\3\2\2\2\u011f\u0120\3\2\2\2\u0120\64\3\2\2\2\u0121"+
		"\u0122\5\63\32\2\u0122\u0123\7\60\2\2\u0123\u0124\5\63\32\2\u0124\66\3"+
		"\2\2\2\u0125\u012c\5\65\33\2\u0126\u0128\59\35\2\u0127\u0129\5;\36\2\u0128"+
		"\u0127\3\2\2\2\u0128\u0129\3\2\2\2\u0129\u012a\3\2\2\2\u012a\u012b\5\63"+
		"\32\2\u012b\u012d\3\2\2\2\u012c\u0126\3\2\2\2\u012c\u012d\3\2\2\2\u012d"+
		"8\3\2\2\2\u012e\u012f\t\3\2\2\u012f:\3\2\2\2\u0130\u0135\5a\61\2\u0131"+
		"\u0135\5\u008fH\2\u0132\u0135\5e\63\2\u0133\u0135\5\u008dG\2\u0134\u0130"+
		"\3\2\2\2\u0134\u0131\3\2\2\2\u0134\u0132\3\2\2\2\u0134\u0133\3\2\2\2\u0135"+
		"<\3\2\2\2\u0136\u0139\5\25\13\2\u0137\u0139\5\23\n\2\u0138\u0136\3\2\2"+
		"\2\u0138\u0137\3\2\2\2\u0139>\3\2\2\2\u013a\u013c\7)\2\2\u013b\u013d\5"+
		"G$\2\u013c\u013b\3\2\2\2\u013c\u013d\3\2\2\2\u013d\u013e\3\2\2\2\u013e"+
		"\u013f\7)\2\2\u013f@\3\2\2\2\u0140\u0141\7^\2\2\u0141\u0144\t\4\2\2\u0142"+
		"\u0144\5C\"\2\u0143\u0140\3\2\2\2\u0143\u0142\3\2\2\2\u0144B\3\2\2\2\u0145"+
		"\u0147\7^\2\2\u0146\u0148\7w\2\2\u0147\u0146\3\2\2\2\u0148\u0149\3\2\2"+
		"\2\u0149\u0147\3\2\2\2\u0149\u014a\3\2\2\2\u014a\u014b\3\2\2\2\u014b\u014c"+
		"\5E#\2\u014c\u014d\5E#\2\u014d\u014e\5E#\2\u014e\u014f\5E#\2\u014fD\3"+
		"\2\2\2\u0150\u0151\t\5\2\2\u0151F\3\2\2\2\u0152\u0154\5I%\2\u0153\u0152"+
		"\3\2\2\2\u0154\u0155\3\2\2\2\u0155\u0153\3\2\2\2\u0155\u0156\3\2\2\2\u0156"+
		"H\3\2\2\2\u0157\u015a\n\6\2\2\u0158\u015a\5A!\2\u0159\u0157\3\2\2\2\u0159"+
		"\u0158\3\2\2\2\u015aJ\3\2\2\2\u015b\u015c\7]\2\2\u015cL\3\2\2\2\u015d"+
		"\u015e\7_\2\2\u015eN\3\2\2\2\u015f\u0160\7.\2\2\u0160P\3\2\2\2\u0161\u0162"+
		"\7<\2\2\u0162R\3\2\2\2\u0163\u0164\7=\2\2\u0164T\3\2\2\2\u0165\u0166\7"+
		"]\2\2\u0166\u0169\7~\2\2\u0167\u0169\7\u27e8\2\2\u0168\u0165\3\2\2\2\u0168"+
		"\u0167\3\2\2\2\u0169V\3\2\2\2\u016a\u016b\7~\2\2\u016b\u016e\7_\2\2\u016c"+
		"\u016e\7\u27e9\2\2\u016d\u016a\3\2\2\2\u016d\u016c\3\2\2\2\u016eX\3\2"+
		"\2\2\u016f\u0170\7/\2\2\u0170\u0173\7@\2\2\u0171\u0173\7\u2194\2\2\u0172"+
		"\u016f\3\2\2\2\u0172\u0171\3\2\2\2\u0173Z\3\2\2\2\u0174\u0175\t\7\2\2"+
		"\u0175\\\3\2\2\2\u0176\u0177\t\b\2\2\u0177^\3\2\2\2\u0178\u0179\7-\2\2"+
		"\u0179\u017a\7-\2\2\u017a`\3\2\2\2\u017b\u017c\7-\2\2\u017cb\3\2\2\2\u017d"+
		"\u017e\7/\2\2\u017e\u017f\7/\2\2\u017fd\3\2\2\2\u0180\u0181\7/\2\2\u0181"+
		"f\3\2\2\2\u0182\u0183\7>\2\2\u0183h\3\2\2\2\u0184\u0185\7@\2\2\u0185j"+
		"\3\2\2\2\u0186\u0187\7?\2\2\u0187l\3\2\2\2\u0188\u0189\7>\2\2\u0189\u018e"+
		"\7?\2\2\u018a\u018e\7\u2266\2\2\u018b\u018c\7?\2\2\u018c\u018e\7>\2\2"+
		"\u018d\u0188\3\2\2\2\u018d\u018a\3\2\2\2\u018d\u018b\3\2\2\2\u018en\3"+
		"\2\2\2\u018f\u0190\7@\2\2\u0190\u0195\7?\2\2\u0191\u0195\7\u2267\2\2\u0192"+
		"\u0193\7?\2\2\u0193\u0195\7@\2\2\u0194\u018f\3\2\2\2\u0194\u0191\3\2\2"+
		"\2\u0194\u0192\3\2\2\2\u0195p\3\2\2\2\u0196\u0197\7?\2\2\u0197\u019a\7"+
		"?\2\2\u0198\u019a\7\u2263\2\2\u0199\u0196\3\2\2\2\u0199\u0198\3\2\2\2"+
		"\u019ar\3\2\2\2\u019b\u019c\7#\2\2\u019c\u019f\7?\2\2\u019d\u019f\7\u2262"+
		"\2\2\u019e\u019b\3\2\2\2\u019e\u019d\3\2\2\2\u019ft\3\2\2\2\u01a0\u01a1"+
		"\7?\2\2\u01a1\u01a4\7\u0080\2\2\u01a2\u01a4\7\u224a\2\2\u01a3\u01a0\3"+
		"\2\2\2\u01a3\u01a2\3\2\2\2\u01a4v\3\2\2\2\u01a5\u01a6\t\t\2\2\u01a6x\3"+
		"\2\2\2\u01a7\u01a8\7`\2\2\u01a8z\3\2\2\2\u01a9\u01aa\7(\2\2\u01aa\u01ad"+
		"\7(\2\2\u01ab\u01ad\t\n\2\2\u01ac\u01a9\3\2\2\2\u01ac\u01ab\3\2\2\2\u01ad"+
		"|\3\2\2\2\u01ae\u01af\7~\2\2\u01af\u01b2\7~\2\2\u01b0\u01b2\t\13\2\2\u01b1"+
		"\u01ae\3\2\2\2\u01b1\u01b0\3\2\2\2\u01b2~\3\2\2\2\u01b3\u01b4\7b\2\2\u01b4"+
		"\u0080\3\2\2\2\u01b5\u01b6\7\'\2\2\u01b6\u0082\3\2\2\2\u01b7\u01b8\7\u0080"+
		"\2\2\u01b8\u0084\3\2\2\2\u01b9\u01ba\7^\2\2\u01ba\u0086\3\2\2\2\u01bb"+
		"\u01bc\7~\2\2\u01bc\u0088\3\2\2\2\u01bd\u01be\7\u0080\2\2\u01be\u01c1"+
		"\7~\2\2\u01bf\u01c1\7\u2243\2\2\u01c0\u01bd\3\2\2\2\u01c0\u01bf\3\2\2"+
		"\2\u01c1\u008a\3\2\2\2\u01c2\u01c3\7\60\2\2\u01c3\u008c\3\2\2\2\u01c4"+
		"\u01c5\7\u00b1\2\2\u01c5\u008e\3\2\2\2\u01c6\u01c7\7\u207c\2\2\u01c7\u0090"+
		"\3\2\2\2\u01c8\u01c9\7\u230c\2\2\u01c9\u0092\3\2\2\2\u01ca\u01cb\7\u230a"+
		"\2\2\u01cb\u0094\3\2\2\2\u01cc\u01cd\t\f\2\2\u01cd\u0096\3\2\2\2\u01ce"+
		"\u01e3\7\u2256\2\2\u01cf\u01d0\7<\2\2\u01d0\u01e3\7?\2\2\u01d1\u01e3\7"+
		"\u2257\2\2\u01d2\u01d3\7?\2\2\u01d3\u01e3\7<\2\2\u01d4\u01d5\7-\2\2\u01d5"+
		"\u01e3\7?\2\2\u01d6\u01d7\7/\2\2\u01d7\u01e3\7?\2\2\u01d8\u01d9\5[.\2"+
		"\u01d9\u01da\7?\2\2\u01da\u01e3\3\2\2\2\u01db\u01dc\5]/\2\u01dc\u01dd"+
		"\7?\2\2\u01dd\u01e3\3\2\2\2\u01de\u01df\7\'\2\2\u01df\u01e3\7?\2\2\u01e0"+
		"\u01e1\7`\2\2\u01e1\u01e3\7?\2\2\u01e2\u01ce\3\2\2\2\u01e2\u01cf\3\2\2"+
		"\2\u01e2\u01d1\3\2\2\2\u01e2\u01d2\3\2\2\2\u01e2\u01d4\3\2\2\2\u01e2\u01d6"+
		"\3\2\2\2\u01e2\u01d8\3\2\2\2\u01e2\u01db\3\2\2\2\u01e2\u01de\3\2\2\2\u01e2"+
		"\u01e0\3\2\2\2\u01e3\u0098\3\2\2\2\u01e4\u01e8\t\r\2\2\u01e5\u01e7\t\16"+
		"\2\2\u01e6\u01e5\3\2\2\2\u01e7\u01ea\3\2\2\2\u01e8\u01e6\3\2\2\2\u01e8"+
		"\u01e9\3\2\2\2\u01e9\u009a\3\2\2\2\u01ea\u01e8\3\2\2\2\u01eb\u01ec\5\u00a1"+
		"Q\2\u01ec\u01ed\7*\2\2\u01ed\u009c\3\2\2\2\u01ee\u01f4\5\u0095K\2\u01ef"+
		"\u01f5\5\u009fP\2\u01f0\u01f5\5\u00a1Q\2\u01f1\u01f2\5\u009bN\2\u01f2"+
		"\u01f3\7+\2\2\u01f3\u01f5\3\2\2\2\u01f4\u01ef\3\2\2\2\u01f4\u01f0\3\2"+
		"\2\2\u01f4\u01f1\3\2\2\2\u01f5\u009e\3\2\2\2\u01f6\u020a\5[.\2\u01f7\u020a"+
		"\5]/\2\u01f8\u020a\5a\61\2\u01f9\u020a\5e\63\2\u01fa\u020a\5g\64\2\u01fb"+
		"\u020a\5m\67\2\u01fc\u020a\5i\65\2\u01fd\u020a\5y=\2\u01fe\u020a\5m\67"+
		"\2\u01ff\u020a\5o8\2\u0200\u020a\5q9\2\u0201\u020a\5s:\2\u0202\u020a\5"+
		"{>\2\u0203\u020a\5}?\2\u0204\u020a\5\u0081A\2\u0205\u020a\5\u0083B\2\u0206"+
		"\u020a\5\u0089E\2\u0207\u020a\5w<\2\u0208\u020a\5u;\2\u0209\u01f6\3\2"+
		"\2\2\u0209\u01f7\3\2\2\2\u0209\u01f8\3\2\2\2\u0209\u01f9\3\2\2\2\u0209"+
		"\u01fa\3\2\2\2\u0209\u01fb\3\2\2\2\u0209\u01fc\3\2\2\2\u0209\u01fd\3\2"+
		"\2\2\u0209\u01fe\3\2\2\2\u0209\u01ff\3\2\2\2\u0209\u0200\3\2\2\2\u0209"+
		"\u0201\3\2\2\2\u0209\u0202\3\2\2\2\u0209\u0203\3\2\2\2\u0209\u0204\3\2"+
		"\2\2\u0209\u0205\3\2\2\2\u0209\u0206\3\2\2\2\u0209\u0207\3\2\2\2\u0209"+
		"\u0208\3\2\2\2\u020a\u00a0\3\2\2\2\u020b\u020f\t\r\2\2\u020c\u020e\t\16"+
		"\2\2\u020d\u020c\3\2\2\2\u020e\u0211\3\2\2\2\u020f\u020d\3\2\2\2\u020f"+
		"\u0210\3\2\2\2\u0210\u00a2\3\2\2\2\u0211\u020f\3\2\2\2\u0212\u0213\7@"+
		"\2\2\u0213\u0214\7@\2\2\u0214\u0218\3\2\2\2\u0215\u0217\n\17\2\2\u0216"+
		"\u0215\3\2\2\2\u0217\u021a\3\2\2\2\u0218\u0216\3\2\2\2\u0218\u0219\3\2"+
		"\2\2\u0219\u00a4\3\2\2\2\u021a\u0218\3\2\2\2\u021b\u021d\t\20\2\2\u021c"+
		"\u021b\3\2\2\2\u021d\u021e\3\2\2\2\u021e\u021c\3\2\2\2\u021e\u021f\3\2"+
		"\2\2\u021f\u0220\3\2\2\2\u0220\u0221\bS\2\2\u0221\u00a6\3\2\2\2\u0222"+
		"\u0223\7\61\2\2\u0223\u0224\7,\2\2\u0224\u0228\3\2\2\2\u0225\u0227\13"+
		"\2\2\2\u0226\u0225\3\2\2\2\u0227\u022a\3\2\2\2\u0228\u0229\3\2\2\2\u0228"+
		"\u0226\3\2\2\2\u0229\u022b\3\2\2\2\u022a\u0228\3\2\2\2\u022b\u022c\7,"+
		"\2\2\u022c\u022d\7\61\2\2\u022d\u022e\3\2\2\2\u022e\u022f\bT\2\2\u022f"+
		"\u00a8\3\2\2\2\u0230\u0231\7\61\2\2\u0231\u0232\7\61\2\2\u0232\u0236\3"+
		"\2\2\2\u0233\u0235\n\17\2\2\u0234\u0233\3\2\2\2\u0235\u0238\3\2\2\2\u0236"+
		"\u0234\3\2\2\2\u0236\u0237\3\2\2\2\u0237\u0239\3\2\2\2\u0238\u0236\3\2"+
		"\2\2\u0239\u023a\bU\2\2\u023a\u00aa\3\2\2\2%\2\u00b9\u00ca\u00d1\u0104"+
		"\u011f\u0128\u012c\u0134\u0138\u013c\u0143\u0149\u0155\u0159\u0168\u016d"+
		"\u0172\u018d\u0194\u0199\u019e\u01a3\u01ac\u01b1\u01c0\u01e2\u01e8\u01f4"+
		"\u0209\u020f\u0218\u021e\u0228\u0236\3\b\2\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}