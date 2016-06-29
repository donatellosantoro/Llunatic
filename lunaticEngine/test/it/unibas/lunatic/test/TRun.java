package it.unibas.lunatic.test;

import it.unibas.lunatic.run.MainExp;
import junit.framework.TestCase;

public class TRun extends TestCase {
    
    public void testRun() {
//        String path = "/chasebench/tools/llunatic/input/deep/deep-200-mcscenario-dbms.xml";
//        String path = "/chasebench/tools/llunatic/input/doctors/doctors-10k-mcscenario-dbms.xml";
//        String path = "/chasebench/tools/llunatic/input/STB-128/STB-128-mcscenario-dbms.xml";
//        String path = "/chasebench/tools/llunatic/input/Ontology-256/Ontology-256-mcscenario-dbms.xml";
//        String path = "/chasebench/tools/llunatic/input/LUBM/LUBM-100-mcscenario-dbms.xml";
//        String path = "/Users/donatello/Desktop/correctess/tgds-egds-mcscenario-dbms.xml";
        String path = "/Users/donatello/Desktop/correctess/tgds-egds-large-mcscenario-dbms.xml";
//        String path = "/Users/donatello/Desktop/correctess/tgds-mcscenario-dbms.xml";
//        String path = "/Users/donatello/Desktop/correctess/tgds5-mcscenario-dbms.xml";
//        String path = "/Users/donatello/Desktop/correctess/vldb2010-mcscenario-dbms.xml";
//        String path = "/Users/donatello/Desktop/correctess/weak-mcscenario-dbms.xml";
        MainExp.main(new String[]{path, "-printsteps=true"});
//        MainExp.main(new String[]{path, "-printsteps=true","-useDictionaryEncoding=false"});
//        MainExp.main(new String[]{path});
    }
    
}
