/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unibas.lunatic.gui.node.chase.mc;

/**
 *
 * @author Tony
 */
public interface IChaseTreeNode {

    void accept(IChaseTreeVisitor visitor);
}
