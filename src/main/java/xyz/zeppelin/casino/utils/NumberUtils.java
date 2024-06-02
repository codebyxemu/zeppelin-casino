package xyz.zeppelin.casino.utils;

import java.math.BigDecimal;

public class NumberUtils {

    public static String fromNumberToFormattedString(BigDecimal number) {
        if (number.compareTo(BigDecimal.valueOf(1000)) < 0) {
            return number.toString();
        }
        int exp = (int) (Math.log(number.doubleValue()) / Math.log(1000));
        return String.format("%.1f%c", number.doubleValue() / Math.pow(1000, exp), "kMGTPE".charAt(exp-1));
    }

}
