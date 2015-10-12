package it.unibas.lunatic.model.chase.chasemc;

import it.unibas.lunatic.model.dependency.Dependency;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import speedy.model.database.AttributeRef;
import speedy.model.database.IValue;
import speedy.utility.comparator.StringComparator;

public class EquivalenceClassForSymmetricEGD {

    private Dependency egd;
    private List<AttributeRef> occurrenceAttributesForConclusionVariable;
    private List<BackwardAttribute> attributesToChangeForBackwardChasing;
    private Map<IValue, EGDEquivalenceClassCells> tupleGroupsWithSameConclusionValue = new HashMap<IValue, EGDEquivalenceClassCells>();

    public EquivalenceClassForSymmetricEGD(Dependency egd, List<AttributeRef> occurrenceAttributesForConclusionVariable,
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

    public Map<IValue, EGDEquivalenceClassCells> getTupleGroupsWithSameConclusionValue() {
        return tupleGroupsWithSameConclusionValue;
    }

    public List<EGDEquivalenceClassCells> getTupleGroups() {
        return new ArrayList<EGDEquivalenceClassCells>(tupleGroupsWithSameConclusionValue.values());
    }

    public boolean isEmpty() {
        return this.tupleGroupsWithSameConclusionValue.isEmpty();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("EquivalenceClass for egd=" + egd.getId());
        sb.append("\nOccurrence Attributes For ConclusionVariable: ").append(occurrenceAttributesForConclusionVariable);
        sb.append("\nAttributes For Backward Chasing\n");
        for (BackwardAttribute backwardAttribute : attributesToChangeForBackwardChasing) {
            sb.append("\t").append(backwardAttribute).append("\n");
        }
        sb.append("Tuple Groups With Same Conclusion Value: \n");
        List<IValue> keys = new ArrayList<IValue>(tupleGroupsWithSameConclusionValue.keySet());
        Collections.sort(keys, new StringComparator());
        for (IValue key : keys) {
            sb.append("\tValue: ").append(key).append("\n");
            sb.append(tupleGroupsWithSameConclusionValue.get(key).toString("\t\t"));
        }
        return sb.toString();
    }
}
