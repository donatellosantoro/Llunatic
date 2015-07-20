package it.unibas.lunatic.model.chase.chasemc;

import it.unibas.lunatic.utility.LunaticUtility;
import java.util.HashMap;
import java.util.Map;

public class CellGroupStats {
    
    public int totalCellGroups;
    public int llunCellGroups;
    public int nullCellGroups;
    public int constantCellGroups;
    public int totalOccurrences;
    public int totalJustifications;
    public int totalUserCells;
    public int totalInvalidCells;
    public int maxOccurrences;
    public int minOccurrences;
    public int maxJustifications;
    public int minJustifications;
    public long totalCellGroupHash;
    
    private Map<CellGroup, Integer> cellGroupHashes = new HashMap<CellGroup, Integer>();
    
    public void addCellGroupHash(CellGroup cellGroup, int hash) {
        this.cellGroupHashes.put(cellGroup, hash);
    }
    
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
        result.append("\t TotalJustifications: ").append(totalJustifications).append("\n");
        result.append("\t TotalUserCells: ").append(totalUserCells).append("\n");
        result.append("\t TotalInvalidCells: ").append(totalInvalidCells).append("\n");
        result.append("\t MaxOccurrences: ").append(maxOccurrences).append("\n");
        result.append("\t MinOccurrences: ").append(minOccurrences).append("\n");
        result.append("\t MaxJustifications: ").append(maxJustifications).append("\n");
        result.append("\t MinJustifications: ").append(minJustifications).append("\n");
        result.append("\t TotalCellGroupHash: ").append(totalCellGroupHash).append("\n");
        result.append("]\n");
        return result.toString();
    }
    
    public String toLongString() {
        return toString() + "\n" + LunaticUtility.printMap(this.cellGroupHashes);
    }
    
}
