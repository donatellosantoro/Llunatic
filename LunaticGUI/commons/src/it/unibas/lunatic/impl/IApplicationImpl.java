package it.unibas.lunatic.impl;

import it.unibas.lunatic.IApplication;

public interface IApplicationImpl extends IApplication, IModelImpl {

    public IModelImpl createModel(String modelName);
}
