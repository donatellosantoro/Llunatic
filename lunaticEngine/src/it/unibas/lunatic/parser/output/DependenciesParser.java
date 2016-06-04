// Generated from Dependencies.g4 by ANTLR 4.5.3

package it.unibas.lunatic.parser.output;

import it.unibas.lunatic.LunaticConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import it.unibas.lunatic.parser.operators.ParseDependencies;
import it.unibas.lunatic.model.dependency.*;
import speedy.model.database.AttributeRef;
import speedy.model.expressions.Expression;
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
public class DependenciesParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.5.3", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, T__2=3, T__3=4, T__4=5, T__5=6, T__6=7, T__7=8, T__8=9, 
		T__9=10, T__10=11, T__11=12, T__12=13, T__13=14, T__14=15, T__15=16, T__16=17, 
		T__17=18, T__18=19, T__19=20, OPERATOR=21, IDENTIFIER=22, STRING=23, NUMBER=24, 
		NULL=25, WHITESPACE=26, LINE_COMMENT=27, EXPRESSION=28;
	public static final int
		RULE_prog = 0, RULE_dependencies = 1, RULE_sttgd = 2, RULE_etgd = 3, RULE_dc = 4, 
		RULE_egd = 5, RULE_eegd = 6, RULE_dedstgd = 7, RULE_dedetgd = 8, RULE_dedegd = 9, 
		RULE_dependency = 10, RULE_ded = 11, RULE_dedConclusion = 12, RULE_positiveFormula = 13, 
		RULE_negatedFormula = 14, RULE_conclusionFormula = 15, RULE_atom = 16, 
		RULE_relationalAtom = 17, RULE_builtin = 18, RULE_comparison = 19, RULE_leftargument = 20, 
		RULE_rightargument = 21, RULE_attribute = 22, RULE_value = 23;
	public static final String[] ruleNames = {
		"prog", "dependencies", "sttgd", "etgd", "dc", "egd", "eegd", "dedstgd", 
		"dedetgd", "dedegd", "dependency", "ded", "dedConclusion", "positiveFormula", 
		"negatedFormula", "conclusionFormula", "atom", "relationalAtom", "builtin", 
		"comparison", "leftargument", "rightargument", "attribute", "value"
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

	@Override
	public String getGrammarFileName() { return "Dependencies.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }


	private static Logger logger = LoggerFactory.getLogger(DependenciesParser.class);

	private ParseDependencies generator = new ParseDependencies();

	private Stack<IFormula> formulaStack = new Stack<IFormula>();

	private Dependency dependency;
	private DED ded;
	private IFormula dedPremise;
	private IFormula formulaWN;
	private PositiveFormula positiveFormula;
	private IFormulaAtom atom;
	private FormulaAttribute attribute;
	private StringBuilder expressionString;
	private String leftConstant;
	private String rightConstant;
	private int counter;

	public void setGenerator(ParseDependencies generator) {
	      this.generator = generator;
	}


	public DependenciesParser(TokenStream input) {
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
			if ( listener instanceof DependenciesListener ) ((DependenciesListener)listener).enterProg(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DependenciesListener ) ((DependenciesListener)listener).exitProg(this);
		}
	}

	public final ProgContext prog() throws RecognitionException {
		ProgContext _localctx = new ProgContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_prog);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(48);
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
		public List<DedstgdContext> dedstgd() {
			return getRuleContexts(DedstgdContext.class);
		}
		public DedstgdContext dedstgd(int i) {
			return getRuleContext(DedstgdContext.class,i);
		}
		public List<EtgdContext> etgd() {
			return getRuleContexts(EtgdContext.class);
		}
		public EtgdContext etgd(int i) {
			return getRuleContext(EtgdContext.class,i);
		}
		public List<DedetgdContext> dedetgd() {
			return getRuleContexts(DedetgdContext.class);
		}
		public DedetgdContext dedetgd(int i) {
			return getRuleContext(DedetgdContext.class,i);
		}
		public List<EgdContext> egd() {
			return getRuleContexts(EgdContext.class);
		}
		public EgdContext egd(int i) {
			return getRuleContext(EgdContext.class,i);
		}
		public List<DedegdContext> dedegd() {
			return getRuleContexts(DedegdContext.class);
		}
		public DedegdContext dedegd(int i) {
			return getRuleContext(DedegdContext.class,i);
		}
		public List<EegdContext> eegd() {
			return getRuleContexts(EegdContext.class);
		}
		public EegdContext eegd(int i) {
			return getRuleContext(EegdContext.class,i);
		}
		public List<DcContext> dc() {
			return getRuleContexts(DcContext.class);
		}
		public DcContext dc(int i) {
			return getRuleContext(DcContext.class,i);
		}
		public DependenciesContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_dependencies; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DependenciesListener ) ((DependenciesListener)listener).enterDependencies(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DependenciesListener ) ((DependenciesListener)listener).exitDependencies(this);
		}
	}

	public final DependenciesContext dependencies() throws RecognitionException {
		DependenciesContext _localctx = new DependenciesContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_dependencies);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(67);
			switch (_input.LA(1)) {
			case T__0:
				{
				setState(51);
				match(T__0);
				setState(53); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(52);
					sttgd();
					}
					}
					setState(55); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==IDENTIFIER );
				 counter = 0;
				}
				break;
			case T__1:
				{
				setState(59);
				match(T__1);
				setState(61); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(60);
					dedstgd();
					}
					}
					setState(63); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==IDENTIFIER );
				counter = 0;
				}
				break;
			case EOF:
			case T__2:
			case T__3:
			case T__4:
			case T__5:
			case T__6:
			case T__7:
				break;
			default:
				throw new NoViableAltException(this);
			}
			setState(85);
			switch (_input.LA(1)) {
			case T__2:
				{
				setState(69);
				match(T__2);
				setState(71); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(70);
					etgd();
					}
					}
					setState(73); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==IDENTIFIER );
				 counter = 0;
				}
				break;
			case T__3:
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
					dedetgd();
					}
					}
					setState(81); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==IDENTIFIER );
				counter = 0;
				}
				break;
			case EOF:
			case T__4:
			case T__5:
			case T__6:
			case T__7:
				break;
			default:
				throw new NoViableAltException(this);
			}
			setState(111);
			switch (_input.LA(1)) {
			case T__4:
				{
				setState(87);
				match(T__4);
				setState(89); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(88);
					egd();
					}
					}
					setState(91); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==IDENTIFIER );
				 counter = 0;
				}
				break;
			case T__5:
				{
				setState(95);
				match(T__5);
				setState(97); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(96);
					dedegd();
					}
					}
					setState(99); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==IDENTIFIER );
				counter = 0;
				}
				break;
			case T__6:
				{
				setState(103);
				match(T__6);
				setState(105); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(104);
					eegd();
					}
					}
					setState(107); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==IDENTIFIER );
				 counter = 0;
				}
				break;
			case EOF:
			case T__7:
				break;
			default:
				throw new NoViableAltException(this);
			}
			setState(121);
			_la = _input.LA(1);
			if (_la==T__7) {
				{
				setState(113);
				match(T__7);
				setState(115); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(114);
					dc();
					}
					}
					setState(117); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==IDENTIFIER );
				 counter = 0;
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
			if ( listener instanceof DependenciesListener ) ((DependenciesListener)listener).enterSttgd(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DependenciesListener ) ((DependenciesListener)listener).exitSttgd(this);
		}
	}

	public final SttgdContext sttgd() throws RecognitionException {
		SttgdContext _localctx = new SttgdContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_sttgd);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(123);
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
			if ( listener instanceof DependenciesListener ) ((DependenciesListener)listener).enterEtgd(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DependenciesListener ) ((DependenciesListener)listener).exitEtgd(this);
		}
	}

	public final EtgdContext etgd() throws RecognitionException {
		EtgdContext _localctx = new EtgdContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_etgd);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(126);
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

	public static class DcContext extends ParserRuleContext {
		public DependencyContext dependency() {
			return getRuleContext(DependencyContext.class,0);
		}
		public DcContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_dc; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DependenciesListener ) ((DependenciesListener)listener).enterDc(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DependenciesListener ) ((DependenciesListener)listener).exitDc(this);
		}
	}

	public final DcContext dc() throws RecognitionException {
		DcContext _localctx = new DcContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_dc);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(129);
			dependency();
			  dependency.setType(LunaticConstants.DC); dependency.setId("d" + counter++); generator.addDC(dependency); 
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
			if ( listener instanceof DependenciesListener ) ((DependenciesListener)listener).enterEgd(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DependenciesListener ) ((DependenciesListener)listener).exitEgd(this);
		}
	}

	public final EgdContext egd() throws RecognitionException {
		EgdContext _localctx = new EgdContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_egd);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(132);
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

	public static class EegdContext extends ParserRuleContext {
		public DependencyContext dependency() {
			return getRuleContext(DependencyContext.class,0);
		}
		public EegdContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_eegd; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DependenciesListener ) ((DependenciesListener)listener).enterEegd(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DependenciesListener ) ((DependenciesListener)listener).exitEegd(this);
		}
	}

	public final EegdContext eegd() throws RecognitionException {
		EegdContext _localctx = new EegdContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_eegd);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(135);
			dependency();
			  dependency.setType(LunaticConstants.ExtEGD); dependency.setId("e" + counter++); generator.addExtEGD(dependency); 
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

	public static class DedstgdContext extends ParserRuleContext {
		public DedContext ded() {
			return getRuleContext(DedContext.class,0);
		}
		public DedstgdContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_dedstgd; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DependenciesListener ) ((DependenciesListener)listener).enterDedstgd(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DependenciesListener ) ((DependenciesListener)listener).exitDedstgd(this);
		}
	}

	public final DedstgdContext dedstgd() throws RecognitionException {
		DedstgdContext _localctx = new DedstgdContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_dedstgd);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(138);
			ded();
			  ded.setType(LunaticConstants.STTGD); ded.setId("ded_m" + counter++); generator.addDEDSTTGD(ded); 
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

	public static class DedetgdContext extends ParserRuleContext {
		public DedContext ded() {
			return getRuleContext(DedContext.class,0);
		}
		public DedetgdContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_dedetgd; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DependenciesListener ) ((DependenciesListener)listener).enterDedetgd(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DependenciesListener ) ((DependenciesListener)listener).exitDedetgd(this);
		}
	}

	public final DedetgdContext dedetgd() throws RecognitionException {
		DedetgdContext _localctx = new DedetgdContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_dedetgd);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(141);
			ded();
			  ded.setType(LunaticConstants.ExtTGD); ded.setId("ded_t" + counter++); generator.addDEDExtTGD(ded); 
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

	public static class DedegdContext extends ParserRuleContext {
		public DedContext ded() {
			return getRuleContext(DedContext.class,0);
		}
		public DedegdContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_dedegd; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DependenciesListener ) ((DependenciesListener)listener).enterDedegd(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DependenciesListener ) ((DependenciesListener)listener).exitDedegd(this);
		}
	}

	public final DedegdContext dedegd() throws RecognitionException {
		DedegdContext _localctx = new DedegdContext(_ctx, getState());
		enterRule(_localctx, 18, RULE_dedegd);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(144);
			ded();
			  ded.setType(LunaticConstants.EGD); ded.setId("ded_e" + counter++); generator.addDEDExtEGD(ded); 
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
		public List<NegatedFormulaContext> negatedFormula() {
			return getRuleContexts(NegatedFormulaContext.class);
		}
		public NegatedFormulaContext negatedFormula(int i) {
			return getRuleContext(NegatedFormulaContext.class,i);
		}
		public TerminalNode IDENTIFIER() { return getToken(DependenciesParser.IDENTIFIER, 0); }
		public DependencyContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_dependency; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DependenciesListener ) ((DependenciesListener)listener).enterDependency(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DependenciesListener ) ((DependenciesListener)listener).exitDependency(this);
		}
	}

	public final DependencyContext dependency() throws RecognitionException {
		DependencyContext _localctx = new DependencyContext(_ctx, getState());
		enterRule(_localctx, 20, RULE_dependency);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(149);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,12,_ctx) ) {
			case 1:
				{
				setState(147);
				((DependencyContext)_localctx).id = match(IDENTIFIER);
				setState(148);
				match(T__8);
				}
				break;
			}
			  dependency = new Dependency(); 
			                    formulaWN = new FormulaWithNegations(); 
			                    formulaStack.push(formulaWN);
			                    dependency.setPremise(formulaWN);
			                    if(((DependencyContext)_localctx).id!=null) dependency.setId(((DependencyContext)_localctx).id.getText()); 
			setState(152);
			positiveFormula();
			setState(156);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__16) {
				{
				{
				setState(153);
				negatedFormula();
				}
				}
				setState(158);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(159);
			match(T__9);
			setState(164);
			switch (_input.LA(1)) {
			case T__10:
				{
				setState(160);
				match(T__10);
				  formulaStack.clear(); 
				                    dependency.setConclusion(NullFormula.getInstance());
				}
				break;
			case T__19:
			case IDENTIFIER:
			case STRING:
			case NUMBER:
			case EXPRESSION:
				{
				  formulaStack.clear(); 
				setState(163);
				conclusionFormula();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			setState(166);
			match(T__11);
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

	public static class DedContext extends ParserRuleContext {
		public PositiveFormulaContext positiveFormula() {
			return getRuleContext(PositiveFormulaContext.class,0);
		}
		public List<DedConclusionContext> dedConclusion() {
			return getRuleContexts(DedConclusionContext.class);
		}
		public DedConclusionContext dedConclusion(int i) {
			return getRuleContext(DedConclusionContext.class,i);
		}
		public List<NegatedFormulaContext> negatedFormula() {
			return getRuleContexts(NegatedFormulaContext.class);
		}
		public NegatedFormulaContext negatedFormula(int i) {
			return getRuleContext(NegatedFormulaContext.class,i);
		}
		public DedContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_ded; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DependenciesListener ) ((DependenciesListener)listener).enterDed(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DependenciesListener ) ((DependenciesListener)listener).exitDed(this);
		}
	}

	public final DedContext ded() throws RecognitionException {
		DedContext _localctx = new DedContext(_ctx, getState());
		enterRule(_localctx, 22, RULE_ded);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			  ded = new DED(); 
			                    formulaWN = new FormulaWithNegations(); 
			                    formulaStack.push(formulaWN);
			                    dedPremise = formulaWN;
			setState(169);
			positiveFormula();
			setState(173);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__16) {
				{
				{
				setState(170);
				negatedFormula();
				}
				}
				setState(175);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(176);
			match(T__9);
			setState(177);
			dedConclusion();
			setState(182);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__12) {
				{
				{
				setState(178);
				match(T__12);
				setState(179);
				dedConclusion();
				}
				}
				setState(184);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(185);
			match(T__11);
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

	public static class DedConclusionContext extends ParserRuleContext {
		public List<AtomContext> atom() {
			return getRuleContexts(AtomContext.class);
		}
		public AtomContext atom(int i) {
			return getRuleContext(AtomContext.class,i);
		}
		public DedConclusionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_dedConclusion; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DependenciesListener ) ((DependenciesListener)listener).enterDedConclusion(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DependenciesListener ) ((DependenciesListener)listener).exitDedConclusion(this);
		}
	}

	public final DedConclusionContext dedConclusion() throws RecognitionException {
		DedConclusionContext _localctx = new DedConclusionContext(_ctx, getState());
		enterRule(_localctx, 24, RULE_dedConclusion);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(187);
			match(T__13);
			 formulaStack.clear(); 
			                        dependency = new Dependency();
			                        ded.addAssociatedDependency(dependency);                        
			                        dependency.setPremise(dedPremise.clone());
						positiveFormula = new PositiveFormula(); 
			                        dependency.setConclusion(positiveFormula); 
			setState(189);
			atom();
			setState(194);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__14) {
				{
				{
				setState(190);
				match(T__14);
				setState(191);
				atom();
				}
				}
				setState(196);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(197);
			match(T__15);
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
			if ( listener instanceof DependenciesListener ) ((DependenciesListener)listener).enterPositiveFormula(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DependenciesListener ) ((DependenciesListener)listener).exitPositiveFormula(this);
		}
	}

	public final PositiveFormulaContext positiveFormula() throws RecognitionException {
		PositiveFormulaContext _localctx = new PositiveFormulaContext(_ctx, getState());
		enterRule(_localctx, 26, RULE_positiveFormula);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			  positiveFormula = new PositiveFormula(); 
			                    positiveFormula.setFather(formulaStack.peek()); 
			                    formulaStack.peek().setPositiveFormula(positiveFormula); 
			setState(200);
			relationalAtom();
			setState(205);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__14) {
				{
				{
				setState(201);
				match(T__14);
				setState(202);
				atom();
				}
				}
				setState(207);
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

	public static class NegatedFormulaContext extends ParserRuleContext {
		public PositiveFormulaContext positiveFormula() {
			return getRuleContext(PositiveFormulaContext.class,0);
		}
		public List<NegatedFormulaContext> negatedFormula() {
			return getRuleContexts(NegatedFormulaContext.class);
		}
		public NegatedFormulaContext negatedFormula(int i) {
			return getRuleContext(NegatedFormulaContext.class,i);
		}
		public NegatedFormulaContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_negatedFormula; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DependenciesListener ) ((DependenciesListener)listener).enterNegatedFormula(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DependenciesListener ) ((DependenciesListener)listener).exitNegatedFormula(this);
		}
	}

	public final NegatedFormulaContext negatedFormula() throws RecognitionException {
		NegatedFormulaContext _localctx = new NegatedFormulaContext(_ctx, getState());
		enterRule(_localctx, 28, RULE_negatedFormula);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			  formulaWN = new FormulaWithNegations(); 
					    formulaWN.setFather(formulaStack.peek());
					    formulaStack.peek().addNegatedFormula(formulaWN);
			                    formulaStack.push(formulaWN); 
			setState(209);
			match(T__16);
			setState(210);
			match(T__17);
			{
			setState(211);
			positiveFormula();
			setState(215);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__16) {
				{
				{
				setState(212);
				negatedFormula();
				}
				}
				setState(217);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
			setState(218);
			match(T__18);
			  formulaStack.pop(); 
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
			if ( listener instanceof DependenciesListener ) ((DependenciesListener)listener).enterConclusionFormula(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DependenciesListener ) ((DependenciesListener)listener).exitConclusionFormula(this);
		}
	}

	public final ConclusionFormulaContext conclusionFormula() throws RecognitionException {
		ConclusionFormulaContext _localctx = new ConclusionFormulaContext(_ctx, getState());
		enterRule(_localctx, 30, RULE_conclusionFormula);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			  positiveFormula = new PositiveFormula(); 
			                      dependency.setConclusion(positiveFormula); 
			setState(222);
			atom();
			setState(227);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__14) {
				{
				{
				setState(223);
				match(T__14);
				setState(224);
				atom();
				}
				}
				setState(229);
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
			if ( listener instanceof DependenciesListener ) ((DependenciesListener)listener).enterAtom(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DependenciesListener ) ((DependenciesListener)listener).exitAtom(this);
		}
	}

	public final AtomContext atom() throws RecognitionException {
		AtomContext _localctx = new AtomContext(_ctx, getState());
		enterRule(_localctx, 32, RULE_atom);
		try {
			setState(233);
			switch (_input.LA(1)) {
			case IDENTIFIER:
				enterOuterAlt(_localctx, 1);
				{
				setState(230);
				relationalAtom();
				}
				break;
			case EXPRESSION:
				enterOuterAlt(_localctx, 2);
				{
				setState(231);
				builtin();
				}
				break;
			case T__19:
			case STRING:
			case NUMBER:
				enterOuterAlt(_localctx, 3);
				{
				setState(232);
				comparison();
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

	public static class RelationalAtomContext extends ParserRuleContext {
		public Token name;
		public List<AttributeContext> attribute() {
			return getRuleContexts(AttributeContext.class);
		}
		public AttributeContext attribute(int i) {
			return getRuleContext(AttributeContext.class,i);
		}
		public TerminalNode IDENTIFIER() { return getToken(DependenciesParser.IDENTIFIER, 0); }
		public RelationalAtomContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_relationalAtom; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DependenciesListener ) ((DependenciesListener)listener).enterRelationalAtom(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DependenciesListener ) ((DependenciesListener)listener).exitRelationalAtom(this);
		}
	}

	public final RelationalAtomContext relationalAtom() throws RecognitionException {
		RelationalAtomContext _localctx = new RelationalAtomContext(_ctx, getState());
		enterRule(_localctx, 34, RULE_relationalAtom);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(235);
			((RelationalAtomContext)_localctx).name = match(IDENTIFIER);
			 atom = new RelationalAtom(((RelationalAtomContext)_localctx).name.getText()); 
			setState(237);
			match(T__17);
			setState(238);
			attribute();
			setState(243);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__14) {
				{
				{
				setState(239);
				match(T__14);
				setState(240);
				attribute();
				}
				}
				setState(245);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(246);
			match(T__18);
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
		public TerminalNode EXPRESSION() { return getToken(DependenciesParser.EXPRESSION, 0); }
		public BuiltinContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_builtin; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DependenciesListener ) ((DependenciesListener)listener).enterBuiltin(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DependenciesListener ) ((DependenciesListener)listener).exitBuiltin(this);
		}
	}

	public final BuiltinContext builtin() throws RecognitionException {
		BuiltinContext _localctx = new BuiltinContext(_ctx, getState());
		enterRule(_localctx, 36, RULE_builtin);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(249);
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
		public TerminalNode OPERATOR() { return getToken(DependenciesParser.OPERATOR, 0); }
		public ComparisonContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_comparison; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DependenciesListener ) ((DependenciesListener)listener).enterComparison(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DependenciesListener ) ((DependenciesListener)listener).exitComparison(this);
		}
	}

	public final ComparisonContext comparison() throws RecognitionException {
		ComparisonContext _localctx = new ComparisonContext(_ctx, getState());
		enterRule(_localctx, 38, RULE_comparison);
		try {
			enterOuterAlt(_localctx, 1);
			{
			   expressionString = new StringBuilder(); 
					     leftConstant = null;
					     rightConstant = null;
			setState(253);
			leftargument();
			setState(254);
			((ComparisonContext)_localctx).oper = match(OPERATOR);
			 expressionString.append(" ").append(((ComparisonContext)_localctx).oper.getText()); 
			setState(256);
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
		public TerminalNode IDENTIFIER() { return getToken(DependenciesParser.IDENTIFIER, 0); }
		public TerminalNode STRING() { return getToken(DependenciesParser.STRING, 0); }
		public TerminalNode NUMBER() { return getToken(DependenciesParser.NUMBER, 0); }
		public LeftargumentContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_leftargument; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DependenciesListener ) ((DependenciesListener)listener).enterLeftargument(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DependenciesListener ) ((DependenciesListener)listener).exitLeftargument(this);
		}
	}

	public final LeftargumentContext leftargument() throws RecognitionException {
		LeftargumentContext _localctx = new LeftargumentContext(_ctx, getState());
		enterRule(_localctx, 40, RULE_leftargument);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(264);
			switch (_input.LA(1)) {
			case T__19:
				{
				setState(259);
				match(T__19);
				setState(260);
				((LeftargumentContext)_localctx).var = match(IDENTIFIER);
				 expressionString.append(((LeftargumentContext)_localctx).var.getText()); 
				}
				break;
			case STRING:
			case NUMBER:
				{
				setState(262);
				((LeftargumentContext)_localctx).constant = _input.LT(1);
				_la = _input.LA(1);
				if ( !(_la==STRING || _la==NUMBER) ) {
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
		public TerminalNode IDENTIFIER() { return getToken(DependenciesParser.IDENTIFIER, 0); }
		public TerminalNode STRING() { return getToken(DependenciesParser.STRING, 0); }
		public TerminalNode NUMBER() { return getToken(DependenciesParser.NUMBER, 0); }
		public RightargumentContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_rightargument; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DependenciesListener ) ((DependenciesListener)listener).enterRightargument(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DependenciesListener ) ((DependenciesListener)listener).exitRightargument(this);
		}
	}

	public final RightargumentContext rightargument() throws RecognitionException {
		RightargumentContext _localctx = new RightargumentContext(_ctx, getState());
		enterRule(_localctx, 42, RULE_rightargument);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(271);
			switch (_input.LA(1)) {
			case T__19:
				{
				setState(266);
				match(T__19);
				setState(267);
				((RightargumentContext)_localctx).var = match(IDENTIFIER);
				 expressionString.append(((RightargumentContext)_localctx).var.getText()); 
				}
				break;
			case STRING:
			case NUMBER:
				{
				setState(269);
				((RightargumentContext)_localctx).constant = _input.LT(1);
				_la = _input.LA(1);
				if ( !(_la==STRING || _la==NUMBER) ) {
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
		public Token attr;
		public ValueContext value() {
			return getRuleContext(ValueContext.class,0);
		}
		public TerminalNode IDENTIFIER() { return getToken(DependenciesParser.IDENTIFIER, 0); }
		public AttributeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_attribute; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DependenciesListener ) ((DependenciesListener)listener).enterAttribute(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DependenciesListener ) ((DependenciesListener)listener).exitAttribute(this);
		}
	}

	public final AttributeContext attribute() throws RecognitionException {
		AttributeContext _localctx = new AttributeContext(_ctx, getState());
		enterRule(_localctx, 44, RULE_attribute);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(273);
			((AttributeContext)_localctx).attr = match(IDENTIFIER);
			setState(274);
			match(T__8);
			 attribute = new FormulaAttribute(((AttributeContext)_localctx).attr.getText()); 
			setState(276);
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

	public static class ValueContext extends ParserRuleContext {
		public Token var;
		public Token constant;
		public Token nullValue;
		public Token expression;
		public TerminalNode IDENTIFIER() { return getToken(DependenciesParser.IDENTIFIER, 0); }
		public TerminalNode STRING() { return getToken(DependenciesParser.STRING, 0); }
		public TerminalNode NUMBER() { return getToken(DependenciesParser.NUMBER, 0); }
		public TerminalNode NULL() { return getToken(DependenciesParser.NULL, 0); }
		public TerminalNode EXPRESSION() { return getToken(DependenciesParser.EXPRESSION, 0); }
		public ValueContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_value; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DependenciesListener ) ((DependenciesListener)listener).enterValue(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DependenciesListener ) ((DependenciesListener)listener).exitValue(this);
		}
	}

	public final ValueContext value() throws RecognitionException {
		ValueContext _localctx = new ValueContext(_ctx, getState());
		enterRule(_localctx, 46, RULE_value);
		int _la;
		try {
			setState(288);
			switch (_input.LA(1)) {
			case T__19:
				enterOuterAlt(_localctx, 1);
				{
				setState(279);
				match(T__19);
				setState(280);
				((ValueContext)_localctx).var = match(IDENTIFIER);
				 attribute.setValue(new FormulaVariableOccurrence(new AttributeRef(((RelationalAtom)atom).getTableName(), attribute.getAttributeName()), ((ValueContext)_localctx).var.getText())); 
				}
				break;
			case STRING:
			case NUMBER:
				enterOuterAlt(_localctx, 2);
				{
				setState(282);
				((ValueContext)_localctx).constant = _input.LT(1);
				_la = _input.LA(1);
				if ( !(_la==STRING || _la==NUMBER) ) {
					((ValueContext)_localctx).constant = (Token)_errHandler.recoverInline(this);
				} else {
					consume();
				}
				 attribute.setValue(new FormulaConstant(((ValueContext)_localctx).constant.getText())); 
				}
				break;
			case NULL:
				enterOuterAlt(_localctx, 3);
				{
				setState(284);
				((ValueContext)_localctx).nullValue = match(NULL);
				 attribute.setValue(new FormulaConstant(((ValueContext)_localctx).nullValue.getText(), true)); 
				}
				break;
			case EXPRESSION:
				enterOuterAlt(_localctx, 4);
				{
				setState(286);
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

	public static final String _serializedATN =
		"\3\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\3\36\u0125\4\2\t\2"+
		"\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13"+
		"\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31\t\31"+
		"\3\2\3\2\3\2\3\3\3\3\6\38\n\3\r\3\16\39\3\3\3\3\3\3\3\3\6\3@\n\3\r\3\16"+
		"\3A\3\3\3\3\5\3F\n\3\3\3\3\3\6\3J\n\3\r\3\16\3K\3\3\3\3\3\3\3\3\6\3R\n"+
		"\3\r\3\16\3S\3\3\3\3\5\3X\n\3\3\3\3\3\6\3\\\n\3\r\3\16\3]\3\3\3\3\3\3"+
		"\3\3\6\3d\n\3\r\3\16\3e\3\3\3\3\3\3\3\3\6\3l\n\3\r\3\16\3m\3\3\3\3\5\3"+
		"r\n\3\3\3\3\3\6\3v\n\3\r\3\16\3w\3\3\3\3\5\3|\n\3\3\4\3\4\3\4\3\5\3\5"+
		"\3\5\3\6\3\6\3\6\3\7\3\7\3\7\3\b\3\b\3\b\3\t\3\t\3\t\3\n\3\n\3\n\3\13"+
		"\3\13\3\13\3\f\3\f\5\f\u0098\n\f\3\f\3\f\3\f\7\f\u009d\n\f\f\f\16\f\u00a0"+
		"\13\f\3\f\3\f\3\f\3\f\3\f\5\f\u00a7\n\f\3\f\3\f\3\r\3\r\3\r\7\r\u00ae"+
		"\n\r\f\r\16\r\u00b1\13\r\3\r\3\r\3\r\3\r\7\r\u00b7\n\r\f\r\16\r\u00ba"+
		"\13\r\3\r\3\r\3\16\3\16\3\16\3\16\3\16\7\16\u00c3\n\16\f\16\16\16\u00c6"+
		"\13\16\3\16\3\16\3\17\3\17\3\17\3\17\7\17\u00ce\n\17\f\17\16\17\u00d1"+
		"\13\17\3\20\3\20\3\20\3\20\3\20\7\20\u00d8\n\20\f\20\16\20\u00db\13\20"+
		"\3\20\3\20\3\20\3\21\3\21\3\21\3\21\7\21\u00e4\n\21\f\21\16\21\u00e7\13"+
		"\21\3\22\3\22\3\22\5\22\u00ec\n\22\3\23\3\23\3\23\3\23\3\23\3\23\7\23"+
		"\u00f4\n\23\f\23\16\23\u00f7\13\23\3\23\3\23\3\23\3\24\3\24\3\24\3\25"+
		"\3\25\3\25\3\25\3\25\3\25\3\25\3\26\3\26\3\26\3\26\3\26\5\26\u010b\n\26"+
		"\3\27\3\27\3\27\3\27\3\27\5\27\u0112\n\27\3\30\3\30\3\30\3\30\3\30\3\30"+
		"\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\5\31\u0123\n\31\3\31\2\2"+
		"\32\2\4\6\b\n\f\16\20\22\24\26\30\32\34\36 \"$&(*,.\60\2\3\3\2\31\32\u012d"+
		"\2\62\3\2\2\2\4E\3\2\2\2\6}\3\2\2\2\b\u0080\3\2\2\2\n\u0083\3\2\2\2\f"+
		"\u0086\3\2\2\2\16\u0089\3\2\2\2\20\u008c\3\2\2\2\22\u008f\3\2\2\2\24\u0092"+
		"\3\2\2\2\26\u0097\3\2\2\2\30\u00aa\3\2\2\2\32\u00bd\3\2\2\2\34\u00c9\3"+
		"\2\2\2\36\u00d2\3\2\2\2 \u00df\3\2\2\2\"\u00eb\3\2\2\2$\u00ed\3\2\2\2"+
		"&\u00fb\3\2\2\2(\u00fe\3\2\2\2*\u010a\3\2\2\2,\u0111\3\2\2\2.\u0113\3"+
		"\2\2\2\60\u0122\3\2\2\2\62\63\5\4\3\2\63\64\b\2\1\2\64\3\3\2\2\2\65\67"+
		"\7\3\2\2\668\5\6\4\2\67\66\3\2\2\289\3\2\2\29\67\3\2\2\29:\3\2\2\2:;\3"+
		"\2\2\2;<\b\3\1\2<F\3\2\2\2=?\7\4\2\2>@\5\20\t\2?>\3\2\2\2@A\3\2\2\2A?"+
		"\3\2\2\2AB\3\2\2\2BC\3\2\2\2CD\b\3\1\2DF\3\2\2\2E\65\3\2\2\2E=\3\2\2\2"+
		"EF\3\2\2\2FW\3\2\2\2GI\7\5\2\2HJ\5\b\5\2IH\3\2\2\2JK\3\2\2\2KI\3\2\2\2"+
		"KL\3\2\2\2LM\3\2\2\2MN\b\3\1\2NX\3\2\2\2OQ\7\6\2\2PR\5\22\n\2QP\3\2\2"+
		"\2RS\3\2\2\2SQ\3\2\2\2ST\3\2\2\2TU\3\2\2\2UV\b\3\1\2VX\3\2\2\2WG\3\2\2"+
		"\2WO\3\2\2\2WX\3\2\2\2Xq\3\2\2\2Y[\7\7\2\2Z\\\5\f\7\2[Z\3\2\2\2\\]\3\2"+
		"\2\2][\3\2\2\2]^\3\2\2\2^_\3\2\2\2_`\b\3\1\2`r\3\2\2\2ac\7\b\2\2bd\5\24"+
		"\13\2cb\3\2\2\2de\3\2\2\2ec\3\2\2\2ef\3\2\2\2fg\3\2\2\2gh\b\3\1\2hr\3"+
		"\2\2\2ik\7\t\2\2jl\5\16\b\2kj\3\2\2\2lm\3\2\2\2mk\3\2\2\2mn\3\2\2\2no"+
		"\3\2\2\2op\b\3\1\2pr\3\2\2\2qY\3\2\2\2qa\3\2\2\2qi\3\2\2\2qr\3\2\2\2r"+
		"{\3\2\2\2su\7\n\2\2tv\5\n\6\2ut\3\2\2\2vw\3\2\2\2wu\3\2\2\2wx\3\2\2\2"+
		"xy\3\2\2\2yz\b\3\1\2z|\3\2\2\2{s\3\2\2\2{|\3\2\2\2|\5\3\2\2\2}~\5\26\f"+
		"\2~\177\b\4\1\2\177\7\3\2\2\2\u0080\u0081\5\26\f\2\u0081\u0082\b\5\1\2"+
		"\u0082\t\3\2\2\2\u0083\u0084\5\26\f\2\u0084\u0085\b\6\1\2\u0085\13\3\2"+
		"\2\2\u0086\u0087\5\26\f\2\u0087\u0088\b\7\1\2\u0088\r\3\2\2\2\u0089\u008a"+
		"\5\26\f\2\u008a\u008b\b\b\1\2\u008b\17\3\2\2\2\u008c\u008d\5\30\r\2\u008d"+
		"\u008e\b\t\1\2\u008e\21\3\2\2\2\u008f\u0090\5\30\r\2\u0090\u0091\b\n\1"+
		"\2\u0091\23\3\2\2\2\u0092\u0093\5\30\r\2\u0093\u0094\b\13\1\2\u0094\25"+
		"\3\2\2\2\u0095\u0096\7\30\2\2\u0096\u0098\7\13\2\2\u0097\u0095\3\2\2\2"+
		"\u0097\u0098\3\2\2\2\u0098\u0099\3\2\2\2\u0099\u009a\b\f\1\2\u009a\u009e"+
		"\5\34\17\2\u009b\u009d\5\36\20\2\u009c\u009b\3\2\2\2\u009d\u00a0\3\2\2"+
		"\2\u009e\u009c\3\2\2\2\u009e\u009f\3\2\2\2\u009f\u00a1\3\2\2\2\u00a0\u009e"+
		"\3\2\2\2\u00a1\u00a6\7\f\2\2\u00a2\u00a3\7\r\2\2\u00a3\u00a7\b\f\1\2\u00a4"+
		"\u00a5\b\f\1\2\u00a5\u00a7\5 \21\2\u00a6\u00a2\3\2\2\2\u00a6\u00a4\3\2"+
		"\2\2\u00a7\u00a8\3\2\2\2\u00a8\u00a9\7\16\2\2\u00a9\27\3\2\2\2\u00aa\u00ab"+
		"\b\r\1\2\u00ab\u00af\5\34\17\2\u00ac\u00ae\5\36\20\2\u00ad\u00ac\3\2\2"+
		"\2\u00ae\u00b1\3\2\2\2\u00af\u00ad\3\2\2\2\u00af\u00b0\3\2\2\2\u00b0\u00b2"+
		"\3\2\2\2\u00b1\u00af\3\2\2\2\u00b2\u00b3\7\f\2\2\u00b3\u00b8\5\32\16\2"+
		"\u00b4\u00b5\7\17\2\2\u00b5\u00b7\5\32\16\2\u00b6\u00b4\3\2\2\2\u00b7"+
		"\u00ba\3\2\2\2\u00b8\u00b6\3\2\2\2\u00b8\u00b9\3\2\2\2\u00b9\u00bb\3\2"+
		"\2\2\u00ba\u00b8\3\2\2\2\u00bb\u00bc\7\16\2\2\u00bc\31\3\2\2\2\u00bd\u00be"+
		"\7\20\2\2\u00be\u00bf\b\16\1\2\u00bf\u00c4\5\"\22\2\u00c0\u00c1\7\21\2"+
		"\2\u00c1\u00c3\5\"\22\2\u00c2\u00c0\3\2\2\2\u00c3\u00c6\3\2\2\2\u00c4"+
		"\u00c2\3\2\2\2\u00c4\u00c5\3\2\2\2\u00c5\u00c7\3\2\2\2\u00c6\u00c4\3\2"+
		"\2\2\u00c7\u00c8\7\22\2\2\u00c8\33\3\2\2\2\u00c9\u00ca\b\17\1\2\u00ca"+
		"\u00cf\5$\23\2\u00cb\u00cc\7\21\2\2\u00cc\u00ce\5\"\22\2\u00cd\u00cb\3"+
		"\2\2\2\u00ce\u00d1\3\2\2\2\u00cf\u00cd\3\2\2\2\u00cf\u00d0\3\2\2\2\u00d0"+
		"\35\3\2\2\2\u00d1\u00cf\3\2\2\2\u00d2\u00d3\b\20\1\2\u00d3\u00d4\7\23"+
		"\2\2\u00d4\u00d5\7\24\2\2\u00d5\u00d9\5\34\17\2\u00d6\u00d8\5\36\20\2"+
		"\u00d7\u00d6\3\2\2\2\u00d8\u00db\3\2\2\2\u00d9\u00d7\3\2\2\2\u00d9\u00da"+
		"\3\2\2\2\u00da\u00dc\3\2\2\2\u00db\u00d9\3\2\2\2\u00dc\u00dd\7\25\2\2"+
		"\u00dd\u00de\b\20\1\2\u00de\37\3\2\2\2\u00df\u00e0\b\21\1\2\u00e0\u00e5"+
		"\5\"\22\2\u00e1\u00e2\7\21\2\2\u00e2\u00e4\5\"\22\2\u00e3\u00e1\3\2\2"+
		"\2\u00e4\u00e7\3\2\2\2\u00e5\u00e3\3\2\2\2\u00e5\u00e6\3\2\2\2\u00e6!"+
		"\3\2\2\2\u00e7\u00e5\3\2\2\2\u00e8\u00ec\5$\23\2\u00e9\u00ec\5&\24\2\u00ea"+
		"\u00ec\5(\25\2\u00eb\u00e8\3\2\2\2\u00eb\u00e9\3\2\2\2\u00eb\u00ea\3\2"+
		"\2\2\u00ec#\3\2\2\2\u00ed\u00ee\7\30\2\2\u00ee\u00ef\b\23\1\2\u00ef\u00f0"+
		"\7\24\2\2\u00f0\u00f5\5.\30\2\u00f1\u00f2\7\21\2\2\u00f2\u00f4\5.\30\2"+
		"\u00f3\u00f1\3\2\2\2\u00f4\u00f7\3\2\2\2\u00f5\u00f3\3\2\2\2\u00f5\u00f6"+
		"\3\2\2\2\u00f6\u00f8\3\2\2\2\u00f7\u00f5\3\2\2\2\u00f8\u00f9\7\25\2\2"+
		"\u00f9\u00fa\b\23\1\2\u00fa%\3\2\2\2\u00fb\u00fc\7\36\2\2\u00fc\u00fd"+
		"\b\24\1\2\u00fd\'\3\2\2\2\u00fe\u00ff\b\25\1\2\u00ff\u0100\5*\26\2\u0100"+
		"\u0101\7\27\2\2\u0101\u0102\b\25\1\2\u0102\u0103\5,\27\2\u0103\u0104\b"+
		"\25\1\2\u0104)\3\2\2\2\u0105\u0106\7\26\2\2\u0106\u0107\7\30\2\2\u0107"+
		"\u010b\b\26\1\2\u0108\u0109\t\2\2\2\u0109\u010b\b\26\1\2\u010a\u0105\3"+
		"\2\2\2\u010a\u0108\3\2\2\2\u010b+\3\2\2\2\u010c\u010d\7\26\2\2\u010d\u010e"+
		"\7\30\2\2\u010e\u0112\b\27\1\2\u010f\u0110\t\2\2\2\u0110\u0112\b\27\1"+
		"\2\u0111\u010c\3\2\2\2\u0111\u010f\3\2\2\2\u0112-\3\2\2\2\u0113\u0114"+
		"\7\30\2\2\u0114\u0115\7\13\2\2\u0115\u0116\b\30\1\2\u0116\u0117\5\60\31"+
		"\2\u0117\u0118\b\30\1\2\u0118/\3\2\2\2\u0119\u011a\7\26\2\2\u011a\u011b"+
		"\7\30\2\2\u011b\u0123\b\31\1\2\u011c\u011d\t\2\2\2\u011d\u0123\b\31\1"+
		"\2\u011e\u011f\7\33\2\2\u011f\u0123\b\31\1\2\u0120\u0121\7\36\2\2\u0121"+
		"\u0123\b\31\1\2\u0122\u0119\3\2\2\2\u0122\u011c\3\2\2\2\u0122\u011e\3"+
		"\2\2\2\u0122\u0120\3\2\2\2\u0123\61\3\2\2\2\349AEKSW]emqw{\u0097\u009e"+
		"\u00a6\u00af\u00b8\u00c4\u00cf\u00d9\u00e5\u00eb\u00f5\u010a\u0111\u0122";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}