package it.unibas.lunatic.model.dependency;

import java.util.List;
import speedy.model.database.TableAlias;

public class SelfJoin {

    private final List<TableAlias> atoms;
    private final VariableEquivalenceClass variableEquivalenceClass;

    public SelfJoin(List<TableAlias> atoms, VariableEquivalenceClass variableEquivalenceClass) {
        this.atoms = atoms;
        this.variableEquivalenceClass = variableEquivalenceClass;
    }

    public List<TableAlias> getAtoms() {
        return atoms;
    }

    public VariableEquivalenceClass getVariableEquivalenceClass() {
        return variableEquivalenceClass;
    }

    @Override
    public String toString() {
        return "SelfJoin{" + "atoms=" + atoms + " - variables: " + variableEquivalenceClass + '}';
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 67 * hash + (this.atoms != null ? this.atoms.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        final SelfJoin other = (SelfJoin) obj;
        if (this.atoms != other.atoms && (this.atoms == null || !this.atoms.equals(other.atoms))) return false;
        return true;
    }
}