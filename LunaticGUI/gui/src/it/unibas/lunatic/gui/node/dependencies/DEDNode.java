
package it.unibas.lunatic.gui.node.dependencies;

import it.unibas.lunatic.model.dependency.DED;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;

public class DEDNode extends AbstractNode {

    public DEDNode(DED ded, String name) {
        super(Children.create(new DepListChildFactory(ded.getAssociatedDependencies()), true));
        setDisplayName(name);
    }

}
