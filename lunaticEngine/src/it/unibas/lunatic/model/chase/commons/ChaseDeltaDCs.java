package it.unibas.lunatic.model.chase.commons;

import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.model.chase.commons.control.IChaseState;
import it.unibas.lunatic.model.chase.chasemc.operators.IBuildDatabaseForChaseStep;
import it.unibas.lunatic.model.chase.chasemc.DeltaChaseStep;
import speedy.model.database.IDatabase;
import it.unibas.lunatic.model.dependency.Dependency;
import java.util.Date;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import speedy.model.algebra.IAlgebraOperator;
import speedy.model.algebra.operators.ITupleIterator;
import speedy.model.database.operators.IRunQuery;

public class ChaseDeltaDCs {

    private static Logger logger = LoggerFactory.getLogger(ChaseDeltaDCs.class);
    private IRunQuery queryRunner;
    private IBuildDatabaseForChaseStep databaseBuilder;

    public ChaseDeltaDCs(IRunQuery queryRunner, IBuildDatabaseForChaseStep databaseBuilder) {
        this.queryRunner = queryRunner;
        this.databaseBuilder = databaseBuilder;
    }

    public void doChase(DeltaChaseStep treeRoot, Scenario scenario, IChaseState chaseState, Map<Dependency, IAlgebraOperator> tgdTreeMap) {
        if (scenario.getDCs().isEmpty()) return;
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
        if (node.isDuplicate() || node.isInvalid()) {
            return;
        }
        if (logger.isTraceEnabled()) logger.trace("Chasing dcs on scenario: " + scenario);
        IDatabase databaseForStep = databaseBuilder.extractDatabase(node.getId(), node.getDeltaDB(), node.getOriginalDB(), scenario);
        for (Dependency dc : scenario.getDCs()) {
            if (chaseState.isCancelled()) {
                ChaseUtility.stopChase(chaseState); //throw new ChaseException("Chase interrupted by user");
            }
            long startDc = new Date().getTime();
            if (logger.isTraceEnabled()) logger.trace("----Chasing dc: " + dc);
            if (logger.isTraceEnabled()) logger.trace("----Current leaf: " + node.getId());
            IAlgebraOperator tgdQuery = tgdTreeMap.get(dc);
            if (logger.isTraceEnabled()) logger.trace("----DC Query: " + tgdQuery);
            ITupleIterator it = queryRunner.run(tgdQuery, scenario.getSource(), databaseForStep);
            long endDc = new Date().getTime();
            ChaseStats.getInstance().addDepenendecyStat(dc, endDc - startDc);
            if (it.hasNext()) {
                node.setInvalid(true);
                if (logger.isDebugEnabled()) logger.debug("Chase fails. Denial constraint \n" + dc + "\nis violated");
//                throw new ChaseException("Chase fails. Denial constraint \n" + dc + "\nis violated on node \n" + node);
            }
            it.close();
        }
    }
}
