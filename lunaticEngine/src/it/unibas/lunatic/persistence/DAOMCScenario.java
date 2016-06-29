package it.unibas.lunatic.persistence;

import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.exceptions.DAOException;
import java.io.File;
import org.jdom.Document;
import org.jdom.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import speedy.persistence.xml.DAOXmlUtility;

public class DAOMCScenario {

    private static Logger logger = LoggerFactory.getLogger(DAOMCScenario.class);
    private final DAOXmlUtility daoUtility = new DAOXmlUtility();
    private final DAOMCScenarioStandard daoStandard = new DAOMCScenarioStandard();
    private final DAOMCScenarioCF daoCF = new DAOMCScenarioCF();

    public Scenario loadScenario(String fileScenario) throws DAOException {
        return loadScenario(fileScenario, new DAOConfiguration());
    }

    public Scenario loadScenario(String fileScenario, String suffix) throws DAOException {
        DAOConfiguration config = new DAOConfiguration();
        config.setSuffix(suffix);
        return loadScenario(fileScenario, config);
    }

    public Scenario loadScenario(String fileScenario, DAOConfiguration daoConfiguration) throws DAOException {
        File file = new File(fileScenario);
        if (!file.exists()) {
            throw new DAOException("File " + fileScenario + " not found");
        }
        Document document = daoUtility.buildDOM(fileScenario);
        Element rootElement = document.getRootElement();
        Scenario scenario = null;
        if (isStandardScenario(rootElement)) {
            scenario = daoStandard.loadScenario(fileScenario, daoConfiguration);
        } else {
            scenario = daoCF.loadScenario(fileScenario, daoConfiguration);
        }
        return scenario;
    }

    public static boolean isStandardScenario(Element rootElement) {
        Element dependenciesElement = rootElement.getChild("dependencies");
        return dependenciesElement.getChildren().isEmpty();
    }

    public static boolean isCFScenario(Element rootElement) {
        return !isStandardScenario(rootElement);
    }

}
