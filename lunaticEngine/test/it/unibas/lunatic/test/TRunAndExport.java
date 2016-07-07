package it.unibas.lunatic.test;

import it.unibas.lunatic.OperatorFactory;
import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.model.chase.chasemc.DeltaChaseStep;
import it.unibas.lunatic.model.chase.chasemc.operators.ChaseMCScenario;
import it.unibas.lunatic.model.chase.commons.operators.ChaserFactoryMC;
import it.unibas.lunatic.persistence.DAOMCScenario;
import it.unibas.lunatic.persistence.relational.GenerateModifiedCells;
import java.io.IOException;
import junit.framework.TestCase;

public class TRunAndExport extends TestCase {

    private static final DAOMCScenario daoMCScenario = new DAOMCScenario();

    public void testRun() throws IOException {
        Scenario scenario = daoMCScenario.loadScenario("/Users/donatello/Desktop/BART-SIGMOD-Demo/bus/lunatic/bus-5k-5-dbms.xml");
        ChaseMCScenario chaser = ChaserFactoryMC.getChaser(scenario);
        DeltaChaseStep result = chaser.doChase(scenario);
        GenerateModifiedCells operator = new GenerateModifiedCells(OperatorFactory.getInstance().getOccurrenceHandlerMC(scenario));
        operator.generate(result, "/Users/donatello/Desktop/BART-SIGMOD-Demo/bus/OUTPUT/repair.csv");
    }
}
