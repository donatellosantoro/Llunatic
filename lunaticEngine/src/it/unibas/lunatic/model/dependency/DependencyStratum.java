package it.unibas.lunatic.model.dependency;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class DependencyStratum {

    private List<Dependency> dependencies;
    private String id;

    public DependencyStratum(Set<Dependency> dependencies) {
        this.dependencies = new ArrayList<Dependency>(dependencies);
    }

    public List<Dependency> getDependencies() {
        return dependencies;
    }

    public List<ExtendedDependency> getExtendedDependencies() {
        List<ExtendedDependency> result = new ArrayList<ExtendedDependency>();
        for (Dependency dependency : this.dependencies) {
            result.addAll(dependency.getExtendedDependencies());
        }
        return result;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append("Stratum ").append(id).append(" [\n");
        for (Dependency dependency : dependencies) {
            result.append(dependency).append("\n");
        }
        result.append("]");
        return result.toString();
    }
        
}
