package it.unibas.lunatic.model.chase.chasede.operators;

import it.unibas.lunatic.LunaticConfiguration;
import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.model.algebra.operators.BuildAlgebraTreeForCertainAnswerQuery;
import it.unibas.lunatic.model.chase.commons.ChaseStats;
import it.unibas.lunatic.model.dependency.Dependency;
import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import speedy.model.algebra.IAlgebraOperator;
import speedy.model.algebra.operators.ITupleIterator;
import speedy.model.database.Cell;
import speedy.model.database.IDatabase;
import speedy.model.database.Tuple;
import speedy.model.database.dbms.SQLQueryString;
import speedy.model.database.operators.IRunQuery;
import speedy.model.database.operators.dbms.IValueEncoder;
import speedy.model.database.operators.dbms.RunSQLQueryString;
import speedy.utility.PrintUtility;
import speedy.utility.SpeedyUtility;

public class ExecuteFinalQueries {

    private final static Logger logger = LoggerFactory.getLogger(ExecuteFinalQueries.class);
    private BuildAlgebraTreeForCertainAnswerQuery treeBuilder = new BuildAlgebraTreeForCertainAnswerQuery();
    private IRunQuery queryRunner;

    public ExecuteFinalQueries(IRunQuery queryRunner) {
        this.queryRunner = queryRunner;
    }

    public void executeFinalQueries(IDatabase targetDB, Scenario scenario) {
        long start = new Date().getTime();
        executeDependencyQueries(targetDB, scenario);
        executeSQLQueries(targetDB, scenario);
        long end = new Date().getTime();
        ChaseStats.getInstance().addStat(ChaseStats.FINAL_QUERY_TIME, end - start);
    }

    private void executeDependencyQueries(IDatabase targetDB, Scenario scenario) {
        for (Dependency query : scenario.getQueries()) {
            try {
                IAlgebraOperator operator = treeBuilder.generateOperator(query, scenario);
                if (logger.isDebugEnabled()) logger.debug("Operator for query " + query.getId() + ":\n" + operator.toString());
                long start = new Date().getTime();
                ITupleIterator it = queryRunner.run(operator, null, targetDB);
                long resultSize = getQueryResult(it, query, scenario);
                it.close();
                long end = new Date().getTime();
                if (LunaticConfiguration.isPrintResults()) PrintUtility.printInformation("*** Query " + query.getId() + " Time: " + (end - start) + " ms -  Result size: " + resultSize);
            } catch (Exception e) {
                if (LunaticConfiguration.isPrintResults()) PrintUtility.printInformation("*** Error executing query " + query.getId() + ": " + e.getLocalizedMessage());
            }
        }
    }

    private void executeSQLQueries(IDatabase targetDB, Scenario scenario) {
        if (scenario.getSQLQueries().isEmpty()) {
            return;
        }
        if (!scenario.isDBMS()) {
            throw new IllegalArgumentException("SQLs can be executed on DBMS scenario only");
        }
        RunSQLQueryString sqlQueryRunner = new RunSQLQueryString();
        for (SQLQueryString sqlQuery : scenario.getSQLQueries()) {
            long start = new Date().getTime();
            ITupleIterator it = sqlQueryRunner.runQuery(sqlQuery, targetDB);
            long resultSize = SpeedyUtility.getTupleIteratorSize(it);
            it.close();
            long end = new Date().getTime();
            if (LunaticConfiguration.isPrintResults()) PrintUtility.printInformation("*** Query " + sqlQuery.getId() + " Time: " + (end - start) + " ms -  Result size: " + resultSize);
        }
    }

    private long getQueryResult(ITupleIterator it, Dependency query, Scenario scenario) {
//        if (query.getId().equalsIgnoreCase("q3")) {
//            return printQueryResult(it, scenario);
//        }
        return SpeedyUtility.getTupleIteratorSize(it);
    }

    private long printQueryResult(ITupleIterator it, Scenario scenario) { //DEBUG ONLY
        if (scenario.getValueEncoder() != null) scenario.getValueEncoder().prepareForDecoding();
        StringBuilder result = new StringBuilder();
        long counter = 0;
        while (it.hasNext()) {
            counter++;
            Tuple tuple = it.next();
            result.append(printTuple(tuple, scenario)).append("\n");
        }
        if (logger.isDebugEnabled()) logger.debug(result.toString());
        if (scenario.getValueEncoder() != null) scenario.getValueEncoder().closeDecoding();
        return counter;
    }

    private String printTuple(Tuple tuple, Scenario scenario) {
        boolean useDictionaryEncoding = scenario.getConfiguration().isUseDictionaryEncoding();
        StringBuilder sb = new StringBuilder();
        for (Cell cell : tuple.getCells()) {
            if (cell.isOID()) {
                continue;
            }
            if (useDictionaryEncoding) {
                sb.append(scenario.getValueEncoder().decode(cell.getValue().toString()));
            } else {
                sb.append(cell.getValue());
            }
            sb.append(", ");
        }
        SpeedyUtility.removeChars(", ".length(), sb);
        if (useDictionaryEncoding) {
            sb.append("\t").append(tuple);
        }
        return sb.toString();
    }

}
