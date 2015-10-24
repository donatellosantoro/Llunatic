package it.unibas.lunatic.test;

import it.unibas.lunatic.model.similarity.SimilarityFactory;
import junit.framework.TestCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import speedy.model.database.ConstantValue;
import speedy.model.database.IValue;

public class TSimilarity extends TestCase {

    private static Logger logger = LoggerFactory.getLogger(TSimilarity.class);

    private double similarityThreshold = 0.60;

    public void testCommutativity() {
        String[][] values = new String[][]{{"Smith", "Doe"}, {"Doe", "Smith"}, {"Birmingham", "Bixmixgham"}, {"Bixmixgham", "Birmingham"}};
        for (int i = 0; i < values.length; i++) {
            IValue string1 = new ConstantValue(values[i][0]);
            IValue string2 = new ConstantValue(values[i][1]);
            logger.info(string1 + " - " + string2 + " similarity: " + SimilarityFactory.getInstance().getStrategy("Levenshtein", null).computeSimilarity(string1, string2));            
//            logger.info(string1 + " - " + string2 + " similarity: " + SimilarityFactory.getInstance().getStrategy("SmithWaterman").computeSimilarity(string1, string2));            
//            logger.info(string1 + " - " + string2 + " similarity: " + SimilarityFactory.getInstance().getStrategy("Soundex").computeSimilarity(string1, string2));            
//            logger.info(string1 + " - " + string2 + " similarity: " + SimilarityFactory.getInstance().getStrategy("Jaro").computeSimilarity(string1, string2));            
        }
    }
//    @SuppressWarnings("unchecked")
//    public void test() {
//        StringBuilder[] results = {
//            new StringBuilder("MT\n"),
//            new StringBuilder("BACKWARD\n"),
//            new StringBuilder("FORWARD\n")
//        };
//        ArrayList<Repair>[] repairs = new ArrayList[]{
//            new ArrayList<Repair>(new DAOCSVRepair().loadRepair(UtilityTest.getAbsoluteFileName(References.similarity_mt))),
//            new ArrayList<Repair>(new DAOCSVRepair().loadRepair(UtilityTest.getAbsoluteFileName(References.similarity_backward))),
//            new ArrayList<Repair>(new DAOCSVRepair().loadRepair(UtilityTest.getAbsoluteFileName(References.similarity_forward)))
//        };
//        String[] strategies = new String[]{
//            SimilarityFactory.SIMPLE_EDITS,
//            SimilarityFactory.MIXED_STRATEGY,
//            "Levenshtein",
//            "SmithWaterman"
//        };
//        for (String strategy : strategies) {
//            double sum = 0.0;
//            for (int i = 0; i < repairs.length; i++) {
//                ArrayList<Repair> repair = repairs[i];
//                sum += computeQuality(repair, strategy, (i % 2) == 0, results[i]);
//            }
//        }
//        for (int i = 0; i < repairs.length; i++) {
//            System.out.println("");
//            System.out.println(results[i]);
//        }
//    }
//    private double computeQuality(Collection<Repair> repairs, String similarityStrategyName, boolean similar, StringBuilder result) {
//        ISimilarityStrategy similarityStrategy = SimilarityFactory.getInstance().getStrategy(similarityStrategyName);
//        int totalTime = 0;
//        int recognized = 0;
//        double min = 1.0;
//        double max = 0.0;
//        for (Repair repair : repairs) {
//            long t1 = new Date().getTime();
//            double similarity = similarityStrategy.computeSimilarity(repair.getGroundValue(), repair.getDirtyValue());
//            long t2 = new Date().getTime();
//            totalTime += (t2 - t1);
//            if (similarity >= similarityThreshold) {
//                if (similar) {
//                    recognized++;
//                } else {
////                    System.out.println(repair);
//                }
//            } else {
//                if (!similar) {
//                    recognized++;
//                } else {
////                    System.out.println(repair);
//                }
//            }
//            if (similarity > max) {
//                max = similarity;
//            }
//            if (similarity < min) {
//                min = similarity;
//            }
//        }
//        double recall = (recognized / (double) repairs.size());
////        System.out.println("SimilarityStrategy " + similarityStrategyName);
////        System.out.println("Size: " + repairs.size());
////        System.out.println("Total time: " + totalTime);
////        System.out.println("Similar: " + similar);
////        System.out.println("Min: " + min);
////        System.out.println("Max: " + max);
////        System.out.println("Recall: " + recall);
////        System.out.println("\n\n");
//        result.append(recall).append(", ").append(similarityStrategyName).append(", ").append(totalTime).append("\n");
//        return recall;
//    }
}
