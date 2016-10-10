package it.unibas.lunatic.model.chase.chasede.operators;

import it.unibas.lunatic.LunaticConfiguration;
import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.model.chase.chasemc.DeltaChaseStep;
import it.unibas.lunatic.model.chase.chasemc.costmanager.CostManagerUtility;
import it.unibas.lunatic.model.chase.commons.ChaseStats;
import it.unibas.lunatic.model.chase.commons.operators.ChaseUtility;
import it.unibas.lunatic.model.chase.commons.IChaseState;
import speedy.model.thread.IBackgroundThread;
import it.unibas.lunatic.model.dependency.Dependency;
import it.unibas.lunatic.model.dependency.EGDStratum;
import it.unibas.lunatic.utility.LunaticUtility;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import speedy.model.algebra.IAlgebraOperator;
import speedy.model.database.IDatabase;
import speedy.model.database.operators.IRunQuery;

public class ChaseDeltaEGDStratumThread implements IBackgroundThread {
    
    private final static Logger logger = LoggerFactory.getLogger(ChaseDeltaEGDStratumThread.class);
    private ScheduleEGDStrata egdScheduler;
    private EGDStratum stratum;
    private Map<Dependency, IAlgebraOperator> premiseTreeMap;
    private DeltaChaseStep root;
    private Scenario scenario;
    private IChaseState chaseState;
    private final CheckUnsatisfiedDependenciesDE unsatisfiedDependenciesChecker;
    private final IBuildDatabaseForDE databaseBuilder;
    private final IChaseEGDEquivalenceClass symmetricEGDChaser;
    private final IChaseEGDEquivalenceClass egdChaser;
    
    public ChaseDeltaEGDStratumThread(ScheduleEGDStrata egdScheduler, EGDStratum stratum, Map<Dependency, IAlgebraOperator> treeMap, DeltaChaseStep root, Scenario scenario,
            IChaseState chaseState, CheckUnsatisfiedDependenciesDE unsatisfiedDependenciesChecker, IBuildDatabaseForDE databaseBuilder,
            IRunQuery queryRunner, OccurrenceHandlerDE occurrenceHandler, ChangeCellDE cellChanger) {
        this.egdScheduler = egdScheduler;
        this.stratum = stratum;
        this.premiseTreeMap = treeMap;
        this.root = root;
        this.scenario = scenario;
        this.chaseState = chaseState;
        this.unsatisfiedDependenciesChecker = unsatisfiedDependenciesChecker;
        this.databaseBuilder = databaseBuilder;
        this.symmetricEGDChaser = new ChaseSymmetricEGDEquivalenceClass(queryRunner, occurrenceHandler, cellChanger);
        this.egdChaser = new ChaseEGDEquivalenceClass(queryRunner, occurrenceHandler, cellChanger);
    }
    
    public void execute() {
        try {
            if (LunaticConfiguration.isPrintSteps()) System.out.println("  ****Chasing stratum " + stratum);
            while (true) {
                List<Dependency> unsatisfiedDependencies = unsatisfiedDependenciesChecker.findUnsatisfiedEGDsNoQuery(root, stratum.getDependencies());
                List<Dependency> egdsToChase = CostManagerUtility.selectDependenciesToChase(unsatisfiedDependencies, root, scenario.getCostManagerConfiguration());
                if (egdsToChase.isEmpty()) {
                    if (logger.isDebugEnabled()) logger.debug("----All egds are satisfied");
                    break;
                }
                if (logger.isDebugEnabled()) logger.debug("----Unsatisfied Dependencies: " + LunaticUtility.printDependencyIds(unsatisfiedDependencies));
                if (logger.isDebugEnabled()) logger.debug("----Dependencies to chase: " + LunaticUtility.printDependencyIds(egdsToChase));
                for (Dependency egd : egdsToChase) {
                    if (chaseState.isCancelled()) ChaseUtility.stopChase(chaseState); //throw new ChaseException("Chase interrupted by user");
                    if (LunaticConfiguration.isPrintSteps()) System.out.println("\t    **Chasing edg: " + egd.getId());
                    long startEgd = new Date().getTime();
                    if (logger.isDebugEnabled()) logger.info("* Chasing dependency " + egd.getId());
                    if (logger.isDebugEnabled()) logger.info("* Algebra operator " + premiseTreeMap.get(egd));
                    if (logger.isTraceEnabled()) logger.trace("Building database for step id: " + root.getId() + "\nDelta db:\n" + root.getDeltaDB().printInstances());
                    IDatabase databaseForStep = databaseBuilder.extractDatabase(root.getDeltaDB(), root.getOriginalDB(), egd, scenario);
                    if (logger.isTraceEnabled()) logger.trace("Database for step id: " + root.getId() + "\n" + databaseForStep.printInstances());
                    IChaseEGDEquivalenceClass chaser = getChaser(egd);
                    //New chase steps are not used for de
                    boolean changes = chaser.chaseDependency(root, egd, premiseTreeMap.get(egd), scenario, chaseState, databaseForStep);
                    long endEgd = new Date().getTime();
                    ChaseStats.getInstance().addDepenendecyStat(egd, endEgd - startEgd);
                    if (changes) {
                        if (logger.isDebugEnabled()) logger.debug("Changes generated for dependency");
                        egdScheduler.setModified();
                    }
                    if (scenario.getConfiguration().isCheckAllNodesForEGDSatisfaction()) {
                        unsatisfiedDependenciesChecker.checkEGDSatisfactionWithQuery(root, scenario);
                    }
                }
            }
            egdScheduler.addSatisfiedStratum(stratum);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }
    
    private IChaseEGDEquivalenceClass getChaser(Dependency egd) {
        if (egd.hasSymmetricChase()) {
            return this.symmetricEGDChaser;
        }
        return egdChaser;
    }
    
}
