package it.unibas.lunatic.model.chase.chasemc;

import speedy.model.database.IValue;
import java.util.List;

public class TGDViolation {

    private List<IValue> violationValues;

    public TGDViolation(List<IValue> violationValues) {
        this.violationValues = violationValues;
    }

    public List<IValue> getViolationValues() {
        return violationValues;
    }

    @Override
    public String toString() {
        return "TGDViolation: " + violationValues;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + (this.violationValues != null ? this.violationValues.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        final TGDViolation other = (TGDViolation) obj;
        if (this.violationValues != other.violationValues && (this.violationValues == null || !this.violationValues.equals(other.violationValues))) return false;
        return true;
    }
}
