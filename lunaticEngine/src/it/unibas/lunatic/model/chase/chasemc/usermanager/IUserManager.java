package it.unibas.lunatic.model.chase.chasemc.usermanager;

import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.model.chase.chasemc.DeltaChaseStep;
import java.util.List;

public interface IUserManager {

    public boolean isUserInteractionRequired(List<DeltaChaseStep> newSteps, DeltaChaseStep root, Scenario scenario);
}
