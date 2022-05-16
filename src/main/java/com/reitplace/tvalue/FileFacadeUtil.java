package com.reitplace.tvalue;

import java.io.File;
import java.io.FileWriter;

public class FileFacadeUtil {

    private Config config = null;

    private File inventoryFile = null;
    private String inventoryBasePath = "./data";

    public FileFacadeUtil(Config config) throws Exception {
        this.config = config;

    }

    public void createInventoryFile(String userId, String timestamp) throws Exception {
        String invFilePath = this.inventoryBasePath+ "/INV_"+userId+"_"+timestamp+".csv";
        this.inventoryFile = new File(invFilePath);
        File dataFolder = new File(this.inventoryBasePath);
        if(!dataFolder.exists()) {
            dataFolder.mkdirs();
        }
        Console.debug("Inventory file created => "+inventoryFile);
    }
    public boolean addRecord(RecordV2 rec, boolean appendToFile) {
        boolean status = true;
        FileWriter writer = null;
        try {
            if (inventoryFile != null && !inventoryFile.exists()) {
                // create a file
                inventoryFile.createNewFile();
            }
            if (inventoryFile != null && inventoryFile.exists() && inventoryFile.canWrite() ) {
                writer = new FileWriter(inventoryFile, true);
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
}
