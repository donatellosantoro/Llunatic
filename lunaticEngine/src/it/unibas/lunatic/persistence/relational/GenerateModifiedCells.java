package it.unibas.lunatic.persistence.relational;

import it.unibas.lunatic.exceptions.DAOException;
import it.unibas.lunatic.model.chase.chasemc.CellGroup;
import it.unibas.lunatic.model.chase.chasemc.CellGroupCell;
import it.unibas.lunatic.model.chase.chasemc.DeltaChaseStep;
import it.unibas.lunatic.model.chase.chasemc.operators.OccurrenceHandlerMC;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import speedy.model.database.AttributeRef;
import speedy.model.database.IValue;
import speedy.model.database.TupleOID;

public class GenerateModifiedCells {

    private static Logger logger = LoggerFactory.getLogger(GenerateModifiedCells.class);

    private OccurrenceHandlerMC occurrenceHandler;
    private int counter;

    public GenerateModifiedCells(OccurrenceHandlerMC occurrenceHandler) {
        this.occurrenceHandler = occurrenceHandler;
    }

    public void generate(DeltaChaseStep result, String fileName) throws DAOException {
        try {
            List<String> results = generate(result);
            saveResults(results, fileName);
        } catch (IOException ex) {
            ex.printStackTrace();
            throw new DAOException(ex.getLocalizedMessage());
        }
    }

    public List<String> generate(DeltaChaseStep root) throws IOException {
        counter = 0;
        List<String> results = new ArrayList<String>();
        visitForSolutions(root, results);
        if (logger.isDebugEnabled()) logger.debug("");
        return results;
    }

    private void visitForSolutions(DeltaChaseStep step, List<String> results) {
        if (step.isLeaf()) {
            if (step.isDuplicate() || step.isInvalid()) {
                return;
            }
            results.add(generateModifiedCellsForStep(step));
        } else {
            for (DeltaChaseStep child : step.getChildren()) {
                visitForSolutions(child, results);
            }
        }
    }

    private String generateModifiedCellsForStep(DeltaChaseStep step) {
        StringBuilder result = new StringBuilder();
        result.append("+++++++++++++++  Solution ").append(++counter).append(" +++++++++++++++\n");
//        List<CellGroup> cellGroups = occurrenceHandler.loadAllCellGroupsInStepForDebugging(step.getDeltaDB(), step.getId(), step.getScenario());
        List<CellGroup> cellGroups = occurrenceHandler.loadAllCellGroupsForDebugging(step.getDeltaDB(), step.getId(), step.getScenario());
        for (CellGroup cellGroup : cellGroups) {
            for (CellGroupCell occurrence : cellGroup.getOccurrences()) {
                if (occurrence.getOriginalValue().equals(cellGroup.getValue())) {
                    continue;
                }
                if (occurrence.getTupleOID().toString().equals("361") && occurrence.getAttribute().equals("state")) {
                    System.out.println("Occurrence: " + occurrence.toLongString() + " - Original: " + occurrence.getOriginalValue() + " - Current: " + cellGroup.getValue());
                }
                result.append(occurrence.getTupleOID().toString()).append(".");
                result.append(occurrence.getAttribute()).append(",");
                result.append(occurrence.getOriginalValue()).append(",");
                result.append(cellGroup.getValue()).append("\n");
            }
        }
        return result.toString();
    }

    private void saveResults(List<String> results, String fileName) throws IOException {
        File outputFile = new File(fileName);
        outputFile.getParentFile().mkdirs();
        FileWriter printer = new FileWriter(outputFile);
        for (String result : results) {
            printer.write(result);
            printer.write("\n");
        }
        printer.close();
    }

}
