try {
    load("nashorn:mozilla_compat.js");
    //print("Nashorn");
} catch (e) {
    //print("Rhino");
}
importPackage(Packages.it.unibas.lunatic);
importPackage(Packages.it.unibas.lunatic.model.database);
importPackage(Packages.it.unibas.lunatic.model.chase.chasemc.partialorder);
importPackage(java.util);

function canHandleAttributes(attributes){
    var phoneAttribute = new AttributeRef("customers","phone");
    var salaryAttribute = new AttributeRef("treatments","salary");
    if(attributes.contains(phoneAttribute) || attributes.contains(salaryAttribute)){
        return true;
    }
    return false;
}

function generalizeNonAuthoritativeConstantCells(nonAuthoritativeCells, cellGroup, standardValue, scenario){
    //println("Generalizing non authoritative constant cells \n " + nonAuthoritativeCells);
    var max = "";
    var iterator = nonAuthoritativeCells.iterator();
    while(iterator.hasNext()){
        var currentValue = iterator.next().getValue();
        if (currentValue > max){
            max = currentValue;
        }
    }
    //println("Returning " + max);
    return new ConstantValue(max);
}
