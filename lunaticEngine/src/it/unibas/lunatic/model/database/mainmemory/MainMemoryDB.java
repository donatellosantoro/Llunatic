package it.unibas.lunatic.model.database.mainmemory;

import it.unibas.lunatic.LunaticConstants;
import it.unibas.lunatic.utility.LunaticUtility;
import it.unibas.lunatic.model.database.*;
import it.unibas.lunatic.model.database.mainmemory.datasource.DataSource;
import it.unibas.lunatic.model.database.mainmemory.datasource.ForeignKeyConstraint;
import it.unibas.lunatic.model.database.mainmemory.datasource.INode;
import it.unibas.lunatic.model.database.mainmemory.datasource.KeyConstraint;
import it.unibas.lunatic.persistence.PersistenceConstants;
import it.unibas.lunatic.model.database.mainmemory.datasource.operators.CalculateSize;
import it.unibas.lunatic.model.database.mainmemory.paths.PathExpression;
import it.unibas.lunatic.model.database.LLUNValue;
import it.unibas.lunatic.model.database.NullValue;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainMemoryDB implements IDatabase {

    private DataSource dataSource;
    private Map<NullValue, List<Cell>> skolemOccurrences = new HashMap<NullValue, List<Cell>>();

    public MainMemoryDB(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public String getName() {
        return dataSource.getSchema().getLabel();
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public Map<NullValue, List<Cell>> getSkolemOccurrences() {
        return skolemOccurrences;
    }

    public List<String> getTableNames() {
        INode schema = dataSource.getSchema();
        List<String> result = new ArrayList<String>();
        for (INode setNode : schema.getChildren()) {
            result.add(setNode.getLabel());
        }
        return result;
    }

    public List<Key> getKeys() {
        List<Key> result = new ArrayList<Key>();
        for (KeyConstraint keyConstraint : dataSource.getKeyConstraints()) {
            List<AttributeRef> attributeRefs = extractPaths(keyConstraint.getKeyPaths());
            result.add(new Key(attributeRefs, keyConstraint.isPrimaryKey()));
        }
        return result;
    }

    private List<AttributeRef> extractPaths(List<PathExpression> pathExpressions) {
        List<AttributeRef> attributeRefs = new ArrayList<AttributeRef>();
        for (PathExpression pathExpression : pathExpressions) {
            String tableName = pathExpression.getPathSteps().get(1);
            String attributeName = pathExpression.getPathSteps().get(3);
            AttributeRef attributeRef = new AttributeRef(tableName, attributeName);
            attributeRefs.add(attributeRef);
        }
        return attributeRefs;
    }

    public List<Key> getKeys(String table) {
        List<Key> result = new ArrayList<Key>();
        for (Key key : getKeys()) {
            String tableName = key.getAttributes().get(0).getTableName();
            if (tableName.equals(table)) {
                result.add(key);
            }
        }
        return result;
    }

    public List<ForeignKey> getForeignKeys() {
        List<ForeignKey> result = new ArrayList<ForeignKey>();
        for (ForeignKeyConstraint foreignKeyConstraint : dataSource.getForeignKeyConstraints()) {
            List<AttributeRef> keyAttributes = extractPaths(foreignKeyConstraint.getKeyConstraint().getKeyPaths());
            List<AttributeRef> refAttributes = extractPaths(foreignKeyConstraint.getForeignKeyPaths());
            ForeignKey foreignKey = new ForeignKey(keyAttributes, refAttributes);
            result.add(foreignKey);
        }
        return result;
    }

    public List<ForeignKey> getForeignKeys(String table) {
        List<ForeignKey> result = new ArrayList<ForeignKey>();
        for (ForeignKey foreignKey : getForeignKeys()) {
            String tableName = foreignKey.getRefAttributes().get(0).getTableName();
            if (tableName.equals(table)) {
                result.add(foreignKey);
            }
        }
        return result;
    }

    public ITable getTable(String name) {
        INode dbSchema = dataSource.getSchema();
        INode tableSchemaRoot = dbSchema.getChild(name);
        if (tableSchemaRoot == null) {
            throw new IllegalArgumentException("Unable to find table " + name + " in db " + this);
        }
        DataSource tableDataSource = new DataSource(PersistenceConstants.TYPE_ALGEBRA_RESULT, tableSchemaRoot);
        INode dbInstance = dataSource.getInstances().get(0);
        INode instance = dbInstance.getChild(name);
        tableDataSource.addInstance(instance);
        return new MainMemoryTable(tableDataSource, this);
    }

    public ITable getFirstTable() {
        return getTable(getTableNames().get(0));
    }

    public int getSize() {
        return new CalculateSize().getNumberOfTuples(this.dataSource.getInstances().get(0));
    }

    public MainMemoryDB clone() {
        try {
            MainMemoryDB clone = (MainMemoryDB) super.clone();
            clone.dataSource = this.dataSource.clone();
            return clone;
        } catch (CloneNotSupportedException ex) {
            return null;
        }
    }

    public String printSchema() {
        StringBuilder result = new StringBuilder();
        result.append("Schema: ").append(getName()).append(" {\n");
        for (String tableName : getTableNames()) {
            ITable table = getTable(tableName);
            result.append(table.printSchema(LunaticConstants.INDENT));
        }
        if (!getKeys().isEmpty()) {
            result.append(LunaticConstants.INDENT).append("--------------- Keys: ---------------\n");
            for (Key key : getKeys()) {
                result.append(LunaticConstants.INDENT).append(key).append("\n");
            }
        }
        if (!getForeignKeys().isEmpty()) {
            result.append(LunaticConstants.INDENT).append("----------- Foreign Keys: -----------\n");
            for (ForeignKey foreignKey : getForeignKeys()) {
                result.append(LunaticConstants.INDENT).append(foreignKey).append("\n");
            }
        }
        result.append("}\n");
        return result.toString();
    }

    public String printInstances() {
        return printInstances(false);
    }

    public String printInstances(boolean sort) {
        StringBuilder result = new StringBuilder();
        result.append("Tables: ").append(getName()).append(" {\n");
        for (String tableName : getTableNames()) {
            ITable table = getTable(tableName);
            if (sort) {
                result.append(table.toStringWithSort(LunaticConstants.INDENT));
            } else {
                result.append(table.toString(LunaticConstants.INDENT));
            }
        }
        result.append("}\n");
        return result.toString();
    }

    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append(printSchema());
        result.append(printInstances());
        if (!skolemOccurrences.isEmpty()) {
            result.append("Null Occurrences:\n").append(LunaticUtility.printMap(skolemOccurrences));
        }
        return result.toString();
    }

    public void addTable(ITable table) {
        throw new UnsupportedOperationException("Not supported.");
    }
}