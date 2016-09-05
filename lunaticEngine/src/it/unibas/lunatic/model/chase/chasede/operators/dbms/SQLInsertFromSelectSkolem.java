package it.unibas.lunatic.model.chase.chasede.operators.dbms;

import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.model.chase.chasede.operators.IInsertFromSelectNaive;
import it.unibas.lunatic.model.chase.chasede.operators.IRemoveDuplicates;
import it.unibas.lunatic.model.dependency.Dependency;
import it.unibas.lunatic.model.dependency.IFormulaAtom;
import it.unibas.lunatic.model.dependency.RelationalAtom;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import speedy.model.algebra.IAlgebraOperator;
import speedy.model.database.IDatabase;
import speedy.model.database.dbms.DBMSDB;
import speedy.model.database.dbms.DBMSTable;

public class SQLInsertFromSelectSkolem implements IInsertFromSelectNaive {

    private final static Logger logger = LoggerFactory.getLogger(SQLInsertFromSelectSkolem.class);

    private IRemoveDuplicates duplicateRemover = new SQLRemoveDuplicates();

    @Override
    public boolean execute(Dependency dependency, IAlgebraOperator sourceQuery, IDatabase source, IDatabase target, Scenario scenario) {
        List<String> affectedTables = findAffectedTables(dependency);
        DBMSDB dbmsTarget = (DBMSDB) target;
        long size = getSize(affectedTables, dbmsTarget);
        IInsertFromSelectNaive naiveInsert = new SQLInsertFromSelectNaive();//Operator with state
        boolean insertTuples = naiveInsert.execute(dependency, sourceQuery, source, target, scenario);
        if (logger.isDebugEnabled()) logger.debug("Naive insert tuple: " + insertTuples);
        if (!scenario.getConfiguration().isPreventInsertDuplicateTuples()) {
            duplicateRemover.removeDuplicatesModuloOID(target, scenario);
        }
        if (logger.isTraceEnabled()) logger.trace("TargetDB without duplicates:\n" + target.printInstances());
        long newSize = getSize(affectedTables, dbmsTarget);
        if (logger.isDebugEnabled()) logger.debug("Initial size: " + size);
        if (logger.isDebugEnabled()) logger.debug("Size after insert: " + newSize);
        return newSize > size;
    }

    private List<String> findAffectedTables(Dependency dependency) {
        List<String> tables = new ArrayList<String>();
        for (IFormulaAtom atom : dependency.getConclusion().getAtoms()) {
            RelationalAtom relationalAtom = (RelationalAtom) atom;
            String tableToInsert = relationalAtom.getTableName();
            tables.add(tableToInsert);
        }
        return tables;
    }

    private long getSize(List<String> affectedTables, DBMSDB dbmsTarget) {
        long size = 0;
        for (String affectedTable : affectedTables) {
            size += ((DBMSTable) dbmsTarget.getTable(affectedTable)).getSizeNoCache();
        }
        return size;
    }
}
