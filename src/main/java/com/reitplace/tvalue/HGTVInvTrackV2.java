package com.reitplace.tvalue;

import java.awt.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;


public class HGTVInvTrackV2 {

    private String startTimestamp = (new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss")).format(new Date());

    private Config config = new Config();
    private boolean exitPrompt = false;

    private String actionInput = "";
    private String upcInput = null;
    private String locTag = "";

    private boolean doSimulate = false;
    private String simulateTestFile = null;

    /**
     * HGTVUSR000287
     *
     * HGTVMDE000001 (001 = Product only), HGTVMDE000001 (002 = Product+Qty mode)
     *
     * HGTVSA1000A12
     * 123456789001
     * HGTV000Q25000    [zero qty: HGTV000Q00000 ]
     *  -> Exit: HGTV000X00000
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

    public void run() throws Exception {

        // Create system input reader
        BufferedReader reader = null;
        FileReader fileReader = null;

        TagProcessor tagProcessor = null;
        HGTVActions actions = null;

        try {

            if (!this.doSimulate) {
                reader = new BufferedReader(new InputStreamReader(System.in));
            } else {
                fileReader = new FileReader(this.simulateTestFile);
                reader = new BufferedReader(fileReader);
            }

            tagProcessor = new TagProcessor(config);
            actions = new HGTVActions(config);
            WorkFlowV2 workFlow = new WorkFlowV2(actions);
            RecordV2 record = new RecordV2();



            // Request User to scan tag
            String scanTagPrompt = "Scan a Tag => ";
            String lineInput = "";
            InputTag inputTag = new InputTag();
            while (true) {

                // Prompt to scan a tag
                Console.out(scanTagPrompt);
                lineInput = reader.readLine();

                // Get Tag Type
                inputTag = tagProcessor.process(lineInput);
                if(inputTag != null && inputTag.getTagType() != Constants.TagTypeInvalid) {
                    Console.debug(inputTag.toString());
                } else {
                    Console.debug("[911] inputTag is null.");
                    Console.out("Invalid tag, please try again...", Console.ANSI_RED);
                    Toolkit.getDefaultToolkit().beep();
                    continue;
                }

                // Can Perform Step?
                boolean canPerform = workFlow.canPerformStep(inputTag, record);
                Console.debug("Record => " + record);
                Console.debug("canPerformStep => " + canPerform);

                // Perform Step (but break if Exit action was requested)
                if(canPerform) {

                    // If Exit Action, then perform here
                    if(inputTag.getTagType() == Constants.TagTypeAction &&
                            Constants.ACTION_EXIT.equalsIgnoreCase((String)inputTag.getInputObject())) {
                        Console.out("Exiting...");
                        break;
                    }

                    workFlow.performStep(inputTag, record);
                    Console.debug("Record (after performStep) => " + record);

                    // If any other action, then there is no need to proceed further
                    // as the action would have been performed above
                    if(inputTag.getTagType() == Constants.TagTypeAction ) {
                        continue;
                    }

                    Console.out(record.toDisplay(actions.getConfig()));

                    // Is Record Complete?
                    if(record.isRecordComplete()) {
                        // Save Record
                        if (actions.saveRecord(record)) {
                            Console.out("Record added successfully");
                            // record.resetProduct(); //Not needed, since when a new UPC is scanned, the product is reset
                        } else {
                            Console.out("Record failed to update. Resolve and restart the application.");
                            Toolkit.getDefaultToolkit().beep();
                        }
                    } else if (record.isProductOnlyComplete() &&
                            Constants.SCAN_MODE_PROD_ONLY.equalsIgnoreCase(record.getScanMode())) {
                        if (actions.saveRecord(record)) {
                            Console.out("Product added successfully");
                            // record.resetProduct(); //Not needed, since when a new UPC is scanned, the product is reset
                        } else {
                            Console.out("Product failed to update. Resolve and restart the application.");
                            Toolkit.getDefaultToolkit().beep();
                        }
                    }

                } else {
                    Console.out("Cannot perform step", Console.ANSI_RED);
                    Toolkit.getDefaultToolkit().beep();
                }

            } // while loop

        } catch (Exception e) {
            // e.printStackTrace();
            Console.out("Error Occurred - "+e.getMessage(), Console.ANSI_RED);

        } finally {
            try {
                if(actions != null) actions.cleanUp();
                if(fileReader != null) fileReader.close();
                if(reader != null) reader.close();
            } catch (Exception e) {}
        }

        Console.out("Thanks for using!");
    }

    public void initialize() throws Exception {
        // load config
        config.loadConfig();
    }

    private void cleanup() throws Exception {
        // perform cleanup and house keeping task here


    }

    public static void main(String[] args) throws Exception {

        HGTVInvTrackV2 tracker = new HGTVInvTrackV2();
        tracker.initialize();
        tracker.run();

    }

    public void simulate(String testFilePath) {
        this.doSimulate = true;
        this.simulateTestFile = testFilePath;
    }


}
