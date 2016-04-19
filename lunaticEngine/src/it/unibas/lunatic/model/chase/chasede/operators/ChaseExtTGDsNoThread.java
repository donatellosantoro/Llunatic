package it.unibas.lunatic.model.chase.chasede.operators;

import it.unibas.lunatic.LunaticConfiguration;
import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.exceptions.ChaseException;
import it.unibas.lunatic.model.algebra.operators.BuildAlgebraTreeForStandardChase;
import it.unibas.lunatic.model.chase.commons.ChaseUtility;
import it.unibas.lunatic.model.chase.commons.control.IChaseState;
import it.unibas.lunatic.model.dependency.Dependency;
import it.unibas.lunatic.model.dependency.DependencyStratification;
import it.unibas.lunatic.model.dependency.TGDStratum;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import speedy.model.algebra.IAlgebraOperator;

public class ChaseExtTGDsNoThread {

    private final static Logger logger = LoggerFactory.getLogger(ChaseExtTGDsNoThread.class);

    private BuildAlgebraTreeForStandardChase insertGenerator = new BuildAlgebraTreeForStandardChase();
    private IInsertFromSelectNaive naiveInsert;

    public ChaseExtTGDsNoThread(IInsertFromSelectNaive naiveInsert) {
        this.naiveInsert = naiveInsert;
    }

    public boolean doChase(Scenario scenario, IChaseState chaseState) {
        if (logger.isDebugEnabled()) logger.debug("Chasing t-tgds " + scenario.getExtTGDs());
        Map<Dependency, IAlgebraOperator> treeMap = buildAlgebraTrees(scenario.getExtTGDs(), scenario);
        boolean modified = false;
        DependencyStratification stratification = scenario.getStratification();
        for (TGDStratum stratum : stratification.getTGDStrata()) {
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
                        ChaseUtility.stopChase(chaseState); //throw new ChaseException("Chase interrupted by user");
                    }
                    if (!unsatisfiedTGDs.contains(eTgd)) {
                        continue;
                    }
                    if (LunaticConfiguration.isPrintSteps()) System.out.println("   ****Chasing tgd: " + eTgd.getId());
                    if (logger.isDebugEnabled()) logger.debug("----Chasing tgd: " + eTgd);
                    IAlgebraOperator treeRoot = treeMap.get(eTgd);
                    boolean newTuples = naiveInsert.execute(eTgd, treeRoot, scenario.getSource(), scenario.getTarget(), scenario) || insertedTuples;
                    unsatisfiedTGDs.remove(eTgd);
                    if (newTuples) {
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
                    modified = true;
                }
            }
        }
        return modified;
    }

    private Map<Dependency, IAlgebraOperator> buildAlgebraTrees(List<Dependency> extTGDs, Scenario scenario) {
        Map<Dependency, IAlgebraOperator> result = new HashMap<Dependency, IAlgebraOperator>();
        for (Dependency dependency : extTGDs) {
            IAlgebraOperator standardInsert = insertGenerator.generate(dependency, scenario);
            if (logger.isDebugEnabled()) logger.debug("Operator for dependency " + dependency + "\n" + standardInsert);
            result.put(dependency, standardInsert);
        }
        return result;
    }
}
