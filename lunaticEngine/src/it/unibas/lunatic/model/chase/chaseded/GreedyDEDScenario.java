package it.unibas.lunatic.model.chase.chaseded;

import it.unibas.lunatic.model.dependency.DED;
import it.unibas.lunatic.model.dependency.Dependency;
import java.util.HashMap;
import java.util.Map;

public class GreedyDEDScenario {

    private Map<DED, Dependency> dedInstantiations = new HashMap<DED, Dependency>();

    public void addDEDInstantiation(DED ded, Dependency dependency) {
        this.dedInstantiations.put(ded, dependency);
    }

    public Dependency getDependencyForDED(DED ded) {
        return dedInstantiations.get(ded);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (DED ded : dedInstantiations.keySet()) {
            sb.append("DED ").append(ded.getId()).append(" ").append(dedInstantiations.get(ded)).append("\n");
        }
        return sb.toString();
    }
}
