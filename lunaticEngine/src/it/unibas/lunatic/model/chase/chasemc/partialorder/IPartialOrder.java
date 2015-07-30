package it.unibas.lunatic.model.chase.chasemc.partialorder;

import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.exceptions.PartialOrderException;
import it.unibas.lunatic.model.chase.chasemc.CellGroup;
import it.unibas.lunatic.model.chase.chasemc.CellGroupCell;
import it.unibas.lunatic.model.database.IValue;
import java.util.List;
import java.util.Set;

public interface IPartialOrder {

    public CellGroup findLUB(List<CellGroup> cellGroups, Scenario scenario) throws PartialOrderException;
    
    public void setCellGroupValue(CellGroup lubCellGroup, Scenario scenario);

    public IValue generalizeNonAuthoritativeConstantCells(Set<CellGroupCell> nonAuthoritativeCells, CellGroup cellGroup, Scenario scenario);

}
