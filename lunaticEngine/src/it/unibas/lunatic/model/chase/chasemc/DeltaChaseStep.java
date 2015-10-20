package it.unibas.lunatic.model.chase.chasemc;

import it.unibas.lunatic.OperatorFactory;
import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.model.chase.commons.ChaseUtility;
import it.unibas.lunatic.model.chase.chasemc.operators.ChaseTreeSize;
import it.unibas.lunatic.model.dependency.Dependency;
import java.util.ArrayList;
import java.util.List;
import speedy.model.database.AttributeRef;
import speedy.model.database.IDatabase;

public class DeltaChaseStep {

    private Scenario scenario;
    private ChaseTree chaseTree;
    private IDatabase originalDB;
    private IDatabase deltaDB;
    private Repair repair;
    private List<Dependency> satisfiedEGDs = new ArrayList<Dependency>();
    private String chaseMode;
    private DeltaChaseStep father;
    private String localId;
    private List<DeltaChaseStep> children = new ArrayList<DeltaChaseStep>();
    private List<AttributeRef> affectedAttributes = new ArrayList<AttributeRef>();
    private boolean invalid = false;
    private boolean solution = false;
    private boolean ground = false;
    private boolean editedByUser = false;
    private CellGroupStats cellGroupStats;
    private boolean duplicate;
    private List<DeltaChaseStep> duplicateNodes;

    //Root node
    public DeltaChaseStep(Scenario scenario, ChaseTree chaseTree, String localId, IDatabase originalDB, IDatabase deltaDB) {
        this.scenario = scenario;
        this.localId = localId;
        this.originalDB = originalDB;
        this.deltaDB = deltaDB;
        this.chaseTree = chaseTree;
        chaseTree.setRoot(this);
    }

    //EGDs
    public DeltaChaseStep(Scenario scenario, DeltaChaseStep father, String localId, Dependency dependency, Repair repair, String chaseMode) {
        this.scenario = scenario;
        this.father = father;
        this.localId = localId;
        this.chaseMode = chaseMode;
        this.originalDB = ((DeltaChaseStep) father).getOriginalDB();
        this.deltaDB = ((DeltaChaseStep) father).getDeltaDB();
        if (scenario.getConfiguration().isDebugMode()) {
            this.repair = repair;
        }
    }

    //TGDs and User nodes
    public DeltaChaseStep(Scenario scenario, DeltaChaseStep father, String localId, String chaseMode) {
        this.scenario = scenario;
        this.father = father;
        this.localId = localId;
        this.chaseMode = chaseMode;
        this.originalDB = ((DeltaChaseStep) father).getOriginalDB();
        this.deltaDB = ((DeltaChaseStep) father).getDeltaDB();
    }

    public ChaseTree getChaseTree() {
        if (this.chaseTree != null) {
            return chaseTree;
        }
        return this.father.getChaseTree();
    }

    public IDatabase getOriginalDB() {
        return originalDB;
    }

    public IDatabase getDeltaDB() {
        return deltaDB;
    }

    public Repair getRepair() {
        return repair;
    }

    public void setRepair(Repair repair) {
        this.repair = repair;
    }

    public boolean isRoot() {
        return this.father == null;
    }

    public boolean isLeaf() {
        return this.children.isEmpty();
    }

    public String getLocalId() {
        return localId;
    }

    public DeltaChaseStep getFather() {
        return father;
    }

    public String getChaseMode() {
        return chaseMode;
    }

    public Dependency getFirstSatisfiedEGD() {
        if (satisfiedEGDs.isEmpty()) {
            return null;
        }
        return satisfiedEGDs.get(0);
    }

    public List<Dependency> getSatisfiedEGDs() {
        return satisfiedEGDs;
    }

    public void addSatisfiedEGD(Dependency dependency) {
        if (!this.satisfiedEGDs.contains(dependency)) {
            this.satisfiedEGDs.add(dependency);
        }
    }

    public List<DeltaChaseStep> getChildren() {
        return this.children;
    }

    public void setChildren(List<DeltaChaseStep> children) {
        this.children = children;
    }

    public void addChild(DeltaChaseStep child) {
        this.children.add(child);
    }

