package it.unibas.lunatic.model.chase.chasemc.costmanager;

import it.unibas.lunatic.model.chase.chasemc.CellGroup;
import it.unibas.lunatic.model.chase.chasemc.EGDEquivalenceClassTupleCells;
import java.util.Comparator;

public class TupleGroupComparator implements Comparator<EGDEquivalenceClassTupleCells> {

    //V0
//    public int compare(TupleGroup t1, TupleGroup t2) {
//        int sizeDifference = t1.getConclusionGroup().getOccurrences().size() - t2.getConclusionGroup().getOccurrences().size();
//        if (sizeDifference != 0) {
//            return sizeDifference;
//        }
//        return t1.getConclusionGroup().getValue().toString().compareTo(t2.getConclusionGroup().getValue().toString());
//    }
//    public int compare(EGDEquivalenceClassTupleCellsOLD t1, EGDEquivalenceClassTupleCellsOLD t2) {
//        int sizeDifference = getOccurrencesAndProvenances(t1) - getOccurrencesAndProvenances(t2);
//        if (sizeDifference != 0) {
//            return sizeDifference;
//        }
//        return t1.getCellGroupForForwardRepair().getValue().toString().compareTo(t2.getCellGroupForForwardRepair().getValue().toString());
//    }
//
//    private int getOccurrencesAndProvenances(EGDEquivalenceClassTupleCellsOLD t) {
//        int count = 0;
//        for (Set<Cell> witnessCells : t.getWitnessCells().values()) {
//            count += witnessCells.size();
//        }
//        return count;
//    }
    //V2 TODO++ check
    public int compare(EGDEquivalenceClassTupleCells t1, EGDEquivalenceClassTupleCells t2) {
        int sizeDifference = size(t1.getConclusionGroup()) - size(t2.getConclusionGroup());
        if (sizeDifference != 0) {
            return sizeDifference;
        }
        return t1.getConclusionGroup().getValue().toString().compareTo(t2.getConclusionGroup().getValue().toString());
    }

    private int size(CellGroup cellGroup) {
        return cellGroup.getOccurrences().size() + cellGroup.getJustifications().size() + cellGroup.getUserCells().size();
    }
}
