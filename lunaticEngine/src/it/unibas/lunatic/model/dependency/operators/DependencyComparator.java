package it.unibas.lunatic.model.dependency.operators;

import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.model.dependency.Dependency;
import java.util.Comparator;

class DependencyComparator implements Comparator<Dependency> {

    private Scenario scenario;

    public DependencyComparator(Scenario scenario) {
        this.scenario = scenario;
    }

    public int compare(Dependency t1, Dependency t2) {
        int t1AuthAtoms = DependencyUtility.findAuthoritativeAtoms(t1, scenario).size();
        int t2AuthAtoms = DependencyUtility.findAuthoritativeAtoms(t2, scenario).size();
        if (t1AuthAtoms == t2AuthAtoms) {
            return t1.getId().compareTo(t2.getId());
        }
        return (t2AuthAtoms - t1AuthAtoms);
    }

}
