importPackage(Packages.it.unibas.lunatic);
importPackage(Packages.it.unibas.lunatic.model.database);
importPackage(Packages.it.unibas.lunatic.model.chase.chasemc.partialorder);
importPackage(java.util);

function mergeGroups(group1, group2, standardResult){
    fixPhone(group1, group2, standardResult);
    fixSalary(group1, group2, standardResult);
    return standardResult;
}

function canHandleAttributes(attributes){
    var phoneAttribute = new AttributeRef("customers","phone");
    var salaryAttribute = new AttributeRef("treatments","salary");
    if(attributes.contains(phoneAttribute) || attributes.contains(salaryAttribute)){
        return true;
    }
    return false;
}

//////////////// PHONE ////////////////////////
function fixPhone(group1, group2, standardResult){
    var phoneAttribute = new AttributeRef("customers","phone");
    var containsPhone1 = ScriptUtility.containAttributeInOccurrences(group1,phoneAttribute);
    var containsPhone2 = ScriptUtility.containAttributeInOccurrences(group2,phoneAttribute);
    if(containsPhone1 && containsPhone2){
        var confidence1 = findConfidence(group1);
        var confidence2 = findConfidence(group2);
        if(confidence1 > confidence2){
            standardResult.setValue(group1.getValue());
        }else if(confidence1 < confidence2){
            standardResult.setValue(group2.getValue());
        }
    }
}

function findConfidence(group){
    var max = 0.0;
    var phoneCfAttribute = new AttributeRef("customers","cfphone");
    var confidenceCells = group.getAdditionalCells().get(phoneCfAttribute);
    if(confidenceCells == null){
        return max;
    }
    var iterator = confidenceCells.iterator();
    while(iterator.hasNext()){
        var confidence = iterator.next().getValue();
        if (confidence > max){
            max = confidence;
        }
    }
    return max;
}

//////////////// SALARY ////////////////////////
function fixSalary(group1, group2, standardResult){
    var dateAttribute = new AttributeRef("treatments","salary");
    var containsSalary1 = ScriptUtility.containAttributeInOccurrences(group1,dateAttribute);
    var containsSalary2 = ScriptUtility.containAttributeInOccurrences(group2,dateAttribute);
    if(containsSalary1 && containsSalary2){
        var currency1 = findDate(group1);
        var currency2 = findDate(group2);
        if(currency1 > currency2){
            standardResult.setValue(group1.getValue());
        }else if(currency1 < currency2){
            standardResult.setValue(group2.getValue());
        }
    }
}

function findDate(group){
    var dates = new ArrayList();
    var dateAttribute = new AttributeRef("treatments","date");
    var dateCell = group.getAdditionalCells().get(dateAttribute);
    if(dateCell == null){
        return "0000-00-00";
    }
    var iterator = dateCell.iterator();
    while(iterator.hasNext()){
        var date = iterator.next().getValue();
        dates.add(date.toString());
    }
    Collections.sort(dates);
    Collections.reverse(dates);
    return dates.get(0);
}