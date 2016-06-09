package it.unibas.lunatic.persistence.relational;

import it.unibas.lunatic.OperatorFactory;
import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.exceptions.DAOException;
import it.unibas.lunatic.model.algebra.operators.BuildAlgebraTreeForTGD;
import it.unibas.lunatic.model.chase.chasemc.ChaseTree;
import it.unibas.lunatic.model.chase.chasemc.DeltaChaseStep;
import it.unibas.lunatic.model.chase.commons.operators.IBuildDatabaseForChaseStep;
import it.unibas.lunatic.model.chase.commons.ChaseStats;
import it.unibas.lunatic.model.chase.commons.operators.ChaseUtility;
import it.unibas.lunatic.model.dependency.Dependency;
import it.unibas.lunatic.model.dependency.IFormulaAtom;
import it.unibas.lunatic.model.dependency.RelationalAtom;
import it.unibas.lunatic.utility.LunaticUtility;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import speedy.SpeedyConstants;
import speedy.model.algebra.IAlgebraOperator;
import speedy.model.algebra.operators.ITupleIterator;
import speedy.model.algebra.operators.sql.AlgebraTreeToSQL;
import speedy.model.database.Attribute;
import speedy.model.database.Cell;
import speedy.model.database.IDatabase;
import speedy.model.database.ITable;
import speedy.model.database.Tuple;
import speedy.model.database.dbms.DBMSDB;
import speedy.model.database.dbms.DBMSTable;
import speedy.model.database.dbms.DBMSVirtualDB;
import speedy.model.database.mainmemory.MainMemoryVirtualDB;
import speedy.model.database.mainmemory.MainMemoryVirtualTable;
import speedy.model.database.operators.dbms.IValueEncoder;
import speedy.persistence.file.operators.ExportCSVFileWithCopy;
import speedy.persistence.relational.AccessConfiguration;
import speedy.persistence.relational.QueryManager;
import speedy.utility.DBMSUtility;

public class ExportChaseStepResultsCSV {

    private ExportCSVFileWithCopy csvExporter = new ExportCSVFileWithCopy();
    private String CSV_SEPARATOR = ",";
    private String tablePrefix = "#";
    private int counter = 0;

    public List<String> exportResult(DeltaChaseStep result, String folder, boolean materializeFKJoins) throws DAOException {
        counter = 0;
        if (!folder.endsWith(File.separator)) {
            folder = folder + File.separator;
        }
        File fileFolder = new File(folder);
        fileFolder.mkdirs();
        List<String> resultFiles = new ArrayList<String>();
        exportSolutions(result, folder, resultFiles, materializeFKJoins);
        return resultFiles;
    }

    public void exportSolutionsInSeparateFiles(ChaseTree chaseTree, Scenario scenario) {
        long start = new Date().getTime();
        if (scenario.getValueEncoder() != null) scenario.getValueEncoder().prepareForDecoding();
        List<DeltaChaseStep> leaves = ChaseUtility.getAllLeaves(chaseTree.getRoot());
        int solutionIndex = 0;
        for (DeltaChaseStep step : leaves) {
            if (step.isInvalid() || step.isDuplicate()) {
                continue;
            }
            String path = scenario.getConfiguration().getExportSolutionsPath() + "/Solution_" + solutionIndex++ + "/";
            System.out.println("Exporting solution " + solutionIndex + " into " + path);
            IDatabase database = OperatorFactory.getInstance().getDatabaseBuilder(scenario).extractDatabase(step.getId(), step.getDeltaDB(), step.getOriginalDB(), step.getScenario());
            for (String tableName : database.getTableNames()) {
                ITable table = database.getTable(tableName);
                exportTable(table, path, scenario.getValueEncoder());
            }
        }
        if (scenario.getValueEncoder() != null) scenario.getValueEncoder().closeDecoding();
        long end = new Date().getTime();
        ChaseStats.getInstance().addStat(ChaseStats.WRITE_TIME, end - start);
    }

    public void exportSolutionInSeparateFiles(IDatabase database, Scenario scenario) {
        exportSolutionInSeparateFiles(database, scenario.getValueEncoder(), scenario.getConfiguration().isExportSolutionsWithHeader(), scenario.getConfiguration().getExportSolutionsPath(), scenario.getConfiguration().getMaxNumberOfThreads());
    }

    public void exportSolutionInSeparateFiles(IDatabase database, IValueEncoder valueEncoder, boolean withHeader, String path, int numberOfThreads) {
        System.out.println("Exporting solution in " + path);
        long start = new Date().getTime();
        if (valueEncoder != null) valueEncoder.prepareForDecoding();
        if (isDBMS(database)) {
            csvExporter.exportDatabase(database, valueEncoder, withHeader, path, numberOfThreads);
        } else {
            exportMainMemory(database, valueEncoder, path);
        }
        if (valueEncoder != null) valueEncoder.closeDecoding();
        long end = new Date().getTime();
        ChaseStats.getInstance().addStat(ChaseStats.WRITE_TIME, end - start);
    }

    private boolean isDBMS(IDatabase database) {
        return (database instanceof DBMSDB) || (database instanceof DBMSVirtualDB);
    }

