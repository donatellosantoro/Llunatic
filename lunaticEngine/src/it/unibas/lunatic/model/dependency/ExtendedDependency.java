package it.unibas.lunatic.model.dependency;

import it.unibas.lunatic.LunaticConstants;
import java.util.List;
import speedy.model.database.AttributeRef;

public class ExtendedDependency {
    
    private String id;
    private Dependency dependency;
    private String chaseMode;
    private FormulaVariableOccurrence occurrence;    
    private List<AttributeRef> affectedAttributes;
    private List<AttributeRef> localAffectedAttributes;

    public ExtendedDependency(String id, Dependency dependency, String chaseMode) {
        this.id = id;
        this.dependency = dependency;
        this.chaseMode = chaseMode;
    }

    public ExtendedDependency(String id, Dependency dependency, String chaseMode, FormulaVariableOccurrence occurrence) {
        this.id = id;
        this.dependency = dependency;
        this.chaseMode = chaseMode;
        this.occurrence = occurrence;
    }

    public String getId() {
        return id;
    }

    public Dependency getDependency() {
        return dependency;
    }

    public String getChaseMode() {
        return chaseMode;
    }

    public FormulaVariableOccurrence getOccurrence() {
        return occurrence;
    }

    public List<AttributeRef> getAffectedAttributes() {
        return affectedAttributes;
    }

    public void setAffectedAttributes(List<AttributeRef> affectedAttributes) {
        this.affectedAttributes = affectedAttributes;
    }

    public List<AttributeRef> getQueriedAttributes() {
        return this.dependency.getQueriedAttributes();
    }

    public List<AttributeRef> getLocalAffectedAttributes() {
        return localAffectedAttributes;
    }

    public void setLocalAffectedAttributes(List<AttributeRef> localAffectedAttributes) {
        this.localAffectedAttributes = localAffectedAttributes;
    }

    public boolean isForward() {
        return this.chaseMode.equals(LunaticConstants.CHASE_FORWARD);
    }

    public boolean isBackward() {
        return !isForward();
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 17 * hash + (this.id != null ? this.id.hashCode() : 0);
        hash = 17 * hash + (this.chaseMode != null ? this.chaseMode.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        final ExtendedDependency other = (ExtendedDependency) obj;
        if ((this.id == null) ? (other.id != null) : !this.id.equals(other.id)) return false;
        if ((this.chaseMode == null) ? (other.chaseMode != null) : !this.chaseMode.equals(other.chaseMode)) return false;
        return true;
    }
    
    @Override
    public String toString() {
        return id + ": " + dependency + (occurrence != null ? "   occurrence=" + occurrence.toLongString() + "\n" : "");
    }

    public String toLongString() {
        StringBuilder result = new StringBuilder();
        result.append(toString());
        result.append("    ").append("Queried attributes: ").append(getQueriedAttributes()).append("\n");
        result.append("    ").append("Local affected attributes: ").append(localAffectedAttributes).append("\n");
        result.append("    ").append("Affected attributes: ").append(affectedAttributes).append("\n");
        return result.toString();
    }
    

}
