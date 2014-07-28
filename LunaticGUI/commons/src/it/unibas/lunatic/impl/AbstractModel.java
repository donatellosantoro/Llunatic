package it.unibas.lunatic.impl.model;

import it.unibas.lunatic.impl.IModelImpl;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

abstract class AbstractModel implements IModelImpl {

    protected Map<String, Object> beans = new ConcurrentHashMap<String, Object>();
    protected Log logger = LogFactory.getLog(getClass());
    protected String name;

    public AbstractModel(String name) {
        this.name = name;
    }

    @Override
    public <BeanType> BeanType get(String key, Class<BeanType> beanClass) {
        Object result = beans.get(key);
        if (result != null) {
            return beanClass.cast(result);
        }
        return null;
    }

    @Override
    public void put(String s, Object value) {
        beans.put(s, value);
        logger.debug("Added " + s + "=" + value.toString());
        notifyChange(s, value.getClass());
    }

    @Override
    public boolean remove(String s) {
        return removeBean(s);
    }

    @Override
    public boolean remove(String s, Object o) {
        if (beans.containsKey(s)) {
            Object bean = get(s, o.getClass());
            if (bean.equals(o)) {
                return removeBean(s);
            }
            logger.warn("Remove: bean not corresponding for key " + s);
        }
        logger.debug("Bean not found with key " + s);
        return false;
    }

    protected boolean removeBean(String s) {
        Object removedObject = beans.remove(s);
        boolean removed = removedObject != null;
        if (removedObject != null) {
            notifyChange(s, removedObject.getClass());
            logger.debug("Removed " + s  + "=" + removedObject.toString());
        } else {
            logger.trace("Unable to remove " + s);
        }
        return removed;
    }

    @Override
    public String getName() {
        return name;
    }
}
