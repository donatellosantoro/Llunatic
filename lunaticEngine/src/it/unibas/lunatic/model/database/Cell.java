package it.unibas.lunatic.model.database;

import it.unibas.lunatic.LunaticConstants;
import java.io.Serializable;

public class Cell implements Serializable{

    private TupleOID tupleOid;
    private AttributeRef attributeRef;
    private IValue value;

    public Cell(TupleOID tupleOid, AttributeRef attributeRef, IValue value) {
        this.tupleOid = tupleOid;
        this.attributeRef = attributeRef;
        this.value = value;
    }

    public Cell(CellRef cellRef, IValue value) {
        this(cellRef.getTupleOID(), cellRef.getAttributeRef(), value);
    }

    public Cell(Cell originalCell, Tuple newTuple) {
        this.tupleOid = newTuple.getOid();
        this.attributeRef = originalCell.attributeRef;
        this.value = originalCell.value;
    }

    public boolean isOID() {
        return attributeRef.getName().equals(LunaticConstants.OID);
    }

    public AttributeRef getAttributeRef() {
        return attributeRef;
    }

    public void setAttributeRef(AttributeRef attributeRef) {
        this.attributeRef = attributeRef;
    }

    public String getAttribute() {
        return attributeRef.getName();
    }

    public IValue getValue() {
        return value;
    }

    public TupleOID getTupleOID() {
        return tupleOid;
    }

//    @Override
//    public int hashCode() {
//        int hash = 5;
//        hash = 71 * hash + (this.tupleOid != null ? this.tupleOid.hashCode() : 0);
//        hash = 71 * hash + (this.attributeRef != null ? this.attributeRef.hashCode() : 0);
//        hash = 71 * hash + (this.value != null ? this.value.hashCode() : 0);
//        return hash;
//    }
//
//    @Override
//    public boolean equals(Object obj) {
//        if (obj == null) return false;
//        if (getClass() != obj.getClass()) return false;
//        final Cell other = (Cell) obj;
//        if (this.tupleOid != other.tupleOid && (this.tupleOid == null || !this.tupleOid.equals(other.tupleOid))) return false;
//        if (this.attributeRef != other.attributeRef && (this.attributeRef == null || !this.attributeRef.equals(other.attributeRef))) return false;
//        if (this.value != other.value && (this.value == null || !this.value.equals(other.value))) return false;
//        return true;
//    }

    @Override
    public boolean equals(Object obj) {
        if ( obj == null ){
            return false;
        }
        return this.toString().equals(obj.toString());
    }

    @Override
    public int hashCode() {
        return this.toString().hashCode();
    }

    @Override
    public String toString() {
        return tupleOid + ":" + attributeRef + "-" + value;
    }

    public String toShortString() {
        return attributeRef.getName() + ":" + value;
    }

    public String toStringWithAlias() {
        return attributeRef + ":" + value;
    }

    public String toStringWithOIDAndAlias() {
        return tupleOid + ":" + attributeRef + ":" + value;
    }
}
