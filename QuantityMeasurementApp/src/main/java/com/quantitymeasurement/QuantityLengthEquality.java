package com.quantitymeasurement;
import java.util.Objects;

public class QuantityLengthEquality {

    /**
     * Converts a value between two length units.
     */
    public static double convert(double value, LengthUnit sourceUnit, LengthUnit targetUnit) {
        return new Quantity<>(value, sourceUnit).convertTo(targetUnit).getValue();
    }

    public static boolean areEqual(double firstValue, LengthUnit firstUnit,
                                   double secondValue, LengthUnit secondUnit) {
        return new Quantity<>(firstValue, firstUnit)
                .equals(new Quantity<>(secondValue, secondUnit));
    }
    public static double demonstrateLengthConversion(double value, LengthUnit sourceUnit, LengthUnit targetUnit) {
        return convert(value, sourceUnit, targetUnit);
    }

    public static double demonstrateLengthConversion(Quantity<LengthUnit> length, LengthUnit targetUnit) {
        if (length == null) {
            throw new IllegalArgumentException("Length must not be null");
        }
        return length.convertTo(targetUnit).getValue();
    }

    public static  Quantity<LengthUnit>  add( Quantity<LengthUnit>  first,  Quantity<LengthUnit>  second) {
        if (first == null || second == null) {
            throw new IllegalArgumentException("Lengths must not be null");
        }
        return first.add(second);
    }



    public static  Quantity<LengthUnit>  add(double firstValue, LengthUnit firstUnit,
                                     double secondValue, LengthUnit secondUnit) {
        return new Quantity<>(firstValue, firstUnit)
                .add(new Quantity<>(secondValue, secondUnit));
    }

    public static  Quantity<LengthUnit>  add( Quantity<LengthUnit>  first,  Quantity<LengthUnit>  second, LengthUnit targetUnit) {
        if (first == null || second == null) {
            throw new IllegalArgumentException("Lengths must not be null");
        }
        if (targetUnit == null) {
            throw new IllegalArgumentException("Target unit must not be null");
        }
        return first.add(second, targetUnit);
    }

    public static  Quantity<LengthUnit> add(double firstValue, LengthUnit firstUnit,
                                     double secondValue, LengthUnit secondUnit,
                                     LengthUnit targetUnit) {
        return new Quantity<>(firstValue, firstUnit)
                .add(new Quantity<>(secondValue, secondUnit), targetUnit);
    }

}
