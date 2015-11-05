package it.unibas.lunatic.test.mc.dbms;

import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.model.chase.chasemc.DeltaChaseStep;
import it.unibas.lunatic.model.chase.chasemc.operators.IExportSolution;
import it.unibas.lunatic.model.chase.chasemc.operators.dbms.SQLExportSolution;
import it.unibas.lunatic.model.chase.commons.ChaseUtility;
import it.unibas.lunatic.model.chase.commons.ChaserFactory;
import it.unibas.lunatic.test.References;
import it.unibas.lunatic.test.UtilityTest;
import it.unibas.lunatic.test.checker.CheckExpectedSolutionsTest;
import junit.framework.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestSQLIncrementalChase extends CheckExpectedSolutionsTest {

    private static Logger logger = LoggerFactory.getLogger(TestSQLIncrementalChase.class);
    private IExportSolution solutionExporter = new SQLExportSolution();

//    public void testScenario() throws Exception {
//        //STEP0
//        Scenario scenario00 = UtilityTest.loadScenarioFromResources(References.persons_incremental_00, true);
//        DeltaChaseStep result00 = ChaserFactory.getChaser(scenario00).doChase(scenario00);
//        Assert.assertEquals(1, resultSizer.getPotentialSolutions(result00));
//        DeltaChaseStep solution00 = ChaseUtility.getFirstLeaf(result00);
//        if (logger.isDebugEnabled()) logger.debug("First step result:\n" + solution00.toStringLeavesOnlyWithSort());
//        checkExpectedSolutions("expected-person-00", result00);
//        String suffix = "01";
//        solutionExporter.export(solution00, suffix, scenario00);
//        //STEP1
//        Scenario scenario01 = UtilityTest.loadScenarioFromResources(References.persons_incremental_01, suffix);
//        DeltaChaseStep result01 = ChaserFactory.getChaser(scenario01).doChase(scenario01);
//        Assert.assertEquals(1, resultSizer.getPotentialSolutions(result01));
//        DeltaChaseStep solution01 = ChaseUtility.getFirstLeaf(result01);
//        if (logger.isDebugEnabled()) logger.debug("First step result:\n" + solution01.toStringLeavesOnlyWithSort());
//        checkExpectedSolutions("expected-person-01", result01);
//        suffix = "02";
//        solutionExporter.export(solution01, suffix, scenario01);
//        //STEP2
//        Scenario scenario02 = UtilityTest.loadScenarioFromResources(References.persons_incremental_02, suffix);
//        DeltaChaseStep result02 = ChaserFactory.getChaser(scenario02).doChase(scenario02);
//        Assert.assertEquals(1, resultSizer.getPotentialSolutions(result02));
//        DeltaChaseStep solution02 = ChaseUtility.getFirstLeaf(result02);
//        if (logger.isDebugEnabled()) logger.debug("First step result:\n" + solution02.toStringLeavesOnlyWithSort());
//        checkExpectedSolutions("expected-person-02", result02);
//        suffix = "03";
//        solutionExporter.export(solution02, suffix, scenario02);
//    }
    public void testScenarioOverride() throws Exception {
        //STEP0
        Scenario scenario00 = UtilityTest.loadScenarioFromResources(References.persons_incremental_00, true);
        DeltaChaseStep result00 = ChaserFactory.getChaser(scenario00).doChase(scenario00);
        Assert.assertEquals(1, resultSizer.getPotentialSolutions(result00));
        DeltaChaseStep solution00 = ChaseUtility.getFirstLeaf(result00);
        if (logger.isDebugEnabled()) logger.debug("First step result:\n" + solution00.toStringLeavesOnlyWithSort());
        checkExpectedSolutions("expected-person-00", result00);
        String suffix = "01";
        solutionExporter.overrideWorkSchema(solution00, suffix, scenario00, true);
        //STEP1
        Scenario scenario01 = UtilityTest.loadScenarioFromResources(References.persons_incremental_01, suffix);
        DeltaChaseStep result01 = ChaserFactory.getChaser(scenario01).doChase(scenario01);
        Assert.assertEquals(1, resultSizer.getPotentialSolutions(result01));
        DeltaChaseStep solution01 = ChaseUtility.getFirstLeaf(result01);
        if (logger.isDebugEnabled()) logger.debug("First step result:\n" + solution01.toStringLeavesOnlyWithSort());
        checkExpectedSolutions("expected-person-01", result01);
        suffix = "02";
        solutionExporter.overrideWorkSchema(solution01, suffix, scenario01, true);
        //STEP2
        Scenario scenario02 = UtilityTest.loadScenarioFromResources(References.persons_incremental_02, suffix);
        DeltaChaseStep result02 = ChaserFactory.getChaser(scenario02).doChase(scenario02);
        Assert.assertEquals(1, resultSizer.getPotentialSolutions(result02));
        DeltaChaseStep solution02 = ChaseUtility.getFirstLeaf(result02);
        if (logger.isDebugEnabled()) logger.debug("First step result:\n" + solution02.toStringLeavesOnlyWithSort());
        checkExpectedSolutions("expected-person-02", result02);
        suffix = "03";
        solutionExporter.overrideWorkSchema(solution02, suffix, scenario02, true);
    }

}
