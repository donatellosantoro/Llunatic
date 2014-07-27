package it.unibas.lunatic.model.chase.chasede.operators;

import it.unibas.lunatic.model.algebra.IAlgebraOperator;
import it.unibas.lunatic.model.database.IDatabase;
import it.unibas.lunatic.model.dependency.Dependency;


public interface IInsertFromSelectNaive {

    boolean execute(Dependency dependency, IAlgebraOperator sourceQuery, IDatabase source, IDatabase target);

}
