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
		Tilde=56, Backslash=57, Stile=58, TildeRight=59, StemDot=60, ASSIGN=61, 
		Identifier=62, FuncStart=63, F_REF=64, FDOC=65, WS=66, COMMENT=67, LINE_COMMENT=68;
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
			"StemDot", "ASSIGN", "Identifier", "FuncStart", "F_REF", "AllOps", "FUNCTION_NAME", 
			"FDOC", "WS", "COMMENT", "LINE_COMMENT"
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
			"'\\'", "'|'", "'~|'", "'.'"
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
			"Percent", "Tilde", "Backslash", "Stile", "TildeRight", "StemDot", "ASSIGN", 
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
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2F\u0213\b\1\4\2\t"+
		"\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13"+
		"\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31\t\31"+
		"\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4 \t \4!"+
		"\t!\4\"\t\"\4#\t#\4$\t$\4%\t%\4&\t&\4\'\t\'\4(\t(\4)\t)\4*\t*\4+\t+\4"+
		",\t,\4-\t-\4.\t.\4/\t/\4\60\t\60\4\61\t\61\4\62\t\62\4\63\t\63\4\64\t"+
		"\64\4\65\t\65\4\66\t\66\4\67\t\67\48\t8\49\t9\4:\t:\4;\t;\4<\t<\4=\t="+
		"\4>\t>\4?\t?\4@\t@\4A\tA\4B\tB\4C\tC\4D\tD\4E\tE\4F\tF\4G\tG\4H\tH\4I"+
		"\tI\4J\tJ\4K\tK\4L\tL\4M\tM\4N\tN\3\2\3\2\3\3\3\3\3\4\3\4\3\5\3\5\3\6"+
		"\3\6\3\7\3\7\3\7\5\7\u00ab\n\7\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\t\3\t\3\n"+
		"\3\n\3\n\3\n\3\n\3\n\5\n\u00bc\n\n\3\13\3\13\3\13\3\13\3\13\5\13\u00c3"+
		"\n\13\3\f\3\f\3\f\3\f\3\f\3\r\3\r\3\r\3\r\3\r\3\r\3\16\3\16\3\16\3\16"+
		"\3\16\3\16\3\16\3\17\3\17\3\17\3\20\3\20\3\20\3\20\3\20\3\21\3\21\3\21"+
		"\3\22\3\22\3\22\3\22\3\22\3\22\3\22\3\23\3\23\3\23\3\23\3\23\5\23\u00ee"+
		"\n\23\3\24\3\24\3\24\3\24\3\24\3\24\3\24\3\25\3\25\3\25\3\25\3\25\3\26"+
		"\3\26\3\26\3\26\3\27\3\27\3\27\3\27\3\27\3\27\3\30\6\30\u0107\n\30\r\30"+
		"\16\30\u0108\3\31\3\31\3\31\3\31\3\32\3\32\3\32\5\32\u0112\n\32\3\32\3"+
		"\32\5\32\u0116\n\32\3\33\3\33\3\34\3\34\3\35\3\35\5\35\u011e\n\35\3\36"+
		"\3\36\5\36\u0122\n\36\3\36\3\36\3\37\3\37\3\37\5\37\u0129\n\37\3 \3 \6"+
		" \u012d\n \r \16 \u012e\3 \3 \3 \3 \3 \3!\3!\3\"\6\"\u0139\n\"\r\"\16"+
		"\"\u013a\3#\3#\5#\u013f\n#\3$\3$\3%\3%\3&\3&\3\'\3\'\3(\3(\3)\3)\3)\5"+
		")\u014e\n)\3*\3*\3*\5*\u0153\n*\3+\3+\3+\5+\u0158\n+\3,\3,\3-\3-\3.\3"+
		".\3.\3/\3/\3\60\3\60\3\60\3\61\3\61\3\62\3\62\3\63\3\63\3\64\3\64\3\65"+
		"\3\65\3\65\3\65\3\65\5\65\u0173\n\65\3\66\3\66\3\66\3\66\3\66\5\66\u017a"+
		"\n\66\3\67\3\67\3\67\5\67\u017f\n\67\38\38\38\58\u0184\n8\39\39\39\59"+
		"\u0189\n9\3:\3:\3;\3;\3<\3<\3<\5<\u0192\n<\3=\3=\3=\5=\u0197\n=\3>\3>"+
		"\3?\3?\3@\3@\3A\3A\3B\3B\3C\3C\3C\3D\3D\3E\3E\3E\3E\3E\3E\3E\3E\3E\3E"+
		"\3E\3E\3E\3E\3E\3E\3E\3E\3E\3E\5E\u01bc\nE\3F\3F\7F\u01c0\nF\fF\16F\u01c3"+
		"\13F\3G\3G\3G\3H\3H\3H\3H\3H\3H\5H\u01ce\nH\3I\3I\3I\3I\3I\3I\3I\3I\3"+
		"I\3I\3I\3I\3I\3I\3I\3I\3I\3I\5I\u01e2\nI\3J\3J\7J\u01e6\nJ\fJ\16J\u01e9"+
		"\13J\3K\3K\3K\3K\7K\u01ef\nK\fK\16K\u01f2\13K\3L\6L\u01f5\nL\rL\16L\u01f6"+
		"\3L\3L\3M\3M\3M\3M\7M\u01ff\nM\fM\16M\u0202\13M\3M\3M\3M\3M\3M\3N\3N\3"+
		"N\3N\7N\u020d\nN\fN\16N\u0210\13N\3N\3N\3\u0200\2O\3\3\5\4\7\5\t\6\13"+
		"\7\r\b\17\t\21\n\23\13\25\f\27\r\31\16\33\17\35\20\37\21!\22#\23%\24\'"+
		"\25)\26+\27-\30/\31\61\32\63\33\65\2\67\29\34;\35=\2?\2A\2C\2E\2G\36I"+
		"\37K M!O\"Q#S$U%W&Y\'[(])_*a+c,e-g.i/k\60m\61o\62q\63s\64u\65w\66y\67"+
		"{8}9\177:\u0081;\u0083<\u0085=\u0087>\u0089?\u008b@\u008dA\u008fB\u0091"+
		"\2\u0093\2\u0095C\u0097D\u0099E\u009bF\3\2\21\3\2\62;\4\2GGgg\4\2--//"+
		"\t\2))^^ddhhppttvv\5\2\62;CHch\6\2\f\f\17\17))^^\4\2,,\u00d9\u00d9\4\2"+
		"\61\61\u00f9\u00f9\4\2##\u00ae\u00ae\4\2\u2229\u2229\u22c2\u22c2\4\2\u222a"+
		"\u222a\u22c3\u22c3\b\2%&C\\aac|\u0393\u03ab\u03b3\u03cb\t\2%&\62;C\\a"+
		"ac|\u0393\u03ab\u03b3\u03cb\4\2\f\f\17\17\5\2\13\f\16\17\"\"\2\u0245\2"+
		"\3\3\2\2\2\2\5\3\2\2\2\2\7\3\2\2\2\2\t\3\2\2\2\2\13\3\2\2\2\2\r\3\2\2"+
		"\2\2\17\3\2\2\2\2\21\3\2\2\2\2\23\3\2\2\2\2\25\3\2\2\2\2\27\3\2\2\2\2"+
		"\31\3\2\2\2\2\33\3\2\2\2\2\35\3\2\2\2\2\37\3\2\2\2\2!\3\2\2\2\2#\3\2\2"+
		"\2\2%\3\2\2\2\2\'\3\2\2\2\2)\3\2\2\2\2+\3\2\2\2\2-\3\2\2\2\2/\3\2\2\2"+
		"\2\61\3\2\2\2\2\63\3\2\2\2\29\3\2\2\2\2;\3\2\2\2\2G\3\2\2\2\2I\3\2\2\2"+
		"\2K\3\2\2\2\2M\3\2\2\2\2O\3\2\2\2\2Q\3\2\2\2\2S\3\2\2\2\2U\3\2\2\2\2W"+
		"\3\2\2\2\2Y\3\2\2\2\2[\3\2\2\2\2]\3\2\2\2\2_\3\2\2\2\2a\3\2\2\2\2c\3\2"+
		"\2\2\2e\3\2\2\2\2g\3\2\2\2\2i\3\2\2\2\2k\3\2\2\2\2m\3\2\2\2\2o\3\2\2\2"+
		"\2q\3\2\2\2\2s\3\2\2\2\2u\3\2\2\2\2w\3\2\2\2\2y\3\2\2\2\2{\3\2\2\2\2}"+
		"\3\2\2\2\2\177\3\2\2\2\2\u0081\3\2\2\2\2\u0083\3\2\2\2\2\u0085\3\2\2\2"+
		"\2\u0087\3\2\2\2\2\u0089\3\2\2\2\2\u008b\3\2\2\2\2\u008d\3\2\2\2\2\u008f"+
		"\3\2\2\2\2\u0095\3\2\2\2\2\u0097\3\2\2\2\2\u0099\3\2\2\2\2\u009b\3\2\2"+
		"\2\3\u009d\3\2\2\2\5\u009f\3\2\2\2\7\u00a1\3\2\2\2\t\u00a3\3\2\2\2\13"+
		"\u00a5\3\2\2\2\r\u00aa\3\2\2\2\17\u00ac\3\2\2\2\21\u00b3\3\2\2\2\23\u00bb"+
		"\3\2\2\2\25\u00c2\3\2\2\2\27\u00c4\3\2\2\2\31\u00c9\3\2\2\2\33\u00cf\3"+
		"\2\2\2\35\u00d6\3\2\2\2\37\u00d9\3\2\2\2!\u00de\3\2\2\2#\u00e1\3\2\2\2"+
		"%\u00ed\3\2\2\2\'\u00ef\3\2\2\2)\u00f6\3\2\2\2+\u00fb\3\2\2\2-\u00ff\3"+
		"\2\2\2/\u0106\3\2\2\2\61\u010a\3\2\2\2\63\u010e\3\2\2\2\65\u0117\3\2\2"+
		"\2\67\u0119\3\2\2\29\u011d\3\2\2\2;\u011f\3\2\2\2=\u0128\3\2\2\2?\u012a"+
		"\3\2\2\2A\u0135\3\2\2\2C\u0138\3\2\2\2E\u013e\3\2\2\2G\u0140\3\2\2\2I"+
		"\u0142\3\2\2\2K\u0144\3\2\2\2M\u0146\3\2\2\2O\u0148\3\2\2\2Q\u014d\3\2"+
		"\2\2S\u0152\3\2\2\2U\u0157\3\2\2\2W\u0159\3\2\2\2Y\u015b\3\2\2\2[\u015d"+
		"\3\2\2\2]\u0160\3\2\2\2_\u0162\3\2\2\2a\u0165\3\2\2\2c\u0167\3\2\2\2e"+
		"\u0169\3\2\2\2g\u016b\3\2\2\2i\u0172\3\2\2\2k\u0179\3\2\2\2m\u017e\3\2"+
		"\2\2o\u0183\3\2\2\2q\u0188\3\2\2\2s\u018a\3\2\2\2u\u018c\3\2\2\2w\u0191"+
		"\3\2\2\2y\u0196\3\2\2\2{\u0198\3\2\2\2}\u019a\3\2\2\2\177\u019c\3\2\2"+
		"\2\u0081\u019e\3\2\2\2\u0083\u01a0\3\2\2\2\u0085\u01a2\3\2\2\2\u0087\u01a5"+
		"\3\2\2\2\u0089\u01bb\3\2\2\2\u008b\u01bd\3\2\2\2\u008d\u01c4\3\2\2\2\u008f"+
		"\u01c7\3\2\2\2\u0091\u01e1\3\2\2\2\u0093\u01e3\3\2\2\2\u0095\u01ea\3\2"+
		"\2\2\u0097\u01f4\3\2\2\2\u0099\u01fa\3\2\2\2\u009b\u0208\3\2\2\2\u009d"+
		"\u009e\7}\2\2\u009e\4\3\2\2\2\u009f\u00a0\7\177\2\2\u00a0\6\3\2\2\2\u00a1"+
		"\u00a2\7+\2\2\u00a2\b\3\2\2\2\u00a3\u00a4\7*\2\2\u00a4\n\3\2\2\2\u00a5"+
		"\u00a6\7A\2\2\u00a6\f\3\2\2\2\u00a7\u00ab\5\25\13\2\u00a8\u00ab\5\23\n"+
		"\2\u00a9\u00ab\5%\23\2\u00aa\u00a7\3\2\2\2\u00aa\u00a8\3\2\2\2\u00aa\u00a9"+
		"\3\2\2\2\u00ab\16\3\2\2\2\u00ac\u00ad\7c\2\2\u00ad\u00ae\7u\2\2\u00ae"+
		"\u00af\7u\2\2\u00af\u00b0\7g\2\2\u00b0\u00b1\7t\2\2\u00b1\u00b2\7v\2\2"+
		"\u00b2\20\3\2\2\2\u00b3\u00b4\7\u22aa\2\2\u00b4\22\3\2\2\2\u00b5\u00b6"+
		"\7h\2\2\u00b6\u00b7\7c\2\2\u00b7\u00b8\7n\2\2\u00b8\u00b9\7u\2\2\u00b9"+
		"\u00bc\7g\2\2\u00ba\u00bc\7\u22a7\2\2\u00bb\u00b5\3\2\2\2\u00bb\u00ba"+
		"\3\2\2\2\u00bc\24\3\2\2\2\u00bd\u00be\7v\2\2\u00be\u00bf\7t\2\2\u00bf"+
		"\u00c0\7w\2\2\u00c0\u00c3\7g\2\2\u00c1\u00c3\7\u22a6\2\2\u00c2\u00bd\3"+
		"\2\2\2\u00c2\u00c1\3\2\2\2\u00c3\26\3\2\2\2\u00c4\u00c5\7d\2\2\u00c5\u00c6"+
		"\7q\2\2\u00c6\u00c7\7f\2\2\u00c7\u00c8\7{\2\2\u00c8\30\3\2\2\2\u00c9\u00ca"+
		"\7e\2\2\u00ca\u00cb\7c\2\2\u00cb\u00cc\7v\2\2\u00cc\u00cd\7e\2\2\u00cd"+
		"\u00ce\7j\2\2\u00ce\32\3\2\2\2\u00cf\u00d0\7f\2\2\u00d0\u00d1\7g\2\2\u00d1"+
		"\u00d2\7h\2\2\u00d2\u00d3\7k\2\2\u00d3\u00d4\7p\2\2\u00d4\u00d5\7g\2\2"+
		"\u00d5\34\3\2\2\2\u00d6\u00d7\7f\2\2\u00d7\u00d8\7q\2\2\u00d8\36\3\2\2"+
		"\2\u00d9\u00da\7g\2\2\u00da\u00db\7n\2\2\u00db\u00dc\7u\2\2\u00dc\u00dd"+
		"\7g\2\2\u00dd \3\2\2\2\u00de\u00df\7k\2\2\u00df\u00e0\7h\2\2\u00e0\"\3"+
		"\2\2\2\u00e1\u00e2\7o\2\2\u00e2\u00e3\7q\2\2\u00e3\u00e4\7f\2\2\u00e4"+
		"\u00e5\7w\2\2\u00e5\u00e6\7n\2\2\u00e6\u00e7\7g\2\2\u00e7$\3\2\2\2\u00e8"+
		"\u00e9\7p\2\2\u00e9\u00ea\7w\2\2\u00ea\u00eb\7n\2\2\u00eb\u00ee\7n\2\2"+
		"\u00ec\u00ee\7\u2207\2\2\u00ed\u00e8\3\2\2\2\u00ed\u00ec\3\2\2\2\u00ee"+
		"&\3\2\2\2\u00ef\u00f0\7u\2\2\u00f0\u00f1\7y\2\2\u00f1\u00f2\7k\2\2\u00f2"+
		"\u00f3\7v\2\2\u00f3\u00f4\7e\2\2\u00f4\u00f5\7j\2\2\u00f5(\3\2\2\2\u00f6"+
		"\u00f7\7v\2\2\u00f7\u00f8\7j\2\2\u00f8\u00f9\7g\2\2\u00f9\u00fa\7p\2\2"+
		"\u00fa*\3\2\2\2\u00fb\u00fc\7v\2\2\u00fc\u00fd\7t\2\2\u00fd\u00fe\7{\2"+
		"\2\u00fe,\3\2\2\2\u00ff\u0100\7y\2\2\u0100\u0101\7j\2\2\u0101\u0102\7"+
		"k\2\2\u0102\u0103\7n\2\2\u0103\u0104\7g\2\2\u0104.\3\2\2\2\u0105\u0107"+
		"\t\2\2\2\u0106\u0105\3\2\2\2\u0107\u0108\3\2\2\2\u0108\u0106\3\2\2\2\u0108"+
		"\u0109\3\2\2\2\u0109\60\3\2\2\2\u010a\u010b\5/\30\2\u010b\u010c\7\60\2"+
		"\2\u010c\u010d\5/\30\2\u010d\62\3\2\2\2\u010e\u0115\5\61\31\2\u010f\u0111"+
		"\5\65\33\2\u0110\u0112\5\67\34\2\u0111\u0110\3\2\2\2\u0111\u0112\3\2\2"+
		"\2\u0112\u0113\3\2\2\2\u0113\u0114\5/\30\2\u0114\u0116\3\2\2\2\u0115\u010f"+
		"\3\2\2\2\u0115\u0116\3\2\2\2\u0116\64\3\2\2\2\u0117\u0118\t\3\2\2\u0118"+
		"\66\3\2\2\2\u0119\u011a\t\4\2\2\u011a8\3\2\2\2\u011b\u011e\5\25\13\2\u011c"+
		"\u011e\5\23\n\2\u011d\u011b\3\2\2\2\u011d\u011c\3\2\2\2\u011e:\3\2\2\2"+
		"\u011f\u0121\7)\2\2\u0120\u0122\5C\"\2\u0121\u0120\3\2\2\2\u0121\u0122"+
		"\3\2\2\2\u0122\u0123\3\2\2\2\u0123\u0124\7)\2\2\u0124<\3\2\2\2\u0125\u0126"+
		"\7^\2\2\u0126\u0129\t\5\2\2\u0127\u0129\5? \2\u0128\u0125\3\2\2\2\u0128"+
		"\u0127\3\2\2\2\u0129>\3\2\2\2\u012a\u012c\7^\2\2\u012b\u012d\7w\2\2\u012c"+
		"\u012b\3\2\2\2\u012d\u012e\3\2\2\2\u012e\u012c\3\2\2\2\u012e\u012f\3\2"+
		"\2\2\u012f\u0130\3\2\2\2\u0130\u0131\5A!\2\u0131\u0132\5A!\2\u0132\u0133"+
		"\5A!\2\u0133\u0134\5A!\2\u0134@\3\2\2\2\u0135\u0136\t\6\2\2\u0136B\3\2"+
		"\2\2\u0137\u0139\5E#\2\u0138\u0137\3\2\2\2\u0139\u013a\3\2\2\2\u013a\u0138"+
		"\3\2\2\2\u013a\u013b\3\2\2\2\u013bD\3\2\2\2\u013c\u013f\n\7\2\2\u013d"+
		"\u013f\5=\37\2\u013e\u013c\3\2\2\2\u013e\u013d\3\2\2\2\u013fF\3\2\2\2"+
		"\u0140\u0141\7]\2\2\u0141H\3\2\2\2\u0142\u0143\7_\2\2\u0143J\3\2\2\2\u0144"+
		"\u0145\7.\2\2\u0145L\3\2\2\2\u0146\u0147\7<\2\2\u0147N\3\2\2\2\u0148\u0149"+
		"\7=\2\2\u0149P\3\2\2\2\u014a\u014b\7]\2\2\u014b\u014e\7~\2\2\u014c\u014e"+
		"\7\u27e8\2\2\u014d\u014a\3\2\2\2\u014d\u014c\3\2\2\2\u014eR\3\2\2\2\u014f"+
		"\u0150\7~\2\2\u0150\u0153\7_\2\2\u0151\u0153\7\u27e9\2\2\u0152\u014f\3"+
		"\2\2\2\u0152\u0151\3\2\2\2\u0153T\3\2\2\2\u0154\u0155\7/\2\2\u0155\u0158"+
		"\7@\2\2\u0156\u0158\7\u2194\2\2\u0157\u0154\3\2\2\2\u0157\u0156\3\2\2"+
		"\2\u0158V\3\2\2\2\u0159\u015a\t\b\2\2\u015aX\3\2\2\2\u015b\u015c\t\t\2"+
		"\2\u015cZ\3\2\2\2\u015d\u015e\7-\2\2\u015e\u015f\7-\2\2\u015f\\\3\2\2"+
		"\2\u0160\u0161\7-\2\2\u0161^\3\2\2\2\u0162\u0163\7/\2\2\u0163\u0164\7"+
		"/\2\2\u0164`\3\2\2\2\u0165\u0166\7/\2\2\u0166b\3\2\2\2\u0167\u0168\7>"+
		"\2\2\u0168d\3\2\2\2\u0169\u016a\7@\2\2\u016af\3\2\2\2\u016b\u016c\7?\2"+
		"\2\u016ch\3\2\2\2\u016d\u016e\7>\2\2\u016e\u0173\7?\2\2\u016f\u0173\7"+
		"\u2266\2\2\u0170\u0171\7?\2\2\u0171\u0173\7>\2\2\u0172\u016d\3\2\2\2\u0172"+
		"\u016f\3\2\2\2\u0172\u0170\3\2\2\2\u0173j\3\2\2\2\u0174\u0175\7@\2\2\u0175"+
		"\u017a\7?\2\2\u0176\u017a\7\u2267\2\2\u0177\u0178\7?\2\2\u0178\u017a\7"+
		"@\2\2\u0179\u0174\3\2\2\2\u0179\u0176\3\2\2\2\u0179\u0177\3\2\2\2\u017a"+
		"l\3\2\2\2\u017b\u017c\7?\2\2\u017c\u017f\7?\2\2\u017d\u017f\7\u2263\2"+
		"\2\u017e\u017b\3\2\2\2\u017e\u017d\3\2\2\2\u017fn\3\2\2\2\u0180\u0181"+
		"\7#\2\2\u0181\u0184\7?\2\2\u0182\u0184\7\u2262\2\2\u0183\u0180\3\2\2\2"+
		"\u0183\u0182\3\2\2\2\u0184p\3\2\2\2\u0185\u0186\7?\2\2\u0186\u0189\7\u0080"+
		"\2\2\u0187\u0189\7\u224a\2\2\u0188\u0185\3\2\2\2\u0188\u0187\3\2\2\2\u0189"+
		"r\3\2\2\2\u018a\u018b\t\n\2\2\u018bt\3\2\2\2\u018c\u018d\7`\2\2\u018d"+
		"v\3\2\2\2\u018e\u018f\7(\2\2\u018f\u0192\7(\2\2\u0190\u0192\t\13\2\2\u0191"+
		"\u018e\3\2\2\2\u0191\u0190\3\2\2\2\u0192x\3\2\2\2\u0193\u0194\7~\2\2\u0194"+
		"\u0197\7~\2\2\u0195\u0197\t\f\2\2\u0196\u0193\3\2\2\2\u0196\u0195\3\2"+
		"\2\2\u0197z\3\2\2\2\u0198\u0199\7b\2\2\u0199|\3\2\2\2\u019a\u019b\7\'"+
		"\2\2\u019b~\3\2\2\2\u019c\u019d\7\u0080\2\2\u019d\u0080\3\2\2\2\u019e"+
		"\u019f\7^\2\2\u019f\u0082\3\2\2\2\u01a0\u01a1\7~\2\2\u01a1\u0084\3\2\2"+
		"\2\u01a2\u01a3\7\u0080\2\2\u01a3\u01a4\7~\2\2\u01a4\u0086\3\2\2\2\u01a5"+
		"\u01a6\7\60\2\2\u01a6\u0088\3\2\2\2\u01a7\u01bc\7\u2256\2\2\u01a8\u01a9"+
		"\7<\2\2\u01a9\u01bc\7?\2\2\u01aa\u01bc\7\u2257\2\2\u01ab\u01ac\7?\2\2"+
		"\u01ac\u01bc\7<\2\2\u01ad\u01ae\7-\2\2\u01ae\u01bc\7?\2\2\u01af\u01b0"+
		"\7/\2\2\u01b0\u01bc\7?\2\2\u01b1\u01b2\5W,\2\u01b2\u01b3\7?\2\2\u01b3"+
		"\u01bc\3\2\2\2\u01b4\u01b5\5Y-\2\u01b5\u01b6\7?\2\2\u01b6\u01bc\3\2\2"+
		"\2\u01b7\u01b8\7\'\2\2\u01b8\u01bc\7?\2\2\u01b9\u01ba\7`\2\2\u01ba\u01bc"+
		"\7?\2\2\u01bb\u01a7\3\2\2\2\u01bb\u01a8\3\2\2\2\u01bb\u01aa\3\2\2\2\u01bb"+
		"\u01ab\3\2\2\2\u01bb\u01ad\3\2\2\2\u01bb\u01af\3\2\2\2\u01bb\u01b1\3\2"+
		"\2\2\u01bb\u01b4\3\2\2\2\u01bb\u01b7\3\2\2\2\u01bb\u01b9\3\2\2\2\u01bc"+
		"\u008a\3\2\2\2\u01bd\u01c1\t\r\2\2\u01be\u01c0\t\16\2\2\u01bf\u01be\3"+
		"\2\2\2\u01c0\u01c3\3\2\2\2\u01c1\u01bf\3\2\2\2\u01c1\u01c2\3\2\2\2\u01c2"+
		"\u008c\3\2\2\2\u01c3\u01c1\3\2\2\2\u01c4\u01c5\5\u0093J\2\u01c5\u01c6"+
		"\7*\2\2\u01c6\u008e\3\2\2\2\u01c7\u01cd\7B\2\2\u01c8\u01ce\5\u0091I\2"+
		"\u01c9\u01ce\5\u0093J\2\u01ca\u01cb\5\u008dG\2\u01cb\u01cc\7+\2\2\u01cc"+
		"\u01ce\3\2\2\2\u01cd\u01c8\3\2\2\2\u01cd\u01c9\3\2\2\2\u01cd\u01ca\3\2"+
		"\2\2\u01ce\u0090\3\2\2\2\u01cf\u01e2\5W,\2\u01d0\u01e2\5Y-\2\u01d1\u01e2"+
		"\5]/\2\u01d2\u01e2\5a\61\2\u01d3\u01e2\5c\62\2\u01d4\u01e2\5i\65\2\u01d5"+
		"\u01e2\5e\63\2\u01d6\u01e2\5u;\2\u01d7\u01e2\5i\65\2\u01d8\u01e2\5k\66"+
		"\2\u01d9\u01e2\5m\67\2\u01da\u01e2\5o8\2\u01db\u01e2\5w<\2\u01dc\u01e2"+
		"\5y=\2\u01dd\u01e2\5}?\2\u01de\u01e2\5\177@\2\u01df\u01e2\5s:\2\u01e0"+
		"\u01e2\5q9\2\u01e1\u01cf\3\2\2\2\u01e1\u01d0\3\2\2\2\u01e1\u01d1\3\2\2"+
		"\2\u01e1\u01d2\3\2\2\2\u01e1\u01d3\3\2\2\2\u01e1\u01d4\3\2\2\2\u01e1\u01d5"+
		"\3\2\2\2\u01e1\u01d6\3\2\2\2\u01e1\u01d7\3\2\2\2\u01e1\u01d8\3\2\2\2\u01e1"+
		"\u01d9\3\2\2\2\u01e1\u01da\3\2\2\2\u01e1\u01db\3\2\2\2\u01e1\u01dc\3\2"+
		"\2\2\u01e1\u01dd\3\2\2\2\u01e1\u01de\3\2\2\2\u01e1\u01df\3\2\2\2\u01e1"+
		"\u01e0\3\2\2\2\u01e2\u0092\3\2\2\2\u01e3\u01e7\t\r\2\2\u01e4\u01e6\t\16"+
		"\2\2\u01e5\u01e4\3\2\2\2\u01e6\u01e9\3\2\2\2\u01e7\u01e5\3\2\2\2\u01e7"+
		"\u01e8\3\2\2\2\u01e8\u0094\3\2\2\2\u01e9\u01e7\3\2\2\2\u01ea\u01eb\7@"+
		"\2\2\u01eb\u01ec\7@\2\2\u01ec\u01f0\3\2\2\2\u01ed\u01ef\n\17\2\2\u01ee"+
		"\u01ed\3\2\2\2\u01ef\u01f2\3\2\2\2\u01f0\u01ee\3\2\2\2\u01f0\u01f1\3\2"+
		"\2\2\u01f1\u0096\3\2\2\2\u01f2\u01f0\3\2\2\2\u01f3\u01f5\t\20\2\2\u01f4"+
		"\u01f3\3\2\2\2\u01f5\u01f6\3\2\2\2\u01f6\u01f4\3\2\2\2\u01f6\u01f7\3\2"+
		"\2\2\u01f7\u01f8\3\2\2\2\u01f8\u01f9\bL\2\2\u01f9\u0098\3\2\2\2\u01fa"+
		"\u01fb\7\61\2\2\u01fb\u01fc\7,\2\2\u01fc\u0200\3\2\2\2\u01fd\u01ff\13"+
		"\2\2\2\u01fe\u01fd\3\2\2\2\u01ff\u0202\3\2\2\2\u0200\u0201\3\2\2\2\u0200"+
		"\u01fe\3\2\2\2\u0201\u0203\3\2\2\2\u0202\u0200\3\2\2\2\u0203\u0204\7,"+
		"\2\2\u0204\u0205\7\61\2\2\u0205\u0206\3\2\2\2\u0206\u0207\bM\2\2\u0207"+
		"\u009a\3\2\2\2\u0208\u0209\7\61\2\2\u0209\u020a\7\61\2\2\u020a\u020e\3"+
		"\2\2\2\u020b\u020d\n\17\2\2\u020c\u020b\3\2\2\2\u020d\u0210\3\2\2\2\u020e"+
		"\u020c\3\2\2\2\u020e\u020f\3\2\2\2\u020f\u0211\3\2\2\2\u0210\u020e\3\2"+
		"\2\2\u0211\u0212\bN\2\2\u0212\u009c\3\2\2\2#\2\u00aa\u00bb\u00c2\u00ed"+
		"\u0108\u0111\u0115\u011d\u0121\u0128\u012e\u013a\u013e\u014d\u0152\u0157"+
		"\u0172\u0179\u017e\u0183\u0188\u0191\u0196\u01bb\u01c1\u01cd\u01e1\u01e7"+
		"\u01f0\u01f6\u0200\u020e\3\b\2\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}