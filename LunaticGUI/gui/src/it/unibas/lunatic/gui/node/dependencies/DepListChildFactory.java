
package it.unibas.lunatic.gui.node.dependencies;

import it.unibas.lunatic.model.dependency.Dependency;
import java.util.List;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Node;

class DepListChildFactory extends ChildFactory<Dependency> {
    
    private final List<Dependency> depList;

    public DepListChildFactory(List<Dependency> depList) {
        this.depList = depList;
    }

    @Override
    protected boolean createKeys(List<Dependency> toPopulate) {
        toPopulate.addAll(depList);
        return true;
    }

    @Override
    protected Node createNodeForKey(Dependency key) {
        return new DepTupleNode(key);
    }
    
    

}
