// Generated from QDLParser.g4 by ANTLR 4.7.2
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
	static { RuntimeMetaData.checkVersion("4.7.2", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, T__2=3, T__3=4, T__4=5, T__5=6, T__6=7, T__7=8, Number=9, 
		ID=10, Bool=11, ASSIGN=12, FuncStart=13, BOOL_TRUE=14, BOOL_FALSE=15, 
		Null=16, STRING=17, Times=18, Divide=19, PlusPlus=20, Plus=21, MinusMinus=22, 
		Minus=23, LessThan=24, GreaterThan=25, LessEquals=26, MoreEquals=27, Equals=28, 
		NotEquals=29, And=30, Or=31, Backtick=32, Percent=33, LeftBracket=34, 
		LogicalIf=35, LogicalThen=36, LogicalElse=37, WhileLoop=38, WhileDo=39, 
		SwitchStatement=40, DefineStatement=41, BodyStatement=42, ModuleStatement=43, 
		TryStatement=44, CatchStatement=45, COMMENT=46, LINE_COMMENT=47, WS2=48, 
		FDOC=49;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	private static String[] makeRuleNames() {
		return new String[] {
			"T__0", "T__1", "T__2", "T__3", "T__4", "T__5", "T__6", "T__7", "Number", 
			"ID", "Bool", "ASSIGN", "FuncStart", "BOOL_TRUE", "BOOL_FALSE", "Null", 
			"STRING", "Times", "Divide", "PlusPlus", "Plus", "MinusMinus", "Minus", 
			"LessThan", "GreaterThan", "LessEquals", "MoreEquals", "Equals", "NotEquals", 
			"And", "Or", "Backtick", "Percent", "LeftBracket", "LogicalIf", "LogicalThen", 
			"LogicalElse", "WhileLoop", "WhileDo", "SwitchStatement", "DefineStatement", 
			"BodyStatement", "ModuleStatement", "TryStatement", "CatchStatement", 
			"ESC", "COMMENT", "LINE_COMMENT", "WS2", "FDOC"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "','", "')'", "'!'", "'^'", "'=<'", "'=>'", "'('", "';'", null, 
			null, null, null, null, "'true'", "'false'", "'null'", null, "'*'", "'/'", 
			"'++'", "'+'", "'--'", "'-'", "'<'", "'>'", "'<='", "'>='", "'=='", "'!='", 
			"'&&'", "'||'", "'`'", "'%'", "']'", "'if['", "']then['", "']else['", 
			"'while['", "']do['", "'switch['", "'define['", "']body['", "'module['", 
			"'try['", "']catch['"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, null, null, null, null, null, null, null, null, "Number", "ID", 
			"Bool", "ASSIGN", "FuncStart", "BOOL_TRUE", "BOOL_FALSE", "Null", "STRING", 
			"Times", "Divide", "PlusPlus", "Plus", "MinusMinus", "Minus", "LessThan", 
			"GreaterThan", "LessEquals", "MoreEquals", "Equals", "NotEquals", "And", 
			"Or", "Backtick", "Percent", "LeftBracket", "LogicalIf", "LogicalThen", 
			"LogicalElse", "WhileLoop", "WhileDo", "SwitchStatement", "DefineStatement", 
			"BodyStatement", "ModuleStatement", "TryStatement", "CatchStatement", 
			"COMMENT", "LINE_COMMENT", "WS2", "FDOC"
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
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2\63\u0167\b\1\4\2"+
		"\t\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4"+
		"\13\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22"+
		"\t\22\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31"+
		"\t\31\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4 \t"+
		" \4!\t!\4\"\t\"\4#\t#\4$\t$\4%\t%\4&\t&\4\'\t\'\4(\t(\4)\t)\4*\t*\4+\t"+
		"+\4,\t,\4-\t-\4.\t.\4/\t/\4\60\t\60\4\61\t\61\4\62\t\62\4\63\t\63\3\2"+
		"\3\2\3\3\3\3\3\4\3\4\3\5\3\5\3\6\3\6\3\6\3\7\3\7\3\7\3\b\3\b\3\t\3\t\3"+
		"\n\6\n{\n\n\r\n\16\n|\3\n\5\n\u0080\n\n\3\n\7\n\u0083\n\n\f\n\16\n\u0086"+
		"\13\n\3\n\3\n\6\n\u008a\n\n\r\n\16\n\u008b\5\n\u008e\n\n\3\13\3\13\7\13"+
		"\u0092\n\13\f\13\16\13\u0095\13\13\3\f\3\f\5\f\u0099\n\f\3\r\3\r\3\r\3"+
		"\r\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3\r\5\r\u00a9\n\r\3\16\3\16\3\16"+
		"\3\17\3\17\3\17\3\17\3\17\3\20\3\20\3\20\3\20\3\20\3\20\3\21\3\21\3\21"+
		"\3\21\3\21\3\22\3\22\3\22\7\22\u00c1\n\22\f\22\16\22\u00c4\13\22\3\22"+
		"\3\22\3\23\3\23\3\24\3\24\3\25\3\25\3\25\3\26\3\26\3\27\3\27\3\27\3\30"+
		"\3\30\3\31\3\31\3\32\3\32\3\33\3\33\3\33\3\34\3\34\3\34\3\35\3\35\3\35"+
		"\3\36\3\36\3\36\3\37\3\37\3\37\3 \3 \3 \3!\3!\3\"\3\"\3#\3#\3$\3$\3$\3"+
		"$\3%\3%\3%\3%\3%\3%\3%\3&\3&\3&\3&\3&\3&\3&\3\'\3\'\3\'\3\'\3\'\3\'\3"+
		"\'\3(\3(\3(\3(\3(\3)\3)\3)\3)\3)\3)\3)\3)\3*\3*\3*\3*\3*\3*\3*\3*\3+\3"+
		"+\3+\3+\3+\3+\3+\3,\3,\3,\3,\3,\3,\3,\3,\3-\3-\3-\3-\3-\3.\3.\3.\3.\3"+
		".\3.\3.\3.\3/\3/\3/\3\60\3\60\3\60\3\60\7\60\u0143\n\60\f\60\16\60\u0146"+
		"\13\60\3\60\3\60\3\60\3\60\3\60\3\61\3\61\3\61\3\61\7\61\u0151\n\61\f"+
		"\61\16\61\u0154\13\61\3\61\3\61\3\62\6\62\u0159\n\62\r\62\16\62\u015a"+
		"\3\62\3\62\3\63\3\63\3\63\3\63\7\63\u0163\n\63\f\63\16\63\u0166\13\63"+
		"\4\u00c2\u0144\2\64\3\3\5\4\7\5\t\6\13\7\r\b\17\t\21\n\23\13\25\f\27\r"+
		"\31\16\33\17\35\20\37\21!\22#\23%\24\'\25)\26+\27-\30/\31\61\32\63\33"+
		"\65\34\67\359\36;\37= ?!A\"C#E$G%I&K\'M(O)Q*S+U,W-Y.[/]\2_\60a\61c\62"+
		"e\63\3\2\7\3\2\62;\6\2%&C\\aac|\b\2%&\60\60\62;C\\aac|\4\2\f\f\17\17\5"+
		"\2\13\f\17\17\"\"\2\u0178\2\3\3\2\2\2\2\5\3\2\2\2\2\7\3\2\2\2\2\t\3\2"+
		"\2\2\2\13\3\2\2\2\2\r\3\2\2\2\2\17\3\2\2\2\2\21\3\2\2\2\2\23\3\2\2\2\2"+
		"\25\3\2\2\2\2\27\3\2\2\2\2\31\3\2\2\2\2\33\3\2\2\2\2\35\3\2\2\2\2\37\3"+
		"\2\2\2\2!\3\2\2\2\2#\3\2\2\2\2%\3\2\2\2\2\'\3\2\2\2\2)\3\2\2\2\2+\3\2"+
		"\2\2\2-\3\2\2\2\2/\3\2\2\2\2\61\3\2\2\2\2\63\3\2\2\2\2\65\3\2\2\2\2\67"+
		"\3\2\2\2\29\3\2\2\2\2;\3\2\2\2\2=\3\2\2\2\2?\3\2\2\2\2A\3\2\2\2\2C\3\2"+
		"\2\2\2E\3\2\2\2\2G\3\2\2\2\2I\3\2\2\2\2K\3\2\2\2\2M\3\2\2\2\2O\3\2\2\2"+
		"\2Q\3\2\2\2\2S\3\2\2\2\2U\3\2\2\2\2W\3\2\2\2\2Y\3\2\2\2\2[\3\2\2\2\2_"+
		"\3\2\2\2\2a\3\2\2\2\2c\3\2\2\2\2e\3\2\2\2\3g\3\2\2\2\5i\3\2\2\2\7k\3\2"+
		"\2\2\tm\3\2\2\2\13o\3\2\2\2\rr\3\2\2\2\17u\3\2\2\2\21w\3\2\2\2\23\u008d"+
		"\3\2\2\2\25\u008f\3\2\2\2\27\u0098\3\2\2\2\31\u00a8\3\2\2\2\33\u00aa\3"+
		"\2\2\2\35\u00ad\3\2\2\2\37\u00b2\3\2\2\2!\u00b8\3\2\2\2#\u00bd\3\2\2\2"+
		"%\u00c7\3\2\2\2\'\u00c9\3\2\2\2)\u00cb\3\2\2\2+\u00ce\3\2\2\2-\u00d0\3"+
		"\2\2\2/\u00d3\3\2\2\2\61\u00d5\3\2\2\2\63\u00d7\3\2\2\2\65\u00d9\3\2\2"+
		"\2\67\u00dc\3\2\2\29\u00df\3\2\2\2;\u00e2\3\2\2\2=\u00e5\3\2\2\2?\u00e8"+
		"\3\2\2\2A\u00eb\3\2\2\2C\u00ed\3\2\2\2E\u00ef\3\2\2\2G\u00f1\3\2\2\2I"+
		"\u00f5\3\2\2\2K\u00fc\3\2\2\2M\u0103\3\2\2\2O\u010a\3\2\2\2Q\u010f\3\2"+
		"\2\2S\u0117\3\2\2\2U\u011f\3\2\2\2W\u0126\3\2\2\2Y\u012e\3\2\2\2[\u0133"+
		"\3\2\2\2]\u013b\3\2\2\2_\u013e\3\2\2\2a\u014c\3\2\2\2c\u0158\3\2\2\2e"+
		"\u015e\3\2\2\2gh\7.\2\2h\4\3\2\2\2ij\7+\2\2j\6\3\2\2\2kl\7#\2\2l\b\3\2"+
		"\2\2mn\7`\2\2n\n\3\2\2\2op\7?\2\2pq\7>\2\2q\f\3\2\2\2rs\7?\2\2st\7@\2"+
		"\2t\16\3\2\2\2uv\7*\2\2v\20\3\2\2\2wx\7=\2\2x\22\3\2\2\2y{\t\2\2\2zy\3"+
		"\2\2\2{|\3\2\2\2|z\3\2\2\2|}\3\2\2\2}\177\3\2\2\2~\u0080\7\60\2\2\177"+
		"~\3\2\2\2\177\u0080\3\2\2\2\u0080\u0084\3\2\2\2\u0081\u0083\t\2\2\2\u0082"+
		"\u0081\3\2\2\2\u0083\u0086\3\2\2\2\u0084\u0082\3\2\2\2\u0084\u0085\3\2"+
		"\2\2\u0085\u008e\3\2\2\2\u0086\u0084\3\2\2\2\u0087\u0089\7\60\2\2\u0088"+
		"\u008a\t\2\2\2\u0089\u0088\3\2\2\2\u008a\u008b\3\2\2\2\u008b\u0089\3\2"+
		"\2\2\u008b\u008c\3\2\2\2\u008c\u008e\3\2\2\2\u008dz\3\2\2\2\u008d\u0087"+
		"\3\2\2\2\u008e\24\3\2\2\2\u008f\u0093\t\3\2\2\u0090\u0092\t\4\2\2\u0091"+
		"\u0090\3\2\2\2\u0092\u0095\3\2\2\2\u0093\u0091\3\2\2\2\u0093\u0094\3\2"+
		"\2\2\u0094\26\3\2\2\2\u0095\u0093\3\2\2\2\u0096\u0099\5\35\17\2\u0097"+
		"\u0099\5\37\20\2\u0098\u0096\3\2\2\2\u0098\u0097\3\2\2\2\u0099\30\3\2"+
		"\2\2\u009a\u009b\7<\2\2\u009b\u00a9\7?\2\2\u009c\u009d\7-\2\2\u009d\u00a9"+
		"\7?\2\2\u009e\u009f\7/\2\2\u009f\u00a9\7?\2\2\u00a0\u00a1\7,\2\2\u00a1"+
		"\u00a9\7?\2\2\u00a2\u00a3\7\61\2\2\u00a3\u00a9\7?\2\2\u00a4\u00a5\7\'"+
		"\2\2\u00a5\u00a9\7?\2\2\u00a6\u00a7\7`\2\2\u00a7\u00a9\7?\2\2\u00a8\u009a"+
		"\3\2\2\2\u00a8\u009c\3\2\2\2\u00a8\u009e\3\2\2\2\u00a8\u00a0\3\2\2\2\u00a8"+
		"\u00a2\3\2\2\2\u00a8\u00a4\3\2\2\2\u00a8\u00a6\3\2\2\2\u00a9\32\3\2\2"+
		"\2\u00aa\u00ab\5\25\13\2\u00ab\u00ac\7*\2\2\u00ac\34\3\2\2\2\u00ad\u00ae"+
		"\7v\2\2\u00ae\u00af\7t\2\2\u00af\u00b0\7w\2\2\u00b0\u00b1\7g\2\2\u00b1"+
		"\36\3\2\2\2\u00b2\u00b3\7h\2\2\u00b3\u00b4\7c\2\2\u00b4\u00b5\7n\2\2\u00b5"+
		"\u00b6\7u\2\2\u00b6\u00b7\7g\2\2\u00b7 \3\2\2\2\u00b8\u00b9\7p\2\2\u00b9"+
		"\u00ba\7w\2\2\u00ba\u00bb\7n\2\2\u00bb\u00bc\7n\2\2\u00bc\"\3\2\2\2\u00bd"+
		"\u00c2\7)\2\2\u00be\u00c1\5]/\2\u00bf\u00c1\13\2\2\2\u00c0\u00be\3\2\2"+
		"\2\u00c0\u00bf\3\2\2\2\u00c1\u00c4\3\2\2\2\u00c2\u00c3\3\2\2\2\u00c2\u00c0"+
		"\3\2\2\2\u00c3\u00c5\3\2\2\2\u00c4\u00c2\3\2\2\2\u00c5\u00c6\7)\2\2\u00c6"+
		"$\3\2\2\2\u00c7\u00c8\7,\2\2\u00c8&\3\2\2\2\u00c9\u00ca\7\61\2\2\u00ca"+
		"(\3\2\2\2\u00cb\u00cc\7-\2\2\u00cc\u00cd\7-\2\2\u00cd*\3\2\2\2\u00ce\u00cf"+
		"\7-\2\2\u00cf,\3\2\2\2\u00d0\u00d1\7/\2\2\u00d1\u00d2\7/\2\2\u00d2.\3"+
		"\2\2\2\u00d3\u00d4\7/\2\2\u00d4\60\3\2\2\2\u00d5\u00d6\7>\2\2\u00d6\62"+
		"\3\2\2\2\u00d7\u00d8\7@\2\2\u00d8\64\3\2\2\2\u00d9\u00da\7>\2\2\u00da"+
		"\u00db\7?\2\2\u00db\66\3\2\2\2\u00dc\u00dd\7@\2\2\u00dd\u00de\7?\2\2\u00de"+
		"8\3\2\2\2\u00df\u00e0\7?\2\2\u00e0\u00e1\7?\2\2\u00e1:\3\2\2\2\u00e2\u00e3"+
		"\7#\2\2\u00e3\u00e4\7?\2\2\u00e4<\3\2\2\2\u00e5\u00e6\7(\2\2\u00e6\u00e7"+
		"\7(\2\2\u00e7>\3\2\2\2\u00e8\u00e9\7~\2\2\u00e9\u00ea\7~\2\2\u00ea@\3"+
		"\2\2\2\u00eb\u00ec\7b\2\2\u00ecB\3\2\2\2\u00ed\u00ee\7\'\2\2\u00eeD\3"+
		"\2\2\2\u00ef\u00f0\7_\2\2\u00f0F\3\2\2\2\u00f1\u00f2\7k\2\2\u00f2\u00f3"+
		"\7h\2\2\u00f3\u00f4\7]\2\2\u00f4H\3\2\2\2\u00f5\u00f6\7_\2\2\u00f6\u00f7"+
		"\7v\2\2\u00f7\u00f8\7j\2\2\u00f8\u00f9\7g\2\2\u00f9\u00fa\7p\2\2\u00fa"+
		"\u00fb\7]\2\2\u00fbJ\3\2\2\2\u00fc\u00fd\7_\2\2\u00fd\u00fe\7g\2\2\u00fe"+
		"\u00ff\7n\2\2\u00ff\u0100\7u\2\2\u0100\u0101\7g\2\2\u0101\u0102\7]\2\2"+
		"\u0102L\3\2\2\2\u0103\u0104\7y\2\2\u0104\u0105\7j\2\2\u0105\u0106\7k\2"+
		"\2\u0106\u0107\7n\2\2\u0107\u0108\7g\2\2\u0108\u0109\7]\2\2\u0109N\3\2"+
		"\2\2\u010a\u010b\7_\2\2\u010b\u010c\7f\2\2\u010c\u010d\7q\2\2\u010d\u010e"+
		"\7]\2\2\u010eP\3\2\2\2\u010f\u0110\7u\2\2\u0110\u0111\7y\2\2\u0111\u0112"+
		"\7k\2\2\u0112\u0113\7v\2\2\u0113\u0114\7e\2\2\u0114\u0115\7j\2\2\u0115"+
		"\u0116\7]\2\2\u0116R\3\2\2\2\u0117\u0118\7f\2\2\u0118\u0119\7g\2\2\u0119"+
		"\u011a\7h\2\2\u011a\u011b\7k\2\2\u011b\u011c\7p\2\2\u011c\u011d\7g\2\2"+
		"\u011d\u011e\7]\2\2\u011eT\3\2\2\2\u011f\u0120\7_\2\2\u0120\u0121\7d\2"+
		"\2\u0121\u0122\7q\2\2\u0122\u0123\7f\2\2\u0123\u0124\7{\2\2\u0124\u0125"+
		"\7]\2\2\u0125V\3\2\2\2\u0126\u0127\7o\2\2\u0127\u0128\7q\2\2\u0128\u0129"+
		"\7f\2\2\u0129\u012a\7w\2\2\u012a\u012b\7n\2\2\u012b\u012c\7g\2\2\u012c"+
		"\u012d\7]\2\2\u012dX\3\2\2\2\u012e\u012f\7v\2\2\u012f\u0130\7t\2\2\u0130"+
		"\u0131\7{\2\2\u0131\u0132\7]\2\2\u0132Z\3\2\2\2\u0133\u0134\7_\2\2\u0134"+
		"\u0135\7e\2\2\u0135\u0136\7c\2\2\u0136\u0137\7v\2\2\u0137\u0138\7e\2\2"+
		"\u0138\u0139\7j\2\2\u0139\u013a\7]\2\2\u013a\\\3\2\2\2\u013b\u013c\7^"+
		"\2\2\u013c\u013d\7)\2\2\u013d^\3\2\2\2\u013e\u013f\7\61\2\2\u013f\u0140"+
		"\7,\2\2\u0140\u0144\3\2\2\2\u0141\u0143\13\2\2\2\u0142\u0141\3\2\2\2\u0143"+
		"\u0146\3\2\2\2\u0144\u0145\3\2\2\2\u0144\u0142\3\2\2\2\u0145\u0147\3\2"+
		"\2\2\u0146\u0144\3\2\2\2\u0147\u0148\7,\2\2\u0148\u0149\7\61\2\2\u0149"+
		"\u014a\3\2\2\2\u014a\u014b\b\60\2\2\u014b`\3\2\2\2\u014c\u014d\7\61\2"+
		"\2\u014d\u014e\7\61\2\2\u014e\u0152\3\2\2\2\u014f\u0151\n\5\2\2\u0150"+
		"\u014f\3\2\2\2\u0151\u0154\3\2\2\2\u0152\u0150\3\2\2\2\u0152\u0153\3\2"+
		"\2\2\u0153\u0155\3\2\2\2\u0154\u0152\3\2\2\2\u0155\u0156\b\61\2\2\u0156"+
		"b\3\2\2\2\u0157\u0159\t\6\2\2\u0158\u0157\3\2\2\2\u0159\u015a\3\2\2\2"+
		"\u015a\u0158\3\2\2\2\u015a\u015b\3\2\2\2\u015b\u015c\3\2\2\2\u015c\u015d"+
		"\b\62\2\2\u015dd\3\2\2\2\u015e\u015f\7@\2\2\u015f\u0160\7@\2\2\u0160\u0164"+
		"\3\2\2\2\u0161\u0163\n\5\2\2\u0162\u0161\3\2\2\2\u0163\u0166\3\2\2\2\u0164"+
		"\u0162\3\2\2\2\u0164\u0165\3\2\2\2\u0165f\3\2\2\2\u0166\u0164\3\2\2\2"+
		"\21\2|\177\u0084\u008b\u008d\u0093\u0098\u00a8\u00c0\u00c2\u0144\u0152"+
		"\u015a\u0164\3\b\2\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}