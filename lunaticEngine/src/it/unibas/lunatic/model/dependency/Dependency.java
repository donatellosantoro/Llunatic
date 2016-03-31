package it.unibas.lunatic.model.dependency;

import it.unibas.lunatic.model.dependency.operators.CloneDependency;
import it.unibas.lunatic.model.dependency.operators.DependencyToString;
import it.unibas.lunatic.model.dependency.operators.IFormulaVisitor;
import it.unibas.lunatic.model.generators.IValueGenerator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import speedy.model.database.AttributeRef;

public class Dependency implements Cloneable {

    private String id;
    private IFormula premise;
    private IFormula conclusion;
    private String type;
    private List<AttributeRef> additionalAttributes = new ArrayList<AttributeRef>();
    private List<ExtendedEGD> extendedDependencies = new ArrayList<ExtendedEGD>();
    private Map<AttributeRef, IValueGenerator> targetGenerators = new HashMap<AttributeRef, IValueGenerator>();
    private List<AttributeRef> queriedAttributes = new ArrayList<AttributeRef>();
    private List<AttributeRef> affectedAttributes = new ArrayList<AttributeRef>();
    private List<FormulaVariableOccurrence> backwardAttributes = new ArrayList<FormulaVariableOccurrence>();
    private SymmetricAtoms symmetricAtoms = new SymmetricAtoms();
    private boolean joinGraphIsCyclic; // R(v1, v1)
    private boolean overlapBetweenAffectedAndQueried;

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

    public void setAdditionalAttributes(List<AttributeRef> additionalAttributes) {
        this.additionalAttributes = additionalAttributes;
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

    public void setTargetGenerators(Map<AttributeRef, IValueGenerator> targetGenerators) {
        this.targetGenerators = targetGenerators;
    }

    public List<ExtendedEGD> getExtendedDependencies() {
        return extendedDependencies;
    }

    public void setExtendedDependencies(List<ExtendedEGD> extendedDependencies) {
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

    public List<FormulaVariableOccurrence> getBackwardAttributes() {
        return backwardAttributes;
    }

    public void setBackwardAttributes(List<FormulaVariableOccurrence> backwardAttributes) {
        this.backwardAttributes = backwardAttributes;
    }

    public boolean hasSymmetricChase() {
        // Symmetric chase is used only for FDs
        return this.symmetricAtoms.getSize() == 1 && !hasNegations() && !isOverlapBetweenAffectedAndQueried();
        // in case there are overlaps, conclusion variables become part of the witness, and therefore generate different equivalence classes
//        return !this.symmetricAtoms.isEmpty() && !hasNegations() && !isOverlapBetweenAffectedAndQueried();
    }

    public SymmetricAtoms getSymmetricAtoms() {
        return symmetricAtoms;
    }

    public void setSymmetricAtoms(SymmetricAtoms symmetricAtoms) {
        this.symmetricAtoms = symmetricAtoms;
    }

    public boolean joinGraphIsCyclic() {
        return joinGraphIsCyclic;
    }

    public void setJoinGraphIsCyclic(boolean joinGraphIsCyclic) {
        this.joinGraphIsCyclic = joinGraphIsCyclic;
    }

    public boolean hasNegations() {
        return !this.premise.getNegatedSubFormulas().isEmpty();
    }

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
        return new CloneDependency().clone(this);
    }

    public Dependency superficialClone() {
        try {
            return (Dependency) super.clone();
        } catch (CloneNotSupportedException ex) {
            throw new UnsupportedOperationException("Unable to clone dependency");
        }
    }

    @Override
    public String toString() {
        return this.id;
    }

    public String toLogicalString() {
        return new DependencyToString().toLogicalString(this, "", false);
    }

    public String toLongString() {
        StringBuilder result = new StringBuilder();
        result.append(this.toLogicalString());
        result.append("  Premise: ").append(this.getPremise().getPositiveFormula().toLongString()).append("\n");
        result.append("  Conclusion: ").append(this.getConclusion().getPositiveFormula().toLongString()).append("\n");
        result.append("  Type: ").append(type).append("\n");
        result.append("  Queried attributes: ").append(queriedAttributes).append("\n");
        result.append("  Affected attributes: ").append(affectedAttributes).append("\n");
        result.append("  Overlap between queried and affected: ").append(overlapBetweenAffectedAndQueried).append("\n");
        result.append("  Symmetric atoms: ").append(symmetricAtoms).append("\n");
        result.append("  Has Symmetric Atoms: ").append(hasSymmetricChase()).append("\n");
        result.append("  Join Graph Is Cyclic: ").append(joinGraphIsCyclic).append("\n");
//        result.append("  Extended dependencies:\n");
//        for (ExtendedDependency extendedDependency : extendedDependencies) {
//            result.append("      ").append(extendedDependency.toString());
//        }
        return result.toString();
    }
}
