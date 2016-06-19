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

import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link DependenciesCFParser}.
 */
public interface DependenciesCFListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link DependenciesCFParser#prog}.
	 * @param ctx the parse tree
	 */
	void enterProg(DependenciesCFParser.ProgContext ctx);
	/**
	 * Exit a parse tree produced by {@link DependenciesCFParser#prog}.
	 * @param ctx the parse tree
	 */
	void exitProg(DependenciesCFParser.ProgContext ctx);
	/**
	 * Enter a parse tree produced by {@link DependenciesCFParser#dependencies}.
	 * @param ctx the parse tree
	 */
	void enterDependencies(DependenciesCFParser.DependenciesContext ctx);
	/**
	 * Exit a parse tree produced by {@link DependenciesCFParser#dependencies}.
	 * @param ctx the parse tree
	 */
	void exitDependencies(DependenciesCFParser.DependenciesContext ctx);
	/**
	 * Enter a parse tree produced by {@link DependenciesCFParser#sttgd}.
	 * @param ctx the parse tree
	 */
	void enterSttgd(DependenciesCFParser.SttgdContext ctx);
	/**
	 * Exit a parse tree produced by {@link DependenciesCFParser#sttgd}.
	 * @param ctx the parse tree
	 */
	void exitSttgd(DependenciesCFParser.SttgdContext ctx);
	/**
	 * Enter a parse tree produced by {@link DependenciesCFParser#etgd}.
	 * @param ctx the parse tree
	 */
	void enterEtgd(DependenciesCFParser.EtgdContext ctx);
	/**
	 * Exit a parse tree produced by {@link DependenciesCFParser#etgd}.
	 * @param ctx the parse tree
	 */
	void exitEtgd(DependenciesCFParser.EtgdContext ctx);
	/**
	 * Enter a parse tree produced by {@link DependenciesCFParser#egd}.
	 * @param ctx the parse tree
	 */
	void enterEgd(DependenciesCFParser.EgdContext ctx);
	/**
	 * Exit a parse tree produced by {@link DependenciesCFParser#egd}.
	 * @param ctx the parse tree
	 */
	void exitEgd(DependenciesCFParser.EgdContext ctx);
	/**
	 * Enter a parse tree produced by {@link DependenciesCFParser#query}.
	 * @param ctx the parse tree
	 */
	void enterQuery(DependenciesCFParser.QueryContext ctx);
	/**
	 * Exit a parse tree produced by {@link DependenciesCFParser#query}.
	 * @param ctx the parse tree
	 */
	void exitQuery(DependenciesCFParser.QueryContext ctx);
	/**
	 * Enter a parse tree produced by {@link DependenciesCFParser#dependency}.
	 * @param ctx the parse tree
	 */
	void enterDependency(DependenciesCFParser.DependencyContext ctx);
	/**
	 * Exit a parse tree produced by {@link DependenciesCFParser#dependency}.
	 * @param ctx the parse tree
	 */
	void exitDependency(DependenciesCFParser.DependencyContext ctx);
	/**
	 * Enter a parse tree produced by {@link DependenciesCFParser#querydependency}.
	 * @param ctx the parse tree
	 */
	void enterQuerydependency(DependenciesCFParser.QuerydependencyContext ctx);
	/**
	 * Exit a parse tree produced by {@link DependenciesCFParser#querydependency}.
	 * @param ctx the parse tree
	 */
	void exitQuerydependency(DependenciesCFParser.QuerydependencyContext ctx);
	/**
	 * Enter a parse tree produced by {@link DependenciesCFParser#positiveFormula}.
	 * @param ctx the parse tree
	 */
	void enterPositiveFormula(DependenciesCFParser.PositiveFormulaContext ctx);
	/**
	 * Exit a parse tree produced by {@link DependenciesCFParser#positiveFormula}.
	 * @param ctx the parse tree
	 */
	void exitPositiveFormula(DependenciesCFParser.PositiveFormulaContext ctx);
	/**
	 * Enter a parse tree produced by {@link DependenciesCFParser#conclusionFormula}.
	 * @param ctx the parse tree
	 */
	void enterConclusionFormula(DependenciesCFParser.ConclusionFormulaContext ctx);
	/**
	 * Exit a parse tree produced by {@link DependenciesCFParser#conclusionFormula}.
	 * @param ctx the parse tree
	 */
	void exitConclusionFormula(DependenciesCFParser.ConclusionFormulaContext ctx);
	/**
	 * Enter a parse tree produced by {@link DependenciesCFParser#conclusionQueryFormula}.
	 * @param ctx the parse tree
	 */
	void enterConclusionQueryFormula(DependenciesCFParser.ConclusionQueryFormulaContext ctx);
	/**
	 * Exit a parse tree produced by {@link DependenciesCFParser#conclusionQueryFormula}.
	 * @param ctx the parse tree
	 */
	void exitConclusionQueryFormula(DependenciesCFParser.ConclusionQueryFormulaContext ctx);
	/**
	 * Enter a parse tree produced by {@link DependenciesCFParser#atom}.
	 * @param ctx the parse tree
	 */
	void enterAtom(DependenciesCFParser.AtomContext ctx);
	/**
	 * Exit a parse tree produced by {@link DependenciesCFParser#atom}.
	 * @param ctx the parse tree
	 */
	void exitAtom(DependenciesCFParser.AtomContext ctx);
	/**
	 * Enter a parse tree produced by {@link DependenciesCFParser#relationalAtom}.
	 * @param ctx the parse tree
	 */
	void enterRelationalAtom(DependenciesCFParser.RelationalAtomContext ctx);
	/**
	 * Exit a parse tree produced by {@link DependenciesCFParser#relationalAtom}.
	 * @param ctx the parse tree
	 */
	void exitRelationalAtom(DependenciesCFParser.RelationalAtomContext ctx);
	/**
	 * Enter a parse tree produced by {@link DependenciesCFParser#queryAtom}.
	 * @param ctx the parse tree
	 */
	void enterQueryAtom(DependenciesCFParser.QueryAtomContext ctx);
	/**
	 * Exit a parse tree produced by {@link DependenciesCFParser#queryAtom}.
	 * @param ctx the parse tree
	 */
	void exitQueryAtom(DependenciesCFParser.QueryAtomContext ctx);
	/**
	 * Enter a parse tree produced by {@link DependenciesCFParser#builtin}.
	 * @param ctx the parse tree
	 */
	void enterBuiltin(DependenciesCFParser.BuiltinContext ctx);
	/**
	 * Exit a parse tree produced by {@link DependenciesCFParser#builtin}.
	 * @param ctx the parse tree
	 */
	void exitBuiltin(DependenciesCFParser.BuiltinContext ctx);
	/**
	 * Enter a parse tree produced by {@link DependenciesCFParser#comparison}.
	 * @param ctx the parse tree
	 */
	void enterComparison(DependenciesCFParser.ComparisonContext ctx);
	/**
	 * Exit a parse tree produced by {@link DependenciesCFParser#comparison}.
	 * @param ctx the parse tree
	 */
	void exitComparison(DependenciesCFParser.ComparisonContext ctx);
	/**
	 * Enter a parse tree produced by {@link DependenciesCFParser#leftargument}.
	 * @param ctx the parse tree
	 */
	void enterLeftargument(DependenciesCFParser.LeftargumentContext ctx);
	/**
	 * Exit a parse tree produced by {@link DependenciesCFParser#leftargument}.
	 * @param ctx the parse tree
	 */
	void exitLeftargument(DependenciesCFParser.LeftargumentContext ctx);
	/**
	 * Enter a parse tree produced by {@link DependenciesCFParser#rightargument}.
	 * @param ctx the parse tree
	 */
	void enterRightargument(DependenciesCFParser.RightargumentContext ctx);
	/**
	 * Exit a parse tree produced by {@link DependenciesCFParser#rightargument}.
	 * @param ctx the parse tree
	 */
	void exitRightargument(DependenciesCFParser.RightargumentContext ctx);
	/**
	 * Enter a parse tree produced by {@link DependenciesCFParser#attribute}.
	 * @param ctx the parse tree
	 */
	void enterAttribute(DependenciesCFParser.AttributeContext ctx);
	/**
	 * Exit a parse tree produced by {@link DependenciesCFParser#attribute}.
	 * @param ctx the parse tree
	 */
	void exitAttribute(DependenciesCFParser.AttributeContext ctx);
	/**
	 * Enter a parse tree produced by {@link DependenciesCFParser#queryattribute}.
	 * @param ctx the parse tree
	 */
	void enterQueryattribute(DependenciesCFParser.QueryattributeContext ctx);
	/**
	 * Exit a parse tree produced by {@link DependenciesCFParser#queryattribute}.
	 * @param ctx the parse tree
	 */
	void exitQueryattribute(DependenciesCFParser.QueryattributeContext ctx);
	/**
	 * Enter a parse tree produced by {@link DependenciesCFParser#value}.
	 * @param ctx the parse tree
	 */
	void enterValue(DependenciesCFParser.ValueContext ctx);
	/**
	 * Exit a parse tree produced by {@link DependenciesCFParser#value}.
	 * @param ctx the parse tree
	 */
	void exitValue(DependenciesCFParser.ValueContext ctx);
	/**
	 * Enter a parse tree produced by {@link DependenciesCFParser#queryvalue}.
	 * @param ctx the parse tree
	 */
	void enterQueryvalue(DependenciesCFParser.QueryvalueContext ctx);
	/**
	 * Exit a parse tree produced by {@link DependenciesCFParser#queryvalue}.
	 * @param ctx the parse tree
	 */
	void exitQueryvalue(DependenciesCFParser.QueryvalueContext ctx);
}