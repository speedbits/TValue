package com.reitplace.tvalue;

import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;


public class HGTVInvTrackCopy4 {

    public final static String EXIT = "exit";
    public final static String DONE = "done";
    public final static String HG_IDENTIFIER = "HGTV";

    private final static String InventoryFile = "./data/inv.csv";
    private static File file = new File(InventoryFile);


    private static Config config = new Config();
    private static boolean exitPrompt = false;

    private static String actionInput = "";
    private static String upcInput = null;
    private static String locTag = "";
    public static void main(String[] args) throws Exception {
        Console.debug("Test only....");

        // load config
        config.loadConfig();

        // Create system input reader
        BufferedReader buffer = new BufferedReader(new InputStreamReader(System.in));

        //HashMap<String, String> record = new HashMap<String, String>();
        Record4 record = new Record4();


        String code = "";
        // HGTVA12000000
        // HGTVS02000000
        // HGTVE05000000
        // HGTVW05000000
        // 123456789001

        // Scenario-1
        // HGTVA1200000, HGTVS0200000, 123456789001, HGTV000Q2500, 123450009002, HGTV000Q1500, HGTV000Q2000, HGTV000X0000

        // Scenario-2
        // HGTVA1200000, HGTVW0500000, HGTVS0200000, 123456789003, HGTV000Q2000, HGTV000Q1000, HGTVW0500000 (err)
        // 123456789004, HGTV000Q1000, 123456789005, HGTV000Q2000, HGTVA1200000, HGTV000X0000

        // Scenario-3
        // HGTVA1200000, HGTVS0200000, 123456789001, HGTV000Q2500, HGTVS0300000, 123450009002, HGTV000Q1500, HGTV000Q2000, HGTV000X0000

        String lineInput = EXIT;


        while (!exitPrompt) {
            Location4 location = new Location4(locTag);
            Product upcProduct = new Product();


            // prompt for location if not set already
            Console.debug("Record location start of while => "+record.getLocation());
            if (record.getLocation() == null || !config.isLocationComplete(record.getLocation().getCode())) {
                Console.debug("Let's scan for location. Current Location state is => "+record.getLocation());
                scanForLocation(buffer, record);

                if (exitPrompt) {
                    Console.out("Exit triggered. Exiting...");
                    break;
                }

                Console.debug("Full Location Tag => " + locTag.toString());
                //location = new Location(locTag.toString());
                // record.setLocation(location);
                Console.out("Location => "+record.getLocation().toString());
            }

            // prompt of UPC
            //Console.debug("Before scannerInput => " + upcInput);
            if (record.getProduct() == null || !record.getProduct().isComplete()) {
                // If the UPC was set while scanning the Qty
                Console.debug("Before Product => " + record.getProduct());
                if (record.getProduct() == null || record.getProduct().getUpc() == null) {
                    scanForUPC(buffer, record);
                }

                // prompt for Qty
                scanForUPCQty(buffer, record);

                // Final Product Details
                Console.out("Product => " + record.getProduct());
            }

            saveRecord(record);

            record.resetProduct();


            Console.debug("Setting upcInput => "+upcInput);
            // If scanner finds a upc input during the Qty workflow then set UPC
            if (upcInput != null) {
                record.getProduct().reset();
                record.getProduct().setUpc(upcInput);
                upcInput = null;
            }

        }

    }

    private static void saveRecord(Record4 record) {
        if (record.location != null && config.isLocationComplete(record.getLocation().getCode())
                && record.getProduct() != null && record.getProduct().isComplete()) {
            Console.debug("Adding record => location: "+record.getLocation()+", product: "+record.getProduct());
            boolean status = addRecord(file, true, record);
            if (status) {
                Console.out("Record added successfully");
            } else {
                Console.out("Record failed to update the file. Resolve and restart the application.");
                Toolkit.getDefaultToolkit().beep();
            }
        }
    }

