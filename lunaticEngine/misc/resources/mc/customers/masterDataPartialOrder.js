function compareCells(cell1, cell2){
	var masterDetailTables = new java.util.ArrayList();
        masterDetailTables.add("const1");
        masterDetailTables.add("const2");

	var cell1_MD = false;
	for (i=0;i<cell1.size();i++) {
		var cellTable = cell1.get(i).getAttributeRef().getTableName();
                print('***********' + cellTable);
		if( masterDetailTables.indexOf(cellTable) != -1 ){
//		if( cellTable == "const1" || cellTable == "const2"){
			cell1_MD = true;
		}
	}	

	var cell2_MD = false;
	for (i=0;i<cell2.size();i++) {
		var cellTable = cell2.get(i).getAttributeRef().getTableName();
                print('***********' + cellTable);
		if( masterDetailTables.indexOf(cellTable) != -1 ){
//		if( cellTable == "const1" || cellTable == "const2"){
			cell2_MD = true;
		}
	}

	if(cell1_MD == true && cell2_MD == false){
		print("cell 1 is preferrable to cell 2\n");
		return Constant.FOLLOWS;
	}
        
	if(cell1_MD == false && cell2_MD == true){
		print("cell 2 is preferrable to cell 1\n");
		return Constant.PRECEDES;
	}

    return Constant.NO_ORDER;
}