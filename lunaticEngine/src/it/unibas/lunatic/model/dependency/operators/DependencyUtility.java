package it.unibas.lunatic.model.dependency.operators;

import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.model.algebra.operators.AlgebraUtility;
import it.unibas.lunatic.model.chase.commons.operators.ChaseUtility;
import it.unibas.lunatic.model.dependency.Dependency;
import it.unibas.lunatic.model.dependency.FormulaVariable;
import it.unibas.lunatic.model.dependency.FormulaVariableOccurrence;
import it.unibas.lunatic.model.dependency.IFormula;
import it.unibas.lunatic.model.dependency.IFormulaAtom;
import it.unibas.lunatic.model.dependency.RelationalAtom;
import it.unibas.lunatic.model.dependency.VariableEquivalenceClass;
import it.unibas.lunatic.model.dependency.operators.FindSourceAtoms;
import it.unibas.lunatic.utility.LunaticUtility;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import speedy.model.database.AttributeRef;
import speedy.model.database.IValue;
import speedy.model.database.TableAlias;
import speedy.model.database.Tuple;

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
        if (logger.isTraceEnabled()) logger.trace("Finding universal variables in conclusion for dependency " + dependency.toLogicalString());
        List<FormulaVariable> result = new ArrayList<FormulaVariable>();
        for (FormulaVariable formulaVariable : dependency.getPremise().getLocalVariables()) {
            List<FormulaVariableOccurrence> variablesOccurrence = formulaVariable.getConclusionRelationalOccurrences();
            if (logger.isTraceEnabled()) logger.trace("Conclusion relational occurrences for variable " + formulaVariable.getId() + ": " + variablesOccurrence);
            if (!variablesOccurrence.isEmpty()) {
                LunaticUtility.addIfNotContained(result, formulaVariable);
            }
        }
        return result;
    }

    public static List<FormulaVariable> getExistentialVariables(Dependency dependency) {
        return dependency.getConclusion().getLocalVariables();
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

    public static List<AttributeRef> extractRequestedAttributesWithExistential(Dependency dependency) {
        List<AttributeRef> result = new ArrayList<AttributeRef>();
        result.addAll(dependency.getQueriedAttributes());
        for (FormulaVariable variable : dependency.getConclusion().getLocalVariables()) {
            if (variable.getConclusionRelationalOccurrences().size() == 1) {
                continue;
            }
            for (FormulaVariableOccurrence occurrence : variable.getConclusionRelationalOccurrences()) {
                if (logger.isTraceEnabled()) logger.trace("Inspecting occurrence: " + occurrence);
                AttributeRef attribute = occurrence.getAttributeRef();
                AttributeRef unaliasedAttribute = ChaseUtility.unAlias(attribute);
                LunaticUtility.addIfNotContained(result, unaliasedAttribute);
            }
        }
        for (AttributeRef attributeRef : dependency.getAdditionalAttributes()) {
            if (!result.contains(attributeRef)) {
                result.add(attributeRef);
            }
        }
        return result;
    }

    public static List<AttributeRef> findTargetQueriedAttributesForExtTGD(Dependency dependency) {
        List<AttributeRef> queriedAttributes = new ArrayList<AttributeRef>();
        LunaticUtility.addAllIfNotContained(queriedAttributes, findTargetQueriedAttributesInPremise(dependency));
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

    public static List<AttributeRef> findTargetQueriedAttributesInPremise(Dependency dependency) {
        if (logger.isTraceEnabled()) logger.trace("Searching query attributes for dependency: \n" + dependency);
        List<AttributeRef> queriedAttributes = new ArrayList<AttributeRef>();
        for (FormulaVariable variable : dependency.getPremise().getLocalVariables()) {
            if (logger.isTraceEnabled()) logger.trace("Inspecting variable: " + variable);
            if (hasSingleOccurrenceInPremise(variable)) {
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

    private static boolean hasSingleOccurrenceInPremise(FormulaVariable variable) {
        if (logger.isTraceEnabled()) logger.trace("Occurrences for variable: " + variable.toLongString());
        int relationalPremiseOccurrences = variable.getPremiseRelationalOccurrences().size();
        int nonRelationalOccurrences = variable.getNonRelationalOccurrences().size();
        return relationalPremiseOccurrences + nonRelationalOccurrences <= 1;
    }

    public static List<AttributeRef> findTargetJoinAttributesInPositiveFormula(Dependency dependency) {
        List<VariableEquivalenceClass> relevantVariableClasses = ChaseUtility.findJoinVariablesInTarget(dependency);
        List<AttributeRef> targetJoinAttributes = new ArrayList<AttributeRef>();
        for (VariableEquivalenceClass variableEquivalenceClass : relevantVariableClasses) {
            List<FormulaVariableOccurrence> targetOccurrencesForEquivalenceClass = ChaseUtility.findTargetOccurrences(variableEquivalenceClass);
            List<FormulaVariableOccurrence> positiveTargetOccurrencesForEquivalenceClass = ChaseUtility.findPositiveOccurrences(dependency.getPremise().getPositiveFormula(), targetOccurrencesForEquivalenceClass);
            for (FormulaVariableOccurrence occurrence : positiveTargetOccurrencesForEquivalenceClass) {
                targetJoinAttributes.add(occurrence.getAttributeRef());
            }
        }
        return targetJoinAttributes;
    }

    public static boolean hasUniversalVariablesInConclusion(Dependency dependency) {
        List<FormulaVariable> universalVariablesInConclusion = DependencyUtility.getUniversalVariablesInConclusion(dependency);
        return !universalVariablesInConclusion.isEmpty();
    }

    public static boolean hasSourceSymbols(Dependency dependency) {
        FindSourceAtoms sourceAtomFinder = new FindSourceAtoms();
        return sourceAtomFinder.hasSourceAtoms(dependency);
    }

    public static String buildVariableIdForConstant(Object constantValue) {
        return "v" + valueWithOnlyChars(constantValue);
    }

    public static String buildTableNameForConstants(Dependency dependency, boolean premise) {
        String suffix = (premise ? "prem" : "conc");
        return "c_in_" + dependency.getId() + "_" + suffix;
    }

    public static String buildAttributeNameForConstant(Object constantValue) {
        return "a_" + valueWithOnlyChars(constantValue);
    }

    public static String valueWithoutSpaces(Object value) {
        return value.toString().replaceAll(" ", "");
    }

    public static String valueWithOnlyChars(Object value) {
        String valueString = value.toString().replaceAll("[^A-Za-z0-9]", "_").toLowerCase();
        if (valueString.length() > 35) valueString = valueString.substring(0, 35);
        return valueString;
    }

    public static Dependency findDependency(String dependencyId, List<Dependency> dependencies) {
        for (Dependency dependency : dependencies) {
            if (dependency.getId().equalsIgnoreCase(dependencyId)) {
                return dependency;
            }
        }
        throw new IllegalArgumentException("Unable to find dependency with id " + dependencyId + " in " + dependencies);
    }

    public static List<String> findSourceAtoms(Dependency egd, Scenario scenario) {
        List<String> result = new ArrayList<String>();
        if (!DependencyUtility.hasSourceSymbols(egd)) {
            return result;
        }
        for (IFormulaAtom atom : egd.getPremise().getPositiveFormula().getAtoms()) {
            if (!(atom instanceof RelationalAtom)) {
                continue;
            }
            RelationalAtom relationalAtom = (RelationalAtom) atom;
            if (!relationalAtom.isSource()) {
                continue;
            }
            result.add(relationalAtom.getTableName());
        }
        return result;
    }

    public static List<String> findAuthoritativeAtoms(Dependency egd, Scenario scenario) {
        List<String> result = new ArrayList<String>();
        for (String sourceTable : findSourceAtoms(egd, scenario)) {
            if (scenario.getAuthoritativeSources().contains(sourceTable)) {
                result.add(sourceTable);
            }
        }
        return result;
    }

    public static boolean hasAuthoritativeAtoms(Dependency egd, Scenario scenario) {
        return !findAuthoritativeAtoms(egd, scenario).isEmpty();
    }

    public static boolean isLav(Dependency tgd) {
//        return stTgd.getPremise().getPositiveFormula().getAtoms().size() == 1;
          return hasSingleAtom(tgd.getPremise());
    }

    public static boolean isGav(Dependency tgd) {
//        return stTgd.getConclusion().getPositiveFormula().getAtoms().size() == 1;
          return hasSingleAtom(tgd.getConclusion());
    }

    public static boolean hasSingleAtom(IFormula formula) {
        return formula.getPositiveFormula().getAtoms().size() == 1 &&
                formula.getNegatedSubFormulas().isEmpty();
    }

    public static boolean allVariablesHaveSingletonOccurrences(Dependency tgd) {
        List<VariableEquivalenceClass> equivalenceClasses = tgd.getPremise().getLocalVariableEquivalenceClasses();
        for (VariableEquivalenceClass equivalenceClass : equivalenceClasses) {
            if (equivalenceClass.getPremiseRelationalOccurrences().size() > 1) {
                return false;
            }
        }
        return true;
    }

    public static String clean(String expressionString) {
        String result = expressionString.trim();
        result = result.replaceAll("\\$", "");
        return result.substring(1, result.length() - 1);
    }

    public static String printDependencies(List<Dependency> dependencies) {
        StringBuilder sb = new StringBuilder();
        for (Dependency dependency : dependencies) {
            sb.append(dependency.toLogicalString());
        }
        return sb.toString();
    }

}
