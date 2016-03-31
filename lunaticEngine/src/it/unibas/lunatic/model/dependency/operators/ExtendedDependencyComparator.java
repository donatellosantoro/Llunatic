package it.unibas.lunatic.model.dependency.operators;

import it.unibas.lunatic.LunaticConstants;
import it.unibas.lunatic.model.dependency.ExtendedEGD;
import java.util.Comparator;

class ExtendedDependencyComparator implements Comparator<ExtendedEGD> {

    public int compare(ExtendedEGD t1, ExtendedEGD t2) {
        if (t1.getDependency().getId().compareTo(t2.getDependency().getId()) == 0) {
            if (t1.getChaseMode().equals(LunaticConstants.CHASE_FORWARD)) {
                return -1;
            }
            if (t2.getChaseMode().equals(LunaticConstants.CHASE_FORWARD)) {
                return 1;
            }
        }
        return t1.getId().compareTo(t2.getId());
    }
    
}
