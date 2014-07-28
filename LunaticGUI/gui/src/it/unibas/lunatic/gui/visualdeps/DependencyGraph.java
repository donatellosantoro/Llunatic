package it.unibas.lunatic.gui.visualdeps;

import javax.swing.JComponent;
import org.netbeans.api.visual.widget.Scene;

public class DependencyGraph {

    private final JComponent sceneView;
    private final Scene scene;

    public DependencyGraph(Scene scene) {
        this.scene = scene;
        this.sceneView = scene.createView();
    }

    public Scene getScene() {
        return scene;
    }

    public JComponent getSceneView() {
        return sceneView;
    }
    
    
}
