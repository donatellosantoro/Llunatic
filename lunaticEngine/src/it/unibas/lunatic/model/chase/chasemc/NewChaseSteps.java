package it.unibas.lunatic.model.chase.chasemc;

import it.unibas.lunatic.model.dependency.Dependency;
import java.util.ArrayList;
import java.util.List;

public class NewChaseSteps {

    private Dependency egd;
    private List<DeltaChaseStep> chaseSteps = new ArrayList<DeltaChaseStep>();
    private boolean noRepairsNeeded;

    public NewChaseSteps(Dependency egd) {
        this.egd = egd;
    }

    public void addChaseStep(DeltaChaseStep step) {
        this.chaseSteps.add(step);
    }
    
    public void addAllChaseSteps(List<DeltaChaseStep> steps) {
        this.chaseSteps.addAll(steps);
    }

    public Dependency getEgd() {
        return egd;
    }

    public List<DeltaChaseStep> getChaseSteps() {
        return chaseSteps;
    }

    public void setNoRepairsNeeded(boolean noRepairsNeeded) {
        this.noRepairsNeeded = noRepairsNeeded;
    }

    public boolean isNoRepairsNeeded() {
        return noRepairsNeeded;
    }
    
    public int size() {
        return this.chaseSteps.size();
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append("------------NewChaseSteps for " + "egd=").append(egd.getId()).append((noRepairsNeeded ? "(noRepairsNeeded)" : "(repairs needed)")).append("\n");
        for (DeltaChaseStep deltaChaseStep : chaseSteps) {
            result.append(deltaChaseStep);
        }
        result.append("------------\n");
        return result.toString();
    }
}
