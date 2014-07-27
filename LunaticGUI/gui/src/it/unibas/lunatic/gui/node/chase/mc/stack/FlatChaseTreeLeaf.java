/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unibas.lunatic.gui.node.chase.mc.stack;

import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.gui.node.chase.mc.ChaseStepNode;
import it.unibas.lunatic.model.chasemc.DeltaChaseStep;

/**
 *
 * @author Antonio Galotta
 */
public class FlatChaseTreeLeaf extends ChaseStepNode {

    public FlatChaseTreeLeaf(DeltaChaseStep key, Scenario s) {
        super(key, s);
        setDisplayName(key.getId());
    }

}
