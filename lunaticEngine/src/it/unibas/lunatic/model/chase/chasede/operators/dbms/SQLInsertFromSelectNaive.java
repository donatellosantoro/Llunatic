package it.unibas.lunatic.model.chase.chasede.operators.dbms;

import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.model.algebra.sql.FormulaAttributeToSQL;
import it.unibas.lunatic.model.chase.chasede.operators.IInsertFromSelectNaive;
import it.unibas.lunatic.model.dependency.Dependency;
import it.unibas.lunatic.model.dependency.FormulaAttribute;
import it.unibas.lunatic.model.dependency.FormulaVariable;
import it.unibas.lunatic.model.dependency.IFormulaAtom;
import it.unibas.lunatic.model.dependency.RelationalAtom;
import it.unibas.lunatic.model.generators.SkolemFunctionGenerator;
import it.unibas.lunatic.persistence.relational.LunaticDBMSUtility;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import speedy.SpeedyConstants;
import speedy.exceptions.DBMSException;
import speedy.model.algebra.IAlgebraOperator;
import speedy.model.algebra.operators.sql.AlgebraTreeToSQL;
import speedy.model.database.IDatabase;
import speedy.model.database.dbms.DBMSDB;
import speedy.persistence.relational.QueryManager;
import speedy.utility.DBMSUtility;
import speedy.utility.SpeedyUtility;

public class SQLInsertFromSelectNaive implements IInsertFromSelectNaive {

    private final static Logger logger = LoggerFactory.getLogger(SQLInsertFromSelectNaive.class);
    private AlgebraTreeToSQL queryBuilder = new AlgebraTreeToSQL();
    private FormulaAttributeToSQL attributeGenerator = new FormulaAttributeToSQL();

    public boolean execute(Dependency dependency, IAlgebraOperator sourceQuery, IDatabase source, IDatabase target, Scenario scenario) {
        try {
//        LunaticDBMSUtility.createFunctionsForNumericalSkolem(((DBMSDB) target).getAccessConfiguration());
            String selectQuery = queryBuilder.treeToSQL(sourceQuery, source, target, SpeedyConstants.INDENT + SpeedyConstants.INDENT);
            String insertQuery = generateInsertScript(dependency, selectQuery, (DBMSDB) target, scenario);
            return QueryManager.executeInsertOrDelete(insertQuery, ((DBMSDB) target).getAccessConfiguration());
        } catch (DBMSException ex) {
            if (ex.getMessage().contains("ERROR: function bigint_skolem(text) does not exist")
                    || ex.getMessage().contains("ERROR: function double_skolem(text) does not exist")) {
                logger.warn("Some functions are missing in the current C3p0 thread. Retrying...");
                return execute(dependency, sourceQuery, source, target, scenario);
            }
            throw ex;
        }
    }

    private String generateInsertScript(Dependency dependency, String selectQuery, DBMSDB target, Scenario scenario) {
        StringBuilder result = new StringBuilder();
        String targetSchemaName = DBMSUtility.getSchemaNameAndDot(target.getAccessConfiguration());
        for (IFormulaAtom atom : dependency.getConclusion().getAtoms()) {
            RelationalAtom relationalAtom = (RelationalAtom) atom;
            String tableToInsert = relationalAtom.getTableName();
            result.append("INSERT INTO ").append(targetSchemaName).append(tableToInsert).append("(");
            for (FormulaAttribute formulaAttribute : relationalAtom.getAttributes()) {
                if (formulaAttribute.getAttributeName().equalsIgnoreCase(SpeedyConstants.OID)) {
                    continue;
                }
                result.append(formulaAttribute.getAttributeName()).append(", ");
            }
            SpeedyUtility.removeChars(", ".length(), result);
            result.append(")\n");
            result.append(generateSelectForInsert(relationalAtom, dependency, selectQuery, scenario));
            result.append(";\n\n");
        }
        return result.toString();
    }

    private String generateSelectForInsert(RelationalAtom relationalAtom, Dependency stTgd, String selectQuery, Scenario scenario) {
        StringBuilder result = new StringBuilder();
        result.append(SpeedyConstants.INDENT).append("SELECT DISTINCT ");
        Map<FormulaVariable, SkolemFunctionGenerator> skolems = new HashMap<FormulaVariable, SkolemFunctionGenerator>();
        for (FormulaAttribute formulaAttribute : relationalAtom.getAttributes()) {
            result.append(attributeGenerator.generateSQL(formulaAttribute, stTgd, skolems, scenario));
            result.append(", ");
        }
        SpeedyUtility.removeChars(", ".length(), result);
        result.append("\n").append(SpeedyConstants.INDENT);
        result.append(" FROM (");
        result.append("\n");
        result.append(selectQuery).append(") AS ").append("Q").append(LunaticDBMSUtility.cleanRelationName(stTgd.getId()));
        return result.toString();
    }
}
