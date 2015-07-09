package it.unibas.lunatic.model.chase.chasede.operators.mainmemory;

import it.unibas.lunatic.utility.LunaticUtility;
import it.unibas.lunatic.model.algebra.IAlgebraOperator;
import it.unibas.lunatic.model.algebra.operators.AlgebraUtility;
import it.unibas.lunatic.model.algebra.operators.ITupleIterator;
import it.unibas.lunatic.model.chase.chasede.operators.IInsertFromSelectNaive;
import it.unibas.lunatic.model.algebra.operators.mainmemory.InsertTuple;
import it.unibas.lunatic.model.chase.chasede.operators.IValueOccurrenceHandlerDE;
import it.unibas.lunatic.model.database.Attribute;
import it.unibas.lunatic.model.database.AttributeRef;
import it.unibas.lunatic.model.database.Cell;
import it.unibas.lunatic.model.database.CellRef;
import it.unibas.lunatic.model.database.IDatabase;
import it.unibas.lunatic.model.database.ITable;
import it.unibas.lunatic.model.database.IValue;
import it.unibas.lunatic.model.database.TableAlias;
import it.unibas.lunatic.model.database.Tuple;
import it.unibas.lunatic.model.database.TupleOID;
import it.unibas.lunatic.model.database.mainmemory.MainMemoryDB;
import it.unibas.lunatic.model.database.mainmemory.MainMemoryTable;
import it.unibas.lunatic.model.database.mainmemory.datasource.INode;
import it.unibas.lunatic.model.database.mainmemory.datasource.IntegerOIDGenerator;
import it.unibas.lunatic.model.database.NullValue;
import it.unibas.lunatic.model.dependency.Dependency;
import it.unibas.lunatic.model.generators.IValueGenerator;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MainMemoryInsertFromSelectNaive implements IInsertFromSelectNaive {

    private static Logger logger = LoggerFactory.getLogger(MainMemoryInsertFromSelectNaive.class);

    private InsertTuple insertOperator = new InsertTuple();
    private IValueOccurrenceHandlerDE occurrenceHandlerDE = new MainMemoryDEOccurrenceHandler();

    @Override
    public boolean execute(Dependency dependency, IAlgebraOperator sourceQuery, IDatabase source, IDatabase target) {
        Map<AttributeRef, IValueGenerator> targetGenerators = dependency.getTargetGenerators();
        if (logger.isDebugEnabled()) logger.debug("----Executing insert. Tgd generator map: " + LunaticUtility.printMap(targetGenerators));
        if (logger.isDebugEnabled()) logger.debug("----Source query: " + sourceQuery);
        List<TableAlias> targetTablesToInsert = findTableAliases(targetGenerators);
        if (logger.isDebugEnabled()) logger.debug("----Tables to insert: " + targetTablesToInsert);
        ITupleIterator it = sourceQuery.execute(source, target);
        boolean insertedTuple = it.hasNext();
        while (it.hasNext()) {
            Tuple premiseTuple = it.next();
            if (logger.isDebugEnabled()) logger.debug("----Premise tuple: " + premiseTuple);
            for (TableAlias tableAlias : targetTablesToInsert) {
                if (logger.isDebugEnabled()) logger.debug("----Inserting into table: " + tableAlias);
                MainMemoryTable table = (MainMemoryTable) target.getTable(tableAlias.getTableName());
                Tuple targetTuple = buildTargetTuple(tableAlias, table, premiseTuple, targetGenerators);
                if (logger.isDebugEnabled()) logger.debug("----Target tuple: " + targetTuple);
                insertOperator.execute(table, targetTuple, source, target);
                for (Cell cell : targetTuple.getCells()) {
                    IValue cellValue = cell.getValue();
                    if (cellValue instanceof NullValue) {
                        occurrenceHandlerDE.addOccurrenceForNull(target, (NullValue) cellValue, new CellRef(cell));
                    }
                }
            }
        }
        it.close();
//        removeDuplicates(targetTablesToInsert, target);
        return insertedTuple;
    }

    private List<TableAlias> findTableAliases(Map<AttributeRef, IValueGenerator> targetGenerators) {
        List<TableAlias> result = new ArrayList<TableAlias>();
        for (AttributeRef attributeRef : targetGenerators.keySet()) {
            TableAlias tableAlias = attributeRef.getTableAlias();
            LunaticUtility.addIfNotContained(result, tableAlias);
        }
        return result;
    }

    private Tuple buildTargetTuple(TableAlias tableAlias, ITable table, Tuple sourceTuple, Map<AttributeRef, IValueGenerator> targetGenerators) {
        Tuple result = new Tuple(new TupleOID(IntegerOIDGenerator.getNextOID()));
        for (Attribute attribute : table.getAttributes()) {
            AttributeRef attributeRef = new AttributeRef(table.getName(), attribute.getName());
            IValue value = computeValue(tableAlias, attribute, targetGenerators, sourceTuple);
            Cell cell = new Cell(result.getOid(), attributeRef, value);
            result.addCell(cell);
        }
        return result;
    }

    private IValue computeValue(TableAlias tableAlias, Attribute attribute, Map<AttributeRef, IValueGenerator> targetGenerators, Tuple sourceTuple) {
        AttributeRef attributeRef = new AttributeRef(tableAlias, attribute.getName());
        IValueGenerator generator = targetGenerators.get(attributeRef);
        return generator.generateValue(sourceTuple);
    }

    private void removeDuplicates(List<TableAlias> targetTablesToInsert, MainMemoryDB target) {
        INode instance = target.getDataSource().getInstances().get(0);
        for (TableAlias table : targetTablesToInsert) {
            INode tableRoot = instance.getChild(table.getTableName());
            AlgebraUtility.removeDuplicates(tableRoot.getChildren());
        }
    }
}
