package it.unibas.lunatic.test.checker;

import it.unibas.lunatic.LunaticConstants;
import it.unibas.lunatic.utility.LunaticUtility;
import it.unibas.lunatic.test.UtilityTest;
import java.util.*;
import junit.framework.TestCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import speedy.SpeedyConstants;
import speedy.model.algebra.operators.ITupleIterator;
import speedy.model.database.Cell;
import speedy.model.database.IDatabase;
import speedy.model.database.ITable;
import speedy.model.database.IValue;
import speedy.model.database.NullValue;
import speedy.model.database.Tuple;

public class DataSourceTxtInstanceChecker {

    private static Logger logger = LoggerFactory.getLogger(DataSourceTxtInstanceChecker.class);

    public void checkInstance(IDatabase database, String expectedInstanceFile) throws Exception {
        DAOTxt dao = new DAOTxt();
        Map<String, List<IExpectedTuple>> instanceMap = dao.loadData(expectedInstanceFile);
        if (logger.isDebugEnabled()) logger.debug("Expected instance: " + LunaticUtility.printMap(instanceMap));
        for (String tableName : database.getTableNames()) {
            ITable table = database.getTable(tableName);
            checkTable(database, table, instanceMap, expectedInstanceFile);
//            if (table instanceof DBMSTable) {
//                ((DBMSTable) table).closeConnection();
//            }
        }
        checkJoins(database, instanceMap, expectedInstanceFile);
    }

    protected void checkTable(IDatabase database, ITable table, Map<String, List<IExpectedTuple>> instanceMap, String expectedInstanceFile) {
        String tableName = table.getName();
        List<IExpectedTuple> expectedTuples = instanceMap.get(tableName.toLowerCase());
        if (UtilityTest.getSize(table) == 0) {
            TestCase.assertNull("Unable to find generated tuples for set: " + tableName + " in " + expectedInstanceFile, expectedTuples);
            return;
        }
        TestCase.assertNotNull("Unable to find expected tuples for set: " + tableName + " in " + expectedInstanceFile, expectedTuples);
        List<IExpectedTuple> expectedTuplesClone = new ArrayList<IExpectedTuple>(expectedTuples);
        ITupleIterator it = table.getTupleIterator();
        while (it.hasNext()) {
            Tuple instanceTuple = it.next();
            TestCase.assertTrue("Extra tuples were generated: " + instanceTuple.toString() + getMessage(expectedInstanceFile, instanceMap, database), checkTupleValues(instanceTuple, expectedTuplesClone));
        }
        it.close();
        TestCase.assertEquals("Unable to find tuple: " + expectedTuples + getMessage(expectedInstanceFile, instanceMap, database) + "\n" + expectedTuples, 0, expectedTuplesClone.size());
    }

    private String getMessage(String expectedInstanceFile, Map<String, List<IExpectedTuple>> instanceMap, IDatabase database) {
        StringBuilder result = new StringBuilder();
        result.append("\n");
        result.append("Instance file: ").append(expectedInstanceFile).append("\n");
        result.append("Expected tuples:\n").append(LunaticUtility.printMap(instanceMap));
        result.append("Generated tuples:\n").append(database.printInstances());
        return result.toString();
    }

    private String getJoinMessage(String expectedInstanceFile, Map<String, List<IExpectedTuple>> instanceMap, IDatabase instance, List<ExpectedTuplePair> expectedTuplePairs, List<InstanceTuplePair> spicyTuplePairs) {
        StringBuilder result = new StringBuilder();
        result.append("\n").append(getMessage(expectedInstanceFile, instanceMap, instance)).append("\n");
        result.append("Expected tuple pairs:\n").append(LunaticUtility.printCollection(expectedTuplePairs)).append("\n");
        result.append("Instance tuple pairs:\n").append(LunaticUtility.printCollection(spicyTuplePairs)).append("\n");
        return result.toString();
    }

    private boolean checkTupleValues(Tuple instanceTuple, List<IExpectedTuple> expectedTuples) {
        for (Iterator<IExpectedTuple> tupleIterator = expectedTuples.iterator(); tupleIterator.hasNext();) {
            IExpectedTuple expectedTuple = tupleIterator.next();
            if (matches(instanceTuple, expectedTuple)) {
                tupleIterator.remove();
                return true;
            }
        }
        return false;
    }

    private boolean matches(Tuple instanceTuple, IExpectedTuple expectedTuple) {
        if ((instanceTuple.getCells().size() - 1) //Exclude OID cell
                != expectedTuple.getValues().size()) {
            return false;
        }
        for (int i = 0; i < expectedTuple.getValues().size(); i++) {
            IExpectedValue value = expectedTuple.getValues().get(i);
            IValue instanceValue = instanceTuple.getCells().get(i + 1).getValue();
            if (value.isNullOrSkolem()) {
                if (!checkNullOrSkolem(instanceValue)) {
                    return false;
                }
            } else if (!value.equals(instanceValue)) {
                return false;
            }
        }
        return true;
    }

