package it.unibas.lunatic.gui.node.scenario;

import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.model.dependency.DED;
import java.util.List;

public class DepCounter {

    public int getNumber(Scenario scenario) {
        int deps = 0;
        deps += count(scenario.getDEDEGDs());
        deps += count(scenario.getDEDextTGDs());
//        deps += count(scenario.getDEDstTGDs());
        deps += scenario.getDCs().size();
        deps += scenario.getEGDs().size();
        deps += scenario.getExtEGDs().size();
        deps += scenario.getExtTGDs().size();
        deps += scenario.getSTTgds().size();
        return deps;
    }

    public int count(List<DED> dedList) {
        int count = 0;
        for (DED ded : dedList) {
            count += ded.getAssociatedDependencies().size();
        }
        return count;
    }
}
