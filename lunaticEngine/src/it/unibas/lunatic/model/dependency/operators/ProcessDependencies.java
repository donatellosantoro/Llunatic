package it.unibas.lunatic.model.dependency.operators;

import it.unibas.lunatic.LunaticConstants;
import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.exceptions.ParserException;
import it.unibas.lunatic.model.chase.commons.ChaseStats;
import it.unibas.lunatic.model.dependency.ComparisonAtom;
import it.unibas.lunatic.model.dependency.DED;
import it.unibas.lunatic.model.dependency.Dependency;
import it.unibas.lunatic.model.dependency.IFormula;
import it.unibas.lunatic.model.dependency.IFormulaAtom;
import it.unibas.lunatic.model.dependency.RelationalAtom;
import it.unibas.lunatic.parser.ParserOutput;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import speedy.model.database.IDatabase;
import speedy.model.database.TableAlias;

public class ProcessDependencies {

    private final FindVariableEquivalenceClasses equivalenceClassFinder = new FindVariableEquivalenceClasses();
    private final CheckRecursion recursionChecker = new CheckRecursion();
    private final AssignAliasesInFormulas aliasAssigner = new AssignAliasesInFormulas();
    private final CheckVariablesInExpressions checker = new CheckVariablesInExpressions();
    private final FindFormulaVariables variableFinder = new FindFormulaVariables();
    private final FindTargetGenerators generatorFinder = new FindTargetGenerators();
    private final NormalizeConclusionsInTGDs tgdConclusionNormalizer = new NormalizeConclusionsInTGDs();
    private final ReplaceConstantsWithVariables constantReplacer = new ReplaceConstantsWithVariables();
    private final NormalizeJoinsInEGDs egdJoinNormalizer = new NormalizeJoinsInEGDs();

    public void processDependencies(ParserOutput parserOutput, Scenario scenario) {
        long start = new Date().getTime();
        checkDCs(parserOutput, scenario);
        checkDEDExtEGDs(parserOutput, scenario);
        checkDEDExtTGDs(parserOutput, scenario);
        checkDEDSTTGDs(parserOutput, scenario);
        checkEGDs(parserOutput, scenario);
        checkExtEGDs(parserOutput, scenario);
        checkExtTGDs(parserOutput, scenario);
        checkSTTGDs(parserOutput, scenario);
        scenario.setSTTGDs(processDependencies(parserOutput.getStTGDs(), scenario));
        scenario.setExtTGDs(processExtTGDs(parserOutput.geteTGDs(), false, scenario));
        scenario.setDCs(processDependencies(parserOutput.getDcs(), scenario));
        scenario.setEGDs(processDependencies(parserOutput.getEgds(), scenario));
        scenario.setExtEGDs(processDependencies(parserOutput.geteEGDs(), scenario));
        scenario.setDEDstTGDs(processDEDs(parserOutput.getDedstTGDs(), scenario));
        scenario.setDEDextTGDs(processDEDExtTGDs(parserOutput.getDedeTGDs(), scenario));
        scenario.setDEDEGDs(processDEDs(parserOutput.getDedegds(), scenario));
        scenario.setQueries(processDependencies(parserOutput.getQueries(), scenario));
        long end = new Date().getTime();
        ChaseStats.getInstance().addStat(ChaseStats.PROCESS_DEPENDENCIES_TIME, end - start);
    }

    @SuppressWarnings("unchecked")
    private void checkSTTGDs(ParserOutput parserOutput, Scenario scenario) {
        for (Dependency d : parserOutput.getStTGDs()) {
            IDatabase source = scenario.getSource();
            List<String> sourceTables = source.getTableNames();
            checkAtoms(sourceTables, Collections.EMPTY_LIST, d.getPremise(), false);
            IDatabase target = scenario.getTarget();
            List<String> targetTables = target.getTableNames();
            checkAtoms(Collections.EMPTY_LIST, targetTables, d.getConclusion(), true);
        }
    }

    @SuppressWarnings("unchecked")
    private void checkExtTGDs(ParserOutput parserOutput, Scenario scenario) {
        for (Dependency d : parserOutput.geteTGDs()) {
            IDatabase source = scenario.getSource();
            List<String> sourceTables = source.getTableNames();
            IDatabase target = scenario.getTarget();
            List<String> targetTables = target.getTableNames();
            checkAtoms(sourceTables, targetTables, d.getPremise(), false);
            checkAtoms(Collections.EMPTY_LIST, targetTables, d.getConclusion(), true);
        }
    }

    public void checkDCs(ParserOutput parserOutput, Scenario scenario) {
        for (Dependency d : parserOutput.getDcs()) {
            IDatabase source = scenario.getSource();
            List<String> sourceTables = source.getTableNames();
            IDatabase target = scenario.getTarget();
            List<String> targetTables = target.getTableNames();
            checkAtoms(sourceTables, targetTables, d.getPremise(), false);
        }
    }

    @SuppressWarnings("unchecked")
    private void checkEGDs(ParserOutput parserOutput, Scenario scenario) {
        for (Dependency d : parserOutput.getEgds()) {
            IDatabase target = scenario.getTarget();
            List<String> targetTables = target.getTableNames();
            checkAtoms(Collections.EMPTY_LIST, targetTables, d.getPremise(), false);
            checkComparisons(d);
        }
    }

