package it.unibas.lunatic.model.chase.chasede.operators;

import java.util.HashSet;
import java.util.Set;
import speedy.SpeedyConstants;
import speedy.model.algebra.operators.ITupleIterator;
import speedy.model.database.Cell;
import speedy.model.database.IDatabase;
import speedy.model.database.ITable;
import speedy.model.database.Tuple;
import speedy.model.database.dbms.DBMSTable;

public class AnalyzeDatabase {

    public long countNulls(IDatabase database) {
        Set<String> nulls = new HashSet<String>();
        for (String tableName : database.getTableNames()) {
            ITable table = database.getTable(tableName);
            ITupleIterator it = table.getTupleIterator();
            while (it.hasNext()) {
                Tuple tuple = it.next();
                for (Cell cell : tuple.getCells()) {
                    String value = cell.getValue().toString();
                    if (value.startsWith(SpeedyConstants.SKOLEM_PREFIX)) {
                        nulls.add(value);
                    }
                }
            }
            it.close();
        }
        return nulls.size();
    }

    public long getTableSize(ITable table) {
        return table.getNumberOfDistinctTuples();
    }

}