    private static void scanForUPCQty(BufferedReader buffer, Record4 record) throws Exception {

        String lineInput = "";
        int qty = 0;
        while (! WorkFlow.EXIT.equalsIgnoreCase(lineInput) && ! exitPrompt ) {
            String inputTag = "";
            lineInput = promptForUPCQty(buffer);
            Console.debug("lineInput => "+lineInput);
            if (isValidLUPCQtyInput(lineInput)) {
                int upcQtyInput = getUPCQty(lineInput);
                Console.debug("Adding qty => "+upcQtyInput);
                qty = qty + upcQtyInput;
                // upcProduct.addQty(Integer.parseInt(lineInput));
                Console.debug("total qty => "+qty);
                record.getProduct().setQty(qty);
            } else if(isValidActionCode(lineInput)) {
                Console.debug("Is a valid action input => " + lineInput);
                inputTag = getActionTag(lineInput);
                if (WorkFlow.RESET_QTY.equals(inputTag.toUpperCase())) {
                    record.getProduct().setQty(-1);
                    qty = 0;
                    continue;
                } if (WorkFlow.DELETE_PRODUCT.equals(inputTag.toUpperCase())) {
                    record.resetProduct();
                    break;
                } else if (WorkFlow.EXIT.equals(inputTag.toUpperCase())) {
                    exitPrompt = true;
                } else {
                    Console.out("Not a valid action code in this context, please try again...");
                    Toolkit.getDefaultToolkit().beep();
                    continue;
                }

            } else if (isValidLUPCInput(lineInput)) {
                // seems like new UPC was scanned, set UPC and consider this as done.
                upcInput = lineInput;
                break;
            } else if (isValidLocationInput(lineInput)) {
                // seems like new UPC was scanned, set UPC and consider this as done.
                saveRecord(record);
                record.resetLocation();
                Location4 loc = new Location4();
                loc.setTag(getLocationTag(lineInput), getLocationTagNumber(lineInput));
                record.setLocation(loc);
                break;
            } else {
                Console.out("Not a valid UPC Qty, please try again or enter 'done'");
                Toolkit.getDefaultToolkit().beep();
                continue;
            }
        }

    }

    private static void scanForUPC(BufferedReader buffer, Record4 record) throws Exception {

        String lineInput = "";
        if (record.getProduct() == null) {
            record.setProduct(new Product());
        }
        while (! WorkFlow.EXIT.equalsIgnoreCase(lineInput) && ! exitPrompt ) {
            String inputTag = "";
            lineInput = promptForUPC(buffer);
            Console.debug("inputTag => "+lineInput);
            if (isValidLUPCInput(lineInput)) {
                record.getProduct().setUpc(lineInput);
                break;
            } else if(isValidActionCode(lineInput)) {
                Console.debug("Is a valid action input => "+lineInput);
                inputTag = getActionTag(lineInput);
                if (WorkFlow.DELETE_PRODUCT.equals(inputTag.toUpperCase())) {
                    record.getProduct().setUpc(null);
                    continue;
                } else if (WorkFlow.EXIT.equals(inputTag.toUpperCase())) {
                    exitPrompt = true;
                } else {
                    Console.out("Not a valid action code in this context, please try again...");
                    Toolkit.getDefaultToolkit().beep();
                    continue;
                }

            } else {
                Console.out("Not a valid UPC, please try again...");
                Toolkit.getDefaultToolkit().beep();
                continue;
            }
        }

    }

    private static void scanForLocation(BufferedReader buffer, Record4 record) throws Exception  {

        StringBuffer locTag = new StringBuffer();

        if (record.getLocation() == null )
            record.setLocation(new Location4());
        locTag.append(record.getLocation().getCode());
        Console.debug("scanForLocation(): LocTag at start => "+locTag);
        //Location loc = new Location();
        String lineInput = "";
        while ( ! DONE.equalsIgnoreCase(lineInput) && ! exitPrompt ) {
            String inputTag = "";
            ArrayList<String> validTags = null;
            Console.debug("inputTag => "+inputTag);
            Console.debug("Location Code before prompting => "+record.getLocation().getCode());
            if (!config.isLocationComplete(locTag.toString())) {
                lineInput = promptForLocation(buffer);
                Console.debug("input from prompt => "+lineInput);
                if (isValidLocationInput(lineInput)) {
                    Console.debug("Is a valid location input => "+lineInput);
                    inputTag = getLocationTag(lineInput);
                    Console.debug("inputTag => "+inputTag);
                    validTags = config.getNextValidTags(locTag.toString());
                    Console.debug("validTags (count: "+validTags.size() +") => "+validTags);
                    if(validTags.size() >= 1) {
                        Console.debug("Adding tag ("+inputTag+") to current locTag ("+locTag+")...");
                        locTag.append(inputTag);
                        //loc.setTag(inputTag,getLocationTagNumber(lineInput));
                        record.getLocation().setTag(inputTag,getLocationTagNumber(lineInput));

                    } else {
                        Console.out("Not a valid tag, please try again...");
                        Toolkit.getDefaultToolkit().beep();
                        continue;
                    }
                } else if(isValidActionCode(lineInput)) {
                    Console.debug("Is a valid action input => "+lineInput);
                    inputTag = getActionTag(lineInput);
                    if (WorkFlow.RESET_ALL.equals(inputTag.toUpperCase())) {
                        locTag = new StringBuffer();
                        continue;
                    } else if (WorkFlow.EXIT.equals(inputTag.toUpperCase())) {
                        exitPrompt = true;
                    } else {
                        Console.out("Not a valid action code in this context, please try again...");
                        Toolkit.getDefaultToolkit().beep();
                        continue;
                    }

                }
            } // Is location complete
            else {
                lineInput = DONE;
            }

        }

    }

