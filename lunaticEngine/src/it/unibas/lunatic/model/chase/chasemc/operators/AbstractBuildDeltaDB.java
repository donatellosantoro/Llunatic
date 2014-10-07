package it.unibas.lunatic.model.chase.chasemc.operators;

import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.model.database.AttributeRef;
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

    protected List<AttributeRef> findAllAffectedAttributes(Scenario scenario) {
        List<AttributeRef> result = new ArrayList<AttributeRef>();
        result.addAll(findAllAttributesWithSkolem(scenario.getSTTgds()));
        for (Dependency egd : scenario.getExtEGDs()) {
            List<AttributeRef> affectedAttributes = egd.getAffectedAttributes();
            for (AttributeRef affectedAttribute : affectedAttributes) {
                LunaticUtility.addIfNotContained(result, affectedAttribute);
            }
        }
        for (Dependency tgd : scenario.getExtTGDs()) {
            for (IFormulaAtom conclusionAtom : tgd.getConclusion().getAtoms()) {
                RelationalAtom relationalAtom = (RelationalAtom) conclusionAtom;
                for (FormulaAttribute attribute : relationalAtom.getAttributes()) {
                    AttributeRef attributeRef = new AttributeRef(relationalAtom.getTableName(), attribute.getAttributeName());
                    LunaticUtility.addIfNotContained(result, attributeRef);
                }
            }
            List<FormulaVariable> universalVariablesInConclusion = DependencyUtility.getUniversalVariablesInConclusion(tgd);
            for (FormulaVariable formulaVariable : universalVariablesInConclusion) {
                for (FormulaVariableOccurrence formulaVariableOccurrence : formulaVariable.getPremiseRelationalOccurrences()) {
                    LunaticUtility.addIfNotContained(result, formulaVariableOccurrence.getAttributeRef());
                }
            }
        }
        return result;
    }

    protected boolean isAffected(AttributeRef attributeRef, List<AttributeRef> affectedAttributes) {
        return affectedAttributes.contains(attributeRef);
    }

    private List<AttributeRef> findAllAttributesWithSkolem(List<Dependency> stTgds) {
        List<AttributeRef> result = new ArrayList<AttributeRef>();
        for (Dependency stTgd : stTgds) {
            for (FormulaVariable v : stTgd.getConclusion().getLocalVariables()) {
                if(v.isUniversal()){
                    continue;
                }
                for (FormulaVariableOccurrence variableOccurrence :v.getConclusionRelationalOccurrences() ) {
                    LunaticUtility.addIfNotContained(result, variableOccurrence.getAttributeRef());
                }
            }
        }
        return result;
    }
}
