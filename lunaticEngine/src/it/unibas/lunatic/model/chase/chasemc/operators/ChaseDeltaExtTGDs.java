package it.unibas.lunatic.model.chase.chasemc.operators;

import it.unibas.lunatic.LunaticConfiguration;
import it.unibas.lunatic.LunaticConstants;
import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.exceptions.ChaseException;
import it.unibas.lunatic.model.algebra.IAlgebraOperator;
import it.unibas.lunatic.model.chase.commons.ChaseStats;
import it.unibas.lunatic.model.chase.commons.ChaseUtility;
import it.unibas.lunatic.model.chase.commons.control.IChaseState;
import it.unibas.lunatic.model.chase.chasede.operators.IUpdateCell;
import it.unibas.lunatic.model.chase.chasemc.TGDViolation;
import it.unibas.lunatic.model.database.IDatabase;
import it.unibas.lunatic.model.dependency.Dependency;
import it.unibas.lunatic.model.chase.chasemc.DeltaChaseStep;
import it.unibas.lunatic.utility.LunaticUtility;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChaseDeltaExtTGDs implements IChaseDeltaExtTGDs {

    public static final int ITERATION_LIMIT = 10;
    private static Logger logger = LoggerFactory.getLogger(ChaseDeltaExtTGDs.class);

    private IInsertTuplesForTGDs insertTuples;
    private IMaintainCellGroupsForTGD cellGroupMantainer;
    private IBuildDatabaseForChaseStep databaseBuilder;

    public ChaseDeltaExtTGDs(IInsertTuplesForTGDs insertTuples, IRunQuery queryRunner, IBuildDatabaseForChaseStep databaseBuilder,
            IValueOccurrenceHandlerMC occurrenceHandler, IUpdateCell cellUpdater,
            IMaintainCellGroupsForTGD cellGroupMantainer) {
        this.insertTuples = insertTuples;
        this.databaseBuilder = databaseBuilder;
        this.cellGroupMantainer = cellGroupMantainer;
    }

    @Override
    public boolean doChase(DeltaChaseStep treeRoot, Scenario scenario, IChaseState chaseState, Map<Dependency, IAlgebraOperator> tgdTreeMap, Map<Dependency, IAlgebraOperator> tgdQuerySatisfactionMap) {
        if (scenario.getExtTGDs().isEmpty()) {
            return false;
        }
        long start = new Date().getTime();
        int size = treeRoot.getNumberOfNodes();
        chaseTree(treeRoot, scenario, chaseState, tgdTreeMap, tgdQuerySatisfactionMap);
        int newSize = treeRoot.getNumberOfNodes();
        long end = new Date().getTime();
        ChaseStats.getInstance().addStat(ChaseStats.TGD_TIME, end - start);
        return (size != newSize);
    }

    private void chaseTree(DeltaChaseStep treeRoot, Scenario scenario, IChaseState chaseState, Map<Dependency, IAlgebraOperator> tgdTreeMap, Map<Dependency, IAlgebraOperator> tgdQuerySatisfactionMap) {
        if (treeRoot.isInvalid()) {
            return;
        }
        if (treeRoot.isLeaf()) {
            chaseNode((DeltaChaseStep) treeRoot, scenario, chaseState, tgdTreeMap, tgdQuerySatisfactionMap);
        }
        for (DeltaChaseStep child : treeRoot.getChildren()) {
            chaseTree(child, scenario, chaseState, tgdTreeMap, tgdQuerySatisfactionMap);
        }
    }

    private void chaseNode(DeltaChaseStep node, Scenario scenario, IChaseState chaseState, Map<Dependency, IAlgebraOperator> tgdTreeMap, Map<Dependency, IAlgebraOperator> tgdQuerySatisfactionMap) {
        if (node.isDuplicate() || node.isInvalid()) {
            return;
        }
        if (node.isEditedByUser()) {
            DeltaChaseStep newStep = new DeltaChaseStep(scenario, node, LunaticConstants.CHASE_USER, LunaticConstants.CHASE_USER);
            node.addChild(newStep);
            chaseNode(newStep, scenario, chaseState, tgdTreeMap, tgdQuerySatisfactionMap);
            return;
        }
        if (LunaticConfiguration.sout) System.out.println("******Chasing node for tgds: " + node.getId());
        if (logger.isDebugEnabled()) logger.debug("Chasing ext tgds:\n" + LunaticUtility.printCollection(scenario.getExtTGDs()) + "\non tree: " + node);
        int iterations = 0;
        while (true) {
            boolean newNode = false;
            for (Dependency eTgd : scenario.getExtTGDs()) {
                if (chaseState.isCancelled()) {
                    ChaseUtility.stopChase(chaseState);
                }
                long startTgd = new Date().getTime();
                String localId = ChaseUtility.generateChaseStepIdForTGDs(eTgd);
                DeltaChaseStep newStep = new DeltaChaseStep(scenario, node, localId, LunaticConstants.CHASE_STEP_TGD);
                if (logger.isDebugEnabled()) logger.debug("----Chasing tgd: " + eTgd);
                if (logger.isDebugEnabled()) logger.debug("----Current leaf: " + newStep);
                IAlgebraOperator tgdQuery = tgdTreeMap.get(eTgd);
                if (logger.isDebugEnabled()) logger.debug("----TGD Query: " + tgdQuery);
                IDatabase databaseForStep = databaseBuilder.extractDatabase(newStep.getId(), newStep.getDeltaDB(), newStep.getOriginalDB(), eTgd);
                Set<TGDViolation> tgdViolations = cellGroupMantainer.extractViolationValues(eTgd, tgdQuery, databaseForStep, scenario);
                long start = new Date().getTime();
                boolean insertedTuples = insertTuples.execute(tgdQuery, newStep, eTgd, scenario, databaseForStep);
                long end = new Date().getTime();
                if (LunaticConfiguration.sout) System.out.println("insertTuples Execution time: " + (end - start) + " ms");
                if (insertedTuples) {
                    if (logger.isDebugEnabled()) logger.debug("Tuples have been inserted, adding new step to tree...");
                    databaseForStep = databaseBuilder.extractDatabase(newStep.getId(), newStep.getDeltaDB(), newStep.getOriginalDB(), eTgd);
                    IAlgebraOperator tgdSatisfactionQuery = tgdQuerySatisfactionMap.get(eTgd);
                    start = new Date().getTime();
                    cellGroupMantainer.maintainCellGroupsForTGD(eTgd, tgdSatisfactionQuery, tgdViolations, node.getDeltaDB(), newStep.getId(), databaseForStep, scenario);
                    end = new Date().getTime();
                    if (LunaticConfiguration.sout) System.out.println("maintainCellGroupsForTGD Execution time: " + (end - start) + " ms");
                    node.addChild(newStep);
                    node = newStep;
                    newNode = true;
                }
                long endTgd = new Date().getTime();
                ChaseStats.getInstance().addDepenendecyStat(eTgd, endTgd - startTgd);
            }
            if (!newNode) {
                if (logger.isDebugEnabled()) logger.debug("***** No new nodes, exit tgd chase...");
                return;
            } else {
                if (logger.isDebugEnabled()) logger.debug("***** There are new nodes, cycling the chase...");
                iterations++;
            }
            if (iterations > ITERATION_LIMIT) {
                throw new ChaseException("Reached iteration limit " + ITERATION_LIMIT + " with no solution...");
            }
        }
    }
}
