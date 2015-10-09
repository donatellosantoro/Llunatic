package it.unibas.lunatic.model.chase.chasemc.operators;

import it.unibas.lunatic.LunaticConfiguration;
import it.unibas.lunatic.LunaticConstants;
import it.unibas.lunatic.utility.LunaticUtility;
import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.exceptions.ChaseException;
import it.unibas.lunatic.model.chase.commons.ChaseStats;
import it.unibas.lunatic.model.chase.commons.ChaseUtility;
import it.unibas.lunatic.model.chase.commons.control.IChaseState;
import it.unibas.lunatic.model.chase.chasemc.DeltaChaseStep;
import it.unibas.lunatic.model.chase.chasemc.NewChaseSteps;
import it.unibas.lunatic.model.dependency.Dependency;
import it.unibas.lunatic.model.dependency.DependencyStratification;
import it.unibas.lunatic.model.dependency.DependencyStratum;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import speedy.model.algebra.IAlgebraOperator;
import speedy.model.algebra.operators.IDelete;
import speedy.model.algebra.operators.IInsertTuple;
import speedy.model.database.IDatabase;
import speedy.model.database.operators.IRunQuery;

public class ChaseDeltaExtEGDs {

    private static Logger logger = LoggerFactory.getLogger(ChaseDeltaExtEGDs.class);
    private CheckUnsatisfiedDependencies unsatisfiedDependenciesChecker;
    private IBuildDatabaseForChaseStep databaseBuilder;
    private ChangeCell cellChanger;
    private CheckDuplicates duplicateChecker;
    private IChaseEGDEquivalenceClass symmetricEGDChaser;
    private IChaseEGDEquivalenceClass egdChaser;
    private OccurrenceHandlerMC occurrenceHandler;

    public ChaseDeltaExtEGDs(IBuildDeltaDB deltaBuilder, IBuildDatabaseForChaseStep stepBuilder, IRunQuery queryRunner,
            IInsertTuple insertOperator, IDelete deleteOperator, OccurrenceHandlerMC occurrenceHandler, CheckUnsatisfiedDependencies unsatisfiedDependenciesChecker) {
        this.databaseBuilder = stepBuilder;
        this.cellChanger = new ChangeCell(insertOperator, deleteOperator, occurrenceHandler);
        this.duplicateChecker = new CheckDuplicates();
        this.symmetricEGDChaser = new ChaseSymmetricEGDEquivalenceClass(queryRunner, occurrenceHandler, databaseBuilder, cellChanger);
        this.egdChaser = new ChaseEGDEquivalenceClass(queryRunner, occurrenceHandler, cellChanger);
        this.unsatisfiedDependenciesChecker = new CheckUnsatisfiedDependencies(databaseBuilder, occurrenceHandler, queryRunner);
        this.occurrenceHandler = occurrenceHandler;
    }

    public ChaserResult doChase(DeltaChaseStep root, Scenario scenario, IChaseState chaseState, Map<Dependency, IAlgebraOperator> premiseTreeMap) {
        long start = new Date().getTime();
        int size = root.getNumberOfNodes();
        boolean userInteractionRequired = false;
        DependencyStratification stratification = scenario.getStratification();
        for (DependencyStratum stratum : stratification.getStrata()) {
            if (LunaticConfiguration.sout) System.out.println("---- Chasing egd stratum: " + stratum.getId());
            if (logger.isDebugEnabled()) logger.debug("------------------Chasing stratum: ----\n" + stratum);
            userInteractionRequired = userInteractionRequired || chaseTree(root, scenario, chaseState, stratum.getDependencies(), premiseTreeMap);
//            userInteractionRequired = chaseTree(root, scenario, chaseState, stratum.getDependencies(), premiseTreeMap);
//            if (userInteractionRequired) {
//                break;
//            }
        }
        long end = new Date().getTime();
        ChaseStats.getInstance().addStat(ChaseStats.EGD_TIME, end - start);
        int newSize = root.getNumberOfNodes();
        boolean newNodes = (size != newSize);
        return new ChaserResult(newNodes, userInteractionRequired);
    }

    private boolean chaseTree(DeltaChaseStep treeRoot, Scenario scenario, IChaseState chaseState, List<Dependency> egds, Map<Dependency, IAlgebraOperator> premiseTreeMap) {
        if (treeRoot.isInvalid()) {
            return false;
        }
        if (treeRoot.isLeaf()) {
            return chaseNode((DeltaChaseStep) treeRoot, scenario, chaseState, egds, premiseTreeMap);
        }
        for (DeltaChaseStep child : treeRoot.getChildren()) {
            boolean userInteractionRequired = chaseTree(child, scenario, chaseState, egds, premiseTreeMap);
            if (userInteractionRequired) {
                return true;
            }
        }
        return false;
    }

