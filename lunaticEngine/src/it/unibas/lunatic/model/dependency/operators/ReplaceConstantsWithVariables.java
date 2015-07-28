package it.unibas.lunatic.model.dependency.operators;

import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.model.chase.chasemc.operators.ICreateTablesForConstants;
import it.unibas.lunatic.model.chase.chasemc.operators.dbms.SQLCreateTablesForConstants;
import it.unibas.lunatic.model.chase.chasemc.operators.mainmemory.MainMemoryCreateTableForConstants;
import it.unibas.lunatic.model.database.AttributeRef;
import it.unibas.lunatic.model.dependency.ComparisonAtom;
import it.unibas.lunatic.model.dependency.ConstantInFormula;
import it.unibas.lunatic.model.dependency.ConstantsInFormula;
import it.unibas.lunatic.model.dependency.Dependency;
import it.unibas.lunatic.model.dependency.FormulaAttribute;
import it.unibas.lunatic.model.dependency.FormulaVariable;
import it.unibas.lunatic.model.dependency.FormulaVariableOccurrence;
import it.unibas.lunatic.model.dependency.IFormulaAtom;
import it.unibas.lunatic.model.dependency.PositiveFormula;
import it.unibas.lunatic.model.dependency.RelationalAtom;
import it.unibas.lunatic.model.expressions.Expression;
import it.unibas.lunatic.utility.DependencyUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReplaceConstantsWithVariables {

    private static final Logger logger = LoggerFactory.getLogger(ReplaceConstantsWithVariables.class.getName());

    private ICreateTablesForConstants tableCreator;

    private void initTableCreator(Scenario scenario) {
        if (scenario.isMainMemory()) {
            this.tableCreator = new MainMemoryCreateTableForConstants();
        } else {
            this.tableCreator = new SQLCreateTablesForConstants();
        }
    }

    public void replaceConstants(Dependency dependency, Scenario scenario) {
        initTableCreator(scenario);
        if (logger.isDebugEnabled()) logger.debug("Before constant removal: " + dependency);
        ConstantsInFormula constantsInFormula = new ConstantsInFormula(dependency);
        findAndReplaceConstantsInPositiveFormula(dependency.getPremise().getPositiveFormula(), constantsInFormula, true);
        findAndReplaceConstantsInPositiveFormula(dependency.getConclusion().getPositiveFormula(), constantsInFormula, false);
        if (constantsInFormula.isEmpty()) {
            return;
        }
        addAtomAndVariables(dependency, constantsInFormula);
        createTable(constantsInFormula, scenario);
        if (logger.isDebugEnabled()) logger.debug("After constant removal: " + dependency.toLongString());
        if (logger.isDebugEnabled()) logger.debug("Constant Table: " + constantsInFormula.toString());
        return;
    }

    private void findAndReplaceConstantsInPositiveFormula(PositiveFormula positiveFormula, ConstantsInFormula constantsInFormula, boolean premise) {
        for (IFormulaAtom atom : positiveFormula.getAtoms()) {
            if (atom instanceof RelationalAtom) {
                handleRelationalAtom(atom, constantsInFormula, premise);
            }
            if (atom instanceof ComparisonAtom) {
                handleComparisonAtom(atom, constantsInFormula);
            }
        }
    }

    private void handleRelationalAtom(IFormulaAtom atom, ConstantsInFormula constantsInFormula, boolean premise) {
        RelationalAtom relationalAtom = (RelationalAtom) atom;
        for (FormulaAttribute attribute : relationalAtom.getAttributes()) {
            if (attribute.getValue().isVariable() || attribute.getValue().isNull()) {
                continue;
            }
            Object constantValue = createConstantValue(attribute.getValue());
            ConstantInFormula constantInFormula = getConstantInFormula(constantValue, constantsInFormula);
            AttributeRef attributeRef = new AttributeRef(relationalAtom.getTableAlias(), attribute.getAttributeName());
            if (premise) {
                constantInFormula.addPremiseRelationalOccurrence(attributeRef);
            } else {
                constantInFormula.addConclusionRelationalOccurrence(attributeRef);
            }
            attribute.setValue(new FormulaVariableOccurrence(attributeRef, constantInFormula.getVariableId()));
        }
    }

    private void handleComparisonAtom(IFormulaAtom atom, ConstantsInFormula constantsInFormula) {
        ComparisonAtom comparisonAtom = (ComparisonAtom) atom;
        if (comparisonAtom.getVariables().size() == 2) {
            return;
        }
        Object originalConstantValue = (comparisonAtom.getLeftConstant() != null ? comparisonAtom.getLeftConstant() : comparisonAtom.getRightConstant());
        Object constantValue = createConstantValue(originalConstantValue);
        ConstantInFormula constantInFormula = getConstantInFormula(constantValue, constantsInFormula);
        if (comparisonAtom.getLeftConstant() != null) {
            comparisonAtom.setLeftConstant(null);
            comparisonAtom.getVariables().add(0, constantInFormula.getFormulaVariable());
        } else {
            comparisonAtom.setRightConstant(null);
            comparisonAtom.addVariable(constantInFormula.getFormulaVariable());
        }
        constantInFormula.getFormulaVariable().addNonRelationalOccurrence(comparisonAtom);
        fixExpression(comparisonAtom, originalConstantValue, constantInFormula.getFormulaVariable());
    }

    private Object createConstantValue(Object value) {
        String valueString = value.toString().replaceAll("\"", "");
        return valueString;
    }

    private ConstantInFormula getConstantInFormula(Object constantValue, ConstantsInFormula constantsInFormula) {
        ConstantInFormula constantInFormula = constantsInFormula.getConstantMap().get(constantValue.toString());
        if (constantInFormula == null) {
            constantInFormula = new ConstantInFormula(constantValue);
            constantsInFormula.getConstantMap().put(constantValue.toString(), constantInFormula);
        }
        return constantInFormula;
    }

    private void fixExpression(ComparisonAtom comparisonAtom, Object constantValue, FormulaVariable formulaVariable) {
        String expressionWithDelimiters = comparisonAtom.getExpression().toVariableDelimitedString();
        String newExpressionString = expressionWithDelimiters.replaceAll("§" + constantValue + "#", formulaVariable.getId());
        newExpressionString = newExpressionString.replaceAll("§", "");
        newExpressionString = newExpressionString.replaceAll("#", "");
        Expression newExpression = new Expression(newExpressionString);
        comparisonAtom.setExpression(newExpression);
        for (FormulaVariable variableInAtom : comparisonAtom.getVariables()) {
            newExpression.setVariableDescription(variableInAtom.getId(), variableInAtom);
        }
    }

    @SuppressWarnings("unchecked")
    private void addAtomAndVariables(Dependency dependency, ConstantsInFormula constantsInFormula) {
        PositiveFormula premise = dependency.getPremise().getPositiveFormula();
        RelationalAtom newAtom = new RelationalAtom(DependencyUtility.buildTableNameForConstants(dependency));
        newAtom.getTableAlias().setSource(true);
        newAtom.getTableAlias().setAuthoritative(true);
        newAtom.setFormula(premise);
        premise.getAtoms().add(0, newAtom);
        for (String constantValue : constantsInFormula.getOrderedKeys()) {
            ConstantInFormula constantInFormula = constantsInFormula.getConstantMap().get(constantValue);
            FormulaVariable variable = constantInFormula.getFormulaVariable();
            FormulaAttribute attribute = new FormulaAttribute(DependencyUtility.buildAttributeNameForConstant(constantValue));
            AttributeRef attributeRef = new AttributeRef(newAtom.getTableAlias(), DependencyUtility.buildAttributeNameForConstant(constantValue));
            FormulaVariableOccurrence occurrence = new FormulaVariableOccurrence(attributeRef, variable.getId());
            attribute.setValue(occurrence);
            newAtom.addAttribute(attribute);
            variable.addPremiseRelationalOccurrence(occurrence);
            premise.getLocalVariables().add(variable);
        }
    }

    private void createTable(ConstantsInFormula constantsInFormula, Scenario scenario) {
        this.tableCreator.createTable(constantsInFormula, scenario);
    }
}
