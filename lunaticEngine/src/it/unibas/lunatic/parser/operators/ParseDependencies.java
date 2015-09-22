package it.unibas.lunatic.parser.operators;

import it.unibas.lunatic.LunaticConstants;
import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.exceptions.ParserException;
import it.unibas.lunatic.model.dependency.*;
import it.unibas.lunatic.model.dependency.operators.AssignAliasesInFormulas;
import it.unibas.lunatic.model.dependency.operators.CheckRecursion;
import it.unibas.lunatic.model.dependency.operators.CheckVariablesInExpressions;
import it.unibas.lunatic.model.dependency.operators.FindFormulaVariables;
import it.unibas.lunatic.model.dependency.operators.FindTargetGenerators;
import it.unibas.lunatic.model.dependency.operators.FindVariableEquivalenceClasses;
import it.unibas.lunatic.model.dependency.operators.NormalizeConclusionsInTGDs;
import it.unibas.lunatic.model.dependency.operators.NormalizeJoinsInEGDs;
import it.unibas.lunatic.model.dependency.operators.ReplaceConstantsWithVariables;
import it.unibas.lunatic.parser.output.DependenciesLexer;
import it.unibas.lunatic.parser.output.DependenciesParser;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import speedy.model.database.IDatabase;
import speedy.model.database.TableAlias;

@SuppressWarnings("unchecked")
public class ParseDependencies {

    public final static String NULL = "#NULL#";
    private static Logger logger = LoggerFactory.getLogger(ParseDependencies.class);

    private FindVariableEquivalenceClasses equivalenceClassFinder = new FindVariableEquivalenceClasses();
    private CheckRecursion recursionChecker = new CheckRecursion();
    private AssignAliasesInFormulas aliasAssigner = new AssignAliasesInFormulas();
    private CheckVariablesInExpressions checker = new CheckVariablesInExpressions();
    private FindFormulaVariables variableFinder = new FindFormulaVariables();
    private FindTargetGenerators generatorFinder = new FindTargetGenerators();
    private NormalizeConclusionsInTGDs tgdConclusionNormalizer = new NormalizeConclusionsInTGDs();
    private ReplaceConstantsWithVariables constantReplacer = new ReplaceConstantsWithVariables();
    private NormalizeJoinsInEGDs egdJoinNormalizer = new NormalizeJoinsInEGDs();

    private List<Dependency> stTGDs = new ArrayList<Dependency>();
    private List<Dependency> eTGDs = new ArrayList<Dependency>();
    private List<Dependency> dcs = new ArrayList<Dependency>();
    private List<Dependency> egds = new ArrayList<Dependency>();
    private List<Dependency> eEGDs = new ArrayList<Dependency>();
    private List<DED> dedstTGDs = new ArrayList<DED>();
    private List<DED> dedeTGDs = new ArrayList<DED>();
    private List<DED> dedegds = new ArrayList<DED>();
    private Scenario scenario;

    public void generateDependencies(String text, Scenario scenario) throws Exception {
        try {
            this.scenario = scenario;
            DependenciesLexer lex = new DependenciesLexer(new ANTLRStringStream(text));
            CommonTokenStream tokens = new CommonTokenStream(lex);
            DependenciesParser g = new DependenciesParser(tokens);
            try {
                g.setGenerator(this);
                g.prog();
            } catch (RecognitionException ex) {
                logger.error("Unable to load mapping task: " + ex.getMessage());
                throw new ParserException(ex);
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getLocalizedMessage());
            throw new ParserException(e);
        }
    }

    public void addSTTGD(Dependency d) {
        IDatabase source = scenario.getSource();
        List<String> sourceTables = source.getTableNames();
        checkAtoms(sourceTables, Collections.EMPTY_LIST, d.getPremise(), false);
        IDatabase target = scenario.getTarget();
        List<String> targetTables = target.getTableNames();
        checkAtoms(Collections.EMPTY_LIST, targetTables, d.getConclusion(), true);
        this.stTGDs.add(d);
    }

    public void addExtTGD(Dependency d) {
        IDatabase source = scenario.getSource();
        List<String> sourceTables = source.getTableNames();
        IDatabase target = scenario.getTarget();
        List<String> targetTables = target.getTableNames();
        checkAtoms(sourceTables, targetTables, d.getPremise(), false);
        checkAtoms(Collections.EMPTY_LIST, targetTables, d.getConclusion(), true);
        this.eTGDs.add(d);
    }

