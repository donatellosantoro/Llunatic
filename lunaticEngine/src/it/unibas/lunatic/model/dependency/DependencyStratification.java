package it.unibas.lunatic.model.dependency;

import java.util.ArrayList;
import java.util.List;

public class DependencyStratification {
    
    private List<DependencyStratum> strata = new ArrayList<DependencyStratum>();

    public List<DependencyStratum> getStrata() {
        return strata;
    }
    
    public void addStratum(DependencyStratum stratum) {
        this.strata.add(stratum);
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
