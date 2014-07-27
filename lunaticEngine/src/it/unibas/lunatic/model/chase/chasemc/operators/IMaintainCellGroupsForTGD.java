package it.unibas.lunatic.model.chase.chasemc.operators;

import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.model.algebra.IAlgebraOperator;
import it.unibas.lunatic.model.chase.chasemc.TGDViolation;
import it.unibas.lunatic.model.database.IDatabase;
import it.unibas.lunatic.model.dependency.Dependency;
import java.util.Set;


public interface IMaintainCellGroupsForTGD {

    Set<TGDViolation> extractViolationValues(Dependency extTGD, IAlgebraOperator tgdQuery, IDatabase databaseForStep, Scenario scenario);
    void maintainCellGroupsForTGD(Dependency extTGD, IAlgebraOperator tgdSatisfactionQuery, Set<TGDViolation> tgdViolations, IDatabase deltaDB, String stepId, IDatabase databaseForStep, Scenario scenario);

}