    public void addDC(Dependency d) {
        if (!(d.getConclusion() instanceof NullFormula)) {
            throw new ParserException("DC must have no conclusion");
        }
        IDatabase source = scenario.getSource();
        List<String> sourceTables = source.getTableNames();
        IDatabase target = scenario.getTarget();
        List<String> targetTables = target.getTableNames();
        checkAtoms(sourceTables, targetTables, d.getPremise(), false);
        this.dcs.add(d);
    }

    public void addEGD(Dependency d) {
        IDatabase target = scenario.getTarget();
        List<String> targetTables = target.getTableNames();
        checkAtoms(Collections.EMPTY_LIST, targetTables, d.getPremise(), false);
        checkComparisons(d);
        this.egds.add(d);
    }

    public void addExtEGD(Dependency d) {
        IDatabase source = scenario.getSource();
        List<String> sourceTables = source.getTableNames();
        IDatabase target = scenario.getTarget();
        List<String> targetTables = target.getTableNames();
        checkAtoms(sourceTables, targetTables, d.getPremise(), false);
        checkComparisons(d);
        this.eEGDs.add(d);
    }

    public void addDEDSTTGD(DED ded) {
        for (Dependency dependency : ded.getAssociatedDependencies()) {
            IDatabase source = scenario.getSource();
            List<String> sourceTables = source.getTableNames();
            checkAtoms(sourceTables, Collections.EMPTY_LIST, dependency.getPremise(), false);
            IDatabase target = scenario.getTarget();
            List<String> targetTables = target.getTableNames();
            checkAtoms(Collections.EMPTY_LIST, targetTables, dependency.getConclusion(), true);
        }
        this.dedstTGDs.add(ded);
    }

    public void addDEDExtTGD(DED ded) {
        for (Dependency dependency : ded.getAssociatedDependencies()) {
            IDatabase source = scenario.getSource();
            List<String> sourceTables = source.getTableNames();
            IDatabase target = scenario.getTarget();
            List<String> targetTables = target.getTableNames();
            checkAtoms(sourceTables, targetTables, dependency.getPremise(), false);
            checkAtoms(Collections.EMPTY_LIST, targetTables, dependency.getConclusion(), true);
        }
        this.dedeTGDs.add(ded);
    }

    public void addDEDExtEGD(DED ded) {
        for (Dependency dependency : ded.getAssociatedDependencies()) {
            IDatabase target = scenario.getTarget();
            List<String> targetTables = target.getTableNames();
            checkAtoms(Collections.EMPTY_LIST, targetTables, dependency.getPremise(), false);
            checkComparisons(dependency);
        }
        this.dedegds.add(ded);
    }

    private void checkAtoms(List<String> sourceTables, List<String> targetTables, IFormula formula, boolean onlyRelational) {
        StringBuilder errors = new StringBuilder();
        for (IFormulaAtom atom : formula.getAtoms()) {
            if (!(atom instanceof RelationalAtom)) {
                if (onlyRelational) {
                    errors.append("Only relational atoms allowed in formula: ").append(atom).append("\n");
                }
            } else {
                RelationalAtom relationalAtom = (RelationalAtom) atom;
                if (sourceTables.contains(relationalAtom.getTableName())) {
                    relationalAtom.setSource(true);
                } else if (targetTables.contains(relationalAtom.getTableName())) {
                    relationalAtom.setSource(false);
                } else {
                    errors.append("Table not allowed in formula: ").append(relationalAtom.getTableName()).append(" - ").append(sourceTables).append(" - ").append(targetTables).append("\n");
                }
            }
        }
        if (!errors.toString().isEmpty()) {
            throw new ParserException(errors.toString());
        }
    }

    private void checkComparisons(Dependency d) {
        for (IFormulaAtom atom : d.getConclusion().getAtoms()) {
            if (!(atom instanceof ComparisonAtom)) {
                throw new ParserException("EGDs must have comparisons in their conclusions: " + d);
            }
            if (!atom.toString().contains("==")) {
                throw new ParserException("EGDs must have equalies in their conclusions: " + d);
            }
            ComparisonAtom comparison = (ComparisonAtom) atom;
            if (comparison.getVariables().size() < 1 && comparison.getVariables().size() > 2) {
                throw new ParserException("Illegal comparison " + comparison + " in dependency " + d);
            }
        }
    }

// final callback method for processing tgds
    public void processDependencies() {
        scenario.setSTTGDs(processDependencies(stTGDs));
        scenario.setExtTGDs(processExtTGDs(eTGDs, false));
        scenario.setDCs(processDependencies(dcs));
        scenario.setEGDs(processDependencies(egds));
        scenario.setExtEGDs(processDependencies(eEGDs));
        scenario.setDEDstTGDs(processDEDs(dedstTGDs));
        scenario.setDEDextTGDs(processDEDExtTGDs(dedeTGDs));
        scenario.setDEDEGDs(processDEDs(dedegds));
    }