    protected void checkJoins(IDatabase instance, Map<String, List<IExpectedTuple>> instanceMap, String expectedInstanceFile) {
        List<ExpectedTuplePair> expectedTuplePairs = buildExpectedTuplePairs(instanceMap);
        List<InstanceTuplePair> tuplePairs = buildInstanceTuplePairs(instance);
        List<InstanceTuplePair> tuplePairsClone = new ArrayList<InstanceTuplePair>(tuplePairs);
        for (ExpectedTuplePair expectedTuplePair : expectedTuplePairs) {
            TestCase.assertTrue("Unable to find tuple pair: " + expectedTuplePair.toString() + getJoinMessage(expectedInstanceFile, instanceMap, instance, expectedTuplePairs, tuplePairs), checkTuplePairs(expectedTuplePair, tuplePairsClone));
        }
        TestCase.assertEquals("Extra tuple pairs were generated: " + tuplePairsClone + getJoinMessage(expectedInstanceFile, instanceMap, instance, expectedTuplePairs, tuplePairs), 0, tuplePairsClone.size());
    }

    private List<ExpectedTuplePair> buildExpectedTuplePairs(Map<String, List<IExpectedTuple>> instanceMap) {
        List<ExpectedTuplePair> pairs = new ArrayList<ExpectedTuplePair>();
        List<IExpectedTuple> tuples = extractExpectedTuples(instanceMap);
        for (int i = 0; i < tuples.size() - 1; i++) {
            IExpectedTuple currentTuple = tuples.get(i);
            if (currentTuple.containsNullOrSkolem()) {
                for (int j = 0; j < currentTuple.getValues().size(); j++) {
                    IExpectedValue expectedValue = currentTuple.getValues().get(j);
                    if (expectedValue.isNullOrSkolem()) {
                        findExpectedTuplePairs(tuples, i, j, expectedValue, pairs);
                    }
                }
            }
        }
        return pairs;
    }

    private List<IExpectedTuple> extractExpectedTuples(Map<String, List<IExpectedTuple>> instanceMap) {
        List<IExpectedTuple> allTuples = new ArrayList<IExpectedTuple>();
        Collection<List<IExpectedTuple>> tuples = instanceMap.values();
        for (Iterator<List<IExpectedTuple>> it = tuples.iterator(); it.hasNext();) {
            List<IExpectedTuple> list = it.next();
            allTuples.addAll(list);
        }
        return allTuples;
    }

    private List<InstanceTuplePair> buildInstanceTuplePairs(IDatabase instance) {
        List<InstanceTuplePair> pairs = new ArrayList<InstanceTuplePair>();
        List<Tuple> tuples = extractInstanceTuplesWithSkolem(instance);
        for (int i = 0; i < tuples.size() - 1; i++) {
            Tuple currentTuple = tuples.get(i);
            for (int j = 0; j < currentTuple.getCells().size(); j++) {
                Cell cell = currentTuple.getCells().get(j);
                IValue value = cell.getValue();
                if (isSkolem(value)) {
                    findInstanceTuplePairs(tuples, i, j, value, pairs);
                }
            }
        }
        return pairs;
    }

    private List<Tuple> extractInstanceTuplesWithSkolem(IDatabase instance) {
        List<Tuple> tupleNodes = new ArrayList<Tuple>();
        for (String tableName : instance.getTableNames()) {
            ITable table = instance.getTable(tableName);
            ITupleIterator tupleIterator = table.getTupleIterator();
            while (tupleIterator.hasNext()) {
                Tuple tuple = tupleIterator.next();
                if (containsSkolem(tuple)) {
                    tupleNodes.add(tuple);
                }
            }
            tupleIterator.close();
//            if (table instanceof DBMSTable) {
//                ((DBMSTable) table).closeConnection();
//            }
        }
        return tupleNodes;
    }

    private boolean containsSkolem(Tuple tuple) {
        for (Cell cell : tuple.getCells()) {
            IValue value = cell.getValue();
            if (isSkolem(value)) {
                return true;
            }
        }
        return false;
    }

    private List<Integer> containsSameSkolem(IValue skolemValue, Tuple tupleNode) {
        List<Integer> result = new ArrayList<Integer>();
        for (int i = 0; i < tupleNode.getCells().size(); i++) {
            Cell cell = tupleNode.getCells().get(i);
            IValue value = cell.getValue();
            if (isSkolem(value) && skolemValue.equals(value)) {
                result.add(i);
            }
        }
        return result;
    }

    private void findExpectedTuplePairs(List<IExpectedTuple> tuples, int firstTuplePosition, int firstSkolemPosition, IExpectedValue currentExpectedValue, List<ExpectedTuplePair> tuplePairs) {
        IExpectedTuple firstTuple = tuples.get(firstTuplePosition);
        for (int i = firstTuplePosition + 1; i < tuples.size(); i++) {
            IExpectedTuple secondTuple = tuples.get(i);
            List<Integer> secondSkolemPositions = secondTuple.containsSameSkolem(currentExpectedValue);
            for (Integer secondSkolemPosition : secondSkolemPositions) {
                ExpectedTuplePair tuplePair = new ExpectedTuplePair(firstTuple, secondTuple, firstSkolemPosition, secondSkolemPosition);
                tuplePairs.add(tuplePair);
            }
        }
    }

