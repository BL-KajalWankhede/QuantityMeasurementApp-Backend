package com.quantitymeasurement.equality;

/**
 * Unit test for simple App.
 */
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.equality.Feet;
import org.junit.jupiter.api.Test;

/**
 * Unit test for simple App.
 */
public class AppTest

{
    @Test
    void testEquality_SameValue() {
        Feet feet1 = new Feet(1.0);
        Feet feet2 = new Feet(1.0);

        assertTrue(feet1.equals(feet2),
                "1.0 ft should be equal to 1.0 ft");
    }

    @Test
    void testEquality_DifferentValue() {
        Feet feet1 = new Feet(1.0);
        Feet feet2 = new Feet(2.0);

        assertEquals(feet1.equals(feet2),
                "1.0 ft should not be equal to 2.0 ft");
    }

    @Test
    void testEquality_NullComparison() {
        Feet feet = new Feet(1.0);

        assertEquals(feet.equals(null),
                "Feet value should not be equal to null");
    }

    @Test
    void testEquality_NonNumericInput() {
        Feet feet = new Feet(1.0);
        String nonNumeric = "1.0";

        assertFalse(feet.equals(nonNumeric),
                "Feet value should not be equal to non-numeric input");
    }

    @Test
    void testEquality_SameReference() {
        Feet feet = new Feet(1.0);

        assertTrue(feet.equals(feet),
                "Feet value should be equal to itself (reflexive property)");
    }
}