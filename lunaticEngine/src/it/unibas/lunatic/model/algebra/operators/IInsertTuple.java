package it.unibas.lunatic.model.algebra.operators;

import it.unibas.lunatic.model.database.IDatabase;
import it.unibas.lunatic.model.database.ITable;
import it.unibas.lunatic.model.database.Tuple;

public interface IInsertTuple {

    void execute(ITable table, Tuple tuple, IDatabase source, IDatabase target);

}
