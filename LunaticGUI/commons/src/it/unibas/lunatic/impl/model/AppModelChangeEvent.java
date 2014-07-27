/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unibas.lunatic.impl.model;

import it.unibas.lunatic.IApplication;
import it.unibas.lunatic.impl.EventBusApplication;
import java.util.EventObject;

/**
 *
 * @author Antonio Galotta
 */
public class AppModelChangeEvent extends EventObject {

    private final String beanName;
    private final Class beanType;

    public AppModelChangeEvent(EventBusApplication model, String beanName, Class beanClass) {
        super(model);
        this.beanName = beanName;
        this.beanType = beanClass;
    }

    public Class getBeanType() {
        return beanType;
    }

    @Override
    public IApplication getSource() {
        return (EventBusApplication) super.getSource();
    }

    public String getBeanName() {
        return beanName;
    }

    @Override
    public String toString() {
        return getClass().getName() + "[beanName=" + beanName + ", source="
                + source.toString() + "]";
    }
}
