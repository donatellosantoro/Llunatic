package it.unibas.lunatic.gui.node;

import speedy.SpeedyConstants;
import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.gui.R;
import it.unibas.lunatic.gui.node.chase.mc.ChaseStepNode;
import it.unibas.lunatic.model.chase.chasemc.DeltaChaseStep;
import speedy.model.database.Attribute;
import speedy.model.database.IDatabase;
import speedy.model.database.ITable;
import java.util.ArrayList;
import java.util.List;
import javax.swing.Action;
import org.openide.awt.Actions;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Sheet;

public class TableNode extends AbstractNode implements IChaseNode {

    private Scenario scenario;
    private IDatabase db;
    private ITable table;
    private DeltaChaseStep chaseStep;
    private ChaseStepNode chaseStepNode;
    private Action[] actions = new Action[]{Actions.forID("Window", R.ActionId.SHOW_TABLE)};
    private TableNodeSheetSetGenerator sheetGenerator = new TableNodeSheetSetGenerator();
    private List<String> columns;

    public TableNode(ITable table, IDatabase db, ChaseStepNode chaseStepNode, DeltaChaseStep cs, Scenario scenario) {
        this(scenario, db, table, Children.LEAF);
        this.chaseStep = cs;
        this.chaseStepNode = chaseStepNode;
    }

    public TableNode(ITable table, IDatabase db, Scenario scenario) {
        this(scenario, db, table, Children.LEAF);
    }

    public TableNode(Scenario scenario, IDatabase db, ITable table, Children children) {
        super(children);
        this.db = db;
        this.scenario = scenario;
        this.table = table;
        setName(table.getName());
        setDisplayName(table.getName());
        setIconBaseWithExtension("it/unibas/lunatic/icons/table.png");
    }

    public ITable getTable() {
        assert table != null;
        return table;
    }

    public IDatabase getDb() {
        assert db != null;
        return db;
    }

    public List<String> getVisibleColumns() {
        if (columns == null) {
            columns = new ArrayList<String>();
            List<Attribute> attributes = table.getAttributes();
            for (Attribute a : attributes) {
                if (!(a.getName().equals(SpeedyConstants.TID) || a.getName().equals(SpeedyConstants.OID))) {
                    columns.add(a.getName());
                }
            }
        }
        return columns;
    }

    @Override
    public Action[] getActions(boolean context) {
        return actions;
    }

    @Override
    public Action getPreferredAction() {
        return getActions(true)[0];
    }

    @Override
    public DeltaChaseStep getChaseStep() {
        return chaseStep;
    }

    @Override
    public boolean isMcResultNode() {
        return chaseStep != null;
    }

    public Scenario getScenario() {
        return scenario;
    }

    public String getDbDisplayName() {
        return DbNode.getTargetDbName(scenario, db);
    }

    @Override
    protected Sheet createSheet() {
        Sheet sheet = super.createSheet();
        sheet.put(sheetGenerator.createSheetSet(this));
        return sheet;
    }

    public boolean hasChaseStepNode() {
        return chaseStepNode != null;
    }

    public ChaseStepNode getChaseStepNode() {
        return chaseStepNode;
    }
}
