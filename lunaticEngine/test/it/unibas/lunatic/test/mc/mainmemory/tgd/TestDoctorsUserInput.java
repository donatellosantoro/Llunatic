package it.unibas.lunatic.test.mc.mainmemory.tgd;

import it.unibas.lunatic.OperatorFactory;
import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.model.chase.chasemc.CellGroup;
import it.unibas.lunatic.model.chase.chasemc.operators.ChaseMCScenario;
import it.unibas.lunatic.model.chase.chasemc.operators.AddUserNode;
import it.unibas.lunatic.model.chase.chasemc.DeltaChaseStep;
import it.unibas.lunatic.model.chase.commons.ChaserFactory;
import it.unibas.lunatic.test.References;
import it.unibas.lunatic.test.UtilityTest;
import it.unibas.lunatic.test.checker.CheckTest;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import speedy.model.database.ConstantValue;
import speedy.model.database.LLUNValue;

public class TestDoctorsUserInput extends CheckTest {

    private static Logger logger = LoggerFactory.getLogger(TestDoctorsUserInput.class);

    private AddUserNode userNodeCreator;

    public void testScenario() throws Exception {
        Scenario scenario = UtilityTest.loadScenarioFromResources(References.doctors_usermanager);
//        Scenario scenario = UtilityTest.loadScenarioFromResources(References.treatments_usermanager);
        userNodeCreator = OperatorFactory.getInstance().getUserNodeCreator(scenario);
        scenario.getCostManagerConfiguration().setDoBackward(false);
        scenario.getCostManagerConfiguration().setDoPermutations(false);
//        scenario.setUserManager(new StandardUserManager());
        ChaseMCScenario chaser = ChaserFactory.getChaser(scenario);
        DeltaChaseStep result = chaser.doChase(scenario);
        if (logger.isDebugEnabled()) logger.debug("Result #1:\n" + result.toStringWithSort());
        addUserNode(result, scenario);
//        if (logger.isDebugEnabled()) logger.debug("Result after user node #1:\n" + result.toStringWithSort());

        result = chaser.doChase(result, scenario);
        if (logger.isDebugEnabled()) logger.debug("Result #2:\n" + result.toStringWithSort());

//        Assert.assertEquals(resultSizer.getLeaves(result), resultSizer.getSolutions(result)); //With StandardUserManager
        if (logger.isDebugEnabled()) logger.debug("Number of leaves: " + resultSizer.getPotentialSolutions(result));
        if (logger.isDebugEnabled()) logger.debug("Number of solutions: " + resultSizer.getSolutions(result));
        if (logger.isDebugEnabled()) logger.debug("Number of duplicate solutions: " + resultSizer.getDuplicates(result));
        if (logger.isDebugEnabled()) logger.debug("Number of duplicate solutions: " + resultSizer.getGroundSolutions(result));
//        Assert.assertEquals(53, resultSizer.getSize(result));
//        Assert.assertEquals(0, resultSizer.getDuplicates(result));
    }
    private static int changed = 0;

    private void addUserNode(DeltaChaseStep node, Scenario scenario) {
        if (node.isLeaf()) {
            if (changed < 1) {
                DeltaChaseStep newStep = userNodeCreator.addUserNode(node, scenario);
                List<CellGroup> cellGroups = OperatorFactory.getInstance().getOccurrenceHandlerMC(scenario).loadAllCellGroupsForDebugging(newStep.getDeltaDB(), newStep.getId(), scenario);
                for (CellGroup cellGroup : cellGroups) {
                    if (cellGroup.getValue() instanceof LLUNValue) {
                        if (logger.isDebugEnabled()) logger.debug("Changing " + cellGroup.getValue() + " to 6666");
                        userNodeCreator.addChange(newStep, cellGroup, new ConstantValue("6666"), scenario);
                        changed++;
                    }
                }
            }
        } else {
            for (DeltaChaseStep child : node.getChildren()) {
                addUserNode(child, scenario);
            }
        }
    }
}
