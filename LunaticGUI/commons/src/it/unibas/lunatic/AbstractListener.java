/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unibas.lunatic;

import it.unibas.lunatic.impl.IApplicationImpl;
import it.unibas.lunatic.impl.listener.IListenerImpl;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openide.util.Lookup;

/**
 *
 * @author Antonio Galotta
 */
public abstract class AbstractListener<Bean> implements IListener<Bean> {

    protected Log logger = LogFactory.getLog(getClass());
    private IApplicationImpl application = Lookup.getDefault().lookup(IApplicationImpl.class);
    private IListenerImpl<Bean> implementation;
    protected IApplication app = application;

    protected AbstractListener() {
    }

    protected void registerBean(String modelName, String beanName, Class<Bean> type) {
        implementation = application.createListener();
        implementation.registerBean(this, modelName, beanName, type);
    }

    protected void registerBean(String beanName, Class<Bean> type) {
        implementation = application.createListener();
        implementation.registerBean(this, beanName, type);
    }

    public Bean getBean() {
        return implementation.getBean();
    }

    protected IModel getModel() {
        return implementation.getModel();
    }

    public void update() {
        implementation.update();
    }

    @Override
    public void remove() {
        if (implementation != null) {
            implementation.remove();
        }
    }

    public String getBeanName() {
        return implementation.getBeanName();
    }

    public String getModelName() {
        return implementation.getModelName();
    }
}
