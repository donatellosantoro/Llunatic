package it.unibas.lunatic.test.checker;

import it.unibas.lunatic.utility.LunaticUtility;
import it.unibas.lunatic.model.database.mainmemory.datasource.INode;
import it.unibas.lunatic.model.database.NullValue;
import java.util.*;
import junit.framework.TestCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TxtInstanceChecker {

    private static Logger logger = LoggerFactory.getLogger(TxtInstanceChecker.class);

    public void checkInstance(INode instance, String expectedInstanceFile) throws Exception {
        DAOTxt dao = new DAOTxt();
        Map<String, List<IExpectedTuple>> instanceMap = dao.loadData(expectedInstanceFile);
        if (logger.isDebugEnabled()) logger.debug("Expected instance: " + LunaticUtility.printMap(instanceMap));
        for (INode setNode : instance.getChildren()) {
            checkSetTuples(instance, setNode, instanceMap, expectedInstanceFile);
        }
        checkJoins(instance, instanceMap, expectedInstanceFile);
    }

    protected void checkSetTuples(INode instance, INode setNode, Map<String, List<IExpectedTuple>> instanceMap, String expectedInstanceFile) {
        String setName = setNode.getLabel();
        List<IExpectedTuple> expectedTuples = instanceMap.get(setName.toLowerCase());
        if (setNode.getChildren().isEmpty()) {
            TestCase.assertNull("Unable to find generated tuples for set: " + setName + " in " + expectedInstanceFile, expectedTuples);
            return;
        }
        TestCase.assertNotNull("Unable to find expected tuples for set: " + setName + " in " + expectedInstanceFile, expectedTuples);
        INode setNodeClone = setNode.clone();
        for (IExpectedTuple expectedTuple : expectedTuples) {
            TestCase.assertTrue("Unable to find tuple : " + expectedTuple.toString() + getMessage(expectedInstanceFile, instanceMap, instance), checkTupleValues(expectedTuple, setNodeClone));
        }
        TestCase.assertEquals("Extra tuples were generated: " + setNodeClone + getMessage(expectedInstanceFile, instanceMap, instance) + "\n" + setNodeClone, 0, setNodeClone.getChildren().size());
    }

    private String getMessage(String expectedInstanceFile, Map<String, List<IExpectedTuple>> instanceMap, INode instance) {
        StringBuilder result = new StringBuilder();
        result.append("\n");
        result.append("Instance file: ").append(expectedInstanceFile).append("\n");
        result.append("Expected tuples:\n").append(LunaticUtility.printMap(instanceMap));
        result.append("Generated tuples:\n").append(instance);
        return result.toString();
    }

    private String getJoinMessage(String expectedInstanceFile, Map<String, List<IExpectedTuple>> instanceMap, INode instance, List<ExpectedTuplePair> expectedTuplePairs, List<InstanceTuplePair> spicyTuplePairs) {
        StringBuilder result = new StringBuilder();
        result.append("\n").append(getMessage(expectedInstanceFile, instanceMap, instance)).append("\n");
        result.append("Expected tuple pairs:\n").append(LunaticUtility.printCollection(expectedTuplePairs)).append("\n");
        result.append("Instance tuple pairs:\n").append(LunaticUtility.printCollection(spicyTuplePairs)).append("\n");
        return result.toString();
    }

    private boolean checkTupleValues(IExpectedTuple expectedTuple, INode setNode) {
        for (Iterator<INode> tupleIterator = setNode.getChildren().iterator(); tupleIterator.hasNext();) {
            INode instanceTuple = tupleIterator.next();
            if (matches(instanceTuple, expectedTuple)) {
                tupleIterator.remove();
                return true;
            }
        }
        return false;
    }

    private boolean matches(INode instanceTuple, IExpectedTuple expectedTuple) {
        if (instanceTuple.getChildren().size() != expectedTuple.getValues().size()) {
            return false;
        }
        for (int i = 0; i < expectedTuple.getValues().size(); i++) {
            IExpectedValue value = expectedTuple.getValues().get(i);
            Object instanceValue = instanceTuple.getChild(i).getChild(0).getValue();
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

    protected void checkJoins(INode instance, Map<String, List<IExpectedTuple>> instanceMap, String expectedInstanceFile) {
        List<ExpectedTuplePair> expectedTuplePairs = buildExpectedTuplePairs(instanceMap);
        List<InstanceTuplePair> spicyTuplePairs = buildInstanceTuplePairs(instance);
        List<InstanceTuplePair> spicyTuplePairsClone = new ArrayList<InstanceTuplePair>(spicyTuplePairs);
        for (ExpectedTuplePair expectedTuplePair : expectedTuplePairs) {
            TestCase.assertTrue("Unable to find tuple pair: " + expectedTuplePair.toString() + getJoinMessage(expectedInstanceFile, instanceMap, instance, expectedTuplePairs, spicyTuplePairs), checkTuplePairs(expectedTuplePair, spicyTuplePairsClone));
        }
        TestCase.assertEquals("Extra tuple pairs were generated: " + spicyTuplePairsClone + getJoinMessage(expectedInstanceFile, instanceMap, instance, expectedTuplePairs, spicyTuplePairs), 0, spicyTuplePairsClone.size());
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

    private List<InstanceTuplePair> buildInstanceTuplePairs(INode instance) {
        List<InstanceTuplePair> pairs = new ArrayList<InstanceTuplePair>();
        List<INode> tuples = extractInstanceTuplesWithSkolem(instance);
        for (int i = 0; i < tuples.size() - 1; i++) {
            INode currentTuple = tuples.get(i);
            for (int j = 0; j < currentTuple.getChildren().size(); j++) {
                INode attributeNode = currentTuple.getChild(j);
                if (isSkolem(attributeNode)) {
                    findInstanceTuplePairs(tuples, i, j, attributeNode, pairs);
                }
            }
        }
        return pairs;
    }

    private List<INode> extractInstanceTuplesWithSkolem(INode instance) {
        List<INode> tupleNodes = new ArrayList<INode>();
        for (INode setNode : instance.getChildren()) {
            for (INode tupleNode : setNode.getChildren()) {
                if (containsSkolem(tupleNode)) {
                    tupleNodes.add(tupleNode);
                }
            }
        }
        return tupleNodes;
    }

    private boolean containsSkolem(INode tupleNode) {
        for (INode attributeNode : tupleNode.getChildren()) {
            if (isSkolem(attributeNode)) {
                return true;
            }
        }
        return false;
    }

    private List<Integer> containsSameSkolem(Object skolemValue, INode tupleNode) {
        List<Integer> result = new ArrayList<Integer>();
        for (int i = 0; i < tupleNode.getChildren().size(); i++) {
            INode attributeNode = tupleNode.getChild(i);
            Object value = attributeNode.getChild(0).getValue();
            if (isSkolem(attributeNode) && skolemValue.equals(value)) {
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

    private void findInstanceTuplePairs(List<INode> tuples, int firstTuplePosition, int firstSkolemPosition, INode currentAttributeNode, List<InstanceTuplePair> tuplePairs) {
        INode firstTuple = tuples.get(firstTuplePosition);
        Object skolemValue = currentAttributeNode.getChild(0).getValue();
        for (int i = firstTuplePosition + 1; i < tuples.size(); i++) {
            INode secondTuple = tuples.get(i);
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
        INode firstSpicyTupleNode = spicyTuplePair.getFirstTuple();
        INode secondSpicyTupleNode = spicyTuplePair.getSecondTuple();
        IExpectedTuple firstExpectedTuple = expectedTuplePair.getFirstTuple();
        IExpectedTuple secondExpectedTuple = expectedTuplePair.getSecondTuple();
        if ((matches(firstSpicyTupleNode, firstExpectedTuple) && matches(secondSpicyTupleNode, secondExpectedTuple))
                || (matches(secondSpicyTupleNode, firstExpectedTuple) && matches(firstSpicyTupleNode, secondExpectedTuple))) {
            return true;
        }
        return false;
    }

    protected boolean checkNullOrSkolem(Object instanceValue) {
//        return (instanceValue instanceof INullValue) || (instanceValue instanceof OID);
        return (instanceValue instanceof NullValue);
    }

    protected boolean isSkolem(INode attributeNode) {
        Object instanceValue = attributeNode.getChild(0).getValue();
//        if (instanceValue instanceof OID) {
//            return true;
//        }
        if (instanceValue instanceof NullValue) {
            if (instanceValue.toString().equals("NULL")) {
                return false;
            }
            return true;
        }
        return false;
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

        private INode firstTuple;
        private INode secondTuple;
        private int firstSkolemPosition;
        private int secondSkolemPosition;

        public InstanceTuplePair(INode firstTuple, INode secondTuple, int firstSkolemPosition, int secondSkolemPosition) {
            this.firstTuple = firstTuple;
            this.secondTuple = secondTuple;
            this.firstSkolemPosition = firstSkolemPosition;
            this.secondSkolemPosition = secondSkolemPosition;
        }

        public int getFirstSkolemPosition() {
            return firstSkolemPosition;
        }

        public INode getFirstTuple() {
            return firstTuple;
        }

        public int getSecondSkolemPosition() {
            return secondSkolemPosition;
        }

        public INode getSecondTuple() {
            return secondTuple;
        }

        @Override
        public String toString() {
            return "[P=" + firstSkolemPosition + "]" + firstTuple + " - " + "[P=" + secondSkolemPosition + "]" + secondTuple;
        }
    }
}