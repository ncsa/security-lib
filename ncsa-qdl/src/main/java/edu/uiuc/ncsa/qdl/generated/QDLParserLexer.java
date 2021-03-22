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
		T__0=1, T__1=2, T__2=3, T__3=4, T__4=5, T__5=6, T__6=7, T__7=8, T__8=9, 
		T__9=10, Integer=11, Identifier=12, Bool=13, ASSIGN=14, FuncStart=15, 
		F_REF=16, BOOL_TRUE=17, BOOL_FALSE=18, Null=19, STRING=20, Decimal=21, 
		LambdaConnector=22, Times=23, Divide=24, PlusPlus=25, Plus=26, MinusMinus=27, 
		Minus=28, LessThan=29, GreaterThan=30, SingleEqual=31, LessEquals=32, 
		MoreEquals=33, Equals=34, NotEquals=35, LogicalNot=36, Exponentiation=37, 
		And=38, Or=39, Backtick=40, Percent=41, Tilde=42, TildeRight=43, LeftBracket=44, 
		RightBracket=45, LogicalIf=46, LogicalThen=47, LogicalElse=48, WhileLoop=49, 
		WhileDo=50, SwitchStatement=51, DefineStatement=52, BodyStatement=53, 
		ModuleStatement=54, TryStatement=55, CatchStatement=56, StatementConnector=57, 
		COMMENT=58, LINE_COMMENT=59, WS2=60, FDOC=61;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	private static String[] makeRuleNames() {
		return new String[] {
			"T__0", "T__1", "T__2", "T__3", "T__4", "T__5", "T__6", "T__7", "T__8", 
			"T__9", "Integer", "Identifier", "Bool", "ASSIGN", "FuncStart", "F_REF", 
			"BOOL_TRUE", "BOOL_FALSE", "Null", "STRING", "Decimal", "AllOps", "LambdaConnector", 
			"Times", "Divide", "PlusPlus", "Plus", "MinusMinus", "Minus", "LessThan", 
			"GreaterThan", "SingleEqual", "LessEquals", "MoreEquals", "Equals", "NotEquals", 
			"LogicalNot", "Exponentiation", "And", "Or", "Backtick", "Percent", "Tilde", 
			"TildeRight", "LeftBracket", "RightBracket", "LogicalIf", "LogicalThen", 
			"LogicalElse", "WhileLoop", "WhileDo", "SwitchStatement", "DefineStatement", 
			"BodyStatement", "ModuleStatement", "TryStatement", "CatchStatement", 
			"StatementConnector", "ESC", "COMMENT", "LINE_COMMENT", "WS2", "FDOC"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'{'", "','", "'}'", "':'", "')'", "'.'", "'=<'", "'=>'", "'('", 
			"';'", null, null, null, null, null, null, "'true'", "'false'", "'null'", 
			null, null, "'->'", "'*'", "'/'", "'++'", "'+'", "'--'", "'-'", "'<'", 
			"'>'", "'='", "'<='", "'>='", "'=='", "'!='", "'!'", "'^'", "'&&'", "'||'", 
			"'`'", "'%'", "'~'", "'~|'", "']'", "'['", "'if['", "']then['", "']else['", 
			"'while['", "']do['", "'switch['", "'define['", "']body['", "'module['", 
			"'try['", "']catch['", "']['"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, null, null, null, null, null, null, null, null, null, null, "Integer", 
			"Identifier", "Bool", "ASSIGN", "FuncStart", "F_REF", "BOOL_TRUE", "BOOL_FALSE", 
			"Null", "STRING", "Decimal", "LambdaConnector", "Times", "Divide", "PlusPlus", 
			"Plus", "MinusMinus", "Minus", "LessThan", "GreaterThan", "SingleEqual", 
			"LessEquals", "MoreEquals", "Equals", "NotEquals", "LogicalNot", "Exponentiation", 
			"And", "Or", "Backtick", "Percent", "Tilde", "TildeRight", "LeftBracket", 
			"RightBracket", "LogicalIf", "LogicalThen", "LogicalElse", "WhileLoop", 
			"WhileDo", "SwitchStatement", "DefineStatement", "BodyStatement", "ModuleStatement", 
			"TryStatement", "CatchStatement", "StatementConnector", "COMMENT", "LINE_COMMENT", 
			"WS2", "FDOC"
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
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2?\u01af\b\1\4\2\t"+
		"\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13"+
		"\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31\t\31"+
		"\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4 \t \4!"+
		"\t!\4\"\t\"\4#\t#\4$\t$\4%\t%\4&\t&\4\'\t\'\4(\t(\4)\t)\4*\t*\4+\t+\4"+
		",\t,\4-\t-\4.\t.\4/\t/\4\60\t\60\4\61\t\61\4\62\t\62\4\63\t\63\4\64\t"+
		"\64\4\65\t\65\4\66\t\66\4\67\t\67\48\t8\49\t9\4:\t:\4;\t;\4<\t<\4=\t="+
		"\4>\t>\4?\t?\4@\t@\3\2\3\2\3\3\3\3\3\4\3\4\3\5\3\5\3\6\3\6\3\7\3\7\3\b"+
		"\3\b\3\b\3\t\3\t\3\t\3\n\3\n\3\13\3\13\3\f\6\f\u0099\n\f\r\f\16\f\u009a"+
		"\3\r\3\r\7\r\u009f\n\r\f\r\16\r\u00a2\13\r\3\16\3\16\5\16\u00a6\n\16\3"+
		"\17\3\17\3\17\3\17\3\17\3\17\3\17\3\17\3\17\3\17\3\17\3\17\3\17\3\17\5"+
		"\17\u00b6\n\17\3\20\3\20\7\20\u00ba\n\20\f\20\16\20\u00bd\13\20\3\20\3"+
		"\20\3\21\3\21\3\21\3\21\3\21\5\21\u00c6\n\21\3\22\3\22\3\22\3\22\3\22"+
		"\3\23\3\23\3\23\3\23\3\23\3\23\3\24\3\24\3\24\3\24\3\24\3\25\3\25\3\25"+
		"\7\25\u00db\n\25\f\25\16\25\u00de\13\25\3\25\3\25\3\26\3\26\3\26\3\26"+
		"\3\26\3\26\5\26\u00e8\n\26\3\27\3\27\3\27\3\27\3\27\3\27\3\27\3\27\3\27"+
		"\3\27\3\27\3\27\3\27\3\27\3\27\3\27\3\27\5\27\u00fb\n\27\3\30\3\30\3\30"+
		"\3\31\3\31\3\32\3\32\3\33\3\33\3\33\3\34\3\34\3\35\3\35\3\35\3\36\3\36"+
		"\3\37\3\37\3 \3 \3!\3!\3\"\3\"\3\"\3#\3#\3#\3$\3$\3$\3%\3%\3%\3&\3&\3"+
		"\'\3\'\3(\3(\3(\3)\3)\3)\3*\3*\3+\3+\3,\3,\3-\3-\3-\3.\3.\3/\3/\3\60\3"+
		"\60\3\60\3\60\3\61\3\61\3\61\3\61\3\61\3\61\3\61\3\62\3\62\3\62\3\62\3"+
		"\62\3\62\3\62\3\63\3\63\3\63\3\63\3\63\3\63\3\63\3\64\3\64\3\64\3\64\3"+
		"\64\3\65\3\65\3\65\3\65\3\65\3\65\3\65\3\65\3\66\3\66\3\66\3\66\3\66\3"+
		"\66\3\66\3\66\3\67\3\67\3\67\3\67\3\67\3\67\3\67\38\38\38\38\38\38\38"+
		"\38\39\39\39\39\39\3:\3:\3:\3:\3:\3:\3:\3:\3;\3;\3;\3<\3<\3<\3=\3=\3="+
		"\3=\7=\u018b\n=\f=\16=\u018e\13=\3=\3=\3=\3=\3=\3>\3>\3>\3>\7>\u0199\n"+
		">\f>\16>\u019c\13>\3>\3>\3?\6?\u01a1\n?\r?\16?\u01a2\3?\3?\3@\3@\3@\3"+
		"@\7@\u01ab\n@\f@\16@\u01ae\13@\4\u00dc\u018c\2A\3\3\5\4\7\5\t\6\13\7\r"+
		"\b\17\t\21\n\23\13\25\f\27\r\31\16\33\17\35\20\37\21!\22#\23%\24\'\25"+
		")\26+\27-\2/\30\61\31\63\32\65\33\67\349\35;\36=\37? A!C\"E#G$I%K&M\'"+
		"O(Q)S*U+W,Y-[.]/_\60a\61c\62e\63g\64i\65k\66m\67o8q9s:u;w\2y<{=}>\177"+
		"?\3\2\b\3\2\62;\6\2%&C\\aac|\b\2%&\60\60\62;C\\aac|\7\2%&\62;C\\aac|\4"+
		"\2\f\f\17\17\5\2\13\f\17\17\"\"\2\u01ce\2\3\3\2\2\2\2\5\3\2\2\2\2\7\3"+
		"\2\2\2\2\t\3\2\2\2\2\13\3\2\2\2\2\r\3\2\2\2\2\17\3\2\2\2\2\21\3\2\2\2"+
		"\2\23\3\2\2\2\2\25\3\2\2\2\2\27\3\2\2\2\2\31\3\2\2\2\2\33\3\2\2\2\2\35"+
		"\3\2\2\2\2\37\3\2\2\2\2!\3\2\2\2\2#\3\2\2\2\2%\3\2\2\2\2\'\3\2\2\2\2)"+
		"\3\2\2\2\2+\3\2\2\2\2/\3\2\2\2\2\61\3\2\2\2\2\63\3\2\2\2\2\65\3\2\2\2"+
		"\2\67\3\2\2\2\29\3\2\2\2\2;\3\2\2\2\2=\3\2\2\2\2?\3\2\2\2\2A\3\2\2\2\2"+
		"C\3\2\2\2\2E\3\2\2\2\2G\3\2\2\2\2I\3\2\2\2\2K\3\2\2\2\2M\3\2\2\2\2O\3"+
		"\2\2\2\2Q\3\2\2\2\2S\3\2\2\2\2U\3\2\2\2\2W\3\2\2\2\2Y\3\2\2\2\2[\3\2\2"+
		"\2\2]\3\2\2\2\2_\3\2\2\2\2a\3\2\2\2\2c\3\2\2\2\2e\3\2\2\2\2g\3\2\2\2\2"+
		"i\3\2\2\2\2k\3\2\2\2\2m\3\2\2\2\2o\3\2\2\2\2q\3\2\2\2\2s\3\2\2\2\2u\3"+
		"\2\2\2\2y\3\2\2\2\2{\3\2\2\2\2}\3\2\2\2\2\177\3\2\2\2\3\u0081\3\2\2\2"+
		"\5\u0083\3\2\2\2\7\u0085\3\2\2\2\t\u0087\3\2\2\2\13\u0089\3\2\2\2\r\u008b"+
		"\3\2\2\2\17\u008d\3\2\2\2\21\u0090\3\2\2\2\23\u0093\3\2\2\2\25\u0095\3"+
		"\2\2\2\27\u0098\3\2\2\2\31\u009c\3\2\2\2\33\u00a5\3\2\2\2\35\u00b5\3\2"+
		"\2\2\37\u00b7\3\2\2\2!\u00c0\3\2\2\2#\u00c7\3\2\2\2%\u00cc\3\2\2\2\'\u00d2"+
		"\3\2\2\2)\u00d7\3\2\2\2+\u00e7\3\2\2\2-\u00fa\3\2\2\2/\u00fc\3\2\2\2\61"+
		"\u00ff\3\2\2\2\63\u0101\3\2\2\2\65\u0103\3\2\2\2\67\u0106\3\2\2\29\u0108"+
		"\3\2\2\2;\u010b\3\2\2\2=\u010d\3\2\2\2?\u010f\3\2\2\2A\u0111\3\2\2\2C"+
		"\u0113\3\2\2\2E\u0116\3\2\2\2G\u0119\3\2\2\2I\u011c\3\2\2\2K\u011f\3\2"+
		"\2\2M\u0121\3\2\2\2O\u0123\3\2\2\2Q\u0126\3\2\2\2S\u0129\3\2\2\2U\u012b"+
		"\3\2\2\2W\u012d\3\2\2\2Y\u012f\3\2\2\2[\u0132\3\2\2\2]\u0134\3\2\2\2_"+
		"\u0136\3\2\2\2a\u013a\3\2\2\2c\u0141\3\2\2\2e\u0148\3\2\2\2g\u014f\3\2"+
		"\2\2i\u0154\3\2\2\2k\u015c\3\2\2\2m\u0164\3\2\2\2o\u016b\3\2\2\2q\u0173"+
		"\3\2\2\2s\u0178\3\2\2\2u\u0180\3\2\2\2w\u0183\3\2\2\2y\u0186\3\2\2\2{"+
		"\u0194\3\2\2\2}\u01a0\3\2\2\2\177\u01a6\3\2\2\2\u0081\u0082\7}\2\2\u0082"+
		"\4\3\2\2\2\u0083\u0084\7.\2\2\u0084\6\3\2\2\2\u0085\u0086\7\177\2\2\u0086"+
		"\b\3\2\2\2\u0087\u0088\7<\2\2\u0088\n\3\2\2\2\u0089\u008a\7+\2\2\u008a"+
		"\f\3\2\2\2\u008b\u008c\7\60\2\2\u008c\16\3\2\2\2\u008d\u008e\7?\2\2\u008e"+
		"\u008f\7>\2\2\u008f\20\3\2\2\2\u0090\u0091\7?\2\2\u0091\u0092\7@\2\2\u0092"+
		"\22\3\2\2\2\u0093\u0094\7*\2\2\u0094\24\3\2\2\2\u0095\u0096\7=\2\2\u0096"+
		"\26\3\2\2\2\u0097\u0099\t\2\2\2\u0098\u0097\3\2\2\2\u0099\u009a\3\2\2"+
		"\2\u009a\u0098\3\2\2\2\u009a\u009b\3\2\2\2\u009b\30\3\2\2\2\u009c\u00a0"+
		"\t\3\2\2\u009d\u009f\t\4\2\2\u009e\u009d\3\2\2\2\u009f\u00a2\3\2\2\2\u00a0"+
		"\u009e\3\2\2\2\u00a0\u00a1\3\2\2\2\u00a1\32\3\2\2\2\u00a2\u00a0\3\2\2"+
		"\2\u00a3\u00a6\5#\22\2\u00a4\u00a6\5%\23\2\u00a5\u00a3\3\2\2\2\u00a5\u00a4"+
		"\3\2\2\2\u00a6\34\3\2\2\2\u00a7\u00a8\7<\2\2\u00a8\u00b6\7?\2\2\u00a9"+
		"\u00aa\7-\2\2\u00aa\u00b6\7?\2\2\u00ab\u00ac\7/\2\2\u00ac\u00b6\7?\2\2"+
		"\u00ad\u00ae\7,\2\2\u00ae\u00b6\7?\2\2\u00af\u00b0\7\61\2\2\u00b0\u00b6"+
		"\7?\2\2\u00b1\u00b2\7\'\2\2\u00b2\u00b6\7?\2\2\u00b3\u00b4\7`\2\2\u00b4"+
		"\u00b6\7?\2\2\u00b5\u00a7\3\2\2\2\u00b5\u00a9\3\2\2\2\u00b5\u00ab\3\2"+
		"\2\2\u00b5\u00ad\3\2\2\2\u00b5\u00af\3\2\2\2\u00b5\u00b1\3\2\2\2\u00b5"+
		"\u00b3\3\2\2\2\u00b6\36\3\2\2\2\u00b7\u00bb\t\3\2\2\u00b8\u00ba\t\5\2"+
		"\2\u00b9\u00b8\3\2\2\2\u00ba\u00bd\3\2\2\2\u00bb\u00b9\3\2\2\2\u00bb\u00bc"+
		"\3\2\2\2\u00bc\u00be\3\2\2\2\u00bd\u00bb\3\2\2\2\u00be\u00bf\7*\2\2\u00bf"+
		" \3\2\2\2\u00c0\u00c5\7,\2\2\u00c1\u00c6\5-\27\2\u00c2\u00c3\5\37\20\2"+
		"\u00c3\u00c4\7+\2\2\u00c4\u00c6\3\2\2\2\u00c5\u00c1\3\2\2\2\u00c5\u00c2"+
		"\3\2\2\2\u00c6\"\3\2\2\2\u00c7\u00c8\7v\2\2\u00c8\u00c9\7t\2\2\u00c9\u00ca"+
		"\7w\2\2\u00ca\u00cb\7g\2\2\u00cb$\3\2\2\2\u00cc\u00cd\7h\2\2\u00cd\u00ce"+
		"\7c\2\2\u00ce\u00cf\7n\2\2\u00cf\u00d0\7u\2\2\u00d0\u00d1\7g\2\2\u00d1"+
		"&\3\2\2\2\u00d2\u00d3\7p\2\2\u00d3\u00d4\7w\2\2\u00d4\u00d5\7n\2\2\u00d5"+
		"\u00d6\7n\2\2\u00d6(\3\2\2\2\u00d7\u00dc\7)\2\2\u00d8\u00db\5w<\2\u00d9"+
		"\u00db\13\2\2\2\u00da\u00d8\3\2\2\2\u00da\u00d9\3\2\2\2\u00db\u00de\3"+
		"\2\2\2\u00dc\u00dd\3\2\2\2\u00dc\u00da\3\2\2\2\u00dd\u00df\3\2\2\2\u00de"+
		"\u00dc\3\2\2\2\u00df\u00e0\7)\2\2\u00e0*\3\2\2\2\u00e1\u00e2\5\27\f\2"+
		"\u00e2\u00e3\7\60\2\2\u00e3\u00e4\5\27\f\2\u00e4\u00e8\3\2\2\2\u00e5\u00e6"+
		"\7\60\2\2\u00e6\u00e8\5\27\f\2\u00e7\u00e1\3\2\2\2\u00e7\u00e5\3\2\2\2"+
		"\u00e8,\3\2\2\2\u00e9\u00fb\5\61\31\2\u00ea\u00fb\5\63\32\2\u00eb\u00fb"+
		"\5\67\34\2\u00ec\u00fb\5;\36\2\u00ed\u00fb\5=\37\2\u00ee\u00fb\5C\"\2"+
		"\u00ef\u00fb\5? \2\u00f0\u00fb\5M\'\2\u00f1\u00fb\5C\"\2\u00f2\u00fb\5"+
		"E#\2\u00f3\u00fb\5G$\2\u00f4\u00fb\5I%\2\u00f5\u00fb\5O(\2\u00f6\u00fb"+
		"\5Q)\2\u00f7\u00fb\5U+\2\u00f8\u00fb\5W,\2\u00f9\u00fb\5K&\2\u00fa\u00e9"+
		"\3\2\2\2\u00fa\u00ea\3\2\2\2\u00fa\u00eb\3\2\2\2\u00fa\u00ec\3\2\2\2\u00fa"+
		"\u00ed\3\2\2\2\u00fa\u00ee\3\2\2\2\u00fa\u00ef\3\2\2\2\u00fa\u00f0\3\2"+
		"\2\2\u00fa\u00f1\3\2\2\2\u00fa\u00f2\3\2\2\2\u00fa\u00f3\3\2\2\2\u00fa"+
		"\u00f4\3\2\2\2\u00fa\u00f5\3\2\2\2\u00fa\u00f6\3\2\2\2\u00fa\u00f7\3\2"+
		"\2\2\u00fa\u00f8\3\2\2\2\u00fa\u00f9\3\2\2\2\u00fb.\3\2\2\2\u00fc\u00fd"+
		"\7/\2\2\u00fd\u00fe\7@\2\2\u00fe\60\3\2\2\2\u00ff\u0100\7,\2\2\u0100\62"+
		"\3\2\2\2\u0101\u0102\7\61\2\2\u0102\64\3\2\2\2\u0103\u0104\7-\2\2\u0104"+
		"\u0105\7-\2\2\u0105\66\3\2\2\2\u0106\u0107\7-\2\2\u01078\3\2\2\2\u0108"+
		"\u0109\7/\2\2\u0109\u010a\7/\2\2\u010a:\3\2\2\2\u010b\u010c\7/\2\2\u010c"+
		"<\3\2\2\2\u010d\u010e\7>\2\2\u010e>\3\2\2\2\u010f\u0110\7@\2\2\u0110@"+
		"\3\2\2\2\u0111\u0112\7?\2\2\u0112B\3\2\2\2\u0113\u0114\7>\2\2\u0114\u0115"+
		"\7?\2\2\u0115D\3\2\2\2\u0116\u0117\7@\2\2\u0117\u0118\7?\2\2\u0118F\3"+
		"\2\2\2\u0119\u011a\7?\2\2\u011a\u011b\7?\2\2\u011bH\3\2\2\2\u011c\u011d"+
		"\7#\2\2\u011d\u011e\7?\2\2\u011eJ\3\2\2\2\u011f\u0120\7#\2\2\u0120L\3"+
		"\2\2\2\u0121\u0122\7`\2\2\u0122N\3\2\2\2\u0123\u0124\7(\2\2\u0124\u0125"+
		"\7(\2\2\u0125P\3\2\2\2\u0126\u0127\7~\2\2\u0127\u0128\7~\2\2\u0128R\3"+
		"\2\2\2\u0129\u012a\7b\2\2\u012aT\3\2\2\2\u012b\u012c\7\'\2\2\u012cV\3"+
		"\2\2\2\u012d\u012e\7\u0080\2\2\u012eX\3\2\2\2\u012f\u0130\7\u0080\2\2"+
		"\u0130\u0131\7~\2\2\u0131Z\3\2\2\2\u0132\u0133\7_\2\2\u0133\\\3\2\2\2"+
		"\u0134\u0135\7]\2\2\u0135^\3\2\2\2\u0136\u0137\7k\2\2\u0137\u0138\7h\2"+
		"\2\u0138\u0139\7]\2\2\u0139`\3\2\2\2\u013a\u013b\7_\2\2\u013b\u013c\7"+
		"v\2\2\u013c\u013d\7j\2\2\u013d\u013e\7g\2\2\u013e\u013f\7p\2\2\u013f\u0140"+
		"\7]\2\2\u0140b\3\2\2\2\u0141\u0142\7_\2\2\u0142\u0143\7g\2\2\u0143\u0144"+
		"\7n\2\2\u0144\u0145\7u\2\2\u0145\u0146\7g\2\2\u0146\u0147\7]\2\2\u0147"+
		"d\3\2\2\2\u0148\u0149\7y\2\2\u0149\u014a\7j\2\2\u014a\u014b\7k\2\2\u014b"+
		"\u014c\7n\2\2\u014c\u014d\7g\2\2\u014d\u014e\7]\2\2\u014ef\3\2\2\2\u014f"+
		"\u0150\7_\2\2\u0150\u0151\7f\2\2\u0151\u0152\7q\2\2\u0152\u0153\7]\2\2"+
		"\u0153h\3\2\2\2\u0154\u0155\7u\2\2\u0155\u0156\7y\2\2\u0156\u0157\7k\2"+
		"\2\u0157\u0158\7v\2\2\u0158\u0159\7e\2\2\u0159\u015a\7j\2\2\u015a\u015b"+
		"\7]\2\2\u015bj\3\2\2\2\u015c\u015d\7f\2\2\u015d\u015e\7g\2\2\u015e\u015f"+
		"\7h\2\2\u015f\u0160\7k\2\2\u0160\u0161\7p\2\2\u0161\u0162\7g\2\2\u0162"+
		"\u0163\7]\2\2\u0163l\3\2\2\2\u0164\u0165\7_\2\2\u0165\u0166\7d\2\2\u0166"+
		"\u0167\7q\2\2\u0167\u0168\7f\2\2\u0168\u0169\7{\2\2\u0169\u016a\7]\2\2"+
		"\u016an\3\2\2\2\u016b\u016c\7o\2\2\u016c\u016d\7q\2\2\u016d\u016e\7f\2"+
		"\2\u016e\u016f\7w\2\2\u016f\u0170\7n\2\2\u0170\u0171\7g\2\2\u0171\u0172"+
		"\7]\2\2\u0172p\3\2\2\2\u0173\u0174\7v\2\2\u0174\u0175\7t\2\2\u0175\u0176"+
		"\7{\2\2\u0176\u0177\7]\2\2\u0177r\3\2\2\2\u0178\u0179\7_\2\2\u0179\u017a"+
		"\7e\2\2\u017a\u017b\7c\2\2\u017b\u017c\7v\2\2\u017c\u017d\7e\2\2\u017d"+
		"\u017e\7j\2\2\u017e\u017f\7]\2\2\u017ft\3\2\2\2\u0180\u0181\7_\2\2\u0181"+
		"\u0182\7]\2\2\u0182v\3\2\2\2\u0183\u0184\7^\2\2\u0184\u0185\7)\2\2\u0185"+
		"x\3\2\2\2\u0186\u0187\7\61\2\2\u0187\u0188\7,\2\2\u0188\u018c\3\2\2\2"+
		"\u0189\u018b\13\2\2\2\u018a\u0189\3\2\2\2\u018b\u018e\3\2\2\2\u018c\u018d"+
		"\3\2\2\2\u018c\u018a\3\2\2\2\u018d\u018f\3\2\2\2\u018e\u018c\3\2\2\2\u018f"+
		"\u0190\7,\2\2\u0190\u0191\7\61\2\2\u0191\u0192\3\2\2\2\u0192\u0193\b="+
		"\2\2\u0193z\3\2\2\2\u0194\u0195\7\61\2\2\u0195\u0196\7\61\2\2\u0196\u019a"+
		"\3\2\2\2\u0197\u0199\n\6\2\2\u0198\u0197\3\2\2\2\u0199\u019c\3\2\2\2\u019a"+
		"\u0198\3\2\2\2\u019a\u019b\3\2\2\2\u019b\u019d\3\2\2\2\u019c\u019a\3\2"+
		"\2\2\u019d\u019e\b>\2\2\u019e|\3\2\2\2\u019f\u01a1\t\7\2\2\u01a0\u019f"+
		"\3\2\2\2\u01a1\u01a2\3\2\2\2\u01a2\u01a0\3\2\2\2\u01a2\u01a3\3\2\2\2\u01a3"+
		"\u01a4\3\2\2\2\u01a4\u01a5\b?\2\2\u01a5~\3\2\2\2\u01a6\u01a7\7@\2\2\u01a7"+
		"\u01a8\7@\2\2\u01a8\u01ac\3\2\2\2\u01a9\u01ab\n\6\2\2\u01aa\u01a9\3\2"+
		"\2\2\u01ab\u01ae\3\2\2\2\u01ac\u01aa\3\2\2\2\u01ac\u01ad\3\2\2\2\u01ad"+
		"\u0080\3\2\2\2\u01ae\u01ac\3\2\2\2\21\2\u009a\u00a0\u00a5\u00b5\u00bb"+
		"\u00c5\u00da\u00dc\u00e7\u00fa\u018c\u019a\u01a2\u01ac\3\b\2\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}