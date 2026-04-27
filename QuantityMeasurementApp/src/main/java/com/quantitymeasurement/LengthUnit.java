package com.quantitymeasurement;

public enum LengthUnit {
    /**
     * Supported length units with conversion factors to feet.
     */
        FEET(1.0),
        INCH(1.0 / 12.0),
        YARDS(3.0),
        CENTIMETERS(1.0 / 30.48);

        private final double toFeetFactor;

        LengthUnit(double toFeetFactor) {
            this.toFeetFactor = toFeetFactor;
        }

        public double convertToBaseUnit(double value) {
            return value * toFeetFactor;
        }

        public double convertFromBaseUnit(double baseValue) {
            return baseValue / toFeetFactor;
        }
    }
