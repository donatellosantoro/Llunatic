/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unibas.lunatic;

import java.util.Collection;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.util.Utilities;

/**
 *
 * @author Antonio Galotta
 */
public abstract class AbstractSelectionListener<Bean> implements LookupListener {

    protected Log logger = LogFactory.getLog(getClass());
    private Lookup lookup = Utilities.actionsGlobalContext();
    private Lookup.Result<Bean> subject;

    public AbstractSelectionListener() {
    }

    public AbstractSelectionListener(Lookup context) {
        this.lookup = context;
    }

    protected void registerBean(Class<Bean> c) {
        this.subject = lookup.lookupResult(c);
        subject.addLookupListener(this);
        logger.debug("Listener registered for: " + c.getName());
        logger.debug("Result: " + subject.toString());
        update();
    }

    @Override
    public final void resultChanged(LookupEvent e) {
        logger.debug("Listener called");
        update();
    }

    public void remove() {
        if (subject != null) {
            subject.removeLookupListener(this);
            logger.debug("Listener removed");
        } else {
            logger.warn("Called remove before registration");
        }
    }

    protected Bean getBean(Collection<? extends Bean> collection) {
        if (!collection.isEmpty()) {
            if (collection.size() > 1) {
                logger.warn("Multiple instances");
            }
            return collection.iterator().next();
        }
        return null;
    }

    protected Collection<? extends Bean> getBeans() {
        return subject.allInstances();
    }

    public void update() {
        onChange(getBeans());
    }

    protected abstract void onChange(Collection<? extends Bean> beans);
}
