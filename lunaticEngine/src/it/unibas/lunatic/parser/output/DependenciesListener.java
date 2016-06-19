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

import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link DependenciesParser}.
 */
public interface DependenciesListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link DependenciesParser#prog}.
	 * @param ctx the parse tree
	 */
	void enterProg(DependenciesParser.ProgContext ctx);
	/**
	 * Exit a parse tree produced by {@link DependenciesParser#prog}.
	 * @param ctx the parse tree
	 */
	void exitProg(DependenciesParser.ProgContext ctx);
	/**
	 * Enter a parse tree produced by {@link DependenciesParser#dependencies}.
	 * @param ctx the parse tree
	 */
	void enterDependencies(DependenciesParser.DependenciesContext ctx);
	/**
	 * Exit a parse tree produced by {@link DependenciesParser#dependencies}.
	 * @param ctx the parse tree
	 */
	void exitDependencies(DependenciesParser.DependenciesContext ctx);
	/**
	 * Enter a parse tree produced by {@link DependenciesParser#sttgd}.
	 * @param ctx the parse tree
	 */
	void enterSttgd(DependenciesParser.SttgdContext ctx);
	/**
	 * Exit a parse tree produced by {@link DependenciesParser#sttgd}.
	 * @param ctx the parse tree
	 */
	void exitSttgd(DependenciesParser.SttgdContext ctx);
	/**
	 * Enter a parse tree produced by {@link DependenciesParser#etgd}.
	 * @param ctx the parse tree
	 */
	void enterEtgd(DependenciesParser.EtgdContext ctx);
	/**
	 * Exit a parse tree produced by {@link DependenciesParser#etgd}.
	 * @param ctx the parse tree
	 */
	void exitEtgd(DependenciesParser.EtgdContext ctx);
	/**
	 * Enter a parse tree produced by {@link DependenciesParser#dc}.
	 * @param ctx the parse tree
	 */
	void enterDc(DependenciesParser.DcContext ctx);
	/**
	 * Exit a parse tree produced by {@link DependenciesParser#dc}.
	 * @param ctx the parse tree
	 */
	void exitDc(DependenciesParser.DcContext ctx);
	/**
	 * Enter a parse tree produced by {@link DependenciesParser#egd}.
	 * @param ctx the parse tree
	 */
	void enterEgd(DependenciesParser.EgdContext ctx);
	/**
	 * Exit a parse tree produced by {@link DependenciesParser#egd}.
	 * @param ctx the parse tree
	 */
	void exitEgd(DependenciesParser.EgdContext ctx);
	/**
	 * Enter a parse tree produced by {@link DependenciesParser#eegd}.
	 * @param ctx the parse tree
	 */
	void enterEegd(DependenciesParser.EegdContext ctx);
	/**
	 * Exit a parse tree produced by {@link DependenciesParser#eegd}.
	 * @param ctx the parse tree
	 */
	void exitEegd(DependenciesParser.EegdContext ctx);
	/**
	 * Enter a parse tree produced by {@link DependenciesParser#dedstgd}.
	 * @param ctx the parse tree
	 */
	void enterDedstgd(DependenciesParser.DedstgdContext ctx);
	/**
	 * Exit a parse tree produced by {@link DependenciesParser#dedstgd}.
	 * @param ctx the parse tree
	 */
	void exitDedstgd(DependenciesParser.DedstgdContext ctx);
	/**
	 * Enter a parse tree produced by {@link DependenciesParser#dedetgd}.
	 * @param ctx the parse tree
	 */
	void enterDedetgd(DependenciesParser.DedetgdContext ctx);
	/**
	 * Exit a parse tree produced by {@link DependenciesParser#dedetgd}.
	 * @param ctx the parse tree
	 */
	void exitDedetgd(DependenciesParser.DedetgdContext ctx);
	/**
	 * Enter a parse tree produced by {@link DependenciesParser#dedegd}.
	 * @param ctx the parse tree
	 */
	void enterDedegd(DependenciesParser.DedegdContext ctx);
	/**
	 * Exit a parse tree produced by {@link DependenciesParser#dedegd}.
	 * @param ctx the parse tree
	 */
	void exitDedegd(DependenciesParser.DedegdContext ctx);
	/**
	 * Enter a parse tree produced by {@link DependenciesParser#dependency}.
	 * @param ctx the parse tree
	 */
	void enterDependency(DependenciesParser.DependencyContext ctx);
	/**
	 * Exit a parse tree produced by {@link DependenciesParser#dependency}.
	 * @param ctx the parse tree
	 */
	void exitDependency(DependenciesParser.DependencyContext ctx);
	/**
	 * Enter a parse tree produced by {@link DependenciesParser#ded}.
	 * @param ctx the parse tree
	 */
	void enterDed(DependenciesParser.DedContext ctx);
	/**
	 * Exit a parse tree produced by {@link DependenciesParser#ded}.
	 * @param ctx the parse tree
	 */
	void exitDed(DependenciesParser.DedContext ctx);
	/**
	 * Enter a parse tree produced by {@link DependenciesParser#dedConclusion}.
	 * @param ctx the parse tree
	 */
	void enterDedConclusion(DependenciesParser.DedConclusionContext ctx);
	/**
	 * Exit a parse tree produced by {@link DependenciesParser#dedConclusion}.
	 * @param ctx the parse tree
	 */
	void exitDedConclusion(DependenciesParser.DedConclusionContext ctx);
	/**
	 * Enter a parse tree produced by {@link DependenciesParser#positiveFormula}.
	 * @param ctx the parse tree
	 */
	void enterPositiveFormula(DependenciesParser.PositiveFormulaContext ctx);
	/**
	 * Exit a parse tree produced by {@link DependenciesParser#positiveFormula}.
	 * @param ctx the parse tree
	 */
	void exitPositiveFormula(DependenciesParser.PositiveFormulaContext ctx);
	/**
	 * Enter a parse tree produced by {@link DependenciesParser#negatedFormula}.
	 * @param ctx the parse tree
	 */
	void enterNegatedFormula(DependenciesParser.NegatedFormulaContext ctx);
	/**
	 * Exit a parse tree produced by {@link DependenciesParser#negatedFormula}.
	 * @param ctx the parse tree
	 */
	void exitNegatedFormula(DependenciesParser.NegatedFormulaContext ctx);
	/**
	 * Enter a parse tree produced by {@link DependenciesParser#conclusionFormula}.
	 * @param ctx the parse tree
	 */
	void enterConclusionFormula(DependenciesParser.ConclusionFormulaContext ctx);
	/**
	 * Exit a parse tree produced by {@link DependenciesParser#conclusionFormula}.
	 * @param ctx the parse tree
	 */
	void exitConclusionFormula(DependenciesParser.ConclusionFormulaContext ctx);
	/**
	 * Enter a parse tree produced by {@link DependenciesParser#atom}.
	 * @param ctx the parse tree
	 */
	void enterAtom(DependenciesParser.AtomContext ctx);
	/**
	 * Exit a parse tree produced by {@link DependenciesParser#atom}.
	 * @param ctx the parse tree
	 */
	void exitAtom(DependenciesParser.AtomContext ctx);
	/**
	 * Enter a parse tree produced by {@link DependenciesParser#relationalAtom}.
	 * @param ctx the parse tree
	 */
	void enterRelationalAtom(DependenciesParser.RelationalAtomContext ctx);
	/**
	 * Exit a parse tree produced by {@link DependenciesParser#relationalAtom}.
	 * @param ctx the parse tree
	 */
	void exitRelationalAtom(DependenciesParser.RelationalAtomContext ctx);
	/**
	 * Enter a parse tree produced by {@link DependenciesParser#builtin}.
	 * @param ctx the parse tree
	 */
	void enterBuiltin(DependenciesParser.BuiltinContext ctx);
	/**
	 * Exit a parse tree produced by {@link DependenciesParser#builtin}.
	 * @param ctx the parse tree
	 */
	void exitBuiltin(DependenciesParser.BuiltinContext ctx);
	/**
	 * Enter a parse tree produced by {@link DependenciesParser#comparison}.
	 * @param ctx the parse tree
	 */
	void enterComparison(DependenciesParser.ComparisonContext ctx);
	/**
	 * Exit a parse tree produced by {@link DependenciesParser#comparison}.
	 * @param ctx the parse tree
	 */
	void exitComparison(DependenciesParser.ComparisonContext ctx);
	/**
	 * Enter a parse tree produced by {@link DependenciesParser#leftargument}.
	 * @param ctx the parse tree
	 */
	void enterLeftargument(DependenciesParser.LeftargumentContext ctx);
	/**
	 * Exit a parse tree produced by {@link DependenciesParser#leftargument}.
	 * @param ctx the parse tree
	 */
	void exitLeftargument(DependenciesParser.LeftargumentContext ctx);
	/**
	 * Enter a parse tree produced by {@link DependenciesParser#rightargument}.
	 * @param ctx the parse tree
	 */
	void enterRightargument(DependenciesParser.RightargumentContext ctx);
	/**
	 * Exit a parse tree produced by {@link DependenciesParser#rightargument}.
	 * @param ctx the parse tree
	 */
	void exitRightargument(DependenciesParser.RightargumentContext ctx);
	/**
	 * Enter a parse tree produced by {@link DependenciesParser#attribute}.
	 * @param ctx the parse tree
	 */
	void enterAttribute(DependenciesParser.AttributeContext ctx);
	/**
	 * Exit a parse tree produced by {@link DependenciesParser#attribute}.
	 * @param ctx the parse tree
	 */
	void exitAttribute(DependenciesParser.AttributeContext ctx);
	/**
	 * Enter a parse tree produced by {@link DependenciesParser#value}.
	 * @param ctx the parse tree
	 */
	void enterValue(DependenciesParser.ValueContext ctx);
	/**
	 * Exit a parse tree produced by {@link DependenciesParser#value}.
	 * @param ctx the parse tree
	 */
	void exitValue(DependenciesParser.ValueContext ctx);
}