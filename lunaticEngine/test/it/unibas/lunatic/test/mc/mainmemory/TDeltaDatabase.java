package it.unibas.lunatic.test.mc.mainmemory;

import it.unibas.lunatic.LunaticConstants;
import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.model.algebra.operators.mainmemory.InsertTuple;
import it.unibas.lunatic.model.chase.chasemc.operators.mainmemory.BuildMainMemoryDBForChaseStep;
import it.unibas.lunatic.model.chase.chasemc.operators.mainmemory.BuildMainMemoryDeltaDB;
import it.unibas.lunatic.model.database.AttributeRef;
import it.unibas.lunatic.model.database.Cell;
import it.unibas.lunatic.model.database.IValue;
import it.unibas.lunatic.model.database.Tuple;
import it.unibas.lunatic.model.database.TupleOID;
import it.unibas.lunatic.model.database.mainmemory.MainMemoryDB;
import it.unibas.lunatic.model.database.mainmemory.MainMemoryTable;
import it.unibas.lunatic.model.database.mainmemory.datasource.IntegerOIDGenerator;
import it.unibas.lunatic.model.database.mainmemory.datasource.OID;
import it.unibas.lunatic.model.database.ConstantValue;
import it.unibas.lunatic.test.References;
import it.unibas.lunatic.test.UtilityTest;
import it.unibas.lunatic.test.checker.CheckTest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TDeltaDatabase extends CheckTest {

    private static Logger logger = LoggerFactory.getLogger(TDeltaDatabase.class);

    private InsertTuple insertOperator = new InsertTuple();

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
        tuple.addCell(new Cell(oid, new AttributeRef(tableName, LunaticConstants.TID), new ConstantValue(tid)));
        tuple.addCell(new Cell(oid, new AttributeRef(tableName, LunaticConstants.STEP), new ConstantValue(stepId)));
        tuple.addCell(new Cell(oid, new AttributeRef(tableName, attributeName), newValue));
        return tuple;
    }

}
