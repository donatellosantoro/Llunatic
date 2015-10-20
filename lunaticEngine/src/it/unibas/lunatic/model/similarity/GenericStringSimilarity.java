package it.unibas.lunatic.model.similarity;

import speedy.model.database.IValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.shef.wit.simmetrics.similaritymetrics.InterfaceStringMetric;

public class GenericStringSimilarity implements ISimilarityStrategy {

    private static Logger logger = LoggerFactory.getLogger(GenericStringSimilarity.class);

    private static int minimumLenght = 1;
//    private static int minimumLenght = 3;
    private static InterfaceStringMetric comparator;
    private static final String PACKAGE_NAME = "uk.ac.shef.wit.simmetrics.similaritymetrics";

    public GenericStringSimilarity(String className) {
        try {
            comparator = (InterfaceStringMetric) Class.forName(PACKAGE_NAME + "." + className).newInstance();
        } catch (Exception ex) {
            throw new IllegalArgumentException("Unable to load class " + className);
        }
    }

    public double computeSimilarity(IValue v1, IValue v2) {
        if (!v1.getType().equals(v2.getType())) {
            return 0.0;
        }
        String s1 = v1.toString();
        String s2 = v2.toString();
        return computeSimilarity(s1, s2);
    }

    public double computeSimilarity(String s1, String s2) {
        if (isNumeric(s1) && isNumeric(s2)) {
            double d1 = Double.parseDouble(s1);
            double d2 = Double.parseDouble(s2);
            return computeNumericalSimilarity(d1, d2);
        }
        if (logger.isDebugEnabled()) logger.debug("Comparing value " + s1 + " with " + s2);
        if (s1.length() < minimumLenght || s2.length() < minimumLenght) {
            return 0.0;
        }
        float similarity = comparator.getSimilarity(s1, s2);
        if (logger.isDebugEnabled()) logger.debug("Similarity: " + similarity);
        return similarity;
    }

    private boolean isNumeric(String s) {
        try {
            Double.parseDouble(s);
            return true;
        } catch (NumberFormatException ne) {
            return false;
        }
    }

    private double computeNumericalSimilarity(double d1, double d2) {
        return 1 - (Math.abs(d1 - d2) / Math.max(d1, d2));
    }
}
