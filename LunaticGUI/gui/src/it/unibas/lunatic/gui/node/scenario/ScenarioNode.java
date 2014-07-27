/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unibas.lunatic.gui.node.scenario;

import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.gui.R;
import it.unibas.lunatic.gui.model.LoadedScenario;
import it.unibas.lunatic.gui.node.dependencies.DepRootNode;
import java.awt.Image;
import javax.swing.Action;
import org.openide.awt.Actions;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.nodes.Sheet;

/**
 *
 * @author Antonio Galotta
 */
public class ScenarioNode extends AbstractNode {

    public static final String USER_MANAGER = "userManager";
    public static final String COST_MANAGER = "costManager";
    public static final String ITERATION_LIMIT = "IterationLimit";
    public static final String CHASE_BACKWARD = "chaseBackward";
    public static final String PERMUTATION_ALLOWED = "permutationAllowed";
    public static final String DEPENDENCY_LIMIT = "dependencyLimit";
    public static final String CHASE_TREE_TRESHOLD = "chaseTreeThreshold";
    public static final String USE_LIMIT = "UseLimit";
    public static final String REMOVE_DUPLICATES = "RemoveDuplicates";
    public static final String USE_CELL_GROUPS_FOR_TG_DS = "UseCellGroupsForTGDs";
    public static final String CHECK_ALL_NODES_FOR_EGD_SATISFACTION = "CheckAllNodesForEGDSatisfaction";
    public static final String REMOVE_SUSPICIOUS_SOLUTIONS = "RemoveSuspiciousSolutions";
    public static final String CHECK_SOLUTIONS_QUERY = "CheckSolutionsQuery";
    public static final String CHECK_SOLUTIONS = "CheckSolutions";
    public static final String CHECK_GROUND_SOLUTIONS = "CheckGroundSolutions";
    public static final String DEBUG_MODE = "DebugMode";
    //private
    private String PROP_SET_COST_MANAGER = COST_MANAGER;
    private final LoadedScenario loadedScenario;
    private final Node dataNode;
    private ConfigurationSheetSetGenerator configuration = new ConfigurationSheetSetGenerator();
    private CostManagerSheetSetGenerator costManager = new CostManagerSheetSetGenerator();
    private Action[] actions = new Action[]{
        Actions.forID("Run", R.ActionId.RUN_CHASE),
        Actions.forID("System", "org.openide.actions.OpenAction"),
        null,
        Actions.forID("Window", R.ActionId.SHOW_DEPS),
        Actions.forID("Window", R.ActionId.SHOW_ORDERING_ATTRIBUTES),
        Actions.forID("System", "org.openide.actions.PropertiesAction"),
        null,
        Actions.forID("File", R.ActionId.CLOSE_SCENARIO_FROM_NODE)};

    public ScenarioNode(LoadedScenario ls) {
        super(Children.create(new ScenarioChildFactory(ls), false), ls.getDataObject().getLookup());
        this.loadedScenario = ls;
        this.dataNode = ls.getDataObject().getNodeDelegate();
        this.setDisplayName(ls.getDataObject().getName());
    }

    @Override
    protected Sheet createSheet() {
        Sheet sheet = super.createSheet();
        Sheet.Set configSet = configuration.createSheetSet(this);
        Sheet.Set costManagerSet = costManager.createSheetSet(PROP_SET_COST_MANAGER, loadedScenario);
        sheet.put(configSet);
        sheet.put(costManagerSet);
        return sheet;
    }

    @Override
    public Image getIcon(int type) {
        return dataNode.getIcon(type);
    }

    @Override
    public Image getOpenedIcon(int type) {
        return dataNode.getOpenedIcon(type);
    }

    @Override
    public Action[] getActions(boolean context) {
        return actions;
    }

    public LoadedScenario getLoadedScenario() {
        return loadedScenario;
    }

    public Scenario getScenario() {
        return loadedScenario.getScenario();
    }
    private DepRootNode dependenciesNode;

    public DepRootNode getDependenciesNode() {
        if (dependenciesNode == null) {
            dependenciesNode = new DepRootNode(loadedScenario);
        }
        return dependenciesNode;
    }

    public void setDependenciesNode(DepRootNode dependenciesNode) {
        this.dependenciesNode = dependenciesNode;
    }

    public void updateCostManagerSet() {
        Sheet sheet = getSheet();
        Sheet.Set oldCostManagerSet = sheet.get(PROP_SET_COST_MANAGER);
        Sheet.Set newCostManagerSet = costManager.createSheetSet(PROP_SET_COST_MANAGER, loadedScenario);
        sheet.put(newCostManagerSet);
        firePropertySetsChange(new PropertySet[]{oldCostManagerSet}, new PropertySet[]{newCostManagerSet});
    }
}