    private IChaseEGDEquivalenceClass getChaser(Dependency egd) {
        if (egd.hasSymmetricChase()) {
            return this.symmetricEGDChaser;
        }
        return egdChaser;
    }

    private boolean chaseNode(DeltaChaseStep currentNode, Scenario scenario, IChaseState chaseState, List<Dependency> egds, Map<Dependency, IAlgebraOperator> premiseTreeMap) {
        if (scenario.getConfiguration().isRemoveDuplicates()) {
            this.occurrenceHandler.generateCellGroupStats(currentNode);
            duplicateChecker.findDuplicates(currentNode, scenario);
        }
        if (currentNode.isDuplicate() || currentNode.isInvalid()) {
            return false;
        }
        if (currentNode.isEditedByUser()) {
            DeltaChaseStep newStep = new DeltaChaseStep(scenario, currentNode, LunaticConstants.CHASE_USER, LunaticConstants.CHASE_USER);
            currentNode.addChild(newStep);
            return chaseNode(newStep, scenario, chaseState, egds, premiseTreeMap);
        }
        if (LunaticConfiguration.sout) System.out.println("******Chasing node for egds: " + currentNode.getId());
        if (logger.isDebugEnabled()) logger.debug("----Chase iteration starting...");
        List<DeltaChaseStep> newSteps = new ArrayList<DeltaChaseStep>();
        List<Dependency> unsatisfiedDependencies = unsatisfiedDependenciesChecker.findUnsatisfiedEGDsNoQuery(currentNode, egds);
        List<Dependency> egdsToChase = scenario.getCostManager().selectDependenciesToChase(unsatisfiedDependencies, currentNode.getRoot());
        if (logger.isDebugEnabled()) logger.debug("----Unsatisfied Dependencies: " + LunaticUtility.printDependencyIds(unsatisfiedDependencies));
        if (logger.isDebugEnabled()) logger.debug("----Dependencies to chase: " + LunaticUtility.printDependencyIds(egdsToChase));
        boolean userInteractionRequired = false;
        for (Dependency egd : egdsToChase) {
            if (chaseState.isCancelled()) ChaseUtility.stopChase(chaseState); //throw new ChaseException("Chase interrupted by user");
            long startEgd = new Date().getTime();
            if (logger.isDebugEnabled()) logger.info("* Chasing dependency " + egd);
            if (logger.isDebugEnabled()) logger.info("* Algebra operator " + premiseTreeMap.get(egd));
            if (logger.isDebugEnabled()) logger.debug("Building database for step id: " + currentNode.getId() + "\nDelta db:\n" + currentNode.getDeltaDB().printInstances());
            IDatabase databaseForStep = databaseBuilder.extractDatabase(currentNode.getId(), currentNode.getDeltaDB(), currentNode.getOriginalDB(), egd);
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
                newSteps.addAll(newChaseSteps.getChaseSteps());
                if (scenario.getConfiguration().isCheckAllNodesForEGDSatisfaction()) {
                    for (DeltaChaseStep newStep : newChaseSteps.getChaseSteps()) {
                        unsatisfiedDependenciesChecker.checkEGDSatisfactionWithQuery(newStep, scenario);
                    }
                }
                if (scenario.getUserManager().isUserInteractionRequired(newChaseSteps.getChaseSteps(), currentNode, scenario)) {
                    userInteractionRequired = true;
//                    break;
                }
            }
        }
        currentNode.setChildren(newSteps);
//        if (scenario.getConfiguration().isRemoveDuplicates()) {
//            findDuplicates(newSteps, scenario);
//        }
        if (userInteractionRequired) {
            return true;
        }
        if (newSteps.size() > 0 || thereAreUnsatisfiedDependencies(unsatisfiedDependencies, egdsToChase)) {
            userInteractionRequired = chaseTree(currentNode, scenario, chaseState, egds, premiseTreeMap);
            if (userInteractionRequired) {
                return true;
            }
        }
        return false;
    }

    private boolean thereAreUnsatisfiedDependencies(List<Dependency> unsatisfiedDependencies, List<Dependency> dependenciesToChase) {
        return unsatisfiedDependencies.size() != dependenciesToChase.size();
    }
//    private void findDuplicates(List<DeltaChaseStep> newChaseSteps, Scenario scenario) {
//        List<DeltaChaseStep> revertedNewChaseSteps = new ArrayList<DeltaChaseStep>(newChaseSteps);
//        Collections.reverse(revertedNewChaseSteps);
//        for (DeltaChaseStep newChaseStep : revertedNewChaseSteps) {
//            duplicateChecker.findDuplicateNode(newChaseStep, scenario);
//        }
//    }

}
