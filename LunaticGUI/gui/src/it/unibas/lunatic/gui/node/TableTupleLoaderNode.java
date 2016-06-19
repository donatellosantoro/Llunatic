package it.unibas.lunatic.gui.node;

import it.unibas.lunatic.gui.node.utils.StringProperty;
import it.unibas.lunatic.model.chase.chasemc.DeltaChaseStep;
import speedy.model.database.Cell;
import speedy.model.database.IDatabase;
import speedy.model.database.ITable;
import speedy.model.database.Tuple;
import speedy.model.database.operators.lazyloading.ITupleLoader;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Sheet;

public class TableTupleLoaderNode extends AbstractNode implements IChaseNode {

    private static Log logger = LogFactory.getLog(TableTupleLoaderNode.class);
    private ITupleLoader tupleLoader;
    private Tuple tuple;
    private ITable table;
    private IDatabase db;
    private DeltaChaseStep chaseStep;

    public TableTupleLoaderNode(ITupleLoader tupleLoader, ITable table, IDatabase db, DeltaChaseStep chaseStep) {
        this(tupleLoader, table, db);
        this.chaseStep = chaseStep;
    }

    public TableTupleLoaderNode(ITupleLoader tupleLoader, ITable table, IDatabase db) {
        super(Children.LEAF);
        this.tupleLoader = tupleLoader;
        this.table = table;
        this.db = db;
        setName(tupleLoader.getOid().toString());
    }

    public ITable getTable() {
        assert table != null;
        return table;
    }

    public IDatabase getDb() {
        assert db != null;
        return db;
    }

    @Override
    public DeltaChaseStep getChaseStep() {
        assert chaseStep != null;
        return chaseStep;
    }

    @Override
    public boolean isMcResultNode() {
        return chaseStep != null;
    }

    @Override
    protected Sheet createSheet() {
        logger.trace("Create tuple data: " + getName());
        Sheet sheet = Sheet.createDefault();
        Sheet.Set set = Sheet.createPropertiesSet();
        sheet.put(set);
        List<Cell> tupleCells = getTuple().getCells();
        for (final Cell cell : tupleCells) {
            StringProperty property = new StringProperty(cell.getAttribute()) {
                @Override
                public String getValue() throws IllegalAccessException, InvocationTargetException {
                    return cell.getValue().toString();
                }

                //TODO: implement inplace editor
                @Override
                public boolean canWrite() {
                    return true;
                }
            };
            set.put(property);
        }
        return sheet;
    }

    public Cell getCell(String columnName) {
        for (Cell c : getTuple().getCells()) {
            if (c.getAttribute().equals(columnName)) {
                return c;
            }
        }
        throw new IllegalArgumentException("Unable to find cell for attribute " + columnName + " in " + tuple.toStringWithAlias());
    }

    private Tuple getTuple() {
        if (tuple == null) {
            tuple = tupleLoader.loadTuple();
            logger.trace("Tuple loaded: " + tuple);
        }
        assert tuple!=null;
        return tuple;
    }
}
