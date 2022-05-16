package com.reitplace.tvalue;

import com.opencsv.CSVReader;

import java.io.FileReader;
import java.sql.*;
import java.util.ArrayList;
import java.util.Properties;

public class DBFacadeUtil {
    private Connection conn = null;
    private Config config = null;

    private String invRecordImportSQLStmt = null;
    private String invRecordUpdateSQLStmt = null;
    private String invRecordDeleteSQLStmt = null;

    private String invRecordFindSQLStmt = null;

    PreparedStatement invRecordImportPS = null;
    PreparedStatement invRecordUpdatePS = null;
    PreparedStatement invRecordDeletePS = null;

    PreparedStatement invRecordFindPS = null;


    public DBFacadeUtil(Config config) throws Exception {
        this.config = config;
        if(this.conn == null) {
            this.conn = getConnection(this.config.dbConfigMap.get(Constants.DB_URL),
                    this.config.dbConfigMap.get(Constants.DB_USER),
                    this.config.dbConfigMap.get(Constants.DB_PASWD));
        }
    }

    private Connection getConnection(String url, String user, String paswd) throws Exception {
        return DriverManager.getConnection(url, user, paswd);
    }

    private Connection getConnection() throws Exception {
        if(this.conn != null) {
            return this.conn;
        } else {
            return getConnection(this.config.dbConfigMap.get(Constants.DB_URL),
                    this.config.dbConfigMap.get(Constants.DB_USER),
                    this.config.dbConfigMap.get(Constants.DB_PASWD));
        }
    }

    public ArrayList<RecordV2> getRecordsByUPC(String upc) throws Exception {
        ArrayList<RecordV2> recs = new ArrayList<RecordV2>();
        Statement stmt = this.conn.createStatement();
        try
        {
            if(invRecordFindPS == null || invRecordFindPS.isClosed()) {
                invRecordFindSQLStmt = "select * from "+this.config.dbConfigMap.get(Constants.DB_INV_TABLE)
                        +" WHERE UPC = ? ";
                invRecordFindPS = this.getConnection().prepareStatement(invRecordFindSQLStmt);
                Console.debug("invRecordFindSQLStmt => "+invRecordFindSQLStmt);
            }

            invRecordFindPS.setString(1, upc);
            ResultSet rs = invRecordFindPS.executeQuery();

            while (rs.next()) {
                RecordV2 rec = new RecordV2();

                LocationV2 loc = new LocationV2();
                loc.setPrimary(rs.getString("LOC_PRI_LBL"));
                loc.setPrimaryValue(rs.getString("LOC_PRI_VAL"));
                loc.setSecondary(rs.getString("LOC_SEC_LBL"));
                loc.setSecondaryValue(rs.getString("LOC_SEC_VAL"));
                Product prod = new Product();
                prod.setUpc(rs.getString("UPC"));
                prod.setQty(rs.getInt("QTY"));
                rec.setLocation(loc);
                rec.setProduct(prod);
                recs.add(rec);
            } //while
        } catch (Exception e)
        {
            // e.printStackTrace();
            Console.out("Error while finding records - "+e.getMessage(), Console.ANSI_RED);
        }

        return recs;
    }

    public boolean updateInventoryRecord(RecordV2 record)
    {
        boolean status = true;
        int updateCount = 0;
        try
        {
            if(invRecordUpdatePS == null || invRecordUpdatePS.isClosed()) {
                invRecordUpdateSQLStmt = "REPLACE INTO "+this.config.dbConfigMap.get(Constants.DB_INV_TABLE)
                        +" (LOC_PRI_LBL, LOC_PRI_VAL, LOC_SEC_LBL, LOC_SEC_VAL, UPC, QTY, CREATED_BY, IS_ACTIVE)"
                        +" values (?,?,?,?,?,?,?,?) ";
                invRecordUpdatePS = this.getConnection().prepareStatement(invRecordUpdateSQLStmt);
                Console.debug("invRecordUpdateSQLStmt => "+invRecordUpdateSQLStmt);
            }

            // LOC_PRI_LBL, LOC_PRI_VAL, LOC_SEC_LBL, LOC_SEC_VAL, UPC, QTY, CREATED_BY, IS_ACTIVE
            invRecordUpdatePS.setString(1, record.getLocation().getPrimary());
            invRecordUpdatePS.setString(2, record.getLocation().getPrimaryValue());
            invRecordUpdatePS.setString(3, record.getLocation().getSecondary());
            invRecordUpdatePS.setString(4, record.getLocation().getSecondaryValue());
            invRecordUpdatePS.setString(5, record.getProduct().getUpc());
            invRecordUpdatePS.setString(6, String.valueOf(record.getProduct().getQty()));
            invRecordUpdatePS.setString(7, record.getUser());
            invRecordUpdatePS.setBoolean(8, Boolean.parseBoolean("true"));

            updateCount = invRecordUpdatePS.executeUpdate();
            Console.debug("Total records inserted/updated into table => "+updateCount);
        }
        catch (Exception e)
        {
            // e.printStackTrace();
            Console.out("Error while updating record - "+e.getMessage(), Console.ANSI_RED);
            status = false;
        }
        return status;
    }

