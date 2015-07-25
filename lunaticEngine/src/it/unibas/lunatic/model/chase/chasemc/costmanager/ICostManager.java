package it.unibas.lunatic.model.chase.chasemc.costmanager;

import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.model.chase.chasemc.CellGroup;
import it.unibas.lunatic.model.chase.chasemc.ChaseMCScenario;
import it.unibas.lunatic.model.chase.chasemc.EquivalenceClassForEGD;
import it.unibas.lunatic.model.chase.chasemc.Repair;
import it.unibas.lunatic.model.chase.chasemc.DeltaChaseStep;
import it.unibas.lunatic.model.chase.chasemc.operators.OccurrenceHandlerMC;
import it.unibas.lunatic.model.dependency.Dependency;
import java.util.List;

public interface ICostManager {

    public List<Repair> chooseRepairStrategy(
            EquivalenceClassForEGD equivalenceClass, DeltaChaseStep chaseTreeRoot,
            List<Repair> repairsForDependency,
            Scenario scenario, String stepId, OccurrenceHandlerMC occurrenceHandler);
    
    public ChaseMCScenario getChaser(Scenario scenario);
    public List<Dependency> selectDependenciesToChase(List<Dependency> unsatisfiedDependencies, DeltaChaseStep chaseRoot);
    public boolean isDoBackward();
    public boolean isDoPermutations();
    public void setDoBackward(boolean doBackward);
    public void setDoPermutations(boolean doPermutations);
    public int getDependencyLimit() ;
    public void setDependencyLimit(int dependencyLimit);
    public int getChaseBranchingThreshold();
    public void setChaseBranchingThreshold(int chaseBranchingThreshold);
    public int getPotentialSolutionsThreshold();
    public void setPotentialSolutionsThreshold(int PotentialSolutionsThreshold);
    public boolean checkContainment(List<CellGroup> cellGroups, Scenario scenario);
    public String toLongString();
}
