package it.unibas.lunatic.model.chase.chasemc.usermanager;

import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.model.chase.chasemc.DeltaChaseStep;
import java.util.List;

public class AfterForkUserManager implements IUserManager {

    public boolean isUserInteractionRequired(List<DeltaChaseStep> newSteps, DeltaChaseStep root, Scenario scenario) {
        if (newSteps.size() > 1) {
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return "After Fork";
    }
}