    public boolean deleteInventoryRecord(RecordV2 record)
    {
        boolean status = true;
        int updateCount = 0;
        try
        {

            if(invRecordDeletePS == null || invRecordDeletePS.isClosed()) {
                invRecordDeleteSQLStmt = "DELETE FROM "+this.config.dbConfigMap.get(Constants.DB_INV_TABLE)
                        +" WHERE LOC_PRI_VAL = ? AND LOC_SEC_VAL = ? AND UPC = ?";
                invRecordDeletePS = this.getConnection().prepareStatement(invRecordDeleteSQLStmt);
                Console.debug("invRecordDeleteSQLStmt => "+invRecordDeleteSQLStmt);
            }
            // LOC_PRI_VAL, LOC_SEC_VAL, UPC
            invRecordDeletePS.setString(1, record.getLocation().getPrimaryValue());
            invRecordDeletePS.setString(2, record.getLocation().getSecondaryValue());
            invRecordDeletePS.setString(3, record.getProduct().getUpc());

            updateCount = invRecordDeletePS.executeUpdate();
            Console.debug("Total records deleted from table => "+updateCount);
            if(updateCount <= 0) {
                Console.out("No item found to delete.", Console.ANSI_RED);
            }
        }
        catch (Exception e)
        {
            // e.printStackTrace();
            Console.out("Error while deleting record - "+e.getMessage(), Console.ANSI_RED);
            status = false;
        }
        return status;
    }

    public boolean importInventoryCSV(String csvFilePath)
    {
        boolean importStatus = true;
        try
        {
            CSVReader reader = new CSVReader(new FileReader(csvFilePath));

            if(invRecordImportPS == null || invRecordImportPS.isClosed()) {
                invRecordImportSQLStmt = "REPLACE INTO "+this.config.dbConfigMap.get(Constants.DB_INV_TABLE)
                        +" (LOC_PRI_LBL, LOC_PRI_VAL, LOC_SEC_LBL, LOC_SEC_VAL, UPC, QTY, CREATED_BY, CREATED_AT, IS_ACTIVE)"
                        +" values (?,?,?,?,?,?,?,?,?) ";
                invRecordImportPS = this.getConnection().prepareStatement(invRecordImportSQLStmt);
                Console.debug("invRecordImportSQLStmt => "+invRecordImportSQLStmt);
            }

            String[] rowData = null;
            int rowCount = 0;
            int updateCount = 0;
            while((rowData = reader.readNext()) != null)
            {
                // LOC_PRI_LBL, LOC_PRI_VAL, LOC_SEC_LBL, LOC_SEC_VAL, UPC, QTY, CREATED_BY, CREATED_AT, IS_ACTIVE
                invRecordImportPS.setString(1, rowData[0]);
                invRecordImportPS.setString(2, rowData[1]);
                invRecordImportPS.setString(3, rowData[2]);
                invRecordImportPS.setString(4, rowData[3]);
                invRecordImportPS.setString(5, rowData[4]);
                invRecordImportPS.setString(6, rowData[5]);
                invRecordImportPS.setString(7, rowData[6]);
                invRecordImportPS.setString(8, rowData[7]);
                invRecordImportPS.setBoolean(9, Boolean.parseBoolean(rowData[8]));
                // update
                //pstmt.setString(10, rowData[5]);

                updateCount = updateCount + invRecordImportPS.executeUpdate();
                rowCount++;
                /*if(i % 2 == 0) {
                    pstmt.executeBatch();
                }*/
            }
            Console.debug("Total records read from file => "+rowCount);
            Console.debug("Total records inserted/updated into table => "+updateCount);
        }
        catch (Exception e)
        {
            // e.printStackTrace();
            Console.out("Error while importing records - "+e.getMessage(), Console.ANSI_RED);
            importStatus = false;
        }
        return importStatus;
    }

    public void cleanUp() {
        try {
            if(this.conn != null) this.conn.close();
        } catch (Exception e) {

        }
    }


}
