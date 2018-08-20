package com.n26.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class DecimalUtils {
	 
    public static BigDecimal round(BigDecimal value, int numberOfDigitsAfterDecimalPoint) {
        
        BigDecimal bigDecimal = value.setScale(numberOfDigitsAfterDecimalPoint,
                RoundingMode.HALF_UP);
        //System.out.println(">>>>>>>>>>>>>>>>>>>>> Digits: " + numberOfDigitsAfterDecimalPoint + " Dec: " + bigDecimal.doubleValue());
        return bigDecimal;
    }
}
