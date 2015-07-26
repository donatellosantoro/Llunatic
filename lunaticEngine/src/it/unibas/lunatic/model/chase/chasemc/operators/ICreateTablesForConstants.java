package it.unibas.lunatic.model.chase.chasemc.operators;

import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.model.dependency.ConstantsInFormula;

public interface ICreateTablesForConstants {

    public void createTable(ConstantsInFormula constantsInFormula, Scenario scenario);

}
