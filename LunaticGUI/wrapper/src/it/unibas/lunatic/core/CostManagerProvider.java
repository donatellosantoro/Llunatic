package it.unibas.lunatic.core;

import it.unibas.lunatic.LunaticConstants;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CostManagerProvider {

    private static CostManagerProvider instance;

    public static CostManagerProvider getInstance() {
        if (instance == null) {
            instance = new CostManagerProvider();
        }
        return instance;
    }

    public Collection<String> getAll() {
        List<String> list = new ArrayList<String>();
        list.add(LunaticConstants.COST_MANAGER_STANDARD);
        list.add(LunaticConstants.COST_MANAGER_SIMILARITY);
        list.add(LunaticConstants.COST_MANAGER_SAMPLING);
        list.add(LunaticConstants.COST_MANAGER_GREEDY);
        return list;
    }
}
