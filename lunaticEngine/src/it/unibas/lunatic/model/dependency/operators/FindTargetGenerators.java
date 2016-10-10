package it.unibas.lunatic.model.dependency.operators;

import it.unibas.lunatic.LunaticConstants;
import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.model.database.skolem.AppendSkolemPart;
import it.unibas.lunatic.model.database.skolem.ISkolemPart;
import it.unibas.lunatic.model.database.skolem.StringSkolemPart;
import it.unibas.lunatic.model.database.skolem.SubGeneratorSkolemPart;
import it.unibas.lunatic.utility.LunaticUtility;
import speedy.model.database.AttributeRef;
import it.unibas.lunatic.model.dependency.*;
import speedy.model.expressions.Expression;
import it.unibas.lunatic.model.generators.ExpressionGenerator;
import it.unibas.lunatic.model.generators.FreshNullGenerator;
import it.unibas.lunatic.model.generators.IValueGenerator;
import it.unibas.lunatic.model.generators.SkolemFunctionGenerator;
import it.unibas.lunatic.model.generators.operators.MainMemoryGenerateFreshNullsForStandardChase;
import it.unibas.lunatic.model.generators.operators.SQLGenerateFreshNullForStandardChase;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import speedy.SpeedyConstants;
import speedy.model.database.Attribute;

public class FindTargetGenerators {

    private MainMemoryGenerateFreshNullsForStandardChase mmGenerator = new MainMemoryGenerateFreshNullsForStandardChase();
    private SQLGenerateFreshNullForStandardChase sqlGenerator = new SQLGenerateFreshNullForStandardChase();

    public FindTargetGenerators() {
    }

    public void findGenerators(Dependency dependency, Scenario scenario) {
        Map<FormulaVariable, IValueGenerator> generatorMap = new HashMap<FormulaVariable, IValueGenerator>();
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
                    generator = createGeneratorForVariable(attribute, attributeRef, dependency, generatorMap, scenario);
                }
                dependency.addTargetGenerator(attributeRef, generator);
            }
        }
    }

    private IValueGenerator createExpressionGenerator(FormulaAttribute attribute) {
        FormulaExpression expression = (FormulaExpression) attribute.getValue();
        return new ExpressionGenerator(expression.getExpression());
    }

    private IValueGenerator createExpressionGeneratorForConstant(FormulaAttribute attribute) {
        FormulaConstant constant = (FormulaConstant) attribute.getValue();
        Expression expression = new Expression(constant.toString());
        return new ExpressionGenerator(expression);
    }

    private IValueGenerator createGeneratorForVariable(FormulaAttribute attribute, AttributeRef attributeRef, Dependency dependency, Map<FormulaVariable, IValueGenerator> generatorMap, Scenario scenario) {
        FormulaVariableOccurrence occurrence = (FormulaVariableOccurrence) attribute.getValue();
        FormulaVariable existentialVariable = LunaticUtility.findVariableInList(occurrence, dependency.getConclusion().getLocalVariables());
        if (existentialVariable != null) {
            return createGeneratorForExistentialVariable(attributeRef, existentialVariable, dependency, generatorMap, scenario);
        }
        FormulaVariable universalVariable = LunaticUtility.findVariableInList(occurrence, dependency.getPremise().getLocalVariables());
        Expression expression = new Expression(universalVariable.getId());
        expression.changeVariableDescription(universalVariable.getId(), universalVariable);
        return new ExpressionGenerator(expression);
    }

    private IValueGenerator createGeneratorForExistentialVariable(AttributeRef attributeRef, FormulaVariable variable, Dependency dependency, Map<FormulaVariable, IValueGenerator> generatorMap, Scenario scenario) {
        IValueGenerator generatorForVariable = generatorMap.get(variable);
        if (generatorForVariable != null) {
            return generatorForVariable;
        }
        if (scenario.getConfiguration().getNullGenerationStrategy().equals(LunaticConstants.FRESH_NULL_STRATEGY)) {
            generatorForVariable = createFreshNullGenerator(attributeRef, variable, dependency, scenario);
        } else if (scenario.getConfiguration().getNullGenerationStrategy().equals(LunaticConstants.SKOLEM_STRATEGY)) {
            generatorForVariable = createSkolemGenerator(attributeRef, variable, dependency, scenario);
        } else {
            throw new IllegalArgumentException("Unknown null generation strategy " + scenario.getConfiguration().getNullGenerationStrategy());
        }
        generatorMap.put(variable, generatorForVariable);
        return generatorForVariable;
    }

    private IValueGenerator createSkolemGenerator(AttributeRef attributeRef, FormulaVariable variable, Dependency dependency, Scenario scenario) {
        Attribute attribute = LunaticUtility.getAttribute(attributeRef, LunaticUtility.getDatabase(attributeRef, scenario));
        String type = attribute.getType();
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
        return new SkolemFunctionGenerator(root, type);
    }

    private IValueGenerator createFreshNullGenerator(AttributeRef attributeRef, FormulaVariable variable, Dependency dependency, Scenario scenario) {
        Attribute attribute = LunaticUtility.getAttribute(attributeRef, LunaticUtility.getDatabase(attributeRef, scenario));
        String type = attribute.getType();
        return new FreshNullGenerator(mmGenerator, sqlGenerator, variable, dependency, type);
    }
}
