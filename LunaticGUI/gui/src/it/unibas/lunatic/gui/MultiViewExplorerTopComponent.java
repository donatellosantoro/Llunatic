package it.unibas.lunatic.gui;

import java.awt.Image;
import javax.swing.Action;
import javax.swing.JComponent;
import org.netbeans.core.spi.multiview.CloseOperationState;
import org.netbeans.core.spi.multiview.MultiViewDescription;
import org.netbeans.core.spi.multiview.MultiViewElement;
import org.netbeans.core.spi.multiview.MultiViewElementCallback;
import org.openide.awt.UndoRedo;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;

public abstract class MultiViewExplorerTopComponent extends ExplorerTopComponent {

    protected MultiViewElementCallback callback;
    private Element multiView = new Element();
    private Description description = new Description();

    public MultiViewDescription getMultiViewDescription() {
        return description;
    }

    public MultiViewElement getMultiViewElement() {
        return multiView;
    }

    public abstract JComponent getToolBar();

    private class Description implements MultiViewDescription {

        @Override
        public int getPersistenceType() {
            return MultiViewExplorerTopComponent.this.getPersistenceType();
        }

        @Override
        public String getDisplayName() {
            return MultiViewExplorerTopComponent.this.getDisplayName();
        }

        @Override
        public Image getIcon() {
            return MultiViewExplorerTopComponent.this.getIcon();
        }

        @Override
        public HelpCtx getHelpCtx() {
            return MultiViewExplorerTopComponent.this.getHelpCtx();
        }

        @Override
        public String preferredID() {
            return MultiViewExplorerTopComponent.this.preferredID();
        }

        @Override
        public MultiViewElement createElement() {
            return MultiViewExplorerTopComponent.this.getMultiViewElement();
        }
    }

    private class Element implements MultiViewElement {

        @Override
        public JComponent getVisualRepresentation() {
            return MultiViewExplorerTopComponent.this;
        }

        @Override
        public JComponent getToolbarRepresentation() {
            return MultiViewExplorerTopComponent.this.getToolBar();
        }

        @Override
        public Action[] getActions() {
            return MultiViewExplorerTopComponent.this.getActions();
        }

        @Override
        public Lookup getLookup() {
            return MultiViewExplorerTopComponent.this.getLookup();
        }

        @Override
        public void componentOpened() {
            MultiViewExplorerTopComponent.this.componentOpened();
        }

        @Override
        public void componentClosed() {
            MultiViewExplorerTopComponent.this.componentClosed();
        }

        @Override
        public void componentShowing() {
            MultiViewExplorerTopComponent.this.componentShowing();
        }

        @Override
        public void componentHidden() {
            MultiViewExplorerTopComponent.this.componentHidden();
        }

        @Override
        public void componentActivated() {
            MultiViewExplorerTopComponent.this.componentActivated();
        }

        @Override
        public void componentDeactivated() {
            MultiViewExplorerTopComponent.this.componentDeactivated();
        }

        @Override
        public UndoRedo getUndoRedo() {
            return MultiViewExplorerTopComponent.this.getUndoRedo();
        }

        @Override
        public void setMultiViewCallback(MultiViewElementCallback callback) {
            MultiViewExplorerTopComponent.this.callback = callback;
        }

        @Override
        public CloseOperationState canCloseElement() {
            return CloseOperationState.STATE_OK;
        }
    }
}
