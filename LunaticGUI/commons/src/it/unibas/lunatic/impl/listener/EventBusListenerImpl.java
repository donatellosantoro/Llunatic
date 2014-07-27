/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unibas.lunatic.impl.listener;

import it.unibas.lunatic.*;
import com.google.common.eventbus.Subscribe;
import it.unibas.lunatic.impl.model.AppModelChangeEvent;
import it.unibas.lunatic.impl.model.BeanModelChangeEvent;
import it.unibas.lunatic.impl.model.EventBusModel;

/**
 *
 * @author Antonio Galotta
 */
public class EventBusListenerImpl<Bean> extends AbstractListenerImpl<Bean> {

    protected EventBusModel model;
    private boolean registered;

    public EventBusListenerImpl(EventBusModel model) {
        super(model);
        this.model = model;
    }

    @Override
    public void registerBean(IListener<Bean> listener, String modelName, String beanName, Class<Bean> type) {
        super.initialize(listener, modelName, beanName, type);
        model.addListener(this);
        registered = true;
        update();
    }

    @Override
    public void registerBean(IListener<Bean> listener, String beanName, Class<Bean> type) {
        super.initialize(listener, beanName, type);
        model.addListener(this);
        registered = true;
        update();
    }

    @Override
    public void remove() {
        if (registered) {
            model.removeListener(this);
            registered = false;
        }
    }

    @Subscribe
    public final void appContentChanged(AppModelChangeEvent ev) {
        if (ev.getBeanName().equals(beanName) || ev.getBeanName().equals(modelName)) {
            update();
        }
    }

    @Subscribe
    public final void modelContentChanged(BeanModelChangeEvent ev) {
        if (modelName != null && ev.getModelName().equals(modelName) && ev.getBeanName().equals(beanName)) {
            update();
        }
    }
}
