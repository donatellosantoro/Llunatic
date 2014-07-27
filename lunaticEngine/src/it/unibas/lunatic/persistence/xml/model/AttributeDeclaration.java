package it.unibas.lunatic.persistence.xml.model;

import it.unibas.lunatic.persistence.xml.operators.IXSDNodeVisitor;

public class AttributeDeclaration extends Particle {
    
    public AttributeDeclaration(String label) {
        super(label);
    }

    public void accept(IXSDNodeVisitor visitor) {
        visitor.visitAttributeDeclaration(this);
    }

}
