package it.unibas.lunatic.model.chase.chasemc.operators;

import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.model.algebra.IAlgebraOperator;
import it.unibas.lunatic.model.chase.commons.control.IChaseState;
import it.unibas.lunatic.model.chase.chasemc.DeltaChaseStep;
import it.unibas.lunatic.model.dependency.Dependency;
import java.util.Map;


public interface IChaseDeltaExtTGDs {

    boolean doChase(DeltaChaseStep treeRoot, Scenario scenario, IChaseState chaseState, Map<Dependency, IAlgebraOperator> tgdTreeMap, Map<Dependency, IAlgebraOperator> tgdQuerySatisfactionMap);

}
