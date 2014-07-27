/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unibas.lunatic.gui.node;

import org.openide.nodes.Node;

/**
 *
 * @author Antonio Galotta
 */
public interface ITupleFactory {

    void interrupt();

    public Node createTuples();
}
