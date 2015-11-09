package it.unibas.lunatic.gui.node.chase.mc;

import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.gui.R;
import it.unibas.lunatic.core.StepCellGroups;
import it.unibas.lunatic.gui.node.dependencies.StepDepRootNode;
import it.unibas.lunatic.model.chase.chasemc.DeltaChaseStep;
import java.io.IOException;
import java.lang.ref.WeakReference;
import javax.swing.Action;
import org.openide.awt.Actions;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.nodes.Sheet;

public class ChaseStepNode extends AbstractNode implements IChaseTreeNode {

    public static final String PROP_INVALID_STEP = "invalid";
    public static final String PROP_DUPLICATE_STEP = "duplicate";
    public static final String PROP_LEAF_STEP = "leaf";
    public static final String PROP_INTERMEDIATE_STEP = "intermediate";
    public static final String PROP_SOLUTION_STEP = "solution";
    public static final String PROP_GROUND_SOLUTION_STEP = "groundSolution";
    public static final String PROP_EDITED_BY_USER = "user";
    public static final String PROP_SCORE = "score";
    public static final String PROP_CHANGED_CELLS = "changedCells";
    public static final String PROP_LLUN_CELL_GROUPS = "llunCellGroups";
    private ChaseTreeSupport treeSupport = ChaseTreeSupport.getInstance();
    private DeltaChaseStep chaseStep;
    private WeakReference<StepCellGroups> cellGroups;
    private Children oldChildren;
    private Scenario scenario;
    private ChaseStepPropertySheetGenerator propertySheetGenerator = new ChaseStepPropertySheetGenerator();
    private StepDepRootNode stepDependenciesNode;

    public ChaseStepNode(DeltaChaseStep key, Scenario scenario) {
        this(key, scenario, Children.LEAF);
        this.setChildren(Children.create(new ChaseStepChildFactory(this, key, scenario), false));
    }

    protected ChaseStepNode(DeltaChaseStep key, Scenario scenario, Children children) {
        super(children);
        this.scenario = scenario;
        this.chaseStep = key;
        setName(treeSupport.createChildName(key));
        setDisplayName(key.getLocalId());
        updateIcon(key, false);
        if (chaseStep.isInvalid()) {
            oldChildren = getChildren();
            setChildren(Children.LEAF);
        }
    }

    public StepCellGroups getCellGroups() {
        assert cellGroups != null;
        assert cellGroups.get() != null;
        return cellGroups.get();
    }

    public DeltaChaseStep getChaseStep() {
        assert chaseStep != null;
        return chaseStep;
    }

    public void refreshCellGroups() {
        cellGroups = null;
    }

    public void cacheCellGroups(StepCellGroups stepCellGroups) {
        cellGroups = new WeakReference<StepCellGroups>(stepCellGroups);
    }

    public boolean hasCellGroupsLoaded() {
        return cellGroups != null && cellGroups.get() != null;
    }

    @Override
    public Action[] getActions(boolean context) {
        if (chaseStep.isEditedByUser()) {
            return userAction;
        }
        return defaultActions;
    }

    public void setInvalid() {
        boolean invalid = chaseStep.isInvalid();
        this.chaseStep.setInvalid(true);
        oldChildren = getChildren();
        setChildren(Children.LEAF);
        updateIcon(chaseStep, true);
        firePropertyChange(PROP_INVALID_STEP, invalid, true);
    }

    public void setValid() {
        boolean invalid = chaseStep.isInvalid();
        this.chaseStep.setInvalid(false);
        if (oldChildren != null) {
            setChildren(oldChildren);
        }
        updateIcon(chaseStep, true);
        firePropertyChange(PROP_INVALID_STEP, invalid, false);
    }

    private void updateIcon(DeltaChaseStep key, boolean fireChange) {
        if (key.isGround() && key.isSolution()) {
            setIconBaseWithExtension("it/unibas/lunatic/icons/cs-solution-ground.png");
        } else if (key.isSolution()) {
            setIconBaseWithExtension("it/unibas/lunatic/icons/cs-solution.png");
        } else if (key.isEditedByUser()) {
            setIconBaseWithExtension("it/unibas/lunatic/icons/cs-user.png");
        } else {
            setIconBaseWithExtension("it/unibas/lunatic/icons/cs-intermediate.png");
        }
        if (key.isInvalid()) {
            setIconBaseWithExtension("it/unibas/lunatic/icons/cs-invalid.png");
        } else if (key.isDuplicate()) {
            setIconBaseWithExtension("it/unibas/lunatic/icons/cs-duplicate.png");
        }
        if (fireChange) {
            fireIconChange();
        }
    }

    public boolean delete() throws IOException {
        if (canDestroy()) {
            super.destroy();
            return true;
        }
        return false;
    }

    @Override
    public boolean canDestroy() {
        return chaseStep.isEditedByUser();
    }

    public void add(Node chaseStepNode) {
        super.getChildren().add(new Node[]{chaseStepNode});
    }

    public void addUserNode(DeltaChaseStep userChaseStep) {
        add(new ChaseStepNode(userChaseStep, scenario));
    }

    public Scenario getScenario() {
        return scenario;
    }
    private Action[] defaultActions = new Action[]{
        Actions.forID("Window", R.ActionId.SHOW_STEP_CELL_GROUPS_ALL),
        Actions.forID("Window", R.ActionId.SHOW_STEP_DEPS),
        null,
        Actions.forID("Run", R.ActionId.ENABLE_CHASE_STEP),
        Actions.forID("Run", R.ActionId.INVALID_CHASE_STEP),
        null,
        Actions.forID("Run", R.ActionId.CREATE_USER_NODE),
        null,
        Actions.forID("File", R.ActionId.SAVE_CHASE_STEP)
    };
    private Action[] userAction = new Action[]{
        Actions.forID("Window", R.ActionId.EDIT_STEP_CELL_GROUPS), //null,
    //Actions.forID("Run", R.ActionId.DELETE_USER_NODE)
    };

    @Override
    protected Sheet createSheet() {
        Sheet sheet = super.createSheet();
        propertySheetGenerator.populateSheet(sheet, this);
        return sheet;
    }

    @Override
    public void accept(IChaseTreeVisitor visitor) {
        if (chaseStep.isLeaf()) {
            visitor.visitLeaf(this);
        } else {
            visitor.visitIntermediateNode(this);
        }
    }

    public StepDepRootNode getStepDependenciesNode() {
        if (stepDependenciesNode == null) {
            stepDependenciesNode = new StepDepRootNode(this);
        }
        return stepDependenciesNode;
    }

    public void setStepDependenciesNode(StepDepRootNode stepDependenciesNode) {
        this.stepDependenciesNode = stepDependenciesNode;
    }

    public boolean hasBeenEvaluated() {
        return !chaseStep.isLeaf();
    }

}
