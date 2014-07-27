package it.unibas.lunatic.model.chase.chasemc.operators.mainmemory;

import it.unibas.lunatic.model.algebra.IAlgebraOperator;
import it.unibas.lunatic.model.algebra.operators.ITupleIterator;
import it.unibas.lunatic.model.chase.chasemc.operators.IRunQuery;
import it.unibas.lunatic.model.database.IDatabase;

public class MainMemoryRunQuery implements IRunQuery {

    public ITupleIterator run(IAlgebraOperator query, IDatabase source, IDatabase target) {
        return query.execute(source, target);
    }

    public boolean isUseTrigger() {
        return false;
    }

}
