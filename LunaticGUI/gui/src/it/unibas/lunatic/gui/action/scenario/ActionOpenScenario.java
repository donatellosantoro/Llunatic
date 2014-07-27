/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unibas.lunatic.gui.action.scenario;

import it.unibas.lunatic.gui.data.ScenarioDataObject;
import it.unibas.lunatic.gui.model.LoadedScenario;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.filechooser.FileFilter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.awt.StatusDisplayer;
import org.openide.filesystems.FileChooserBuilder;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.util.NbBundle.Messages;

@ActionID(
        category = "File",
        id = "it.unibas.lunatic.gui.action.ActionOpenScenario")
@ActionRegistration(
        iconBase = "it/unibas/lunatic/icons/open.24.png",
        displayName = "#CTL_ActionOpenScenario",
        iconInMenu = false)
@ActionReferences({
    @ActionReference(path = "Menu/File", position = 450),
    @ActionReference(path = "Toolbars/File", position = 250)
})
@Messages({"CTL_ActionOpenScenario=Open scenario", "MSG_invalidFile= is not a valid scenario"})
public final class ActionOpenScenario implements ActionListener {

    private FileChooserBuilder fileChooserBuilder = new FileChooserBuilder(LoadedScenario.class);
    private Log logger = LogFactory.getLog(getClass());
    private StatusDisplayer status = StatusDisplayer.getDefault();

    public ActionOpenScenario() {
        fileChooserBuilder.setAcceptAllFileFilterUsed(false);
        fileChooserBuilder.addFileFilter(new ScenarioFileFilter());
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        File f = fileChooserBuilder.showOpenDialog();
        if (f != null) {
            FileObject fo = FileUtil.toFileObject(f);
            try {
                DataObject dao = DataObject.find(fo);
                if (dao instanceof ScenarioDataObject) {
                    new ActionLoadScenario((ScenarioDataObject) dao).actionPerformed(e);
                    return;
                }
            } catch (DataObjectNotFoundException ex) {
                logger.info(ex.getMessage());
                logger.debug(ex);
            }
            status.setStatusText(f.getPath().concat(Bundle.MSG_invalidFile()));
        }
    }
}

class ScenarioFileFilter extends FileFilter {

    @Override
    public boolean accept(File f) {
        if (f.isDirectory() || f.getAbsolutePath().toLowerCase().endsWith(".xml")) {
            return true;
        }
        return false;
    }

    @Override
    public String getDescription() {
        return "Scenario (.xml)";
    }
}
