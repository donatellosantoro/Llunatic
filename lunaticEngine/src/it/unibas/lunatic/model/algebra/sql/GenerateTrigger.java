package it.unibas.lunatic.model.algebra.sql;

import it.unibas.lunatic.LunaticConstants;
import it.unibas.lunatic.utility.LunaticUtility;
import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.model.dependency.*;
import it.unibas.lunatic.model.generators.IValueGenerator;
import it.unibas.lunatic.model.generators.SkolemFunctionGenerator;
import it.unibas.lunatic.persistence.relational.LunaticDBMSUtility;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import speedy.SpeedyConstants;
import speedy.model.database.AttributeRef;
import speedy.model.database.dbms.DBMSDB;

public class GenerateTrigger {

    private static Logger logger = LoggerFactory.getLogger(GenerateTrigger.class);

    public String generateScript(Scenario scenario) {
        StringBuilder result = new StringBuilder();
        if (scenario.getEGDs().isEmpty() && scenario.getExtEGDs().isEmpty()) {
            return "";
        }
        result.append("----- Generating trigger for skolem occurrences -----\n");
        result.append("CREATE TABLE ").append(getSchema(scenario)).append(".").append(LunaticConstants.SKOLEM_OCC_TABLE).append("(").append("\n");
        result.append(SpeedyConstants.INDENT).append("skolem text,").append("\n");
        result.append(SpeedyConstants.INDENT).append("table_name text,").append("\n");
        result.append(SpeedyConstants.INDENT).append("tuple_oid oid,").append("\n");
        result.append(SpeedyConstants.INDENT).append("attribute text").append("\n");
        result.append(") WITH OIDS;").append("\n\n");
//        result.append("DELETE FROM ").append(getSchema(scenario)).append(".").append(LunaticConstants.SKOLEM_OCC_TABLE).append(";\n\n");
        result.append(generateTrigger(scenario));
        if (logger.isDebugEnabled()) logger.debug("Trigger script:\n" + result.toString());
        return result.toString();
    }

    private String generateTrigger(Scenario scenario) {
        DBMSDB target = ((DBMSDB) scenario.getTarget());
        String targetSchema = LunaticDBMSUtility.getSchemaWithSuffix(target.getAccessConfiguration(), scenario);
        List<AttributeRef> attributeRefsWithSkolem = findAttributeRefsWithSkolem(scenario.getSTTgds());
        StringBuilder result = new StringBuilder();
        Map<String, List<String>> tableWithSkolem = groupAttributeByTable(attributeRefsWithSkolem);
        for (String tableName : tableWithSkolem.keySet()) {
            List<String> attributes = tableWithSkolem.get(tableName);
            result.append(generateTriggerForTable(tableName, attributes, scenario));
            result.append(addTriggerToTable(tableName, attributes, targetSchema));
        }
        return result.toString();
    }

    private String generateTriggerForTable(String tableName, List<String> attributes, Scenario scenario) {
        StringBuilder result = new StringBuilder();
        result.append("CREATE OR REPLACE FUNCTION update_skolem_occurrences_");
        result.append(tableName).append("() RETURNS TRIGGER AS $$").append("\n");
        result.append(SpeedyConstants.INDENT).append("BEGIN").append("\n");
        String indent = SpeedyConstants.INDENT + SpeedyConstants.INDENT;
        String longIndent = indent + SpeedyConstants.INDENT;
        result.append(indent).append("IF (TG_OP = 'DELETE') THEN").append("\n");
        result.append(longIndent).append("DELETE FROM ").append(getSchema(scenario));
        result.append(".").append(LunaticConstants.SKOLEM_OCC_TABLE).append(" WHERE table_name = TG_TABLE_NAME AND tuple_oid = OLD.oid;").append("\n");
        result.append(longIndent).append("RETURN OLD;").append("\n");
        result.append(indent).append("ELSIF (TG_OP = 'UPDATE') THEN").append("\n");
        result.append(createUpdatePart(attributes, longIndent, scenario));
        result.append(longIndent).append("RETURN NEW;").append("\n");
        result.append(indent).append("ELSIF (TG_OP = 'INSERT') THEN").append("\n");
        result.append(createInsertPart(attributes, longIndent, scenario));
        result.append(longIndent).append("RETURN NEW;").append("\n");
        result.append(indent).append("END IF;").append("\n");
        result.append(SpeedyConstants.INDENT).append("END;").append("\n");
        result.append("$$ LANGUAGE plpgsql;").append("\n\n");
        return result.toString();
    }

