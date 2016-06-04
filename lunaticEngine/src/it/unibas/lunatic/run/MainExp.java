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
            if (!chaseOnly) exec(MainExpImport.class, fileScenario);
            long endLoad = new Date().getTime();
            long loadTime = endLoad - startLoad;
            PrintUtility.printMessage("PreProcessing time: " + loadTime + " ms");
            //RUN
            long startRun = new Date().getTime();
            exec(MainExpRun.class, fileScenario);
            long endRun = new Date().getTime();
            long runTime = endRun - startRun;
            PrintUtility.printMessage("Chase and Query time: " + runTime + " ms");
            //EXPORT
            long startExport = new Date().getTime();
            if (!chaseOnly) exec(MainExpExport.class, fileScenario);
            long endExport = new Date().getTime();
            long exportTime = endExport - startExport;
            PrintUtility.printMessage("PostProcessing time: " + exportTime + " ms");
            //Results
            PrintUtility.printInformation("------------------------------------------");
            PrintUtility.printInformation("*** PreProcessing time:   " + loadTime + " ms");
            PrintUtility.printInformation("*** Chase and Query time: " + runTime + " ms");
            PrintUtility.printInformation("*** PostProcessing time:  " + exportTime + " ms");
            PrintUtility.printInformation("*** TOTAL TIME:           " + (loadTime + runTime + exportTime) + " ms");
            PrintUtility.printInformation("------------------------------------------");
        } catch (Exception ex) {
            PrintUtility.printError("Unable to execute command " + ex.getLocalizedMessage());
        }
    }

    public static int exec(Class klass, String fileScenario) throws Exception {
        String javaHome = System.getProperty("java.home");
        String javaBin = javaHome + File.separator + "bin" + File.separator + "java";
        if (logger.isDebugEnabled()) logger.debug("VM: " + javaBin);
        String classpath = System.getProperty("java.class.path");
        if (logger.isDebugEnabled()) logger.debug("Classpath: " + classpath);
        long memorySize = ((com.sun.management.OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean()).getTotalPhysicalMemorySize();
        int mbRam = (int) (memorySize / 1024 / 1024);
        System.out.println("Total RAM: " + mbRam + "mb");
        int vbRam = (int) (mbRam * 0.85);
        System.out.println("VM RAM: " + vbRam + "mb");
        String className = klass.getCanonicalName();
        String vmRamParams = "-Xmx" + vbRam + "m";
        String extraParams = "-Djava.util.logging.config.class=it.unibas.lunatic.utility.JavaUtilLoggingConfig";
        ProcessBuilder builder = new ProcessBuilder(javaBin, "-cp", classpath, vmRamParams, extraParams, className, fileScenario);
        builder.redirectOutput(ProcessBuilder.Redirect.INHERIT);
        builder.redirectError(ProcessBuilder.Redirect.INHERIT);
        Process process = builder.start();
        process.waitFor();
        return process.exitValue();
    }
}
