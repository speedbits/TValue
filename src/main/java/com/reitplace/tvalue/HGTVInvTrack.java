package com.reitplace.tvalue;

import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;


public class HGTVInvTrack {

    public final static String EXIT = "exit";
    public final static String DONE = "done";
    public final static String HG_IDENTIFIER = "HGTV";
    public final static int HG_TAG_LENGTH = 13;

    private final static String startTimestamp = (new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss")).format(new Date());

    private static String InventoryFile = null;
    private static File file = null;


    private static Config config = new Config();
    private static boolean exitPrompt = false;

    private static String actionInput = "";
    private static String upcInput = null;
    private static String locTag = "";


    public static void main(String[] args) throws Exception {

        //String computerName = java.net.InetAddress.getLocalHost().getCanonicalHostName();

        // load config
        config.loadConfig();

        // Create system input reader
        BufferedReader buffer = new BufferedReader(new InputStreamReader(System.in));

        // scan user ID
        String userId = scanUserID(buffer);
        InventoryFile = "./data/INV_"+userId+"_"+startTimestamp+".csv";
        file = new File(InventoryFile);
        Console.debug("Filename => "+InventoryFile);

        //HashMap<String, String> record = new HashMap<String, String>();
        Record record = new Record();

        // Scenario-1
        // HGTVSA1000A12, 123456789001, HGTV000Q25000, 123450009002, HGTV000Q15000, HGTV000Q20000, HGTV000X00000

        // Scenario-2
        // HGTVSA3000A13, 123456789003, HGTV000Q20000, HGTV000Q10000, HGTVW05000000 (err)
        // 123456789004, HGTV000Q10000, 123456789005, HGTV000Q20000, HGTVS02000H10, HGTV000X00000

        // Scenario-3
        // HGTVS02000H09, 123456789022, HGTV000Q25000, HGTV000Q15000, HGTVS00000B01, 123450009002, HGTV000Q15000, HGTV000Q20000, HGTV000X00000

        /**
         * HGTVUSR000287
         *
         * HGTVSA1000A12
         * 123456789001
         * HGTV000Q25000    [zero qty: HGTV000Q00000 ]
         * HGTV000Q25000
         * 123456789003
         * HGTV000Q25000
         * HGTVS02000H09
         * 123456789022
         * HGTV000Q20000
         * HGTVS00000B01
         * HGTVS00000B02
         * HGTVS00000B03
         * -> At this point, sys does not accept UPC (when it should)
         */
        /**
         * HGTVSA1000A12 => Section A1 in Aisle 12.
         * HGTVS02000H09 => Section 02 in WareHouse 09.
         * HGTVS00000B01 => Back office 1 with S 00
         */
        /**
         * Display prompts - Needs improvement
         * Cannot jump from one UPC to another UPC without entering Qty or Delete Product
         * Each CSV filename is appended with Timestamp.
         */

        String lineInput = EXIT;

        while (!exitPrompt) {
            Location location = new Location();
            Product  upcProduct = new Product();

            // prompt for location if not set already
            Console.debug("Record location start of while => "+record.getLocation());
            if (record.getLocation() == null || !config.isLocationComplete(record.getLocation().getCode())) {
                Console.debug("Let's scan for location. Current Location state is => "+record.getLocation());
                scanForLocation(buffer, record);
                //scanForUPCQty(buffer, record);

                if (exitPrompt) {
                    Console.out("Exit triggered. Exiting...");
                    break;
                }
                Console.out(record.getLocation().toCompactString());
                //Console.out("-------------------");
                Console.debug("Location => "+record.getLocation().toString());
            }

            // prompt of UPC
            //Console.debug("Before scannerInput => " + upcInput);
            if (config.isLocationComplete(record.getLocation().getCode()) &&
                    (record.getProduct() == null || !record.getProduct().isComplete())) {
                // If the UPC was set while scanning the Qty
                Console.debug("Product => " + record.getProduct());
                if (record.getProduct() == null || record.getProduct().getUpc() == null) {
                    Console.debug("Let's scan for UPC... ");
                    scanForUPC(buffer, record);

                    Console.out(record.getProduct().toCompactString());
                    Console.out("===== 1 =====");
                }

                // prompt for Qty
                if (record.getProduct() != null && record.getProduct().getUpc() != null) {
                    Console.debug("Let's scan for Qty... ");
                    scanForUPCQty(buffer, record);
                    // Added by Raj - Start - Separations
                    Console.out("====================", Console.ANSI_GREEN);
                    Console.out("Previous UPC and Quantity");
                    Console.out("====================", Console.ANSI_GREEN);
                    Console.out(record.getLocation().toCompactString());
                    // Added by Raj - End
                }


                // Final Product Details
                Console.out(record.getProduct().toCompactString());
                Console.debug("Product => " + record.getProduct().toString());
                // Added by Raj - Start
                Console.out("===================", Console.ANSI_GREEN);
                Console.out("===== New UPC =====", Console.ANSI_GREEN);
                // Added by Raj - End

            }

            if (config.isLocationComplete(record.getLocation().getCode()) &&
                    record.getProduct().isComplete()){

                saveRecord(record);

                record.resetProduct();
            }


            Console.debug("Setting upcInput => "+upcInput);
            // If scanner finds a upc input during the Qty workflow then set UPC
            if (upcInput != null) {
                record.getProduct().reset();
                record.getProduct().setUpc(upcInput);
                upcInput = null;
            }

        }

    }

    private static void saveRecord(Record record) {
        Console.debug("Start - saveRecord");
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
        Console.debug("End - saveRecord");
    }

    static String consoleUserIDPrompt = "Scan your ID => ";
    private static String scanUserID(BufferedReader buffer) throws Exception {
        Console.debug("Start - scanForUserID");
        //HGTVUSR000287
        String lineInput = "";
        boolean exitPrompt = false;

        while ( ! exitPrompt ) {
            Console.out(consoleUserIDPrompt);
            lineInput = buffer.readLine();
            if (isValidUserIDInput(lineInput)) {
                lineInput = getUserID(lineInput);
                break;
            } else if(isValidActionCode(lineInput)) {
                Console.debug("Is a valid action input => "+lineInput);
                String inputTag = getActionTag(lineInput);
                Console.debug("Action Tag => "+inputTag);
                if (WorkFlow.EXIT.equals(inputTag.toUpperCase())) {
                    exitPrompt = true;
                } else {
                    Console.out("Not a valid action code (Options: Try again or Exit.", Console.ANSI_RED);
                    Toolkit.getDefaultToolkit().beep();
                }

            }  else {
                Console.out("Not a valid User ID, please try again...", Console.ANSI_RED);
                Toolkit.getDefaultToolkit().beep();
            }
        }

        Console.debug("End - scanForUserID");
        return lineInput;
    }

    private static boolean isValidUserIDInput(String input) {
        boolean status = false;
        Console.debug("input length => "+input.length());
        if (input.toUpperCase().startsWith(HG_IDENTIFIER) && input.length() == HG_TAG_LENGTH
                && "USR".equalsIgnoreCase(input.substring(4,7)) ) {
            status = true;

        }
        return status;
    }

    private static String getUserID(String input) {
        String userId = null;
        Console.debug("getUserID: input => "+input);
        userId = input.substring(7,13);
        return userId;
    }


    private static void scanForUPCQty(BufferedReader buffer, Record record) throws Exception {
        Console.debug("Start - scanForUPCQty");
        String lineInput = "";
        // Initialize Qty to invalid value
        int qty = -1;
        while (! WorkFlow.EXIT.equalsIgnoreCase(lineInput) && ! exitPrompt ) {
            String inputTag = "";
            lineInput = promptForUPCQty(buffer);
            Console.debug("Input from prompt => "+lineInput);
            // Validate UPC Qty, should handle manual entry of quantity
            // Manual qty entry: Number less than 9999 (max 4 digits)
            if (isValidLUPCQtyInput(lineInput)) {
                int upcQtyInput = getUPCQty(lineInput);
                Console.debug("Adding qty => "+upcQtyInput);
                // If Qty is set for the first time then set to input Quantity else aggregate
                if (qty == -1) {
                    qty = upcQtyInput;
                } else {
                    qty = qty + upcQtyInput;
                }

                // upcProduct.addQty(Integer.parseInt(lineInput));
                Console.debug("Total qty => "+qty);
                record.getProduct().setQty(qty);
                //Added by Raj - Start - Qty Updates
                Console.out(record.getProduct().toCompactString());
                Console.out("===== QTY =====");
                //Added by Raj - End
            } else if(isValidActionCode(lineInput)) {
                Console.debug("Is a valid action input => " + lineInput);
                inputTag = getActionTag(lineInput);
                Console.debug("Action tag => "+inputTag);
                if (WorkFlow.RESET_QTY.equals(inputTag.toUpperCase())) {
                    record.getProduct().setQty(-1);
                    qty = 0;
                    Console.debug("Qty reset to -1.");
                    continue;
                } if (WorkFlow.DELETE_PRODUCT.equals(inputTag.toUpperCase())) {
                    record.resetProduct();
                    Console.debug("Product reset.");
                    break;
                } else if (WorkFlow.EXIT.equals(inputTag.toUpperCase())) {
                    exitPrompt = true;
                    Console.debug("Exit triggered.");
                } else {
                    Console.out("Not a valid action (Options: Try again, Reset Qty, Delete Product or Exit).", Console.ANSI_RED);
                    Toolkit.getDefaultToolkit().beep();
                    continue;
                }

            } else if (isValidLUPCInput(lineInput) && qty >= 0) {
                // seems like new UPC was scanned, set UPC and consider this as done.
                upcInput = lineInput;
                Console.debug("Is valid UPC with previous Qty >= 0 (means, it is ok to transition to new UPC).");
                break;
            } else if (isValidLocationInput(lineInput) && qty >= 0) {
                // seems like new UPC was scanned, set UPC and consider this as done.
                Console.debug("Is valid location with previous Qty >= 0 (means, it is ok to transition to new Location).");
                Console.debug("Saving record...");
                saveRecord(record);
                record.resetLocation();
                record.setLocation(getLocation(lineInput));
                Console.debug("New Location set to => "+lineInput);
                Console.debug("Since it is a new Location. Resetting product...");
                record.resetProduct();
                break;
            } else {
                Console.debug("Not a valid entry. Possible reasons/rules \n" +
                        "1. UPC Qty tag was invalid, \n" +
                        "2. Action tag was invalid \n" +
                        "3. UPC invalid and/or the Qty for previous UPC is invalid ( < 0)\n" +
                        "4. Location invalid and/or the Qty for previous UPC is valid ( < 0)\n");
                Console.out("Not a valid entry (Options: Try again, or Delete Product)", Console.ANSI_RED);
                Toolkit.getDefaultToolkit().beep();
                continue;
            }
        } // While
        Console.debug("End - scanForUPCQty");
    }

    private static void scanForUPC(BufferedReader buffer, Record record) throws Exception {
        Console.debug("Start - scanForUPC");
        String lineInput = "";
        if (record.getProduct() == null) {
            record.setProduct(new Product());
        }
        while (! WorkFlow.EXIT.equalsIgnoreCase(lineInput) && ! exitPrompt ) {
            String inputTag = "";
            lineInput = promptForUPC(buffer);
            Console.debug("Input from prompt => "+lineInput);
            // If the input is valid UPC and no UPC was scanned previously
            // THIS IS TO AVOID OVERRIDING
            if (isValidLUPCInput(lineInput) && record.getProduct().getUpc() == null) {
                record.getProduct().setUpc(lineInput);
                record.getProduct().setQty(-1);
                Console.debug("Is valid UPC. Set to Record.");
                break;
            } else if (isValidLocationInput(lineInput)) {
                // seems like new Location/section was scanned, override previous location.
                record.resetLocation();
                record.setLocation(getLocation(lineInput));
                record.resetProduct(); //added by Sri
                Console.debug("Is valid location. Reset location & Product and new loaction set to => "+lineInput);
                break;
            } else if(isValidActionCode(lineInput)) {
                Console.debug("Is a valid action input => "+lineInput);
                inputTag = getActionTag(lineInput);
                Console.debug("Action tag => "+inputTag);
                if (WorkFlow.DELETE_PRODUCT.equals(inputTag.toUpperCase())) {
                    record.getProduct().setUpc(null);
                    Console.debug("Product Deleted.");
                    continue;
                } else if (WorkFlow.EXIT.equals(inputTag.toUpperCase())) {
                    exitPrompt = true;
                    Console.debug("Exit triggered.");
                } else {
                    Console.out("Not a valid action code (Options: Delete Product or Exit.", Console.ANSI_RED);
                    Toolkit.getDefaultToolkit().beep();
                    continue;
                }

            } else {
                Console.out("Not a valid UPC or Action (Options: Try again, Delete Product or Exit.", Console.ANSI_RED);
                Toolkit.getDefaultToolkit().beep();
                continue;
            }
        }
        Console.debug("End - scanForUPC");
    }

    private static void scanForLocation(BufferedReader buffer, Record record) throws Exception  {
        Console.debug("Start - scanForLocation");
        if (record.getLocation() == null ) {
            record.setLocation(new Location());
        }

        Console.debug("Location => "+record.getLocation());

        String lineInput = promptForLocation(buffer);
        Console.debug("Input from prompt => "+lineInput);
        if (isValidLocationInput(lineInput)) {
            record.setLocation(getLocation(lineInput));
            Console.debug("Location ["+lineInput+"] set to Record.");
        } else if(isValidActionCode(lineInput)) {
            Console.debug("Is a valid action input => "+lineInput);
            String inputTag = getActionTag(lineInput);
            Console.debug("Action Tag => "+inputTag);
            if (WorkFlow.EXIT.equals(inputTag.toUpperCase())) {
                exitPrompt = true;
            } else {
                Console.out("Not a valid action code (Options: Try again or Exit.", Console.ANSI_RED);
                Toolkit.getDefaultToolkit().beep();
            }

        }  else {
            Console.out("Not a valid Location, please try again...", Console.ANSI_RED);
            Toolkit.getDefaultToolkit().beep();
        }
        Console.debug("End - scanForLocation");
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

    public static boolean addRecord(File file, boolean append, Record rec ) {
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
        if (input == null || input.length() == 0) {
            status = false;
            Console.out("Error!! Either invalid Quantity tag or invalid Quantity.");
        } else if (input.toUpperCase().startsWith(HG_IDENTIFIER)
                && input.length()  == HG_TAG_LENGTH) {
            if (WorkFlow.QTY.equals(input.substring(7,8))) {
                try {
                    Integer.parseInt(input.substring(8,10));
                    status = true;
                } catch (NumberFormatException ne) {
                    Console.out("Error!! Either valid Quantity tag or Invalid Quantity. A number is expected.");
                    status = false;
                }
            }
        } else if (input.length() <= 2) {
            try {
                Integer.parseInt(input);
                status = true;
            } catch (NumberFormatException nfe) {
                status = false;
            }

        }
        return status;
    }

    private static int getUPCQty(String input) {
        if (input != null && input.length() > 2) {
            return Integer.parseInt(input.substring(8,10));
        } else {
            return Integer.parseInt(input);
        }

    }

    private static boolean isValidLocationInput(String input) {
        boolean status = false;
        Console.debug("input length => "+input.length());
        if (input.toUpperCase().startsWith(HG_IDENTIFIER) && input.length() == HG_TAG_LENGTH) {
            if (config.locationCodeMap.containsKey(input.substring(4,5))) {
                status = true;
            }
            /**
             * This is commented because for UserID tag, below Location tag condition holds true as well
             * So, system is assuming UserID tag as a valid Location tag.
             * Therefore replaced with above condition
             * NOTE: Since 'U' is used by UserID, this code cannot be used by Location
             */
            /*if (!"0".equals(input.substring(4,5)) && !"0".equals(input.substring(10,11))) {
                status = true;
            }*/
        }
        return status;
    }

    private static Location getLocation(String input) {
        Location location = null;
        Console.debug("input length => "+input.length());
        if (input.toUpperCase().startsWith(HG_IDENTIFIER) && input.length() == HG_TAG_LENGTH) {
            String primaryTag = "";
            if (!"0".equals(input.substring(10,11))) {
                primaryTag = input.substring(10,11);
            }
            String sectionTag = "";
            if (!"0".equals(input.substring(4,5))) {
                sectionTag = input.substring(4,5);
            }
            if (config.isLocationComplete(primaryTag + sectionTag)) {
                location = new Location(sectionTag,input.substring(5,7),primaryTag,input.substring(11,13));
            }
        }
        return location;
    }

    static String consoleLocationPromptVerbose = "Scan a Section Tag => \n" +
            "\t(Exit - To Exit)\n" +
            "\t(Reset All - To reset current location and start over)";
    static String consoleLocationPrompt = "Scan a Tag =>";
    public static String promptForLocation(BufferedReader buffer) throws Exception {
        Console.out(consoleLocationPrompt);
        return buffer.readLine();
    }

    static String consoleUPCPromptVerbose = "Scan a UPC tag => \n" +
            "\t(Exit - To Exit)\n" +
            "\t(Delete Product - To Delete Product and Start over)";
    static String consoleUPCPrompt = "Scan a Tag => ";
    public static String promptForUPC(BufferedReader buffer) throws Exception {
        Console.out(consoleUPCPrompt);
        return buffer.readLine();
    }

    static String consoleUPCQtyPromptVerbose = "Scan a UPC Qty => \n" +
            "\t(Exit - To Exit)\n" +
            "\t(Reset Qty - To erase Qty and restart scanning Qty)";
    static String consoleUPCQtyPrompt = "Scan a Tag => ";
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
