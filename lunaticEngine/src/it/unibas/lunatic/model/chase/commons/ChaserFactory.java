package it.unibas.lunatic.model.chase.commons;

import it.unibas.lunatic.OperatorFactory;
import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.model.chase.chasemc.ChaseMCScenario;
import it.unibas.lunatic.model.chase.chasemc.operators.ChaseDeltaExtEGDs;
import it.unibas.lunatic.model.chase.chasemc.operators.CheckSolution;
import it.unibas.lunatic.model.chase.chasemc.operators.IBuildDatabaseForChaseStep;
import it.unibas.lunatic.model.chase.chasemc.operators.IBuildDeltaDB;
import it.unibas.lunatic.model.chase.chasemc.operators.IChaseDeltaExtTGDs;
import it.unibas.lunatic.model.chase.chasemc.operators.OccurrenceHandlerMC;
import speedy.model.algebra.operators.IInsertTuple;
import speedy.model.database.operators.IRunQuery;

public class ChaserFactory {

    public static ChaseMCScenario getChaser(Scenario scenario) {
        IChaseSTTGDs stChaser = OperatorFactory.getInstance().getSTChaser(scenario);
        IBuildDeltaDB deltaBuilder = OperatorFactory.getInstance().getDeltaDBBuilder(scenario);
        IBuildDatabaseForChaseStep stepBuilder = OperatorFactory.getInstance().getDatabaseBuilder(scenario);
        IRunQuery queryRunner = OperatorFactory.getInstance().getQueryRunner(scenario);
        IInsertTuple insertOperatorForEgds = OperatorFactory.getInstance().getInsertTuple(scenario);
        OccurrenceHandlerMC occurrenceHandler = OperatorFactory.getInstance().getOccurrenceHandlerMC(scenario);
        IChaseDeltaExtTGDs extTgdChaser = OperatorFactory.getInstance().getExtTgdChaser(scenario);
        CheckSolution solutionChecker = OperatorFactory.getInstance().getSolutionChecker(scenario);
        ChaseDeltaExtEGDs egdChaser = OperatorFactory.getInstance().getEGDChaser(scenario);
        return new ChaseMCScenario(stChaser, extTgdChaser, deltaBuilder, stepBuilder, queryRunner, insertOperatorForEgds, occurrenceHandler, egdChaser, solutionChecker);
    }
}
