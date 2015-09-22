package it.unibas.lunatic.model.dependency.operators;

import it.unibas.lunatic.LunaticConstants;
import it.unibas.lunatic.model.database.skolem.AppendSkolemPart;
import it.unibas.lunatic.model.database.skolem.ISkolemPart;
import it.unibas.lunatic.model.database.skolem.StringSkolemPart;
import it.unibas.lunatic.model.database.skolem.SubGeneratorSkolemPart;
import it.unibas.lunatic.utility.LunaticUtility;
import speedy.model.database.AttributeRef;
import it.unibas.lunatic.model.dependency.*;
import speedy.model.expressions.Expression;
import it.unibas.lunatic.model.generators.ExpressionGenerator;
import it.unibas.lunatic.model.generators.IValueGenerator;
import it.unibas.lunatic.model.generators.SkolemFunctionGenerator;
import it.unibas.lunatic.utility.DependencyUtility;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import speedy.SpeedyConstants;

public class FindTargetGenerators {

    public void findGenerators(Dependency dependency) {
        Map<FormulaVariable, SkolemFunctionGenerator> skolems = new HashMap<FormulaVariable, SkolemFunctionGenerator>();
        for (IFormulaAtom atom : dependency.getConclusion().getPositiveFormula().getAtoms()) {
            RelationalAtom relationalAtom = (RelationalAtom) atom;
            for (FormulaAttribute attribute : relationalAtom.getAttributes()) {
                AttributeRef attributeRef = new AttributeRef(relationalAtom.getTableAlias(), attribute.getAttributeName());
                IValueGenerator generator = null;
                if (attribute.getValue() instanceof FormulaExpression) {
                    generator = createExpressionGenerator(attribute);
                } else if (attribute.getValue() instanceof FormulaConstant) {
                    generator = createExpressionGeneratorForConstant(attribute);
                } else if (attribute.getValue() instanceof FormulaVariableOccurrence) {
                    generator = createGeneratorForVariable(attribute, dependency, skolems);
                }
                dependency.addTargetGenerator(attributeRef, generator);
            }
        }
    }

    private IValueGenerator createExpressionGenerator(FormulaAttribute attribute) {
        FormulaExpression expression = (FormulaExpression) attribute.getValue();
        ExpressionGenerator generator = new ExpressionGenerator(expression.getExpression());
        return generator;
    }

    private IValueGenerator createExpressionGeneratorForConstant(FormulaAttribute attribute) {
        FormulaConstant constant = (FormulaConstant) attribute.getValue();
        Expression expression = new Expression(constant.toString());
        ExpressionGenerator generator = new ExpressionGenerator(expression);
        return generator;
    }

    private IValueGenerator createGeneratorForVariable(FormulaAttribute attribute, Dependency dependency, Map<FormulaVariable, SkolemFunctionGenerator> skolems) {
        FormulaVariableOccurrence occurrence = (FormulaVariableOccurrence) attribute.getValue();
        FormulaVariable existentialVariable = LunaticUtility.findVariableInList(occurrence, dependency.getConclusion().getLocalVariables());
        if (existentialVariable != null) {
            return createSkolemGenerator(attribute, existentialVariable, dependency, skolems);
        }
        FormulaVariable universalVariable = LunaticUtility.findVariableInList(occurrence, dependency.getPremise().getLocalVariables());
        Expression expression = new Expression(universalVariable.getId());
        expression.changeVariableDescription(universalVariable.getId(), universalVariable);
        ExpressionGenerator generator = new ExpressionGenerator(expression);
        return generator;
    }

    private IValueGenerator createSkolemGenerator(FormulaAttribute attribute, FormulaVariable variable, Dependency dependency, Map<FormulaVariable, SkolemFunctionGenerator> skolems) {
        SkolemFunctionGenerator generatorForVariable = skolems.get(variable);
        if (generatorForVariable != null) {
            return generatorForVariable;
        }
        ISkolemPart root = new AppendSkolemPart();
        ISkolemPart name = new StringSkolemPart(SpeedyConstants.SKOLEM_PREFIX + dependency.getId() + SpeedyConstants.SKOLEM_SEPARATOR + variable.getId());
        root.addChild(name);
        AppendSkolemPart append = new AppendSkolemPart("(", ")", ";");
        root.addChild(append);
        List<FormulaVariable> universalVariablesInConclusion = DependencyUtility.findUniversalVariablesInConclusion(dependency);
        for (FormulaVariable formulaVariable : universalVariablesInConclusion) {
            Expression expression = new Expression(formulaVariable.getId());
            expression.changeVariableDescription(formulaVariable.getId(), formulaVariable);
            append.addChild(new SubGeneratorSkolemPart(new ExpressionGenerator(expression)));
        }
        generatorForVariable = new SkolemFunctionGenerator(root);
        skolems.put(variable, generatorForVariable);
        return generatorForVariable;
    }
}
