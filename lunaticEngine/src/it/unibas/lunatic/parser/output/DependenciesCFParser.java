// $ANTLR 3.5.1 /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/DependenciesCF.g 2016-05-11 13:13:04

package it.unibas.lunatic.parser.output;

import it.unibas.lunatic.LunaticConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import it.unibas.lunatic.parser.operators.ParseDependenciesCF;
import it.unibas.lunatic.model.dependency.*;
import speedy.model.database.AttributeRef;
import speedy.model.expressions.Expression;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

import org.antlr.runtime.tree.*;


@SuppressWarnings("all")
public class DependenciesCFParser extends Parser {
	public static final String[] tokenNames = new String[] {
		"<invalid>", "<EOR>", "<DOWN>", "<UP>", "DIGIT", "EXPRESSION", "IDENTIFIER", 
		"LETTER", "LINE_COMMENT", "NULL", "NUMBER", "OPERATOR", "STRING", "WHITESPACE", 
		"'('", "')'", "','", "'->'", "'.'", "':'", "'<-'", "'EGDs:'", "'Queries:'", 
		"'ST-TGDs:'", "'T-TGDs:'", "'\\?'"
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


	public DependenciesCFParser(TokenStream input) {
		this(input, new RecognizerSharedState());
	}
	public DependenciesCFParser(TokenStream input, RecognizerSharedState state) {
		super(input, state);
	}

	protected TreeAdaptor adaptor = new CommonTreeAdaptor();

	public void setTreeAdaptor(TreeAdaptor adaptor) {
		this.adaptor = adaptor;
	}
	public TreeAdaptor getTreeAdaptor() {
		return adaptor;
	}
	@Override public String[] getTokenNames() { return DependenciesCFParser.tokenNames; }
	@Override public String getGrammarFileName() { return "/Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/DependenciesCF.g"; }


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



	public static class prog_return extends ParserRuleReturnScope {
		CommonTree tree;
		@Override
		public CommonTree getTree() { return tree; }
	};


	// $ANTLR start "prog"
	// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/DependenciesCF.g:56:1: prog : dependencies ;
	public final DependenciesCFParser.prog_return prog() throws RecognitionException {
		DependenciesCFParser.prog_return retval = new DependenciesCFParser.prog_return();
		retval.start = input.LT(1);

		CommonTree root_0 = null;

		ParserRuleReturnScope dependencies1 =null;


		try {
			// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/DependenciesCF.g:56:5: ( dependencies )
			// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/DependenciesCF.g:56:7: dependencies
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
	// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/DependenciesCF.g:58:1: dependencies : ( 'ST-TGDs:' ( sttgd )+ )? ( 'T-TGDs:' ( etgd )+ )? ( 'EGDs:' ( egd )+ )? ( 'Queries:' ( query )+ )? ;
	public final DependenciesCFParser.dependencies_return dependencies() throws RecognitionException {
		DependenciesCFParser.dependencies_return retval = new DependenciesCFParser.dependencies_return();
		retval.start = input.LT(1);

		CommonTree root_0 = null;

		Token string_literal2=null;
		Token string_literal4=null;
		Token string_literal6=null;
		Token string_literal8=null;
		ParserRuleReturnScope sttgd3 =null;
		ParserRuleReturnScope etgd5 =null;
		ParserRuleReturnScope egd7 =null;
		ParserRuleReturnScope query9 =null;

		CommonTree string_literal2_tree=null;
		CommonTree string_literal4_tree=null;
		CommonTree string_literal6_tree=null;
		CommonTree string_literal8_tree=null;

		try {
			// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/DependenciesCF.g:58:13: ( ( 'ST-TGDs:' ( sttgd )+ )? ( 'T-TGDs:' ( etgd )+ )? ( 'EGDs:' ( egd )+ )? ( 'Queries:' ( query )+ )? )
			// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/DependenciesCF.g:59:11: ( 'ST-TGDs:' ( sttgd )+ )? ( 'T-TGDs:' ( etgd )+ )? ( 'EGDs:' ( egd )+ )? ( 'Queries:' ( query )+ )?
			{
			root_0 = (CommonTree)adaptor.nil();


			// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/DependenciesCF.g:59:11: ( 'ST-TGDs:' ( sttgd )+ )?
			int alt2=2;
			int LA2_0 = input.LA(1);
			if ( (LA2_0==23) ) {
				alt2=1;
			}
			switch (alt2) {
				case 1 :
					// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/DependenciesCF.g:59:12: 'ST-TGDs:' ( sttgd )+
					{
					string_literal2=(Token)match(input,23,FOLLOW_23_in_dependencies80); 
					string_literal2_tree = (CommonTree)adaptor.create(string_literal2);
					adaptor.addChild(root_0, string_literal2_tree);

					// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/DependenciesCF.g:59:23: ( sttgd )+
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
							// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/DependenciesCF.g:59:23: sttgd
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

			}

			// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/DependenciesCF.g:60:11: ( 'T-TGDs:' ( etgd )+ )?
			int alt4=2;
			int LA4_0 = input.LA(1);
			if ( (LA4_0==24) ) {
				alt4=1;
			}
			switch (alt4) {
				case 1 :
					// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/DependenciesCF.g:60:12: 'T-TGDs:' ( etgd )+
					{
					string_literal4=(Token)match(input,24,FOLLOW_24_in_dependencies101); 
					string_literal4_tree = (CommonTree)adaptor.create(string_literal4);
					adaptor.addChild(root_0, string_literal4_tree);

					// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/DependenciesCF.g:60:22: ( etgd )+
					int cnt3=0;
					loop3:
					while (true) {
						int alt3=2;
						int LA3_0 = input.LA(1);
						if ( (LA3_0==IDENTIFIER) ) {
							alt3=1;
						}

						switch (alt3) {
						case 1 :
							// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/DependenciesCF.g:60:22: etgd
							{
							pushFollow(FOLLOW_etgd_in_dependencies103);
							etgd5=etgd();
							state._fsp--;

							adaptor.addChild(root_0, etgd5.getTree());

							}
							break;

						default :
							if ( cnt3 >= 1 ) break loop3;
							EarlyExitException eee = new EarlyExitException(3, input);
							throw eee;
						}
						cnt3++;
					}

					 counter = 0;
					}
					break;

			}

			// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/DependenciesCF.g:61:11: ( 'EGDs:' ( egd )+ )?
			int alt6=2;
			int LA6_0 = input.LA(1);
			if ( (LA6_0==21) ) {
				alt6=1;
			}
			switch (alt6) {
				case 1 :
					// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/DependenciesCF.g:61:12: 'EGDs:' ( egd )+
					{
					string_literal6=(Token)match(input,21,FOLLOW_21_in_dependencies122); 
					string_literal6_tree = (CommonTree)adaptor.create(string_literal6);
					adaptor.addChild(root_0, string_literal6_tree);

					// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/DependenciesCF.g:61:20: ( egd )+
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
							// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/DependenciesCF.g:61:20: egd
							{
							pushFollow(FOLLOW_egd_in_dependencies124);
							egd7=egd();
							state._fsp--;

							adaptor.addChild(root_0, egd7.getTree());

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

			// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/DependenciesCF.g:62:11: ( 'Queries:' ( query )+ )?
			int alt8=2;
			int LA8_0 = input.LA(1);
			if ( (LA8_0==22) ) {
				alt8=1;
			}
			switch (alt8) {
				case 1 :
					// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/DependenciesCF.g:62:12: 'Queries:' ( query )+
					{
					string_literal8=(Token)match(input,22,FOLLOW_22_in_dependencies143); 
					string_literal8_tree = (CommonTree)adaptor.create(string_literal8);
					adaptor.addChild(root_0, string_literal8_tree);

					// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/DependenciesCF.g:62:23: ( query )+
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
							// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/DependenciesCF.g:62:23: query
							{
							pushFollow(FOLLOW_query_in_dependencies145);
							query9=query();
							state._fsp--;

							adaptor.addChild(root_0, query9.getTree());

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
	// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/DependenciesCF.g:64:1: sttgd : dependency ;
	public final DependenciesCFParser.sttgd_return sttgd() throws RecognitionException {
		DependenciesCFParser.sttgd_return retval = new DependenciesCFParser.sttgd_return();
		retval.start = input.LT(1);

		CommonTree root_0 = null;

		ParserRuleReturnScope dependency10 =null;


		try {
			// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/DependenciesCF.g:64:6: ( dependency )
			// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/DependenciesCF.g:64:11: dependency
			{
			root_0 = (CommonTree)adaptor.nil();


			 stTGD = true; 
			pushFollow(FOLLOW_dependency_in_sttgd163);
			dependency10=dependency();
			state._fsp--;

			adaptor.addChild(root_0, dependency10.getTree());

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
	// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/DependenciesCF.g:66:1: etgd : dependency ;
	public final DependenciesCFParser.etgd_return etgd() throws RecognitionException {
		DependenciesCFParser.etgd_return retval = new DependenciesCFParser.etgd_return();
		retval.start = input.LT(1);

		CommonTree root_0 = null;

		ParserRuleReturnScope dependency11 =null;


		try {
			// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/DependenciesCF.g:66:5: ( dependency )
			// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/DependenciesCF.g:66:10: dependency
			{
			root_0 = (CommonTree)adaptor.nil();


			 stTGD = false; 
			pushFollow(FOLLOW_dependency_in_etgd178);
			dependency11=dependency();
			state._fsp--;

			adaptor.addChild(root_0, dependency11.getTree());

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


	public static class egd_return extends ParserRuleReturnScope {
		CommonTree tree;
		@Override
		public CommonTree getTree() { return tree; }
	};


	// $ANTLR start "egd"
	// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/DependenciesCF.g:68:1: egd : dependency ;
	public final DependenciesCFParser.egd_return egd() throws RecognitionException {
		DependenciesCFParser.egd_return retval = new DependenciesCFParser.egd_return();
		retval.start = input.LT(1);

		CommonTree root_0 = null;

		ParserRuleReturnScope dependency12 =null;


		try {
			// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/DependenciesCF.g:68:4: ( dependency )
			// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/DependenciesCF.g:68:9: dependency
			{
			root_0 = (CommonTree)adaptor.nil();


			 stTGD = false; 
			pushFollow(FOLLOW_dependency_in_egd193);
			dependency12=dependency();
			state._fsp--;

			adaptor.addChild(root_0, dependency12.getTree());

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


	public static class query_return extends ParserRuleReturnScope {
		CommonTree tree;
		@Override
		public CommonTree getTree() { return tree; }
	};


	// $ANTLR start "query"
	// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/DependenciesCF.g:70:1: query : querydependency ;
	public final DependenciesCFParser.query_return query() throws RecognitionException {
		DependenciesCFParser.query_return retval = new DependenciesCFParser.query_return();
		retval.start = input.LT(1);

		CommonTree root_0 = null;

		ParserRuleReturnScope querydependency13 =null;


		try {
			// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/DependenciesCF.g:70:6: ( querydependency )
			// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/DependenciesCF.g:70:11: querydependency
			{
			root_0 = (CommonTree)adaptor.nil();


			 stTGD = false; 
			pushFollow(FOLLOW_querydependency_in_query214);
			querydependency13=querydependency();
			state._fsp--;

			adaptor.addChild(root_0, querydependency13.getTree());

			 dependency.setType(LunaticConstants.QUERY); dependency.setId("q" + counter++); generator.addQuery(dependency); 
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
	// $ANTLR end "query"


	public static class dependency_return extends ParserRuleReturnScope {
		CommonTree tree;
		@Override
		public CommonTree getTree() { return tree; }
	};


	// $ANTLR start "dependency"
	// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/DependenciesCF.g:72:1: dependency : (id= IDENTIFIER ':' )? positiveFormula '->' ( conclusionFormula ) '.' ;
	public final DependenciesCFParser.dependency_return dependency() throws RecognitionException {
		DependenciesCFParser.dependency_return retval = new DependenciesCFParser.dependency_return();
		retval.start = input.LT(1);

		CommonTree root_0 = null;

		Token id=null;
		Token char_literal14=null;
		Token string_literal16=null;
		Token char_literal18=null;
		ParserRuleReturnScope positiveFormula15 =null;
		ParserRuleReturnScope conclusionFormula17 =null;

		CommonTree id_tree=null;
		CommonTree char_literal14_tree=null;
		CommonTree string_literal16_tree=null;
		CommonTree char_literal18_tree=null;

		try {
			// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/DependenciesCF.g:72:11: ( (id= IDENTIFIER ':' )? positiveFormula '->' ( conclusionFormula ) '.' )
			// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/DependenciesCF.g:72:14: (id= IDENTIFIER ':' )? positiveFormula '->' ( conclusionFormula ) '.'
			{
			root_0 = (CommonTree)adaptor.nil();


			// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/DependenciesCF.g:72:14: (id= IDENTIFIER ':' )?
			int alt9=2;
			int LA9_0 = input.LA(1);
			if ( (LA9_0==IDENTIFIER) ) {
				int LA9_1 = input.LA(2);
				if ( (LA9_1==19) ) {
					alt9=1;
				}
			}
			switch (alt9) {
				case 1 :
					// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/DependenciesCF.g:72:15: id= IDENTIFIER ':'
					{
					id=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_dependency236); 
					id_tree = (CommonTree)adaptor.create(id);
					adaptor.addChild(root_0, id_tree);

					char_literal14=(Token)match(input,19,FOLLOW_19_in_dependency237); 
					char_literal14_tree = (CommonTree)adaptor.create(char_literal14);
					adaptor.addChild(root_0, char_literal14_tree);

					}
					break;

			}

			  dependency = new Dependency(); 
			                    formulaWN = new FormulaWithNegations(); 
			                    formulaStack.push(formulaWN);
			                    dependency.setPremise(formulaWN);
			                    inPremise = true;
			                    if(id!=null) dependency.setId(id.getText()); 
			pushFollow(FOLLOW_positiveFormula_in_dependency246);
			positiveFormula15=positiveFormula();
			state._fsp--;

			adaptor.addChild(root_0, positiveFormula15.getTree());

			string_literal16=(Token)match(input,17,FOLLOW_17_in_dependency248); 
			string_literal16_tree = (CommonTree)adaptor.create(string_literal16);
			adaptor.addChild(root_0, string_literal16_tree);

			// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/DependenciesCF.g:79:4: ( conclusionFormula )
			// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/DependenciesCF.g:79:5: conclusionFormula
			{
			  formulaStack.clear(); inPremise = false;
			pushFollow(FOLLOW_conclusionFormula_in_dependency276);
			conclusionFormula17=conclusionFormula();
			state._fsp--;

			adaptor.addChild(root_0, conclusionFormula17.getTree());

			}

			char_literal18=(Token)match(input,18,FOLLOW_18_in_dependency279); 
			char_literal18_tree = (CommonTree)adaptor.create(char_literal18);
			adaptor.addChild(root_0, char_literal18_tree);

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


	public static class querydependency_return extends ParserRuleReturnScope {
		CommonTree tree;
		@Override
		public CommonTree getTree() { return tree; }
	};


	// $ANTLR start "querydependency"
	// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/DependenciesCF.g:82:1: querydependency : (id= IDENTIFIER ':' )? conclusionQueryFormula '<-' ( positiveFormula ) '.' ;
	public final DependenciesCFParser.querydependency_return querydependency() throws RecognitionException {
		DependenciesCFParser.querydependency_return retval = new DependenciesCFParser.querydependency_return();
		retval.start = input.LT(1);

		CommonTree root_0 = null;

		Token id=null;
		Token char_literal19=null;
		Token string_literal21=null;
		Token char_literal23=null;
		ParserRuleReturnScope conclusionQueryFormula20 =null;
		ParserRuleReturnScope positiveFormula22 =null;

		CommonTree id_tree=null;
		CommonTree char_literal19_tree=null;
		CommonTree string_literal21_tree=null;
		CommonTree char_literal23_tree=null;

		try {
			// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/DependenciesCF.g:82:17: ( (id= IDENTIFIER ':' )? conclusionQueryFormula '<-' ( positiveFormula ) '.' )
			// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/DependenciesCF.g:82:19: (id= IDENTIFIER ':' )? conclusionQueryFormula '<-' ( positiveFormula ) '.'
			{
			root_0 = (CommonTree)adaptor.nil();


			// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/DependenciesCF.g:82:19: (id= IDENTIFIER ':' )?
			int alt10=2;
			int LA10_0 = input.LA(1);
			if ( (LA10_0==IDENTIFIER) ) {
				int LA10_1 = input.LA(2);
				if ( (LA10_1==19) ) {
					alt10=1;
				}
			}
			switch (alt10) {
				case 1 :
					// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/DependenciesCF.g:82:20: id= IDENTIFIER ':'
					{
					id=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_querydependency301); 
					id_tree = (CommonTree)adaptor.create(id);
					adaptor.addChild(root_0, id_tree);

					char_literal19=(Token)match(input,19,FOLLOW_19_in_querydependency302); 
					char_literal19_tree = (CommonTree)adaptor.create(char_literal19);
					adaptor.addChild(root_0, char_literal19_tree);

					}
					break;

			}

			  dependency = new Dependency(); 
			                    formulaWN = new FormulaWithNegations(); 
			                    formulaStack.clear(); 
			                    formulaStack.push(formulaWN);
			                    dependency.setPremise(formulaWN);
			                    inPremise = false;
			                    if(id!=null) dependency.setId(id.getText()); 
			pushFollow(FOLLOW_conclusionQueryFormula_in_querydependency311);
			conclusionQueryFormula20=conclusionQueryFormula();
			state._fsp--;

			adaptor.addChild(root_0, conclusionQueryFormula20.getTree());

			string_literal21=(Token)match(input,20,FOLLOW_20_in_querydependency313); 
			string_literal21_tree = (CommonTree)adaptor.create(string_literal21);
			adaptor.addChild(root_0, string_literal21_tree);

			// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/DependenciesCF.g:90:4: ( positiveFormula )
			// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/DependenciesCF.g:90:5: positiveFormula
			{
			   inPremise = true;
			pushFollow(FOLLOW_positiveFormula_in_querydependency341);
			positiveFormula22=positiveFormula();
			state._fsp--;

			adaptor.addChild(root_0, positiveFormula22.getTree());

			}

			char_literal23=(Token)match(input,18,FOLLOW_18_in_querydependency344); 
			char_literal23_tree = (CommonTree)adaptor.create(char_literal23);
			adaptor.addChild(root_0, char_literal23_tree);

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
	// $ANTLR end "querydependency"


	public static class positiveFormula_return extends ParserRuleReturnScope {
		CommonTree tree;
		@Override
		public CommonTree getTree() { return tree; }
	};


	// $ANTLR start "positiveFormula"
	// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/DependenciesCF.g:93:1: positiveFormula : relationalAtom ( ',' atom )* ;
	public final DependenciesCFParser.positiveFormula_return positiveFormula() throws RecognitionException {
		DependenciesCFParser.positiveFormula_return retval = new DependenciesCFParser.positiveFormula_return();
		retval.start = input.LT(1);

		CommonTree root_0 = null;

		Token char_literal25=null;
		ParserRuleReturnScope relationalAtom24 =null;
		ParserRuleReturnScope atom26 =null;

		CommonTree char_literal25_tree=null;

		try {
			// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/DependenciesCF.g:93:16: ( relationalAtom ( ',' atom )* )
			// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/DependenciesCF.g:93:18: relationalAtom ( ',' atom )*
			{
			root_0 = (CommonTree)adaptor.nil();


			  positiveFormula = new PositiveFormula(); 
			                    positiveFormula.setFather(formulaStack.peek()); 
			                    formulaStack.peek().setPositiveFormula(positiveFormula); 
			pushFollow(FOLLOW_relationalAtom_in_positiveFormula398);
			relationalAtom24=relationalAtom();
			state._fsp--;

			adaptor.addChild(root_0, relationalAtom24.getTree());

			// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/DependenciesCF.g:96:34: ( ',' atom )*
			loop11:
			while (true) {
				int alt11=2;
				int LA11_0 = input.LA(1);
				if ( (LA11_0==16) ) {
					alt11=1;
				}

				switch (alt11) {
				case 1 :
					// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/DependenciesCF.g:96:35: ',' atom
					{
					char_literal25=(Token)match(input,16,FOLLOW_16_in_positiveFormula401); 
					char_literal25_tree = (CommonTree)adaptor.create(char_literal25);
					adaptor.addChild(root_0, char_literal25_tree);

					pushFollow(FOLLOW_atom_in_positiveFormula403);
					atom26=atom();
					state._fsp--;

					adaptor.addChild(root_0, atom26.getTree());

					}
					break;

				default :
					break loop11;
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


	public static class conclusionFormula_return extends ParserRuleReturnScope {
		CommonTree tree;
		@Override
		public CommonTree getTree() { return tree; }
	};


	// $ANTLR start "conclusionFormula"
	// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/DependenciesCF.g:98:1: conclusionFormula : atom ( ',' atom )* ;
	public final DependenciesCFParser.conclusionFormula_return conclusionFormula() throws RecognitionException {
		DependenciesCFParser.conclusionFormula_return retval = new DependenciesCFParser.conclusionFormula_return();
		retval.start = input.LT(1);

		CommonTree root_0 = null;

		Token char_literal28=null;
		ParserRuleReturnScope atom27 =null;
		ParserRuleReturnScope atom29 =null;

		CommonTree char_literal28_tree=null;

		try {
			// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/DependenciesCF.g:98:18: ( atom ( ',' atom )* )
			// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/DependenciesCF.g:98:20: atom ( ',' atom )*
			{
			root_0 = (CommonTree)adaptor.nil();


			  positiveFormula = new PositiveFormula(); 
			                      dependency.setConclusion(positiveFormula); 
			pushFollow(FOLLOW_atom_in_conclusionFormula470);
			atom27=atom();
			state._fsp--;

			adaptor.addChild(root_0, atom27.getTree());

			// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/DependenciesCF.g:100:24: ( ',' atom )*
			loop12:
			while (true) {
				int alt12=2;
				int LA12_0 = input.LA(1);
				if ( (LA12_0==16) ) {
					alt12=1;
				}

				switch (alt12) {
				case 1 :
					// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/DependenciesCF.g:100:25: ',' atom
					{
					char_literal28=(Token)match(input,16,FOLLOW_16_in_conclusionFormula473); 
					char_literal28_tree = (CommonTree)adaptor.create(char_literal28);
					adaptor.addChild(root_0, char_literal28_tree);

					pushFollow(FOLLOW_atom_in_conclusionFormula475);
					atom29=atom();
					state._fsp--;

					adaptor.addChild(root_0, atom29.getTree());

					}
					break;

				default :
					break loop12;
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


	public static class conclusionQueryFormula_return extends ParserRuleReturnScope {
		CommonTree tree;
		@Override
		public CommonTree getTree() { return tree; }
	};


	// $ANTLR start "conclusionQueryFormula"
	// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/DependenciesCF.g:102:1: conclusionQueryFormula : queryAtom ;
	public final DependenciesCFParser.conclusionQueryFormula_return conclusionQueryFormula() throws RecognitionException {
		DependenciesCFParser.conclusionQueryFormula_return retval = new DependenciesCFParser.conclusionQueryFormula_return();
		retval.start = input.LT(1);

		CommonTree root_0 = null;

		ParserRuleReturnScope queryAtom30 =null;


		try {
			// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/DependenciesCF.g:102:23: ( queryAtom )
			// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/DependenciesCF.g:102:25: queryAtom
			{
			root_0 = (CommonTree)adaptor.nil();


			  positiveFormula = new PositiveFormula(); 
			                      dependency.setConclusion(positiveFormula); 
			pushFollow(FOLLOW_queryAtom_in_conclusionQueryFormula542);
			queryAtom30=queryAtom();
			state._fsp--;

			adaptor.addChild(root_0, queryAtom30.getTree());

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
	// $ANTLR end "conclusionQueryFormula"


	public static class atom_return extends ParserRuleReturnScope {
		CommonTree tree;
		@Override
		public CommonTree getTree() { return tree; }
	};


	// $ANTLR start "atom"
	// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/DependenciesCF.g:106:1: atom : ( relationalAtom | builtin | comparison );
	public final DependenciesCFParser.atom_return atom() throws RecognitionException {
		DependenciesCFParser.atom_return retval = new DependenciesCFParser.atom_return();
		retval.start = input.LT(1);

		CommonTree root_0 = null;

		ParserRuleReturnScope relationalAtom31 =null;
		ParserRuleReturnScope builtin32 =null;
		ParserRuleReturnScope comparison33 =null;


		try {
			// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/DependenciesCF.g:106:6: ( relationalAtom | builtin | comparison )
			int alt13=3;
			switch ( input.LA(1) ) {
			case IDENTIFIER:
				{
				alt13=1;
				}
				break;
			case EXPRESSION:
				{
				alt13=2;
				}
				break;
			case NUMBER:
			case STRING:
			case 25:
				{
				alt13=3;
				}
				break;
			default:
				NoViableAltException nvae =
					new NoViableAltException("", 13, 0, input);
				throw nvae;
			}
			switch (alt13) {
				case 1 :
					// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/DependenciesCF.g:106:9: relationalAtom
					{
					root_0 = (CommonTree)adaptor.nil();


					pushFollow(FOLLOW_relationalAtom_in_atom552);
					relationalAtom31=relationalAtom();
					state._fsp--;

					adaptor.addChild(root_0, relationalAtom31.getTree());

					}
					break;
				case 2 :
					// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/DependenciesCF.g:106:26: builtin
					{
					root_0 = (CommonTree)adaptor.nil();


					pushFollow(FOLLOW_builtin_in_atom556);
					builtin32=builtin();
					state._fsp--;

					adaptor.addChild(root_0, builtin32.getTree());

					}
					break;
				case 3 :
					// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/DependenciesCF.g:106:36: comparison
					{
					root_0 = (CommonTree)adaptor.nil();


					pushFollow(FOLLOW_comparison_in_atom560);
					comparison33=comparison();
					state._fsp--;

					adaptor.addChild(root_0, comparison33.getTree());

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
	// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/DependenciesCF.g:108:1: relationalAtom : name= IDENTIFIER '(' attribute ( ',' attribute )* ')' ;
	public final DependenciesCFParser.relationalAtom_return relationalAtom() throws RecognitionException {
		DependenciesCFParser.relationalAtom_return retval = new DependenciesCFParser.relationalAtom_return();
		retval.start = input.LT(1);

		CommonTree root_0 = null;

		Token name=null;
		Token char_literal34=null;
		Token char_literal36=null;
		Token char_literal38=null;
		ParserRuleReturnScope attribute35 =null;
		ParserRuleReturnScope attribute37 =null;

		CommonTree name_tree=null;
		CommonTree char_literal34_tree=null;
		CommonTree char_literal36_tree=null;
		CommonTree char_literal38_tree=null;

		try {
			// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/DependenciesCF.g:108:15: (name= IDENTIFIER '(' attribute ( ',' attribute )* ')' )
			// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/DependenciesCF.g:108:18: name= IDENTIFIER '(' attribute ( ',' attribute )* ')'
			{
			root_0 = (CommonTree)adaptor.nil();


			name=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_relationalAtom571); 
			name_tree = (CommonTree)adaptor.create(name);
			adaptor.addChild(root_0, name_tree);

			 atom = new RelationalAtom(name.getText()); attributePosition = 0; 
			char_literal34=(Token)match(input,14,FOLLOW_14_in_relationalAtom575); 
			char_literal34_tree = (CommonTree)adaptor.create(char_literal34);
			adaptor.addChild(root_0, char_literal34_tree);

			pushFollow(FOLLOW_attribute_in_relationalAtom577);
			attribute35=attribute();
			state._fsp--;

			adaptor.addChild(root_0, attribute35.getTree());

			// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/DependenciesCF.g:108:118: ( ',' attribute )*
			loop14:
			while (true) {
				int alt14=2;
				int LA14_0 = input.LA(1);
				if ( (LA14_0==16) ) {
					alt14=1;
				}

				switch (alt14) {
				case 1 :
					// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/DependenciesCF.g:108:119: ',' attribute
					{
					char_literal36=(Token)match(input,16,FOLLOW_16_in_relationalAtom580); 
					char_literal36_tree = (CommonTree)adaptor.create(char_literal36);
					adaptor.addChild(root_0, char_literal36_tree);

					pushFollow(FOLLOW_attribute_in_relationalAtom582);
					attribute37=attribute();
					state._fsp--;

					adaptor.addChild(root_0, attribute37.getTree());

					}
					break;

				default :
					break loop14;
				}
			}

			char_literal38=(Token)match(input,15,FOLLOW_15_in_relationalAtom586); 
			char_literal38_tree = (CommonTree)adaptor.create(char_literal38);
			adaptor.addChild(root_0, char_literal38_tree);

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


	public static class queryAtom_return extends ParserRuleReturnScope {
		CommonTree tree;
		@Override
		public CommonTree getTree() { return tree; }
	};


	// $ANTLR start "queryAtom"
	// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/DependenciesCF.g:111:1: queryAtom : name= IDENTIFIER '(' queryattribute ( ',' queryattribute )* ')' ;
	public final DependenciesCFParser.queryAtom_return queryAtom() throws RecognitionException {
		DependenciesCFParser.queryAtom_return retval = new DependenciesCFParser.queryAtom_return();
		retval.start = input.LT(1);

		CommonTree root_0 = null;

		Token name=null;
		Token char_literal39=null;
		Token char_literal41=null;
		Token char_literal43=null;
		ParserRuleReturnScope queryattribute40 =null;
		ParserRuleReturnScope queryattribute42 =null;

		CommonTree name_tree=null;
		CommonTree char_literal39_tree=null;
		CommonTree char_literal41_tree=null;
		CommonTree char_literal43_tree=null;

		try {
			// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/DependenciesCF.g:111:10: (name= IDENTIFIER '(' queryattribute ( ',' queryattribute )* ')' )
			// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/DependenciesCF.g:111:13: name= IDENTIFIER '(' queryattribute ( ',' queryattribute )* ')'
			{
			root_0 = (CommonTree)adaptor.nil();


			name=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_queryAtom604); 
			name_tree = (CommonTree)adaptor.create(name);
			adaptor.addChild(root_0, name_tree);

			 atom = new QueryAtom(name.getText()); attributePosition = 0; 
			char_literal39=(Token)match(input,14,FOLLOW_14_in_queryAtom608); 
			char_literal39_tree = (CommonTree)adaptor.create(char_literal39);
			adaptor.addChild(root_0, char_literal39_tree);

			pushFollow(FOLLOW_queryattribute_in_queryAtom610);
			queryattribute40=queryattribute();
			state._fsp--;

			adaptor.addChild(root_0, queryattribute40.getTree());

			// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/DependenciesCF.g:111:113: ( ',' queryattribute )*
			loop15:
			while (true) {
				int alt15=2;
				int LA15_0 = input.LA(1);
				if ( (LA15_0==16) ) {
					alt15=1;
				}

				switch (alt15) {
				case 1 :
					// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/DependenciesCF.g:111:114: ',' queryattribute
					{
					char_literal41=(Token)match(input,16,FOLLOW_16_in_queryAtom613); 
					char_literal41_tree = (CommonTree)adaptor.create(char_literal41);
					adaptor.addChild(root_0, char_literal41_tree);

					pushFollow(FOLLOW_queryattribute_in_queryAtom615);
					queryattribute42=queryattribute();
					state._fsp--;

					adaptor.addChild(root_0, queryattribute42.getTree());

					}
					break;

				default :
					break loop15;
				}
			}

			char_literal43=(Token)match(input,15,FOLLOW_15_in_queryAtom619); 
			char_literal43_tree = (CommonTree)adaptor.create(char_literal43);
			adaptor.addChild(root_0, char_literal43_tree);

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
	// $ANTLR end "queryAtom"


	public static class builtin_return extends ParserRuleReturnScope {
		CommonTree tree;
		@Override
		public CommonTree getTree() { return tree; }
	};


	// $ANTLR start "builtin"
	// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/DependenciesCF.g:114:1: builtin : expression= EXPRESSION ;
	public final DependenciesCFParser.builtin_return builtin() throws RecognitionException {
		DependenciesCFParser.builtin_return retval = new DependenciesCFParser.builtin_return();
		retval.start = input.LT(1);

		CommonTree root_0 = null;

		Token expression=null;

		CommonTree expression_tree=null;

		try {
			// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/DependenciesCF.g:114:9: (expression= EXPRESSION )
			// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/DependenciesCF.g:114:12: expression= EXPRESSION
			{
			root_0 = (CommonTree)adaptor.nil();


			expression=(Token)match(input,EXPRESSION,FOLLOW_EXPRESSION_in_builtin635); 
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
	// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/DependenciesCF.g:118:1: comparison : leftargument oper= OPERATOR rightargument ;
	public final DependenciesCFParser.comparison_return comparison() throws RecognitionException {
		DependenciesCFParser.comparison_return retval = new DependenciesCFParser.comparison_return();
		retval.start = input.LT(1);

		CommonTree root_0 = null;

		Token oper=null;
		ParserRuleReturnScope leftargument44 =null;
		ParserRuleReturnScope rightargument45 =null;

		CommonTree oper_tree=null;

		try {
			// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/DependenciesCF.g:118:12: ( leftargument oper= OPERATOR rightargument )
			// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/DependenciesCF.g:118:15: leftargument oper= OPERATOR rightargument
			{
			root_0 = (CommonTree)adaptor.nil();


			   expressionString = new StringBuilder(); 
					     leftConstant = null;
					     rightConstant = null;
			pushFollow(FOLLOW_leftargument_in_comparison694);
			leftargument44=leftargument();
			state._fsp--;

			adaptor.addChild(root_0, leftargument44.getTree());

			oper=(Token)match(input,OPERATOR,FOLLOW_OPERATOR_in_comparison716); 
			oper_tree = (CommonTree)adaptor.create(oper);
			adaptor.addChild(root_0, oper_tree);

			 
			                 	String operatorText = oper.getText();
			                 	if(operatorText.equals("=")){
			                 	   operatorText = "==";
			                 	}
			                 	expressionString.append(" ").append(operatorText); 
			                 
			pushFollow(FOLLOW_rightargument_in_comparison737);
			rightargument45=rightargument();
			state._fsp--;

			adaptor.addChild(root_0, rightargument45.getTree());

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
	// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/DependenciesCF.g:134:1: leftargument : ( '\\?' var= IDENTIFIER |constant= ( STRING | NUMBER ) ) ;
	public final DependenciesCFParser.leftargument_return leftargument() throws RecognitionException {
		DependenciesCFParser.leftargument_return retval = new DependenciesCFParser.leftargument_return();
		retval.start = input.LT(1);

		CommonTree root_0 = null;

		Token var=null;
		Token constant=null;
		Token char_literal46=null;

		CommonTree var_tree=null;
		CommonTree constant_tree=null;
		CommonTree char_literal46_tree=null;

		try {
			// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/DependenciesCF.g:134:13: ( ( '\\?' var= IDENTIFIER |constant= ( STRING | NUMBER ) ) )
			// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/DependenciesCF.g:134:16: ( '\\?' var= IDENTIFIER |constant= ( STRING | NUMBER ) )
			{
			root_0 = (CommonTree)adaptor.nil();


			// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/DependenciesCF.g:134:16: ( '\\?' var= IDENTIFIER |constant= ( STRING | NUMBER ) )
			int alt16=2;
			int LA16_0 = input.LA(1);
			if ( (LA16_0==25) ) {
				alt16=1;
			}
			else if ( (LA16_0==NUMBER||LA16_0==STRING) ) {
				alt16=2;
			}

			else {
				NoViableAltException nvae =
					new NoViableAltException("", 16, 0, input);
				throw nvae;
			}

			switch (alt16) {
				case 1 :
					// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/DependenciesCF.g:134:17: '\\?' var= IDENTIFIER
					{
					char_literal46=(Token)match(input,25,FOLLOW_25_in_leftargument767); 
					char_literal46_tree = (CommonTree)adaptor.create(char_literal46);
					adaptor.addChild(root_0, char_literal46_tree);

					var=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_leftargument770); 
					var_tree = (CommonTree)adaptor.create(var);
					adaptor.addChild(root_0, var_tree);

					 expressionString.append(var.getText()); 
					}
					break;
				case 2 :
					// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/DependenciesCF.g:135:18: constant= ( STRING | NUMBER )
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
	// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/DependenciesCF.g:138:1: rightargument : ( '\\?' var= IDENTIFIER |constant= ( STRING | NUMBER ) ) ;
	public final DependenciesCFParser.rightargument_return rightargument() throws RecognitionException {
		DependenciesCFParser.rightargument_return retval = new DependenciesCFParser.rightargument_return();
		retval.start = input.LT(1);

		CommonTree root_0 = null;

		Token var=null;
		Token constant=null;
		Token char_literal47=null;

		CommonTree var_tree=null;
		CommonTree constant_tree=null;
		CommonTree char_literal47_tree=null;

		try {
			// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/DependenciesCF.g:138:14: ( ( '\\?' var= IDENTIFIER |constant= ( STRING | NUMBER ) ) )
			// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/DependenciesCF.g:138:17: ( '\\?' var= IDENTIFIER |constant= ( STRING | NUMBER ) )
			{
			root_0 = (CommonTree)adaptor.nil();


			// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/DependenciesCF.g:138:17: ( '\\?' var= IDENTIFIER |constant= ( STRING | NUMBER ) )
			int alt17=2;
			int LA17_0 = input.LA(1);
			if ( (LA17_0==25) ) {
				alt17=1;
			}
			else if ( (LA17_0==NUMBER||LA17_0==STRING) ) {
				alt17=2;
			}

			else {
				NoViableAltException nvae =
					new NoViableAltException("", 17, 0, input);
				throw nvae;
			}

			switch (alt17) {
				case 1 :
					// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/DependenciesCF.g:138:18: '\\?' var= IDENTIFIER
					{
					char_literal47=(Token)match(input,25,FOLLOW_25_in_rightargument848); 
					char_literal47_tree = (CommonTree)adaptor.create(char_literal47);
					adaptor.addChild(root_0, char_literal47_tree);

					var=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_rightargument851); 
					var_tree = (CommonTree)adaptor.create(var);
					adaptor.addChild(root_0, var_tree);

					 expressionString.append(var.getText()); 
					}
					break;
				case 2 :
					// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/DependenciesCF.g:139:18: constant= ( STRING | NUMBER )
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
	// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/DependenciesCF.g:142:1: attribute : value ;
	public final DependenciesCFParser.attribute_return attribute() throws RecognitionException {
		DependenciesCFParser.attribute_return retval = new DependenciesCFParser.attribute_return();
		retval.start = input.LT(1);

		CommonTree root_0 = null;

		ParserRuleReturnScope value48 =null;


		try {
			// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/DependenciesCF.g:142:10: ( value )
			// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/DependenciesCF.g:142:13: value
			{
			root_0 = (CommonTree)adaptor.nil();


			 String attributeName = generator.findAttributeName(((RelationalAtom)atom).getTableName(), attributePosition, inPremise, stTGD); 
			                   attribute = new FormulaAttribute(attributeName); attributePosition++;
			pushFollow(FOLLOW_value_in_attribute930);
			value48=value();
			state._fsp--;

			adaptor.addChild(root_0, value48.getTree());

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


	public static class queryattribute_return extends ParserRuleReturnScope {
		CommonTree tree;
		@Override
		public CommonTree getTree() { return tree; }
	};


	// $ANTLR start "queryattribute"
	// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/DependenciesCF.g:146:1: queryattribute : queryvalue ;
	public final DependenciesCFParser.queryattribute_return queryattribute() throws RecognitionException {
		DependenciesCFParser.queryattribute_return retval = new DependenciesCFParser.queryattribute_return();
		retval.start = input.LT(1);

		CommonTree root_0 = null;

		ParserRuleReturnScope queryvalue49 =null;


		try {
			// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/DependenciesCF.g:146:15: ( queryvalue )
			// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/DependenciesCF.g:146:18: queryvalue
			{
			root_0 = (CommonTree)adaptor.nil();


			 String attributeName = "a" + attributePosition; 
			                   attribute = new FormulaAttribute(attributeName); attributePosition++;
			pushFollow(FOLLOW_queryvalue_in_queryattribute949);
			queryvalue49=queryvalue();
			state._fsp--;

			adaptor.addChild(root_0, queryvalue49.getTree());

			 ((QueryAtom)atom).addAttribute(attribute); 
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
	// $ANTLR end "queryattribute"


	public static class value_return extends ParserRuleReturnScope {
		CommonTree tree;
		@Override
		public CommonTree getTree() { return tree; }
	};


	// $ANTLR start "value"
	// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/DependenciesCF.g:150:1: value : ( '\\?' var= IDENTIFIER |constant= ( STRING | NUMBER ) |nullValue= NULL |expression= EXPRESSION );
	public final DependenciesCFParser.value_return value() throws RecognitionException {
		DependenciesCFParser.value_return retval = new DependenciesCFParser.value_return();
		retval.start = input.LT(1);

		CommonTree root_0 = null;

		Token var=null;
		Token constant=null;
		Token nullValue=null;
		Token expression=null;
		Token char_literal50=null;

		CommonTree var_tree=null;
		CommonTree constant_tree=null;
		CommonTree nullValue_tree=null;
		CommonTree expression_tree=null;
		CommonTree char_literal50_tree=null;

		try {
			// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/DependenciesCF.g:150:7: ( '\\?' var= IDENTIFIER |constant= ( STRING | NUMBER ) |nullValue= NULL |expression= EXPRESSION )
			int alt18=4;
			switch ( input.LA(1) ) {
			case 25:
				{
				alt18=1;
				}
				break;
			case NUMBER:
			case STRING:
				{
				alt18=2;
				}
				break;
			case NULL:
				{
				alt18=3;
				}
				break;
			case EXPRESSION:
				{
				alt18=4;
				}
				break;
			default:
				NoViableAltException nvae =
					new NoViableAltException("", 18, 0, input);
				throw nvae;
			}
			switch (alt18) {
				case 1 :
					// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/DependenciesCF.g:150:10: '\\?' var= IDENTIFIER
					{
					root_0 = (CommonTree)adaptor.nil();


					char_literal50=(Token)match(input,25,FOLLOW_25_in_value967); 
					char_literal50_tree = (CommonTree)adaptor.create(char_literal50);
					adaptor.addChild(root_0, char_literal50_tree);

					var=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_value970); 
					var_tree = (CommonTree)adaptor.create(var);
					adaptor.addChild(root_0, var_tree);

					 attribute.setValue(new FormulaVariableOccurrence(new AttributeRef(((RelationalAtom)atom).getTableName(), attribute.getAttributeName()), var.getText())); 
					}
					break;
				case 2 :
					// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/DependenciesCF.g:151:18: constant= ( STRING | NUMBER )
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
					// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/DependenciesCF.g:152:18: nullValue= NULL
					{
					root_0 = (CommonTree)adaptor.nil();


					nullValue=(Token)match(input,NULL,FOLLOW_NULL_in_value1026); 
					nullValue_tree = (CommonTree)adaptor.create(nullValue);
					adaptor.addChild(root_0, nullValue_tree);

					 attribute.setValue(new FormulaConstant(nullValue.getText(), true)); 
					}
					break;
				case 4 :
					// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/DependenciesCF.g:153:18: expression= EXPRESSION
					{
					root_0 = (CommonTree)adaptor.nil();


					expression=(Token)match(input,EXPRESSION,FOLLOW_EXPRESSION_in_value1051); 
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


	public static class queryvalue_return extends ParserRuleReturnScope {
		CommonTree tree;
		@Override
		public CommonTree getTree() { return tree; }
	};


	// $ANTLR start "queryvalue"
	// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/DependenciesCF.g:156:1: queryvalue : ( '\\?' var= IDENTIFIER |constant= ( STRING | NUMBER ) |nullValue= NULL |expression= EXPRESSION );
	public final DependenciesCFParser.queryvalue_return queryvalue() throws RecognitionException {
		DependenciesCFParser.queryvalue_return retval = new DependenciesCFParser.queryvalue_return();
		retval.start = input.LT(1);

		CommonTree root_0 = null;

		Token var=null;
		Token constant=null;
		Token nullValue=null;
		Token expression=null;
		Token char_literal51=null;

		CommonTree var_tree=null;
		CommonTree constant_tree=null;
		CommonTree nullValue_tree=null;
		CommonTree expression_tree=null;
		CommonTree char_literal51_tree=null;

		try {
			// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/DependenciesCF.g:156:11: ( '\\?' var= IDENTIFIER |constant= ( STRING | NUMBER ) |nullValue= NULL |expression= EXPRESSION )
			int alt19=4;
			switch ( input.LA(1) ) {
			case 25:
				{
				alt19=1;
				}
				break;
			case NUMBER:
			case STRING:
				{
				alt19=2;
				}
				break;
			case NULL:
				{
				alt19=3;
				}
				break;
			case EXPRESSION:
				{
				alt19=4;
				}
				break;
			default:
				NoViableAltException nvae =
					new NoViableAltException("", 19, 0, input);
				throw nvae;
			}
			switch (alt19) {
				case 1 :
					// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/DependenciesCF.g:156:14: '\\?' var= IDENTIFIER
					{
					root_0 = (CommonTree)adaptor.nil();


					char_literal51=(Token)match(input,25,FOLLOW_25_in_queryvalue1062); 
					char_literal51_tree = (CommonTree)adaptor.create(char_literal51);
					adaptor.addChild(root_0, char_literal51_tree);

					var=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_queryvalue1065); 
					var_tree = (CommonTree)adaptor.create(var);
					adaptor.addChild(root_0, var_tree);

					 attribute.setValue(new FormulaVariableOccurrence(new AttributeRef(((QueryAtom)atom).getQueryId(), attribute.getAttributeName()), var.getText())); 
					}
					break;
				case 2 :
					// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/DependenciesCF.g:157:18: constant= ( STRING | NUMBER )
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
					// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/DependenciesCF.g:158:18: nullValue= NULL
					{
					root_0 = (CommonTree)adaptor.nil();


					nullValue=(Token)match(input,NULL,FOLLOW_NULL_in_queryvalue1121); 
					nullValue_tree = (CommonTree)adaptor.create(nullValue);
					adaptor.addChild(root_0, nullValue_tree);

					 attribute.setValue(new FormulaConstant(nullValue.getText(), true)); 
					}
					break;
				case 4 :
					// /Users/donatello/Projects/Llunatic/lunaticEngine/src/it/unibas/lunatic/parser/DependenciesCF.g:159:18: expression= EXPRESSION
					{
					root_0 = (CommonTree)adaptor.nil();


					expression=(Token)match(input,EXPRESSION,FOLLOW_EXPRESSION_in_queryvalue1146); 
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
	// $ANTLR end "queryvalue"

	// Delegated rules



	public static final BitSet FOLLOW_dependencies_in_prog54 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_23_in_dependencies80 = new BitSet(new long[]{0x0000000000000040L});
	public static final BitSet FOLLOW_sttgd_in_dependencies82 = new BitSet(new long[]{0x0000000001600042L});
	public static final BitSet FOLLOW_24_in_dependencies101 = new BitSet(new long[]{0x0000000000000040L});
	public static final BitSet FOLLOW_etgd_in_dependencies103 = new BitSet(new long[]{0x0000000000600042L});
	public static final BitSet FOLLOW_21_in_dependencies122 = new BitSet(new long[]{0x0000000000000040L});
	public static final BitSet FOLLOW_egd_in_dependencies124 = new BitSet(new long[]{0x0000000000400042L});
	public static final BitSet FOLLOW_22_in_dependencies143 = new BitSet(new long[]{0x0000000000000040L});
	public static final BitSet FOLLOW_query_in_dependencies145 = new BitSet(new long[]{0x0000000000000042L});
	public static final BitSet FOLLOW_dependency_in_sttgd163 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_dependency_in_etgd178 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_dependency_in_egd193 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_querydependency_in_query214 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_IDENTIFIER_in_dependency236 = new BitSet(new long[]{0x0000000000080000L});
	public static final BitSet FOLLOW_19_in_dependency237 = new BitSet(new long[]{0x0000000000000040L});
	public static final BitSet FOLLOW_positiveFormula_in_dependency246 = new BitSet(new long[]{0x0000000000020000L});
	public static final BitSet FOLLOW_17_in_dependency248 = new BitSet(new long[]{0x0000000002001460L});
	public static final BitSet FOLLOW_conclusionFormula_in_dependency276 = new BitSet(new long[]{0x0000000000040000L});
	public static final BitSet FOLLOW_18_in_dependency279 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_IDENTIFIER_in_querydependency301 = new BitSet(new long[]{0x0000000000080000L});
	public static final BitSet FOLLOW_19_in_querydependency302 = new BitSet(new long[]{0x0000000000000040L});
	public static final BitSet FOLLOW_conclusionQueryFormula_in_querydependency311 = new BitSet(new long[]{0x0000000000100000L});
	public static final BitSet FOLLOW_20_in_querydependency313 = new BitSet(new long[]{0x0000000000000040L});
	public static final BitSet FOLLOW_positiveFormula_in_querydependency341 = new BitSet(new long[]{0x0000000000040000L});
	public static final BitSet FOLLOW_18_in_querydependency344 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_relationalAtom_in_positiveFormula398 = new BitSet(new long[]{0x0000000000010002L});
	public static final BitSet FOLLOW_16_in_positiveFormula401 = new BitSet(new long[]{0x0000000002001460L});
	public static final BitSet FOLLOW_atom_in_positiveFormula403 = new BitSet(new long[]{0x0000000000010002L});
	public static final BitSet FOLLOW_atom_in_conclusionFormula470 = new BitSet(new long[]{0x0000000000010002L});
	public static final BitSet FOLLOW_16_in_conclusionFormula473 = new BitSet(new long[]{0x0000000002001460L});
	public static final BitSet FOLLOW_atom_in_conclusionFormula475 = new BitSet(new long[]{0x0000000000010002L});
	public static final BitSet FOLLOW_queryAtom_in_conclusionQueryFormula542 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_relationalAtom_in_atom552 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_builtin_in_atom556 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_comparison_in_atom560 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_IDENTIFIER_in_relationalAtom571 = new BitSet(new long[]{0x0000000000004000L});
	public static final BitSet FOLLOW_14_in_relationalAtom575 = new BitSet(new long[]{0x0000000002001620L});
	public static final BitSet FOLLOW_attribute_in_relationalAtom577 = new BitSet(new long[]{0x0000000000018000L});
	public static final BitSet FOLLOW_16_in_relationalAtom580 = new BitSet(new long[]{0x0000000002001620L});
	public static final BitSet FOLLOW_attribute_in_relationalAtom582 = new BitSet(new long[]{0x0000000000018000L});
	public static final BitSet FOLLOW_15_in_relationalAtom586 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_IDENTIFIER_in_queryAtom604 = new BitSet(new long[]{0x0000000000004000L});
	public static final BitSet FOLLOW_14_in_queryAtom608 = new BitSet(new long[]{0x0000000002001620L});
	public static final BitSet FOLLOW_queryattribute_in_queryAtom610 = new BitSet(new long[]{0x0000000000018000L});
	public static final BitSet FOLLOW_16_in_queryAtom613 = new BitSet(new long[]{0x0000000002001620L});
	public static final BitSet FOLLOW_queryattribute_in_queryAtom615 = new BitSet(new long[]{0x0000000000018000L});
	public static final BitSet FOLLOW_15_in_queryAtom619 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_EXPRESSION_in_builtin635 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_leftargument_in_comparison694 = new BitSet(new long[]{0x0000000000000800L});
	public static final BitSet FOLLOW_OPERATOR_in_comparison716 = new BitSet(new long[]{0x0000000002001400L});
	public static final BitSet FOLLOW_rightargument_in_comparison737 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_25_in_leftargument767 = new BitSet(new long[]{0x0000000000000040L});
	public static final BitSet FOLLOW_IDENTIFIER_in_leftargument770 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_set_in_leftargument795 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_25_in_rightargument848 = new BitSet(new long[]{0x0000000000000040L});
	public static final BitSet FOLLOW_IDENTIFIER_in_rightargument851 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_set_in_rightargument876 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_value_in_attribute930 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_queryvalue_in_queryattribute949 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_25_in_value967 = new BitSet(new long[]{0x0000000000000040L});
	public static final BitSet FOLLOW_IDENTIFIER_in_value970 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_set_in_value995 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_NULL_in_value1026 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_EXPRESSION_in_value1051 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_25_in_queryvalue1062 = new BitSet(new long[]{0x0000000000000040L});
	public static final BitSet FOLLOW_IDENTIFIER_in_queryvalue1065 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_set_in_queryvalue1090 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_NULL_in_queryvalue1121 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_EXPRESSION_in_queryvalue1146 = new BitSet(new long[]{0x0000000000000002L});
}
