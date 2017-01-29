package it.unibas.lunatic.model.algebra.sql;

import it.unibas.lunatic.LunaticConstants;
import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.model.database.skolem.AppendSkolemPart;
import it.unibas.lunatic.model.database.skolem.ISkolemPart;
import it.unibas.lunatic.model.database.skolem.StringSkolemPart;
import it.unibas.lunatic.utility.LunaticUtility;
import it.unibas.lunatic.model.dependency.*;
import it.unibas.lunatic.model.generators.IValueGenerator;
import it.unibas.lunatic.model.generators.SkolemFunctionGenerator;
import it.unibas.lunatic.persistence.relational.LunaticDBMSUtility;
import it.unibas.lunatic.model.dependency.operators.DependencyUtility;
import it.unibas.lunatic.model.generators.FreshNullGenerator;
import it.unibas.lunatic.model.generators.operators.MainMemoryGenerateFreshNullsForStandardChase;
import it.unibas.lunatic.model.generators.operators.SQLGenerateFreshNullForStandardChase;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import speedy.SpeedyConstants;
import speedy.model.algebra.operators.sql.ExpressionToSQL;
import speedy.model.database.Attribute;
import speedy.model.database.AttributeRef;
import speedy.model.expressions.Expression;
import speedy.utility.DBMSUtility;
import speedy.utility.SpeedyUtility;

public class FormulaAttributeToSQL {

    private final static Logger logger = LoggerFactory.getLogger(FormulaAttributeToSQL.class);
    private MainMemoryGenerateFreshNullsForStandardChase mmGenerator = new MainMemoryGenerateFreshNullsForStandardChase();
    private SQLGenerateFreshNullForStandardChase sqlGenerator = new SQLGenerateFreshNullForStandardChase();
    private ExpressionToSQL sqlExpressionGenerator = new ExpressionToSQL();

    public FormulaAttributeToSQL() {
    }

    public String generateSQL(FormulaAttribute attribute, Dependency dependency, Map<FormulaVariable, IValueGenerator> generatorMap, Scenario scenario) {
        if (attribute.getValue() instanceof FormulaExpression) {
            return generateSQLFromExpression((FormulaExpression) attribute.getValue(), scenario);
        } else if (attribute.getValue() instanceof FormulaConstant) {
            FormulaConstant constant = (FormulaConstant) attribute.getValue();
            return "'" + constant.toString() + "'";
        } else if (attribute.getValue() instanceof FormulaVariableOccurrence) {
            return formulaVariableOccurrenceToSQL(attribute, dependency, generatorMap, scenario);
        }
        throw new IllegalArgumentException("Unknow type for attribute " + attribute);
    }

    private String formulaVariableOccurrenceToSQL(FormulaAttribute formulaAttribute, Dependency dependency, Map<FormulaVariable, IValueGenerator> generatorMap, Scenario scenario) {
        FormulaVariableOccurrence occurrence = (FormulaVariableOccurrence) formulaAttribute.getValue();
        FormulaVariable existentialVariable = LunaticUtility.findVariableInList(occurrence, dependency.getConclusion().getLocalVariables());
        Attribute attribute = LunaticUtility.getAttribute(occurrence.getAttributeRef(), LunaticUtility.getDatabase(occurrence.getAttributeRef(), scenario));
        String type = attribute.getType();
        if (existentialVariable != null) {
            return createGeneratorForExistentialVariable(existentialVariable, type, dependency, generatorMap, scenario).toSQLString();
        }
        FormulaVariable universalVariable = LunaticUtility.findVariableInList(occurrence, dependency.getPremise().getLocalVariables());
        FormulaVariableOccurrence sourceOccurrence = universalVariable.getPremiseRelationalOccurrences().get(0);
//        return dependency.getId() + "." + sourceOccurrence.getAttributeRef().toScriptString();
        AttributeRef attributeRef = sourceOccurrence.getAttributeRef();
        String attributeRefToSQL = LunaticDBMSUtility.attributeRefToSQL(attributeRef);
        Attribute sourceAttribute = LunaticUtility.getAttribute(attributeRef, LunaticUtility.getDatabase(attributeRef, scenario));
        Attribute targetAttribute = LunaticUtility.getAttribute(occurrence.getAttributeRef(), LunaticUtility.getDatabase(occurrence.getAttributeRef(), scenario));
        if (isCastNeeded(sourceAttribute, targetAttribute)) {
            attributeRefToSQL = "CAST(" + attributeRefToSQL + " as " + DBMSUtility.convertDataSourceTypeToDBType(targetAttribute.getType()) + ")";
        }
        return attributeRefToSQL;
    }

