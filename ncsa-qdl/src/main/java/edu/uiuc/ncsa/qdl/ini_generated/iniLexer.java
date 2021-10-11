// Generated from ini.g4 by ANTLR 4.9.1
package edu.uiuc.ncsa.qdl.ini_generated;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class iniLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.9.1", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, T__2=3, ConstantKeywords=4, UnaryMinus=5, UnaryPlus=6, 
		Plus=7, Minus=8, Assign=9, String=10, Identifier=11, BOOL_FALSE=12, BOOL_TRUE=13, 
		Bool=14, Number=15, Integer=16, Decimal=17, SCIENTIFIC_NUMBER=18, LINE_COMMENT=19, 
		COMMENT=20, EOL=21, WS=22;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	private static String[] makeRuleNames() {
		return new String[] {
			"T__0", "T__1", "T__2", "ConstantKeywords", "UnaryMinus", "UnaryPlus", 
			"Plus", "Minus", "Assign", "String", "ESC", "UnicodeEscape", "HexDigit", 
			"StringCharacters", "StringCharacter", "Identifier", "BOOL_FALSE", "BOOL_TRUE", 
			"Bool", "Number", "Integer", "Decimal", "SCIENTIFIC_NUMBER", "E", "SIGN", 
			"LINE_COMMENT", "COMMENT", "EOL", "WS"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'['", "']'", "','", null, "'\u00AF'", "'\u207A'", "'+'", "'-'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, null, null, null, "ConstantKeywords", "UnaryMinus", "UnaryPlus", 
			"Plus", "Minus", "Assign", "String", "Identifier", "BOOL_FALSE", "BOOL_TRUE", 
			"Bool", "Number", "Integer", "Decimal", "SCIENTIFIC_NUMBER", "LINE_COMMENT", 
			"COMMENT", "EOL", "WS"
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


	public iniLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "ini.g4"; }

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
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2\30\u00db\b\1\4\2"+
		"\t\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4"+
		"\13\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22"+
		"\t\22\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31"+
		"\t\31\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\3\2\3\2\3\3\3"+
		"\3\3\4\3\4\3\5\3\5\5\5F\n\5\3\6\3\6\3\7\3\7\3\b\3\b\3\t\3\t\3\n\3\n\3"+
		"\n\3\n\5\nT\n\n\3\13\3\13\5\13X\n\13\3\13\3\13\3\f\3\f\3\f\5\f_\n\f\3"+
		"\r\3\r\6\rc\n\r\r\r\16\rd\3\r\3\r\3\r\3\r\3\r\3\16\3\16\3\17\6\17o\n\17"+
		"\r\17\16\17p\3\20\3\20\5\20u\n\20\3\21\3\21\7\21y\n\21\f\21\16\21|\13"+
		"\21\3\22\3\22\3\22\3\22\3\22\3\22\5\22\u0084\n\22\3\23\3\23\3\23\3\23"+
		"\3\23\5\23\u008b\n\23\3\24\3\24\5\24\u008f\n\24\3\25\5\25\u0092\n\25\3"+
		"\25\3\25\3\25\5\25\u0097\n\25\3\26\6\26\u009a\n\26\r\26\16\26\u009b\3"+
		"\27\5\27\u009f\n\27\3\27\3\27\3\27\3\30\5\30\u00a5\n\30\3\30\3\30\3\30"+
		"\5\30\u00aa\n\30\3\30\3\30\5\30\u00ae\n\30\3\31\3\31\3\32\3\32\3\32\3"+
		"\32\5\32\u00b6\n\32\3\33\3\33\3\33\3\33\7\33\u00bc\n\33\f\33\16\33\u00bf"+
		"\13\33\3\33\3\33\3\33\3\33\3\34\3\34\3\34\3\34\7\34\u00c9\n\34\f\34\16"+
		"\34\u00cc\13\34\3\34\3\34\3\34\3\34\3\34\3\35\3\35\3\36\6\36\u00d6\n\36"+
		"\r\36\16\36\u00d7\3\36\3\36\3\u00ca\2\37\3\3\5\4\7\5\t\6\13\7\r\b\17\t"+
		"\21\n\23\13\25\f\27\2\31\2\33\2\35\2\37\2!\r#\16%\17\'\20)\21+\22-\23"+
		"/\24\61\2\63\2\65\25\67\269\27;\30\3\2\13\t\2))^^ddhhppttvv\5\2\62;CH"+
		"ch\6\2\f\f\17\17))^^\13\2&&C\\aac|\u0393\u03ab\u03b3\u03cb\u03d3\u03d3"+
		"\u03d8\u03d8\u03f2\u03f3\n\2&&\62;C\\aac|\u0393\u03ab\u03b3\u03cb\u03d3"+
		"\u03d3\3\2\62;\4\2GGgg\4\2\f\f\17\17\5\2\13\13\16\16\"\"\2\u00ed\2\3\3"+
		"\2\2\2\2\5\3\2\2\2\2\7\3\2\2\2\2\t\3\2\2\2\2\13\3\2\2\2\2\r\3\2\2\2\2"+
		"\17\3\2\2\2\2\21\3\2\2\2\2\23\3\2\2\2\2\25\3\2\2\2\2!\3\2\2\2\2#\3\2\2"+
		"\2\2%\3\2\2\2\2\'\3\2\2\2\2)\3\2\2\2\2+\3\2\2\2\2-\3\2\2\2\2/\3\2\2\2"+
		"\2\65\3\2\2\2\2\67\3\2\2\2\29\3\2\2\2\2;\3\2\2\2\3=\3\2\2\2\5?\3\2\2\2"+
		"\7A\3\2\2\2\tE\3\2\2\2\13G\3\2\2\2\rI\3\2\2\2\17K\3\2\2\2\21M\3\2\2\2"+
		"\23S\3\2\2\2\25U\3\2\2\2\27^\3\2\2\2\31`\3\2\2\2\33k\3\2\2\2\35n\3\2\2"+
		"\2\37t\3\2\2\2!v\3\2\2\2#\u0083\3\2\2\2%\u008a\3\2\2\2\'\u008e\3\2\2\2"+
		")\u0096\3\2\2\2+\u0099\3\2\2\2-\u009e\3\2\2\2/\u00a4\3\2\2\2\61\u00af"+
		"\3\2\2\2\63\u00b5\3\2\2\2\65\u00b7\3\2\2\2\67\u00c4\3\2\2\29\u00d2\3\2"+
		"\2\2;\u00d5\3\2\2\2=>\7]\2\2>\4\3\2\2\2?@\7_\2\2@\6\3\2\2\2AB\7.\2\2B"+
		"\b\3\2\2\2CF\5%\23\2DF\5#\22\2EC\3\2\2\2ED\3\2\2\2F\n\3\2\2\2GH\7\u00b1"+
		"\2\2H\f\3\2\2\2IJ\7\u207c\2\2J\16\3\2\2\2KL\7-\2\2L\20\3\2\2\2MN\7/\2"+
		"\2N\22\3\2\2\2OT\7?\2\2PQ\7<\2\2QT\7?\2\2RT\7\u2256\2\2SO\3\2\2\2SP\3"+
		"\2\2\2SR\3\2\2\2T\24\3\2\2\2UW\7)\2\2VX\5\35\17\2WV\3\2\2\2WX\3\2\2\2"+
		"XY\3\2\2\2YZ\7)\2\2Z\26\3\2\2\2[\\\7^\2\2\\_\t\2\2\2]_\5\31\r\2^[\3\2"+
		"\2\2^]\3\2\2\2_\30\3\2\2\2`b\7^\2\2ac\7w\2\2ba\3\2\2\2cd\3\2\2\2db\3\2"+
		"\2\2de\3\2\2\2ef\3\2\2\2fg\5\33\16\2gh\5\33\16\2hi\5\33\16\2ij\5\33\16"+
		"\2j\32\3\2\2\2kl\t\3\2\2l\34\3\2\2\2mo\5\37\20\2nm\3\2\2\2op\3\2\2\2p"+
		"n\3\2\2\2pq\3\2\2\2q\36\3\2\2\2ru\n\4\2\2su\5\27\f\2tr\3\2\2\2ts\3\2\2"+
		"\2u \3\2\2\2vz\t\5\2\2wy\t\6\2\2xw\3\2\2\2y|\3\2\2\2zx\3\2\2\2z{\3\2\2"+
		"\2{\"\3\2\2\2|z\3\2\2\2}~\7h\2\2~\177\7c\2\2\177\u0080\7n\2\2\u0080\u0081"+
		"\7u\2\2\u0081\u0084\7g\2\2\u0082\u0084\7\u22a7\2\2\u0083}\3\2\2\2\u0083"+
		"\u0082\3\2\2\2\u0084$\3\2\2\2\u0085\u0086\7v\2\2\u0086\u0087\7t\2\2\u0087"+
		"\u0088\7w\2\2\u0088\u008b\7g\2\2\u0089\u008b\7\u22a6\2\2\u008a\u0085\3"+
		"\2\2\2\u008a\u0089\3\2\2\2\u008b&\3\2\2\2\u008c\u008f\5%\23\2\u008d\u008f"+
		"\5#\22\2\u008e\u008c\3\2\2\2\u008e\u008d\3\2\2\2\u008f(\3\2\2\2\u0090"+
		"\u0092\5\63\32\2\u0091\u0090\3\2\2\2\u0091\u0092\3\2\2\2\u0092\u0093\3"+
		"\2\2\2\u0093\u0097\5+\26\2\u0094\u0097\5-\27\2\u0095\u0097\5/\30\2\u0096"+
		"\u0091\3\2\2\2\u0096\u0094\3\2\2\2\u0096\u0095\3\2\2\2\u0097*\3\2\2\2"+
		"\u0098\u009a\t\7\2\2\u0099\u0098\3\2\2\2\u009a\u009b\3\2\2\2\u009b\u0099"+
		"\3\2\2\2\u009b\u009c\3\2\2\2\u009c,\3\2\2\2\u009d\u009f\5+\26\2\u009e"+
		"\u009d\3\2\2\2\u009e\u009f\3\2\2\2\u009f\u00a0\3\2\2\2\u00a0\u00a1\7\60"+
		"\2\2\u00a1\u00a2\5+\26\2\u00a2.\3\2\2\2\u00a3\u00a5\5\63\32\2\u00a4\u00a3"+
		"\3\2\2\2\u00a4\u00a5\3\2\2\2\u00a5\u00a6\3\2\2\2\u00a6\u00ad\5-\27\2\u00a7"+
		"\u00a9\5\61\31\2\u00a8\u00aa\5\63\32\2\u00a9\u00a8\3\2\2\2\u00a9\u00aa"+
		"\3\2\2\2\u00aa\u00ab\3\2\2\2\u00ab\u00ac\5+\26\2\u00ac\u00ae\3\2\2\2\u00ad"+
		"\u00a7\3\2\2\2\u00ad\u00ae\3\2\2\2\u00ae\60\3\2\2\2\u00af\u00b0\t\b\2"+
		"\2\u00b0\62\3\2\2\2\u00b1\u00b6\5\17\b\2\u00b2\u00b6\5\r\7\2\u00b3\u00b6"+
		"\5\21\t\2\u00b4\u00b6\5\13\6\2\u00b5\u00b1\3\2\2\2\u00b5\u00b2\3\2\2\2"+
		"\u00b5\u00b3\3\2\2\2\u00b5\u00b4\3\2\2\2\u00b6\64\3\2\2\2\u00b7\u00b8"+
		"\7\61\2\2\u00b8\u00b9\7\61\2\2\u00b9\u00bd\3\2\2\2\u00ba\u00bc\n\t\2\2"+
		"\u00bb\u00ba\3\2\2\2\u00bc\u00bf\3\2\2\2\u00bd\u00bb\3\2\2\2\u00bd\u00be"+
		"\3\2\2\2\u00be\u00c0\3\2\2\2\u00bf\u00bd\3\2\2\2\u00c0\u00c1\59\35\2\u00c1"+
		"\u00c2\3\2\2\2\u00c2\u00c3\b\33\2\2\u00c3\66\3\2\2\2\u00c4\u00c5\7\61"+
		"\2\2\u00c5\u00c6\7,\2\2\u00c6\u00ca\3\2\2\2\u00c7\u00c9\13\2\2\2\u00c8"+
		"\u00c7\3\2\2\2\u00c9\u00cc\3\2\2\2\u00ca\u00cb\3\2\2\2\u00ca\u00c8\3\2"+
		"\2\2\u00cb\u00cd\3\2\2\2\u00cc\u00ca\3\2\2\2\u00cd\u00ce\7,\2\2\u00ce"+
		"\u00cf\7\61\2\2\u00cf\u00d0\3\2\2\2\u00d0\u00d1\b\34\2\2\u00d18\3\2\2"+
		"\2\u00d2\u00d3\t\t\2\2\u00d3:\3\2\2\2\u00d4\u00d6\t\n\2\2\u00d5\u00d4"+
		"\3\2\2\2\u00d6\u00d7\3\2\2\2\u00d7\u00d5\3\2\2\2\u00d7\u00d8\3\2\2\2\u00d8"+
		"\u00d9\3\2\2\2\u00d9\u00da\b\36\2\2\u00da<\3\2\2\2\31\2ESW^dptz\u0083"+
		"\u008a\u008e\u0091\u0096\u009b\u009e\u00a4\u00a9\u00ad\u00b5\u00bd\u00ca"+
		"\u00d7\3\b\2\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}