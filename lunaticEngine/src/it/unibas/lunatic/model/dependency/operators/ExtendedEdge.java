package it.unibas.lunatic.model.dependency.operators;

import org.jgrapht.graph.DefaultEdge;

public class ExtendedEdge extends DefaultEdge {

    private boolean normal = false;
    private boolean special = false;

    public boolean isSpecial() {
        return special;
    }

    public void setSpecial(boolean special) {
        this.special = special;
    }

    public boolean isNormal() {
        return normal;
    }

    public void setNormal(boolean normal) {
        this.normal = normal;
    }

    @Override
    public String toString() {
        return super.toString() + (special ? "*" : "");
    }

}
