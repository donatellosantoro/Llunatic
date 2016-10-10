package it.unibas.lunatic.model.chase.chasemc.operators;

import it.unibas.lunatic.LunaticConfiguration;
import it.unibas.lunatic.LunaticConstants;
import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.exceptions.ChaseException;
import it.unibas.lunatic.model.chase.commons.ChaseStats;
import it.unibas.lunatic.model.chase.commons.operators.ChaseUtility;
import it.unibas.lunatic.model.chase.commons.IChaseState;
import it.unibas.lunatic.model.dependency.Dependency;
import it.unibas.lunatic.model.chase.chasemc.DeltaChaseStep;
import it.unibas.lunatic.model.dependency.DependencyStratification;
import it.unibas.lunatic.model.dependency.TGDStratum;
import it.unibas.lunatic.utility.LunaticUtility;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import speedy.model.algebra.IAlgebraOperator;
import speedy.model.database.IDatabase;
import speedy.model.database.operators.IRunQuery;

public class ChaseDeltaExtTGDs implements IChaseDeltaExtTGDs {

    public static final int ITERATION_LIMIT = 10;
    private static final Logger logger = LoggerFactory.getLogger(ChaseDeltaExtTGDs.class);

    private final ChaseTGDEquivalenceClass dependencyChaser;
    private final IBuildDatabaseForChaseStepMC databaseBuilder;
    private final IOIDGenerator oidGenerator;

    public ChaseDeltaExtTGDs(IRunQuery queryRunner, IBuildDatabaseForChaseStepMC databaseBuilder, OccurrenceHandlerMC occurrenceHandler, IOIDGenerator oidGenerator, ChangeCellMC cellChanger) {
        this.databaseBuilder = databaseBuilder;
        this.oidGenerator = oidGenerator;
        this.dependencyChaser = new ChaseTGDEquivalenceClass(queryRunner, oidGenerator, occurrenceHandler, cellChanger);
    }

    @Override
    public boolean doChase(DeltaChaseStep treeRoot, Scenario scenario, IChaseState chaseState, Map<Dependency, IAlgebraOperator> tgdTreeMap, Map<Dependency, IAlgebraOperator> tgdQuerySatisfactionMap) {
        if (scenario.getExtTGDs().isEmpty()) {
            return false;
        }
        long start = new Date().getTime();
        int size = treeRoot.getNumberOfNodes();
        DependencyStratification stratification = scenario.getStratification();
        for (TGDStratum stratum : stratification.getTGDStrata()) {
            if (LunaticConfiguration.isPrintSteps()) System.out.println("---- Chasing tgd stratum: " + stratum.getId());
            if (logger.isDebugEnabled()) logger.debug("------------------Chasing stratum: ----\n" + stratum);
            chaseTree(treeRoot, scenario, chaseState, stratum.getTgds(), tgdTreeMap, tgdQuerySatisfactionMap);
        }
        int newSize = treeRoot.getNumberOfNodes();
        long end = new Date().getTime();
        ChaseStats.getInstance().addStat(ChaseStats.TGD_TIME, end - start);
        return (size != newSize);
    }

    private void chaseTree(DeltaChaseStep treeRoot, Scenario scenario, IChaseState chaseState, List<Dependency> tgds, Map<Dependency, IAlgebraOperator> tgdTreeMap, Map<Dependency, IAlgebraOperator> tgdQuerySatisfactionMap) {
        if (treeRoot.isInvalid()) {
            return;
        }
        if (treeRoot.isLeaf()) {
            chaseNode((DeltaChaseStep) treeRoot, scenario, chaseState, tgds, tgdTreeMap, tgdQuerySatisfactionMap);
        } else {
            for (DeltaChaseStep child : treeRoot.getChildren()) {
                chaseTree(child, scenario, chaseState, tgds, tgdTreeMap, tgdQuerySatisfactionMap);
            }
        }
    }

