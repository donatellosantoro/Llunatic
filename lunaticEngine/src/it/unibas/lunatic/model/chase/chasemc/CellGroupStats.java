package it.unibas.lunatic.model.chase.chasemc;

public class CellGroupStats {
    
    public int totalCellGroups;
    public int llunCellGroups;
    public int nullCellGroups;
    public int constantCellGroups;
    public int totalOccurrences;
    public int totalProvenances;
    public int maxOccurrences;
    public int minOccurrences;
    public int maxProvenances;
    public int minProvenances;
    public long totalCellGroupHash;

    @Override
    public int hashCode() {
        return this.toString().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        final CellGroupStats other = (CellGroupStats) obj;
        return (this.toString().equals(other.toString()));
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append("CellGroupStats [").append("\n");
        result.append("\t TotalCellGroups: ").append(totalCellGroups).append("\n");
        result.append("\t LlunCellGroups: ").append(llunCellGroups).append("\n");
        result.append("\t NullCellGroups: ").append(nullCellGroups).append("\n");
        result.append("\t ConstantCellGroups: ").append(constantCellGroups).append("\n");
        result.append("\t TotalOccurrences: ").append(totalOccurrences).append("\n");
        result.append("\t TotalProvenances: ").append(totalProvenances).append("\n");
        result.append("\t MaxOccurrences: ").append(maxOccurrences).append("\n");
        result.append("\t MinOccurrences: ").append(minOccurrences).append("\n");
        result.append("\t MaxProvenances: ").append(maxProvenances).append("\n");
        result.append("\t MinProvenances: ").append(minProvenances).append("\n");
        result.append("\t TotalCellGroupHash: ").append(totalCellGroupHash).append("\n");
        result.append("]\n");
        return result.toString();
    }

    
    
    
}
