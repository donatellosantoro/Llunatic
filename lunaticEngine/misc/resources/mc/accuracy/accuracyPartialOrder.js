importPackage(Packages.it.unibas.lunatic);
importPackage(Packages.speedy.model.database);
importPackage(Packages.it.unibas.lunatic.model.chasemc);
importPackage(Packages.it.unibas.lunatic.model.chasemc.partialorder);
importPackage(java.util);
importPackage(java.lang);

var fnAttribute = new AttributeRef("stat","fn");
var mnAttribute = new AttributeRef("stat","mn");
var rndsAttribute = new AttributeRef("stat","rnds");
var jnAttribute = new AttributeRef("stat","j");
var ptsAttribute = new AttributeRef("stat","totalpts");

function mergeGroups(group1, group2, standardResult, scenario){
    fix(group1, group2, fnAttribute, mnAttribute, standardResult, scenario);
    fix(group1, group2, jnAttribute, rndsAttribute, standardResult, scenario);
    fix(group1, group2, ptsAttribute, rndsAttribute, standardResult, scenario);
    fixRnds(group1, group2, standardResult);
    return standardResult;
}

function canHandleAttributes(attributes){
    if(attributes.contains(fnAttribute)){
        return true;
    }
    if(attributes.contains(ptsAttribute)){
        return true;
    }
    if(attributes.contains(jnAttribute)){
        return true;
    }
    if(attributes.contains(rndsAttribute)){
        return true;
    }
    return false;
}

function fixRnds(group1, group2, standardResult){
    var contains1 = ScriptUtility.containAttributeInOccurrences(group1,rndsAttribute);
    var contains2 = ScriptUtility.containAttributeInOccurrences(group2,rndsAttribute);
    if(contains1 && contains2){
        var val1 = Integer.parseInt(group1.getValue().toString());
        var val2 = Integer.parseInt(group2.getValue().toString());
        if(val1 >= val2){
            standardResult.setValue(group1.getValue());
        }else{
            standardResult.setValue(group2.getValue());
        }
    }
}

function fix(group1, group2, conflictAttribute, additionalAttribute, standardResult, scenario){
    var contains1 = ScriptUtility.containAttributeInOccurrences(group1,conflictAttribute);
    var contains2 = ScriptUtility.containAttributeInOccurrences(group2,conflictAttribute);
    if(contains1 && contains2){
        var order = ScriptUtility.findPreferredValue(group1, group2, additionalAttribute, scenario);
        if(order == Constant.FOLLOWS){
            standardResult.setValue(group1.getValue());
        }
        if(order == Constant.PRECEDES){
            standardResult.setValue(group2.getValue());
        }
        if(order == Constant.EQUAL){
            standardResult.setValue(group1.getValue());
        }
    }
}