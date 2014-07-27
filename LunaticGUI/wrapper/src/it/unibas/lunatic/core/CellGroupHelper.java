/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unibas.lunatic.core;

import it.unibas.lunatic.OperatorFactory;
import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.model.chasemc.CellGroup;
import it.unibas.lunatic.model.chasemc.DeltaChaseStep;
import it.unibas.lunatic.model.chasemc.operators.AddUserNode;
import it.unibas.lunatic.model.chasemc.operators.CheckUnsatisfiedDependencies;
import it.unibas.lunatic.model.chasemc.operators.IBuildDatabaseForChaseStep;
import it.unibas.lunatic.model.chasemc.operators.IRunQuery;
import it.unibas.lunatic.model.chasemc.operators.IValueOccurrenceHandlerMC;
import it.unibas.lunatic.model.chasemc.operators.ValueExtractor;
import it.unibas.lunatic.model.database.IValue;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author Antonio Galotta
 */
public class CellGroupHelper {

    private static Log logger = LogFactory.getLog(CellGroupHelper.class);
//    private MainMemoryRunQuery mmRunQuery = new MainMemoryRunQuery();
//    private SQLRunQuery sqlRunQuery = new SQLRunQuery();
//    private MainMemoryMCOccurrenceHandler mmOccurrenceHandler = new MainMemoryMCOccurrenceHandler();
//    private SQLOccurrenceHandlerWithCache sqlOccurrenceHandler = new SQLOccurrenceHandlerWithCache();
//    private ExtractCellGroups mmExtractor = new ExtractCellGroups(mmRunQuery, mmOccurrenceHandler);
//    private ExtractCellGroups sqlExtractor = new ExtractCellGroups(sqlRunQuery, sqlOccurrenceHandler);
//    private ValueExtractor mmValueExtractor = new ValueExtractor(mmRunQuery);
//    private ValueExtractor sqlValueExtractor = new ValueExtractor(sqlRunQuery);
//    private AddUserNode mmAddUserNode = new AddUserNode(new CellChanger(new InsertTuple(), mmOccurrenceHandler));
//    private AddUserNode sqlAddUserNode = new AddUserNode(new CellChanger(new SQLInsertTuple(), sqlOccurrenceHandler));
    private OperatorFactory operatorFactory = OperatorFactory.getInstance();

    private CellGroupHelper() {
    }
    private static CellGroupHelper instance = new CellGroupHelper();

    public static CellGroupHelper getInstance() {
        return instance;
    }

    public StepCellGroups retrieveStepCellGroups(Scenario s, DeltaChaseStep step) {
        List<CellGroup> cellGroups = getValueOccurrenceHandlerMCExtractor(s).loadAllCellGroups(step.getDeltaDB(), step.getId());
        List<CellGroup> changedCellGroups = getValueOccurrenceHandlerMCExtractor(s).loadAllCellGroupsInStep(step.getDeltaDB(), step.getId());
        return new StepCellGroups(cellGroups, changedCellGroups);
    }

    public CellGroup findCellGroup(List<CellGroup> cgList, IValue value) {
        CellGroup selected = null;
        for (CellGroup c : cgList) {
            if (c.getValue().equals(value)) {
                selected = c;
                break;
            }
        }
        return selected;
    }

    public List<IValue> findCellGroupsValues(Scenario scenario, DeltaChaseStep step) {
        List<CellGroup> cgList = getValueOccurrenceHandlerMCExtractor(scenario).loadAllCellGroupsInStep(step.getDeltaDB(), step.getId());
        return extractValues(cgList);
    }

    public ValueExtractor getValueExtractor(Scenario s) {
        return new ValueExtractor(operatorFactory.getQueryRunner(s));
    }

    private IValueOccurrenceHandlerMC getValueOccurrenceHandlerMCExtractor(Scenario s) {
        return operatorFactory.getOccurrenceHandlerMC(s);
    }

    public List<IValue> extractValues(List<CellGroup> cgList) {
        List<IValue> cgValues = new ArrayList<IValue>();
        for (CellGroup cg : cgList) {
            cgValues.add(cg.getValue());
        }
        return cgValues;
    }

    public AddUserNode getEditor(Scenario scenario) {
        return operatorFactory.getUserNodeCreator(scenario);
    }

    public CheckUnsatisfiedDependencies getUnsatisfiedDependencyChecker(Scenario scenario) {
        IBuildDatabaseForChaseStep databaseBuilder = operatorFactory.getDatabaseBuilder(scenario);
        IRunQuery queryRunner = operatorFactory.getQueryRunner(scenario);
        IValueOccurrenceHandlerMC valueOccurrenceHandlerMC = getValueOccurrenceHandlerMCExtractor(scenario);
        return new CheckUnsatisfiedDependencies(databaseBuilder, valueOccurrenceHandlerMC, queryRunner);
    }
}
