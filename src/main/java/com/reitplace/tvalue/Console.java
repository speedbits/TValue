package com.reitplace.tvalue;

public class Console {
    public static int CONSOLE_LEVEL = 1;

    public static int CONSOLE_MAIN = 1;
    public static int CONSOLE_DEBUG = 2;

    final static String ANSI_GREEN  = "\u001B[32m";
    final static String ANSI_RED    = "\u001B[31m";
    final static String ANSI_RESET  = "\u001B[0m";

    public static void out(String str) {
        if (CONSOLE_LEVEL == CONSOLE_MAIN || CONSOLE_LEVEL == CONSOLE_DEBUG) {
            System.out.println(str);
        }
    }
    public static void out(String str, String color) {
        if (CONSOLE_LEVEL == CONSOLE_MAIN || CONSOLE_LEVEL == CONSOLE_DEBUG) {
            if (color != null && ANSI_GREEN.equalsIgnoreCase(color)) {
                System.out.println(ANSI_GREEN + str + ANSI_RESET);
            } else if (color != null && ANSI_RED.equalsIgnoreCase(color)) {
                System.out.println(ANSI_RED + str + ANSI_RESET);
            } else {
                System.out.println(str);
            }

        }
    }
    public static void debug(String str) {
        if (CONSOLE_LEVEL == CONSOLE_DEBUG) {
            System.out.println(str);
        }
    }
}
