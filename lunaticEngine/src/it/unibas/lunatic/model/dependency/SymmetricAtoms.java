package it.unibas.lunatic.model.dependency;

import java.util.HashSet;
import java.util.Set;
import speedy.model.database.TableAlias;

public class SymmetricAtoms {

    private Set<TableAlias> symmetricTableAliases = new HashSet<TableAlias>();
    private SelfJoin symmetricSelfJoin;

    public SymmetricAtoms() {
    }

    public SymmetricAtoms(Set<TableAlias> symmetricTableAliases, SelfJoin selfJoin) {
        this.symmetricTableAliases = symmetricTableAliases;
        this.symmetricSelfJoin = selfJoin;
    }

    public Set<TableAlias> getSymmetricAliases() {
        return symmetricTableAliases;
    }

    public SelfJoin getSymmetricSelfJoin() {
        return symmetricSelfJoin;
    }

    public boolean isEmpty() {
        return this.symmetricTableAliases.isEmpty();
    }

    public int getSize() {
        return this.symmetricTableAliases.size();
    }

    @Override
    public String toString() {
        return "SymmetricAtoms{" + "symmetric atoms=" + symmetricTableAliases + ", selfJoin=" + symmetricSelfJoin + '}';
    }
}