    private boolean isCastNeeded(Attribute sourceAttribute, Attribute targetAttribute) {
        if (SpeedyUtility.isNumeric(sourceAttribute.getType()) && SpeedyUtility.isNumeric(targetAttribute.getType())) {
            return false;
        }
        return !sourceAttribute.getType().equals(targetAttribute.getType());
    }

    private IValueGenerator createGeneratorForExistentialVariable(FormulaVariable variable, String type, Dependency dependency, Map<FormulaVariable, IValueGenerator> generatorMap, Scenario scenario) {
        IValueGenerator generatorForVariable = generatorMap.get(variable);
        if (generatorForVariable != null) {
            return generatorForVariable;
        }
        if (scenario.getConfiguration().getNullGenerationStrategy().equals(LunaticConstants.FRESH_NULL_STRATEGY)) {
            generatorForVariable = createFreshNullGenerator(variable, type, dependency);
        } else if (scenario.getConfiguration().getNullGenerationStrategy().equals(LunaticConstants.SKOLEM_STRATEGY)) {
            generatorForVariable = createSkolemGenerator(variable, type, dependency, scenario);
        } else {
            throw new IllegalArgumentException("Unknown null generation strategy " + scenario.getConfiguration().getNullGenerationStrategy());
        }
        generatorMap.put(variable, generatorForVariable);
        return generatorForVariable;
    }

    private IValueGenerator createSkolemGenerator(FormulaVariable variable, String type, Dependency dependency, Scenario scenario) {
        boolean useHash = scenario.getConfiguration().isUseHashForSkolem();
        ISkolemPart root = new AppendSkolemPart();
        String prefix = "(['||";
        String suffix = "||'])'";
        String skolemFunctionString = "";
        if (!DependencyUtility.hasUniversalVariablesInConclusion(dependency)) {
            useHash = false;
            suffix = "'])'";
        }
        if (SpeedyUtility.isBigInt(type)) {
            skolemFunctionString = "bigint_skolem(";
            suffix = suffix + ")";
            useHash = false;
        }
        if (SpeedyUtility.isDoublePrecision(type)) {
            skolemFunctionString = "double_skolem(";
            suffix = suffix + ")";
            useHash = false;
        }
        ISkolemPart name = new StringSkolemPart(skolemFunctionString + "'" + SpeedyConstants.SKOLEM_PREFIX + dependency.getId() + SpeedyConstants.SKOLEM_SEPARATOR + variable.getId());
        root.addChild(name);
        if (useHash) {
            prefix += " right(md5(";
            suffix = "),15) " + suffix;
        }
        AppendSkolemPart append = new AppendSkolemPart(prefix, suffix, "||']-['||");
        root.addChild(append);
        List<FormulaVariable> universalVariablesInConclusion = findUniversalVariablesInConclusion(dependency);
        for (FormulaVariable formulaVariable : universalVariablesInConclusion) {
            FormulaVariableOccurrence sourceOccurrence = formulaVariable.getPremiseRelationalOccurrences().get(0);
            String attributeString = LunaticDBMSUtility.attributeRefToSQL(sourceOccurrence.getAttributeRef());
            append.addChild(new StringSkolemPart(attributeString));
        }
        return new SkolemFunctionGenerator(root, type);
    }

    private IValueGenerator createFreshNullGenerator(FormulaVariable variable, String type, Dependency dependency) {
        return new FreshNullGenerator(mmGenerator, sqlGenerator, variable, dependency, type);
    }

    private List<FormulaVariable> findUniversalVariablesInConclusion(Dependency dependency) {
        List<FormulaVariable> result = new ArrayList<FormulaVariable>();
        for (FormulaVariable formulaVariable : dependency.getPremise().getLocalVariables()) {
            if (formulaVariable.getConclusionRelationalOccurrences().size() > 0) {
                result.add(formulaVariable);
            }
        }
        return result;
    }

    private String generateSQLFromExpression(FormulaExpression formulaExpression, Scenario scenario) {
        Expression expression = formulaExpression.getExpression();
        return sqlExpressionGenerator.expressionToSQL(expression, false, scenario.getSource(), scenario.getTarget());
    }

}
