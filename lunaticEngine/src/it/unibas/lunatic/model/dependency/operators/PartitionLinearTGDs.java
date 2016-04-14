package it.unibas.lunatic.model.dependency.operators;

import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.model.dependency.Dependency;
import it.unibas.lunatic.model.dependency.VariableEquivalenceClass;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PartitionLinearTGDs {

    private final static Logger logger = LoggerFactory.getLogger(PartitionLinearTGDs.class);

    public void findLinearTGD(Scenario scenario) {
        for (Dependency extTGD : scenario.getExtTGDs()) {
            extTGD.setLinearTGD(isLinearTGD(extTGD));
            if (logger.isDebugEnabled()) logger.debug("TGD: " + extTGD.toLogicalString() + "\n Linear: " + extTGD.isLinearTGD());
        }
    }

    private boolean isLinearTGD(Dependency extTGD) {
        List<VariableEquivalenceClass> equivalenceClasses = extTGD.getPremise().getLocalVariableEquivalenceClasses();
        for (VariableEquivalenceClass equivalenceClass : equivalenceClasses) {
            if (equivalenceClass.getPremiseRelationalOccurrences().size() > 1) {
                return false;
            }
        }
        return true;
    }

}
