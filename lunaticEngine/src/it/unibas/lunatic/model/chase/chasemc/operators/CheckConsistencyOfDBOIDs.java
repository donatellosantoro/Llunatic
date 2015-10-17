package it.unibas.lunatic.model.chase.chasemc.operators;

import java.util.HashMap;
import java.util.Map;
import speedy.model.algebra.operators.ITupleIterator;
import speedy.model.database.IDatabase;
import speedy.model.database.ITable;
import speedy.model.database.Tuple;
import speedy.model.database.TupleOID;

public class CheckConsistencyOfDBOIDs {

    public void checkDatabase(IDatabase database) {
        for (String tableName : database.getTableNames()) {
            ITable table = database.getTable(tableName);
            checkOIDsInTable(table);
        }
    }

    private void checkOIDsInTable(ITable table) {
        Map<TupleOID, Tuple> oidMap = new HashMap<TupleOID, Tuple>();
        ITupleIterator it = table.getTupleIterator();
        while (it.hasNext()) {
            Tuple tuple = it.next();
            TupleOID tupleOID = tuple.getOid();
            if (oidMap.containsKey(tupleOID)) {
                throw new IllegalArgumentException("Multiple tuples with same oid " + tupleOID + "\n" + oidMap.get(tupleOID) + "\n" + tuple);
            }
            oidMap.put(tupleOID, tuple);
        }
        it.close();
    }

}
