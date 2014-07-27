/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unibas.lunatic.gui.action.chase;

import it.unibas.lunatic.gui.R;
import it.unibas.lunatic.core.persistence.DaoChaseStep;
import it.unibas.lunatic.gui.node.chase.mc.ChaseStepNode;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.filechooser.FileFilter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openide.awt.ActionID;
import org.openide.awt.ActionRegistration;
import org.openide.awt.StatusDisplayer;
import org.openide.filesystems.FileChooserBuilder;
import org.openide.util.NbBundle.Messages;

@ActionID(
        category = "File",
        id = R.ActionId.SAVE_CHASE_STEP)
@ActionRegistration(
        displayName = "#CTL_ActionSaveCells")
@Messages({
    "CTL_ActionSaveCells=Export changes as csv",
    "MSG_FileSaved=File saved successfully",
    "MSG_FileNotSaved=File not saved: "
})
public final class ActionSaveChaseStep implements ActionListener {

    private static Log logger = LogFactory.getLog(ActionSaveChaseStep.class);
    private final ChaseStepNode context;
    private DaoChaseStep daoChaseStep = DaoChaseStep.getInstance();
    private FileChooserBuilder fileChooserBuilder = new FileChooserBuilder(ActionSaveChaseStep.class);
    private StatusDisplayer status = StatusDisplayer.getDefault();
    private CvsFileFilter fileFilter = new CvsFileFilter();

    public ActionSaveChaseStep(ChaseStepNode context) {
        this.context = context;
        fileChooserBuilder.setAcceptAllFileFilterUsed(false);
        fileChooserBuilder.setFileFilter(new CvsFileFilter());
    }

    @Override
    public void actionPerformed(ActionEvent ev) {
        try {
            File file = fileChooserBuilder.showSaveDialog();
            if (file != null) {
                String filename = file.getAbsolutePath();
                if (!fileFilter.accept(filename)) {
                    filename = filename + ".csv";
                }
                daoChaseStep.persist(context.getScenario(), context.getChaseStep(), filename);
                status.setStatusText(Bundle.MSG_FileSaved());
            }
        } catch (Exception e) {
            status.setStatusText(Bundle.MSG_FileNotSaved().concat(e.getMessage()));
            logger.warn(e);
            if (logger.isDebugEnabled()) e.printStackTrace();
        }

    }
}

class CvsFileFilter extends FileFilter {

    @Override
    public boolean accept(File file) {
        if (file.isDirectory() || accept(file.getAbsolutePath().toLowerCase())) {
            return true;
        }
        return false;
    }

    public boolean accept(String filename) {
        if (filename.endsWith(".csv")) {
            return true;
        }
        return false;
    }

    @Override
    public String getDescription() {
        return "CSV";
    }
}