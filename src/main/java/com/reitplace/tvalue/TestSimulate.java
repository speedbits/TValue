package com.reitplace.tvalue;

import java.io.BufferedReader;
import java.io.FileReader;

public class TestSimulate {
    public static void main(String[] args) {

       try {
           // Test-1
           String testFilePath = "./tests/test3.txt";

            HGTVInvTrackV2 tracker = new HGTVInvTrackV2();
            tracker.initialize();
            tracker.simulate(testFilePath);
            tracker.run();

            // import data
/*           Config config = new Config();
           config.loadConfig();
           DBFacadeUtil dbFacadeUtil = new DBFacadeUtil(config);
           dbFacadeUtil.importInventoryCSV("./data/INV_000200_2022-05-14_17-04-52.csv");*/

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
