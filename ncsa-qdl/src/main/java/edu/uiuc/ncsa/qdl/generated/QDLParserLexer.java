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
		ASSERT2=9, BOOL_FALSE=10, BOOL_TRUE=11, BLOCK=12, BODY=13, CATCH=14, COMPLEX_I=15, 
		DEFINE=16, DO=17, ELSE=18, IF=19, MODULE=20, Null=21, SWITCH=22, THEN=23, 
		TRY=24, WHILE=25, Integer=26, Decimal=27, SCIENTIFIC_NUMBER=28, Bool=29, 
		STRING=30, LeftBracket=31, RightBracket=32, Comma=33, Colon=34, SemiColon=35, 
		LDoubleBracket=36, RDoubleBracket=37, LambdaConnector=38, Times=39, Divide=40, 
		PlusPlus=41, Plus=42, MinusMinus=43, Minus=44, LessThan=45, GreaterThan=46, 
		SingleEqual=47, LessEquals=48, MoreEquals=49, Equals=50, NotEquals=51, 
		RegexMatches=52, LogicalNot=53, Exponentiation=54, And=55, Or=56, Backtick=57, 
		Percent=58, Tilde=59, Backslash=60, Hash=61, Stile=62, TildeRight=63, 
		StemDot=64, UnaryMinus=65, UnaryPlus=66, Floor=67, Ceiling=68, FunctionMarker=69, 
		ASSIGN=70, Identifier=71, FuncStart=72, F_REF=73, FDOC=74, WS=75, COMMENT=76, 
		LINE_COMMENT=77;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	private static String[] makeRuleNames() {
		return new String[] {
			"T__0", "T__1", "T__2", "T__3", "T__4", "T__5", "ConstantKeywords", "ASSERT", 
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
			null, "'{'", "'}'", "')'", "'('", "'?'", "'\u2208'", null, "'assert'", 
			"'\u22A8'", null, null, "'block'", "'body'", "'catch'", "'I'", "'define'", 
			"'do'", "'else'", "'if'", "'module'", null, "'switch'", "'then'", "'try'", 
			"'while'", null, null, null, null, null, "'['", "']'", "','", "':'", 
			"';'", null, null, null, null, null, "'++'", "'+'", "'--'", "'-'", "'<'", 
			"'>'", "'='", null, null, null, null, null, null, "'^'", null, null, 
			"'`'", "'%'", "'~'", "'\\'", "'#'", "'|'", null, "'.'", "'\u00AF'", "'\u207A'", 
			"'\u230A'", "'\u2308'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, null, null, null, null, null, null, "ConstantKeywords", "ASSERT", 
			"ASSERT2", "BOOL_FALSE", "BOOL_TRUE", "BLOCK", "BODY", "CATCH", "COMPLEX_I", 
			"DEFINE", "DO", "ELSE", "IF", "MODULE", "Null", "SWITCH", "THEN", "TRY", 
			"WHILE", "Integer", "Decimal", "SCIENTIFIC_NUMBER", "Bool", "STRING", 
			"LeftBracket", "RightBracket", "Comma", "Colon", "SemiColon", "LDoubleBracket", 
			"RDoubleBracket", "LambdaConnector", "Times", "Divide", "PlusPlus", "Plus", 
			"MinusMinus", "Minus", "LessThan", "GreaterThan", "SingleEqual", "LessEquals", 
			"MoreEquals", "Equals", "NotEquals", "RegexMatches", "LogicalNot", "Exponentiation", 
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
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2O\u0243\b\1\4\2\t"+
		"\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13"+
		"\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31\t\31"+
		"\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4 \t \4!"+
		"\t!\4\"\t\"\4#\t#\4$\t$\4%\t%\4&\t&\4\'\t\'\4(\t(\4)\t)\4*\t*\4+\t+\4"+
		",\t,\4-\t-\4.\t.\4/\t/\4\60\t\60\4\61\t\61\4\62\t\62\4\63\t\63\4\64\t"+
		"\64\4\65\t\65\4\66\t\66\4\67\t\67\48\t8\49\t9\4:\t:\4;\t;\4<\t<\4=\t="+
		"\4>\t>\4?\t?\4@\t@\4A\tA\4B\tB\4C\tC\4D\tD\4E\tE\4F\tF\4G\tG\4H\tH\4I"+
		"\tI\4J\tJ\4K\tK\4L\tL\4M\tM\4N\tN\4O\tO\4P\tP\4Q\tQ\4R\tR\4S\tS\4T\tT"+
		"\4U\tU\4V\tV\4W\tW\3\2\3\2\3\3\3\3\3\4\3\4\3\5\3\5\3\6\3\6\3\7\3\7\3\b"+
		"\3\b\3\b\3\b\5\b\u00c0\n\b\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\n\3\n\3\13\3"+
		"\13\3\13\3\13\3\13\3\13\5\13\u00d1\n\13\3\f\3\f\3\f\3\f\3\f\5\f\u00d8"+
		"\n\f\3\r\3\r\3\r\3\r\3\r\3\r\3\16\3\16\3\16\3\16\3\16\3\17\3\17\3\17\3"+
		"\17\3\17\3\17\3\20\3\20\3\21\3\21\3\21\3\21\3\21\3\21\3\21\3\22\3\22\3"+
		"\22\3\23\3\23\3\23\3\23\3\23\3\24\3\24\3\24\3\25\3\25\3\25\3\25\3\25\3"+
		"\25\3\25\3\26\3\26\3\26\3\26\3\26\5\26\u010b\n\26\3\27\3\27\3\27\3\27"+
		"\3\27\3\27\3\27\3\30\3\30\3\30\3\30\3\30\3\31\3\31\3\31\3\31\3\32\3\32"+
		"\3\32\3\32\3\32\3\32\3\33\6\33\u0124\n\33\r\33\16\33\u0125\3\34\3\34\3"+
		"\34\3\34\3\35\3\35\3\35\5\35\u012f\n\35\3\35\3\35\5\35\u0133\n\35\3\36"+
		"\3\36\3\37\3\37\3\37\3\37\5\37\u013b\n\37\3 \3 \5 \u013f\n \3!\3!\5!\u0143"+
		"\n!\3!\3!\3\"\3\"\3\"\5\"\u014a\n\"\3#\3#\6#\u014e\n#\r#\16#\u014f\3#"+
		"\3#\3#\3#\3#\3$\3$\3%\6%\u015a\n%\r%\16%\u015b\3&\3&\5&\u0160\n&\3\'\3"+
		"\'\3(\3(\3)\3)\3*\3*\3+\3+\3,\3,\3,\5,\u016f\n,\3-\3-\3-\5-\u0174\n-\3"+
		".\3.\3.\5.\u0179\n.\3/\3/\3\60\3\60\3\61\3\61\3\61\3\62\3\62\3\63\3\63"+
		"\3\63\3\64\3\64\3\65\3\65\3\66\3\66\3\67\3\67\38\38\38\38\38\58\u0194"+
		"\n8\39\39\39\39\39\59\u019b\n9\3:\3:\3:\5:\u01a0\n:\3;\3;\3;\5;\u01a5"+
		"\n;\3<\3<\3<\5<\u01aa\n<\3=\3=\3>\3>\3?\3?\3?\5?\u01b3\n?\3@\3@\3@\5@"+
		"\u01b8\n@\3A\3A\3B\3B\3C\3C\3D\3D\3E\3E\3F\3F\3G\3G\3G\5G\u01c9\nG\3H"+
		"\3H\3I\3I\3J\3J\3K\3K\3L\3L\3M\3M\3N\3N\3N\3N\3N\3N\3N\3N\3N\3N\3N\3N"+
		"\3N\3N\3N\3N\3N\3N\3N\3N\5N\u01eb\nN\3O\3O\7O\u01ef\nO\fO\16O\u01f2\13"+
		"O\3P\3P\3P\3Q\3Q\3Q\3Q\3Q\3Q\5Q\u01fd\nQ\3R\3R\3R\3R\3R\3R\3R\3R\3R\3"+
		"R\3R\3R\3R\3R\3R\3R\3R\3R\3R\5R\u0212\nR\3S\3S\7S\u0216\nS\fS\16S\u0219"+
		"\13S\3T\3T\3T\3T\7T\u021f\nT\fT\16T\u0222\13T\3U\6U\u0225\nU\rU\16U\u0226"+
		"\3U\3U\3V\3V\3V\3V\7V\u022f\nV\fV\16V\u0232\13V\3V\3V\3V\3V\3V\3W\3W\3"+
		"W\3W\7W\u023d\nW\fW\16W\u0240\13W\3W\3W\3\u0230\2X\3\3\5\4\7\5\t\6\13"+
		"\7\r\b\17\t\21\n\23\13\25\f\27\r\31\16\33\17\35\20\37\21!\22#\23%\24\'"+
		"\25)\26+\27-\30/\31\61\32\63\33\65\34\67\359\36;\2=\2?\37A C\2E\2G\2I"+
		"\2K\2M!O\"Q#S$U%W&Y\'[(])_*a+c,e-g.i/k\60m\61o\62q\63s\64u\65w\66y\67"+
		"{8}9\177:\u0081;\u0083<\u0085=\u0087>\u0089?\u008b@\u008dA\u008fB\u0091"+
		"C\u0093D\u0095E\u0097F\u0099G\u009bH\u009dI\u009fJ\u00a1K\u00a3\2\u00a5"+
		"\2\u00a7L\u00a9M\u00abN\u00adO\3\2\21\3\2\62;\4\2GGgg\t\2))^^ddhhpptt"+
		"vv\5\2\62;CHch\6\2\f\f\17\17))^^\4\2,,\u00d9\u00d9\4\2\61\61\u00f9\u00f9"+
		"\4\2##\u00ae\u00ae\4\2\u2229\u2229\u22c2\u22c2\4\2\u222a\u222a\u22c3\u22c3"+
		"\4\2BB\u2299\u2299\13\2&&C\\aac|\u0393\u03ab\u03b3\u03cb\u03d3\u03d3\u03d8"+
		"\u03d8\u03f2\u03f3\n\2&&\62;C\\aac|\u0393\u03ab\u03b3\u03cb\u03d3\u03d3"+
		"\4\2\f\f\17\17\5\2\13\f\16\17\"\"\2\u027b\2\3\3\2\2\2\2\5\3\2\2\2\2\7"+
		"\3\2\2\2\2\t\3\2\2\2\2\13\3\2\2\2\2\r\3\2\2\2\2\17\3\2\2\2\2\21\3\2\2"+
		"\2\2\23\3\2\2\2\2\25\3\2\2\2\2\27\3\2\2\2\2\31\3\2\2\2\2\33\3\2\2\2\2"+
		"\35\3\2\2\2\2\37\3\2\2\2\2!\3\2\2\2\2#\3\2\2\2\2%\3\2\2\2\2\'\3\2\2\2"+
		"\2)\3\2\2\2\2+\3\2\2\2\2-\3\2\2\2\2/\3\2\2\2\2\61\3\2\2\2\2\63\3\2\2\2"+
		"\2\65\3\2\2\2\2\67\3\2\2\2\29\3\2\2\2\2?\3\2\2\2\2A\3\2\2\2\2M\3\2\2\2"+
		"\2O\3\2\2\2\2Q\3\2\2\2\2S\3\2\2\2\2U\3\2\2\2\2W\3\2\2\2\2Y\3\2\2\2\2["+
		"\3\2\2\2\2]\3\2\2\2\2_\3\2\2\2\2a\3\2\2\2\2c\3\2\2\2\2e\3\2\2\2\2g\3\2"+
		"\2\2\2i\3\2\2\2\2k\3\2\2\2\2m\3\2\2\2\2o\3\2\2\2\2q\3\2\2\2\2s\3\2\2\2"+
		"\2u\3\2\2\2\2w\3\2\2\2\2y\3\2\2\2\2{\3\2\2\2\2}\3\2\2\2\2\177\3\2\2\2"+
		"\2\u0081\3\2\2\2\2\u0083\3\2\2\2\2\u0085\3\2\2\2\2\u0087\3\2\2\2\2\u0089"+
		"\3\2\2\2\2\u008b\3\2\2\2\2\u008d\3\2\2\2\2\u008f\3\2\2\2\2\u0091\3\2\2"+
		"\2\2\u0093\3\2\2\2\2\u0095\3\2\2\2\2\u0097\3\2\2\2\2\u0099\3\2\2\2\2\u009b"+
		"\3\2\2\2\2\u009d\3\2\2\2\2\u009f\3\2\2\2\2\u00a1\3\2\2\2\2\u00a7\3\2\2"+
		"\2\2\u00a9\3\2\2\2\2\u00ab\3\2\2\2\2\u00ad\3\2\2\2\3\u00af\3\2\2\2\5\u00b1"+
		"\3\2\2\2\7\u00b3\3\2\2\2\t\u00b5\3\2\2\2\13\u00b7\3\2\2\2\r\u00b9\3\2"+
		"\2\2\17\u00bf\3\2\2\2\21\u00c1\3\2\2\2\23\u00c8\3\2\2\2\25\u00d0\3\2\2"+
		"\2\27\u00d7\3\2\2\2\31\u00d9\3\2\2\2\33\u00df\3\2\2\2\35\u00e4\3\2\2\2"+
		"\37\u00ea\3\2\2\2!\u00ec\3\2\2\2#\u00f3\3\2\2\2%\u00f6\3\2\2\2\'\u00fb"+
		"\3\2\2\2)\u00fe\3\2\2\2+\u010a\3\2\2\2-\u010c\3\2\2\2/\u0113\3\2\2\2\61"+
		"\u0118\3\2\2\2\63\u011c\3\2\2\2\65\u0123\3\2\2\2\67\u0127\3\2\2\29\u012b"+
		"\3\2\2\2;\u0134\3\2\2\2=\u013a\3\2\2\2?\u013e\3\2\2\2A\u0140\3\2\2\2C"+
		"\u0149\3\2\2\2E\u014b\3\2\2\2G\u0156\3\2\2\2I\u0159\3\2\2\2K\u015f\3\2"+
		"\2\2M\u0161\3\2\2\2O\u0163\3\2\2\2Q\u0165\3\2\2\2S\u0167\3\2\2\2U\u0169"+
		"\3\2\2\2W\u016e\3\2\2\2Y\u0173\3\2\2\2[\u0178\3\2\2\2]\u017a\3\2\2\2_"+
		"\u017c\3\2\2\2a\u017e\3\2\2\2c\u0181\3\2\2\2e\u0183\3\2\2\2g\u0186\3\2"+
		"\2\2i\u0188\3\2\2\2k\u018a\3\2\2\2m\u018c\3\2\2\2o\u0193\3\2\2\2q\u019a"+
		"\3\2\2\2s\u019f\3\2\2\2u\u01a4\3\2\2\2w\u01a9\3\2\2\2y\u01ab\3\2\2\2{"+
		"\u01ad\3\2\2\2}\u01b2\3\2\2\2\177\u01b7\3\2\2\2\u0081\u01b9\3\2\2\2\u0083"+
		"\u01bb\3\2\2\2\u0085\u01bd\3\2\2\2\u0087\u01bf\3\2\2\2\u0089\u01c1\3\2"+
		"\2\2\u008b\u01c3\3\2\2\2\u008d\u01c8\3\2\2\2\u008f\u01ca\3\2\2\2\u0091"+
		"\u01cc\3\2\2\2\u0093\u01ce\3\2\2\2\u0095\u01d0\3\2\2\2\u0097\u01d2\3\2"+
		"\2\2\u0099\u01d4\3\2\2\2\u009b\u01ea\3\2\2\2\u009d\u01ec\3\2\2\2\u009f"+
		"\u01f3\3\2\2\2\u00a1\u01f6\3\2\2\2\u00a3\u0211\3\2\2\2\u00a5\u0213\3\2"+
		"\2\2\u00a7\u021a\3\2\2\2\u00a9\u0224\3\2\2\2\u00ab\u022a\3\2\2\2\u00ad"+
		"\u0238\3\2\2\2\u00af\u00b0\7}\2\2\u00b0\4\3\2\2\2\u00b1\u00b2\7\177\2"+
		"\2\u00b2\6\3\2\2\2\u00b3\u00b4\7+\2\2\u00b4\b\3\2\2\2\u00b5\u00b6\7*\2"+
		"\2\u00b6\n\3\2\2\2\u00b7\u00b8\7A\2\2\u00b8\f\3\2\2\2\u00b9\u00ba\7\u220a"+
		"\2\2\u00ba\16\3\2\2\2\u00bb\u00c0\5\27\f\2\u00bc\u00c0\5\25\13\2\u00bd"+
		"\u00c0\5+\26\2\u00be\u00c0\5\37\20\2\u00bf\u00bb\3\2\2\2\u00bf\u00bc\3"+
		"\2\2\2\u00bf\u00bd\3\2\2\2\u00bf\u00be\3\2\2\2\u00c0\20\3\2\2\2\u00c1"+
		"\u00c2\7c\2\2\u00c2\u00c3\7u\2\2\u00c3\u00c4\7u\2\2\u00c4\u00c5\7g\2\2"+
		"\u00c5\u00c6\7t\2\2\u00c6\u00c7\7v\2\2\u00c7\22\3\2\2\2\u00c8\u00c9\7"+
		"\u22aa\2\2\u00c9\24\3\2\2\2\u00ca\u00cb\7h\2\2\u00cb\u00cc\7c\2\2\u00cc"+
		"\u00cd\7n\2\2\u00cd\u00ce\7u\2\2\u00ce\u00d1\7g\2\2\u00cf\u00d1\7\u22a7"+
		"\2\2\u00d0\u00ca\3\2\2\2\u00d0\u00cf\3\2\2\2\u00d1\26\3\2\2\2\u00d2\u00d3"+
		"\7v\2\2\u00d3\u00d4\7t\2\2\u00d4\u00d5\7w\2\2\u00d5\u00d8\7g\2\2\u00d6"+
		"\u00d8\7\u22a6\2\2\u00d7\u00d2\3\2\2\2\u00d7\u00d6\3\2\2\2\u00d8\30\3"+
		"\2\2\2\u00d9\u00da\7d\2\2\u00da\u00db\7n\2\2\u00db\u00dc\7q\2\2\u00dc"+
		"\u00dd\7e\2\2\u00dd\u00de\7m\2\2\u00de\32\3\2\2\2\u00df\u00e0\7d\2\2\u00e0"+
		"\u00e1\7q\2\2\u00e1\u00e2\7f\2\2\u00e2\u00e3\7{\2\2\u00e3\34\3\2\2\2\u00e4"+
		"\u00e5\7e\2\2\u00e5\u00e6\7c\2\2\u00e6\u00e7\7v\2\2\u00e7\u00e8\7e\2\2"+
		"\u00e8\u00e9\7j\2\2\u00e9\36\3\2\2\2\u00ea\u00eb\7K\2\2\u00eb \3\2\2\2"+
		"\u00ec\u00ed\7f\2\2\u00ed\u00ee\7g\2\2\u00ee\u00ef\7h\2\2\u00ef\u00f0"+
		"\7k\2\2\u00f0\u00f1\7p\2\2\u00f1\u00f2\7g\2\2\u00f2\"\3\2\2\2\u00f3\u00f4"+
		"\7f\2\2\u00f4\u00f5\7q\2\2\u00f5$\3\2\2\2\u00f6\u00f7\7g\2\2\u00f7\u00f8"+
		"\7n\2\2\u00f8\u00f9\7u\2\2\u00f9\u00fa\7g\2\2\u00fa&\3\2\2\2\u00fb\u00fc"+
		"\7k\2\2\u00fc\u00fd\7h\2\2\u00fd(\3\2\2\2\u00fe\u00ff\7o\2\2\u00ff\u0100"+
		"\7q\2\2\u0100\u0101\7f\2\2\u0101\u0102\7w\2\2\u0102\u0103\7n\2\2\u0103"+
		"\u0104\7g\2\2\u0104*\3\2\2\2\u0105\u0106\7p\2\2\u0106\u0107\7w\2\2\u0107"+
		"\u0108\7n\2\2\u0108\u010b\7n\2\2\u0109\u010b\7\u2207\2\2\u010a\u0105\3"+
		"\2\2\2\u010a\u0109\3\2\2\2\u010b,\3\2\2\2\u010c\u010d\7u\2\2\u010d\u010e"+
		"\7y\2\2\u010e\u010f\7k\2\2\u010f\u0110\7v\2\2\u0110\u0111\7e\2\2\u0111"+
		"\u0112\7j\2\2\u0112.\3\2\2\2\u0113\u0114\7v\2\2\u0114\u0115\7j\2\2\u0115"+
		"\u0116\7g\2\2\u0116\u0117\7p\2\2\u0117\60\3\2\2\2\u0118\u0119\7v\2\2\u0119"+
		"\u011a\7t\2\2\u011a\u011b\7{\2\2\u011b\62\3\2\2\2\u011c\u011d\7y\2\2\u011d"+
		"\u011e\7j\2\2\u011e\u011f\7k\2\2\u011f\u0120\7n\2\2\u0120\u0121\7g\2\2"+
		"\u0121\64\3\2\2\2\u0122\u0124\t\2\2\2\u0123\u0122\3\2\2\2\u0124\u0125"+
		"\3\2\2\2\u0125\u0123\3\2\2\2\u0125\u0126\3\2\2\2\u0126\66\3\2\2\2\u0127"+
		"\u0128\5\65\33\2\u0128\u0129\7\60\2\2\u0129\u012a\5\65\33\2\u012a8\3\2"+
		"\2\2\u012b\u0132\5\67\34\2\u012c\u012e\5;\36\2\u012d\u012f\5=\37\2\u012e"+
		"\u012d\3\2\2\2\u012e\u012f\3\2\2\2\u012f\u0130\3\2\2\2\u0130\u0131\5\65"+
		"\33\2\u0131\u0133\3\2\2\2\u0132\u012c\3\2\2\2\u0132\u0133\3\2\2\2\u0133"+
		":\3\2\2\2\u0134\u0135\t\3\2\2\u0135<\3\2\2\2\u0136\u013b\5c\62\2\u0137"+
		"\u013b\5\u0093J\2\u0138\u013b\5g\64\2\u0139\u013b\5\u0091I\2\u013a\u0136"+
		"\3\2\2\2\u013a\u0137\3\2\2\2\u013a\u0138\3\2\2\2\u013a\u0139\3\2\2\2\u013b"+
		">\3\2\2\2\u013c\u013f\5\27\f\2\u013d\u013f\5\25\13\2\u013e\u013c\3\2\2"+
		"\2\u013e\u013d\3\2\2\2\u013f@\3\2\2\2\u0140\u0142\7)\2\2\u0141\u0143\5"+
		"I%\2\u0142\u0141\3\2\2\2\u0142\u0143\3\2\2\2\u0143\u0144\3\2\2\2\u0144"+
		"\u0145\7)\2\2\u0145B\3\2\2\2\u0146\u0147\7^\2\2\u0147\u014a\t\4\2\2\u0148"+
		"\u014a\5E#\2\u0149\u0146\3\2\2\2\u0149\u0148\3\2\2\2\u014aD\3\2\2\2\u014b"+
		"\u014d\7^\2\2\u014c\u014e\7w\2\2\u014d\u014c\3\2\2\2\u014e\u014f\3\2\2"+
		"\2\u014f\u014d\3\2\2\2\u014f\u0150\3\2\2\2\u0150\u0151\3\2\2\2\u0151\u0152"+
		"\5G$\2\u0152\u0153\5G$\2\u0153\u0154\5G$\2\u0154\u0155\5G$\2\u0155F\3"+
		"\2\2\2\u0156\u0157\t\5\2\2\u0157H\3\2\2\2\u0158\u015a\5K&\2\u0159\u0158"+
		"\3\2\2\2\u015a\u015b\3\2\2\2\u015b\u0159\3\2\2\2\u015b\u015c\3\2\2\2\u015c"+
		"J\3\2\2\2\u015d\u0160\n\6\2\2\u015e\u0160\5C\"\2\u015f\u015d\3\2\2\2\u015f"+
		"\u015e\3\2\2\2\u0160L\3\2\2\2\u0161\u0162\7]\2\2\u0162N\3\2\2\2\u0163"+
		"\u0164\7_\2\2\u0164P\3\2\2\2\u0165\u0166\7.\2\2\u0166R\3\2\2\2\u0167\u0168"+
		"\7<\2\2\u0168T\3\2\2\2\u0169\u016a\7=\2\2\u016aV\3\2\2\2\u016b\u016c\7"+
		"]\2\2\u016c\u016f\7~\2\2\u016d\u016f\7\u27e8\2\2\u016e\u016b\3\2\2\2\u016e"+
		"\u016d\3\2\2\2\u016fX\3\2\2\2\u0170\u0171\7~\2\2\u0171\u0174\7_\2\2\u0172"+
		"\u0174\7\u27e9\2\2\u0173\u0170\3\2\2\2\u0173\u0172\3\2\2\2\u0174Z\3\2"+
		"\2\2\u0175\u0176\7/\2\2\u0176\u0179\7@\2\2\u0177\u0179\7\u2194\2\2\u0178"+
		"\u0175\3\2\2\2\u0178\u0177\3\2\2\2\u0179\\\3\2\2\2\u017a\u017b\t\7\2\2"+
		"\u017b^\3\2\2\2\u017c\u017d\t\b\2\2\u017d`\3\2\2\2\u017e\u017f\7-\2\2"+
		"\u017f\u0180\7-\2\2\u0180b\3\2\2\2\u0181\u0182\7-\2\2\u0182d\3\2\2\2\u0183"+
		"\u0184\7/\2\2\u0184\u0185\7/\2\2\u0185f\3\2\2\2\u0186\u0187\7/\2\2\u0187"+
		"h\3\2\2\2\u0188\u0189\7>\2\2\u0189j\3\2\2\2\u018a\u018b\7@\2\2\u018bl"+
		"\3\2\2\2\u018c\u018d\7?\2\2\u018dn\3\2\2\2\u018e\u018f\7>\2\2\u018f\u0194"+
		"\7?\2\2\u0190\u0194\7\u2266\2\2\u0191\u0192\7?\2\2\u0192\u0194\7>\2\2"+
		"\u0193\u018e\3\2\2\2\u0193\u0190\3\2\2\2\u0193\u0191\3\2\2\2\u0194p\3"+
		"\2\2\2\u0195\u0196\7@\2\2\u0196\u019b\7?\2\2\u0197\u019b\7\u2267\2\2\u0198"+
		"\u0199\7?\2\2\u0199\u019b\7@\2\2\u019a\u0195\3\2\2\2\u019a\u0197\3\2\2"+
		"\2\u019a\u0198\3\2\2\2\u019br\3\2\2\2\u019c\u019d\7?\2\2\u019d\u01a0\7"+
		"?\2\2\u019e\u01a0\7\u2263\2\2\u019f\u019c\3\2\2\2\u019f\u019e\3\2\2\2"+
		"\u01a0t\3\2\2\2\u01a1\u01a2\7#\2\2\u01a2\u01a5\7?\2\2\u01a3\u01a5\7\u2262"+
		"\2\2\u01a4\u01a1\3\2\2\2\u01a4\u01a3\3\2\2\2\u01a5v\3\2\2\2\u01a6\u01a7"+
		"\7?\2\2\u01a7\u01aa\7\u0080\2\2\u01a8\u01aa\7\u224a\2\2\u01a9\u01a6\3"+
		"\2\2\2\u01a9\u01a8\3\2\2\2\u01aax\3\2\2\2\u01ab\u01ac\t\t\2\2\u01acz\3"+
		"\2\2\2\u01ad\u01ae\7`\2\2\u01ae|\3\2\2\2\u01af\u01b0\7(\2\2\u01b0\u01b3"+
		"\7(\2\2\u01b1\u01b3\t\n\2\2\u01b2\u01af\3\2\2\2\u01b2\u01b1\3\2\2\2\u01b3"+
		"~\3\2\2\2\u01b4\u01b5\7~\2\2\u01b5\u01b8\7~\2\2\u01b6\u01b8\t\13\2\2\u01b7"+
		"\u01b4\3\2\2\2\u01b7\u01b6\3\2\2\2\u01b8\u0080\3\2\2\2\u01b9\u01ba\7b"+
		"\2\2\u01ba\u0082\3\2\2\2\u01bb\u01bc\7\'\2\2\u01bc\u0084\3\2\2\2\u01bd"+
		"\u01be\7\u0080\2\2\u01be\u0086\3\2\2\2\u01bf\u01c0\7^\2\2\u01c0\u0088"+
		"\3\2\2\2\u01c1\u01c2\7%\2\2\u01c2\u008a\3\2\2\2\u01c3\u01c4\7~\2\2\u01c4"+
		"\u008c\3\2\2\2\u01c5\u01c6\7\u0080\2\2\u01c6\u01c9\7~\2\2\u01c7\u01c9"+
		"\7\u2243\2\2\u01c8\u01c5\3\2\2\2\u01c8\u01c7\3\2\2\2\u01c9\u008e\3\2\2"+
		"\2\u01ca\u01cb\7\60\2\2\u01cb\u0090\3\2\2\2\u01cc\u01cd\7\u00b1\2\2\u01cd"+
		"\u0092\3\2\2\2\u01ce\u01cf\7\u207c\2\2\u01cf\u0094\3\2\2\2\u01d0\u01d1"+
		"\7\u230c\2\2\u01d1\u0096\3\2\2\2\u01d2\u01d3\7\u230a\2\2\u01d3\u0098\3"+
		"\2\2\2\u01d4\u01d5\t\f\2\2\u01d5\u009a\3\2\2\2\u01d6\u01eb\7\u2256\2\2"+
		"\u01d7\u01d8\7<\2\2\u01d8\u01eb\7?\2\2\u01d9\u01eb\7\u2257\2\2\u01da\u01db"+
		"\7?\2\2\u01db\u01eb\7<\2\2\u01dc\u01dd\7-\2\2\u01dd\u01eb\7?\2\2\u01de"+
		"\u01df\7/\2\2\u01df\u01eb\7?\2\2\u01e0\u01e1\5]/\2\u01e1\u01e2\7?\2\2"+
		"\u01e2\u01eb\3\2\2\2\u01e3\u01e4\5_\60\2\u01e4\u01e5\7?\2\2\u01e5\u01eb"+
		"\3\2\2\2\u01e6\u01e7\7\'\2\2\u01e7\u01eb\7?\2\2\u01e8\u01e9\7`\2\2\u01e9"+
		"\u01eb\7?\2\2\u01ea\u01d6\3\2\2\2\u01ea\u01d7\3\2\2\2\u01ea\u01d9\3\2"+
		"\2\2\u01ea\u01da\3\2\2\2\u01ea\u01dc\3\2\2\2\u01ea\u01de\3\2\2\2\u01ea"+
		"\u01e0\3\2\2\2\u01ea\u01e3\3\2\2\2\u01ea\u01e6\3\2\2\2\u01ea\u01e8\3\2"+
		"\2\2\u01eb\u009c\3\2\2\2\u01ec\u01f0\t\r\2\2\u01ed\u01ef\t\16\2\2\u01ee"+
		"\u01ed\3\2\2\2\u01ef\u01f2\3\2\2\2\u01f0\u01ee\3\2\2\2\u01f0\u01f1\3\2"+
		"\2\2\u01f1\u009e\3\2\2\2\u01f2\u01f0\3\2\2\2\u01f3\u01f4\5\u00a5S\2\u01f4"+
		"\u01f5\7*\2\2\u01f5\u00a0\3\2\2\2\u01f6\u01fc\5\u0099M\2\u01f7\u01fd\5"+
		"\u00a3R\2\u01f8\u01fd\5\u00a5S\2\u01f9\u01fa\5\u009fP\2\u01fa\u01fb\7"+
		"+\2\2\u01fb\u01fd\3\2\2\2\u01fc\u01f7\3\2\2\2\u01fc\u01f8\3\2\2\2\u01fc"+
		"\u01f9\3\2\2\2\u01fd\u00a2\3\2\2\2\u01fe\u0212\5]/\2\u01ff\u0212\5_\60"+
		"\2\u0200\u0212\5c\62\2\u0201\u0212\5g\64\2\u0202\u0212\5i\65\2\u0203\u0212"+
		"\5o8\2\u0204\u0212\5k\66\2\u0205\u0212\5{>\2\u0206\u0212\5o8\2\u0207\u0212"+
		"\5q9\2\u0208\u0212\5s:\2\u0209\u0212\5u;\2\u020a\u0212\5}?\2\u020b\u0212"+
		"\5\177@\2\u020c\u0212\5\u0083B\2\u020d\u0212\5\u0085C\2\u020e\u0212\5"+
		"\u008dG\2\u020f\u0212\5y=\2\u0210\u0212\5w<\2\u0211\u01fe\3\2\2\2\u0211"+
		"\u01ff\3\2\2\2\u0211\u0200\3\2\2\2\u0211\u0201\3\2\2\2\u0211\u0202\3\2"+
		"\2\2\u0211\u0203\3\2\2\2\u0211\u0204\3\2\2\2\u0211\u0205\3\2\2\2\u0211"+
		"\u0206\3\2\2\2\u0211\u0207\3\2\2\2\u0211\u0208\3\2\2\2\u0211\u0209\3\2"+
		"\2\2\u0211\u020a\3\2\2\2\u0211\u020b\3\2\2\2\u0211\u020c\3\2\2\2\u0211"+
		"\u020d\3\2\2\2\u0211\u020e\3\2\2\2\u0211\u020f\3\2\2\2\u0211\u0210\3\2"+
		"\2\2\u0212\u00a4\3\2\2\2\u0213\u0217\t\r\2\2\u0214\u0216\t\16\2\2\u0215"+
		"\u0214\3\2\2\2\u0216\u0219\3\2\2\2\u0217\u0215\3\2\2\2\u0217\u0218\3\2"+
		"\2\2\u0218\u00a6\3\2\2\2\u0219\u0217\3\2\2\2\u021a\u021b\7@\2\2\u021b"+
		"\u021c\7@\2\2\u021c\u0220\3\2\2\2\u021d\u021f\n\17\2\2\u021e\u021d\3\2"+
		"\2\2\u021f\u0222\3\2\2\2\u0220\u021e\3\2\2\2\u0220\u0221\3\2\2\2\u0221"+
		"\u00a8\3\2\2\2\u0222\u0220\3\2\2\2\u0223\u0225\t\20\2\2\u0224\u0223\3"+
		"\2\2\2\u0225\u0226\3\2\2\2\u0226\u0224\3\2\2\2\u0226\u0227\3\2\2\2\u0227"+
		"\u0228\3\2\2\2\u0228\u0229\bU\2\2\u0229\u00aa\3\2\2\2\u022a\u022b\7\61"+
		"\2\2\u022b\u022c\7,\2\2\u022c\u0230\3\2\2\2\u022d\u022f\13\2\2\2\u022e"+
		"\u022d\3\2\2\2\u022f\u0232\3\2\2\2\u0230\u0231\3\2\2\2\u0230\u022e\3\2"+
		"\2\2\u0231\u0233\3\2\2\2\u0232\u0230\3\2\2\2\u0233\u0234\7,\2\2\u0234"+
		"\u0235\7\61\2\2\u0235\u0236\3\2\2\2\u0236\u0237\bV\2\2\u0237\u00ac\3\2"+
		"\2\2\u0238\u0239\7\61\2\2\u0239\u023a\7\61\2\2\u023a\u023e\3\2\2\2\u023b"+
		"\u023d\n\17\2\2\u023c\u023b\3\2\2\2\u023d\u0240\3\2\2\2\u023e\u023c\3"+
		"\2\2\2\u023e\u023f\3\2\2\2\u023f\u0241\3\2\2\2\u0240\u023e\3\2\2\2\u0241"+
		"\u0242\bW\2\2\u0242\u00ae\3\2\2\2%\2\u00bf\u00d0\u00d7\u010a\u0125\u012e"+
		"\u0132\u013a\u013e\u0142\u0149\u014f\u015b\u015f\u016e\u0173\u0178\u0193"+
		"\u019a\u019f\u01a4\u01a9\u01b2\u01b7\u01c8\u01ea\u01f0\u01fc\u0211\u0217"+
		"\u0220\u0226\u0230\u023e\3\b\2\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}