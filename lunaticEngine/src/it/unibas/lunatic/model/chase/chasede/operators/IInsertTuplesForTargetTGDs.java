package it.unibas.lunatic.model.chase.chasede.operators;

import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.model.chase.chasemc.DeltaChaseStep;
import it.unibas.lunatic.model.dependency.Dependency;
import speedy.model.algebra.IAlgebraOperator;
import speedy.model.database.IDatabase;

public interface IInsertTuplesForTargetTGDs {

    boolean execute(IAlgebraOperator sourceQuery, DeltaChaseStep currentNode, Dependency tgd, Scenario scenario, IDatabase databaseForStep);

    void initializeOIDs(IDatabase database);
}
