package it.unibas.lunatic.model.similarity;

import it.unibas.lunatic.model.database.IValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimpleEditsSimilarity implements ISimilarityStrategy {

    private static Logger logger = LoggerFactory.getLogger(SimpleEditsSimilarity.class);

    private static int minimumLenght = 3;
    private static int maximumDifferences = 2;

    public double computeSimilarity(IValue v1, IValue v2) {
        if (!v1.getType().equals(v2.getType())) {
            return 0.0;
        }
        String s1 = v1.toString();
        String s2 = v2.toString();
        return computeSimilarity(s1, s2);
    }

    public double computeSimilarity(String s1, String s2) {
        if (logger.isDebugEnabled()) logger.debug("Comparing value " + s1 + " with " + s2);
        if (s1.length() != s2.length()) {
            return 0.0;
        }
        if (s1.length() < minimumLenght) {
            return 0.0;
        }
        int differences = 0;
        for (int i = 0; i < s1.length(); i++) {
            char c1 = s1.charAt(i);
            char c2 = s2.charAt(i);
            if (c1 == c2) {
                continue;
            }
            differences++;
        }
        if (differences > maximumDifferences) {
            return 0.0;
        }
        return 1.0;
//        return 1 - ((double) differences) / s1.length();
    }
}
