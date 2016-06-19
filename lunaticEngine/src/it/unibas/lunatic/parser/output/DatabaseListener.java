// Generated from Database.g4 by ANTLR 4.5.3

package it.unibas.lunatic.parser.output;

import speedy.model.expressions.Expression;
import it.unibas.lunatic.parser.operators.ParseDatabase;
import it.unibas.lunatic.parser.*;
import java.util.Stack;

import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link DatabaseParser}.
 */
public interface DatabaseListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link DatabaseParser#prog}.
	 * @param ctx the parse tree
	 */
	void enterProg(DatabaseParser.ProgContext ctx);
	/**
	 * Exit a parse tree produced by {@link DatabaseParser#prog}.
	 * @param ctx the parse tree
	 */
	void exitProg(DatabaseParser.ProgContext ctx);
	/**
	 * Enter a parse tree produced by {@link DatabaseParser#database}.
	 * @param ctx the parse tree
	 */
	void enterDatabase(DatabaseParser.DatabaseContext ctx);
	/**
	 * Exit a parse tree produced by {@link DatabaseParser#database}.
	 * @param ctx the parse tree
	 */
	void exitDatabase(DatabaseParser.DatabaseContext ctx);
	/**
	 * Enter a parse tree produced by {@link DatabaseParser#schema}.
	 * @param ctx the parse tree
	 */
	void enterSchema(DatabaseParser.SchemaContext ctx);
	/**
	 * Exit a parse tree produced by {@link DatabaseParser#schema}.
	 * @param ctx the parse tree
	 */
	void exitSchema(DatabaseParser.SchemaContext ctx);
	/**
	 * Enter a parse tree produced by {@link DatabaseParser#relation}.
	 * @param ctx the parse tree
	 */
	void enterRelation(DatabaseParser.RelationContext ctx);
	/**
	 * Exit a parse tree produced by {@link DatabaseParser#relation}.
	 * @param ctx the parse tree
	 */
	void exitRelation(DatabaseParser.RelationContext ctx);
	/**
	 * Enter a parse tree produced by {@link DatabaseParser#attrName}.
	 * @param ctx the parse tree
	 */
	void enterAttrName(DatabaseParser.AttrNameContext ctx);
	/**
	 * Exit a parse tree produced by {@link DatabaseParser#attrName}.
	 * @param ctx the parse tree
	 */
	void exitAttrName(DatabaseParser.AttrNameContext ctx);
	/**
	 * Enter a parse tree produced by {@link DatabaseParser#instance}.
	 * @param ctx the parse tree
	 */
	void enterInstance(DatabaseParser.InstanceContext ctx);
	/**
	 * Exit a parse tree produced by {@link DatabaseParser#instance}.
	 * @param ctx the parse tree
	 */
	void exitInstance(DatabaseParser.InstanceContext ctx);
	/**
	 * Enter a parse tree produced by {@link DatabaseParser#fact}.
	 * @param ctx the parse tree
	 */
	void enterFact(DatabaseParser.FactContext ctx);
	/**
	 * Exit a parse tree produced by {@link DatabaseParser#fact}.
	 * @param ctx the parse tree
	 */
	void exitFact(DatabaseParser.FactContext ctx);
	/**
	 * Enter a parse tree produced by {@link DatabaseParser#attrValue}.
	 * @param ctx the parse tree
	 */
	void enterAttrValue(DatabaseParser.AttrValueContext ctx);
	/**
	 * Exit a parse tree produced by {@link DatabaseParser#attrValue}.
	 * @param ctx the parse tree
	 */
	void exitAttrValue(DatabaseParser.AttrValueContext ctx);
}