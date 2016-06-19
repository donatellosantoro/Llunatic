package it.unibas.lunatic.gui.node.dependencies;

import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.model.dependency.DED;
import it.unibas.lunatic.model.dependency.Dependency;
import java.util.ArrayList;
import java.util.List;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Node;

public class DepRootChildFactory extends ChildFactory<Scenario> {
    
    private Scenario scenario;
    
    public DepRootChildFactory(Scenario s) {
        this.scenario = s;
    }
    
    @Override
    protected boolean createKeys(List<Scenario> toPopulate) {
        toPopulate.add(scenario);
        return true;
    }
    
    @Override
    protected Node[] createNodesForKey(Scenario key) {
        ArrayList<Node> nodes = new ArrayList<Node>(6);
        addDEDNode(key.getDEDEGDs(), nodes,"DEDEGDs");
        addDEDNode(key.getDEDextTGDs(), nodes, "DEDextTGDs");
//        addDEDNode(key.getDEDstTGDs(), nodes, "DEDstTGDs");
        addNode(key.getDCs(), nodes, "DTGDs");
        addNode(key.getEGDs(), nodes, "EGDs");
        addNode(key.getExtEGDs(), nodes, "ExtEGDs");
        addNode(key.getExtTGDs(), nodes, "ExtTGDs");
        addNode(key.getSTTgds(), nodes, "STTGDs");
        Node[] result = new Node[nodes.size()];
        return nodes.toArray(result);
    }
    
    private void addDEDNode(List<DED> dedList, ArrayList<Node> nodes, String name) {
        for (DED ded : dedList) {
            if (!ded.getAssociatedDependencies().isEmpty()) {
                nodes.add(new DEDNode(ded,name));
            }
        }
    }
    
    private void addNode(List<Dependency> depList, ArrayList<Node> nodes, String name) {
        if (!depList.isEmpty()) {
            nodes.add(new DepListNode(depList,name));
        }
    }
}
