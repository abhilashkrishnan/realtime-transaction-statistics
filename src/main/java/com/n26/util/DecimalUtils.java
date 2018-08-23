package com.n26.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class DecimalUtils {
	
	/**
	 * Utility method for rounding BigDecimal value
	 * @param BigDecimal value 
	 * @param Number Of Digits After Decimal Point
	 * @return BigDecimal HALF ROUND UP 
	 */
    public static BigDecimal round(BigDecimal value, int numberOfDigitsAfterDecimalPoint) {
        
        BigDecimal bigDecimal = value.setScale(numberOfDigitsAfterDecimalPoint,
                RoundingMode.HALF_UP);
        return bigDecimal;
    }
}
