package it.unibas.lunatic.test.mc.mainmemory;

import it.unibas.lunatic.LunaticConstants;
import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.model.chase.chasemc.operators.mainmemory.BuildMainMemoryDBForChaseStep;
import it.unibas.lunatic.model.chase.chasemc.operators.mainmemory.BuildMainMemoryDeltaDB;
import it.unibas.lunatic.test.References;
import it.unibas.lunatic.test.UtilityTest;
import it.unibas.lunatic.test.checker.CheckTest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import speedy.SpeedyConstants;
import speedy.model.algebra.operators.IInsertTuple;
import speedy.model.algebra.operators.mainmemory.MainMemoryInsertTuple;
import speedy.model.database.AttributeRef;
import speedy.model.database.Cell;
import speedy.model.database.ConstantValue;
import speedy.model.database.IValue;
import speedy.model.database.Tuple;
import speedy.model.database.TupleOID;
import speedy.model.database.mainmemory.MainMemoryDB;
import speedy.model.database.mainmemory.MainMemoryTable;
import speedy.model.database.mainmemory.datasource.IntegerOIDGenerator;
import speedy.model.database.mainmemory.datasource.OID;

public class TDeltaDatabase extends CheckTest {

    private static Logger logger = LoggerFactory.getLogger(TDeltaDatabase.class);

    private IInsertTuple insertOperator = new MainMemoryInsertTuple();

    public void testScenario() throws Exception {
        Scenario scenario = UtilityTest.loadScenarioFromResources(References.persons);
        MainMemoryDB deltaDB = new BuildMainMemoryDeltaDB().generate((MainMemoryDB) scenario.getTarget(), scenario, LunaticConstants.CHASE_STEP_ROOT);
        insertOperator.execute((MainMemoryTable) deltaDB.getTable("person_name"), buildTuple(new TupleOID(new OID(10)), "r.f", new ConstantValue("JACK"), "person_name", "name"), scenario.getSource(), scenario.getTarget());
        insertOperator.execute((MainMemoryTable) deltaDB.getTable("person_name"), buildTuple(new TupleOID(new OID(10)), "r.f.f", new ConstantValue("JOE"), "person_name", "name"), scenario.getSource(), scenario.getTarget());
        insertOperator.execute((MainMemoryTable) deltaDB.getTable("person_ssn"), buildTuple(new TupleOID(new OID(10)), "r.f.f", new ConstantValue("321"), "person_ssn", "ssn"), scenario.getSource(), scenario.getTarget());
        if (logger.isDebugEnabled()) logger.debug(deltaDB.toString());
        new BuildMainMemoryDBForChaseStep().extractDatabase("r.f", deltaDB, (MainMemoryDB) scenario.getTarget());
    }

    private Tuple buildTuple(TupleOID tid, String stepId, IValue newValue, String tableName, String attributeName) {
        TupleOID oid = new TupleOID(IntegerOIDGenerator.getNextOID());
        Tuple tuple = new Tuple(oid);
        tuple.addCell(new Cell(oid, new AttributeRef(tableName, SpeedyConstants.TID), new ConstantValue(tid)));
        tuple.addCell(new Cell(oid, new AttributeRef(tableName, SpeedyConstants.STEP), new ConstantValue(stepId)));
        tuple.addCell(new Cell(oid, new AttributeRef(tableName, attributeName), newValue));
        return tuple;
    }

}
