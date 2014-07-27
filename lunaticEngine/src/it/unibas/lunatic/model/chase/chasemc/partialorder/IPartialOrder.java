package it.unibas.lunatic.model.chase.chasemc.partialorder;

import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.exceptions.PartialOrderException;
import it.unibas.lunatic.model.chase.chasemc.CellGroup;
import it.unibas.lunatic.model.database.IValue;
import java.util.List;

public interface IPartialOrder {

    public CellGroup findLUB(List<CellGroup> cellGroups, Scenario scenario) throws PartialOrderException;
    
    public CellGroup mergeCellGroups(CellGroup group1, CellGroup group2, IValue newValue, Scenario scenario);
    
    
}
