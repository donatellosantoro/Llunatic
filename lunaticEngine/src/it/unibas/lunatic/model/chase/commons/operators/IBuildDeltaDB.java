package it.unibas.lunatic.model.chase.commons.operators;

import it.unibas.lunatic.Scenario;
import speedy.model.database.IDatabase;

public interface IBuildDeltaDB {

    IDatabase generate(IDatabase database, Scenario scenario, String rootName);

}
