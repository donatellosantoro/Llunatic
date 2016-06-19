package it.unibas.lunatic.gui.node.utils;

import java.lang.reflect.InvocationTargetException;
import org.openide.nodes.PropertySupport;

public abstract class StringProperty extends PropertySupport<String> {

    public StringProperty(String name) {
        this(name, name, name);
    }

    public StringProperty(String name, String displayName) {
        this(name, displayName, displayName);
    }

    public StringProperty(String name, String displayName, String shortDescription) {
        super(name, String.class, displayName, shortDescription, true, false);
        setValue("suppressCustomEditor", Boolean.TRUE);
    }

    @Override
    public void setValue(String val) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
    }

    protected void enableEditor() {
        setValue("suppressCustomEditor", Boolean.FALSE);
    }

//    @Override
//    public final boolean canWrite() {
//        return true;
//    }
    
    
}
