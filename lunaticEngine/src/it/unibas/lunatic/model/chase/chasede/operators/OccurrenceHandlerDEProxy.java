package it.unibas.lunatic.model.chase.chasede.operators;

import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.model.chase.chasemc.CellGroup;
import it.unibas.lunatic.model.chase.chasemc.CellGroupCell;
import it.unibas.lunatic.model.chase.chasemc.operators.OccurrenceHandlerMC;
import it.unibas.lunatic.model.chase.chasemc.operators.cache.ICacheManager;
import speedy.model.algebra.operators.IDelete;
import speedy.model.algebra.operators.IInsertTuple;
import speedy.model.database.ConstantValue;
import speedy.model.database.IDatabase;
import speedy.model.database.NullValue;
import speedy.model.database.operators.IRunQuery;

public class OccurrenceHandlerDEProxy extends OccurrenceHandlerMC {

    public OccurrenceHandlerDEProxy(IRunQuery queryRunner, IInsertTuple insertOperator, IDelete deleteOperator, ICacheManager cacheManager) {
        super(queryRunner, insertOperator, deleteOperator, cacheManager);
    }

    @Override
    public CellGroup enrichCellGroups(CellGroup preliminaryCellGroup, IDatabase deltaDB, String step, Scenario scenario) {
        if (preliminaryCellGroup.getValue() instanceof ConstantValue) {
            for (CellGroupCell occurrence : preliminaryCellGroup.getOccurrences()) {
                if(occurrence.getValue() instanceof NullValue){
                    throw new IllegalArgumentException("CellGroup with ConstantValues and NullCells");
                }
                occurrence.setOriginalValue(occurrence.getValue());
            }
            return preliminaryCellGroup;
        }
        return super.enrichCellGroups(preliminaryCellGroup, deltaDB, step, scenario);
    }
}
