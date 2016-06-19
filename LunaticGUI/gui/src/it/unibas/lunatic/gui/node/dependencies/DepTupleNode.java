package it.unibas.lunatic.gui.node.dependencies;

import it.unibas.lunatic.gui.R;
import it.unibas.lunatic.gui.node.utils.StringProperty;
import it.unibas.lunatic.gui.visualdeps.DependencyGraph;
import it.unibas.lunatic.model.dependency.Dependency;
import it.unibas.lunatic.model.dependency.operators.DependencyToString;
import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationTargetException;
import javax.swing.Action;
import org.openide.awt.Actions;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import static org.openide.nodes.Node.PROP_NAME;
import org.openide.nodes.Sheet;
import org.openide.util.NbBundle;

@NbBundle.Messages({
    "PROP_DependencyType=Dependency type",
    "PROP_DependencyId=id"
})
public class DepTupleNode extends AbstractNode {

    private Action[] actions = new Action[]{Actions.forID("Window", R.ActionId.SHOW_DEP_VISUAL)};
    private Dependency dependency;
    private DependencyToString formatter = new DependencyToString();
    private WeakReference<DependencyGraph> dependencyGraphRef;

    public DepTupleNode(Dependency dep) {
        super(Children.LEAF);
        this.dependency = dep;
        setName(dep.getId());
        this.setDisplayName(dependency.getId().concat(":").concat(formatter.toLogicalString(dependency, "", true)));
    }

    public Dependency getDependency() {
        return dependency;
    }

    @Override
    public Action[] getActions(boolean context) {
        return actions;
    }

    @Override
    public Action getPreferredAction() {
        return actions[0];
    }

    public DependencyGraph getDependencyGraph() {
        if (dependencyGraphRef != null) {
            return dependencyGraphRef.get();
        }
        return null;
    }

    public void cacheDependencyGraph(DependencyGraph dependencyGraph) {
        this.dependencyGraphRef = new WeakReference<DependencyGraph>(dependencyGraph);
    }

    @Override
    protected Sheet createSheet() {
        Sheet sheet = super.createSheet();
        Sheet.Set set = Sheet.createPropertiesSet();
        set.put(new StringProperty(Bundle.PROP_DependencyType()) {
            @Override
            public String getValue() throws IllegalAccessException, InvocationTargetException {
                return dependency.getType();
            }
        });
        set.put(new StringProperty(Bundle.PROP_DependencyId()) {
            @Override
            public String getValue() throws IllegalAccessException, InvocationTargetException {
                return dependency.getId();
            }
        });
        sheet.put(set);
        return sheet;
    }
}
