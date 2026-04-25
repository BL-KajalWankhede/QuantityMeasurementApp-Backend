package com.quantitymeasurement;

public class Feet {
    private final double value;
    public Feet(double value) {
        this.value = value;
    }

    public double getValue() {
        return value;
    }

    @Override
    public boolean equals(Object obj) {

        // Step 1: Check same reference
        if (this == obj) {
            return true;
        }

        // Step 2: Check null or type mismatch
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        // Step 3: Cast safely
        Feet other = (Feet) obj;

        // Step 4: Compare double values correctly
        return Double.compare(this.value, other.value) == 0;
    }

    @Override
    public int hashCode() {
        return Double.hashCode(value);
    }
}