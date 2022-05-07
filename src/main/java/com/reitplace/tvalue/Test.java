package com.reitplace.tvalue;

public class Test {
    public static void main(String[] args) {
        String str = "HGTVSA1000A12";
        System.out.println("Section => "+str.substring(4,5));
        System.out.println("Section# => "+str.substring(5,7));
        System.out.println("Aisle => "+str.substring(10,11));
        System.out.println("Aisle# => "+str.substring(11,13));
    }
}
