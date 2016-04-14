package it.unibas.lunatic.model.chase.chasede;

import it.unibas.lunatic.LunaticConstants;
import it.unibas.lunatic.OperatorFactory;
import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.model.chase.chasede.operators.ChaseDEScenarioProxy;
import it.unibas.lunatic.model.chase.chasede.operators.ChaseDEWithTGDOnlyScenario;
import it.unibas.lunatic.model.chase.chasede.operators.IInsertFromSelectNaive;
import it.unibas.lunatic.model.chase.chasede.operators.IRemoveDuplicates;
import it.unibas.lunatic.model.chase.chasemc.operators.IBuildDatabaseForChaseStep;
import it.unibas.lunatic.model.chase.chasemc.operators.IBuildDeltaDB;
import it.unibas.lunatic.model.chase.commons.IChaseSTTGDs;
import speedy.model.database.operators.IRunQuery;

public class DEChaserFactory {

    public static IDEChaser getChaser(Scenario scenario) {
        String deChaserStrategy = scenario.getConfiguration().getDeChaser();
        if (deChaserStrategy.equals(LunaticConstants.OPTIMIZED_CHASER)) {
            return getOptimizedChaser(scenario);
        }
        if (deChaserStrategy.equals(LunaticConstants.PROXY_MC_CHASER)) {
            return getProxyMCChaser();
        }
        throw new IllegalArgumentException("DE Chaser " + deChaserStrategy + " is not supported");
    }

    private static IDEChaser getOptimizedChaser(Scenario scenario) {
        if (scenario.getEGDs().isEmpty()) {
            IChaseSTTGDs stChaser = OperatorFactory.getInstance().getSTChaser(scenario);
            IInsertFromSelectNaive insertFromSelectNaive = OperatorFactory.getInstance().getInsertFromSelectNaive(scenario);
            IRunQuery queryRunner = OperatorFactory.getInstance().getQueryRunner(scenario);
            IBuildDeltaDB deltaBuilder = OperatorFactory.getInstance().getDeltaDBBuilder(scenario);
            IBuildDatabaseForChaseStep databaseBuilder = OperatorFactory.getInstance().getDatabaseBuilder(scenario);
            IRemoveDuplicates duplicateRemover = OperatorFactory.getInstance().getDuplicateRemover(scenario);
            ChaseDEWithTGDOnlyScenario tgdChaser = new ChaseDEWithTGDOnlyScenario(stChaser, queryRunner, insertFromSelectNaive, deltaBuilder, databaseBuilder, duplicateRemover);
            return tgdChaser;
        }
        return getProxyMCChaser();
    }

    private static IDEChaser getProxyMCChaser() {
        return new ChaseDEScenarioProxy();
    }

}
