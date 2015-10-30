package it.unibas.lunatic.model.chase.chasemc.costmanager;

import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.model.chase.chasemc.Repair;
import it.unibas.lunatic.model.chase.chasemc.DeltaChaseStep;
import it.unibas.lunatic.model.chase.chasemc.EquivalenceClassForEGDProxy;
import it.unibas.lunatic.model.chase.chasemc.operators.IOccurrenceHandler;
import java.util.List;

public interface ICostManager {

    public List<Repair> chooseRepairStrategy(
            EquivalenceClassForEGDProxy equivalenceClass, DeltaChaseStep chaseTreeRoot,
            List<Repair> repairsForDependency,
            Scenario scenario, String stepId, IOccurrenceHandler occurrenceHandler);
}
