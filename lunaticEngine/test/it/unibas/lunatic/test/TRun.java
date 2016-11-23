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
//        String path = "/chasebench/tools/llunatic/input/Ontology-256/Ontology-256-mcscenario-dbms.xml";
//        String path = "/chasebench/tools/llunatic/input-noegd/Ontology-256/Ontology-256-mcscenario-dbms.xml";
//        String path = "/chasebench/tools/llunatic/input-stonly/Ontology-256/Ontology-256-mcscenario-dbms.xml";
//        String path = "/chasebench/tools/llunatic/input/STB-128/STB-128-mcscenario-dbms.xml";
//        String path = "/chasebench/tools/llunatic/input-noegd/STB-128/STB-128-mcscenario-dbms.xml";
//        String path = "/chasebench/tools/llunatic/input-stonly/STB-128/STB-128-mcscenario-dbms.xml";
//        String path = "/chasebench/tools/llunatic/input/LUBM/LUBM-001-mcscenario-dbms.xml";
//        String path = "/chasebench/tools/llunatic/input/LUBM/LUBM-010-mcscenario-dbms.xml";
//        String path = "/chasebench/tools/llunatic/input/LUBM/LUBM-100-mcscenario-dbms.xml";
//        String path = "/chasebench/tools/llunatic/input/LUBM/LUBM-001-mcscenario-dbms.xml";
//        String path = "/chasebench/tools/llunatic/input/deep/deep-100-mcscenario-dbms.xml";
//        String path = "/chasebench/tools/llunatic/input/deep/deep-200-mcscenario-dbms.xml";
//        String path = "/chasebench/tools/llunatic/input/doctors/doctors-10k-mcscenario-dbms.xml";
//        String path = "/chasebench/tools/llunatic/input/doctors/doctors-500k-mcscenario-dbms.xml";
//        String path = "/chasebench/tools/llunatic/input/doctors/doctors-1m-mcscenario-dbms.xml";
//        String path = "/chasebench/tools/llunatic/input/doctors-fd/doctors-fd-10k-mcscenario-dbms.xml";
//        String path = "/chasebench/tools/llunatic/input/doctors-fd/doctors-fd-10k-mcscenario-dbms.xml";
//        String path = "/chasebench/tools/llunatic/input/doctors-fd/doctors-fd-1m-mcscenario-dbms.xml";
//        String path = "/chasebench/tools/llunatic/input-stonly/doctors/doctors-10k-mcscenario-dbms.xml";
//        String path = "/chasebench/tools/llunatic/input-stonly/doctors/doctors-100k-mcscenario-dbms.xml";
//        String path = "/chasebench/tools/llunatic/input-stonly/doctors/doctors-1m-mcscenario-dbms.xml";
//        String path = "/chasebench/tools/llunatic/input-stonly/doctors/doctors-500k-mcscenario-dbms.xml";
//        String path = "/Users/donatello/Temp/chaseBench-workspace/STB-128-lite/STB-128-lite-mcscenario-dbms.xml";
        String path = "/Users/donatello/Temp/chaseBench-workspace/500/general-mcscenario-dbms.xml";
//        Main.main(new String[]{path});
//        MainExp.main(new String[]{path});
        MainExp.main(new String[]{path, "-printTargetStats=true", "-printsteps=true"});
//        MainExp.main(new String[]{path, "-printTargetStats=true","-chaseMode=unrestricted-skolem", "-printsteps=true"});
//        MainExp.main(new String[]{path, "-printTargetStats=true","-chaseMode=restricted-skolem", "-printsteps=true"});
//        MainExp.main(new String[]{path, "-printTargetStats=true","-chaseMode=standard"});
//        MainExp.main(new String[]{path, "-queryonly"});
//        MainExp.main(new String[]{path, "-printsteps=true"});
//        MainExp.main(new String[]{path, "-useDictionaryEncoding=false"});
//        MainExp.main(new String[]{path, "-printsteps=true","-useDictionaryEncoding=false"});
    }

}
