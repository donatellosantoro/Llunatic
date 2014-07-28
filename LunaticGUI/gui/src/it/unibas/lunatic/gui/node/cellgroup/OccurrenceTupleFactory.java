package it.unibas.lunatic.gui.node.cellgroup;

import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.core.CellGroupHelper;
import it.unibas.lunatic.model.chase.chasemc.DeltaChaseStep;
import it.unibas.lunatic.model.database.CellRef;
import it.unibas.lunatic.model.database.IValue;
import java.util.List;
import java.util.Set;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Node;

public class OccurrenceTupleFactory extends ChildFactory<CellRef> {

    private DeltaChaseStep chaseStep;
    private final Set<CellRef> occurrences;
    private CellGroupHelper cgHelper = CellGroupHelper.getInstance();
    private Scenario scenario;
    private final StepCellGroupNode cellGroupNode;

    public OccurrenceTupleFactory(StepCellGroupNode cellGroupNode) {
        this.cellGroupNode = cellGroupNode;
        this.occurrences = cellGroupNode.getCellGroup().getOccurrences();
        this.scenario = cellGroupNode.getChaseStepNode().getScenario();
        this.chaseStep = cellGroupNode.getChaseStep();
    }

    @Override
    protected boolean createKeys(List<CellRef> toPopulate) {
        for (CellRef cr : occurrences) {
            if (Thread.interrupted()) {
                return false;
            }
            toPopulate.add(cr);
        }
        return true;
    }

    @Override
    protected Node createNodeForKey(CellRef cellRef) {
        IValue original = cgHelper.getValueExtractor(scenario).getOriginalValue(cellRef, chaseStep.getDeltaDB());
        OccurrenceTupleNode occurrence = new OccurrenceTupleNode(cellGroupNode.getChaseStepNode(), cellRef, original);
        if (!chaseStep.isRoot()) {
            IValue prev = cgHelper.getValueExtractor(scenario).getPreviousValue(cellRef, chaseStep);
            occurrence.setPreviousValue(prev);
        }
        return occurrence;
    }
}
