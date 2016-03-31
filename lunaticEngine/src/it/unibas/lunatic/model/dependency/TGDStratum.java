package it.unibas.lunatic.model.dependency;

import java.util.List;

public class TGDStratum {

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
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append("Stratum ").append(id).append(" [\n");
        for (Dependency dependency : tgds) {
            result.append(dependency.toLogicalString()).append("\n");
        }
        result.append("]");
        return result.toString();
    }

}
