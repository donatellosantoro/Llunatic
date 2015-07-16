package it.unibas.lunatic.model.chase.chasemc.operators;

import it.unibas.lunatic.LunaticConstants;
import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.model.chase.chasemc.CellGroup;
import it.unibas.lunatic.model.chase.chasemc.CellGroupCell;
import it.unibas.lunatic.model.chase.chasemc.ChangeSet;
import it.unibas.lunatic.model.database.ConstantValue;
import it.unibas.lunatic.model.chase.chasemc.DeltaChaseStep;
import it.unibas.lunatic.model.chase.chasemc.partialorder.IPartialOrder;
import java.util.Collections;

public class AddUserNode {

    private ChangeCell cellChanger;

    public AddUserNode(ChangeCell cellChanger) {
        this.cellChanger = cellChanger;
    }

    public DeltaChaseStep addUserNode(DeltaChaseStep father, Scenario scenario) {
        DeltaChaseStep newStep = new DeltaChaseStep(scenario, father, LunaticConstants.CHASE_USER, LunaticConstants.CHASE_USER);
        newStep.setEditedByUser(true);
        father.addChild(newStep);
        return newStep;
    }

    @SuppressWarnings("unchecked")
    public CellGroup addChange(DeltaChaseStep userNode, CellGroup cellGroup, ConstantValue newValue, Scenario scenario) {
        if (!userNode.isEditedByUser()) {
            throw new IllegalArgumentException("Changes are allowed only into nodes edited by user");
        }
        CellGroup newCellGroup = cellGroup.clone();
        CellGroupCell userCell = new CellGroupCell(LunaticConstants.USER_CELL_REF, newValue, newValue, LunaticConstants.TYPE_USER, true);
        newCellGroup.addUserCell(userCell);
        IPartialOrder po = scenario.getPartialOrder();
        po.setCellGroupValue(newCellGroup, scenario);
        ChangeSet changeSet = new ChangeSet(newCellGroup, LunaticConstants.CHASE_USER, Collections.EMPTY_LIST);
        cellChanger.changeCells(changeSet, userNode.getDeltaDB(), userNode.getId(), scenario);
        return newCellGroup;
    }
}
