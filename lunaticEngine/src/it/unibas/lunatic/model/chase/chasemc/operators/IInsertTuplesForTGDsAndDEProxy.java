package it.unibas.lunatic.model.chase.chasemc.operators;

import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.model.algebra.IAlgebraOperator;
import it.unibas.lunatic.model.chase.chasemc.DeltaChaseStep;
import it.unibas.lunatic.model.database.IDatabase;
import it.unibas.lunatic.model.dependency.Dependency;

public interface IInsertTuplesForTGDsAndDEProxy {

    public boolean execute(IAlgebraOperator violationQuery, DeltaChaseStep currentNode, Dependency tgd, Scenario scenario, IDatabase databaseForStep);

    public void initializeOIDs(IDatabase database);
}
