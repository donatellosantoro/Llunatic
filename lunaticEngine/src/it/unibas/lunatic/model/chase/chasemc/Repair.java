package it.unibas.lunatic.model.chase.chasemc;

import it.unibas.lunatic.LunaticConstants;
import java.util.ArrayList;
import java.util.List;

public class Repair {

    private List<ViolationContext> violationContexts = new ArrayList<ViolationContext>();
    private boolean suspicious;

    public List<ViolationContext> getViolationContexts() {
        return violationContexts;
    }

    public void setViolationContexts(List<ViolationContext> violationContexts) {
        this.violationContexts = violationContexts;
    }

    public void addViolationContext(ViolationContext changeSet) {
        if (changeSet == null) {
            throw new IllegalArgumentException("Unable to add null changeSet");
        }
        this.violationContexts.add(changeSet);
    }

    public boolean isSuspicious() {
        return suspicious;
    }

    public void setSuspicious(boolean suspicious) {
        this.suspicious = suspicious;
    }

    public String getChaseModes() {
        boolean forward = false;
        boolean backward = false;
        for (ViolationContext changeSet : violationContexts) {
            if (changeSet.getChaseMode().equals(LunaticConstants.CHASE_FORWARD)) {
                forward = true;
            }
        }
        for (ViolationContext changeSet : violationContexts) {
            if (changeSet.getChaseMode().equals(LunaticConstants.CHASE_BACKWARD)) {
                backward = true;
            }
        }
        if (forward && backward) {
            return "fb";
        }
        if (backward) {
            return "b";
        }
        return "f";
    }

    public boolean isOnlyForward() {
        for (ViolationContext changeSet : violationContexts) {
            if (changeSet.getChaseMode().equals(LunaticConstants.CHASE_BACKWARD)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        final Repair other = (Repair) obj;
        if (this.violationContexts != other.violationContexts && (this.violationContexts == null || !this.violationContexts.equals(other.violationContexts))) return false;
        if (this.suspicious != other.suspicious) return false;
        return true;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append("Repair: ").append(suspicious ? " (suspicious)" : "").append("\n");
        for (ViolationContext changeSet : violationContexts) {
            result.append("\t").append(changeSet).append("\n");
        }
        return result.toString();
    }
}
