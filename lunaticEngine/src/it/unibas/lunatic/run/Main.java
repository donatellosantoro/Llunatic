package it.unibas.lunatic.run;

import it.unibas.lunatic.LunaticConfiguration;
import it.unibas.lunatic.LunaticConstants;
import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.exceptions.DAOException;
import it.unibas.lunatic.model.chase.chasede.CheckConflictsResult;
import it.unibas.lunatic.model.chase.chasede.DEChaserFactory;
import it.unibas.lunatic.model.chase.chasede.IDEChaser;
import it.unibas.lunatic.model.chase.chasede.operators.CheckConflicts;
import it.unibas.lunatic.model.chase.chaseded.DEDChaserFactory;
import it.unibas.lunatic.model.chase.chasemc.operators.ChaseMCScenario;
import it.unibas.lunatic.model.chase.chasemc.DeltaChaseStep;
import it.unibas.lunatic.model.chase.chasemc.operators.ChaseTreeSize;
import it.unibas.lunatic.model.chase.commons.ChaseStats;
import it.unibas.lunatic.model.chase.commons.operators.ChaserFactoryMC;
import it.unibas.lunatic.persistence.DAOAccessConfiguration;
import it.unibas.lunatic.persistence.DAOConfiguration;
import it.unibas.lunatic.persistence.DAOLunaticConfiguration;
import it.unibas.lunatic.persistence.DAOMCScenario;
import it.unibas.lunatic.utility.LunaticUtility;
import java.io.File;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import org.jdom.Document;
import org.jdom.Element;
import speedy.exceptions.DBMSException;
import speedy.model.database.IDatabase;
import speedy.model.database.ITable;
import speedy.persistence.relational.AccessConfiguration;
import speedy.persistence.relational.QueryManager;
import speedy.persistence.xml.DAOXmlUtility;
import speedy.utility.DBMSUtility;
import speedy.utility.PrintUtility;

public class Main {

