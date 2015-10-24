package it.unibas.lunatic.model.chase.chasemc.costmanager;

public class SimilarityConfiguration {

    private String strategy;
    private double threshold;

    public SimilarityConfiguration(String strategy, double threshold) {
        this.strategy = strategy;
        this.threshold = threshold;
    }

    public String getStrategy() {
        return strategy;
    }

    public double getThreshold() {
        return threshold;
    }

    public void setStrategy(String strategy) {
        this.strategy = strategy;
    }

    public void setThreshold(double threshold) {
        this.threshold = threshold;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 47 * hash + (this.strategy != null ? this.strategy.hashCode() : 0);
        hash = 47 * hash + (int) (Double.doubleToLongBits(this.threshold) ^ (Double.doubleToLongBits(this.threshold) >>> 32));
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        final SimilarityConfiguration other = (SimilarityConfiguration) obj;
        if ((this.strategy == null) ? (other.strategy != null) : !this.strategy.equals(other.strategy)) return false;
        if (Double.doubleToLongBits(this.threshold) != Double.doubleToLongBits(other.threshold)) return false;
        return true;
    }

    @Override
    public String toString() {
        return "Similarity Strategy: " + strategy + ", threshold: " + threshold;
    }

}
