/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unibas.lunatic.gui.model;

/**
 *
 * @author Antonio Galotta
 */
public interface IChaseResult {

    LoadedScenario getLoadedScenario();

    public String getWindowName();

    public boolean IsDataExchange();
}
