package it.unibas.lunatic.impl.model;

import it.unibas.lunatic.IModel;
import java.util.EventObject;

public class BeanModelChangeEvent extends EventObject {

    private final String beanName;
    private final Class type;
    private final String modelName;

    public BeanModelChangeEvent(IModel model, String modelName, String beanName, Class type) {
        super(model);
        this.beanName = beanName;
        this.type = type;
        this.modelName = modelName;
    }

    public String getModelName() {
        return modelName;
    }

    public Class getBeanType() {
        return type;
    }

    public String getBeanName() {
        return beanName;
    }

    @Override
    public IModel getSource() {
        return (IModel) super.getSource();
    }

    @Override
    public String toString() {
        return getClass().getName() + "[beanName=" + beanName
                + ", modelName=" + modelName
                + ", source=" + source.toString() + "]";
    }
}
