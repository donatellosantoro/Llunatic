package it.unibas.lunatic.model.chase.chasede.operators.mainmemory;

import it.unibas.lunatic.model.chase.chasede.operators.IRemoveDuplicates;
import speedy.model.database.IDatabase;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import speedy.model.algebra.operators.StringComparator;
import speedy.model.database.mainmemory.MainMemoryDB;
import speedy.model.database.mainmemory.datasource.INode;

@SuppressWarnings("unchecked")
public class MainMemoryRemoveDuplicates implements IRemoveDuplicates {

    public void removeDuplicatesModuloOID(IDatabase database) {
        MainMemoryDB mainMemoryDB = (MainMemoryDB) database;
        INode instance = mainMemoryDB.getDataSource().getInstances().get(0);
        for (String table : mainMemoryDB.getTableNames()) {
            INode tableRoot = instance.getChild(table);
            if(tableRoot==null){
                continue;
            }
            removeDuplicatesModuloOID(tableRoot.getChildren());
        }
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
