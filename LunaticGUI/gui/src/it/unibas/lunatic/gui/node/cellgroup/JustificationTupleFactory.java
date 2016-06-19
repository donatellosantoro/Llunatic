package it.unibas.lunatic.gui.node.cellgroup;

import it.unibas.lunatic.model.chase.chasemc.CellGroupCell;
import java.util.List;
import java.util.Set;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Node;

class JustificationTupleFactory extends ChildFactory<CellGroupCell> {

    private final Set<CellGroupCell> provenances;
    private final StepCellGroupNode node;

    JustificationTupleFactory(StepCellGroupNode node, Set<CellGroupCell> provenances) {
        this.provenances = provenances;
        this.node = node;
    }

    @Override
    protected boolean createKeys(List<CellGroupCell> toPopulate) {
        for (CellGroupCell c : provenances) {
            if (Thread.interrupted()) {
                return false;
            }
            toPopulate.add(c);
        }
        return true;
    }

    @Override
    protected Node createNodeForKey(CellGroupCell key) {
        return new JustificationTupleNode(key, node);
    }
}
