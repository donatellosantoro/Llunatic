package it.unibas.lunatic.test;

import it.unibas.lunatic.run.MainExp;
import junit.framework.TestCase;

public class TRun extends TestCase {

    public void testRun() {
//        String relativePath = "/resources/de/conflicts/instance1/mcscenario-dbms.xml";
//        String relativePath = "/resources/de/chasebench/correctess/tgds-egds-large-mcscenario-dbms.xml";
//        String relativePath = "/resources/de/chasebench/correctess/tgds-egds-mcscenario-dbms.xml";
//        String relativePath = "/resources/de/chasebench/correctess/tgds-mcscenario-dbms.xml";
//        String relativePath = "/resources/de/chasebench/correctess/tgds5-mcscenario-dbms.xml";
//        String relativePath = "/resources/de/chasebench/correctess/vldb2010-mcscenario-dbms.xml";
//        String relativePath = "/resources/de/chasebench/correctess/weak-mcscenario-dbms.xml";
//        String path = UtilityTest.getAbsoluteFileName(relativePath);
        String path = "/chasebench/tools/llunatic/input/deep/deep-100-mcscenario-dbms.xml";
//        String path = "/chasebench/tools/llunatic/input/deep/deep-200-mcscenario-dbms.xml";
//        Main.main(new String[]{path});
        MainExp.main(new String[]{path});
//        MainExp.main(new String[]{path, "-printsteps=true"});
//        MainExp.main(new String[]{path, "-useDictionaryEncoding=false"});
//        MainExp.main(new String[]{path, "-printsteps=true","-useDictionaryEncoding=false"});
    }

}
