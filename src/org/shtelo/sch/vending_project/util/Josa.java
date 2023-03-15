package org.shtelo.sch.vending_project.util;

public class Josa {
    private static final int GA = 0xAC00;

    private static boolean hasBatchim(char letter) {
        int value = letter - GA;
        return value % 28 != 0;
    }

    public static char eulReul(String string) {
        if (hasBatchim(string.charAt(string.length() - 1))) {
            return '을';
        } else {
            return '를';
        }
    }
}
