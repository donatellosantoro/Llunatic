package it.unibas.lunatic.test.checker;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

public class DAOTxt {

    public Map<String, List<IExpectedTuple>> loadData(String fileName) throws Exception {
        Map<String, List<IExpectedTuple>> result = new HashMap<String, List<IExpectedTuple>>();
        BufferedReader reader = new BufferedReader(new FileReader(fileName));
        String line = null;
        while ((line = reader.readLine()) != null) {
            handleFactLine(line, result);
        }
        return result;
    }

    private void handleFactLine(String line, Map<String, List<IExpectedTuple>> result) {
        StringTokenizer tokenizer = new StringTokenizer(line, ",()");
        String relationName = tokenizer.nextToken().trim().toLowerCase();
        List<IExpectedTuple> tuples = result.get(relationName);
        if (tuples == null) {
            tuples = new ArrayList<IExpectedTuple>();
            result.put(relationName, tuples);
        }
        FactTuple tuple = new FactTuple(relationName);
        tuples.add(tuple);
        while (tokenizer.hasMoreTokens()) {
            String value = tokenizer.nextToken().trim();
            if (!value.equals(")")) {
                tuple.addValue(new FactValue(value));
            }
        }
    }

    public Map<String, List<IExpectedTuple>> loadDataForXML(String fileName) throws Exception {
        Map<String, List<IExpectedTuple>> result = new HashMap<String, List<IExpectedTuple>>();
        BufferedReader reader = new BufferedReader(new FileReader(fileName));
        String line = null;
        while ((line = reader.readLine()) != null) {
            handleNestedFactLine(line, result);
        }
        return result;
    }

    private void handleNestedFactLine(String line, Map<String, List<IExpectedTuple>> result) {
        StringTokenizer tokenizer = new StringTokenizer(line, ",");
        String relationName = cleanString(tokenizer.nextToken().toLowerCase());
        String oid = cleanString(tokenizer.nextToken().toLowerCase());
        String fatherOid = cleanString(tokenizer.nextToken().toLowerCase());
        List<IExpectedTuple> tuples = result.get(relationName);
        if (tuples == null) {
            tuples = new ArrayList<IExpectedTuple>();
            result.put(relationName, tuples);
        }
        FactTuple tuple = new FactTuple(relationName);
        tuple.setOid(oid);
        tuple.setFatherOid(fatherOid);
        tuples.add(tuple);
        while (tokenizer.hasMoreTokens()) {
            String value = cleanString(tokenizer.nextToken());
            tuple.addValue(new FactValue(value));
        }
    }

    private String cleanString(String token) {
        return token.substring(1, token.length() - 1).trim();
    }

    public String print(Map<String, List<IExpectedTuple>> db) {
        StringBuilder result = new StringBuilder();
        List<String> relationNames = new ArrayList<String>(db.keySet());
        Collections.sort(relationNames);
        for (String relationName : relationNames) {
            result.append("----------- " + relationName + " -----------\n");
            for (IExpectedTuple line : db.get(relationName)) {
                result.append(line).append("\n");
            }
            result.append("---------------------------\n");
        }
        return result.toString();
    }

}
