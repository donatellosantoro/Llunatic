// Generated from DependenciesCF.g4 by ANTLR 4.5.3

package it.unibas.lunatic.parser.output;

import it.unibas.lunatic.LunaticConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Stack;
import it.unibas.lunatic.parser.operators.ParseDependenciesCF;
import it.unibas.lunatic.model.dependency.*;
import speedy.model.database.AttributeRef;
import speedy.model.expressions.Expression;

import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class DependenciesCFParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.5.3", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, T__2=3, T__3=4, T__4=5, T__5=6, T__6=7, T__7=8, T__8=9, 
		T__9=10, T__10=11, T__11=12, OPERATOR=13, IDENTIFIER=14, STRING=15, NUMBER=16, 
		NULL=17, WHITESPACE=18, LINE_COMMENT=19, EXPRESSION=20;
	public static final int
		RULE_prog = 0, RULE_dependencies = 1, RULE_sttgd = 2, RULE_etgd = 3, RULE_egd = 4, 
		RULE_query = 5, RULE_dependency = 6, RULE_querydependency = 7, RULE_positiveFormula = 8, 
		RULE_conclusionFormula = 9, RULE_conclusionQueryFormula = 10, RULE_atom = 11, 
		RULE_relationalAtom = 12, RULE_queryAtom = 13, RULE_builtin = 14, RULE_comparison = 15, 
		RULE_leftargument = 16, RULE_rightargument = 17, RULE_attribute = 18, 
		RULE_queryattribute = 19, RULE_value = 20, RULE_queryvalue = 21;
	public static final String[] ruleNames = {
		"prog", "dependencies", "sttgd", "etgd", "egd", "query", "dependency", 
		"querydependency", "positiveFormula", "conclusionFormula", "conclusionQueryFormula", 
		"atom", "relationalAtom", "queryAtom", "builtin", "comparison", "leftargument", 
		"rightargument", "attribute", "queryattribute", "value", "queryvalue"
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

	@Override
	public String getGrammarFileName() { return "DependenciesCF.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }


	private static Logger logger = LoggerFactory.getLogger(DependenciesParser.class);

	private ParseDependenciesCF generator = new ParseDependenciesCF();

	private Stack<IFormula> formulaStack = new Stack<IFormula>();

	private Dependency dependency;
	private IFormula formulaWN;
	private PositiveFormula positiveFormula;
	private IFormulaAtom atom;
	private FormulaAttribute attribute;
	private StringBuilder expressionString;
	private String leftConstant;
	private String rightConstant;
	private int counter;
	private int attributePosition;
	private boolean inPremise;
	private boolean stTGD;

	public void setGenerator(ParseDependenciesCF generator) {
	      this.generator = generator;
	}


	public DependenciesCFParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}
	public static class ProgContext extends ParserRuleContext {
		public DependenciesContext dependencies() {
			return getRuleContext(DependenciesContext.class,0);
		}
		public ProgContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_prog; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DependenciesCFListener ) ((DependenciesCFListener)listener).enterProg(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DependenciesCFListener ) ((DependenciesCFListener)listener).exitProg(this);
		}
	}

	public final ProgContext prog() throws RecognitionException {
		ProgContext _localctx = new ProgContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_prog);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(44);
			dependencies();
			  
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

	public static class DependenciesContext extends ParserRuleContext {
		public List<SttgdContext> sttgd() {
			return getRuleContexts(SttgdContext.class);
		}
		public SttgdContext sttgd(int i) {
			return getRuleContext(SttgdContext.class,i);
		}
		public List<EtgdContext> etgd() {
			return getRuleContexts(EtgdContext.class);
		}
		public EtgdContext etgd(int i) {
			return getRuleContext(EtgdContext.class,i);
		}
		public List<EgdContext> egd() {
			return getRuleContexts(EgdContext.class);
		}
		public EgdContext egd(int i) {
			return getRuleContext(EgdContext.class,i);
		}
		public List<QueryContext> query() {
			return getRuleContexts(QueryContext.class);
		}
		public QueryContext query(int i) {
			return getRuleContext(QueryContext.class,i);
		}
		public DependenciesContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_dependencies; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DependenciesCFListener ) ((DependenciesCFListener)listener).enterDependencies(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DependenciesCFListener ) ((DependenciesCFListener)listener).exitDependencies(this);
		}
	}

	public final DependenciesContext dependencies() throws RecognitionException {
		DependenciesContext _localctx = new DependenciesContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_dependencies);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(55);
			_la = _input.LA(1);
			if (_la==T__0) {
				{
				setState(47);
				match(T__0);
				setState(49); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(48);
					sttgd();
					}
					}
					setState(51); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==IDENTIFIER );
				 counter = 1;
				}
			}

			setState(65);
			_la = _input.LA(1);
			if (_la==T__1) {
				{
				setState(57);
				match(T__1);
				setState(59); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(58);
					etgd();
					}
					}
					setState(61); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==IDENTIFIER );
				 counter = 1;
				}
			}

			setState(75);
			_la = _input.LA(1);
			if (_la==T__2) {
				{
				setState(67);
				match(T__2);
				setState(69); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(68);
					egd();
					}
					}
					setState(71); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==IDENTIFIER );
				 counter = 1;
				}
			}

			setState(85);
			_la = _input.LA(1);
			if (_la==T__3) {
				{
				setState(77);
				match(T__3);
				setState(79); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(78);
					query();
					}
					}
					setState(81); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==IDENTIFIER );
				 counter = 1;
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

	public static class SttgdContext extends ParserRuleContext {
		public DependencyContext dependency() {
			return getRuleContext(DependencyContext.class,0);
		}
		public SttgdContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_sttgd; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DependenciesCFListener ) ((DependenciesCFListener)listener).enterSttgd(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DependenciesCFListener ) ((DependenciesCFListener)listener).exitSttgd(this);
		}
	}

	public final SttgdContext sttgd() throws RecognitionException {
		SttgdContext _localctx = new SttgdContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_sttgd);
		try {
			enterOuterAlt(_localctx, 1);
			{
			 stTGD = true; 
			setState(88);
			dependency();
			 dependency.setType(LunaticConstants.STTGD); dependency.setId("m" + counter++); generator.addSTTGD(dependency); 
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

	public static class EtgdContext extends ParserRuleContext {
		public DependencyContext dependency() {
			return getRuleContext(DependencyContext.class,0);
		}
		public EtgdContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_etgd; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DependenciesCFListener ) ((DependenciesCFListener)listener).enterEtgd(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DependenciesCFListener ) ((DependenciesCFListener)listener).exitEtgd(this);
		}
	}

	public final EtgdContext etgd() throws RecognitionException {
		EtgdContext _localctx = new EtgdContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_etgd);
		try {
			enterOuterAlt(_localctx, 1);
			{
			 stTGD = false; 
			setState(92);
			dependency();
			 dependency.setType(LunaticConstants.ExtTGD); dependency.setId("t" + counter++); generator.addExtTGD(dependency); 
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

	public static class EgdContext extends ParserRuleContext {
		public DependencyContext dependency() {
			return getRuleContext(DependencyContext.class,0);
		}
		public EgdContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_egd; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DependenciesCFListener ) ((DependenciesCFListener)listener).enterEgd(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DependenciesCFListener ) ((DependenciesCFListener)listener).exitEgd(this);
		}
	}

	public final EgdContext egd() throws RecognitionException {
		EgdContext _localctx = new EgdContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_egd);
		try {
			enterOuterAlt(_localctx, 1);
			{
			 stTGD = false; 
			setState(96);
			dependency();
			 dependency.setType(LunaticConstants.EGD); dependency.setId("e" + counter++); generator.addEGD(dependency); 
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

	public static class QueryContext extends ParserRuleContext {
		public QuerydependencyContext querydependency() {
			return getRuleContext(QuerydependencyContext.class,0);
		}
		public QueryContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_query; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DependenciesCFListener ) ((DependenciesCFListener)listener).enterQuery(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DependenciesCFListener ) ((DependenciesCFListener)listener).exitQuery(this);
		}
	}

	public final QueryContext query() throws RecognitionException {
		QueryContext _localctx = new QueryContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_query);
		try {
			enterOuterAlt(_localctx, 1);
			{
			 stTGD = false; 
			setState(100);
			querydependency();
			 dependency.setType(LunaticConstants.QUERY); dependency.setId("q" + counter++); generator.addQuery(dependency); 
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

	public static class DependencyContext extends ParserRuleContext {
		public Token id;
		public PositiveFormulaContext positiveFormula() {
			return getRuleContext(PositiveFormulaContext.class,0);
		}
		public ConclusionFormulaContext conclusionFormula() {
			return getRuleContext(ConclusionFormulaContext.class,0);
		}
		public TerminalNode IDENTIFIER() { return getToken(DependenciesCFParser.IDENTIFIER, 0); }
		public DependencyContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_dependency; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DependenciesCFListener ) ((DependenciesCFListener)listener).enterDependency(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DependenciesCFListener ) ((DependenciesCFListener)listener).exitDependency(this);
		}
	}

	public final DependencyContext dependency() throws RecognitionException {
		DependencyContext _localctx = new DependencyContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_dependency);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(105);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,8,_ctx) ) {
			case 1:
				{
				setState(103);
				((DependencyContext)_localctx).id = match(IDENTIFIER);
				setState(104);
				match(T__4);
				}
				break;
			}
			  dependency = new Dependency(); 
			                    formulaWN = new FormulaWithNegations(); 
			                    formulaStack.push(formulaWN);
			                    dependency.setPremise(formulaWN);
			                    inPremise = true;
			                    if(((DependencyContext)_localctx).id!=null) dependency.setId(((DependencyContext)_localctx).id.getText()); 
			setState(108);
			positiveFormula();
			setState(109);
			match(T__5);
			{
			  formulaStack.clear(); inPremise = false;
			setState(111);
			conclusionFormula();
			}
			setState(113);
			match(T__6);
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

	public static class QuerydependencyContext extends ParserRuleContext {
		public Token id;
		public ConclusionQueryFormulaContext conclusionQueryFormula() {
			return getRuleContext(ConclusionQueryFormulaContext.class,0);
		}
		public PositiveFormulaContext positiveFormula() {
			return getRuleContext(PositiveFormulaContext.class,0);
		}
		public TerminalNode IDENTIFIER() { return getToken(DependenciesCFParser.IDENTIFIER, 0); }
		public QuerydependencyContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_querydependency; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DependenciesCFListener ) ((DependenciesCFListener)listener).enterQuerydependency(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DependenciesCFListener ) ((DependenciesCFListener)listener).exitQuerydependency(this);
		}
	}

	public final QuerydependencyContext querydependency() throws RecognitionException {
		QuerydependencyContext _localctx = new QuerydependencyContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_querydependency);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(117);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,9,_ctx) ) {
			case 1:
				{
				setState(115);
				((QuerydependencyContext)_localctx).id = match(IDENTIFIER);
				setState(116);
				match(T__4);
				}
				break;
			}
			  dependency = new Dependency(); 
			                    formulaWN = new FormulaWithNegations(); 
			                    formulaStack.clear(); 
			                    formulaStack.push(formulaWN);
			                    dependency.setPremise(formulaWN);
			                    inPremise = false;
			                    if(((QuerydependencyContext)_localctx).id!=null) dependency.setId(((QuerydependencyContext)_localctx).id.getText()); 
			setState(120);
			conclusionQueryFormula();
			setState(121);
			match(T__7);
			{
			   inPremise = true;
			setState(123);
			positiveFormula();
			}
			setState(125);
			match(T__6);
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

	public static class PositiveFormulaContext extends ParserRuleContext {
		public RelationalAtomContext relationalAtom() {
			return getRuleContext(RelationalAtomContext.class,0);
		}
		public List<AtomContext> atom() {
			return getRuleContexts(AtomContext.class);
		}
		public AtomContext atom(int i) {
			return getRuleContext(AtomContext.class,i);
		}
		public PositiveFormulaContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_positiveFormula; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DependenciesCFListener ) ((DependenciesCFListener)listener).enterPositiveFormula(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DependenciesCFListener ) ((DependenciesCFListener)listener).exitPositiveFormula(this);
		}
	}

	public final PositiveFormulaContext positiveFormula() throws RecognitionException {
		PositiveFormulaContext _localctx = new PositiveFormulaContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_positiveFormula);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			  positiveFormula = new PositiveFormula(); 
			                    positiveFormula.setFather(formulaStack.peek()); 
			                    formulaStack.peek().setPositiveFormula(positiveFormula); 
			setState(128);
			relationalAtom();
			setState(133);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__8) {
				{
				{
				setState(129);
				match(T__8);
				setState(130);
				atom();
				}
				}
				setState(135);
				_errHandler.sync(this);
				_la = _input.LA(1);
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

	public static class ConclusionFormulaContext extends ParserRuleContext {
		public List<AtomContext> atom() {
			return getRuleContexts(AtomContext.class);
		}
		public AtomContext atom(int i) {
			return getRuleContext(AtomContext.class,i);
		}
		public ConclusionFormulaContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_conclusionFormula; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DependenciesCFListener ) ((DependenciesCFListener)listener).enterConclusionFormula(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DependenciesCFListener ) ((DependenciesCFListener)listener).exitConclusionFormula(this);
		}
	}

	public final ConclusionFormulaContext conclusionFormula() throws RecognitionException {
		ConclusionFormulaContext _localctx = new ConclusionFormulaContext(_ctx, getState());
		enterRule(_localctx, 18, RULE_conclusionFormula);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			  positiveFormula = new PositiveFormula(); 
			                      dependency.setConclusion(positiveFormula); 
			setState(137);
			atom();
			setState(142);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__8) {
				{
				{
				setState(138);
				match(T__8);
				setState(139);
				atom();
				}
				}
				setState(144);
				_errHandler.sync(this);
				_la = _input.LA(1);
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

	public static class ConclusionQueryFormulaContext extends ParserRuleContext {
		public QueryAtomContext queryAtom() {
			return getRuleContext(QueryAtomContext.class,0);
		}
		public ConclusionQueryFormulaContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_conclusionQueryFormula; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DependenciesCFListener ) ((DependenciesCFListener)listener).enterConclusionQueryFormula(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DependenciesCFListener ) ((DependenciesCFListener)listener).exitConclusionQueryFormula(this);
		}
	}

	public final ConclusionQueryFormulaContext conclusionQueryFormula() throws RecognitionException {
		ConclusionQueryFormulaContext _localctx = new ConclusionQueryFormulaContext(_ctx, getState());
		enterRule(_localctx, 20, RULE_conclusionQueryFormula);
		try {
			enterOuterAlt(_localctx, 1);
			{
			  positiveFormula = new PositiveFormula(); 
			                      dependency.setConclusion(positiveFormula); 
			setState(146);
			queryAtom();
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

	public static class AtomContext extends ParserRuleContext {
		public RelationalAtomContext relationalAtom() {
			return getRuleContext(RelationalAtomContext.class,0);
		}
		public BuiltinContext builtin() {
			return getRuleContext(BuiltinContext.class,0);
		}
		public ComparisonContext comparison() {
			return getRuleContext(ComparisonContext.class,0);
		}
		public AtomContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_atom; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DependenciesCFListener ) ((DependenciesCFListener)listener).enterAtom(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DependenciesCFListener ) ((DependenciesCFListener)listener).exitAtom(this);
		}
	}

	public final AtomContext atom() throws RecognitionException {
		AtomContext _localctx = new AtomContext(_ctx, getState());
		enterRule(_localctx, 22, RULE_atom);
		try {
			setState(151);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,12,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(148);
				relationalAtom();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(149);
				builtin();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(150);
				comparison();
				}
				break;
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

	public static class RelationalAtomContext extends ParserRuleContext {
		public Token name;
		public List<AttributeContext> attribute() {
			return getRuleContexts(AttributeContext.class);
		}
		public AttributeContext attribute(int i) {
			return getRuleContext(AttributeContext.class,i);
		}
		public TerminalNode IDENTIFIER() { return getToken(DependenciesCFParser.IDENTIFIER, 0); }
		public RelationalAtomContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_relationalAtom; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DependenciesCFListener ) ((DependenciesCFListener)listener).enterRelationalAtom(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DependenciesCFListener ) ((DependenciesCFListener)listener).exitRelationalAtom(this);
		}
	}

	public final RelationalAtomContext relationalAtom() throws RecognitionException {
		RelationalAtomContext _localctx = new RelationalAtomContext(_ctx, getState());
		enterRule(_localctx, 24, RULE_relationalAtom);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(153);
			((RelationalAtomContext)_localctx).name = match(IDENTIFIER);
			 atom = new RelationalAtom(generator.cleanTableName(((RelationalAtomContext)_localctx).name.getText())); attributePosition = 0; 
			setState(155);
			match(T__9);
			setState(156);
			attribute();
			setState(161);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__8) {
				{
				{
				setState(157);
				match(T__8);
				setState(158);
				attribute();
				}
				}
				setState(163);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(164);
			match(T__10);
			  positiveFormula.addAtom(atom); atom.setFormula(positiveFormula); 
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

	public static class QueryAtomContext extends ParserRuleContext {
		public Token name;
		public List<QueryattributeContext> queryattribute() {
			return getRuleContexts(QueryattributeContext.class);
		}
		public QueryattributeContext queryattribute(int i) {
			return getRuleContext(QueryattributeContext.class,i);
		}
		public TerminalNode IDENTIFIER() { return getToken(DependenciesCFParser.IDENTIFIER, 0); }
		public QueryAtomContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_queryAtom; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DependenciesCFListener ) ((DependenciesCFListener)listener).enterQueryAtom(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DependenciesCFListener ) ((DependenciesCFListener)listener).exitQueryAtom(this);
		}
	}

	public final QueryAtomContext queryAtom() throws RecognitionException {
		QueryAtomContext _localctx = new QueryAtomContext(_ctx, getState());
		enterRule(_localctx, 26, RULE_queryAtom);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(167);
			((QueryAtomContext)_localctx).name = match(IDENTIFIER);
			 atom = new QueryAtom(((QueryAtomContext)_localctx).name.getText()); attributePosition = 0; 
			setState(169);
			match(T__9);
			setState(170);
			queryattribute();
			setState(175);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__8) {
				{
				{
				setState(171);
				match(T__8);
				setState(172);
				queryattribute();
				}
				}
				setState(177);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(178);
			match(T__10);
			  positiveFormula.addAtom(atom); atom.setFormula(positiveFormula); 
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

	public static class BuiltinContext extends ParserRuleContext {
		public Token expression;
		public TerminalNode EXPRESSION() { return getToken(DependenciesCFParser.EXPRESSION, 0); }
		public BuiltinContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_builtin; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DependenciesCFListener ) ((DependenciesCFListener)listener).enterBuiltin(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DependenciesCFListener ) ((DependenciesCFListener)listener).exitBuiltin(this);
		}
	}

	public final BuiltinContext builtin() throws RecognitionException {
		BuiltinContext _localctx = new BuiltinContext(_ctx, getState());
		enterRule(_localctx, 28, RULE_builtin);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(181);
			((BuiltinContext)_localctx).expression = match(EXPRESSION);
			  atom = new BuiltInAtom(positiveFormula, new Expression(generator.clean(((BuiltinContext)_localctx).expression.getText()))); 
			                    positiveFormula.addAtom(atom);  
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

	public static class ComparisonContext extends ParserRuleContext {
		public Token oper;
		public LeftargumentContext leftargument() {
			return getRuleContext(LeftargumentContext.class,0);
		}
		public RightargumentContext rightargument() {
			return getRuleContext(RightargumentContext.class,0);
		}
		public TerminalNode OPERATOR() { return getToken(DependenciesCFParser.OPERATOR, 0); }
		public ComparisonContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_comparison; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DependenciesCFListener ) ((DependenciesCFListener)listener).enterComparison(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DependenciesCFListener ) ((DependenciesCFListener)listener).exitComparison(this);
		}
	}

	public final ComparisonContext comparison() throws RecognitionException {
		ComparisonContext _localctx = new ComparisonContext(_ctx, getState());
		enterRule(_localctx, 30, RULE_comparison);
		try {
			enterOuterAlt(_localctx, 1);
			{
			   expressionString = new StringBuilder(); 
					     leftConstant = null;
					     rightConstant = null;
			setState(185);
			leftargument();
			setState(186);
			((ComparisonContext)_localctx).oper = match(OPERATOR);
			 
			                 	String operatorText = ((ComparisonContext)_localctx).oper.getText();
			                 	if(operatorText.equals("=")){
			                 	   operatorText = "==";
			                 	}
			                 	expressionString.append(" ").append(operatorText); 
			                 
			setState(188);
			rightargument();
			  Expression expression = new Expression(expressionString.toString()); 
			                    atom = new ComparisonAtom(positiveFormula, expression, leftConstant, rightConstant, ((ComparisonContext)_localctx).oper.getText()); 
			                    positiveFormula.addAtom(atom); 
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

	public static class LeftargumentContext extends ParserRuleContext {
		public Token var;
		public Token constant;
		public TerminalNode IDENTIFIER() { return getToken(DependenciesCFParser.IDENTIFIER, 0); }
		public TerminalNode STRING() { return getToken(DependenciesCFParser.STRING, 0); }
		public TerminalNode NUMBER() { return getToken(DependenciesCFParser.NUMBER, 0); }
		public LeftargumentContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_leftargument; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DependenciesCFListener ) ((DependenciesCFListener)listener).enterLeftargument(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DependenciesCFListener ) ((DependenciesCFListener)listener).exitLeftargument(this);
		}
	}

	public final LeftargumentContext leftargument() throws RecognitionException {
		LeftargumentContext _localctx = new LeftargumentContext(_ctx, getState());
		enterRule(_localctx, 32, RULE_leftargument);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(196);
			switch (_input.LA(1)) {
			case T__11:
				{
				setState(191);
				match(T__11);
				setState(192);
				((LeftargumentContext)_localctx).var = match(IDENTIFIER);
				 expressionString.append(((LeftargumentContext)_localctx).var.getText()); 
				}
				break;
			case IDENTIFIER:
			case STRING:
			case NUMBER:
				{
				setState(194);
				((LeftargumentContext)_localctx).constant = _input.LT(1);
				_la = _input.LA(1);
				if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << IDENTIFIER) | (1L << STRING) | (1L << NUMBER))) != 0)) ) {
					((LeftargumentContext)_localctx).constant = (Token)_errHandler.recoverInline(this);
				} else {
					consume();
				}
				 expressionString.append(((LeftargumentContext)_localctx).constant.getText()); leftConstant = ((LeftargumentContext)_localctx).constant.getText();
				}
				break;
			default:
				throw new NoViableAltException(this);
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

	public static class RightargumentContext extends ParserRuleContext {
		public Token var;
		public Token constant;
		public TerminalNode IDENTIFIER() { return getToken(DependenciesCFParser.IDENTIFIER, 0); }
		public TerminalNode STRING() { return getToken(DependenciesCFParser.STRING, 0); }
		public TerminalNode NUMBER() { return getToken(DependenciesCFParser.NUMBER, 0); }
		public RightargumentContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_rightargument; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DependenciesCFListener ) ((DependenciesCFListener)listener).enterRightargument(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DependenciesCFListener ) ((DependenciesCFListener)listener).exitRightargument(this);
		}
	}

	public final RightargumentContext rightargument() throws RecognitionException {
		RightargumentContext _localctx = new RightargumentContext(_ctx, getState());
		enterRule(_localctx, 34, RULE_rightargument);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(203);
			switch (_input.LA(1)) {
			case T__11:
				{
				setState(198);
				match(T__11);
				setState(199);
				((RightargumentContext)_localctx).var = match(IDENTIFIER);
				 expressionString.append(((RightargumentContext)_localctx).var.getText()); 
				}
				break;
			case IDENTIFIER:
			case STRING:
			case NUMBER:
				{
				setState(201);
				((RightargumentContext)_localctx).constant = _input.LT(1);
				_la = _input.LA(1);
				if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << IDENTIFIER) | (1L << STRING) | (1L << NUMBER))) != 0)) ) {
					((RightargumentContext)_localctx).constant = (Token)_errHandler.recoverInline(this);
				} else {
					consume();
				}
				 expressionString.append(((RightargumentContext)_localctx).constant.getText()); rightConstant = ((RightargumentContext)_localctx).constant.getText();
				}
				break;
			default:
				throw new NoViableAltException(this);
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

	public static class AttributeContext extends ParserRuleContext {
		public ValueContext value() {
			return getRuleContext(ValueContext.class,0);
		}
		public AttributeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_attribute; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DependenciesCFListener ) ((DependenciesCFListener)listener).enterAttribute(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DependenciesCFListener ) ((DependenciesCFListener)listener).exitAttribute(this);
		}
	}

	public final AttributeContext attribute() throws RecognitionException {
		AttributeContext _localctx = new AttributeContext(_ctx, getState());
		enterRule(_localctx, 36, RULE_attribute);
		try {
			enterOuterAlt(_localctx, 1);
			{
			 String attributeName = generator.findAttributeName(((RelationalAtom)atom).getTableName(), attributePosition, inPremise, stTGD); 
			                   attribute = new FormulaAttribute(attributeName); attributePosition++;
			setState(206);
			value();
			 ((RelationalAtom)atom).addAttribute(attribute); 
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

	public static class QueryattributeContext extends ParserRuleContext {
		public QueryvalueContext queryvalue() {
			return getRuleContext(QueryvalueContext.class,0);
		}
		public QueryattributeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_queryattribute; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DependenciesCFListener ) ((DependenciesCFListener)listener).enterQueryattribute(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DependenciesCFListener ) ((DependenciesCFListener)listener).exitQueryattribute(this);
		}
	}

	public final QueryattributeContext queryattribute() throws RecognitionException {
		QueryattributeContext _localctx = new QueryattributeContext(_ctx, getState());
		enterRule(_localctx, 38, RULE_queryattribute);
		try {
			enterOuterAlt(_localctx, 1);
			{
			 String attributeName = "a" + attributePosition; 
			                   attribute = new FormulaAttribute(attributeName); attributePosition++;
			setState(210);
			queryvalue();
			 ((QueryAtom)atom).addAttribute(attribute); 
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

	public static class ValueContext extends ParserRuleContext {
		public Token var;
		public Token constant;
		public Token symbol;
		public Token nullValue;
		public Token expression;
		public TerminalNode IDENTIFIER() { return getToken(DependenciesCFParser.IDENTIFIER, 0); }
		public TerminalNode STRING() { return getToken(DependenciesCFParser.STRING, 0); }
		public TerminalNode NUMBER() { return getToken(DependenciesCFParser.NUMBER, 0); }
		public TerminalNode NULL() { return getToken(DependenciesCFParser.NULL, 0); }
		public TerminalNode EXPRESSION() { return getToken(DependenciesCFParser.EXPRESSION, 0); }
		public ValueContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_value; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DependenciesCFListener ) ((DependenciesCFListener)listener).enterValue(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DependenciesCFListener ) ((DependenciesCFListener)listener).exitValue(this);
		}
	}

	public final ValueContext value() throws RecognitionException {
		ValueContext _localctx = new ValueContext(_ctx, getState());
		enterRule(_localctx, 40, RULE_value);
		int _la;
		try {
			setState(224);
			switch (_input.LA(1)) {
			case T__11:
				enterOuterAlt(_localctx, 1);
				{
				setState(213);
				match(T__11);
				setState(214);
				((ValueContext)_localctx).var = match(IDENTIFIER);
				 attribute.setValue(new FormulaVariableOccurrence(new AttributeRef(((RelationalAtom)atom).getTableName(), attribute.getAttributeName()), ((ValueContext)_localctx).var.getText())); 
				}
				break;
			case STRING:
			case NUMBER:
				enterOuterAlt(_localctx, 2);
				{
				setState(216);
				((ValueContext)_localctx).constant = _input.LT(1);
				_la = _input.LA(1);
				if ( !(_la==STRING || _la==NUMBER) ) {
					((ValueContext)_localctx).constant = (Token)_errHandler.recoverInline(this);
				} else {
					consume();
				}
				 attribute.setValue(new FormulaConstant(generator.convertValue(((ValueContext)_localctx).constant.getText()))); 
				}
				break;
			case IDENTIFIER:
				enterOuterAlt(_localctx, 3);
				{
				setState(218);
				((ValueContext)_localctx).symbol = match(IDENTIFIER);
				 attribute.setValue(new FormulaSymbol(generator.convertValue(((ValueContext)_localctx).symbol.getText()))); 
				}
				break;
			case NULL:
				enterOuterAlt(_localctx, 4);
				{
				setState(220);
				((ValueContext)_localctx).nullValue = match(NULL);
				 attribute.setValue(new FormulaConstant(((ValueContext)_localctx).nullValue.getText(), true)); 
				}
				break;
			case EXPRESSION:
				enterOuterAlt(_localctx, 5);
				{
				setState(222);
				((ValueContext)_localctx).expression = match(EXPRESSION);
				 attribute.setValue(new FormulaExpression(new Expression(generator.clean(((ValueContext)_localctx).expression.getText())))); 
				}
				break;
			default:
				throw new NoViableAltException(this);
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

	public static class QueryvalueContext extends ParserRuleContext {
		public Token var;
		public Token constant;
		public Token symbol;
		public Token nullValue;
		public Token expression;
		public TerminalNode IDENTIFIER() { return getToken(DependenciesCFParser.IDENTIFIER, 0); }
		public TerminalNode STRING() { return getToken(DependenciesCFParser.STRING, 0); }
		public TerminalNode NUMBER() { return getToken(DependenciesCFParser.NUMBER, 0); }
		public TerminalNode NULL() { return getToken(DependenciesCFParser.NULL, 0); }
		public TerminalNode EXPRESSION() { return getToken(DependenciesCFParser.EXPRESSION, 0); }
		public QueryvalueContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_queryvalue; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DependenciesCFListener ) ((DependenciesCFListener)listener).enterQueryvalue(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DependenciesCFListener ) ((DependenciesCFListener)listener).exitQueryvalue(this);
		}
	}

	public final QueryvalueContext queryvalue() throws RecognitionException {
		QueryvalueContext _localctx = new QueryvalueContext(_ctx, getState());
		enterRule(_localctx, 42, RULE_queryvalue);
		int _la;
		try {
			setState(237);
			switch (_input.LA(1)) {
			case T__11:
				enterOuterAlt(_localctx, 1);
				{
				setState(226);
				match(T__11);
				setState(227);
				((QueryvalueContext)_localctx).var = match(IDENTIFIER);
				 attribute.setValue(new FormulaVariableOccurrence(new AttributeRef(((QueryAtom)atom).getQueryId(), attribute.getAttributeName()), ((QueryvalueContext)_localctx).var.getText())); 
				}
				break;
			case STRING:
			case NUMBER:
				enterOuterAlt(_localctx, 2);
				{
				setState(229);
				((QueryvalueContext)_localctx).constant = _input.LT(1);
				_la = _input.LA(1);
				if ( !(_la==STRING || _la==NUMBER) ) {
					((QueryvalueContext)_localctx).constant = (Token)_errHandler.recoverInline(this);
				} else {
					consume();
				}
				 attribute.setValue(new FormulaConstant(generator.convertValue(((QueryvalueContext)_localctx).constant.getText()))); 
				}
				break;
			case IDENTIFIER:
				enterOuterAlt(_localctx, 3);
				{
				setState(231);
				((QueryvalueContext)_localctx).symbol = match(IDENTIFIER);
				 attribute.setValue(new FormulaSymbol(generator.convertValue(((QueryvalueContext)_localctx).symbol.getText()))); 
				}
				break;
			case NULL:
				enterOuterAlt(_localctx, 4);
				{
				setState(233);
				((QueryvalueContext)_localctx).nullValue = match(NULL);
				 attribute.setValue(new FormulaConstant(((QueryvalueContext)_localctx).nullValue.getText(), true)); 
				}
				break;
			case EXPRESSION:
				enterOuterAlt(_localctx, 5);
				{
				setState(235);
				((QueryvalueContext)_localctx).expression = match(EXPRESSION);
				 attribute.setValue(new FormulaExpression(new Expression(generator.clean(((QueryvalueContext)_localctx).expression.getText())))); 
				}
				break;
			default:
				throw new NoViableAltException(this);
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
		"\3\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\3\26\u00f2\4\2\t\2"+
		"\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13"+
		"\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\3\2\3\2\3\2\3\3\3\3"+
		"\6\3\64\n\3\r\3\16\3\65\3\3\3\3\5\3:\n\3\3\3\3\3\6\3>\n\3\r\3\16\3?\3"+
		"\3\3\3\5\3D\n\3\3\3\3\3\6\3H\n\3\r\3\16\3I\3\3\3\3\5\3N\n\3\3\3\3\3\6"+
		"\3R\n\3\r\3\16\3S\3\3\3\3\5\3X\n\3\3\4\3\4\3\4\3\4\3\5\3\5\3\5\3\5\3\6"+
		"\3\6\3\6\3\6\3\7\3\7\3\7\3\7\3\b\3\b\5\bl\n\b\3\b\3\b\3\b\3\b\3\b\3\b"+
		"\3\b\3\b\3\t\3\t\5\tx\n\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\n\3\n\3\n"+
		"\3\n\7\n\u0086\n\n\f\n\16\n\u0089\13\n\3\13\3\13\3\13\3\13\7\13\u008f"+
		"\n\13\f\13\16\13\u0092\13\13\3\f\3\f\3\f\3\r\3\r\3\r\5\r\u009a\n\r\3\16"+
		"\3\16\3\16\3\16\3\16\3\16\7\16\u00a2\n\16\f\16\16\16\u00a5\13\16\3\16"+
		"\3\16\3\16\3\17\3\17\3\17\3\17\3\17\3\17\7\17\u00b0\n\17\f\17\16\17\u00b3"+
		"\13\17\3\17\3\17\3\17\3\20\3\20\3\20\3\21\3\21\3\21\3\21\3\21\3\21\3\21"+
		"\3\22\3\22\3\22\3\22\3\22\5\22\u00c7\n\22\3\23\3\23\3\23\3\23\3\23\5\23"+
		"\u00ce\n\23\3\24\3\24\3\24\3\24\3\25\3\25\3\25\3\25\3\26\3\26\3\26\3\26"+
		"\3\26\3\26\3\26\3\26\3\26\3\26\3\26\5\26\u00e3\n\26\3\27\3\27\3\27\3\27"+
		"\3\27\3\27\3\27\3\27\3\27\3\27\3\27\5\27\u00f0\n\27\3\27\2\2\30\2\4\6"+
		"\b\n\f\16\20\22\24\26\30\32\34\36 \"$&(*,\2\4\3\2\20\22\3\2\21\22\u00f5"+
		"\2.\3\2\2\2\49\3\2\2\2\6Y\3\2\2\2\b]\3\2\2\2\na\3\2\2\2\fe\3\2\2\2\16"+
		"k\3\2\2\2\20w\3\2\2\2\22\u0081\3\2\2\2\24\u008a\3\2\2\2\26\u0093\3\2\2"+
		"\2\30\u0099\3\2\2\2\32\u009b\3\2\2\2\34\u00a9\3\2\2\2\36\u00b7\3\2\2\2"+
		" \u00ba\3\2\2\2\"\u00c6\3\2\2\2$\u00cd\3\2\2\2&\u00cf\3\2\2\2(\u00d3\3"+
		"\2\2\2*\u00e2\3\2\2\2,\u00ef\3\2\2\2./\5\4\3\2/\60\b\2\1\2\60\3\3\2\2"+
		"\2\61\63\7\3\2\2\62\64\5\6\4\2\63\62\3\2\2\2\64\65\3\2\2\2\65\63\3\2\2"+
		"\2\65\66\3\2\2\2\66\67\3\2\2\2\678\b\3\1\28:\3\2\2\29\61\3\2\2\29:\3\2"+
		"\2\2:C\3\2\2\2;=\7\4\2\2<>\5\b\5\2=<\3\2\2\2>?\3\2\2\2?=\3\2\2\2?@\3\2"+
		"\2\2@A\3\2\2\2AB\b\3\1\2BD\3\2\2\2C;\3\2\2\2CD\3\2\2\2DM\3\2\2\2EG\7\5"+
		"\2\2FH\5\n\6\2GF\3\2\2\2HI\3\2\2\2IG\3\2\2\2IJ\3\2\2\2JK\3\2\2\2KL\b\3"+
		"\1\2LN\3\2\2\2ME\3\2\2\2MN\3\2\2\2NW\3\2\2\2OQ\7\6\2\2PR\5\f\7\2QP\3\2"+
		"\2\2RS\3\2\2\2SQ\3\2\2\2ST\3\2\2\2TU\3\2\2\2UV\b\3\1\2VX\3\2\2\2WO\3\2"+
		"\2\2WX\3\2\2\2X\5\3\2\2\2YZ\b\4\1\2Z[\5\16\b\2[\\\b\4\1\2\\\7\3\2\2\2"+
		"]^\b\5\1\2^_\5\16\b\2_`\b\5\1\2`\t\3\2\2\2ab\b\6\1\2bc\5\16\b\2cd\b\6"+
		"\1\2d\13\3\2\2\2ef\b\7\1\2fg\5\20\t\2gh\b\7\1\2h\r\3\2\2\2ij\7\20\2\2"+
		"jl\7\7\2\2ki\3\2\2\2kl\3\2\2\2lm\3\2\2\2mn\b\b\1\2no\5\22\n\2op\7\b\2"+
		"\2pq\b\b\1\2qr\5\24\13\2rs\3\2\2\2st\7\t\2\2t\17\3\2\2\2uv\7\20\2\2vx"+
		"\7\7\2\2wu\3\2\2\2wx\3\2\2\2xy\3\2\2\2yz\b\t\1\2z{\5\26\f\2{|\7\n\2\2"+
		"|}\b\t\1\2}~\5\22\n\2~\177\3\2\2\2\177\u0080\7\t\2\2\u0080\21\3\2\2\2"+
		"\u0081\u0082\b\n\1\2\u0082\u0087\5\32\16\2\u0083\u0084\7\13\2\2\u0084"+
		"\u0086\5\30\r\2\u0085\u0083\3\2\2\2\u0086\u0089\3\2\2\2\u0087\u0085\3"+
		"\2\2\2\u0087\u0088\3\2\2\2\u0088\23\3\2\2\2\u0089\u0087\3\2\2\2\u008a"+
		"\u008b\b\13\1\2\u008b\u0090\5\30\r\2\u008c\u008d\7\13\2\2\u008d\u008f"+
		"\5\30\r\2\u008e\u008c\3\2\2\2\u008f\u0092\3\2\2\2\u0090\u008e\3\2\2\2"+
		"\u0090\u0091\3\2\2\2\u0091\25\3\2\2\2\u0092\u0090\3\2\2\2\u0093\u0094"+
		"\b\f\1\2\u0094\u0095\5\34\17\2\u0095\27\3\2\2\2\u0096\u009a\5\32\16\2"+
		"\u0097\u009a\5\36\20\2\u0098\u009a\5 \21\2\u0099\u0096\3\2\2\2\u0099\u0097"+
		"\3\2\2\2\u0099\u0098\3\2\2\2\u009a\31\3\2\2\2\u009b\u009c\7\20\2\2\u009c"+
		"\u009d\b\16\1\2\u009d\u009e\7\f\2\2\u009e\u00a3\5&\24\2\u009f\u00a0\7"+
		"\13\2\2\u00a0\u00a2\5&\24\2\u00a1\u009f\3\2\2\2\u00a2\u00a5\3\2\2\2\u00a3"+
		"\u00a1\3\2\2\2\u00a3\u00a4\3\2\2\2\u00a4\u00a6\3\2\2\2\u00a5\u00a3\3\2"+
		"\2\2\u00a6\u00a7\7\r\2\2\u00a7\u00a8\b\16\1\2\u00a8\33\3\2\2\2\u00a9\u00aa"+
		"\7\20\2\2\u00aa\u00ab\b\17\1\2\u00ab\u00ac\7\f\2\2\u00ac\u00b1\5(\25\2"+
		"\u00ad\u00ae\7\13\2\2\u00ae\u00b0\5(\25\2\u00af\u00ad\3\2\2\2\u00b0\u00b3"+
		"\3\2\2\2\u00b1\u00af\3\2\2\2\u00b1\u00b2\3\2\2\2\u00b2\u00b4\3\2\2\2\u00b3"+
		"\u00b1\3\2\2\2\u00b4\u00b5\7\r\2\2\u00b5\u00b6\b\17\1\2\u00b6\35\3\2\2"+
		"\2\u00b7\u00b8\7\26\2\2\u00b8\u00b9\b\20\1\2\u00b9\37\3\2\2\2\u00ba\u00bb"+
		"\b\21\1\2\u00bb\u00bc\5\"\22\2\u00bc\u00bd\7\17\2\2\u00bd\u00be\b\21\1"+
		"\2\u00be\u00bf\5$\23\2\u00bf\u00c0\b\21\1\2\u00c0!\3\2\2\2\u00c1\u00c2"+
		"\7\16\2\2\u00c2\u00c3\7\20\2\2\u00c3\u00c7\b\22\1\2\u00c4\u00c5\t\2\2"+
		"\2\u00c5\u00c7\b\22\1\2\u00c6\u00c1\3\2\2\2\u00c6\u00c4\3\2\2\2\u00c7"+
		"#\3\2\2\2\u00c8\u00c9\7\16\2\2\u00c9\u00ca\7\20\2\2\u00ca\u00ce\b\23\1"+
		"\2\u00cb\u00cc\t\2\2\2\u00cc\u00ce\b\23\1\2\u00cd\u00c8\3\2\2\2\u00cd"+
		"\u00cb\3\2\2\2\u00ce%\3\2\2\2\u00cf\u00d0\b\24\1\2\u00d0\u00d1\5*\26\2"+
		"\u00d1\u00d2\b\24\1\2\u00d2\'\3\2\2\2\u00d3\u00d4\b\25\1\2\u00d4\u00d5"+
		"\5,\27\2\u00d5\u00d6\b\25\1\2\u00d6)\3\2\2\2\u00d7\u00d8\7\16\2\2\u00d8"+
		"\u00d9\7\20\2\2\u00d9\u00e3\b\26\1\2\u00da\u00db\t\3\2\2\u00db\u00e3\b"+
		"\26\1\2\u00dc\u00dd\7\20\2\2\u00dd\u00e3\b\26\1\2\u00de\u00df\7\23\2\2"+
		"\u00df\u00e3\b\26\1\2\u00e0\u00e1\7\26\2\2\u00e1\u00e3\b\26\1\2\u00e2"+
		"\u00d7\3\2\2\2\u00e2\u00da\3\2\2\2\u00e2\u00dc\3\2\2\2\u00e2\u00de\3\2"+
		"\2\2\u00e2\u00e0\3\2\2\2\u00e3+\3\2\2\2\u00e4\u00e5\7\16\2\2\u00e5\u00e6"+
		"\7\20\2\2\u00e6\u00f0\b\27\1\2\u00e7\u00e8\t\3\2\2\u00e8\u00f0\b\27\1"+
		"\2\u00e9\u00ea\7\20\2\2\u00ea\u00f0\b\27\1\2\u00eb\u00ec\7\23\2\2\u00ec"+
		"\u00f0\b\27\1\2\u00ed\u00ee\7\26\2\2\u00ee\u00f0\b\27\1\2\u00ef\u00e4"+
		"\3\2\2\2\u00ef\u00e7\3\2\2\2\u00ef\u00e9\3\2\2\2\u00ef\u00eb\3\2\2\2\u00ef"+
		"\u00ed\3\2\2\2\u00f0-\3\2\2\2\25\659?CIMSWkw\u0087\u0090\u0099\u00a3\u00b1"+
		"\u00c6\u00cd\u00e2\u00ef";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}