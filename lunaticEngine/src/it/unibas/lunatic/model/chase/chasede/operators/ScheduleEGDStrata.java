package it.unibas.lunatic.model.chase.chasede.operators;

import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.model.chase.chasemc.DeltaChaseStep;
import it.unibas.lunatic.model.chase.commons.IChaseState;
import speedy.model.thread.ThreadManager;
import it.unibas.lunatic.model.dependency.Dependency;
import it.unibas.lunatic.model.dependency.EGDStratum;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import speedy.model.algebra.IAlgebraOperator;
import speedy.model.database.operators.IRunQuery;

public class ScheduleEGDStrata {

    private final static Logger logger = LoggerFactory.getLogger(ScheduleEGDStrata.class);

    private ThreadManager threadManager;
    private Map<Dependency, IAlgebraOperator> treeMap;
    private IChaseState chaseState;
    private DeltaChaseStep root;
    private Scenario scenario;
    private boolean modified = false;
    //Unsatisfied strata
    private Set<EGDStratum> unsatisfiedStrata;
    private Lock unsatisfiedStrataLock;
    private Condition unsatisfiedStrataCondition;
    private final CheckUnsatisfiedDependenciesDE unsatisfiedDependenciesChecker;
    private final IBuildDatabaseForDE databaseBuilder;
    private final IRunQuery queryRunner;
    private final OccurrenceHandlerDE occurrenceHandler;
    private final ChangeCellDE cellChanger;

    public ScheduleEGDStrata(Map<Dependency, IAlgebraOperator> treeMap, IChaseState chaseState, DeltaChaseStep root, Scenario scenario,
            CheckUnsatisfiedDependenciesDE unsatisfiedDependenciesChecker, IBuildDatabaseForDE databaseBuilder,
            IRunQuery queryRunner, OccurrenceHandlerDE occurrenceHandler, ChangeCellDE cellChanger) {
        this.treeMap = treeMap;
        this.chaseState = chaseState;
        this.root = root;
        this.scenario = scenario;
        this.unsatisfiedStrataLock = new ReentrantLock();
        this.unsatisfiedStrataCondition = this.unsatisfiedStrataLock.newCondition();
        this.unsatisfiedStrata = Collections.synchronizedSet(new HashSet<EGDStratum>());
        int numberOfThreads = scenario.getConfiguration().getMaxNumberOfThreads();
        this.threadManager = new ThreadManager(numberOfThreads);
        this.unsatisfiedDependenciesChecker = unsatisfiedDependenciesChecker;
        this.databaseBuilder = databaseBuilder;
        this.queryRunner = queryRunner;
        this.occurrenceHandler = occurrenceHandler;
        this.cellChanger = cellChanger;
    }

    public void startThreadForEGDStratum(EGDStratum egdStratum) {
        this.unsatisfiedStrataLock.lock();
        try {
            if (!unsatisfiedStrata.contains(egdStratum)) {
                return;
            }
            if (!allPreviousStrataAreSatisfied(egdStratum)) {
                if (logger.isDebugEnabled()) logger.debug("Stratum " + egdStratum + " is waiting for previous strata...");
                return;
            }
            if (logger.isDebugEnabled()) logger.debug("Starting thread for egdStratum " + egdStratum);
            ChaseDeltaEGDStratumThread stratumThread = new ChaseDeltaEGDStratumThread(this, egdStratum, treeMap, root, scenario, chaseState, unsatisfiedDependenciesChecker, databaseBuilder, queryRunner, occurrenceHandler, cellChanger);
            threadManager.startThread(stratumThread);
        } finally {
            this.unsatisfiedStrataLock.unlock();
        }
    }

    private boolean allPreviousStrataAreSatisfied(EGDStratum egdStratum) {
        this.unsatisfiedStrataLock.lock();
        try {
            DirectedGraph<EGDStratum, DefaultEdge> strataGraph = scenario.getStratification().getEgdStrataGraph();
            for (DefaultEdge inEdge : strataGraph.incomingEdgesOf(egdStratum)) {
                EGDStratum prevStratum = strataGraph.getEdgeSource(inEdge);
                if (unsatisfiedStrata.contains(prevStratum)) {
                    return false;
                }
            }
            return true;
        } finally {
            this.unsatisfiedStrataLock.unlock();
        }
    }

    public void waitForUnsatisfiedStrata() {
        this.unsatisfiedStrataLock.lock();
        try {
            while (!unsatisfiedStrata.isEmpty()) {
                if (logger.isDebugEnabled()) logger.debug("Waiting for unsatisfied strata: " + unsatisfiedStrata);
                try {
                    this.unsatisfiedStrataCondition.await();
                } catch (InterruptedException ex) {
                }
            }
        } finally {
            this.unsatisfiedStrataLock.unlock();
        }
    }

    public void addUnsatisfiedStrata(List<EGDStratum> egdStratum) {
        this.unsatisfiedStrataLock.lock();
        try {
            this.unsatisfiedStrata.addAll(egdStratum);
        } finally {
            this.unsatisfiedStrataLock.unlock();
        }
    }

    public void addSatisfiedStratum(EGDStratum egdStratum) {
        this.unsatisfiedStrataLock.lock();
        try {
            if (logger.isDebugEnabled()) logger.debug("** Stratum satisfied: " + egdStratum);
            this.unsatisfiedStrata.remove(egdStratum);
            this.unsatisfiedStrataCondition.signalAll();
            DirectedGraph<EGDStratum, DefaultEdge> strataGraph = scenario.getStratification().getEgdStrataGraph();
            for (DefaultEdge outEdge : strataGraph.outgoingEdgesOf(egdStratum)) {
                EGDStratum nextStratum = strataGraph.getEdgeTarget(outEdge);
                this.startThreadForEGDStratum(nextStratum);
            }
        } finally {
            this.unsatisfiedStrataLock.unlock();
        }
    }

    public boolean isModified() {
        return modified;
    }

    public void setModified() {
        this.modified = true;
    }

}
