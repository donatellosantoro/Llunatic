package it.unibas.lunatic.impl.listener;

import it.unibas.lunatic.IListener;
import it.unibas.lunatic.IModel;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

abstract class AbstractListenerImpl<Bean> implements IListenerImpl<Bean> {

    protected Log logger = LogFactory.getLog(getClass());
    protected String beanName;
    protected Class<Bean> type;
    protected String modelName;
    private IListener<Bean> listener;
    private IModel application;

    public AbstractListenerImpl(IModel application) {
        this.application = application;
    }

    protected void initialize(IListener<Bean> listener, String modelName, String beanName, Class<Bean> type) {
        initialize(listener, beanName, type);
        this.modelName = modelName;
    }

    protected void initialize(IListener<Bean> listener, String beanName, Class<Bean> type) {
        this.beanName = beanName;
        this.type = type;
        this.listener = listener;
    }

    @Override
    public String getBeanName() {
        return beanName;
    }

    @Override
    public Bean getBean() {
        assert beanName != null;
        assert type != null;
        IModel m = getModel();
        if (m != null) {
            return m.get(beanName, type);
        }
        return null;
    }

    @Override
    public void update() {
        assert listener != null;
        assert beanName != null;
        assert type != null;
        logger.trace("Called:  " + listener.getClass().getSimpleName());
        logger.trace("Bean: [name=" + beanName + ", value=" + getBean() + "]");
        logger.trace("Model: [name=" + modelName + ", impl=" + getModel() + "]");
        listener.onChange(getModel(), getBean());
    }

    @Override
    public IModel getModel() {
        if (modelName == null) {
            return application;
        }
        return application.get(modelName, IModel.class);
    }

    @Override
    public String getModelName() {
        return modelName;
    }
}
