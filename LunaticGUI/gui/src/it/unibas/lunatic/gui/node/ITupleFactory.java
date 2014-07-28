package it.unibas.lunatic.gui.node;

import org.openide.nodes.Node;

public interface ITupleFactory {

    void interrupt();

    public Node createTuples();
}
