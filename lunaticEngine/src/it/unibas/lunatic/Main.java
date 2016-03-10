package it.unibas.lunatic;

import it.unibas.lunatic.exceptions.DAOException;
import it.unibas.lunatic.model.chase.chasede.DEChaserFactory;
import it.unibas.lunatic.model.chase.chasede.IDEChaser;
import it.unibas.lunatic.model.chase.chaseded.DEDChaserFactory;
import it.unibas.lunatic.model.chase.chasemc.operators.ChaseMCScenario;
import it.unibas.lunatic.model.chase.chasemc.DeltaChaseStep;
import it.unibas.lunatic.model.chase.chasemc.operators.ChaseTreeSize;
import it.unibas.lunatic.model.chase.commons.ChaseStats;
import it.unibas.lunatic.model.chase.commons.ChaserFactory;
import it.unibas.lunatic.persistence.DAOMCScenario;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import speedy.model.database.IDatabase;
import speedy.model.database.ITable;

public class Main {

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.print("Usage: java -jar lunaticEngine.jar <path_scenario.xml>\n");
            return;
        }
        String relativePathScenario = args[0];
        File confFile = new File(relativePathScenario).getAbsoluteFile();
        if (!confFile.exists()) {
            System.out.println("Unable to load scenario. File " + relativePathScenario + " not found");
            return;
        }
        try {
            String fileScenario = confFile.getAbsolutePath();
            System.out.print("*** Loading scenario " + fileScenario + "... ");
            DAOMCScenario daoScenario = new DAOMCScenario();
            Scenario scenario = daoScenario.loadScenario(fileScenario);
            System.out.println(" Scenario loaded!");
            if (!bigScenario(scenario)) {
                System.out.println(scenario);
            }
            System.out.println("*** Chasing scenario...");
            long start = new Date().getTime();
            if (scenario.isDEDScenario()) {
                chaseDEDScenario(scenario);
            } else if (scenario.isDEScenario()) {
                chaseDEScenario(scenario);
            } else if (scenario.isMCScenario()) {
                chaseMCScenario(scenario);
            } else {
                throw new IllegalArgumentException("Scenario non supported!");
            }
            long end = new Date().getTime();
            double executionTime = (end - start) / 1000.0;
            System.out.println("*** Execution time: " + executionTime + " sec");
            if (scenario.isDBMS()) {
                System.out.println("*** Check results on DBMS");
            }
        } catch (DAOException ex) {
            System.out.println("\nUnable to load scenario. \n" + ex.getLocalizedMessage());
        }
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
        System.out.println(ChaseStats.getInstance().toString());
    }

    private static void chaseMCScenario(Scenario scenario) {
        ChaseMCScenario chaser = ChaserFactory.getChaser(scenario);
        long start = new Date().getTime();
        DeltaChaseStep result = chaser.doChase(scenario);
        long end = new Date().getTime();
        double sec = (end - start) / 1000.0;
        ChaseTreeSize resultSizer = new ChaseTreeSize();
        System.out.println("*** Chasing MC scenario successful...");
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
}
