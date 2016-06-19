package it.unibas.lunatic.model.chase.chasede.operators;

import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.exceptions.ChaseException;
import it.unibas.lunatic.exceptions.ChaseFailedException;
import it.unibas.lunatic.model.algebra.operators.BuildAlgebraTree;
import it.unibas.lunatic.model.chase.commons.operators.ChaseUtility;
import it.unibas.lunatic.model.chase.commons.IChaseState;
import it.unibas.lunatic.model.dependency.Dependency;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import speedy.model.algebra.IAlgebraOperator;
import speedy.model.algebra.operators.ITupleIterator;
import speedy.model.database.operators.IRunQuery;

public class ChaseDCs {

    private final static Logger logger = LoggerFactory.getLogger(ChaseDCs.class);

    private IRunQuery queryRunner;
    private BuildAlgebraTree treeBuilder = new BuildAlgebraTree();

    public ChaseDCs(IRunQuery queryRunner) {
        this.queryRunner = queryRunner;
    }

    public void doChase(Scenario scenario, IChaseState chaseState) {
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
    }
}
