/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unibas.lunatic.gui.action.chase.task;

import it.unibas.lunatic.gui.model.IChaseResult;
import it.unibas.lunatic.gui.model.LoadedScenario;

/**
 *
 * @author Antonio Galotta
 */
interface IChaseOperator {

    IChaseResult chase(LoadedScenario loadedScenario);
    
}
