package it.unibas.lunatic.gui.node.scenario;

import it.unibas.lunatic.gui.node.scenario.editor.UserManagerEditor;
import it.unibas.lunatic.LunaticConfiguration;
import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.gui.model.LoadedScenario;
import it.unibas.lunatic.gui.node.scenario.editor.CostManagerEditor;
import it.unibas.lunatic.gui.node.utils.StringProperty;
import it.unibas.lunatic.model.chase.chasemc.costmanager.ICostManager;
import it.unibas.lunatic.model.chase.chasemc.partialorder.IPartialOrder;
import it.unibas.lunatic.model.chase.chasemc.usermanager.IUserManager;
import java.beans.PropertyEditor;
import java.lang.reflect.InvocationTargetException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openide.nodes.PropertySupport;
import org.openide.nodes.Sheet;
import org.openide.util.NbBundle;

@NbBundle.Messages({
    "PROP_partialOrder=Partial order",
    "PROP_costManager=Cost manager",
    "PROP_userManager=User manager",
    "PROP_dependencies=N. of dependencies",
    "PROP_removeDuplicates=Remove duplicates",
    "PROP_iterationLimit=Iteration limit",
    "PROP_noLimit=none",
    "PROP_debugMode=Debug mode",
    "PROP_checkGroundSolutions=Check ground solutions",
    "PROP_checkSolutions=Check solutions",
    "PROP_checkSolutionsQuery=Check solutions query",
    "PROP_removeSuspiciousSolutions=Remove suspicious solutions",
    "PROP_checkAllNodesForEGDSatisfaction=Check all nodes for EGD satisfaction",
    "PROP_useCellGroupsForTGDs=Use cell groups for TGDs",
    "PROP_useLimit=Use limit"
})
public class ConfigurationSheetSetGenerator {

    private Log logger = LogFactory.getLog(getClass());
    private final DepCounter depCounter = new DepCounter();

