package it.unibas.lunatic.model.similarity;

import speedy.model.database.IValue;

public class MixedSimilarity implements ISimilarityStrategy {
    
    private ISimilarityStrategy similarityA = new SimpleEditsSimilarity();
    private ISimilarityStrategy similarityB = new GenericStringSimilarity(SimilarityFactory.SMITH_WATERMAN_STRATEGY);
//    private ISimilarityStrategy similarityB = new GenericStringSimilarity(SimilarityFactory.LEVENSHTEIN_STRATEGY);


    public double computeSimilarity(IValue v1, IValue v2) {
        if (!v1.getType().equals(v2.getType())) {
            return 0.0;
        }
        String s1 = v1.toString();
        String s2 = v2.toString();
        return computeSimilarity(s1, s2);
    }

    public double computeSimilarity(String s1, String s2) {
        return Math.max(similarityA.computeSimilarity(s1, s2), similarityB.computeSimilarity(s1, s2));
    }
}
