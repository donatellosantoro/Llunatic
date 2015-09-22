package it.unibas.lunatic.model.chase.chasemc.operators;

import it.unibas.lunatic.Scenario;
import speedy.model.database.IDatabase;

public interface IBuildDeltaDB {

    IDatabase generate(IDatabase database, Scenario scenario, String rootName);

}
