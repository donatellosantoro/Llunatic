package it.unibas.lunatic.model.dependency.operators;

import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.exceptions.ChaseException;
import it.unibas.lunatic.exceptions.ParserException;
import it.unibas.lunatic.model.dependency.*;
import it.unibas.lunatic.utility.LunaticUtility;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import speedy.model.database.AttributeRef;
import speedy.model.expressions.Expression;

public class CheckVariablesInExpressions {

    public void checkVariables(Dependency dependency, Scenario scenario) {
        CheckFormulaVariablesVisitor visitor = new CheckFormulaVariablesVisitor();
        visitor.setScenario(scenario);
        dependency.accept(visitor);
    }
}

class CheckFormulaVariablesVisitor implements IFormulaVisitor {

    private static Logger logger = LoggerFactory.getLogger(CheckVariablesInExpressions.class);

    private Dependency dependency;
    private Scenario scenario;

    public void visitDependency(Dependency dependency) {
        this.dependency = dependency;
        dependency.getPremise().accept(this);
        dependency.getConclusion().accept(this);
    }

    public Scenario getScenario() {
        return scenario;
    }

    public void setScenario(Scenario scenario) {
        this.scenario = scenario;
    }

    public void visitPositiveFormula(PositiveFormula formula) {
        for (IFormulaAtom atom : formula.getAtoms()) {
            if ((atom instanceof QueryAtom)) {
                continue;
            }
            if (atom instanceof RelationalAtom) {
                RelationalAtom relAtom = (RelationalAtom) atom;
                for (FormulaAttribute attribute : relAtom.getAttributes()) {
                    IFormulaValue formulaValue = attribute.getValue();
                    if (formulaValue instanceof FormulaExpression) {
                        if (scenario.getConfiguration().isChaseRestricted()) {
                            throw new ChaseException("Formulas in conclusion are allowed only using unrestricted chase");
                        }
                        Expression expression = ((FormulaExpression) formulaValue).getExpression();
                        visitExpressionInRelationalAtom(expression, attribute, relAtom, formula);
                    }
                }
            } else {
                Expression expression = atom.getExpression();
                visitExpressionAtom(expression, atom, formula);
            }
        }
        if (logger.isDebugEnabled()) logger.debug("Variables found in dependency: " + dependency.getId() + "\n" + LunaticUtility.printVariablesWithOccurrences(dependency.getPremise().getLocalVariables()) + "\n" + LunaticUtility.printVariablesWithOccurrences(dependency.getConclusion().getLocalVariables()));
    }

    private void visitExpressionInRelationalAtom(Expression expression, FormulaAttribute attribute, RelationalAtom atom, PositiveFormula formula) throws ParserException {
        if (logger.isDebugEnabled()) logger.debug("Visiting expression: " + expression);
        List<String> variableIds = expression.getVariables();
        for (String variableId : variableIds) {
            FormulaVariable variable = findVariableInList(variableId, atom.getFormula().getAllVariables());
            if (variable == null) {
                variable = findVariableInList(variableId, dependency.getPremise().getLocalVariables());
                if (variable == null) {
                    throw new ParserException("Unable to find variable " + variableId + " in expression " + expression + " in formula " + formula.getId());
                }
            }
            AttributeRef attributeRef = new AttributeRef(atom.getTableAlias(), attribute.getAttributeName());
            variable.addConclusionRelationalOccurrence(new FormulaVariableOccurrence(attributeRef, variableId));
            expression.changeVariableDescription(variableId, variable);
            if (logger.isDebugEnabled()) logger.debug("Adding non relational occurrence to variable " + variable + " in atom " + atom);
        }
    }

    private void visitExpressionAtom(Expression expression, IFormulaAtom atom, PositiveFormula formula) throws ParserException {
        if (logger.isDebugEnabled()) logger.debug("Visiting expression: " + expression);
        List<String> variableIds = expression.getVariables();
        for (String variableId : variableIds) {
            FormulaVariable variable = findVariableInList(variableId, atom.getFormula().getAllVariables());
            if (variable == null) {
                variable = findVariableInList(variableId, dependency.getPremise().getLocalVariables());
                if (variable == null) {
                    throw new ParserException("Unable to find variable " + variableId + " in expression " + expression + " in formula " + formula.getId());
                }
            }
            expression.changeVariableDescription(variableId, variable);
            atom.addVariable(variable);
            variable.addNonRelationalOccurrence(atom);
            if (logger.isDebugEnabled()) logger.debug("Adding non relational occurrence to variable " + variable + " in atom " + atom);
        }
    }

    public void visitFormulaWithNegations(FormulaWithNegations formula) {
        formula.getPositiveFormula().accept(this);
        for (IFormula negatedFormula : formula.getNegatedSubFormulas()) {
            negatedFormula.accept(this);
        }
    }

    public Object getResult() {
        return null;
    }

    private FormulaVariable findVariableInList(String variableId, List<FormulaVariable> variables) {
        for (FormulaVariable variable : variables) {
            if (variable.getId().equals(variableId)) {
                return variable;
            }
        }
        return null;
    }
}
