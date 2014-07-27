package it.unibas.lunatic.model.chase.chasemc.partialorder;

import it.unibas.lunatic.model.chase.chasemc.partialorder.valuecomparator.IValueComparator;
import it.unibas.lunatic.model.database.AttributeRef;

public class OrderingAttribute {

    private AttributeRef attribute;
    private AttributeRef associatedAttribute;
    private IValueComparator valueComparator;

    public OrderingAttribute() {
    }

    public OrderingAttribute(AttributeRef attribute, AttributeRef associatedAttribute, IValueComparator valueComparator) {
        this.attribute = attribute;
        this.associatedAttribute = associatedAttribute;
        this.valueComparator = valueComparator;
    }

    public AttributeRef getAttribute() {
        return attribute;
    }

    public void setAttribute(AttributeRef attribute) {
        this.attribute = attribute;
    }

    public AttributeRef getAssociatedAttribute() {
        return associatedAttribute;
    }

    public void setAssociatedAttribute(AttributeRef associatedAttribute) {
        this.associatedAttribute = associatedAttribute;
    }

    public IValueComparator getValueComparator() {
        return valueComparator;
    }

    public void setValueComparator(IValueComparator valueComparator) {
        this.valueComparator = valueComparator;
    }

    @Override
    public String toString() {
        return "Attribute " + attribute + " orderedBy " + associatedAttribute + " using " + valueComparator;
    }
}
