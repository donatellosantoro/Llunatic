package it.unibas.lunatic.persistence;

import it.unibas.lunatic.LunaticConfiguration;
import org.jdom.Document;
import org.jdom.Element;
import speedy.persistence.relational.AccessConfiguration;
import speedy.persistence.xml.DAOXmlUtility;

public class DAOAccessConfiguration {

    public AccessConfiguration loadTargetAccessConfiguration(String fileScenario, LunaticConfiguration conf) {
        Document document = new DAOXmlUtility().buildDOM(fileScenario);
        Element rootElement = document.getRootElement();
        Element databaseElement = rootElement.getChild("target");
        Element dbmsElement = databaseElement.getChild("access-configuration");
        if (dbmsElement == null) {
            return null;
        }
        boolean useDictionaryEncoding = conf.isUseDictionaryEncoding();
        AccessConfiguration accessConfiguration = new AccessConfiguration();
        accessConfiguration.setDriver(dbmsElement.getChildText("driver").trim());
        accessConfiguration.setUri(dbmsElement.getChildText("uri").trim() + (useDictionaryEncoding ? "_enc" : ""));
//        accessConfiguration.setUri(dbmsElement.getChildText("uri").trim());
        accessConfiguration.setSchemaName(dbmsElement.getChildText("schema").trim());
        accessConfiguration.setLogin(dbmsElement.getChildText("login").trim());
        accessConfiguration.setPassword(dbmsElement.getChildText("password").trim());
        return accessConfiguration;
    }
}
