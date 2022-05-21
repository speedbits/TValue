package com.reitplace.tvalue;

import java.util.ArrayList;
import java.util.Iterator;

public class WorkFlowV2 {

    private HGTVActions actions = null;

    public WorkFlowV2(HGTVActions actions) {

        this.actions = actions;
    }
    public boolean canPerformStep(InputTag inputTag, RecordV2 record) {
        boolean canPerform = false;

        switch(inputTag.getTagType()) {
            case Constants.TagTypeUser:
                canPerform = true;
                break;
            case Constants.TagTypeMode:
                if(record.isUserSet()) canPerform = true;
                break;
            case Constants.TagTypeLocation:
                Console.debug("Location tag  record.isUserSet() => "+record.isUserSet());
                Console.debug("Mode tag  record.isScanModeSet() => "+record.isScanModeSet());
                if(record.isUserSet() && record.isScanModeSet()) canPerform = true;
                break;
            case Constants.TagTypeUPC:
                if(record.isLocationSet()) canPerform = true;
                break;
            case Constants.TagTypeQty:
                Console.debug("TagTypeQty tag  record.isUPCSet() => "+record.isUserSet());
                if(record.isUPCSet()) canPerform = true;
                break;
            case Constants.TagTypeAction:
                String actionCode = (String) inputTag.getInputObject();
                if(Constants.ACTION_RESET_QTY.equalsIgnoreCase(actionCode)) {
                    if (record.isUPCSet()) canPerform = true;
                } else if(Constants.ACTION_EXIT.equalsIgnoreCase(actionCode) ||             // HGTV000X00000
                        Constants.ACTION_RESET_QTY.equalsIgnoreCase(actionCode) ||
                        Constants.ACTION_DELETE_PRODUCT.equalsIgnoreCase(actionCode) ||     // HGTV000D00000
                        Constants.ACTION_RESET_ALL.equalsIgnoreCase(actionCode) ||
                        Constants.ACTION_FIND_PRODUCT.equalsIgnoreCase(actionCode)){        // HGTV000F00000
                    canPerform = true;
                }
                break;
            default:
                canPerform = false;
        }

        return canPerform;
    }

    public void performStep(InputTag inputTag, RecordV2 record) throws Exception{

        switch(inputTag.getTagType()) {
            case Constants.TagTypeUser:
                // Initialize Record (that resets all - User, Location, Product
                record.resetProduct();
                record.resetLocation();
                String userId = (String)inputTag.getInputObject();
                record.setUser(userId);
                // Setup repository / File by user
                this.actions.setupRepository(userId);
                Console.out("User Logged.");
                break;
            case Constants.TagTypeMode:
                // Reset product and set Location, set scan mode
                record.setScanMode((String)inputTag.getInputObject());
                record.resetProduct();
                record.resetLocation();
                break;
            case Constants.TagTypeLocation:
                // Reset product and set Location
                record.resetProduct();
                record.setLocation((LocationV2) inputTag.getInputObject());
                break;
            case Constants.TagTypeUPC:
                // initialize product that resets UPC & Qty, then set new UPC
                record.getProduct().reset();
                Console.debug("TagTypeQty setting UPC => "+((String) inputTag.getInputObject()));
                record.getProduct().setUpc((String) inputTag.getInputObject());
                /**
                 * Displays only when
                 *  Persistence Mode = DB (db)
                 *  UPC  found in DB, else nothing is displayed
                 */

                if(Constants.PERSIST_MODE_DB.equalsIgnoreCase(actions.getConfig().getPersistMode())) {
                    getAndDisplayProductsByUPC((String) inputTag.getInputObject());
                }
                break;
            case Constants.TagTypeQty:
                // If quantity in the product has positive value, then add to it else set it

                int qty = record.getProduct().getQty();
                Console.debug("TagTypeQty setting qty => "+qty);
                if(qty > -1) {
                    qty = qty + (Integer)inputTag.getInputObject();
                    record.getProduct().setQty(qty);
                } else {
                    record.getProduct().setQty((Integer)inputTag.getInputObject());
                }
                break;
            case Constants.TagTypeAction:
                String actionCode = (String) inputTag.getInputObject();
                if(Constants.ACTION_RESET_QTY.equalsIgnoreCase(actionCode)) {
                    record.getProduct().setQty(-1);
                } else if (Constants.ACTION_RESET_ALL.equalsIgnoreCase(actionCode)) {
                    record.resetLocation();
                    record.resetProduct();
                    record.setUser(null);
                } else if (Constants.ACTION_DELETE_PRODUCT.equalsIgnoreCase(actionCode)) {
                    // record.resetProduct();
                    // delete the product associated to the record from DB
                    Console.debug("Deleting product => "+record.getProduct().toCompactString());
                    boolean status = actions.deleteRecord(record);
                    if(status) {
                        Console.out("Deleted product => "+record.getProduct().toDisplay());
                    } else {
                        Console.out("Error deleting product => "+record.getProduct().toCompactString(), Console.ANSI_RED);
                    }

                } else if (Constants.ACTION_FIND_PRODUCT.equalsIgnoreCase(actionCode)) {
                    // get the product associated to record from DB and display
                    boolean alertWhenNoneFound = getAndDisplayProductsByUPC(record.getProduct().getUpc());
                    if(alertWhenNoneFound) {
                        Console.out("Could not locate product => "+record.getProduct().toDisplay(), Console.ANSI_RED);
                    }

                } // NOT NEEDED - AUTOMATICALLY UPDATES THE PRODUCT IF IT EXITS
                /*else if (Constants.ACTION_UPDATE_PRODUCT.equalsIgnoreCase(actionCode)) {
                    // update the product associated to record in DB
                    Console.debug("Updating product => "+record.getProduct().toCompactString());
                    boolean status = actions.updateRecord(record);
                    if(status) {
                        Console.out("Updated product => "+record.getProduct().toCompactString());
                    } else {
                        Console.out("Error updating product => "+record.getProduct().toCompactString(), Console.ANSI_RED);
                    }
                }*/
                break;
            default:
                Console.out("Unknown Step", Console.ANSI_RED);
        }
    }

    private boolean getAndDisplayProductsByUPC(String upc) throws Exception {
        ArrayList<RecordV2> recs =  actions.getRecordsByUPC(upc);
        if (recs != null && !recs.isEmpty()) {
            Iterator itr = recs.iterator();
            while (itr.hasNext()) {
                RecordV2 rec = (RecordV2) itr.next();
                Console.out(rec.toDisplay(actions.getConfig()), Console.ANSI_GREEN);
            }
        }  else return false;
        return true;
    }
}


