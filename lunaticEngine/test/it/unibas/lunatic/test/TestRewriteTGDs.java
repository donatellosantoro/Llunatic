package it.unibas.lunatic.test;

import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.model.dependency.Dependency;
import it.unibas.lunatic.model.dependency.RewriteSTTGDs;
import it.unibas.spicy.persistence.DAOException;
import junit.framework.TestCase;

public class TestRewriteTGDs extends TestCase {

    public void testRewrite() throws DAOException {
//        Scenario scenario = UtilityTest.loadScenarioFromAbsolutePath("/Users/donatello/Projects/Llunatic-Ex/lunaticExperiments/misc/experiments/chasebench/synthetic/synthetic-100-mcscenario-dbms.xml");
        Scenario scenario = UtilityTest.loadScenarioFromAbsolutePath("/Users/donatello/Projects/Llunatic-Ex/lunaticExperiments/misc/experiments/chasebench/doctors/doctors-10k-mcscenario-dbms.xml");
        RewriteSTTGDs rewriter = new RewriteSTTGDs();
        rewriter.rewrite(scenario);
        assertEquals(5, scenario.getSTTgds().size());
        for (Dependency stTgd : scenario.getSTTgds()) {
            System.out.println(stTgd.toLogicalString());
        }
        if (scenario.getValueEncoder() != null) scenario.getValueEncoder().waitingForEnding();
    }

}
