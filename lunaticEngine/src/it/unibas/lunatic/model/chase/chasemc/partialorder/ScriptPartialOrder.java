package it.unibas.lunatic.model.chase.chasemc.partialorder;

import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.PartialOrderConstants;
import it.unibas.lunatic.exceptions.PartialOrderException;
import it.unibas.lunatic.model.chase.chasemc.CellGroup;
import it.unibas.lunatic.model.database.AttributeRef;
import it.unibas.lunatic.model.database.IValue;
import java.io.*;
import java.util.List;
import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ScriptPartialOrder extends AbstractPartialOrder {

    private static Logger logger = LoggerFactory.getLogger(ScriptPartialOrder.class);
    private IPartialOrder standardPO = new StandardPartialOrder();
    private String scriptFile;
    private InputStream scriptInputStream;
    private ScriptEngine jsEngine;

    public ScriptPartialOrder(String scriptFile) throws ScriptException {
        this.scriptFile = scriptFile;
        try {
            scriptInputStream = new FileInputStream(scriptFile);
        } catch (FileNotFoundException ex) {
            throw new ScriptException("Unable to load script file. " + ex);
        }
        loadScript();
    }

    private void loadScript() throws ScriptException {
        ScriptEngineManager mgr = new ScriptEngineManager();
        jsEngine = mgr.getEngineByName("JavaScript");
        if (jsEngine == null) {
            throw new ScriptException("There is no Script Engine for Javascript");
        }
        if (logger.isDebugEnabled()) {
            String engineName = jsEngine.getFactory().getEngineName();
            engineName += " " + jsEngine.getFactory().getEngineVersion();
            if (logger.isDebugEnabled()) logger.debug("JS Script Engine: " + engineName);
        }
        Reader reader = new InputStreamReader(scriptInputStream);
        jsEngine.put("Constant", new PartialOrderConstants());
        jsEngine.eval(reader);
    }

    public boolean canHandleAttributes(List<AttributeRef> attributes) {
        try {
            Invocable invocableEngine = (Invocable) jsEngine;
            return (Boolean) invocableEngine.invokeFunction("canHandleAttributes", attributes);
        } catch (ScriptException ex) {
            throw new PartialOrderException("Unable to execute canHandleAttributes(attributes) " + ex);
        } catch (NoSuchMethodException ex) {
            throw new PartialOrderException("Partial order script must implement canHandleAttributes(attributes) " + ex);
        }
    }

    public CellGroup mergeCellGroups(CellGroup group1, CellGroup group2, IValue newValue, Scenario scenario) {
        CellGroup standardResult = standardPO.mergeCellGroups(group1, group2, newValue, scenario);
        try {
            Invocable invocableEngine = (Invocable) jsEngine;
            CellGroup result = (CellGroup) invocableEngine.invokeFunction("mergeGroups", group1, group2, standardResult, scenario);
            return result;
        } catch (ScriptException ex) {
            throw new PartialOrderException("Unable to compare cells. " + ex);
        } catch (NoSuchMethodException ex) {
            throw new PartialOrderException("Unable to compare cells. " + ex);
        }
    }

    public String getScriptFile() {
        return scriptFile;
    }

    public String toString() {
        return "Script partial order: " + scriptFile;
    }
}
