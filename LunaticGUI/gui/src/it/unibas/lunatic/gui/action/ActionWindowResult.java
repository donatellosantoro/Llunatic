package it.unibas.lunatic.gui.action;

import it.unibas.lunatic.ContextAwareActionProxy;
import it.unibas.lunatic.IModel;
import it.unibas.lunatic.gui.IViewManager;
import it.unibas.lunatic.gui.R;
import it.unibas.lunatic.gui.model.IChaseResult;
import java.awt.Component;
import java.awt.event.ActionEvent;
import javax.swing.JButton;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;
import org.openide.util.NbBundle.Messages;
import org.openide.util.actions.Presenter;

@ActionID(
        category = "Window",
        id = "it.unibas.lunatic.gui.action.ActionShowResult")
@ActionRegistration(
        displayName = "#CTL_ActionShowResult", lazy = false)
@ActionReferences({
    @ActionReference(path = "Menu/Window", position = 250),
    @ActionReference(path = "Toolbars/Window" /*, position = 0*/)})
@Messages("CTL_ActionShowResult=Result")
public final class ActionWindowResult extends ContextAwareActionProxy<IChaseResult> implements Presenter.Toolbar {

    public ActionWindowResult() {
        putValue(NAME, Bundle.CTL_ActionShowResult());
        putValue(SHORT_DESCRIPTION, Bundle.CTL_ActionShowResult());
        putValue(LARGE_ICON_KEY, ImageUtilities.loadImageIcon("it/unibas/lunatic/icons/res-window.24.png", false));
    }
    private IViewManager view = Lookup.getDefault().lookup(IViewManager.class);

    @Override
    public void actionPerformed(ActionEvent e) {
        IChaseResult cs = getBean();
        assert cs != null : "Chase result not found in app map";
        view.show(cs.getWindowName());
    }

    @Override
    public Component getToolbarPresenter() {
        JButton btn = new JButton(this);
        btn.setText("");
        return btn;
    }

    @Override
    protected void register() {
        super.registerBean(R.Bean.LOADED_SCENARIO, R.BeanProperty.CHASE_RESULT, IChaseResult.class);
    }

    @Override
    public void onChange(IModel o, IChaseResult bean) {
        super.defaultChange(o, bean);
    }
}
