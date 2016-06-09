package it.unibas.lunatic.run;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import speedy.utility.PrintUtility;

public class MainExp {

    private final static Logger logger = LoggerFactory.getLogger(MainExp.class);

    public static void main(String[] args) {
        if (args.length < 1 || args.length > 2) {
            System.out.print("Usage: Usage: runExp.sh <path_task.xml> [-chaseonly]\n");
            return;
        }
        boolean chaseOnly = false;
        List<String> options = new ArrayList<String>(Arrays.asList(args));
        if (options.contains("-chaseonly")) {
            options.remove("-chaseonly");
            chaseOnly = true;
        }
        String relativePathScenario = options.get(0);
        File confFile = new File(relativePathScenario).getAbsoluteFile();
        if (!confFile.exists()) {
            System.out.println("Unable to load scenario. File " + relativePathScenario + " not found");
            return;
        }
        String fileScenario = confFile.getAbsolutePath();
        try {
            //LOAD
            long startLoad = new Date().getTime();
            boolean errorsInLoad = exec(MainExpImport.class, fileScenario, chaseOnly);
            long endLoad = new Date().getTime();
            long loadTime = endLoad - startLoad;
            PrintUtility.printMessage("Import time: " + loadTime + " ms");
            //RUN
            long startRun = new Date().getTime();
            boolean errorsInRun = exec(MainExpRun.class, fileScenario, false);
            long endRun = new Date().getTime();
            long runTime = endRun - startRun;
            PrintUtility.printMessage("Chase time: " + runTime + " ms");
            //EXPORT
            long startExport = new Date().getTime();
            boolean errorsInExport = exec(MainExpExport.class, fileScenario, false);
            long endExport = new Date().getTime();
            long exportTime = endExport - startExport;
            PrintUtility.printMessage("Export and Query time: " + exportTime + " ms");
            //Results
            PrintUtility.printInformation("------------------------------------------");
            PrintUtility.printInformation("*** Import time:   " + (errorsInLoad ? "ERRORS" : loadTime + " ms"));
            PrintUtility.printInformation("*** Chase time: " + (errorsInRun ? "ERRORS" : runTime + " ms"));
            PrintUtility.printInformation("*** Export and Query time:  " + (errorsInExport ? "ERRORS" : exportTime + " ms"));
            PrintUtility.printInformation("*** TOTAL TIME:           " + (loadTime + runTime + exportTime) + " ms");
            PrintUtility.printInformation("------------------------------------------");
        } catch (Exception ex) {
            PrintUtility.printError("Unable to execute command " + ex.getLocalizedMessage());
        }
    }

    public static boolean exec(Class klass, String fileScenario, boolean chaseOnly) throws Exception {
        String javaHome = System.getProperty("java.home");
        String javaBin = javaHome + File.separator + "bin" + File.separator + "java";
        if (logger.isDebugEnabled()) logger.debug("VM: " + javaBin);
        String classpath = System.getProperty("java.class.path");
        if (logger.isDebugEnabled()) logger.debug("Classpath: " + classpath);
        long memorySize = ((com.sun.management.OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean()).getTotalPhysicalMemorySize();
        int mbRam = (int) (memorySize / 1024 / 1024);
        if (logger.isDebugEnabled()) logger.debug("Total RAM: " + mbRam + "mb");
        int vbRam = (int) (mbRam * 0.85);
        if (logger.isDebugEnabled()) logger.debug("VM RAM: " + vbRam + "mb");
        String className = klass.getCanonicalName();
        String vmRamParams = "-Xmx" + vbRam + "m";
        String extraParams = "-Djava.util.logging.config.class=it.unibas.lunatic.utility.JavaUtilLoggingConfig";
        List<String> commands = new ArrayList<String>();
        commands.add(javaBin);
        commands.add("-cp");
        commands.add(classpath);
        commands.add(vmRamParams);
        commands.add(extraParams);
        commands.add(className);
        commands.add(fileScenario);
        if (chaseOnly) {
            commands.add("-chaseonly");
        }
        ProcessBuilder builder = new ProcessBuilder(commands);
        builder.redirectOutput(ProcessBuilder.Redirect.INHERIT);
        builder.redirectError(ProcessBuilder.Redirect.INHERIT);
        Process process = builder.start();
        process.waitFor();
        int exitValue = process.exitValue();
        return (exitValue != 0); //Return true if errors
    }
}
