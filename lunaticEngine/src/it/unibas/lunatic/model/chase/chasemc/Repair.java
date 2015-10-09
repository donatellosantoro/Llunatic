package it.unibas.lunatic.model.chase.chasemc;

import it.unibas.lunatic.LunaticConstants;
import java.util.ArrayList;
import java.util.List;

public class Repair {

    private List<ChangeDescription> changeDescriptions = new ArrayList<ChangeDescription>();
    private boolean suspicious = false;

    public List<ChangeDescription> getChangeDescriptions() {
        return changeDescriptions;
    }

    public void addViolationContext(ChangeDescription changeSet) {
        if (changeSet == null) {
            throw new IllegalArgumentException("Unable to add null changeSet");
        }
        this.changeDescriptions.add(changeSet);
    }

    public boolean isSuspicious() {
        return this.suspicious;
//        throw new IllegalArgumentException();
    }

    public void setSuspicious(boolean suspicious) {
        this.suspicious = suspicious;
//        throw new IllegalArgumentException();
    }

    public String getChaseModes() {
        boolean forward = false;
        boolean backward = false;
        for (ChangeDescription changeSet : changeDescriptions) {
            if (changeSet.getChaseMode().equals(LunaticConstants.CHASE_FORWARD)) {
                forward = true;
            }
        }
        for (ChangeDescription changeSet : changeDescriptions) {
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
        for (ChangeDescription changeSet : changeDescriptions) {
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
        if (this.changeDescriptions != other.changeDescriptions && (this.changeDescriptions == null || !this.changeDescriptions.equals(other.changeDescriptions))) return false;
        return true;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append("Repair: ").append("\n");
        for (ChangeDescription changeSet : changeDescriptions) {
            result.append("\t").append(changeSet).append("\n");
        }
        return result.toString();
    }
}
