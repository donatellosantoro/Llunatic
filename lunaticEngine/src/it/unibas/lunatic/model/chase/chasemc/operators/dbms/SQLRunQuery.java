package it.unibas.lunatic.model.chase.chasemc.operators.dbms;

import it.unibas.lunatic.model.algebra.IAlgebraOperator;
import it.unibas.lunatic.model.algebra.operators.ITupleIterator;
import it.unibas.lunatic.model.algebra.sql.AlgebraTreeToSQL;
import it.unibas.lunatic.model.chase.chasemc.operators.IRunQuery;
import it.unibas.lunatic.model.database.IDatabase;
import it.unibas.lunatic.model.database.dbms.DBMSDB;
import it.unibas.lunatic.model.database.dbms.DBMSTupleIterator;
import it.unibas.lunatic.model.database.dbms.DBMSVirtualDB;
import it.unibas.lunatic.persistence.relational.AccessConfiguration;
import it.unibas.lunatic.persistence.relational.QueryManager;
import java.sql.ResultSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SQLRunQuery implements IRunQuery {

    private static Logger logger = LoggerFactory.getLogger(SQLRunQuery.class);

    private AlgebraTreeToSQL translator = new AlgebraTreeToSQL();

    public ITupleIterator run(IAlgebraOperator query, IDatabase source, IDatabase target) {
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

    public boolean isUseTrigger() {
        return true;
    }
//    
//    public ITupleIterator run(IAlgebraOperator query, IDatabase source, IDatabase target) {
//        //DEBUG MODE
//        AccessConfiguration accessConfiguration;
//        if (target instanceof DBMSDB) {
//            accessConfiguration = ((DBMSDB) target).getAccessConfiguration();
//        } else if (target instanceof DBMSVirtualDB) {
//            accessConfiguration = ((DBMSVirtualDB) target).getAccessConfiguration();
//        } else {
//            throw new IllegalArgumentException("Unable to execute SQL on main memory db.");
//        }
//        if (logger.isDebugEnabled()) logger.debug("Executing query \n" + query);
//
//        ITupleIterator mainMemoryTupleIterator = query.execute(source, target);
//        String sqlCode = translator.treeToSQL(query, source, target, "");
//        ResultSet resultSet = QueryManager.executeQuery(sqlCode, accessConfiguration);
//        ITupleIterator dbmsTupleIterator = new DBMSTupleIterator(resultSet);
//        compare(mainMemoryTupleIterator, dbmsTupleIterator, query, sqlCode);
//        mainMemoryTupleIterator.close();
//        dbmsTupleIterator.reset();
//        return dbmsTupleIterator;
//    }
//
//    private void compare(ITupleIterator mainMemoryTupleIterator, ITupleIterator dbmsTupleIterator, IAlgebraOperator query, String sqlCode) {
//        List<String> mainMemoryResult = materializeResult(mainMemoryTupleIterator);
//        List<String> dbmsResult = materializeResult(dbmsTupleIterator);
//        Collections.sort(mainMemoryResult);
//        Collections.sort(dbmsResult);
//        if (mainMemoryResult.size() != dbmsResult.size() || !mainMemoryResult.equals(dbmsResult)) {
//            logger.info("\n\n\n#############################\n");
//            logger.info.println("\n#### Query:\n" + query);
//            logger.info.println("\n#### SQLCode:\n" + sqlCode);
//            logger.info.println("\n#### MainMemoryResult:\n" + LunaticUtility.printCollection(mainMemoryResult));
//            logger.info.println("\n#### DbmsResult:\n" + LunaticUtility.printCollection(dbmsResult));
//            logger.info.println("\n#############################\n\n\n");
//            throw new IllegalArgumentException("WRONG EXECUTION");
//        }
//    }
//
//    private List<String> materializeResult(ITupleIterator iterator) {
//        List<String> result = new ArrayList<String>();
//        while (iterator.hasNext()) {
//            result.add(createString(iterator.next()));
//        }
//        return result;
//    }
//
//    private String createString(Tuple next) {
//        String result = "";
//        for (Cell cell : next.getCells()) {
//            if(!cell.isOID()){
//                result += cell.getAttribute() + ":" + cell.getValue() + " | ";
//            }
//        }
//        return result.toLowerCase();
//    }
}
