package it.unibas.lunatic.model.chase.chasede.operators;

import it.unibas.lunatic.LunaticConfiguration;
import it.unibas.lunatic.OperatorFactory;
import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.exceptions.ChaseException;
import it.unibas.lunatic.model.chase.commons.operators.ChaseUtility;
import it.unibas.lunatic.model.chase.commons.IChaseState;
import speedy.model.thread.IBackgroundThread;
import it.unibas.lunatic.model.dependency.Dependency;
import it.unibas.lunatic.model.dependency.TGDStratum;
import it.unibas.lunatic.model.dependency.operators.DependencyUtility;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import speedy.model.algebra.IAlgebraOperator;

public class ChaseTargetTGDStratumThread implements IBackgroundThread {

    private final static Logger logger = LoggerFactory.getLogger(ChaseTargetTGDStratumThread.class);
    private ScheduleTGDStrata tgdScheduler;
    private TGDStratum stratum;
    private Map<Dependency, IAlgebraOperator> treeMap;
    private Scenario scenario;
    private IChaseState chaseState;

    public ChaseTargetTGDStratumThread(ScheduleTGDStrata tgdScheduler, TGDStratum stratum, Map<Dependency, IAlgebraOperator> treeMap, Scenario scenario, IChaseState chaseState) {
        this.tgdScheduler = tgdScheduler;
        this.stratum = stratum;
        this.treeMap = treeMap;
        this.scenario = scenario;
        this.chaseState = chaseState;
    }

    public void execute() {
        try {
            int iterations = 0;
            if (LunaticConfiguration.isPrintSteps()) System.out.println("---- Chasing tgd stratum: " + stratum.getId());
            if (logger.isDebugEnabled()) logger.debug("------------------Chasing stratum: ----\n" + stratum);
            List<Dependency> tgds = stratum.getTgds();
            Set<Dependency> unsatisfiedTGDs = new HashSet<Dependency>(tgds);
            while (true) {
                if (logger.isDebugEnabled()) logger.debug("Unsatisfied TGDs: " + unsatisfiedTGDs);
                boolean insertedTuples = false;
                for (Dependency eTgd : tgds) {
                    if (chaseState.isCancelled()) {
                        ChaseUtility.stopChase(chaseState);
                    }
                    if (!unsatisfiedTGDs.contains(eTgd)) {
                        continue;
                    }
                    if (LunaticConfiguration.isPrintSteps()) System.out.println("   ****Chasing tgd: " + eTgd.getId());
                    if (logger.isDebugEnabled()) logger.debug("----Chasing tgd: " + eTgd);
                    IAlgebraOperator treeRoot = treeMap.get(eTgd);
                    IInsertFromSelectNaive naiveInsert = OperatorFactory.getInstance().getInsertFromSelectNaive(scenario);
                    boolean newTuples = naiveInsert.execute(eTgd, treeRoot, scenario.getSource(), scenario.getTarget(), scenario) || insertedTuples;
                    if (!newTuples) {
                        if (logger.isDebugEnabled()) logger.debug("No new tuples. TGD " + eTgd + " is satisfied.");
                        unsatisfiedTGDs.remove(eTgd);
                    } else if (!(ChaseUtility.isUseLimit1ForTGD(eTgd, scenario)) || !DependencyUtility.hasUniversalVariablesInConclusion(eTgd)) {
                        if (logger.isDebugEnabled()) logger.debug("TGD " + eTgd + " is satisfied.");
                        unsatisfiedTGDs.remove(eTgd);
                    }
                    if (newTuples && DependencyUtility.hasUniversalVariablesInConclusion(eTgd)) {
                        if (logger.isDebugEnabled()) logger.debug("TGD " + eTgd.getId() + " inserted new tuples. Marking as unsatisfied the dependencies " + scenario.getStratification().getAffectedTGDsMap().get(eTgd));
                        unsatisfiedTGDs.addAll(scenario.getStratification().getAffectedTGDsMap().get(eTgd));
                    }
                    insertedTuples = newTuples || insertedTuples;
                }
                if (!insertedTuples) {
                    break;
                } else {
                    iterations++;
                    if (scenario.getConfiguration().getIterationLimit() != null && iterations > scenario.getConfiguration().getIterationLimit()) {
                        throw new ChaseException("Iteration limit reached in chasing extTgds. Stopping after " + scenario.getConfiguration().getIterationLimit() + " iterations");
                    }
                    tgdScheduler.setModified();
                }
            }
            tgdScheduler.addSatisfiedStratum(stratum);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

}
