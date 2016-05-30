package it.unibas.lunatic.model.dependency.operators;

import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.model.dependency.Dependency;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FindInclusionDependencies {

    private final static Logger logger = LoggerFactory.getLogger(FindInclusionDependencies.class);

    public void findInclusionDependenciesAndLinearTgds(Scenario scenario) {
        for (Dependency extTGD : scenario.getExtTGDs()) {
            extTGD.setInclusionDependency(isInclusionDependency(extTGD));
            if (logger.isDebugEnabled()) logger.debug("TGD: " + extTGD.toLogicalString() + "\n Inclusion dep.: " + extTGD.isInclusionDependency());
            extTGD.setLinearTgd(isLinearTgd(extTGD));
            if (logger.isDebugEnabled()) logger.debug("TGD: " + extTGD.toLogicalString() + "\n Linear: " + extTGD.isLinearTgd());
        }
    }

    private boolean isInclusionDependency(Dependency tgd) {
        if (!DependencyUtility.hasSingleAtom(tgd.getPremise()) || !DependencyUtility.hasSingleAtom(tgd.getConclusion())) {
            return false;
        }
        return DependencyUtility.allVariablesHaveSingletonOccurrences(tgd);
    }

    private boolean isLinearTgd(Dependency tgd) {
        if (!DependencyUtility.hasSingleAtom(tgd.getPremise()) || !DependencyUtility.hasSingleAtom(tgd.getConclusion())) {
            return false;
        }
        return DependencyUtility.allVariablesHaveSingletonOccurrences(tgd);
    }
    
}
