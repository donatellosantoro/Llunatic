package it.unibas.lunatic.model.chase.chasemc.operators;

import it.unibas.lunatic.model.dependency.Dependency;
import speedy.model.database.IDatabase;

public interface IBuildDatabaseForChaseStep {

    IDatabase extractDatabase(String stepId, IDatabase deltaDB, IDatabase originalDB, Dependency dependency);
    IDatabase extractDatabase(String stepId, IDatabase deltaDB, IDatabase originalDB);
    IDatabase extractDatabaseWithDistinct(String stepId, IDatabase deltaDB, IDatabase originalDB);

}
