package it.unibas.lunatic.model.dependency.operators;

import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.model.chase.chasemc.operators.ICreateTablesForConstants;
import it.unibas.lunatic.model.chase.chasemc.operators.dbms.SQLCreateTablesForConstants;
import it.unibas.lunatic.model.chase.chasemc.operators.mainmemory.MainMemoryCreateTableForConstants;
import speedy.model.database.AttributeRef;
import it.unibas.lunatic.model.dependency.ComparisonAtom;
import it.unibas.lunatic.model.dependency.ConstantInFormula;
import it.unibas.lunatic.model.dependency.AllConstantsInFormula;
import it.unibas.lunatic.model.dependency.Dependency;
import it.unibas.lunatic.model.dependency.FormulaAttribute;
import it.unibas.lunatic.model.dependency.FormulaVariable;
import it.unibas.lunatic.model.dependency.FormulaVariableOccurrence;
import it.unibas.lunatic.model.dependency.IFormulaAtom;
import it.unibas.lunatic.model.dependency.PositiveFormula;
import it.unibas.lunatic.model.dependency.RelationalAtom;
import speedy.model.expressions.Expression;
import it.unibas.lunatic.utility.LunaticUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import speedy.persistence.Types;

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
        AllConstantsInFormula constantsInFormula = new AllConstantsInFormula(dependency);
        findAndReplaceConstantsInPositiveFormula(dependency.getPremise().getPositiveFormula(), constantsInFormula, true);
        findAndReplaceConstantsInPositiveFormula(dependency.getConclusion().getPositiveFormula(), constantsInFormula, false);
        if (constantsInFormula.isEmpty()) {
            return;
        }
        addJoinAttribute(constantsInFormula);
        addAtomsAndVariables(dependency, constantsInFormula);
        if (logger.isDebugEnabled()) logger.debug("Constant Table: " + constantsInFormula.toString());
        if (logger.isDebugEnabled()) logger.debug("After constant removal: " + dependency.toLongString());
        createTable(constantsInFormula, scenario, true);
