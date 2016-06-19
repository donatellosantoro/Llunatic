package it.unibas.lunatic.model.similarity;

import java.util.Map;
import speedy.model.database.IValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NumericalSimilarity implements ISimilarityStrategy {

    private static Logger logger = LoggerFactory.getLogger(NumericalSimilarity.class);

    private Map<String, String> params;

    public NumericalSimilarity(Map<String, String> params) {
        this.params = params;
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
        if (!isNumeric(s1) || !isNumeric(s2)) {
            throw new IllegalArgumentException("Numerical similarity is for number only. " + s1 + " - " + s2);
        }
        double d1 = Double.parseDouble(s1);
        double d2 = Double.parseDouble(s2);
        double difference = Math.abs(d1 - d2);
        double maximalDifference = getMaximalDifference();
        if (difference > maximalDifference) {
            return 0.0;
        }
        return 1 - (difference / maximalDifference);
    }

    private boolean isNumeric(String s) {
        try {
            Double.parseDouble(s);
            return true;
        } catch (NumberFormatException ne) {
            return false;
        }
    }

    private double getMaximalDifference() {
        String value = params.get("maximalDifference");
        if (value == null) {
            return 0.0;
        }
        return Double.parseDouble(value);
    }
}