    public String clean(String expressionString) {
        String result = expressionString.trim();
        result = result.replaceAll("\\$", "");
        return result.substring(1, result.length() - 1);
    }

    private List<Dependency> processDependencies(List<Dependency> dependencies) {
        List<Dependency> result = new ArrayList<Dependency>();
        for (Dependency dependency : dependencies) {
            processInitialDependency(dependency);
            Dependency normalizedDependency = normalizeVariablesInDependency(dependency);
            if (normalizedDependency.getType().equals(LunaticConstants.STTGD)) {
                generatorFinder.findGenerators(normalizedDependency);
            }
            result.add(normalizedDependency);
        }
        return result;
    }

    private List<Dependency> processExtTGDs(List<Dependency> etgds, boolean fromDEDs) {
        List<Dependency> result = new ArrayList<Dependency>();
        for (Dependency etgd : etgds) {
            assert (etgd.getType().equals(LunaticConstants.ExtTGD)) : "Conclusion normalization is only needed for etgds";
            processInitialDependency(etgd);
            List<Dependency> tgdsWithNormalizedJoinsInConclusion = null;
            if (fromDEDs) {
                // DED tgds cannot be normalized at this time to avoid confusing the greedy scenario generator
                tgdsWithNormalizedJoinsInConclusion = Arrays.asList(new Dependency[]{etgd});
            } else {
                tgdsWithNormalizedJoinsInConclusion = tgdConclusionNormalizer.normalizeTGD(etgd);
            }
            for (Dependency normalizedTgd : tgdsWithNormalizedJoinsInConclusion) {
                Dependency newTgd = normalizeVariablesInDependency(normalizedTgd);
                generatorFinder.findGenerators(newTgd);
                result.add(newTgd);
            }
        }
        return result;
    }

    private List<DED> processDEDs(List<DED> deds) {
        for (DED ded : deds) {
            List<Dependency> processedDependencies = processDependencies(ded.getAssociatedDependencies());
            ded.setAssociatedDependencies(processedDependencies);
        }
        return deds;
    }

    private List<DED> processDEDExtTGDs(List<DED> dedExtTGDs) {
        for (DED ded : dedExtTGDs) {
            List<Dependency> processedExtTGDs = processExtTGDs(ded.getAssociatedDependencies(), true);
            ded.setAssociatedDependencies(processedExtTGDs);
        }
        return dedExtTGDs;
    }

    /////////////////////   INITIAL PROCESSING    //////////////////////////////////
    private void processInitialDependency(Dependency dependency) {
        assignAuthoritativeSources(dependency);
        recursionChecker.checkRecursion(dependency);
        aliasAssigner.assignAliases(dependency);
        variableFinder.findVariables(dependency, scenario.getSource().getTableNames(), scenario.getAuthoritativeSources());
        checker.checkVariables(dependency);
        equivalenceClassFinder.findVariableEquivalenceClasses(dependency);
    }

    private void assignAuthoritativeSources(Dependency dependency) {
        for (IFormulaAtom formulaAtom : dependency.getPremise().getAtoms()) {
            if (!(formulaAtom instanceof RelationalAtom)) {
                continue;
            }
            TableAlias tableAlias = ((RelationalAtom) formulaAtom).getTableAlias();
            if (scenario.getAuthoritativeSources().contains(tableAlias.getTableName())) {
                tableAlias.setAuthoritative(true);
            }
        }
    }

    /////////////////////   NORMALIZATION    //////////////////////////////////
    private Dependency normalizeVariablesInDependency(Dependency dependency) {
        if (dependency.getType().equals(LunaticConstants.ExtEGD) || dependency.getType().equals(LunaticConstants.ExtTGD)) {
            constantReplacer.replaceConstants(dependency, scenario);
            equivalenceClassFinder.findVariableEquivalenceClasses(dependency); // needed after each variable change
//            assignAuthoritativeSources(dependency); // needed twice
        }
        if (dependency.getType().equals(LunaticConstants.ExtTGD) || dependency.getType().equals(LunaticConstants.EGD) 
                || dependency.getType().equals(LunaticConstants.ExtEGD)) {
            dependency = egdJoinNormalizer.normalizeJoinsInEgd(dependency);
        }
        equivalenceClassFinder.findVariableEquivalenceClasses(dependency); // needed after each variable change
        return dependency;
    }

}
