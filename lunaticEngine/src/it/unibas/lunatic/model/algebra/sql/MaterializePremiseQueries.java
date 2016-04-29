package it.unibas.lunatic.model.algebra.sql;

import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.model.algebra.operators.BuildAlgebraTree;
import it.unibas.lunatic.model.dependency.Dependency;
import it.unibas.lunatic.persistence.relational.LunaticDBMSUtility;
import speedy.SpeedyConstants;
import speedy.model.algebra.IAlgebraOperator;
import speedy.model.algebra.operators.sql.AlgebraTreeToSQL;

public class MaterializePremiseQueries {

    private AlgebraTreeToSQL queryBuilder = new AlgebraTreeToSQL();
    private BuildAlgebraTree treeBuilder = new BuildAlgebraTree();

    public String generateScript(Scenario scenario) {
        StringBuilder result = new StringBuilder();
        result.append("----- Materializing queries of TGDs -----\n");
        for (Dependency dependency : scenario.getSTTgds()) {
            IAlgebraOperator operator = treeBuilder.buildTreeForPremise(dependency, scenario);
            String unloggedOption = (scenario.getConfiguration().isUseUnloggedWorkTables() ? " UNLOGGED " : "");
            result.append("CREATE ").append(unloggedOption).append(" TABLE ").append(LunaticDBMSUtility.getWorkSchema(scenario)).append(".").append(dependency.getId()).append(" AS\n");
            result.append(queryBuilder.treeToSQL(operator, scenario.getSource(), scenario.getTarget(), SpeedyConstants.INDENT));
            result.append(";\n\n");
        }
        result.append("\n");
        return result.toString();
    }

}
