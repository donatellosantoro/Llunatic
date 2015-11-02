package it.unibas.lunatic.model.chase.chaseded.mainmemory;

import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.model.chase.chaseded.IDEDDatabaseManager;
import speedy.model.database.IDatabase;

public class MainMemoryDEDDatabaseManager implements IDEDDatabaseManager {

    public IDatabase cloneTarget(Scenario scenario) {
        return scenario.getTarget().clone();
    }

    public void restoreTarget(IDatabase original, Scenario scenario) {
        scenario.setTarget(original.clone());
    }

    public void removeClone(IDatabase clone, Scenario scenario) {
    }
}
