package it.unibas.lunatic.model.chase.chasemc;

import it.unibas.lunatic.model.dependency.FormulaVariable;
import speedy.model.database.AttributeRef;

public class BackwardAttribute {

    private AttributeRef attributeRef;
    private FormulaVariable variable;

    public BackwardAttribute(AttributeRef attributeRef, FormulaVariable variable) {
        this.attributeRef = attributeRef;
        this.variable = variable;
    }

    public AttributeRef getAttributeRef() {
        return attributeRef;
    }

    public FormulaVariable getVariable() {
        return variable;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 13 * hash + (this.attributeRef != null ? this.attributeRef.hashCode() : 0);
        hash = 13 * hash + (this.variable != null ? this.variable.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        final BackwardAttribute other = (BackwardAttribute) obj;
        if (this.attributeRef != other.attributeRef && (this.attributeRef == null || !this.attributeRef.equals(other.attributeRef))) return false;
        if (this.variable != other.variable && (this.variable == null || !this.variable.equals(other.variable))) return false;
        return true;
    }

    @Override
    public String toString() {
        return "[" + attributeRef + ", var=" + variable + "]";
    }
}
