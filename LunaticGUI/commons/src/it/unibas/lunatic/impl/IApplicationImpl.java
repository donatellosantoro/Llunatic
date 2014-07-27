/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unibas.lunatic.impl;

import it.unibas.lunatic.IApplication;

/**
 *
 * @author Antonio Galotta
 */
public interface IApplicationImpl extends IApplication, IModelImpl {

    public IModelImpl createModel(String modelName);
}
