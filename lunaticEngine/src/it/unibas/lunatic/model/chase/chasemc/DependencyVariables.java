package it.unibas.lunatic.model.chase.chasemc;

import it.unibas.lunatic.model.dependency.Dependency;
import it.unibas.lunatic.model.dependency.VariableEquivalenceClass;
import java.util.ArrayList;
import java.util.List;

public class DependencyVariables {

    private final Dependency egd;
    private List<VariableEquivalenceClass> witnessVariables = new ArrayList<VariableEquivalenceClass>();
    private final List<VariableEquivalenceClass> conclusionVariables = new ArrayList<VariableEquivalenceClass>();

    public DependencyVariables(Dependency egd) {
        this.egd = egd;
    }

    public Dependency getEgd() {
        return egd;
    }

    public List<VariableEquivalenceClass> getConclusionVariables() {
        return conclusionVariables;
    }

    public void addConclusionVariable(VariableEquivalenceClass variable) {
        this.conclusionVariables.add(variable);
    }

    public List<VariableEquivalenceClass> getWitnessVariables() {
        return witnessVariables;
    }

    public void setWitnessVariables(List<VariableEquivalenceClass> witnessVariables) {
        this.witnessVariables = witnessVariables;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("EGD ").append(egd.getId()).append("\n");
        sb.append("\tWitness VEQs: ").append(witnessVariables).append("\n");
        sb.append("\tConclusion VEQs: ").append(conclusionVariables).append("");
        return sb.toString();
    }

}
