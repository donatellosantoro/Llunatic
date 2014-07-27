package it.unibas.lunatic.persistence.relational;

import it.unibas.lunatic.exceptions.DAOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public interface IConnectionFactory {
            
    public Connection getConnection(AccessConfiguration accessConfiguration) throws DAOException;
    
    public void close(Connection connection);
    
    public void close(Statement statement);

    public void close(ResultSet resultSet);

}