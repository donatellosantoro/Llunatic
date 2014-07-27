/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unibas.lunatic.gui.visualdeps;

import java.awt.Color;
import java.awt.Graphics;
import javax.swing.JComponent;

/**
 *
 * @author Antonio Galotta
 */
public class LegendRect extends JComponent {

    @Override
    public void paint(Graphics g) {
        g.setColor(Color.red);
        g.fillRect(0, 0, 16, 16);
    }
}
