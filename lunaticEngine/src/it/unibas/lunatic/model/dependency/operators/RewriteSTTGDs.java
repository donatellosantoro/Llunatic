package it.unibas.lunatic.model.dependency.operators;

import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.model.chase.commons.ChaseStats;
import it.unibas.lunatic.model.chase.commons.operators.ChaseUtility;
import it.unibas.lunatic.model.dependency.Dependency;
import it.unibas.lunatic.persistence.DAOMCScenarioStandard;
import it.unibas.spicy.model.mapping.rewriting.operators.RewriteAndExportSTTgds;
import it.unibas.spicy.persistence.DAOException;
import it.unibas.lunatic.persistence.DAOConfiguration;
import it.unibas.spicy.model.mapping.rewriting.RewritingConfiguration;
import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RewriteSTTGDs {

    private final static Logger logger = LoggerFactory.getLogger(RewriteSTTGDs.class);
    private final RewriteAndExportSTTgds rewriter = new RewriteAndExportSTTgds();
    private final DAOMCScenarioStandard daoMCScenario = new DAOMCScenarioStandard();

    public void rewrite(Scenario scenario) {
        try {
            if(!ChaseUtility.isRewriteSTTGDs(scenario)){
                return;
            }
            long start = new Date().getTime();
            RewritingConfiguration config = createConfig(scenario);
            String originalTGDs = buildTGDStringToRewrite(scenario);
            if (logger.isDebugEnabled()) logger.debug("Original ST-TGDs: \n" + originalTGDs);
            String fdString = buildFDString(scenario);
            if (logger.isDebugEnabled()) logger.debug("FDs: \n" + fdString);
            String rewrittenTGDs = getRewrittenTGDString(originalTGDs, fdString, config);
            if (logger.isDebugEnabled()) logger.debug("Rewritten ST-TGDs: \n" + rewrittenTGDs);
            Scenario rewrittenScenario = new Scenario(scenario.getFileName(), scenario.getSuffix());
            rewrittenScenario.setSource(scenario.getSource());
            rewrittenScenario.setTarget(scenario.getTarget());
            daoMCScenario.loadDependencies(rewrittenTGDs, new DAOConfiguration(), rewrittenScenario);
            scenario.getSTTgds().clear();
            scenario.getSTTgds().addAll(rewrittenScenario.getSTTgds());
            if (logger.isDebugEnabled()) logger.debug(DependencyUtility.printDependencies(scenario.getSTTgds()));
            long end = new Date().getTime();
            ChaseStats.getInstance().addStat(ChaseStats.STTGD_REWRITING, end-start);
        } catch (DAOException ex) {
//            logger.warn("Unable to rewrite ST-TGDs: " + ex.getLocalizedMessage());
//            throw new it.unibas.lunatic.exceptions.DAOException("Unable to rewrite ST-TGDs: " + ex.getLocalizedMessage());
        }
    }

    private RewritingConfiguration createConfig(Scenario scenario) {
        RewritingConfiguration config = new RewritingConfiguration();
        config.setRewriteSubsumptions(scenario.getConfiguration().isOptimizeSTTGDs());
        config.setRewriteCoverages(scenario.getConfiguration().isOptimizeSTTGDs());
        config.setRewriteSelfJoins(false);
        config.setRewriteOnlyProperHomomorphisms(false);
        config.setRewriteOverlaps(scenario.getConfiguration().isRewriteSTTGDOverlaps());
        config.setUseLocalSkolems(true);
        return config;
    }

    private String buildTGDStringToRewrite(Scenario scenario) {
        StringBuilder sb = new StringBuilder();
        for (Dependency stTgd : scenario.getSTTgds()) {
            sb.append(stTgd.toSaveString());
        }
        return sb.toString();
    }

    private String buildFDString(Scenario scenario) {
        if (!scenario.getConfiguration().isRewriteSTTGDOverlaps()) {
            return "";
        }
        StringBuilder result = new StringBuilder();
        for (Dependency egd : scenario.getExtEGDs()) {
            if (!egd.isFunctionalDependency()) {
                continue;
            }
            result.append(egd.getFunctionalDependency().toString()).append("\n");
        }
        return result.toString();
    }

    private String getRewrittenTGDString(String originalTGDs, String fdString, RewritingConfiguration config) throws DAOException {
        String rewrittenTGDs = rewriter.rewriteAndExport(originalTGDs, fdString, config);
        StringBuilder stTGDsString = new StringBuilder();
        stTGDsString.append("STTGDs:\n");
        stTGDsString.append(rewrittenTGDs);
        return stTGDsString.toString();
    }

}
