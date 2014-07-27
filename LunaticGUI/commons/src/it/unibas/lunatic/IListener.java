/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unibas.lunatic;

/**
 *
 * @author Antonio Galotta
 */
public interface IListener<Bean> {

    void remove();

    void onChange(IModel model, Bean bean);
}
