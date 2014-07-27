package it.unibas.lunatic.model.chase.chasemc;

import it.unibas.lunatic.utility.LunaticUtility;
import it.unibas.lunatic.model.database.AttributeRef;
import it.unibas.lunatic.model.database.IValue;
import it.unibas.lunatic.model.dependency.Dependency;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EquivalenceClass {

    private Dependency dependency;
    private List<AttributeRef> occurrenceAttributesForConclusionVariable;
    private List<BackwardAttribute> attributesToChangeForBackwardChasing;
    private Map<IValue, TargetCellsToChange> tupleGroupsWithSameConclusionValue = new HashMap<IValue, TargetCellsToChange>();

    public EquivalenceClass(Dependency dependency, List<AttributeRef> occurrenceAttributesForConclusionVariable, 
            List<BackwardAttribute> attributesForBackwardChasing) {
        this.dependency = dependency;
        this.occurrenceAttributesForConclusionVariable = occurrenceAttributesForConclusionVariable;
        this.attributesToChangeForBackwardChasing = attributesForBackwardChasing;
    }

    public Dependency getDependency() {
        return dependency;
    }

    public List<AttributeRef> getOccurrenceAttributesForConclusionVariable() {
        return occurrenceAttributesForConclusionVariable;
    }

    public List<BackwardAttribute> getAttributesToChangeForBackwardChasing() {
        return attributesToChangeForBackwardChasing;
    }

    public Map<IValue, TargetCellsToChange> getTupleGroupsWithSameConclusionValue() {
        return tupleGroupsWithSameConclusionValue;
    }

    public List<TargetCellsToChange> getTupleGroups() {
        return new ArrayList<TargetCellsToChange>(tupleGroupsWithSameConclusionValue.values());
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("EquivalenceClass for dependency=" + dependency.getId());
        sb.append("\nOccurrence Attributes For ConclusionVariable: ").append(occurrenceAttributesForConclusionVariable);
        sb.append("\nAttributes For Backward Chasing\n");
        for (BackwardAttribute backwardAttribute : attributesToChangeForBackwardChasing) {
            sb.append("\t").append(backwardAttribute).append("\n");
        }
        sb.append("\nTuple Groups With Same Conclusion Value: ").append(LunaticUtility.printMap(tupleGroupsWithSameConclusionValue));
        return sb.toString();
    }
}
