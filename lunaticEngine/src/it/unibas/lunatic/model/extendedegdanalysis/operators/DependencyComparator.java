package it.unibas.lunatic.model.extendedegdanalysis.operators;

import it.unibas.lunatic.model.dependency.Dependency;
import java.util.Comparator;

class DependencyComparator implements Comparator<Dependency> {

    public int compare(Dependency t1, Dependency t2) {
        return t1.getId().compareTo(t2.getId());
    }
    
}
