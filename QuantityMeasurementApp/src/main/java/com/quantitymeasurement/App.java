package com.quantitymeasurement;

import static com.quantitymeasurement.FeetAndInchesEquality.areFeetEqual;
import static com.quantitymeasurement.FeetAndInchesEquality.areInchesEqual;
import static com.quantitymeasurement.QuantityLengthEquality.areEqual;

/**
 * Hello world!
 *
 */
public class App
{
    public static void main( String[] args )
    {
        Feet value1 = new Feet(1.0);
        Feet value2 = new Feet(1.0);
        boolean result = value1.equals(value2);
        System.out.println("Input: 1.0 ft and 1.0 ft");
        System.out.println("Output: Equal (" + result + ")");
        System.out.println("---------------------------------");
        System.out.println("Input: 1.0 inch and 1.0 inch");
        System.out.println("Output: Equal (" + areInchesEqual(1.0, 1.0) + ")");
        System.out.println("Input: 1.0 ft and 1.0 ft");
        System.out.println("Output: Equal (" + areFeetEqual(1.0, 1.0) + ")");
        System.out.println("----------------------------------");
        System.out.println("Input: Quantity(1.0, \"feet\") and Quantity(12.0, \"inches\")");
        System.out.println("Output: Equal (" + areEqual(1.0, QuantityLengthEquality.LengthUnit.FEET, 12.0, QuantityLengthEquality.LengthUnit.INCH) + ")");
        System.out.println("Input: Quantity(1.0, \"inch\") and Quantity(1.0, \"inch\")");
        System.out.println("Output: Equal (" + areEqual(1.0, QuantityLengthEquality.LengthUnit.INCH, 1.0, QuantityLengthEquality.LengthUnit.INCH) + ")");
        System.out.println("-----------------------------------");
        System.out.println("Input: Quantity(1.0, YARDS) and Quantity(3.0, FEET)");
        System.out.println("Output: Equal (" + areEqual(1.0, QuantityLengthEquality.LengthUnit.YARDS, 3.0, QuantityLengthEquality.LengthUnit.FEET) + ")");
        System.out.println("Input: Quantity(1.0, YARDS) and Quantity(36.0, INCHES)");
        System.out.println("Output: Equal (" + areEqual(1.0, QuantityLengthEquality.LengthUnit.YARDS, 36.0, QuantityLengthEquality.LengthUnit.INCH) + ")");
        System.out.println("Input: Quantity(2.0, CENTIMETERS) and Quantity(2.0, CENTIMETERS)");
        System.out.println("Output: Equal (" + areEqual(2.0, QuantityLengthEquality.LengthUnit.CENTIMETERS, 2.0, QuantityLengthEquality.LengthUnit.CENTIMETERS) + ")");
        System.out.println("Input: Quantity(1.0, CENTIMETERS) and Quantity(0.393701, INCHES)");
        System.out.println("Output: Equal (" + areEqual(1.0, QuantityLengthEquality.LengthUnit.CENTIMETERS, 0.393701, QuantityLengthEquality.LengthUnit.INCH) + ")");

    }
}
