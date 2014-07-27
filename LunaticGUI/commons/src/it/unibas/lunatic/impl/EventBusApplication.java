/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unibas.lunatic.impl;

import it.unibas.lunatic.impl.model.EventBusModel;
import it.unibas.lunatic.impl.model.AppModelChangeEvent;
import com.google.common.eventbus.EventBus;
import it.unibas.lunatic.IApplication;
import java.util.EventObject;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Antonio Galotta
 */
@ServiceProvider(service = IApplication.class, position = 5)
public class EventBusApplication extends EventBusModel implements IApplicationImpl {

    public EventBusApplication() {
        super(EventBusApplication.class.getName(), new EventBus("Application bus"));
    }

    @Override
    public IModelImpl createModel(String name) {
        return new EventBusModel(name, bus);
    }

    @Override
    public void notifyChange(String key, Class beanClass) {
        EventObject ev = new AppModelChangeEvent(this, key, beanClass);
        logger.trace(ev.toString());
        bus.post(ev);
    }
}
