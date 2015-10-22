package it.unibas.lunatic.model.dependency;

public interface IFormulaValue extends Cloneable{
    
    public boolean isVariable();
    public boolean isNull();
    public IFormulaValue clone(); 
    public String toLongString();
}
