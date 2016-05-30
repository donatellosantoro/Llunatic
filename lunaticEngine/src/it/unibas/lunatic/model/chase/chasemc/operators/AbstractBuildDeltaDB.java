package it.unibas.lunatic.model.chase.chasemc.operators;

import it.unibas.lunatic.model.chase.commons.operators.IBuildDeltaDB;
import it.unibas.lunatic.Scenario;
import speedy.model.database.AttributeRef;
import it.unibas.lunatic.model.dependency.Dependency;
import it.unibas.lunatic.model.dependency.FormulaAttribute;
import it.unibas.lunatic.model.dependency.FormulaVariable;
import it.unibas.lunatic.model.dependency.FormulaVariableOccurrence;
import it.unibas.lunatic.model.dependency.IFormulaAtom;
import it.unibas.lunatic.model.dependency.RelationalAtom;
import it.unibas.lunatic.utility.DependencyUtility;
import it.unibas.lunatic.utility.LunaticUtility;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class AbstractBuildDeltaDB implements IBuildDeltaDB {

    protected Set<AttributeRef> findAllAffectedAttributes(Scenario scenario) {
        Set<AttributeRef> result = new HashSet<AttributeRef>();
        result.addAll(findAllAttributesWithSkolem(scenario.getSTTgds()));
        Set<AttributeRef> allAffectedAttributesForEGDs = findAllAffectedAttributesForEGDs(scenario.getExtEGDs());
        result.addAll(allAffectedAttributesForEGDs);
        for (Dependency tgd : scenario.getExtTGDs()) {
            for (IFormulaAtom conclusionAtom : tgd.getConclusion().getAtoms()) {
                RelationalAtom relationalAtom = (RelationalAtom) conclusionAtom;
                for (FormulaAttribute attribute : relationalAtom.getAttributes()) {
                    AttributeRef attributeRef = new AttributeRef(relationalAtom.getTableName(), attribute.getAttributeName());
                    result.add(attributeRef);
                }
            }
            List<FormulaVariable> universalVariablesInConclusion = DependencyUtility.getUniversalVariablesInConclusion(tgd);
            for (FormulaVariable formulaVariable : universalVariablesInConclusion) {
                for (FormulaVariableOccurrence formulaVariableOccurrence : formulaVariable.getPremiseRelationalOccurrences()) {
                    result.add(formulaVariableOccurrence.getAttributeRef());
                }
            }
        }
        return result;
    }

    protected Set<AttributeRef> findAllAffectedAttributesForDEScenario(Scenario scenario) {
        Set<AttributeRef> intersection = findAllAffectedAttributesForEGDs(scenario.getExtEGDs());
        intersection.retainAll(scenario.getAttributesWithLabeledNulls());
        return intersection;
    }

    private Set<AttributeRef> findAllAffectedAttributesForEGDs(List<Dependency> egds) {
        Set<AttributeRef> result = new HashSet<AttributeRef>();
        for (Dependency egd : egds) {
            result.addAll(egd.getAffectedAttributes());
        }
        return result;
    }

    protected boolean isAffected(AttributeRef attributeRef, Set<AttributeRef> affectedAttributes) {
        return affectedAttributes.contains(attributeRef);
    }

    private List<AttributeRef> findAllAttributesWithSkolem(List<Dependency> stTgds) {
        List<AttributeRef> result = new ArrayList<AttributeRef>();
        for (Dependency stTgd : stTgds) {
            for (FormulaVariable v : stTgd.getConclusion().getLocalVariables()) {
                if (v.isUniversal()) {
                    continue;
                }
                for (FormulaVariableOccurrence variableOccurrence : v.getConclusionRelationalOccurrences()) {
                    LunaticUtility.addIfNotContained(result, variableOccurrence.getAttributeRef());
                }
            }
        }
        return result;
    }

}
