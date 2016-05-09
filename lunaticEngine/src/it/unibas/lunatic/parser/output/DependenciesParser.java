// $ANTLR 3.5.1 /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g 2016-05-09 17:47:52

package it.unibas.lunatic.parser.output;

import it.unibas.lunatic.LunaticConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import it.unibas.lunatic.parser.operators.ParseDependencies;
import it.unibas.lunatic.model.dependency.*;
import speedy.model.database.AttributeRef;
import speedy.model.expressions.Expression;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

import org.antlr.runtime.tree.*;


@SuppressWarnings("all")
public class DependenciesParser extends Parser {
	public static final String[] tokenNames = new String[] {
		"<invalid>", "<EOR>", "<DOWN>", "<UP>", "DIGIT", "EXPRESSION", "IDENTIFIER", 
		"LETTER", "LINE_COMMENT", "NULL", "NUMBER", "OPERATOR", "STRING", "WHITESPACE", 
		"'#fail'", "'('", "')'", "','", "'->'", "'.'", "':'", "'DCs:'", "'DED-EGDs:'", 
		"'DED-ExtTGDs:'", "'DED-STTGDs:'", "'EGDs:'", "'ExtEGDs:'", "'ExtTGDs:'", 
		"'STTGDs:'", "'['", "'\\$'", "']'", "'and not exists'", "'|'"
	};
	public static final int EOF=-1;
	public static final int T__14=14;
	public static final int T__15=15;
	public static final int T__16=16;
	public static final int T__17=17;
	public static final int T__18=18;
	public static final int T__19=19;
	public static final int T__20=20;
	public static final int T__21=21;
	public static final int T__22=22;
	public static final int T__23=23;
	public static final int T__24=24;
	public static final int T__25=25;
	public static final int T__26=26;
	public static final int T__27=27;
	public static final int T__28=28;
	public static final int T__29=29;
	public static final int T__30=30;
	public static final int T__31=31;
	public static final int T__32=32;
	public static final int T__33=33;
	public static final int DIGIT=4;
	public static final int EXPRESSION=5;
	public static final int IDENTIFIER=6;
	public static final int LETTER=7;
	public static final int LINE_COMMENT=8;
	public static final int NULL=9;
	public static final int NUMBER=10;
	public static final int OPERATOR=11;
	public static final int STRING=12;
	public static final int WHITESPACE=13;

	// delegates
	public Parser[] getDelegates() {
		return new Parser[] {};
	}

	// delegators


	public DependenciesParser(TokenStream input) {
		this(input, new RecognizerSharedState());
	}
	public DependenciesParser(TokenStream input, RecognizerSharedState state) {
		super(input, state);
	}

	protected TreeAdaptor adaptor = new CommonTreeAdaptor();

	public void setTreeAdaptor(TreeAdaptor adaptor) {
		this.adaptor = adaptor;
	}
	public TreeAdaptor getTreeAdaptor() {
		return adaptor;
	}
	@Override public String[] getTokenNames() { return DependenciesParser.tokenNames; }
	@Override public String getGrammarFileName() { return "/Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g"; }


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



	public static class prog_return extends ParserRuleReturnScope {
		CommonTree tree;
		@Override
		public CommonTree getTree() { return tree; }
	};