    private final static DateFormat df = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.LONG);
    private final static DAOMCScenario daoScenario = new DAOMCScenario();
    private final static DAOLunaticConfiguration daoConfiguration = new DAOLunaticConfiguration();
    private final static DAOAccessConfiguration daoAccessConfiguration = new DAOAccessConfiguration();

    public static void main(String[] args) {
        if (args.length < 1) {
            printUsage();
            return;
        }
        List<String> options = new ArrayList<String>(Arrays.asList(args));
        String relativePathScenario = null;
        for (Iterator<String> iterator = options.iterator(); iterator.hasNext();) {
            String option = iterator.next();
            if (option.startsWith("-")) {
                continue;
            }
            relativePathScenario = option;
            iterator.remove();
        }
        if (relativePathScenario == null) {
            printUsage();
            return;
        }
        File confFile = new File(relativePathScenario).getAbsoluteFile();
        if (!confFile.exists()) {
            System.out.println("Unable to load scenario. File " + relativePathScenario + " not found");
            return;
        }
        try {
            String fileScenario = confFile.getAbsolutePath();
            String chaseMode = LunaticUtility.getChaseMode(options);
            LunaticConfiguration conf = daoConfiguration.loadConfiguration(fileScenario, chaseMode);
            if (conf.isRecreateDBOnStart()) {
                removeExistingDB(fileScenario, conf);
            } else if (isDEScenario(fileScenario) && conf.isCleanSchemasOnStartForDEScenarios()) {
                AccessConfiguration accessConfiguration = daoAccessConfiguration.loadTargetAccessConfiguration(fileScenario, conf);
                DBMSUtility.cleanWorkTargetSchemas(accessConfiguration);
            }
            System.out.println("*** Loading scenario " + fileScenario + "... ");
            DAOConfiguration daoConfig = new DAOConfiguration();
            if (isCheckConflict(options)) {
                daoConfig.setUseCompactAttributeName(false);
            }
            daoConfig.setChaseMode(chaseMode);
            Scenario scenario = daoScenario.loadScenario(fileScenario, daoConfig);
            System.out.println(" Scenario loaded!");
            if (!bigScenario(scenario)) {
                System.out.println(scenario);
            }
            System.out.println("*** Chasing scenario (" + df.format(new Date()) + ")...");
            if (isCheckConflict(options)) {
                checkConflicts(scenario);
                return;
            }
            if (scenario.isDEDScenario()) {
                chaseDEDScenario(scenario);
            } else if (scenario.isDEScenario()) {
                chaseDEScenario(scenario);
            } else if (scenario.isMCScenario()) {
                chaseMCScenario(scenario);
            } else {
                throw new IllegalArgumentException("Scenario non supported!");
            }
            if (scenario.isDBMS()) {
                System.out.println("*** Check results on DBMS");
            }
        } catch (DAOException ex) {
            System.out.println("\nUnable to load scenario. \n" + ex.getLocalizedMessage());
        }finally{
            QueryManager.close();
        }
    }

    private static boolean isCheckConflict(List<String> options) {
        return options.contains(LunaticConstants.OPTION_CHECK_CONFLICTS);
    }

    private static void chaseDEScenario(Scenario scenario) {
        IDEChaser chaser = DEChaserFactory.getChaser(scenario);
        IDatabase result = chaser.doChase(scenario);
        System.out.println("*** Chasing DE scenario successful...");
        if (printDetails(scenario)) {
            System.out.println("--------------");
            System.out.println("Chase Result:");
            System.out.println(result);
        }
    }

    private static void chaseMCScenario(Scenario scenario) {
        ChaseMCScenario chaser = ChaserFactoryMC.getChaser(scenario);
        long start = new Date().getTime();
        DeltaChaseStep result = chaser.doChase(scenario);
        long end = new Date().getTime();
        double sec = (end - start) / 1000.0;
        ChaseTreeSize resultSizer = new ChaseTreeSize();
        PrintUtility.printSuccess("*** Chasing MC scenario successful...");
        System.out.println("Time elapsed: " + sec + " sec");
        System.out.println("Number of solutions: " + resultSizer.getPotentialSolutions(result));
        System.out.println("Number of duplicate solutions: " + resultSizer.getDuplicates(result));
        if (printDetails(scenario)) {
            System.out.println("--------------");
            System.out.println("Chase Result:");
            ChaseTreeSize sizer = new ChaseTreeSize();
            int size = sizer.getPotentialSolutions(result);
            if (size < 50) {
                System.out.println(result);
            } else {
                System.out.println("Solutions: " + size);
            }
        }
        System.out.println(ChaseStats.getInstance().toString());
    }

    private static void checkConflicts(Scenario scenario) {
        CheckConflicts checker = new CheckConflicts();
        CheckConflictsResult result = checker.doCheck(scenario);
        if (result.getConstantsToRemove().isEmpty()) {
            PrintUtility.printSuccess("** This de scenario does not contain conflicting constants.");
            return;
        }
        PrintUtility.printInformation("In order to remove hard conflicts, please delete the following values from source tables:");
        for (String value : result.getConstantsToRemove().keySet()) {
            System.out.println("Value: " + value + " from " + result.getConstantsToRemove().get(value));
        }
    }

    private static void chaseDEDScenario(Scenario scenario) {
        IDatabase result = DEDChaserFactory.getChaser(scenario).doChase(scenario);
        System.out.println("*** Chasing DED scenario successful...");
        if (printDetails(scenario)) {
            System.out.println("--------------");
            System.out.println("Chase Result:");
            System.out.println(result);
        }
        System.out.println(ChaseStats.getInstance().toString());
    }

    private static boolean printDetails(Scenario scenario) {
        return scenario.isMainMemory() && !bigScenario(scenario);
    }

    private static boolean bigScenario(Scenario scenario) {
        if (scenario.getSource() != null && scenario.getSource().getTableNames().size() > 10) {
            return true;
        }
        if (scenario.getTarget() != null && scenario.getTarget().getTableNames().size() > 10) {
            return true;
        }
        if (scenario.getSTTgds().size() > 10
                || scenario.getEGDs().size() > 10
                || scenario.getExtEGDs().size() > 10
                || scenario.getExtTGDs().size() > 10
                || scenario.getDEDEGDs().size() > 10) {
            return true;
        }
        for (String tableName : scenario.getSource().getTableNames()) {
            ITable table = scenario.getSource().getTable(tableName);
            if (table.getSize() > 100) {
                return true;
            }
        }
        for (String tableName : scenario.getTarget().getTableNames()) {
            ITable table = scenario.getTarget().getTable(tableName);
            if (table.getSize() > 100) {
                return true;
            }
        }
        return false;
    }

    public static void removeExistingDB(String fileScenario, LunaticConfiguration conf) {
        AccessConfiguration accessConfiguration = daoAccessConfiguration.loadTargetAccessConfiguration(fileScenario, conf);
        if (accessConfiguration == null) {
            return;
        }
        try {
            PrintUtility.printInformation("Removing db " + accessConfiguration.getDatabaseName() + ", if exist...");
            DBMSUtility.deleteDB(accessConfiguration);
            PrintUtility.printSuccess("Database removed!");
        } catch (DBMSException ex) {
            String message = ex.getMessage();
            if (!message.contains("does not exist")) {
                PrintUtility.printError("Unable to drop database.\n" + ex.getLocalizedMessage());
            }
        }
    }

    public static boolean isDEScenario(String fileScenario) {
        Document document = new DAOXmlUtility().buildDOM(fileScenario);
        Element rootElement = document.getRootElement();
        Element dependenciesElement = rootElement.getChild("dependencies");
        if (dependenciesElement == null) {
            return false;
        }
        String dependenciesString = dependenciesElement.getValue().trim();
        return !(dependenciesString.contains("ExtEGDs") && !dependenciesString.contains("DED-"));
    }

    private static void printUsage() {
        System.out.print("Usage: java -jar lunaticEngine.jar <path_scenario.xml>\n");
    }
}
