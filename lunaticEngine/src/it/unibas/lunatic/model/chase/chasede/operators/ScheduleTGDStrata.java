package it.unibas.lunatic.model.chase.chasede.operators;

import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.model.chase.commons.IChaseState;
import it.unibas.lunatic.model.chase.commons.thread.ThreadManager;
import it.unibas.lunatic.model.dependency.Dependency;
import it.unibas.lunatic.model.dependency.TGDStratum;
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

public class ScheduleTGDStrata {

    private final static Logger logger = LoggerFactory.getLogger(ScheduleTGDStrata.class);

    private ThreadManager threadManager;
    private Map<Dependency, IAlgebraOperator> treeMap;
    private IInsertFromSelectNaive naiveInsert;
    private IChaseState chaseState;
    private Scenario scenario;
    private boolean modified = false;
    //Unsatisfied strata
    private Set<TGDStratum> unsatisfiedStrata;
    private Lock unsatisfiedStrataLock;
    private Condition unsatisfiedStrataCondition;

    public ScheduleTGDStrata(Map<Dependency, IAlgebraOperator> treeMap, IInsertFromSelectNaive naiveInsert, IChaseState chaseState, Scenario scenario) {
        this.treeMap = treeMap;
        this.naiveInsert = naiveInsert;
        this.chaseState = chaseState;
        this.scenario = scenario;
        this.unsatisfiedStrataLock = new ReentrantLock();
        this.unsatisfiedStrataCondition = this.unsatisfiedStrataLock.newCondition();
        this.unsatisfiedStrata = Collections.synchronizedSet(new HashSet<TGDStratum>());
        int numberOfThreads = scenario.getConfiguration().getMaxNumberOfThreads();
        this.threadManager = new ThreadManager(numberOfThreads);
    }

    public void startThreadForTGDStratum(TGDStratum tgdStratum) {
        this.unsatisfiedStrataLock.lock();
        try {
            if (!unsatisfiedStrata.contains(tgdStratum)) {
                return;
            }
            if (!allPreviousStrataAreSatisfied(tgdStratum)) {
                if (logger.isDebugEnabled()) logger.debug("Stratum " + tgdStratum + " is waiting for previous strata...");
                return;
            }
            if (logger.isDebugEnabled()) logger.debug("Starting thread for tgdStratum " + tgdStratum);
            ChaseTargetTGDStratumThread stratumThread = new ChaseTargetTGDStratumThread(this, tgdStratum, treeMap, naiveInsert, scenario, chaseState);
            threadManager.startThread(stratumThread);
        } finally {
            this.unsatisfiedStrataLock.unlock();
        }
    }

    private boolean allPreviousStrataAreSatisfied(TGDStratum tgdStratum) {
        this.unsatisfiedStrataLock.lock();
        try {
            DirectedGraph<TGDStratum, DefaultEdge> strataGraph = scenario.getStratification().getStrataGraph();
            for (DefaultEdge inEdge : strataGraph.incomingEdgesOf(tgdStratum)) {
                TGDStratum prevStratum = strataGraph.getEdgeSource(inEdge);
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

    public void addUnsatisfiedStrata(List<TGDStratum> tgdStratum) {
        this.unsatisfiedStrataLock.lock();
        try {
            this.unsatisfiedStrata.addAll(tgdStratum);
        } finally {
            this.unsatisfiedStrataLock.unlock();
        }
    }

    public void addSatisfiedStratum(TGDStratum tgdStratum) {
        this.unsatisfiedStrataLock.lock();
        try {
            if (logger.isDebugEnabled()) logger.debug("** Stratum satisfied: " + tgdStratum);
            this.unsatisfiedStrata.remove(tgdStratum);
            this.unsatisfiedStrataCondition.signalAll();
            DirectedGraph<TGDStratum, DefaultEdge> strataGraph = scenario.getStratification().getStrataGraph();
            for (DefaultEdge outEdge : strataGraph.outgoingEdgesOf(tgdStratum)) {
                TGDStratum nextStratum = strataGraph.getEdgeTarget(outEdge);
                this.startThreadForTGDStratum(nextStratum);
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
