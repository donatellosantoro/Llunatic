package it.unibas.lunatic.model.dependency;

import it.unibas.lunatic.LunaticConstants;
import it.unibas.lunatic.model.dependency.operators.DependencyToString;
import java.util.ArrayList;
import java.util.List;

public class DED implements Cloneable {

    private String id;
    private String type;
    private List<Dependency> associatedDependencies = new ArrayList<Dependency>();

    public DED() {
    }

    public DED(String id, String type) {
        this.id = id;
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
        for (int i = 0; i < associatedDependencies.size(); i++) {
            Dependency dependency = associatedDependencies.get(i);
            dependency.setId(id + "_" + i);
        }
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = LunaticConstants.DED + "_" + type;
        for (Dependency dependency : associatedDependencies) {
            dependency.setType(type);
        }
    }

    public List<Dependency> getAssociatedDependencies() {
        return associatedDependencies;
    }

    public void setAssociatedDependencies(List<Dependency> associatedDependencies) {
        this.associatedDependencies = associatedDependencies;
    }


    public void addAssociatedDependency(Dependency dependency) {
        this.associatedDependencies.add(dependency);
    }
    
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 83 * hash + (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        final DED other = (DED) obj;
        if ((this.id == null) ? (other.id != null) : !this.id.equals(other.id)) return false;
        return true;
    }

    @Override
    public DED clone() {
        DED clone = null;
        try {
            clone = (DED) super.clone();
            clone.associatedDependencies = new ArrayList<Dependency>();
            for (Dependency dependency : associatedDependencies) {
                clone.associatedDependencies.add(dependency.clone());
            }
        } catch (CloneNotSupportedException ex) {
        }
        return clone;
    }

    @Override
    public String toString() {
        return new DependencyToString().toLogicalString(this, "", false);
    }
}
