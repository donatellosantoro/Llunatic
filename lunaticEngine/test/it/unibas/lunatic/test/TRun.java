package it.unibas.lunatic.test;

import it.unibas.lunatic.run.MainExp;
import junit.framework.TestCase;

public class TRun extends TestCase {
    
    public void testRun() {
        String path = "/Users/donatello/Projects/Llunatic-Ex/lunaticExperiments/misc/experiments/chasebench/doctors/doctors-10k-mcscenario-dbms.xml";
        MainExp.main(new String[]{path});
    }
    
}
