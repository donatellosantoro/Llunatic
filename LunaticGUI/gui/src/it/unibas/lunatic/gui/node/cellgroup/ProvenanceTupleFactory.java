/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unibas.lunatic.gui.node.cellgroup;

import it.unibas.lunatic.model.database.Cell;
import java.util.List;
import java.util.Set;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Node;

/**
 *
 * @author Antonio Galotta
 */
class ProvenanceTupleFactory extends ChildFactory<Cell> {

    private final Set<Cell> provenances;
    private final StepCellGroupNode node;

    ProvenanceTupleFactory(StepCellGroupNode node, Set<Cell> provenances) {
        this.provenances = provenances;
        this.node = node;
    }

    @Override
    protected boolean createKeys(List<Cell> toPopulate) {
        for (Cell c : provenances) {
            if (Thread.interrupted()) {
                return false;
            }
            toPopulate.add(c);
        }
        return true;
    }

    @Override
    protected Node createNodeForKey(Cell key) {
        return new ProvenanceTupleNode(key, node);
    }
}
