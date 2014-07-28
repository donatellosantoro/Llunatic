package it.unibas.lunatic.impl;

import it.unibas.lunatic.impl.model.ObservableModel;

//@ServiceProvider(service = ApplicationSetup.class, position = 2)
public class ObservableApplication extends ObservableModel implements IApplicationImpl {

    public ObservableApplication() {
        super(ObservableApplication.class.getName());
    }

    @Override
    public IModelImpl createModel(String name) {
        return new ObservableModel(name);
    }
}
