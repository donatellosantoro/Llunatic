package it.unibas.lunatic.model.dependency;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import speedy.model.database.AttributeRef;

public class DependencyStratification {

    private List<DependencyStratum> strata = new ArrayList<DependencyStratum>();
    private Map<AttributeRef, List<Dependency>> attributeDependencyMap = new HashMap<AttributeRef, List<Dependency>>();

    public List<DependencyStratum> getStrata() {
        return strata;
    }

    public void addStratum(DependencyStratum stratum) {
        this.strata.add(stratum);
    }

    public List<Dependency> getDependenciesForAttribute(AttributeRef attribute) {
        return attributeDependencyMap.get(attribute);
    }

    public void addDependencyForAttribute(AttributeRef attribute, Dependency value) {
        List<Dependency> dependenciesForAttribute = this.attributeDependencyMap.get(attribute);
        if (dependenciesForAttribute == null) {
            dependenciesForAttribute = new ArrayList<Dependency>();
            this.attributeDependencyMap.put(attribute, dependenciesForAttribute);
        }
        dependenciesForAttribute.add(value);
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        if (strata.size() == 1) {
            return strata.get(0).toString();
        }
        result.append("Dependency Stratification (").append(strata.size()).append(") {\n");
        for (DependencyStratum stratum : strata) {
            result.append(stratum).append("\n");
        }
        result.append("}");
        return result.toString();
    }
}
