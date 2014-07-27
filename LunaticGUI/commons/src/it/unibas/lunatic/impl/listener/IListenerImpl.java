/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unibas.lunatic.impl.listener;

import it.unibas.lunatic.IListener;
import it.unibas.lunatic.IModel;

/**
 *
 * @author Antonio Galotta
 */
public interface IListenerImpl<Bean> {

    void registerBean(IListener<Bean> abstractListener, String modelName, String beanName, Class<Bean> type);

    void registerBean(IListener<Bean> abstractListener, String beanName, Class<Bean> type);

    Bean getBean();

    String getBeanName();

    String getModelName();

    IModel getModel();

    void remove();

    public void update();
}
