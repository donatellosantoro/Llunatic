package it.unibas.lunatic.lookup.impl;

import com.google.common.eventbus.EventBus;
import it.unibas.lunatic.AppMapChangeEvent;
import it.unibas.lunatic.BeanChangeEvent;
import it.unibas.lunatic.IApplication;
import it.unibas.lunatic.IListener;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.netbeans.modules.openide.windows.GlobalActionContextImpl;
import org.openide.util.ContextGlobalProvider;
import org.openide.util.Lookup;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;
import org.openide.util.lookup.ProxyLookup;
import org.openide.util.lookup.ServiceProvider;
import org.openide.util.lookup.ServiceProviders;

@ServiceProviders({
    @ServiceProvider(
            service = ContextGlobalProvider.class,
            supersedes = "org.netbeans.modules.openide.windows.GlobalActionContextImpl"),
    @ServiceProvider(
            service = IApplication.class,
            position = 1)
})
public class ContextGlobalProviderProxy implements ContextGlobalProvider, IApplication {

    //The native NetBeans global context Lookup provider
    private ContextGlobalProvider originalGlobalContextProvider = null;
    //The primary lookup managed by the platform
    private final Lookup globalContextLookup;
    //The lookup managed by this class
    private final Lookup appLookup;
    //The actual Lookup returned by this class
    private Lookup proxyLookup;
    private final InstanceContent content;

    public ContextGlobalProviderProxy() {
        originalGlobalContextProvider = new GlobalActionContextImpl();
        this.content = new InstanceContent();
        this.globalContextLookup = this.originalGlobalContextProvider
                .createGlobalContext();
        this.appLookup = new AbstractLookup(content);
    }

    @Override
    public Lookup createGlobalContext() {
        if (this.proxyLookup == null) {
            this.proxyLookup = new ProxyLookup(this.globalContextLookup,
                    this.appLookup);
        }
        return this.proxyLookup;
    }
    private Map<String, Class<?>> classMap = new HashMap<String, Class<?>>();
    private Map<Class<?>, String> keyMap = new HashMap<Class<?>, String>();
    private Log logger = LogFactory.getLog(getClass());
    private EventBus bus = new EventBus("Application Map Bus");
    private String errorMessage = "This implementation does not support multiple objects with the same type in map";

    @Override
    public <BeanType> BeanType get(String key, Class<BeanType> beanClass) {
        if (keyMap.containsKey(beanClass)) {
            String myKey = keyMap.get(beanClass);
            if (!key.equals(myKey)) {
                throw new UnsupportedOperationException(errorMessage);
            }
        }
        return appLookup.lookup(beanClass);
    }

    @Override
    public synchronized void put(String key, Object value) {
        Object previous = get(key, value.getClass());
        if (previous != null) {
            content.remove(previous);
        } else {
            keyMap.put(value.getClass(), key);
            classMap.put(key, value.getClass());
        }
        content.add(value);
        logger.debug("Added " + key);
        notifyChange(key);
    }

    @Override
    public synchronized boolean remove(String s, Object o) {
        Object bean = get(s, o.getClass());
        if (!bean.equals(o)) {
            logger.warn("Remove: bean not corresponding for key " + s);
        }
        return remove(s);
    }

    @Override
    public synchronized boolean remove(String beanName) {
        return removeBean(beanName);
    }

    private boolean removeBean(String s) {
        if (classMap.containsKey(s)) {
            Class<?> clazz = classMap.get(s);
            Object previous = get(s, clazz);
            content.remove(previous);
            notifyChange(s);
            logger.debug("Removed " + s);
            return true;
        }
        logger.trace("Unable to remove " + s);
        return false;
    }

    @Override
    public void addListener(IListener l) {
        bus.register(l);
    }

    @Override
    public void removeListener(IListener l) {
        bus.unregister(l);
    }

    public void notifyChange(String beanName) {
        bus.post(new AppMapChangeEvent(this, beanName));
    }

    @Override
    public void notifyBeanChange(String beanName, String propertyName) {
        bus.post(new BeanChangeEvent(this, beanName, propertyName));
    }
}
