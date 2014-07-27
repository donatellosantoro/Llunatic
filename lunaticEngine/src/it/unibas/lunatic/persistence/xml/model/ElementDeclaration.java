package it.unibas.lunatic.persistence.xml.model;

import it.unibas.lunatic.persistence.xml.operators.IXSDNodeVisitor;

public class ElementDeclaration extends Particle {
    
    public ElementDeclaration(String label) {
        super(label);
    }

    public void accept(IXSDNodeVisitor visitor) {
        visitor.visitElementDeclaration(this);
    }

}
