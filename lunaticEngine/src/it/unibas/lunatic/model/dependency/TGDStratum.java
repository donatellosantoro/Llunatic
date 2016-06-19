package it.unibas.lunatic.model.dependency;

import java.io.Serializable;
import java.util.List;

public class TGDStratum implements Serializable {

    private List<Dependency> tgds;
    private String id;

    public TGDStratum(List<Dependency> tgds) {
        this.tgds = tgds;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<Dependency> getTgds() {
        return tgds;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 89 * hash + (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        final TGDStratum other = (TGDStratum) obj;
        if ((this.id == null) ? (other.id != null) : !this.id.equals(other.id)) return false;
        return true;
    }

    @Override
    public String toString() {
        return "Stratutm " + id;
    }

    public String toLongString() {
        StringBuilder result = new StringBuilder();
        result.append("Stratum ").append(id).append(" [\n");
        for (Dependency dependency : tgds) {
            result.append(dependency.toLogicalString()).append("\n");
        }
        result.append("]");
        return result.toString();
    }

}
