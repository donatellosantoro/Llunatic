// Generated from Dependencies.g4 by ANTLR 4.5.3

package it.unibas.lunatic.parser.output;

import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class DependenciesLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.5.3", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, T__2=3, T__3=4, T__4=5, T__5=6, T__6=7, T__7=8, T__8=9, 
		T__9=10, T__10=11, T__11=12, T__12=13, T__13=14, T__14=15, T__15=16, T__16=17, 
		T__17=18, T__18=19, T__19=20, OPERATOR=21, IDENTIFIER=22, STRING=23, NUMBER=24, 
		NULL=25, WHITESPACE=26, LINE_COMMENT=27, EXPRESSION=28;
	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	public static final String[] ruleNames = {
		"T__0", "T__1", "T__2", "T__3", "T__4", "T__5", "T__6", "T__7", "T__8", 
		"T__9", "T__10", "T__11", "T__12", "T__13", "T__14", "T__15", "T__16", 
		"T__17", "T__18", "T__19", "OPERATOR", "IDENTIFIER", "STRING", "NUMBER", 
		"NULL", "DIGIT", "LETTER", "WHITESPACE", "LINE_COMMENT", "EXPRESSION"
	};

	private static final String[] _LITERAL_NAMES = {
		null, "'STTGDs:'", "'DED-STTGDs:'", "'ExtTGDs:'", "'DED-ExtTGDs:'", "'EGDs:'", 
		"'DED-EGDs:'", "'ExtEGDs:'", "'DCs:'", "':'", "'->'", "'#fail'", "'.'", 
		"'|'", "'['", "','", "']'", "'and not exists'", "'('", "')'", "'$'", null, 
		null, null, null, "'#NULL#'"
	};
	private static final String[] _SYMBOLIC_NAMES = {
		null, null, null, null, null, null, null, null, null, null, null, null, 
		null, null, null, null, null, null, null, null, null, "OPERATOR", "IDENTIFIER", 
		"STRING", "NUMBER", "NULL", "WHITESPACE", "LINE_COMMENT", "EXPRESSION"
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

	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}



	public void emitErrorMessage(String msg) {
		throw new it.unibas.lunatic.exceptions.ParserException(msg);
	}


	public DependenciesLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "Dependencies.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public String[] getModeNames() { return modeNames; }

	@Override
	public ATN getATN() { return _ATN; }

	@Override
	public void action(RuleContext _localctx, int ruleIndex, int actionIndex) {
		switch (ruleIndex) {
		case 27:
			WHITESPACE_action((RuleContext)_localctx, actionIndex);
			break;
		case 28:
			LINE_COMMENT_action((RuleContext)_localctx, actionIndex);
			break;
		}
	}
	private void WHITESPACE_action(RuleContext _localctx, int actionIndex) {
		switch (actionIndex) {
		case 0:
			 skip(); 
			break;
		}
	}
	private void LINE_COMMENT_action(RuleContext _localctx, int actionIndex) {
		switch (actionIndex) {
		case 1:
			 skip(); 
			break;
		}
	}

	public static final String _serializedATN =
		"\3\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\2\36\u0104\b\1\4\2"+
		"\t\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4"+
		"\13\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22"+
		"\t\22\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31"+
		"\t\31\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\3\2"+
		"\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3"+
		"\3\3\3\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\5\3\5\3\5\3\5\3\5\3\5\3\5"+
		"\3\5\3\5\3\5\3\5\3\5\3\5\3\6\3\6\3\6\3\6\3\6\3\6\3\7\3\7\3\7\3\7\3\7\3"+
		"\7\3\7\3\7\3\7\3\7\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\t\3\t\3\t\3\t"+
		"\3\t\3\n\3\n\3\13\3\13\3\13\3\f\3\f\3\f\3\f\3\f\3\f\3\r\3\r\3\16\3\16"+
		"\3\17\3\17\3\20\3\20\3\21\3\21\3\22\3\22\3\22\3\22\3\22\3\22\3\22\3\22"+
		"\3\22\3\22\3\22\3\22\3\22\3\22\3\22\3\23\3\23\3\24\3\24\3\25\3\25\3\26"+
		"\3\26\3\26\3\26\3\26\3\26\3\26\3\26\3\26\5\26\u00bb\n\26\3\27\3\27\3\27"+
		"\3\27\7\27\u00c1\n\27\f\27\16\27\u00c4\13\27\3\30\3\30\7\30\u00c8\n\30"+
		"\f\30\16\30\u00cb\13\30\3\30\3\30\3\31\5\31\u00d0\n\31\3\31\6\31\u00d3"+
		"\n\31\r\31\16\31\u00d4\3\31\3\31\6\31\u00d9\n\31\r\31\16\31\u00da\5\31"+
		"\u00dd\n\31\3\32\3\32\3\32\3\32\3\32\3\32\3\32\3\33\3\33\3\34\3\34\3\35"+
		"\6\35\u00eb\n\35\r\35\16\35\u00ec\3\35\3\35\3\36\3\36\3\36\3\36\7\36\u00f5"+
		"\n\36\f\36\16\36\u00f8\13\36\3\36\3\36\3\37\3\37\7\37\u00fe\n\37\f\37"+
		"\16\37\u0101\13\37\3\37\3\37\3\u00ff\2 \3\3\5\4\7\5\t\6\13\7\r\b\17\t"+
		"\21\n\23\13\25\f\27\r\31\16\33\17\35\20\37\21!\22#\23%\24\'\25)\26+\27"+
		"-\30/\31\61\32\63\33\65\2\67\29\34;\35=\36\3\2\b\4\2>>@@\4\2//aa\5\2\f"+
		"\f\17\17$$\4\2C\\c|\5\2\13\f\16\17\"\"\4\2\f\f\17\17\u0110\2\3\3\2\2\2"+
		"\2\5\3\2\2\2\2\7\3\2\2\2\2\t\3\2\2\2\2\13\3\2\2\2\2\r\3\2\2\2\2\17\3\2"+
		"\2\2\2\21\3\2\2\2\2\23\3\2\2\2\2\25\3\2\2\2\2\27\3\2\2\2\2\31\3\2\2\2"+
		"\2\33\3\2\2\2\2\35\3\2\2\2\2\37\3\2\2\2\2!\3\2\2\2\2#\3\2\2\2\2%\3\2\2"+
		"\2\2\'\3\2\2\2\2)\3\2\2\2\2+\3\2\2\2\2-\3\2\2\2\2/\3\2\2\2\2\61\3\2\2"+
		"\2\2\63\3\2\2\2\29\3\2\2\2\2;\3\2\2\2\2=\3\2\2\2\3?\3\2\2\2\5G\3\2\2\2"+
		"\7S\3\2\2\2\t\\\3\2\2\2\13i\3\2\2\2\ro\3\2\2\2\17y\3\2\2\2\21\u0082\3"+
		"\2\2\2\23\u0087\3\2\2\2\25\u0089\3\2\2\2\27\u008c\3\2\2\2\31\u0092\3\2"+
		"\2\2\33\u0094\3\2\2\2\35\u0096\3\2\2\2\37\u0098\3\2\2\2!\u009a\3\2\2\2"+
		"#\u009c\3\2\2\2%\u00ab\3\2\2\2\'\u00ad\3\2\2\2)\u00af\3\2\2\2+\u00ba\3"+
		"\2\2\2-\u00bc\3\2\2\2/\u00c5\3\2\2\2\61\u00cf\3\2\2\2\63\u00de\3\2\2\2"+
		"\65\u00e5\3\2\2\2\67\u00e7\3\2\2\29\u00ea\3\2\2\2;\u00f0\3\2\2\2=\u00fb"+
		"\3\2\2\2?@\7U\2\2@A\7V\2\2AB\7V\2\2BC\7I\2\2CD\7F\2\2DE\7u\2\2EF\7<\2"+
		"\2F\4\3\2\2\2GH\7F\2\2HI\7G\2\2IJ\7F\2\2JK\7/\2\2KL\7U\2\2LM\7V\2\2MN"+
		"\7V\2\2NO\7I\2\2OP\7F\2\2PQ\7u\2\2QR\7<\2\2R\6\3\2\2\2ST\7G\2\2TU\7z\2"+
		"\2UV\7v\2\2VW\7V\2\2WX\7I\2\2XY\7F\2\2YZ\7u\2\2Z[\7<\2\2[\b\3\2\2\2\\"+
		"]\7F\2\2]^\7G\2\2^_\7F\2\2_`\7/\2\2`a\7G\2\2ab\7z\2\2bc\7v\2\2cd\7V\2"+
		"\2de\7I\2\2ef\7F\2\2fg\7u\2\2gh\7<\2\2h\n\3\2\2\2ij\7G\2\2jk\7I\2\2kl"+
		"\7F\2\2lm\7u\2\2mn\7<\2\2n\f\3\2\2\2op\7F\2\2pq\7G\2\2qr\7F\2\2rs\7/\2"+
		"\2st\7G\2\2tu\7I\2\2uv\7F\2\2vw\7u\2\2wx\7<\2\2x\16\3\2\2\2yz\7G\2\2z"+
		"{\7z\2\2{|\7v\2\2|}\7G\2\2}~\7I\2\2~\177\7F\2\2\177\u0080\7u\2\2\u0080"+
		"\u0081\7<\2\2\u0081\20\3\2\2\2\u0082\u0083\7F\2\2\u0083\u0084\7E\2\2\u0084"+
		"\u0085\7u\2\2\u0085\u0086\7<\2\2\u0086\22\3\2\2\2\u0087\u0088\7<\2\2\u0088"+
		"\24\3\2\2\2\u0089\u008a\7/\2\2\u008a\u008b\7@\2\2\u008b\26\3\2\2\2\u008c"+
		"\u008d\7%\2\2\u008d\u008e\7h\2\2\u008e\u008f\7c\2\2\u008f\u0090\7k\2\2"+
		"\u0090\u0091\7n\2\2\u0091\30\3\2\2\2\u0092\u0093\7\60\2\2\u0093\32\3\2"+
		"\2\2\u0094\u0095\7~\2\2\u0095\34\3\2\2\2\u0096\u0097\7]\2\2\u0097\36\3"+
		"\2\2\2\u0098\u0099\7.\2\2\u0099 \3\2\2\2\u009a\u009b\7_\2\2\u009b\"\3"+
		"\2\2\2\u009c\u009d\7c\2\2\u009d\u009e\7p\2\2\u009e\u009f\7f\2\2\u009f"+
		"\u00a0\7\"\2\2\u00a0\u00a1\7p\2\2\u00a1\u00a2\7q\2\2\u00a2\u00a3\7v\2"+
		"\2\u00a3\u00a4\7\"\2\2\u00a4\u00a5\7g\2\2\u00a5\u00a6\7z\2\2\u00a6\u00a7"+
		"\7k\2\2\u00a7\u00a8\7u\2\2\u00a8\u00a9\7v\2\2\u00a9\u00aa\7u\2\2\u00aa"+
		"$\3\2\2\2\u00ab\u00ac\7*\2\2\u00ac&\3\2\2\2\u00ad\u00ae\7+\2\2\u00ae("+
		"\3\2\2\2\u00af\u00b0\7&\2\2\u00b0*\3\2\2\2\u00b1\u00b2\7?\2\2\u00b2\u00bb"+
		"\7?\2\2\u00b3\u00b4\7#\2\2\u00b4\u00bb\7?\2\2\u00b5\u00bb\t\2\2\2\u00b6"+
		"\u00b7\7@\2\2\u00b7\u00bb\7?\2\2\u00b8\u00b9\7>\2\2\u00b9\u00bb\7?\2\2"+
		"\u00ba\u00b1\3\2\2\2\u00ba\u00b3\3\2\2\2\u00ba\u00b5\3\2\2\2\u00ba\u00b6"+
		"\3\2\2\2\u00ba\u00b8\3\2\2\2\u00bb,\3\2\2\2\u00bc\u00c2\5\67\34\2\u00bd"+
		"\u00c1\5\67\34\2\u00be\u00c1\5\65\33\2\u00bf\u00c1\t\3\2\2\u00c0\u00bd"+
		"\3\2\2\2\u00c0\u00be\3\2\2\2\u00c0\u00bf\3\2\2\2\u00c1\u00c4\3\2\2\2\u00c2"+
		"\u00c0\3\2\2\2\u00c2\u00c3\3\2\2\2\u00c3.\3\2\2\2\u00c4\u00c2\3\2\2\2"+
		"\u00c5\u00c9\7$\2\2\u00c6\u00c8\n\4\2\2\u00c7\u00c6\3\2\2\2\u00c8\u00cb"+
		"\3\2\2\2\u00c9\u00c7\3\2\2\2\u00c9\u00ca\3\2\2\2\u00ca\u00cc\3\2\2\2\u00cb"+
		"\u00c9\3\2\2\2\u00cc\u00cd\7$\2\2\u00cd\60\3\2\2\2\u00ce\u00d0\7/\2\2"+
		"\u00cf\u00ce\3\2\2\2\u00cf\u00d0\3\2\2\2\u00d0\u00d2\3\2\2\2\u00d1\u00d3"+
		"\5\65\33\2\u00d2\u00d1\3\2\2\2\u00d3\u00d4\3\2\2\2\u00d4\u00d2\3\2\2\2"+
		"\u00d4\u00d5\3\2\2\2\u00d5\u00dc\3\2\2\2\u00d6\u00d8\7\60\2\2\u00d7\u00d9"+
		"\5\65\33\2\u00d8\u00d7\3\2\2\2\u00d9\u00da\3\2\2\2\u00da\u00d8\3\2\2\2"+
		"\u00da\u00db\3\2\2\2\u00db\u00dd\3\2\2\2\u00dc\u00d6\3\2\2\2\u00dc\u00dd"+
		"\3\2\2\2\u00dd\62\3\2\2\2\u00de\u00df\7%\2\2\u00df\u00e0\7P\2\2\u00e0"+
		"\u00e1\7W\2\2\u00e1\u00e2\7N\2\2\u00e2\u00e3\7N\2\2\u00e3\u00e4\7%\2\2"+
		"\u00e4\64\3\2\2\2\u00e5\u00e6\4\62;\2\u00e6\66\3\2\2\2\u00e7\u00e8\t\5"+
		"\2\2\u00e88\3\2\2\2\u00e9\u00eb\t\6\2\2\u00ea\u00e9\3\2\2\2\u00eb\u00ec"+
		"\3\2\2\2\u00ec\u00ea\3\2\2\2\u00ec\u00ed\3\2\2\2\u00ed\u00ee\3\2\2\2\u00ee"+
		"\u00ef\b\35\2\2\u00ef:\3\2\2\2\u00f0\u00f1\7\61\2\2\u00f1\u00f2\7\61\2"+
		"\2\u00f2\u00f6\3\2\2\2\u00f3\u00f5\n\7\2\2\u00f4\u00f3\3\2\2\2\u00f5\u00f8"+
		"\3\2\2\2\u00f6\u00f4\3\2\2\2\u00f6\u00f7\3\2\2\2\u00f7\u00f9\3\2\2\2\u00f8"+
		"\u00f6\3\2\2\2\u00f9\u00fa\b\36\3\2\u00fa<\3\2\2\2\u00fb\u00ff\7}\2\2"+
		"\u00fc\u00fe\13\2\2\2\u00fd\u00fc\3\2\2\2\u00fe\u0101\3\2\2\2\u00ff\u0100"+
		"\3\2\2\2\u00ff\u00fd\3\2\2\2\u0100\u0102\3\2\2\2\u0101\u00ff\3\2\2\2\u0102"+
		"\u0103\7\177\2\2\u0103>\3\2\2\2\16\2\u00ba\u00c0\u00c2\u00c9\u00cf\u00d4"+
		"\u00da\u00dc\u00ec\u00f6\u00ff\4\3\35\2\3\36\3";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}