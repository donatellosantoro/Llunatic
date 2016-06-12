package it.unibas.lunatic.run;

import it.unibas.lunatic.LunaticConfiguration;
import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.model.chase.commons.ChaseStats;
import it.unibas.lunatic.persistence.DAOAccessConfiguration;
import it.unibas.lunatic.persistence.DAOConfiguration;
import it.unibas.lunatic.persistence.DAOLunaticConfiguration;
import it.unibas.lunatic.persistence.DAOMCScenario;
import static it.unibas.lunatic.run.Main.isDEScenario;
import static it.unibas.lunatic.run.Main.removeExistingDB;
import it.unibas.lunatic.utility.LunaticUtility;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import speedy.persistence.relational.AccessConfiguration;
import speedy.utility.DBMSUtility;

public class MainExpImport {

    private final static DAOMCScenario daoScenario = new DAOMCScenario();
    private final static DAOLunaticConfiguration daoConfiguration = new DAOLunaticConfiguration();
    private final static DAOAccessConfiguration daoAccessConfiguration = new DAOAccessConfiguration();

    public static void main(String[] args) {
        List<String> options = new ArrayList<String>(Arrays.asList(args));
        boolean chaseOnly = false;
        if (options.contains("-chaseonly")) {
            options.remove("-chaseonly");
            chaseOnly = true;
        }
        String fileScenario = options.get(0);
        LunaticConfiguration conf = daoConfiguration.loadConfiguration(fileScenario);
        LunaticUtility.applyCommandLineOptions(conf, options);
        if (chaseOnly) {
            conf.setRecreateDBOnStart(false);
            conf.setCleanSchemasOnStartForDEScenarios(true);
        }
        DAOConfiguration daoConfig = new DAOConfiguration();
        if (conf.isRecreateDBOnStart()) {
            long start = new Date().getTime();
            removeExistingDB(fileScenario, conf);
            long end = new Date().getTime();
            ChaseStats.getInstance().addStat(ChaseStats.DROP_EXISTING_DB, (end - start));
            daoConfig.setRemoveExistingDictionary(true);
        } else if (isDEScenario(fileScenario) && conf.isCleanSchemasOnStartForDEScenarios()) {
            long start = new Date().getTime();
            AccessConfiguration accessConfiguration = daoAccessConfiguration.loadTargetAccessConfiguration(fileScenario, conf);
            DBMSUtility.renameExistingWorkTargetSchemas(accessConfiguration);
            long end = new Date().getTime();
            ChaseStats.getInstance().addStat(ChaseStats.CLEAN_EXISTING_DB, (end - start));
        }
        System.out.println("*** Loading scenario " + fileScenario + "... ");
        daoConfig.setImportData(true);
        daoConfig.setProcessDependencies(false);
        daoConfig.setExportEncodedDependencies(true);
        LunaticUtility.applyCommandLineOptions(daoConfig, options);
        Scenario scenario = daoScenario.loadScenario(fileScenario, daoConfig);
        System.out.println(" Scenario loaded!");
        if (scenario.getValueEncoder() != null) {
            scenario.getValueEncoder().waitingForEnding();
        }
        if (LunaticConfiguration.isPrintSteps()) System.out.println(ChaseStats.getInstance().toString());
    }
}
