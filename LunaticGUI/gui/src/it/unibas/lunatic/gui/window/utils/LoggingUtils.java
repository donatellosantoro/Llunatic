/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unibas.lunatic.gui.window.utils;

/**
 *
 * @author Antonio Galotta
 */
public class LoggingUtils {

    public static String printArray(Object[] array) {
        String s = "[";
        for (int i = 0; i < array.length; i++) {
            s = s.concat(array[i] + ",");
        }
        return s.concat("]");
    }
}
