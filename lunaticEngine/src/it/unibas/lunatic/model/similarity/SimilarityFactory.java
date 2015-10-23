package it.unibas.lunatic.model.similarity;

import java.util.HashMap;
import java.util.Map;

public class SimilarityFactory {

    public static final String MIXED_STRATEGY = "Mixed";
    public static final String SIMPLE_EDITS = "SimpleEdits";
    public static final String LEVENSHTEIN_STRATEGY = "Levenshtein";
    public static final String SMITH_WATERMAN_STRATEGY = "SmithWaterman";
    public static final String JARO_STRATEGY = "Jaro";
    public static final String SOUNDEX_STRATEGY = "Soundex";
    public static final String JARO_WINKLER = "JaroWinkler";
    private static SimilarityFactory singleton = new SimilarityFactory();
    private Map<String, ISimilarityStrategy> cache = new HashMap<String, ISimilarityStrategy>();

    public static SimilarityFactory getInstance() {
        return singleton;
    }

    public ISimilarityStrategy getStrategy(String strategyName) {
        ISimilarityStrategy cachedStrategy = cache.get(strategyName);
        if (cachedStrategy == null) {
            cachedStrategy = createStrategy(strategyName);
            cache.put(strategyName, cachedStrategy);
        }
        return cachedStrategy;
    }

    private ISimilarityStrategy createStrategy(String strategyName) {
        if (SIMPLE_EDITS.equals(strategyName)) {
            return new SimpleEditsSimilarity();
        }
        if (MIXED_STRATEGY.equals(strategyName)) {
            return new MixedSimilarity();
        }
        return new GenericStringSimilarity(strategyName);
    }

    public void reset() {
        this.cache.clear();
    }
}
