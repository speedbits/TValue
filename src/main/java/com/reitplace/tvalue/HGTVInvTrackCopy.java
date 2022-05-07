package com.reitplace.tvalue;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;


public class HGTVInvTrackCopy {
    private static Config config = new Config();
    public static void main(String[] args) throws Exception {
        System.out.println("Test only....");

        // load config
        config.loadConfig();

        // Create system input reader
        BufferedReader buffer = new BufferedReader(new InputStreamReader(System.in));

        HashMap<String, String> record = new HashMap<String, String>();
        String code = "";
        // HGTVA12000000
        // HGTVS02000000
        // HGTVE05000000
        // 123456789001
        String line=buffer.readLine();
        System.out.println("initInput => "+line);
        while(line != null && !"".equals(line)) {
            if (line.toUpperCase().startsWith("HGTV")) {
                System.out.println("input starts with HGTV => "+line);
                String tag = line.substring(4,5);
                System.out.println("Tag => "+tag);
                addLocation(record, tag, line);

            } else { // It if UPC
                String upc = new String(line);
                System.out.println("UPC => "+line);
                String qtyLine = buffer.readLine();
                if(qtyLine.length() > 3) {
                    System.err.println("Error!! Quantity is expected. Please enter quantity: ");
                } else {
                    addUPC(record, upc, qtyLine);
                }

            }
            code = code + line;
            System.out.println("line = "+line);
            line=buffer.readLine();
        }
        System.out.println("code = "+code);
        System.out.println(record.toString());


        //
    }

    private static void addLocation(HashMap<String, String> record, String name, String input) {
        System.out.println("Started addColValue()");
        if("A".equals(name.toUpperCase())) {
            String value = input.substring(5,7);
            record.put("LocTag", getLocationCode(name));
            record.put("LocTagNo", value);
            System.out.println("Value for name "+name+" = "+value);
        } else if ("S".equals(name.toUpperCase())) {
            String value = input.substring(5,7);
            record.put("LocSec", getLocationCode(name));
            record.put("LocSecNo", value);
            System.out.println("Value for name "+name+" = "+value);
        } else if ("E".equals(name.toUpperCase())) {
            String value = input.substring(5,7);
            record.put("LocSubTag", getLocationCode(name));
            record.put("LocSSubTagNo", value);
            System.out.println("Value for name "+name+" = "+value);
        }
        /*switch(tag.charAt(0)) {
            case 'A':

                break;
            case 'S':
                // code block
                break;
            default:
                // code block
        }*/

        System.out.println("End addColValue()");
    }

    private static void addUPC(HashMap<String, String> record, String name, String value) throws Exception {
        System.out.println("Started addUPC()");
        try {
            Integer.parseInt(value);
        } catch (NumberFormatException ne) {
            System.err.println("Error!! Invalid Quantity. A number is expected.");
            throw new Exception("Error!! Invalid Quantity. A number is expected.");
        }
        record.put("UPC", name);
        record.put("Qty", value);
        System.out.println("Qty for UPC '"+name+"' = "+value);
        System.out.println("End addUPC()");
    }


    private static String getLocationCode(String locCode) {
        return config.locationCodeMap.get(locCode);
    }

}
