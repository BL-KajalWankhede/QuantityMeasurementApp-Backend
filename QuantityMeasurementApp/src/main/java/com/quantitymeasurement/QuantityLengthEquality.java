package com.quantitymeasurement;
import java.util.Objects;

public class QuantityLengthEquality {

    /**
     * Supported length units with conversion factors to feet.
     */

    public enum LengthUnit {
        FEET(1.0),
        INCH(1.0 / 12.0),
        YARDS(3.0),// 1 yd = 36 inches
        CENTIMETERS(0.393701 / 12.0);

        private final double toFeetFactor;

        LengthUnit(double toFeetFactor) {
            this.toFeetFactor = toFeetFactor;
        }

        public double toFeet(double value) {
            return value * toFeetFactor;
        }
        public double fromFeet(double feetValue) {
            return feetValue / toFeetFactor;
        }
   }
    /**
     * Immutable value object representing a length with a unit.
     */
   public static final class QuantityLength {
        private final double value;
        private final LengthUnit unit;

        public QuantityLength(double value, LengthUnit unit) {
            if (unit == null) {
                throw new IllegalArgumentException("Unit must not be null");
            }
            if (Double.isNaN(value) || Double.isInfinite(value)) {
                throw new IllegalArgumentException("Value must be a finite number");
            }
            this.value = value;
            this.unit = unit;
      }

        private double valueInFeet() {
            return unit.toFeet(value);
  }
  public QuantityLength convertTo(LengthUnit targetUnit) {
       if (targetUnit == null) {
            throw new IllegalArgumentException("Target unit must not be null");
        }
          double valueInFeet = valueInFeet();
         double converted = targetUnit.fromFeet(valueInFeet);
         return new QuantityLength(converted, targetUnit);
     }

   @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            QuantityLength other = (QuantityLength) obj;
            return Double.compare(this.valueInFeet(), other.valueInFeet()) == 0;
 }

    @Override
    public int hashCode() {
            return Objects.hash(valueInFeet());
        }
        @Override
        public String toString() {
            return "QuantityLength(" + value + ", " + unit + ")";
        }
    }

    /**
     * Converts a value between two length units.
     */
    public static double convert(double value, LengthUnit sourceUnit, LengthUnit targetUnit) {
        return new QuantityLength(value, sourceUnit).convertTo(targetUnit).value;
    }

    public static boolean areEqual(double firstValue, LengthUnit firstUnit,
                                   double secondValue, LengthUnit secondUnit) {
        return new QuantityLength(firstValue, firstUnit)
                .equals(new QuantityLength(secondValue, secondUnit));
    }
    public static double demonstrateLengthConversion(double value, LengthUnit sourceUnit, LengthUnit targetUnit) {
        return convert(value, sourceUnit, targetUnit);
    }

    public static double demonstrateLengthConversion(QuantityLength length, LengthUnit targetUnit) {
        if (length == null) {
            throw new IllegalArgumentException("Length must not be null");
        }
        return length.convertTo(targetUnit).value;
    }

}
