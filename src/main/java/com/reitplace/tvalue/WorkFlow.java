package com.reitplace.tvalue;

import java.awt.*;

public class WorkFlow {

    private String locationCode = null;
    private Product product = null;
    private String expectedLocation = null;


    final static String NO_NEXT_TAG = "$";
    final static String EXIT = "X";
    final static String QTY = "Q";
    final static String RESET_QTY = "R";
    final static String DELETE_PRODUCT = "D";
    final static String RESET_ALL = "A";

    public WorkFlow(String expLocation) {
        this.setExpectedLocation(expLocation);
    }

    public boolean isLocationComplete() {
        boolean status = false;
        if (locationCode != null && expectedLocation != null) {
            status = expectedLocation.equalsIgnoreCase(locationCode);
        }
        Console.debug("isLocationComplete => "+status);
        return status;
    }

    public boolean isProductComplete() {
        boolean status = false;
        if (product != null && product.getUpc() != null && product.getQty() >= 0){
            status = true;
        }
        return status;
    }

    public String nextTag() throws Exception {
        String nextT = NO_NEXT_TAG;
        int index = -1;
        if ( !isLocationComplete() ) {
            try {
                if (expectedLocation.startsWith(locationCode)) {
                    index = expectedLocation.indexOf(locationCode);
                    Console.debug("nextTag index => "+index);
                    nextT = String.valueOf(expectedLocation.substring(locationCode.length()).charAt(0));
                    Console.debug("nextTag => "+nextT);
                } else {
                    throw new Exception("Invalid location tag ["+locationCode+"]");
                }

            } catch (Exception e) {
                throw new Exception("Invalid location tag ["+locationCode+"]");
            }

        }
        return nextT;
    }

    private String getDiff(String confStr, String inputStr) {

        return "";
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public String getExpectedLocation() {
        return expectedLocation;
    }

    public void setExpectedLocation(String expectedLocation) {
        this.expectedLocation = expectedLocation;
    }

    public String getLocationCode() {
        return locationCode;
    }

    public void setLocationCode(String locationCode) {
        this.locationCode = locationCode;
    }

    public static void main(String[] args) {
        WorkFlow wf = new WorkFlow("AWS");

        System.out.println("Test: Tag input is invalid");
        String nextT = "A";
        try {
            wf.setLocationCode(nextT);
            nextT = nextT + wf.nextTag();
            System.out.println("Input => AP");
            wf.setLocationCode("AP");
            nextT = nextT + wf.nextTag();
            System.out.println("Input => "+nextT);
        } catch (Exception e) {
            System.err.println("Exception => "+e.getMessage());
        }


        // Test with full and valid location
        System.out.println("Test: Ideal scenario");
        try {
            String nextT2 = "AWS";
            wf.setLocationCode(nextT2);
            System.out.println("Next Tag => "+wf.nextTag());
            if (!NO_NEXT_TAG.equals(wf.nextTag())) {
                nextT2 = nextT2 + wf.nextTag();
                System.out.println("Input => "+nextT2);
            } else {
                System.out.println("Not tag suggestion ");
            }

        } catch (Exception e) {
            System.err.println("Exception => "+e.getMessage());
        }

    }
}