    private void chaseNode(DeltaChaseStep node, Scenario scenario, IChaseState chaseState, List<Dependency> tgds, Map<Dependency, IAlgebraOperator> tgdTreeMap, Map<Dependency, IAlgebraOperator> tgdQuerySatisfactionMap) {
        if (node.isDuplicate() || node.isInvalid()) {
            return;
        }
        if (node.isEditedByUser()) {
            DeltaChaseStep newStep = new DeltaChaseStep(scenario, node, LunaticConstants.CHASE_USER, LunaticConstants.CHASE_USER);
            node.addChild(newStep);
            chaseNode(newStep, scenario, chaseState, tgds, tgdTreeMap, tgdQuerySatisfactionMap);
            return;
        }
        if (LunaticConfiguration.isPrintSteps()) System.out.println("  ****Chasing node " + node.getId() + " for tgds " + tgds + "...");
        if (logger.isDebugEnabled()) logger.debug("Chasing ext tgds:\n" + LunaticUtility.printCollection(tgds) + "\non tree: " + node);
        int iterations = 0;
        Set<Dependency> unsatisfiedTGDs = new HashSet<Dependency>(tgds);
        while (true) {
            if (logger.isDebugEnabled()) logger.debug("======= Starting tgd chase cycle on step " + node.getId());
            boolean newNode = false;
            for (Dependency eTgd : tgds) { //TGDs are sorted using input degree
                if (chaseState.isCancelled()) {
                    ChaseUtility.stopChase(chaseState);
                }
                if (!unsatisfiedTGDs.contains(eTgd)) {
                    continue;
                }
                long startTgd = new Date().getTime();
                String localId = ChaseUtility.generateChaseStepIdForTGDs(eTgd);
                DeltaChaseStep newStep = new DeltaChaseStep(scenario, node, localId, LunaticConstants.CHASE_STEP_TGD);
                if (logger.isDebugEnabled()) logger.debug("---- Candidate new step: " + newStep.getId() + "- Chasing tgd: " + eTgd);
                IAlgebraOperator tgdQuery = tgdTreeMap.get(eTgd);
                if (logger.isTraceEnabled()) logger.trace("----TGD Query: " + tgdQuery);
                IDatabase databaseForStep = databaseBuilder.extractDatabase(newStep.getId(), newStep.getDeltaDB(), newStep.getOriginalDB(), eTgd, scenario);
//                IDatabase databaseForStep = databaseBuilder.extractDatabase(newStep.getId(), newStep.getDeltaDB(), newStep.getOriginalDB()); //TODO Optimize deds workers
                if (logger.isTraceEnabled()) logger.trace("Database for step: " + databaseBuilder.extractDatabase(newStep.getId(), newStep.getDeltaDB(), newStep.getOriginalDB(), scenario).printInstances() + "\nDeltaDB: " + newStep.getDeltaDB().printInstances());
                long start = new Date().getTime();
                boolean insertedTuples = dependencyChaser.chaseDependency(newStep, eTgd, tgdQuery, scenario, chaseState, databaseForStep);
                long end = new Date().getTime();
                if (!(ChaseUtility.isUseLimit1ForTGD(eTgd, scenario)) || !insertedTuples) {
                    unsatisfiedTGDs.remove(eTgd);
                }
                if (LunaticConfiguration.isPrintSteps()) System.out.println("Dependency chasing Execution time: " + (end - start) + " ms");
                ChaseStats.getInstance().addDepenendecyStat(eTgd, end - start);
                if (insertedTuples) {
                    unsatisfiedTGDs.addAll(scenario.getStratification().getAffectedTGDsMap().get(eTgd));
                    if (logger.isDebugEnabled()) logger.debug("Tuples have been inserted, adding new step to tree...");
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
                if (iterations > ITERATION_LIMIT) {
                    throw new ChaseException("Reached iteration limit " + ITERATION_LIMIT + " with no solution...");
                }
            }
        }
    }

    @Override
    public void initializeOIDs(IDatabase targetDB, Scenario scenario) {
        this.oidGenerator.initializeOIDs(targetDB, scenario);
    }
}
