function compareCells(cell1, cell2){
	/*
        print("----- CELL 1 ------\n")
	print("Attribute: " + cell1 + "\n");
	print("Value: " + cell1.getValue() + "\n");
	print("Value type: " + cell1.getValue().getType() + "\n");
	print("Tuple: " + cell1.getTuple() + "\n");

	print("----- CELL 2 ------\n")
	print("Attribute: " + cell2 + "\n");
	print("Value: " + cell2.getValue() + "\n");
	print("Value type: " + cell2.getValue().getType() + "\n");
	print("Tuple: " + cell2.getTuple() + "\n");
        */

	var cell1type = cell1.getValue().getType();
	var cell2type = cell2.getValue().getType();
	if(cell1type == Constant.NULL && cell2type == Constant.NULL){
		print("cell 1 is equal to cell 2\n");
		return Constant.EQUALS;
	}
        
	if(cell1type != Constant.NULL && cell2type == Constant.NULL){
		print("cell 1 is preferrable to cell 2\n");
		return Constant.PRECEDES;
	}

	if(cell1type == Constant.NULL && cell2type != Constant.NULL){
		print("cell 2 is preferrable to cell 1\n");
		return Constant.FOLLOWS;
	}

	return Constant.NO_ORDER;
}