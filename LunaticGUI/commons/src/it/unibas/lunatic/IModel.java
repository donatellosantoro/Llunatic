/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unibas.lunatic;

/**
 *
 * @author Antonio Galotta
 */
public interface IModel {

    <BeanType> BeanType get(String key, Class<BeanType> beanClass);

    void put(String s, Object value);

    boolean remove(String s);

    boolean remove(String s, Object o);

    public void notifyChange(String key, Class beanClass);

    String getName();
}
