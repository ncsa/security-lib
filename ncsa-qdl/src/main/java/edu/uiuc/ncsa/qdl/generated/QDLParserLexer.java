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
		T__0=1, T__1=2, T__2=3, T__3=4, T__4=5, T__5=6, T__6=7, T__7=8, Integer=9, 
		Identifier=10, Bool=11, ASSIGN=12, FuncStart=13, F_REF=14, BOOL_TRUE=15, 
		BOOL_FALSE=16, Null=17, STRING=18, Decimal=19, SCIENTIFIC_NUMBER=20, LambdaConnector=21, 
		Times=22, Divide=23, PlusPlus=24, Plus=25, MinusMinus=26, Minus=27, LessThan=28, 
		GreaterThan=29, SingleEqual=30, LessEquals=31, MoreEquals=32, Equals=33, 
		NotEquals=34, LogicalNot=35, Exponentiation=36, And=37, Or=38, Backtick=39, 
		Percent=40, Tilde=41, TildeRight=42, LeftBracket=43, RightBracket=44, 
		LogicalIf=45, LogicalThen=46, LogicalElse=47, WhileLoop=48, WhileDo=49, 
		SwitchStatement=50, DefineStatement=51, BodyStatement=52, ModuleStatement=53, 
		TryStatement=54, CatchStatement=55, StatementConnector=56, COMMENT=57, 
		LINE_COMMENT=58, WS2=59, FDOC=60;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	private static String[] makeRuleNames() {
		return new String[] {
			"T__0", "T__1", "T__2", "T__3", "T__4", "T__5", "T__6", "T__7", "Integer", 
			"Identifier", "Bool", "ASSIGN", "FuncStart", "F_REF", "BOOL_TRUE", "BOOL_FALSE", 
			"Null", "STRING", "Decimal", "AllOps", "SCIENTIFIC_NUMBER", "E", "SIGN", 
			"LambdaConnector", "Times", "Divide", "PlusPlus", "Plus", "MinusMinus", 
			"Minus", "LessThan", "GreaterThan", "SingleEqual", "LessEquals", "MoreEquals", 
			"Equals", "NotEquals", "LogicalNot", "Exponentiation", "And", "Or", "Backtick", 
			"Percent", "Tilde", "TildeRight", "LeftBracket", "RightBracket", "LogicalIf", 
			"LogicalThen", "LogicalElse", "WhileLoop", "WhileDo", "SwitchStatement", 
			"DefineStatement", "BodyStatement", "ModuleStatement", "TryStatement", 
			"CatchStatement", "StatementConnector", "ESC", "COMMENT", "LINE_COMMENT", 
			"WS2", "FDOC"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'{'", "','", "'}'", "':'", "')'", "'.'", "'('", "';'", null, null, 
			null, null, null, null, "'true'", "'false'", null, null, null, null, 
			null, null, null, "'++'", "'+'", "'--'", "'-'", "'<'", "'>'", "'='", 
			null, null, null, null, null, "'^'", null, null, "'`'", "'%'", "'~'", 
			"'~|'", "']'", "'['", "'if['", "']then['", "']else['", "'while['", "']do['", 
			"'switch['", "'define['", "']body['", "'module['", "'try['", "']catch['", 
			"']['"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, null, null, null, null, null, null, null, null, "Integer", "Identifier", 
			"Bool", "ASSIGN", "FuncStart", "F_REF", "BOOL_TRUE", "BOOL_FALSE", "Null", 
			"STRING", "Decimal", "SCIENTIFIC_NUMBER", "LambdaConnector", "Times", 
			"Divide", "PlusPlus", "Plus", "MinusMinus", "Minus", "LessThan", "GreaterThan", 
			"SingleEqual", "LessEquals", "MoreEquals", "Equals", "NotEquals", "LogicalNot", 
			"Exponentiation", "And", "Or", "Backtick", "Percent", "Tilde", "TildeRight", 
			"LeftBracket", "RightBracket", "LogicalIf", "LogicalThen", "LogicalElse", 
			"WhileLoop", "WhileDo", "SwitchStatement", "DefineStatement", "BodyStatement", 
			"ModuleStatement", "TryStatement", "CatchStatement", "StatementConnector", 
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
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2>\u01cf\b\1\4\2\t"+
		"\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13"+
		"\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31\t\31"+
		"\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4 \t \4!"+
		"\t!\4\"\t\"\4#\t#\4$\t$\4%\t%\4&\t&\4\'\t\'\4(\t(\4)\t)\4*\t*\4+\t+\4"+
		",\t,\4-\t-\4.\t.\4/\t/\4\60\t\60\4\61\t\61\4\62\t\62\4\63\t\63\4\64\t"+
		"\64\4\65\t\65\4\66\t\66\4\67\t\67\48\t8\49\t9\4:\t:\4;\t;\4<\t<\4=\t="+
		"\4>\t>\4?\t?\4@\t@\4A\tA\3\2\3\2\3\3\3\3\3\4\3\4\3\5\3\5\3\6\3\6\3\7\3"+
		"\7\3\b\3\b\3\t\3\t\3\n\6\n\u0095\n\n\r\n\16\n\u0096\3\13\3\13\7\13\u009b"+
		"\n\13\f\13\16\13\u009e\13\13\3\f\3\f\5\f\u00a2\n\f\3\r\3\r\3\r\3\r\3\r"+
		"\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3\r\5\r\u00b5\n\r\3\16\3"+
		"\16\7\16\u00b9\n\16\f\16\16\16\u00bc\13\16\3\16\3\16\3\17\3\17\3\17\3"+
		"\17\3\17\5\17\u00c5\n\17\3\20\3\20\3\20\3\20\3\20\3\21\3\21\3\21\3\21"+
		"\3\21\3\21\3\22\3\22\3\22\3\22\3\22\5\22\u00d7\n\22\3\23\3\23\3\23\7\23"+
		"\u00dc\n\23\f\23\16\23\u00df\13\23\3\23\3\23\3\24\3\24\3\24\3\24\3\24"+
		"\3\24\5\24\u00e9\n\24\3\25\3\25\3\25\3\25\3\25\3\25\3\25\3\25\3\25\3\25"+
		"\3\25\3\25\3\25\3\25\3\25\3\25\3\25\5\25\u00fc\n\25\3\26\3\26\3\26\5\26"+
		"\u0101\n\26\3\26\3\26\5\26\u0105\n\26\3\27\3\27\3\30\3\30\3\31\3\31\3"+
		"\31\5\31\u010e\n\31\3\32\3\32\3\33\3\33\3\34\3\34\3\34\3\35\3\35\3\36"+
		"\3\36\3\36\3\37\3\37\3 \3 \3!\3!\3\"\3\"\3#\3#\3#\3#\3#\5#\u0129\n#\3"+
		"$\3$\3$\3$\3$\5$\u0130\n$\3%\3%\3%\5%\u0135\n%\3&\3&\3&\5&\u013a\n&\3"+
		"\'\3\'\3(\3(\3)\3)\3)\5)\u0143\n)\3*\3*\3*\5*\u0148\n*\3+\3+\3,\3,\3-"+
		"\3-\3.\3.\3.\3/\3/\3\60\3\60\3\61\3\61\3\61\3\61\3\62\3\62\3\62\3\62\3"+
		"\62\3\62\3\62\3\63\3\63\3\63\3\63\3\63\3\63\3\63\3\64\3\64\3\64\3\64\3"+
		"\64\3\64\3\64\3\65\3\65\3\65\3\65\3\65\3\66\3\66\3\66\3\66\3\66\3\66\3"+
		"\66\3\66\3\67\3\67\3\67\3\67\3\67\3\67\3\67\3\67\38\38\38\38\38\38\38"+
		"\39\39\39\39\39\39\39\39\3:\3:\3:\3:\3:\3;\3;\3;\3;\3;\3;\3;\3;\3<\3<"+
		"\3<\3=\3=\3=\3>\3>\3>\3>\7>\u01ab\n>\f>\16>\u01ae\13>\3>\3>\3>\3>\3>\3"+
		"?\3?\3?\3?\7?\u01b9\n?\f?\16?\u01bc\13?\3?\3?\3@\6@\u01c1\n@\r@\16@\u01c2"+
		"\3@\3@\3A\3A\3A\3A\7A\u01cb\nA\fA\16A\u01ce\13A\4\u00dd\u01ac\2B\3\3\5"+
		"\4\7\5\t\6\13\7\r\b\17\t\21\n\23\13\25\f\27\r\31\16\33\17\35\20\37\21"+
		"!\22#\23%\24\'\25)\2+\26-\2/\2\61\27\63\30\65\31\67\329\33;\34=\35?\36"+
		"A\37C E!G\"I#K$M%O&Q\'S(U)W*Y+[,]-_.a/c\60e\61g\62i\63k\64m\65o\66q\67"+
		"s8u9w:y\2{;}<\177=\u0081>\3\2\17\3\2\62;\6\2%&C\\aac|\b\2%&\60\60\62;"+
		"C\\aac|\7\2%&\62;C\\aac|\4\2GGgg\4\2--//\4\2,,\u00d9\u00d9\4\2\61\61\u00f9"+
		"\u00f9\4\2##\u00ae\u00ae\4\2\u2229\u2229\u22c2\u22c2\4\2\u222a\u222a\u22c3"+
		"\u22c3\4\2\f\f\17\17\5\2\13\f\17\17\"\"\2\u01f9\2\3\3\2\2\2\2\5\3\2\2"+
		"\2\2\7\3\2\2\2\2\t\3\2\2\2\2\13\3\2\2\2\2\r\3\2\2\2\2\17\3\2\2\2\2\21"+
		"\3\2\2\2\2\23\3\2\2\2\2\25\3\2\2\2\2\27\3\2\2\2\2\31\3\2\2\2\2\33\3\2"+
		"\2\2\2\35\3\2\2\2\2\37\3\2\2\2\2!\3\2\2\2\2#\3\2\2\2\2%\3\2\2\2\2\'\3"+
		"\2\2\2\2+\3\2\2\2\2\61\3\2\2\2\2\63\3\2\2\2\2\65\3\2\2\2\2\67\3\2\2\2"+
		"\29\3\2\2\2\2;\3\2\2\2\2=\3\2\2\2\2?\3\2\2\2\2A\3\2\2\2\2C\3\2\2\2\2E"+
		"\3\2\2\2\2G\3\2\2\2\2I\3\2\2\2\2K\3\2\2\2\2M\3\2\2\2\2O\3\2\2\2\2Q\3\2"+
		"\2\2\2S\3\2\2\2\2U\3\2\2\2\2W\3\2\2\2\2Y\3\2\2\2\2[\3\2\2\2\2]\3\2\2\2"+
		"\2_\3\2\2\2\2a\3\2\2\2\2c\3\2\2\2\2e\3\2\2\2\2g\3\2\2\2\2i\3\2\2\2\2k"+
		"\3\2\2\2\2m\3\2\2\2\2o\3\2\2\2\2q\3\2\2\2\2s\3\2\2\2\2u\3\2\2\2\2w\3\2"+
		"\2\2\2{\3\2\2\2\2}\3\2\2\2\2\177\3\2\2\2\2\u0081\3\2\2\2\3\u0083\3\2\2"+
		"\2\5\u0085\3\2\2\2\7\u0087\3\2\2\2\t\u0089\3\2\2\2\13\u008b\3\2\2\2\r"+
		"\u008d\3\2\2\2\17\u008f\3\2\2\2\21\u0091\3\2\2\2\23\u0094\3\2\2\2\25\u0098"+
		"\3\2\2\2\27\u00a1\3\2\2\2\31\u00b4\3\2\2\2\33\u00b6\3\2\2\2\35\u00bf\3"+
		"\2\2\2\37\u00c6\3\2\2\2!\u00cb\3\2\2\2#\u00d6\3\2\2\2%\u00d8\3\2\2\2\'"+
		"\u00e8\3\2\2\2)\u00fb\3\2\2\2+\u00fd\3\2\2\2-\u0106\3\2\2\2/\u0108\3\2"+
		"\2\2\61\u010d\3\2\2\2\63\u010f\3\2\2\2\65\u0111\3\2\2\2\67\u0113\3\2\2"+
		"\29\u0116\3\2\2\2;\u0118\3\2\2\2=\u011b\3\2\2\2?\u011d\3\2\2\2A\u011f"+
		"\3\2\2\2C\u0121\3\2\2\2E\u0128\3\2\2\2G\u012f\3\2\2\2I\u0134\3\2\2\2K"+
		"\u0139\3\2\2\2M\u013b\3\2\2\2O\u013d\3\2\2\2Q\u0142\3\2\2\2S\u0147\3\2"+
		"\2\2U\u0149\3\2\2\2W\u014b\3\2\2\2Y\u014d\3\2\2\2[\u014f\3\2\2\2]\u0152"+
		"\3\2\2\2_\u0154\3\2\2\2a\u0156\3\2\2\2c\u015a\3\2\2\2e\u0161\3\2\2\2g"+
		"\u0168\3\2\2\2i\u016f\3\2\2\2k\u0174\3\2\2\2m\u017c\3\2\2\2o\u0184\3\2"+
		"\2\2q\u018b\3\2\2\2s\u0193\3\2\2\2u\u0198\3\2\2\2w\u01a0\3\2\2\2y\u01a3"+
		"\3\2\2\2{\u01a6\3\2\2\2}\u01b4\3\2\2\2\177\u01c0\3\2\2\2\u0081\u01c6\3"+
		"\2\2\2\u0083\u0084\7}\2\2\u0084\4\3\2\2\2\u0085\u0086\7.\2\2\u0086\6\3"+
		"\2\2\2\u0087\u0088\7\177\2\2\u0088\b\3\2\2\2\u0089\u008a\7<\2\2\u008a"+
		"\n\3\2\2\2\u008b\u008c\7+\2\2\u008c\f\3\2\2\2\u008d\u008e\7\60\2\2\u008e"+
		"\16\3\2\2\2\u008f\u0090\7*\2\2\u0090\20\3\2\2\2\u0091\u0092\7=\2\2\u0092"+
		"\22\3\2\2\2\u0093\u0095\t\2\2\2\u0094\u0093\3\2\2\2\u0095\u0096\3\2\2"+
		"\2\u0096\u0094\3\2\2\2\u0096\u0097\3\2\2\2\u0097\24\3\2\2\2\u0098\u009c"+
		"\t\3\2\2\u0099\u009b\t\4\2\2\u009a\u0099\3\2\2\2\u009b\u009e\3\2\2\2\u009c"+
		"\u009a\3\2\2\2\u009c\u009d\3\2\2\2\u009d\26\3\2\2\2\u009e\u009c\3\2\2"+
		"\2\u009f\u00a2\5\37\20\2\u00a0\u00a2\5!\21\2\u00a1\u009f\3\2\2\2\u00a1"+
		"\u00a0\3\2\2\2\u00a2\30\3\2\2\2\u00a3\u00b5\7\u2256\2\2\u00a4\u00a5\7"+
		"<\2\2\u00a5\u00b5\7?\2\2\u00a6\u00a7\7-\2\2\u00a7\u00b5\7?\2\2\u00a8\u00a9"+
		"\7/\2\2\u00a9\u00b5\7?\2\2\u00aa\u00ab\5\63\32\2\u00ab\u00ac\7?\2\2\u00ac"+
		"\u00b5\3\2\2\2\u00ad\u00ae\5\65\33\2\u00ae\u00af\7?\2\2\u00af\u00b5\3"+
		"\2\2\2\u00b0\u00b1\7\'\2\2\u00b1\u00b5\7?\2\2\u00b2\u00b3\7`\2\2\u00b3"+
		"\u00b5\7?\2\2\u00b4\u00a3\3\2\2\2\u00b4\u00a4\3\2\2\2\u00b4\u00a6\3\2"+
		"\2\2\u00b4\u00a8\3\2\2\2\u00b4\u00aa\3\2\2\2\u00b4\u00ad\3\2\2\2\u00b4"+
		"\u00b0\3\2\2\2\u00b4\u00b2\3\2\2\2\u00b5\32\3\2\2\2\u00b6\u00ba\t\3\2"+
		"\2\u00b7\u00b9\t\5\2\2\u00b8\u00b7\3\2\2\2\u00b9\u00bc\3\2\2\2\u00ba\u00b8"+
		"\3\2\2\2\u00ba\u00bb\3\2\2\2\u00bb\u00bd\3\2\2\2\u00bc\u00ba\3\2\2\2\u00bd"+
		"\u00be\7*\2\2\u00be\34\3\2\2\2\u00bf\u00c4\7B\2\2\u00c0\u00c5\5)\25\2"+
		"\u00c1\u00c2\5\33\16\2\u00c2\u00c3\7+\2\2\u00c3\u00c5\3\2\2\2\u00c4\u00c0"+
		"\3\2\2\2\u00c4\u00c1\3\2\2\2\u00c5\36\3\2\2\2\u00c6\u00c7\7v\2\2\u00c7"+
		"\u00c8\7t\2\2\u00c8\u00c9\7w\2\2\u00c9\u00ca\7g\2\2\u00ca \3\2\2\2\u00cb"+
		"\u00cc\7h\2\2\u00cc\u00cd\7c\2\2\u00cd\u00ce\7n\2\2\u00ce\u00cf\7u\2\2"+
		"\u00cf\u00d0\7g\2\2\u00d0\"\3\2\2\2\u00d1\u00d2\7p\2\2\u00d2\u00d3\7w"+
		"\2\2\u00d3\u00d4\7n\2\2\u00d4\u00d7\7n\2\2\u00d5\u00d7\7\u2207\2\2\u00d6"+
		"\u00d1\3\2\2\2\u00d6\u00d5\3\2\2\2\u00d7$\3\2\2\2\u00d8\u00dd\7)\2\2\u00d9"+
		"\u00dc\5y=\2\u00da\u00dc\13\2\2\2\u00db\u00d9\3\2\2\2\u00db\u00da\3\2"+
		"\2\2\u00dc\u00df\3\2\2\2\u00dd\u00de\3\2\2\2\u00dd\u00db\3\2\2\2\u00de"+
		"\u00e0\3\2\2\2\u00df\u00dd\3\2\2\2\u00e0\u00e1\7)\2\2\u00e1&\3\2\2\2\u00e2"+
		"\u00e3\5\23\n\2\u00e3\u00e4\7\60\2\2\u00e4\u00e5\5\23\n\2\u00e5\u00e9"+
		"\3\2\2\2\u00e6\u00e7\7\60\2\2\u00e7\u00e9\5\23\n\2\u00e8\u00e2\3\2\2\2"+
		"\u00e8\u00e6\3\2\2\2\u00e9(\3\2\2\2\u00ea\u00fc\5\63\32\2\u00eb\u00fc"+
		"\5\65\33\2\u00ec\u00fc\59\35\2\u00ed\u00fc\5=\37\2\u00ee\u00fc\5? \2\u00ef"+
		"\u00fc\5E#\2\u00f0\u00fc\5A!\2\u00f1\u00fc\5O(\2\u00f2\u00fc\5E#\2\u00f3"+
		"\u00fc\5G$\2\u00f4\u00fc\5I%\2\u00f5\u00fc\5K&\2\u00f6\u00fc\5Q)\2\u00f7"+
		"\u00fc\5S*\2\u00f8\u00fc\5W,\2\u00f9\u00fc\5Y-\2\u00fa\u00fc\5M\'\2\u00fb"+
		"\u00ea\3\2\2\2\u00fb\u00eb\3\2\2\2\u00fb\u00ec\3\2\2\2\u00fb\u00ed\3\2"+
		"\2\2\u00fb\u00ee\3\2\2\2\u00fb\u00ef\3\2\2\2\u00fb\u00f0\3\2\2\2\u00fb"+
		"\u00f1\3\2\2\2\u00fb\u00f2\3\2\2\2\u00fb\u00f3\3\2\2\2\u00fb\u00f4\3\2"+
		"\2\2\u00fb\u00f5\3\2\2\2\u00fb\u00f6\3\2\2\2\u00fb\u00f7\3\2\2\2\u00fb"+
		"\u00f8\3\2\2\2\u00fb\u00f9\3\2\2\2\u00fb\u00fa\3\2\2\2\u00fc*\3\2\2\2"+
		"\u00fd\u0104\5\'\24\2\u00fe\u0100\5-\27\2\u00ff\u0101\5/\30\2\u0100\u00ff"+
		"\3\2\2\2\u0100\u0101\3\2\2\2\u0101\u0102\3\2\2\2\u0102\u0103\5\23\n\2"+
		"\u0103\u0105\3\2\2\2\u0104\u00fe\3\2\2\2\u0104\u0105\3\2\2\2\u0105,\3"+
		"\2\2\2\u0106\u0107\t\6\2\2\u0107.\3\2\2\2\u0108\u0109\t\7\2\2\u0109\60"+
		"\3\2\2\2\u010a\u010b\7/\2\2\u010b\u010e\7@\2\2\u010c\u010e\7\u2194\2\2"+
		"\u010d\u010a\3\2\2\2\u010d\u010c\3\2\2\2\u010e\62\3\2\2\2\u010f\u0110"+
		"\t\b\2\2\u0110\64\3\2\2\2\u0111\u0112\t\t\2\2\u0112\66\3\2\2\2\u0113\u0114"+
		"\7-\2\2\u0114\u0115\7-\2\2\u01158\3\2\2\2\u0116\u0117\7-\2\2\u0117:\3"+
		"\2\2\2\u0118\u0119\7/\2\2\u0119\u011a\7/\2\2\u011a<\3\2\2\2\u011b\u011c"+
		"\7/\2\2\u011c>\3\2\2\2\u011d\u011e\7>\2\2\u011e@\3\2\2\2\u011f\u0120\7"+
		"@\2\2\u0120B\3\2\2\2\u0121\u0122\7?\2\2\u0122D\3\2\2\2\u0123\u0124\7>"+
		"\2\2\u0124\u0129\7?\2\2\u0125\u0129\7\u2266\2\2\u0126\u0127\7?\2\2\u0127"+
		"\u0129\7>\2\2\u0128\u0123\3\2\2\2\u0128\u0125\3\2\2\2\u0128\u0126\3\2"+
		"\2\2\u0129F\3\2\2\2\u012a\u012b\7@\2\2\u012b\u0130\7?\2\2\u012c\u0130"+
		"\7\u2267\2\2\u012d\u012e\7?\2\2\u012e\u0130\7@\2\2\u012f\u012a\3\2\2\2"+
		"\u012f\u012c\3\2\2\2\u012f\u012d\3\2\2\2\u0130H\3\2\2\2\u0131\u0132\7"+
		"?\2\2\u0132\u0135\7?\2\2\u0133\u0135\7\u2263\2\2\u0134\u0131\3\2\2\2\u0134"+
		"\u0133\3\2\2\2\u0135J\3\2\2\2\u0136\u0137\7#\2\2\u0137\u013a\7?\2\2\u0138"+
		"\u013a\7\u2262\2\2\u0139\u0136\3\2\2\2\u0139\u0138\3\2\2\2\u013aL\3\2"+
		"\2\2\u013b\u013c\t\n\2\2\u013cN\3\2\2\2\u013d\u013e\7`\2\2\u013eP\3\2"+
		"\2\2\u013f\u0140\7(\2\2\u0140\u0143\7(\2\2\u0141\u0143\t\13\2\2\u0142"+
		"\u013f\3\2\2\2\u0142\u0141\3\2\2\2\u0143R\3\2\2\2\u0144\u0145\7~\2\2\u0145"+
		"\u0148\7~\2\2\u0146\u0148\t\f\2\2\u0147\u0144\3\2\2\2\u0147\u0146\3\2"+
		"\2\2\u0148T\3\2\2\2\u0149\u014a\7b\2\2\u014aV\3\2\2\2\u014b\u014c\7\'"+
		"\2\2\u014cX\3\2\2\2\u014d\u014e\7\u0080\2\2\u014eZ\3\2\2\2\u014f\u0150"+
		"\7\u0080\2\2\u0150\u0151\7~\2\2\u0151\\\3\2\2\2\u0152\u0153\7_\2\2\u0153"+
		"^\3\2\2\2\u0154\u0155\7]\2\2\u0155`\3\2\2\2\u0156\u0157\7k\2\2\u0157\u0158"+
		"\7h\2\2\u0158\u0159\7]\2\2\u0159b\3\2\2\2\u015a\u015b\7_\2\2\u015b\u015c"+
		"\7v\2\2\u015c\u015d\7j\2\2\u015d\u015e\7g\2\2\u015e\u015f\7p\2\2\u015f"+
		"\u0160\7]\2\2\u0160d\3\2\2\2\u0161\u0162\7_\2\2\u0162\u0163\7g\2\2\u0163"+
		"\u0164\7n\2\2\u0164\u0165\7u\2\2\u0165\u0166\7g\2\2\u0166\u0167\7]\2\2"+
		"\u0167f\3\2\2\2\u0168\u0169\7y\2\2\u0169\u016a\7j\2\2\u016a\u016b\7k\2"+
		"\2\u016b\u016c\7n\2\2\u016c\u016d\7g\2\2\u016d\u016e\7]\2\2\u016eh\3\2"+
		"\2\2\u016f\u0170\7_\2\2\u0170\u0171\7f\2\2\u0171\u0172\7q\2\2\u0172\u0173"+
		"\7]\2\2\u0173j\3\2\2\2\u0174\u0175\7u\2\2\u0175\u0176\7y\2\2\u0176\u0177"+
		"\7k\2\2\u0177\u0178\7v\2\2\u0178\u0179\7e\2\2\u0179\u017a\7j\2\2\u017a"+
		"\u017b\7]\2\2\u017bl\3\2\2\2\u017c\u017d\7f\2\2\u017d\u017e\7g\2\2\u017e"+
		"\u017f\7h\2\2\u017f\u0180\7k\2\2\u0180\u0181\7p\2\2\u0181\u0182\7g\2\2"+
		"\u0182\u0183\7]\2\2\u0183n\3\2\2\2\u0184\u0185\7_\2\2\u0185\u0186\7d\2"+
		"\2\u0186\u0187\7q\2\2\u0187\u0188\7f\2\2\u0188\u0189\7{\2\2\u0189\u018a"+
		"\7]\2\2\u018ap\3\2\2\2\u018b\u018c\7o\2\2\u018c\u018d\7q\2\2\u018d\u018e"+
		"\7f\2\2\u018e\u018f\7w\2\2\u018f\u0190\7n\2\2\u0190\u0191\7g\2\2\u0191"+
		"\u0192\7]\2\2\u0192r\3\2\2\2\u0193\u0194\7v\2\2\u0194\u0195\7t\2\2\u0195"+
		"\u0196\7{\2\2\u0196\u0197\7]\2\2\u0197t\3\2\2\2\u0198\u0199\7_\2\2\u0199"+
		"\u019a\7e\2\2\u019a\u019b\7c\2\2\u019b\u019c\7v\2\2\u019c\u019d\7e\2\2"+
		"\u019d\u019e\7j\2\2\u019e\u019f\7]\2\2\u019fv\3\2\2\2\u01a0\u01a1\7_\2"+
		"\2\u01a1\u01a2\7]\2\2\u01a2x\3\2\2\2\u01a3\u01a4\7^\2\2\u01a4\u01a5\7"+
		")\2\2\u01a5z\3\2\2\2\u01a6\u01a7\7\61\2\2\u01a7\u01a8\7,\2\2\u01a8\u01ac"+
		"\3\2\2\2\u01a9\u01ab\13\2\2\2\u01aa\u01a9\3\2\2\2\u01ab\u01ae\3\2\2\2"+
		"\u01ac\u01ad\3\2\2\2\u01ac\u01aa\3\2\2\2\u01ad\u01af\3\2\2\2\u01ae\u01ac"+
		"\3\2\2\2\u01af\u01b0\7,\2\2\u01b0\u01b1\7\61\2\2\u01b1\u01b2\3\2\2\2\u01b2"+
		"\u01b3\b>\2\2\u01b3|\3\2\2\2\u01b4\u01b5\7\61\2\2\u01b5\u01b6\7\61\2\2"+
		"\u01b6\u01ba\3\2\2\2\u01b7\u01b9\n\r\2\2\u01b8\u01b7\3\2\2\2\u01b9\u01bc"+
		"\3\2\2\2\u01ba\u01b8\3\2\2\2\u01ba\u01bb\3\2\2\2\u01bb\u01bd\3\2\2\2\u01bc"+
		"\u01ba\3\2\2\2\u01bd\u01be\b?\2\2\u01be~\3\2\2\2\u01bf\u01c1\t\16\2\2"+
		"\u01c0\u01bf\3\2\2\2\u01c1\u01c2\3\2\2\2\u01c2\u01c0\3\2\2\2\u01c2\u01c3"+
		"\3\2\2\2\u01c3\u01c4\3\2\2\2\u01c4\u01c5\b@\2\2\u01c5\u0080\3\2\2\2\u01c6"+
		"\u01c7\7@\2\2\u01c7\u01c8\7@\2\2\u01c8\u01cc\3\2\2\2\u01c9\u01cb\n\r\2"+
		"\2\u01ca\u01c9\3\2\2\2\u01cb\u01ce\3\2\2\2\u01cc\u01ca\3\2\2\2\u01cc\u01cd"+
		"\3\2\2\2\u01cd\u0082\3\2\2\2\u01ce\u01cc\3\2\2\2\33\2\u0096\u009c\u00a1"+
		"\u00b4\u00ba\u00c4\u00d6\u00db\u00dd\u00e8\u00fb\u0100\u0104\u010d\u0128"+
		"\u012f\u0134\u0139\u0142\u0147\u01ac\u01ba\u01c2\u01cc\3\b\2\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}