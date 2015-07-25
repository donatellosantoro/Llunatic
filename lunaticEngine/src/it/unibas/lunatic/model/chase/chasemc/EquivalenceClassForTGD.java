package it.unibas.lunatic.model.chase.chasemc;

import it.unibas.lunatic.model.database.TableAlias;
import it.unibas.lunatic.model.database.TupleOID;
import it.unibas.lunatic.model.dependency.Dependency;
import it.unibas.lunatic.model.dependency.FormulaVariable;
import it.unibas.lunatic.utility.LunaticUtility;
import java.util.HashMap;
import java.util.Map;

public class EquivalenceClassForTGD {

    private Dependency tgd;
    private Map<FormulaVariable, TargetCellsToInsertForTGD> cellGroupForVariables = new HashMap<FormulaVariable, TargetCellsToInsertForTGD>();
    private Map<TableAlias, TupleOID> newTupleOIDs = new HashMap<TableAlias, TupleOID>();

    public EquivalenceClassForTGD(Dependency tgd) {
        this.tgd = tgd;
    }

    public Dependency getTgd() {
        return tgd;
    }

    public void setTargetCellToInsertForVariable(FormulaVariable variable, TargetCellsToInsertForTGD targetCell) {
        this.cellGroupForVariables.put(variable, targetCell);
    }

    public TargetCellsToInsertForTGD getTargetCellForVariable(FormulaVariable variable) {
        return cellGroupForVariables.get(variable);
    }

    public Map<FormulaVariable, TargetCellsToInsertForTGD> getCellGroupForVariables() {
        return cellGroupForVariables;
    }

    public TupleOID getTupleOIDForTable(TableAlias tableAlias) {
        return newTupleOIDs.get(tableAlias);
    }

    public void putTupleOIDForTableAlias(TableAlias tableAlias, TupleOID tupleOID) {
        this.newTupleOIDs.put(tableAlias, tupleOID);
    }

    public boolean hasNewCellsForVariable(FormulaVariable variable) {
        return cellGroupForVariables.get(variable).hasNewCells();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("EquivalenceClass for tgd=" + tgd.getId());
        sb.append("\nCellGroup For Variables: ").append(LunaticUtility.printMap(cellGroupForVariables));
        return sb.toString();
    }

}
