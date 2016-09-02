package it.unibas.lunatic.model.chase.chasede.operators.mainmemory;

import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.model.chase.chasede.operators.IRemoveDuplicates;
import it.unibas.lunatic.model.chase.commons.ChaseStats;
import speedy.model.database.IDatabase;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import speedy.model.database.mainmemory.MainMemoryDB;
import speedy.model.database.mainmemory.MainMemoryVirtualDB;
import speedy.model.database.mainmemory.datasource.DataSource;
import speedy.model.database.mainmemory.datasource.INode;
import speedy.utility.comparator.StringComparator;

@SuppressWarnings("unchecked")
public class MainMemoryRemoveDuplicates implements IRemoveDuplicates {

    public void removeDuplicatesModuloOID(IDatabase database, Scenario scenario) {
        long start = new Date().getTime();
        DataSource dataSource;
        if (database instanceof MainMemoryDB) {
            dataSource = ((MainMemoryDB) database).getDataSource();
        }else if (database instanceof MainMemoryVirtualDB) {
            dataSource = ((MainMemoryVirtualDB) database).getDataSource();
        }else {
            throw new IllegalArgumentException();
        }
        INode instance = dataSource.getInstances().get(0);
        for (String table : database.getTableNames()) {
            INode tableRoot = instance.getChild(table);
            if (tableRoot == null) {
                continue;
            }
            removeDuplicatesModuloOID(tableRoot.getChildren());
        }
        long end = new Date().getTime();
        ChaseStats.getInstance().addStat(ChaseStats.REMOVE_DUPLICATE_TIME, end - start);
    }

    private void removeDuplicatesModuloOID(List<INode> result) {
        if (result.isEmpty()) {
            return;
        }
        Collections.sort(result, new StringComparator());
        Iterator<INode> tupleIterator = result.iterator();
        String prevValues = tupleIterator.next().toString();
        while (tupleIterator.hasNext()) {
            INode currentTuple = tupleIterator.next();
            String currentValues = currentTuple.toStringNoOID();
            if (prevValues.equals(currentValues)) {
                tupleIterator.remove();
            } else {
                prevValues = currentValues;
            }
        }
    }
}