    private String createUpdatePart(List<String> attributes, String indent, Scenario scenario) {
        StringBuilder result = new StringBuilder();
        String longIndent = indent + SpeedyConstants.INDENT;
        for (String attribute : attributes) {
            result.append(indent).append("IF (OLD.").append(attribute);
            result.append(" != NEW.").append(attribute).append(") ");
            result.append("THEN").append("\n");
            result.append(longIndent).append("IF (OLD.").append(attribute);
            result.append(" LIKE '").append(SpeedyConstants.SKOLEM_PREFIX).append("%') THEN").append("\n");
            result.append(longIndent).append(SpeedyConstants.INDENT);
            result.append("DELETE FROM ").append(getSchema(scenario));
            result.append(".").append(LunaticConstants.SKOLEM_OCC_TABLE).append(" WHERE table_name = TG_TABLE_NAME ");
            result.append("AND tuple_oid = OLD.oid AND attribute = '").append(attribute).append("';").append("\n");
            result.append(longIndent).append("END IF;").append("\n");

            result.append(longIndent).append("IF (NEW.").append(attribute);
            result.append(" LIKE '").append(SpeedyConstants.SKOLEM_PREFIX).append("%') THEN").append("\n");
            result.append(longIndent).append(SpeedyConstants.INDENT);
            result.append("INSERT INTO ").append(getSchema(scenario));
            result.append(".").append(LunaticConstants.SKOLEM_OCC_TABLE).append(" VALUES(NEW.").append(attribute).append(", TG_TABLE_NAME, NEW.oid, ");
            result.append("'").append(attribute).append("');").append("\n");
            result.append(longIndent).append("END IF;").append("\n");

            result.append(indent).append("END IF;").append("\n");
        }
        return result.toString();
    }

    private String createInsertPart(List<String> attributes, String indent, Scenario scenario) {
        StringBuilder result = new StringBuilder();
        String longIndent = indent + SpeedyConstants.INDENT;
        for (String attribute : attributes) {
            result.append(indent).append("IF (NEW.").append(attribute);
            result.append(" LIKE '").append(SpeedyConstants.SKOLEM_PREFIX).append("%') THEN").append("\n");
            result.append(longIndent).append("INSERT INTO ");
            result.append(getSchema(scenario)).append(".").append(LunaticConstants.SKOLEM_OCC_TABLE).append(" VALUES(");
            result.append("NEW.").append(attribute).append(", TG_TABLE_NAME, NEW.oid, ");
            result.append("'").append(attribute).append("');\n");
            result.append(indent).append("END IF;").append("\n");
        }
        return result.toString();
    }

    private String addTriggerToTable(String tableName, List<String> attributes, String targetSchema) {
        StringBuilder result = new StringBuilder();
        result.append("DROP TRIGGER IF EXISTS trigg_update_skolem_").append(tableName);
        result.append(" ON ").append(targetSchema).append(".").append(tableName).append(";").append("\n\n");
        result.append("CREATE TRIGGER trigg_update_skolem_").append(tableName).append("\n");
        result.append("AFTER INSERT OR UPDATE OF ");
        for (String attribute : attributes) {
            result.append(attribute).append(", ");
        }
        LunaticUtility.removeChars(", ".length(), result);
        result.append(" OR DELETE ON ").append(targetSchema).append(".").append(tableName).append("\n");
        result.append("FOR EACH ROW EXECUTE PROCEDURE update_skolem_occurrences_");
        result.append(tableName).append("();").append("\n\n");
        return result.toString();
    }

    private List<AttributeRef> findAttributeRefsWithSkolem(List<Dependency> stTgds) {
        List<AttributeRef> result = new ArrayList<AttributeRef>();
        for (Dependency dependency : stTgds) {
            Map<AttributeRef, IValueGenerator> targetGenerators = dependency.getTargetGenerators();
            for (AttributeRef attributeRef : targetGenerators.keySet()) {
                IValueGenerator valueGenerator = targetGenerators.get(attributeRef);
                if (valueGenerator instanceof SkolemFunctionGenerator) {
                    LunaticUtility.addIfNotContained(result, attributeRef);
                }
            }
        }
        return result;
    }

    private Map<String, List<String>> groupAttributeByTable(List<AttributeRef> attributeRefsWithSkolem) {
        Map<String, List<String>> result = new HashMap<String, List<String>>();
        for (AttributeRef attributeRef : attributeRefsWithSkolem) {
            List<String> attributes = result.get(attributeRef.getTableName());
            if (attributes == null) {
                attributes = new ArrayList<String>();
                result.put(attributeRef.getTableName(), attributes);
            }
            LunaticUtility.addIfNotContained(attributes, attributeRef.getName());
        }
        return result;
    }

    private String getSchema(Scenario scenario) {
        return LunaticDBMSUtility.getWorkSchema(scenario);
    }

}
