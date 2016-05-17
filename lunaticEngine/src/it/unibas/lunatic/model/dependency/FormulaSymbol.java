package it.unibas.lunatic.model.dependency;

public class FormulaSymbol implements IFormulaValue {

    private Object value;

    public FormulaSymbol(Object value) {
        this.value = value;
    }

    public Object getValue() {
        if (true) throw new UnsupportedOperationException();
        return value;
    }

    public boolean isNull() {
        return false;
    }

    public boolean isVariable() {
        return false;
    }

    public IFormulaValue clone() {
        try {
            return (IFormulaValue) super.clone();
        } catch (CloneNotSupportedException ex) {
            throw new IllegalArgumentException("Unable to clone FormulaValue " + ex.getLocalizedMessage());
        }
    }

    @Override
    public String toString() {
        return value.toString();
//        return "\"" + value.toString() + "\"";
    }

    public String toLongString() {
        if (true) throw new UnsupportedOperationException();
        return "Constant : " + value.toString();
    }

}
