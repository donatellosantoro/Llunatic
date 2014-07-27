/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unibas.lunatic.gui;

import java.awt.Frame;
import java.lang.reflect.InvocationTargetException;
import org.openide.windows.TopComponent;

/**
 *
 * @author Antonio Galotta
 */
public interface IViewManager {

    TopComponent show(String name);

    public TopComponent findOpenedWindow(String name);

    public TopComponent getActivatedWindow();

    public TopComponent findWindowByName(String name);

    public Frame getMainFrame();

    public void invokeLater(Runnable runnable);
    
    public void invokeAndWait(Runnable runnable) throws InterruptedException, InvocationTargetException;
}
