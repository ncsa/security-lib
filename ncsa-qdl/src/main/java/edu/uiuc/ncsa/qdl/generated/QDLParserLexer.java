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
		Percent=40, Tilde=41, Backslash=42, Stile=43, TildeRight=44, LeftBracket=45, 
		RightBracket=46, LogicalIf=47, LogicalThen=48, LogicalElse=49, WhileLoop=50, 
		WhileDo=51, SwitchStatement=52, DefineStatement=53, BodyStatement=54, 
		ModuleStatement=55, TryStatement=56, CatchStatement=57, StatementConnector=58, 
		COMMENT=59, LINE_COMMENT=60, WS2=61, FDOC=62;
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
			"Percent", "Tilde", "Backslash", "Stile", "TildeRight", "LeftBracket", 
			"RightBracket", "LogicalIf", "LogicalThen", "LogicalElse", "WhileLoop", 
			"WhileDo", "SwitchStatement", "DefineStatement", "BodyStatement", "ModuleStatement", 
			"TryStatement", "CatchStatement", "StatementConnector", "ESC", "UNICODE", 
			"HEX", "SAFECODEPOINT", "COMMENT", "LINE_COMMENT", "WS2", "FDOC"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'{'", "','", "'}'", "':'", "')'", "'.'", "'('", "';'", null, null, 
			null, null, null, null, "'true'", "'false'", null, null, null, null, 
			null, null, null, "'++'", "'+'", "'--'", "'-'", "'<'", "'>'", "'='", 
			null, null, null, null, null, "'^'", null, null, "'`'", "'%'", "'~'", 
			"'\\'", "'|'", "'~|'", "']'", "'['", "'if['", "']then['", "']else['", 
			"'while['", "']do['", "'switch['", "'define['", "']body['", "'module['", 
			"'try['", "']catch['", "']['"
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
			"Exponentiation", "And", "Or", "Backtick", "Percent", "Tilde", "Backslash", 
			"Stile", "TildeRight", "LeftBracket", "RightBracket", "LogicalIf", "LogicalThen", 
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
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2@\u01e9\b\1\4\2\t"+
		"\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13"+
		"\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31\t\31"+
		"\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4 \t \4!"+
		"\t!\4\"\t\"\4#\t#\4$\t$\4%\t%\4&\t&\4\'\t\'\4(\t(\4)\t)\4*\t*\4+\t+\4"+
		",\t,\4-\t-\4.\t.\4/\t/\4\60\t\60\4\61\t\61\4\62\t\62\4\63\t\63\4\64\t"+
		"\64\4\65\t\65\4\66\t\66\4\67\t\67\48\t8\49\t9\4:\t:\4;\t;\4<\t<\4=\t="+
		"\4>\t>\4?\t?\4@\t@\4A\tA\4B\tB\4C\tC\4D\tD\4E\tE\4F\tF\3\2\3\2\3\3\3\3"+
		"\3\4\3\4\3\5\3\5\3\6\3\6\3\7\3\7\3\b\3\b\3\t\3\t\3\n\6\n\u009f\n\n\r\n"+
		"\16\n\u00a0\3\13\3\13\7\13\u00a5\n\13\f\13\16\13\u00a8\13\13\3\f\3\f\5"+
		"\f\u00ac\n\f\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3"+
		"\r\3\r\3\r\5\r\u00bf\n\r\3\16\3\16\7\16\u00c3\n\16\f\16\16\16\u00c6\13"+
		"\16\3\16\3\16\3\17\3\17\3\17\3\17\3\17\5\17\u00cf\n\17\3\20\3\20\3\20"+
		"\3\20\3\20\3\21\3\21\3\21\3\21\3\21\3\21\3\22\3\22\3\22\3\22\3\22\5\22"+
		"\u00e1\n\22\3\23\3\23\3\23\7\23\u00e6\n\23\f\23\16\23\u00e9\13\23\3\23"+
		"\3\23\3\24\3\24\3\24\3\24\3\24\3\24\5\24\u00f3\n\24\3\25\3\25\3\25\3\25"+
		"\3\25\3\25\3\25\3\25\3\25\3\25\3\25\3\25\3\25\3\25\3\25\3\25\3\25\5\25"+
		"\u0106\n\25\3\26\3\26\3\26\5\26\u010b\n\26\3\26\3\26\5\26\u010f\n\26\3"+
		"\27\3\27\3\30\3\30\3\31\3\31\3\31\5\31\u0118\n\31\3\32\3\32\3\33\3\33"+
		"\3\34\3\34\3\34\3\35\3\35\3\36\3\36\3\36\3\37\3\37\3 \3 \3!\3!\3\"\3\""+
		"\3#\3#\3#\3#\3#\5#\u0133\n#\3$\3$\3$\3$\3$\5$\u013a\n$\3%\3%\3%\5%\u013f"+
		"\n%\3&\3&\3&\5&\u0144\n&\3\'\3\'\3(\3(\3)\3)\3)\5)\u014d\n)\3*\3*\3*\5"+
		"*\u0152\n*\3+\3+\3,\3,\3-\3-\3.\3.\3/\3/\3\60\3\60\3\60\3\61\3\61\3\62"+
		"\3\62\3\63\3\63\3\63\3\63\3\64\3\64\3\64\3\64\3\64\3\64\3\64\3\65\3\65"+
		"\3\65\3\65\3\65\3\65\3\65\3\66\3\66\3\66\3\66\3\66\3\66\3\66\3\67\3\67"+
		"\3\67\3\67\3\67\38\38\38\38\38\38\38\38\39\39\39\39\39\39\39\39\3:\3:"+
		"\3:\3:\3:\3:\3:\3;\3;\3;\3;\3;\3;\3;\3;\3<\3<\3<\3<\3<\3=\3=\3=\3=\3="+
		"\3=\3=\3=\3>\3>\3>\3?\3?\3?\5?\u01b5\n?\3@\3@\3@\3@\3@\3@\3A\3A\3B\3B"+
		"\3C\3C\3C\3C\7C\u01c5\nC\fC\16C\u01c8\13C\3C\3C\3C\3C\3C\3D\3D\3D\3D\7"+
		"D\u01d3\nD\fD\16D\u01d6\13D\3D\3D\3E\6E\u01db\nE\rE\16E\u01dc\3E\3E\3"+
		"F\3F\3F\3F\7F\u01e5\nF\fF\16F\u01e8\13F\4\u00e7\u01c6\2G\3\3\5\4\7\5\t"+
		"\6\13\7\r\b\17\t\21\n\23\13\25\f\27\r\31\16\33\17\35\20\37\21!\22#\23"+
		"%\24\'\25)\2+\26-\2/\2\61\27\63\30\65\31\67\329\33;\34=\35?\36A\37C E"+
		"!G\"I#K$M%O&Q\'S(U)W*Y+[,]-_.a/c\60e\61g\62i\63k\64m\65o\66q\67s8u9w:"+
		"y;{<}\2\177\2\u0081\2\u0083\2\u0085=\u0087>\u0089?\u008b@\3\2\22\3\2\62"+
		";\b\2%&C\\aac|\u0393\u03ab\u03b3\u03cb\n\2%&\60\60\62;C\\aac|\u0393\u03ab"+
		"\u03b3\u03cb\t\2%&\62;C\\aac|\u0393\u03ab\u03b3\u03cb\4\2GGgg\4\2--//"+
		"\4\2,,\u00d9\u00d9\4\2\61\61\u00f9\u00f9\4\2##\u00ae\u00ae\4\2\u2229\u2229"+
		"\u22c2\u22c2\4\2\u222a\u222a\u22c3\u22c3\n\2))\61\61^^ddhhppttvv\5\2\62"+
		";CHch\5\2\2!))^^\4\2\f\f\17\17\5\2\13\f\17\17\"\"\2\u0211\2\3\3\2\2\2"+
		"\2\5\3\2\2\2\2\7\3\2\2\2\2\t\3\2\2\2\2\13\3\2\2\2\2\r\3\2\2\2\2\17\3\2"+
		"\2\2\2\21\3\2\2\2\2\23\3\2\2\2\2\25\3\2\2\2\2\27\3\2\2\2\2\31\3\2\2\2"+
		"\2\33\3\2\2\2\2\35\3\2\2\2\2\37\3\2\2\2\2!\3\2\2\2\2#\3\2\2\2\2%\3\2\2"+
		"\2\2\'\3\2\2\2\2+\3\2\2\2\2\61\3\2\2\2\2\63\3\2\2\2\2\65\3\2\2\2\2\67"+
		"\3\2\2\2\29\3\2\2\2\2;\3\2\2\2\2=\3\2\2\2\2?\3\2\2\2\2A\3\2\2\2\2C\3\2"+
		"\2\2\2E\3\2\2\2\2G\3\2\2\2\2I\3\2\2\2\2K\3\2\2\2\2M\3\2\2\2\2O\3\2\2\2"+
		"\2Q\3\2\2\2\2S\3\2\2\2\2U\3\2\2\2\2W\3\2\2\2\2Y\3\2\2\2\2[\3\2\2\2\2]"+
		"\3\2\2\2\2_\3\2\2\2\2a\3\2\2\2\2c\3\2\2\2\2e\3\2\2\2\2g\3\2\2\2\2i\3\2"+
		"\2\2\2k\3\2\2\2\2m\3\2\2\2\2o\3\2\2\2\2q\3\2\2\2\2s\3\2\2\2\2u\3\2\2\2"+
		"\2w\3\2\2\2\2y\3\2\2\2\2{\3\2\2\2\2\u0085\3\2\2\2\2\u0087\3\2\2\2\2\u0089"+
		"\3\2\2\2\2\u008b\3\2\2\2\3\u008d\3\2\2\2\5\u008f\3\2\2\2\7\u0091\3\2\2"+
		"\2\t\u0093\3\2\2\2\13\u0095\3\2\2\2\r\u0097\3\2\2\2\17\u0099\3\2\2\2\21"+
		"\u009b\3\2\2\2\23\u009e\3\2\2\2\25\u00a2\3\2\2\2\27\u00ab\3\2\2\2\31\u00be"+
		"\3\2\2\2\33\u00c0\3\2\2\2\35\u00c9\3\2\2\2\37\u00d0\3\2\2\2!\u00d5\3\2"+
		"\2\2#\u00e0\3\2\2\2%\u00e2\3\2\2\2\'\u00f2\3\2\2\2)\u0105\3\2\2\2+\u0107"+
		"\3\2\2\2-\u0110\3\2\2\2/\u0112\3\2\2\2\61\u0117\3\2\2\2\63\u0119\3\2\2"+
		"\2\65\u011b\3\2\2\2\67\u011d\3\2\2\29\u0120\3\2\2\2;\u0122\3\2\2\2=\u0125"+
		"\3\2\2\2?\u0127\3\2\2\2A\u0129\3\2\2\2C\u012b\3\2\2\2E\u0132\3\2\2\2G"+
		"\u0139\3\2\2\2I\u013e\3\2\2\2K\u0143\3\2\2\2M\u0145\3\2\2\2O\u0147\3\2"+
		"\2\2Q\u014c\3\2\2\2S\u0151\3\2\2\2U\u0153\3\2\2\2W\u0155\3\2\2\2Y\u0157"+
		"\3\2\2\2[\u0159\3\2\2\2]\u015b\3\2\2\2_\u015d\3\2\2\2a\u0160\3\2\2\2c"+
		"\u0162\3\2\2\2e\u0164\3\2\2\2g\u0168\3\2\2\2i\u016f\3\2\2\2k\u0176\3\2"+
		"\2\2m\u017d\3\2\2\2o\u0182\3\2\2\2q\u018a\3\2\2\2s\u0192\3\2\2\2u\u0199"+
		"\3\2\2\2w\u01a1\3\2\2\2y\u01a6\3\2\2\2{\u01ae\3\2\2\2}\u01b1\3\2\2\2\177"+
		"\u01b6\3\2\2\2\u0081\u01bc\3\2\2\2\u0083\u01be\3\2\2\2\u0085\u01c0\3\2"+
		"\2\2\u0087\u01ce\3\2\2\2\u0089\u01da\3\2\2\2\u008b\u01e0\3\2\2\2\u008d"+
		"\u008e\7}\2\2\u008e\4\3\2\2\2\u008f\u0090\7.\2\2\u0090\6\3\2\2\2\u0091"+
		"\u0092\7\177\2\2\u0092\b\3\2\2\2\u0093\u0094\7<\2\2\u0094\n\3\2\2\2\u0095"+
		"\u0096\7+\2\2\u0096\f\3\2\2\2\u0097\u0098\7\60\2\2\u0098\16\3\2\2\2\u0099"+
		"\u009a\7*\2\2\u009a\20\3\2\2\2\u009b\u009c\7=\2\2\u009c\22\3\2\2\2\u009d"+
		"\u009f\t\2\2\2\u009e\u009d\3\2\2\2\u009f\u00a0\3\2\2\2\u00a0\u009e\3\2"+
		"\2\2\u00a0\u00a1\3\2\2\2\u00a1\24\3\2\2\2\u00a2\u00a6\t\3\2\2\u00a3\u00a5"+
		"\t\4\2\2\u00a4\u00a3\3\2\2\2\u00a5\u00a8\3\2\2\2\u00a6\u00a4\3\2\2\2\u00a6"+
		"\u00a7\3\2\2\2\u00a7\26\3\2\2\2\u00a8\u00a6\3\2\2\2\u00a9\u00ac\5\37\20"+
		"\2\u00aa\u00ac\5!\21\2\u00ab\u00a9\3\2\2\2\u00ab\u00aa\3\2\2\2\u00ac\30"+
		"\3\2\2\2\u00ad\u00bf\7\u2256\2\2\u00ae\u00af\7<\2\2\u00af\u00bf\7?\2\2"+
		"\u00b0\u00b1\7-\2\2\u00b1\u00bf\7?\2\2\u00b2\u00b3\7/\2\2\u00b3\u00bf"+
		"\7?\2\2\u00b4\u00b5\5\63\32\2\u00b5\u00b6\7?\2\2\u00b6\u00bf\3\2\2\2\u00b7"+
		"\u00b8\5\65\33\2\u00b8\u00b9\7?\2\2\u00b9\u00bf\3\2\2\2\u00ba\u00bb\7"+
		"\'\2\2\u00bb\u00bf\7?\2\2\u00bc\u00bd\7`\2\2\u00bd\u00bf\7?\2\2\u00be"+
		"\u00ad\3\2\2\2\u00be\u00ae\3\2\2\2\u00be\u00b0\3\2\2\2\u00be\u00b2\3\2"+
		"\2\2\u00be\u00b4\3\2\2\2\u00be\u00b7\3\2\2\2\u00be\u00ba\3\2\2\2\u00be"+
		"\u00bc\3\2\2\2\u00bf\32\3\2\2\2\u00c0\u00c4\t\3\2\2\u00c1\u00c3\t\5\2"+
		"\2\u00c2\u00c1\3\2\2\2\u00c3\u00c6\3\2\2\2\u00c4\u00c2\3\2\2\2\u00c4\u00c5"+
		"\3\2\2\2\u00c5\u00c7\3\2\2\2\u00c6\u00c4\3\2\2\2\u00c7\u00c8\7*\2\2\u00c8"+
		"\34\3\2\2\2\u00c9\u00ce\7B\2\2\u00ca\u00cf\5)\25\2\u00cb\u00cc\5\33\16"+
		"\2\u00cc\u00cd\7+\2\2\u00cd\u00cf\3\2\2\2\u00ce\u00ca\3\2\2\2\u00ce\u00cb"+
		"\3\2\2\2\u00cf\36\3\2\2\2\u00d0\u00d1\7v\2\2\u00d1\u00d2\7t\2\2\u00d2"+
		"\u00d3\7w\2\2\u00d3\u00d4\7g\2\2\u00d4 \3\2\2\2\u00d5\u00d6\7h\2\2\u00d6"+
		"\u00d7\7c\2\2\u00d7\u00d8\7n\2\2\u00d8\u00d9\7u\2\2\u00d9\u00da\7g\2\2"+
		"\u00da\"\3\2\2\2\u00db\u00dc\7p\2\2\u00dc\u00dd\7w\2\2\u00dd\u00de\7n"+
		"\2\2\u00de\u00e1\7n\2\2\u00df\u00e1\7\u2207\2\2\u00e0\u00db\3\2\2\2\u00e0"+
		"\u00df\3\2\2\2\u00e1$\3\2\2\2\u00e2\u00e7\7)\2\2\u00e3\u00e6\5}?\2\u00e4"+
		"\u00e6\5\u0083B\2\u00e5\u00e3\3\2\2\2\u00e5\u00e4\3\2\2\2\u00e6\u00e9"+
		"\3\2\2\2\u00e7\u00e8\3\2\2\2\u00e7\u00e5\3\2\2\2\u00e8\u00ea\3\2\2\2\u00e9"+
		"\u00e7\3\2\2\2\u00ea\u00eb\7)\2\2\u00eb&\3\2\2\2\u00ec\u00ed\5\23\n\2"+
		"\u00ed\u00ee\7\60\2\2\u00ee\u00ef\5\23\n\2\u00ef\u00f3\3\2\2\2\u00f0\u00f1"+
		"\7\60\2\2\u00f1\u00f3\5\23\n\2\u00f2\u00ec\3\2\2\2\u00f2\u00f0\3\2\2\2"+
		"\u00f3(\3\2\2\2\u00f4\u0106\5\63\32\2\u00f5\u0106\5\65\33\2\u00f6\u0106"+
		"\59\35\2\u00f7\u0106\5=\37\2\u00f8\u0106\5? \2\u00f9\u0106\5E#\2\u00fa"+
		"\u0106\5A!\2\u00fb\u0106\5O(\2\u00fc\u0106\5E#\2\u00fd\u0106\5G$\2\u00fe"+
		"\u0106\5I%\2\u00ff\u0106\5K&\2\u0100\u0106\5Q)\2\u0101\u0106\5S*\2\u0102"+
		"\u0106\5W,\2\u0103\u0106\5Y-\2\u0104\u0106\5M\'\2\u0105\u00f4\3\2\2\2"+
		"\u0105\u00f5\3\2\2\2\u0105\u00f6\3\2\2\2\u0105\u00f7\3\2\2\2\u0105\u00f8"+
		"\3\2\2\2\u0105\u00f9\3\2\2\2\u0105\u00fa\3\2\2\2\u0105\u00fb\3\2\2\2\u0105"+
		"\u00fc\3\2\2\2\u0105\u00fd\3\2\2\2\u0105\u00fe\3\2\2\2\u0105\u00ff\3\2"+
		"\2\2\u0105\u0100\3\2\2\2\u0105\u0101\3\2\2\2\u0105\u0102\3\2\2\2\u0105"+
		"\u0103\3\2\2\2\u0105\u0104\3\2\2\2\u0106*\3\2\2\2\u0107\u010e\5\'\24\2"+
		"\u0108\u010a\5-\27\2\u0109\u010b\5/\30\2\u010a\u0109\3\2\2\2\u010a\u010b"+
		"\3\2\2\2\u010b\u010c\3\2\2\2\u010c\u010d\5\23\n\2\u010d\u010f\3\2\2\2"+
		"\u010e\u0108\3\2\2\2\u010e\u010f\3\2\2\2\u010f,\3\2\2\2\u0110\u0111\t"+
		"\6\2\2\u0111.\3\2\2\2\u0112\u0113\t\7\2\2\u0113\60\3\2\2\2\u0114\u0115"+
		"\7/\2\2\u0115\u0118\7@\2\2\u0116\u0118\7\u2194\2\2\u0117\u0114\3\2\2\2"+
		"\u0117\u0116\3\2\2\2\u0118\62\3\2\2\2\u0119\u011a\t\b\2\2\u011a\64\3\2"+
		"\2\2\u011b\u011c\t\t\2\2\u011c\66\3\2\2\2\u011d\u011e\7-\2\2\u011e\u011f"+
		"\7-\2\2\u011f8\3\2\2\2\u0120\u0121\7-\2\2\u0121:\3\2\2\2\u0122\u0123\7"+
		"/\2\2\u0123\u0124\7/\2\2\u0124<\3\2\2\2\u0125\u0126\7/\2\2\u0126>\3\2"+
		"\2\2\u0127\u0128\7>\2\2\u0128@\3\2\2\2\u0129\u012a\7@\2\2\u012aB\3\2\2"+
		"\2\u012b\u012c\7?\2\2\u012cD\3\2\2\2\u012d\u012e\7>\2\2\u012e\u0133\7"+
		"?\2\2\u012f\u0133\7\u2266\2\2\u0130\u0131\7?\2\2\u0131\u0133\7>\2\2\u0132"+
		"\u012d\3\2\2\2\u0132\u012f\3\2\2\2\u0132\u0130\3\2\2\2\u0133F\3\2\2\2"+
		"\u0134\u0135\7@\2\2\u0135\u013a\7?\2\2\u0136\u013a\7\u2267\2\2\u0137\u0138"+
		"\7?\2\2\u0138\u013a\7@\2\2\u0139\u0134\3\2\2\2\u0139\u0136\3\2\2\2\u0139"+
		"\u0137\3\2\2\2\u013aH\3\2\2\2\u013b\u013c\7?\2\2\u013c\u013f\7?\2\2\u013d"+
		"\u013f\7\u2263\2\2\u013e\u013b\3\2\2\2\u013e\u013d\3\2\2\2\u013fJ\3\2"+
		"\2\2\u0140\u0141\7#\2\2\u0141\u0144\7?\2\2\u0142\u0144\7\u2262\2\2\u0143"+
		"\u0140\3\2\2\2\u0143\u0142\3\2\2\2\u0144L\3\2\2\2\u0145\u0146\t\n\2\2"+
		"\u0146N\3\2\2\2\u0147\u0148\7`\2\2\u0148P\3\2\2\2\u0149\u014a\7(\2\2\u014a"+
		"\u014d\7(\2\2\u014b\u014d\t\13\2\2\u014c\u0149\3\2\2\2\u014c\u014b\3\2"+
		"\2\2\u014dR\3\2\2\2\u014e\u014f\7~\2\2\u014f\u0152\7~\2\2\u0150\u0152"+
		"\t\f\2\2\u0151\u014e\3\2\2\2\u0151\u0150\3\2\2\2\u0152T\3\2\2\2\u0153"+
		"\u0154\7b\2\2\u0154V\3\2\2\2\u0155\u0156\7\'\2\2\u0156X\3\2\2\2\u0157"+
		"\u0158\7\u0080\2\2\u0158Z\3\2\2\2\u0159\u015a\7^\2\2\u015a\\\3\2\2\2\u015b"+
		"\u015c\7~\2\2\u015c^\3\2\2\2\u015d\u015e\7\u0080\2\2\u015e\u015f\7~\2"+
		"\2\u015f`\3\2\2\2\u0160\u0161\7_\2\2\u0161b\3\2\2\2\u0162\u0163\7]\2\2"+
		"\u0163d\3\2\2\2\u0164\u0165\7k\2\2\u0165\u0166\7h\2\2\u0166\u0167\7]\2"+
		"\2\u0167f\3\2\2\2\u0168\u0169\7_\2\2\u0169\u016a\7v\2\2\u016a\u016b\7"+
		"j\2\2\u016b\u016c\7g\2\2\u016c\u016d\7p\2\2\u016d\u016e\7]\2\2\u016eh"+
		"\3\2\2\2\u016f\u0170\7_\2\2\u0170\u0171\7g\2\2\u0171\u0172\7n\2\2\u0172"+
		"\u0173\7u\2\2\u0173\u0174\7g\2\2\u0174\u0175\7]\2\2\u0175j\3\2\2\2\u0176"+
		"\u0177\7y\2\2\u0177\u0178\7j\2\2\u0178\u0179\7k\2\2\u0179\u017a\7n\2\2"+
		"\u017a\u017b\7g\2\2\u017b\u017c\7]\2\2\u017cl\3\2\2\2\u017d\u017e\7_\2"+
		"\2\u017e\u017f\7f\2\2\u017f\u0180\7q\2\2\u0180\u0181\7]\2\2\u0181n\3\2"+
		"\2\2\u0182\u0183\7u\2\2\u0183\u0184\7y\2\2\u0184\u0185\7k\2\2\u0185\u0186"+
		"\7v\2\2\u0186\u0187\7e\2\2\u0187\u0188\7j\2\2\u0188\u0189\7]\2\2\u0189"+
		"p\3\2\2\2\u018a\u018b\7f\2\2\u018b\u018c\7g\2\2\u018c\u018d\7h\2\2\u018d"+
		"\u018e\7k\2\2\u018e\u018f\7p\2\2\u018f\u0190\7g\2\2\u0190\u0191\7]\2\2"+
		"\u0191r\3\2\2\2\u0192\u0193\7_\2\2\u0193\u0194\7d\2\2\u0194\u0195\7q\2"+
		"\2\u0195\u0196\7f\2\2\u0196\u0197\7{\2\2\u0197\u0198\7]\2\2\u0198t\3\2"+
		"\2\2\u0199\u019a\7o\2\2\u019a\u019b\7q\2\2\u019b\u019c\7f\2\2\u019c\u019d"+
		"\7w\2\2\u019d\u019e\7n\2\2\u019e\u019f\7g\2\2\u019f\u01a0\7]\2\2\u01a0"+
		"v\3\2\2\2\u01a1\u01a2\7v\2\2\u01a2\u01a3\7t\2\2\u01a3\u01a4\7{\2\2\u01a4"+
		"\u01a5\7]\2\2\u01a5x\3\2\2\2\u01a6\u01a7\7_\2\2\u01a7\u01a8\7e\2\2\u01a8"+
		"\u01a9\7c\2\2\u01a9\u01aa\7v\2\2\u01aa\u01ab\7e\2\2\u01ab\u01ac\7j\2\2"+
		"\u01ac\u01ad\7]\2\2\u01adz\3\2\2\2\u01ae\u01af\7_\2\2\u01af\u01b0\7]\2"+
		"\2\u01b0|\3\2\2\2\u01b1\u01b4\7^\2\2\u01b2\u01b5\t\r\2\2\u01b3\u01b5\5"+
		"\177@\2\u01b4\u01b2\3\2\2\2\u01b4\u01b3\3\2\2\2\u01b5~\3\2\2\2\u01b6\u01b7"+
		"\7w\2\2\u01b7\u01b8\5\u0081A\2\u01b8\u01b9\5\u0081A\2\u01b9\u01ba\5\u0081"+
		"A\2\u01ba\u01bb\5\u0081A\2\u01bb\u0080\3\2\2\2\u01bc\u01bd\t\16\2\2\u01bd"+
		"\u0082\3\2\2\2\u01be\u01bf\n\17\2\2\u01bf\u0084\3\2\2\2\u01c0\u01c1\7"+
		"\61\2\2\u01c1\u01c2\7,\2\2\u01c2\u01c6\3\2\2\2\u01c3\u01c5\13\2\2\2\u01c4"+
		"\u01c3\3\2\2\2\u01c5\u01c8\3\2\2\2\u01c6\u01c7\3\2\2\2\u01c6\u01c4\3\2"+
		"\2\2\u01c7\u01c9\3\2\2\2\u01c8\u01c6\3\2\2\2\u01c9\u01ca\7,\2\2\u01ca"+
		"\u01cb\7\61\2\2\u01cb\u01cc\3\2\2\2\u01cc\u01cd\bC\2\2\u01cd\u0086\3\2"+
		"\2\2\u01ce\u01cf\7\61\2\2\u01cf\u01d0\7\61\2\2\u01d0\u01d4\3\2\2\2\u01d1"+
		"\u01d3\n\20\2\2\u01d2\u01d1\3\2\2\2\u01d3\u01d6\3\2\2\2\u01d4\u01d2\3"+
		"\2\2\2\u01d4\u01d5\3\2\2\2\u01d5\u01d7\3\2\2\2\u01d6\u01d4\3\2\2\2\u01d7"+
		"\u01d8\bD\2\2\u01d8\u0088\3\2\2\2\u01d9\u01db\t\21\2\2\u01da\u01d9\3\2"+
		"\2\2\u01db\u01dc\3\2\2\2\u01dc\u01da\3\2\2\2\u01dc\u01dd\3\2\2\2\u01dd"+
		"\u01de\3\2\2\2\u01de\u01df\bE\2\2\u01df\u008a\3\2\2\2\u01e0\u01e1\7@\2"+
		"\2\u01e1\u01e2\7@\2\2\u01e2\u01e6\3\2\2\2\u01e3\u01e5\n\20\2\2\u01e4\u01e3"+
		"\3\2\2\2\u01e5\u01e8\3\2\2\2\u01e6\u01e4\3\2\2\2\u01e6\u01e7\3\2\2\2\u01e7"+
		"\u008c\3\2\2\2\u01e8\u01e6\3\2\2\2\34\2\u00a0\u00a6\u00ab\u00be\u00c4"+
		"\u00ce\u00e0\u00e5\u00e7\u00f2\u0105\u010a\u010e\u0117\u0132\u0139\u013e"+
		"\u0143\u014c\u0151\u01b4\u01c6\u01d4\u01dc\u01e6\3\b\2\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}