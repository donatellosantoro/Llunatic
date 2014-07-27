package it.unibas.lunatic.model.algebra.operators;

import it.unibas.lunatic.model.algebra.IAlgebraOperator;
import it.unibas.lunatic.model.database.IDatabase;

public interface IDelete {

    boolean execute(String tableName, IAlgebraOperator sourceQuery, IDatabase source, IDatabase target);

}
