package it.unibas.lunatic.model.chase.chasede.operators;

import it.unibas.lunatic.LunaticConfiguration;
import it.unibas.lunatic.OperatorFactory;
import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.model.algebra.operators.BuildAlgebraTreeForCertainAnswerQuery;
import it.unibas.lunatic.model.chase.commons.ChaseStats;
import speedy.model.thread.IBackgroundThread;
import speedy.model.thread.ThreadManager;
import it.unibas.lunatic.model.dependency.Dependency;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import speedy.model.algebra.IAlgebraOperator;
import speedy.model.algebra.operators.ITupleIterator;
import speedy.model.database.IDatabase;
import speedy.model.database.dbms.DBMSDB;
import speedy.model.database.dbms.SQLQueryString;
import speedy.model.database.operators.IDatabaseManager;
import speedy.model.database.operators.dbms.IValueEncoder;
import speedy.model.database.operators.dbms.RunSQLQueryString;
import speedy.persistence.file.operators.ExportCSVFileWithCopy;
import speedy.utility.PrintUtility;
import speedy.utility.SpeedyUtility;

public class ExecuteFinalQueries {

    private final static Logger logger = LoggerFactory.getLogger(ExecuteFinalQueries.class);
    private BuildAlgebraTreeForCertainAnswerQuery treeBuilder = new BuildAlgebraTreeForCertainAnswerQuery();
    private ExportCSVFileWithCopy csvExporter = new ExportCSVFileWithCopy();

    public void executeFinalQueries(IDatabase targetDB, Scenario scenario) {
        long start = new Date().getTime();
        if(!scenario.getQueries().isEmpty() || !scenario.getSQLQueries().isEmpty()){
            OperatorFactory.getInstance().getDatabaseAnalyzer(scenario).analyze(targetDB, scenario.getConfiguration().getMaxNumberOfThreads());
        }
        executeDependencyQueries(targetDB, scenario);
        executeSQLQueries(targetDB, scenario);
        long end = new Date().getTime();
        ChaseStats.getInstance().addStat(ChaseStats.FINAL_QUERY_TIME, end - start);
    }

    private void executeDependencyQueries(IDatabase targetDB, Scenario scenario) {
        int numberOfThreads = scenario.getConfiguration().getMaxNumberOfThreads();
        if (!scenario.getConfiguration().isUseThreadsForQueries()) {
            numberOfThreads = 1;
        }
        Map<String, String> queryResults = Collections.synchronizedMap(new HashMap<String, String>());
        ThreadManager threadManager = new ThreadManager(numberOfThreads);
        for (Dependency query : scenario.getQueries()) {
            ExecuteDependencyQueryThread execThread = new ExecuteDependencyQueryThread(query, queryResults, targetDB, scenario);
            threadManager.startThread(execThread);
        }
        threadManager.waitForActiveThread();
        for (Dependency query : scenario.getQueries()) {
            if (LunaticConfiguration.isPrintResults()) PrintUtility.printInformation(queryResults.get(query.getId()));
        }
    }

    private void executeSQLQueries(IDatabase targetDB, Scenario scenario) {
        if (scenario.getSQLQueries().isEmpty()) {
            return;
        }
        if (!scenario.isDBMS()) {
            throw new IllegalArgumentException("SQLs can be executed on DBMS scenario only");
        }
        executeSQLQueries(targetDB, scenario.getSQLQueries(), scenario.getConfiguration(), scenario.getValueEncoder());
    }

    public void executeSQLQueries(IDatabase targetDB, List<SQLQueryString> sqlQueries, LunaticConfiguration conf, IValueEncoder valueEncoder) {
        if (sqlQueries.isEmpty()) {
            return;
        }
        if (conf.isExportQueryResults()) {
            System.out.println("Exporting query results in " + conf.getExportQueryResultsPath());
        }
        int numberOfThreads = conf.getMaxNumberOfThreads();
        if (!conf.isUseThreadsForQueries()) {
            numberOfThreads = 1;
        }
        Map<String, String> queryResults = Collections.synchronizedMap(new HashMap<String, String>());
        ThreadManager threadManager = new ThreadManager(numberOfThreads);
        for (SQLQueryString sqlQuery : sqlQueries) {
            ExecuteSQLQueryThread execThread = new ExecuteSQLQueryThread(sqlQuery, queryResults, targetDB, conf, valueEncoder);
            threadManager.startThread(execThread);
        }
        threadManager.waitForActiveThread();
        for (SQLQueryString query : sqlQueries) {
            if (LunaticConfiguration.isPrintResults()) PrintUtility.printInformation(queryResults.get(query.getId()));
        }
    }

