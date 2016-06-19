package it.unibas.lunatic.model.chase.chasede.costmanager;

import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.model.chase.chasede.operators.OccurrenceHandlerDE;
import it.unibas.lunatic.model.chase.chasemc.Repair;
import it.unibas.lunatic.model.chase.chasemc.DeltaChaseStep;
import it.unibas.lunatic.model.chase.chasemc.EquivalenceClassForEGDProxy;
import java.util.List;

public interface ICostManagerDE {

    public List<Repair> chooseRepairStrategy(
            EquivalenceClassForEGDProxy equivalenceClass, DeltaChaseStep chaseTreeRoot,
            List<Repair> repairsForDependency,
            Scenario scenario, String stepId, OccurrenceHandlerDE occurrenceHandler);
}
