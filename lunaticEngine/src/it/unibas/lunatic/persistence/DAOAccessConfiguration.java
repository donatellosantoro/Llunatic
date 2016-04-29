package it.unibas.lunatic.persistence;

import org.jdom.Document;
import org.jdom.Element;
import speedy.persistence.relational.AccessConfiguration;
import speedy.persistence.xml.DAOXmlUtility;

public class DAOAccessConfiguration {

    public AccessConfiguration loadTargetAccessConfiguration(String fileScenario) {
        Document document = new DAOXmlUtility().buildDOM(fileScenario);
        Element rootElement = document.getRootElement();
        Element databaseElement = rootElement.getChild("target");
        Element dbmsElement = databaseElement.getChild("access-configuration");
        if (dbmsElement == null) {
            return null;
        }
        AccessConfiguration accessConfiguration = new AccessConfiguration();
        accessConfiguration.setDriver(dbmsElement.getChildText("driver").trim());
        accessConfiguration.setUri(dbmsElement.getChildText("uri").trim());
        accessConfiguration.setSchemaName(dbmsElement.getChildText("schema").trim());
        accessConfiguration.setLogin(dbmsElement.getChildText("login").trim());
        accessConfiguration.setPassword(dbmsElement.getChildText("password").trim());
        return accessConfiguration;
    }
}
