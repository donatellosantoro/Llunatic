package it.unibas.lunatic.model.chase.chasede.operators;

import it.unibas.lunatic.LunaticConfiguration;
import it.unibas.lunatic.LunaticConstants;
import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.exceptions.ChaseException;
import it.unibas.lunatic.model.chase.chasemc.DeltaChaseStep;
import it.unibas.lunatic.model.chase.commons.operators.IBuildDatabaseForChaseStep;
import it.unibas.lunatic.model.chase.chasemc.operators.IChaseDeltaExtTGDs;
import it.unibas.lunatic.model.chase.commons.ChaseStats;
import it.unibas.lunatic.model.chase.commons.operators.ChaseUtility;
import it.unibas.lunatic.model.chase.commons.IChaseState;
import it.unibas.lunatic.model.dependency.Dependency;
import it.unibas.lunatic.model.dependency.DependencyStratification;
import it.unibas.lunatic.model.dependency.TGDStratum;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import speedy.model.algebra.IAlgebraOperator;
import speedy.model.database.IDatabase;

public class ChaseDeltaTargetTGDsWithBatchInsert implements IChaseDeltaExtTGDs {

    public static final int ITERATION_LIMIT = 10;
    private static Logger logger = LoggerFactory.getLogger(ChaseDeltaTargetTGDsWithBatchInsert.class);

    private IInsertDeltaTuplesForTargetTGDs insertTuples;
    private IBuildDatabaseForChaseStep databaseBuilder;

    public ChaseDeltaTargetTGDsWithBatchInsert(IInsertDeltaTuplesForTargetTGDs insertTuples, IBuildDatabaseForChaseStep databaseBuilder) {
        this.insertTuples = insertTuples;
        this.databaseBuilder = databaseBuilder;
    }

    public boolean doChase(DeltaChaseStep treeRoot, Scenario scenario, IChaseState chaseState, Map<Dependency, IAlgebraOperator> tgdTreeMap, Map<Dependency, IAlgebraOperator> tgdQuerySatisfactionMap) {
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
        } else {
            for (DeltaChaseStep child : treeRoot.getChildren()) {
                chaseTree(child, scenario, chaseState, tgdTreeMap);
            }
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
        if (LunaticConfiguration.isPrintSteps()) System.out.println("  ****Chasing node " + node.getId() + " for tgds...");
        if (logger.isDebugEnabled()) logger.debug("Chasing ext tgds on scenario: " + scenario);
        boolean modified = false;
        String localId = "t";
        DeltaChaseStep newStep = new DeltaChaseStep(scenario, node, localId, LunaticConstants.CHASE_STEP_TGD);
        DependencyStratification stratification = scenario.getStratification();
        for (TGDStratum stratum : stratification.getTGDStrata()) {
            int iterations = 0;
            if (LunaticConfiguration.isPrintSteps()) System.out.println("---- Chasing tgd stratum: " + stratum.getId());
            if (logger.isDebugEnabled()) logger.debug("------------------Chasing stratum: ----\n" + stratum);
            List<Dependency> tgds = stratum.getTgds();
            Set<Dependency> unsatisfiedTGDs = new HashSet<Dependency>(tgds);
            while (true) {
                if (logger.isDebugEnabled()) logger.debug("Unsatisfied TGDs: " + unsatisfiedTGDs);
                boolean insertedTuples = false;
                for (Dependency eTgd : tgds) {
                    if (chaseState.isCancelled()) {
                        ChaseUtility.stopChase(chaseState); //throw new ChaseException("Chase interrupted by user");
                    }
                    if (!unsatisfiedTGDs.contains(eTgd)) {
                        continue;
                    }
                    if (LunaticConfiguration.isPrintSteps()) System.out.print("   ****Building database for step " + newStep.getId() + " and tgd " + eTgd.getId() + "...");
//                    IDatabase databaseForStep = databaseBuilder.extractDatabase(newStep.getId(), newStep.getDeltaDB(), newStep.getOriginalDB(), scenario);
                    IDatabase databaseForStep = databaseBuilder.extractDatabase(newStep.getId(), newStep.getDeltaDB(), newStep.getOriginalDB(), eTgd, scenario);
                    if (LunaticConfiguration.isPrintSteps()) System.out.println(" DONE");
                    if (LunaticConfiguration.isPrintSteps()) System.out.println("   ****Chasing tgd: " + eTgd.getId());
                    if (logger.isDebugEnabled()) logger.debug("----Chasing tgd: " + eTgd);
                    if (logger.isDebugEnabled()) logger.debug("----Current leaf: " + newStep);
                    IAlgebraOperator tgdQuery = tgdTreeMap.get(eTgd);
                    if (logger.isDebugEnabled()) logger.debug("----TGD Query: " + tgdQuery);
                    boolean newTuples = insertTuples.execute(tgdQuery, newStep, eTgd, scenario, databaseForStep);
                    unsatisfiedTGDs.remove(eTgd);
                    if (newTuples) {
                        if (logger.isDebugEnabled()) logger.debug("TGD " + eTgd.getId() + " inserted new tuples. Marking as unsatisfied the dependencies " + scenario.getStratification().getAffectedTGDsMap().get(eTgd));
                        unsatisfiedTGDs.addAll(scenario.getStratification().getAffectedTGDsMap().get(eTgd));
                    }
                    insertedTuples = newTuples || insertedTuples;
                }
                if (!insertedTuples) {
                    break;
                } else {
                    if (logger.isDebugEnabled()) logger.debug("***** There are new nodes, cycling the chase... Iteration " + iterations);
                    iterations++;
                    modified = true;
                }
                if (iterations > ITERATION_LIMIT) {
                    throw new ChaseException("Reached iteration limit " + ITERATION_LIMIT + " with no solution...");
                }
            }
        }
        if (modified) {
            node.addChild(newStep);
        }
    }

    @Override
    public void initializeOIDs(IDatabase targetDB, Scenario scenario) {
        insertTuples.initializeOIDs(targetDB, scenario);
    }
}
