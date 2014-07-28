package it.unibas.lunatic.gui.window.utils;

public class LoggingUtils {

    public static String printArray(Object[] array) {
        String s = "[";
        for (int i = 0; i < array.length; i++) {
            s = s.concat(array[i] + ",");
        }
        return s.concat("]");
    }
}
