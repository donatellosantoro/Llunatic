/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unibas.lunatic.gui.node;

import it.unibas.lunatic.model.chasemc.DeltaChaseStep;

/**
 *
 * @author Antonio Galotta
 */
interface IChaseNode {

    DeltaChaseStep getChaseStep();

    boolean isMcResultNode();
    
}
