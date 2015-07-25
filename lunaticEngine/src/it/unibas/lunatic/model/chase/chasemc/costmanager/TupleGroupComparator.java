package it.unibas.lunatic.model.chase.chasemc.costmanager;

import it.unibas.lunatic.model.chase.chasemc.CellGroup;
import it.unibas.lunatic.model.chase.chasemc.TargetCellsToChangeForEGD;
import java.util.Comparator;

class TupleGroupComparator implements Comparator<TargetCellsToChangeForEGD> {

    //V0
//    public int compare(TupleGroup t1, TupleGroup t2) {
//        int sizeDifference = t1.getConclusionGroup().getOccurrences().size() - t2.getConclusionGroup().getOccurrences().size();
//        if (sizeDifference != 0) {
//            return sizeDifference;
//        }
//        return t1.getConclusionGroup().getValue().toString().compareTo(t2.getConclusionGroup().getValue().toString());
//    }
    //V1
    public int compare(TargetCellsToChangeForEGD t1, TargetCellsToChangeForEGD t2) {
        int sizeDifference = getOccurrencesAndProvenances(t1) - getOccurrencesAndProvenances(t2);
        if (sizeDifference != 0) {
            return sizeDifference;
        }
        return t1.getCellGroupForForwardRepair().getValue().toString().compareTo(t2.getCellGroupForForwardRepair().getValue().toString());
    }

    private int getOccurrencesAndProvenances(TargetCellsToChangeForEGD t) {
        int count = 0;
        for (CellGroup cellGroup : t.getCellGroupsForBackwardRepairs().values()) {
            count += cellGroup.getOccurrences().size();
            count += cellGroup.getJustifications().size();
        }
        return count;
    }
}