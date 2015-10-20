package it.unibas.lunatic.model.algebra.sql;

import it.unibas.lunatic.LunaticConstants;
import it.unibas.lunatic.model.database.skolem.AppendSkolemPart;
import it.unibas.lunatic.model.database.skolem.ISkolemPart;
import it.unibas.lunatic.model.database.skolem.StringSkolemPart;
import it.unibas.lunatic.utility.LunaticUtility;
import it.unibas.lunatic.model.dependency.*;
import it.unibas.lunatic.model.generators.IValueGenerator;
import it.unibas.lunatic.model.generators.SkolemFunctionGenerator;
import it.unibas.lunatic.persistence.relational.DBMSUtility;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import speedy.SpeedyConstants;

public class FormulaAttributeToSQL {

    public String generateSQL(FormulaAttribute attribute, Dependency dependency, Map<FormulaVariable, SkolemFunctionGenerator> skolems) {
        if (attribute.getValue() instanceof FormulaExpression) {
            throw new UnsupportedOperationException("Target expressions are not supported yet in SQL script");
        } else if (attribute.getValue() instanceof FormulaConstant) {
            FormulaConstant constant = (FormulaConstant) attribute.getValue();
            return "'" + constant.toString() + "'";
        } else if (attribute.getValue() instanceof FormulaVariableOccurrence) {
            return formulaVariableOccurrenceToSQL(attribute, dependency, skolems);
        }
        throw new IllegalArgumentException("Unknow type for attribute " + attribute);
    }

    private String formulaVariableOccurrenceToSQL(FormulaAttribute attribute, Dependency dependency, Map<FormulaVariable, SkolemFunctionGenerator> skolems) {
        FormulaVariableOccurrence occurrence = (FormulaVariableOccurrence) attribute.getValue();
        FormulaVariable existentialVariable = LunaticUtility.findVariableInList(occurrence, dependency.getConclusion().getLocalVariables());
        if (existentialVariable != null) {
            return createSkolemGenerator(existentialVariable, dependency, skolems).toString();
        }
        FormulaVariable universalVariable = LunaticUtility.findVariableInList(occurrence, dependency.getPremise().getLocalVariables());
        FormulaVariableOccurrence sourceOccurrence = universalVariable.getPremiseRelationalOccurrences().get(0);
//        return dependency.getId() + "." + sourceOccurrence.getAttributeRef().toScriptString();
        return DBMSUtility.attributeRefToSQL(sourceOccurrence.getAttributeRef());
    }

    private IValueGenerator createSkolemGenerator(FormulaVariable variable, Dependency dependency, Map<FormulaVariable, SkolemFunctionGenerator> skolems) {
        SkolemFunctionGenerator generatorForVariable = skolems.get(variable);
        if (generatorForVariable != null) {
            return generatorForVariable;
        }
        ISkolemPart root = new AppendSkolemPart();
        ISkolemPart name = new StringSkolemPart("'" + SpeedyConstants.SKOLEM_PREFIX + dependency.getId() + SpeedyConstants.SKOLEM_SEPARATOR + variable.getId());
        root.addChild(name);
        AppendSkolemPart append = new AppendSkolemPart("(['||", "||'])'", "||']-['||");
        root.addChild(append);
        List<FormulaVariable> universalVariablesInConclusion = findUniversalVariablesInConclusion(dependency);
        for (FormulaVariable formulaVariable : universalVariablesInConclusion) {
            FormulaVariableOccurrence sourceOccurrence = formulaVariable.getPremiseRelationalOccurrences().get(0);
//            String attributeString = dependency.getId() + "." + sourceOccurrence.getAttributeRef().toScriptString();
            String attributeString = DBMSUtility.attributeRefToSQL(sourceOccurrence.getAttributeRef());
            append.addChild(new StringSkolemPart(attributeString));
        }
        generatorForVariable = new SkolemFunctionGenerator(root);
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
