package it.unibas.lunatic.model.chase.chasede.operators;

import it.unibas.lunatic.model.chase.commons.operators.IBuildDatabaseForChaseStep;
import it.unibas.lunatic.model.chase.chasemc.operators.*;
import it.unibas.lunatic.LunaticConfiguration;
import it.unibas.lunatic.utility.LunaticUtility;
import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.exceptions.ChaseException;
import it.unibas.lunatic.model.chase.chasemc.DeltaChaseStep;
import it.unibas.lunatic.model.chase.commons.ChaseStats;
import it.unibas.lunatic.model.chase.commons.operators.ChaseUtility;
import it.unibas.lunatic.model.chase.commons.IChaseState;
import it.unibas.lunatic.model.chase.chasemc.NewChaseSteps;
import it.unibas.lunatic.model.chase.chasemc.costmanager.CostManagerUtility;
import it.unibas.lunatic.model.dependency.Dependency;
import it.unibas.lunatic.model.dependency.DependencyStratification;
import it.unibas.lunatic.model.dependency.EGDStratum;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import speedy.model.algebra.IAlgebraOperator;
import speedy.model.algebra.operators.IBatchInsert;
import speedy.model.algebra.operators.IInsertTuple;
import speedy.model.database.IDatabase;
import speedy.model.database.operators.IRunQuery;

public class ChaseDeltaEGDs {

    private static final Logger logger = LoggerFactory.getLogger(ChaseDeltaEGDs.class);
    private final CheckUnsatisfiedDependencies unsatisfiedDependenciesChecker;
    private final IBuildDatabaseForChaseStep databaseBuilder;
    private final IChaseEGDEquivalenceClass symmetricEGDChaser;
    private final IChaseEGDEquivalenceClass egdChaser;

    public ChaseDeltaEGDs(IBuildDatabaseForChaseStep databaseBuilder, IRunQuery queryRunner,
            IInsertTuple insertOperator, IBatchInsert batchInsertOperator, IChangeCell cellChanger,
            IOccurrenceHandler occurrenceHandler, CheckUnsatisfiedDependencies unsatisfiedDependenciesChecker) {
        this.databaseBuilder = databaseBuilder;
        this.symmetricEGDChaser = new ChaseSymmetricEGDEquivalenceClass(queryRunner, occurrenceHandler, cellChanger);
        this.egdChaser = new ChaseEGDEquivalenceClass(queryRunner, occurrenceHandler, cellChanger);
        this.unsatisfiedDependenciesChecker = new CheckUnsatisfiedDependencies(databaseBuilder, occurrenceHandler, queryRunner);
    }

    public ChaserResult doChase(DeltaChaseStep root, Scenario scenario, IChaseState chaseState, Map<Dependency, IAlgebraOperator> premiseTreeMap) {
        long start = new Date().getTime();
        int size = root.getNumberOfNodes();
        DependencyStratification stratification = scenario.getStratification();
        for (EGDStratum stratum : stratification.getEGDStrata()) {
            if (LunaticConfiguration.isPrintSteps()) System.out.println("---- Chasing egd stratum: " + stratum.getId());
            if (logger.isDebugEnabled()) logger.debug("------------------Chasing stratum: ----\n" + stratum);
            chaseTree(root, scenario, chaseState, stratum.getDependencies(), premiseTreeMap);
        }
        long end = new Date().getTime();
        ChaseStats.getInstance().addStat(ChaseStats.EGD_TIME, end - start);
        int newSize = root.getNumberOfNodes();
        boolean newNodes = (size != newSize);
        return new ChaserResult(newNodes, false);
    }

    private void chaseTree(DeltaChaseStep step, Scenario scenario, IChaseState chaseState, List<Dependency> egds, Map<Dependency, IAlgebraOperator> premiseTreeMap) {
        if (step.isLeaf()) {
            chaseNode((DeltaChaseStep) step, scenario, chaseState, egds, premiseTreeMap);
            return;
        }
        chaseTree(step.getChildren().get(0), scenario, chaseState, egds, premiseTreeMap);
    }

    private IChaseEGDEquivalenceClass getChaser(Dependency egd) {
        if (egd.hasSymmetricChase()) {
            return this.symmetricEGDChaser;
        }
        return egdChaser;
    }

