package it.unibas.lunatic.model.chase.chasede.operators;

import java.util.HashSet;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import speedy.model.algebra.operators.ITupleIterator;
import speedy.model.database.Cell;
import speedy.model.database.IDatabase;
import speedy.model.database.ITable;
import speedy.model.database.Tuple;
import speedy.utility.SpeedyUtility;

public class ComputeDatabaseSize {

    private long THRESHOLD = 2000000;
    private final static Logger logger = LoggerFactory.getLogger(ComputeDatabaseSize.class);

    public Integer countNulls(IDatabase database) {
        Set<String> nulls = new HashSet<String>();
        for (String tableName : database.getTableNames()) {
            ITable table = database.getTable(tableName);
            ITupleIterator it = table.getTupleIterator();
            while (it.hasNext()) {
                Tuple tuple = it.next();
                for (Cell cell : tuple.getCells()) {
                    String value = cell.getValue().toString();
                    if (SpeedyUtility.isSkolem(value)) {
                        nulls.add(value);
                    }
                    if (nulls.size() > THRESHOLD) {
                        logger.error("More than " + THRESHOLD + " nulls");
                        return null;
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
