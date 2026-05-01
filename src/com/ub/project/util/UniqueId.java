package com.ub.project.util;

public class UniqueId {
    private static int disasterCounter = 0;
    private static int responderCounter = 0;

    /* 
     * Using java.lang.Math would mean having to check 
     * if the random ID generated somehow matces a prevoius one
     * This just keeps ID's unique for a session
     */
    
    public static String genDisasterId(String type) {
        disasterCounter++; // Increment first so every call is unique

        // Get a 3-letter prefix (Flood -> FLO)
        String prefix = type.substring(0, 3).toUpperCase();

        return prefix + "-" + disasterCounter;
    }

    public static String genResponderId(String category) {
        responderCounter++;

        // e.g., HEL-5001, FIR-5002
        String prefix = category.substring(0, 4).toUpperCase();

        return prefix + "-" + responderCounter;
    }

}