package it.unibas.lunatic.test.comparator.instances;

import it.unibas.lunatic.exceptions.ChaseException;
import it.unibas.lunatic.test.comparator.repairs.PrecisionAndRecall;
import it.unibas.spicybenchmark.model.features.FeatureResult;
import it.unibas.spicybenchmark.model.features.SimilarityResult;
import it.unibas.spicybenchmark.persistence.DAOConfiguration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CompareInstances {

    private static Logger logger = LoggerFactory.getLogger(CompareInstances.class);

    public List<PrecisionAndRecall> calculatePrecisionAndRecallValue(String expectedFile, String generatedFile, List<String> exclude, double precisionForVariable) {
        return calculatePrecisionAndRecallValue(Arrays.asList(new String[]{expectedFile}), Arrays.asList(new String[]{generatedFile}), exclude, precisionForVariable).get(expectedFile);
    }

    public List<PrecisionAndRecall> calculatePrecisionAndRecallValue(String expectedFile, List<String> generatedFiles, List<String> exclude, double precisionForVariable) {
        return calculatePrecisionAndRecallValue(Arrays.asList(new String[]{expectedFile}), generatedFiles, exclude, precisionForVariable).get(expectedFile);
    }

    public Map<String, List<PrecisionAndRecall>> calculatePrecisionAndRecallValue(List<String> expectedFiles, List<String> generatedFiles, List<String> exclude, double precisionForVariable) {
        Map<String, List<PrecisionAndRecall>> result = new HashMap<String, List<PrecisionAndRecall>>();
        try {
            it.unibas.spicybenchmark.CompareInstances comparator = new it.unibas.spicybenchmark.CompareInstances();
            List<SimilarityResult> similarityResults = comparator.evaluate(expectedFiles, generatedFiles, Arrays.asList(new String[]{DAOConfiguration.FEATURE_LOCAL_ID_WITH_LLUNS}), exclude, precisionForVariable);
            for (SimilarityResult similarityResult : similarityResults) {
                FeatureResult feature = similarityResult.getFeatureResultByFeatureName("FeatureLocalIdWithLLUNs");
                List<PrecisionAndRecall> precisionForExpected = result.get(similarityResult.getExpectedInstance());
                if (precisionForExpected == null) {
                    precisionForExpected = new ArrayList<PrecisionAndRecall>();
                    result.put(similarityResult.getExpectedInstance(), precisionForExpected);
                }
                PrecisionAndRecall pr = new PrecisionAndRecall(feature.getPrecision(), feature.getRecall(), feature.getFmeasure());
                pr.setExpectedInstance(similarityResult.getExpectedInstance());
                pr.setGeneratedInstance(similarityResult.getTranslatedInstance());
                precisionForExpected.add(pr);
                if (logger.isTraceEnabled()) logger.debug("## Similarity result\n\tExpected: " + similarityResult.getExpectedInstance() + "\n\tTranslated: " + similarityResult.getTranslatedInstance() + "\n\t" + pr);
                if (logger.isTraceEnabled()) logger.debug("## VIOLATIONS\n" + feature.getViolations().printViolations());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new ChaseException(ex.getLocalizedMessage());
        }
        return result;
    }
}
