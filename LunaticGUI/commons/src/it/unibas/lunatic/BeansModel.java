package it.unibas.lunatic;

import it.unibas.lunatic.impl.IModelImpl;
import it.unibas.lunatic.impl.IApplicationImpl;
import org.openide.util.Lookup;

public abstract class BeansModel implements IModel {

    private IApplicationImpl applicationInjector = Lookup.getDefault().lookup(IApplicationImpl.class);
    private IModelImpl impl;

    protected BeansModel(String id) {
        impl = applicationInjector.createModel(id);
    }

    @Override
    public <BeanType> BeanType get(String key, Class<BeanType> beanClass) {
        return impl.get(key, beanClass);
    }

    @Override
    public void put(String s, Object value) {
        impl.put(s, value);
    }

    @Override
    public boolean remove(String s) {
        return impl.remove(s);
    }

    @Override
    public boolean remove(String s, Object o) {
        return impl.remove(s, o);
    }

    @Override
    public void notifyChange(String key, Class beanClass) {
        impl.notifyChange(key, beanClass);
    }

    @Override
    public String getName() {
        return impl.getName();
    }
}
