package it.unibas.lunatic.persistence;

import it.unibas.lunatic.LunaticConfiguration;
import org.jdom.Document;
import org.jdom.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import speedy.exceptions.DAOException;
import speedy.persistence.xml.DAOXmlUtility;

public class DAOLunaticConfiguration {

    private final static Logger logger = LoggerFactory.getLogger(DAOLunaticConfiguration.class);
    private DAOXmlUtility daoUtility = new DAOXmlUtility();

    public LunaticConfiguration loadConfiguration(String fileScenario) {
        try {
            Document document = daoUtility.buildDOM(fileScenario);
            Element rootElement = document.getRootElement();
            Element configurationElement = rootElement.getChild("configuration");
            return loadConfiguration(configurationElement);
        } catch (Throwable ex) {
            logger.error(ex.getLocalizedMessage());
            ex.printStackTrace();
            String message = "Unable to load egtask from file " + fileScenario;
            if (ex.getMessage() != null && !ex.getMessage().equals("NULL")) {
                message += "\n" + ex.getMessage();
            }
            throw new DAOException(message);
        }
    }

    public LunaticConfiguration loadConfiguration(Element configurationElement) {
        LunaticConfiguration configuration = new LunaticConfiguration();
        if (configurationElement == null || configurationElement.getChildren().isEmpty()) {
            return configuration;
        }
        Element printStepsElement = configurationElement.getChild("printSteps");
        if (printStepsElement != null) {
            configuration.setPrintSteps(Boolean.parseBoolean(printStepsElement.getValue()));
        }
        Element recreateDBOnStartElement = configurationElement.getChild("recreateDBOnStart");
        if (recreateDBOnStartElement != null) {
            configuration.setRecreateDBOnStart(Boolean.parseBoolean(recreateDBOnStartElement.getValue()));
        }
        Element useLimit1Element = configurationElement.getChild("useLimit1");
        if (useLimit1Element != null) {
            configuration.setUseLimit1ForEGDs(Boolean.parseBoolean(useLimit1Element.getValue()));
        }
        Element removeDuplicatesElement = configurationElement.getChild("removeDuplicates");
        if (removeDuplicatesElement != null) {
            configuration.setRemoveDuplicates(Boolean.parseBoolean(removeDuplicatesElement.getValue()));
        }
        Element checkGroundSolutionsElement = configurationElement.getChild("checkGroundSolutions");
        if (checkGroundSolutionsElement != null) {
            configuration.setCheckGroundSolutions(Boolean.parseBoolean(checkGroundSolutionsElement.getValue()));
        }
        Element exportSolutionsElement = configurationElement.getChild("exportSolutions");
        if (exportSolutionsElement != null) {
            configuration.setExportSolutions(Boolean.parseBoolean(exportSolutionsElement.getValue()));
        }
        Element exportSolutionsPathElement = configurationElement.getChild("exportSolutionsPath");
        if (exportSolutionsPathElement != null) {
            configuration.setExportSolutionsPath(exportSolutionsPathElement.getValue());
        }
        Element exportSolutionsTypeElement = configurationElement.getChild("exportSolutionsType");
        if (exportSolutionsTypeElement != null) {
            configuration.setExportSolutionsType(exportSolutionsTypeElement.getValue());
            if (!configuration.getExportSolutionsType().equals("CSV")) {
                throw new it.unibas.lunatic.exceptions.DAOException("Export type not supported");
            }
        }
        Element exportChangesElement = configurationElement.getChild("exportChanges");
        if (exportChangesElement != null) {
            configuration.setExportChanges(Boolean.parseBoolean(exportChangesElement.getValue()));
        }
        Element exportChangesPathElement = configurationElement.getChild("exportChangesPath");
        if (exportChangesPathElement != null) {
            configuration.setExportChangesPath(exportChangesPathElement.getValue());
        }
        return configuration;
    }
}
