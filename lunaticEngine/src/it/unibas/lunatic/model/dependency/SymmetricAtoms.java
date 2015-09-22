package it.unibas.lunatic.model.dependency;

import java.util.HashSet;
import java.util.Set;
import speedy.model.database.TableAlias;

public class SymmetricAtoms {

    private Set<TableAlias> symmetricTableAliases = new HashSet<TableAlias>();
    private SelfJoin selfJoin;

    public SymmetricAtoms() {
    }

    public SymmetricAtoms(Set<TableAlias> symmetricTableAliases, SelfJoin selfJoin) {
        this.symmetricTableAliases = symmetricTableAliases;
        this.selfJoin = selfJoin;
    }

    public Set<TableAlias> getSymmetricAliases() {
        return symmetricTableAliases;
    }

    public void setSymmetricAliases(Set<TableAlias> symmetricAtoms) {
        this.symmetricTableAliases = symmetricAtoms;
    }

    public SelfJoin getSelfJoin() {
        return selfJoin;
    }

    public void setSelfJoin(SelfJoin selfJoin) {
        this.selfJoin = selfJoin;
    }
    
    public boolean isEmpty(){
        return this.symmetricTableAliases.isEmpty();
    }

    @Override
    public String toString() {
        return "SymmetricAtoms{" + "symmetric atoms=" + symmetricTableAliases + ", selfJoin=" + selfJoin + '}';
    }
}
