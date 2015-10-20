package it.unibas.lunatic.model.chase.chasemc.operators;

import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.model.chase.chasemc.DeltaChaseStep;
import it.unibas.lunatic.model.chase.chasemc.NewChaseSteps;
import it.unibas.lunatic.model.chase.commons.control.IChaseState;
import it.unibas.lunatic.model.dependency.Dependency;
import speedy.model.algebra.IAlgebraOperator;
import speedy.model.database.IDatabase;

public interface IChaseEGDEquivalenceClass {

    public NewChaseSteps chaseDependency(DeltaChaseStep currentNode, Dependency egd, IAlgebraOperator premiseQuery, Scenario scenario, IChaseState chaseState, IDatabase databaseForStep);

}
