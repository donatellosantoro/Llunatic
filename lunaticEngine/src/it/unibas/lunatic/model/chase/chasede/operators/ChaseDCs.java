package it.unibas.lunatic.model.chase.chasede.operators;

import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.exceptions.ChaseException;
import it.unibas.lunatic.exceptions.ChaseFailedException;
import it.unibas.lunatic.model.algebra.IAlgebraOperator;
import it.unibas.lunatic.model.algebra.operators.BuildAlgebraTree;
import it.unibas.lunatic.model.algebra.operators.ITupleIterator;
import it.unibas.lunatic.model.chase.commons.ChaseUtility;
import it.unibas.lunatic.model.chase.commons.control.IChaseState;
import it.unibas.lunatic.model.chase.chasemc.operators.IRunQuery;
import it.unibas.lunatic.model.chase.commons.ChaseStats;
import it.unibas.lunatic.model.dependency.Dependency;
import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChaseDCs {

    private static Logger logger = LoggerFactory.getLogger(ChaseDCs.class);

    private IRunQuery queryRunner;
    private BuildAlgebraTree treeBuilder = new BuildAlgebraTree();

    public ChaseDCs(IRunQuery queryRunner) {
        this.queryRunner = queryRunner;
    }

    public void doChase(Scenario scenario, IChaseState chaseState) {
        long start = new Date().getTime();
        if (logger.isDebugEnabled()) logger.debug("Chasing dtgds on scenario: " + scenario);
        for (Dependency dc : scenario.getDCs()) {
            if (chaseState.isCancelled()) ChaseUtility.stopChase(chaseState); //throw new ChaseException("Chase interrupted by user");
            if (logger.isDebugEnabled()) logger.debug("----Chasing denial constraint: " + dc);
            IAlgebraOperator treeRoot = treeBuilder.buildTreeForPremise(dc, scenario);
            ITupleIterator result = queryRunner.run(treeRoot, scenario.getSource(), scenario.getTarget());
            if (result.hasNext()) {
                result.close();
                throw new ChaseFailedException("Chase fails. Denial constraint is violated: " + dc);
            }
            result.close();
        }
        long end = new Date().getTime();
        ChaseStats.getInstance().addStat(ChaseStats.DTGD_TIME, end - start);
    }
}
