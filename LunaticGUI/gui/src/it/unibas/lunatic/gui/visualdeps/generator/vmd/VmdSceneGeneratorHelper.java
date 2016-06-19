package it.unibas.lunatic.gui.visualdeps.generator.vmd;

import it.unibas.lunatic.LunaticConstants;
import it.unibas.lunatic.gui.visualdeps.IDependencySceneGenerator;
import it.unibas.lunatic.model.dependency.Dependency;
import org.netbeans.api.visual.vmd.VMDGraphScene;
import org.netbeans.api.visual.widget.Scene;

public class VmdSceneGeneratorHelper implements IDependencySceneGenerator {

    @Override
    public Scene createScene(Dependency dependency) {
        VMDGraphScene scene = new VMDGraphScene();
        IVmdSceneGenerator generator = getGenerator(dependency);
        generator.populateScene(scene);
        scene.layoutScene();
        return scene;
    }

    private IVmdSceneGenerator getGenerator(Dependency dependency) {
        if (dependency.getType().equals(LunaticConstants.EGD) || dependency.getType().equals(LunaticConstants.ExtEGD)) {
            return new VmdEgdSceneGenerator(dependency);
        }
        return new VmdTgdDepSceneGenerator(dependency);
    }
}
