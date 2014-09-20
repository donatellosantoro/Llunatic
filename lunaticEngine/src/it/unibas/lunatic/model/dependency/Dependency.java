package it.unibas.lunatic.model.dependency;

import it.unibas.lunatic.model.chase.chasemc.BackwardAttribute;
import it.unibas.lunatic.model.database.AttributeRef;
import it.unibas.lunatic.model.dependency.operators.DependencyToString;
import it.unibas.lunatic.model.dependency.operators.IFormulaVisitor;
import it.unibas.lunatic.model.generators.IValueGenerator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Dependency implements Cloneable {

    private String id;
    private IFormula premise;
    private IFormula conclusion;
    private String type;
    private List<AttributeRef> additionalAttributes = new ArrayList<AttributeRef>();
    private List<ExtendedDependency> extendedDependencies = new ArrayList<ExtendedDependency>();
    private Map<AttributeRef, IValueGenerator> targetGenerators = new HashMap<AttributeRef, IValueGenerator>();
    private List<AttributeRef> queriedAttributes = new ArrayList<AttributeRef>();
    private List<AttributeRef> affectedAttributes = new ArrayList<AttributeRef>();
    private List<BackwardAttribute> attributesForBackwardChasing = new ArrayList<BackwardAttribute>();
    private SymmetricAtoms symmetricAtoms = new SymmetricAtoms();
    private boolean overlapBetweenAffectedAndQueried;
//    private boolean recursive;

    public Dependency() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        if (this.id != null) {
            return;
        }
        this.id = id;
    }

    public void addSuffixId(String suffixId) {
        this.id += suffixId;
    }

    public IFormula getConclusion() {
        return conclusion;
    }

    public IFormula getPremise() {
        return premise;
    }

    public String getType() {
        return type;
    }

    public void setConclusion(IFormula conclusion) {
        this.conclusion = conclusion;
    }

    public void setPremise(IFormula premise) {
        this.premise = premise;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<AttributeRef> getAdditionalAttributes() {
        return additionalAttributes;
    }

    public void addAdditionalAttribute(AttributeRef attributeRef) {
        this.additionalAttributes.add(attributeRef);
    }

    public void accept(IFormulaVisitor visitor) {
        visitor.visitDependency(this);
    }

    public void addTargetGenerator(AttributeRef targetAttribute, IValueGenerator generator) {
        this.targetGenerators.put(targetAttribute, generator);
    }

    public Map<AttributeRef, IValueGenerator> getTargetGenerators() {
        return targetGenerators;
    }

    public List<ExtendedDependency> getExtendedDependencies() {
        return extendedDependencies;
    }

    public void setExtendedDependencies(List<ExtendedDependency> extendedDependencies) {
        this.extendedDependencies = extendedDependencies;
    }

    public boolean isOverlapBetweenAffectedAndQueried() {
        return overlapBetweenAffectedAndQueried;
    }

    public void setOverlapBetweenAffectedAndQueried(boolean overlapBetweenAffectedAndQueried) {
        this.overlapBetweenAffectedAndQueried = overlapBetweenAffectedAndQueried;
    }

    public List<AttributeRef> getQueriedAttributes() {
        return queriedAttributes;
    }

    public void setQueriedAttributes(List<AttributeRef> queriedAttributes) {
        this.queriedAttributes = queriedAttributes;
    }

    public List<AttributeRef> getAffectedAttributes() {
        return affectedAttributes;
    }

    public void setAffectedAttributes(List<AttributeRef> affectedAttributes) {
        this.affectedAttributes = affectedAttributes;
    }

    public List<BackwardAttribute> getAttributesForBackwardChasing() {
        return attributesForBackwardChasing;
    }

    public void setAttributesForBackwardChasing(List<BackwardAttribute> attributesForBackwardChasing) {
        this.attributesForBackwardChasing = attributesForBackwardChasing;
    }

    public boolean hasSymmetricAtoms() {
        // in case there are overlaps, conclusion variables become part of the witness, and therefore generate different equivalence classes
        return !this.symmetricAtoms.isEmpty() && !hasNegations() && !isOverlapBetweenAffectedAndQueried();
    }

    public SymmetricAtoms getSymmetricAtoms() {
        return symmetricAtoms;
    }

    public void setSymmetricAtoms(SymmetricAtoms symmetricAtoms) {
        this.symmetricAtoms = symmetricAtoms;
    }

//    public String getTableNameForSymmetricAtom() {
//        return this.symmetricAtoms.getSelfJoin().getAtoms().get(0).getTableName();
//    }

    public boolean hasNegations() {
        return !this.premise.getNegatedSubFormulas().isEmpty();
    }

//    public boolean isRecursive() {
//        return recursive;
//    }
//
//    public void setRecursive(boolean recursive) {
//        this.recursive = recursive;
//    }
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 83 * hash + (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        final Dependency other = (Dependency) obj;
        if ((this.id == null) ? (other.id != null) : !this.id.equals(other.id)) return false;
        return true;
    }

    @Override
    public Dependency clone() {
        Dependency clone = null;
        try {
            clone = (Dependency) super.clone();
            clone.premise = premise.clone();
            clone.conclusion = conclusion.clone();
            clone.additionalAttributes = new ArrayList<AttributeRef>();
            for (AttributeRef attributeRef : this.additionalAttributes) {
                clone.additionalAttributes.add(new AttributeRef(attributeRef.getTableAlias(), attributeRef.getName()));
            }
            clone.extendedDependencies = new ArrayList<ExtendedDependency>();
            if (!this.extendedDependencies.isEmpty()) {
                throw new UnsupportedOperationException("ExtendedDependency clone is not supported");
            }
            clone.targetGenerators = new HashMap<AttributeRef, IValueGenerator>();
            for (AttributeRef attributeRef : this.targetGenerators.keySet()) {
                clone.targetGenerators.put(attributeRef, this.targetGenerators.get(attributeRef).clone());
            }
            clone.queriedAttributes = new ArrayList<AttributeRef>();
            for (AttributeRef attributeRef : this.queriedAttributes) {
                clone.queriedAttributes.add(new AttributeRef(attributeRef.getTableAlias(), attributeRef.getName()));
            }
        } catch (CloneNotSupportedException ex) {
        }
        return clone;
    }

    @Override
    public String toString() {
        return new DependencyToString().toLogicalString(this, "", false);
    }

    public String toLongString() {
        StringBuilder result = new StringBuilder();
        result.append(this.toString());
        result.append("  Type: ").append(type).append("\n");
        result.append("  Queried attributes: ").append(queriedAttributes).append("\n");
        result.append("  Affected attributes: ").append(affectedAttributes).append("\n");
        result.append("  Overlap between queried and affected: ").append(overlapBetweenAffectedAndQueried).append("\n");
        result.append("  Attributes for backward chasing: ").append(attributesForBackwardChasing).append("\n");
        result.append("  Symmetric atoms: ").append(symmetricAtoms).append("\n");
        result.append("  Has Symmetric Atoms: ").append(hasSymmetricAtoms()).append("\n");
//        result.append("  Extended dependencies:\n");
//        for (ExtendedDependency extendedDependency : extendedDependencies) {
//            result.append("      ").append(extendedDependency.toString());
//        }
        return result.toString();
    }
}
