package it.unibas.lunatic.model.chase.chasede.operators.mainmemory;

import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.utility.LunaticUtility;
import it.unibas.lunatic.model.chase.chasede.operators.IInsertFromSelectNaive;
import speedy.model.database.Attribute;
import speedy.model.database.AttributeRef;
import speedy.model.database.Cell;
import speedy.model.database.IDatabase;
import speedy.model.database.ITable;
import speedy.model.database.IValue;
import it.unibas.lunatic.model.dependency.Dependency;
import it.unibas.lunatic.model.generators.IValueGenerator;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import speedy.model.algebra.IAlgebraOperator;
import speedy.model.algebra.operators.IInsertTuple;
import speedy.model.algebra.operators.ITupleIterator;
import speedy.model.algebra.operators.mainmemory.MainMemoryInsertTuple;
import speedy.model.database.NullValue;
import speedy.model.database.TableAlias;
import speedy.model.database.Tuple;
import speedy.model.database.TupleOID;
import speedy.model.database.mainmemory.datasource.IntegerOIDGenerator;

public class MainMemoryInsertFromSelectRestricted implements IInsertFromSelectNaive {

    private static Logger logger = LoggerFactory.getLogger(MainMemoryInsertFromSelectRestricted.class);

    private IInsertTuple insertOperator = new MainMemoryInsertTuple();
    private MainMemoryDEOccurrenceHandler occurrenceHandlerDE = new MainMemoryDEOccurrenceHandler();

    @Override
    public boolean execute(Dependency dependency, IAlgebraOperator sourceQuery, IDatabase source, IDatabase target, Scenario scenario) {
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
                ITable table = target.getTable(tableAlias.getTableName());
                Tuple targetTuple = buildTargetTuple(tableAlias, table, premiseTuple, targetGenerators);
                if (logger.isDebugEnabled()) logger.debug("----Target tuple: " + targetTuple);
                insertOperator.execute(table, targetTuple, source, target);
                for (Cell cell : targetTuple.getCells()) {
                    IValue cellValue = cell.getValue();
                    if (cellValue instanceof NullValue) {
                        occurrenceHandlerDE.addOccurrenceForNull(target, (NullValue) cellValue, cell);
                    }
                }
            }
        }
        it.close();
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
}
