function compareCells(cell1, cell2){
	var masterDetailTables = ['ConstTable', 'ConstTable2'];

	//var cell1Table = cell1.getTuple().getTable().getName();
	//var cell2Table = cell2.getTuple().getTable().getName();

	//
	var cell1Table = "ConstTable"; 
	var cell2Table = "OtherTable";
	//

	var cell1_MD = masterDetailTables.indexOf(cell1Table) != -1;
	var cell2_MD = masterDetailTables.indexOf(cell2Table) != -1;
	if(cell1_MD == true && cell2_MD == false){
		print("cell 1 is preferrable to cell 2\n");
		return Constant.PRECEDES;
	}
	if(cell1_MD == false && cell2_MD == true){
		print("cell 2 is preferrable to cell 1\n");
		return Constant.FOLLOWS;
	}

    return Constant.NO_ORDER;
}