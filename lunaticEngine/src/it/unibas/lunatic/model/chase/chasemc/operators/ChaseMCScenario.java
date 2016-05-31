package it.unibas.lunatic.model.chase.chasemc.operators;

import it.unibas.lunatic.model.chase.commons.operators.IBuildDeltaDB;
import it.unibas.lunatic.model.chase.commons.operators.IBuildDatabaseForChaseStep;
import it.unibas.lunatic.LunaticConfiguration;
import it.unibas.lunatic.LunaticConstants;
import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.exceptions.ChaseException;
import it.unibas.lunatic.exceptions.ChaseFailedException;
import it.unibas.lunatic.model.algebra.operators.BuildAlgebraTreeForEGD;
import it.unibas.lunatic.model.algebra.operators.BuildAlgebraTreeForTGD;
import it.unibas.lunatic.model.chase.chasemc.ChaseTree;
import it.unibas.lunatic.model.chase.chasemc.DeltaChaseStep;
import it.unibas.lunatic.model.chase.commons.operators.ChaseDeltaDCs;
import it.unibas.lunatic.model.chase.commons.ChaseStats;
import it.unibas.lunatic.model.chase.commons.operators.ChaseUtility;
import it.unibas.lunatic.model.chase.commons.operators.IChaseSTTGDs;
import it.unibas.lunatic.model.chase.commons.IChaseState;
import it.unibas.lunatic.model.chase.commons.ImmutableChaseState;
import speedy.model.database.IDatabase;
import it.unibas.lunatic.model.dependency.Dependency;
import it.unibas.lunatic.model.dependency.operators.AnalyzeDependencies;
import it.unibas.lunatic.persistence.relational.ExportChaseStepResultsCSV;
import java.util.Date;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import speedy.model.algebra.IAlgebraOperator;
import speedy.model.algebra.operators.IInsertTuple;
import speedy.model.database.operators.IRunQuery;
import speedy.utility.PrintUtility;

public class ChaseMCScenario {

    private static final Logger logger = LoggerFactory.getLogger(ChaseMCScenario.class);
    public static final int ITERATION_LIMIT = 10;
    private final BuildAlgebraTreeForTGD treeBuilderForTGD = new BuildAlgebraTreeForTGD();
    private final BuildAlgebraTreeForEGD treeBuilderForEGD = new BuildAlgebraTreeForEGD();
    private final IChaseSTTGDs stChaser;
    private final IChaseDeltaExtTGDs extTgdChaser;
    private final ChaseDeltaDCs dChaser;
    private final ChaseDeltaExtEGDs egdChaser;
    private final IBuildDeltaDB deltaBuilder;
    private final AnalyzeDependencies dependencyAnalyzer = new AnalyzeDependencies();
    private final CheckRedundancy redundancyChecker = new CheckRedundancy();
    private final CheckSolution solutionChecker;
    private final RankSolutions solutionRanker = new RankSolutions();
    private final PrintRankedSolutions solutionPrinter = new PrintRankedSolutions();
    private final ChaseTreeSize resultSizer = new ChaseTreeSize();
    private final ExportChaseStepResultsCSV resultExporter = new ExportChaseStepResultsCSV();

    public ChaseMCScenario(IChaseSTTGDs stChaser, IChaseDeltaExtTGDs extTgdChaser,
            IBuildDeltaDB deltaBuilder, IBuildDatabaseForChaseStep stepBuilder, IRunQuery queryRunner,
            IInsertTuple insertOperatorForEgds, IOccurrenceHandler occurrenceHandler,
            ChaseDeltaExtEGDs egdChaser, CheckSolution solutionChecker) {
        this.stChaser = stChaser;
        this.deltaBuilder = deltaBuilder;
        this.extTgdChaser = extTgdChaser;
        this.dChaser = new ChaseDeltaDCs(queryRunner, stepBuilder);
        this.egdChaser = egdChaser;
        this.solutionChecker = solutionChecker;
    }

    public ChaseTree doChase(Scenario scenario, IChaseState chaseState) {
        checkDataSources(scenario);
        long start = new Date().getTime();
        try {
            dependencyAnalyzer.analyzeDependencies(scenario);
            if (scenario.getConfiguration().isPrintStatsOnly()) {
                return new ChaseTree(scenario);
            }
            // s-t tgds are chased in the standard way; this works fine as long as there are no authoritative sources
            // in place of null cells + justifications, new cells with values are generated
            stChaser.doChase(scenario, false);
            ChaseTree chaseTree = new ChaseTree(scenario);
            IDatabase targetDB = scenario.getTarget();
            if (logger.isDebugEnabled()) logger.debug("-------------------Chasing dependencies on mc scenario: " + scenario);
            IDatabase deltaDB = deltaBuilder.generate(targetDB, scenario, LunaticConstants.CHASE_STEP_ROOT);
            if (logger.isDebugEnabled()) logger.debug("DeltaDB: " + deltaDB);
            DeltaChaseStep root = new DeltaChaseStep(scenario, chaseTree, LunaticConstants.CHASE_STEP_ROOT, targetDB, deltaDB);
            DeltaChaseStep result = doChase(root, scenario, chaseState);
            long end = new Date().getTime();
            ChaseStats.getInstance().addStat(ChaseStats.CHASE_TIME, end - start);
            chaseTree.setRoot(result);
            if (ChaseUtility.hasChaseStats(scenario)) {
                solutionRanker.rankSolutions(chaseTree);
            }
            if (scenario.getConfiguration().isExportSolutions()) {
                resultExporter.exportSolutionsInSeparateFiles(chaseTree, scenario);
            }
            if (scenario.getConfiguration().isExportChanges()) {
                resultExporter.exportChangesInSeparateFiles(chaseTree, scenario);
            }
            printResult(chaseTree);
            return chaseTree;
        } catch (ChaseFailedException e) {
            throw e;
        } finally {
            if (logger.isDebugEnabled()) ChaseStats.getInstance().printStatistics();
        }
    }

