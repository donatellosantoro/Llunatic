package it.unibas.lunatic.test;

import it.unibas.lunatic.utility.LunaticUtility;
import it.unibas.lunatic.utility.combinatorial.GenericMultiCombinationsGenerator;
import java.util.Arrays;
import java.util.List;
import junit.framework.TestCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TGenericMultiCombinationsGenerator extends TestCase {

    private static Logger logger = LoggerFactory.getLogger(TGenericMultiCombinationsGenerator.class);

    public void test() {
        List<Integer> list1 = Arrays.asList(new Integer[]{1, 2, 3});
        List<List<Integer>> result = new GenericMultiCombinationsGenerator<Integer>().generate(list1, 4);
        logger.info(LunaticUtility.printCollection(result));
    }
}