//        for (FormulaVariable variable : dependency.getPremise().getLocalVariables()) {
//            logger.info(variable.toLongString());
//        }
    }

    private void findAndReplaceConstantsInPositiveFormula(PositiveFormula positiveFormula, AllConstantsInFormula constantsInFormula, boolean premise) {
        for (IFormulaAtom atom : positiveFormula.getAtoms()) {
            if (atom instanceof RelationalAtom) {
                handleRelationalAtom(atom, constantsInFormula, premise);
            }
            if (atom instanceof ComparisonAtom) {
                handleComparisonAtom(atom, constantsInFormula, premise);
            }
        }
    }

    private void handleRelationalAtom(IFormulaAtom atom, AllConstantsInFormula constantsInFormula, boolean premise) {
        RelationalAtom relationalAtom = (RelationalAtom) atom;
        for (FormulaAttribute attribute : relationalAtom.getAttributes()) {
            if (attribute.getValue().isVariable() || attribute.getValue().isNull()) {
                continue;
            }
            ConstantWithType constant = createConstantValue(attribute.getValue());
            ConstantInFormula constantInFormula = getConstantInFormula(constant, constantsInFormula, premise);
            AttributeRef attributeRef = new AttributeRef(relationalAtom.getTableAlias(), attribute.getAttributeName());
            if (premise) {
                constantInFormula.addPremiseRelationalOccurrence(attributeRef);
            } else {
                constantInFormula.addConclusionRelationalOccurrence(attributeRef);
            }
            attribute.setValue(new FormulaVariableOccurrence(attributeRef, constantInFormula.getVariableId()));
        }
    }

    private void handleComparisonAtom(IFormulaAtom atom, AllConstantsInFormula constantsInFormula, boolean premise) {
        ComparisonAtom comparisonAtom = (ComparisonAtom) atom;
        if (comparisonAtom.getVariables().size() == 2) {
            return;
        }
        Object originalConstantValue = (comparisonAtom.getLeftConstant() != null ? comparisonAtom.getLeftConstant() : comparisonAtom.getRightConstant());
        ConstantWithType constant = createConstantValue(originalConstantValue);
        ConstantInFormula constantInFormula = getConstantInFormula(constant, constantsInFormula, premise);
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

    private ConstantWithType createConstantValue(Object value) {
        if (value.toString().startsWith("\"")) {
            String valueString = value.toString().replaceAll("\"", "");
            String type = Types.STRING;
            return new ConstantWithType(valueString, type);
        }
        String valueString = value.toString();
        String type = LunaticUtility.findType(valueString);
        return new ConstantWithType(valueString, type);
    }

    private ConstantInFormula getConstantInFormula(ConstantWithType constant, AllConstantsInFormula constantsInFormula, boolean premise) {
        ConstantInFormula constantInFormula = constantsInFormula.getConstantMap().get(getKey(constant.value, premise));
        if (constantInFormula == null) {
            constantInFormula = new ConstantInFormula(constant.value, constant.type, premise);
            constantsInFormula.getConstantMap().put(getKey(constant.value, premise), constantInFormula);
        }
        return constantInFormula;
    }

    private String getKey(Object constantValue, boolean premise) {
        return constantValue.toString() + "-" + premise;
    }

    private void fixExpression(ComparisonAtom comparisonAtom, Object constantValue, FormulaVariable formulaVariable) {
        String expressionWithDelimiters = comparisonAtom.getExpression().toVariableDelimitedString();
//        String newExpressionString = expressionWithDelimiters.replaceAll("§" + constantValue + "#", formulaVariable.getId());
        String newExpressionString = expressionWithDelimiters.replace("§" + constantValue + "#", formulaVariable.getId());
//        newExpressionString = newExpressionString.replaceAll("§", "");
        newExpressionString = newExpressionString.replace("§", "");
//        newExpressionString = newExpressionString.replaceAll("#", "");
        newExpressionString = newExpressionString.replace("#", "");
        Expression newExpression = new Expression(newExpressionString);
        comparisonAtom.setExpression(newExpression);
        for (FormulaVariable variableInAtom : comparisonAtom.getVariables()) {
            newExpression.setVariableDescription(variableInAtom.getId(), variableInAtom);
        }
    }

    private void addJoinAttribute(AllConstantsInFormula constantsInFormula) {
        ConstantInFormula premiseConstant = new ConstantInFormula("joinv", Types.STRING, true);
        constantsInFormula.getConstantMap().put(getKey("joinv", true), premiseConstant);
        ConstantInFormula conclusionConstant = new ConstantInFormula("joinv", Types.STRING, false);
        constantsInFormula.getConstantMap().put(getKey("joinv", false), conclusionConstant);
        conclusionConstant.setFormulaVariable(premiseConstant.getFormulaVariable());
    }

    @SuppressWarnings("unchecked")
    private void addAtomsAndVariables(Dependency dependency, AllConstantsInFormula constantsInFormula) {
        // Premise constants atom
        PositiveFormula premise = dependency.getPremise().getPositiveFormula();
        RelationalAtom newAtomForConstantsInPremise = new RelationalAtom(DependencyUtility.buildTableNameForConstants(dependency, true));
        newAtomForConstantsInPremise.getTableAlias().setSource(true);
        newAtomForConstantsInPremise.setFormula(premise);
        premise.getAtoms().add(0, newAtomForConstantsInPremise);
        // Conclusion constants atom
        RelationalAtom newAtomForConstantsInConclusion = new RelationalAtom(DependencyUtility.buildTableNameForConstants(dependency, false));
        newAtomForConstantsInConclusion.getTableAlias().setSource(true);
        newAtomForConstantsInConclusion.getTableAlias().setAuthoritative(true);
        newAtomForConstantsInConclusion.setFormula(premise);
        premise.getAtoms().add(1, newAtomForConstantsInConclusion);
        // Variables
        for (String constantKey : constantsInFormula.getOrderedKeys()) {
            ConstantInFormula constantInFormula = constantsInFormula.getConstantMap().get(constantKey);
            FormulaVariable variable = constantInFormula.getFormulaVariable();
            String constantValue = constantInFormula.getConstantValue().toString();
            FormulaAttribute attribute = new FormulaAttribute(DependencyUtility.buildAttributeNameForConstant(constantValue));
            AttributeRef attributeRef;
            if (constantInFormula.isPremise()) {
                attributeRef = new AttributeRef(newAtomForConstantsInPremise.getTableAlias(), DependencyUtility.buildAttributeNameForConstant(constantValue));
                newAtomForConstantsInPremise.addAttribute(attribute);
            } else {
                attributeRef = new AttributeRef(newAtomForConstantsInConclusion.getTableAlias(), DependencyUtility.buildAttributeNameForConstant(constantValue));
                newAtomForConstantsInConclusion.addAttribute(attribute);
            }
            FormulaVariableOccurrence occurrence = new FormulaVariableOccurrence(attributeRef, variable.getId());
            attribute.setValue(occurrence);
            variable.addPremiseRelationalOccurrence(occurrence);
            if (!premise.getLocalVariables().contains(variable)) {
                premise.getLocalVariables().add(variable);
            }
        }
    }

    private void createTable(AllConstantsInFormula constantsInFormula, Scenario scenario, boolean autoritative) {
        this.tableCreator.createTable(constantsInFormula, scenario, autoritative);
    }

}

class ConstantWithType {

    Object value;
    String type;

    public ConstantWithType(Object value, String type) {
        this.value = value;
        this.type = type;
    }

}
