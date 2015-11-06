package it.unibas.lunatic.gui.node.cellgroup;

import it.unibas.lunatic.model.chase.chasemc.CellGroupCell;
import java.util.List;
import java.util.Set;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Node;

class UserCellTupleFactory extends ChildFactory<CellGroupCell> {

    private final Set<CellGroupCell> userCells;
    private final StepCellGroupNode node;

    UserCellTupleFactory(StepCellGroupNode node, Set<CellGroupCell> userCells) {
        this.userCells = userCells;
        this.node = node;
    }

    @Override
    protected boolean createKeys(List<CellGroupCell> toPopulate) {
        for (CellGroupCell c : userCells) {
            if (Thread.interrupted()) {
                return false;
            }
            toPopulate.add(c);
        }
        return true;
    }

    @Override
    protected Node createNodeForKey(CellGroupCell key) {
        return new UserCellTupleNode(key, node);
    }
}
