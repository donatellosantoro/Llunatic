package it.unibas.lunatic.gui.node.chase.mc;

/**
 *
 * @author Tony
 */
public interface IChaseTreeNode {

    void accept(IChaseTreeVisitor visitor);
}
