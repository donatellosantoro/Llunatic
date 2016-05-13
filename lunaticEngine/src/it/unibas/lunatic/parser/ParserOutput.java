package it.unibas.lunatic.parser;

import it.unibas.lunatic.model.dependency.DED;
import it.unibas.lunatic.model.dependency.Dependency;
import java.util.ArrayList;
import java.util.List;

public class ParserOutput {

    private final List<Dependency> stTGDs = new ArrayList<Dependency>();
    private final List<Dependency> eTGDs = new ArrayList<Dependency>();
    private final List<Dependency> dcs = new ArrayList<Dependency>();
    private final List<Dependency> egds = new ArrayList<Dependency>();
    private final List<Dependency> eEGDs = new ArrayList<Dependency>();
    private final List<DED> dedstTGDs = new ArrayList<DED>();
    private final List<DED> dedeTGDs = new ArrayList<DED>();
    private final List<DED> dedegds = new ArrayList<DED>();
    private final List<Dependency> queries = new ArrayList<Dependency>();

    public List<Dependency> getStTGDs() {
        return stTGDs;
    }

    public List<Dependency> geteTGDs() {
        return eTGDs;
    }

    public List<Dependency> getDcs() {
        return dcs;
    }

    public List<Dependency> getEgds() {
        return egds;
    }

    public List<Dependency> geteEGDs() {
        return eEGDs;
    }

    public List<DED> getDedstTGDs() {
        return dedstTGDs;
    }

    public List<DED> getDedeTGDs() {
        return dedeTGDs;
    }

    public List<DED> getDedegds() {
        return dedegds;
    }

    public List<Dependency> getQueries() {
        return queries;
    }

}
