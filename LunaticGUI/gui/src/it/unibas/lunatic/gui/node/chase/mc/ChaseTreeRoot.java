package it.unibas.lunatic.gui.node.chase.mc;

import it.unibas.lunatic.gui.model.McChaseResult;
import it.unibas.lunatic.gui.node.utils.StringProperty;
import it.unibas.lunatic.model.chase.chasemc.DeltaChaseStep;
import it.unibas.lunatic.model.chase.chasemc.operators.ChaseTreeSize;
import java.lang.reflect.InvocationTargetException;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Sheet;
import org.openide.util.NbBundle;

public class ChaseTreeRoot extends AbstractNode {

    private ChaseTreeSize chaseTreeSize = new ChaseTreeSize();
    private McChaseResult result;

    @NbBundle.Messages({
        "PROP_AllNodes=Number of nodes",
        "PROP_AllLeaves=Number of leaves",
        "PROP_Solutions=Solutions",
        "PROP_GroundSolutions=Ground solutions",
        "PROP_Duplicates=Duplicates",
        "PROP_Invalid=Invalids"
    })
    public ChaseTreeRoot(McChaseResult chaseResult) {
        super(Children.create(new ChaseStepChildFactory(chaseResult.getResult().getRoot(), chaseResult.getLoadedScenario().getScenario()), false));
        this.result = chaseResult;
        setDisplayName("Chase tree (" + result.getLoadedScenario().getDataObject().getName() + ")");
        setIconBaseWithExtension("it/unibas/lunatic/icons/chase_tree.png");
    }

    public McChaseResult getResult() {
        return result;
    }

    @Override
    protected Sheet createSheet() {
        Sheet sheet = super.createSheet();
        Sheet.Set resultSet = createSheetSet(result.getResult().getRoot());
        sheet.put(resultSet);
        return sheet;
    }

    private Sheet.Set createSheetSet(final DeltaChaseStep step) {
        Sheet.Set set = Sheet.createPropertiesSet();
        set.put(new StringProperty(Bundle.PROP_AllNodes()) {
            @Override
            public String getValue() throws IllegalAccessException, InvocationTargetException {
                return step.getNumberOfNodes() + "";
            }
        });
        set.put(new StringProperty(Bundle.PROP_AllLeaves()) {
            @Override
            public String getValue() throws IllegalAccessException, InvocationTargetException {
                return step.getNumberOfLeaves() + "";
            }
        });
        set.put(new StringProperty(Bundle.PROP_Solutions()) {
            @Override
            public String getValue() throws IllegalAccessException, InvocationTargetException {
                return chaseTreeSize.getSolutions(step) + "";
            }
        });
        set.put(new StringProperty(Bundle.PROP_GroundSolutions()) {
            @Override
            public String getValue() throws IllegalAccessException, InvocationTargetException {
                return chaseTreeSize.getGroundSolutions(step) + "";
            }
        });
        set.put(new StringProperty(Bundle.PROP_Duplicates()) {
            @Override
            public String getValue() throws IllegalAccessException, InvocationTargetException {
                return chaseTreeSize.getDuplicates(step) + "";
            }
        });
        set.put(new StringProperty(Bundle.PROP_Invalid()) {
            @Override
            public String getValue() throws IllegalAccessException, InvocationTargetException {
                return chaseTreeSize.getInvalids(step) + "";
            }
        });
        return set;
    }
}
