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
		NotEquals=34, RegexMatches=35, LogicalNot=36, Exponentiation=37, And=38, 
		Or=39, Backtick=40, Percent=41, Tilde=42, Backslash=43, Stile=44, TildeRight=45, 
		LeftBracket=46, RightBracket=47, LogicalIf=48, LogicalThen=49, LogicalElse=50, 
		WhileLoop=51, WhileDo=52, SwitchStatement=53, DefineStatement=54, BodyStatement=55, 
		ModuleStatement=56, TryStatement=57, CatchStatement=58, StatementConnector=59, 
		COMMENT=60, LINE_COMMENT=61, WS2=62, FDOC=63;
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
			"Null", "STRING", "Decimal", "AllOps", "FUNCTION_NAME", "SCIENTIFIC_NUMBER", 
			"E", "SIGN", "LambdaConnector", "Times", "Divide", "PlusPlus", "Plus", 
			"MinusMinus", "Minus", "LessThan", "GreaterThan", "SingleEqual", "LessEquals", 
			"MoreEquals", "Equals", "NotEquals", "RegexMatches", "LogicalNot", "Exponentiation", 
			"And", "Or", "Backtick", "Percent", "Tilde", "Backslash", "Stile", "TildeRight", 
			"LeftBracket", "RightBracket", "LogicalIf", "LogicalThen", "LogicalElse", 
			"WhileLoop", "WhileDo", "SwitchStatement", "DefineStatement", "BodyStatement", 
			"ModuleStatement", "TryStatement", "CatchStatement", "StatementConnector", 
			"ESC", "UNICODE", "HEX", "SAFECODEPOINT", "COMMENT", "LINE_COMMENT", 
			"WS2", "FDOC"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'{'", "','", "'}'", "':'", "')'", "'.'", "'('", "';'", null, null, 
			null, null, null, null, "'true'", "'false'", null, null, null, null, 
			null, null, null, "'++'", "'+'", "'--'", "'-'", "'<'", "'>'", "'='", 
			null, null, null, null, null, null, "'^'", null, null, "'`'", "'%'", 
			"'~'", "'\\'", "'|'", "'~|'", "']'", "'['", "'if['", "']then['", "']else['", 
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
			"SingleEqual", "LessEquals", "MoreEquals", "Equals", "NotEquals", "RegexMatches", 
			"LogicalNot", "Exponentiation", "And", "Or", "Backtick", "Percent", "Tilde", 
			"Backslash", "Stile", "TildeRight", "LeftBracket", "RightBracket", "LogicalIf", 
			"LogicalThen", "LogicalElse", "WhileLoop", "WhileDo", "SwitchStatement", 
			"DefineStatement", "BodyStatement", "ModuleStatement", "TryStatement", 
			"CatchStatement", "StatementConnector", "COMMENT", "LINE_COMMENT", "WS2", 
			"FDOC"
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
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2A\u01f7\b\1\4\2\t"+
		"\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13"+
		"\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31\t\31"+
		"\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4 \t \4!"+
		"\t!\4\"\t\"\4#\t#\4$\t$\4%\t%\4&\t&\4\'\t\'\4(\t(\4)\t)\4*\t*\4+\t+\4"+
		",\t,\4-\t-\4.\t.\4/\t/\4\60\t\60\4\61\t\61\4\62\t\62\4\63\t\63\4\64\t"+
		"\64\4\65\t\65\4\66\t\66\4\67\t\67\48\t8\49\t9\4:\t:\4;\t;\4<\t<\4=\t="+
		"\4>\t>\4?\t?\4@\t@\4A\tA\4B\tB\4C\tC\4D\tD\4E\tE\4F\tF\4G\tG\4H\tH\3\2"+
		"\3\2\3\3\3\3\3\4\3\4\3\5\3\5\3\6\3\6\3\7\3\7\3\b\3\b\3\t\3\t\3\n\6\n\u00a3"+
		"\n\n\r\n\16\n\u00a4\3\13\3\13\7\13\u00a9\n\13\f\13\16\13\u00ac\13\13\3"+
		"\f\3\f\5\f\u00b0\n\f\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3"+
		"\r\3\r\3\r\3\r\3\r\3\r\3\r\3\r\5\r\u00c6\n\r\3\16\3\16\3\16\3\17\3\17"+
		"\3\17\3\17\3\17\3\17\5\17\u00d1\n\17\3\20\3\20\3\20\3\20\3\20\3\21\3\21"+
		"\3\21\3\21\3\21\3\21\3\22\3\22\3\22\3\22\3\22\5\22\u00e3\n\22\3\23\3\23"+
		"\3\23\7\23\u00e8\n\23\f\23\16\23\u00eb\13\23\3\23\3\23\3\24\3\24\3\24"+
		"\3\24\3\24\3\24\5\24\u00f5\n\24\3\25\3\25\3\25\3\25\3\25\3\25\3\25\3\25"+
		"\3\25\3\25\3\25\3\25\3\25\3\25\3\25\3\25\3\25\5\25\u0108\n\25\3\26\3\26"+
		"\7\26\u010c\n\26\f\26\16\26\u010f\13\26\3\27\3\27\3\27\5\27\u0114\n\27"+
		"\3\27\3\27\5\27\u0118\n\27\3\30\3\30\3\31\3\31\3\32\3\32\3\32\5\32\u0121"+
		"\n\32\3\33\3\33\3\34\3\34\3\35\3\35\3\35\3\36\3\36\3\37\3\37\3\37\3 \3"+
		" \3!\3!\3\"\3\"\3#\3#\3$\3$\3$\3$\3$\5$\u013c\n$\3%\3%\3%\3%\3%\5%\u0143"+
		"\n%\3&\3&\3&\5&\u0148\n&\3\'\3\'\3\'\5\'\u014d\n\'\3(\3(\3(\5(\u0152\n"+
		"(\3)\3)\3*\3*\3+\3+\3+\5+\u015b\n+\3,\3,\3,\5,\u0160\n,\3-\3-\3.\3.\3"+
		"/\3/\3\60\3\60\3\61\3\61\3\62\3\62\3\62\3\63\3\63\3\64\3\64\3\65\3\65"+
		"\3\65\3\65\3\66\3\66\3\66\3\66\3\66\3\66\3\66\3\67\3\67\3\67\3\67\3\67"+
		"\3\67\3\67\38\38\38\38\38\38\38\39\39\39\39\39\3:\3:\3:\3:\3:\3:\3:\3"+
		":\3;\3;\3;\3;\3;\3;\3;\3;\3<\3<\3<\3<\3<\3<\3<\3=\3=\3=\3=\3=\3=\3=\3"+
		"=\3>\3>\3>\3>\3>\3?\3?\3?\3?\3?\3?\3?\3?\3@\3@\3@\3A\3A\3A\5A\u01c3\n"+
		"A\3B\3B\3B\3B\3B\3B\3C\3C\3D\3D\3E\3E\3E\3E\7E\u01d3\nE\fE\16E\u01d6\13"+
		"E\3E\3E\3E\3E\3E\3F\3F\3F\3F\7F\u01e1\nF\fF\16F\u01e4\13F\3F\3F\3G\6G"+
		"\u01e9\nG\rG\16G\u01ea\3G\3G\3H\3H\3H\3H\7H\u01f3\nH\fH\16H\u01f6\13H"+
		"\4\u00e9\u01d4\2I\3\3\5\4\7\5\t\6\13\7\r\b\17\t\21\n\23\13\25\f\27\r\31"+
		"\16\33\17\35\20\37\21!\22#\23%\24\'\25)\2+\2-\26/\2\61\2\63\27\65\30\67"+
		"\319\32;\33=\34?\35A\36C\37E G!I\"K#M$O%Q&S\'U(W)Y*[+],_-a.c/e\60g\61"+
		"i\62k\63m\64o\65q\66s\67u8w9y:{;}<\177=\u0081\2\u0083\2\u0085\2\u0087"+
		"\2\u0089>\u008b?\u008d@\u008fA\3\2\22\3\2\62;\b\2%&C\\aac|\u0393\u03ab"+
		"\u03b3\u03cb\n\2%&\60\60\62;C\\aac|\u0393\u03ab\u03b3\u03cb\t\2%&\62;"+
		"C\\aac|\u0393\u03ab\u03b3\u03cb\4\2GGgg\4\2--//\4\2,,\u00d9\u00d9\4\2"+
		"\61\61\u00f9\u00f9\4\2##\u00ae\u00ae\4\2\u2229\u2229\u22c2\u22c2\4\2\u222a"+
		"\u222a\u22c3\u22c3\n\2))\61\61^^ddhhppttvv\5\2\62;CHch\5\2\2!))^^\4\2"+
		"\f\f\17\17\5\2\13\f\17\17\"\"\2\u0222\2\3\3\2\2\2\2\5\3\2\2\2\2\7\3\2"+
		"\2\2\2\t\3\2\2\2\2\13\3\2\2\2\2\r\3\2\2\2\2\17\3\2\2\2\2\21\3\2\2\2\2"+
		"\23\3\2\2\2\2\25\3\2\2\2\2\27\3\2\2\2\2\31\3\2\2\2\2\33\3\2\2\2\2\35\3"+
		"\2\2\2\2\37\3\2\2\2\2!\3\2\2\2\2#\3\2\2\2\2%\3\2\2\2\2\'\3\2\2\2\2-\3"+
		"\2\2\2\2\63\3\2\2\2\2\65\3\2\2\2\2\67\3\2\2\2\29\3\2\2\2\2;\3\2\2\2\2"+
		"=\3\2\2\2\2?\3\2\2\2\2A\3\2\2\2\2C\3\2\2\2\2E\3\2\2\2\2G\3\2\2\2\2I\3"+
		"\2\2\2\2K\3\2\2\2\2M\3\2\2\2\2O\3\2\2\2\2Q\3\2\2\2\2S\3\2\2\2\2U\3\2\2"+
		"\2\2W\3\2\2\2\2Y\3\2\2\2\2[\3\2\2\2\2]\3\2\2\2\2_\3\2\2\2\2a\3\2\2\2\2"+
		"c\3\2\2\2\2e\3\2\2\2\2g\3\2\2\2\2i\3\2\2\2\2k\3\2\2\2\2m\3\2\2\2\2o\3"+
		"\2\2\2\2q\3\2\2\2\2s\3\2\2\2\2u\3\2\2\2\2w\3\2\2\2\2y\3\2\2\2\2{\3\2\2"+
		"\2\2}\3\2\2\2\2\177\3\2\2\2\2\u0089\3\2\2\2\2\u008b\3\2\2\2\2\u008d\3"+
		"\2\2\2\2\u008f\3\2\2\2\3\u0091\3\2\2\2\5\u0093\3\2\2\2\7\u0095\3\2\2\2"+
		"\t\u0097\3\2\2\2\13\u0099\3\2\2\2\r\u009b\3\2\2\2\17\u009d\3\2\2\2\21"+
		"\u009f\3\2\2\2\23\u00a2\3\2\2\2\25\u00a6\3\2\2\2\27\u00af\3\2\2\2\31\u00c5"+
		"\3\2\2\2\33\u00c7\3\2\2\2\35\u00ca\3\2\2\2\37\u00d2\3\2\2\2!\u00d7\3\2"+
		"\2\2#\u00e2\3\2\2\2%\u00e4\3\2\2\2\'\u00f4\3\2\2\2)\u0107\3\2\2\2+\u0109"+
		"\3\2\2\2-\u0110\3\2\2\2/\u0119\3\2\2\2\61\u011b\3\2\2\2\63\u0120\3\2\2"+
		"\2\65\u0122\3\2\2\2\67\u0124\3\2\2\29\u0126\3\2\2\2;\u0129\3\2\2\2=\u012b"+
		"\3\2\2\2?\u012e\3\2\2\2A\u0130\3\2\2\2C\u0132\3\2\2\2E\u0134\3\2\2\2G"+
		"\u013b\3\2\2\2I\u0142\3\2\2\2K\u0147\3\2\2\2M\u014c\3\2\2\2O\u0151\3\2"+
		"\2\2Q\u0153\3\2\2\2S\u0155\3\2\2\2U\u015a\3\2\2\2W\u015f\3\2\2\2Y\u0161"+
		"\3\2\2\2[\u0163\3\2\2\2]\u0165\3\2\2\2_\u0167\3\2\2\2a\u0169\3\2\2\2c"+
		"\u016b\3\2\2\2e\u016e\3\2\2\2g\u0170\3\2\2\2i\u0172\3\2\2\2k\u0176\3\2"+
		"\2\2m\u017d\3\2\2\2o\u0184\3\2\2\2q\u018b\3\2\2\2s\u0190\3\2\2\2u\u0198"+
		"\3\2\2\2w\u01a0\3\2\2\2y\u01a7\3\2\2\2{\u01af\3\2\2\2}\u01b4\3\2\2\2\177"+
		"\u01bc\3\2\2\2\u0081\u01bf\3\2\2\2\u0083\u01c4\3\2\2\2\u0085\u01ca\3\2"+
		"\2\2\u0087\u01cc\3\2\2\2\u0089\u01ce\3\2\2\2\u008b\u01dc\3\2\2\2\u008d"+
		"\u01e8\3\2\2\2\u008f\u01ee\3\2\2\2\u0091\u0092\7}\2\2\u0092\4\3\2\2\2"+
		"\u0093\u0094\7.\2\2\u0094\6\3\2\2\2\u0095\u0096\7\177\2\2\u0096\b\3\2"+
		"\2\2\u0097\u0098\7<\2\2\u0098\n\3\2\2\2\u0099\u009a\7+\2\2\u009a\f\3\2"+
		"\2\2\u009b\u009c\7\60\2\2\u009c\16\3\2\2\2\u009d\u009e\7*\2\2\u009e\20"+
		"\3\2\2\2\u009f\u00a0\7=\2\2\u00a0\22\3\2\2\2\u00a1\u00a3\t\2\2\2\u00a2"+
		"\u00a1\3\2\2\2\u00a3\u00a4\3\2\2\2\u00a4\u00a2\3\2\2\2\u00a4\u00a5\3\2"+
		"\2\2\u00a5\24\3\2\2\2\u00a6\u00aa\t\3\2\2\u00a7\u00a9\t\4\2\2\u00a8\u00a7"+
		"\3\2\2\2\u00a9\u00ac\3\2\2\2\u00aa\u00a8\3\2\2\2\u00aa\u00ab\3\2\2\2\u00ab"+
		"\26\3\2\2\2\u00ac\u00aa\3\2\2\2\u00ad\u00b0\5\37\20\2\u00ae\u00b0\5!\21"+
		"\2\u00af\u00ad\3\2\2\2\u00af\u00ae\3\2\2\2\u00b0\30\3\2\2\2\u00b1\u00c6"+
		"\7\u2256\2\2\u00b2\u00b3\7<\2\2\u00b3\u00c6\7?\2\2\u00b4\u00c6\7\u2257"+
		"\2\2\u00b5\u00b6\7?\2\2\u00b6\u00c6\7<\2\2\u00b7\u00b8\7-\2\2\u00b8\u00c6"+
		"\7?\2\2\u00b9\u00ba\7/\2\2\u00ba\u00c6\7?\2\2\u00bb\u00bc\5\65\33\2\u00bc"+
		"\u00bd\7?\2\2\u00bd\u00c6\3\2\2\2\u00be\u00bf\5\67\34\2\u00bf\u00c0\7"+
		"?\2\2\u00c0\u00c6\3\2\2\2\u00c1\u00c2\7\'\2\2\u00c2\u00c6\7?\2\2\u00c3"+
		"\u00c4\7`\2\2\u00c4\u00c6\7?\2\2\u00c5\u00b1\3\2\2\2\u00c5\u00b2\3\2\2"+
		"\2\u00c5\u00b4\3\2\2\2\u00c5\u00b5\3\2\2\2\u00c5\u00b7\3\2\2\2\u00c5\u00b9"+
		"\3\2\2\2\u00c5\u00bb\3\2\2\2\u00c5\u00be\3\2\2\2\u00c5\u00c1\3\2\2\2\u00c5"+
		"\u00c3\3\2\2\2\u00c6\32\3\2\2\2\u00c7\u00c8\5+\26\2\u00c8\u00c9\7*\2\2"+
		"\u00c9\34\3\2\2\2\u00ca\u00d0\7B\2\2\u00cb\u00d1\5)\25\2\u00cc\u00d1\5"+
		"+\26\2\u00cd\u00ce\5\33\16\2\u00ce\u00cf\7+\2\2\u00cf\u00d1\3\2\2\2\u00d0"+
		"\u00cb\3\2\2\2\u00d0\u00cc\3\2\2\2\u00d0\u00cd\3\2\2\2\u00d1\36\3\2\2"+
		"\2\u00d2\u00d3\7v\2\2\u00d3\u00d4\7t\2\2\u00d4\u00d5\7w\2\2\u00d5\u00d6"+
		"\7g\2\2\u00d6 \3\2\2\2\u00d7\u00d8\7h\2\2\u00d8\u00d9\7c\2\2\u00d9\u00da"+
		"\7n\2\2\u00da\u00db\7u\2\2\u00db\u00dc\7g\2\2\u00dc\"\3\2\2\2\u00dd\u00de"+
		"\7p\2\2\u00de\u00df\7w\2\2\u00df\u00e0\7n\2\2\u00e0\u00e3\7n\2\2\u00e1"+
		"\u00e3\7\u2207\2\2\u00e2\u00dd\3\2\2\2\u00e2\u00e1\3\2\2\2\u00e3$\3\2"+
		"\2\2\u00e4\u00e9\7)\2\2\u00e5\u00e8\5\u0081A\2\u00e6\u00e8\5\u0087D\2"+
		"\u00e7\u00e5\3\2\2\2\u00e7\u00e6\3\2\2\2\u00e8\u00eb\3\2\2\2\u00e9\u00ea"+
		"\3\2\2\2\u00e9\u00e7\3\2\2\2\u00ea\u00ec\3\2\2\2\u00eb\u00e9\3\2\2\2\u00ec"+
		"\u00ed\7)\2\2\u00ed&\3\2\2\2\u00ee\u00ef\5\23\n\2\u00ef\u00f0\7\60\2\2"+
		"\u00f0\u00f1\5\23\n\2\u00f1\u00f5\3\2\2\2\u00f2\u00f3\7\60\2\2\u00f3\u00f5"+
		"\5\23\n\2\u00f4\u00ee\3\2\2\2\u00f4\u00f2\3\2\2\2\u00f5(\3\2\2\2\u00f6"+
		"\u0108\5\65\33\2\u00f7\u0108\5\67\34\2\u00f8\u0108\5;\36\2\u00f9\u0108"+
		"\5? \2\u00fa\u0108\5A!\2\u00fb\u0108\5G$\2\u00fc\u0108\5C\"\2\u00fd\u0108"+
		"\5S*\2\u00fe\u0108\5G$\2\u00ff\u0108\5I%\2\u0100\u0108\5K&\2\u0101\u0108"+
		"\5M\'\2\u0102\u0108\5U+\2\u0103\u0108\5W,\2\u0104\u0108\5[.\2\u0105\u0108"+
		"\5]/\2\u0106\u0108\5Q)\2\u0107\u00f6\3\2\2\2\u0107\u00f7\3\2\2\2\u0107"+
		"\u00f8\3\2\2\2\u0107\u00f9\3\2\2\2\u0107\u00fa\3\2\2\2\u0107\u00fb\3\2"+
		"\2\2\u0107\u00fc\3\2\2\2\u0107\u00fd\3\2\2\2\u0107\u00fe\3\2\2\2\u0107"+
		"\u00ff\3\2\2\2\u0107\u0100\3\2\2\2\u0107\u0101\3\2\2\2\u0107\u0102\3\2"+
		"\2\2\u0107\u0103\3\2\2\2\u0107\u0104\3\2\2\2\u0107\u0105\3\2\2\2\u0107"+
		"\u0106\3\2\2\2\u0108*\3\2\2\2\u0109\u010d\t\3\2\2\u010a\u010c\t\5\2\2"+
		"\u010b\u010a\3\2\2\2\u010c\u010f\3\2\2\2\u010d\u010b\3\2\2\2\u010d\u010e"+
		"\3\2\2\2\u010e,\3\2\2\2\u010f\u010d\3\2\2\2\u0110\u0117\5\'\24\2\u0111"+
		"\u0113\5/\30\2\u0112\u0114\5\61\31\2\u0113\u0112\3\2\2\2\u0113\u0114\3"+
		"\2\2\2\u0114\u0115\3\2\2\2\u0115\u0116\5\23\n\2\u0116\u0118\3\2\2\2\u0117"+
		"\u0111\3\2\2\2\u0117\u0118\3\2\2\2\u0118.\3\2\2\2\u0119\u011a\t\6\2\2"+
		"\u011a\60\3\2\2\2\u011b\u011c\t\7\2\2\u011c\62\3\2\2\2\u011d\u011e\7/"+
		"\2\2\u011e\u0121\7@\2\2\u011f\u0121\7\u2194\2\2\u0120\u011d\3\2\2\2\u0120"+
		"\u011f\3\2\2\2\u0121\64\3\2\2\2\u0122\u0123\t\b\2\2\u0123\66\3\2\2\2\u0124"+
		"\u0125\t\t\2\2\u01258\3\2\2\2\u0126\u0127\7-\2\2\u0127\u0128\7-\2\2\u0128"+
		":\3\2\2\2\u0129\u012a\7-\2\2\u012a<\3\2\2\2\u012b\u012c\7/\2\2\u012c\u012d"+
		"\7/\2\2\u012d>\3\2\2\2\u012e\u012f\7/\2\2\u012f@\3\2\2\2\u0130\u0131\7"+
		">\2\2\u0131B\3\2\2\2\u0132\u0133\7@\2\2\u0133D\3\2\2\2\u0134\u0135\7?"+
		"\2\2\u0135F\3\2\2\2\u0136\u0137\7>\2\2\u0137\u013c\7?\2\2\u0138\u013c"+
		"\7\u2266\2\2\u0139\u013a\7?\2\2\u013a\u013c\7>\2\2\u013b\u0136\3\2\2\2"+
		"\u013b\u0138\3\2\2\2\u013b\u0139\3\2\2\2\u013cH\3\2\2\2\u013d\u013e\7"+
		"@\2\2\u013e\u0143\7?\2\2\u013f\u0143\7\u2267\2\2\u0140\u0141\7?\2\2\u0141"+
		"\u0143\7@\2\2\u0142\u013d\3\2\2\2\u0142\u013f\3\2\2\2\u0142\u0140\3\2"+
		"\2\2\u0143J\3\2\2\2\u0144\u0145\7?\2\2\u0145\u0148\7?\2\2\u0146\u0148"+
		"\7\u2263\2\2\u0147\u0144\3\2\2\2\u0147\u0146\3\2\2\2\u0148L\3\2\2\2\u0149"+
		"\u014a\7#\2\2\u014a\u014d\7?\2\2\u014b\u014d\7\u2262\2\2\u014c\u0149\3"+
		"\2\2\2\u014c\u014b\3\2\2\2\u014dN\3\2\2\2\u014e\u014f\7?\2\2\u014f\u0152"+
		"\7\u0080\2\2\u0150\u0152\7\u224a\2\2\u0151\u014e\3\2\2\2\u0151\u0150\3"+
		"\2\2\2\u0152P\3\2\2\2\u0153\u0154\t\n\2\2\u0154R\3\2\2\2\u0155\u0156\7"+
		"`\2\2\u0156T\3\2\2\2\u0157\u0158\7(\2\2\u0158\u015b\7(\2\2\u0159\u015b"+
		"\t\13\2\2\u015a\u0157\3\2\2\2\u015a\u0159\3\2\2\2\u015bV\3\2\2\2\u015c"+
		"\u015d\7~\2\2\u015d\u0160\7~\2\2\u015e\u0160\t\f\2\2\u015f\u015c\3\2\2"+
		"\2\u015f\u015e\3\2\2\2\u0160X\3\2\2\2\u0161\u0162\7b\2\2\u0162Z\3\2\2"+
		"\2\u0163\u0164\7\'\2\2\u0164\\\3\2\2\2\u0165\u0166\7\u0080\2\2\u0166^"+
		"\3\2\2\2\u0167\u0168\7^\2\2\u0168`\3\2\2\2\u0169\u016a\7~\2\2\u016ab\3"+
		"\2\2\2\u016b\u016c\7\u0080\2\2\u016c\u016d\7~\2\2\u016dd\3\2\2\2\u016e"+
		"\u016f\7_\2\2\u016ff\3\2\2\2\u0170\u0171\7]\2\2\u0171h\3\2\2\2\u0172\u0173"+
		"\7k\2\2\u0173\u0174\7h\2\2\u0174\u0175\7]\2\2\u0175j\3\2\2\2\u0176\u0177"+
		"\7_\2\2\u0177\u0178\7v\2\2\u0178\u0179\7j\2\2\u0179\u017a\7g\2\2\u017a"+
		"\u017b\7p\2\2\u017b\u017c\7]\2\2\u017cl\3\2\2\2\u017d\u017e\7_\2\2\u017e"+
		"\u017f\7g\2\2\u017f\u0180\7n\2\2\u0180\u0181\7u\2\2\u0181\u0182\7g\2\2"+
		"\u0182\u0183\7]\2\2\u0183n\3\2\2\2\u0184\u0185\7y\2\2\u0185\u0186\7j\2"+
		"\2\u0186\u0187\7k\2\2\u0187\u0188\7n\2\2\u0188\u0189\7g\2\2\u0189\u018a"+
		"\7]\2\2\u018ap\3\2\2\2\u018b\u018c\7_\2\2\u018c\u018d\7f\2\2\u018d\u018e"+
		"\7q\2\2\u018e\u018f\7]\2\2\u018fr\3\2\2\2\u0190\u0191\7u\2\2\u0191\u0192"+
		"\7y\2\2\u0192\u0193\7k\2\2\u0193\u0194\7v\2\2\u0194\u0195\7e\2\2\u0195"+
		"\u0196\7j\2\2\u0196\u0197\7]\2\2\u0197t\3\2\2\2\u0198\u0199\7f\2\2\u0199"+
		"\u019a\7g\2\2\u019a\u019b\7h\2\2\u019b\u019c\7k\2\2\u019c\u019d\7p\2\2"+
		"\u019d\u019e\7g\2\2\u019e\u019f\7]\2\2\u019fv\3\2\2\2\u01a0\u01a1\7_\2"+
		"\2\u01a1\u01a2\7d\2\2\u01a2\u01a3\7q\2\2\u01a3\u01a4\7f\2\2\u01a4\u01a5"+
		"\7{\2\2\u01a5\u01a6\7]\2\2\u01a6x\3\2\2\2\u01a7\u01a8\7o\2\2\u01a8\u01a9"+
		"\7q\2\2\u01a9\u01aa\7f\2\2\u01aa\u01ab\7w\2\2\u01ab\u01ac\7n\2\2\u01ac"+
		"\u01ad\7g\2\2\u01ad\u01ae\7]\2\2\u01aez\3\2\2\2\u01af\u01b0\7v\2\2\u01b0"+
		"\u01b1\7t\2\2\u01b1\u01b2\7{\2\2\u01b2\u01b3\7]\2\2\u01b3|\3\2\2\2\u01b4"+
		"\u01b5\7_\2\2\u01b5\u01b6\7e\2\2\u01b6\u01b7\7c\2\2\u01b7\u01b8\7v\2\2"+
		"\u01b8\u01b9\7e\2\2\u01b9\u01ba\7j\2\2\u01ba\u01bb\7]\2\2\u01bb~\3\2\2"+
		"\2\u01bc\u01bd\7_\2\2\u01bd\u01be\7]\2\2\u01be\u0080\3\2\2\2\u01bf\u01c2"+
		"\7^\2\2\u01c0\u01c3\t\r\2\2\u01c1\u01c3\5\u0083B\2\u01c2\u01c0\3\2\2\2"+
		"\u01c2\u01c1\3\2\2\2\u01c3\u0082\3\2\2\2\u01c4\u01c5\7w\2\2\u01c5\u01c6"+
		"\5\u0085C\2\u01c6\u01c7\5\u0085C\2\u01c7\u01c8\5\u0085C\2\u01c8\u01c9"+
		"\5\u0085C\2\u01c9\u0084\3\2\2\2\u01ca\u01cb\t\16\2\2\u01cb\u0086\3\2\2"+
		"\2\u01cc\u01cd\n\17\2\2\u01cd\u0088\3\2\2\2\u01ce\u01cf\7\61\2\2\u01cf"+
		"\u01d0\7,\2\2\u01d0\u01d4\3\2\2\2\u01d1\u01d3\13\2\2\2\u01d2\u01d1\3\2"+
		"\2\2\u01d3\u01d6\3\2\2\2\u01d4\u01d5\3\2\2\2\u01d4\u01d2\3\2\2\2\u01d5"+
		"\u01d7\3\2\2\2\u01d6\u01d4\3\2\2\2\u01d7\u01d8\7,\2\2\u01d8\u01d9\7\61"+
		"\2\2\u01d9\u01da\3\2\2\2\u01da\u01db\bE\2\2\u01db\u008a\3\2\2\2\u01dc"+
		"\u01dd\7\61\2\2\u01dd\u01de\7\61\2\2\u01de\u01e2\3\2\2\2\u01df\u01e1\n"+
		"\20\2\2\u01e0\u01df\3\2\2\2\u01e1\u01e4\3\2\2\2\u01e2\u01e0\3\2\2\2\u01e2"+
		"\u01e3\3\2\2\2\u01e3\u01e5\3\2\2\2\u01e4\u01e2\3\2\2\2\u01e5\u01e6\bF"+
		"\2\2\u01e6\u008c\3\2\2\2\u01e7\u01e9\t\21\2\2\u01e8\u01e7\3\2\2\2\u01e9"+
		"\u01ea\3\2\2\2\u01ea\u01e8\3\2\2\2\u01ea\u01eb\3\2\2\2\u01eb\u01ec\3\2"+
		"\2\2\u01ec\u01ed\bG\2\2\u01ed\u008e\3\2\2\2\u01ee\u01ef\7@\2\2\u01ef\u01f0"+
		"\7@\2\2\u01f0\u01f4\3\2\2\2\u01f1\u01f3\n\20\2\2\u01f2\u01f1\3\2\2\2\u01f3"+
		"\u01f6\3\2\2\2\u01f4\u01f2\3\2\2\2\u01f4\u01f5\3\2\2\2\u01f5\u0090\3\2"+
		"\2\2\u01f6\u01f4\3\2\2\2\35\2\u00a4\u00aa\u00af\u00c5\u00d0\u00e2\u00e7"+
		"\u00e9\u00f4\u0107\u010d\u0113\u0117\u0120\u013b\u0142\u0147\u014c\u0151"+
		"\u015a\u015f\u01c2\u01d4\u01e2\u01ea\u01f4\3\b\2\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}