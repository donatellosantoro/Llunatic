package it.unibas.lunatic.model.chase.chasemc.partialorder;

import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.PartialOrderConstants;
import it.unibas.lunatic.exceptions.PartialOrderException;
import it.unibas.lunatic.model.chase.chasemc.CellGroup;
import it.unibas.lunatic.model.chase.chasemc.CellGroupCell;
import it.unibas.lunatic.utility.LunaticUtility;
import java.io.*;
import java.util.List;
import java.util.Set;
import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import speedy.model.database.AttributeRef;
import speedy.model.database.IValue;

public class ScriptPartialOrder extends StandardPartialOrder {

    private static Logger logger = LoggerFactory.getLogger(ScriptPartialOrder.class);
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
    
    @Override
    public CellGroup findLUB(List<CellGroup> cellGroups, Scenario scenario) throws PartialOrderException {
        if (!canHandleAttributes(LunaticUtility.extractAttributesInCellGroups(cellGroups))) {
            return null;
        }
        return super.findLUB(cellGroups, scenario);
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

    @Override
    public IValue generalizeNonAuthoritativeConstantCells(Set<CellGroupCell> nonAuthoritativeCells, CellGroup cellGroup, Scenario scenario) {
        IValue standardValue = super.generalizeNonAuthoritativeConstantCells(nonAuthoritativeCells, cellGroup, scenario);
        try {
            Invocable invocableEngine = (Invocable) jsEngine;
            return (IValue) invocableEngine.invokeFunction("generalizeNonAuthoritativeConstantCells", nonAuthoritativeCells, cellGroup, standardValue, scenario);
        } catch (ScriptException ex) {
            throw new PartialOrderException("Unable to compare cells. " + ex);
        } catch (NoSuchMethodException ex) {
            throw new PartialOrderException("Partial order script must implement generalizeNonAuthoritativeConstantCells(nonAuthoritativeCells, cellGroup, standardValue, scenario) " + ex);
        }
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

    public String getScriptFile() {
        return scriptFile;
    }

    public String toString() {
        return "Script partial order: " + scriptFile;
    }
}
