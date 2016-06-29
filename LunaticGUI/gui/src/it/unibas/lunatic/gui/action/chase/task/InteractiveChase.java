package it.unibas.lunatic.gui.action.chase.task;

import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.gui.R;
import it.unibas.lunatic.gui.model.IChaseResult;
import it.unibas.lunatic.gui.model.LoadedScenario;
import it.unibas.lunatic.gui.model.McChaseResult;
import it.unibas.lunatic.model.chase.chasemc.ChaseTree;
import it.unibas.lunatic.model.chase.chasemc.DeltaChaseStep;
import it.unibas.lunatic.model.chase.chasemc.operators.ChaseMCScenario;
import it.unibas.lunatic.model.chase.chasemc.operators.PrintRankedSolutions;
import it.unibas.lunatic.model.chase.chasemc.operators.RankSolutions;
import it.unibas.lunatic.model.chase.commons.IChaseState;
import it.unibas.lunatic.model.chase.commons.operators.ChaseUtility;
import it.unibas.lunatic.model.chase.commons.operators.ChaserFactoryMC;

public class InteractiveChase implements IChaseOperator {

    private final RankSolutions solutionRanker = new RankSolutions();
    private final PrintRankedSolutions solutionPrinter = new PrintRankedSolutions();

    @Override
    public IChaseResult chase(LoadedScenario loadedScenario) {
        assert loadedScenario.getScenario().isMCScenario();
        ChaseTree chaseTree = loadedScenario.get(R.BeanProperty.CHASE_RESULT, McChaseResult.class).getResult();
        IChaseState chaseState = loadedScenario.get(R.BeanProperty.CHASE_STATE, IChaseState.class);
        Scenario scenario = loadedScenario.getScenario();
        ChaseMCScenario chaser = ChaserFactoryMC.getChaser(scenario);
        DeltaChaseStep newRoot = chaser.doChase(chaseTree.getRoot(), scenario, chaseState);
        chaseTree.setRoot(newRoot);
        if (ChaseUtility.hasChaseStats(scenario)) {
            solutionRanker.rankSolutions(chaseTree);
            System.out.println(solutionPrinter.toString(chaseTree));
        }
        return new McChaseResult(loadedScenario, chaseTree);
    }
}
