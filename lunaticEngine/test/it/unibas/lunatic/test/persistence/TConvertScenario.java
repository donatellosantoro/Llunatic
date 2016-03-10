package it.unibas.lunatic.test.persistence;

import it.unibas.lunatic.persistence.relational.GenerateSQLFromDependencies;
import junit.framework.TestCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TConvertScenario extends TestCase {

    private final static Logger logger = LoggerFactory.getLogger(TConvertScenario.class);
    private GenerateSQLFromDependencies sqlGenerator = new GenerateSQLFromDependencies();

    public void testConvertMMToDBMS() throws Exception {
        String baseDir = "/Users/donatello/Temp/datasets/";
        String dependenciesString = baseDir + "dependencies-synth-chain0.xml";
        String sourceInitDBOutFile = baseDir + "source-initdb-chain0.xml";
        String targetInitDBOutFile = baseDir + "target-initdb-chain0.xml";
        sqlGenerator.generateSQL(dependenciesString, sourceInitDBOutFile, targetInitDBOutFile);
    }

}
