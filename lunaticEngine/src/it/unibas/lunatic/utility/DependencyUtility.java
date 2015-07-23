package it.unibas.lunatic.utility;

import it.unibas.lunatic.model.algebra.operators.AlgebraUtility;
import it.unibas.lunatic.model.chase.commons.ChaseUtility;
import it.unibas.lunatic.model.database.AttributeRef;
import it.unibas.lunatic.model.database.IValue;
import it.unibas.lunatic.model.database.TableAlias;
import it.unibas.lunatic.model.database.Tuple;
import it.unibas.lunatic.model.dependency.Dependency;
import it.unibas.lunatic.model.dependency.FormulaVariable;
import it.unibas.lunatic.model.dependency.FormulaVariableOccurrence;
import it.unibas.lunatic.model.dependency.IFormula;
import it.unibas.lunatic.model.dependency.VariableEquivalenceClass;
import it.unibas.lunatic.model.dependency.operators.FindSourceAtoms;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DependencyUtility {

    private static Logger logger = LoggerFactory.getLogger(DependencyUtility.class);

    public static List<AttributeRef> getFirstAttributesOfUniversalVariablesInConclusion(Dependency dependency) {
        List<AttributeRef> result = new ArrayList<AttributeRef>();
        List<FormulaVariable> universalVariable = DependencyUtility.getUniversalVariablesInConclusion(dependency);
        for (FormulaVariable formulaVariable : universalVariable) {
            AttributeRef firstAttribute = DependencyUtility.findFirstOccurrenceInFormula(dependency.getPremise(), formulaVariable.getPremiseRelationalOccurrences());
//            LunaticUtility.addIfNotContained(result, firstAttribute);
            result.add(firstAttribute);
        }
        return result;
    }

    public static List<FormulaVariable> getUniversalVariablesInConclusion(Dependency dependency) {
        List<FormulaVariable> result = new ArrayList<FormulaVariable>();
        for (FormulaVariable formulaVariable : dependency.getPremise().getLocalVariables()) {
            List<FormulaVariableOccurrence> variablesOccurrence = formulaVariable.getConclusionRelationalOccurrences();
            if (!variablesOccurrence.isEmpty()) {
                LunaticUtility.addIfNotContained(result, formulaVariable);
            }
        }
        return result;
    }

    public static List<FormulaVariable> findUniversalVariablesInConclusion(Dependency dependency) {
        List<FormulaVariable> result = new ArrayList<FormulaVariable>();
        for (FormulaVariable formulaVariable : dependency.getPremise().getLocalVariables()) {
            if (formulaVariable.getConclusionRelationalOccurrences().size() > 0) {
                result.add(formulaVariable);
            }
        }
        return result;
    }

    public static List<IValue> extractUniversalValuesInConclusion(Tuple tuple, Dependency extTGD) {
        List<IValue> result = new ArrayList<IValue>();
        List<AttributeRef> universalAttribute = getFirstAttributesOfUniversalVariablesInConclusion(extTGD);
        for (AttributeRef attributeRef : universalAttribute) {
            IValue value = tuple.getCell(attributeRef).getValue();
            result.add(value);
        }
        return result;
    }

    public static List<AttributeRef> getUniversalAttributesInPremise(List<FormulaVariable> universalVariables) {
        List<AttributeRef> result = new ArrayList<AttributeRef>();
        for (FormulaVariable formulaVariable : universalVariables) {
            LunaticUtility.addIfNotContained(result, formulaVariable.getPremiseRelationalOccurrences().get(0).getAttributeRef());
        }
        return result;
    }

    public static List<AttributeRef> getUniversalAttributesInConclusion(List<FormulaVariable> universalVariables) {
        List<AttributeRef> result = new ArrayList<AttributeRef>();
        for (FormulaVariable formulaVariable : universalVariables) {
            LunaticUtility.addIfNotContained(result, formulaVariable.getConclusionRelationalOccurrences().get(0).getAttributeRef());
        }
        return result;
    }

    public static AttributeRef findFirstOccurrenceInFormula(IFormula formula, List<FormulaVariableOccurrence> occurrences) {
        List<TableAlias> aliasesInFormula = AlgebraUtility.findAliasesForFormula(formula.getPositiveFormula());
        for (FormulaVariableOccurrence occurrence : occurrences) {
            AttributeRef attribute = occurrence.getAttributeRef();
            if (aliasesInFormula.contains(attribute.getTableAlias())) {
                return attribute;
            }
        }
        return null;
    }

    public static List<AttributeRef> extractRequestedAttributes(Dependency dependency) {
        List<AttributeRef> result = new ArrayList<AttributeRef>();
        result.addAll(dependency.getQueriedAttributes());
        for (AttributeRef attributeRef : dependency.getAdditionalAttributes()) {
            if (!result.contains(attributeRef)) {
                result.add(attributeRef);
            }
        }
        return result;
    }

    public static List<AttributeRef> findQueriedAttributesForExtTGD(Dependency dependency) {
        List<AttributeRef> queriedAttributes = new ArrayList<AttributeRef>();
        LunaticUtility.addAllIfNotContained(queriedAttributes, findQueriedAttributesInPremise(dependency));
        for (FormulaVariable formulaVariable : dependency.getPremise().getLocalVariables()) {
            if (formulaVariable.getConclusionRelationalOccurrences().isEmpty()) {
                continue;
            }
            AttributeRef firstPremiseOccurrenceInTarget = getFirstPremiseOccurrenceInTarget(formulaVariable.getPremiseRelationalOccurrences());
            if (firstPremiseOccurrenceInTarget != null) {
                AttributeRef unaliasedPremiseAttribute = ChaseUtility.unAlias(firstPremiseOccurrenceInTarget);
                LunaticUtility.addIfNotContained(queriedAttributes, unaliasedPremiseAttribute);
            }
            for (FormulaVariableOccurrence conclusionOccurrence : formulaVariable.getConclusionRelationalOccurrences()) {
                AttributeRef attribute = conclusionOccurrence.getAttributeRef();
                if (attribute.getTableAlias().isSource()) {
                    continue;
                }
                AttributeRef unaliasedConclusionAttribute = ChaseUtility.unAlias(conclusionOccurrence.getAttributeRef());
                LunaticUtility.addIfNotContained(queriedAttributes, unaliasedConclusionAttribute);
            }
        }
        return queriedAttributes;
    }

    private static AttributeRef getFirstPremiseOccurrenceInTarget(List<FormulaVariableOccurrence> premiseOccurrences) {
        for (FormulaVariableOccurrence formulaVariableOccurrence : premiseOccurrences) {
            if (formulaVariableOccurrence.getAttributeRef().isTarget()) {
                return formulaVariableOccurrence.getAttributeRef();
            }
        }
        return null;
    }

    public static List<AttributeRef> findQueriedAttributesInPremise(Dependency dependency) {
        if (logger.isTraceEnabled()) logger.trace("Searching query attributes for dependency: \n" + dependency);
        List<AttributeRef> queriedAttributes = new ArrayList<AttributeRef>();
        for (FormulaVariable variable : dependency.getPremise().getLocalVariables()) {
            if (logger.isTraceEnabled()) logger.trace("Inspecting variable: " + variable);
            if (hasSingleOccurrence(variable)) {
                continue;
            }
            for (FormulaVariableOccurrence occurrence : variable.getPremiseRelationalOccurrences()) {
                if (logger.isTraceEnabled()) logger.trace("Inspecting occurrence: " + occurrence);
                AttributeRef attribute = occurrence.getAttributeRef();
                if (attribute.getTableAlias().isSource()) {
                    continue;
                }
                AttributeRef unaliasedAttribute = ChaseUtility.unAlias(attribute);
                LunaticUtility.addIfNotContained(queriedAttributes, unaliasedAttribute);
            }
        }
        if (logger.isTraceEnabled()) logger.trace("Result: " + queriedAttributes);
        return queriedAttributes;
    }

    private static boolean hasSingleOccurrence(FormulaVariable variable) {
        if (logger.isTraceEnabled()) logger.trace("Occurrences for variable: " + variable.toLongString());
        int relationalPremiseOccurrences = variable.getPremiseRelationalOccurrences().size();
        int nonRelationalOccurrences = variable.getNonRelationalOccurrences().size();
        return relationalPremiseOccurrences + nonRelationalOccurrences <= 1;
    }

    public static List<AttributeRef> findTargetJoinAttributes(Dependency dependency) {
        List<VariableEquivalenceClass> relevantVariableClasses = ChaseUtility.findJoinVariablesInTarget(dependency);
        List<AttributeRef> targetJoinAttributes = new ArrayList<AttributeRef>();
        for (VariableEquivalenceClass variableEquivalenceClass : relevantVariableClasses) {
            for (FormulaVariableOccurrence occurrence : ChaseUtility.findTargetOccurrences(variableEquivalenceClass)) {
                targetJoinAttributes.add(occurrence.getAttributeRef());
            }
        }
        return targetJoinAttributes;
    }

    public static boolean hasSourceSymbols(Dependency dependency) {
        FindSourceAtoms sourceAtomFinder = new FindSourceAtoms();
        return sourceAtomFinder.hasSourceAtoms(dependency);
    }
}
