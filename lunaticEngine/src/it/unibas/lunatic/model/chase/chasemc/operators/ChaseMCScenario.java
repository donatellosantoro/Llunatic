package it.unibas.lunatic.model.chase.chasemc.operators;

import it.unibas.lunatic.LunaticConfiguration;
import it.unibas.lunatic.LunaticConstants;
import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.exceptions.ChaseException;
import it.unibas.lunatic.exceptions.ChaseFailedException;
import it.unibas.lunatic.model.algebra.operators.BuildAlgebraTreeForEGD;
import it.unibas.lunatic.model.algebra.operators.BuildAlgebraTreeForTGD;
import it.unibas.lunatic.model.chase.chasemc.ChaseTree;
import it.unibas.lunatic.model.chase.chasemc.DeltaChaseStep;
import it.unibas.lunatic.model.chase.commons.ChaseDeltaDCs;
import it.unibas.lunatic.model.chase.commons.ChaseStats;
import it.unibas.lunatic.model.chase.commons.ChaseUtility;
import it.unibas.lunatic.model.chase.commons.IChaseSTTGDs;
import it.unibas.lunatic.model.chase.commons.control.IChaseState;
import it.unibas.lunatic.model.chase.commons.control.ImmutableChaseState;
import speedy.model.database.IDatabase;
import it.unibas.lunatic.model.dependency.Dependency;
import it.unibas.lunatic.model.dependency.operators.AnalyzeDependencies;
import java.util.Date;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import speedy.model.algebra.IAlgebraOperator;
import speedy.model.algebra.operators.IInsertTuple;
import speedy.model.database.operators.IRunQuery;

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
    private final AnalyzeDependencies stratificationBuilder = new AnalyzeDependencies();
    private final CheckSolution solutionChecker;
    private final RankSolutions solutionRanker = new RankSolutions();

    public ChaseMCScenario(IChaseSTTGDs stChaser, IChaseDeltaExtTGDs extTgdChaser,
            IBuildDeltaDB deltaBuilder, IBuildDatabaseForChaseStep stepBuilder, IRunQuery queryRunner,
            IInsertTuple insertOperatorForEgds, OccurrenceHandlerMC occurrenceHandler,
            ChaseDeltaExtEGDs egdChaser, CheckSolution solutionChecker) {
        this.stChaser = stChaser;
        this.deltaBuilder = deltaBuilder;
        this.extTgdChaser = extTgdChaser;
        this.dChaser = new ChaseDeltaDCs(queryRunner, stepBuilder);
        this.egdChaser = egdChaser;
        this.solutionChecker = solutionChecker;
    }

    public ChaseTree doChase(Scenario scenario, IChaseState chaseState) {
        long start = new Date().getTime();
        try {
            // s-t tgds are chased in the standard way; this works fine as long as there are no authoritative sources
            // in place of null cells + justifications, new cells with values are generated
            stChaser.doChase(scenario, false);
            ChaseTree chaseTree = new ChaseTree(scenario);
            IDatabase targetDB = scenario.getTarget();
            if (logger.isDebugEnabled()) logger.debug("-------------------Chasing dependencies on mc scenario: " + scenario);
            //Generate stratification (must be first step because affects other steps)
            stratificationBuilder.prepareDependenciesAndGenerateStratification(scenario);
            IDatabase deltaDB = deltaBuilder.generate(targetDB, scenario, LunaticConstants.CHASE_STEP_ROOT);
            if (logger.isDebugEnabled()) logger.debug("DeltaDB: " + deltaDB);
            DeltaChaseStep root = new DeltaChaseStep(scenario, chaseTree, LunaticConstants.CHASE_STEP_ROOT, targetDB, deltaDB);
            DeltaChaseStep result = doChase(root, scenario, chaseState);
            chaseTree.setRoot(result);
            if (hasChaseStats(scenario)) {
                solutionRanker.rankSolutions(chaseTree);
            }
            return chaseTree;
        } catch (ChaseFailedException e) {
            throw e;
        } finally {
            long end = new Date().getTime();
            ChaseStats.getInstance().addStat(ChaseStats.TOTAL_TIME, end - start);
            if (logger.isDebugEnabled()) ChaseStats.getInstance().printStatistics();
        }

    }

    private DeltaChaseStep doChase(DeltaChaseStep root, Scenario scenario, IChaseState chaseState) {
        if (scenario.isDEDScenario()) {
            throw new ChaseException("ChaseMCScenario cannot handle scenarios with deds");
        }
        ChaseStats.getInstance().addStat(ChaseStats.NUMBER_OF_STTGDS, scenario.getSTTgds().size());
        ChaseStats.getInstance().addStat(ChaseStats.NUMBER_OF_EXTEGDS, scenario.getExtEGDs().size());
        ChaseStats.getInstance().addStat(ChaseStats.NUMBER_OF_EXTGDS, scenario.getExtTGDs().size());
        ChaseStats.getInstance().addStat(ChaseStats.NUMBER_OF_DCS, scenario.getDCs().size());
        if (logger.isDebugEnabled()) ChaseStats.getInstance().printStatistics();
        Map<Dependency, IAlgebraOperator> egdQueryMap = treeBuilderForEGD.buildPremiseAlgebraTreesForEGDs(scenario.getExtEGDs(), scenario);
        Map<Dependency, IAlgebraOperator> tgdQueryMap = treeBuilderForTGD.buildAlgebraTreesForTGDViolationsChase(scenario.getExtTGDs(), scenario);
        Map<Dependency, IAlgebraOperator> tgdQuerySatisfactionMap = treeBuilderForTGD.buildAlgebraTreesForTGDSatisfaction(scenario.getExtTGDs(), scenario);
        Map<Dependency, IAlgebraOperator> dQueryMap = treeBuilderForTGD.buildAlgebraTreesForDTGD(scenario.getDCs(), scenario);
        IDatabase targetDB = scenario.getTarget();
        extTgdChaser.initializeOIDs(targetDB);
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
            if (!newTgdNodes && !egdResult.isNewNodes()) {
                break;
            } else {
                iterations++;
            }
            if (iterations > ITERATION_LIMIT) {
                StringBuilder errorMessage = new StringBuilder("Reached iteration limit " + ITERATION_LIMIT + " with no solution...");
                if (logger.isDebugEnabled()) errorMessage.append("\nScenario: ").append(scenario).append("\nChase tree:\n" + root).append("\nDelta db:\n" + root.getDeltaDB());
                throw new ChaseException(errorMessage.toString());
            }
        }
        if (!scenario.getConfiguration().isCheckSolutions() && !userInteractionRequired) {
            solutionChecker.markLeavesAsSolutions(root, scenario);
        } else {
            if (LunaticConfiguration.sout) System.out.println("------ Checking solutions...");
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

    private boolean hasChaseStats(Scenario scenario) {
        return !scenario.getExtEGDs().isEmpty() && scenario.getConfiguration().isRemoveDuplicates();
    }
}