    private void chaseNode(DeltaChaseStep currentNode, Scenario scenario, IChaseState chaseState, List<Dependency> egds, Map<Dependency, IAlgebraOperator> premiseTreeMap) {
        if (LunaticConfiguration.isPrintSteps()) System.out.println("  ****Chasing node " + currentNode.getId() + " for egds...");
        if (logger.isDebugEnabled()) logger.debug("----Chase iteration starting on step " + currentNode.getId() + " ...");
        DeltaChaseStep newStep = null;
        List<Dependency> unsatisfiedDependencies = unsatisfiedDependenciesChecker.findUnsatisfiedEGDsNoQuery(currentNode, egds);
        List<Dependency> egdsToChase = CostManagerUtility.selectDependenciesToChase(unsatisfiedDependencies, currentNode.getRoot(), scenario.getCostManagerConfiguration());
        if (logger.isDebugEnabled()) logger.debug("----Unsatisfied Dependencies: " + LunaticUtility.printDependencyIds(unsatisfiedDependencies));
        if (logger.isDebugEnabled()) logger.debug("----Dependencies to chase: " + LunaticUtility.printDependencyIds(egdsToChase));
        for (Dependency egd : egdsToChase) {
            if (chaseState.isCancelled()) ChaseUtility.stopChase(chaseState); //throw new ChaseException("Chase interrupted by user");
            if (LunaticConfiguration.isPrintSteps()) System.out.println("\t    **Chasing edg: " + egd.getId());
            long startEgd = new Date().getTime();
            if (logger.isDebugEnabled()) logger.info("* Chasing dependency " + egd.getId() + " on step " + currentNode.getId());
            if (logger.isDebugEnabled()) logger.info("* Algebra operator " + premiseTreeMap.get(egd));
            if (logger.isDebugEnabled()) logger.debug("Building database for step id: " + currentNode.getId() + "\nDelta db:\n" + currentNode.getDeltaDB().printInstances());
            IDatabase databaseForStep = databaseBuilder.extractDatabase(currentNode.getId(), currentNode.getDeltaDB(), currentNode.getOriginalDB(), egd, scenario);
            if (logger.isTraceEnabled()) logger.trace("Database for step id: " + currentNode.getId() + "\n" + databaseForStep.printInstances());
            IChaseEGDEquivalenceClass chaser = getChaser(egd);
            NewChaseSteps newChaseSteps = chaser.chaseDependency(currentNode, egd, premiseTreeMap.get(egd), scenario, chaseState, databaseForStep);
            long endEgd = new Date().getTime();
            ChaseStats.getInstance().addDepenendecyStat(egd, endEgd - startEgd);
            if (logger.isDebugEnabled()) logger.trace("New steps generated by dependency: " + newChaseSteps);
            if (logger.isDebugEnabled()) logger.debug("New steps generated by dependency: " + newChaseSteps.size());
            if (newChaseSteps.isNoRepairsNeeded()) {
                if (logger.isDebugEnabled()) logger.debug("Step " + currentNode.getId() + " satisfies dependency " + egd.getId());
                currentNode.addSatisfiedEGD(egd);
                if (scenario.getConfiguration().isCheckAllNodesForEGDSatisfaction()) {
                    unsatisfiedDependenciesChecker.checkEGDSatisfactionWithQuery(currentNode, scenario);
                }
            } else {
                if (newChaseSteps.getChaseSteps().isEmpty()) {
                    throw new ChaseException("Unable to repair dependency " + egd + " in node\n" + currentNode.getId());
                }
                newStep = newChaseSteps.getChaseSteps().get(0);
                if (scenario.getConfiguration().isCheckAllNodesForEGDSatisfaction()) {
                    unsatisfiedDependenciesChecker.checkEGDSatisfactionWithQuery(newStep, scenario);
                }
            }
        }
        if (newStep != null) {
            List<DeltaChaseStep> newSteps = new ArrayList<DeltaChaseStep>();
            newSteps.add(newStep);
            currentNode.setChildren(newSteps);
        }
        if (newStep != null || thereAreUnsatisfiedDependencies(unsatisfiedDependencies, egdsToChase)) {
            chaseTree(currentNode, scenario, chaseState, egds, premiseTreeMap);
        }
    }

    private boolean thereAreUnsatisfiedDependencies(List<Dependency> unsatisfiedDependencies, List<Dependency> dependenciesToChase) {
        return unsatisfiedDependencies.size() != dependenciesToChase.size();
    }

}
