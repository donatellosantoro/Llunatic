package it.unibas.lunatic.model.chase.chasemc;

import it.unibas.lunatic.utility.LunaticUtility;
import it.unibas.lunatic.model.database.AttributeRef;
import it.unibas.lunatic.model.database.IValue;
import it.unibas.lunatic.model.dependency.Dependency;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EquivalenceClassForEGD {

    private Dependency egd;
    private List<AttributeRef> occurrenceAttributesForConclusionVariable;
    private List<BackwardAttribute> attributesToChangeForBackwardChasing;
    private Map<IValue, TargetCellsToChangeForEGD> tupleGroupsWithSameConclusionValue = new HashMap<IValue, TargetCellsToChangeForEGD>();

    public EquivalenceClassForEGD(Dependency egd, List<AttributeRef> occurrenceAttributesForConclusionVariable, 
            List<BackwardAttribute> attributesForBackwardChasing) {
        this.egd = egd;
        this.occurrenceAttributesForConclusionVariable = occurrenceAttributesForConclusionVariable;
        this.attributesToChangeForBackwardChasing = attributesForBackwardChasing;
    }

    public Dependency getEGD() {
        return egd;
    }

    public List<AttributeRef> getOccurrenceAttributesForConclusionVariable() {
        return occurrenceAttributesForConclusionVariable;
    }

    public List<BackwardAttribute> getAttributesToChangeForBackwardChasing() {
        return attributesToChangeForBackwardChasing;
    }

    public Map<IValue, TargetCellsToChangeForEGD> getTupleGroupsWithSameConclusionValue() {
        return tupleGroupsWithSameConclusionValue;
    }

    public List<TargetCellsToChangeForEGD> getTupleGroups() {
        return new ArrayList<TargetCellsToChangeForEGD>(tupleGroupsWithSameConclusionValue.values());
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("EquivalenceClass for egd=" + egd.getId());
        sb.append("\nOccurrence Attributes For ConclusionVariable: ").append(occurrenceAttributesForConclusionVariable);
        sb.append("\nAttributes For Backward Chasing\n");
        for (BackwardAttribute backwardAttribute : attributesToChangeForBackwardChasing) {
            sb.append("\t").append(backwardAttribute).append("\n");
        }
        sb.append("\nTuple Groups With Same Conclusion Value: ").append(LunaticUtility.printMap(tupleGroupsWithSameConclusionValue));
        return sb.toString();
    }
}
