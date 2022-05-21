package com.reitplace.tvalue;

public class Constants {
    public final static String DB_URL = "db.url";
    public final static String DB_USER = "db.user";
    public final static String DB_PASWD= "db.pswd";
    public final static String DB_INV_TABLE= "db.inventory.table";
    public final static String PERSISTENCE_MODE= "persistence.mode";


    public final static String DONE = "done";
    public final static String HG_IDENTIFIER = "HGTV";
    public final static int HG_TAG_LENGTH = 13;
    public final static String HG_USER_IDENTIFIER = "USR";
    public final static String HG_MODE_IDENTIFIER = "MDE";
    public final static int UPC_TAG_MIN_LENGTH = 4;
    public final static int UPC_QTY_TAG_MAX_LENGTH = 3;

    public final static String PERSIST_MODE_FILE = "file";
    public final static String PERSIST_MODE_DB = "db";

    public final static String SCAN_MODE_PROD_ONLY = "000001";
    public final static String SCAN_MODE_PROD_AND_QTY = "000002";


    // Tag Types defined for Version-2
    public final static int TagTypeUser = 100;
    public final static int TagTypeLocation = 101;
    public final static int TagTypeUPC = 102;
    public final static int TagTypeQty = 103;
    public final static int TagTypeAction = 104;

    public final static int TagTypeMode = 105;
    public final static int TagTypeInvalid = 999;

    // Action codes
    final static String ACTION_EXIT = "X";
    final static String ACTION_QTY = "Q";
    final static String ACTION_RESET_QTY = "R";
    final static String ACTION_DELETE_PRODUCT = "D";
    final static String ACTION_RESET_ALL = "A";
    final static String ACTION_FIND_PRODUCT = "F";
    final static String ACTION_UPDATE_PRODUCT = "U";

}