    private void checkExtEGDs(ParserOutput parserOutput, Scenario scenario) {
        for (Dependency d : parserOutput.geteEGDs()) {
            IDatabase source = scenario.getSource();
            List<String> sourceTables = source.getTableNames();
            IDatabase target = scenario.getTarget();
            List<String> targetTables = target.getTableNames();
            checkAtoms(sourceTables, targetTables, d.getPremise(), false);
            checkComparisons(d);
        }
    }

    @SuppressWarnings("unchecked")
    private void checkDEDSTTGDs(ParserOutput parserOutput, Scenario scenario) {
        for (DED ded : parserOutput.getDedstTGDs()) {
            for (Dependency dependency : ded.getAssociatedDependencies()) {
                IDatabase source = scenario.getSource();
                List<String> sourceTables = source.getTableNames();
                checkAtoms(sourceTables, Collections.EMPTY_LIST, dependency.getPremise(), false);
                IDatabase target = scenario.getTarget();
                List<String> targetTables = target.getTableNames();
                checkAtoms(Collections.EMPTY_LIST, targetTables, dependency.getConclusion(), true);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void checkDEDExtTGDs(ParserOutput parserOutput, Scenario scenario) {
        for (DED ded : parserOutput.getDedeTGDs()) {
            for (Dependency dependency : ded.getAssociatedDependencies()) {
                IDatabase source = scenario.getSource();
                List<String> sourceTables = source.getTableNames();
                IDatabase target = scenario.getTarget();
                List<String> targetTables = target.getTableNames();
                checkAtoms(sourceTables, targetTables, dependency.getPremise(), false);
                checkAtoms(Collections.EMPTY_LIST, targetTables, dependency.getConclusion(), true);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void checkDEDExtEGDs(ParserOutput parserOutput, Scenario scenario) {
        for (DED ded : parserOutput.getDedegds()) {
            for (Dependency dependency : ded.getAssociatedDependencies()) {
                IDatabase target = scenario.getTarget();
                List<String> targetTables = target.getTableNames();
                checkAtoms(Collections.EMPTY_LIST, targetTables, dependency.getPremise(), false);
                checkComparisons(dependency);
            }
        }
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
                throw new ParserException("EGDs must have comparisons in their conclusions: " + d.toLogicalString());
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

    private List<Dependency> processDependencies(List<Dependency> dependencies, Scenario scenario) {
        List<Dependency> result = new ArrayList<Dependency>();
        for (Dependency dependency : dependencies) {
            processInitialDependency(dependency, scenario);
            Dependency normalizedDependency = normalizeVariablesInDependency(dependency, scenario);
            if (normalizedDependency.getType().equals(LunaticConstants.STTGD)) {
                generatorFinder.findGenerators(normalizedDependency, scenario);
            }
            result.add(normalizedDependency);
        }
        return result;
    }

    private List<Dependency> processExtTGDs(List<Dependency> etgds, boolean fromDEDs, Scenario scenario) {
        List<Dependency> result = new ArrayList<Dependency>();
        for (Dependency etgd : etgds) {
            assert (etgd.getType().equals(LunaticConstants.ExtTGD)) : "Conclusion normalization is only needed for etgds";
            processInitialDependency(etgd, scenario);
            List<Dependency> tgdsWithNormalizedJoinsInConclusion = null;
            if (fromDEDs) {
                // DED tgds cannot be normalized at this time to avoid confusing the greedy scenario generator
                tgdsWithNormalizedJoinsInConclusion = Arrays.asList(new Dependency[]{etgd});
            } else {
                tgdsWithNormalizedJoinsInConclusion = tgdConclusionNormalizer.normalizeTGD(etgd);
            }
            for (Dependency normalizedTgd : tgdsWithNormalizedJoinsInConclusion) {
                Dependency newTgd = normalizeVariablesInDependency(normalizedTgd, scenario);
                generatorFinder.findGenerators(newTgd, scenario);
                result.add(newTgd);
            }
        }
        return result;
    }

    private List<DED> processDEDs(List<DED> deds, Scenario scenario) {
        for (DED ded : deds) {
            List<Dependency> processedDependencies = processDependencies(ded.getAssociatedDependencies(), scenario);
            ded.setAssociatedDependencies(processedDependencies);
        }
        return deds;
    }

    private List<DED> processDEDExtTGDs(List<DED> dedExtTGDs, Scenario scenario) {
        for (DED ded : dedExtTGDs) {
            List<Dependency> processedExtTGDs = processExtTGDs(ded.getAssociatedDependencies(), true, scenario);
            ded.setAssociatedDependencies(processedExtTGDs);
        }
        return dedExtTGDs;
    }

    /////////////////////   INITIAL PROCESSING    //////////////////////////////////
    private void processInitialDependency(Dependency dependency, Scenario scenario) {
        assignAuthoritativeSources(dependency, scenario);
        recursionChecker.checkRecursion(dependency);
        aliasAssigner.assignAliases(dependency);
        variableFinder.findVariables(dependency, scenario.getSource().getTableNames(), scenario.getAuthoritativeSources());
        checker.checkVariables(dependency);
        equivalenceClassFinder.findVariableEquivalenceClasses(dependency);
    }

    private void assignAuthoritativeSources(Dependency dependency, Scenario scenario) {
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
    private Dependency normalizeVariablesInDependency(Dependency dependency, Scenario scenario) {
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
