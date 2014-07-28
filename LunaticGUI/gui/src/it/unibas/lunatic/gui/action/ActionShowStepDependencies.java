package it.unibas.lunatic.gui.action;

import it.unibas.lunatic.IApplication;
import it.unibas.lunatic.gui.IViewManager;
import it.unibas.lunatic.gui.R;
import it.unibas.lunatic.gui.node.chase.mc.ChaseStepNode;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.openide.awt.ActionID;
import org.openide.awt.ActionRegistration;
import org.openide.util.Lookup;
import org.openide.util.NbBundle.Messages;

@ActionID(
        category = "Window",
        id = R.ActionId.SHOW_STEP_DEPS)
@ActionRegistration(
        displayName = "#CTL_ActionCheckDependencies")
@Messages("CTL_ActionCheckDependencies=Check dependencies")
public final class ActionShowStepDependencies implements ActionListener {

//    private final ChaseStepNode context;
//    private CheckUnsatisfiedDependencies checker = new CheckUnsatisfiedDependencies();
    private IApplication app = Lookup.getDefault().lookup(IApplication.class);
    private IViewManager view = Lookup.getDefault().lookup(IViewManager.class);

    public ActionShowStepDependencies(ChaseStepNode chaseStep) {
//        this.context = chaseStep;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        view.show(R.Window.UNSATISFIED_DEPENDENCIES);
    }
}
