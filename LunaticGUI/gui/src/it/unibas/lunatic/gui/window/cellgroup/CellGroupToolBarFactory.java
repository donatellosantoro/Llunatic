/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unibas.lunatic.gui.window.cellgroup;

import javax.swing.JToolBar;
import org.openide.awt.Toolbar;

/**
 *
 * @author Antonio Galotta
 */
public class CellGroupToolBarFactory {

    private static CellGroupToolBarFactory instance;
    private JToolBar toolBar;

    public static CellGroupToolBarFactory getInstance() {
        if (instance == null) {
            instance = new CellGroupToolBarFactory();
            instance.createToolBar();
        }
        return instance;
    }

    public JToolBar getToolBar() {
        return toolBar;
    }

    private void createToolBar() {
        toolBar = new Toolbar();
    }
    
    
}
