package it.unibas.lunatic.model.chase.chasemc.usermanager;

import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.model.chase.chasemc.DeltaChaseStep;
import it.unibas.lunatic.model.chase.chasemc.operators.IOccurrenceHandler;
import java.util.List;

public class AfterLLUNForkUserManager implements IUserManager {

    private AfterLLUNUserManager afterLLUNUserManager;

    public AfterLLUNForkUserManager(IOccurrenceHandler occurrenceHandler) {
        this.afterLLUNUserManager = new AfterLLUNUserManager(occurrenceHandler);
    }

    public boolean isUserInteractionRequired(List<DeltaChaseStep> newSteps, DeltaChaseStep root, Scenario scenario) {
        if (newSteps.size() > 1) {
            return afterLLUNUserManager.isUserInteractionRequired(newSteps, root, scenario);
        }
        return false;
    }

    @Override
    public String toString() {
        return "After LLUN Fork";
    }
}
