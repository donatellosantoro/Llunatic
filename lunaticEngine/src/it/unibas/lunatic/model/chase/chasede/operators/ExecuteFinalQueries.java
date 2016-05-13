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
import speedy.model.database.IDatabase;
import speedy.model.database.dbms.SQLQueryString;
import speedy.model.database.operators.IRunQuery;
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
            IAlgebraOperator operator = treeBuilder.generateOperator(query, scenario);
            if (logger.isDebugEnabled()) logger.debug("Operator for query " + query.getId() + ":\n" + operator.toString());
            long start = new Date().getTime();
            ITupleIterator it = queryRunner.run(operator, null, targetDB);
            long resultSize = SpeedyUtility.getTupleIteratorSize(it);
            it.close();
            long end = new Date().getTime();
            if (LunaticConfiguration.isPrintSteps()) PrintUtility.printInformation("*** Query " + query.getId() + " Time: " + (end - start) + " ms -  Result size: " + resultSize);
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
            if (LunaticConfiguration.isPrintSteps()) PrintUtility.printInformation("*** Query " + sqlQuery.getId() + " Time: " + (end - start) + " ms -  Result size: " + resultSize);
        }
    }

}
