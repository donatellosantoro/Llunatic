package it.unibas.lunatic.model.chase.chasede;

import it.unibas.lunatic.LunaticConstants;
import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.model.chase.chasede.operators.ChaseDEScenarioProxy;

public class DEChaserFactory {

    public static IDEChaser getChaser(Scenario scenario) {
        String deChaserStrategy = scenario.getConfiguration().getDeChaser();
        if (deChaserStrategy.equals(LunaticConstants.PROXY_MC_CHASER)) {
            return getProxyMCChaser(scenario);
        }
        throw new IllegalArgumentException("DE Chaser " + deChaserStrategy + " is not supported");
    }

    private static IDEChaser getProxyMCChaser(Scenario scenario) {
        return new ChaseDEScenarioProxy();
    }

}
