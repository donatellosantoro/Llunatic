/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unibas.lunatic.gui.window.dependencies;

import it.unibas.lunatic.AbstractSelectionListener;
import it.unibas.lunatic.IApplication;
import it.unibas.lunatic.gui.node.dependencies.DepTupleNode;
import it.unibas.lunatic.gui.visualdeps.DependencyGraph;
import it.unibas.lunatic.gui.visualdeps.IDependencySceneGenerator;
import it.unibas.lunatic.gui.visualdeps.generator.LunaticDepSceneGeneratorHelper;
import it.unibas.lunatic.gui.window.dependencies.VisualDepsTopComponent;
import it.unibas.lunatic.model.dependency.operators.DependencyToString;
import java.util.Collection;
import org.netbeans.api.visual.widget.Scene;
import org.openide.util.Lookup;

/**
 *
 * @author Antonio Galotta
 */
public class DependencySceneBinder extends AbstractSelectionListener<DepTupleNode> {

    private IApplication app = Lookup.getDefault().lookup(IApplication.class);
    private VisualDepsTopComponent tc;
    private IDependencySceneGenerator dependencySceneGenerator = new LunaticDepSceneGeneratorHelper();
    private DependencyToString dependencyFormatter = new DependencyToString();

    @Override
    public void onChange(Collection<? extends DepTupleNode> collection) {
        DepTupleNode dep = getBean(collection);
        if (dep != null) {
            DependencyGraph dependencyGraph = dep.getDependencyGraph();
            if (dependencyGraph == null) {
                Scene scene = dependencySceneGenerator.createScene(dep.getDependency());
                dependencyGraph = new DependencyGraph(scene);
                dep.cacheDependencyGraph(dependencyGraph);
            }
            tc.getScenePane().setViewportView(dependencyGraph.getSceneView());
//            String depText = dependencyFormatter.toLogicalString(dep.getDependency(), "", true);
            tc.getDepTextArea().setText(dep.getDisplayName());
        }
    }

    public void register(VisualDepsTopComponent tc) {
        this.tc = tc;
        super.registerBean(DepTupleNode.class);
    }
}
