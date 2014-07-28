package it.unibas.lunatic.gui.node.scenario;

import it.unibas.lunatic.gui.model.LoadedScenario;
import it.unibas.lunatic.model.chase.chasemc.costmanager.ICostManager;
import java.lang.reflect.InvocationTargetException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openide.nodes.PropertySupport;
import org.openide.nodes.Sheet;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;

@NbBundle.Messages({
    "PSET_costManagerProperties=Cost manager properties",
    "PROP_chaseBackward=Chase backward",
    "PROP_permutationsAllowed=Permutations allowed",
    "PROP_dependencyLimit=Dependency limit",
    "PROP_chaseTreeSizeThreshold=Chase tree size threshold",
    "VAL_noLimit=none"
})
public class CostManagerSheetSetGenerator {

    private Log logger = LogFactory.getLog(getClass());

    public Sheet.Set createSheetSet(String setName, LoadedScenario loadedScenario) {
        Sheet.Set set = new Sheet.Set();
        set.setName(setName);
        set.setDisplayName(Bundle.PSET_costManagerProperties());
        final ICostManager costManager = loadedScenario.getScenario().getCostManager();
        set.put(new PropertySupport.ReadWrite<Boolean>(ScenarioNode.CHASE_BACKWARD, Boolean.class, Bundle.PROP_chaseBackward(), Bundle.PROP_chaseBackward()) {
            @Override
            public Boolean getValue() throws IllegalAccessException, InvocationTargetException {
                return costManager.isDoBackward();
            }

            @Override
            public void setValue(Boolean val) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
                costManager.setDoBackward(val);
            }

        });
        set.put(new PropertySupport.ReadWrite<Boolean>(ScenarioNode.PERMUTATION_ALLOWED, Boolean.class, Bundle.PROP_permutationsAllowed(), Bundle.PROP_permutationsAllowed()) {
            @Override
            public Boolean getValue() throws IllegalAccessException, InvocationTargetException {
                return costManager.isDoPermutations();
            }

            @Override
            public void setValue(Boolean val) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
                costManager.setDoPermutations(val);
            }
        });
        set.put(new PropertySupport.ReadWrite<Integer>(ScenarioNode.DEPENDENCY_LIMIT, Integer.class, Bundle.PROP_dependencyLimit(), Bundle.PROP_dependencyLimit()) {
            @Override
            public Integer getValue() throws IllegalAccessException, InvocationTargetException {
                return costManager.getDependencyLimit();
            }

            @Override
            public void setValue(Integer val) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
                costManager.setDependencyLimit(val);
            }
        });
        set.put(new PropertySupport.ReadWrite<Integer>(ScenarioNode.CHASE_TREE_TRESHOLD, Integer.class, Bundle.PROP_chaseTreeSizeThreshold(), Bundle.PROP_chaseTreeSizeThreshold()) {
            @Override
            public Integer getValue() throws IllegalAccessException, InvocationTargetException {
                return costManager.getPotentialSolutionsThreshold();
            }

            @Override
            public void setValue(Integer val) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
                costManager.setPotentialSolutionsThreshold(val);
            }
        });
        return set;
    }
}
