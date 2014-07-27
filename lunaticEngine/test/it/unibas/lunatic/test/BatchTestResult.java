package it.unibas.lunatic.test;

import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.model.chase.chasemc.DeltaChaseStep;
import it.unibas.lunatic.model.database.IDatabase;

public class BatchTestResult {

    private String testName;
    private double executionTime;
    private int solutions;
    private int duplicateSolutions;
    private DeltaChaseStep chaseTreeRoot;
    private IDatabase deResult;
    private Scenario scenario;

    private BatchTestResult(Scenario scenario, String testName, double executionTime, int solutions, int duplicateSolutions) {
        this.scenario = scenario;
        this.testName = testName;
        this.executionTime = executionTime;
        this.solutions = solutions;
        this.duplicateSolutions = duplicateSolutions;
    }

    public BatchTestResult(Scenario scenario, String testName, double executionTime, int solutions, int duplicateSolutions, DeltaChaseStep chaseTreeRoot) {
        this(scenario, testName, executionTime, solutions, duplicateSolutions);
        this.chaseTreeRoot = chaseTreeRoot;
    }

    public BatchTestResult(Scenario scenario, String testName, double executionTime, int solutions, int duplicateSolutions, IDatabase deResult) {
        this(scenario, testName, executionTime, solutions, duplicateSolutions);
        this.deResult = deResult;
    }

    public double getExecutionTime() {
        return executionTime;
    }

    public int getSolutions() {
        return solutions;
    }

    public int getDuplicateSolutions() {
        return duplicateSolutions;
    }

    public int getNumberOfNodes() {
        if (chaseTreeRoot == null) {
            return 0;
        }
        return chaseTreeRoot.getNumberOfNodes();
    }

    public DeltaChaseStep getChaseTreeRoot() {
        return chaseTreeRoot;
    }

    public String getTestName() {
        return testName;
    }

    public IDatabase getDeResult() {
        return deResult;
    }

    public void setDeResult(IDatabase deResult) {
        this.deResult = deResult;
    }

    public Scenario getScenario() {
        return scenario;
    }

    public void setScenario(Scenario scenario) {
        this.scenario = scenario;
    }

    public void printResult() {
        System.out.println("Scenario " + testName);
        System.out.println("Number of solutions: " + solutions);
        System.out.println("Number of duplicate solutions: " + duplicateSolutions);
        System.out.println("Execution time: " + executionTime);
    }
}