    public String getId() {
        return ChaseUtility.getChaseNodeId(father, localId);
    }

    public List<AttributeRef> getAffectedAttributes() {
        return affectedAttributes;
    }

    public void setAffectedAttributes(List<AttributeRef> affectedAttributes) {
        this.affectedAttributes = affectedAttributes;
    }

    public DeltaChaseStep getRoot() {
        if (this.isRoot()) {
            return this;
        }
        return this.getFather().getRoot();
    }

    public int getNumberOfNodes() {
//        return new ChaseTreeSize().getPotentialSolutions(this.getRoot());
        return new ChaseTreeSize().getAllNodes(this.getRoot());
    }

    public int getNumberOfLeaves() {
        return new ChaseTreeSize().getAllLeaves(this.getRoot());
    }

    public int getPotentialSolutions() {
        return new ChaseTreeSize().getPotentialSolutions(this.getRoot());
    }

    public boolean isInvalid() {
        return invalid;
    }

    public void setInvalid(boolean invalid) {
        this.invalid = invalid;
    }

    public boolean isDuplicate() {
        return duplicate;
    }

    public void setDuplicate(boolean duplicate) {
        this.duplicate = duplicate;
    }

    public void setSolution(boolean solution) {
        this.solution = solution;
    }

    public boolean isSolution() {
        return solution;
    }

    public boolean isGround() {
        if (cellGroupStats != null && cellGroupStats.llunCellGroups == 0) {
            return true;
        }
        return ground;
    }

    public void setGround(boolean ground) {
        this.ground = ground;
    }

    public boolean isEditedByUser() {
        return editedByUser;
    }

    public void setEditedByUser(boolean editedByUser) {
        this.editedByUser = editedByUser;
    }

    public Scenario getScenario() {
        return scenario;
    }

    public CellGroupStats getCellGroupStats() {
        return cellGroupStats;
    }

    public void setCellGroupStats(CellGroupStats cellGroupStats) {
        this.cellGroupStats = cellGroupStats;
    }

    public List<DeltaChaseStep> getDuplicateNodes() {
        return duplicateNodes;
    }

    public void setDuplicateNodes(List<DeltaChaseStep> duplicateNodes) {
        this.duplicateNodes = duplicateNodes;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 53 * hash + (this.getId() != null ? this.getId().hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        final DeltaChaseStep other = (DeltaChaseStep) obj;
        if ((this.getId() == null) ? (other.getId() != null) : !this.getId().equals(other.getId())) return false;
        return true;
    }

    @Override
    public String toString() {
        return OperatorFactory.getInstance().getChaseTreeToString(scenario).toString(this);
    }

    public String toStringLeavesOnly() {
        return OperatorFactory.getInstance().getChaseTreeToString(scenario).toStringLeavesOnly(this);
    }

    public String toStringLeavesOnlyWithSort() {
        return OperatorFactory.getInstance().getChaseTreeToString(scenario).toStringLeavesOnlyWithSort(this);
    }

    public String toLongStringLeavesOnlyWithSort() {
        return OperatorFactory.getInstance().getChaseTreeToString(scenario).toLongStringLeavesOnlyWithSort(this);
    }

    public String toLongString() {
        return OperatorFactory.getInstance().getChaseTreeToString(scenario).toLongString(this);
    }

    public String toShortString() {
        return OperatorFactory.getInstance().getChaseTreeToString(scenario).toShortString(this);
    }

    public String toLongStringWithSort() {
        return OperatorFactory.getInstance().getChaseTreeToString(scenario).toLongStringWithSort(this);
    }

    public String toStringWithSort() {
        return OperatorFactory.getInstance().getChaseTreeToString(scenario).toStringWithSort(this);
    }

    public String toShortStringWithSort() {
        return OperatorFactory.getInstance().getChaseTreeToString(scenario).toShortStringWithSort(this);
    }

    public String toShortStringWithSortWithoutDuplicates() {
        return OperatorFactory.getInstance().getChaseTreeToString(scenario).toShortStringWithSortWithoutDuplicates(this);
    }

    public String toStats() {
        return OperatorFactory.getInstance().getChaseTreeToString(scenario).toStatString(this);
    }
}