    public Sheet.Set createSheetSet(final ScenarioNode scenarioNode) {
        LoadedScenario loadedScenario = scenarioNode.getLoadedScenario();
        final Scenario scenario = loadedScenario.getScenario();
        Sheet.Set set = Sheet.createPropertiesSet();
        final IPartialOrder partialOrder = loadedScenario.getScenario().getPartialOrder();
        set.put(new StringProperty(Bundle.PROP_partialOrder()) {
            @Override
            public String getValue() throws IllegalAccessException, InvocationTargetException {
                return partialOrder.getClass().getSimpleName();
            }
        });
        set.put(new PropertySupport.ReadWrite<ICostManager>(ScenarioNode.COST_MANAGER, ICostManager.class, Bundle.PROP_costManager(), Bundle.PROP_costManager()) {
            @Override
            public PropertyEditor getPropertyEditor() {
                return new CostManagerEditor();
            }

            @Override
            public ICostManager getValue() throws IllegalAccessException, InvocationTargetException {
                return scenario.getCostManager();
            }

            @Override
            public void setValue(ICostManager val) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
                scenario.setCostManager(val);
                scenarioNode.updateCostManagerSet();
            }
        });
        set.put(new PropertySupport.ReadWrite<IUserManager>(ScenarioNode.USER_MANAGER, IUserManager.class, Bundle.PROP_userManager(), Bundle.PROP_userManager()) {
            @Override
            public IUserManager getValue() throws IllegalAccessException, InvocationTargetException {
                return scenario.getUserManager();
            }

            @Override
            public void setValue(IUserManager val) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
                logger.debug("Set user manager: " + val);
                scenario.setUserManager(val);
            }

            @Override
            public PropertyEditor getPropertyEditor() {
                return new UserManagerEditor(scenario);
            }
        });
        set.put(new StringProperty(Bundle.PROP_dependencies()) {
            @Override
            public String getValue() throws IllegalAccessException, InvocationTargetException {
                return depCounter.getNumber(scenario) + "";
            }
        });
        final LunaticConfiguration config = loadedScenario.getScenario().getConfiguration();
        set.put(new PropertySupport.ReadWrite<Boolean>(ScenarioNode.DEBUG_MODE, Boolean.class, Bundle.PROP_debugMode(), Bundle.PROP_debugMode()) {
            @Override
            public Boolean getValue() throws IllegalAccessException, InvocationTargetException {
                return config.isDebugMode();
            }

            @Override
            public void setValue(Boolean val) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
                config.setDebugMode(val);
            }
        });
        set.put(new PropertySupport.ReadWrite<Boolean>(ScenarioNode.CHECK_GROUND_SOLUTIONS, Boolean.class, Bundle.PROP_checkGroundSolutions(), Bundle.PROP_checkGroundSolutions()) {
            @Override
            public Boolean getValue() throws IllegalAccessException, InvocationTargetException {
                return config.isCheckGroundSolutions();
            }

            @Override
            public void setValue(Boolean val) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
                config.setCheckGroundSolutions(val);
            }
        });
        set.put(new PropertySupport.ReadWrite<Boolean>(ScenarioNode.CHECK_SOLUTIONS, Boolean.class, Bundle.PROP_checkSolutions(), Bundle.PROP_checkSolutions()) {
            @Override
            public Boolean getValue() throws IllegalAccessException, InvocationTargetException {
                return config.isCheckSolutions();
            }

            @Override
            public void setValue(Boolean val) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
                config.setCheckSolutions(val);
            }
        });
        set.put(new PropertySupport.ReadWrite<Boolean>(ScenarioNode.CHECK_SOLUTIONS_QUERY, Boolean.class, Bundle.PROP_checkSolutionsQuery(), Bundle.PROP_checkSolutionsQuery()) {
            @Override
            public Boolean getValue() throws IllegalAccessException, InvocationTargetException {
                return config.isCheckSolutionsQuery();
            }

            @Override
            public void setValue(Boolean val) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
                config.setCheckSolutionsQuery(val);
            }
        });
        set.put(new PropertySupport.ReadWrite<Boolean>(ScenarioNode.REMOVE_SUSPICIOUS_SOLUTIONS, Boolean.class, Bundle.PROP_removeSuspiciousSolutions(), Bundle.PROP_removeSuspiciousSolutions()) {
            @Override
            public Boolean getValue() throws IllegalAccessException, InvocationTargetException {
                return config.isRemoveSuspiciousSolutions();
            }

            @Override
            public void setValue(Boolean val) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
                config.setRemoveSuspiciousSolutions(val);
            }
        });
        set.put(new PropertySupport.ReadWrite<Boolean>(ScenarioNode.CHECK_ALL_NODES_FOR_EGD_SATISFACTION, Boolean.class, Bundle.PROP_checkAllNodesForEGDSatisfaction(), Bundle.PROP_checkAllNodesForEGDSatisfaction()) {
            @Override
            public Boolean getValue() throws IllegalAccessException, InvocationTargetException {
                return config.isCheckAllNodesForEGDSatisfaction();
            }

            @Override
            public void setValue(Boolean val) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
                config.setCheckAllNodesForEGDSatisfaction(val);
            }
        });
        set.put(new PropertySupport.ReadWrite<Boolean>(ScenarioNode.REMOVE_DUPLICATES, Boolean.class, Bundle.PROP_removeDuplicates(), Bundle.PROP_removeDuplicates()) {
            @Override
            public Boolean getValue() throws IllegalAccessException, InvocationTargetException {
                return config.isRemoveDuplicates();
            }

            @Override
            public void setValue(Boolean val) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
                config.setRemoveDuplicates(val);
            }
        });
        set.put(new PropertySupport.ReadWrite<Boolean>(ScenarioNode.USE_LIMIT, Boolean.class, Bundle.PROP_useLimit(), Bundle.PROP_useLimit()) {
            @Override
            public Boolean getValue() throws IllegalAccessException, InvocationTargetException {
                return config.isUseLimit1ForEGDs();
            }

            @Override
            public void setValue(Boolean val) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
                config.setUseLimit1ForEGDs(val);
            }
        });
        set.put(new PropertySupport.ReadWrite<Integer>(ScenarioNode.ITERATION_LIMIT, Integer.class, Bundle.PROP_iterationLimit(), Bundle.PROP_iterationLimit()) {
            @Override
            public Integer getValue() throws IllegalAccessException, InvocationTargetException {
                if (config.getIterationLimit() == null) {
                    return -1;
                }
                return config.getIterationLimit();
            }

            @Override
            public void setValue(Integer val) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
                config.setIterationLimit(val);
            }
        });
        return set;
    }
}
