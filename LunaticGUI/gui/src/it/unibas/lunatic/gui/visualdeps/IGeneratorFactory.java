package it.unibas.lunatic.gui.visualdeps;

import it.unibas.lunatic.model.dependency.Dependency;

public interface IGeneratorFactory {

    public IDependencySceneGenerator getGenerator(Dependency dependency);
    
}
