package it.unibas.lunatic.model.chase.chasemc.operators;

import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.model.dependency.Dependency;
import speedy.model.database.IDatabase;

public interface IBuildDatabaseForChaseStepMC {

    IDatabase extractDatabase(String stepId, IDatabase deltaDB, IDatabase originalDB, Dependency dependency, Scenario scenario);
    IDatabase extractDatabase(String stepId, IDatabase deltaDB, IDatabase originalDB, Scenario scenario);
    IDatabase extractDatabaseWithDistinct(String stepId, IDatabase deltaDB, IDatabase originalDB, Scenario scenario);

}
