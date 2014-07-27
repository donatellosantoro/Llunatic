package it.unibas.lunatic.model.chase.chasemc.operators;

import it.unibas.lunatic.model.algebra.IAlgebraOperator;
import it.unibas.lunatic.model.algebra.operators.ITupleIterator;
import it.unibas.lunatic.model.database.IDatabase;

public interface IRunQuery {

    ITupleIterator run(IAlgebraOperator query, IDatabase source, IDatabase target);

    public boolean isUseTrigger();

}
