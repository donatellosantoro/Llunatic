package it.unibas.lunatic.model.dependency;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import speedy.model.database.AttributeRef;
import speedy.utility.SpeedyUtility;

public class DependencyStratification {

    //EGDs
    private List<EGDStratum> egdStrata = new ArrayList<EGDStratum>();
    private Map<AttributeRef, List<Dependency>> attributeEGDMap = new HashMap<AttributeRef, List<Dependency>>();
    //TGDs
    private List<TGDStratum> tgdStrata = new ArrayList<TGDStratum>();
    private Map<Dependency, Set<Dependency>> affectedTGDsMap;
    private DirectedGraph<TGDStratum, DefaultEdge> strataGraph;

    public List<EGDStratum> getEGDStrata() {
        return egdStrata;
    }

    public void addEGDStratum(EGDStratum stratum) {
        this.egdStrata.add(stratum);
    }

    public List<Dependency> getEGDDependenciesForAttribute(AttributeRef attribute) {
        return attributeEGDMap.get(attribute);
    }

    public void addEGDDependencyForAttribute(AttributeRef attribute, Dependency value) {
        List<Dependency> dependenciesForAttribute = this.attributeEGDMap.get(attribute);
        if (dependenciesForAttribute == null) {
            dependenciesForAttribute = new ArrayList<Dependency>();
            this.attributeEGDMap.put(attribute, dependenciesForAttribute);
        }
        dependenciesForAttribute.add(value);
    }

    public Map<Dependency, Set<Dependency>> getAffectedTGDsMap() {
        return affectedTGDsMap;
    }

    public void setAffectedTGDsMap(Map<Dependency, Set<Dependency>> affectedTGDsMap) {
        this.affectedTGDsMap = affectedTGDsMap;
    }

    public List<TGDStratum> getTGDStrata() {
        return tgdStrata;
    }

    public void addTGDStratum(TGDStratum stratum) {
        this.tgdStrata.add(stratum);
    }

    public DirectedGraph<TGDStratum, DefaultEdge> getStrataGraph() {
        return strataGraph;
    }

    public void setStrataGraph(DirectedGraph<TGDStratum, DefaultEdge> strataGraph) {
        this.strataGraph = strataGraph;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        if (egdStrata.size() == 1) {
            result.append(egdStrata.get(0).toString());
        } else {
            result.append("EGD Stratification (").append(egdStrata.size()).append(") {\n");
            for (EGDStratum stratum : egdStrata) {
                result.append(stratum).append("\n");
            }
            result.append("}\n");
        }
        if (tgdStrata.size() == 1) {
            result.append(tgdStrata.get(0).toString());
        } else {
            result.append("TGD Stratification (").append(tgdStrata.size()).append(") {\n");
            for (TGDStratum stratum : tgdStrata) {
                result.append(stratum.toLongString()).append("\n");
            }
            result.append("}\n");
        }
        result.append("Affected TGDs Map: \n").append(SpeedyUtility.printMap(affectedTGDsMap));
        return result.toString();
    }
}
