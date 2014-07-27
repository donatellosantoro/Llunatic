package it.unibas.lunatic.model.algebra.sql;

import it.unibas.lunatic.LunaticConstants;
import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.model.algebra.IAlgebraOperator;
import it.unibas.lunatic.model.algebra.operators.BuildAlgebraTree;
import it.unibas.lunatic.model.dependency.Dependency;

public class MaterializePremiseQueries {
    
    private AlgebraTreeToSQL queryBuilder = new AlgebraTreeToSQL();
    private BuildAlgebraTree treeBuilder = new BuildAlgebraTree();

    public String generateScript(Scenario scenario){
        StringBuilder result = new StringBuilder();
        result.append("----- Materializing queries of TGDs -----\n");
        for (Dependency dependency : scenario.getSTTgds()) {
            IAlgebraOperator operator = treeBuilder.buildTreeForPremise(dependency, scenario);
            result.append("CREATE TABLE ").append(LunaticConstants.WORK_SCHEMA).append(".").append(dependency.getId()).append(" AS\n");
            result.append(queryBuilder.treeToSQL(operator, scenario, LunaticConstants.INDENT));
            result.append(";\n\n");
        }
        result.append("\n");
        return result.toString();
    }
    
}
