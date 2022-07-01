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
		T__9=10, T__10=11, ConstantKeywords=12, ASSERT=13, ASSERT2=14, BOOL_FALSE=15, 
		BOOL_TRUE=16, BLOCK=17, LOCAL=18, BODY=19, CATCH=20, DEFINE=21, DO=22, 
		ELSE=23, IF=24, MODULE=25, Null=26, Null_Set=27, SWITCH=28, THEN=29, TRY=30, 
		WHILE=31, Integer=32, Decimal=33, SCIENTIFIC_NUMBER=34, Bool=35, STRING=36, 
		LeftBracket=37, RightBracket=38, Comma=39, Colon=40, SemiColon=41, LDoubleBracket=42, 
		RDoubleBracket=43, LambdaConnector=44, Times=45, Divide=46, PlusPlus=47, 
		Plus=48, MinusMinus=49, Minus=50, LessThan=51, GreaterThan=52, SingleEqual=53, 
		To_Set=54, LessEquals=55, MoreEquals=56, IsA=57, Equals=58, NotEquals=59, 
		RegexMatches=60, LogicalNot=61, Membership=62, Exponentiation=63, And=64, 
		Or=65, Backtick=66, Percent=67, Tilde=68, Backslash=69, Backslash2=70, 
		Backslash3=71, Backslash4=72, Hash=73, Stile=74, TildeRight=75, StemDot=76, 
		UnaryMinus=77, UnaryPlus=78, Floor=79, Ceiling=80, FunctionMarker=81, 
		ASSIGN=82, Identifier=83, FuncStart=84, F_REF=85, FDOC=86, WS=87, COMMENT=88, 
		LINE_COMMENT=89;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	private static String[] makeRuleNames() {
		return new String[] {
			"T__0", "T__1", "T__2", "T__3", "T__4", "T__5", "T__6", "T__7", "T__8", 
			"T__9", "T__10", "ConstantKeywords", "ASSERT", "ASSERT2", "BOOL_FALSE", 
			"BOOL_TRUE", "BLOCK", "LOCAL", "BODY", "CATCH", "DEFINE", "DO", "ELSE", 
			"IF", "MODULE", "Null", "Null_Set", "SWITCH", "THEN", "TRY", "WHILE", 
			"Integer", "Decimal", "SCIENTIFIC_NUMBER", "E", "SIGN", "Bool", "STRING", 
			"ESC", "UnicodeEscape", "HexDigit", "StringCharacters", "StringCharacter", 
			"LeftBracket", "RightBracket", "Comma", "Colon", "SemiColon", "LDoubleBracket", 
			"RDoubleBracket", "LambdaConnector", "Times", "Divide", "PlusPlus", "Plus", 
			"MinusMinus", "Minus", "LessThan", "GreaterThan", "SingleEqual", "To_Set", 
			"LessEquals", "MoreEquals", "IsA", "Equals", "NotEquals", "RegexMatches", 
			"LogicalNot", "Membership", "Exponentiation", "And", "Or", "Backtick", 
			"Percent", "Tilde", "Backslash", "Backslash2", "Backslash3", "Backslash4", 
			"Hash", "Stile", "TildeRight", "StemDot", "UnaryMinus", "UnaryPlus", 
			"Floor", "Ceiling", "FunctionMarker", "ASSIGN", "Identifier", "FuncStart", 
			"F_REF", "AllOps", "FUNCTION_NAME", "FDOC", "WS", "COMMENT", "LINE_COMMENT"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'{'", "'}'", "')'", "'('", "'\\/'", "'\u2229'", "'/\\'", "'\u222A'", 
			"'!'", "'\u00AC'", "'?'", null, "'assert'", "'\u22A8'", "'false'", "'true'", 
			"'block'", "'local'", "'body'", "'catch'", "'define'", "'do'", "'else'", 
			"'if'", "'module'", "'null'", "'\u2205'", "'switch'", "'then'", "'try'", 
			"'while'", null, null, null, null, null, "'['", "']'", "','", "':'", 
			"';'", null, null, null, null, null, "'++'", "'+'", "'--'", "'-'", "'<'", 
			"'>'", "'='", null, null, null, "'<<'", null, null, null, null, null, 
			"'^'", null, null, "'`'", null, "'~'", null, null, null, null, "'#'", 
			"'|'", null, "'.'", "'\u00AF'", "'\u207A'", "'\u230A'", "'\u2308'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, null, null, null, null, null, null, null, null, null, null, null, 
			"ConstantKeywords", "ASSERT", "ASSERT2", "BOOL_FALSE", "BOOL_TRUE", "BLOCK", 
			"LOCAL", "BODY", "CATCH", "DEFINE", "DO", "ELSE", "IF", "MODULE", "Null", 
			"Null_Set", "SWITCH", "THEN", "TRY", "WHILE", "Integer", "Decimal", "SCIENTIFIC_NUMBER", 
			"Bool", "STRING", "LeftBracket", "RightBracket", "Comma", "Colon", "SemiColon", 
			"LDoubleBracket", "RDoubleBracket", "LambdaConnector", "Times", "Divide", 
			"PlusPlus", "Plus", "MinusMinus", "Minus", "LessThan", "GreaterThan", 
			"SingleEqual", "To_Set", "LessEquals", "MoreEquals", "IsA", "Equals", 
			"NotEquals", "RegexMatches", "LogicalNot", "Membership", "Exponentiation", 
			"And", "Or", "Backtick", "Percent", "Tilde", "Backslash", "Backslash2", 
			"Backslash3", "Backslash4", "Hash", "Stile", "TildeRight", "StemDot", 
			"UnaryMinus", "UnaryPlus", "Floor", "Ceiling", "FunctionMarker", "ASSIGN", 
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
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2[\u0295\b\1\4\2\t"+
		"\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13"+
		"\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31\t\31"+
		"\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4 \t \4!"+
		"\t!\4\"\t\"\4#\t#\4$\t$\4%\t%\4&\t&\4\'\t\'\4(\t(\4)\t)\4*\t*\4+\t+\4"+
		",\t,\4-\t-\4.\t.\4/\t/\4\60\t\60\4\61\t\61\4\62\t\62\4\63\t\63\4\64\t"+
		"\64\4\65\t\65\4\66\t\66\4\67\t\67\48\t8\49\t9\4:\t:\4;\t;\4<\t<\4=\t="+
		"\4>\t>\4?\t?\4@\t@\4A\tA\4B\tB\4C\tC\4D\tD\4E\tE\4F\tF\4G\tG\4H\tH\4I"+
		"\tI\4J\tJ\4K\tK\4L\tL\4M\tM\4N\tN\4O\tO\4P\tP\4Q\tQ\4R\tR\4S\tS\4T\tT"+
		"\4U\tU\4V\tV\4W\tW\4X\tX\4Y\tY\4Z\tZ\4[\t[\4\\\t\\\4]\t]\4^\t^\4_\t_\4"+
		"`\t`\4a\ta\4b\tb\4c\tc\3\2\3\2\3\3\3\3\3\4\3\4\3\5\3\5\3\6\3\6\3\6\3\7"+
		"\3\7\3\b\3\b\3\b\3\t\3\t\3\n\3\n\3\13\3\13\3\f\3\f\3\r\3\r\3\r\3\r\5\r"+
		"\u00e4\n\r\3\16\3\16\3\16\3\16\3\16\3\16\3\16\3\17\3\17\3\20\3\20\3\20"+
		"\3\20\3\20\3\20\3\21\3\21\3\21\3\21\3\21\3\22\3\22\3\22\3\22\3\22\3\22"+
		"\3\23\3\23\3\23\3\23\3\23\3\23\3\24\3\24\3\24\3\24\3\24\3\25\3\25\3\25"+
		"\3\25\3\25\3\25\3\26\3\26\3\26\3\26\3\26\3\26\3\26\3\27\3\27\3\27\3\30"+
		"\3\30\3\30\3\30\3\30\3\31\3\31\3\31\3\32\3\32\3\32\3\32\3\32\3\32\3\32"+
		"\3\33\3\33\3\33\3\33\3\33\3\34\3\34\3\35\3\35\3\35\3\35\3\35\3\35\3\35"+
		"\3\36\3\36\3\36\3\36\3\36\3\37\3\37\3\37\3\37\3 \3 \3 \3 \3 \3 \3!\6!"+
		"\u0148\n!\r!\16!\u0149\3\"\3\"\3\"\3\"\3#\3#\3#\5#\u0153\n#\3#\3#\5#\u0157"+
		"\n#\3$\3$\3%\3%\3%\3%\5%\u015f\n%\3&\3&\5&\u0163\n&\3\'\3\'\5\'\u0167"+
		"\n\'\3\'\3\'\3(\3(\3(\5(\u016e\n(\3)\3)\6)\u0172\n)\r)\16)\u0173\3)\3"+
		")\3)\3)\3)\3*\3*\3+\6+\u017e\n+\r+\16+\u017f\3,\3,\5,\u0184\n,\3-\3-\3"+
		".\3.\3/\3/\3\60\3\60\3\61\3\61\3\62\3\62\3\62\5\62\u0193\n\62\3\63\3\63"+
		"\3\63\5\63\u0198\n\63\3\64\3\64\3\64\5\64\u019d\n\64\3\65\3\65\3\66\3"+
		"\66\3\67\3\67\3\67\38\38\39\39\39\3:\3:\3;\3;\3<\3<\3=\3=\3>\3>\3>\5>"+
		"\u01b6\n>\3?\3?\3?\5?\u01bb\n?\3@\3@\3@\5@\u01c0\n@\3A\3A\3A\3B\3B\3B"+
		"\5B\u01c8\nB\3C\3C\3C\5C\u01cd\nC\3D\3D\3D\5D\u01d2\nD\3E\3E\3F\3F\3G"+
		"\3G\3H\3H\3H\5H\u01dd\nH\3I\3I\3I\5I\u01e2\nI\3J\3J\3K\3K\3L\3L\3M\3M"+
		"\3M\5M\u01ed\nM\3N\3N\3N\3N\3N\5N\u01f4\nN\3O\3O\3O\3O\3O\5O\u01fb\nO"+
		"\3P\3P\3P\3P\3P\3P\3P\5P\u0204\nP\3Q\3Q\3R\3R\3S\3S\3S\5S\u020d\nS\3T"+
		"\3T\3U\3U\3V\3V\3W\3W\3X\3X\3Y\3Y\3Z\3Z\3Z\3Z\3Z\3Z\3Z\3Z\3Z\3Z\3Z\3Z"+
		"\3Z\3Z\3Z\3Z\3Z\3Z\3Z\3Z\5Z\u022f\nZ\3[\5[\u0232\n[\3[\3[\7[\u0236\n["+
		"\f[\16[\u0239\13[\3\\\3\\\3\\\3]\3]\3]\3]\3]\7]\u0243\n]\f]\16]\u0246"+
		"\13]\3]\3]\3]\3]\5]\u024c\n]\3^\3^\3^\3^\3^\3^\3^\3^\3^\3^\3^\3^\3^\3"+
		"^\3^\3^\3^\3^\3^\3^\3^\3^\3^\3^\5^\u0266\n^\3_\3_\7_\u026a\n_\f_\16_\u026d"+
		"\13_\3`\3`\7`\u0271\n`\f`\16`\u0274\13`\3a\6a\u0277\na\ra\16a\u0278\3"+
		"a\3a\3b\3b\3b\3b\7b\u0281\nb\fb\16b\u0284\13b\3b\3b\3b\3b\3b\3c\3c\3c"+
		"\3c\7c\u028f\nc\fc\16c\u0292\13c\3c\3c\3\u0282\2d\3\3\5\4\7\5\t\6\13\7"+
		"\r\b\17\t\21\n\23\13\25\f\27\r\31\16\33\17\35\20\37\21!\22#\23%\24\'\25"+
		")\26+\27-\30/\31\61\32\63\33\65\34\67\359\36;\37= ?!A\"C#E$G\2I\2K%M&"+
		"O\2Q\2S\2U\2W\2Y\'[(])_*a+c,e-g.i/k\60m\61o\62q\63s\64u\65w\66y\67{8}"+
		"9\177:\u0081;\u0083<\u0085=\u0087>\u0089?\u008b@\u008dA\u008fB\u0091C"+
		"\u0093D\u0095E\u0097F\u0099G\u009bH\u009dI\u009fJ\u00a1K\u00a3L\u00a5"+
		"M\u00a7N\u00a9O\u00abP\u00adQ\u00afR\u00b1S\u00b3T\u00b5U\u00b7V\u00b9"+
		"W\u00bb\2\u00bd\2\u00bfX\u00c1Y\u00c3Z\u00c5[\3\2\20\3\2\62;\4\2GGgg\t"+
		"\2))^^ddhhppttvv\5\2\62;CHch\6\2\f\f\17\17))^^\4\2,,\u00d9\u00d9\4\2\61"+
		"\61\u00f9\u00f9\4\2##\u00ae\u00ae\4\2\'\'\u2208\u2208\4\2BB\u2299\u2299"+
		"\13\2&&C\\aac|\u0393\u03ab\u03b3\u03cb\u03d3\u03d3\u03d8\u03d8\u03f2\u03f3"+
		"\n\2&&\62;C\\aac|\u0393\u03ab\u03b3\u03cb\u03d3\u03d3\4\2\f\f\17\17\5"+
		"\2\13\f\16\17\"\"\2\u02d4\2\3\3\2\2\2\2\5\3\2\2\2\2\7\3\2\2\2\2\t\3\2"+
		"\2\2\2\13\3\2\2\2\2\r\3\2\2\2\2\17\3\2\2\2\2\21\3\2\2\2\2\23\3\2\2\2\2"+
		"\25\3\2\2\2\2\27\3\2\2\2\2\31\3\2\2\2\2\33\3\2\2\2\2\35\3\2\2\2\2\37\3"+
		"\2\2\2\2!\3\2\2\2\2#\3\2\2\2\2%\3\2\2\2\2\'\3\2\2\2\2)\3\2\2\2\2+\3\2"+
		"\2\2\2-\3\2\2\2\2/\3\2\2\2\2\61\3\2\2\2\2\63\3\2\2\2\2\65\3\2\2\2\2\67"+
		"\3\2\2\2\29\3\2\2\2\2;\3\2\2\2\2=\3\2\2\2\2?\3\2\2\2\2A\3\2\2\2\2C\3\2"+
		"\2\2\2E\3\2\2\2\2K\3\2\2\2\2M\3\2\2\2\2Y\3\2\2\2\2[\3\2\2\2\2]\3\2\2\2"+
		"\2_\3\2\2\2\2a\3\2\2\2\2c\3\2\2\2\2e\3\2\2\2\2g\3\2\2\2\2i\3\2\2\2\2k"+
		"\3\2\2\2\2m\3\2\2\2\2o\3\2\2\2\2q\3\2\2\2\2s\3\2\2\2\2u\3\2\2\2\2w\3\2"+
		"\2\2\2y\3\2\2\2\2{\3\2\2\2\2}\3\2\2\2\2\177\3\2\2\2\2\u0081\3\2\2\2\2"+
		"\u0083\3\2\2\2\2\u0085\3\2\2\2\2\u0087\3\2\2\2\2\u0089\3\2\2\2\2\u008b"+
		"\3\2\2\2\2\u008d\3\2\2\2\2\u008f\3\2\2\2\2\u0091\3\2\2\2\2\u0093\3\2\2"+
		"\2\2\u0095\3\2\2\2\2\u0097\3\2\2\2\2\u0099\3\2\2\2\2\u009b\3\2\2\2\2\u009d"+
		"\3\2\2\2\2\u009f\3\2\2\2\2\u00a1\3\2\2\2\2\u00a3\3\2\2\2\2\u00a5\3\2\2"+
		"\2\2\u00a7\3\2\2\2\2\u00a9\3\2\2\2\2\u00ab\3\2\2\2\2\u00ad\3\2\2\2\2\u00af"+
		"\3\2\2\2\2\u00b1\3\2\2\2\2\u00b3\3\2\2\2\2\u00b5\3\2\2\2\2\u00b7\3\2\2"+
		"\2\2\u00b9\3\2\2\2\2\u00bf\3\2\2\2\2\u00c1\3\2\2\2\2\u00c3\3\2\2\2\2\u00c5"+
		"\3\2\2\2\3\u00c7\3\2\2\2\5\u00c9\3\2\2\2\7\u00cb\3\2\2\2\t\u00cd\3\2\2"+
		"\2\13\u00cf\3\2\2\2\r\u00d2\3\2\2\2\17\u00d4\3\2\2\2\21\u00d7\3\2\2\2"+
		"\23\u00d9\3\2\2\2\25\u00db\3\2\2\2\27\u00dd\3\2\2\2\31\u00e3\3\2\2\2\33"+
		"\u00e5\3\2\2\2\35\u00ec\3\2\2\2\37\u00ee\3\2\2\2!\u00f4\3\2\2\2#\u00f9"+
		"\3\2\2\2%\u00ff\3\2\2\2\'\u0105\3\2\2\2)\u010a\3\2\2\2+\u0110\3\2\2\2"+
		"-\u0117\3\2\2\2/\u011a\3\2\2\2\61\u011f\3\2\2\2\63\u0122\3\2\2\2\65\u0129"+
		"\3\2\2\2\67\u012e\3\2\2\29\u0130\3\2\2\2;\u0137\3\2\2\2=\u013c\3\2\2\2"+
		"?\u0140\3\2\2\2A\u0147\3\2\2\2C\u014b\3\2\2\2E\u014f\3\2\2\2G\u0158\3"+
		"\2\2\2I\u015e\3\2\2\2K\u0162\3\2\2\2M\u0164\3\2\2\2O\u016d\3\2\2\2Q\u016f"+
		"\3\2\2\2S\u017a\3\2\2\2U\u017d\3\2\2\2W\u0183\3\2\2\2Y\u0185\3\2\2\2["+
		"\u0187\3\2\2\2]\u0189\3\2\2\2_\u018b\3\2\2\2a\u018d\3\2\2\2c\u0192\3\2"+
		"\2\2e\u0197\3\2\2\2g\u019c\3\2\2\2i\u019e\3\2\2\2k\u01a0\3\2\2\2m\u01a2"+
		"\3\2\2\2o\u01a5\3\2\2\2q\u01a7\3\2\2\2s\u01aa\3\2\2\2u\u01ac\3\2\2\2w"+
		"\u01ae\3\2\2\2y\u01b0\3\2\2\2{\u01b5\3\2\2\2}\u01ba\3\2\2\2\177\u01bf"+
		"\3\2\2\2\u0081\u01c1\3\2\2\2\u0083\u01c7\3\2\2\2\u0085\u01cc\3\2\2\2\u0087"+
		"\u01d1\3\2\2\2\u0089\u01d3\3\2\2\2\u008b\u01d5\3\2\2\2\u008d\u01d7\3\2"+
		"\2\2\u008f\u01dc\3\2\2\2\u0091\u01e1\3\2\2\2\u0093\u01e3\3\2\2\2\u0095"+
		"\u01e5\3\2\2\2\u0097\u01e7\3\2\2\2\u0099\u01ec\3\2\2\2\u009b\u01f3\3\2"+
		"\2\2\u009d\u01fa\3\2\2\2\u009f\u0203\3\2\2\2\u00a1\u0205\3\2\2\2\u00a3"+
		"\u0207\3\2\2\2\u00a5\u020c\3\2\2\2\u00a7\u020e\3\2\2\2\u00a9\u0210\3\2"+
		"\2\2\u00ab\u0212\3\2\2\2\u00ad\u0214\3\2\2\2\u00af\u0216\3\2\2\2\u00b1"+
		"\u0218\3\2\2\2\u00b3\u022e\3\2\2\2\u00b5\u0231\3\2\2\2\u00b7\u023a\3\2"+
		"\2\2\u00b9\u023d\3\2\2\2\u00bb\u0265\3\2\2\2\u00bd\u0267\3\2\2\2\u00bf"+
		"\u026e\3\2\2\2\u00c1\u0276\3\2\2\2\u00c3\u027c\3\2\2\2\u00c5\u028a\3\2"+
		"\2\2\u00c7\u00c8\7}\2\2\u00c8\4\3\2\2\2\u00c9\u00ca\7\177\2\2\u00ca\6"+
		"\3\2\2\2\u00cb\u00cc\7+\2\2\u00cc\b\3\2\2\2\u00cd\u00ce\7*\2\2\u00ce\n"+
		"\3\2\2\2\u00cf\u00d0\7^\2\2\u00d0\u00d1\7\61\2\2\u00d1\f\3\2\2\2\u00d2"+
		"\u00d3\7\u222b\2\2\u00d3\16\3\2\2\2\u00d4\u00d5\7\61\2\2\u00d5\u00d6\7"+
		"^\2\2\u00d6\20\3\2\2\2\u00d7\u00d8\7\u222c\2\2\u00d8\22\3\2\2\2\u00d9"+
		"\u00da\7#\2\2\u00da\24\3\2\2\2\u00db\u00dc\7\u00ae\2\2\u00dc\26\3\2\2"+
		"\2\u00dd\u00de\7A\2\2\u00de\30\3\2\2\2\u00df\u00e4\5!\21\2\u00e0\u00e4"+
		"\5\37\20\2\u00e1\u00e4\5\65\33\2\u00e2\u00e4\5\67\34\2\u00e3\u00df\3\2"+
		"\2\2\u00e3\u00e0\3\2\2\2\u00e3\u00e1\3\2\2\2\u00e3\u00e2\3\2\2\2\u00e4"+
		"\32\3\2\2\2\u00e5\u00e6\7c\2\2\u00e6\u00e7\7u\2\2\u00e7\u00e8\7u\2\2\u00e8"+
		"\u00e9\7g\2\2\u00e9\u00ea\7t\2\2\u00ea\u00eb\7v\2\2\u00eb\34\3\2\2\2\u00ec"+
		"\u00ed\7\u22aa\2\2\u00ed\36\3\2\2\2\u00ee\u00ef\7h\2\2\u00ef\u00f0\7c"+
		"\2\2\u00f0\u00f1\7n\2\2\u00f1\u00f2\7u\2\2\u00f2\u00f3\7g\2\2\u00f3 \3"+
		"\2\2\2\u00f4\u00f5\7v\2\2\u00f5\u00f6\7t\2\2\u00f6\u00f7\7w\2\2\u00f7"+
		"\u00f8\7g\2\2\u00f8\"\3\2\2\2\u00f9\u00fa\7d\2\2\u00fa\u00fb\7n\2\2\u00fb"+
		"\u00fc\7q\2\2\u00fc\u00fd\7e\2\2\u00fd\u00fe\7m\2\2\u00fe$\3\2\2\2\u00ff"+
		"\u0100\7n\2\2\u0100\u0101\7q\2\2\u0101\u0102\7e\2\2\u0102\u0103\7c\2\2"+
		"\u0103\u0104\7n\2\2\u0104&\3\2\2\2\u0105\u0106\7d\2\2\u0106\u0107\7q\2"+
		"\2\u0107\u0108\7f\2\2\u0108\u0109\7{\2\2\u0109(\3\2\2\2\u010a\u010b\7"+
		"e\2\2\u010b\u010c\7c\2\2\u010c\u010d\7v\2\2\u010d\u010e\7e\2\2\u010e\u010f"+
		"\7j\2\2\u010f*\3\2\2\2\u0110\u0111\7f\2\2\u0111\u0112\7g\2\2\u0112\u0113"+
		"\7h\2\2\u0113\u0114\7k\2\2\u0114\u0115\7p\2\2\u0115\u0116\7g\2\2\u0116"+
		",\3\2\2\2\u0117\u0118\7f\2\2\u0118\u0119\7q\2\2\u0119.\3\2\2\2\u011a\u011b"+
		"\7g\2\2\u011b\u011c\7n\2\2\u011c\u011d\7u\2\2\u011d\u011e\7g\2\2\u011e"+
		"\60\3\2\2\2\u011f\u0120\7k\2\2\u0120\u0121\7h\2\2\u0121\62\3\2\2\2\u0122"+
		"\u0123\7o\2\2\u0123\u0124\7q\2\2\u0124\u0125\7f\2\2\u0125\u0126\7w\2\2"+
		"\u0126\u0127\7n\2\2\u0127\u0128\7g\2\2\u0128\64\3\2\2\2\u0129\u012a\7"+
		"p\2\2\u012a\u012b\7w\2\2\u012b\u012c\7n\2\2\u012c\u012d\7n\2\2\u012d\66"+
		"\3\2\2\2\u012e\u012f\7\u2207\2\2\u012f8\3\2\2\2\u0130\u0131\7u\2\2\u0131"+
		"\u0132\7y\2\2\u0132\u0133\7k\2\2\u0133\u0134\7v\2\2\u0134\u0135\7e\2\2"+
		"\u0135\u0136\7j\2\2\u0136:\3\2\2\2\u0137\u0138\7v\2\2\u0138\u0139\7j\2"+
		"\2\u0139\u013a\7g\2\2\u013a\u013b\7p\2\2\u013b<\3\2\2\2\u013c\u013d\7"+
		"v\2\2\u013d\u013e\7t\2\2\u013e\u013f\7{\2\2\u013f>\3\2\2\2\u0140\u0141"+
		"\7y\2\2\u0141\u0142\7j\2\2\u0142\u0143\7k\2\2\u0143\u0144\7n\2\2\u0144"+
		"\u0145\7g\2\2\u0145@\3\2\2\2\u0146\u0148\t\2\2\2\u0147\u0146\3\2\2\2\u0148"+
		"\u0149\3\2\2\2\u0149\u0147\3\2\2\2\u0149\u014a\3\2\2\2\u014aB\3\2\2\2"+
		"\u014b\u014c\5A!\2\u014c\u014d\7\60\2\2\u014d\u014e\5A!\2\u014eD\3\2\2"+
		"\2\u014f\u0156\5C\"\2\u0150\u0152\5G$\2\u0151\u0153\5I%\2\u0152\u0151"+
		"\3\2\2\2\u0152\u0153\3\2\2\2\u0153\u0154\3\2\2\2\u0154\u0155\5A!\2\u0155"+
		"\u0157\3\2\2\2\u0156\u0150\3\2\2\2\u0156\u0157\3\2\2\2\u0157F\3\2\2\2"+
		"\u0158\u0159\t\3\2\2\u0159H\3\2\2\2\u015a\u015f\5o8\2\u015b\u015f\5\u00ab"+
		"V\2\u015c\u015f\5s:\2\u015d\u015f\5\u00a9U\2\u015e\u015a\3\2\2\2\u015e"+
		"\u015b\3\2\2\2\u015e\u015c\3\2\2\2\u015e\u015d\3\2\2\2\u015fJ\3\2\2\2"+
		"\u0160\u0163\5!\21\2\u0161\u0163\5\37\20\2\u0162\u0160\3\2\2\2\u0162\u0161"+
		"\3\2\2\2\u0163L\3\2\2\2\u0164\u0166\7)\2\2\u0165\u0167\5U+\2\u0166\u0165"+
		"\3\2\2\2\u0166\u0167\3\2\2\2\u0167\u0168\3\2\2\2\u0168\u0169\7)\2\2\u0169"+
		"N\3\2\2\2\u016a\u016b\7^\2\2\u016b\u016e\t\4\2\2\u016c\u016e\5Q)\2\u016d"+
		"\u016a\3\2\2\2\u016d\u016c\3\2\2\2\u016eP\3\2\2\2\u016f\u0171\7^\2\2\u0170"+
		"\u0172\7w\2\2\u0171\u0170\3\2\2\2\u0172\u0173\3\2\2\2\u0173\u0171\3\2"+
		"\2\2\u0173\u0174\3\2\2\2\u0174\u0175\3\2\2\2\u0175\u0176\5S*\2\u0176\u0177"+
		"\5S*\2\u0177\u0178\5S*\2\u0178\u0179\5S*\2\u0179R\3\2\2\2\u017a\u017b"+
		"\t\5\2\2\u017bT\3\2\2\2\u017c\u017e\5W,\2\u017d\u017c\3\2\2\2\u017e\u017f"+
		"\3\2\2\2\u017f\u017d\3\2\2\2\u017f\u0180\3\2\2\2\u0180V\3\2\2\2\u0181"+
		"\u0184\n\6\2\2\u0182\u0184\5O(\2\u0183\u0181\3\2\2\2\u0183\u0182\3\2\2"+
		"\2\u0184X\3\2\2\2\u0185\u0186\7]\2\2\u0186Z\3\2\2\2\u0187\u0188\7_\2\2"+
		"\u0188\\\3\2\2\2\u0189\u018a\7.\2\2\u018a^\3\2\2\2\u018b\u018c\7<\2\2"+
		"\u018c`\3\2\2\2\u018d\u018e\7=\2\2\u018eb\3\2\2\2\u018f\u0190\7]\2\2\u0190"+
		"\u0193\7~\2\2\u0191\u0193\7\u27e8\2\2\u0192\u018f\3\2\2\2\u0192\u0191"+
		"\3\2\2\2\u0193d\3\2\2\2\u0194\u0195\7~\2\2\u0195\u0198\7_\2\2\u0196\u0198"+
		"\7\u27e9\2\2\u0197\u0194\3\2\2\2\u0197\u0196\3\2\2\2\u0198f\3\2\2\2\u0199"+
		"\u019a\7/\2\2\u019a\u019d\7@\2\2\u019b\u019d\7\u2194\2\2\u019c\u0199\3"+
		"\2\2\2\u019c\u019b\3\2\2\2\u019dh\3\2\2\2\u019e\u019f\t\7\2\2\u019fj\3"+
		"\2\2\2\u01a0\u01a1\t\b\2\2\u01a1l\3\2\2\2\u01a2\u01a3\7-\2\2\u01a3\u01a4"+
		"\7-\2\2\u01a4n\3\2\2\2\u01a5\u01a6\7-\2\2\u01a6p\3\2\2\2\u01a7\u01a8\7"+
		"/\2\2\u01a8\u01a9\7/\2\2\u01a9r\3\2\2\2\u01aa\u01ab\7/\2\2\u01abt\3\2"+
		"\2\2\u01ac\u01ad\7>\2\2\u01adv\3\2\2\2\u01ae\u01af\7@\2\2\u01afx\3\2\2"+
		"\2\u01b0\u01b1\7?\2\2\u01b1z\3\2\2\2\u01b2\u01b3\7~\2\2\u01b3\u01b6\7"+
		"@\2\2\u01b4\u01b6\7\u22a4\2\2\u01b5\u01b2\3\2\2\2\u01b5\u01b4\3\2\2\2"+
		"\u01b6|\3\2\2\2\u01b7\u01b8\7>\2\2\u01b8\u01bb\7?\2\2\u01b9\u01bb\7\u2266"+
		"\2\2\u01ba\u01b7\3\2\2\2\u01ba\u01b9\3\2\2\2\u01bb~\3\2\2\2\u01bc\u01bd"+
		"\7@\2\2\u01bd\u01c0\7?\2\2\u01be\u01c0\7\u2267\2\2\u01bf\u01bc\3\2\2\2"+
		"\u01bf\u01be\3\2\2\2\u01c0\u0080\3\2\2\2\u01c1\u01c2\7>\2\2\u01c2\u01c3"+
		"\7>\2\2\u01c3\u0082\3\2\2\2\u01c4\u01c5\7?\2\2\u01c5\u01c8\7?\2\2\u01c6"+
		"\u01c8\7\u2263\2\2\u01c7\u01c4\3\2\2\2\u01c7\u01c6\3\2\2\2\u01c8\u0084"+
		"\3\2\2\2\u01c9\u01ca\7#\2\2\u01ca\u01cd\7?\2\2\u01cb\u01cd\7\u2262\2\2"+
		"\u01cc\u01c9\3\2\2\2\u01cc\u01cb\3\2\2\2\u01cd\u0086\3\2\2\2\u01ce\u01cf"+
		"\7?\2\2\u01cf\u01d2\7\u0080\2\2\u01d0\u01d2\7\u224a\2\2\u01d1\u01ce\3"+
		"\2\2\2\u01d1\u01d0\3\2\2\2\u01d2\u0088\3\2\2\2\u01d3\u01d4\t\t\2\2\u01d4"+
		"\u008a\3\2\2\2\u01d5\u01d6\4\u220a\u220b\2\u01d6\u008c\3\2\2\2\u01d7\u01d8"+
		"\7`\2\2\u01d8\u008e\3\2\2\2\u01d9\u01da\7(\2\2\u01da\u01dd\7(\2\2\u01db"+
		"\u01dd\7\u2229\2\2\u01dc\u01d9\3\2\2\2\u01dc\u01db\3\2\2\2\u01dd\u0090"+
		"\3\2\2\2\u01de\u01df\7~\2\2\u01df\u01e2\7~\2\2\u01e0\u01e2\7\u222a\2\2"+
		"\u01e1\u01de\3\2\2\2\u01e1\u01e0\3\2\2\2\u01e2\u0092\3\2\2\2\u01e3\u01e4"+
		"\7b\2\2\u01e4\u0094\3\2\2\2\u01e5\u01e6\t\n\2\2\u01e6\u0096\3\2\2\2\u01e7"+
		"\u01e8\7\u0080\2\2\u01e8\u0098\3\2\2\2\u01e9\u01ea\7^\2\2\u01ea\u01ed"+
		"\7#\2\2\u01eb\u01ed\7^\2\2\u01ec\u01e9\3\2\2\2\u01ec\u01eb\3\2\2\2\u01ed"+
		"\u009a\3\2\2\2\u01ee\u01ef\7^\2\2\u01ef\u01f0\7#\2\2\u01f0\u01f4\7,\2"+
		"\2\u01f1\u01f2\7^\2\2\u01f2\u01f4\7,\2\2\u01f3\u01ee\3\2\2\2\u01f3\u01f1"+
		"\3\2\2\2\u01f4\u009c\3\2\2\2\u01f5\u01f6\7^\2\2\u01f6\u01f7\7@\2\2\u01f7"+
		"\u01fb\7#\2\2\u01f8\u01f9\7^\2\2\u01f9\u01fb\7@\2\2\u01fa\u01f5\3\2\2"+
		"\2\u01fa\u01f8\3\2\2\2\u01fb\u009e\3\2\2\2\u01fc\u01fd\7^\2\2\u01fd\u01fe"+
		"\7@\2\2\u01fe\u01ff\7#\2\2\u01ff\u0204\7,\2\2\u0200\u0201\7^\2\2\u0201"+
		"\u0202\7@\2\2\u0202\u0204\7,\2\2\u0203\u01fc\3\2\2\2\u0203\u0200\3\2\2"+
		"\2\u0204\u00a0\3\2\2\2\u0205\u0206\7%\2\2\u0206\u00a2\3\2\2\2\u0207\u0208"+
		"\7~\2\2\u0208\u00a4\3\2\2\2\u0209\u020a\7\u0080\2\2\u020a\u020d\7~\2\2"+
		"\u020b\u020d\7\u2243\2\2\u020c\u0209\3\2\2\2\u020c\u020b\3\2\2\2\u020d"+
		"\u00a6\3\2\2\2\u020e\u020f\7\60\2\2\u020f\u00a8\3\2\2\2\u0210\u0211\7"+
		"\u00b1\2\2\u0211\u00aa\3\2\2\2\u0212\u0213\7\u207c\2\2\u0213\u00ac\3\2"+
		"\2\2\u0214\u0215\7\u230c\2\2\u0215\u00ae\3\2\2\2\u0216\u0217\7\u230a\2"+
		"\2\u0217\u00b0\3\2\2\2\u0218\u0219\t\13\2\2\u0219\u00b2\3\2\2\2\u021a"+
		"\u022f\7\u2256\2\2\u021b\u021c\7<\2\2\u021c\u022f\7?\2\2\u021d\u022f\7"+
		"\u2257\2\2\u021e\u021f\7?\2\2\u021f\u022f\7<\2\2\u0220\u0221\7-\2\2\u0221"+
		"\u022f\7?\2\2\u0222\u0223\7/\2\2\u0223\u022f\7?\2\2\u0224\u0225\5i\65"+
		"\2\u0225\u0226\7?\2\2\u0226\u022f\3\2\2\2\u0227\u0228\5k\66\2\u0228\u0229"+
		"\7?\2\2\u0229\u022f\3\2\2\2\u022a\u022b\7\'\2\2\u022b\u022f\7?\2\2\u022c"+
		"\u022d\7`\2\2\u022d\u022f\7?\2\2\u022e\u021a\3\2\2\2\u022e\u021b\3\2\2"+
		"\2\u022e\u021d\3\2\2\2\u022e\u021e\3\2\2\2\u022e\u0220\3\2\2\2\u022e\u0222"+
		"\3\2\2\2\u022e\u0224\3\2\2\2\u022e\u0227\3\2\2\2\u022e\u022a\3\2\2\2\u022e"+
		"\u022c\3\2\2\2\u022f\u00b4\3\2\2\2\u0230\u0232\7(\2\2\u0231\u0230\3\2"+
		"\2\2\u0231\u0232\3\2\2\2\u0232\u0233\3\2\2\2\u0233\u0237\t\f\2\2\u0234"+
		"\u0236\t\r\2\2\u0235\u0234\3\2\2\2\u0236\u0239\3\2\2\2\u0237\u0235\3\2"+
		"\2\2\u0237\u0238\3\2\2\2\u0238\u00b6\3\2\2\2\u0239\u0237\3\2\2\2\u023a"+
		"\u023b\5\u00bd_\2\u023b\u023c\7*\2\2\u023c\u00b8\3\2\2\2\u023d\u024b\5"+
		"\u00b1Y\2\u023e\u024c\5\u00bb^\2\u023f\u0240\5\u00b5[\2\u0240\u0241\5"+
		"\u00a1Q\2\u0241\u0243\3\2\2\2\u0242\u023f\3\2\2\2\u0243\u0246\3\2\2\2"+
		"\u0244\u0242\3\2\2\2\u0244\u0245\3\2\2\2\u0245\u0247\3\2\2\2\u0246\u0244"+
		"\3\2\2\2\u0247\u024c\5\u00bd_\2\u0248\u0249\5\u00b7\\\2\u0249\u024a\7"+
		"+\2\2\u024a\u024c\3\2\2\2\u024b\u023e\3\2\2\2\u024b\u0244\3\2\2\2\u024b"+
		"\u0248\3\2\2\2\u024c\u00ba\3\2\2\2\u024d\u0266\5i\65\2\u024e\u0266\5k"+
		"\66\2\u024f\u0266\5o8\2\u0250\u0266\5s:\2\u0251\u0266\5u;\2\u0252\u0266"+
		"\5}?\2\u0253\u0266\5w<\2\u0254\u0266\5\u008dG\2\u0255\u0266\5}?\2\u0256"+
		"\u0266\5\177@\2\u0257\u0266\5\u0083B\2\u0258\u0266\5\u0085C\2\u0259\u0266"+
		"\5\u008fH\2\u025a\u0266\5\u0091I\2\u025b\u0266\5\u0095K\2\u025c\u0266"+
		"\5\u0097L\2\u025d\u0266\5\u00a5S\2\u025e\u0266\5\u0089E\2\u025f\u0266"+
		"\5\u0087D\2\u0260\u0266\5\u00adW\2\u0261\u0266\5\u00afX\2\u0262\u0266"+
		"\5\u008bF\2\u0263\u0266\5{>\2\u0264\u0266\5\u0081A\2\u0265\u024d\3\2\2"+
		"\2\u0265\u024e\3\2\2\2\u0265\u024f\3\2\2\2\u0265\u0250\3\2\2\2\u0265\u0251"+
		"\3\2\2\2\u0265\u0252\3\2\2\2\u0265\u0253\3\2\2\2\u0265\u0254\3\2\2\2\u0265"+
		"\u0255\3\2\2\2\u0265\u0256\3\2\2\2\u0265\u0257\3\2\2\2\u0265\u0258\3\2"+
		"\2\2\u0265\u0259\3\2\2\2\u0265\u025a\3\2\2\2\u0265\u025b\3\2\2\2\u0265"+
		"\u025c\3\2\2\2\u0265\u025d\3\2\2\2\u0265\u025e\3\2\2\2\u0265\u025f\3\2"+
		"\2\2\u0265\u0260\3\2\2\2\u0265\u0261\3\2\2\2\u0265\u0262\3\2\2\2\u0265"+
		"\u0263\3\2\2\2\u0265\u0264\3\2\2\2\u0266\u00bc\3\2\2\2\u0267\u026b\t\f"+
		"\2\2\u0268\u026a\t\r\2\2\u0269\u0268\3\2\2\2\u026a\u026d\3\2\2\2\u026b"+
		"\u0269\3\2\2\2\u026b\u026c\3\2\2\2\u026c\u00be\3\2\2\2\u026d\u026b\3\2"+
		"\2\2\u026e\u0272\7\u00bd\2\2\u026f\u0271\n\16\2\2\u0270\u026f\3\2\2\2"+
		"\u0271\u0274\3\2\2\2\u0272\u0270\3\2\2\2\u0272\u0273\3\2\2\2\u0273\u00c0"+
		"\3\2\2\2\u0274\u0272\3\2\2\2\u0275\u0277\t\17\2\2\u0276\u0275\3\2\2\2"+
		"\u0277\u0278\3\2\2\2\u0278\u0276\3\2\2\2\u0278\u0279\3\2\2\2\u0279\u027a"+
		"\3\2\2\2\u027a\u027b\ba\2\2\u027b\u00c2\3\2\2\2\u027c\u027d\7\61\2\2\u027d"+
		"\u027e\7,\2\2\u027e\u0282\3\2\2\2\u027f\u0281\13\2\2\2\u0280\u027f\3\2"+
		"\2\2\u0281\u0284\3\2\2\2\u0282\u0283\3\2\2\2\u0282\u0280\3\2\2\2\u0283"+
		"\u0285\3\2\2\2\u0284\u0282\3\2\2\2\u0285\u0286\7,\2\2\u0286\u0287\7\61"+
		"\2\2\u0287\u0288\3\2\2\2\u0288\u0289\bb\2\2\u0289\u00c4\3\2\2\2\u028a"+
		"\u028b\7\61\2\2\u028b\u028c\7\61\2\2\u028c\u0290\3\2\2\2\u028d\u028f\n"+
		"\16\2\2\u028e\u028d\3\2\2\2\u028f\u0292\3\2\2\2\u0290\u028e\3\2\2\2\u0290"+
		"\u0291\3\2\2\2\u0291\u0293\3\2\2\2\u0292\u0290\3\2\2\2\u0293\u0294\bc"+
		"\2\2\u0294\u00c6\3\2\2\2)\2\u00e3\u0149\u0152\u0156\u015e\u0162\u0166"+
		"\u016d\u0173\u017f\u0183\u0192\u0197\u019c\u01b5\u01ba\u01bf\u01c7\u01cc"+
		"\u01d1\u01dc\u01e1\u01ec\u01f3\u01fa\u0203\u020c\u022e\u0231\u0237\u0244"+
		"\u024b\u0265\u026b\u0272\u0278\u0282\u0290\3\b\2\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}