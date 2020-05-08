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
		TryStatement=44, CatchStatement=45, StatementConnector=46, COMMENT=47, 
		LINE_COMMENT=48, WS2=49, FDOC=50;
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
			"StatementConnector", "ESC", "COMMENT", "LINE_COMMENT", "WS2", "FDOC"
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
			"'try['", "']catch['", "']['"
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
			"StatementConnector", "COMMENT", "LINE_COMMENT", "WS2", "FDOC"
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
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2\64\u016c\b\1\4\2"+
		"\t\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4"+
		"\13\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22"+
		"\t\22\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31"+
		"\t\31\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4 \t"+
		" \4!\t!\4\"\t\"\4#\t#\4$\t$\4%\t%\4&\t&\4\'\t\'\4(\t(\4)\t)\4*\t*\4+\t"+
		"+\4,\t,\4-\t-\4.\t.\4/\t/\4\60\t\60\4\61\t\61\4\62\t\62\4\63\t\63\4\64"+
		"\t\64\3\2\3\2\3\3\3\3\3\4\3\4\3\5\3\5\3\6\3\6\3\6\3\7\3\7\3\7\3\b\3\b"+
		"\3\t\3\t\3\n\6\n}\n\n\r\n\16\n~\3\n\5\n\u0082\n\n\3\n\7\n\u0085\n\n\f"+
		"\n\16\n\u0088\13\n\3\n\3\n\6\n\u008c\n\n\r\n\16\n\u008d\5\n\u0090\n\n"+
		"\3\13\3\13\7\13\u0094\n\13\f\13\16\13\u0097\13\13\3\f\3\f\5\f\u009b\n"+
		"\f\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3\r\5\r\u00ab\n"+
		"\r\3\16\3\16\3\16\3\17\3\17\3\17\3\17\3\17\3\20\3\20\3\20\3\20\3\20\3"+
		"\20\3\21\3\21\3\21\3\21\3\21\3\22\3\22\3\22\7\22\u00c3\n\22\f\22\16\22"+
		"\u00c6\13\22\3\22\3\22\3\23\3\23\3\24\3\24\3\25\3\25\3\25\3\26\3\26\3"+
		"\27\3\27\3\27\3\30\3\30\3\31\3\31\3\32\3\32\3\33\3\33\3\33\3\34\3\34\3"+
		"\34\3\35\3\35\3\35\3\36\3\36\3\36\3\37\3\37\3\37\3 \3 \3 \3!\3!\3\"\3"+
		"\"\3#\3#\3$\3$\3$\3$\3%\3%\3%\3%\3%\3%\3%\3&\3&\3&\3&\3&\3&\3&\3\'\3\'"+
		"\3\'\3\'\3\'\3\'\3\'\3(\3(\3(\3(\3(\3)\3)\3)\3)\3)\3)\3)\3)\3*\3*\3*\3"+
		"*\3*\3*\3*\3*\3+\3+\3+\3+\3+\3+\3+\3,\3,\3,\3,\3,\3,\3,\3,\3-\3-\3-\3"+
		"-\3-\3.\3.\3.\3.\3.\3.\3.\3.\3/\3/\3/\3\60\3\60\3\60\3\61\3\61\3\61\3"+
		"\61\7\61\u0148\n\61\f\61\16\61\u014b\13\61\3\61\3\61\3\61\3\61\3\61\3"+
		"\62\3\62\3\62\3\62\7\62\u0156\n\62\f\62\16\62\u0159\13\62\3\62\3\62\3"+
		"\63\6\63\u015e\n\63\r\63\16\63\u015f\3\63\3\63\3\64\3\64\3\64\3\64\7\64"+
		"\u0168\n\64\f\64\16\64\u016b\13\64\4\u00c4\u0149\2\65\3\3\5\4\7\5\t\6"+
		"\13\7\r\b\17\t\21\n\23\13\25\f\27\r\31\16\33\17\35\20\37\21!\22#\23%\24"+
		"\'\25)\26+\27-\30/\31\61\32\63\33\65\34\67\359\36;\37= ?!A\"C#E$G%I&K"+
		"\'M(O)Q*S+U,W-Y.[/]\60_\2a\61c\62e\63g\64\3\2\7\3\2\62;\6\2%&C\\aac|\b"+
		"\2%&\60\60\62;C\\aac|\4\2\f\f\17\17\5\2\13\f\17\17\"\"\2\u017d\2\3\3\2"+
		"\2\2\2\5\3\2\2\2\2\7\3\2\2\2\2\t\3\2\2\2\2\13\3\2\2\2\2\r\3\2\2\2\2\17"+
		"\3\2\2\2\2\21\3\2\2\2\2\23\3\2\2\2\2\25\3\2\2\2\2\27\3\2\2\2\2\31\3\2"+
		"\2\2\2\33\3\2\2\2\2\35\3\2\2\2\2\37\3\2\2\2\2!\3\2\2\2\2#\3\2\2\2\2%\3"+
		"\2\2\2\2\'\3\2\2\2\2)\3\2\2\2\2+\3\2\2\2\2-\3\2\2\2\2/\3\2\2\2\2\61\3"+
		"\2\2\2\2\63\3\2\2\2\2\65\3\2\2\2\2\67\3\2\2\2\29\3\2\2\2\2;\3\2\2\2\2"+
		"=\3\2\2\2\2?\3\2\2\2\2A\3\2\2\2\2C\3\2\2\2\2E\3\2\2\2\2G\3\2\2\2\2I\3"+
		"\2\2\2\2K\3\2\2\2\2M\3\2\2\2\2O\3\2\2\2\2Q\3\2\2\2\2S\3\2\2\2\2U\3\2\2"+
		"\2\2W\3\2\2\2\2Y\3\2\2\2\2[\3\2\2\2\2]\3\2\2\2\2a\3\2\2\2\2c\3\2\2\2\2"+
		"e\3\2\2\2\2g\3\2\2\2\3i\3\2\2\2\5k\3\2\2\2\7m\3\2\2\2\to\3\2\2\2\13q\3"+
		"\2\2\2\rt\3\2\2\2\17w\3\2\2\2\21y\3\2\2\2\23\u008f\3\2\2\2\25\u0091\3"+
		"\2\2\2\27\u009a\3\2\2\2\31\u00aa\3\2\2\2\33\u00ac\3\2\2\2\35\u00af\3\2"+
		"\2\2\37\u00b4\3\2\2\2!\u00ba\3\2\2\2#\u00bf\3\2\2\2%\u00c9\3\2\2\2\'\u00cb"+
		"\3\2\2\2)\u00cd\3\2\2\2+\u00d0\3\2\2\2-\u00d2\3\2\2\2/\u00d5\3\2\2\2\61"+
		"\u00d7\3\2\2\2\63\u00d9\3\2\2\2\65\u00db\3\2\2\2\67\u00de\3\2\2\29\u00e1"+
		"\3\2\2\2;\u00e4\3\2\2\2=\u00e7\3\2\2\2?\u00ea\3\2\2\2A\u00ed\3\2\2\2C"+
		"\u00ef\3\2\2\2E\u00f1\3\2\2\2G\u00f3\3\2\2\2I\u00f7\3\2\2\2K\u00fe\3\2"+
		"\2\2M\u0105\3\2\2\2O\u010c\3\2\2\2Q\u0111\3\2\2\2S\u0119\3\2\2\2U\u0121"+
		"\3\2\2\2W\u0128\3\2\2\2Y\u0130\3\2\2\2[\u0135\3\2\2\2]\u013d\3\2\2\2_"+
		"\u0140\3\2\2\2a\u0143\3\2\2\2c\u0151\3\2\2\2e\u015d\3\2\2\2g\u0163\3\2"+
		"\2\2ij\7.\2\2j\4\3\2\2\2kl\7+\2\2l\6\3\2\2\2mn\7#\2\2n\b\3\2\2\2op\7`"+
		"\2\2p\n\3\2\2\2qr\7?\2\2rs\7>\2\2s\f\3\2\2\2tu\7?\2\2uv\7@\2\2v\16\3\2"+
		"\2\2wx\7*\2\2x\20\3\2\2\2yz\7=\2\2z\22\3\2\2\2{}\t\2\2\2|{\3\2\2\2}~\3"+
		"\2\2\2~|\3\2\2\2~\177\3\2\2\2\177\u0081\3\2\2\2\u0080\u0082\7\60\2\2\u0081"+
		"\u0080\3\2\2\2\u0081\u0082\3\2\2\2\u0082\u0086\3\2\2\2\u0083\u0085\t\2"+
		"\2\2\u0084\u0083\3\2\2\2\u0085\u0088\3\2\2\2\u0086\u0084\3\2\2\2\u0086"+
		"\u0087\3\2\2\2\u0087\u0090\3\2\2\2\u0088\u0086\3\2\2\2\u0089\u008b\7\60"+
		"\2\2\u008a\u008c\t\2\2\2\u008b\u008a\3\2\2\2\u008c\u008d\3\2\2\2\u008d"+
		"\u008b\3\2\2\2\u008d\u008e\3\2\2\2\u008e\u0090\3\2\2\2\u008f|\3\2\2\2"+
		"\u008f\u0089\3\2\2\2\u0090\24\3\2\2\2\u0091\u0095\t\3\2\2\u0092\u0094"+
		"\t\4\2\2\u0093\u0092\3\2\2\2\u0094\u0097\3\2\2\2\u0095\u0093\3\2\2\2\u0095"+
		"\u0096\3\2\2\2\u0096\26\3\2\2\2\u0097\u0095\3\2\2\2\u0098\u009b\5\35\17"+
		"\2\u0099\u009b\5\37\20\2\u009a\u0098\3\2\2\2\u009a\u0099\3\2\2\2\u009b"+
		"\30\3\2\2\2\u009c\u009d\7<\2\2\u009d\u00ab\7?\2\2\u009e\u009f\7-\2\2\u009f"+
		"\u00ab\7?\2\2\u00a0\u00a1\7/\2\2\u00a1\u00ab\7?\2\2\u00a2\u00a3\7,\2\2"+
		"\u00a3\u00ab\7?\2\2\u00a4\u00a5\7\61\2\2\u00a5\u00ab\7?\2\2\u00a6\u00a7"+
		"\7\'\2\2\u00a7\u00ab\7?\2\2\u00a8\u00a9\7`\2\2\u00a9\u00ab\7?\2\2\u00aa"+
		"\u009c\3\2\2\2\u00aa\u009e\3\2\2\2\u00aa\u00a0\3\2\2\2\u00aa\u00a2\3\2"+
		"\2\2\u00aa\u00a4\3\2\2\2\u00aa\u00a6\3\2\2\2\u00aa\u00a8\3\2\2\2\u00ab"+
		"\32\3\2\2\2\u00ac\u00ad\5\25\13\2\u00ad\u00ae\7*\2\2\u00ae\34\3\2\2\2"+
		"\u00af\u00b0\7v\2\2\u00b0\u00b1\7t\2\2\u00b1\u00b2\7w\2\2\u00b2\u00b3"+
		"\7g\2\2\u00b3\36\3\2\2\2\u00b4\u00b5\7h\2\2\u00b5\u00b6\7c\2\2\u00b6\u00b7"+
		"\7n\2\2\u00b7\u00b8\7u\2\2\u00b8\u00b9\7g\2\2\u00b9 \3\2\2\2\u00ba\u00bb"+
		"\7p\2\2\u00bb\u00bc\7w\2\2\u00bc\u00bd\7n\2\2\u00bd\u00be\7n\2\2\u00be"+
		"\"\3\2\2\2\u00bf\u00c4\7)\2\2\u00c0\u00c3\5_\60\2\u00c1\u00c3\13\2\2\2"+
		"\u00c2\u00c0\3\2\2\2\u00c2\u00c1\3\2\2\2\u00c3\u00c6\3\2\2\2\u00c4\u00c5"+
		"\3\2\2\2\u00c4\u00c2\3\2\2\2\u00c5\u00c7\3\2\2\2\u00c6\u00c4\3\2\2\2\u00c7"+
		"\u00c8\7)\2\2\u00c8$\3\2\2\2\u00c9\u00ca\7,\2\2\u00ca&\3\2\2\2\u00cb\u00cc"+
		"\7\61\2\2\u00cc(\3\2\2\2\u00cd\u00ce\7-\2\2\u00ce\u00cf\7-\2\2\u00cf*"+
		"\3\2\2\2\u00d0\u00d1\7-\2\2\u00d1,\3\2\2\2\u00d2\u00d3\7/\2\2\u00d3\u00d4"+
		"\7/\2\2\u00d4.\3\2\2\2\u00d5\u00d6\7/\2\2\u00d6\60\3\2\2\2\u00d7\u00d8"+
		"\7>\2\2\u00d8\62\3\2\2\2\u00d9\u00da\7@\2\2\u00da\64\3\2\2\2\u00db\u00dc"+
		"\7>\2\2\u00dc\u00dd\7?\2\2\u00dd\66\3\2\2\2\u00de\u00df\7@\2\2\u00df\u00e0"+
		"\7?\2\2\u00e08\3\2\2\2\u00e1\u00e2\7?\2\2\u00e2\u00e3\7?\2\2\u00e3:\3"+
		"\2\2\2\u00e4\u00e5\7#\2\2\u00e5\u00e6\7?\2\2\u00e6<\3\2\2\2\u00e7\u00e8"+
		"\7(\2\2\u00e8\u00e9\7(\2\2\u00e9>\3\2\2\2\u00ea\u00eb\7~\2\2\u00eb\u00ec"+
		"\7~\2\2\u00ec@\3\2\2\2\u00ed\u00ee\7b\2\2\u00eeB\3\2\2\2\u00ef\u00f0\7"+
		"\'\2\2\u00f0D\3\2\2\2\u00f1\u00f2\7_\2\2\u00f2F\3\2\2\2\u00f3\u00f4\7"+
		"k\2\2\u00f4\u00f5\7h\2\2\u00f5\u00f6\7]\2\2\u00f6H\3\2\2\2\u00f7\u00f8"+
		"\7_\2\2\u00f8\u00f9\7v\2\2\u00f9\u00fa\7j\2\2\u00fa\u00fb\7g\2\2\u00fb"+
		"\u00fc\7p\2\2\u00fc\u00fd\7]\2\2\u00fdJ\3\2\2\2\u00fe\u00ff\7_\2\2\u00ff"+
		"\u0100\7g\2\2\u0100\u0101\7n\2\2\u0101\u0102\7u\2\2\u0102\u0103\7g\2\2"+
		"\u0103\u0104\7]\2\2\u0104L\3\2\2\2\u0105\u0106\7y\2\2\u0106\u0107\7j\2"+
		"\2\u0107\u0108\7k\2\2\u0108\u0109\7n\2\2\u0109\u010a\7g\2\2\u010a\u010b"+
		"\7]\2\2\u010bN\3\2\2\2\u010c\u010d\7_\2\2\u010d\u010e\7f\2\2\u010e\u010f"+
		"\7q\2\2\u010f\u0110\7]\2\2\u0110P\3\2\2\2\u0111\u0112\7u\2\2\u0112\u0113"+
		"\7y\2\2\u0113\u0114\7k\2\2\u0114\u0115\7v\2\2\u0115\u0116\7e\2\2\u0116"+
		"\u0117\7j\2\2\u0117\u0118\7]\2\2\u0118R\3\2\2\2\u0119\u011a\7f\2\2\u011a"+
		"\u011b\7g\2\2\u011b\u011c\7h\2\2\u011c\u011d\7k\2\2\u011d\u011e\7p\2\2"+
		"\u011e\u011f\7g\2\2\u011f\u0120\7]\2\2\u0120T\3\2\2\2\u0121\u0122\7_\2"+
		"\2\u0122\u0123\7d\2\2\u0123\u0124\7q\2\2\u0124\u0125\7f\2\2\u0125\u0126"+
		"\7{\2\2\u0126\u0127\7]\2\2\u0127V\3\2\2\2\u0128\u0129\7o\2\2\u0129\u012a"+
		"\7q\2\2\u012a\u012b\7f\2\2\u012b\u012c\7w\2\2\u012c\u012d\7n\2\2\u012d"+
		"\u012e\7g\2\2\u012e\u012f\7]\2\2\u012fX\3\2\2\2\u0130\u0131\7v\2\2\u0131"+
		"\u0132\7t\2\2\u0132\u0133\7{\2\2\u0133\u0134\7]\2\2\u0134Z\3\2\2\2\u0135"+
		"\u0136\7_\2\2\u0136\u0137\7e\2\2\u0137\u0138\7c\2\2\u0138\u0139\7v\2\2"+
		"\u0139\u013a\7e\2\2\u013a\u013b\7j\2\2\u013b\u013c\7]\2\2\u013c\\\3\2"+
		"\2\2\u013d\u013e\7_\2\2\u013e\u013f\7]\2\2\u013f^\3\2\2\2\u0140\u0141"+
		"\7^\2\2\u0141\u0142\7)\2\2\u0142`\3\2\2\2\u0143\u0144\7\61\2\2\u0144\u0145"+
		"\7,\2\2\u0145\u0149\3\2\2\2\u0146\u0148\13\2\2\2\u0147\u0146\3\2\2\2\u0148"+
		"\u014b\3\2\2\2\u0149\u014a\3\2\2\2\u0149\u0147\3\2\2\2\u014a\u014c\3\2"+
		"\2\2\u014b\u0149\3\2\2\2\u014c\u014d\7,\2\2\u014d\u014e\7\61\2\2\u014e"+
		"\u014f\3\2\2\2\u014f\u0150\b\61\2\2\u0150b\3\2\2\2\u0151\u0152\7\61\2"+
		"\2\u0152\u0153\7\61\2\2\u0153\u0157\3\2\2\2\u0154\u0156\n\5\2\2\u0155"+
		"\u0154\3\2\2\2\u0156\u0159\3\2\2\2\u0157\u0155\3\2\2\2\u0157\u0158\3\2"+
		"\2\2\u0158\u015a\3\2\2\2\u0159\u0157\3\2\2\2\u015a\u015b\b\62\2\2\u015b"+
		"d\3\2\2\2\u015c\u015e\t\6\2\2\u015d\u015c\3\2\2\2\u015e\u015f\3\2\2\2"+
		"\u015f\u015d\3\2\2\2\u015f\u0160\3\2\2\2\u0160\u0161\3\2\2\2\u0161\u0162"+
		"\b\63\2\2\u0162f\3\2\2\2\u0163\u0164\7@\2\2\u0164\u0165\7@\2\2\u0165\u0169"+
		"\3\2\2\2\u0166\u0168\n\5\2\2\u0167\u0166\3\2\2\2\u0168\u016b\3\2\2\2\u0169"+
		"\u0167\3\2\2\2\u0169\u016a\3\2\2\2\u016ah\3\2\2\2\u016b\u0169\3\2\2\2"+
		"\21\2~\u0081\u0086\u008d\u008f\u0095\u009a\u00aa\u00c2\u00c4\u0149\u0157"+
		"\u015f\u0169\3\b\2\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}