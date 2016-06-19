package it.unibas.lunatic.model.chase.chasede.operators;

import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.exceptions.ChaseException;
import it.unibas.lunatic.model.chase.chasemc.DeltaChaseStep;
import it.unibas.lunatic.model.chase.commons.ChaseStats;
import it.unibas.lunatic.model.chase.commons.IChaseState;
import it.unibas.lunatic.model.dependency.Dependency;
import it.unibas.lunatic.model.dependency.DependencyStratification;
import it.unibas.lunatic.model.dependency.EGDStratum;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import speedy.model.algebra.IAlgebraOperator;
import speedy.model.database.operators.IRunQuery;

public class ChaseDeltaEGDs {

    private static final Logger logger = LoggerFactory.getLogger(ChaseDeltaEGDs.class);
    private ScheduleEGDStrata egdScheduler;
    private final CheckUnsatisfiedDependenciesDE unsatisfiedDependenciesChecker;
    private final IBuildDatabaseForDE databaseBuilderForDE;
    private final IRunQuery queryRunner;
    private final OccurrenceHandlerDE occurrenceHandler;
    private final ChangeCellDE cellChanger;

    public ChaseDeltaEGDs(CheckUnsatisfiedDependenciesDE unsatisfiedDependenciesChecker, IBuildDatabaseForDE databaseBuilderForDE, IRunQuery queryRunner, OccurrenceHandlerDE occurrenceHandler, ChangeCellDE cellChanger) {
        this.unsatisfiedDependenciesChecker = unsatisfiedDependenciesChecker;
        this.databaseBuilderForDE = databaseBuilderForDE;
        this.queryRunner = queryRunner;
        this.occurrenceHandler = occurrenceHandler;
        this.cellChanger = cellChanger;
    }

    public boolean doChase(DeltaChaseStep root, Scenario scenario, IChaseState chaseState, Map<Dependency, IAlgebraOperator> premiseTreeMap) {
        this.egdScheduler = new ScheduleEGDStrata(premiseTreeMap, chaseState, root, scenario, unsatisfiedDependenciesChecker, databaseBuilderForDE, queryRunner, occurrenceHandler, cellChanger);
        long start = new Date().getTime();
        if (logger.isDebugEnabled()) logger.debug("Chasing egds " + scenario.getExtEGDs() + " with " + scenario.getConfiguration().getMaxNumberOfThreads() + " threads");
        DependencyStratification stratification = scenario.getStratification();
        egdScheduler.addUnsatisfiedStrata(stratification.getEGDStrata());
        List<EGDStratum> initialStrata = findInitialStrata(stratification);
        if (initialStrata.isEmpty()) {
            throw new ChaseException("Unable to find initial strata for egds");
        }
        for (EGDStratum egdStratum : initialStrata) {
            egdScheduler.startThreadForEGDStratum(egdStratum);
        }
        egdScheduler.waitForUnsatisfiedStrata();
        long end = new Date().getTime();
        ChaseStats.getInstance().addStat(ChaseStats.EGD_TIME, end - start);
        return egdScheduler.isModified();
    }

    private List<EGDStratum> findInitialStrata(DependencyStratification stratification) {
        List<EGDStratum> result = new ArrayList<EGDStratum>();
        DirectedGraph<EGDStratum, DefaultEdge> strataGraph = stratification.getEgdStrataGraph();
        for (EGDStratum egdStratum : stratification.getEGDStrata()) {
            if (strataGraph.inDegreeOf(egdStratum) == 0) {
                result.add(egdStratum);
            }
        }
        return result;
    }

}
