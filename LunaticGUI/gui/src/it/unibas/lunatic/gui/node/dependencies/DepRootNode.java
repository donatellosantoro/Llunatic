package it.unibas.lunatic.gui.node.dependencies;

import it.unibas.lunatic.gui.model.LoadedScenario;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.util.NbBundle;

@NbBundle.Messages("NODE_dependencies=Dependencies - ")
public class DepRootNode extends AbstractNode {

    public DepRootNode(LoadedScenario ls) {
        super(Children.create(new DepRootChildFactory(ls.getScenario()), true));
        setName(Bundle.NODE_dependencies().concat(ls.getDataObject().getName()));
    }
}
