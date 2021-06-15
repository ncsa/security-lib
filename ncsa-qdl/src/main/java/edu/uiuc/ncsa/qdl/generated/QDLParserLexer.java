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
		ASSERT2=8, BOOL_FALSE=9, BOOL_TRUE=10, BODY=11, CATCH=12, DEFINE=13, DO=14, 
		ELSE=15, IF=16, MODULE=17, Null=18, SWITCH=19, THEN=20, TRY=21, WHILE=22, 
		Integer=23, Decimal=24, SCIENTIFIC_NUMBER=25, Bool=26, STRING=27, LeftBracket=28, 
		RightBracket=29, Comma=30, Colon=31, SemiColon=32, LDoubleBracket=33, 
		RDoubleBracket=34, LambdaConnector=35, Times=36, Divide=37, PlusPlus=38, 
		Plus=39, MinusMinus=40, Minus=41, LessThan=42, GreaterThan=43, SingleEqual=44, 
		LessEquals=45, MoreEquals=46, Equals=47, NotEquals=48, RegexMatches=49, 
		LogicalNot=50, Exponentiation=51, And=52, Or=53, Backtick=54, Percent=55, 
		Tilde=56, Backslash=57, Stile=58, TildeRight=59, StemDot=60, UnaryMinus=61, 
		ASSIGN=62, Identifier=63, FuncStart=64, F_REF=65, FDOC=66, WS=67, COMMENT=68, 
		LINE_COMMENT=69;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	private static String[] makeRuleNames() {
		return new String[] {
			"T__0", "T__1", "T__2", "T__3", "T__4", "ConstantKeywords", "ASSERT", 
			"ASSERT2", "BOOL_FALSE", "BOOL_TRUE", "BODY", "CATCH", "DEFINE", "DO", 
			"ELSE", "IF", "MODULE", "Null", "SWITCH", "THEN", "TRY", "WHILE", "Integer", 
			"Decimal", "SCIENTIFIC_NUMBER", "E", "SIGN", "Bool", "STRING", "ESC", 
			"UnicodeEscape", "HexDigit", "StringCharacters", "StringCharacter", "LeftBracket", 
			"RightBracket", "Comma", "Colon", "SemiColon", "LDoubleBracket", "RDoubleBracket", 
			"LambdaConnector", "Times", "Divide", "PlusPlus", "Plus", "MinusMinus", 
			"Minus", "LessThan", "GreaterThan", "SingleEqual", "LessEquals", "MoreEquals", 
			"Equals", "NotEquals", "RegexMatches", "LogicalNot", "Exponentiation", 
			"And", "Or", "Backtick", "Percent", "Tilde", "Backslash", "Stile", "TildeRight", 
			"StemDot", "UnaryMinus", "ASSIGN", "Identifier", "FuncStart", "F_REF", 
			"AllOps", "FUNCTION_NAME", "FDOC", "WS", "COMMENT", "LINE_COMMENT"
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
			"'\\'", "'|'", "'~|'", "'.'", "'\u00AF'"
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
			"Percent", "Tilde", "Backslash", "Stile", "TildeRight", "StemDot", "UnaryMinus", 
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
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2G\u0217\b\1\4\2\t"+
		"\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13"+
		"\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31\t\31"+
		"\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4 \t \4!"+
		"\t!\4\"\t\"\4#\t#\4$\t$\4%\t%\4&\t&\4\'\t\'\4(\t(\4)\t)\4*\t*\4+\t+\4"+
		",\t,\4-\t-\4.\t.\4/\t/\4\60\t\60\4\61\t\61\4\62\t\62\4\63\t\63\4\64\t"+
		"\64\4\65\t\65\4\66\t\66\4\67\t\67\48\t8\49\t9\4:\t:\4;\t;\4<\t<\4=\t="+
		"\4>\t>\4?\t?\4@\t@\4A\tA\4B\tB\4C\tC\4D\tD\4E\tE\4F\tF\4G\tG\4H\tH\4I"+
		"\tI\4J\tJ\4K\tK\4L\tL\4M\tM\4N\tN\4O\tO\3\2\3\2\3\3\3\3\3\4\3\4\3\5\3"+
		"\5\3\6\3\6\3\7\3\7\3\7\5\7\u00ad\n\7\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\t\3"+
		"\t\3\n\3\n\3\n\3\n\3\n\3\n\5\n\u00be\n\n\3\13\3\13\3\13\3\13\3\13\5\13"+
		"\u00c5\n\13\3\f\3\f\3\f\3\f\3\f\3\r\3\r\3\r\3\r\3\r\3\r\3\16\3\16\3\16"+
		"\3\16\3\16\3\16\3\16\3\17\3\17\3\17\3\20\3\20\3\20\3\20\3\20\3\21\3\21"+
		"\3\21\3\22\3\22\3\22\3\22\3\22\3\22\3\22\3\23\3\23\3\23\3\23\3\23\5\23"+
		"\u00f0\n\23\3\24\3\24\3\24\3\24\3\24\3\24\3\24\3\25\3\25\3\25\3\25\3\25"+
		"\3\26\3\26\3\26\3\26\3\27\3\27\3\27\3\27\3\27\3\27\3\30\6\30\u0109\n\30"+
		"\r\30\16\30\u010a\3\31\3\31\3\31\3\31\3\32\3\32\3\32\5\32\u0114\n\32\3"+
		"\32\3\32\5\32\u0118\n\32\3\33\3\33\3\34\3\34\3\35\3\35\5\35\u0120\n\35"+
		"\3\36\3\36\5\36\u0124\n\36\3\36\3\36\3\37\3\37\3\37\5\37\u012b\n\37\3"+
		" \3 \6 \u012f\n \r \16 \u0130\3 \3 \3 \3 \3 \3!\3!\3\"\6\"\u013b\n\"\r"+
		"\"\16\"\u013c\3#\3#\5#\u0141\n#\3$\3$\3%\3%\3&\3&\3\'\3\'\3(\3(\3)\3)"+
		"\3)\5)\u0150\n)\3*\3*\3*\5*\u0155\n*\3+\3+\3+\5+\u015a\n+\3,\3,\3-\3-"+
		"\3.\3.\3.\3/\3/\3\60\3\60\3\60\3\61\3\61\3\62\3\62\3\63\3\63\3\64\3\64"+
		"\3\65\3\65\3\65\3\65\3\65\5\65\u0175\n\65\3\66\3\66\3\66\3\66\3\66\5\66"+
		"\u017c\n\66\3\67\3\67\3\67\5\67\u0181\n\67\38\38\38\58\u0186\n8\39\39"+
		"\39\59\u018b\n9\3:\3:\3;\3;\3<\3<\3<\5<\u0194\n<\3=\3=\3=\5=\u0199\n="+
		"\3>\3>\3?\3?\3@\3@\3A\3A\3B\3B\3C\3C\3C\3D\3D\3E\3E\3F\3F\3F\3F\3F\3F"+
		"\3F\3F\3F\3F\3F\3F\3F\3F\3F\3F\3F\3F\3F\3F\5F\u01c0\nF\3G\3G\7G\u01c4"+
		"\nG\fG\16G\u01c7\13G\3H\3H\3H\3I\3I\3I\3I\3I\3I\5I\u01d2\nI\3J\3J\3J\3"+
		"J\3J\3J\3J\3J\3J\3J\3J\3J\3J\3J\3J\3J\3J\3J\5J\u01e6\nJ\3K\3K\7K\u01ea"+
		"\nK\fK\16K\u01ed\13K\3L\3L\3L\3L\7L\u01f3\nL\fL\16L\u01f6\13L\3M\6M\u01f9"+
		"\nM\rM\16M\u01fa\3M\3M\3N\3N\3N\3N\7N\u0203\nN\fN\16N\u0206\13N\3N\3N"+
		"\3N\3N\3N\3O\3O\3O\3O\7O\u0211\nO\fO\16O\u0214\13O\3O\3O\3\u0204\2P\3"+
		"\3\5\4\7\5\t\6\13\7\r\b\17\t\21\n\23\13\25\f\27\r\31\16\33\17\35\20\37"+
		"\21!\22#\23%\24\'\25)\26+\27-\30/\31\61\32\63\33\65\2\67\29\34;\35=\2"+
		"?\2A\2C\2E\2G\36I\37K M!O\"Q#S$U%W&Y\'[(])_*a+c,e-g.i/k\60m\61o\62q\63"+
		"s\64u\65w\66y\67{8}9\177:\u0081;\u0083<\u0085=\u0087>\u0089?\u008b@\u008d"+
		"A\u008fB\u0091C\u0093\2\u0095\2\u0097D\u0099E\u009bF\u009dG\3\2\21\3\2"+
		"\62;\4\2GGgg\4\2--//\t\2))^^ddhhppttvv\5\2\62;CHch\6\2\f\f\17\17))^^\4"+
		"\2,,\u00d9\u00d9\4\2\61\61\u00f9\u00f9\4\2##\u00ae\u00ae\4\2\u2229\u2229"+
		"\u22c2\u22c2\4\2\u222a\u222a\u22c3\u22c3\b\2%&C\\aac|\u0393\u03ab\u03b3"+
		"\u03cb\t\2%&\62;C\\aac|\u0393\u03ab\u03b3\u03cb\4\2\f\f\17\17\5\2\13\f"+
		"\16\17\"\"\2\u0249\2\3\3\2\2\2\2\5\3\2\2\2\2\7\3\2\2\2\2\t\3\2\2\2\2\13"+
		"\3\2\2\2\2\r\3\2\2\2\2\17\3\2\2\2\2\21\3\2\2\2\2\23\3\2\2\2\2\25\3\2\2"+
		"\2\2\27\3\2\2\2\2\31\3\2\2\2\2\33\3\2\2\2\2\35\3\2\2\2\2\37\3\2\2\2\2"+
		"!\3\2\2\2\2#\3\2\2\2\2%\3\2\2\2\2\'\3\2\2\2\2)\3\2\2\2\2+\3\2\2\2\2-\3"+
		"\2\2\2\2/\3\2\2\2\2\61\3\2\2\2\2\63\3\2\2\2\29\3\2\2\2\2;\3\2\2\2\2G\3"+
		"\2\2\2\2I\3\2\2\2\2K\3\2\2\2\2M\3\2\2\2\2O\3\2\2\2\2Q\3\2\2\2\2S\3\2\2"+
		"\2\2U\3\2\2\2\2W\3\2\2\2\2Y\3\2\2\2\2[\3\2\2\2\2]\3\2\2\2\2_\3\2\2\2\2"+
		"a\3\2\2\2\2c\3\2\2\2\2e\3\2\2\2\2g\3\2\2\2\2i\3\2\2\2\2k\3\2\2\2\2m\3"+
		"\2\2\2\2o\3\2\2\2\2q\3\2\2\2\2s\3\2\2\2\2u\3\2\2\2\2w\3\2\2\2\2y\3\2\2"+
		"\2\2{\3\2\2\2\2}\3\2\2\2\2\177\3\2\2\2\2\u0081\3\2\2\2\2\u0083\3\2\2\2"+
		"\2\u0085\3\2\2\2\2\u0087\3\2\2\2\2\u0089\3\2\2\2\2\u008b\3\2\2\2\2\u008d"+
		"\3\2\2\2\2\u008f\3\2\2\2\2\u0091\3\2\2\2\2\u0097\3\2\2\2\2\u0099\3\2\2"+
		"\2\2\u009b\3\2\2\2\2\u009d\3\2\2\2\3\u009f\3\2\2\2\5\u00a1\3\2\2\2\7\u00a3"+
		"\3\2\2\2\t\u00a5\3\2\2\2\13\u00a7\3\2\2\2\r\u00ac\3\2\2\2\17\u00ae\3\2"+
		"\2\2\21\u00b5\3\2\2\2\23\u00bd\3\2\2\2\25\u00c4\3\2\2\2\27\u00c6\3\2\2"+
		"\2\31\u00cb\3\2\2\2\33\u00d1\3\2\2\2\35\u00d8\3\2\2\2\37\u00db\3\2\2\2"+
		"!\u00e0\3\2\2\2#\u00e3\3\2\2\2%\u00ef\3\2\2\2\'\u00f1\3\2\2\2)\u00f8\3"+
		"\2\2\2+\u00fd\3\2\2\2-\u0101\3\2\2\2/\u0108\3\2\2\2\61\u010c\3\2\2\2\63"+
		"\u0110\3\2\2\2\65\u0119\3\2\2\2\67\u011b\3\2\2\29\u011f\3\2\2\2;\u0121"+
		"\3\2\2\2=\u012a\3\2\2\2?\u012c\3\2\2\2A\u0137\3\2\2\2C\u013a\3\2\2\2E"+
		"\u0140\3\2\2\2G\u0142\3\2\2\2I\u0144\3\2\2\2K\u0146\3\2\2\2M\u0148\3\2"+
		"\2\2O\u014a\3\2\2\2Q\u014f\3\2\2\2S\u0154\3\2\2\2U\u0159\3\2\2\2W\u015b"+
		"\3\2\2\2Y\u015d\3\2\2\2[\u015f\3\2\2\2]\u0162\3\2\2\2_\u0164\3\2\2\2a"+
		"\u0167\3\2\2\2c\u0169\3\2\2\2e\u016b\3\2\2\2g\u016d\3\2\2\2i\u0174\3\2"+
		"\2\2k\u017b\3\2\2\2m\u0180\3\2\2\2o\u0185\3\2\2\2q\u018a\3\2\2\2s\u018c"+
		"\3\2\2\2u\u018e\3\2\2\2w\u0193\3\2\2\2y\u0198\3\2\2\2{\u019a\3\2\2\2}"+
		"\u019c\3\2\2\2\177\u019e\3\2\2\2\u0081\u01a0\3\2\2\2\u0083\u01a2\3\2\2"+
		"\2\u0085\u01a4\3\2\2\2\u0087\u01a7\3\2\2\2\u0089\u01a9\3\2\2\2\u008b\u01bf"+
		"\3\2\2\2\u008d\u01c1\3\2\2\2\u008f\u01c8\3\2\2\2\u0091\u01cb\3\2\2\2\u0093"+
		"\u01e5\3\2\2\2\u0095\u01e7\3\2\2\2\u0097\u01ee\3\2\2\2\u0099\u01f8\3\2"+
		"\2\2\u009b\u01fe\3\2\2\2\u009d\u020c\3\2\2\2\u009f\u00a0\7}\2\2\u00a0"+
		"\4\3\2\2\2\u00a1\u00a2\7\177\2\2\u00a2\6\3\2\2\2\u00a3\u00a4\7+\2\2\u00a4"+
		"\b\3\2\2\2\u00a5\u00a6\7*\2\2\u00a6\n\3\2\2\2\u00a7\u00a8\7A\2\2\u00a8"+
		"\f\3\2\2\2\u00a9\u00ad\5\25\13\2\u00aa\u00ad\5\23\n\2\u00ab\u00ad\5%\23"+
		"\2\u00ac\u00a9\3\2\2\2\u00ac\u00aa\3\2\2\2\u00ac\u00ab\3\2\2\2\u00ad\16"+
		"\3\2\2\2\u00ae\u00af\7c\2\2\u00af\u00b0\7u\2\2\u00b0\u00b1\7u\2\2\u00b1"+
		"\u00b2\7g\2\2\u00b2\u00b3\7t\2\2\u00b3\u00b4\7v\2\2\u00b4\20\3\2\2\2\u00b5"+
		"\u00b6\7\u22aa\2\2\u00b6\22\3\2\2\2\u00b7\u00b8\7h\2\2\u00b8\u00b9\7c"+
		"\2\2\u00b9\u00ba\7n\2\2\u00ba\u00bb\7u\2\2\u00bb\u00be\7g\2\2\u00bc\u00be"+
		"\7\u22a7\2\2\u00bd\u00b7\3\2\2\2\u00bd\u00bc\3\2\2\2\u00be\24\3\2\2\2"+
		"\u00bf\u00c0\7v\2\2\u00c0\u00c1\7t\2\2\u00c1\u00c2\7w\2\2\u00c2\u00c5"+
		"\7g\2\2\u00c3\u00c5\7\u22a6\2\2\u00c4\u00bf\3\2\2\2\u00c4\u00c3\3\2\2"+
		"\2\u00c5\26\3\2\2\2\u00c6\u00c7\7d\2\2\u00c7\u00c8\7q\2\2\u00c8\u00c9"+
		"\7f\2\2\u00c9\u00ca\7{\2\2\u00ca\30\3\2\2\2\u00cb\u00cc\7e\2\2\u00cc\u00cd"+
		"\7c\2\2\u00cd\u00ce\7v\2\2\u00ce\u00cf\7e\2\2\u00cf\u00d0\7j\2\2\u00d0"+
		"\32\3\2\2\2\u00d1\u00d2\7f\2\2\u00d2\u00d3\7g\2\2\u00d3\u00d4\7h\2\2\u00d4"+
		"\u00d5\7k\2\2\u00d5\u00d6\7p\2\2\u00d6\u00d7\7g\2\2\u00d7\34\3\2\2\2\u00d8"+
		"\u00d9\7f\2\2\u00d9\u00da\7q\2\2\u00da\36\3\2\2\2\u00db\u00dc\7g\2\2\u00dc"+
		"\u00dd\7n\2\2\u00dd\u00de\7u\2\2\u00de\u00df\7g\2\2\u00df \3\2\2\2\u00e0"+
		"\u00e1\7k\2\2\u00e1\u00e2\7h\2\2\u00e2\"\3\2\2\2\u00e3\u00e4\7o\2\2\u00e4"+
		"\u00e5\7q\2\2\u00e5\u00e6\7f\2\2\u00e6\u00e7\7w\2\2\u00e7\u00e8\7n\2\2"+
		"\u00e8\u00e9\7g\2\2\u00e9$\3\2\2\2\u00ea\u00eb\7p\2\2\u00eb\u00ec\7w\2"+
		"\2\u00ec\u00ed\7n\2\2\u00ed\u00f0\7n\2\2\u00ee\u00f0\7\u2207\2\2\u00ef"+
		"\u00ea\3\2\2\2\u00ef\u00ee\3\2\2\2\u00f0&\3\2\2\2\u00f1\u00f2\7u\2\2\u00f2"+
		"\u00f3\7y\2\2\u00f3\u00f4\7k\2\2\u00f4\u00f5\7v\2\2\u00f5\u00f6\7e\2\2"+
		"\u00f6\u00f7\7j\2\2\u00f7(\3\2\2\2\u00f8\u00f9\7v\2\2\u00f9\u00fa\7j\2"+
		"\2\u00fa\u00fb\7g\2\2\u00fb\u00fc\7p\2\2\u00fc*\3\2\2\2\u00fd\u00fe\7"+
		"v\2\2\u00fe\u00ff\7t\2\2\u00ff\u0100\7{\2\2\u0100,\3\2\2\2\u0101\u0102"+
		"\7y\2\2\u0102\u0103\7j\2\2\u0103\u0104\7k\2\2\u0104\u0105\7n\2\2\u0105"+
		"\u0106\7g\2\2\u0106.\3\2\2\2\u0107\u0109\t\2\2\2\u0108\u0107\3\2\2\2\u0109"+
		"\u010a\3\2\2\2\u010a\u0108\3\2\2\2\u010a\u010b\3\2\2\2\u010b\60\3\2\2"+
		"\2\u010c\u010d\5/\30\2\u010d\u010e\7\60\2\2\u010e\u010f\5/\30\2\u010f"+
		"\62\3\2\2\2\u0110\u0117\5\61\31\2\u0111\u0113\5\65\33\2\u0112\u0114\5"+
		"\67\34\2\u0113\u0112\3\2\2\2\u0113\u0114\3\2\2\2\u0114\u0115\3\2\2\2\u0115"+
		"\u0116\5/\30\2\u0116\u0118\3\2\2\2\u0117\u0111\3\2\2\2\u0117\u0118\3\2"+
		"\2\2\u0118\64\3\2\2\2\u0119\u011a\t\3\2\2\u011a\66\3\2\2\2\u011b\u011c"+
		"\t\4\2\2\u011c8\3\2\2\2\u011d\u0120\5\25\13\2\u011e\u0120\5\23\n\2\u011f"+
		"\u011d\3\2\2\2\u011f\u011e\3\2\2\2\u0120:\3\2\2\2\u0121\u0123\7)\2\2\u0122"+
		"\u0124\5C\"\2\u0123\u0122\3\2\2\2\u0123\u0124\3\2\2\2\u0124\u0125\3\2"+
		"\2\2\u0125\u0126\7)\2\2\u0126<\3\2\2\2\u0127\u0128\7^\2\2\u0128\u012b"+
		"\t\5\2\2\u0129\u012b\5? \2\u012a\u0127\3\2\2\2\u012a\u0129\3\2\2\2\u012b"+
		">\3\2\2\2\u012c\u012e\7^\2\2\u012d\u012f\7w\2\2\u012e\u012d\3\2\2\2\u012f"+
		"\u0130\3\2\2\2\u0130\u012e\3\2\2\2\u0130\u0131\3\2\2\2\u0131\u0132\3\2"+
		"\2\2\u0132\u0133\5A!\2\u0133\u0134\5A!\2\u0134\u0135\5A!\2\u0135\u0136"+
		"\5A!\2\u0136@\3\2\2\2\u0137\u0138\t\6\2\2\u0138B\3\2\2\2\u0139\u013b\5"+
		"E#\2\u013a\u0139\3\2\2\2\u013b\u013c\3\2\2\2\u013c\u013a\3\2\2\2\u013c"+
		"\u013d\3\2\2\2\u013dD\3\2\2\2\u013e\u0141\n\7\2\2\u013f\u0141\5=\37\2"+
		"\u0140\u013e\3\2\2\2\u0140\u013f\3\2\2\2\u0141F\3\2\2\2\u0142\u0143\7"+
		"]\2\2\u0143H\3\2\2\2\u0144\u0145\7_\2\2\u0145J\3\2\2\2\u0146\u0147\7."+
		"\2\2\u0147L\3\2\2\2\u0148\u0149\7<\2\2\u0149N\3\2\2\2\u014a\u014b\7=\2"+
		"\2\u014bP\3\2\2\2\u014c\u014d\7]\2\2\u014d\u0150\7~\2\2\u014e\u0150\7"+
		"\u27e8\2\2\u014f\u014c\3\2\2\2\u014f\u014e\3\2\2\2\u0150R\3\2\2\2\u0151"+
		"\u0152\7~\2\2\u0152\u0155\7_\2\2\u0153\u0155\7\u27e9\2\2\u0154\u0151\3"+
		"\2\2\2\u0154\u0153\3\2\2\2\u0155T\3\2\2\2\u0156\u0157\7/\2\2\u0157\u015a"+
		"\7@\2\2\u0158\u015a\7\u2194\2\2\u0159\u0156\3\2\2\2\u0159\u0158\3\2\2"+
		"\2\u015aV\3\2\2\2\u015b\u015c\t\b\2\2\u015cX\3\2\2\2\u015d\u015e\t\t\2"+
		"\2\u015eZ\3\2\2\2\u015f\u0160\7-\2\2\u0160\u0161\7-\2\2\u0161\\\3\2\2"+
		"\2\u0162\u0163\7-\2\2\u0163^\3\2\2\2\u0164\u0165\7/\2\2\u0165\u0166\7"+
		"/\2\2\u0166`\3\2\2\2\u0167\u0168\7/\2\2\u0168b\3\2\2\2\u0169\u016a\7>"+
		"\2\2\u016ad\3\2\2\2\u016b\u016c\7@\2\2\u016cf\3\2\2\2\u016d\u016e\7?\2"+
		"\2\u016eh\3\2\2\2\u016f\u0170\7>\2\2\u0170\u0175\7?\2\2\u0171\u0175\7"+
		"\u2266\2\2\u0172\u0173\7?\2\2\u0173\u0175\7>\2\2\u0174\u016f\3\2\2\2\u0174"+
		"\u0171\3\2\2\2\u0174\u0172\3\2\2\2\u0175j\3\2\2\2\u0176\u0177\7@\2\2\u0177"+
		"\u017c\7?\2\2\u0178\u017c\7\u2267\2\2\u0179\u017a\7?\2\2\u017a\u017c\7"+
		"@\2\2\u017b\u0176\3\2\2\2\u017b\u0178\3\2\2\2\u017b\u0179\3\2\2\2\u017c"+
		"l\3\2\2\2\u017d\u017e\7?\2\2\u017e\u0181\7?\2\2\u017f\u0181\7\u2263\2"+
		"\2\u0180\u017d\3\2\2\2\u0180\u017f\3\2\2\2\u0181n\3\2\2\2\u0182\u0183"+
		"\7#\2\2\u0183\u0186\7?\2\2\u0184\u0186\7\u2262\2\2\u0185\u0182\3\2\2\2"+
		"\u0185\u0184\3\2\2\2\u0186p\3\2\2\2\u0187\u0188\7?\2\2\u0188\u018b\7\u0080"+
		"\2\2\u0189\u018b\7\u224a\2\2\u018a\u0187\3\2\2\2\u018a\u0189\3\2\2\2\u018b"+
		"r\3\2\2\2\u018c\u018d\t\n\2\2\u018dt\3\2\2\2\u018e\u018f\7`\2\2\u018f"+
		"v\3\2\2\2\u0190\u0191\7(\2\2\u0191\u0194\7(\2\2\u0192\u0194\t\13\2\2\u0193"+
		"\u0190\3\2\2\2\u0193\u0192\3\2\2\2\u0194x\3\2\2\2\u0195\u0196\7~\2\2\u0196"+
		"\u0199\7~\2\2\u0197\u0199\t\f\2\2\u0198\u0195\3\2\2\2\u0198\u0197\3\2"+
		"\2\2\u0199z\3\2\2\2\u019a\u019b\7b\2\2\u019b|\3\2\2\2\u019c\u019d\7\'"+
		"\2\2\u019d~\3\2\2\2\u019e\u019f\7\u0080\2\2\u019f\u0080\3\2\2\2\u01a0"+
		"\u01a1\7^\2\2\u01a1\u0082\3\2\2\2\u01a2\u01a3\7~\2\2\u01a3\u0084\3\2\2"+
		"\2\u01a4\u01a5\7\u0080\2\2\u01a5\u01a6\7~\2\2\u01a6\u0086\3\2\2\2\u01a7"+
		"\u01a8\7\60\2\2\u01a8\u0088\3\2\2\2\u01a9\u01aa\7\u00b1\2\2\u01aa\u008a"+
		"\3\2\2\2\u01ab\u01c0\7\u2256\2\2\u01ac\u01ad\7<\2\2\u01ad\u01c0\7?\2\2"+
		"\u01ae\u01c0\7\u2257\2\2\u01af\u01b0\7?\2\2\u01b0\u01c0\7<\2\2\u01b1\u01b2"+
		"\7-\2\2\u01b2\u01c0\7?\2\2\u01b3\u01b4\7/\2\2\u01b4\u01c0\7?\2\2\u01b5"+
		"\u01b6\5W,\2\u01b6\u01b7\7?\2\2\u01b7\u01c0\3\2\2\2\u01b8\u01b9\5Y-\2"+
		"\u01b9\u01ba\7?\2\2\u01ba\u01c0\3\2\2\2\u01bb\u01bc\7\'\2\2\u01bc\u01c0"+
		"\7?\2\2\u01bd\u01be\7`\2\2\u01be\u01c0\7?\2\2\u01bf\u01ab\3\2\2\2\u01bf"+
		"\u01ac\3\2\2\2\u01bf\u01ae\3\2\2\2\u01bf\u01af\3\2\2\2\u01bf\u01b1\3\2"+
		"\2\2\u01bf\u01b3\3\2\2\2\u01bf\u01b5\3\2\2\2\u01bf\u01b8\3\2\2\2\u01bf"+
		"\u01bb\3\2\2\2\u01bf\u01bd\3\2\2\2\u01c0\u008c\3\2\2\2\u01c1\u01c5\t\r"+
		"\2\2\u01c2\u01c4\t\16\2\2\u01c3\u01c2\3\2\2\2\u01c4\u01c7\3\2\2\2\u01c5"+
		"\u01c3\3\2\2\2\u01c5\u01c6\3\2\2\2\u01c6\u008e\3\2\2\2\u01c7\u01c5\3\2"+
		"\2\2\u01c8\u01c9\5\u0095K\2\u01c9\u01ca\7*\2\2\u01ca\u0090\3\2\2\2\u01cb"+
		"\u01d1\7B\2\2\u01cc\u01d2\5\u0093J\2\u01cd\u01d2\5\u0095K\2\u01ce\u01cf"+
		"\5\u008fH\2\u01cf\u01d0\7+\2\2\u01d0\u01d2\3\2\2\2\u01d1\u01cc\3\2\2\2"+
		"\u01d1\u01cd\3\2\2\2\u01d1\u01ce\3\2\2\2\u01d2\u0092\3\2\2\2\u01d3\u01e6"+
		"\5W,\2\u01d4\u01e6\5Y-\2\u01d5\u01e6\5]/\2\u01d6\u01e6\5a\61\2\u01d7\u01e6"+
		"\5c\62\2\u01d8\u01e6\5i\65\2\u01d9\u01e6\5e\63\2\u01da\u01e6\5u;\2\u01db"+
		"\u01e6\5i\65\2\u01dc\u01e6\5k\66\2\u01dd\u01e6\5m\67\2\u01de\u01e6\5o"+
		"8\2\u01df\u01e6\5w<\2\u01e0\u01e6\5y=\2\u01e1\u01e6\5}?\2\u01e2\u01e6"+
		"\5\177@\2\u01e3\u01e6\5s:\2\u01e4\u01e6\5q9\2\u01e5\u01d3\3\2\2\2\u01e5"+
		"\u01d4\3\2\2\2\u01e5\u01d5\3\2\2\2\u01e5\u01d6\3\2\2\2\u01e5\u01d7\3\2"+
		"\2\2\u01e5\u01d8\3\2\2\2\u01e5\u01d9\3\2\2\2\u01e5\u01da\3\2\2\2\u01e5"+
		"\u01db\3\2\2\2\u01e5\u01dc\3\2\2\2\u01e5\u01dd\3\2\2\2\u01e5\u01de\3\2"+
		"\2\2\u01e5\u01df\3\2\2\2\u01e5\u01e0\3\2\2\2\u01e5\u01e1\3\2\2\2\u01e5"+
		"\u01e2\3\2\2\2\u01e5\u01e3\3\2\2\2\u01e5\u01e4\3\2\2\2\u01e6\u0094\3\2"+
		"\2\2\u01e7\u01eb\t\r\2\2\u01e8\u01ea\t\16\2\2\u01e9\u01e8\3\2\2\2\u01ea"+
		"\u01ed\3\2\2\2\u01eb\u01e9\3\2\2\2\u01eb\u01ec\3\2\2\2\u01ec\u0096\3\2"+
		"\2\2\u01ed\u01eb\3\2\2\2\u01ee\u01ef\7@\2\2\u01ef\u01f0\7@\2\2\u01f0\u01f4"+
		"\3\2\2\2\u01f1\u01f3\n\17\2\2\u01f2\u01f1\3\2\2\2\u01f3\u01f6\3\2\2\2"+
		"\u01f4\u01f2\3\2\2\2\u01f4\u01f5\3\2\2\2\u01f5\u0098\3\2\2\2\u01f6\u01f4"+
		"\3\2\2\2\u01f7\u01f9\t\20\2\2\u01f8\u01f7\3\2\2\2\u01f9\u01fa\3\2\2\2"+
		"\u01fa\u01f8\3\2\2\2\u01fa\u01fb\3\2\2\2\u01fb\u01fc\3\2\2\2\u01fc\u01fd"+
		"\bM\2\2\u01fd\u009a\3\2\2\2\u01fe\u01ff\7\61\2\2\u01ff\u0200\7,\2\2\u0200"+
		"\u0204\3\2\2\2\u0201\u0203\13\2\2\2\u0202\u0201\3\2\2\2\u0203\u0206\3"+
		"\2\2\2\u0204\u0205\3\2\2\2\u0204\u0202\3\2\2\2\u0205\u0207\3\2\2\2\u0206"+
		"\u0204\3\2\2\2\u0207\u0208\7,\2\2\u0208\u0209\7\61\2\2\u0209\u020a\3\2"+
		"\2\2\u020a\u020b\bN\2\2\u020b\u009c\3\2\2\2\u020c\u020d\7\61\2\2\u020d"+
		"\u020e\7\61\2\2\u020e\u0212\3\2\2\2\u020f\u0211\n\17\2\2\u0210\u020f\3"+
		"\2\2\2\u0211\u0214\3\2\2\2\u0212\u0210\3\2\2\2\u0212\u0213\3\2\2\2\u0213"+
		"\u0215\3\2\2\2\u0214\u0212\3\2\2\2\u0215\u0216\bO\2\2\u0216\u009e\3\2"+
		"\2\2#\2\u00ac\u00bd\u00c4\u00ef\u010a\u0113\u0117\u011f\u0123\u012a\u0130"+
		"\u013c\u0140\u014f\u0154\u0159\u0174\u017b\u0180\u0185\u018a\u0193\u0198"+
		"\u01bf\u01c5\u01d1\u01e5\u01eb\u01f4\u01fa\u0204\u0212\3\b\2\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}