package it.unibas.lunatic.model.chase.commons.operators;

import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.model.dependency.Dependency;
import speedy.model.database.IDatabase;

public interface IBuildDatabaseForChaseStep {

    IDatabase extractDatabase(String stepId, IDatabase deltaDB, IDatabase originalDB, Dependency dependency, Scenario scenario);
    IDatabase extractDatabase(String stepId, IDatabase deltaDB, IDatabase originalDB, Scenario scenario);
    IDatabase extractDatabaseWithDistinct(String stepId, IDatabase deltaDB, IDatabase originalDB, Scenario scenario);

}
