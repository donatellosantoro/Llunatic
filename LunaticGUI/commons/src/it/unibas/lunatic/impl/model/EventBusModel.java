/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unibas.lunatic.impl.model;

import com.google.common.eventbus.EventBus;
import it.unibas.lunatic.impl.listener.EventBusListenerImpl;
import it.unibas.lunatic.impl.listener.IListenerImpl;
import java.util.EventObject;

/**
 *
 * @author Antonio Galotta
 */
public class EventBusModel extends AbstractModel {

    protected final EventBus bus;

    public EventBusModel(String name, EventBus bus) {
        super(name);
        this.bus = bus;
    }

    @Override
    public void notifyChange(String bean, Class beanClass) {
        EventObject ev = new BeanModelChangeEvent(this, name, bean, beanClass);
        logger.trace(ev.toString());
        bus.post(ev);
    }

    @Override
    public <Bean> IListenerImpl<Bean> createListener() {
        return new EventBusListenerImpl<Bean>(this);
    }

    public void addListener(Object l) {
        bus.register(l);
    }

    public void removeListener(Object l) {
        bus.unregister(l);
    }
}
