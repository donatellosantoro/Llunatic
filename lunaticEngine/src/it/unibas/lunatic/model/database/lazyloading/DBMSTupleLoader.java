package it.unibas.lunatic.model.database.lazyloading;

import it.unibas.lunatic.persistence.relational.DBMSUtility;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import speedy.exceptions.DBMSException;
import speedy.model.database.Tuple;
import speedy.model.database.TupleOID;
import speedy.persistence.relational.AccessConfiguration;
import speedy.persistence.relational.QueryManager;

public class DBMSTupleLoader implements ITupleLoader {

    private final String tableName;
    private final AccessConfiguration accessConfiguration;
    private final String virtualTableName;
    private final TupleOID oid;

    public DBMSTupleLoader(String tableName, String virtualTableName, TupleOID oid, AccessConfiguration accessConfiguration) {
        this.oid = oid;
        this.tableName = tableName;
        this.virtualTableName = virtualTableName;
        this.accessConfiguration = accessConfiguration;
    }

    @Override
    public TupleOID getOid() {
        return oid;
    }

    public Tuple loadTuple() {
        Tuple tuple = null;
        try {
            Connection connection = QueryManager.getConnection(accessConfiguration);
            ResultSet tupleResultSet = DBMSUtility.getTupleResultSet(virtualTableName, oid, accessConfiguration, connection);
            tupleResultSet.next();
            tuple = DBMSUtility.createTuple(tupleResultSet, tableName);
            tupleResultSet.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
            if (tuple == null) {
                throw new DBMSException("Tuple lazy loading failed: " + e.getMessage());
            }
        }
        return tuple;
    }
}
