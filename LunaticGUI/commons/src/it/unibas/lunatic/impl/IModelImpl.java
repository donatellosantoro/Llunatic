package it.unibas.lunatic.impl;

import it.unibas.lunatic.IModel;
import it.unibas.lunatic.impl.listener.IListenerImpl;

public interface IModelImpl extends IModel {

    <Bean> IListenerImpl<Bean> createListener();
}
