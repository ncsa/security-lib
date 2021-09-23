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
		Percent=57, Tilde=58, Backslash=59, Hash=60, Stile=61, TildeRight=62, 
		StemDot=63, UnaryMinus=64, UnaryPlus=65, Floor=66, Ceiling=67, FunctionMarker=68, 
		ASSIGN=69, Identifier=70, FuncStart=71, F_REF=72, FDOC=73, WS=74, COMMENT=75, 
		LINE_COMMENT=76;
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
			"And", "Or", "Backtick", "Percent", "Tilde", "Backslash", "Hash", "Stile", 
			"TildeRight", "StemDot", "UnaryMinus", "UnaryPlus", "Floor", "Ceiling", 
			"FunctionMarker", "ASSIGN", "Identifier", "FuncStart", "F_REF", "AllOps", 
			"FUNCTION_NAME", "FDOC", "WS", "COMMENT", "LINE_COMMENT"
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
			"'~'", "'\\'", "'#'", "'|'", null, "'.'", "'\u00AF'", "'\u207A'", "'\u230A'", 
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
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2N\u023f\b\1\4\2\t"+
		"\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13"+
		"\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31\t\31"+
		"\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4 \t \4!"+
		"\t!\4\"\t\"\4#\t#\4$\t$\4%\t%\4&\t&\4\'\t\'\4(\t(\4)\t)\4*\t*\4+\t+\4"+
		",\t,\4-\t-\4.\t.\4/\t/\4\60\t\60\4\61\t\61\4\62\t\62\4\63\t\63\4\64\t"+
		"\64\4\65\t\65\4\66\t\66\4\67\t\67\48\t8\49\t9\4:\t:\4;\t;\4<\t<\4=\t="+
		"\4>\t>\4?\t?\4@\t@\4A\tA\4B\tB\4C\tC\4D\tD\4E\tE\4F\tF\4G\tG\4H\tH\4I"+
		"\tI\4J\tJ\4K\tK\4L\tL\4M\tM\4N\tN\4O\tO\4P\tP\4Q\tQ\4R\tR\4S\tS\4T\tT"+
		"\4U\tU\4V\tV\3\2\3\2\3\3\3\3\3\4\3\4\3\5\3\5\3\6\3\6\3\7\3\7\3\7\3\7\5"+
		"\7\u00bc\n\7\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\t\3\t\3\n\3\n\3\n\3\n\3\n\3"+
		"\n\5\n\u00cd\n\n\3\13\3\13\3\13\3\13\3\13\5\13\u00d4\n\13\3\f\3\f\3\f"+
		"\3\f\3\f\3\f\3\r\3\r\3\r\3\r\3\r\3\16\3\16\3\16\3\16\3\16\3\16\3\17\3"+
		"\17\3\20\3\20\3\20\3\20\3\20\3\20\3\20\3\21\3\21\3\21\3\22\3\22\3\22\3"+
		"\22\3\22\3\23\3\23\3\23\3\24\3\24\3\24\3\24\3\24\3\24\3\24\3\25\3\25\3"+
		"\25\3\25\3\25\5\25\u0107\n\25\3\26\3\26\3\26\3\26\3\26\3\26\3\26\3\27"+
		"\3\27\3\27\3\27\3\27\3\30\3\30\3\30\3\30\3\31\3\31\3\31\3\31\3\31\3\31"+
		"\3\32\6\32\u0120\n\32\r\32\16\32\u0121\3\33\3\33\3\33\3\33\3\34\3\34\3"+
		"\34\5\34\u012b\n\34\3\34\3\34\5\34\u012f\n\34\3\35\3\35\3\36\3\36\3\36"+
		"\3\36\5\36\u0137\n\36\3\37\3\37\5\37\u013b\n\37\3 \3 \5 \u013f\n \3 \3"+
		" \3!\3!\3!\5!\u0146\n!\3\"\3\"\6\"\u014a\n\"\r\"\16\"\u014b\3\"\3\"\3"+
		"\"\3\"\3\"\3#\3#\3$\6$\u0156\n$\r$\16$\u0157\3%\3%\5%\u015c\n%\3&\3&\3"+
		"\'\3\'\3(\3(\3)\3)\3*\3*\3+\3+\3+\5+\u016b\n+\3,\3,\3,\5,\u0170\n,\3-"+
		"\3-\3-\5-\u0175\n-\3.\3.\3/\3/\3\60\3\60\3\60\3\61\3\61\3\62\3\62\3\62"+
		"\3\63\3\63\3\64\3\64\3\65\3\65\3\66\3\66\3\67\3\67\3\67\3\67\3\67\5\67"+
		"\u0190\n\67\38\38\38\38\38\58\u0197\n8\39\39\39\59\u019c\n9\3:\3:\3:\5"+
		":\u01a1\n:\3;\3;\3;\5;\u01a6\n;\3<\3<\3=\3=\3>\3>\3>\5>\u01af\n>\3?\3"+
		"?\3?\5?\u01b4\n?\3@\3@\3A\3A\3B\3B\3C\3C\3D\3D\3E\3E\3F\3F\3F\5F\u01c5"+
		"\nF\3G\3G\3H\3H\3I\3I\3J\3J\3K\3K\3L\3L\3M\3M\3M\3M\3M\3M\3M\3M\3M\3M"+
		"\3M\3M\3M\3M\3M\3M\3M\3M\3M\3M\5M\u01e7\nM\3N\3N\7N\u01eb\nN\fN\16N\u01ee"+
		"\13N\3O\3O\3O\3P\3P\3P\3P\3P\3P\5P\u01f9\nP\3Q\3Q\3Q\3Q\3Q\3Q\3Q\3Q\3"+
		"Q\3Q\3Q\3Q\3Q\3Q\3Q\3Q\3Q\3Q\3Q\5Q\u020e\nQ\3R\3R\7R\u0212\nR\fR\16R\u0215"+
		"\13R\3S\3S\3S\3S\7S\u021b\nS\fS\16S\u021e\13S\3T\6T\u0221\nT\rT\16T\u0222"+
		"\3T\3T\3U\3U\3U\3U\7U\u022b\nU\fU\16U\u022e\13U\3U\3U\3U\3U\3U\3V\3V\3"+
		"V\3V\7V\u0239\nV\fV\16V\u023c\13V\3V\3V\3\u022c\2W\3\3\5\4\7\5\t\6\13"+
		"\7\r\b\17\t\21\n\23\13\25\f\27\r\31\16\33\17\35\20\37\21!\22#\23%\24\'"+
		"\25)\26+\27-\30/\31\61\32\63\33\65\34\67\359\2;\2=\36?\37A\2C\2E\2G\2"+
		"I\2K M!O\"Q#S$U%W&Y\'[(])_*a+c,e-g.i/k\60m\61o\62q\63s\64u\65w\66y\67"+
		"{8}9\177:\u0081;\u0083<\u0085=\u0087>\u0089?\u008b@\u008dA\u008fB\u0091"+
		"C\u0093D\u0095E\u0097F\u0099G\u009bH\u009dI\u009fJ\u00a1\2\u00a3\2\u00a5"+
		"K\u00a7L\u00a9M\u00abN\3\2\21\3\2\62;\4\2GGgg\t\2))^^ddhhppttvv\5\2\62"+
		";CHch\6\2\f\f\17\17))^^\4\2,,\u00d9\u00d9\4\2\61\61\u00f9\u00f9\4\2##"+
		"\u00ae\u00ae\4\2\u2229\u2229\u22c2\u22c2\4\2\u222a\u222a\u22c3\u22c3\4"+
		"\2BB\u2299\u2299\13\2&&C\\aac|\u0393\u03ab\u03b3\u03cb\u03d3\u03d3\u03d8"+
		"\u03d8\u03f2\u03f3\n\2&&\62;C\\aac|\u0393\u03ab\u03b3\u03cb\u03d3\u03d3"+
		"\4\2\f\f\17\17\5\2\13\f\16\17\"\"\2\u0277\2\3\3\2\2\2\2\5\3\2\2\2\2\7"+
		"\3\2\2\2\2\t\3\2\2\2\2\13\3\2\2\2\2\r\3\2\2\2\2\17\3\2\2\2\2\21\3\2\2"+
		"\2\2\23\3\2\2\2\2\25\3\2\2\2\2\27\3\2\2\2\2\31\3\2\2\2\2\33\3\2\2\2\2"+
		"\35\3\2\2\2\2\37\3\2\2\2\2!\3\2\2\2\2#\3\2\2\2\2%\3\2\2\2\2\'\3\2\2\2"+
		"\2)\3\2\2\2\2+\3\2\2\2\2-\3\2\2\2\2/\3\2\2\2\2\61\3\2\2\2\2\63\3\2\2\2"+
		"\2\65\3\2\2\2\2\67\3\2\2\2\2=\3\2\2\2\2?\3\2\2\2\2K\3\2\2\2\2M\3\2\2\2"+
		"\2O\3\2\2\2\2Q\3\2\2\2\2S\3\2\2\2\2U\3\2\2\2\2W\3\2\2\2\2Y\3\2\2\2\2["+
		"\3\2\2\2\2]\3\2\2\2\2_\3\2\2\2\2a\3\2\2\2\2c\3\2\2\2\2e\3\2\2\2\2g\3\2"+
		"\2\2\2i\3\2\2\2\2k\3\2\2\2\2m\3\2\2\2\2o\3\2\2\2\2q\3\2\2\2\2s\3\2\2\2"+
		"\2u\3\2\2\2\2w\3\2\2\2\2y\3\2\2\2\2{\3\2\2\2\2}\3\2\2\2\2\177\3\2\2\2"+
		"\2\u0081\3\2\2\2\2\u0083\3\2\2\2\2\u0085\3\2\2\2\2\u0087\3\2\2\2\2\u0089"+
		"\3\2\2\2\2\u008b\3\2\2\2\2\u008d\3\2\2\2\2\u008f\3\2\2\2\2\u0091\3\2\2"+
		"\2\2\u0093\3\2\2\2\2\u0095\3\2\2\2\2\u0097\3\2\2\2\2\u0099\3\2\2\2\2\u009b"+
		"\3\2\2\2\2\u009d\3\2\2\2\2\u009f\3\2\2\2\2\u00a5\3\2\2\2\2\u00a7\3\2\2"+
		"\2\2\u00a9\3\2\2\2\2\u00ab\3\2\2\2\3\u00ad\3\2\2\2\5\u00af\3\2\2\2\7\u00b1"+
		"\3\2\2\2\t\u00b3\3\2\2\2\13\u00b5\3\2\2\2\r\u00bb\3\2\2\2\17\u00bd\3\2"+
		"\2\2\21\u00c4\3\2\2\2\23\u00cc\3\2\2\2\25\u00d3\3\2\2\2\27\u00d5\3\2\2"+
		"\2\31\u00db\3\2\2\2\33\u00e0\3\2\2\2\35\u00e6\3\2\2\2\37\u00e8\3\2\2\2"+
		"!\u00ef\3\2\2\2#\u00f2\3\2\2\2%\u00f7\3\2\2\2\'\u00fa\3\2\2\2)\u0106\3"+
		"\2\2\2+\u0108\3\2\2\2-\u010f\3\2\2\2/\u0114\3\2\2\2\61\u0118\3\2\2\2\63"+
		"\u011f\3\2\2\2\65\u0123\3\2\2\2\67\u0127\3\2\2\29\u0130\3\2\2\2;\u0136"+
		"\3\2\2\2=\u013a\3\2\2\2?\u013c\3\2\2\2A\u0145\3\2\2\2C\u0147\3\2\2\2E"+
		"\u0152\3\2\2\2G\u0155\3\2\2\2I\u015b\3\2\2\2K\u015d\3\2\2\2M\u015f\3\2"+
		"\2\2O\u0161\3\2\2\2Q\u0163\3\2\2\2S\u0165\3\2\2\2U\u016a\3\2\2\2W\u016f"+
		"\3\2\2\2Y\u0174\3\2\2\2[\u0176\3\2\2\2]\u0178\3\2\2\2_\u017a\3\2\2\2a"+
		"\u017d\3\2\2\2c\u017f\3\2\2\2e\u0182\3\2\2\2g\u0184\3\2\2\2i\u0186\3\2"+
		"\2\2k\u0188\3\2\2\2m\u018f\3\2\2\2o\u0196\3\2\2\2q\u019b\3\2\2\2s\u01a0"+
		"\3\2\2\2u\u01a5\3\2\2\2w\u01a7\3\2\2\2y\u01a9\3\2\2\2{\u01ae\3\2\2\2}"+
		"\u01b3\3\2\2\2\177\u01b5\3\2\2\2\u0081\u01b7\3\2\2\2\u0083\u01b9\3\2\2"+
		"\2\u0085\u01bb\3\2\2\2\u0087\u01bd\3\2\2\2\u0089\u01bf\3\2\2\2\u008b\u01c4"+
		"\3\2\2\2\u008d\u01c6\3\2\2\2\u008f\u01c8\3\2\2\2\u0091\u01ca\3\2\2\2\u0093"+
		"\u01cc\3\2\2\2\u0095\u01ce\3\2\2\2\u0097\u01d0\3\2\2\2\u0099\u01e6\3\2"+
		"\2\2\u009b\u01e8\3\2\2\2\u009d\u01ef\3\2\2\2\u009f\u01f2\3\2\2\2\u00a1"+
		"\u020d\3\2\2\2\u00a3\u020f\3\2\2\2\u00a5\u0216\3\2\2\2\u00a7\u0220\3\2"+
		"\2\2\u00a9\u0226\3\2\2\2\u00ab\u0234\3\2\2\2\u00ad\u00ae\7}\2\2\u00ae"+
		"\4\3\2\2\2\u00af\u00b0\7\177\2\2\u00b0\6\3\2\2\2\u00b1\u00b2\7+\2\2\u00b2"+
		"\b\3\2\2\2\u00b3\u00b4\7*\2\2\u00b4\n\3\2\2\2\u00b5\u00b6\7A\2\2\u00b6"+
		"\f\3\2\2\2\u00b7\u00bc\5\25\13\2\u00b8\u00bc\5\23\n\2\u00b9\u00bc\5)\25"+
		"\2\u00ba\u00bc\5\35\17\2\u00bb\u00b7\3\2\2\2\u00bb\u00b8\3\2\2\2\u00bb"+
		"\u00b9\3\2\2\2\u00bb\u00ba\3\2\2\2\u00bc\16\3\2\2\2\u00bd\u00be\7c\2\2"+
		"\u00be\u00bf\7u\2\2\u00bf\u00c0\7u\2\2\u00c0\u00c1\7g\2\2\u00c1\u00c2"+
		"\7t\2\2\u00c2\u00c3\7v\2\2\u00c3\20\3\2\2\2\u00c4\u00c5\7\u22aa\2\2\u00c5"+
		"\22\3\2\2\2\u00c6\u00c7\7h\2\2\u00c7\u00c8\7c\2\2\u00c8\u00c9\7n\2\2\u00c9"+
		"\u00ca\7u\2\2\u00ca\u00cd\7g\2\2\u00cb\u00cd\7\u22a7\2\2\u00cc\u00c6\3"+
		"\2\2\2\u00cc\u00cb\3\2\2\2\u00cd\24\3\2\2\2\u00ce\u00cf\7v\2\2\u00cf\u00d0"+
		"\7t\2\2\u00d0\u00d1\7w\2\2\u00d1\u00d4\7g\2\2\u00d2\u00d4\7\u22a6\2\2"+
		"\u00d3\u00ce\3\2\2\2\u00d3\u00d2\3\2\2\2\u00d4\26\3\2\2\2\u00d5\u00d6"+
		"\7d\2\2\u00d6\u00d7\7n\2\2\u00d7\u00d8\7q\2\2\u00d8\u00d9\7e\2\2\u00d9"+
		"\u00da\7m\2\2\u00da\30\3\2\2\2\u00db\u00dc\7d\2\2\u00dc\u00dd\7q\2\2\u00dd"+
		"\u00de\7f\2\2\u00de\u00df\7{\2\2\u00df\32\3\2\2\2\u00e0\u00e1\7e\2\2\u00e1"+
		"\u00e2\7c\2\2\u00e2\u00e3\7v\2\2\u00e3\u00e4\7e\2\2\u00e4\u00e5\7j\2\2"+
		"\u00e5\34\3\2\2\2\u00e6\u00e7\7K\2\2\u00e7\36\3\2\2\2\u00e8\u00e9\7f\2"+
		"\2\u00e9\u00ea\7g\2\2\u00ea\u00eb\7h\2\2\u00eb\u00ec\7k\2\2\u00ec\u00ed"+
		"\7p\2\2\u00ed\u00ee\7g\2\2\u00ee \3\2\2\2\u00ef\u00f0\7f\2\2\u00f0\u00f1"+
		"\7q\2\2\u00f1\"\3\2\2\2\u00f2\u00f3\7g\2\2\u00f3\u00f4\7n\2\2\u00f4\u00f5"+
		"\7u\2\2\u00f5\u00f6\7g\2\2\u00f6$\3\2\2\2\u00f7\u00f8\7k\2\2\u00f8\u00f9"+
		"\7h\2\2\u00f9&\3\2\2\2\u00fa\u00fb\7o\2\2\u00fb\u00fc\7q\2\2\u00fc\u00fd"+
		"\7f\2\2\u00fd\u00fe\7w\2\2\u00fe\u00ff\7n\2\2\u00ff\u0100\7g\2\2\u0100"+
		"(\3\2\2\2\u0101\u0102\7p\2\2\u0102\u0103\7w\2\2\u0103\u0104\7n\2\2\u0104"+
		"\u0107\7n\2\2\u0105\u0107\7\u2207\2\2\u0106\u0101\3\2\2\2\u0106\u0105"+
		"\3\2\2\2\u0107*\3\2\2\2\u0108\u0109\7u\2\2\u0109\u010a\7y\2\2\u010a\u010b"+
		"\7k\2\2\u010b\u010c\7v\2\2\u010c\u010d\7e\2\2\u010d\u010e\7j\2\2\u010e"+
		",\3\2\2\2\u010f\u0110\7v\2\2\u0110\u0111\7j\2\2\u0111\u0112\7g\2\2\u0112"+
		"\u0113\7p\2\2\u0113.\3\2\2\2\u0114\u0115\7v\2\2\u0115\u0116\7t\2\2\u0116"+
		"\u0117\7{\2\2\u0117\60\3\2\2\2\u0118\u0119\7y\2\2\u0119\u011a\7j\2\2\u011a"+
		"\u011b\7k\2\2\u011b\u011c\7n\2\2\u011c\u011d\7g\2\2\u011d\62\3\2\2\2\u011e"+
		"\u0120\t\2\2\2\u011f\u011e\3\2\2\2\u0120\u0121\3\2\2\2\u0121\u011f\3\2"+
		"\2\2\u0121\u0122\3\2\2\2\u0122\64\3\2\2\2\u0123\u0124\5\63\32\2\u0124"+
		"\u0125\7\60\2\2\u0125\u0126\5\63\32\2\u0126\66\3\2\2\2\u0127\u012e\5\65"+
		"\33\2\u0128\u012a\59\35\2\u0129\u012b\5;\36\2\u012a\u0129\3\2\2\2\u012a"+
		"\u012b\3\2\2\2\u012b\u012c\3\2\2\2\u012c\u012d\5\63\32\2\u012d\u012f\3"+
		"\2\2\2\u012e\u0128\3\2\2\2\u012e\u012f\3\2\2\2\u012f8\3\2\2\2\u0130\u0131"+
		"\t\3\2\2\u0131:\3\2\2\2\u0132\u0137\5a\61\2\u0133\u0137\5\u0091I\2\u0134"+
		"\u0137\5e\63\2\u0135\u0137\5\u008fH\2\u0136\u0132\3\2\2\2\u0136\u0133"+
		"\3\2\2\2\u0136\u0134\3\2\2\2\u0136\u0135\3\2\2\2\u0137<\3\2\2\2\u0138"+
		"\u013b\5\25\13\2\u0139\u013b\5\23\n\2\u013a\u0138\3\2\2\2\u013a\u0139"+
		"\3\2\2\2\u013b>\3\2\2\2\u013c\u013e\7)\2\2\u013d\u013f\5G$\2\u013e\u013d"+
		"\3\2\2\2\u013e\u013f\3\2\2\2\u013f\u0140\3\2\2\2\u0140\u0141\7)\2\2\u0141"+
		"@\3\2\2\2\u0142\u0143\7^\2\2\u0143\u0146\t\4\2\2\u0144\u0146\5C\"\2\u0145"+
		"\u0142\3\2\2\2\u0145\u0144\3\2\2\2\u0146B\3\2\2\2\u0147\u0149\7^\2\2\u0148"+
		"\u014a\7w\2\2\u0149\u0148\3\2\2\2\u014a\u014b\3\2\2\2\u014b\u0149\3\2"+
		"\2\2\u014b\u014c\3\2\2\2\u014c\u014d\3\2\2\2\u014d\u014e\5E#\2\u014e\u014f"+
		"\5E#\2\u014f\u0150\5E#\2\u0150\u0151\5E#\2\u0151D\3\2\2\2\u0152\u0153"+
		"\t\5\2\2\u0153F\3\2\2\2\u0154\u0156\5I%\2\u0155\u0154\3\2\2\2\u0156\u0157"+
		"\3\2\2\2\u0157\u0155\3\2\2\2\u0157\u0158\3\2\2\2\u0158H\3\2\2\2\u0159"+
		"\u015c\n\6\2\2\u015a\u015c\5A!\2\u015b\u0159\3\2\2\2\u015b\u015a\3\2\2"+
		"\2\u015cJ\3\2\2\2\u015d\u015e\7]\2\2\u015eL\3\2\2\2\u015f\u0160\7_\2\2"+
		"\u0160N\3\2\2\2\u0161\u0162\7.\2\2\u0162P\3\2\2\2\u0163\u0164\7<\2\2\u0164"+
		"R\3\2\2\2\u0165\u0166\7=\2\2\u0166T\3\2\2\2\u0167\u0168\7]\2\2\u0168\u016b"+
		"\7~\2\2\u0169\u016b\7\u27e8\2\2\u016a\u0167\3\2\2\2\u016a\u0169\3\2\2"+
		"\2\u016bV\3\2\2\2\u016c\u016d\7~\2\2\u016d\u0170\7_\2\2\u016e\u0170\7"+
		"\u27e9\2\2\u016f\u016c\3\2\2\2\u016f\u016e\3\2\2\2\u0170X\3\2\2\2\u0171"+
		"\u0172\7/\2\2\u0172\u0175\7@\2\2\u0173\u0175\7\u2194\2\2\u0174\u0171\3"+
		"\2\2\2\u0174\u0173\3\2\2\2\u0175Z\3\2\2\2\u0176\u0177\t\7\2\2\u0177\\"+
		"\3\2\2\2\u0178\u0179\t\b\2\2\u0179^\3\2\2\2\u017a\u017b\7-\2\2\u017b\u017c"+
		"\7-\2\2\u017c`\3\2\2\2\u017d\u017e\7-\2\2\u017eb\3\2\2\2\u017f\u0180\7"+
		"/\2\2\u0180\u0181\7/\2\2\u0181d\3\2\2\2\u0182\u0183\7/\2\2\u0183f\3\2"+
		"\2\2\u0184\u0185\7>\2\2\u0185h\3\2\2\2\u0186\u0187\7@\2\2\u0187j\3\2\2"+
		"\2\u0188\u0189\7?\2\2\u0189l\3\2\2\2\u018a\u018b\7>\2\2\u018b\u0190\7"+
		"?\2\2\u018c\u0190\7\u2266\2\2\u018d\u018e\7?\2\2\u018e\u0190\7>\2\2\u018f"+
		"\u018a\3\2\2\2\u018f\u018c\3\2\2\2\u018f\u018d\3\2\2\2\u0190n\3\2\2\2"+
		"\u0191\u0192\7@\2\2\u0192\u0197\7?\2\2\u0193\u0197\7\u2267\2\2\u0194\u0195"+
		"\7?\2\2\u0195\u0197\7@\2\2\u0196\u0191\3\2\2\2\u0196\u0193\3\2\2\2\u0196"+
		"\u0194\3\2\2\2\u0197p\3\2\2\2\u0198\u0199\7?\2\2\u0199\u019c\7?\2\2\u019a"+
		"\u019c\7\u2263\2\2\u019b\u0198\3\2\2\2\u019b\u019a\3\2\2\2\u019cr\3\2"+
		"\2\2\u019d\u019e\7#\2\2\u019e\u01a1\7?\2\2\u019f\u01a1\7\u2262\2\2\u01a0"+
		"\u019d\3\2\2\2\u01a0\u019f\3\2\2\2\u01a1t\3\2\2\2\u01a2\u01a3\7?\2\2\u01a3"+
		"\u01a6\7\u0080\2\2\u01a4\u01a6\7\u224a\2\2\u01a5\u01a2\3\2\2\2\u01a5\u01a4"+
		"\3\2\2\2\u01a6v\3\2\2\2\u01a7\u01a8\t\t\2\2\u01a8x\3\2\2\2\u01a9\u01aa"+
		"\7`\2\2\u01aaz\3\2\2\2\u01ab\u01ac\7(\2\2\u01ac\u01af\7(\2\2\u01ad\u01af"+
		"\t\n\2\2\u01ae\u01ab\3\2\2\2\u01ae\u01ad\3\2\2\2\u01af|\3\2\2\2\u01b0"+
		"\u01b1\7~\2\2\u01b1\u01b4\7~\2\2\u01b2\u01b4\t\13\2\2\u01b3\u01b0\3\2"+
		"\2\2\u01b3\u01b2\3\2\2\2\u01b4~\3\2\2\2\u01b5\u01b6\7b\2\2\u01b6\u0080"+
		"\3\2\2\2\u01b7\u01b8\7\'\2\2\u01b8\u0082\3\2\2\2\u01b9\u01ba\7\u0080\2"+
		"\2\u01ba\u0084\3\2\2\2\u01bb\u01bc\7^\2\2\u01bc\u0086\3\2\2\2\u01bd\u01be"+
		"\7%\2\2\u01be\u0088\3\2\2\2\u01bf\u01c0\7~\2\2\u01c0\u008a\3\2\2\2\u01c1"+
		"\u01c2\7\u0080\2\2\u01c2\u01c5\7~\2\2\u01c3\u01c5\7\u2243\2\2\u01c4\u01c1"+
		"\3\2\2\2\u01c4\u01c3\3\2\2\2\u01c5\u008c\3\2\2\2\u01c6\u01c7\7\60\2\2"+
		"\u01c7\u008e\3\2\2\2\u01c8\u01c9\7\u00b1\2\2\u01c9\u0090\3\2\2\2\u01ca"+
		"\u01cb\7\u207c\2\2\u01cb\u0092\3\2\2\2\u01cc\u01cd\7\u230c\2\2\u01cd\u0094"+
		"\3\2\2\2\u01ce\u01cf\7\u230a\2\2\u01cf\u0096\3\2\2\2\u01d0\u01d1\t\f\2"+
		"\2\u01d1\u0098\3\2\2\2\u01d2\u01e7\7\u2256\2\2\u01d3\u01d4\7<\2\2\u01d4"+
		"\u01e7\7?\2\2\u01d5\u01e7\7\u2257\2\2\u01d6\u01d7\7?\2\2\u01d7\u01e7\7"+
		"<\2\2\u01d8\u01d9\7-\2\2\u01d9\u01e7\7?\2\2\u01da\u01db\7/\2\2\u01db\u01e7"+
		"\7?\2\2\u01dc\u01dd\5[.\2\u01dd\u01de\7?\2\2\u01de\u01e7\3\2\2\2\u01df"+
		"\u01e0\5]/\2\u01e0\u01e1\7?\2\2\u01e1\u01e7\3\2\2\2\u01e2\u01e3\7\'\2"+
		"\2\u01e3\u01e7\7?\2\2\u01e4\u01e5\7`\2\2\u01e5\u01e7\7?\2\2\u01e6\u01d2"+
		"\3\2\2\2\u01e6\u01d3\3\2\2\2\u01e6\u01d5\3\2\2\2\u01e6\u01d6\3\2\2\2\u01e6"+
		"\u01d8\3\2\2\2\u01e6\u01da\3\2\2\2\u01e6\u01dc\3\2\2\2\u01e6\u01df\3\2"+
		"\2\2\u01e6\u01e2\3\2\2\2\u01e6\u01e4\3\2\2\2\u01e7\u009a\3\2\2\2\u01e8"+
		"\u01ec\t\r\2\2\u01e9\u01eb\t\16\2\2\u01ea\u01e9\3\2\2\2\u01eb\u01ee\3"+
		"\2\2\2\u01ec\u01ea\3\2\2\2\u01ec\u01ed\3\2\2\2\u01ed\u009c\3\2\2\2\u01ee"+
		"\u01ec\3\2\2\2\u01ef\u01f0\5\u00a3R\2\u01f0\u01f1\7*\2\2\u01f1\u009e\3"+
		"\2\2\2\u01f2\u01f8\5\u0097L\2\u01f3\u01f9\5\u00a1Q\2\u01f4\u01f9\5\u00a3"+
		"R\2\u01f5\u01f6\5\u009dO\2\u01f6\u01f7\7+\2\2\u01f7\u01f9\3\2\2\2\u01f8"+
		"\u01f3\3\2\2\2\u01f8\u01f4\3\2\2\2\u01f8\u01f5\3\2\2\2\u01f9\u00a0\3\2"+
		"\2\2\u01fa\u020e\5[.\2\u01fb\u020e\5]/\2\u01fc\u020e\5a\61\2\u01fd\u020e"+
		"\5e\63\2\u01fe\u020e\5g\64\2\u01ff\u020e\5m\67\2\u0200\u020e\5i\65\2\u0201"+
		"\u020e\5y=\2\u0202\u020e\5m\67\2\u0203\u020e\5o8\2\u0204\u020e\5q9\2\u0205"+
		"\u020e\5s:\2\u0206\u020e\5{>\2\u0207\u020e\5}?\2\u0208\u020e\5\u0081A"+
		"\2\u0209\u020e\5\u0083B\2\u020a\u020e\5\u008bF\2\u020b\u020e\5w<\2\u020c"+
		"\u020e\5u;\2\u020d\u01fa\3\2\2\2\u020d\u01fb\3\2\2\2\u020d\u01fc\3\2\2"+
		"\2\u020d\u01fd\3\2\2\2\u020d\u01fe\3\2\2\2\u020d\u01ff\3\2\2\2\u020d\u0200"+
		"\3\2\2\2\u020d\u0201\3\2\2\2\u020d\u0202\3\2\2\2\u020d\u0203\3\2\2\2\u020d"+
		"\u0204\3\2\2\2\u020d\u0205\3\2\2\2\u020d\u0206\3\2\2\2\u020d\u0207\3\2"+
		"\2\2\u020d\u0208\3\2\2\2\u020d\u0209\3\2\2\2\u020d\u020a\3\2\2\2\u020d"+
		"\u020b\3\2\2\2\u020d\u020c\3\2\2\2\u020e\u00a2\3\2\2\2\u020f\u0213\t\r"+
		"\2\2\u0210\u0212\t\16\2\2\u0211\u0210\3\2\2\2\u0212\u0215\3\2\2\2\u0213"+
		"\u0211\3\2\2\2\u0213\u0214\3\2\2\2\u0214\u00a4\3\2\2\2\u0215\u0213\3\2"+
		"\2\2\u0216\u0217\7@\2\2\u0217\u0218\7@\2\2\u0218\u021c\3\2\2\2\u0219\u021b"+
		"\n\17\2\2\u021a\u0219\3\2\2\2\u021b\u021e\3\2\2\2\u021c\u021a\3\2\2\2"+
		"\u021c\u021d\3\2\2\2\u021d\u00a6\3\2\2\2\u021e\u021c\3\2\2\2\u021f\u0221"+
		"\t\20\2\2\u0220\u021f\3\2\2\2\u0221\u0222\3\2\2\2\u0222\u0220\3\2\2\2"+
		"\u0222\u0223\3\2\2\2\u0223\u0224\3\2\2\2\u0224\u0225\bT\2\2\u0225\u00a8"+
		"\3\2\2\2\u0226\u0227\7\61\2\2\u0227\u0228\7,\2\2\u0228\u022c\3\2\2\2\u0229"+
		"\u022b\13\2\2\2\u022a\u0229\3\2\2\2\u022b\u022e\3\2\2\2\u022c\u022d\3"+
		"\2\2\2\u022c\u022a\3\2\2\2\u022d\u022f\3\2\2\2\u022e\u022c\3\2\2\2\u022f"+
		"\u0230\7,\2\2\u0230\u0231\7\61\2\2\u0231\u0232\3\2\2\2\u0232\u0233\bU"+
		"\2\2\u0233\u00aa\3\2\2\2\u0234\u0235\7\61\2\2\u0235\u0236\7\61\2\2\u0236"+
		"\u023a\3\2\2\2\u0237\u0239\n\17\2\2\u0238\u0237\3\2\2\2\u0239\u023c\3"+
		"\2\2\2\u023a\u0238\3\2\2\2\u023a\u023b\3\2\2\2\u023b\u023d\3\2\2\2\u023c"+
		"\u023a\3\2\2\2\u023d\u023e\bV\2\2\u023e\u00ac\3\2\2\2%\2\u00bb\u00cc\u00d3"+
		"\u0106\u0121\u012a\u012e\u0136\u013a\u013e\u0145\u014b\u0157\u015b\u016a"+
		"\u016f\u0174\u018f\u0196\u019b\u01a0\u01a5\u01ae\u01b3\u01c4\u01e6\u01ec"+
		"\u01f8\u020d\u0213\u021c\u0222\u022c\u023a\3\b\2\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}