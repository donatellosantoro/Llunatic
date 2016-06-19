// Generated from Database.g4 by ANTLR 4.5.3

package it.unibas.lunatic.parser.output;

import speedy.model.expressions.Expression;
import it.unibas.lunatic.parser.operators.ParseDatabase;
import it.unibas.lunatic.parser.*;
import java.util.Stack;

import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class DatabaseParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.5.3", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, T__2=3, T__3=4, T__4=5, T__5=6, IDENTIFIER=7, STRING=8, 
		NUMBER=9, NULL=10, WHITESPACE=11, LINE_COMMENT=12;
	public static final int
		RULE_prog = 0, RULE_database = 1, RULE_schema = 2, RULE_relation = 3, 
		RULE_attrName = 4, RULE_instance = 5, RULE_fact = 6, RULE_attrValue = 7;
	public static final String[] ruleNames = {
		"prog", "database", "schema", "relation", "attrName", "instance", "fact", 
		"attrValue"
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

	@Override
	public String getGrammarFileName() { return "Database.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }


	private static org.apache.commons.logging.Log logger = org.apache.commons.logging.LogFactory.getLog(DatabaseParser.class.getName());
	private ParseDatabase generator;

	private ParserSchema currentSchema;
	private ParserInstance currentInstance;
	private ParserTable currentTable;
	private ParserFact currentFact;

	public void setGenerator(ParseDatabase generator) {
	      this.generator = generator;
	}

	public DatabaseParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}
	public static class ProgContext extends ParserRuleContext {
		public DatabaseContext database() {
			return getRuleContext(DatabaseContext.class,0);
		}
		public ProgContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_prog; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DatabaseListener ) ((DatabaseListener)listener).enterProg(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DatabaseListener ) ((DatabaseListener)listener).exitProg(this);
		}
	}

	public final ProgContext prog() throws RecognitionException {
		ProgContext _localctx = new ProgContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_prog);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(16);
			database();
			  
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class DatabaseContext extends ParserRuleContext {
		public SchemaContext schema() {
			return getRuleContext(SchemaContext.class,0);
		}
		public InstanceContext instance() {
			return getRuleContext(InstanceContext.class,0);
		}
		public DatabaseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_database; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DatabaseListener ) ((DatabaseListener)listener).enterDatabase(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DatabaseListener ) ((DatabaseListener)listener).exitDatabase(this);
		}
	}

	public final DatabaseContext database() throws RecognitionException {
		DatabaseContext _localctx = new DatabaseContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_database);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(19);
			schema();
			setState(21);
			_la = _input.LA(1);
			if (_la==T__4) {
				{
				setState(20);
				instance();
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class SchemaContext extends ParserRuleContext {
		public List<RelationContext> relation() {
			return getRuleContexts(RelationContext.class);
		}
		public RelationContext relation(int i) {
			return getRuleContext(RelationContext.class,i);
		}
		public SchemaContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_schema; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DatabaseListener ) ((DatabaseListener)listener).enterSchema(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DatabaseListener ) ((DatabaseListener)listener).exitSchema(this);
		}
	}

	public final SchemaContext schema() throws RecognitionException {
		SchemaContext _localctx = new SchemaContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_schema);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(23);
			match(T__0);
			 currentSchema = new ParserSchema(); 
			setState(26); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(25);
				relation();
				}
				}
				setState(28); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==IDENTIFIER );
			 generator.setSchema(currentSchema); 
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class RelationContext extends ParserRuleContext {
		public Token set;
		public List<AttrNameContext> attrName() {
			return getRuleContexts(AttrNameContext.class);
		}
		public AttrNameContext attrName(int i) {
			return getRuleContext(AttrNameContext.class,i);
		}
		public TerminalNode IDENTIFIER() { return getToken(DatabaseParser.IDENTIFIER, 0); }
		public RelationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_relation; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DatabaseListener ) ((DatabaseListener)listener).enterRelation(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DatabaseListener ) ((DatabaseListener)listener).exitRelation(this);
		}
	}

	public final RelationContext relation() throws RecognitionException {
		RelationContext _localctx = new RelationContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_relation);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(32);
			((RelationContext)_localctx).set = match(IDENTIFIER);
			 currentTable = new ParserTable(((RelationContext)_localctx).set.getText()); 
			setState(34);
			match(T__1);
			setState(35);
			attrName();
			setState(40);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__2) {
				{
				{
				setState(36);
				match(T__2);
				setState(37);
				attrName();
				}
				}
				setState(42);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(43);
			match(T__3);
			 currentSchema.addTable(currentTable); 
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class AttrNameContext extends ParserRuleContext {
		public Token attr;
		public TerminalNode IDENTIFIER() { return getToken(DatabaseParser.IDENTIFIER, 0); }
		public AttrNameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_attrName; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DatabaseListener ) ((DatabaseListener)listener).enterAttrName(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DatabaseListener ) ((DatabaseListener)listener).exitAttrName(this);
		}
	}

	public final AttrNameContext attrName() throws RecognitionException {
		AttrNameContext _localctx = new AttrNameContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_attrName);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(46);
			((AttrNameContext)_localctx).attr = match(IDENTIFIER);
			 currentTable.addAttribute(new ParserAttribute(((AttrNameContext)_localctx).attr.getText(), null));  
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class InstanceContext extends ParserRuleContext {
		public List<FactContext> fact() {
			return getRuleContexts(FactContext.class);
		}
		public FactContext fact(int i) {
			return getRuleContext(FactContext.class,i);
		}
		public InstanceContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_instance; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DatabaseListener ) ((DatabaseListener)listener).enterInstance(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DatabaseListener ) ((DatabaseListener)listener).exitInstance(this);
		}
	}

	public final InstanceContext instance() throws RecognitionException {
		InstanceContext _localctx = new InstanceContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_instance);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(49);
			match(T__4);
			 currentInstance = new ParserInstance(); 
			setState(52); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(51);
				fact();
				}
				}
				setState(54); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==IDENTIFIER );
			 generator.setInstance(currentInstance); 
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class FactContext extends ParserRuleContext {
		public Token set;
		public List<AttrValueContext> attrValue() {
			return getRuleContexts(AttrValueContext.class);
		}
		public AttrValueContext attrValue(int i) {
			return getRuleContext(AttrValueContext.class,i);
		}
		public TerminalNode IDENTIFIER() { return getToken(DatabaseParser.IDENTIFIER, 0); }
		public FactContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_fact; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DatabaseListener ) ((DatabaseListener)listener).enterFact(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DatabaseListener ) ((DatabaseListener)listener).exitFact(this);
		}
	}

	public final FactContext fact() throws RecognitionException {
		FactContext _localctx = new FactContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_fact);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(58);
			((FactContext)_localctx).set = match(IDENTIFIER);
			 currentFact = new ParserFact(((FactContext)_localctx).set.getText()); 
			setState(60);
			match(T__1);
			setState(61);
			attrValue();
			setState(66);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__2) {
				{
				{
				setState(62);
				match(T__2);
				setState(63);
				attrValue();
				}
				}
				setState(68);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(69);
			match(T__3);
			 currentInstance.addFact(currentFact); 
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class AttrValueContext extends ParserRuleContext {
		public Token attr;
		public Token val;
		public TerminalNode IDENTIFIER() { return getToken(DatabaseParser.IDENTIFIER, 0); }
		public TerminalNode NULL() { return getToken(DatabaseParser.NULL, 0); }
		public TerminalNode STRING() { return getToken(DatabaseParser.STRING, 0); }
		public TerminalNode NUMBER() { return getToken(DatabaseParser.NUMBER, 0); }
		public AttrValueContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_attrValue; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DatabaseListener ) ((DatabaseListener)listener).enterAttrValue(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DatabaseListener ) ((DatabaseListener)listener).exitAttrValue(this);
		}
	}

	public final AttrValueContext attrValue() throws RecognitionException {
		AttrValueContext _localctx = new AttrValueContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_attrValue);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(72);
			((AttrValueContext)_localctx).attr = match(IDENTIFIER);
			setState(73);
			match(T__5);
			setState(74);
			((AttrValueContext)_localctx).val = _input.LT(1);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << STRING) | (1L << NUMBER) | (1L << NULL))) != 0)) ) {
				((AttrValueContext)_localctx).val = (Token)_errHandler.recoverInline(this);
			} else {
				consume();
			}
			 currentFact.addAttribute(new ParserAttribute(((AttrValueContext)_localctx).attr.getText(), ((AttrValueContext)_localctx).val.getText()));  
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static final String _serializedATN =
		"\3\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\3\16P\4\2\t\2\4\3\t"+
		"\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\3\2\3\2\3\2\3\3\3\3"+
		"\5\3\30\n\3\3\4\3\4\3\4\6\4\35\n\4\r\4\16\4\36\3\4\3\4\3\5\3\5\3\5\3\5"+
		"\3\5\3\5\7\5)\n\5\f\5\16\5,\13\5\3\5\3\5\3\5\3\6\3\6\3\6\3\7\3\7\3\7\6"+
		"\7\67\n\7\r\7\16\78\3\7\3\7\3\b\3\b\3\b\3\b\3\b\3\b\7\bC\n\b\f\b\16\b"+
		"F\13\b\3\b\3\b\3\b\3\t\3\t\3\t\3\t\3\t\3\t\2\2\n\2\4\6\b\n\f\16\20\2\3"+
		"\3\2\n\fL\2\22\3\2\2\2\4\25\3\2\2\2\6\31\3\2\2\2\b\"\3\2\2\2\n\60\3\2"+
		"\2\2\f\63\3\2\2\2\16<\3\2\2\2\20J\3\2\2\2\22\23\5\4\3\2\23\24\b\2\1\2"+
		"\24\3\3\2\2\2\25\27\5\6\4\2\26\30\5\f\7\2\27\26\3\2\2\2\27\30\3\2\2\2"+
		"\30\5\3\2\2\2\31\32\7\3\2\2\32\34\b\4\1\2\33\35\5\b\5\2\34\33\3\2\2\2"+
		"\35\36\3\2\2\2\36\34\3\2\2\2\36\37\3\2\2\2\37 \3\2\2\2 !\b\4\1\2!\7\3"+
		"\2\2\2\"#\7\t\2\2#$\b\5\1\2$%\7\4\2\2%*\5\n\6\2&\'\7\5\2\2\')\5\n\6\2"+
		"(&\3\2\2\2),\3\2\2\2*(\3\2\2\2*+\3\2\2\2+-\3\2\2\2,*\3\2\2\2-.\7\6\2\2"+
		"./\b\5\1\2/\t\3\2\2\2\60\61\7\t\2\2\61\62\b\6\1\2\62\13\3\2\2\2\63\64"+
		"\7\7\2\2\64\66\b\7\1\2\65\67\5\16\b\2\66\65\3\2\2\2\678\3\2\2\28\66\3"+
		"\2\2\289\3\2\2\29:\3\2\2\2:;\b\7\1\2;\r\3\2\2\2<=\7\t\2\2=>\b\b\1\2>?"+
		"\7\4\2\2?D\5\20\t\2@A\7\5\2\2AC\5\20\t\2B@\3\2\2\2CF\3\2\2\2DB\3\2\2\2"+
		"DE\3\2\2\2EG\3\2\2\2FD\3\2\2\2GH\7\6\2\2HI\b\b\1\2I\17\3\2\2\2JK\7\t\2"+
		"\2KL\7\b\2\2LM\t\2\2\2MN\b\t\1\2N\21\3\2\2\2\7\27\36*8D";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}