/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unibas.lunatic.gui.visualdeps.generator;

import it.unibas.lunatic.gui.visualdeps.IGeneratorFactory;
import it.unibas.lunatic.LunaticConstants;
import it.unibas.lunatic.gui.visualdeps.IDependencySceneGenerator;
import it.unibas.lunatic.model.dependency.Dependency;
import org.netbeans.api.visual.widget.Scene;

/**
 *
 * @author Antonio Galotta
 */
public class LunaticDepSceneGeneratorHelper implements IDependencySceneGenerator {

    private IGeneratorFactory generatorFactory = new CustomGeneratorFactory();

    @Override
    public Scene createScene(Dependency dependency) {
        IDependencySceneGenerator generator = generatorFactory.getGenerator(dependency);
        return generator.createScene(dependency);
    }
}

class CustomGeneratorFactory implements IGeneratorFactory {

    @Override
    public IDependencySceneGenerator getGenerator(Dependency dependency) {
        if (dependency.getType().equals(LunaticConstants.EGD) || dependency.getType().equals(LunaticConstants.ExtEGD)) {
            return new EgdSceneGenerator();
        }
        return new TgdDepSceneGenerator();
    }
}
