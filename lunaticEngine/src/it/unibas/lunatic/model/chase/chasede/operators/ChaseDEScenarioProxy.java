package it.unibas.lunatic.model.chase.chasede.operators;

import it.unibas.lunatic.OperatorFactory;
import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.exceptions.ChaseException;
import it.unibas.lunatic.exceptions.ChaseFailedException;
import it.unibas.lunatic.model.chase.commons.control.IChaseState;
import it.unibas.lunatic.model.chase.commons.control.ImmutableChaseState;
import it.unibas.lunatic.model.chase.chasede.IDEChaser;
import it.unibas.lunatic.model.chase.chasemc.operators.ChaseMCScenario;
import it.unibas.lunatic.model.chase.chasemc.DeltaChaseStep;
import it.unibas.lunatic.model.chase.chasemc.costmanager.CostManagerConfiguration;
import it.unibas.lunatic.model.chase.chasemc.operators.IBuildDatabaseForChaseStep;
import it.unibas.lunatic.model.chase.chasemc.partialorder.DEPartialOrder;
import it.unibas.lunatic.model.chase.commons.ChaserFactory;
import speedy.model.database.IDatabase;
import it.unibas.lunatic.model.dependency.Dependency;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChaseDEScenarioProxy implements IDEChaser {

    private static Logger logger = LoggerFactory.getLogger(ChaseDEScenarioProxy.class);

    public IDatabase doChase(Scenario scenario, IChaseState chaseState) {
        ChaseMCScenario mcChaser = ChaserFactory.getChaser(scenario);
        List<Dependency> egds = scenario.getEGDs();
        scenario.setEGDs(new ArrayList<Dependency>());
        scenario.setExtEGDs(egds);
        CostManagerConfiguration costManagerConfiguration = new CostManagerConfiguration();
        costManagerConfiguration.setDoBackward(false);
        costManagerConfiguration.setDoPermutations(false);
        scenario.setCostManagerConfiguration(costManagerConfiguration);
        scenario.setPartialOrder(new DEPartialOrder());
        scenario.getConfiguration().setDeProxyMode(true);
        scenario.getConfiguration().setRemoveDuplicates(false);
        DeltaChaseStep chaseStep = mcChaser.doChase(scenario);
        if (logger.isDebugEnabled()) logger.debug("----MC result: " + chaseStep);
        if (chaseStep.getNumberOfLeaves() > 1) {
            throw new ChaseException("MCChaser returns more then one solution");
        }
        DeltaChaseStep solution = getSolution(chaseStep);
        if (solution.isInvalid()) {
            throw new ChaseFailedException("Chase fails. No solutions...");
        }
        IBuildDatabaseForChaseStep databaseBuilder = OperatorFactory.getInstance().getDatabaseBuilder(scenario);
        IDatabase result = databaseBuilder.extractDatabaseWithDistinct(solution.getId(), solution.getDeltaDB(), solution.getOriginalDB());
        if (logger.isDebugEnabled()) logger.debug("----Result of chase: " + result);
        return result;
    }

    public IDatabase doChase(Scenario scenario) {
        return doChase(scenario, ImmutableChaseState.getInstance());
    }

    private DeltaChaseStep getSolution(DeltaChaseStep chaseStep) {
        if (chaseStep.isLeaf()) {
            return chaseStep;
        }
        return getSolution(chaseStep.getChildren().get(0));
    }
}
