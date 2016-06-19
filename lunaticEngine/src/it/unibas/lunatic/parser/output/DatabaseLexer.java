// Generated from Database.g4 by ANTLR 4.5.3

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
public class DatabaseLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.5.3", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, T__2=3, T__3=4, T__4=5, T__5=6, IDENTIFIER=7, STRING=8, 
		NUMBER=9, NULL=10, WHITESPACE=11, LINE_COMMENT=12;
	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	public static final String[] ruleNames = {
		"T__0", "T__1", "T__2", "T__3", "T__4", "T__5", "IDENTIFIER", "STRING", 
		"NUMBER", "NULL", "DIGIT", "LETTER", "WHITESPACE", "LINE_COMMENT"
	};

	private static final String[] _LITERAL_NAMES = {
		null, "'SCHEMA:'", "'('", "','", "')'", "'INSTANCE:'", "':'", null, null, 
		null, "'#NULL#'"
	};
	private static final String[] _SYMBOLIC_NAMES = {
		null, null, null, null, null, null, null, "IDENTIFIER", "STRING", "NUMBER", 
		"NULL", "WHITESPACE", "LINE_COMMENT"
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


	public DatabaseLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "Database.g4"; }

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
		case 12:
			WHITESPACE_action((RuleContext)_localctx, actionIndex);
			break;
		case 13:
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
		"\3\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\2\16y\b\1\4\2\t\2\4"+
		"\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13\t"+
		"\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3"+
		"\2\3\3\3\3\3\4\3\4\3\5\3\5\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\7"+
		"\3\7\3\b\3\b\3\b\3\b\7\b>\n\b\f\b\16\bA\13\b\3\t\3\t\3\t\3\t\6\tG\n\t"+
		"\r\t\16\tH\3\t\3\t\3\n\5\nN\n\n\3\n\6\nQ\n\n\r\n\16\nR\3\n\3\n\6\nW\n"+
		"\n\r\n\16\nX\5\n[\n\n\3\13\3\13\3\13\3\13\3\13\3\13\3\13\3\f\3\f\3\r\3"+
		"\r\3\16\6\16i\n\16\r\16\16\16j\3\16\3\16\3\17\3\17\3\17\3\17\7\17s\n\17"+
		"\f\17\16\17v\13\17\3\17\3\17\2\2\20\3\3\5\4\7\5\t\6\13\7\r\b\17\t\21\n"+
		"\23\13\25\f\27\2\31\2\33\r\35\16\3\2\7\4\2/\60aa\5\2\"\"/\60BB\4\2C\\"+
		"c|\5\2\13\f\16\17\"\"\4\2\f\f\17\17\u0082\2\3\3\2\2\2\2\5\3\2\2\2\2\7"+
		"\3\2\2\2\2\t\3\2\2\2\2\13\3\2\2\2\2\r\3\2\2\2\2\17\3\2\2\2\2\21\3\2\2"+
		"\2\2\23\3\2\2\2\2\25\3\2\2\2\2\33\3\2\2\2\2\35\3\2\2\2\3\37\3\2\2\2\5"+
		"\'\3\2\2\2\7)\3\2\2\2\t+\3\2\2\2\13-\3\2\2\2\r\67\3\2\2\2\179\3\2\2\2"+
		"\21B\3\2\2\2\23M\3\2\2\2\25\\\3\2\2\2\27c\3\2\2\2\31e\3\2\2\2\33h\3\2"+
		"\2\2\35n\3\2\2\2\37 \7U\2\2 !\7E\2\2!\"\7J\2\2\"#\7G\2\2#$\7O\2\2$%\7"+
		"C\2\2%&\7<\2\2&\4\3\2\2\2\'(\7*\2\2(\6\3\2\2\2)*\7.\2\2*\b\3\2\2\2+,\7"+
		"+\2\2,\n\3\2\2\2-.\7K\2\2./\7P\2\2/\60\7U\2\2\60\61\7V\2\2\61\62\7C\2"+
		"\2\62\63\7P\2\2\63\64\7E\2\2\64\65\7G\2\2\65\66\7<\2\2\66\f\3\2\2\2\67"+
		"8\7<\2\28\16\3\2\2\29?\5\31\r\2:>\5\31\r\2;>\5\27\f\2<>\t\2\2\2=:\3\2"+
		"\2\2=;\3\2\2\2=<\3\2\2\2>A\3\2\2\2?=\3\2\2\2?@\3\2\2\2@\20\3\2\2\2A?\3"+
		"\2\2\2BF\7$\2\2CG\5\31\r\2DG\5\27\f\2EG\t\3\2\2FC\3\2\2\2FD\3\2\2\2FE"+
		"\3\2\2\2GH\3\2\2\2HF\3\2\2\2HI\3\2\2\2IJ\3\2\2\2JK\7$\2\2K\22\3\2\2\2"+
		"LN\7/\2\2ML\3\2\2\2MN\3\2\2\2NP\3\2\2\2OQ\5\27\f\2PO\3\2\2\2QR\3\2\2\2"+
		"RP\3\2\2\2RS\3\2\2\2SZ\3\2\2\2TV\7\60\2\2UW\5\27\f\2VU\3\2\2\2WX\3\2\2"+
		"\2XV\3\2\2\2XY\3\2\2\2Y[\3\2\2\2ZT\3\2\2\2Z[\3\2\2\2[\24\3\2\2\2\\]\7"+
		"%\2\2]^\7P\2\2^_\7W\2\2_`\7N\2\2`a\7N\2\2ab\7%\2\2b\26\3\2\2\2cd\4\62"+
		";\2d\30\3\2\2\2ef\t\4\2\2f\32\3\2\2\2gi\t\5\2\2hg\3\2\2\2ij\3\2\2\2jh"+
		"\3\2\2\2jk\3\2\2\2kl\3\2\2\2lm\b\16\2\2m\34\3\2\2\2no\7\61\2\2op\7\61"+
		"\2\2pt\3\2\2\2qs\n\6\2\2rq\3\2\2\2sv\3\2\2\2tr\3\2\2\2tu\3\2\2\2uw\3\2"+
		"\2\2vt\3\2\2\2wx\b\17\3\2x\36\3\2\2\2\r\2=?FHMRXZjt\4\3\16\2\3\17\3";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}