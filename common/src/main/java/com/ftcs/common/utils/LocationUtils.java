package com.ftcs.common.utils;

public class LocationUtils {
    public static String formatLocation(double[] location) {
        return location[0] + "," + location[1];
    }

    public static double[] parseLocation(String location) {
        String[] parts = location.split(",");
        return new double[]{Double.parseDouble(parts[0]), Double.parseDouble(parts[1])};
    }
}
