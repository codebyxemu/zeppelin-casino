package xyz.zeppelin.casino.util;

import java.math.BigDecimal;
import java.math.MathContext;

public class Utils {

    public static MathContext MATH_CONTEXT_TWO_DECIMALS = new MathContext(2);

    public static BigDecimal toBigDecimalOrNull(String s) {
        try {
            return new BigDecimal(s);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
