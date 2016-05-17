package it.unibas.lunatic.model.algebra.sql;

import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.model.database.skolem.AppendSkolemPart;
import it.unibas.lunatic.model.database.skolem.ISkolemPart;
import it.unibas.lunatic.model.database.skolem.StringSkolemPart;
import it.unibas.lunatic.utility.LunaticUtility;
import it.unibas.lunatic.model.dependency.*;
import it.unibas.lunatic.model.generators.IValueGenerator;
import it.unibas.lunatic.model.generators.SkolemFunctionGenerator;
import it.unibas.lunatic.persistence.relational.LunaticDBMSUtility;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import speedy.SpeedyConstants;
import speedy.model.database.Attribute;
import speedy.model.database.AttributeRef;
import speedy.persistence.Types;
import speedy.utility.DBMSUtility;
import speedy.utility.SpeedyUtility;

public class FormulaAttributeToSQL {

    public String generateSQL(FormulaAttribute attribute, Dependency dependency, Map<FormulaVariable, SkolemFunctionGenerator> skolems, Scenario scenario) {
        if (attribute.getValue() instanceof FormulaExpression) {
            throw new UnsupportedOperationException("Target expressions are not supported yet in SQL script");
        } else if (attribute.getValue() instanceof FormulaConstant) {
            FormulaConstant constant = (FormulaConstant) attribute.getValue();
            return "'" + constant.toString() + "'";
        } else if (attribute.getValue() instanceof FormulaVariableOccurrence) {
            return formulaVariableOccurrenceToSQL(attribute, dependency, skolems, scenario);
        }
        throw new IllegalArgumentException("Unknow type for attribute " + attribute);
    }

    private String formulaVariableOccurrenceToSQL(FormulaAttribute formulaAttribute, Dependency dependency, Map<FormulaVariable, SkolemFunctionGenerator> skolems, Scenario scenario) {
        FormulaVariableOccurrence occurrence = (FormulaVariableOccurrence) formulaAttribute.getValue();
        FormulaVariable existentialVariable = LunaticUtility.findVariableInList(occurrence, dependency.getConclusion().getLocalVariables());
        Attribute attribute = LunaticUtility.getAttribute(occurrence.getAttributeRef(), LunaticUtility.getDatabase(occurrence.getAttributeRef(), scenario));
        String type = attribute.getType();
        if (existentialVariable != null) {
            return createSkolemGenerator(existentialVariable, type, dependency, skolems, scenario).toString();
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

    private IValueGenerator createSkolemGenerator(FormulaVariable variable, String type, Dependency dependency, Map<FormulaVariable, SkolemFunctionGenerator> skolems, Scenario scenario) {
        boolean useHash = scenario.getConfiguration().isUseHashForSkolem();
        SkolemFunctionGenerator generatorForVariable = skolems.get(variable);
        if (generatorForVariable != null) {
            return generatorForVariable;
        }
        ISkolemPart root = new AppendSkolemPart();
        String prefix = "(['||";
        String suffix = "||'])'";
        String skolemFunctionString = "";
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
//            String attributeString = dependency.getId() + "." + sourceOccurrence.getAttributeRef().toScriptString();
            String attributeString = LunaticDBMSUtility.attributeRefToSQL(sourceOccurrence.getAttributeRef());
            append.addChild(new StringSkolemPart(attributeString));
        }
        generatorForVariable = new SkolemFunctionGenerator(root, type);
        skolems.put(variable, generatorForVariable);
        return generatorForVariable;
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

}
