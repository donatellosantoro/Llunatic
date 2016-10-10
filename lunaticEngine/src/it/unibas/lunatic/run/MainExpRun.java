package it.unibas.lunatic.run;

import it.unibas.lunatic.LunaticConfiguration;
import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.model.chase.chasede.DEChaserFactory;
import it.unibas.lunatic.model.chase.chaseded.DEDChaserFactory;
import it.unibas.lunatic.model.chase.commons.ChaseStats;
import it.unibas.lunatic.model.chase.commons.operators.ChaserFactoryMC;
import it.unibas.lunatic.persistence.DAOConfiguration;
import it.unibas.lunatic.persistence.DAOMCScenario;
import it.unibas.lunatic.utility.LunaticUtility;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import speedy.utility.PrintUtility;

public class MainExpRun {

    private final static DAOMCScenario daoScenario = new DAOMCScenario();

    public static void main(String[] args) {
        List<String> options = new ArrayList<String>(Arrays.asList(args));
        if (options.contains("-queryonly")) {
            return;
        }
        String fileScenario = options.get(0);
        String chaseMode = LunaticUtility.getChaseMode(options);
        DAOConfiguration daoConfig = new DAOConfiguration();
        daoConfig.setImportData(false);
        daoConfig.setUseEncodedDependencies(true);
        daoConfig.setUseCompactAttributeName(true);
        daoConfig.setChaseMode(chaseMode);
        LunaticUtility.applyCommandLineOptions(daoConfig, options);
        Scenario scenario = daoScenario.loadScenario(fileScenario, daoConfig);
        LunaticConfiguration conf = scenario.getConfiguration();
        LunaticUtility.applyCommandLineOptions(conf, options);
        conf.setCleanSchemasOnStartForDEScenarios(false);
        conf.setRecreateDBOnStart(false);
        conf.setExportSolutions(false);
        conf.setExportChanges(false);
        conf.setPrintResults(false);
        scenario.getQueries().clear();//Handled in MainExpExport
        scenario.getSQLQueries().clear(); //Handled in MainExpExport
        if (scenario.isDEDScenario()) {
            DEDChaserFactory.getChaser(scenario).doChase(scenario);
        } else if (scenario.isDEScenario()) {
            DEChaserFactory.getChaser(scenario).doChase(scenario);
        } else if (scenario.isMCScenario()) {
            ChaserFactoryMC.getChaser(scenario).doChase(scenario);
        } else {
            throw new IllegalArgumentException("Scenario non supported!");
        }
        if (LunaticConfiguration.isPrintSteps()) System.out.println(ChaseStats.getInstance().toString());
        PrintUtility.printMessage("-> ST-TGD time: " + ChaseStats.getInstance().getStat(ChaseStats.STTGD_TIME) + " ms");
    }
}
