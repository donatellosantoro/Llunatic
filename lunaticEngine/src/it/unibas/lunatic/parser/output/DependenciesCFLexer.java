// Generated from DependenciesCF.g4 by ANTLR 4.5.3

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
public class DependenciesCFLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.5.3", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, T__2=3, T__3=4, T__4=5, T__5=6, T__6=7, T__7=8, T__8=9, 
		T__9=10, T__10=11, T__11=12, OPERATOR=13, IDENTIFIER=14, STRING=15, NUMBER=16, 
		NULL=17, WHITESPACE=18, LINE_COMMENT=19, EXPRESSION=20;
	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	public static final String[] ruleNames = {
		"T__0", "T__1", "T__2", "T__3", "T__4", "T__5", "T__6", "T__7", "T__8", 
		"T__9", "T__10", "T__11", "OPERATOR", "IDENTIFIER", "STRING", "NUMBER", 
		"NULL", "DIGIT", "LETTER", "WHITESPACE", "LINE_COMMENT", "EXPRESSION"
	};

	private static final String[] _LITERAL_NAMES = {
		null, "'ST-TGDs:'", "'T-TGDs:'", "'EGDs:'", "'Queries:'", "':'", "'->'", 
		"'.'", "'<-'", "','", "'('", "')'", "'?'", null, null, null, null, "'#NULL#'"
	};
	private static final String[] _SYMBOLIC_NAMES = {
		null, null, null, null, null, null, null, null, null, null, null, null, 
		null, "OPERATOR", "IDENTIFIER", "STRING", "NUMBER", "NULL", "WHITESPACE", 
		"LINE_COMMENT", "EXPRESSION"
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


	public DependenciesCFLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "DependenciesCF.g4"; }

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
		case 19:
			WHITESPACE_action((RuleContext)_localctx, actionIndex);
			break;
		case 20:
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
		"\3\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\2\26\u00b3\b\1\4\2"+
		"\t\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4"+
		"\13\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22"+
		"\t\22\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\3\2\3\2\3\2\3"+
		"\2\3\2\3\2\3\2\3\2\3\2\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\4\3\4\3\4\3\4"+
		"\3\4\3\4\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\6\3\6\3\7\3\7\3\7\3\b\3"+
		"\b\3\t\3\t\3\t\3\n\3\n\3\13\3\13\3\f\3\f\3\r\3\r\3\16\3\16\3\16\3\16\3"+
		"\16\3\16\3\16\3\16\5\16j\n\16\3\17\3\17\3\17\3\17\7\17p\n\17\f\17\16\17"+
		"s\13\17\3\20\3\20\7\20w\n\20\f\20\16\20z\13\20\3\20\3\20\3\21\5\21\177"+
		"\n\21\3\21\6\21\u0082\n\21\r\21\16\21\u0083\3\21\3\21\6\21\u0088\n\21"+
		"\r\21\16\21\u0089\5\21\u008c\n\21\3\22\3\22\3\22\3\22\3\22\3\22\3\22\3"+
		"\23\3\23\3\24\3\24\3\25\6\25\u009a\n\25\r\25\16\25\u009b\3\25\3\25\3\26"+
		"\3\26\3\26\3\26\7\26\u00a4\n\26\f\26\16\26\u00a7\13\26\3\26\3\26\3\27"+
		"\3\27\7\27\u00ad\n\27\f\27\16\27\u00b0\13\27\3\27\3\27\3\u00ae\2\30\3"+
		"\3\5\4\7\5\t\6\13\7\r\b\17\t\21\n\23\13\25\f\27\r\31\16\33\17\35\20\37"+
		"\21!\22#\23%\2\'\2)\24+\25-\26\3\2\b\4\2>>@@\4\2//aa\5\2\f\f\17\17$$\4"+
		"\2C\\c|\5\2\13\f\16\17\"\"\4\2\f\f\17\17\u00bf\2\3\3\2\2\2\2\5\3\2\2\2"+
		"\2\7\3\2\2\2\2\t\3\2\2\2\2\13\3\2\2\2\2\r\3\2\2\2\2\17\3\2\2\2\2\21\3"+
		"\2\2\2\2\23\3\2\2\2\2\25\3\2\2\2\2\27\3\2\2\2\2\31\3\2\2\2\2\33\3\2\2"+
		"\2\2\35\3\2\2\2\2\37\3\2\2\2\2!\3\2\2\2\2#\3\2\2\2\2)\3\2\2\2\2+\3\2\2"+
		"\2\2-\3\2\2\2\3/\3\2\2\2\58\3\2\2\2\7@\3\2\2\2\tF\3\2\2\2\13O\3\2\2\2"+
		"\rQ\3\2\2\2\17T\3\2\2\2\21V\3\2\2\2\23Y\3\2\2\2\25[\3\2\2\2\27]\3\2\2"+
		"\2\31_\3\2\2\2\33i\3\2\2\2\35k\3\2\2\2\37t\3\2\2\2!~\3\2\2\2#\u008d\3"+
		"\2\2\2%\u0094\3\2\2\2\'\u0096\3\2\2\2)\u0099\3\2\2\2+\u009f\3\2\2\2-\u00aa"+
		"\3\2\2\2/\60\7U\2\2\60\61\7V\2\2\61\62\7/\2\2\62\63\7V\2\2\63\64\7I\2"+
		"\2\64\65\7F\2\2\65\66\7u\2\2\66\67\7<\2\2\67\4\3\2\2\289\7V\2\29:\7/\2"+
		"\2:;\7V\2\2;<\7I\2\2<=\7F\2\2=>\7u\2\2>?\7<\2\2?\6\3\2\2\2@A\7G\2\2AB"+
		"\7I\2\2BC\7F\2\2CD\7u\2\2DE\7<\2\2E\b\3\2\2\2FG\7S\2\2GH\7w\2\2HI\7g\2"+
		"\2IJ\7t\2\2JK\7k\2\2KL\7g\2\2LM\7u\2\2MN\7<\2\2N\n\3\2\2\2OP\7<\2\2P\f"+
		"\3\2\2\2QR\7/\2\2RS\7@\2\2S\16\3\2\2\2TU\7\60\2\2U\20\3\2\2\2VW\7>\2\2"+
		"WX\7/\2\2X\22\3\2\2\2YZ\7.\2\2Z\24\3\2\2\2[\\\7*\2\2\\\26\3\2\2\2]^\7"+
		"+\2\2^\30\3\2\2\2_`\7A\2\2`\32\3\2\2\2aj\7?\2\2bc\7#\2\2cj\7?\2\2dj\t"+
		"\2\2\2ef\7@\2\2fj\7?\2\2gh\7>\2\2hj\7?\2\2ia\3\2\2\2ib\3\2\2\2id\3\2\2"+
		"\2ie\3\2\2\2ig\3\2\2\2j\34\3\2\2\2kq\5\'\24\2lp\5\'\24\2mp\5%\23\2np\t"+
		"\3\2\2ol\3\2\2\2om\3\2\2\2on\3\2\2\2ps\3\2\2\2qo\3\2\2\2qr\3\2\2\2r\36"+
		"\3\2\2\2sq\3\2\2\2tx\7$\2\2uw\n\4\2\2vu\3\2\2\2wz\3\2\2\2xv\3\2\2\2xy"+
		"\3\2\2\2y{\3\2\2\2zx\3\2\2\2{|\7$\2\2| \3\2\2\2}\177\7/\2\2~}\3\2\2\2"+
		"~\177\3\2\2\2\177\u0081\3\2\2\2\u0080\u0082\5%\23\2\u0081\u0080\3\2\2"+
		"\2\u0082\u0083\3\2\2\2\u0083\u0081\3\2\2\2\u0083\u0084\3\2\2\2\u0084\u008b"+
		"\3\2\2\2\u0085\u0087\7\60\2\2\u0086\u0088\5%\23\2\u0087\u0086\3\2\2\2"+
		"\u0088\u0089\3\2\2\2\u0089\u0087\3\2\2\2\u0089\u008a\3\2\2\2\u008a\u008c"+
		"\3\2\2\2\u008b\u0085\3\2\2\2\u008b\u008c\3\2\2\2\u008c\"\3\2\2\2\u008d"+
		"\u008e\7%\2\2\u008e\u008f\7P\2\2\u008f\u0090\7W\2\2\u0090\u0091\7N\2\2"+
		"\u0091\u0092\7N\2\2\u0092\u0093\7%\2\2\u0093$\3\2\2\2\u0094\u0095\4\62"+
		";\2\u0095&\3\2\2\2\u0096\u0097\t\5\2\2\u0097(\3\2\2\2\u0098\u009a\t\6"+
		"\2\2\u0099\u0098\3\2\2\2\u009a\u009b\3\2\2\2\u009b\u0099\3\2\2\2\u009b"+
		"\u009c\3\2\2\2\u009c\u009d\3\2\2\2\u009d\u009e\b\25\2\2\u009e*\3\2\2\2"+
		"\u009f\u00a0\7\61\2\2\u00a0\u00a1\7\61\2\2\u00a1\u00a5\3\2\2\2\u00a2\u00a4"+
		"\n\7\2\2\u00a3\u00a2\3\2\2\2\u00a4\u00a7\3\2\2\2\u00a5\u00a3\3\2\2\2\u00a5"+
		"\u00a6\3\2\2\2\u00a6\u00a8\3\2\2\2\u00a7\u00a5\3\2\2\2\u00a8\u00a9\b\26"+
		"\3\2\u00a9,\3\2\2\2\u00aa\u00ae\7}\2\2\u00ab\u00ad\13\2\2\2\u00ac\u00ab"+
		"\3\2\2\2\u00ad\u00b0\3\2\2\2\u00ae\u00af\3\2\2\2\u00ae\u00ac\3\2\2\2\u00af"+
		"\u00b1\3\2\2\2\u00b0\u00ae\3\2\2\2\u00b1\u00b2\7\177\2\2\u00b2.\3\2\2"+
		"\2\16\2ioqx~\u0083\u0089\u008b\u009b\u00a5\u00ae\4\3\25\2\3\26\3";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}