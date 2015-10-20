package it.unibas.lunatic;

import static speedy.SpeedyConstants.NULL_VALUE;
import speedy.model.database.AttributeRef;
import speedy.model.database.ConstantValue;
import speedy.model.database.IValue;

public class LunaticConstants {

    public static final String STTGD = "STGD";
    public static final String ExtTGD = "ExtTGD";
    public static final String ExtEGD = "ExtEGD";
    public static final String EGD = "EGD";
    public static final String DC = "DC";
    public static final String DED = "DED";

    public static final String WORK_SCHEMA = "work";
    public static final String SKOLEM_OCC_TABLE = "skolem_occ";

    public static final String CHASE_FORWARD = "f";
    public static final String CHASE_BACKWARD = "b";
    public static final String CHASE_USER = "u";
    public static final String CHASE_STEP_ROOT = "r";
    public static final String CHASE_STEP_TGD = "t";

    public static final String DELTA_TABLE_SEPARATOR = "__";
    public static final String NA_TABLE_SUFFIX = DELTA_TABLE_SEPARATOR + "NA";

    public static String CELLGROUP_TABLE = "cellgroup_table";
//    public static String OCCURRENCE_TABLE = "OccurrenceTable";
//    public static String PROVENANCE_TABLE = "ProvenanceTable";

    public static String GROUP_ID = "cellGroupId";
    public static String CELL_OID = "cellOid";
    public static String CELL_TABLE = "cellTable";
    public static String CELL_ATTRIBUTE = "cellAttr";
    public static String CELL_ORIGINAL_VALUE = "cellOrigValue";
    public static String CELL_TYPE = "cellType";
//    public static String PROVENANCE_CELL_VALUE = "provCellValue";

    public static final String TYPE_OCCURRENCE = "OCC";
    public static final String TYPE_JUSTIFICATION = "JUST";
    public static final String TYPE_USER = "USER";
    public static final String TYPE_INVALID = "INVALID";
    public static final String TYPE_ADDITIONAL = "ADDTL_FOR_";

    ///////////////    CACHE STRATEGIES    ///////////////////
//    public static final String NO_CACHE = "NO_CACHE";
//    public static final String LAZY_CACHE = "SIMPLE";
    public static final String GREEDY_SIMPLE_CACHE = "GREEDY";
    public static final String GREEDY_EHCACHE = "EHCACHE_GREEDY";
    public static final String GREEDY_JCS = "JCS_GREEDY";
    public static final String GREEDY_SINGLESTEP_SIMPLE_CACHE = "GREEDY_SIMPLE_SINGLESTEP";
    public static final String GREEDY_SINGLESTEP_EHCACHE_CACHE = "GREEDY_EHCACHE_SINGLESTEP";
    public static final String GREEDY_SINGLESTEP_JCS_CACHE = "GREEDY_JCS_SINGLESTEP";
//    public static final String GREEDY_FATHER_JCS_CACHE = "GREEDY_JCS_FATHER";

    ///////////////    DE CHASER STRATEGIES    ///////////////////
    public static final String CLASSIC_DE_CHASER = "CLASSIC_DE_CHASER";
    public static final String PROXY_MC_CHASER = "PROXY_MC_CHASER";

    ///////////////    DEBUG MODE     ///////////////////
//    public static final boolean DBMS_DEBUG = true;
    public static final boolean DBMS_DEBUG = false;
    
    // NULL OBJECTS
    public static final AttributeRef NULL_ATTRIBUTE_REF = new AttributeRef("_NullTable_", "_NullAttribute_");
    public static final AttributeRef INVALID_ATTRIBUTE_REF = new AttributeRef("_InvalidTable_", "_InvalidAttribute_");
    public static final AttributeRef USER_ATTRIBUTE_REF = new AttributeRef("_User_", "_User_");
    public static final IValue BOTTOM_VALUE = new ConstantValue("_BottomValue_");
    public static final IValue NULL_IVALUE = new ConstantValue(NULL_VALUE);
//    public static final TupleOID NULL_TUPLE_ID = new TupleOID(0);
//    public static final CellRef INVALID_CELL_REF = new CellRef(NULL_TUPLE_ID, INVALID_ATTRIBUTE_REF);
//    public static final TupleOID USER_TUPLE_ID = new TupleOID("_UserupleOID_");
//    public static final CellRef USER_CELL_REF = new CellRef(USER_TUPLE_ID, USER_ATTRIBUTE_REF);
//    public static final CellGroupCell INVALID_CELL = new CellGroupCell(INVALID_CELL_REF, BOTTOM_VALUE, BOTTOM_VALUE, LunaticConstants.TYPE_INVALID, true);

}
