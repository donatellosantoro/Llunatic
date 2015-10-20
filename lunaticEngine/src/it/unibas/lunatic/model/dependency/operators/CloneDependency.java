package it.unibas.lunatic.model.dependency.operators;

import speedy.model.database.AttributeRef;
import it.unibas.lunatic.model.dependency.Dependency;
import it.unibas.lunatic.model.dependency.ExtendedDependency;
import it.unibas.lunatic.model.generators.IValueGenerator;
import java.util.ArrayList;
import java.util.HashMap;

public class CloneDependency {

    public Dependency clone(Dependency dependency) {
        Dependency clone = dependency.superficialClone();
        CloneFormulaVisitor cloner = new CloneFormulaVisitor();
        dependency.getPremise().accept(cloner);
        dependency.setPremise(cloner.getResult());
        cloner.reset();
        dependency.getConclusion().accept(cloner);
        dependency.setConclusion(cloner.getResult());
        clone.setAdditionalAttributes(new ArrayList<AttributeRef>());
        for (AttributeRef attributeRef : dependency.getAdditionalAttributes()) {
            clone.getAdditionalAttributes().add(new AttributeRef(attributeRef.getTableAlias(), attributeRef.getName()));
        }
        clone.setExtendedDependencies(new ArrayList<ExtendedDependency>());
        if (!dependency.getExtendedDependencies().isEmpty()) {
            throw new UnsupportedOperationException("ExtendedDependency clone is not supported");
        }
        clone.setTargetGenerators(new HashMap<AttributeRef, IValueGenerator>());
        for (AttributeRef attributeRef : dependency.getTargetGenerators().keySet()) {
            clone.getTargetGenerators().put(attributeRef, dependency.getTargetGenerators().get(attributeRef).clone());
        }
        clone.setQueriedAttributes(new ArrayList<AttributeRef>());
        for (AttributeRef attributeRef : dependency.getQueriedAttributes()) {
            clone.getQueriedAttributes().add(new AttributeRef(attributeRef.getTableAlias(), attributeRef.getName()));
        }
        return clone;

    }
}
