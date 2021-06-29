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
		ASSERT2=8, BOOL_FALSE=9, BOOL_TRUE=10, BLOCK=11, BODY=12, CATCH=13, DEFINE=14, 
		DO=15, ELSE=16, IF=17, MODULE=18, Null=19, SWITCH=20, THEN=21, TRY=22, 
		WHILE=23, Integer=24, Decimal=25, SCIENTIFIC_NUMBER=26, Bool=27, STRING=28, 
		LeftBracket=29, RightBracket=30, Comma=31, Colon=32, SemiColon=33, LDoubleBracket=34, 
		RDoubleBracket=35, LambdaConnector=36, Times=37, Divide=38, PlusPlus=39, 
		Plus=40, MinusMinus=41, Minus=42, LessThan=43, GreaterThan=44, SingleEqual=45, 
		LessEquals=46, MoreEquals=47, Equals=48, NotEquals=49, RegexMatches=50, 
		LogicalNot=51, Exponentiation=52, And=53, Or=54, Backtick=55, Percent=56, 
		Tilde=57, Backslash=58, Stile=59, TildeRight=60, StemDot=61, UnaryMinus=62, 
		UnaryPlus=63, ASSIGN=64, Identifier=65, FuncStart=66, F_REF=67, FDOC=68, 
		WS=69, COMMENT=70, LINE_COMMENT=71;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	private static String[] makeRuleNames() {
		return new String[] {
			"T__0", "T__1", "T__2", "T__3", "T__4", "ConstantKeywords", "ASSERT", 
			"ASSERT2", "BOOL_FALSE", "BOOL_TRUE", "BLOCK", "BODY", "CATCH", "DEFINE", 
			"DO", "ELSE", "IF", "MODULE", "Null", "SWITCH", "THEN", "TRY", "WHILE", 
			"Integer", "Decimal", "SCIENTIFIC_NUMBER", "E", "SIGN", "Bool", "STRING", 
			"ESC", "UnicodeEscape", "HexDigit", "StringCharacters", "StringCharacter", 
			"LeftBracket", "RightBracket", "Comma", "Colon", "SemiColon", "LDoubleBracket", 
			"RDoubleBracket", "LambdaConnector", "Times", "Divide", "PlusPlus", "Plus", 
			"MinusMinus", "Minus", "LessThan", "GreaterThan", "SingleEqual", "LessEquals", 
			"MoreEquals", "Equals", "NotEquals", "RegexMatches", "LogicalNot", "Exponentiation", 
			"And", "Or", "Backtick", "Percent", "Tilde", "Backslash", "Stile", "TildeRight", 
			"StemDot", "UnaryMinus", "UnaryPlus", "ASSIGN", "Identifier", "FuncStart", 
			"F_REF", "AllOps", "FUNCTION_NAME", "FDOC", "WS", "COMMENT", "LINE_COMMENT"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'{'", "'}'", "')'", "'('", "'?'", null, "'assert'", "'\u22A8'", 
			null, null, "'block'", "'body'", "'catch'", "'define'", "'do'", "'else'", 
			"'if'", "'module'", null, "'switch'", "'then'", "'try'", "'while'", null, 
			null, null, null, null, "'['", "']'", "','", "':'", "';'", null, null, 
			null, null, null, "'++'", "'+'", "'--'", "'-'", "'<'", "'>'", "'='", 
			null, null, null, null, null, null, "'^'", null, null, "'`'", "'%'", 
			"'~'", "'\\'", "'|'", "'~|'", "'.'", "'\u00AF'", "'\u207A'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, null, null, null, null, null, "ConstantKeywords", "ASSERT", "ASSERT2", 
			"BOOL_FALSE", "BOOL_TRUE", "BLOCK", "BODY", "CATCH", "DEFINE", "DO", 
			"ELSE", "IF", "MODULE", "Null", "SWITCH", "THEN", "TRY", "WHILE", "Integer", 
			"Decimal", "SCIENTIFIC_NUMBER", "Bool", "STRING", "LeftBracket", "RightBracket", 
			"Comma", "Colon", "SemiColon", "LDoubleBracket", "RDoubleBracket", "LambdaConnector", 
			"Times", "Divide", "PlusPlus", "Plus", "MinusMinus", "Minus", "LessThan", 
			"GreaterThan", "SingleEqual", "LessEquals", "MoreEquals", "Equals", "NotEquals", 
			"RegexMatches", "LogicalNot", "Exponentiation", "And", "Or", "Backtick", 
			"Percent", "Tilde", "Backslash", "Stile", "TildeRight", "StemDot", "UnaryMinus", 
			"UnaryPlus", "ASSIGN", "Identifier", "FuncStart", "F_REF", "FDOC", "WS", 
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
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2I\u0227\b\1\4\2\t"+
		"\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13"+
		"\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31\t\31"+
		"\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4 \t \4!"+
		"\t!\4\"\t\"\4#\t#\4$\t$\4%\t%\4&\t&\4\'\t\'\4(\t(\4)\t)\4*\t*\4+\t+\4"+
		",\t,\4-\t-\4.\t.\4/\t/\4\60\t\60\4\61\t\61\4\62\t\62\4\63\t\63\4\64\t"+
		"\64\4\65\t\65\4\66\t\66\4\67\t\67\48\t8\49\t9\4:\t:\4;\t;\4<\t<\4=\t="+
		"\4>\t>\4?\t?\4@\t@\4A\tA\4B\tB\4C\tC\4D\tD\4E\tE\4F\tF\4G\tG\4H\tH\4I"+
		"\tI\4J\tJ\4K\tK\4L\tL\4M\tM\4N\tN\4O\tO\4P\tP\4Q\tQ\3\2\3\2\3\3\3\3\3"+
		"\4\3\4\3\5\3\5\3\6\3\6\3\7\3\7\3\7\5\7\u00b1\n\7\3\b\3\b\3\b\3\b\3\b\3"+
		"\b\3\b\3\t\3\t\3\n\3\n\3\n\3\n\3\n\3\n\5\n\u00c2\n\n\3\13\3\13\3\13\3"+
		"\13\3\13\5\13\u00c9\n\13\3\f\3\f\3\f\3\f\3\f\3\f\3\r\3\r\3\r\3\r\3\r\3"+
		"\16\3\16\3\16\3\16\3\16\3\16\3\17\3\17\3\17\3\17\3\17\3\17\3\17\3\20\3"+
		"\20\3\20\3\21\3\21\3\21\3\21\3\21\3\22\3\22\3\22\3\23\3\23\3\23\3\23\3"+
		"\23\3\23\3\23\3\24\3\24\3\24\3\24\3\24\5\24\u00fa\n\24\3\25\3\25\3\25"+
		"\3\25\3\25\3\25\3\25\3\26\3\26\3\26\3\26\3\26\3\27\3\27\3\27\3\27\3\30"+
		"\3\30\3\30\3\30\3\30\3\30\3\31\6\31\u0113\n\31\r\31\16\31\u0114\3\32\3"+
		"\32\3\32\3\32\3\33\3\33\3\33\5\33\u011e\n\33\3\33\3\33\5\33\u0122\n\33"+
		"\3\34\3\34\3\35\3\35\3\35\3\35\5\35\u012a\n\35\3\36\3\36\5\36\u012e\n"+
		"\36\3\37\3\37\5\37\u0132\n\37\3\37\3\37\3 \3 \3 \5 \u0139\n \3!\3!\6!"+
		"\u013d\n!\r!\16!\u013e\3!\3!\3!\3!\3!\3\"\3\"\3#\6#\u0149\n#\r#\16#\u014a"+
		"\3$\3$\5$\u014f\n$\3%\3%\3&\3&\3\'\3\'\3(\3(\3)\3)\3*\3*\3*\5*\u015e\n"+
		"*\3+\3+\3+\5+\u0163\n+\3,\3,\3,\5,\u0168\n,\3-\3-\3.\3.\3/\3/\3/\3\60"+
		"\3\60\3\61\3\61\3\61\3\62\3\62\3\63\3\63\3\64\3\64\3\65\3\65\3\66\3\66"+
		"\3\66\3\66\3\66\5\66\u0183\n\66\3\67\3\67\3\67\3\67\3\67\5\67\u018a\n"+
		"\67\38\38\38\58\u018f\n8\39\39\39\59\u0194\n9\3:\3:\3:\5:\u0199\n:\3;"+
		"\3;\3<\3<\3=\3=\3=\5=\u01a2\n=\3>\3>\3>\5>\u01a7\n>\3?\3?\3@\3@\3A\3A"+
		"\3B\3B\3C\3C\3D\3D\3D\3E\3E\3F\3F\3G\3G\3H\3H\3H\3H\3H\3H\3H\3H\3H\3H"+
		"\3H\3H\3H\3H\3H\3H\3H\3H\3H\3H\5H\u01d0\nH\3I\3I\7I\u01d4\nI\fI\16I\u01d7"+
		"\13I\3J\3J\3J\3K\3K\3K\3K\3K\3K\5K\u01e2\nK\3L\3L\3L\3L\3L\3L\3L\3L\3"+
		"L\3L\3L\3L\3L\3L\3L\3L\3L\3L\5L\u01f6\nL\3M\3M\7M\u01fa\nM\fM\16M\u01fd"+
		"\13M\3N\3N\3N\3N\7N\u0203\nN\fN\16N\u0206\13N\3O\6O\u0209\nO\rO\16O\u020a"+
		"\3O\3O\3P\3P\3P\3P\7P\u0213\nP\fP\16P\u0216\13P\3P\3P\3P\3P\3P\3Q\3Q\3"+
		"Q\3Q\7Q\u0221\nQ\fQ\16Q\u0224\13Q\3Q\3Q\3\u0214\2R\3\3\5\4\7\5\t\6\13"+
		"\7\r\b\17\t\21\n\23\13\25\f\27\r\31\16\33\17\35\20\37\21!\22#\23%\24\'"+
		"\25)\26+\27-\30/\31\61\32\63\33\65\34\67\29\2;\35=\36?\2A\2C\2E\2G\2I"+
		"\37K M!O\"Q#S$U%W&Y\'[(])_*a+c,e-g.i/k\60m\61o\62q\63s\64u\65w\66y\67"+
		"{8}9\177:\u0081;\u0083<\u0085=\u0087>\u0089?\u008b@\u008dA\u008fB\u0091"+
		"C\u0093D\u0095E\u0097\2\u0099\2\u009bF\u009dG\u009fH\u00a1I\3\2\20\3\2"+
		"\62;\4\2GGgg\t\2))^^ddhhppttvv\5\2\62;CHch\6\2\f\f\17\17))^^\4\2,,\u00d9"+
		"\u00d9\4\2\61\61\u00f9\u00f9\4\2##\u00ae\u00ae\4\2\u2229\u2229\u22c2\u22c2"+
		"\4\2\u222a\u222a\u22c3\u22c3\b\2%&C\\aac|\u0393\u03ab\u03b3\u03cb\t\2"+
		"%&\62;C\\aac|\u0393\u03ab\u03b3\u03cb\4\2\f\f\17\17\5\2\13\f\16\17\"\""+
		"\2\u025c\2\3\3\2\2\2\2\5\3\2\2\2\2\7\3\2\2\2\2\t\3\2\2\2\2\13\3\2\2\2"+
		"\2\r\3\2\2\2\2\17\3\2\2\2\2\21\3\2\2\2\2\23\3\2\2\2\2\25\3\2\2\2\2\27"+
		"\3\2\2\2\2\31\3\2\2\2\2\33\3\2\2\2\2\35\3\2\2\2\2\37\3\2\2\2\2!\3\2\2"+
		"\2\2#\3\2\2\2\2%\3\2\2\2\2\'\3\2\2\2\2)\3\2\2\2\2+\3\2\2\2\2-\3\2\2\2"+
		"\2/\3\2\2\2\2\61\3\2\2\2\2\63\3\2\2\2\2\65\3\2\2\2\2;\3\2\2\2\2=\3\2\2"+
		"\2\2I\3\2\2\2\2K\3\2\2\2\2M\3\2\2\2\2O\3\2\2\2\2Q\3\2\2\2\2S\3\2\2\2\2"+
		"U\3\2\2\2\2W\3\2\2\2\2Y\3\2\2\2\2[\3\2\2\2\2]\3\2\2\2\2_\3\2\2\2\2a\3"+
		"\2\2\2\2c\3\2\2\2\2e\3\2\2\2\2g\3\2\2\2\2i\3\2\2\2\2k\3\2\2\2\2m\3\2\2"+
		"\2\2o\3\2\2\2\2q\3\2\2\2\2s\3\2\2\2\2u\3\2\2\2\2w\3\2\2\2\2y\3\2\2\2\2"+
		"{\3\2\2\2\2}\3\2\2\2\2\177\3\2\2\2\2\u0081\3\2\2\2\2\u0083\3\2\2\2\2\u0085"+
		"\3\2\2\2\2\u0087\3\2\2\2\2\u0089\3\2\2\2\2\u008b\3\2\2\2\2\u008d\3\2\2"+
		"\2\2\u008f\3\2\2\2\2\u0091\3\2\2\2\2\u0093\3\2\2\2\2\u0095\3\2\2\2\2\u009b"+
		"\3\2\2\2\2\u009d\3\2\2\2\2\u009f\3\2\2\2\2\u00a1\3\2\2\2\3\u00a3\3\2\2"+
		"\2\5\u00a5\3\2\2\2\7\u00a7\3\2\2\2\t\u00a9\3\2\2\2\13\u00ab\3\2\2\2\r"+
		"\u00b0\3\2\2\2\17\u00b2\3\2\2\2\21\u00b9\3\2\2\2\23\u00c1\3\2\2\2\25\u00c8"+
		"\3\2\2\2\27\u00ca\3\2\2\2\31\u00d0\3\2\2\2\33\u00d5\3\2\2\2\35\u00db\3"+
		"\2\2\2\37\u00e2\3\2\2\2!\u00e5\3\2\2\2#\u00ea\3\2\2\2%\u00ed\3\2\2\2\'"+
		"\u00f9\3\2\2\2)\u00fb\3\2\2\2+\u0102\3\2\2\2-\u0107\3\2\2\2/\u010b\3\2"+
		"\2\2\61\u0112\3\2\2\2\63\u0116\3\2\2\2\65\u011a\3\2\2\2\67\u0123\3\2\2"+
		"\29\u0129\3\2\2\2;\u012d\3\2\2\2=\u012f\3\2\2\2?\u0138\3\2\2\2A\u013a"+
		"\3\2\2\2C\u0145\3\2\2\2E\u0148\3\2\2\2G\u014e\3\2\2\2I\u0150\3\2\2\2K"+
		"\u0152\3\2\2\2M\u0154\3\2\2\2O\u0156\3\2\2\2Q\u0158\3\2\2\2S\u015d\3\2"+
		"\2\2U\u0162\3\2\2\2W\u0167\3\2\2\2Y\u0169\3\2\2\2[\u016b\3\2\2\2]\u016d"+
		"\3\2\2\2_\u0170\3\2\2\2a\u0172\3\2\2\2c\u0175\3\2\2\2e\u0177\3\2\2\2g"+
		"\u0179\3\2\2\2i\u017b\3\2\2\2k\u0182\3\2\2\2m\u0189\3\2\2\2o\u018e\3\2"+
		"\2\2q\u0193\3\2\2\2s\u0198\3\2\2\2u\u019a\3\2\2\2w\u019c\3\2\2\2y\u01a1"+
		"\3\2\2\2{\u01a6\3\2\2\2}\u01a8\3\2\2\2\177\u01aa\3\2\2\2\u0081\u01ac\3"+
		"\2\2\2\u0083\u01ae\3\2\2\2\u0085\u01b0\3\2\2\2\u0087\u01b2\3\2\2\2\u0089"+
		"\u01b5\3\2\2\2\u008b\u01b7\3\2\2\2\u008d\u01b9\3\2\2\2\u008f\u01cf\3\2"+
		"\2\2\u0091\u01d1\3\2\2\2\u0093\u01d8\3\2\2\2\u0095\u01db\3\2\2\2\u0097"+
		"\u01f5\3\2\2\2\u0099\u01f7\3\2\2\2\u009b\u01fe\3\2\2\2\u009d\u0208\3\2"+
		"\2\2\u009f\u020e\3\2\2\2\u00a1\u021c\3\2\2\2\u00a3\u00a4\7}\2\2\u00a4"+
		"\4\3\2\2\2\u00a5\u00a6\7\177\2\2\u00a6\6\3\2\2\2\u00a7\u00a8\7+\2\2\u00a8"+
		"\b\3\2\2\2\u00a9\u00aa\7*\2\2\u00aa\n\3\2\2\2\u00ab\u00ac\7A\2\2\u00ac"+
		"\f\3\2\2\2\u00ad\u00b1\5\25\13\2\u00ae\u00b1\5\23\n\2\u00af\u00b1\5\'"+
		"\24\2\u00b0\u00ad\3\2\2\2\u00b0\u00ae\3\2\2\2\u00b0\u00af\3\2\2\2\u00b1"+
		"\16\3\2\2\2\u00b2\u00b3\7c\2\2\u00b3\u00b4\7u\2\2\u00b4\u00b5\7u\2\2\u00b5"+
		"\u00b6\7g\2\2\u00b6\u00b7\7t\2\2\u00b7\u00b8\7v\2\2\u00b8\20\3\2\2\2\u00b9"+
		"\u00ba\7\u22aa\2\2\u00ba\22\3\2\2\2\u00bb\u00bc\7h\2\2\u00bc\u00bd\7c"+
		"\2\2\u00bd\u00be\7n\2\2\u00be\u00bf\7u\2\2\u00bf\u00c2\7g\2\2\u00c0\u00c2"+
		"\7\u22a7\2\2\u00c1\u00bb\3\2\2\2\u00c1\u00c0\3\2\2\2\u00c2\24\3\2\2\2"+
		"\u00c3\u00c4\7v\2\2\u00c4\u00c5\7t\2\2\u00c5\u00c6\7w\2\2\u00c6\u00c9"+
		"\7g\2\2\u00c7\u00c9\7\u22a6\2\2\u00c8\u00c3\3\2\2\2\u00c8\u00c7\3\2\2"+
		"\2\u00c9\26\3\2\2\2\u00ca\u00cb\7d\2\2\u00cb\u00cc\7n\2\2\u00cc\u00cd"+
		"\7q\2\2\u00cd\u00ce\7e\2\2\u00ce\u00cf\7m\2\2\u00cf\30\3\2\2\2\u00d0\u00d1"+
		"\7d\2\2\u00d1\u00d2\7q\2\2\u00d2\u00d3\7f\2\2\u00d3\u00d4\7{\2\2\u00d4"+
		"\32\3\2\2\2\u00d5\u00d6\7e\2\2\u00d6\u00d7\7c\2\2\u00d7\u00d8\7v\2\2\u00d8"+
		"\u00d9\7e\2\2\u00d9\u00da\7j\2\2\u00da\34\3\2\2\2\u00db\u00dc\7f\2\2\u00dc"+
		"\u00dd\7g\2\2\u00dd\u00de\7h\2\2\u00de\u00df\7k\2\2\u00df\u00e0\7p\2\2"+
		"\u00e0\u00e1\7g\2\2\u00e1\36\3\2\2\2\u00e2\u00e3\7f\2\2\u00e3\u00e4\7"+
		"q\2\2\u00e4 \3\2\2\2\u00e5\u00e6\7g\2\2\u00e6\u00e7\7n\2\2\u00e7\u00e8"+
		"\7u\2\2\u00e8\u00e9\7g\2\2\u00e9\"\3\2\2\2\u00ea\u00eb\7k\2\2\u00eb\u00ec"+
		"\7h\2\2\u00ec$\3\2\2\2\u00ed\u00ee\7o\2\2\u00ee\u00ef\7q\2\2\u00ef\u00f0"+
		"\7f\2\2\u00f0\u00f1\7w\2\2\u00f1\u00f2\7n\2\2\u00f2\u00f3\7g\2\2\u00f3"+
		"&\3\2\2\2\u00f4\u00f5\7p\2\2\u00f5\u00f6\7w\2\2\u00f6\u00f7\7n\2\2\u00f7"+
		"\u00fa\7n\2\2\u00f8\u00fa\7\u2207\2\2\u00f9\u00f4\3\2\2\2\u00f9\u00f8"+
		"\3\2\2\2\u00fa(\3\2\2\2\u00fb\u00fc\7u\2\2\u00fc\u00fd\7y\2\2\u00fd\u00fe"+
		"\7k\2\2\u00fe\u00ff\7v\2\2\u00ff\u0100\7e\2\2\u0100\u0101\7j\2\2\u0101"+
		"*\3\2\2\2\u0102\u0103\7v\2\2\u0103\u0104\7j\2\2\u0104\u0105\7g\2\2\u0105"+
		"\u0106\7p\2\2\u0106,\3\2\2\2\u0107\u0108\7v\2\2\u0108\u0109\7t\2\2\u0109"+
		"\u010a\7{\2\2\u010a.\3\2\2\2\u010b\u010c\7y\2\2\u010c\u010d\7j\2\2\u010d"+
		"\u010e\7k\2\2\u010e\u010f\7n\2\2\u010f\u0110\7g\2\2\u0110\60\3\2\2\2\u0111"+
		"\u0113\t\2\2\2\u0112\u0111\3\2\2\2\u0113\u0114\3\2\2\2\u0114\u0112\3\2"+
		"\2\2\u0114\u0115\3\2\2\2\u0115\62\3\2\2\2\u0116\u0117\5\61\31\2\u0117"+
		"\u0118\7\60\2\2\u0118\u0119\5\61\31\2\u0119\64\3\2\2\2\u011a\u0121\5\63"+
		"\32\2\u011b\u011d\5\67\34\2\u011c\u011e\59\35\2\u011d\u011c\3\2\2\2\u011d"+
		"\u011e\3\2\2\2\u011e\u011f\3\2\2\2\u011f\u0120\5\61\31\2\u0120\u0122\3"+
		"\2\2\2\u0121\u011b\3\2\2\2\u0121\u0122\3\2\2\2\u0122\66\3\2\2\2\u0123"+
		"\u0124\t\3\2\2\u01248\3\2\2\2\u0125\u012a\5_\60\2\u0126\u012a\5\u008d"+
		"G\2\u0127\u012a\5c\62\2\u0128\u012a\5\u008bF\2\u0129\u0125\3\2\2\2\u0129"+
		"\u0126\3\2\2\2\u0129\u0127\3\2\2\2\u0129\u0128\3\2\2\2\u012a:\3\2\2\2"+
		"\u012b\u012e\5\25\13\2\u012c\u012e\5\23\n\2\u012d\u012b\3\2\2\2\u012d"+
		"\u012c\3\2\2\2\u012e<\3\2\2\2\u012f\u0131\7)\2\2\u0130\u0132\5E#\2\u0131"+
		"\u0130\3\2\2\2\u0131\u0132\3\2\2\2\u0132\u0133\3\2\2\2\u0133\u0134\7)"+
		"\2\2\u0134>\3\2\2\2\u0135\u0136\7^\2\2\u0136\u0139\t\4\2\2\u0137\u0139"+
		"\5A!\2\u0138\u0135\3\2\2\2\u0138\u0137\3\2\2\2\u0139@\3\2\2\2\u013a\u013c"+
		"\7^\2\2\u013b\u013d\7w\2\2\u013c\u013b\3\2\2\2\u013d\u013e\3\2\2\2\u013e"+
		"\u013c\3\2\2\2\u013e\u013f\3\2\2\2\u013f\u0140\3\2\2\2\u0140\u0141\5C"+
		"\"\2\u0141\u0142\5C\"\2\u0142\u0143\5C\"\2\u0143\u0144\5C\"\2\u0144B\3"+
		"\2\2\2\u0145\u0146\t\5\2\2\u0146D\3\2\2\2\u0147\u0149\5G$\2\u0148\u0147"+
		"\3\2\2\2\u0149\u014a\3\2\2\2\u014a\u0148\3\2\2\2\u014a\u014b\3\2\2\2\u014b"+
		"F\3\2\2\2\u014c\u014f\n\6\2\2\u014d\u014f\5? \2\u014e\u014c\3\2\2\2\u014e"+
		"\u014d\3\2\2\2\u014fH\3\2\2\2\u0150\u0151\7]\2\2\u0151J\3\2\2\2\u0152"+
		"\u0153\7_\2\2\u0153L\3\2\2\2\u0154\u0155\7.\2\2\u0155N\3\2\2\2\u0156\u0157"+
		"\7<\2\2\u0157P\3\2\2\2\u0158\u0159\7=\2\2\u0159R\3\2\2\2\u015a\u015b\7"+
		"]\2\2\u015b\u015e\7~\2\2\u015c\u015e\7\u27e8\2\2\u015d\u015a\3\2\2\2\u015d"+
		"\u015c\3\2\2\2\u015eT\3\2\2\2\u015f\u0160\7~\2\2\u0160\u0163\7_\2\2\u0161"+
		"\u0163\7\u27e9\2\2\u0162\u015f\3\2\2\2\u0162\u0161\3\2\2\2\u0163V\3\2"+
		"\2\2\u0164\u0165\7/\2\2\u0165\u0168\7@\2\2\u0166\u0168\7\u2194\2\2\u0167"+
		"\u0164\3\2\2\2\u0167\u0166\3\2\2\2\u0168X\3\2\2\2\u0169\u016a\t\7\2\2"+
		"\u016aZ\3\2\2\2\u016b\u016c\t\b\2\2\u016c\\\3\2\2\2\u016d\u016e\7-\2\2"+
		"\u016e\u016f\7-\2\2\u016f^\3\2\2\2\u0170\u0171\7-\2\2\u0171`\3\2\2\2\u0172"+
		"\u0173\7/\2\2\u0173\u0174\7/\2\2\u0174b\3\2\2\2\u0175\u0176\7/\2\2\u0176"+
		"d\3\2\2\2\u0177\u0178\7>\2\2\u0178f\3\2\2\2\u0179\u017a\7@\2\2\u017ah"+
		"\3\2\2\2\u017b\u017c\7?\2\2\u017cj\3\2\2\2\u017d\u017e\7>\2\2\u017e\u0183"+
		"\7?\2\2\u017f\u0183\7\u2266\2\2\u0180\u0181\7?\2\2\u0181\u0183\7>\2\2"+
		"\u0182\u017d\3\2\2\2\u0182\u017f\3\2\2\2\u0182\u0180\3\2\2\2\u0183l\3"+
		"\2\2\2\u0184\u0185\7@\2\2\u0185\u018a\7?\2\2\u0186\u018a\7\u2267\2\2\u0187"+
		"\u0188\7?\2\2\u0188\u018a\7@\2\2\u0189\u0184\3\2\2\2\u0189\u0186\3\2\2"+
		"\2\u0189\u0187\3\2\2\2\u018an\3\2\2\2\u018b\u018c\7?\2\2\u018c\u018f\7"+
		"?\2\2\u018d\u018f\7\u2263\2\2\u018e\u018b\3\2\2\2\u018e\u018d\3\2\2\2"+
		"\u018fp\3\2\2\2\u0190\u0191\7#\2\2\u0191\u0194\7?\2\2\u0192\u0194\7\u2262"+
		"\2\2\u0193\u0190\3\2\2\2\u0193\u0192\3\2\2\2\u0194r\3\2\2\2\u0195\u0196"+
		"\7?\2\2\u0196\u0199\7\u0080\2\2\u0197\u0199\7\u224a\2\2\u0198\u0195\3"+
		"\2\2\2\u0198\u0197\3\2\2\2\u0199t\3\2\2\2\u019a\u019b\t\t\2\2\u019bv\3"+
		"\2\2\2\u019c\u019d\7`\2\2\u019dx\3\2\2\2\u019e\u019f\7(\2\2\u019f\u01a2"+
		"\7(\2\2\u01a0\u01a2\t\n\2\2\u01a1\u019e\3\2\2\2\u01a1\u01a0\3\2\2\2\u01a2"+
		"z\3\2\2\2\u01a3\u01a4\7~\2\2\u01a4\u01a7\7~\2\2\u01a5\u01a7\t\13\2\2\u01a6"+
		"\u01a3\3\2\2\2\u01a6\u01a5\3\2\2\2\u01a7|\3\2\2\2\u01a8\u01a9\7b\2\2\u01a9"+
		"~\3\2\2\2\u01aa\u01ab\7\'\2\2\u01ab\u0080\3\2\2\2\u01ac\u01ad\7\u0080"+
		"\2\2\u01ad\u0082\3\2\2\2\u01ae\u01af\7^\2\2\u01af\u0084\3\2\2\2\u01b0"+
		"\u01b1\7~\2\2\u01b1\u0086\3\2\2\2\u01b2\u01b3\7\u0080\2\2\u01b3\u01b4"+
		"\7~\2\2\u01b4\u0088\3\2\2\2\u01b5\u01b6\7\60\2\2\u01b6\u008a\3\2\2\2\u01b7"+
		"\u01b8\7\u00b1\2\2\u01b8\u008c\3\2\2\2\u01b9\u01ba\7\u207c\2\2\u01ba\u008e"+
		"\3\2\2\2\u01bb\u01d0\7\u2256\2\2\u01bc\u01bd\7<\2\2\u01bd\u01d0\7?\2\2"+
		"\u01be\u01d0\7\u2257\2\2\u01bf\u01c0\7?\2\2\u01c0\u01d0\7<\2\2\u01c1\u01c2"+
		"\7-\2\2\u01c2\u01d0\7?\2\2\u01c3\u01c4\7/\2\2\u01c4\u01d0\7?\2\2\u01c5"+
		"\u01c6\5Y-\2\u01c6\u01c7\7?\2\2\u01c7\u01d0\3\2\2\2\u01c8\u01c9\5[.\2"+
		"\u01c9\u01ca\7?\2\2\u01ca\u01d0\3\2\2\2\u01cb\u01cc\7\'\2\2\u01cc\u01d0"+
		"\7?\2\2\u01cd\u01ce\7`\2\2\u01ce\u01d0\7?\2\2\u01cf\u01bb\3\2\2\2\u01cf"+
		"\u01bc\3\2\2\2\u01cf\u01be\3\2\2\2\u01cf\u01bf\3\2\2\2\u01cf\u01c1\3\2"+
		"\2\2\u01cf\u01c3\3\2\2\2\u01cf\u01c5\3\2\2\2\u01cf\u01c8\3\2\2\2\u01cf"+
		"\u01cb\3\2\2\2\u01cf\u01cd\3\2\2\2\u01d0\u0090\3\2\2\2\u01d1\u01d5\t\f"+
		"\2\2\u01d2\u01d4\t\r\2\2\u01d3\u01d2\3\2\2\2\u01d4\u01d7\3\2\2\2\u01d5"+
		"\u01d3\3\2\2\2\u01d5\u01d6\3\2\2\2\u01d6\u0092\3\2\2\2\u01d7\u01d5\3\2"+
		"\2\2\u01d8\u01d9\5\u0099M\2\u01d9\u01da\7*\2\2\u01da\u0094\3\2\2\2\u01db"+
		"\u01e1\7B\2\2\u01dc\u01e2\5\u0097L\2\u01dd\u01e2\5\u0099M\2\u01de\u01df"+
		"\5\u0093J\2\u01df\u01e0\7+\2\2\u01e0\u01e2\3\2\2\2\u01e1\u01dc\3\2\2\2"+
		"\u01e1\u01dd\3\2\2\2\u01e1\u01de\3\2\2\2\u01e2\u0096\3\2\2\2\u01e3\u01f6"+
		"\5Y-\2\u01e4\u01f6\5[.\2\u01e5\u01f6\5_\60\2\u01e6\u01f6\5c\62\2\u01e7"+
		"\u01f6\5e\63\2\u01e8\u01f6\5k\66\2\u01e9\u01f6\5g\64\2\u01ea\u01f6\5w"+
		"<\2\u01eb\u01f6\5k\66\2\u01ec\u01f6\5m\67\2\u01ed\u01f6\5o8\2\u01ee\u01f6"+
		"\5q9\2\u01ef\u01f6\5y=\2\u01f0\u01f6\5{>\2\u01f1\u01f6\5\177@\2\u01f2"+
		"\u01f6\5\u0081A\2\u01f3\u01f6\5u;\2\u01f4\u01f6\5s:\2\u01f5\u01e3\3\2"+
		"\2\2\u01f5\u01e4\3\2\2\2\u01f5\u01e5\3\2\2\2\u01f5\u01e6\3\2\2\2\u01f5"+
		"\u01e7\3\2\2\2\u01f5\u01e8\3\2\2\2\u01f5\u01e9\3\2\2\2\u01f5\u01ea\3\2"+
		"\2\2\u01f5\u01eb\3\2\2\2\u01f5\u01ec\3\2\2\2\u01f5\u01ed\3\2\2\2\u01f5"+
		"\u01ee\3\2\2\2\u01f5\u01ef\3\2\2\2\u01f5\u01f0\3\2\2\2\u01f5\u01f1\3\2"+
		"\2\2\u01f5\u01f2\3\2\2\2\u01f5\u01f3\3\2\2\2\u01f5\u01f4\3\2\2\2\u01f6"+
		"\u0098\3\2\2\2\u01f7\u01fb\t\f\2\2\u01f8\u01fa\t\r\2\2\u01f9\u01f8\3\2"+
		"\2\2\u01fa\u01fd\3\2\2\2\u01fb\u01f9\3\2\2\2\u01fb\u01fc\3\2\2\2\u01fc"+
		"\u009a\3\2\2\2\u01fd\u01fb\3\2\2\2\u01fe\u01ff\7@\2\2\u01ff\u0200\7@\2"+
		"\2\u0200\u0204\3\2\2\2\u0201\u0203\n\16\2\2\u0202\u0201\3\2\2\2\u0203"+
		"\u0206\3\2\2\2\u0204\u0202\3\2\2\2\u0204\u0205\3\2\2\2\u0205\u009c\3\2"+
		"\2\2\u0206\u0204\3\2\2\2\u0207\u0209\t\17\2\2\u0208\u0207\3\2\2\2\u0209"+
		"\u020a\3\2\2\2\u020a\u0208\3\2\2\2\u020a\u020b\3\2\2\2\u020b\u020c\3\2"+
		"\2\2\u020c\u020d\bO\2\2\u020d\u009e\3\2\2\2\u020e\u020f\7\61\2\2\u020f"+
		"\u0210\7,\2\2\u0210\u0214\3\2\2\2\u0211\u0213\13\2\2\2\u0212\u0211\3\2"+
		"\2\2\u0213\u0216\3\2\2\2\u0214\u0215\3\2\2\2\u0214\u0212\3\2\2\2\u0215"+
		"\u0217\3\2\2\2\u0216\u0214\3\2\2\2\u0217\u0218\7,\2\2\u0218\u0219\7\61"+
		"\2\2\u0219\u021a\3\2\2\2\u021a\u021b\bP\2\2\u021b\u00a0\3\2\2\2\u021c"+
		"\u021d\7\61\2\2\u021d\u021e\7\61\2\2\u021e\u0222\3\2\2\2\u021f\u0221\n"+
		"\16\2\2\u0220\u021f\3\2\2\2\u0221\u0224\3\2\2\2\u0222\u0220\3\2\2\2\u0222"+
		"\u0223\3\2\2\2\u0223\u0225\3\2\2\2\u0224\u0222\3\2\2\2\u0225\u0226\bQ"+
		"\2\2\u0226\u00a2\3\2\2\2$\2\u00b0\u00c1\u00c8\u00f9\u0114\u011d\u0121"+
		"\u0129\u012d\u0131\u0138\u013e\u014a\u014e\u015d\u0162\u0167\u0182\u0189"+
		"\u018e\u0193\u0198\u01a1\u01a6\u01cf\u01d5\u01e1\u01f5\u01fb\u0204\u020a"+
		"\u0214\u0222\3\b\2\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}