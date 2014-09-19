// $ANTLR 3.5.1 /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g 2014-09-19 10:25:07

package it.unibas.lunatic.parser.output;

import it.unibas.lunatic.LunaticConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import it.unibas.lunatic.model.database.AttributeRef;
import it.unibas.lunatic.parser.operators.ParseDependencies;
import it.unibas.lunatic.model.dependency.*;
import it.unibas.lunatic.model.expressions.Expression;


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
	// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:53:1: prog : dependencies ;
	public final DependenciesParser.prog_return prog() throws RecognitionException {
		DependenciesParser.prog_return retval = new DependenciesParser.prog_return();
		retval.start = input.LT(1);

		CommonTree root_0 = null;

		ParserRuleReturnScope dependencies1 =null;


		try {
			// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:53:5: ( dependencies )
			// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:53:7: dependencies
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
	// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:55:1: dependencies : ( 'STTGDs:' ( sttgd )+ | 'DED-STTGDs:' ( dedstgd )+ )? ( 'ExtTGDs:' ( etgd )+ | 'DED-ExtTGDs:' ( dedetgd )+ )? ( 'DCs:' ( dc )+ )? ( 'EGDs:' ( egd )+ | 'DED-EGDs:' ( dedegd )+ | 'ExtEGDs:' ( eegd )+ )? ;
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
		ParserRuleReturnScope dc11 =null;
		ParserRuleReturnScope egd13 =null;
		ParserRuleReturnScope dedegd15 =null;
		ParserRuleReturnScope eegd17 =null;

		CommonTree string_literal2_tree=null;
		CommonTree string_literal4_tree=null;
		CommonTree string_literal6_tree=null;
		CommonTree string_literal8_tree=null;
		CommonTree string_literal10_tree=null;
		CommonTree string_literal12_tree=null;
		CommonTree string_literal14_tree=null;
		CommonTree string_literal16_tree=null;

		try {
			// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:55:13: ( ( 'STTGDs:' ( sttgd )+ | 'DED-STTGDs:' ( dedstgd )+ )? ( 'ExtTGDs:' ( etgd )+ | 'DED-ExtTGDs:' ( dedetgd )+ )? ( 'DCs:' ( dc )+ )? ( 'EGDs:' ( egd )+ | 'DED-EGDs:' ( dedegd )+ | 'ExtEGDs:' ( eegd )+ )? )
			// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:56:11: ( 'STTGDs:' ( sttgd )+ | 'DED-STTGDs:' ( dedstgd )+ )? ( 'ExtTGDs:' ( etgd )+ | 'DED-ExtTGDs:' ( dedetgd )+ )? ( 'DCs:' ( dc )+ )? ( 'EGDs:' ( egd )+ | 'DED-EGDs:' ( dedegd )+ | 'ExtEGDs:' ( eegd )+ )?
			{
			root_0 = (CommonTree)adaptor.nil();


			// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:56:11: ( 'STTGDs:' ( sttgd )+ | 'DED-STTGDs:' ( dedstgd )+ )?
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
					// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:56:12: 'STTGDs:' ( sttgd )+
					{
					string_literal2=(Token)match(input,28,FOLLOW_28_in_dependencies80); 
					string_literal2_tree = (CommonTree)adaptor.create(string_literal2);
					adaptor.addChild(root_0, string_literal2_tree);

					// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:56:22: ( sttgd )+
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
							// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:56:22: sttgd
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
					// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:57:12: 'DED-STTGDs:' ( dedstgd )+
					{
					string_literal4=(Token)match(input,24,FOLLOW_24_in_dependencies100); 
					string_literal4_tree = (CommonTree)adaptor.create(string_literal4);
					adaptor.addChild(root_0, string_literal4_tree);

					// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:57:26: ( dedstgd )+
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
							// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:57:26: dedstgd
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

			// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:58:11: ( 'ExtTGDs:' ( etgd )+ | 'DED-ExtTGDs:' ( dedetgd )+ )?
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
					// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:58:12: 'ExtTGDs:' ( etgd )+
					{
					string_literal6=(Token)match(input,27,FOLLOW_27_in_dependencies121); 
					string_literal6_tree = (CommonTree)adaptor.create(string_literal6);
					adaptor.addChild(root_0, string_literal6_tree);

					// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:58:23: ( etgd )+
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
							// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:58:23: etgd
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
					// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:59:12: 'DED-ExtTGDs:' ( dedetgd )+
					{
					string_literal8=(Token)match(input,23,FOLLOW_23_in_dependencies141); 
					string_literal8_tree = (CommonTree)adaptor.create(string_literal8);
					adaptor.addChild(root_0, string_literal8_tree);

					// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:59:27: ( dedetgd )+
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
							// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:59:27: dedetgd
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

			// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:60:11: ( 'DCs:' ( dc )+ )?
			int alt8=2;
			int LA8_0 = input.LA(1);
			if ( (LA8_0==21) ) {
				alt8=1;
			}
			switch (alt8) {
				case 1 :
					// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:60:12: 'DCs:' ( dc )+
					{
					string_literal10=(Token)match(input,21,FOLLOW_21_in_dependencies162); 
					string_literal10_tree = (CommonTree)adaptor.create(string_literal10);
					adaptor.addChild(root_0, string_literal10_tree);

					// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:60:19: ( dc )+
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
							// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:60:19: dc
							{
							pushFollow(FOLLOW_dc_in_dependencies164);
							dc11=dc();
							state._fsp--;

							adaptor.addChild(root_0, dc11.getTree());

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

			}

			// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:61:11: ( 'EGDs:' ( egd )+ | 'DED-EGDs:' ( dedegd )+ | 'ExtEGDs:' ( eegd )+ )?
			int alt12=4;
			switch ( input.LA(1) ) {
				case 25:
					{
					alt12=1;
					}
					break;
				case 22:
					{
					alt12=2;
					}
					break;
				case 26:
					{
					alt12=3;
					}
					break;
			}
			switch (alt12) {
				case 1 :
					// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:61:12: 'EGDs:' ( egd )+
					{
					string_literal12=(Token)match(input,25,FOLLOW_25_in_dependencies183); 
					string_literal12_tree = (CommonTree)adaptor.create(string_literal12);
					adaptor.addChild(root_0, string_literal12_tree);

					// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:61:20: ( egd )+
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
							// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:61:20: egd
							{
							pushFollow(FOLLOW_egd_in_dependencies185);
							egd13=egd();
							state._fsp--;

							adaptor.addChild(root_0, egd13.getTree());

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
				case 2 :
					// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:62:12: 'DED-EGDs:' ( dedegd )+
					{
					string_literal14=(Token)match(input,22,FOLLOW_22_in_dependencies203); 
					string_literal14_tree = (CommonTree)adaptor.create(string_literal14);
					adaptor.addChild(root_0, string_literal14_tree);

					// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:62:24: ( dedegd )+
					int cnt10=0;
					loop10:
					while (true) {
						int alt10=2;
						int LA10_0 = input.LA(1);
						if ( (LA10_0==IDENTIFIER) ) {
							alt10=1;
						}

						switch (alt10) {
						case 1 :
							// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:62:24: dedegd
							{
							pushFollow(FOLLOW_dedegd_in_dependencies205);
							dedegd15=dedegd();
							state._fsp--;

							adaptor.addChild(root_0, dedegd15.getTree());

							}
							break;

						default :
							if ( cnt10 >= 1 ) break loop10;
							EarlyExitException eee = new EarlyExitException(10, input);
							throw eee;
						}
						cnt10++;
					}

					counter = 0;
					}
					break;
				case 3 :
					// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:63:12: 'ExtEGDs:' ( eegd )+
					{
					string_literal16=(Token)match(input,26,FOLLOW_26_in_dependencies224); 
					string_literal16_tree = (CommonTree)adaptor.create(string_literal16);
					adaptor.addChild(root_0, string_literal16_tree);

					// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:63:23: ( eegd )+
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
							// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:63:23: eegd
							{
							pushFollow(FOLLOW_eegd_in_dependencies226);
							eegd17=eegd();
							state._fsp--;

							adaptor.addChild(root_0, eegd17.getTree());

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

			 generator.processDependencies(); 
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
	// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:66:1: sttgd : dependency ;
	public final DependenciesParser.sttgd_return sttgd() throws RecognitionException {
		DependenciesParser.sttgd_return retval = new DependenciesParser.sttgd_return();
		retval.start = input.LT(1);

		CommonTree root_0 = null;

		ParserRuleReturnScope dependency18 =null;


		try {
			// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:66:6: ( dependency )
			// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:66:11: dependency
			{
			root_0 = (CommonTree)adaptor.nil();


			pushFollow(FOLLOW_dependency_in_sttgd247);
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
	// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:68:1: etgd : dependency ;
	public final DependenciesParser.etgd_return etgd() throws RecognitionException {
		DependenciesParser.etgd_return retval = new DependenciesParser.etgd_return();
		retval.start = input.LT(1);

		CommonTree root_0 = null;

		ParserRuleReturnScope dependency19 =null;


		try {
			// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:68:5: ( dependency )
			// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:68:10: dependency
			{
			root_0 = (CommonTree)adaptor.nil();


			pushFollow(FOLLOW_dependency_in_etgd260);
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
	// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:70:1: dc : dependency ;
	public final DependenciesParser.dc_return dc() throws RecognitionException {
		DependenciesParser.dc_return retval = new DependenciesParser.dc_return();
		retval.start = input.LT(1);

		CommonTree root_0 = null;

		ParserRuleReturnScope dependency20 =null;


		try {
			// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:70:3: ( dependency )
			// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:70:8: dependency
			{
			root_0 = (CommonTree)adaptor.nil();


			pushFollow(FOLLOW_dependency_in_dc273);
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
	// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:72:1: egd : dependency ;
	public final DependenciesParser.egd_return egd() throws RecognitionException {
		DependenciesParser.egd_return retval = new DependenciesParser.egd_return();
		retval.start = input.LT(1);

		CommonTree root_0 = null;

		ParserRuleReturnScope dependency21 =null;


		try {
			// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:72:4: ( dependency )
			// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:72:9: dependency
			{
			root_0 = (CommonTree)adaptor.nil();


			pushFollow(FOLLOW_dependency_in_egd286);
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
	// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:74:1: eegd : dependency ;
	public final DependenciesParser.eegd_return eegd() throws RecognitionException {
		DependenciesParser.eegd_return retval = new DependenciesParser.eegd_return();
		retval.start = input.LT(1);

		CommonTree root_0 = null;

		ParserRuleReturnScope dependency22 =null;


		try {
			// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:74:5: ( dependency )
			// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:74:10: dependency
			{
			root_0 = (CommonTree)adaptor.nil();


			pushFollow(FOLLOW_dependency_in_eegd305);
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
	// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:76:1: dedstgd : ded ;
	public final DependenciesParser.dedstgd_return dedstgd() throws RecognitionException {
		DependenciesParser.dedstgd_return retval = new DependenciesParser.dedstgd_return();
		retval.start = input.LT(1);

		CommonTree root_0 = null;

		ParserRuleReturnScope ded23 =null;


		try {
			// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:76:8: ( ded )
			// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:76:11: ded
			{
			root_0 = (CommonTree)adaptor.nil();


			pushFollow(FOLLOW_ded_in_dedstgd322);
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
	// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:78:1: dedetgd : ded ;
	public final DependenciesParser.dedetgd_return dedetgd() throws RecognitionException {
		DependenciesParser.dedetgd_return retval = new DependenciesParser.dedetgd_return();
		retval.start = input.LT(1);

		CommonTree root_0 = null;

		ParserRuleReturnScope ded24 =null;


		try {
			// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:78:8: ( ded )
			// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:78:11: ded
			{
			root_0 = (CommonTree)adaptor.nil();


			pushFollow(FOLLOW_ded_in_dedetgd345);
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
	// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:80:1: dedegd : ded ;
	public final DependenciesParser.dedegd_return dedegd() throws RecognitionException {
		DependenciesParser.dedegd_return retval = new DependenciesParser.dedegd_return();
		retval.start = input.LT(1);

		CommonTree root_0 = null;

		ParserRuleReturnScope ded25 =null;


		try {
			// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:80:7: ( ded )
			// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:80:18: ded
			{
			root_0 = (CommonTree)adaptor.nil();


			pushFollow(FOLLOW_ded_in_dedegd370);
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
	// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:82:1: dependency : (id= IDENTIFIER ':' )? positiveFormula ( negatedFormula )* '->' ( '#fail' | conclusionFormula ) '.' ;
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
			// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:82:11: ( (id= IDENTIFIER ':' )? positiveFormula ( negatedFormula )* '->' ( '#fail' | conclusionFormula ) '.' )
			// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:82:14: (id= IDENTIFIER ':' )? positiveFormula ( negatedFormula )* '->' ( '#fail' | conclusionFormula ) '.'
			{
			root_0 = (CommonTree)adaptor.nil();


			// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:82:14: (id= IDENTIFIER ':' )?
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
					// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:82:15: id= IDENTIFIER ':'
					{
					id=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_dependency392); 
					id_tree = (CommonTree)adaptor.create(id);
					adaptor.addChild(root_0, id_tree);

					char_literal26=(Token)match(input,20,FOLLOW_20_in_dependency393); 
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
			pushFollow(FOLLOW_positiveFormula_in_dependency402);
			positiveFormula27=positiveFormula();
			state._fsp--;

			adaptor.addChild(root_0, positiveFormula27.getTree());

			// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:87:21: ( negatedFormula )*
			loop14:
			while (true) {
				int alt14=2;
				int LA14_0 = input.LA(1);
				if ( (LA14_0==32) ) {
					alt14=1;
				}

				switch (alt14) {
				case 1 :
					// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:87:23: negatedFormula
					{
					pushFollow(FOLLOW_negatedFormula_in_dependency407);
					negatedFormula28=negatedFormula();
					state._fsp--;

					adaptor.addChild(root_0, negatedFormula28.getTree());

					}
					break;

				default :
					break loop14;
				}
			}

			string_literal29=(Token)match(input,18,FOLLOW_18_in_dependency414); 
			string_literal29_tree = (CommonTree)adaptor.create(string_literal29);
			adaptor.addChild(root_0, string_literal29_tree);

			// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:88:4: ( '#fail' | conclusionFormula )
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
					// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:88:5: '#fail'
					{
					string_literal30=(Token)match(input,14,FOLLOW_14_in_dependency421); 
					string_literal30_tree = (CommonTree)adaptor.create(string_literal30);
					adaptor.addChild(root_0, string_literal30_tree);

					  formulaStack.clear(); 
					                    dependency.setConclusion(NullFormula.getInstance());
					}
					break;
				case 2 :
					// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:92:4: conclusionFormula
					{
					  formulaStack.clear(); 
					pushFollow(FOLLOW_conclusionFormula_in_dependency458);
					conclusionFormula31=conclusionFormula();
					state._fsp--;

					adaptor.addChild(root_0, conclusionFormula31.getTree());

					}
					break;

			}

			char_literal32=(Token)match(input,19,FOLLOW_19_in_dependency461); 
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
	// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:95:1: ded : (id= IDENTIFIER ':' )? positiveFormula ( negatedFormula )* '->' dedConclusion ( '|' dedConclusion )* '.' ;
	public final DependenciesParser.ded_return ded() throws RecognitionException {
		DependenciesParser.ded_return retval = new DependenciesParser.ded_return();
		retval.start = input.LT(1);

		CommonTree root_0 = null;

		Token id=null;
		Token char_literal33=null;
		Token string_literal36=null;
		Token char_literal38=null;
		Token char_literal40=null;
		ParserRuleReturnScope positiveFormula34 =null;
		ParserRuleReturnScope negatedFormula35 =null;
		ParserRuleReturnScope dedConclusion37 =null;
		ParserRuleReturnScope dedConclusion39 =null;

		CommonTree id_tree=null;
		CommonTree char_literal33_tree=null;
		CommonTree string_literal36_tree=null;
		CommonTree char_literal38_tree=null;
		CommonTree char_literal40_tree=null;

		try {
			// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:95:4: ( (id= IDENTIFIER ':' )? positiveFormula ( negatedFormula )* '->' dedConclusion ( '|' dedConclusion )* '.' )
			// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:95:15: (id= IDENTIFIER ':' )? positiveFormula ( negatedFormula )* '->' dedConclusion ( '|' dedConclusion )* '.'
			{
			root_0 = (CommonTree)adaptor.nil();


			// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:95:15: (id= IDENTIFIER ':' )?
			int alt16=2;
			int LA16_0 = input.LA(1);
			if ( (LA16_0==IDENTIFIER) ) {
				int LA16_1 = input.LA(2);
				if ( (LA16_1==20) ) {
					alt16=1;
				}
			}
			switch (alt16) {
				case 1 :
					// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:95:16: id= IDENTIFIER ':'
					{
					id=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_ded489); 
					id_tree = (CommonTree)adaptor.create(id);
					adaptor.addChild(root_0, id_tree);

					char_literal33=(Token)match(input,20,FOLLOW_20_in_ded490); 
					char_literal33_tree = (CommonTree)adaptor.create(char_literal33);
					adaptor.addChild(root_0, char_literal33_tree);

					}
					break;

			}

			  ded = new DED(); 
			                    formulaWN = new FormulaWithNegations(); 
			                    formulaStack.push(formulaWN);
			                    dedPremise = formulaWN;
			                    if(id!=null) dependency.setId(id.getText()); 
			pushFollow(FOLLOW_positiveFormula_in_ded499);
			positiveFormula34=positiveFormula();
			state._fsp--;

			adaptor.addChild(root_0, positiveFormula34.getTree());

			// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:100:21: ( negatedFormula )*
			loop17:
			while (true) {
				int alt17=2;
				int LA17_0 = input.LA(1);
				if ( (LA17_0==32) ) {
					alt17=1;
				}

				switch (alt17) {
				case 1 :
					// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:100:23: negatedFormula
					{
					pushFollow(FOLLOW_negatedFormula_in_ded504);
					negatedFormula35=negatedFormula();
					state._fsp--;

					adaptor.addChild(root_0, negatedFormula35.getTree());

					}
					break;

				default :
					break loop17;
				}
			}

			string_literal36=(Token)match(input,18,FOLLOW_18_in_ded511); 
			string_literal36_tree = (CommonTree)adaptor.create(string_literal36);
			adaptor.addChild(root_0, string_literal36_tree);

			pushFollow(FOLLOW_dedConclusion_in_ded517);
			dedConclusion37=dedConclusion();
			state._fsp--;

			adaptor.addChild(root_0, dedConclusion37.getTree());

			// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:101:18: ( '|' dedConclusion )*
			loop18:
			while (true) {
				int alt18=2;
				int LA18_0 = input.LA(1);
				if ( (LA18_0==33) ) {
					alt18=1;
				}

				switch (alt18) {
				case 1 :
					// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:101:19: '|' dedConclusion
					{
					char_literal38=(Token)match(input,33,FOLLOW_33_in_ded520); 
					char_literal38_tree = (CommonTree)adaptor.create(char_literal38);
					adaptor.addChild(root_0, char_literal38_tree);

					pushFollow(FOLLOW_dedConclusion_in_ded522);
					dedConclusion39=dedConclusion();
					state._fsp--;

					adaptor.addChild(root_0, dedConclusion39.getTree());

					}
					break;

				default :
					break loop18;
				}
			}

			char_literal40=(Token)match(input,19,FOLLOW_19_in_ded526); 
			char_literal40_tree = (CommonTree)adaptor.create(char_literal40);
			adaptor.addChild(root_0, char_literal40_tree);

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

		Token char_literal41=null;
		Token char_literal43=null;
		Token char_literal45=null;
		ParserRuleReturnScope atom42 =null;
		ParserRuleReturnScope atom44 =null;

		CommonTree char_literal41_tree=null;
		CommonTree char_literal43_tree=null;
		CommonTree char_literal45_tree=null;

		try {
			// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:103:14: ( '[' atom ( ',' atom )* ']' )
			// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:103:19: '[' atom ( ',' atom )* ']'
			{
			root_0 = (CommonTree)adaptor.nil();


			char_literal41=(Token)match(input,29,FOLLOW_29_in_dedConclusion536); 
			char_literal41_tree = (CommonTree)adaptor.create(char_literal41);
			adaptor.addChild(root_0, char_literal41_tree);

			 formulaStack.clear(); 
			                        dependency = new Dependency();
			                        ded.addAssociatedDependency(dependency);                        
			                        dependency.setPremise(dedPremise.clone());
						positiveFormula = new PositiveFormula(); 
			                        dependency.setConclusion(positiveFormula); 
			pushFollow(FOLLOW_atom_in_dedConclusion558);
			atom42=atom();
			state._fsp--;

			adaptor.addChild(root_0, atom42.getTree());

			// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:109:24: ( ',' atom )*
			loop19:
			while (true) {
				int alt19=2;
				int LA19_0 = input.LA(1);
				if ( (LA19_0==17) ) {
					alt19=1;
				}

				switch (alt19) {
				case 1 :
					// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:109:25: ',' atom
					{
					char_literal43=(Token)match(input,17,FOLLOW_17_in_dedConclusion561); 
					char_literal43_tree = (CommonTree)adaptor.create(char_literal43);
					adaptor.addChild(root_0, char_literal43_tree);

					pushFollow(FOLLOW_atom_in_dedConclusion563);
					atom44=atom();
					state._fsp--;

					adaptor.addChild(root_0, atom44.getTree());

					}
					break;

				default :
					break loop19;
				}
			}

			char_literal45=(Token)match(input,31,FOLLOW_31_in_dedConclusion568); 
			char_literal45_tree = (CommonTree)adaptor.create(char_literal45);
			adaptor.addChild(root_0, char_literal45_tree);

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

		Token char_literal47=null;
		ParserRuleReturnScope relationalAtom46 =null;
		ParserRuleReturnScope atom48 =null;

		CommonTree char_literal47_tree=null;

		try {
			// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:111:16: ( relationalAtom ( ',' atom )* )
			// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:111:18: relationalAtom ( ',' atom )*
			{
			root_0 = (CommonTree)adaptor.nil();


			  positiveFormula = new PositiveFormula(); 
			                    positiveFormula.setFather(formulaStack.peek()); 
			                    formulaStack.peek().setPositiveFormula(positiveFormula); 
			pushFollow(FOLLOW_relationalAtom_in_positiveFormula622);
			relationalAtom46=relationalAtom();
			state._fsp--;

			adaptor.addChild(root_0, relationalAtom46.getTree());

			// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:114:34: ( ',' atom )*
			loop20:
			while (true) {
				int alt20=2;
				int LA20_0 = input.LA(1);
				if ( (LA20_0==17) ) {
					alt20=1;
				}

				switch (alt20) {
				case 1 :
					// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:114:35: ',' atom
					{
					char_literal47=(Token)match(input,17,FOLLOW_17_in_positiveFormula625); 
					char_literal47_tree = (CommonTree)adaptor.create(char_literal47);
					adaptor.addChild(root_0, char_literal47_tree);

					pushFollow(FOLLOW_atom_in_positiveFormula627);
					atom48=atom();
					state._fsp--;

					adaptor.addChild(root_0, atom48.getTree());

					}
					break;

				default :
					break loop20;
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

		Token string_literal49=null;
		Token char_literal50=null;
		Token char_literal53=null;
		ParserRuleReturnScope positiveFormula51 =null;
		ParserRuleReturnScope negatedFormula52 =null;

		CommonTree string_literal49_tree=null;
		CommonTree char_literal50_tree=null;
		CommonTree char_literal53_tree=null;

		try {
			// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:116:15: ( 'and not exists' '(' ( positiveFormula ( negatedFormula )* ) ')' )
			// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:116:18: 'and not exists' '(' ( positiveFormula ( negatedFormula )* ) ')'
			{
			root_0 = (CommonTree)adaptor.nil();


			  formulaWN = new FormulaWithNegations(); 
					    formulaWN.setFather(formulaStack.peek());
					    formulaStack.peek().addNegatedFormula(formulaWN);
			                    formulaStack.push(formulaWN); 
			string_literal49=(Token)match(input,32,FOLLOW_32_in_negatedFormula658); 
			string_literal49_tree = (CommonTree)adaptor.create(string_literal49);
			adaptor.addChild(root_0, string_literal49_tree);

			char_literal50=(Token)match(input,15,FOLLOW_15_in_negatedFormula659); 
			char_literal50_tree = (CommonTree)adaptor.create(char_literal50);
			adaptor.addChild(root_0, char_literal50_tree);

			// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:120:38: ( positiveFormula ( negatedFormula )* )
			// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:120:40: positiveFormula ( negatedFormula )*
			{
			pushFollow(FOLLOW_positiveFormula_in_negatedFormula663);
			positiveFormula51=positiveFormula();
			state._fsp--;

			adaptor.addChild(root_0, positiveFormula51.getTree());

			// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:120:56: ( negatedFormula )*
			loop21:
			while (true) {
				int alt21=2;
				int LA21_0 = input.LA(1);
				if ( (LA21_0==32) ) {
					alt21=1;
				}

				switch (alt21) {
				case 1 :
					// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:120:58: negatedFormula
					{
					pushFollow(FOLLOW_negatedFormula_in_negatedFormula667);
					negatedFormula52=negatedFormula();
					state._fsp--;

					adaptor.addChild(root_0, negatedFormula52.getTree());

					}
					break;

				default :
					break loop21;
				}
			}

			}

			char_literal53=(Token)match(input,16,FOLLOW_16_in_negatedFormula674); 
			char_literal53_tree = (CommonTree)adaptor.create(char_literal53);
			adaptor.addChild(root_0, char_literal53_tree);

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

		Token char_literal55=null;
		ParserRuleReturnScope atom54 =null;
		ParserRuleReturnScope atom56 =null;

		CommonTree char_literal55_tree=null;

		try {
			// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:123:18: ( atom ( ',' atom )* )
			// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:123:20: atom ( ',' atom )*
			{
			root_0 = (CommonTree)adaptor.nil();


			  positiveFormula = new PositiveFormula(); 
			                      dependency.setConclusion(positiveFormula); 
			pushFollow(FOLLOW_atom_in_conclusionFormula720);
			atom54=atom();
			state._fsp--;

			adaptor.addChild(root_0, atom54.getTree());

			// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:125:24: ( ',' atom )*
			loop22:
			while (true) {
				int alt22=2;
				int LA22_0 = input.LA(1);
				if ( (LA22_0==17) ) {
					alt22=1;
				}

				switch (alt22) {
				case 1 :
					// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:125:25: ',' atom
					{
					char_literal55=(Token)match(input,17,FOLLOW_17_in_conclusionFormula723); 
					char_literal55_tree = (CommonTree)adaptor.create(char_literal55);
					adaptor.addChild(root_0, char_literal55_tree);

					pushFollow(FOLLOW_atom_in_conclusionFormula725);
					atom56=atom();
					state._fsp--;

					adaptor.addChild(root_0, atom56.getTree());

					}
					break;

				default :
					break loop22;
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

		ParserRuleReturnScope relationalAtom57 =null;
		ParserRuleReturnScope builtin58 =null;
		ParserRuleReturnScope comparison59 =null;


		try {
			// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:127:6: ( relationalAtom | builtin | comparison )
			int alt23=3;
			switch ( input.LA(1) ) {
			case IDENTIFIER:
				{
				alt23=1;
				}
				break;
			case EXPRESSION:
				{
				alt23=2;
				}
				break;
			case NUMBER:
			case STRING:
			case 30:
				{
				alt23=3;
				}
				break;
			default:
				NoViableAltException nvae =
					new NoViableAltException("", 23, 0, input);
				throw nvae;
			}
			switch (alt23) {
				case 1 :
					// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:127:9: relationalAtom
					{
					root_0 = (CommonTree)adaptor.nil();


					pushFollow(FOLLOW_relationalAtom_in_atom738);
					relationalAtom57=relationalAtom();
					state._fsp--;

					adaptor.addChild(root_0, relationalAtom57.getTree());

					}
					break;
				case 2 :
					// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:127:26: builtin
					{
					root_0 = (CommonTree)adaptor.nil();


					pushFollow(FOLLOW_builtin_in_atom742);
					builtin58=builtin();
					state._fsp--;

					adaptor.addChild(root_0, builtin58.getTree());

					}
					break;
				case 3 :
					// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:127:36: comparison
					{
					root_0 = (CommonTree)adaptor.nil();


					pushFollow(FOLLOW_comparison_in_atom746);
					comparison59=comparison();
					state._fsp--;

					adaptor.addChild(root_0, comparison59.getTree());

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
		Token char_literal60=null;
		Token char_literal62=null;
		Token char_literal64=null;
		ParserRuleReturnScope attribute61 =null;
		ParserRuleReturnScope attribute63 =null;

		CommonTree name_tree=null;
		CommonTree char_literal60_tree=null;
		CommonTree char_literal62_tree=null;
		CommonTree char_literal64_tree=null;

		try {
			// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:129:15: (name= IDENTIFIER '(' attribute ( ',' attribute )* ')' )
			// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:129:18: name= IDENTIFIER '(' attribute ( ',' attribute )* ')'
			{
			root_0 = (CommonTree)adaptor.nil();


			name=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_relationalAtom757); 
			name_tree = (CommonTree)adaptor.create(name);
			adaptor.addChild(root_0, name_tree);

			 atom = new RelationalAtom(name.getText()); 
			char_literal60=(Token)match(input,15,FOLLOW_15_in_relationalAtom761); 
			char_literal60_tree = (CommonTree)adaptor.create(char_literal60);
			adaptor.addChild(root_0, char_literal60_tree);

			pushFollow(FOLLOW_attribute_in_relationalAtom763);
			attribute61=attribute();
			state._fsp--;

			adaptor.addChild(root_0, attribute61.getTree());

			// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:129:95: ( ',' attribute )*
			loop24:
			while (true) {
				int alt24=2;
				int LA24_0 = input.LA(1);
				if ( (LA24_0==17) ) {
					alt24=1;
				}

				switch (alt24) {
				case 1 :
					// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:129:96: ',' attribute
					{
					char_literal62=(Token)match(input,17,FOLLOW_17_in_relationalAtom766); 
					char_literal62_tree = (CommonTree)adaptor.create(char_literal62);
					adaptor.addChild(root_0, char_literal62_tree);

					pushFollow(FOLLOW_attribute_in_relationalAtom768);
					attribute63=attribute();
					state._fsp--;

					adaptor.addChild(root_0, attribute63.getTree());

					}
					break;

				default :
					break loop24;
				}
			}

			char_literal64=(Token)match(input,16,FOLLOW_16_in_relationalAtom772); 
			char_literal64_tree = (CommonTree)adaptor.create(char_literal64);
			adaptor.addChild(root_0, char_literal64_tree);

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


			expression=(Token)match(input,EXPRESSION,FOLLOW_EXPRESSION_in_builtin788); 
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
	// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:136:1: comparison : argument oper= OPERATOR argument ;
	public final DependenciesParser.comparison_return comparison() throws RecognitionException {
		DependenciesParser.comparison_return retval = new DependenciesParser.comparison_return();
		retval.start = input.LT(1);

		CommonTree root_0 = null;

		Token oper=null;
		ParserRuleReturnScope argument65 =null;
		ParserRuleReturnScope argument66 =null;

		CommonTree oper_tree=null;

		try {
			// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:136:12: ( argument oper= OPERATOR argument )
			// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:136:15: argument oper= OPERATOR argument
			{
			root_0 = (CommonTree)adaptor.nil();


			   expressionString = new StringBuilder(); 
			pushFollow(FOLLOW_argument_in_comparison847);
			argument65=argument();
			state._fsp--;

			adaptor.addChild(root_0, argument65.getTree());

			oper=(Token)match(input,OPERATOR,FOLLOW_OPERATOR_in_comparison869); 
			oper_tree = (CommonTree)adaptor.create(oper);
			adaptor.addChild(root_0, oper_tree);

			 expressionString.append(" ").append(oper.getText()); 
			pushFollow(FOLLOW_argument_in_comparison890);
			argument66=argument();
			state._fsp--;

			adaptor.addChild(root_0, argument66.getTree());

			  Expression expression = new Expression(expressionString.toString()); 
			                    atom = new ComparisonAtom(positiveFormula, expression, oper.getText()); 
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


	public static class argument_return extends ParserRuleReturnScope {
		CommonTree tree;
		@Override
		public CommonTree getTree() { return tree; }
	};


	// $ANTLR start "argument"
	// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:144:1: argument : ( '\\$' var= IDENTIFIER |constant= ( STRING | NUMBER ) ) ;
	public final DependenciesParser.argument_return argument() throws RecognitionException {
		DependenciesParser.argument_return retval = new DependenciesParser.argument_return();
		retval.start = input.LT(1);

		CommonTree root_0 = null;

		Token var=null;
		Token constant=null;
		Token char_literal67=null;

		CommonTree var_tree=null;
		CommonTree constant_tree=null;
		CommonTree char_literal67_tree=null;

		try {
			// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:144:9: ( ( '\\$' var= IDENTIFIER |constant= ( STRING | NUMBER ) ) )
			// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:144:12: ( '\\$' var= IDENTIFIER |constant= ( STRING | NUMBER ) )
			{
			root_0 = (CommonTree)adaptor.nil();


			// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:144:12: ( '\\$' var= IDENTIFIER |constant= ( STRING | NUMBER ) )
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
					// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:144:13: '\\$' var= IDENTIFIER
					{
					char_literal67=(Token)match(input,30,FOLLOW_30_in_argument920); 
					char_literal67_tree = (CommonTree)adaptor.create(char_literal67);
					adaptor.addChild(root_0, char_literal67_tree);

					var=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_argument923); 
					var_tree = (CommonTree)adaptor.create(var);
					adaptor.addChild(root_0, var_tree);

					 expressionString.append(var.getText()); 
					}
					break;
				case 2 :
					// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:145:18: constant= ( STRING | NUMBER )
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
					 expressionString.append(constant.getText()); 
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
	// $ANTLR end "argument"


	public static class attribute_return extends ParserRuleReturnScope {
		CommonTree tree;
		@Override
		public CommonTree getTree() { return tree; }
	};


	// $ANTLR start "attribute"
	// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:148:1: attribute : attr= IDENTIFIER ':' value ;
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
			// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:148:10: (attr= IDENTIFIER ':' value )
			// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:148:13: attr= IDENTIFIER ':' value
			{
			root_0 = (CommonTree)adaptor.nil();


			attr=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_attribute985); 
			attr_tree = (CommonTree)adaptor.create(attr);
			adaptor.addChild(root_0, attr_tree);

			char_literal68=(Token)match(input,20,FOLLOW_20_in_attribute987); 
			char_literal68_tree = (CommonTree)adaptor.create(char_literal68);
			adaptor.addChild(root_0, char_literal68_tree);

			 attribute = new FormulaAttribute(attr.getText()); 
			pushFollow(FOLLOW_value_in_attribute991);
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
	// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:151:1: value : ( '\\$' var= IDENTIFIER |constant= ( STRING | NUMBER ) |nullValue= NULL |expression= EXPRESSION );
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
			// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:151:7: ( '\\$' var= IDENTIFIER |constant= ( STRING | NUMBER ) |nullValue= NULL |expression= EXPRESSION )
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
					// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:151:10: '\\$' var= IDENTIFIER
					{
					root_0 = (CommonTree)adaptor.nil();


					char_literal70=(Token)match(input,30,FOLLOW_30_in_value1006); 
					char_literal70_tree = (CommonTree)adaptor.create(char_literal70);
					adaptor.addChild(root_0, char_literal70_tree);

					var=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_value1009); 
					var_tree = (CommonTree)adaptor.create(var);
					adaptor.addChild(root_0, var_tree);

					 attribute.setValue(new FormulaVariableOccurrence(new AttributeRef(((RelationalAtom)atom).getTableName(), attribute.getAttributeName()), var.getText())); 
					}
					break;
				case 2 :
					// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:152:18: constant= ( STRING | NUMBER )
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
					// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:153:18: nullValue= NULL
					{
					root_0 = (CommonTree)adaptor.nil();


					nullValue=(Token)match(input,NULL,FOLLOW_NULL_in_value1065); 
					nullValue_tree = (CommonTree)adaptor.create(nullValue);
					adaptor.addChild(root_0, nullValue_tree);

					 attribute.setValue(new FormulaConstant(nullValue.getText(), true)); 
					}
					break;
				case 4 :
					// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/Dependencies.g:154:18: expression= EXPRESSION
					{
					root_0 = (CommonTree)adaptor.nil();


					expression=(Token)match(input,EXPRESSION,FOLLOW_EXPRESSION_in_value1090); 
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
	public static final BitSet FOLLOW_21_in_dependencies162 = new BitSet(new long[]{0x0000000000000040L});
	public static final BitSet FOLLOW_dc_in_dependencies164 = new BitSet(new long[]{0x0000000006400042L});
	public static final BitSet FOLLOW_25_in_dependencies183 = new BitSet(new long[]{0x0000000000000040L});
	public static final BitSet FOLLOW_egd_in_dependencies185 = new BitSet(new long[]{0x0000000000000042L});
	public static final BitSet FOLLOW_22_in_dependencies203 = new BitSet(new long[]{0x0000000000000040L});
	public static final BitSet FOLLOW_dedegd_in_dependencies205 = new BitSet(new long[]{0x0000000000000042L});
	public static final BitSet FOLLOW_26_in_dependencies224 = new BitSet(new long[]{0x0000000000000040L});
	public static final BitSet FOLLOW_eegd_in_dependencies226 = new BitSet(new long[]{0x0000000000000042L});
	public static final BitSet FOLLOW_dependency_in_sttgd247 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_dependency_in_etgd260 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_dependency_in_dc273 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_dependency_in_egd286 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_dependency_in_eegd305 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ded_in_dedstgd322 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ded_in_dedetgd345 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ded_in_dedegd370 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_IDENTIFIER_in_dependency392 = new BitSet(new long[]{0x0000000000100000L});
	public static final BitSet FOLLOW_20_in_dependency393 = new BitSet(new long[]{0x0000000000000040L});
	public static final BitSet FOLLOW_positiveFormula_in_dependency402 = new BitSet(new long[]{0x0000000100040000L});
	public static final BitSet FOLLOW_negatedFormula_in_dependency407 = new BitSet(new long[]{0x0000000100040000L});
	public static final BitSet FOLLOW_18_in_dependency414 = new BitSet(new long[]{0x0000000040005460L});
	public static final BitSet FOLLOW_14_in_dependency421 = new BitSet(new long[]{0x0000000000080000L});
	public static final BitSet FOLLOW_conclusionFormula_in_dependency458 = new BitSet(new long[]{0x0000000000080000L});
	public static final BitSet FOLLOW_19_in_dependency461 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_IDENTIFIER_in_ded489 = new BitSet(new long[]{0x0000000000100000L});
	public static final BitSet FOLLOW_20_in_ded490 = new BitSet(new long[]{0x0000000000000040L});
	public static final BitSet FOLLOW_positiveFormula_in_ded499 = new BitSet(new long[]{0x0000000100040000L});
	public static final BitSet FOLLOW_negatedFormula_in_ded504 = new BitSet(new long[]{0x0000000100040000L});
	public static final BitSet FOLLOW_18_in_ded511 = new BitSet(new long[]{0x0000000020000000L});
	public static final BitSet FOLLOW_dedConclusion_in_ded517 = new BitSet(new long[]{0x0000000200080000L});
	public static final BitSet FOLLOW_33_in_ded520 = new BitSet(new long[]{0x0000000020000000L});
	public static final BitSet FOLLOW_dedConclusion_in_ded522 = new BitSet(new long[]{0x0000000200080000L});
	public static final BitSet FOLLOW_19_in_ded526 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_29_in_dedConclusion536 = new BitSet(new long[]{0x0000000040001460L});
	public static final BitSet FOLLOW_atom_in_dedConclusion558 = new BitSet(new long[]{0x0000000080020000L});
	public static final BitSet FOLLOW_17_in_dedConclusion561 = new BitSet(new long[]{0x0000000040001460L});
	public static final BitSet FOLLOW_atom_in_dedConclusion563 = new BitSet(new long[]{0x0000000080020000L});
	public static final BitSet FOLLOW_31_in_dedConclusion568 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_relationalAtom_in_positiveFormula622 = new BitSet(new long[]{0x0000000000020002L});
	public static final BitSet FOLLOW_17_in_positiveFormula625 = new BitSet(new long[]{0x0000000040001460L});
	public static final BitSet FOLLOW_atom_in_positiveFormula627 = new BitSet(new long[]{0x0000000000020002L});
	public static final BitSet FOLLOW_32_in_negatedFormula658 = new BitSet(new long[]{0x0000000000008000L});
	public static final BitSet FOLLOW_15_in_negatedFormula659 = new BitSet(new long[]{0x0000000000000040L});
	public static final BitSet FOLLOW_positiveFormula_in_negatedFormula663 = new BitSet(new long[]{0x0000000100010000L});
	public static final BitSet FOLLOW_negatedFormula_in_negatedFormula667 = new BitSet(new long[]{0x0000000100010000L});
	public static final BitSet FOLLOW_16_in_negatedFormula674 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_atom_in_conclusionFormula720 = new BitSet(new long[]{0x0000000000020002L});
	public static final BitSet FOLLOW_17_in_conclusionFormula723 = new BitSet(new long[]{0x0000000040001460L});
	public static final BitSet FOLLOW_atom_in_conclusionFormula725 = new BitSet(new long[]{0x0000000000020002L});
	public static final BitSet FOLLOW_relationalAtom_in_atom738 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_builtin_in_atom742 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_comparison_in_atom746 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_IDENTIFIER_in_relationalAtom757 = new BitSet(new long[]{0x0000000000008000L});
	public static final BitSet FOLLOW_15_in_relationalAtom761 = new BitSet(new long[]{0x0000000000000040L});
	public static final BitSet FOLLOW_attribute_in_relationalAtom763 = new BitSet(new long[]{0x0000000000030000L});
	public static final BitSet FOLLOW_17_in_relationalAtom766 = new BitSet(new long[]{0x0000000000000040L});
	public static final BitSet FOLLOW_attribute_in_relationalAtom768 = new BitSet(new long[]{0x0000000000030000L});
	public static final BitSet FOLLOW_16_in_relationalAtom772 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_EXPRESSION_in_builtin788 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_argument_in_comparison847 = new BitSet(new long[]{0x0000000000000800L});
	public static final BitSet FOLLOW_OPERATOR_in_comparison869 = new BitSet(new long[]{0x0000000040001400L});
	public static final BitSet FOLLOW_argument_in_comparison890 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_30_in_argument920 = new BitSet(new long[]{0x0000000000000040L});
	public static final BitSet FOLLOW_IDENTIFIER_in_argument923 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_set_in_argument948 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_IDENTIFIER_in_attribute985 = new BitSet(new long[]{0x0000000000100000L});
	public static final BitSet FOLLOW_20_in_attribute987 = new BitSet(new long[]{0x0000000040001620L});
	public static final BitSet FOLLOW_value_in_attribute991 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_30_in_value1006 = new BitSet(new long[]{0x0000000000000040L});
	public static final BitSet FOLLOW_IDENTIFIER_in_value1009 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_set_in_value1034 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_NULL_in_value1065 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_EXPRESSION_in_value1090 = new BitSet(new long[]{0x0000000000000002L});
}
