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
		Bool=14, Number=15, Integer=16, Decimal=17, SCIENTIFIC_NUMBER=18, COMMENT=19, 
		EOL=20, WS=21;
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
			"COMMENT", "EOL", "WS"
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
			"Bool", "Number", "Integer", "Decimal", "SCIENTIFIC_NUMBER", "COMMENT", 
			"EOL", "WS"
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
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2\27\u00ca\b\1\4\2"+
		"\t\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4"+
		"\13\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22"+
		"\t\22\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31"+
		"\t\31\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\3\2\3\2\3\3\3\3\3\4\3\4"+
		"\3\5\3\5\5\5D\n\5\3\6\3\6\3\7\3\7\3\b\3\b\3\t\3\t\3\n\3\n\3\n\3\n\5\n"+
		"R\n\n\3\13\3\13\5\13V\n\13\3\13\3\13\3\f\3\f\3\f\5\f]\n\f\3\r\3\r\6\r"+
		"a\n\r\r\r\16\rb\3\r\3\r\3\r\3\r\3\r\3\16\3\16\3\17\6\17m\n\17\r\17\16"+
		"\17n\3\20\3\20\5\20s\n\20\3\21\3\21\7\21w\n\21\f\21\16\21z\13\21\3\22"+
		"\3\22\3\22\3\22\3\22\3\22\5\22\u0082\n\22\3\23\3\23\3\23\3\23\3\23\5\23"+
		"\u0089\n\23\3\24\3\24\5\24\u008d\n\24\3\25\5\25\u0090\n\25\3\25\3\25\3"+
		"\25\5\25\u0095\n\25\3\26\6\26\u0098\n\26\r\26\16\26\u0099\3\27\5\27\u009d"+
		"\n\27\3\27\3\27\3\27\3\30\3\30\3\30\5\30\u00a5\n\30\3\30\3\30\5\30\u00a9"+
		"\n\30\3\31\3\31\3\32\3\32\3\32\3\32\5\32\u00b1\n\32\3\33\3\33\3\33\5\33"+
		"\u00b6\n\33\3\33\7\33\u00b9\n\33\f\33\16\33\u00bc\13\33\3\33\3\33\3\33"+
		"\3\33\3\34\3\34\3\35\6\35\u00c5\n\35\r\35\16\35\u00c6\3\35\3\35\2\2\36"+
		"\3\3\5\4\7\5\t\6\13\7\r\b\17\t\21\n\23\13\25\f\27\2\31\2\33\2\35\2\37"+
		"\2!\r#\16%\17\'\20)\21+\22-\23/\24\61\2\63\2\65\25\67\269\27\3\2\13\t"+
		"\2))^^ddhhppttvv\5\2\62;CHch\6\2\f\f\17\17))^^\13\2&&C\\aac|\u0393\u03ab"+
		"\u03b3\u03cb\u03d3\u03d3\u03d8\u03d8\u03f2\u03f3\n\2&&\62;C\\aac|\u0393"+
		"\u03ab\u03b3\u03cb\u03d3\u03d3\3\2\62;\4\2GGgg\4\2\f\f\17\17\4\2\13\13"+
		"\"\"\2\u00db\2\3\3\2\2\2\2\5\3\2\2\2\2\7\3\2\2\2\2\t\3\2\2\2\2\13\3\2"+
		"\2\2\2\r\3\2\2\2\2\17\3\2\2\2\2\21\3\2\2\2\2\23\3\2\2\2\2\25\3\2\2\2\2"+
		"!\3\2\2\2\2#\3\2\2\2\2%\3\2\2\2\2\'\3\2\2\2\2)\3\2\2\2\2+\3\2\2\2\2-\3"+
		"\2\2\2\2/\3\2\2\2\2\65\3\2\2\2\2\67\3\2\2\2\29\3\2\2\2\3;\3\2\2\2\5=\3"+
		"\2\2\2\7?\3\2\2\2\tC\3\2\2\2\13E\3\2\2\2\rG\3\2\2\2\17I\3\2\2\2\21K\3"+
		"\2\2\2\23Q\3\2\2\2\25S\3\2\2\2\27\\\3\2\2\2\31^\3\2\2\2\33i\3\2\2\2\35"+
		"l\3\2\2\2\37r\3\2\2\2!t\3\2\2\2#\u0081\3\2\2\2%\u0088\3\2\2\2\'\u008c"+
		"\3\2\2\2)\u0094\3\2\2\2+\u0097\3\2\2\2-\u009c\3\2\2\2/\u00a1\3\2\2\2\61"+
		"\u00aa\3\2\2\2\63\u00b0\3\2\2\2\65\u00b5\3\2\2\2\67\u00c1\3\2\2\29\u00c4"+
		"\3\2\2\2;<\7]\2\2<\4\3\2\2\2=>\7_\2\2>\6\3\2\2\2?@\7.\2\2@\b\3\2\2\2A"+
		"D\5%\23\2BD\5#\22\2CA\3\2\2\2CB\3\2\2\2D\n\3\2\2\2EF\7\u00b1\2\2F\f\3"+
		"\2\2\2GH\7\u207c\2\2H\16\3\2\2\2IJ\7-\2\2J\20\3\2\2\2KL\7/\2\2L\22\3\2"+
		"\2\2MR\7?\2\2NO\7<\2\2OR\7?\2\2PR\7\u2256\2\2QM\3\2\2\2QN\3\2\2\2QP\3"+
		"\2\2\2R\24\3\2\2\2SU\7)\2\2TV\5\35\17\2UT\3\2\2\2UV\3\2\2\2VW\3\2\2\2"+
		"WX\7)\2\2X\26\3\2\2\2YZ\7^\2\2Z]\t\2\2\2[]\5\31\r\2\\Y\3\2\2\2\\[\3\2"+
		"\2\2]\30\3\2\2\2^`\7^\2\2_a\7w\2\2`_\3\2\2\2ab\3\2\2\2b`\3\2\2\2bc\3\2"+
		"\2\2cd\3\2\2\2de\5\33\16\2ef\5\33\16\2fg\5\33\16\2gh\5\33\16\2h\32\3\2"+
		"\2\2ij\t\3\2\2j\34\3\2\2\2km\5\37\20\2lk\3\2\2\2mn\3\2\2\2nl\3\2\2\2n"+
		"o\3\2\2\2o\36\3\2\2\2ps\n\4\2\2qs\5\27\f\2rp\3\2\2\2rq\3\2\2\2s \3\2\2"+
		"\2tx\t\5\2\2uw\t\6\2\2vu\3\2\2\2wz\3\2\2\2xv\3\2\2\2xy\3\2\2\2y\"\3\2"+
		"\2\2zx\3\2\2\2{|\7h\2\2|}\7c\2\2}~\7n\2\2~\177\7u\2\2\177\u0082\7g\2\2"+
		"\u0080\u0082\7\u22a7\2\2\u0081{\3\2\2\2\u0081\u0080\3\2\2\2\u0082$\3\2"+
		"\2\2\u0083\u0084\7v\2\2\u0084\u0085\7t\2\2\u0085\u0086\7w\2\2\u0086\u0089"+
		"\7g\2\2\u0087\u0089\7\u22a6\2\2\u0088\u0083\3\2\2\2\u0088\u0087\3\2\2"+
		"\2\u0089&\3\2\2\2\u008a\u008d\5%\23\2\u008b\u008d\5#\22\2\u008c\u008a"+
		"\3\2\2\2\u008c\u008b\3\2\2\2\u008d(\3\2\2\2\u008e\u0090\5\63\32\2\u008f"+
		"\u008e\3\2\2\2\u008f\u0090\3\2\2\2\u0090\u0091\3\2\2\2\u0091\u0095\5+"+
		"\26\2\u0092\u0095\5-\27\2\u0093\u0095\5/\30\2\u0094\u008f\3\2\2\2\u0094"+
		"\u0092\3\2\2\2\u0094\u0093\3\2\2\2\u0095*\3\2\2\2\u0096\u0098\t\7\2\2"+
		"\u0097\u0096\3\2\2\2\u0098\u0099\3\2\2\2\u0099\u0097\3\2\2\2\u0099\u009a"+
		"\3\2\2\2\u009a,\3\2\2\2\u009b\u009d\5+\26\2\u009c\u009b\3\2\2\2\u009c"+
		"\u009d\3\2\2\2\u009d\u009e\3\2\2\2\u009e\u009f\7\60\2\2\u009f\u00a0\5"+
		"+\26\2\u00a0.\3\2\2\2\u00a1\u00a8\5-\27\2\u00a2\u00a4\5\61\31\2\u00a3"+
		"\u00a5\5\63\32\2\u00a4\u00a3\3\2\2\2\u00a4\u00a5\3\2\2\2\u00a5\u00a6\3"+
		"\2\2\2\u00a6\u00a7\5+\26\2\u00a7\u00a9\3\2\2\2\u00a8\u00a2\3\2\2\2\u00a8"+
		"\u00a9\3\2\2\2\u00a9\60\3\2\2\2\u00aa\u00ab\t\b\2\2\u00ab\62\3\2\2\2\u00ac"+
		"\u00b1\5\17\b\2\u00ad\u00b1\5\r\7\2\u00ae\u00b1\5\21\t\2\u00af\u00b1\5"+
		"\13\6\2\u00b0\u00ac\3\2\2\2\u00b0\u00ad\3\2\2\2\u00b0\u00ae\3\2\2\2\u00b0"+
		"\u00af\3\2\2\2\u00b1\64\3\2\2\2\u00b2\u00b6\7%\2\2\u00b3\u00b4\7\61\2"+
		"\2\u00b4\u00b6\7\61\2\2\u00b5\u00b2\3\2\2\2\u00b5\u00b3\3\2\2\2\u00b6"+
		"\u00ba\3\2\2\2\u00b7\u00b9\n\t\2\2\u00b8\u00b7\3\2\2\2\u00b9\u00bc\3\2"+
		"\2\2\u00ba\u00b8\3\2\2\2\u00ba\u00bb\3\2\2\2\u00bb\u00bd\3\2\2\2\u00bc"+
		"\u00ba\3\2\2\2\u00bd\u00be\5\67\34\2\u00be\u00bf\3\2\2\2\u00bf\u00c0\b"+
		"\33\2\2\u00c0\66\3\2\2\2\u00c1\u00c2\t\t\2\2\u00c28\3\2\2\2\u00c3\u00c5"+
		"\t\n\2\2\u00c4\u00c3\3\2\2\2\u00c5\u00c6\3\2\2\2\u00c6\u00c4\3\2\2\2\u00c6"+
		"\u00c7\3\2\2\2\u00c7\u00c8\3\2\2\2\u00c8\u00c9\b\35\2\2\u00c9:\3\2\2\2"+
		"\30\2CQU\\bnrx\u0081\u0088\u008c\u008f\u0094\u0099\u009c\u00a4\u00a8\u00b0"+
		"\u00b5\u00ba\u00c6\3\b\2\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}