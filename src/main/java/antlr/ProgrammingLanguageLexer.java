// Generated from C:/Users/Rabo/IdeaProjects/ProgrammingLanguage/src/main/java\ProgrammingLanguage.g4 by ANTLR 4.5
package antlr;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class ProgrammingLanguageLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.5", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		While=1, Break=2, Continue=3, Switch=4, Case=5, Return=6, If=7, Else=8, 
		Union=9, Write=10, Read=11, Void_type=12, Int_type=13, Bool_type=14, String_type=15, 
		Or=16, And=17, Equals=18, NEquals=19, GTEquals=20, LTEquals=21, Pow=22, 
		Excl=23, GT=24, LT=25, Add=26, Subtract=27, Multiply=28, Divide=29, Modulus=30, 
		OBrace=31, CBrace=32, OBracket=33, CBracket=34, OParen=35, CParen=36, 
		SColon=37, Assign=38, Comma=39, QMark=40, Colon=41, Int=42, Bool=43, String=44, 
		Id=45, Comment=46, Space=47;
	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	public static final String[] ruleNames = {
		"While", "Break", "Continue", "Switch", "Case", "Return", "If", "Else", 
		"Union", "Write", "Read", "Void_type", "Int_type", "Bool_type", "String_type", 
		"Or", "And", "Equals", "NEquals", "GTEquals", "LTEquals", "Pow", "Excl", 
		"GT", "LT", "Add", "Subtract", "Multiply", "Divide", "Modulus", "OBrace", 
		"CBrace", "OBracket", "CBracket", "OParen", "CParen", "SColon", "Assign", 
		"Comma", "QMark", "Colon", "Int", "Bool", "String", "Id", "Comment", "Space"
	};

	private static final String[] _LITERAL_NAMES = {
		null, "'while'", "'break'", "'continue'", "'switch'", "'case'", "'return'", 
		"'if'", "'else'", "'union'", "'write'", "'read'", "'void'", "'int'", "'bool'", 
		"'string'", "'||'", "'&&'", "'=='", "'!='", "'>='", "'<='", "'^'", "'!'", 
		"'>'", "'<'", "'+'", "'-'", "'*'", "'/'", "'%'", "'{'", "'}'", "'['", 
		"']'", "'('", "')'", "';'", "'='", "','", "'?'", "':'"
	};
	private static final String[] _SYMBOLIC_NAMES = {
		null, "While", "Break", "Continue", "Switch", "Case", "Return", "If", 
		"Else", "Union", "Write", "Read", "Void_type", "Int_type", "Bool_type", 
		"String_type", "Or", "And", "Equals", "NEquals", "GTEquals", "LTEquals", 
		"Pow", "Excl", "GT", "LT", "Add", "Subtract", "Multiply", "Divide", "Modulus", 
		"OBrace", "CBrace", "OBracket", "CBracket", "OParen", "CParen", "SColon", 
		"Assign", "Comma", "QMark", "Colon", "Int", "Bool", "String", "Id", "Comment", 
		"Space"
	};
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
	@NotNull
	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}


	public ProgrammingLanguageLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "ProgrammingLanguage.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public String[] getModeNames() { return modeNames; }

	@Override
	public ATN getATN() { return _ATN; }

	public static final String _serializedATN =
		"\3\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\2\61\u0131\b\1\4\2"+
		"\t\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4"+
		"\13\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22"+
		"\t\22\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31"+
		"\t\31\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4 \t"+
		" \4!\t!\4\"\t\"\4#\t#\4$\t$\4%\t%\4&\t&\4\'\t\'\4(\t(\4)\t)\4*\t*\4+\t"+
		"+\4,\t,\4-\t-\4.\t.\4/\t/\4\60\t\60\3\2\3\2\3\2\3\2\3\2\3\2\3\3\3\3\3"+
		"\3\3\3\3\3\3\3\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\5\3\5\3\5\3\5\3\5"+
		"\3\5\3\5\3\6\3\6\3\6\3\6\3\6\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\b\3\b\3\b\3"+
		"\t\3\t\3\t\3\t\3\t\3\n\3\n\3\n\3\n\3\n\3\n\3\13\3\13\3\13\3\13\3\13\3"+
		"\13\3\f\3\f\3\f\3\f\3\f\3\r\3\r\3\r\3\r\3\r\3\16\3\16\3\16\3\16\3\17\3"+
		"\17\3\17\3\17\3\17\3\20\3\20\3\20\3\20\3\20\3\20\3\20\3\21\3\21\3\21\3"+
		"\22\3\22\3\22\3\23\3\23\3\23\3\24\3\24\3\24\3\25\3\25\3\25\3\26\3\26\3"+
		"\26\3\27\3\27\3\30\3\30\3\31\3\31\3\32\3\32\3\33\3\33\3\34\3\34\3\35\3"+
		"\35\3\36\3\36\3\37\3\37\3 \3 \3!\3!\3\"\3\"\3#\3#\3$\3$\3%\3%\3&\3&\3"+
		"\'\3\'\3(\3(\3)\3)\3*\3*\3+\6+\u00f3\n+\r+\16+\u00f4\3,\3,\3,\3,\3,\3"+
		",\3,\3,\3,\5,\u0100\n,\3-\3-\3-\3-\3-\3-\7-\u0108\n-\f-\16-\u010b\13-"+
		"\3-\3-\3.\3.\7.\u0111\n.\f.\16.\u0114\13.\3/\3/\3/\3/\7/\u011a\n/\f/\16"+
		"/\u011d\13/\3/\3/\3/\3/\7/\u0123\n/\f/\16/\u0126\13/\3/\3/\5/\u012a\n"+
		"/\3/\3/\3\60\3\60\3\60\3\60\3\u0124\2\61\3\3\5\4\7\5\t\6\13\7\r\b\17\t"+
		"\21\n\23\13\25\f\27\r\31\16\33\17\35\20\37\21!\22#\23%\24\'\25)\26+\27"+
		"-\30/\31\61\32\63\33\65\34\67\359\36;\37= ?!A\"C#E$G%I&K\'M(O)Q*S+U,W"+
		"-Y.[/]\60_\61\3\2\t\3\2\62;\3\2$$\5\2\f\f\17\17$$\5\2C\\aac|\6\2\62;C"+
		"\\aac|\4\2\f\f\17\17\5\2\13\f\16\17\"\"\u0139\2\3\3\2\2\2\2\5\3\2\2\2"+
		"\2\7\3\2\2\2\2\t\3\2\2\2\2\13\3\2\2\2\2\r\3\2\2\2\2\17\3\2\2\2\2\21\3"+
		"\2\2\2\2\23\3\2\2\2\2\25\3\2\2\2\2\27\3\2\2\2\2\31\3\2\2\2\2\33\3\2\2"+
		"\2\2\35\3\2\2\2\2\37\3\2\2\2\2!\3\2\2\2\2#\3\2\2\2\2%\3\2\2\2\2\'\3\2"+
		"\2\2\2)\3\2\2\2\2+\3\2\2\2\2-\3\2\2\2\2/\3\2\2\2\2\61\3\2\2\2\2\63\3\2"+
		"\2\2\2\65\3\2\2\2\2\67\3\2\2\2\29\3\2\2\2\2;\3\2\2\2\2=\3\2\2\2\2?\3\2"+
		"\2\2\2A\3\2\2\2\2C\3\2\2\2\2E\3\2\2\2\2G\3\2\2\2\2I\3\2\2\2\2K\3\2\2\2"+
		"\2M\3\2\2\2\2O\3\2\2\2\2Q\3\2\2\2\2S\3\2\2\2\2U\3\2\2\2\2W\3\2\2\2\2Y"+
		"\3\2\2\2\2[\3\2\2\2\2]\3\2\2\2\2_\3\2\2\2\3a\3\2\2\2\5g\3\2\2\2\7m\3\2"+
		"\2\2\tv\3\2\2\2\13}\3\2\2\2\r\u0082\3\2\2\2\17\u0089\3\2\2\2\21\u008c"+
		"\3\2\2\2\23\u0091\3\2\2\2\25\u0097\3\2\2\2\27\u009d\3\2\2\2\31\u00a2\3"+
		"\2\2\2\33\u00a7\3\2\2\2\35\u00ab\3\2\2\2\37\u00b0\3\2\2\2!\u00b7\3\2\2"+
		"\2#\u00ba\3\2\2\2%\u00bd\3\2\2\2\'\u00c0\3\2\2\2)\u00c3\3\2\2\2+\u00c6"+
		"\3\2\2\2-\u00c9\3\2\2\2/\u00cb\3\2\2\2\61\u00cd\3\2\2\2\63\u00cf\3\2\2"+
		"\2\65\u00d1\3\2\2\2\67\u00d3\3\2\2\29\u00d5\3\2\2\2;\u00d7\3\2\2\2=\u00d9"+
		"\3\2\2\2?\u00db\3\2\2\2A\u00dd\3\2\2\2C\u00df\3\2\2\2E\u00e1\3\2\2\2G"+
		"\u00e3\3\2\2\2I\u00e5\3\2\2\2K\u00e7\3\2\2\2M\u00e9\3\2\2\2O\u00eb\3\2"+
		"\2\2Q\u00ed\3\2\2\2S\u00ef\3\2\2\2U\u00f2\3\2\2\2W\u00ff\3\2\2\2Y\u0101"+
		"\3\2\2\2[\u010e\3\2\2\2]\u0129\3\2\2\2_\u012d\3\2\2\2ab\7y\2\2bc\7j\2"+
		"\2cd\7k\2\2de\7n\2\2ef\7g\2\2f\4\3\2\2\2gh\7d\2\2hi\7t\2\2ij\7g\2\2jk"+
		"\7c\2\2kl\7m\2\2l\6\3\2\2\2mn\7e\2\2no\7q\2\2op\7p\2\2pq\7v\2\2qr\7k\2"+
		"\2rs\7p\2\2st\7w\2\2tu\7g\2\2u\b\3\2\2\2vw\7u\2\2wx\7y\2\2xy\7k\2\2yz"+
		"\7v\2\2z{\7e\2\2{|\7j\2\2|\n\3\2\2\2}~\7e\2\2~\177\7c\2\2\177\u0080\7"+
		"u\2\2\u0080\u0081\7g\2\2\u0081\f\3\2\2\2\u0082\u0083\7t\2\2\u0083\u0084"+
		"\7g\2\2\u0084\u0085\7v\2\2\u0085\u0086\7w\2\2\u0086\u0087\7t\2\2\u0087"+
		"\u0088\7p\2\2\u0088\16\3\2\2\2\u0089\u008a\7k\2\2\u008a\u008b\7h\2\2\u008b"+
		"\20\3\2\2\2\u008c\u008d\7g\2\2\u008d\u008e\7n\2\2\u008e\u008f\7u\2\2\u008f"+
		"\u0090\7g\2\2\u0090\22\3\2\2\2\u0091\u0092\7w\2\2\u0092\u0093\7p\2\2\u0093"+
		"\u0094\7k\2\2\u0094\u0095\7q\2\2\u0095\u0096\7p\2\2\u0096\24\3\2\2\2\u0097"+
		"\u0098\7y\2\2\u0098\u0099\7t\2\2\u0099\u009a\7k\2\2\u009a\u009b\7v\2\2"+
		"\u009b\u009c\7g\2\2\u009c\26\3\2\2\2\u009d\u009e\7t\2\2\u009e\u009f\7"+
		"g\2\2\u009f\u00a0\7c\2\2\u00a0\u00a1\7f\2\2\u00a1\30\3\2\2\2\u00a2\u00a3"+
		"\7x\2\2\u00a3\u00a4\7q\2\2\u00a4\u00a5\7k\2\2\u00a5\u00a6\7f\2\2\u00a6"+
		"\32\3\2\2\2\u00a7\u00a8\7k\2\2\u00a8\u00a9\7p\2\2\u00a9\u00aa\7v\2\2\u00aa"+
		"\34\3\2\2\2\u00ab\u00ac\7d\2\2\u00ac\u00ad\7q\2\2\u00ad\u00ae\7q\2\2\u00ae"+
		"\u00af\7n\2\2\u00af\36\3\2\2\2\u00b0\u00b1\7u\2\2\u00b1\u00b2\7v\2\2\u00b2"+
		"\u00b3\7t\2\2\u00b3\u00b4\7k\2\2\u00b4\u00b5\7p\2\2\u00b5\u00b6\7i\2\2"+
		"\u00b6 \3\2\2\2\u00b7\u00b8\7~\2\2\u00b8\u00b9\7~\2\2\u00b9\"\3\2\2\2"+
		"\u00ba\u00bb\7(\2\2\u00bb\u00bc\7(\2\2\u00bc$\3\2\2\2\u00bd\u00be\7?\2"+
		"\2\u00be\u00bf\7?\2\2\u00bf&\3\2\2\2\u00c0\u00c1\7#\2\2\u00c1\u00c2\7"+
		"?\2\2\u00c2(\3\2\2\2\u00c3\u00c4\7@\2\2\u00c4\u00c5\7?\2\2\u00c5*\3\2"+
		"\2\2\u00c6\u00c7\7>\2\2\u00c7\u00c8\7?\2\2\u00c8,\3\2\2\2\u00c9\u00ca"+
		"\7`\2\2\u00ca.\3\2\2\2\u00cb\u00cc\7#\2\2\u00cc\60\3\2\2\2\u00cd\u00ce"+
		"\7@\2\2\u00ce\62\3\2\2\2\u00cf\u00d0\7>\2\2\u00d0\64\3\2\2\2\u00d1\u00d2"+
		"\7-\2\2\u00d2\66\3\2\2\2\u00d3\u00d4\7/\2\2\u00d48\3\2\2\2\u00d5\u00d6"+
		"\7,\2\2\u00d6:\3\2\2\2\u00d7\u00d8\7\61\2\2\u00d8<\3\2\2\2\u00d9\u00da"+
		"\7\'\2\2\u00da>\3\2\2\2\u00db\u00dc\7}\2\2\u00dc@\3\2\2\2\u00dd\u00de"+
		"\7\177\2\2\u00deB\3\2\2\2\u00df\u00e0\7]\2\2\u00e0D\3\2\2\2\u00e1\u00e2"+
		"\7_\2\2\u00e2F\3\2\2\2\u00e3\u00e4\7*\2\2\u00e4H\3\2\2\2\u00e5\u00e6\7"+
		"+\2\2\u00e6J\3\2\2\2\u00e7\u00e8\7=\2\2\u00e8L\3\2\2\2\u00e9\u00ea\7?"+
		"\2\2\u00eaN\3\2\2\2\u00eb\u00ec\7.\2\2\u00ecP\3\2\2\2\u00ed\u00ee\7A\2"+
		"\2\u00eeR\3\2\2\2\u00ef\u00f0\7<\2\2\u00f0T\3\2\2\2\u00f1\u00f3\t\2\2"+
		"\2\u00f2\u00f1\3\2\2\2\u00f3\u00f4\3\2\2\2\u00f4\u00f2\3\2\2\2\u00f4\u00f5"+
		"\3\2\2\2\u00f5V\3\2\2\2\u00f6\u00f7\7h\2\2\u00f7\u00f8\7c\2\2\u00f8\u00f9"+
		"\7n\2\2\u00f9\u00fa\7u\2\2\u00fa\u0100\7g\2\2\u00fb\u00fc\7v\2\2\u00fc"+
		"\u00fd\7t\2\2\u00fd\u00fe\7w\2\2\u00fe\u0100\7g\2\2\u00ff\u00f6\3\2\2"+
		"\2\u00ff\u00fb\3\2\2\2\u0100X\3\2\2\2\u0101\u0109\t\3\2\2\u0102\u0108"+
		"\n\4\2\2\u0103\u0104\7^\2\2\u0104\u0108\7^\2\2\u0105\u0106\7^\2\2\u0106"+
		"\u0108\7$\2\2\u0107\u0102\3\2\2\2\u0107\u0103\3\2\2\2\u0107\u0105\3\2"+
		"\2\2\u0108\u010b\3\2\2\2\u0109\u0107\3\2\2\2\u0109\u010a\3\2\2\2\u010a"+
		"\u010c\3\2\2\2\u010b\u0109\3\2\2\2\u010c\u010d\t\3\2\2\u010dZ\3\2\2\2"+
		"\u010e\u0112\t\5\2\2\u010f\u0111\t\6\2\2\u0110\u010f\3\2\2\2\u0111\u0114"+
		"\3\2\2\2\u0112\u0110\3\2\2\2\u0112\u0113\3\2\2\2\u0113\\\3\2\2\2\u0114"+
		"\u0112\3\2\2\2\u0115\u0116\7\61\2\2\u0116\u0117\7\61\2\2\u0117\u011b\3"+
		"\2\2\2\u0118\u011a\n\7\2\2\u0119\u0118\3\2\2\2\u011a\u011d\3\2\2\2\u011b"+
		"\u0119\3\2\2\2\u011b\u011c\3\2\2\2\u011c\u012a\3\2\2\2\u011d\u011b\3\2"+
		"\2\2\u011e\u011f\7\61\2\2\u011f\u0120\7,\2\2\u0120\u0124\3\2\2\2\u0121"+
		"\u0123\13\2\2\2\u0122\u0121\3\2\2\2\u0123\u0126\3\2\2\2\u0124\u0125\3"+
		"\2\2\2\u0124\u0122\3\2\2\2\u0125\u0127\3\2\2\2\u0126\u0124\3\2\2\2\u0127"+
		"\u0128\7,\2\2\u0128\u012a\7\61\2\2\u0129\u0115\3\2\2\2\u0129\u011e\3\2"+
		"\2\2\u012a\u012b\3\2\2\2\u012b\u012c\b/\2\2\u012c^\3\2\2\2\u012d\u012e"+
		"\t\b\2\2\u012e\u012f\3\2\2\2\u012f\u0130\b\60\2\2\u0130`\3\2\2\2\13\2"+
		"\u00f4\u00ff\u0107\u0109\u0112\u011b\u0124\u0129\3\b\2\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}