package it.unibas.lunatic.model.chase.commons;

import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.model.algebra.IAlgebraOperator;
import it.unibas.lunatic.model.algebra.operators.ITupleIterator;
import it.unibas.lunatic.model.chase.commons.control.IChaseState;
import it.unibas.lunatic.model.chase.chasemc.operators.IBuildDatabaseForChaseStep;
import it.unibas.lunatic.model.chase.chasemc.operators.IRunQuery;
import it.unibas.lunatic.model.chase.chasemc.DeltaChaseStep;
import it.unibas.lunatic.model.database.IDatabase;
import it.unibas.lunatic.model.dependency.Dependency;
import java.util.Date;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChaseDeltaDCs {

    private static Logger logger = LoggerFactory.getLogger(ChaseDeltaDCs.class);
    private IRunQuery queryRunner;
    private IBuildDatabaseForChaseStep databaseBuilder;

    public ChaseDeltaDCs(IRunQuery queryRunner, IBuildDatabaseForChaseStep databaseBuilder) {
        this.queryRunner = queryRunner;
        this.databaseBuilder = databaseBuilder;
    }

    public void doChase(DeltaChaseStep treeRoot, Scenario scenario, IChaseState chaseState, Map<Dependency, IAlgebraOperator> tgdTreeMap) {
        long start = new Date().getTime();
        chaseTree(treeRoot, scenario, chaseState, tgdTreeMap);
        long end = new Date().getTime();
        ChaseStats.getInstance().addStat(ChaseStats.DTGD_TIME, end - start);
    }

    private void chaseTree(DeltaChaseStep treeRoot, Scenario scenario, IChaseState chaseState, Map<Dependency, IAlgebraOperator> tgdTreeMap) {
        if (treeRoot.isLeaf()) {
            chaseNode((DeltaChaseStep) treeRoot, scenario, chaseState, tgdTreeMap);
        }
        for (DeltaChaseStep child : treeRoot.getChildren()) {
            chaseTree(child, scenario, chaseState, tgdTreeMap);
        }
    }

    private void chaseNode(DeltaChaseStep node, Scenario scenario, IChaseState chaseState, Map<Dependency, IAlgebraOperator> tgdTreeMap) {
        if (logger.isDebugEnabled()) logger.debug("Chasing dcs on scenario: " + scenario);
        for (Dependency dc : scenario.getDCs()) {
            if (chaseState.isCancelled()) {
                ChaseUtility.stopChase(chaseState); //throw new ChaseException("Chase interrupted by user");
            }
            if (logger.isDebugEnabled()) logger.debug("----Chasing dc: " + dc);
            if (logger.isDebugEnabled()) logger.debug("----Current leaf: " + node);
            IAlgebraOperator tgdQuery = tgdTreeMap.get(dc);
            if (logger.isDebugEnabled()) logger.debug("----DC Query: " + tgdQuery);
            IDatabase databaseForStep = databaseBuilder.extractDatabase(node.getId(), node.getDeltaDB(), node.getOriginalDB());
            ITupleIterator it = queryRunner.run(tgdQuery, scenario.getSource(), databaseForStep);
            if (it.hasNext()) {
                node.setInvalid(true);
//                throw new ChaseException("Chase fails. Denial constraint \n" + dTgd + "\nis violated on node \n" + node);
            }
        }
    }
}
