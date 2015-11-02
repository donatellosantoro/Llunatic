package it.unibas.lunatic.model.chase.chaseded;

import it.unibas.lunatic.Scenario;
import speedy.model.database.IDatabase;

public interface IDEDDatabaseManager {

    public IDatabase cloneTarget(Scenario scenario);

    public void restoreTarget(IDatabase original, Scenario scenario);

    public void removeClone(IDatabase clone, Scenario scenario);

}
