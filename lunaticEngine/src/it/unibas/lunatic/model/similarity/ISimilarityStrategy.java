package it.unibas.lunatic.model.similarity;

import speedy.model.database.IValue;

public interface ISimilarityStrategy {

    public double computeSimilarity(IValue v1, IValue v2);

    public double computeSimilarity(String s1, String s2);
}
