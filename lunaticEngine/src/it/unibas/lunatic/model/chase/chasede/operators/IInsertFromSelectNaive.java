package it.unibas.lunatic.model.chase.chasede.operators;

import it.unibas.lunatic.Scenario;
import speedy.model.database.IDatabase;
import it.unibas.lunatic.model.dependency.Dependency;
import speedy.model.algebra.IAlgebraOperator;


public interface IInsertFromSelectNaive {

    boolean execute(Dependency dependency, IAlgebraOperator sourceQuery, IDatabase source, IDatabase target, Scenario scenario);

}
