/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unibas.lunatic.impl;

import it.unibas.lunatic.IModel;
import it.unibas.lunatic.impl.listener.IListenerImpl;

/**
 *
 * @author Antonio Galotta
 */
public interface IModelImpl extends IModel {

    <Bean> IListenerImpl<Bean> createListener();
}
