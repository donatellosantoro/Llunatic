package it.unibas.lunatic.gui.node;

import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.gui.node.chase.mc.ChaseStepNode;
import it.unibas.lunatic.model.chase.chasemc.DeltaChaseStep;
import speedy.model.database.IDatabase;

import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;

public class DbNode extends AbstractNode implements IChaseNode {

    private IDatabase db;
    private DeltaChaseStep chaseStep;
    private ChaseStepNode chaseStepNode;
    private Scenario scenario;

    public DbNode(Scenario scenario, IDatabase db, DeltaChaseStep chaseStep, ChaseStepNode chaseStepNode, boolean showId) {
        this(scenario, db, chaseStep.getId(), Children.create(new DbChildFactory(db, chaseStepNode, chaseStep, scenario), true), showId);
        this.chaseStep = chaseStep;
        this.chaseStepNode = chaseStepNode;
    }

    public DbNode(Scenario scenario, IDatabase db, String qualifier, boolean showId) {
        this(scenario, db, qualifier, Children.create(new DbChildFactory(db, scenario), true), showId);
    }

    public DbNode(Scenario scenario, IDatabase db, String qualifier) {
        this(scenario, db, qualifier, true);
    }

    protected DbNode(Scenario scenario, IDatabase db, String qualifier, Children c, boolean showId) {
        super(c);
        this.db = db;
        this.scenario = scenario;
        if (!qualifier.equals("")) {
            qualifier = qualifier.concat(":");
        }
        String name = getTargetDbName(scenario, db);
        setName(qualifier.concat(name));
        if (!showId) {
            setDisplayName(name);
        }
        this.setIconBaseWithExtension("it/unibas/lunatic/icons/database.gif");
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

    public boolean hasChaseStepNode() {
        return chaseStepNode != null;
    }

    public ChaseStepNode getChaseStepNode() {
        return chaseStepNode;
    }

    public IDatabase getDb() {
        assert db != null;
        return db;
    }

    public Scenario getScenario() {
        return scenario;
    }

    public static String getTargetDbName(Scenario scenario, IDatabase db) {
        if (db.getName().equalsIgnoreCase("virtualtarget")) {
            return scenario.getTarget().getName();
        }
        return db.getName();
    }
}