    private void exportMainMemory(IDatabase database, IValueEncoder valueEncoder, String path) {
        for (String tableName : database.getTableNames()) {
            ITable table = database.getTable(tableName);
            exportTable(table, path, valueEncoder);
        }
    }

    public void exportChangesInSeparateFiles(ChaseTree chaseTree, Scenario scenario) {
        long start = new Date().getTime();
        List<DeltaChaseStep> leaves = ChaseUtility.getAllLeaves(chaseTree.getRoot());
        int solutionIndex = 0;
        for (DeltaChaseStep step : leaves) {
            if (step.isInvalid() || step.isDuplicate()) {
                continue;
            }
            String path = scenario.getConfiguration().getExportSolutionsPath() + "/Changes_" + solutionIndex++ + ".csv";
            System.out.println("Exporting changes " + solutionIndex + " into " + path);
            OperatorFactory.getInstance().getGenerateModifiedCells(scenario).generate(step, path);
        }
        long end = new Date().getTime();
        ChaseStats.getInstance().addStat(ChaseStats.WRITE_TIME, end - start);
    }

    private void exportSolutions(DeltaChaseStep step, String folder, List<String> results, boolean materializeFKJoins) {
        if (step.isLeaf()) {
            if (step.isDuplicate() || step.isInvalid()) {
                return;
            }
            counter++;
            String resultFile = folder + "Solution" + (counter < 10 ? "0" + counter : counter) + ".csv";
            IDatabase database = getDatabaseBuilder(step.getScenario()).extractDatabaseWithDistinct(step.getId(), step.getDeltaDB(), step.getOriginalDB(), step.getScenario());
//            IDatabase database = databaseBuilder.extractDatabase(step.getId(), step.getDeltaDB(), step.getOriginalDB());
            if (materializeFKJoins) {
                materializeFKJoins(database, step);
            }
            exportDatabase(database, resultFile, step.getScenario());
            results.add(resultFile);
        } else {
            for (DeltaChaseStep child : step.getChildren()) {
                exportSolutions(child, folder, results, materializeFKJoins);
            }
        }
    }

    public void exportDatabase(DeltaChaseStep step, String stepId, String file) throws DAOException {
        IDatabase database = getDatabaseBuilder(step.getScenario()).extractDatabaseWithDistinct(stepId, step.getDeltaDB(), step.getOriginalDB(), step.getScenario());
//        IDatabase database = databaseBuilder.extractDatabase(stepId, step.getDeltaDB(), step.getOriginalDB());
        exportDatabase(database, file, step.getScenario());
    }

    public void exportDatabase(IDatabase database, String file, Scenario scenario) throws DAOException {
        File outputFile = new File(file);
        outputFile.getParentFile().mkdirs();
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(outputFile), Charset.forName("UTF-8")));
            for (String tableName : database.getTableNames()) {
                ITable table = database.getTable(tableName);
                writer.println(tablePrefix + table.getName());
                writeTable(writer, table, scenario.getValueEncoder());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new DAOException("Unable to export database " + ex);
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
    }

    private void exportTable(ITable table, String folder, IValueEncoder valueEncoder) {
        String file = folder + "/" + table.getName() + ".csv";
        File outputFile = new File(file);
        outputFile.getParentFile().mkdirs();
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(outputFile), Charset.forName("UTF-8")));
            writeTable(writer, table, valueEncoder);
        } catch (Exception ex) {
            throw new DAOException("Unable to export database " + ex);
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
    }

    private void writeTable(PrintWriter writer, ITable table, IValueEncoder valueEncoder) {
        StringBuilder line = new StringBuilder();
        for (Attribute attribute : table.getAttributes()) {
            if (excludeAttribute(attribute.getName())) {
                continue;
            }
            line.append(cleanAttributeName(attribute.getName())).append(CSV_SEPARATOR);
        }
        LunaticUtility.removeChars(CSV_SEPARATOR.length(), line);
        writer.println(line.toString());
        ITupleIterator it = table.getTupleIterator();
//        ITupleIterator it = getDistinctFromTable(table, database);
        while (it.hasNext()) {
            line = new StringBuilder();
            Tuple tuple = it.next();
            for (Cell cell : tuple.getCells()) {
                if (excludeAttribute(cell.getAttribute())) {
                    continue;
                }
                String value = cell.getValue().toString();
                if (SpeedyConstants.NULL_VALUE.equals(value)) {
                    value = "NULL";
                } else if (valueEncoder != null) {
                    value = valueEncoder.decode(value);
                }
//                if (value.startsWith(SpeedyConstants.SKOLEM_PREFIX)) {
//                    value = "NULL";
//                }
                line.append(value).append(CSV_SEPARATOR);
            }
            LunaticUtility.removeChars(CSV_SEPARATOR.length(), line);
            writer.println(line.toString());
        }
        it.close();
        writer.println();
    }

