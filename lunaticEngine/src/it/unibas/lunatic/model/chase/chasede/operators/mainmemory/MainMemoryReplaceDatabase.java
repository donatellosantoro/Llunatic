package it.unibas.lunatic.model.chase.chasede.operators.mainmemory;

import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.model.chase.chasede.operators.IReplaceDatabase;
import java.util.Iterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import speedy.model.algebra.operators.ITupleIterator;
import speedy.model.algebra.operators.mainmemory.MainMemoryInsertTuple;
import speedy.model.database.Cell;
import speedy.model.database.IDatabase;
import speedy.model.database.Tuple;
import speedy.model.database.mainmemory.MainMemoryDB;
import speedy.model.database.mainmemory.MainMemoryTable;
import speedy.model.database.mainmemory.MainMemoryVirtualDB;
import speedy.model.database.mainmemory.MainMemoryVirtualTable;

public class MainMemoryReplaceDatabase implements IReplaceDatabase {

    private final static Logger logger = LoggerFactory.getLogger(MainMemoryReplaceDatabase.class);
    private final static MainMemoryInsertTuple insert = new MainMemoryInsertTuple();

    public void replaceTargetDB(IDatabase newDatabase, Scenario scenario) {
        if (newDatabase instanceof MainMemoryDB) {
            scenario.setTarget(newDatabase);
            return;
        }
        MainMemoryVirtualDB virtualDB = (MainMemoryVirtualDB) newDatabase;
        if (logger.isDebugEnabled()) logger.debug("Copying virtual db\n" + newDatabase);
        MainMemoryDB mainMemoryDB = new MainMemoryDB(virtualDB.getDataSource());
        for (String tableName : mainMemoryDB.getTableNames()) {
            MainMemoryTable table = (MainMemoryTable) mainMemoryDB.getTable(tableName);
            emptyTable(table);
            insertAllTuples(table, (MainMemoryVirtualTable) virtualDB.getTable(tableName));
        }
        if (logger.isDebugEnabled()) logger.debug("New db\n" + mainMemoryDB);
        scenario.setTarget(mainMemoryDB);
    }

    private void emptyTable(MainMemoryTable table) {
        table.getDataSource().getInstances().get(0).getChildren().clear();
    }

    private void insertAllTuples(MainMemoryTable table, MainMemoryVirtualTable mainMemoryVirtualTable) {
        ITupleIterator it = mainMemoryVirtualTable.getTupleIterator();
        while (it.hasNext()) {
            Tuple tuple = it.next();
            removeOID(tuple);
            insert.execute(table, tuple, null, null);
        }
    }

    private void removeOID(Tuple tuple) {
        for (Iterator<Cell> it = tuple.getCells().iterator(); it.hasNext();) {
            Cell cell = it.next();
            if (cell.isOID()) {
                it.remove();
            }
        }
    }

}