    private void checkDataSources(Scenario scenario) {
        redundancyChecker.checkRedundancyInAuthoritativeTables(scenario);
        redundancyChecker.checkDuplicateOIDs(scenario);
    }

    public DeltaChaseStep doChase(DeltaChaseStep root, Scenario scenario, IChaseState chaseState) {
        if (scenario.isDEDScenario()) {
            throw new ChaseException("ChaseMCScenario cannot handle scenarios with deds");
        }
        if (logger.isDebugEnabled()) ChaseStats.getInstance().printStatistics();
        Map<Dependency, IAlgebraOperator> egdQueryMap = treeBuilderForEGD.buildPremiseAlgebraTreesForEGDs(scenario.getExtEGDs(), scenario);
        Map<Dependency, IAlgebraOperator> tgdQueryMap = treeBuilderForTGD.buildAlgebraTreesForTGDViolationsChase(scenario.getExtTGDs(), scenario);
        Map<Dependency, IAlgebraOperator> tgdQuerySatisfactionMap = treeBuilderForTGD.buildAlgebraTreesForTGDSatisfaction(scenario.getExtTGDs(), scenario);
        Map<Dependency, IAlgebraOperator> dQueryMap = treeBuilderForTGD.buildAlgebraTreesForDTGD(scenario.getDCs(), scenario);
        IDatabase targetDB = scenario.getTarget();
        extTgdChaser.initializeOIDs(targetDB, scenario);
        boolean userInteractionRequired = false;
        int iterations = 0;
        while (!userInteractionRequired) {
            if (chaseState.isCancelled()) {
                //throw new ChaseException("Chase interrupted by user");
                ChaseUtility.stopChase(chaseState);
            }
            boolean newTgdNodes = extTgdChaser.doChase(root, scenario, chaseState, tgdQueryMap, tgdQuerySatisfactionMap);
            if (newTgdNodes && logger.isDebugEnabled()) logger.debug("Chase tree after tgd enforcement:\n" + root);
            ChaserResult egdResult = egdChaser.doChase(root, scenario, chaseState, egdQueryMap);
            userInteractionRequired = egdResult.isUserInteractionRequired();
            if (scenario.getConfiguration().isCheckDCDuringChase()) {
                dChaser.doChase(root, scenario, chaseState, dQueryMap);
            }
            if (egdResult.isNewNodes() && logger.isDebugEnabled()) logger.debug("Chase tree after egd enforcement:\n" + root);
//            if (!newTgdNodes && !egdResult.isNewNodes()) {
            if (scenario.getExtEGDs().isEmpty() || !newTgdNodes && !egdResult.isNewNodes()) {
                break;
            } else {
                iterations++;
            }
            if (iterations > ITERATION_LIMIT) {
                StringBuilder errorMessage = new StringBuilder("Reached iteration limit " + ITERATION_LIMIT + " with no solution...");
                if (logger.isDebugEnabled()) errorMessage.append("\nScenario: ").append(scenario).append("\nChase tree:\n").append(root).append("\nDelta db:\n").append(root.getDeltaDB());
                throw new ChaseException(errorMessage.toString());
            }
        }
        if (!scenario.getConfiguration().isCheckSolutions() && !userInteractionRequired) {
            solutionChecker.markLeavesAsSolutions(root, scenario);
        } else if (!userInteractionRequired) {
            if (LunaticConfiguration.isPrintSteps()) System.out.println("------ Checking solutions...");
            solutionChecker.checkSolutions(root, scenario);
        }
        dChaser.doChase(root, scenario, chaseState, dQueryMap);
        if (logger.isDebugEnabled()) logger.debug("------------------Final Chase tree: ----\n" + root.toLongStringWithSort());
        return root;
    }

    // used in tests
    public DeltaChaseStep doChase(Scenario scenario) {
        return doChase(scenario, ImmutableChaseState.getInstance()).getRoot();
    }

    // for user nodes
    public DeltaChaseStep resumeChaseFromStep(DeltaChaseStep step, Scenario scenario) {
        return doChase(step, scenario, ImmutableChaseState.getInstance());
    }

    private void printResult(ChaseTree chaseTree) {
        if (!LunaticConfiguration.isPrintSteps()) {
            return;
        }
        System.out.println("");
        System.out.println("Number of solutions: " + resultSizer.getPotentialSolutions(chaseTree.getRoot()));
        System.out.println("Number of duplicates: " + resultSizer.getDuplicates(chaseTree.getRoot()));
        if (chaseTree.getRankedSolutions() != null && !chaseTree.getRankedSolutions().isEmpty()) {
            System.out.println(solutionPrinter.toString(chaseTree));
        }
        PrintUtility.printSuccess("*** Chase complete in " + ChaseStats.getInstance().getStat(ChaseStats.CHASE_TIME) + " ms");
        Long loadTime = ChaseStats.getInstance().getStat(ChaseStats.LOAD_TIME);
        if (loadTime == null) {
            loadTime = 0L;
        }
        PrintUtility.printInformation("*** Loading time: " + loadTime + " ms");
        Long writeTime = ChaseStats.getInstance().getStat(ChaseStats.WRITE_TIME);
        if (writeTime == null) {
            writeTime = 0L;
        }
        PrintUtility.printInformation("*** Writing time: " + writeTime + " ms");
    }

}
