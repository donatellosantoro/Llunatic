package it.unibas.lunatic.model.chase.chasemc;

import it.unibas.lunatic.LunaticConstants;
import it.unibas.lunatic.PartialOrderConstants;
import speedy.model.database.AttributeRef;
import speedy.model.database.Cell;
import speedy.model.database.CellRef;
import speedy.model.database.IValue;
import speedy.model.database.TupleOID;

public class CellGroupCell extends Cell {

    private String type;
    private Boolean toSave;
    private IValue additionalValue;
    private IValue originalValue;
    private IValue lastSavedCellGroupId;

    public CellGroupCell(TupleOID tupleOid, AttributeRef attributeRef, IValue value, IValue originalValue, String type, Boolean toSave) {
        super(tupleOid, attributeRef, value);
        if (type.equals(LunaticConstants.TYPE_JUSTIFICATION) && value.getType().equals(PartialOrderConstants.LLUN)) {
            throw new IllegalArgumentException();
        }
        this.originalValue = originalValue;
        this.type = type;
        this.toSave = toSave;
    }

    public CellGroupCell(CellRef cellRef, IValue value, IValue originalValue, String type, Boolean toSave) {
        super(cellRef, value);
        if (type.equals(LunaticConstants.TYPE_JUSTIFICATION) && value.getType().equals(PartialOrderConstants.LLUN)) {
            throw new IllegalArgumentException();
        }
        this.originalValue = originalValue;
        this.type = type;
        this.toSave = toSave;
    }

    public CellGroupCell(Cell cell, IValue originalValue, String type, Boolean toSave) {
        super(new CellRef(cell), cell.getValue());
        this.originalValue = originalValue;
        this.type = type;
        this.toSave = toSave;
    }

    public void setValue(IValue value) {
        this.value = value;
    }

    public IValue getOriginalValue() {
        return originalValue;
    }

    public void setOriginalValue(IValue originalValue) {
        this.originalValue = originalValue;
    }

    public String getType() {
        return type;
    }

    public Boolean isToSave() {
        return toSave;
    }

    public void setToSave(Boolean toSave) {
        this.toSave = toSave;
    }

    public IValue getAdditionalValue() {
        return additionalValue;
    }

    public void setAdditionalValue(IValue additionalValue) {
        this.additionalValue = additionalValue;
    }

    public IValue getLastSavedCellGroupId() {
        return lastSavedCellGroupId;
    }

    public void setLastSavedCellGroupId(IValue lastSavedCellGroupId) {
        this.lastSavedCellGroupId = lastSavedCellGroupId;
    }

    @Override
    public String toString() {
        return super.toString() + " (" + originalValue + ")";
    }

    public String toLongString() {
        return this.toString() + type + " - toSave:" + toSave + " - last saved CGid=" + (lastSavedCellGroupId == null ? "[]" : lastSavedCellGroupId)
                + (additionalValue == null ? "" : " additionalValue: " + additionalValue);
    }

}
