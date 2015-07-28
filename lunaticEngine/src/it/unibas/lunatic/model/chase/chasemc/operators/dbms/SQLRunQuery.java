package it.unibas.lunatic.model.chase.chasemc.operators.dbms;

import it.unibas.lunatic.LunaticConstants;
import it.unibas.lunatic.model.algebra.IAlgebraOperator;
import it.unibas.lunatic.model.algebra.operators.ITupleIterator;
import it.unibas.lunatic.model.algebra.sql.AlgebraTreeToSQL;
import it.unibas.lunatic.model.chase.chasemc.operators.IRunQuery;
import it.unibas.lunatic.model.database.Cell;
import it.unibas.lunatic.model.database.IDatabase;
import it.unibas.lunatic.model.database.Tuple;
import it.unibas.lunatic.model.database.dbms.DBMSDB;
import it.unibas.lunatic.model.database.dbms.DBMSTupleIterator;
import it.unibas.lunatic.model.database.dbms.DBMSVirtualDB;
import it.unibas.lunatic.persistence.relational.AccessConfiguration;
import it.unibas.lunatic.persistence.relational.QueryManager;
import it.unibas.lunatic.utility.LunaticUtility;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SQLRunQuery implements IRunQuery {

    private static Logger logger = LoggerFactory.getLogger(SQLRunQuery.class);

    private AlgebraTreeToSQL translator = new AlgebraTreeToSQL();

    public ITupleIterator run(IAlgebraOperator query, IDatabase source, IDatabase target) {
        if (LunaticConstants.DBMS_DEBUG) {
            return runDebug(query, source, target);
        } else {
            return runStandard(query, source, target);
        }
    }

    private ITupleIterator runStandard(IAlgebraOperator query, IDatabase source, IDatabase target) {
        AccessConfiguration accessConfiguration;
        if (target instanceof DBMSDB) {
            accessConfiguration = ((DBMSDB) target).getAccessConfiguration();
        } else if (target instanceof DBMSVirtualDB) {
            accessConfiguration = ((DBMSVirtualDB) target).getAccessConfiguration();
        } else {
            throw new IllegalArgumentException("Unable to execute SQL on main memory db.");
        }
        if (logger.isDebugEnabled()) logger.debug("Executing query \n" + query);
        String sqlCode = translator.treeToSQL(query, source, target, "");
        if (logger.isDebugEnabled()) logger.debug("Executing sql \n" + sqlCode);
        ResultSet resultSet = QueryManager.executeQuery(sqlCode, accessConfiguration);
        return new DBMSTupleIterator(resultSet);
    }
//    

    private ITupleIterator runDebug(IAlgebraOperator query, IDatabase source, IDatabase target) {
        //DEBUG MODE
        AccessConfiguration accessConfiguration;
        if (target instanceof DBMSDB) {
            accessConfiguration = ((DBMSDB) target).getAccessConfiguration();
        } else if (target instanceof DBMSVirtualDB) {
            accessConfiguration = ((DBMSVirtualDB) target).getAccessConfiguration();
        } else {
            throw new IllegalArgumentException("Unable to execute SQL on main memory db.");
        }
        if (logger.isDebugEnabled()) logger.debug("Executing query \n" + query);

        ITupleIterator mainMemoryTupleIterator = query.execute(source, target);
        String sqlCode = translator.treeToSQL(query, source, target, "");
        ResultSet resultSet = QueryManager.executeQuery(sqlCode, accessConfiguration);
        ITupleIterator dbmsTupleIterator = new DBMSTupleIterator(resultSet);
        compare(mainMemoryTupleIterator, dbmsTupleIterator, query, sqlCode);
        mainMemoryTupleIterator.close();
        dbmsTupleIterator.reset();
        return dbmsTupleIterator;
    }

    private void compare(ITupleIterator mainMemoryTupleIterator, ITupleIterator dbmsTupleIterator, IAlgebraOperator query, String sqlCode) {
        List<String> mainMemoryResult = materializeResult(mainMemoryTupleIterator);
        List<String> dbmsResult = materializeResult(dbmsTupleIterator);
        Collections.sort(mainMemoryResult);
        Collections.sort(dbmsResult);
        if (mainMemoryResult.size() != dbmsResult.size() || !mainMemoryResult.equals(dbmsResult)) {
            logger.error("\n\n\n#############################\n");
            logger.error("\n#### Query:\n" + query);
            logger.error("\n#### SQLCode:\n" + sqlCode);
            logger.error("\n#### MainMemoryResult:\n" + LunaticUtility.printCollection(mainMemoryResult));
            logger.error("\n#### DbmsResult:\n" + LunaticUtility.printCollection(dbmsResult));
            logger.error("\n#############################\n\n\n");
//            throw new IllegalArgumentException("WRONG EXECUTION");
        }
    }

    private List<String> materializeResult(ITupleIterator iterator) {
        List<String> result = new ArrayList<String>();
        while (iterator.hasNext()) {
            result.add(createString(iterator.next()));
        }
        return result;
    }

    private String createString(Tuple next) {
        String result = "";
        for (Cell cell : next.getCells()) {
            if (!cell.isOID()) {
                result += cell.getAttribute() + ":" + cell.getValue() + " | ";
            }
        }
        return result.toLowerCase();
    }

    public boolean isUseTrigger() {
        return true;
    }
}
