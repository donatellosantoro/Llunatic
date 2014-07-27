/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unibas.lunatic.gui.visualdeps;

import it.unibas.lunatic.model.dependency.Dependency;

/**
 *
 * @author Antonio Galotta
 */
public interface IGeneratorFactory {

    public IDependencySceneGenerator getGenerator(Dependency dependency);
    
}
