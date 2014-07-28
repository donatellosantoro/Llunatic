package it.unibas.lunatic.impl.model;

import it.unibas.lunatic.IModel;
import it.unibas.lunatic.impl.IModelImpl;
import it.unibas.lunatic.impl.listener.IListenerImpl;
import it.unibas.lunatic.impl.listener.ObserverImpl;
import java.util.Observable;

public class ObservableModel extends Observable implements IModelImpl {

    private Model model;

    public ObservableModel(String name) {
        model = new Model(name);
    }

    @Override
    public <BeanType> BeanType get(String key, Class<BeanType> beanClass) {
        return model.get(key, beanClass);
    }

    @Override
    public void put(String s, Object value) {
        model.put(s, value);
    }

    @Override
    public boolean remove(String s) {
        return model.remove(s);
    }

    @Override
    public boolean remove(String s, Object o) {
        return model.remove(s, o);
    }

    @Override
    public void notifyChange(String key, Class beanClass) {
        model.notifyChange(key, beanClass);
    }

    @Override
    public String getName() {
        return model.getName();
    }

    @Override
    public <Bean> IListenerImpl<Bean> createListener() {
        return model.createListener();
    }

    private class Model extends AbstractModel {

        public Model(String name) {
            super(name);
        }

        @Override
        public void notifyChange(String key, Class type) {
            setChanged();
            notifyObservers(key);
        }

        @Override
        public <Bean> IListenerImpl<Bean> createListener() {
            return new ObserverImpl<Bean>(ObservableModel.this);
        }
    }
}