	// $ANTLR start "prog"
	// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:55:1: prog : dependencies ;
	public final DependenciesParser.prog_return prog() throws RecognitionException {
		DependenciesParser.prog_return retval = new DependenciesParser.prog_return();
		retval.start = input.LT(1);

		CommonTree root_0 = null;

		ParserRuleReturnScope dependencies1 =null;


		try {
			// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:55:5: ( dependencies )
			// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:55:7: dependencies
			{
			root_0 = (CommonTree)adaptor.nil();


			pushFollow(FOLLOW_dependencies_in_prog54);
			dependencies1=dependencies();
			state._fsp--;

			adaptor.addChild(root_0, dependencies1.getTree());

			 if (logger.isDebugEnabled()) logger.debug((dependencies1!=null?((CommonTree)dependencies1.getTree()):null).toStringTree()); 
			}

			retval.stop = input.LT(-1);

			retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "prog"


	public static class dependencies_return extends ParserRuleReturnScope {
		CommonTree tree;
		@Override
		public CommonTree getTree() { return tree; }
	};


	// $ANTLR start "dependencies"
	// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:57:1: dependencies : ( 'STTGDs:' ( sttgd )+ | 'DED-STTGDs:' ( dedstgd )+ )? ( 'ExtTGDs:' ( etgd )+ | 'DED-ExtTGDs:' ( dedetgd )+ )? ( 'EGDs:' ( egd )+ | 'DED-EGDs:' ( dedegd )+ | 'ExtEGDs:' ( eegd )+ )? ( 'DCs:' ( dc )+ )? ;
	public final DependenciesParser.dependencies_return dependencies() throws RecognitionException {
		DependenciesParser.dependencies_return retval = new DependenciesParser.dependencies_return();
		retval.start = input.LT(1);

		CommonTree root_0 = null;

		Token string_literal2=null;
		Token string_literal4=null;
		Token string_literal6=null;
		Token string_literal8=null;
		Token string_literal10=null;
		Token string_literal12=null;
		Token string_literal14=null;
		Token string_literal16=null;
		ParserRuleReturnScope sttgd3 =null;
		ParserRuleReturnScope dedstgd5 =null;
		ParserRuleReturnScope etgd7 =null;
		ParserRuleReturnScope dedetgd9 =null;
		ParserRuleReturnScope egd11 =null;
		ParserRuleReturnScope dedegd13 =null;
		ParserRuleReturnScope eegd15 =null;
		ParserRuleReturnScope dc17 =null;

		CommonTree string_literal2_tree=null;
		CommonTree string_literal4_tree=null;
		CommonTree string_literal6_tree=null;
		CommonTree string_literal8_tree=null;
		CommonTree string_literal10_tree=null;
		CommonTree string_literal12_tree=null;
		CommonTree string_literal14_tree=null;
		CommonTree string_literal16_tree=null;

		try {
			// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:57:13: ( ( 'STTGDs:' ( sttgd )+ | 'DED-STTGDs:' ( dedstgd )+ )? ( 'ExtTGDs:' ( etgd )+ | 'DED-ExtTGDs:' ( dedetgd )+ )? ( 'EGDs:' ( egd )+ | 'DED-EGDs:' ( dedegd )+ | 'ExtEGDs:' ( eegd )+ )? ( 'DCs:' ( dc )+ )? )
			// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:58:11: ( 'STTGDs:' ( sttgd )+ | 'DED-STTGDs:' ( dedstgd )+ )? ( 'ExtTGDs:' ( etgd )+ | 'DED-ExtTGDs:' ( dedetgd )+ )? ( 'EGDs:' ( egd )+ | 'DED-EGDs:' ( dedegd )+ | 'ExtEGDs:' ( eegd )+ )? ( 'DCs:' ( dc )+ )?
			{
			root_0 = (CommonTree)adaptor.nil();


			// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:58:11: ( 'STTGDs:' ( sttgd )+ | 'DED-STTGDs:' ( dedstgd )+ )?
			int alt3=3;
			int LA3_0 = input.LA(1);
			if ( (LA3_0==28) ) {
				alt3=1;
			}
			else if ( (LA3_0==24) ) {
				alt3=2;
			}
			switch (alt3) {
				case 1 :
					// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:58:12: 'STTGDs:' ( sttgd )+
					{
					string_literal2=(Token)match(input,28,FOLLOW_28_in_dependencies80); 
					string_literal2_tree = (CommonTree)adaptor.create(string_literal2);
					adaptor.addChild(root_0, string_literal2_tree);

					// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:58:22: ( sttgd )+
					int cnt1=0;
					loop1:
					while (true) {
						int alt1=2;
						int LA1_0 = input.LA(1);
						if ( (LA1_0==IDENTIFIER) ) {
							alt1=1;
						}

						switch (alt1) {
						case 1 :
							// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:58:22: sttgd
							{
							pushFollow(FOLLOW_sttgd_in_dependencies82);
							sttgd3=sttgd();
							state._fsp--;

							adaptor.addChild(root_0, sttgd3.getTree());

							}
							break;

						default :
							if ( cnt1 >= 1 ) break loop1;
							EarlyExitException eee = new EarlyExitException(1, input);
							throw eee;
						}
						cnt1++;
					}

					 counter = 0;
					}
					break;
				case 2 :
					// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:59:12: 'DED-STTGDs:' ( dedstgd )+
					{
					string_literal4=(Token)match(input,24,FOLLOW_24_in_dependencies100); 
					string_literal4_tree = (CommonTree)adaptor.create(string_literal4);
					adaptor.addChild(root_0, string_literal4_tree);

					// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:59:26: ( dedstgd )+
					int cnt2=0;
					loop2:
					while (true) {
						int alt2=2;
						int LA2_0 = input.LA(1);
						if ( (LA2_0==IDENTIFIER) ) {
							alt2=1;
						}

						switch (alt2) {
						case 1 :
							// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:59:26: dedstgd
							{
							pushFollow(FOLLOW_dedstgd_in_dependencies102);
							dedstgd5=dedstgd();
							state._fsp--;

							adaptor.addChild(root_0, dedstgd5.getTree());

							}
							break;

						default :
							if ( cnt2 >= 1 ) break loop2;
							EarlyExitException eee = new EarlyExitException(2, input);
							throw eee;
						}
						cnt2++;
					}

					counter = 0;
					}
					break;

			}

			// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:60:11: ( 'ExtTGDs:' ( etgd )+ | 'DED-ExtTGDs:' ( dedetgd )+ )?
			int alt6=3;
			int LA6_0 = input.LA(1);
			if ( (LA6_0==27) ) {
				alt6=1;
			}
			else if ( (LA6_0==23) ) {
				alt6=2;
			}
			switch (alt6) {
				case 1 :
					// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:60:12: 'ExtTGDs:' ( etgd )+
					{
					string_literal6=(Token)match(input,27,FOLLOW_27_in_dependencies121); 
					string_literal6_tree = (CommonTree)adaptor.create(string_literal6);
					adaptor.addChild(root_0, string_literal6_tree);

					// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:60:23: ( etgd )+
					int cnt4=0;
					loop4:
					while (true) {
						int alt4=2;
						int LA4_0 = input.LA(1);
						if ( (LA4_0==IDENTIFIER) ) {
							alt4=1;
						}

						switch (alt4) {
						case 1 :
							// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:60:23: etgd
							{
							pushFollow(FOLLOW_etgd_in_dependencies123);
							etgd7=etgd();
							state._fsp--;

							adaptor.addChild(root_0, etgd7.getTree());

							}
							break;

						default :
							if ( cnt4 >= 1 ) break loop4;
							EarlyExitException eee = new EarlyExitException(4, input);
							throw eee;
						}
						cnt4++;
					}

					 counter = 0;
					}
					break;
				case 2 :
					// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:61:12: 'DED-ExtTGDs:' ( dedetgd )+
					{
					string_literal8=(Token)match(input,23,FOLLOW_23_in_dependencies141); 
					string_literal8_tree = (CommonTree)adaptor.create(string_literal8);
					adaptor.addChild(root_0, string_literal8_tree);

					// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:61:27: ( dedetgd )+
					int cnt5=0;
					loop5:
					while (true) {
						int alt5=2;
						int LA5_0 = input.LA(1);
						if ( (LA5_0==IDENTIFIER) ) {
							alt5=1;
						}

						switch (alt5) {
						case 1 :
							// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:61:27: dedetgd
							{
							pushFollow(FOLLOW_dedetgd_in_dependencies143);
							dedetgd9=dedetgd();
							state._fsp--;

							adaptor.addChild(root_0, dedetgd9.getTree());

							}
							break;

						default :
							if ( cnt5 >= 1 ) break loop5;
							EarlyExitException eee = new EarlyExitException(5, input);
							throw eee;
						}
						cnt5++;
					}

					counter = 0;
					}
					break;

			}

			// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:62:11: ( 'EGDs:' ( egd )+ | 'DED-EGDs:' ( dedegd )+ | 'ExtEGDs:' ( eegd )+ )?
			int alt10=4;
			switch ( input.LA(1) ) {
				case 25:
					{
					alt10=1;
					}
					break;
				case 22:
					{
					alt10=2;
					}
					break;
				case 26:
					{
					alt10=3;
					}
					break;
			}
			switch (alt10) {
				case 1 :
					// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:62:12: 'EGDs:' ( egd )+
					{
					string_literal10=(Token)match(input,25,FOLLOW_25_in_dependencies162); 
					string_literal10_tree = (CommonTree)adaptor.create(string_literal10);
					adaptor.addChild(root_0, string_literal10_tree);

					// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:62:20: ( egd )+
					int cnt7=0;
					loop7:
					while (true) {
						int alt7=2;
						int LA7_0 = input.LA(1);
						if ( (LA7_0==IDENTIFIER) ) {
							alt7=1;
						}

						switch (alt7) {
						case 1 :
							// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:62:20: egd
							{
							pushFollow(FOLLOW_egd_in_dependencies164);
							egd11=egd();
							state._fsp--;

							adaptor.addChild(root_0, egd11.getTree());

							}
							break;

						default :
							if ( cnt7 >= 1 ) break loop7;
							EarlyExitException eee = new EarlyExitException(7, input);
							throw eee;
						}
						cnt7++;
					}

					 counter = 0;
					}
					break;
				case 2 :
					// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:63:12: 'DED-EGDs:' ( dedegd )+
					{
					string_literal12=(Token)match(input,22,FOLLOW_22_in_dependencies182); 
					string_literal12_tree = (CommonTree)adaptor.create(string_literal12);
					adaptor.addChild(root_0, string_literal12_tree);

					// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:63:24: ( dedegd )+
					int cnt8=0;
					loop8:
					while (true) {
						int alt8=2;
						int LA8_0 = input.LA(1);
						if ( (LA8_0==IDENTIFIER) ) {
							alt8=1;
						}

						switch (alt8) {
						case 1 :
							// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:63:24: dedegd
							{
							pushFollow(FOLLOW_dedegd_in_dependencies184);
							dedegd13=dedegd();
							state._fsp--;

							adaptor.addChild(root_0, dedegd13.getTree());

							}
							break;

						default :
							if ( cnt8 >= 1 ) break loop8;
							EarlyExitException eee = new EarlyExitException(8, input);
							throw eee;
						}
						cnt8++;
					}

					counter = 0;
					}
					break;
				case 3 :
					// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:64:12: 'ExtEGDs:' ( eegd )+
					{
					string_literal14=(Token)match(input,26,FOLLOW_26_in_dependencies203); 
					string_literal14_tree = (CommonTree)adaptor.create(string_literal14);
					adaptor.addChild(root_0, string_literal14_tree);

					// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:64:23: ( eegd )+
					int cnt9=0;
					loop9:
					while (true) {
						int alt9=2;
						int LA9_0 = input.LA(1);
						if ( (LA9_0==IDENTIFIER) ) {
							alt9=1;
						}

						switch (alt9) {
						case 1 :
							// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:64:23: eegd
							{
							pushFollow(FOLLOW_eegd_in_dependencies205);
							eegd15=eegd();
							state._fsp--;

							adaptor.addChild(root_0, eegd15.getTree());

							}
							break;

						default :
							if ( cnt9 >= 1 ) break loop9;
							EarlyExitException eee = new EarlyExitException(9, input);
							throw eee;
						}
						cnt9++;
					}

					 counter = 0;
					}
					break;

			}

			// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:65:11: ( 'DCs:' ( dc )+ )?
			int alt12=2;
			int LA12_0 = input.LA(1);
			if ( (LA12_0==21) ) {
				alt12=1;
			}
			switch (alt12) {
				case 1 :
					// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:65:12: 'DCs:' ( dc )+
					{
					string_literal16=(Token)match(input,21,FOLLOW_21_in_dependencies224); 
					string_literal16_tree = (CommonTree)adaptor.create(string_literal16);
					adaptor.addChild(root_0, string_literal16_tree);

					// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:65:19: ( dc )+
					int cnt11=0;
					loop11:
					while (true) {
						int alt11=2;
						int LA11_0 = input.LA(1);
						if ( (LA11_0==IDENTIFIER) ) {
							alt11=1;
						}

						switch (alt11) {
						case 1 :
							// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:65:19: dc
							{
							pushFollow(FOLLOW_dc_in_dependencies226);
							dc17=dc();
							state._fsp--;

							adaptor.addChild(root_0, dc17.getTree());

							}
							break;

						default :
							if ( cnt11 >= 1 ) break loop11;
							EarlyExitException eee = new EarlyExitException(11, input);
							throw eee;
						}
						cnt11++;
					}

					 counter = 0;
					}
					break;

			}

			}

			retval.stop = input.LT(-1);

			retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "dependencies"


	public static class sttgd_return extends ParserRuleReturnScope {
		CommonTree tree;
		@Override
		public CommonTree getTree() { return tree; }
	};


	// $ANTLR start "sttgd"
	// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:67:1: sttgd : dependency ;
	public final DependenciesParser.sttgd_return sttgd() throws RecognitionException {
		DependenciesParser.sttgd_return retval = new DependenciesParser.sttgd_return();
		retval.start = input.LT(1);

		CommonTree root_0 = null;

		ParserRuleReturnScope dependency18 =null;


		try {
			// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:67:6: ( dependency )
			// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:67:11: dependency
			{
			root_0 = (CommonTree)adaptor.nil();


			pushFollow(FOLLOW_dependency_in_sttgd242);
			dependency18=dependency();
			state._fsp--;

			adaptor.addChild(root_0, dependency18.getTree());

			  dependency.setType(LunaticConstants.STTGD); dependency.setId("m" + counter++); generator.addSTTGD(dependency); 
			}

			retval.stop = input.LT(-1);

			retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "sttgd"


	public static class etgd_return extends ParserRuleReturnScope {
		CommonTree tree;
		@Override
		public CommonTree getTree() { return tree; }
	};


	// $ANTLR start "etgd"
	// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:69:1: etgd : dependency ;
	public final DependenciesParser.etgd_return etgd() throws RecognitionException {
		DependenciesParser.etgd_return retval = new DependenciesParser.etgd_return();
		retval.start = input.LT(1);

		CommonTree root_0 = null;

		ParserRuleReturnScope dependency19 =null;


		try {
			// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:69:5: ( dependency )
			// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:69:10: dependency
			{
			root_0 = (CommonTree)adaptor.nil();


			pushFollow(FOLLOW_dependency_in_etgd255);
			dependency19=dependency();
			state._fsp--;

			adaptor.addChild(root_0, dependency19.getTree());

			  dependency.setType(LunaticConstants.ExtTGD); dependency.setId("t" + counter++); generator.addExtTGD(dependency); 
			}

			retval.stop = input.LT(-1);

			retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "etgd"


	public static class dc_return extends ParserRuleReturnScope {
		CommonTree tree;
		@Override
		public CommonTree getTree() { return tree; }
	};


	// $ANTLR start "dc"
	// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:71:1: dc : dependency ;
	public final DependenciesParser.dc_return dc() throws RecognitionException {
		DependenciesParser.dc_return retval = new DependenciesParser.dc_return();
		retval.start = input.LT(1);

		CommonTree root_0 = null;

		ParserRuleReturnScope dependency20 =null;


		try {
			// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:71:3: ( dependency )
			// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:71:8: dependency
			{
			root_0 = (CommonTree)adaptor.nil();


			pushFollow(FOLLOW_dependency_in_dc268);
			dependency20=dependency();
			state._fsp--;

			adaptor.addChild(root_0, dependency20.getTree());

			  dependency.setType(LunaticConstants.DC); dependency.setId("d" + counter++); generator.addDC(dependency); 
			}

			retval.stop = input.LT(-1);

			retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "dc"


	public static class egd_return extends ParserRuleReturnScope {
		CommonTree tree;
		@Override
		public CommonTree getTree() { return tree; }
	};


	// $ANTLR start "egd"
	// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:73:1: egd : dependency ;
	public final DependenciesParser.egd_return egd() throws RecognitionException {
		DependenciesParser.egd_return retval = new DependenciesParser.egd_return();
		retval.start = input.LT(1);

		CommonTree root_0 = null;

		ParserRuleReturnScope dependency21 =null;


		try {
			// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:73:4: ( dependency )
			// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:73:9: dependency
			{
			root_0 = (CommonTree)adaptor.nil();


			pushFollow(FOLLOW_dependency_in_egd281);
			dependency21=dependency();
			state._fsp--;

			adaptor.addChild(root_0, dependency21.getTree());

			  dependency.setType(LunaticConstants.EGD); dependency.setId("e" + counter++); generator.addEGD(dependency); 
			}

			retval.stop = input.LT(-1);

			retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "egd"


	public static class eegd_return extends ParserRuleReturnScope {
		CommonTree tree;
		@Override
		public CommonTree getTree() { return tree; }
	};


	// $ANTLR start "eegd"
	// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:75:1: eegd : dependency ;
	public final DependenciesParser.eegd_return eegd() throws RecognitionException {
		DependenciesParser.eegd_return retval = new DependenciesParser.eegd_return();
		retval.start = input.LT(1);

		CommonTree root_0 = null;

		ParserRuleReturnScope dependency22 =null;


		try {
			// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:75:5: ( dependency )
			// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:75:10: dependency
			{
			root_0 = (CommonTree)adaptor.nil();


			pushFollow(FOLLOW_dependency_in_eegd300);
			dependency22=dependency();
			state._fsp--;

			adaptor.addChild(root_0, dependency22.getTree());

			  dependency.setType(LunaticConstants.ExtEGD); dependency.setId("e" + counter++); generator.addExtEGD(dependency); 
			}

			retval.stop = input.LT(-1);

			retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "eegd"


	public static class dedstgd_return extends ParserRuleReturnScope {
		CommonTree tree;
		@Override
		public CommonTree getTree() { return tree; }
	};


	// $ANTLR start "dedstgd"
	// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:77:1: dedstgd : ded ;
	public final DependenciesParser.dedstgd_return dedstgd() throws RecognitionException {
		DependenciesParser.dedstgd_return retval = new DependenciesParser.dedstgd_return();
		retval.start = input.LT(1);

		CommonTree root_0 = null;

		ParserRuleReturnScope ded23 =null;


		try {
			// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:77:8: ( ded )
			// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:77:11: ded
			{
			root_0 = (CommonTree)adaptor.nil();


			pushFollow(FOLLOW_ded_in_dedstgd317);
			ded23=ded();
			state._fsp--;

			adaptor.addChild(root_0, ded23.getTree());

			  ded.setType(LunaticConstants.STTGD); ded.setId("ded_m" + counter++); generator.addDEDSTTGD(ded); 
			}

			retval.stop = input.LT(-1);

			retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "dedstgd"


	public static class dedetgd_return extends ParserRuleReturnScope {
		CommonTree tree;
		@Override
		public CommonTree getTree() { return tree; }
	};


	// $ANTLR start "dedetgd"
	// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:79:1: dedetgd : ded ;
	public final DependenciesParser.dedetgd_return dedetgd() throws RecognitionException {
		DependenciesParser.dedetgd_return retval = new DependenciesParser.dedetgd_return();
		retval.start = input.LT(1);

		CommonTree root_0 = null;

		ParserRuleReturnScope ded24 =null;


		try {
			// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:79:8: ( ded )
			// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:79:11: ded
			{
			root_0 = (CommonTree)adaptor.nil();


			pushFollow(FOLLOW_ded_in_dedetgd340);
			ded24=ded();
			state._fsp--;

			adaptor.addChild(root_0, ded24.getTree());

			  ded.setType(LunaticConstants.ExtTGD); ded.setId("ded_t" + counter++); generator.addDEDExtTGD(ded); 
			}

			retval.stop = input.LT(-1);

			retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "dedetgd"


	public static class dedegd_return extends ParserRuleReturnScope {
		CommonTree tree;
		@Override
		public CommonTree getTree() { return tree; }
	};


	// $ANTLR start "dedegd"
	// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:81:1: dedegd : ded ;
	public final DependenciesParser.dedegd_return dedegd() throws RecognitionException {
		DependenciesParser.dedegd_return retval = new DependenciesParser.dedegd_return();
		retval.start = input.LT(1);

		CommonTree root_0 = null;

		ParserRuleReturnScope ded25 =null;


		try {
			// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:81:7: ( ded )
			// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:81:18: ded
			{
			root_0 = (CommonTree)adaptor.nil();


			pushFollow(FOLLOW_ded_in_dedegd365);
			ded25=ded();
			state._fsp--;

			adaptor.addChild(root_0, ded25.getTree());

			  ded.setType(LunaticConstants.EGD); ded.setId("ded_e" + counter++); generator.addDEDExtEGD(ded); 
			}

			retval.stop = input.LT(-1);

			retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "dedegd"


	public static class dependency_return extends ParserRuleReturnScope {
		CommonTree tree;
		@Override
		public CommonTree getTree() { return tree; }
	};


	// $ANTLR start "dependency"
	// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:83:1: dependency : (id= IDENTIFIER ':' )? positiveFormula ( negatedFormula )* '->' ( '#fail' | conclusionFormula ) '.' ;
	public final DependenciesParser.dependency_return dependency() throws RecognitionException {
		DependenciesParser.dependency_return retval = new DependenciesParser.dependency_return();
		retval.start = input.LT(1);

		CommonTree root_0 = null;

		Token id=null;
		Token char_literal26=null;
		Token string_literal29=null;
		Token string_literal30=null;
		Token char_literal32=null;
		ParserRuleReturnScope positiveFormula27 =null;
		ParserRuleReturnScope negatedFormula28 =null;
		ParserRuleReturnScope conclusionFormula31 =null;

		CommonTree id_tree=null;
		CommonTree char_literal26_tree=null;
		CommonTree string_literal29_tree=null;
		CommonTree string_literal30_tree=null;
		CommonTree char_literal32_tree=null;

		try {
			// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:83:11: ( (id= IDENTIFIER ':' )? positiveFormula ( negatedFormula )* '->' ( '#fail' | conclusionFormula ) '.' )
			// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:83:14: (id= IDENTIFIER ':' )? positiveFormula ( negatedFormula )* '->' ( '#fail' | conclusionFormula ) '.'
			{
			root_0 = (CommonTree)adaptor.nil();


			// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:83:14: (id= IDENTIFIER ':' )?
			int alt13=2;
			int LA13_0 = input.LA(1);
			if ( (LA13_0==IDENTIFIER) ) {
				int LA13_1 = input.LA(2);
				if ( (LA13_1==20) ) {
					alt13=1;
				}
			}
			switch (alt13) {
				case 1 :
					// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:83:15: id= IDENTIFIER ':'
					{
					id=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_dependency387); 
					id_tree = (CommonTree)adaptor.create(id);
					adaptor.addChild(root_0, id_tree);

					char_literal26=(Token)match(input,20,FOLLOW_20_in_dependency388); 
					char_literal26_tree = (CommonTree)adaptor.create(char_literal26);
					adaptor.addChild(root_0, char_literal26_tree);

					}
					break;

			}

			  dependency = new Dependency(); 
			                    formulaWN = new FormulaWithNegations(); 
			                    formulaStack.push(formulaWN);
			                    dependency.setPremise(formulaWN);
			                    if(id!=null) dependency.setId(id.getText()); 
			pushFollow(FOLLOW_positiveFormula_in_dependency397);
			positiveFormula27=positiveFormula();
			state._fsp--;

			adaptor.addChild(root_0, positiveFormula27.getTree());

			// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:88:21: ( negatedFormula )*
			loop14:
			while (true) {
				int alt14=2;
				int LA14_0 = input.LA(1);
				if ( (LA14_0==32) ) {
					alt14=1;
				}

				switch (alt14) {
				case 1 :
					// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:88:23: negatedFormula
					{
					pushFollow(FOLLOW_negatedFormula_in_dependency402);
					negatedFormula28=negatedFormula();
					state._fsp--;

					adaptor.addChild(root_0, negatedFormula28.getTree());

					}
					break;

				default :
					break loop14;
				}
			}

			string_literal29=(Token)match(input,18,FOLLOW_18_in_dependency409); 
			string_literal29_tree = (CommonTree)adaptor.create(string_literal29);
			adaptor.addChild(root_0, string_literal29_tree);

			// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:89:4: ( '#fail' | conclusionFormula )
			int alt15=2;
			int LA15_0 = input.LA(1);
			if ( (LA15_0==14) ) {
				alt15=1;
			}
			else if ( ((LA15_0 >= EXPRESSION && LA15_0 <= IDENTIFIER)||LA15_0==NUMBER||LA15_0==STRING||LA15_0==30) ) {
				alt15=2;
			}

			else {
				NoViableAltException nvae =
					new NoViableAltException("", 15, 0, input);
				throw nvae;
			}

			switch (alt15) {
				case 1 :
					// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:89:5: '#fail'
					{
					string_literal30=(Token)match(input,14,FOLLOW_14_in_dependency416); 
					string_literal30_tree = (CommonTree)adaptor.create(string_literal30);
					adaptor.addChild(root_0, string_literal30_tree);

					  formulaStack.clear(); 
					                    dependency.setConclusion(NullFormula.getInstance());
					}
					break;
				case 2 :
					// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:93:4: conclusionFormula
					{
					  formulaStack.clear(); 
					pushFollow(FOLLOW_conclusionFormula_in_dependency453);
					conclusionFormula31=conclusionFormula();
					state._fsp--;

					adaptor.addChild(root_0, conclusionFormula31.getTree());

					}
					break;

			}

			char_literal32=(Token)match(input,19,FOLLOW_19_in_dependency456); 
			char_literal32_tree = (CommonTree)adaptor.create(char_literal32);
			adaptor.addChild(root_0, char_literal32_tree);

			}

			retval.stop = input.LT(-1);

			retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "dependency"


	public static class ded_return extends ParserRuleReturnScope {
		CommonTree tree;
		@Override
		public CommonTree getTree() { return tree; }
	};


	// $ANTLR start "ded"
	// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:96:1: ded : positiveFormula ( negatedFormula )* '->' dedConclusion ( '|' dedConclusion )* '.' ;
	public final DependenciesParser.ded_return ded() throws RecognitionException {
		DependenciesParser.ded_return retval = new DependenciesParser.ded_return();
		retval.start = input.LT(1);

		CommonTree root_0 = null;

		Token string_literal35=null;
		Token char_literal37=null;
		Token char_literal39=null;
		ParserRuleReturnScope positiveFormula33 =null;
		ParserRuleReturnScope negatedFormula34 =null;
		ParserRuleReturnScope dedConclusion36 =null;
		ParserRuleReturnScope dedConclusion38 =null;

		CommonTree string_literal35_tree=null;
		CommonTree char_literal37_tree=null;
		CommonTree char_literal39_tree=null;

		try {
			// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:96:4: ( positiveFormula ( negatedFormula )* '->' dedConclusion ( '|' dedConclusion )* '.' )
			// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:96:15: positiveFormula ( negatedFormula )* '->' dedConclusion ( '|' dedConclusion )* '.'
			{
			root_0 = (CommonTree)adaptor.nil();


			  ded = new DED(); 
			                    formulaWN = new FormulaWithNegations(); 
			                    formulaStack.push(formulaWN);
			                    dedPremise = formulaWN;
			pushFollow(FOLLOW_positiveFormula_in_ded484);
			positiveFormula33=positiveFormula();
			state._fsp--;

			adaptor.addChild(root_0, positiveFormula33.getTree());

			// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:100:21: ( negatedFormula )*
			loop16:
			while (true) {
				int alt16=2;
				int LA16_0 = input.LA(1);
				if ( (LA16_0==32) ) {
					alt16=1;
				}

				switch (alt16) {
				case 1 :
					// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:100:23: negatedFormula
					{
					pushFollow(FOLLOW_negatedFormula_in_ded489);
					negatedFormula34=negatedFormula();
					state._fsp--;

					adaptor.addChild(root_0, negatedFormula34.getTree());

					}
					break;

				default :
					break loop16;
				}
			}

			string_literal35=(Token)match(input,18,FOLLOW_18_in_ded496); 
			string_literal35_tree = (CommonTree)adaptor.create(string_literal35);
			adaptor.addChild(root_0, string_literal35_tree);

			pushFollow(FOLLOW_dedConclusion_in_ded502);
			dedConclusion36=dedConclusion();
			state._fsp--;

			adaptor.addChild(root_0, dedConclusion36.getTree());

			// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:101:18: ( '|' dedConclusion )*
			loop17:
			while (true) {
				int alt17=2;
				int LA17_0 = input.LA(1);
				if ( (LA17_0==33) ) {
					alt17=1;
				}

				switch (alt17) {
				case 1 :
					// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:101:19: '|' dedConclusion
					{
					char_literal37=(Token)match(input,33,FOLLOW_33_in_ded505); 
					char_literal37_tree = (CommonTree)adaptor.create(char_literal37);
					adaptor.addChild(root_0, char_literal37_tree);

					pushFollow(FOLLOW_dedConclusion_in_ded507);
					dedConclusion38=dedConclusion();
					state._fsp--;

					adaptor.addChild(root_0, dedConclusion38.getTree());

					}
					break;

				default :
					break loop17;
				}
			}

			char_literal39=(Token)match(input,19,FOLLOW_19_in_ded511); 
			char_literal39_tree = (CommonTree)adaptor.create(char_literal39);
			adaptor.addChild(root_0, char_literal39_tree);

			}

			retval.stop = input.LT(-1);

			retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "ded"


	public static class dedConclusion_return extends ParserRuleReturnScope {
		CommonTree tree;
		@Override
		public CommonTree getTree() { return tree; }
	};


	// $ANTLR start "dedConclusion"
	// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:103:1: dedConclusion : '[' atom ( ',' atom )* ']' ;
	public final DependenciesParser.dedConclusion_return dedConclusion() throws RecognitionException {
		DependenciesParser.dedConclusion_return retval = new DependenciesParser.dedConclusion_return();
		retval.start = input.LT(1);

		CommonTree root_0 = null;

		Token char_literal40=null;
		Token char_literal42=null;
		Token char_literal44=null;
		ParserRuleReturnScope atom41 =null;
		ParserRuleReturnScope atom43 =null;

		CommonTree char_literal40_tree=null;
		CommonTree char_literal42_tree=null;
		CommonTree char_literal44_tree=null;

		try {
			// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:103:14: ( '[' atom ( ',' atom )* ']' )
			// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:103:19: '[' atom ( ',' atom )* ']'
			{
			root_0 = (CommonTree)adaptor.nil();


			char_literal40=(Token)match(input,29,FOLLOW_29_in_dedConclusion521); 
			char_literal40_tree = (CommonTree)adaptor.create(char_literal40);
			adaptor.addChild(root_0, char_literal40_tree);

			 formulaStack.clear(); 
			                        dependency = new Dependency();
			                        ded.addAssociatedDependency(dependency);                        
			                        dependency.setPremise(dedPremise.clone());
						positiveFormula = new PositiveFormula(); 
			                        dependency.setConclusion(positiveFormula); 
			pushFollow(FOLLOW_atom_in_dedConclusion543);
			atom41=atom();
			state._fsp--;

			adaptor.addChild(root_0, atom41.getTree());

			// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:109:24: ( ',' atom )*
			loop18:
			while (true) {
				int alt18=2;
				int LA18_0 = input.LA(1);
				if ( (LA18_0==17) ) {
					alt18=1;
				}

				switch (alt18) {
				case 1 :
					// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:109:25: ',' atom
					{
					char_literal42=(Token)match(input,17,FOLLOW_17_in_dedConclusion546); 
					char_literal42_tree = (CommonTree)adaptor.create(char_literal42);
					adaptor.addChild(root_0, char_literal42_tree);

					pushFollow(FOLLOW_atom_in_dedConclusion548);
					atom43=atom();
					state._fsp--;

					adaptor.addChild(root_0, atom43.getTree());

					}
					break;

				default :
					break loop18;
				}
			}

			char_literal44=(Token)match(input,31,FOLLOW_31_in_dedConclusion553); 
			char_literal44_tree = (CommonTree)adaptor.create(char_literal44);
			adaptor.addChild(root_0, char_literal44_tree);

			}

			retval.stop = input.LT(-1);

			retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "dedConclusion"


	public static class positiveFormula_return extends ParserRuleReturnScope {
		CommonTree tree;
		@Override
		public CommonTree getTree() { return tree; }
	};


	// $ANTLR start "positiveFormula"
	// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:111:1: positiveFormula : relationalAtom ( ',' atom )* ;
	public final DependenciesParser.positiveFormula_return positiveFormula() throws RecognitionException {
		DependenciesParser.positiveFormula_return retval = new DependenciesParser.positiveFormula_return();
		retval.start = input.LT(1);

		CommonTree root_0 = null;

		Token char_literal46=null;
		ParserRuleReturnScope relationalAtom45 =null;
		ParserRuleReturnScope atom47 =null;

		CommonTree char_literal46_tree=null;

		try {
			// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:111:16: ( relationalAtom ( ',' atom )* )
			// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:111:18: relationalAtom ( ',' atom )*
			{
			root_0 = (CommonTree)adaptor.nil();


			  positiveFormula = new PositiveFormula(); 
			                    positiveFormula.setFather(formulaStack.peek()); 
			                    formulaStack.peek().setPositiveFormula(positiveFormula); 
			pushFollow(FOLLOW_relationalAtom_in_positiveFormula607);
			relationalAtom45=relationalAtom();
			state._fsp--;

			adaptor.addChild(root_0, relationalAtom45.getTree());

			// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:114:34: ( ',' atom )*
			loop19:
			while (true) {
				int alt19=2;
				int LA19_0 = input.LA(1);
				if ( (LA19_0==17) ) {
					alt19=1;
				}

				switch (alt19) {
				case 1 :
					// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:114:35: ',' atom
					{
					char_literal46=(Token)match(input,17,FOLLOW_17_in_positiveFormula610); 
					char_literal46_tree = (CommonTree)adaptor.create(char_literal46);
					adaptor.addChild(root_0, char_literal46_tree);

					pushFollow(FOLLOW_atom_in_positiveFormula612);
					atom47=atom();
					state._fsp--;

					adaptor.addChild(root_0, atom47.getTree());

					}
					break;

				default :
					break loop19;
				}
			}

			}

			retval.stop = input.LT(-1);

			retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "positiveFormula"


	public static class negatedFormula_return extends ParserRuleReturnScope {
		CommonTree tree;
		@Override
		public CommonTree getTree() { return tree; }
	};


	// $ANTLR start "negatedFormula"
	// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:116:1: negatedFormula : 'and not exists' '(' ( positiveFormula ( negatedFormula )* ) ')' ;
	public final DependenciesParser.negatedFormula_return negatedFormula() throws RecognitionException {
		DependenciesParser.negatedFormula_return retval = new DependenciesParser.negatedFormula_return();
		retval.start = input.LT(1);

		CommonTree root_0 = null;

		Token string_literal48=null;
		Token char_literal49=null;
		Token char_literal52=null;
		ParserRuleReturnScope positiveFormula50 =null;
		ParserRuleReturnScope negatedFormula51 =null;

		CommonTree string_literal48_tree=null;
		CommonTree char_literal49_tree=null;
		CommonTree char_literal52_tree=null;

		try {
			// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:116:15: ( 'and not exists' '(' ( positiveFormula ( negatedFormula )* ) ')' )
			// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:116:18: 'and not exists' '(' ( positiveFormula ( negatedFormula )* ) ')'
			{
			root_0 = (CommonTree)adaptor.nil();


			  formulaWN = new FormulaWithNegations(); 
					    formulaWN.setFather(formulaStack.peek());
					    formulaStack.peek().addNegatedFormula(formulaWN);
			                    formulaStack.push(formulaWN); 
			string_literal48=(Token)match(input,32,FOLLOW_32_in_negatedFormula643); 
			string_literal48_tree = (CommonTree)adaptor.create(string_literal48);
			adaptor.addChild(root_0, string_literal48_tree);

			char_literal49=(Token)match(input,15,FOLLOW_15_in_negatedFormula644); 
			char_literal49_tree = (CommonTree)adaptor.create(char_literal49);
			adaptor.addChild(root_0, char_literal49_tree);

			// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:120:38: ( positiveFormula ( negatedFormula )* )
			// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:120:40: positiveFormula ( negatedFormula )*
			{
			pushFollow(FOLLOW_positiveFormula_in_negatedFormula648);
			positiveFormula50=positiveFormula();
			state._fsp--;

			adaptor.addChild(root_0, positiveFormula50.getTree());

			// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:120:56: ( negatedFormula )*
			loop20:
			while (true) {
				int alt20=2;
				int LA20_0 = input.LA(1);
				if ( (LA20_0==32) ) {
					alt20=1;
				}

				switch (alt20) {
				case 1 :
					// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:120:58: negatedFormula
					{
					pushFollow(FOLLOW_negatedFormula_in_negatedFormula652);
					negatedFormula51=negatedFormula();
					state._fsp--;

					adaptor.addChild(root_0, negatedFormula51.getTree());

					}
					break;

				default :
					break loop20;
				}
			}

			}

			char_literal52=(Token)match(input,16,FOLLOW_16_in_negatedFormula659); 
			char_literal52_tree = (CommonTree)adaptor.create(char_literal52);
			adaptor.addChild(root_0, char_literal52_tree);

			  formulaStack.pop(); 
			}

			retval.stop = input.LT(-1);

			retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "negatedFormula"


	public static class conclusionFormula_return extends ParserRuleReturnScope {
		CommonTree tree;
		@Override
		public CommonTree getTree() { return tree; }
	};


	// $ANTLR start "conclusionFormula"
	// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:123:1: conclusionFormula : atom ( ',' atom )* ;
	public final DependenciesParser.conclusionFormula_return conclusionFormula() throws RecognitionException {
		DependenciesParser.conclusionFormula_return retval = new DependenciesParser.conclusionFormula_return();
		retval.start = input.LT(1);

		CommonTree root_0 = null;

		Token char_literal54=null;
		ParserRuleReturnScope atom53 =null;
		ParserRuleReturnScope atom55 =null;

		CommonTree char_literal54_tree=null;

		try {
			// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:123:18: ( atom ( ',' atom )* )
			// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:123:20: atom ( ',' atom )*
			{
			root_0 = (CommonTree)adaptor.nil();


			  positiveFormula = new PositiveFormula(); 
			                      dependency.setConclusion(positiveFormula); 
			pushFollow(FOLLOW_atom_in_conclusionFormula705);
			atom53=atom();
			state._fsp--;

			adaptor.addChild(root_0, atom53.getTree());

			// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:125:24: ( ',' atom )*
			loop21:
			while (true) {
				int alt21=2;
				int LA21_0 = input.LA(1);
				if ( (LA21_0==17) ) {
					alt21=1;
				}

				switch (alt21) {
				case 1 :
					// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:125:25: ',' atom
					{
					char_literal54=(Token)match(input,17,FOLLOW_17_in_conclusionFormula708); 
					char_literal54_tree = (CommonTree)adaptor.create(char_literal54);
					adaptor.addChild(root_0, char_literal54_tree);

					pushFollow(FOLLOW_atom_in_conclusionFormula710);
					atom55=atom();
					state._fsp--;

					adaptor.addChild(root_0, atom55.getTree());

					}
					break;

				default :
					break loop21;
				}
			}

			}

			retval.stop = input.LT(-1);

			retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "conclusionFormula"


	public static class atom_return extends ParserRuleReturnScope {
		CommonTree tree;
		@Override
		public CommonTree getTree() { return tree; }
	};


	// $ANTLR start "atom"
	// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:127:1: atom : ( relationalAtom | builtin | comparison );
	public final DependenciesParser.atom_return atom() throws RecognitionException {
		DependenciesParser.atom_return retval = new DependenciesParser.atom_return();
		retval.start = input.LT(1);

		CommonTree root_0 = null;

		ParserRuleReturnScope relationalAtom56 =null;
		ParserRuleReturnScope builtin57 =null;
		ParserRuleReturnScope comparison58 =null;


		try {
			// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:127:6: ( relationalAtom | builtin | comparison )
			int alt22=3;
			switch ( input.LA(1) ) {
			case IDENTIFIER:
				{
				alt22=1;
				}
				break;
			case EXPRESSION:
				{
				alt22=2;
				}
				break;
			case NUMBER:
			case STRING:
			case 30:
				{
				alt22=3;
				}
				break;
			default:
				NoViableAltException nvae =
					new NoViableAltException("", 22, 0, input);
				throw nvae;
			}
			switch (alt22) {
				case 1 :
					// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:127:9: relationalAtom
					{
					root_0 = (CommonTree)adaptor.nil();


					pushFollow(FOLLOW_relationalAtom_in_atom723);
					relationalAtom56=relationalAtom();
					state._fsp--;

					adaptor.addChild(root_0, relationalAtom56.getTree());

					}
					break;
				case 2 :
					// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:127:26: builtin
					{
					root_0 = (CommonTree)adaptor.nil();


					pushFollow(FOLLOW_builtin_in_atom727);
					builtin57=builtin();
					state._fsp--;

					adaptor.addChild(root_0, builtin57.getTree());

					}
					break;
				case 3 :
					// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:127:36: comparison
					{
					root_0 = (CommonTree)adaptor.nil();


					pushFollow(FOLLOW_comparison_in_atom731);
					comparison58=comparison();
					state._fsp--;

					adaptor.addChild(root_0, comparison58.getTree());

					}
					break;

			}
			retval.stop = input.LT(-1);

			retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "atom"


	public static class relationalAtom_return extends ParserRuleReturnScope {
		CommonTree tree;
		@Override
		public CommonTree getTree() { return tree; }
	};


	// $ANTLR start "relationalAtom"
	// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:129:1: relationalAtom : name= IDENTIFIER '(' attribute ( ',' attribute )* ')' ;
	public final DependenciesParser.relationalAtom_return relationalAtom() throws RecognitionException {
		DependenciesParser.relationalAtom_return retval = new DependenciesParser.relationalAtom_return();
		retval.start = input.LT(1);

		CommonTree root_0 = null;

		Token name=null;
		Token char_literal59=null;
		Token char_literal61=null;
		Token char_literal63=null;
		ParserRuleReturnScope attribute60 =null;
		ParserRuleReturnScope attribute62 =null;

		CommonTree name_tree=null;
		CommonTree char_literal59_tree=null;
		CommonTree char_literal61_tree=null;
		CommonTree char_literal63_tree=null;

		try {
			// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:129:15: (name= IDENTIFIER '(' attribute ( ',' attribute )* ')' )
			// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:129:18: name= IDENTIFIER '(' attribute ( ',' attribute )* ')'
			{
			root_0 = (CommonTree)adaptor.nil();


			name=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_relationalAtom742); 
			name_tree = (CommonTree)adaptor.create(name);
			adaptor.addChild(root_0, name_tree);

			 atom = new RelationalAtom(name.getText()); 
			char_literal59=(Token)match(input,15,FOLLOW_15_in_relationalAtom746); 
			char_literal59_tree = (CommonTree)adaptor.create(char_literal59);
			adaptor.addChild(root_0, char_literal59_tree);

			pushFollow(FOLLOW_attribute_in_relationalAtom748);
			attribute60=attribute();
			state._fsp--;

			adaptor.addChild(root_0, attribute60.getTree());

			// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:129:95: ( ',' attribute )*
			loop23:
			while (true) {
				int alt23=2;
				int LA23_0 = input.LA(1);
				if ( (LA23_0==17) ) {
					alt23=1;
				}

				switch (alt23) {
				case 1 :
					// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:129:96: ',' attribute
					{
					char_literal61=(Token)match(input,17,FOLLOW_17_in_relationalAtom751); 
					char_literal61_tree = (CommonTree)adaptor.create(char_literal61);
					adaptor.addChild(root_0, char_literal61_tree);

					pushFollow(FOLLOW_attribute_in_relationalAtom753);
					attribute62=attribute();
					state._fsp--;

					adaptor.addChild(root_0, attribute62.getTree());

					}
					break;

				default :
					break loop23;
				}
			}

			char_literal63=(Token)match(input,16,FOLLOW_16_in_relationalAtom757); 
			char_literal63_tree = (CommonTree)adaptor.create(char_literal63);
			adaptor.addChild(root_0, char_literal63_tree);

			  positiveFormula.addAtom(atom); atom.setFormula(positiveFormula); 
			}

			retval.stop = input.LT(-1);

			retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "relationalAtom"


	public static class builtin_return extends ParserRuleReturnScope {
		CommonTree tree;
		@Override
		public CommonTree getTree() { return tree; }
	};


	// $ANTLR start "builtin"
	// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:132:1: builtin : expression= EXPRESSION ;
	public final DependenciesParser.builtin_return builtin() throws RecognitionException {
		DependenciesParser.builtin_return retval = new DependenciesParser.builtin_return();
		retval.start = input.LT(1);

		CommonTree root_0 = null;

		Token expression=null;

		CommonTree expression_tree=null;

		try {
			// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:132:9: (expression= EXPRESSION )
			// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:132:12: expression= EXPRESSION
			{
			root_0 = (CommonTree)adaptor.nil();


			expression=(Token)match(input,EXPRESSION,FOLLOW_EXPRESSION_in_builtin773); 
			expression_tree = (CommonTree)adaptor.create(expression);
			adaptor.addChild(root_0, expression_tree);

			  atom = new BuiltInAtom(positiveFormula, new Expression(generator.clean(expression.getText()))); 
			                    positiveFormula.addAtom(atom);  
			}

			retval.stop = input.LT(-1);

			retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "builtin"


	public static class comparison_return extends ParserRuleReturnScope {
		CommonTree tree;
		@Override
		public CommonTree getTree() { return tree; }
	};


	// $ANTLR start "comparison"
	// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:136:1: comparison : leftargument oper= OPERATOR rightargument ;
	public final DependenciesParser.comparison_return comparison() throws RecognitionException {
		DependenciesParser.comparison_return retval = new DependenciesParser.comparison_return();
		retval.start = input.LT(1);

		CommonTree root_0 = null;

		Token oper=null;
		ParserRuleReturnScope leftargument64 =null;
		ParserRuleReturnScope rightargument65 =null;

		CommonTree oper_tree=null;

		try {
			// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:136:12: ( leftargument oper= OPERATOR rightargument )
			// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:136:15: leftargument oper= OPERATOR rightargument
			{
			root_0 = (CommonTree)adaptor.nil();


			   expressionString = new StringBuilder(); 
					     leftConstant = null;
					     rightConstant = null;
			pushFollow(FOLLOW_leftargument_in_comparison832);
			leftargument64=leftargument();
			state._fsp--;

			adaptor.addChild(root_0, leftargument64.getTree());

			oper=(Token)match(input,OPERATOR,FOLLOW_OPERATOR_in_comparison854); 
			oper_tree = (CommonTree)adaptor.create(oper);
			adaptor.addChild(root_0, oper_tree);

			 expressionString.append(" ").append(oper.getText()); 
			pushFollow(FOLLOW_rightargument_in_comparison875);
			rightargument65=rightargument();
			state._fsp--;

			adaptor.addChild(root_0, rightargument65.getTree());

			  Expression expression = new Expression(expressionString.toString()); 
			                    atom = new ComparisonAtom(positiveFormula, expression, leftConstant, rightConstant, oper.getText()); 
			                    positiveFormula.addAtom(atom); 
			}

			retval.stop = input.LT(-1);

			retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "comparison"


	public static class leftargument_return extends ParserRuleReturnScope {
		CommonTree tree;
		@Override
		public CommonTree getTree() { return tree; }
	};


	// $ANTLR start "leftargument"
	// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:146:1: leftargument : ( '\\$' var= IDENTIFIER |constant= ( STRING | NUMBER ) ) ;
	public final DependenciesParser.leftargument_return leftargument() throws RecognitionException {
		DependenciesParser.leftargument_return retval = new DependenciesParser.leftargument_return();
		retval.start = input.LT(1);

		CommonTree root_0 = null;

		Token var=null;
		Token constant=null;
		Token char_literal66=null;

		CommonTree var_tree=null;
		CommonTree constant_tree=null;
		CommonTree char_literal66_tree=null;

		try {
			// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:146:13: ( ( '\\$' var= IDENTIFIER |constant= ( STRING | NUMBER ) ) )
			// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:146:16: ( '\\$' var= IDENTIFIER |constant= ( STRING | NUMBER ) )
			{
			root_0 = (CommonTree)adaptor.nil();


			// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:146:16: ( '\\$' var= IDENTIFIER |constant= ( STRING | NUMBER ) )
			int alt24=2;
			int LA24_0 = input.LA(1);
			if ( (LA24_0==30) ) {
				alt24=1;
			}
			else if ( (LA24_0==NUMBER||LA24_0==STRING) ) {
				alt24=2;
			}

			else {
				NoViableAltException nvae =
					new NoViableAltException("", 24, 0, input);
				throw nvae;
			}

			switch (alt24) {
				case 1 :
					// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:146:17: '\\$' var= IDENTIFIER
					{
					char_literal66=(Token)match(input,30,FOLLOW_30_in_leftargument905); 
					char_literal66_tree = (CommonTree)adaptor.create(char_literal66);
					adaptor.addChild(root_0, char_literal66_tree);

					var=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_leftargument908); 
					var_tree = (CommonTree)adaptor.create(var);
					adaptor.addChild(root_0, var_tree);

					 expressionString.append(var.getText()); 
					}
					break;
				case 2 :
					// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:147:18: constant= ( STRING | NUMBER )
					{
					constant=input.LT(1);
					if ( input.LA(1)==NUMBER||input.LA(1)==STRING ) {
						input.consume();
						adaptor.addChild(root_0, (CommonTree)adaptor.create(constant));
						state.errorRecovery=false;
					}
					else {
						MismatchedSetException mse = new MismatchedSetException(null,input);
						throw mse;
					}
					 expressionString.append(constant.getText()); leftConstant = constant.getText();
					}
					break;

			}

			}

			retval.stop = input.LT(-1);

			retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "leftargument"


	public static class rightargument_return extends ParserRuleReturnScope {
		CommonTree tree;
		@Override
		public CommonTree getTree() { return tree; }
	};


	// $ANTLR start "rightargument"
	// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:150:1: rightargument : ( '\\$' var= IDENTIFIER |constant= ( STRING | NUMBER ) ) ;
	public final DependenciesParser.rightargument_return rightargument() throws RecognitionException {
		DependenciesParser.rightargument_return retval = new DependenciesParser.rightargument_return();
		retval.start = input.LT(1);

		CommonTree root_0 = null;

		Token var=null;
		Token constant=null;
		Token char_literal67=null;

		CommonTree var_tree=null;
		CommonTree constant_tree=null;
		CommonTree char_literal67_tree=null;

		try {
			// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:150:14: ( ( '\\$' var= IDENTIFIER |constant= ( STRING | NUMBER ) ) )
			// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:150:17: ( '\\$' var= IDENTIFIER |constant= ( STRING | NUMBER ) )
			{
			root_0 = (CommonTree)adaptor.nil();


			// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:150:17: ( '\\$' var= IDENTIFIER |constant= ( STRING | NUMBER ) )
			int alt25=2;
			int LA25_0 = input.LA(1);
			if ( (LA25_0==30) ) {
				alt25=1;
			}
			else if ( (LA25_0==NUMBER||LA25_0==STRING) ) {
				alt25=2;
			}

			else {
				NoViableAltException nvae =
					new NoViableAltException("", 25, 0, input);
				throw nvae;
			}

			switch (alt25) {
				case 1 :
					// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:150:18: '\\$' var= IDENTIFIER
					{
					char_literal67=(Token)match(input,30,FOLLOW_30_in_rightargument986); 
					char_literal67_tree = (CommonTree)adaptor.create(char_literal67);
					adaptor.addChild(root_0, char_literal67_tree);

					var=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_rightargument989); 
					var_tree = (CommonTree)adaptor.create(var);
					adaptor.addChild(root_0, var_tree);

					 expressionString.append(var.getText()); 
					}
					break;
				case 2 :
					// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:151:18: constant= ( STRING | NUMBER )
					{
					constant=input.LT(1);
					if ( input.LA(1)==NUMBER||input.LA(1)==STRING ) {
						input.consume();
						adaptor.addChild(root_0, (CommonTree)adaptor.create(constant));
						state.errorRecovery=false;
					}
					else {
						MismatchedSetException mse = new MismatchedSetException(null,input);
						throw mse;
					}
					 expressionString.append(constant.getText()); rightConstant = constant.getText();
					}
					break;

			}

			}

			retval.stop = input.LT(-1);

			retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "rightargument"


	public static class attribute_return extends ParserRuleReturnScope {
		CommonTree tree;
		@Override
		public CommonTree getTree() { return tree; }
	};


	// $ANTLR start "attribute"
	// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:154:1: attribute : attr= IDENTIFIER ':' value ;
	public final DependenciesParser.attribute_return attribute() throws RecognitionException {
		DependenciesParser.attribute_return retval = new DependenciesParser.attribute_return();
		retval.start = input.LT(1);

		CommonTree root_0 = null;

		Token attr=null;
		Token char_literal68=null;
		ParserRuleReturnScope value69 =null;

		CommonTree attr_tree=null;
		CommonTree char_literal68_tree=null;

		try {
			// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:154:10: (attr= IDENTIFIER ':' value )
			// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:154:13: attr= IDENTIFIER ':' value
			{
			root_0 = (CommonTree)adaptor.nil();


			attr=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_attribute1051); 
			attr_tree = (CommonTree)adaptor.create(attr);
			adaptor.addChild(root_0, attr_tree);

			char_literal68=(Token)match(input,20,FOLLOW_20_in_attribute1053); 
			char_literal68_tree = (CommonTree)adaptor.create(char_literal68);
			adaptor.addChild(root_0, char_literal68_tree);

			 attribute = new FormulaAttribute(attr.getText()); 
			pushFollow(FOLLOW_value_in_attribute1057);
			value69=value();
			state._fsp--;

			adaptor.addChild(root_0, value69.getTree());

			 ((RelationalAtom)atom).addAttribute(attribute); 
			}

			retval.stop = input.LT(-1);

			retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "attribute"


	public static class value_return extends ParserRuleReturnScope {
		CommonTree tree;
		@Override
		public CommonTree getTree() { return tree; }
	};


	// $ANTLR start "value"
	// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:157:1: value : ( '\\$' var= IDENTIFIER |constant= ( STRING | NUMBER ) |nullValue= NULL |expression= EXPRESSION );
	public final DependenciesParser.value_return value() throws RecognitionException {
		DependenciesParser.value_return retval = new DependenciesParser.value_return();
		retval.start = input.LT(1);

		CommonTree root_0 = null;

		Token var=null;
		Token constant=null;
		Token nullValue=null;
		Token expression=null;
		Token char_literal70=null;

		CommonTree var_tree=null;
		CommonTree constant_tree=null;
		CommonTree nullValue_tree=null;
		CommonTree expression_tree=null;
		CommonTree char_literal70_tree=null;

		try {
			// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:157:7: ( '\\$' var= IDENTIFIER |constant= ( STRING | NUMBER ) |nullValue= NULL |expression= EXPRESSION )
			int alt26=4;
			switch ( input.LA(1) ) {
			case 30:
				{
				alt26=1;
				}
				break;
			case NUMBER:
			case STRING:
				{
				alt26=2;
				}
				break;
			case NULL:
				{
				alt26=3;
				}
				break;
			case EXPRESSION:
				{
				alt26=4;
				}
				break;
			default:
				NoViableAltException nvae =
					new NoViableAltException("", 26, 0, input);
				throw nvae;
			}
			switch (alt26) {
				case 1 :
					// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:157:10: '\\$' var= IDENTIFIER
					{
					root_0 = (CommonTree)adaptor.nil();


					char_literal70=(Token)match(input,30,FOLLOW_30_in_value1072); 
					char_literal70_tree = (CommonTree)adaptor.create(char_literal70);
					adaptor.addChild(root_0, char_literal70_tree);

					var=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_value1075); 
					var_tree = (CommonTree)adaptor.create(var);
					adaptor.addChild(root_0, var_tree);

					 attribute.setValue(new FormulaVariableOccurrence(new AttributeRef(((RelationalAtom)atom).getTableName(), attribute.getAttributeName()), var.getText())); 
					}
					break;
				case 2 :
					// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:158:18: constant= ( STRING | NUMBER )
					{
					root_0 = (CommonTree)adaptor.nil();


					constant=input.LT(1);
					if ( input.LA(1)==NUMBER||input.LA(1)==STRING ) {
						input.consume();
						adaptor.addChild(root_0, (CommonTree)adaptor.create(constant));
						state.errorRecovery=false;
					}
					else {
						MismatchedSetException mse = new MismatchedSetException(null,input);
						throw mse;
					}
					 attribute.setValue(new FormulaConstant(constant.getText())); 
					}
					break;
				case 3 :
					// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:159:18: nullValue= NULL
					{
					root_0 = (CommonTree)adaptor.nil();


					nullValue=(Token)match(input,NULL,FOLLOW_NULL_in_value1131); 
					nullValue_tree = (CommonTree)adaptor.create(nullValue);
					adaptor.addChild(root_0, nullValue_tree);

					 attribute.setValue(new FormulaConstant(nullValue.getText(), true)); 
					}
					break;
				case 4 :
					// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:160:18: expression= EXPRESSION
					{
					root_0 = (CommonTree)adaptor.nil();


					expression=(Token)match(input,EXPRESSION,FOLLOW_EXPRESSION_in_value1156); 
					expression_tree = (CommonTree)adaptor.create(expression);
					adaptor.addChild(root_0, expression_tree);

					 attribute.setValue(new FormulaExpression(new Expression(generator.clean(expression.getText())))); 
					}
					break;

			}
			retval.stop = input.LT(-1);

			retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "value"

	// Delegated rules



	public static final BitSet FOLLOW_dependencies_in_prog54 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_28_in_dependencies80 = new BitSet(new long[]{0x0000000000000040L});
	public static final BitSet FOLLOW_sttgd_in_dependencies82 = new BitSet(new long[]{0x000000000EE00042L});
	public static final BitSet FOLLOW_24_in_dependencies100 = new BitSet(new long[]{0x0000000000000040L});
	public static final BitSet FOLLOW_dedstgd_in_dependencies102 = new BitSet(new long[]{0x000000000EE00042L});
	public static final BitSet FOLLOW_27_in_dependencies121 = new BitSet(new long[]{0x0000000000000040L});
	public static final BitSet FOLLOW_etgd_in_dependencies123 = new BitSet(new long[]{0x0000000006600042L});
	public static final BitSet FOLLOW_23_in_dependencies141 = new BitSet(new long[]{0x0000000000000040L});
	public static final BitSet FOLLOW_dedetgd_in_dependencies143 = new BitSet(new long[]{0x0000000006600042L});
	public static final BitSet FOLLOW_25_in_dependencies162 = new BitSet(new long[]{0x0000000000000040L});
	public static final BitSet FOLLOW_egd_in_dependencies164 = new BitSet(new long[]{0x0000000000200042L});
	public static final BitSet FOLLOW_22_in_dependencies182 = new BitSet(new long[]{0x0000000000000040L});
	public static final BitSet FOLLOW_dedegd_in_dependencies184 = new BitSet(new long[]{0x0000000000200042L});
	public static final BitSet FOLLOW_26_in_dependencies203 = new BitSet(new long[]{0x0000000000000040L});
	public static final BitSet FOLLOW_eegd_in_dependencies205 = new BitSet(new long[]{0x0000000000200042L});
	public static final BitSet FOLLOW_21_in_dependencies224 = new BitSet(new long[]{0x0000000000000040L});
	public static final BitSet FOLLOW_dc_in_dependencies226 = new BitSet(new long[]{0x0000000000000042L});
	public static final BitSet FOLLOW_dependency_in_sttgd242 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_dependency_in_etgd255 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_dependency_in_dc268 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_dependency_in_egd281 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_dependency_in_eegd300 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ded_in_dedstgd317 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ded_in_dedetgd340 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ded_in_dedegd365 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_IDENTIFIER_in_dependency387 = new BitSet(new long[]{0x0000000000100000L});
	public static final BitSet FOLLOW_20_in_dependency388 = new BitSet(new long[]{0x0000000000000040L});
	public static final BitSet FOLLOW_positiveFormula_in_dependency397 = new BitSet(new long[]{0x0000000100040000L});
	public static final BitSet FOLLOW_negatedFormula_in_dependency402 = new BitSet(new long[]{0x0000000100040000L});
	public static final BitSet FOLLOW_18_in_dependency409 = new BitSet(new long[]{0x0000000040005460L});
	public static final BitSet FOLLOW_14_in_dependency416 = new BitSet(new long[]{0x0000000000080000L});
	public static final BitSet FOLLOW_conclusionFormula_in_dependency453 = new BitSet(new long[]{0x0000000000080000L});
	public static final BitSet FOLLOW_19_in_dependency456 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_positiveFormula_in_ded484 = new BitSet(new long[]{0x0000000100040000L});
	public static final BitSet FOLLOW_negatedFormula_in_ded489 = new BitSet(new long[]{0x0000000100040000L});
	public static final BitSet FOLLOW_18_in_ded496 = new BitSet(new long[]{0x0000000020000000L});
	public static final BitSet FOLLOW_dedConclusion_in_ded502 = new BitSet(new long[]{0x0000000200080000L});
	public static final BitSet FOLLOW_33_in_ded505 = new BitSet(new long[]{0x0000000020000000L});
	public static final BitSet FOLLOW_dedConclusion_in_ded507 = new BitSet(new long[]{0x0000000200080000L});
	public static final BitSet FOLLOW_19_in_ded511 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_29_in_dedConclusion521 = new BitSet(new long[]{0x0000000040001460L});
	public static final BitSet FOLLOW_atom_in_dedConclusion543 = new BitSet(new long[]{0x0000000080020000L});
	public static final BitSet FOLLOW_17_in_dedConclusion546 = new BitSet(new long[]{0x0000000040001460L});
	public static final BitSet FOLLOW_atom_in_dedConclusion548 = new BitSet(new long[]{0x0000000080020000L});
	public static final BitSet FOLLOW_31_in_dedConclusion553 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_relationalAtom_in_positiveFormula607 = new BitSet(new long[]{0x0000000000020002L});
	public static final BitSet FOLLOW_17_in_positiveFormula610 = new BitSet(new long[]{0x0000000040001460L});
	public static final BitSet FOLLOW_atom_in_positiveFormula612 = new BitSet(new long[]{0x0000000000020002L});
	public static final BitSet FOLLOW_32_in_negatedFormula643 = new BitSet(new long[]{0x0000000000008000L});
	public static final BitSet FOLLOW_15_in_negatedFormula644 = new BitSet(new long[]{0x0000000000000040L});
	public static final BitSet FOLLOW_positiveFormula_in_negatedFormula648 = new BitSet(new long[]{0x0000000100010000L});
	public static final BitSet FOLLOW_negatedFormula_in_negatedFormula652 = new BitSet(new long[]{0x0000000100010000L});
	public static final BitSet FOLLOW_16_in_negatedFormula659 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_atom_in_conclusionFormula705 = new BitSet(new long[]{0x0000000000020002L});
	public static final BitSet FOLLOW_17_in_conclusionFormula708 = new BitSet(new long[]{0x0000000040001460L});
	public static final BitSet FOLLOW_atom_in_conclusionFormula710 = new BitSet(new long[]{0x0000000000020002L});
	public static final BitSet FOLLOW_relationalAtom_in_atom723 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_builtin_in_atom727 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_comparison_in_atom731 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_IDENTIFIER_in_relationalAtom742 = new BitSet(new long[]{0x0000000000008000L});
	public static final BitSet FOLLOW_15_in_relationalAtom746 = new BitSet(new long[]{0x0000000000000040L});
	public static final BitSet FOLLOW_attribute_in_relationalAtom748 = new BitSet(new long[]{0x0000000000030000L});
	public static final BitSet FOLLOW_17_in_relationalAtom751 = new BitSet(new long[]{0x0000000000000040L});
	public static final BitSet FOLLOW_attribute_in_relationalAtom753 = new BitSet(new long[]{0x0000000000030000L});
	public static final BitSet FOLLOW_16_in_relationalAtom757 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_EXPRESSION_in_builtin773 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_leftargument_in_comparison832 = new BitSet(new long[]{0x0000000000000800L});
	public static final BitSet FOLLOW_OPERATOR_in_comparison854 = new BitSet(new long[]{0x0000000040001400L});
	public static final BitSet FOLLOW_rightargument_in_comparison875 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_30_in_leftargument905 = new BitSet(new long[]{0x0000000000000040L});
	public static final BitSet FOLLOW_IDENTIFIER_in_leftargument908 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_set_in_leftargument933 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_30_in_rightargument986 = new BitSet(new long[]{0x0000000000000040L});
	public static final BitSet FOLLOW_IDENTIFIER_in_rightargument989 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_set_in_rightargument1014 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_IDENTIFIER_in_attribute1051 = new BitSet(new long[]{0x0000000000100000L});
	public static final BitSet FOLLOW_20_in_attribute1053 = new BitSet(new long[]{0x0000000040001620L});
	public static final BitSet FOLLOW_value_in_attribute1057 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_30_in_value1072 = new BitSet(new long[]{0x0000000000000040L});
	public static final BitSet FOLLOW_IDENTIFIER_in_value1075 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_set_in_value1100 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_NULL_in_value1131 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_EXPRESSION_in_value1156 = new BitSet(new long[]{0x0000000000000002L});
}
