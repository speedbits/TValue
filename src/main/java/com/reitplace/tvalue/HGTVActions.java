package com.reitplace.tvalue;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class HGTVActions {

    private Config config = null;
    private DBFacadeUtil dbFacadeUtil = null;
    private FileFacadeUtil fileFacadeUtil = null;

    public HGTVActions(Config config) throws Exception{
        this.setConfig(config);
        this.dbFacadeUtil = new DBFacadeUtil(this.getConfig());
        this.fileFacadeUtil = new FileFacadeUtil(this.getConfig());
    }
    public ArrayList<RecordV2> getRecordsByUPC(String upc) throws Exception {
        return this.dbFacadeUtil.getRecordsByUPC(upc);
    }

    public boolean deleteRecord(RecordV2 record) {
        return this.dbFacadeUtil.deleteInventoryRecord(record);
    }

    public boolean updateRecord(RecordV2 record) {
        return this.dbFacadeUtil.updateInventoryRecord(record);
    }

    public void setupRepository(String userId) throws Exception {
        String startTimestamp = (new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss")).format(new Date());
        this.fileFacadeUtil.createInventoryFile(userId, startTimestamp);
    }

    public boolean saveRecord(RecordV2 record) {
        boolean status = false;
        Console.debug("Start - saveRecord");
        Console.debug("Adding record => location: "+record.getLocation()+", product: "+record.getProduct());
        if(Constants.PERSIST_MODE_DB.equalsIgnoreCase(getConfig().getPersistMode())) {
            Console.debug("Persisting to DB...");
            status = dbFacadeUtil.updateInventoryRecord(record);
        } else {
            Console.debug("Persisting to file...");
            status = fileFacadeUtil.addRecord( record,true);
        }

        Console.debug("End - saveRecord");
        return status;
    }

    public Config getConfig() {
        return config;
    }

    public void setConfig(Config config) {
        this.config = config;
    }

    public void cleanUp() {

    }
}
