package it.unibas.lunatic.run;

import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.model.chase.chasede.DEChaserFactory;
import it.unibas.lunatic.model.chase.chaseded.DEDChaserFactory;
import it.unibas.lunatic.model.chase.commons.operators.ChaserFactory;
import it.unibas.lunatic.persistence.DAOConfiguration;
import it.unibas.lunatic.persistence.DAOMCScenario;

public class MainExpRun {

    private final static DAOMCScenario daoScenario = new DAOMCScenario();

    public static void main(String[] args) {
        String fileScenario = args[0];
        DAOConfiguration daoConfig = new DAOConfiguration();
        daoConfig.setImportData(false);
        daoConfig.setUseRewrittenDependencies(true);
        Scenario scenario = daoScenario.loadScenario(fileScenario, daoConfig);
        scenario.getConfiguration().setCleanSchemasOnStartForDEScenarios(false);
        scenario.getConfiguration().setRecreateDBOnStart(false);
        scenario.getConfiguration().setExportSolutions(false);
        scenario.getConfiguration().setExportChanges(false);
        scenario.getConfiguration().setPrintQueryResultsOnly(true);
        if (scenario.isDEDScenario()) {
            DEDChaserFactory.getChaser(scenario).doChase(scenario);
        } else if (scenario.isDEScenario()) {
            DEChaserFactory.getChaser(scenario).doChase(scenario);
        } else if (scenario.isMCScenario()) {
            ChaserFactory.getChaser(scenario).doChase(scenario);
        } else {
            throw new IllegalArgumentException("Scenario non supported!");
        }
    }
}
