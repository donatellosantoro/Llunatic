package it.unibas.lunatic.model.chase.chasemc.operators;

import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.model.chase.commons.IChaseState;
import it.unibas.lunatic.model.chase.chasemc.DeltaChaseStep;
import it.unibas.lunatic.model.dependency.Dependency;
import java.util.Map;
import speedy.model.algebra.IAlgebraOperator;
import speedy.model.database.IDatabase;

public interface IChaseDeltaExtTGDs {

    boolean doChase(DeltaChaseStep treeRoot, Scenario scenario, IChaseState chaseState, Map<Dependency, IAlgebraOperator> tgdTreeMap, Map<Dependency, IAlgebraOperator> tgdQuerySatisfactionMap);

    public void initializeOIDs(IDatabase targetDB, Scenario scenario);

}
