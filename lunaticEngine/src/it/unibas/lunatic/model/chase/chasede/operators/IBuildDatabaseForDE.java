package it.unibas.lunatic.model.chase.chasede.operators;

import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.model.dependency.Dependency;
import speedy.model.database.IDatabase;

public interface IBuildDatabaseForDE {

    IDatabase extractDatabase(IDatabase deltaDB, IDatabase originalDB, Dependency dependency, Scenario scenario);

    IDatabase extractDatabase(IDatabase deltaDB, IDatabase originalDB, Scenario scenario);

    IDatabase extractDatabaseWithDistinct(IDatabase deltaDB, IDatabase originalDB, Scenario scenario);

}
