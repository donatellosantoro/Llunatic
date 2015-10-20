package it.unibas.lunatic.model.chase.chasede.operators.mainmemory;

import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.exceptions.ChaseException;
import it.unibas.lunatic.model.algebra.operators.BuildAlgebraTree;
import it.unibas.lunatic.model.chase.commons.ChaseStats;
import it.unibas.lunatic.model.chase.commons.IChaseSTTGDs;
import it.unibas.lunatic.model.chase.chasede.operators.IRemoveDuplicates;
import speedy.model.database.IDatabase;
import it.unibas.lunatic.model.dependency.Dependency;
import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import speedy.model.algebra.IAlgebraOperator;
import speedy.model.database.mainmemory.MainMemoryDB;

public class ChaseMainMemorySTTGDs implements IChaseSTTGDs {

    private static Logger logger = LoggerFactory.getLogger(ChaseMainMemorySTTGDs.class);

    private BuildAlgebraTree treeBuilder = new BuildAlgebraTree();
    private IRemoveDuplicates duplicateRemover = new MainMemoryRemoveDuplicates();

    @Override
    public void doChase(Scenario scenario, boolean cleanTarget) {
        if (!scenario.isMainMemory()) {
            throw new ChaseException("Unable to chase scenario: data sources are not in main-memory");
        }
        long start = new Date().getTime();
        IDatabase target = scenario.getTarget();
        if (logger.isDebugEnabled()) logger.debug("Chasing st tgds on scenario: " + scenario);
        for (Dependency stTgd : scenario.getSTTgds()) {
            if (logger.isDebugEnabled()) logger.debug("----Chasing tgd: " + stTgd);
            IAlgebraOperator treeRoot = treeBuilder.buildTreeForPremise(stTgd, scenario);
            if (logger.isDebugEnabled()) logger.debug("----Algebra tree: " + treeRoot);
            MainMemoryInsertFromSelectNaive insert = new MainMemoryInsertFromSelectNaive();
            insert.execute(stTgd, treeRoot, (MainMemoryDB) scenario.getSource(), (MainMemoryDB) target);
        }
        duplicateRemover.removeDuplicatesModuloOID((MainMemoryDB) target);
        long end = new Date().getTime();
        ChaseStats.getInstance().addStat(ChaseStats.STTGD_TIME, end - start);
        if (logger.isDebugEnabled()) logger.debug("----Result of chasing st tgds: " + target);
    }
}
