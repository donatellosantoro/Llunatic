package it.unibas.lunatic.model.dependency;

import it.unibas.lunatic.LunaticConstants;
import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.persistence.DAOMCScenarioStandard;
import it.unibas.spicy.model.mapping.rewriting.operators.RewriteAndExportSTTgds;
import it.unibas.spicy.persistence.DAOException;
import it.unibas.lunatic.model.dependency.operators.DependencyUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RewriteSTTGDs {

    private final static Logger logger = LoggerFactory.getLogger(RewriteSTTGDs.class);
    private RewriteAndExportSTTgds rewriter = new RewriteAndExportSTTgds();
    private DAOMCScenarioStandard daoMCScenario = new DAOMCScenarioStandard();

    public void rewrite(Scenario scenario) {
        try {
            if (!scenario.getConfiguration().isRewriteTGDs()
                    || scenario.getSTTgds().isEmpty()
                    || scenario.getSTTgds().size() > LunaticConstants.MAX_NUM_STTGDS_TO_REWRITE
                    || (scenario.getEGDs().isEmpty() && scenario.getExtEGDs().isEmpty())) {
                return;
            }
            String originalTGDs = buildTGDStringToRewrite(scenario);
            String rewrittenTGDs = rewriteTGDs(originalTGDs);
            if (logger.isDebugEnabled()) logger.debug("Rewritten ST-TGDs: \n" + rewrittenTGDs);
            Scenario rewrittenScenario = new Scenario(scenario.getFileName(), scenario.getSuffix());
            rewrittenScenario.setSource(scenario.getSource());
            rewrittenScenario.setTarget(scenario.getTarget());
            daoMCScenario.loadDependencies(rewrittenTGDs, rewrittenScenario);
            scenario.getSTTgds().clear();
            scenario.getSTTgds().addAll(rewrittenScenario.getSTTgds());
            if (logger.isDebugEnabled()) logger.debug(DependencyUtility.printDependencies(scenario.getSTTgds()));
        } catch (DAOException ex) {
            logger.error("Unable to rewrite ST-TGDs: " + ex.getLocalizedMessage());
            throw new it.unibas.lunatic.exceptions.DAOException("Unable to rewrite ST-TGDs: " + ex.getLocalizedMessage());
        }
    }

    private String buildTGDStringToRewrite(Scenario scenario) {
        StringBuilder sb = new StringBuilder();
        for (Dependency stTgd : scenario.getSTTgds()) {
            sb.append(stTgd.toSaveString());
        }
        return sb.toString();
    }

    private String rewriteTGDs(String originalTGDs) throws DAOException {
        String rewrittenTGDs = rewriter.rewriteAndExport(originalTGDs);
        StringBuilder stTGDsString = new StringBuilder();
        stTGDsString.append("STTGDs:\n");
        stTGDsString.append(rewrittenTGDs);
        return stTGDsString.toString();
    }

}
