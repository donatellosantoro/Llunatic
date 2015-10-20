package it.unibas.lunatic.model.chase.chasemc.operators;

import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.model.dependency.AllConstantsInFormula;

public interface ICreateTablesForConstants {

    public void createTable(AllConstantsInFormula constantsInFormula, Scenario scenario, boolean autoritative);

}
