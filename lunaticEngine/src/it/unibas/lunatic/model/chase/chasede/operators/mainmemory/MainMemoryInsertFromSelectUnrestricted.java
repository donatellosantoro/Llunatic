package it.unibas.lunatic.model.chase.chasede.operators.mainmemory;

import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.model.chase.chasede.operators.IInsertFromSelectNaive;
import it.unibas.lunatic.model.chase.chasede.operators.IRemoveDuplicates;
import speedy.model.database.IDatabase;
import it.unibas.lunatic.model.dependency.Dependency;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import speedy.model.algebra.IAlgebraOperator;

public class MainMemoryInsertFromSelectUnrestricted implements IInsertFromSelectNaive {

    private static Logger logger = LoggerFactory.getLogger(MainMemoryInsertFromSelectUnrestricted.class);

    private IRemoveDuplicates duplicateRemover = new MainMemoryRemoveDuplicates();
    private IInsertFromSelectNaive naiveInsert = new MainMemoryInsertFromSelectRestricted();

    @Override
    public boolean execute(Dependency dependency, IAlgebraOperator sourceQuery, IDatabase source, IDatabase target, Scenario scenario) {
        long size = target.getSize();
        naiveInsert.execute(dependency, sourceQuery, source, target, scenario);
        duplicateRemover.removeDuplicatesModuloOID(target, scenario);
        long newSize = target.getSize();
        if (logger.isDebugEnabled()) logger.debug("Initial size: " + size);
        if (logger.isDebugEnabled()) logger.debug("Size after insert: " + newSize);
        return newSize > size;
    }

}
