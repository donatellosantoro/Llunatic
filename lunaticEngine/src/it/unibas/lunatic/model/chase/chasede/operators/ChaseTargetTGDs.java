package it.unibas.lunatic.model.chase.chasede.operators;

import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.exceptions.ChaseException;
import it.unibas.lunatic.model.algebra.operators.BuildAlgebraTreeForStandardChase;
import it.unibas.lunatic.model.chase.commons.ChaseStats;
import it.unibas.lunatic.model.chase.commons.IChaseState;
import it.unibas.lunatic.model.dependency.Dependency;
import it.unibas.lunatic.model.dependency.DependencyStratification;
import it.unibas.lunatic.model.dependency.TGDStratum;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import speedy.model.algebra.IAlgebraOperator;

public class ChaseTargetTGDs {

    private final static Logger logger = LoggerFactory.getLogger(ChaseTargetTGDs.class);

    private BuildAlgebraTreeForStandardChase treeBuilderForStandardChase = new BuildAlgebraTreeForStandardChase();
    private ScheduleTGDStrata tgdScheduler;

    public boolean doChase(Scenario scenario, IChaseState chaseState) {
        if (scenario.getExtTGDs().isEmpty()) {
            return false;
        }
        long start = new Date().getTime();
        Map<Dependency, IAlgebraOperator> treeMap = buildAlgebraTrees(scenario.getExtTGDs(), scenario);
        this.tgdScheduler = new ScheduleTGDStrata(treeMap,  chaseState, scenario);
        if (logger.isDebugEnabled()) logger.debug("Chasing t-tgds " + scenario.getExtTGDs() + " with " + scenario.getConfiguration().getMaxNumberOfThreads() + " threads");
        DependencyStratification stratification = scenario.getStratification();
        tgdScheduler.addUnsatisfiedStrata(stratification.getTGDStrata());
        List<TGDStratum> initialStrata = findInitialStrata(stratification);
        if (initialStrata.isEmpty()) {
            throw new ChaseException("Unable to find initial strata for tgds");
        }
        for (TGDStratum tgdStratum : initialStrata) {
            tgdScheduler.startThreadForTGDStratum(tgdStratum);
        }
        tgdScheduler.waitForUnsatisfiedStrata();
        long end = new Date().getTime();
        ChaseStats.getInstance().addStat(ChaseStats.TGD_TIME, end - start);
        return tgdScheduler.isModified();
    }

    private List<TGDStratum> findInitialStrata(DependencyStratification stratification) {
        List<TGDStratum> result = new ArrayList<TGDStratum>();
        DirectedGraph<TGDStratum, DefaultEdge> strataGraph = stratification.getTgdStrataGraph();
        for (TGDStratum tgdStratum : stratification.getTGDStrata()) {
            if (strataGraph.inDegreeOf(tgdStratum) == 0) {
                result.add(tgdStratum);
            }
        }
        return result;
    }

    private Map<Dependency, IAlgebraOperator> buildAlgebraTrees(List<Dependency> extTGDs, Scenario scenario) {
        Map<Dependency, IAlgebraOperator> result = new HashMap<Dependency, IAlgebraOperator>();
        for (Dependency dependency : extTGDs) {
            IAlgebraOperator standardInsert = treeBuilderForStandardChase.generateAlgebraForTargetTGD(dependency, scenario);
            if (logger.isDebugEnabled()) logger.debug("Operator for dependency " + dependency + "\n" + standardInsert);
            result.put(dependency, standardInsert);
        }
        return result;
    }
    
}