    public static boolean isValidActionCode(String input) {
        boolean status = false;
        if (input != null && !"".equals(input)
                && input.toUpperCase().startsWith(HG_IDENTIFIER)) {
            Console.debug("Action code input => "+input.substring(7,8));
            if (config.actionCodesMap.containsKey(input.substring(7,8))) {
                status = true;
            }
        }
        return status;
    }

    private static String getActionTag(String input) {
        return input.substring(7,8);
    }

    public static boolean addRecord(File file, boolean append, Record4 rec ) {
        boolean status = true;
        FileWriter writer = null;
        try {
            if (file != null && !file.exists()) {
                // create a file
                file.createNewFile();
            }
            if (file != null && file.exists() && file.canWrite() ) {
                writer = new FileWriter(file, true);
                // append to file
                writer.write(rec.toFormatForFile(config));
                writer.flush();
            }

        } catch (Exception e) {
            status = false;
        } finally {
            try {
                if (writer != null) writer.close();
            } catch (Exception ee) {}
        }
        return status;
    }

    private static boolean isValidLUPCInput(String input) {
        boolean status = false;
        Console.debug("input length => "+input.length());
        if (!input.toUpperCase().startsWith(HG_IDENTIFIER)
                && input.length()  > 3) {
            status = true;
        }
        return status;
    }

    private static boolean isValidLUPCQtyInput(String input) {
        boolean status = false;
        Console.debug("input length => "+input.length());
        if (input.toUpperCase().startsWith(HG_IDENTIFIER)
                && input.length()  == 12) {
            if (WorkFlow.QTY.equals(input.substring(7,8))) {
                try {
                    Integer.parseInt(input.substring(8,10));
                    status = true;
                } catch (NumberFormatException ne) {
                    Console.out("Error!! Either valid Quantity tag or Invalid Quantity. A number is expected.");
                    status = false;
                }
            }
        }
        return status;
    }

    private static int getUPCQty(String input) {
        return Integer.parseInt(input.substring(8,10));
    }

    private static boolean isValidLocationInput(String input) {
        boolean status = false;
        Console.debug("input length => "+input.length());
        if (input.toUpperCase().startsWith(HG_IDENTIFIER)
             && input.length() == 12) {
            if (!"0".equals(input.substring(4,5))) {
                status = true;
            }

        }
        return status;
    }
    private static String getLocationTag(String input) {
        return input.substring(4,5);
    }
    private static String getLocationTagNumber(String input) {
        return input.substring(5,7);
    }

    static String consoleLocationPrompt = "Scan a LOCATION Tag => \n" +
            "\t(Exit - To Exit)\n" +
            "\t(Reset All - To reset current location and start over)";
    public static String promptForLocation(BufferedReader buffer) throws Exception {
        Console.out(consoleLocationPrompt);
        return buffer.readLine();
    }

    static String consoleUPCPrompt = "Scan a UPC tag => \n" +
            "\t(Exit - To Exit)\n" +
            "\t(Delete Product - To Delete Product and Start over)";
    public static String promptForUPC(BufferedReader buffer) throws Exception {
        Console.out(consoleUPCPrompt);
        return buffer.readLine();
    }

    static String consoleUPCQtyPrompt = "Scan a UPC Qty => \n" +
            "\t(Exit - To Exit)\n" +
            "\t(Reset Qty - To erase Qty and restart scanning Qty)";
    public static String promptForUPCQty(BufferedReader buffer) throws Exception {
        Console.out(consoleUPCQtyPrompt);
        return buffer.readLine();
    }

    private static void addLocation(HashMap<String, String> record, String name, String input) {
        Console.debug("Started addColValue()");
        if("A".equals(name.toUpperCase())) {
            String value = input.substring(5,7);
            record.put("LocTag", getLocationCode(name));
            record.put("LocTagNo", value);
            Console.debug("Value for name "+name+" = "+value);
        } else if ("S".equals(name.toUpperCase())) {
            String value = input.substring(5,7);
            record.put("LocSec", getLocationCode(name));
            record.put("LocSecNo", value);
            Console.debug("Value for name "+name+" = "+value);
        } else if ("E".equals(name.toUpperCase())) {
            String value = input.substring(5,7);
            record.put("LocSubTag", getLocationCode(name));
            record.put("LocSSubTagNo", value);
            Console.debug("Value for name "+name+" = "+value);
        }

        Console.debug("End addColValue()");
    }

    private static void addUPC(HashMap<String, String> record, String name, String value) throws Exception {
        Console.debug("Started addUPC()");
        try {
            Integer.parseInt(value);
        } catch (NumberFormatException ne) {
            Console.out("Error!! Invalid Quantity. A number is expected.");
            throw new Exception("Error!! Invalid Quantity. A number is expected.");
        }
        record.put("UPC", name);
        record.put("Qty", value);
        Console.debug("Qty for UPC '"+name+"' = "+value);
        Console.debug("End addUPC()");
    }


    private static String getLocationCode(String locCode) {
        return config.locationCodeMap.get(locCode);
    }

}
