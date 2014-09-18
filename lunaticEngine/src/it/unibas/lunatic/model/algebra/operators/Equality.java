package it.unibas.lunatic.model.algebra.operators;

import it.unibas.lunatic.model.database.AttributeRef;

class Equality {

    private AttributeRef leftAttribute;
    private AttributeRef rightAttribute;

    Equality(AttributeRef leftAttribute, AttributeRef rightAttribute) {
        this.leftAttribute = leftAttribute;
        this.rightAttribute = rightAttribute;
    }

    AttributeRef getLeftAttribute() {
        return leftAttribute;
    }

    AttributeRef getRightAttribute() {
        return rightAttribute;
    }

    boolean isTrivial() {
        return leftAttribute.equals(rightAttribute);
    }

    @Override
    public String toString() {
        return "Equality{" + "leftAttribute=" + leftAttribute + ", rightAttribute=" + rightAttribute + '}';
    }
}