/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unibas.lunatic.gui.visualdeps;

import it.unibas.lunatic.model.dependency.Dependency;
import org.netbeans.api.visual.widget.Scene;

/**
 *
 * @author Antonio Galotta
 */
public interface IDependencySceneGenerator {

    Scene createScene(Dependency dependency);
    
}
