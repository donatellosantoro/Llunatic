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
import java.util.Date;
import speedy.persistence.relational.AccessConfiguration;
import speedy.utility.DBMSUtility;

public class MainExpImport {

    private final static DAOMCScenario daoScenario = new DAOMCScenario();
    private final static DAOLunaticConfiguration daoConfiguration = new DAOLunaticConfiguration();
    private final static DAOAccessConfiguration daoAccessConfiguration = new DAOAccessConfiguration();

    public static void main(String[] args) {
        String fileScenario = args[0];
        LunaticConfiguration conf = daoConfiguration.loadConfiguration(fileScenario);
        if (conf.isRecreateDBOnStart()) {
            long start = new Date().getTime();
            removeExistingDB(fileScenario, conf);
            long end = new Date().getTime();
            ChaseStats.getInstance().addStat(ChaseStats.DROP_EXISTING_DB, (end - start));
        } else if (isDEScenario(fileScenario) && conf.isCleanSchemasOnStartForDEScenarios()) {
            long start = new Date().getTime();
            AccessConfiguration accessConfiguration = daoAccessConfiguration.loadTargetAccessConfiguration(fileScenario, conf);
            DBMSUtility.renameExistingWorkTargetSchemas(accessConfiguration);
            long end = new Date().getTime();
            ChaseStats.getInstance().addStat(ChaseStats.CLEAN_EXISTING_DB, (end - start));
        }
        System.out.println("*** Loading scenario " + fileScenario + "... ");
        DAOConfiguration daoConfig = new DAOConfiguration();
        daoConfig.setImportData(true);
        daoConfig.setProcessDependencies(false);
        daoConfig.setExportRewrittenDependencies(true);
        Scenario scenario = daoScenario.loadScenario(fileScenario, daoConfig);
        System.out.println(" Scenario loaded!");
        if (scenario.getValueEncoder() != null) {
            scenario.getValueEncoder().waitingForEnding();
        }
        System.out.println(ChaseStats.getInstance().toString());
    }
}
