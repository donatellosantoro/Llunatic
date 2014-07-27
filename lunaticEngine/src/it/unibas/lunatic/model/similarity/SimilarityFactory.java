package it.unibas.lunatic.model.similarity;

public class SimilarityFactory {

    public static final String MIXED_STRATEGY = "Mixed";
    public static final String SIMPLE_EDITS = "SimpleEdits";
    public static final String LEVENSHTEIN_STRATEGY = "Levenshtein";
    public static final String SMITH_WATERMAN_STRATEGY = "SmithWaterman";
    public static final String JARO_STRATEGY = "Jaro";
    public static final String SOUNDEX_STRATEGY = "Soundex";
    public static final String JARO_WINKLER = "JaroWinkler";
    private static SimilarityFactory singleton = new SimilarityFactory();

    public static SimilarityFactory getInstance() {
        return singleton;
    }

    public ISimilarityStrategy getStrategy(String strategyName) {
        if (SIMPLE_EDITS.equals(strategyName)) {
            return new SimpleEditsSimilarity();
        }
        if (MIXED_STRATEGY.equals(strategyName)) {
            return new MixedSimilarity();
        }
        return new GenericStringSimilarity(strategyName);
    }
}
