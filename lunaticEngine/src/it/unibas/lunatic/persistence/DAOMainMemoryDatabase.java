package it.unibas.lunatic.persistence;

import it.unibas.lunatic.exceptions.DAOException;
import it.unibas.lunatic.parser.operators.ParseDatabase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import speedy.model.database.IDatabase;
import speedy.model.database.mainmemory.MainMemoryDB;
import speedy.model.database.mainmemory.datasource.DataSource;
import speedy.persistence.PersistenceUtility;
import speedy.persistence.xml.DAOXsd;

public class DAOMainMemoryDatabase {

    private DAOXsd daoXSD = new DAOXsd();
    private static Logger logger = LoggerFactory.getLogger(DAOMainMemoryDatabase.class);

    public IDatabase loadXMLScenario(String schemaFile, String instanceFile) throws DAOException {
        logger.debug("Loading main-memory database. Schema " + schemaFile + ". Instance " + instanceFile);
        DataSource dataSource = daoXSD.loadSchema(schemaFile);
        if (instanceFile != null) {
            daoXSD.loadInstance(dataSource, instanceFile);
        }else {
            PersistenceUtility.createEmptyTables(dataSource);
        }
        return new MainMemoryDB(dataSource);
    }

    public IDatabase loadPlainScenario(String text) throws DAOException {
        logger.debug("Loading main-memory database. Plain text: " + text);
        ParseDatabase generator = new ParseDatabase();
        IDatabase database;
        try {
            database = generator.generateDatabase(text);
        return database;
        } catch (Exception ex) {
            throw new DAOException(ex);
        }
    }
}