    private void findInstanceTuplePairs(List<Tuple> tuples, int firstTuplePosition, int firstSkolemPosition, IValue skolemValue, List<InstanceTuplePair> tuplePairs) {
        Tuple firstTuple = tuples.get(firstTuplePosition);
        for (int i = firstTuplePosition + 1; i < tuples.size(); i++) {
            Tuple secondTuple = tuples.get(i);
            List<Integer> secondSkolemPositions = containsSameSkolem(skolemValue, secondTuple);
            for (Integer secondSkolemPosition : secondSkolemPositions) {
                InstanceTuplePair tuplePair = new InstanceTuplePair(firstTuple, secondTuple, firstSkolemPosition, secondSkolemPosition);
                tuplePairs.add(tuplePair);
            }
        }
    }

    private boolean checkTuplePairs(ExpectedTuplePair expectedTuplePair, List<InstanceTuplePair> spicyTuplePairs) {
        for (Iterator<InstanceTuplePair> tupleIterator = spicyTuplePairs.iterator(); tupleIterator.hasNext();) {
            InstanceTuplePair spicyTuplePair = tupleIterator.next();
            if (matchesPair(spicyTuplePair, expectedTuplePair)) {
                tupleIterator.remove();
                return true;
            }
        }
        return false;
    }

    private boolean matchesPair(InstanceTuplePair spicyTuplePair, ExpectedTuplePair expectedTuplePair) {
        Tuple firstSpicyTupleNode = spicyTuplePair.getFirstTuple();
        Tuple secondSpicyTupleNode = spicyTuplePair.getSecondTuple();
        IExpectedTuple firstExpectedTuple = expectedTuplePair.getFirstTuple();
        IExpectedTuple secondExpectedTuple = expectedTuplePair.getSecondTuple();
        if ((matches(firstSpicyTupleNode, firstExpectedTuple) && matches(secondSpicyTupleNode, secondExpectedTuple))
                || (matches(secondSpicyTupleNode, firstExpectedTuple) && matches(firstSpicyTupleNode, secondExpectedTuple))) {
            return true;
        }
        return false;
    }

    protected boolean checkNullOrSkolem(IValue instanceValue) {
//        return (instanceValue instanceof INullValue) || (instanceValue instanceof OID);
        return (instanceValue instanceof NullValue);
    }

    protected boolean isSkolem(IValue value) {
        if (!(value instanceof NullValue)) {
            return false;
        }
        if (value instanceof NullValue
                && value.getPrimitiveValue().toString().startsWith(SpeedyConstants.SKOLEM_PREFIX)) {
            return true;
        }
        return false;
    }
}

class ExpectedTuplePair {

    private IExpectedTuple firstTuple;
    private IExpectedTuple secondTuple;
    private int firstSkolemPosition;
    private int secondSkolemPosition;

    public ExpectedTuplePair(IExpectedTuple firstTuple, IExpectedTuple secondTuple, int firstSkolemPosition, int secondSkolemPosition) {
        this.firstTuple = firstTuple;
        this.secondTuple = secondTuple;
        this.firstSkolemPosition = firstSkolemPosition;
        this.secondSkolemPosition = secondSkolemPosition;
    }

    public int getFirstSkolemPosition() {
        return firstSkolemPosition;
    }

    public IExpectedTuple getFirstTuple() {
        return firstTuple;
    }

    public int getSecondSkolemPosition() {
        return secondSkolemPosition;
    }

    public IExpectedTuple getSecondTuple() {
        return secondTuple;
    }

    @Override
    public String toString() {
        return "[P=" + firstSkolemPosition + "]" + firstTuple + " - " + "[P=" + secondSkolemPosition + "]" + secondTuple;
    }
}

class InstanceTuplePair {

    private Tuple firstTuple;
    private Tuple secondTuple;
    private int firstSkolemPosition;
    private int secondSkolemPosition;

    public InstanceTuplePair(Tuple firstTuple, Tuple secondTuple, int firstSkolemPosition, int secondSkolemPosition) {
        this.firstTuple = firstTuple;
        this.secondTuple = secondTuple;
        this.firstSkolemPosition = firstSkolemPosition;
        this.secondSkolemPosition = secondSkolemPosition;
    }

    public int getFirstSkolemPosition() {
        return firstSkolemPosition;
    }

    public Tuple getFirstTuple() {
        return firstTuple;
    }

    public int getSecondSkolemPosition() {
        return secondSkolemPosition;
    }

    public Tuple getSecondTuple() {
        return secondTuple;
    }

    @Override
    public String toString() {
        return "[P=" + firstSkolemPosition + "]" + firstTuple + " - " + "[P=" + secondSkolemPosition + "]" + secondTuple;
    }
}
