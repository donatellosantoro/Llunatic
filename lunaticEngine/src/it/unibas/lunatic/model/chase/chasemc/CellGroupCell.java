package it.unibas.lunatic.model.chase.chasemc;

import it.unibas.lunatic.LunaticConstants;
import it.unibas.lunatic.PartialOrderConstants;
import it.unibas.lunatic.model.database.AttributeRef;
import it.unibas.lunatic.model.database.Cell;
import it.unibas.lunatic.model.database.CellRef;
import it.unibas.lunatic.model.database.IValue;
import it.unibas.lunatic.model.database.TupleOID;

public class CellGroupCell extends Cell {

    private IValue originalValue;
    private String type;
    private Boolean toSave;
    private IValue additionalValue;
    private IValue originalCellGroupId;

    public CellGroupCell(TupleOID tupleOid, AttributeRef attributeRef, IValue value, IValue originalValue, String type, Boolean toSave) {
        super(tupleOid, attributeRef, value);
        if(type.equals(LunaticConstants.TYPE_JUSTIFICATION) && value.getType().equals(PartialOrderConstants.LLUN)){
            throw new IllegalArgumentException();
        }
        this.originalValue = originalValue;
        this.type = type;
        this.toSave = toSave;
    }

    public CellGroupCell(CellRef cellRef, IValue value, IValue originalValue, String type, Boolean toSave) {
        super(cellRef, value);
        if(type.equals(LunaticConstants.TYPE_JUSTIFICATION) && value.getType().equals(PartialOrderConstants.LLUN)){
            throw new IllegalArgumentException();
        }
        this.originalValue = originalValue;
        this.type = type;
        this.toSave = toSave;
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

    public IValue getOriginalCellGroupId() {
        return originalCellGroupId;
    }

    public void setOriginalCellGroupId(IValue originalCellGroupId) {
        this.originalCellGroupId = originalCellGroupId;
    }

    @Override
    public String toString() {
        return super.toString() + " (" + originalValue + ")";
    }

    public String toLongString() {
        return this.toString() + type + " toSave:" + toSave + (originalCellGroupId == null ? "" : " OriginalCellGroupId: " + originalCellGroupId)
                + (additionalValue == null ? "" : " MaxAdditionalValue: " + additionalValue);
    }

}
