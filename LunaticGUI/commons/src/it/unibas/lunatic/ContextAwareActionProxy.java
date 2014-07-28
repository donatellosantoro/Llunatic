package it.unibas.lunatic;

import it.unibas.lunatic.impl.IApplicationImpl;
import it.unibas.lunatic.impl.listener.IListenerImpl;
import javax.swing.AbstractAction;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openide.util.Lookup;

public abstract class ContextAwareActionProxy<Bean> extends AbstractAction implements IListener<Bean> {

    private IApplicationImpl applicationInjector = Lookup.getDefault().lookup(IApplicationImpl.class);
    private IListenerImpl<Bean> impl = applicationInjector.createListener();
    private boolean registered = false;
    protected IApplication app = applicationInjector;
    protected Log logger = LogFactory.getLog(getClass());

    protected abstract void register();

    protected void defaultChange(IModel model, Bean bean) {
        if (model != null && bean != null) {
            this.setEnabled(true);
        } else {
            this.setEnabled(false);
        }
    }

    @Override
    public final boolean isEnabled() {
        if (!registered) {
            register();
        }
        return super.isEnabled();
    }

    protected final void registerBean(String modelName, String beanName, Class<Bean> beanType) {
        impl.registerBean(this, modelName, beanName, beanType);
        registered = true;
    }

    protected final void registerBean(String beanName, Class<Bean> beanType) {
        impl.registerBean(this, beanName, beanType);
        registered = true;
    }

    @Override
    public void remove() {
    }

    protected final Bean getBean() {
        return impl.getBean();
    }

    public IModel getModel() {
        return impl.getModel();
    }

    public void update() {
        impl.update();
    }

    public String getBeanName() {
        return impl.getBeanName();
    }
}
