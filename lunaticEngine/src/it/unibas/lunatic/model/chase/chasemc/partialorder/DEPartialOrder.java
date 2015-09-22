package it.unibas.lunatic.model.chase.chasemc.partialorder;

import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.exceptions.ChaseFailedException;
import it.unibas.lunatic.model.chase.chasemc.CellGroup;
import it.unibas.lunatic.model.chase.chasemc.CellGroupCell;
import speedy.model.database.IValue;
import it.unibas.lunatic.utility.LunaticUtility;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import speedy.model.database.LLUNValue;

public class DEPartialOrder extends StandardPartialOrder {
    
    private static Logger logger = LoggerFactory.getLogger(DEPartialOrder.class);
    
    @Override
    public IValue generalizeNonAuthoritativeConstantCells(Set<CellGroupCell> nonAuthoritativeCells, CellGroup cellGroup, Scenario scenario) {
        IValue lubValue = super.generalizeNonAuthoritativeConstantCells(nonAuthoritativeCells, cellGroup, scenario);
        if (lubValue instanceof LLUNValue) {
            throw new ChaseFailedException("Unable to equate cells " + LunaticUtility.printCollection(nonAuthoritativeCells));
        }
        return lubValue;
    }
    
    @Override
    public String toString() {
        return "DEPartialOrder";
    }
}
