package it.unibas.lunatic.model.algebra.operators;

import it.unibas.lunatic.model.algebra.*;

public interface IAlgebraTreeVisitor {

    void visitScan(Scan operator);
    void visitSelect(Select operator);
    void visitDistinct(Distinct operator);
    void visitSelectIn(SelectIn operator);
    void visitJoin(Join operator);
    void visitCartesianProduct(CartesianProduct operator);
    void visitProject(Project operator);
    void visitDifference(Difference operator);
    void visitUnion(Union operator);
    void visitGroupBy(GroupBy operator);
    void visitOrderBy(OrderBy operator);
    void visitLimit(Limit operator);
    void visitRestoreOIDs(RestoreOIDs operator);
    void visitCreateTable(CreateTableAs operator);
    Object getResult();
}
