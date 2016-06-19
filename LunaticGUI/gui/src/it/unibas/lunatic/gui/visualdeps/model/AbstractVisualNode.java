package it.unibas.lunatic.gui.visualdeps.model;

import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;

public class AbstractVisualNode extends AbstractNode {

    public AbstractVisualNode(String id) {
        super(Children.LEAF);
        setName(id);
    }

    public AbstractVisualNode(String id, String name) {
        this(id);
        setDisplayName(name);
    }

    public String getId() {
        return getName();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        AbstractVisualNode other = (AbstractVisualNode) obj;
        return getName().equals(other.getName());
    }
}
