package it.unibas.lunatic.model.chase.chasede.operators.mainmemory;

import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.model.chase.chasede.operators.IReplaceDatabase;
import speedy.model.database.IDatabase;

public class MainMemoryReplaceDatabase implements IReplaceDatabase {

    public void replaceTargetDB(IDatabase newDatabase, Scenario scenario) {
        scenario.setTarget(newDatabase);
    }

}
