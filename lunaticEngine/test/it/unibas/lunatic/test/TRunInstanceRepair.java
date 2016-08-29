package it.unibas.lunatic.test;

import it.unibas.lunatic.LunaticConstants;
import it.unibas.lunatic.run.Main;
import junit.framework.TestCase;

public class TRunInstanceRepair extends TestCase {

    public void testRun() {
//        String relativePath = "/resources/de/conflicts/instance0/mcscenario-dbms.xml";
        String relativePath = "/resources/de/conflicts/instance1/mcscenario-dbms.xml";
        String path = UtilityTest.getAbsoluteFileName(relativePath);
        Main.main(new String[]{path, LunaticConstants.OPTION_CHECK_CONFLICTS});
    }

}
