package it.unibas.lunatic.test;

import it.unibas.lunatic.model.similarity.ISimilarityStrategy;
import it.unibas.lunatic.model.similarity.SimilarityFactory;
import it.unibas.lunatic.test.comparator.repairs.DAOCSVRepair;
import it.unibas.lunatic.test.comparator.repairs.Repair;
import java.util.ArrayList;
import java.util.Collection;
import junit.framework.TestCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TSimilarityBatch extends TestCase {

    private static Logger logger = LoggerFactory.getLogger(TSimilarityBatch.class);

    @SuppressWarnings("unchecked")
    public void test() {
        ArrayList<Repair>[] repairs = new ArrayList[]{
            new ArrayList<Repair>(new DAOCSVRepair().loadRepair(UtilityTest.getAbsoluteFileName(References.similarity_mt))),
            new ArrayList<Repair>(new DAOCSVRepair().loadRepair(UtilityTest.getAbsoluteFileName(References.similarity_backward))), //            new ArrayList<Repair>(new DAOCSVRepair().loadRepair(UtilityTest.getAbsoluteFileName(References.similarity_forward)))
        };
        String[] strategies = new String[]{
            "MongeElkan",
            "ChapmanOrderedNameCompoundSimilarity",
            "Jaro",
            "JaroWinkler",
            "Levenshtein",
            "NeedlemanWunch",
            "SmithWaterman",
            "Soundex",
            "TagLinkToken", //            "ChapmanLengthDeviation",//LOW QUALITY
        //            "QGramsDistance", //LOW QUALITY
        //            "OverlapCoefficient",//LOW QUALITY
        //            "BlockDistance", //LOW QUALITY
        //            "ChapmanMeanLength",//LOW QUALITY
        //            "CosineSimilarity", //LOW QUALITY
        //            "DiceSimilarity", //LOW QUALITY
        //            "EuclideanDistance", //LOW QUALITY
        //            "JaccardSimilarity",//LOW QUALITY
        //            "MatchingCoefficient", //LOW QUALITY
        //            "ChapmanMatchingSoundex" //SLOW
        //            "SmithWatermanGotoh", //SLOW
        //            "SmithWatermanGotohWindowedAffine", //SLOW
        };
        StringBuilder totalResult = new StringBuilder();
        for (String strategy : strategies) {
            for (int threashold = 50; threashold < 75; threashold += 5) {
                double sum = 0.0;
                for (int i = 0; i < repairs.length; i++) {
                    ArrayList<Repair> repair = repairs[i];
                    sum += computeQuality(repair, strategy, threashold / 100.0, (i % 2) == 0);
                }
                totalResult.append(sum).append(", ").append(strategy).append(", ").append(threashold).append("\n");
            }
        }
        System.out.println(totalResult);
    }

    private double computeQuality(Collection<Repair> repairs, String similarityStrategyName, double similarityThreshold, boolean similar) {
        ISimilarityStrategy similarityStrategy = SimilarityFactory.getInstance().getStrategy(similarityStrategyName, null);
        int recognized = 0;
        for (Repair repair : repairs) {
            double similarity = similarityStrategy.computeSimilarity(repair.getGroundValue(), repair.getDirtyValue());
            if (similarity >= similarityThreshold) {
                if (similar) {
                    recognized++;
                }
            } else {
                if (!similar) {
                    recognized++;
                }
            }
        }
        return (recognized / (double) repairs.size());
    }
}
