/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unibas.lunatic.impl.listener;

import it.unibas.lunatic.IListener;
import it.unibas.lunatic.impl.model.ObservableModel;
import java.util.Observable;
import java.util.Observer;

/**
 *
 * @author Antonio Galotta
 */
public final class ObserverImpl<Bean> extends AbstractListenerImpl<Bean> implements Observer {

    protected ObservableModel model;

    public ObserverImpl(ObservableModel model) {
        super(model);
        this.model = model;
    }

    @Override
    public void registerBean(IListener<Bean> abstractListener, String beanName, Class<Bean> type) {
        super.initialize(abstractListener, beanName, type);
        model.addObserver(this);
        update();
    }

    @Override
    public void registerBean(IListener<Bean> abstractListener, String modelName, String beanName, Class<Bean> type) {
        super.initialize(abstractListener, modelName, beanName, type);
        model.addObserver(this);
        update();
    }

    @Override
    public void remove() {
        model.deleteObserver(this);
    }

    @Override
    public void update(Observable o, Object updatedBeanName) {
        if (o.equals(model)) {
            onAppContentUpdate((String) updatedBeanName);
        } else {
            onMoldelContentUpdate((String) updatedBeanName);
        }
    }

    //bisogna aregistrarsi come listener
    private void onAppContentUpdate(String updatedBeanName) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    private void onMoldelContentUpdate(String updatedBeanName) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
