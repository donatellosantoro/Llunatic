package it.unibas.lunatic.gui.visualdeps;

import it.unibas.lunatic.model.dependency.Dependency;
import org.netbeans.api.visual.widget.Scene;

public interface IDependencySceneGenerator {

    Scene createScene(Dependency dependency);
    
}
