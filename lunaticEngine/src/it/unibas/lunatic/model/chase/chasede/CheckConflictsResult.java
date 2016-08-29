package it.unibas.lunatic.model.chase.chasede;

import it.unibas.lunatic.model.chase.chasemc.ChaseTree;
import java.util.Map;
import java.util.Set;
import speedy.model.database.AttributeRef;
import speedy.model.database.IDatabase;

public class CheckConflictsResult {

    private ChaseTree chaseResult;
    private Map<String, Set<AttributeRef>> constantsToRemove;

    public CheckConflictsResult(ChaseTree chaseResult, Map<String, Set<AttributeRef>> constantsToRemove) {
        this.chaseResult = chaseResult;
        this.constantsToRemove = constantsToRemove;
    }

    public ChaseTree getChaseResult() {
        return chaseResult;
    }

    public Map<String, Set<AttributeRef>> getConstantsToRemove() {
        return constantsToRemove;
    }

}
