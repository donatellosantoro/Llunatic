package it.unibas.lunatic.model.chase.chasemc.operators;

import it.unibas.lunatic.LunaticConstants;
import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.exceptions.ChaseException;
import it.unibas.lunatic.model.algebra.IAlgebraOperator;
import it.unibas.lunatic.model.chase.commons.ChaseStats;
import it.unibas.lunatic.model.chase.commons.ChaseUtility;
import it.unibas.lunatic.model.chase.commons.control.IChaseState;
import it.unibas.lunatic.model.database.IDatabase;
import it.unibas.lunatic.model.dependency.Dependency;
import it.unibas.lunatic.model.chase.chasemc.DeltaChaseStep;
import java.util.Date;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChaseDeltaExtTGDsWithoutCellGroups implements IChaseDeltaExtTGDs {

    public static final int ITERATION_LIMIT = 10;
    private static Logger logger = LoggerFactory.getLogger(ChaseDeltaExtTGDsWithoutCellGroups.class);

//    private InsertTuplesForTgdsWithoutCellGroups insertTuples;
    private IInsertTuplesForTGDs insertTuples;
    private IBuildDatabaseForChaseStep databaseBuilder;

    public ChaseDeltaExtTGDsWithoutCellGroups(IInsertTuplesForTGDs insertTuples, IBuildDatabaseForChaseStep databaseBuilder) {
        this.insertTuples = insertTuples;
        this.databaseBuilder = databaseBuilder;
    }

    public boolean doChase(DeltaChaseStep treeRoot, Scenario scenario, IChaseState chaseState, Map<Dependency, IAlgebraOperator> tgdTreeMap, Map<Dependency, IAlgebraOperator> tgdQuerySatisfactionMap) {
        if (scenario.getExtTGDs().isEmpty()) {
            return false;
        }
        long start = new Date().getTime();
        int size = treeRoot.getNumberOfNodes();
        chaseTree(treeRoot, scenario, chaseState, tgdTreeMap);
        int newSize = treeRoot.getNumberOfNodes();
        long end = new Date().getTime();
        ChaseStats.getInstance().addStat(ChaseStats.TGD_TIME, end - start);
        return (size != newSize);
    }

    private void chaseTree(DeltaChaseStep treeRoot, Scenario scenario, IChaseState chaseState, Map<Dependency, IAlgebraOperator> tgdTreeMap) {
        if (treeRoot.isInvalid()) {
            return;
        }
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
        if (node.isEditedByUser()) {
            DeltaChaseStep newStep = new DeltaChaseStep(scenario, node, LunaticConstants.CHASE_USER, LunaticConstants.CHASE_USER);
            node.addChild(newStep);
            chaseNode(newStep, scenario, chaseState, tgdTreeMap);
            return;
        }
        if (logger.isDebugEnabled()) logger.debug("Chasing ext tgds on scenario: " + scenario);
        boolean modified = false;
        int iterations = 0;
        String localId = "t";
        DeltaChaseStep newStep = new DeltaChaseStep(scenario, node, localId, LunaticConstants.CHASE_STEP_TGD);
        while (true) {
            boolean insertedTuples = false;
            IDatabase databaseForStep = databaseBuilder.extractDatabase(newStep.getId(), newStep.getDeltaDB(), newStep.getOriginalDB());
            for (Dependency eTgd : scenario.getExtTGDs()) {
                if (chaseState.isCancelled()) ChaseUtility.stopChase(chaseState); //throw new ChaseException("Chase interrupted by user");
                if (logger.isDebugEnabled()) logger.debug("----Chasing tgd: " + eTgd);
                if (logger.isDebugEnabled()) logger.debug("----Current leaf: " + newStep);
                IAlgebraOperator tgdQuery = tgdTreeMap.get(eTgd);
                if (logger.isDebugEnabled()) logger.debug("----TGD Query: " + tgdQuery);
//                insertedTuples = insertTuples.execute(tgdQuery, newStep, eTgd, scenario) || insertedTuples;
                insertedTuples = insertTuples.execute(tgdQuery, newStep, eTgd, scenario, databaseForStep) || insertedTuples;
            }
            if (!insertedTuples) {
                break;
            } else {
                iterations++;
                modified = true;
            }
            if (iterations > ITERATION_LIMIT) {
                throw new ChaseException("Reached iteration limit " + ITERATION_LIMIT + " with no solution...");
            }
        }
        if (modified) {
            node.addChild(newStep);
        }
    }
}
