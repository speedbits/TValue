package com.reitplace.tvalue;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class Config {
    //public static HashMap<String, ArrayList> locationMap = new HashMap<String, ArrayList>();
    public static ArrayList<String> LocationArray = new ArrayList<String>();
    public static ArrayList<String> LocationTagArray = new ArrayList<String>();
    public static ArrayList<String> actionArray = new ArrayList<String>();
    public static HashMap<String, String> locationCodeMap = new HashMap<String, String>();
    public static HashMap<String, String> actionCodesMap = new HashMap<String, String>();

    public static HashMap<String, String> dbConfigMap = new HashMap<String, String>();

    private String persistMode = null;

    // Load from file
    public void loadConfig() throws Exception {

        try {

            InputStream input = Config.class.getClassLoader().getResourceAsStream("config.properties");

            Properties prop = new Properties();

            if (input == null) {
                System.out.println("Sorry, unable to find config.properties");
                return;
            }

            //load a properties file from class path, inside static method
            prop.load(input);

            Console.CONSOLE_LEVEL = Integer.parseInt(prop.getProperty("console_level","1"));

            String locationCodesStr = prop.getProperty("locationCodes");
            LocationArray = new ArrayList(Arrays.asList(locationCodesStr.split(",")));

            String actionCodesStr = prop.getProperty("actionCodes");
            actionArray = new ArrayList(Arrays.asList(actionCodesStr.split(",")));

            String locationTagStr = prop.getProperty("locationTags");
            LocationTagArray = new ArrayList(Arrays.asList(locationTagStr.split(",")));

            //get the property value and print it out
            Console.debug(prop.getProperty("locationCodes"));
            Console.debug(prop.getProperty("location.label.A"));
            Console.debug(prop.getProperty("actionCodes"));
            Console.debug("actionArray => "+actionArray);
            Console.debug("LocationArray => "+LocationArray);

            Iterator actionItr = actionArray.iterator();
            while (actionItr.hasNext()) {
                String code = (String) actionItr.next();
                String label = prop.getProperty("action.label."+code,null);
                if (label != null) {
                    actionCodesMap.put(code, label);
                    Console.debug(code+ " => "+label);
                }
            }
            Console.debug("actionCodesMap => "+actionCodesMap);

            Iterator locItr = LocationTagArray.iterator();
            while (locItr.hasNext()) {
                String code = (String) locItr.next();
                String label = prop.getProperty("location.label."+code,null);
                if (label != null) {
                    locationCodeMap.put(code, label);
                    Console.debug(code+ " => "+label);
                }
            }
            Console.debug("locationCodeMap => "+locationCodeMap);

            dbConfigMap.put(Constants.DB_URL,prop.getProperty(Constants.DB_URL));
            dbConfigMap.put(Constants.DB_USER,prop.getProperty(Constants.DB_USER));
            dbConfigMap.put(Constants.DB_PASWD,prop.getProperty(Constants.DB_PASWD));
            dbConfigMap.put(Constants.DB_INV_TABLE,prop.getProperty(Constants.DB_INV_TABLE));

            this.setPersistMode(prop.getProperty(Constants.PERSISTENCE_MODE));


        } catch (IOException ex) {
            ex.printStackTrace();
        }


        // HGTV000Q25000, HGTV000R00000, HGTV000D00000, HGTV000A00000, HGTV000X00000

    }

    public ArrayList<String> getNextValidTags(String currentInput) {
        Iterator<String> iterator = LocationArray.iterator();
        ArrayList<String> validTags = new ArrayList<String>();
        while (iterator.hasNext()) {
            String loc = iterator.next();
            // System.out.println(loc);
            if(loc.startsWith(currentInput)) {
                validTags.add(loc);
            }
        }
        return validTags;
    }

    public boolean isLocationComplete(String currentInput) {
        boolean status = false;
        Iterator<String> iterator = LocationArray.iterator();
        while (iterator.hasNext()) {
            String loc = iterator.next();
            if(loc.equalsIgnoreCase(currentInput)) {
                status = true;
                break;
            }
        }
        return status;
    }



    public static void main(String[] args) throws Exception {
        Config config = new Config();
        config.loadConfig();

        System.out.println(config.getNextValidTags("A"));
        System.out.println(config.getNextValidTags("AW"));
        System.out.println(config.getNextValidTags("P"));

    }

    public String getPersistMode() {
        return persistMode;
    }

    public void setPersistMode(String persistMode) {
        this.persistMode = persistMode;
    }
}