//    private ITupleIterator getDistinctFromTable(ITable table, IDatabase database) {
//        Project project = new Project(buildAttributes(table.getAttributes()));
//        project.addChild(new Scan(new TableAlias(table.getName())));
//        Distinct distinct = new Distinct();
//        distinct.addChild(project);
//        return queryRunner.run(distinct, null, database);
//    }
//
//    private List<AttributeRef> buildAttributes(List<Attribute> attributes) {
//        List<AttributeRef> result = new ArrayList<AttributeRef>();
//        for (Attribute attribute : attributes) {
//            if (excludeAttribute(attribute.getName())) {
//                continue;
//            }
//            result.add(new AttributeRef(attribute.getTableName(), attribute.getName()));
//        }
//        return result;
//    }
    private String cleanAttributeName(String attributeName) {
        if (!attributeName.contains("_")) {
            return attributeName;
        }
        return attributeName.substring(attributeName.lastIndexOf("_") + 1);
    }

    private boolean excludeAttribute(String attribute) {
        return SpeedyConstants.OID.equals(attribute) || SpeedyConstants.TID.equals(attribute);
//        return false;
//        return SpeedyConstants.OID.equals(attribute) || SpeedyConstants.TID.equals(attribute) || attribute.endsWith("_" + SpeedyConstants.OID);
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    ///////                       MATERIALIZE FOREIGN KEY JOINS                                  //////
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    private BuildAlgebraTreeForTGD treeBuilderForTGD = new BuildAlgebraTreeForTGD();
    private AlgebraTreeToSQL queryBuilder = new AlgebraTreeToSQL();

    private void materializeFKJoins(IDatabase database, DeltaChaseStep step) {
        if (step.getScenario().isDBMS()) {
            materializeFKJoinsDBMS(database, step);
        } else {
            materializeFKJoinsMainMemory(database, step);
        }
    }

    private void materializeFKJoinsDBMS(IDatabase database, DeltaChaseStep step) {
        Scenario scenario = step.getScenario();
        for (Dependency dependency : scenario.getExtTGDs()) {
            IAlgebraOperator satisfactionQuery = treeBuilderForTGD.buildCheckSatisfactionAlgebraTreesForTGD(dependency, scenario, false);
            String tableName = getJoinTableName(dependency);
            materializeJoin(tableName, satisfactionQuery, database, scenario);
            DBMSVirtualDB virtualDB = (DBMSVirtualDB) database;
            DBMSTable joinTable = new DBMSTable(tableName, ((DBMSDB) step.getDeltaDB()).getAccessConfiguration());
            virtualDB.addTable(joinTable);
        }
    }

    private void materializeJoin(String tableName, IAlgebraOperator satisfactionQuery, IDatabase databaseForStep, Scenario scenario) {
        AccessConfiguration accessConfiguration = ((DBMSVirtualDB) databaseForStep).getAccessConfiguration();
        StringBuilder script = new StringBuilder();
        script.append("DROP TABLE IF EXISTS ").append(DBMSUtility.getSchemaNameAndDot(accessConfiguration)).append(tableName).append(";\n");
        script.append("CREATE TABLE ").append(DBMSUtility.getSchemaNameAndDot(accessConfiguration)).append(tableName).append(" WITH OIDS AS (\n");
        script.append(queryBuilder.treeToSQL(satisfactionQuery, null, databaseForStep, ""));
        script.append(");\n");
        QueryManager.executeScript(script.toString(), accessConfiguration, true, true, true, false);
    }

    private void materializeFKJoinsMainMemory(IDatabase database, DeltaChaseStep step) {
        Scenario scenario = step.getScenario();
        Map<Dependency, IAlgebraOperator> tgdQuerySatisfactionMap = treeBuilderForTGD.buildAlgebraTreesForTGDSatisfaction(scenario.getExtTGDs(), scenario);
        for (Dependency dependency : scenario.getExtTGDs()) {
            IAlgebraOperator satisfactionQuery = tgdQuerySatisfactionMap.get(dependency);
            String tableName = getJoinTableName(dependency);
            MainMemoryVirtualTable joinTable = new MainMemoryVirtualTable(tableName, satisfactionQuery, database, step.getOriginalDB());
            MainMemoryVirtualDB virtualDB = (MainMemoryVirtualDB) database;
            virtualDB.addTable(joinTable);
        }
    }

    private String getJoinTableName(Dependency dependency) {
        StringBuilder sb = new StringBuilder();
        for (IFormulaAtom atom : dependency.getPremise().getAtoms()) {
            if (atom instanceof RelationalAtom) {
                RelationalAtom relationalAtom = (RelationalAtom) atom;
                sb.append(relationalAtom.getTableName()).append("_");
            }
        }
        sb.append("join_");
        for (IFormulaAtom atom : dependency.getConclusion().getAtoms()) {
            if (atom instanceof RelationalAtom) {
                RelationalAtom relationalAtom = (RelationalAtom) atom;
                sb.append(relationalAtom.getTableName()).append("_");
            }
        }
        LunaticUtility.removeChars("_".length(), sb);
        return sb.toString();
    }

    private IBuildDatabaseForChaseStep getDatabaseBuilder(Scenario scenario) {
        return OperatorFactory.getInstance().getDatabaseBuilder(scenario);
    }

}