    class ExecuteDependencyQueryThread implements IBackgroundThread {

        private Dependency query;
        private Map<String, String> queryResults;
        private IDatabase targetDB;
        private Scenario scenario;

        public ExecuteDependencyQueryThread(Dependency query, Map<String, String> queryResults, IDatabase targetDB, Scenario scenario) {
            this.query = query;
            this.queryResults = queryResults;
            this.targetDB = targetDB;
            this.scenario = scenario;
        }

        public void execute() {
            String result;
            try {
                LunaticConfiguration conf = scenario.getConfiguration();
                IAlgebraOperator operator = treeBuilder.generateOperator(query, scenario);
                if (logger.isDebugEnabled()) logger.debug("Operator for query " + query.getId() + ":\n" + operator.toString());
                long start = new Date().getTime();
                long resultSize;
                if (conf.isExportQueryResults()) {
                    resultSize = csvExporter.exportQuery(operator, query.getId(), (DBMSDB) scenario.getSource(), (DBMSDB) scenario.getTarget(), scenario.getValueEncoder(), conf.isExportQueryResultsWithHeader(), conf.getExportQueryResultsPath());
                } else {
                    ITupleIterator it = OperatorFactory.getInstance().getQueryRunner(scenario).run(operator, null, targetDB);
                    resultSize = SpeedyUtility.getTupleIteratorSize(it);
                    it.close();
                }
                long end = new Date().getTime();
                result = "*** Query " + query.getId() + " Time: " + (end - start) + " ms -  Result size: " + resultSize;
            } catch (Exception e) {
                result = "*** Error executing query " + query.getId() + ": " + e.getLocalizedMessage();
            }
            queryResults.put(query.getId(), result);
        }
    }

    class ExecuteSQLQueryThread implements IBackgroundThread {

        private RunSQLQueryString sqlQueryRunner = new RunSQLQueryString();
        private SQLQueryString sqlQuery;
        private Map<String, String> queryResults;
        private IDatabase targetDB;
        private LunaticConfiguration conf;
        private IValueEncoder valueEncoder;

        public ExecuteSQLQueryThread(SQLQueryString sqlQuery, Map<String, String> queryResults, IDatabase targetDB, LunaticConfiguration conf, IValueEncoder valueEncoder) {
            this.sqlQuery = sqlQuery;
            this.queryResults = queryResults;
            this.targetDB = targetDB;
            this.conf = conf;
            this.valueEncoder = valueEncoder;
        }

        public void execute() {
            String result;
            try {
                long start = new Date().getTime();
                long resultSize;
                if (logger.isDebugEnabled()) logger.debug("Executing SQL Query " + sqlQuery.getId() + "\n" + sqlQuery.getQuery());
                if (conf.isExportQueryResults()) {
                    resultSize = csvExporter.exportQuery(sqlQuery.getQuery(), sqlQuery.getId(), (DBMSDB) targetDB, valueEncoder, conf.isExportQueryResultsWithHeader(), conf.getExportQueryResultsPath());
                } else {
                    ITupleIterator it = sqlQueryRunner.runQuery(sqlQuery, targetDB);
                    resultSize = SpeedyUtility.getTupleIteratorSize(it);
                    it.close();
                }
                long end = new Date().getTime();
                result = "*** Query " + sqlQuery.getId() + " Time: " + (end - start) + " ms -  Result size: " + resultSize;
            } catch (Exception e) {
                result = "*** Error executing query " + sqlQuery.getId() + ": " + e.getLocalizedMessage();
            }
            queryResults.put(sqlQuery.getId(), result);
        }

    }

}
